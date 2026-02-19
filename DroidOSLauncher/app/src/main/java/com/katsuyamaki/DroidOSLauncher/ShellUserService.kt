package com.katsuyamaki.DroidOSLauncher

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern
import android.os.Build

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    companion object {
        const val POWER_MODE_OFF = 0
        const val POWER_MODE_NORMAL = 2

        @Volatile private var displayControlClass: Class<*>? = null
        @Volatile private var displayControlClassLoaded = false
    }

    // === EFFICIENCY CACHE ===
    private var cachedVisiblePackages: List<String> = emptyList()
    private var lastVisibleCacheTime: Long = 0
    private val VISIBLE_CACHE_TTL = 800L // 800ms Cache Validity
    // Cache for Gemini task ID since it trampolines and becomes invisible
    // The BardEntryPointActivity creates a task, then immediately redirects to Google QSB
    // After trampoline, the original task disappears from am stack list
    // We cache the exact task ID when found and reuse it for subsequent repositions
    private var cachedGeminiTaskId: Int = -1
    private var cachedGeminiTaskTime: Long = 0
    private val GEMINI_CACHE_VALIDITY_MS = 30000L  // Cache valid for 30 seconds
    // === GEMINI TASK CACHE - END ===

    private val surfaceControlClass: Class<*> by lazy {
        Class.forName("android.view.SurfaceControl")
    }

    private fun getDisplayControlClass(): Class<*>? {
        if (displayControlClassLoaded && displayControlClass != null) return displayControlClass
        
        return try {
            val classLoaderFactoryClass = Class.forName("com.android.internal.os.ClassLoaderFactory")
            val createClassLoaderMethod = classLoaderFactoryClass.getDeclaredMethod(
                "createClassLoader",
                String::class.java,
                String::class.java,
                String::class.java,
                ClassLoader::class.java,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                String::class.java
            )
            val classLoader = createClassLoaderMethod.invoke(
                null, "/system/framework/services.jar", null, null,
                ClassLoader.getSystemClassLoader(), 0, true, null
            ) as ClassLoader

            val loadedClass = classLoader.loadClass("com.android.server.display.DisplayControl").also {
                val loadMethod = Runtime::class.java.getDeclaredMethod(
                    "loadLibrary0",
                    Class::class.java,
                    String::class.java
                )
                loadMethod.isAccessible = true
                loadMethod.invoke(Runtime.getRuntime(), it, "android_servers")
            }
            
            displayControlClass = loadedClass
            displayControlClassLoaded = true
            loadedClass
        } catch (e: Throwable) {
            // [STABILITY] Catch Throwable to handle NoClassDefFoundError/LinkageError
            displayControlClassLoaded = true // Stop retrying if it fails
            null
        }
    }

    private fun getAllPhysicalDisplayTokens(): List<IBinder> {
        val tokens = ArrayList<IBinder>()
        try {
            val physicalIds: LongArray = if (Build.VERSION.SDK_INT >= 34) {
                val controlClass = getDisplayControlClass()
                if (controlClass != null) {
                    controlClass.getMethod("getPhysicalDisplayIds").invoke(null) as LongArray
                } else {
                     try {
                        surfaceControlClass.getMethod("getPhysicalDisplayIds").invoke(null) as LongArray
                     } catch (e: Exception) { LongArray(0) }
                }
            } else {
                surfaceControlClass.getMethod("getPhysicalDisplayIds").invoke(null) as LongArray
            }

            if (physicalIds.isEmpty()) {
                getSurfaceControlInternalToken()?.let { tokens.add(it) }
                return tokens
            }

            for (id in physicalIds) {
                try {
                    val token: IBinder? = if (Build.VERSION.SDK_INT >= 34) {
                        val controlClass = getDisplayControlClass()
                        if (controlClass != null) {
                             controlClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
                                .invoke(null, id) as? IBinder
                        } else {
                            surfaceControlClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
                                .invoke(null, id) as? IBinder
                        }
                    } else {
                        surfaceControlClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
                            .invoke(null, id) as? IBinder
                    }
                    
                    if (token != null) tokens.add(token)
                } catch (e: Throwable) {
                }
            }
        } catch (e: Throwable) {
        }
        return tokens
    }

    private fun getSurfaceControlInternalToken(): IBinder? {
        return try {
            if (Build.VERSION.SDK_INT < 29) {
                surfaceControlClass.getMethod("getBuiltInDisplay", Int::class.java).invoke(null, 0) as IBinder
            } else {
                surfaceControlClass.getMethod("getInternalDisplayToken").invoke(null) as IBinder
            }
        } catch (e: Exception) { null }
    }

    private fun setPowerModeOnToken(token: IBinder, mode: Int) {
        try {
            val method = surfaceControlClass.getMethod(
                "setDisplayPowerMode",
                IBinder::class.java,
                Int::class.javaPrimitiveType
            )
            method.invoke(null, token, mode)
        } catch (e: Exception) {
        }
    }

    private fun setDisplayBrightnessOnToken(token: IBinder, brightness: Float): Boolean {
        try {
            val method = surfaceControlClass.getMethod(
                "setDisplayBrightness",
                IBinder::class.java,
                Float::class.javaPrimitiveType
            )
            method.invoke(null, token, brightness)
            return true
        } catch (e: Exception) {
             try {
                val method = surfaceControlClass.getMethod(
                    "setDisplayBrightness",
                    IBinder::class.java,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType
                )
                method.invoke(null, token, brightness, brightness, brightness, brightness)
                return true
            } catch (e2: Exception) {
                return false
            }
        }
    }

    private fun setDisplayBrightnessInternal(displayId: Int, brightness: Float): Boolean {
        // Legacy shim for single-target calls
        val tokens = getAllPhysicalDisplayTokens()
        if (tokens.isNotEmpty()) return setDisplayBrightnessOnToken(tokens[0], brightness)
        return false
    }

    private val shLock = Object()
    private var _shProcess: Process? = null
    private val shProcess: Process
        get() = synchronized(shLock) {
            if (_shProcess?.isAlive == true) _shProcess!!
            else Runtime.getRuntime().exec(arrayOf("sh")).also { _shProcess = it }
        }

    private fun execShellCommand(command: String) {
        synchronized(shLock) {
            try {
                val output = shProcess.outputStream
                output.write("$command\n".toByteArray())
                output.flush()
            } catch (e: Exception) {
            }
        }
    }

    // ============================================================
    // AIDL Interface Implementations
    // ============================================================

    
override fun setBrightness(displayId: Int, brightness: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            if (brightness < 0) {
                // === SCREEN OFF ===
                execShellCommand("settings put system screen_brightness_mode 0")
                
                // Get ALL tokens, but ONLY apply to the first 2 (Main + Cover)
                // This prevents killing the Glasses (which would be index 2+)
                val tokens = getAllPhysicalDisplayTokens()
                val safeTokens = tokens.take(2)
                
                for (t in safeTokens) {
                    setDisplayBrightnessOnToken(t, -1.0f)
                }
                
                execShellCommand("settings put system screen_brightness_float -1.0")
                execShellCommand("settings put system screen_brightness -1")
            } else {
                // === SCREEN ON ===
                val floatVal = brightness.toFloat() / 255.0f
                
                // Restore ALL tokens (safety, in case user replugged glasses)
                val tokens = getAllPhysicalDisplayTokens()
                for (t in tokens) {
                    setDisplayBrightnessOnToken(t, floatVal)
                }
                
                execShellCommand("settings put system screen_brightness_float $floatVal")
                execShellCommand("settings put system screen_brightness $brightness")
            }
        } catch (e: Exception) {
        } finally {
             Binder.restoreCallingIdentity(token)
        }
    }

    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        val token = Binder.clearCallingIdentity()
        try {
            val mode = if (turnOff) POWER_MODE_OFF else POWER_MODE_NORMAL
            
            // Same safety limit: Only affect first 2 physical screens
            val tokens = getAllPhysicalDisplayTokens()
            val safeTokens = tokens.take(2)
            
            for (t in safeTokens) {
                setPowerModeOnToken(t, mode)
            }
        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // --- V1.0 LOGIC: Window Management (Retained for Tiling/Minimizing) ---
    
    override fun forceStop(packageName: String) {
        val token = Binder.clearCallingIdentity()
        try { 
            val realPkg = if (packageName.endsWith(":gemini")) packageName.substringBefore(":") else packageName
            Runtime.getRuntime().exec("am force-stop $realPkg").waitFor() 
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun runCommand(command: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec(command).waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun injectKey(keyCode: Int, action: Int, flags: Int, displayId: Int, metaState: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            val cmd = "input keyevent --display $displayId $keyCode"
            Runtime.getRuntime().exec(cmd).waitFor()
        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }



    // === REPOSITION TASK - START ===
    // Repositions a task window to specified bounds using am task commands
    override fun repositionTask(packageName: String, className: String?, left: Int, top: Int, right: Int, bottom: Int) {
        // Log.d(TAG, "repositionTask: pkg=$packageName cls=$className bounds=[$left,$top,$right,$bottom]")

        val tid = getTaskId(packageName, className)
        // Log.d(TAG, "repositionTask: getTaskId returned $tid")

        if (tid == -1) {
            // Log.w(TAG, "repositionTask: No task found for $packageName / $className")
            return
        }

        val token = Binder.clearCallingIdentity()
        try {
            // Set freeform windowing mode (mode 5)
            val modeCmd = "am task set-windowing-mode $tid 5"
            val modeProc = Runtime.getRuntime().exec(arrayOf("sh", "-c", modeCmd))
            modeProc.waitFor()
            
            // Wait for OS animation/state-change (Samsung needs ~300ms)
            Thread.sleep(300)

            // Apply resize
            val resizeCmd = "am task resize $tid $left $top $right $bottom"
            val resizeProc = Runtime.getRuntime().exec(arrayOf("sh", "-c", resizeCmd))
            val exitCode = resizeProc.waitFor()

        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // === REPOSITION TASK - END ===



    // === GET VISIBLE PACKAGES - START ===
    // Returns list of packages that are actually visible on the specified display
    // Checks both mViewVisibility AND window frame bounds
    // Windows moved off-screen (left >= 10000) are considered not visible
    override fun getVisiblePackages(displayId: Int): List<String> {
        val now = System.currentTimeMillis()
        
        // [EFFICIENCY] Return cached result if valid
        if (now - lastVisibleCacheTime < VISIBLE_CACHE_TTL && cachedVisiblePackages.isNotEmpty()) {
            // Log.d("EFFICIENCY", "getVisiblePackages: CACHE HIT (Age: ${now - lastVisibleCacheTime}ms)")
            return ArrayList(cachedVisiblePackages)
        }
        
        // Log.d("EFFICIENCY", "getVisiblePackages: CACHE MISS - Running dumpsys...")

        val list = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            // Log.d(TAG, "getVisiblePackages: Checking display $displayId")
            val p = Runtime.getRuntime().exec("dumpsys window windows")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            var currentPkg: String? = null
            var isVisible = false
            var onCorrectDisplay = false
            var isOffScreen = false
            val windowPattern = Pattern.compile("Window\\{[0-9a-f]+ u\\d+ ([^\\}/ ]+)")
            val framePattern = Pattern.compile("(?:frame|mFrame)=\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]")

            while (r.readLine().also { line = it } != null) {
                val l = line!!.trim()

                if (l.startsWith("Window #")) {
                    currentPkg = null
                    isVisible = false
                    onCorrectDisplay = false
                    isOffScreen = false
                    val matcher = windowPattern.matcher(l)
                    if (matcher.find()) currentPkg = matcher.group(1)
                }

                if (l.contains("displayId=$displayId") || l.contains("mDisplayId=$displayId")) {
                    onCorrectDisplay = true
                }

                if (l.contains("mViewVisibility=0x0")) {
                    isVisible = true
                }

                val frameMatcher = framePattern.matcher(l)
                if (frameMatcher.find()) {
                    try {
                        val left = frameMatcher.group(1)?.toIntOrNull() ?: 0
                        if (left >= 10000) {
                            isOffScreen = true
                            // Log.d(TAG, "getVisiblePackages: $currentPkg is off-screen (left=$left)")
                        }
                    } catch (e: Exception) {}
                }

                if (currentPkg != null && isVisible && onCorrectDisplay && !isOffScreen) {
                    if (isUserApp(currentPkg!!) && !list.contains(currentPkg!!)) {
                        list.add(currentPkg!!)
                    }
                    currentPkg = null
                }
            }
            r.close()
            p.waitFor()
            
            // [EFFICIENCY] Update Cache
            cachedVisiblePackages = ArrayList(list)
            lastVisibleCacheTime = now
            
        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }
        return list
    }

    // [EFFICIENCY] Manual cache invalidation for instant UI updates after actions
    fun invalidateVisibleCache() {
        lastVisibleCacheTime = 0
    }

    override fun getAllRunningPackages(): List<String> {
        val list = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec("dumpsys activity activities")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            val recordPattern = Pattern.compile("ActivityRecord\\{[0-9a-f]+ u\\d+ ([a-zA-Z0-9_.]+)/")
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains("ActivityRecord{")) {
                    val m = recordPattern.matcher(line!!)
                    if (m.find()) { val pkg = m.group(1); if (pkg != null && !list.contains(pkg) && isUserApp(pkg)) list.add(pkg) }
                }
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return list
    }

override fun getWindowLayouts(displayId: Int): List<String> {
    val results = ArrayList<String>()
    val token = Binder.clearCallingIdentity()
    try {
        val p = Runtime.getRuntime().exec("dumpsys activity activities")
        val r = BufferedReader(InputStreamReader(p.inputStream))
        var line: String?
        
        var currentDisplayId = -1
        var currentTaskBounds: String? = null
        var foundPackages = mutableSetOf<String>()
        
        val displayPattern = Pattern.compile("Display #(\\d+)")
        val boundsPattern = Pattern.compile("bounds=\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]")
        val rectPattern = Pattern.compile("mBounds=Rect\\((\\d+), (\\d+) - (\\d+), (\\d+)\\)")
        val activityPattern = Pattern.compile("ActivityRecord\\{[0-9a-f]+ u\\d+ ([a-zA-Z0-9_.]+)/")

        while (r.readLine().also { line = it } != null) {
            val l = line!!
            
            val displayMatcher = displayPattern.matcher(l)
            if (displayMatcher.find()) {
                currentDisplayId = displayMatcher.group(1)?.toIntOrNull() ?: -1
            }
            
            if (currentDisplayId != displayId) continue
            
            val boundsMatcher = boundsPattern.matcher(l)
            if (boundsMatcher.find()) {
                val left = boundsMatcher.group(1)
                val top = boundsMatcher.group(2)
                val right = boundsMatcher.group(3)
                val bottom = boundsMatcher.group(4)
                currentTaskBounds = "$left,$top,$right,$bottom"
            }
            
            val rectMatcher = rectPattern.matcher(l)
            if (rectMatcher.find()) {
                val left = rectMatcher.group(1)
                val top = rectMatcher.group(2)
                val right = rectMatcher.group(3)
                val bottom = rectMatcher.group(4)
                currentTaskBounds = "$left,$top,$right,$bottom"
            }
            
            if (l.contains("ActivityRecord{") && currentTaskBounds != null) {
                val activityMatcher = activityPattern.matcher(l)
                if (activityMatcher.find()) {
                    val pkg = activityMatcher.group(1)
                    if (pkg != null && isUserApp(pkg) && !foundPackages.contains(pkg)) {
                        results.add("$pkg|$currentTaskBounds")
                        foundPackages.add(pkg)
                    }
                }
            }
        }
        
        r.close()
        p.waitFor()
    } catch (e: Exception) {
    } finally {
        Binder.restoreCallingIdentity(token)
    }
    return results
}


    // === GET TASK ID - START ===
    // Uses 'am stack list' to find task ID
    // PRIORITY: Full component match (pkg/cls) > package match > short activity match
    // Handles trampolining apps like Gemini which redirect to different packages
    // For Gemini: caches the exact task ID when found since it becomes invisible after trampoline

    // === GEMINI TASK CACHE - START ===
    // Cache for Gemini task ID since it trampolines and becomes invisible
    // The BardEntryPointActivity creates a task, then immediately redirects to Google QSB
    // After trampoline, the original task disappears from am stack list
    // We cache the exact task ID when found and reuse it for subsequent repositions
    // === GEMINI TASK CACHE - END ===

    // === GET TASK ID - START ===
    // Uses 'am stack list' to find task ID
    // PRIORITY: Full component match (pkg/cls) > package match > short activity match
    // Handles trampolining apps like Gemini which redirect to different packages
    // For Gemini: caches the exact task ID when found since it becomes invisible after trampoline

    override fun getTaskId(packageName: String, className: String?): Int {
        var exactTaskId = -1      // Best: full component match
        var packageTaskId = -1    // Good: package name match
        var fallbackTaskId = -1   // Last resort: short activity name match
        
        val token = Binder.clearCallingIdentity()
        try {
            // Log.d(TAG, "getTaskId: Looking for pkg=$packageName cls=$className")
            
            // === GEMINI DETECTION ===
            val isGemini = packageName == "com.google.android.apps.bard" || 
                          (className?.contains("Bard") == true) ||
                          (className?.contains("bard") == true)
            
            // === GEMINI CACHE CHECK - START ===
            // For Gemini, we use a very short cache validity because the original task
            // gets destroyed quickly. After ~500ms, we should always search fresh
            // to find the trampoline target instead of the dead original task.
            if (isGemini && cachedGeminiTaskId > 0) {
                val cacheAge = System.currentTimeMillis() - cachedGeminiTaskTime
                // Use very short validity - 500ms max, not 30 seconds
                // After trampoline completes, the cached ID is useless
                val shortValidity = 500L
                if (cacheAge < shortValidity) {
                    return cachedGeminiTaskId
                } else {
                    cachedGeminiTaskId = -1
                }
            }
            // === GEMINI CACHE CHECK - END ===
            
            if (isGemini) {
            }
            
            val cmd = arrayOf("sh", "-c", "am stack list")
            val p = Runtime.getRuntime().exec(cmd)
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            
            // Build component string for exact matching
            val fullComponent = if (!className.isNullOrEmpty() && className != "null" && className != "default") {
                "$packageName/$className"
            } else {
                null
            }
            
            // Short activity name (fallback only)
            val shortActivity = className?.substringAfterLast(".")
            
            while (r.readLine().also { line = it } != null) {
                val l = line!!.trim()
                
                if (!l.contains("taskId=") || !l.contains(":")) continue
                
                // Extract task ID from line
                val match = Regex("taskId=(\\d+):").find(l)
                if (match == null) continue
                
                val foundId = match.groupValues[1].toIntOrNull() ?: continue
                if (foundId <= 0) continue
                
                // PRIORITY 1: Exact full component match (highest priority)
                if (fullComponent != null && l.contains(fullComponent)) {
                    exactTaskId = foundId
                    // Keep searching - want most recent exact match
                }
                // PRIORITY 2: Package name match
                else if (l.contains("$packageName/")) {
                    packageTaskId = foundId
                }
                // PRIORITY 3: Gemini trampoline - check for Google Quick Search Box with Assistant activity
                // The actual Gemini UI runs in Google QSB with an assistant/robin activity
                // Avoid matching Android Auto ghost activities
                else if (isGemini && l.contains("com.google.android.googlequicksearchbox")) {
                    // Check if this is the actual assistant activity (not Auto ghost)
                    val isAssistantActivity = l.contains("assistant") || l.contains("robin") || l.contains("MainActivity")
                    val isAutoGhost = l.contains("auto") || l.contains("ghost")
                    
                    if (isAssistantActivity && !isAutoGhost) {
                        if (foundId > packageTaskId) {
                            packageTaskId = foundId
                        }
                    } else {
                    }
                }

                // PRIORITY 4: Short activity name (ONLY if no better match exists)
                // Skip generic names that cause false positives
                else if (shortActivity != null && 
                         shortActivity != "MainActivity" &&  // Too generic
                         shortActivity != "default" &&       // Too generic
                         l.contains(shortActivity)) {
                    fallbackTaskId = foundId
                }
            }
            r.close()
            p.waitFor()
            
            // Return best match in priority order
            val result = when {
                exactTaskId > 0 -> exactTaskId
                packageTaskId > 0 -> packageTaskId
                fallbackTaskId > 0 -> fallbackTaskId
                else -> -1
            }
            
            // === GEMINI TASK HANDLING - START ===
            // Gemini (com.google.android.apps.bard) is a trampolining app:
            // - BardEntryPointActivity creates a task, then DESTROYS it within ~40ms
            // - User is redirected to Google Quick Search Box
            // - The original task ID is useless because the task no longer exists
            // 
            // Strategy: Don't cache the destroyed task. Instead:
            // - If we have an exact match AND the task has activities, cache it
            // - If task has trampolined (no exact match), use the trampoline target
            // - For repositioning, the trampoline target (Google QSB) is what's actually running
            
            if (isGemini) {
                if (exactTaskId > 0) {
                    // We found an exact match - but is the task still alive?
                    // Check if this task actually has activities (not destroyed)
                    // For now, we'll cache it but with a very short validity
                    cachedGeminiTaskId = exactTaskId
                    cachedGeminiTaskTime = System.currentTimeMillis()
                } else if (packageTaskId > 0) {
                    // No exact match means trampoline completed
                    // The packageTaskId is the Google QSB task that Gemini is running in
                    // This is actually what we should reposition!
                    // DON'T use cached ID - it's destroyed. Use the live trampoline target.
                    // Clear any stale cache
                    if (cachedGeminiTaskId > 0) {
                        cachedGeminiTaskId = -1
                    }
                    
                    return packageTaskId
                }
            }
            // === GEMINI TASK HANDLING - END ===
            
            return result
            
        } catch (e: Exception) {
            return -1
        } finally { 
            Binder.restoreCallingIdentity(token) 
        }
    }
    // === GET TASK ID - END ===

    // === GET TASK ID - END ===

    // === GET TASK ID - END ===

    // === DEBUG DUMP TASKS - START ===
    // Dumps raw task info for debugging
    fun debugDumpTasks(): String {
        val token = Binder.clearCallingIdentity()
        val result = StringBuilder()
        try {
            val cmd = arrayOf("sh", "-c", "dumpsys activity activities | head -100")
            val p = Runtime.getRuntime().exec(cmd)
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            while (r.readLine().also { line = it } != null) {
                result.appendLine(line)
            }
            r.close()
            p.waitFor()
        } catch (e: Exception) {
            result.appendLine("ERROR: ${e.message}")
        } finally {
            Binder.restoreCallingIdentity(token)
        }
        return result.toString()
    }
    // === DEBUG DUMP TASKS - END ===

    // === MOVE TASK TO BACK / MINIMIZE TASK - START ===
    // Minimizes a task using Samsung's IMultiTaskingBinder from ActivityTaskManager
    // This is what Android's freeform minimize button uses on Samsung devices
    override fun moveTaskToBack(taskId: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            var success = false

            try {
                // Get ActivityTaskManager service
                val atmClass = Class.forName("android.app.ActivityTaskManager")
                val getServiceMethod = atmClass.getMethod("getService")
                val atm = getServiceMethod.invoke(null)

                // Call getMultiTaskingBinder()
                val getMultiTaskingBinder = atm.javaClass.getMethod("getMultiTaskingBinder")
                val multiTaskingBinder = getMultiTaskingBinder.invoke(atm)

                if (multiTaskingBinder != null) {
                    // Call minimizeTaskById(taskId)
                    val minimizeMethod = multiTaskingBinder.javaClass.getMethod(
                        "minimizeTaskById",
                        Int::class.javaPrimitiveType
                    )
                    minimizeMethod.invoke(multiTaskingBinder, taskId)

                    success = true
                } else {
                }

            } catch (e: Exception) {
            }

            // FALLBACK: Off-screen positioning (only if Samsung API failed)
            if (!success) {
                val modeCmd = "am task set-windowing-mode $taskId 5"
                Runtime.getRuntime().exec(arrayOf("sh", "-c", modeCmd)).waitFor()
                Thread.sleep(100)
                val resizeCmd = "am task resize $taskId 99999 99999 100000 100000"
                Runtime.getRuntime().exec(arrayOf("sh", "-c", resizeCmd)).waitFor()
            }

        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // === MOVE TASK TO BACK / MINIMIZE TASK - END ===

    // === MOVE TASK TO FRONT / FOCUS TASK - START ===
    // Brings a task to front using ActivityTaskManager.moveTaskToFront() via reflection.
    // This focuses the window WITHOUT re-launching any activity, preserving in-app nav state.
    override fun moveTaskToFront(taskId: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val getServiceMethod = atmClass.getMethod("getService")
            val atm = getServiceMethod.invoke(null)
            // moveTaskToFront(int taskId, int flags, Bundle options)
            val moveMethod = atm.javaClass.getMethod(
                "moveTaskToFront",
                String::class.java,  // callingPackage (Android 12+)
                String::class.java,  // callingFeatureId
                Int::class.javaPrimitiveType,  // taskId
                Int::class.javaPrimitiveType,  // flags
                android.os.Bundle::class.java  // options
            )
            moveMethod.invoke(atm, "com.katsuyamaki.DroidOSLauncher", null, taskId, 0, null)
        } catch (e: NoSuchMethodException) {
            // Fallback: Try 3-param signature (older Android)
            try {
                val atmClass = Class.forName("android.app.ActivityTaskManager")
                val getServiceMethod = atmClass.getMethod("getService")
                val atm = getServiceMethod.invoke(null)
                val moveMethod = atm.javaClass.getMethod(
                    "moveTaskToFront",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    android.os.Bundle::class.java
                )
                moveMethod.invoke(atm, taskId, 0, null)
            } catch (e2: Exception) {
            }
        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // === MOVE TASK TO FRONT / FOCUS TASK - END ===

    private fun isUserApp(pkg: String): Boolean {
        if (pkg == "com.android.systemui") return false
        if (pkg == "com.android.launcher3") return false 
        if (pkg == "com.sec.android.app.launcher") return false 
        if (pkg == "com.katsuyamaki.DroidOSLauncher") return false
        if (pkg == "com.katsuyamaki.DroidOSTrackpadKeyboard") return false
        if (pkg == "com.katsuyamaki.trackpad") return false
        if (pkg.contains("inputmethod")) return false
        if (pkg.contains("navigationbar")) return false
        if (pkg == "ScreenDecorOverlayCover") return false
        if (pkg == "RecentsTransitionOverlay") return false
        if (pkg == "FreeformContainer") return false
        if (pkg == "StatusBar") return false
        if (pkg == "NotificationShade") return false
        return true
    }

    override fun isTaskFreeform(packageName: String): Boolean {
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "am stack list"))
            val lines = BufferedReader(InputStreamReader(p.inputStream)).readLines()
            p.waitFor()
            var currentStackFreeform = false
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("Stack #") || trimmed.startsWith("RootTask #")) {
                    currentStackFreeform = trimmed.contains("type=freeform") || trimmed.contains("windowing-mode=freeform") || trimmed.contains("windowingMode=5")
                }
                if (trimmed.contains("taskId=") && trimmed.contains("$packageName/")) {
                    if (currentStackFreeform) return true
                }
            }
        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }
        return false
    }

    override fun batchResize(packages: List<String>, bounds: IntArray) {

        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "am stack list"))
            val lines = BufferedReader(InputStreamReader(p.inputStream)).readLines()
            p.waitFor()
            val taskIds = mutableMapOf<String, Int>()
            for (line in lines) {
                val trimmed = line.trim()
                if (!trimmed.contains("taskId=") || !trimmed.contains(":")) continue
                val match = Regex("taskId=(\\d+):").find(trimmed) ?: continue
                val tid = match.groupValues[1].toIntOrNull() ?: continue
                if (tid <= 0) continue
                for (pkg in packages) {
                    if (trimmed.contains("$pkg/")) {
                        val prev = taskIds[pkg] ?: -1
                        if (tid > prev) taskIds[pkg] = tid
                    }
                }
            }
            val procs = mutableListOf<Process>()
            for (i in packages.indices) {
                val tid = taskIds[packages[i]] ?: continue
                val off = i * 4
                if (off + 3 >= bounds.size) break
                val cmd = "am task resize $tid ${bounds[off]} ${bounds[off+1]} ${bounds[off+2]} ${bounds[off+3]}"
                procs.add(Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd)))
            }
            for (proc in procs) proc.waitFor()
        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    override fun setSystemBrightness(brightness: Int) { execShellCommand("settings put system screen_brightness $brightness") }


    override fun getSystemBrightness(): Int = 128
    override fun getSystemBrightnessFloat(): Float = 0.5f
    override fun setAutoBrightness(enabled: Boolean) { execShellCommand("settings put system screen_brightness_mode ${if (enabled) 1 else 0}") }
    override fun isAutoBrightness(): Boolean = true
    override fun setBrightnessViaDisplayManager(displayId: Int, brightness: Float): Boolean = setDisplayBrightnessInternal(displayId, brightness)
}
