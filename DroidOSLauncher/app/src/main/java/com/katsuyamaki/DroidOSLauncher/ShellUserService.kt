package com.katsuyamaki.DroidOSLauncher

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
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
    }

    // === EFFICIENCY CACHE ===
    private var cachedVisiblePackages: List<String> = emptyList()
    private var cachedVisibleComponents: List<String> = emptyList()
    private var lastVisibleCacheTime: Long = 0
    private var cachedDisplayId: Int = -1 // Track which display the cache is for
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
        // API 35+: private reflection into ClassLoaderFactory/Runtime.loadLibrary0
        // is blocked by non-SDK interface restrictions. Keep safe fallback path only.
        return null
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

        val tid = getTaskId(packageName, className)

        if (tid == -1) {
            android.util.Log.w("DROIDOS_DEX", "repositionTask SKIP pkg=$packageName cls=$className tid=-1 (task not found)")
            return
        }

        android.util.Log.d("DROIDOS_DEX", "repositionTask START pkg=$packageName cls=$className tid=$tid bounds=[$left,$top,$right,$bottom]")

        val token = Binder.clearCallingIdentity()
        try {
            // Set freeform windowing mode (mode 5)
            val modeCmd = "am task set-windowing-mode $tid 5"
            val modeProc = Runtime.getRuntime().exec(arrayOf("sh", "-c", modeCmd))
            val modeExit = modeProc.waitFor()
            val modeStderr = modeProc.errorStream.bufferedReader().readText().trim()
            android.util.Log.d("DROIDOS_DEX", "repositionTask SET_MODE tid=$tid exit=$modeExit stderr='$modeStderr'")
            
            // Wait for OS animation/state-change (Samsung needs ~300ms)
            Thread.sleep(300)

            // Always try ATM resizeTask first — works on DeX where shell is silently ignored
            val atmOk = resizeTaskViaATM(tid, left, top, right, bottom)
            if (!atmOk) {
                // ATM unavailable — fall back to shell command
                val resizeCmd = "am task resize $tid $left $top $right $bottom"
                val resizeProc = Runtime.getRuntime().exec(arrayOf("sh", "-c", resizeCmd))
                val exitCode = resizeProc.waitFor()
                val resizeStderr = resizeProc.errorStream.bufferedReader().readText().trim()
                android.util.Log.d("DROIDOS_DEX", "repositionTask SHELL_FALLBACK tid=$tid exit=$exitCode stderr='$resizeStderr'")
            }

        } catch (e: Exception) {
            android.util.Log.e("DROIDOS_DEX", "repositionTask EXCEPTION pkg=$packageName tid=$tid", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // === REPOSITION TASK - END ===



    // === GET VISIBLE PACKAGES / COMPONENTS - START ===
    // Components are returned as "package/class" when class is available.
    // Packages are derived from the component list.
    private fun parseWindowComponent(rawToken: String?): String? {
        val token = rawToken?.substringBefore("}")?.trim().orEmpty()
        if (token.isEmpty()) return null

        val pkg = token.substringBefore("/")
        if (!isUserApp(pkg)) return null

        return token
    }

    override fun getVisibleTaskComponents(displayId: Int): List<String> {
        val components = ArrayList<String>()
        val seenTaskIds = HashSet<Int>()
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "am stack list"))
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            var currentDisplayId = -1
            val displayPattern = Regex("displayId=(\\d+)")
            val taskPattern = Regex("taskId=(\\d+):\\s+([^\\s]+)")

            while (r.readLine().also { line = it } != null) {
                val l = line!!.trim()

                val displayMatch = displayPattern.find(l)
                if (displayMatch != null) {
                    currentDisplayId = displayMatch.groupValues[1].toIntOrNull() ?: -1
                }

                if (currentDisplayId != displayId) continue
                if (!l.contains("taskId=") || !l.contains("visible=true")) continue

                val taskMatch = taskPattern.find(l) ?: continue
                val taskId = taskMatch.groupValues[1].toIntOrNull() ?: continue
                if (taskId <= 0 || seenTaskIds.contains(taskId)) continue

                val component = parseWindowComponent(taskMatch.groupValues[2]) ?: continue
                components.add("$taskId|$component")
                seenTaskIds.add(taskId)
            }

            r.close()
            p.waitFor()
        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }

        return components
    }

    override fun getVisibleComponents(displayId: Int): List<String> {
        val now = System.currentTimeMillis()

        // [EFFICIENCY] Return cached result if valid AND for same display
        if (now - lastVisibleCacheTime < VISIBLE_CACHE_TTL && cachedVisibleComponents.isNotEmpty() && cachedDisplayId == displayId) {
            return ArrayList(cachedVisibleComponents)
        }

        val components = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec("dumpsys window windows")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            var currentComponent: String? = null
            var isVisible = false
            var onCorrectDisplay = false
            var isOffScreen = false
            val windowPattern = Pattern.compile("Window\\{[0-9a-f]+ u\\d+ ([^ ]+)")
            val framePattern = Pattern.compile("(?:frame|mFrame)=\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]")

            while (r.readLine().also { line = it } != null) {
                val l = line!!.trim()

                if (l.startsWith("Window #")) {
                    currentComponent = null
                    isVisible = false
                    onCorrectDisplay = false
                    isOffScreen = false
                    val matcher = windowPattern.matcher(l)
                    if (matcher.find()) {
                        currentComponent = parseWindowComponent(matcher.group(1))
                    }
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
                        }
                    } catch (e: Exception) {}
                }

                if (currentComponent != null && isVisible && onCorrectDisplay && !isOffScreen) {
                    if (!components.contains(currentComponent!!)) {
                        components.add(currentComponent!!)
                    }
                    currentComponent = null
                }
            }
            r.close()
            p.waitFor()

            val packages = LinkedHashSet<String>()
            for (component in components) {
                val pkg = component.substringBefore("/")
                if (pkg.isNotEmpty()) packages.add(pkg)
            }

            // [EFFICIENCY] Update cache for both component + package views
            cachedVisibleComponents = ArrayList(components)
            cachedVisiblePackages = ArrayList(packages)
            lastVisibleCacheTime = now
            cachedDisplayId = displayId

        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }

        return ArrayList(cachedVisibleComponents)
    }

    override fun getVisiblePackages(displayId: Int): List<String> {
        val now = System.currentTimeMillis()

        // [EFFICIENCY] Return cached result if valid AND for same display
        if (now - lastVisibleCacheTime < VISIBLE_CACHE_TTL && cachedVisiblePackages.isNotEmpty() && cachedDisplayId == displayId) {
            return ArrayList(cachedVisiblePackages)
        }

        val components = getVisibleComponents(displayId)
        val packages = LinkedHashSet<String>()
        for (component in components) {
            val pkg = component.substringBefore("/")
            if (pkg.isNotEmpty()) packages.add(pkg)
        }

        cachedVisiblePackages = ArrayList(packages)
        if (cachedDisplayId != displayId) cachedDisplayId = displayId
        if (lastVisibleCacheTime == 0L) lastVisibleCacheTime = now

        return ArrayList(cachedVisiblePackages)
    }

    // [EFFICIENCY] Manual cache invalidation for instant UI updates after actions
    fun invalidateVisibleCache() {
        lastVisibleCacheTime = 0
        cachedVisiblePackages = emptyList()
        cachedVisibleComponents = emptyList()
        cachedDisplayId = -1
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
        // Display-aware task IDs: prefer tasks on the active display
        var exactVisibleTaskId = -1
        var exactTaskId = -1
        var packageVisibleTaskId = -1
        var packageTaskId = -1
        var fallbackVisibleTaskId = -1
        var fallbackTaskId = -1
        // Off-display fallbacks (tasks on wrong display, only used if no on-display match)
        var offDisplayExactTaskId = -1
        var offDisplayPackageTaskId = -1
        var offDisplayFallbackTaskId = -1

        val normalizedTargetPackage = AppCompatibilityRegistry.normalizePackage(packageName)
        val taskMatchPackages = AppCompatibilityRegistry.taskMatchPackagesFor(normalizedTargetPackage)
        val preferPackageTask = AppCompatibilityRegistry.shouldPreferPackageTaskMatch(normalizedTargetPackage)
        val resolvedClass = AppCompatibilityRegistry.resolveLaunchClass(normalizedTargetPackage, className)
        val strictTaskScopedClass =
            resolvedClass != null &&
                AppCompatibilityRegistry.shouldAutoSyncObservedComponents(normalizedTargetPackage) &&
                !AppCompatibilityRegistry.isProxyActivity(normalizedTargetPackage, resolvedClass)
        val shouldTrace = AppCompatibilityRegistry.shouldAutoSyncObservedComponents(normalizedTargetPackage)
        val traceCandidates = mutableListOf<String>()
        val activeDisplay = cachedDisplayId

        val isGemini = AppCompatibilityRegistry.packagesEquivalentForTaskIdentity(
            normalizedTargetPackage,
            "com.google.android.apps.bard"
        ) || AppCompatibilityRegistry.packagesEquivalentForTaskIdentity(
            normalizedTargetPackage,
            "com.google.android.googlequicksearchbox"
        ) || (className?.contains("Bard", ignoreCase = true) == true)

        val token = Binder.clearCallingIdentity()
        try {
            // For Gemini, keep very short cache because trampoline task IDs expire quickly.
            if (isGemini && cachedGeminiTaskId > 0) {
                val cacheAge = System.currentTimeMillis() - cachedGeminiTaskTime
                val shortValidity = 500L
                if (cacheAge < shortValidity) {
                    return cachedGeminiTaskId
                } else {
                    cachedGeminiTaskId = -1
                }
            }

            val cmd = arrayOf("sh", "-c", "am stack list")
            val p = Runtime.getRuntime().exec(cmd)
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?

            val fullComponent = if (resolvedClass != null) {
                "$normalizedTargetPackage/$resolvedClass"
            } else {
                null
            }

            val shortActivity = resolvedClass?.substringAfterLast(".")
            val displayPattern = Regex("displayId=(\\d+)")
            var currentDisplayId = -1

            while (r.readLine().also { line = it } != null) {
                val l = line!!.trim()

                // Track display context from stack/root-task headers
                val displayMatch = displayPattern.find(l)
                if (displayMatch != null) {
                    currentDisplayId = displayMatch.groupValues[1].toIntOrNull() ?: -1
                }

                if (!l.contains("taskId=") || !l.contains(":")) continue

                val match = Regex("taskId=(\\d+):").find(l)
                if (match == null) continue

                val foundId = match.groupValues[1].toIntOrNull() ?: continue
                if (foundId <= 0) continue

                val isVisible = l.contains("visible=true")
                val onActiveDisplay = activeDisplay < 0 || currentDisplayId < 0 || currentDisplayId == activeDisplay

                if (fullComponent != null && l.contains(fullComponent)) {
                    if (onActiveDisplay) {
                        exactTaskId = maxOf(exactTaskId, foundId)
                        if (isVisible) exactVisibleTaskId = maxOf(exactVisibleTaskId, foundId)
                    } else {
                        offDisplayExactTaskId = maxOf(offDisplayExactTaskId, foundId)
                    }
                    if (shouldTrace && traceCandidates.size < 10) {
                        traceCandidates.add("exact:tid=$foundId vis=$isVisible disp=$currentDisplayId onActive=$onActiveDisplay")
                    }
                } else if (taskMatchPackages.any { taskPkg -> l.contains("$taskPkg/") }) {
                    if (onActiveDisplay) {
                        packageTaskId = maxOf(packageTaskId, foundId)
                        if (isVisible) packageVisibleTaskId = maxOf(packageVisibleTaskId, foundId)
                    } else {
                        offDisplayPackageTaskId = maxOf(offDisplayPackageTaskId, foundId)
                    }
                    if (shouldTrace && traceCandidates.size < 10) {
                        traceCandidates.add("pkg:tid=$foundId vis=$isVisible disp=$currentDisplayId onActive=$onActiveDisplay")
                    }
                } else if (shortActivity != null &&
                    shortActivity != "MainActivity" &&
                    shortActivity != "default" &&
                    l.contains(shortActivity)
                ) {
                    if (onActiveDisplay) {
                        fallbackTaskId = maxOf(fallbackTaskId, foundId)
                        if (isVisible) fallbackVisibleTaskId = maxOf(fallbackVisibleTaskId, foundId)
                    } else {
                        offDisplayFallbackTaskId = maxOf(offDisplayFallbackTaskId, foundId)
                    }
                    if (shouldTrace && traceCandidates.size < 10) {
                        traceCandidates.add("fallback:tid=$foundId vis=$isVisible disp=$currentDisplayId onActive=$onActiveDisplay")
                    }
                }
            }
            r.close()
            p.waitFor()

            val result = if (strictTaskScopedClass) {
                when {
                    exactVisibleTaskId > 0 -> exactVisibleTaskId
                    exactTaskId > 0 -> exactTaskId
                    // Off-display fallback for strict scoped
                    offDisplayExactTaskId > 0 -> offDisplayExactTaskId
                    else -> -1
                }
            } else if (preferPackageTask) {
                when {
                    packageVisibleTaskId > 0 -> packageVisibleTaskId
                    packageTaskId > 0 -> packageTaskId
                    exactVisibleTaskId > 0 -> exactVisibleTaskId
                    exactTaskId > 0 -> exactTaskId
                    fallbackVisibleTaskId > 0 -> fallbackVisibleTaskId
                    fallbackTaskId > 0 -> fallbackTaskId
                    // Off-display fallbacks (last resort)
                    offDisplayPackageTaskId > 0 -> offDisplayPackageTaskId
                    offDisplayExactTaskId > 0 -> offDisplayExactTaskId
                    offDisplayFallbackTaskId > 0 -> offDisplayFallbackTaskId
                    else -> -1
                }
            } else {
                when {
                    exactVisibleTaskId > 0 -> exactVisibleTaskId
                    exactTaskId > 0 -> exactTaskId
                    packageVisibleTaskId > 0 -> packageVisibleTaskId
                    packageTaskId > 0 -> packageTaskId
                    fallbackVisibleTaskId > 0 -> fallbackVisibleTaskId
                    fallbackTaskId > 0 -> fallbackTaskId
                    // Off-display fallbacks (last resort)
                    offDisplayExactTaskId > 0 -> offDisplayExactTaskId
                    offDisplayPackageTaskId > 0 -> offDisplayPackageTaskId
                    offDisplayFallbackTaskId > 0 -> offDisplayFallbackTaskId
                    else -> -1
                }
            }

            if (shouldTrace || (activeDisplay >= 0 && (offDisplayExactTaskId > 0 || offDisplayPackageTaskId > 0 || offDisplayFallbackTaskId > 0))) {
                android.util.Log.d(
                    "DROIDOS_TASK_RESOLVE",
                    "pkg=$normalizedTargetPackage cls=${resolvedClass ?: "<pkg>"} preferPkg=$preferPackageTask strictTaskScoped=$strictTaskScopedClass " +
                        "exactV=$exactVisibleTaskId exact=$exactTaskId pkgV=$packageVisibleTaskId pkg=$packageTaskId " +
                        "fbV=$fallbackVisibleTaskId fb=$fallbackTaskId " +
                        "offExact=$offDisplayExactTaskId offPkg=$offDisplayPackageTaskId offFb=$offDisplayFallbackTaskId " +
                        "activeDisplay=$activeDisplay result=$result candidates=${traceCandidates.joinToString("|")}"
                )
            }

            if (isGemini) {
                if (exactTaskId > 0) {
                    cachedGeminiTaskId = exactTaskId
                    cachedGeminiTaskTime = System.currentTimeMillis()
                } else if (packageTaskId > 0) {
                    if (cachedGeminiTaskId > 0) {
                        cachedGeminiTaskId = -1
                    }
                    return packageTaskId
                }
            }

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

    // === SAMSUNG DETECTION HELPERS - START ===
    // Detects Samsung devices by checking if MultiTaskingBinder exists.
    // Used to gate Samsung-specific minimize verification and ATM API paths.
    private var samsungMultiTaskingChecked = false
    private var samsungMultiTaskingAvailable = false

    private fun hasSamsungMultiTaskingBinder(): Boolean {
        if (samsungMultiTaskingChecked) return samsungMultiTaskingAvailable
        samsungMultiTaskingChecked = true
        try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val atm = atmClass.getMethod("getService").invoke(null)
            val binder = atm.javaClass.getMethod("getMultiTaskingBinder").invoke(atm)
            samsungMultiTaskingAvailable = binder != null
        } catch (e: Exception) {
            samsungMultiTaskingAvailable = false
        }
        android.util.Log.d("DROIDOS_DEX", "hasSamsungMultiTaskingBinder=$samsungMultiTaskingAvailable")
        return samsungMultiTaskingAvailable
    }

    // Queries Samsung MultiTaskingBinder for the list of actually-minimized freeform task IDs.
    // Used to verify whether a minimize call truly took effect on DeX.
    private fun getMinimizedFreeformTaskIds(): Set<Int> {
        try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val atm = atmClass.getMethod("getService").invoke(null)
            val binder = atm.javaClass.getMethod("getMultiTaskingBinder").invoke(atm) ?: return emptySet()
            val rawResult = binder.javaClass.getMethod("getMinimizedFreeformTasksForCurrentUser").invoke(binder)
            // Unwrap ParceledListSlice → List if needed
            val tasks: List<*>? = when {
                rawResult is List<*> -> rawResult
                rawResult != null -> {
                    try {
                        val getListMethod = rawResult.javaClass.getMethod("getList")
                        getListMethod.invoke(rawResult) as? List<*>
                    } catch (_: Exception) { null }
                }
                else -> null
            }
            if (tasks != null) {
                val ids = mutableSetOf<Int>()
                for (task in tasks) {
                    if (task == null) continue
                    try {
                        val taskIdField = task.javaClass.getField("taskId")
                        ids.add(taskIdField.getInt(task))
                    } catch (e: Exception) {
                        try {
                            val idField = task.javaClass.getField("id")
                            ids.add(idField.getInt(task))
                        } catch (_: Exception) {}
                    }
                }
                android.util.Log.d("DROIDOS_DEX", "getMinimizedFreeformTaskIds ids=$ids")
                return ids
            }
            android.util.Log.d("DROIDOS_DEX", "getMinimizedFreeformTaskIds result type=${rawResult?.javaClass?.name} value=$rawResult")
            return emptySet()
        } catch (e: Exception) {
            android.util.Log.w("DROIDOS_DEX", "getMinimizedFreeformTaskIds failed: ${e.message}")
            return emptySet()
        }
    }

    // Checks whether a specific task is visible on the DeX display using Samsung API.
    private fun isVisibleTaskOnDex(taskId: Int): Boolean {
        try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val atm = atmClass.getMethod("getService").invoke(null)
            val binder = atm.javaClass.getMethod("getMultiTaskingBinder").invoke(atm) ?: return false
            val result = binder.javaClass.getMethod(
                "isVisibleTaskByTaskIdInDexDisplay",
                Int::class.javaPrimitiveType
            ).invoke(binder, taskId)
            return result == true
        } catch (e: Exception) {
            return false
        }
    }

    // Checks visibility using stack snapshot (independent of Samsung binder helpers).
    // This catches states where minimize API returns but the task still remains visible.
    private fun isTaskVisibleInStack(taskId: Int): Boolean {
        if (taskId <= 0) return false
        return try {
            val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "am stack list"))
            val lines = BufferedReader(InputStreamReader(p.inputStream)).readLines()
            p.waitFor()
            lines.any { line ->
                val trimmed = line.trim()
                trimmed.contains("taskId=$taskId:") && trimmed.contains("visible=true")
            }
        } catch (e: Exception) {
            false
        }
    }

    // Direct ATM resizeTask via reflection — bypasses `am task resize` shell command.
    // Shell `am task resize` is silently ignored on Samsung DeX; this direct API works.
    private fun resizeTaskViaATM(taskId: Int, left: Int, top: Int, right: Int, bottom: Int): Boolean {
        try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val atm = atmClass.getMethod("getService").invoke(null)
            val rect = android.graphics.Rect(left, top, right, bottom)
            // resizeTask(int taskId, Rect bounds, int resizeMode)
            // resizeMode 0 = RESIZE_MODE_SYSTEM
            val resizeMethod = atm.javaClass.getMethod(
                "resizeTask",
                Int::class.javaPrimitiveType,
                android.graphics.Rect::class.java,
                Int::class.javaPrimitiveType
            )
            resizeMethod.invoke(atm, taskId, rect, 0)
            android.util.Log.d("DROIDOS_DEX", "resizeTaskViaATM SUCCESS tid=$taskId bounds=[$left,$top,$right,$bottom]")
            return true
        } catch (e: Exception) {
            android.util.Log.w("DROIDOS_DEX", "resizeTaskViaATM FAILED tid=$taskId: ${e.javaClass.simpleName}: ${e.message}")
            return false
        }
    }
    // === SAMSUNG DETECTION HELPERS - END ===

    // moveRootTaskToDisplay crashes SystemUI/WindowManager on Samsung DeX.
    // Stub kept for AIDL compatibility; callers should use shell am task move-task-to-display instead.
    override fun moveTaskToDisplay(taskId: Int, displayId: Int): Boolean {
        android.util.Log.w("DROIDOS_DEX", "moveTaskToDisplay DISABLED (crashes system) tid=$taskId displayId=$displayId")
        return false
    }

    // === MOVE TASK TO BACK / MINIMIZE TASK - START ===
    // On Samsung: complete no-op. Samsung binder APIs (minimizeTaskById, saveFreeformBounds,
    // minimizeTaskToSpecificPosition, removeFocusedTask) dont work for 3rd party apps and
    // add system stress with multiple reflection calls + Thread.sleep. Real minimize goes
    // through minimizeToHiddenDisplay (am task move-task-to-display / am start --display).
    override fun moveTaskToBack(taskId: Int): Boolean {
        val isSamsung = hasSamsungMultiTaskingBinder()
        android.util.Log.d("DROIDOS_DEX", "moveTaskToBack START tid=$taskId samsung=$isSamsung")
        if (isSamsung) {
            android.util.Log.d("DROIDOS_DEX", "moveTaskToBack DONE tid=$taskId success=false (samsung-noop)")
            return false
        }
        val token = Binder.clearCallingIdentity()
        try {
            var success = false

            // FALLBACK 1: platform task minimize command.
            if (!success) {
                try {
                    val moveBackProc = Runtime.getRuntime()
                        .exec(arrayOf("sh", "-c", "am task move-task-to-back $taskId"))
                    val moveBackExit = moveBackProc.waitFor()
                    val moveBackStderr = moveBackProc.errorStream.bufferedReader().readText().trim()
                    success = moveBackExit == 0

                    if (success) {
                        Thread.sleep(120)
                        val visibleAfterMoveBack = isTaskVisibleInStack(taskId)
                        if (visibleAfterMoveBack) {
                            success = false
                        }
                        android.util.Log.d(
                            "DROIDOS_DEX",
                            "moveTaskToBack FALLBACK1_VERIFY tid=$taskId visibleInStack=$visibleAfterMoveBack"
                        )
                    }

                    android.util.Log.d("DROIDOS_DEX", "moveTaskToBack FALLBACK1_MOVE_BACK tid=$taskId exit=$moveBackExit success=$success stderr='$moveBackStderr'")
                } catch (e: Exception) {
                    android.util.Log.w("DROIDOS_DEX", "moveTaskToBack FALLBACK1 exception tid=$taskId: ${e.message}")
                }
            }

            // FALLBACK 2: off-screen positioning only as last resort.
            if (!success) {
                android.util.Log.d("DROIDOS_DEX", "moveTaskToBack FALLBACK2_OFFSCREEN tid=$taskId")
                val modeCmd = "am task set-windowing-mode $taskId 5"
                val modeExit = Runtime.getRuntime().exec(arrayOf("sh", "-c", modeCmd)).waitFor()
                Thread.sleep(100)
                val resizeCmd = "am task resize $taskId 99999 99999 100000 100000"
                val resizeExit = Runtime.getRuntime().exec(arrayOf("sh", "-c", resizeCmd)).waitFor()
                Thread.sleep(120)
                success = modeExit == 0 && resizeExit == 0 && !isTaskVisibleInStack(taskId)
                android.util.Log.d(
                    "DROIDOS_DEX",
                    "moveTaskToBack FALLBACK2_RESULT tid=$taskId modeExit=$modeExit resizeExit=$resizeExit success=$success"
                )
            }

            android.util.Log.d("DROIDOS_DEX", "moveTaskToBack DONE tid=$taskId success=$success")
            return success

        } catch (e: Exception) {
            android.util.Log.e("DROIDOS_DEX", "moveTaskToBack OUTER_EXCEPTION tid=$taskId", e)
            return false
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // === MOVE TASK TO BACK / MINIMIZE TASK - END ===

    // === RESTORE MINIMIZED TASK - START ===
    // Restores a Samsung-minimized task using startActivityFromRecents.
    // This is the API Android launchers use internally to bring back recent/minimized tasks.
    // Unlike moveTaskToFront, this properly handles Samsung's minimized state.
    override fun restoreMinimizedTask(taskId: Int, displayId: Int): Boolean {
        android.util.Log.d("DROIDOS_DEX", "restoreMinimizedTask START tid=$taskId displayId=$displayId")
        val token = Binder.clearCallingIdentity()
        try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val getServiceMethod = atmClass.getMethod("getService")
            val atm = getServiceMethod.invoke(null)

            val options = android.app.ActivityOptions.makeBasic().apply {
                if (displayId >= 0) setLaunchDisplayId(displayId)
                // Request freeform windowing mode
                try {
                    val setWindowingMode = javaClass.getMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType)
                    setWindowingMode.invoke(this, 5)
                } catch (e: Exception) {
                    android.util.Log.w("DROIDOS_DEX", "restoreMinimizedTask setLaunchWindowingMode not available: ${e.message}")
                }
            }.toBundle()

            // Primary: startActivityFromRecents — this is how Android restores minimized/recent tasks.
            try {
                val startFromRecents = atm.javaClass.getMethod(
                    "startActivityFromRecents",
                    Int::class.javaPrimitiveType,
                    android.os.Bundle::class.java
                )
                val result = startFromRecents.invoke(atm, taskId, options)
                android.util.Log.d("DROIDOS_DEX", "restoreMinimizedTask FROM_RECENTS tid=$taskId result=$result")
                if (result is Int && result >= 0) {
                    return true
                }
            } catch (e: Exception) {
                android.util.Log.w("DROIDOS_DEX", "restoreMinimizedTask FROM_RECENTS failed tid=$taskId: ${e.javaClass.simpleName}: ${e.message}")
            }

            android.util.Log.w("DROIDOS_DEX", "restoreMinimizedTask FAILED tid=$taskId")
            return false
        } catch (e: Exception) {
            android.util.Log.e("DROIDOS_DEX", "restoreMinimizedTask OUTER_EXCEPTION tid=$taskId", e)
            return false
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // === RESTORE MINIMIZED TASK - END ===

    // === DUMP MULTITASKING METHODS - START ===
    // Debug helper: enumerate all methods on Samsung's MultiTaskingBinder.
    override fun dumpMultiTaskingMethods(): String {
        val token = Binder.clearCallingIdentity()
        try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val getServiceMethod = atmClass.getMethod("getService")
            val atm = getServiceMethod.invoke(null)

            val result = StringBuilder()

            // Dump IActivityTaskManager methods that contain relevant keywords.
            result.appendLine("=== IActivityTaskManager methods ===")
            for (m in atm.javaClass.methods) {
                val name = m.name.lowercase()
                if (name.contains("task") || name.contains("minimize") || name.contains("maximize") ||
                    name.contains("restore") || name.contains("display") || name.contains("recents") ||
                    name.contains("windowing") || name.contains("freeform") || name.contains("root")) {
                    result.appendLine("${m.name}(${m.parameterTypes.joinToString(", ") { it.simpleName }})")
                }
            }

            // Dump MultiTaskingBinder methods.
            try {
                val getMultiTaskingBinder = atm.javaClass.getMethod("getMultiTaskingBinder")
                val binder = getMultiTaskingBinder.invoke(atm)
                if (binder != null) {
                    result.appendLine("=== MultiTaskingBinder methods ===")
                    for (m in binder.javaClass.methods) {
                        result.appendLine("${m.name}(${m.parameterTypes.joinToString(", ") { it.simpleName }})")
                    }
                } else {
                    result.appendLine("MultiTaskingBinder is null")
                }
            } catch (e: Exception) {
                result.appendLine("MultiTaskingBinder error: ${e.message}")
            }

            return result.toString()
        } catch (e: Exception) {
            return "Error: ${e.message}"
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // === DUMP MULTITASKING METHODS - END ===

    // === MOVE TASK TO FRONT / FOCUS TASK - START ===
    // Brings a task to front using ActivityTaskManager.moveTaskToFront() via reflection.
    // This focuses the window WITHOUT re-launching any activity, preserving in-app nav state.
    override fun moveTaskToFront(taskId: Int, displayId: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            val options = try {
                android.app.ActivityOptions.makeBasic().apply {
                    if (displayId >= 0) setLaunchDisplayId(displayId)
                }.toBundle()
            } catch (e: Exception) {
                null
            }

            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val getServiceMethod = atmClass.getMethod("getService")
            val atm = getServiceMethod.invoke(null)

            var moved = false

            // Preferred signature on newer Android builds.
            try {
                val moveMethod = atm.javaClass.getMethod(
                    "moveTaskToFront",
                    String::class.java,  // callingPackage
                    String::class.java,  // callingFeatureId
                    Int::class.javaPrimitiveType,  // taskId
                    Int::class.javaPrimitiveType,  // flags
                    android.os.Bundle::class.java  // options
                )
                moveMethod.invoke(atm, "com.katsuyamaki.DroidOSLauncher", null, taskId, 0, options)
                moved = true
            } catch (e: Exception) {
            }

            // Fallback signature used on some builds/devices.
            if (!moved) {
                try {
                    val moveMethod = atm.javaClass.getMethod(
                        "moveTaskToFront",
                        Int::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType,
                        android.os.Bundle::class.java
                    )
                    moveMethod.invoke(atm, taskId, 0, options)
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // === MOVE TASK TO FRONT / FOCUS TASK - END ===

    // === SET FOCUSED TASK - START ===
    // Transfers real input focus (IME target) to a task using ActivityTaskManager.setFocusedTask().
    // Unlike moveTaskToFront which only changes Z-order, this properly transfers the IME
    // input connection so text input goes to the correct app — critical for subactivities.
    override fun setFocusedTask(taskId: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val getServiceMethod = atmClass.getMethod("getService")
            val atm = getServiceMethod.invoke(null)

            // setFocusedTask: available in AOSP since Android 10 (IActivityTaskManager)
            try {
                val setFocusMethod = atm.javaClass.getMethod(
                    "setFocusedTask",
                    Int::class.javaPrimitiveType
                )
                setFocusMethod.invoke(atm, taskId)
                return
            } catch (_: NoSuchMethodException) {
            }

            // Fallback: setFocusedStack (Android 7-11, some OEM builds)
            try {
                val setFocusMethod = atm.javaClass.getMethod(
                    "setFocusedStack",
                    Int::class.javaPrimitiveType
                )
                setFocusMethod.invoke(atm, taskId)
                return
            } catch (_: NoSuchMethodException) {
            }

            // Last resort: setFocusedRootTask (Android 12+ renamed API)
            try {
                val setFocusMethod = atm.javaClass.getMethod(
                    "setFocusedRootTask",
                    Int::class.javaPrimitiveType
                )
                setFocusMethod.invoke(atm, taskId)
            } catch (_: NoSuchMethodException) {
            }
        } catch (e: Exception) {
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // === SET FOCUSED TASK - END ===

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
            var currentStackHeader = ""
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("Stack #") || trimmed.startsWith("RootTask #")) {
                    currentStackFreeform = trimmed.contains("type=freeform") || trimmed.contains("windowing-mode=freeform") || trimmed.contains("windowingMode=5")
                    currentStackHeader = trimmed
                }
                if (trimmed.contains("taskId=") && trimmed.contains("$packageName/")) {
                    android.util.Log.d("DROIDOS_DEX", "isTaskFreeform pkg=$packageName freeform=$currentStackFreeform stack='$currentStackHeader' task='$trimmed'")
                    if (currentStackFreeform) return true
                }
            }
            android.util.Log.d("DROIDOS_DEX", "isTaskFreeform pkg=$packageName result=false (not found in freeform stack)")
        } catch (e: Exception) {
            android.util.Log.e("DROIDOS_DEX", "isTaskFreeform pkg=$packageName exception", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
        return false
    }

    private data class StackTaskInfo(
        val taskId: Int,
        val packageName: String,
        val className: String?,
        val displayId: Int = -1,
        val visible: Boolean = false
    )

    private fun normalizeResizeClass(packageName: String, className: String?): String? {
        val trimmed = className?.trim().orEmpty()
        if (trimmed.isEmpty() || trimmed == "null" || trimmed == "default") return null
        return if (trimmed.startsWith(".")) "$packageName$trimmed" else trimmed
    }

    private fun parseStackTasksForResize(): List<StackTaskInfo> {
        val tasks = mutableListOf<StackTaskInfo>()
        try {
            val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "am stack list"))
            val lines = BufferedReader(InputStreamReader(p.inputStream)).readLines()
            p.waitFor()

            val displayPattern = Regex("displayId=(\\d+)")
            val taskPattern = Regex("taskId=(\\d+):\\s+([^\\s]+)")
            var currentDisplayId = -1

            for (line in lines) {
                val trimmed = line.trim()

                // Track display context from stack/root-task header lines
                val displayMatch = displayPattern.find(trimmed)
                if (displayMatch != null) {
                    currentDisplayId = displayMatch.groupValues[1].toIntOrNull() ?: -1
                }

                val match = taskPattern.find(trimmed) ?: continue

                val tid = match.groupValues[1].toIntOrNull() ?: continue
                if (tid <= 0) continue

                val isVisible = trimmed.contains("visible=true")

                val rawComponent = match.groupValues[2]
                val rawPackage = rawComponent.substringBefore("/")
                val normalizedPackage = AppCompatibilityRegistry.normalizePackage(rawPackage)
                if (normalizedPackage.isEmpty() || !isUserApp(normalizedPackage)) continue

                val rawClass = if (rawComponent.contains("/")) rawComponent.substringAfter("/") else null
                val normalizedClass = normalizeResizeClass(normalizedPackage, rawClass)

                tasks.add(
                    StackTaskInfo(
                        taskId = tid,
                        packageName = normalizedPackage,
                        className = normalizedClass,
                        displayId = currentDisplayId,
                        visible = isVisible
                    )
                )
            }
        } catch (e: Exception) {
        }
        return tasks.sortedByDescending { it.taskId }
    }

    private fun scoreTaskForResizeRequest(
        task: StackTaskInfo,
        requestPackage: String,
        requestClass: String?
    ): Int {
        val samePackage = task.packageName == requestPackage
        val identityMatch = samePackage ||
            AppCompatibilityRegistry.packagesEquivalentForTaskIdentity(task.packageName, requestPackage)
        if (!identityMatch) return Int.MIN_VALUE

        if (samePackage && requestClass != null && task.className != null) {
            if (task.className == requestClass) return 100

            val requestShort = requestClass.substringAfterLast(".")
            val taskShort = task.className.substringAfterLast(".")
            if (requestShort == taskShort) return 95

            val bothProxy =
                AppCompatibilityRegistry.isProxyActivity(requestPackage, requestClass) &&
                    AppCompatibilityRegistry.isProxyActivity(task.packageName, task.className)
            if (bothProxy) return 90
        }

        if (samePackage) return 70

        if (requestClass != null && task.className != null) {
            val bothProxyEquivalent =
                AppCompatibilityRegistry.isProxyActivity(requestPackage, requestClass) &&
                    AppCompatibilityRegistry.isProxyActivity(task.packageName, task.className)
            if (bothProxyEquivalent) return 60
        }

        return 50
    }

    private fun resolveTaskIdsForResize(packages: List<String>, classes: List<String?>): IntArray {
        val resolved = IntArray(packages.size) { -1 }
        if (packages.isEmpty()) return resolved

        val tasks = parseStackTasksForResize()
        val usedTaskIds = mutableSetOf<Int>()
        val activeDisplay = cachedDisplayId

        for (i in packages.indices) {
            val requestPackage = AppCompatibilityRegistry.normalizePackage(packages[i])
            if (requestPackage.isEmpty()) continue

            val requestClass = classes.getOrNull(i)
            var bestTask: StackTaskInfo? = null
            var bestScore = Int.MIN_VALUE

            for (task in tasks) {
                if (task.taskId in usedTaskIds) continue
                var score = scoreTaskForResizeRequest(task, requestPackage, requestClass)
                if (score <= Int.MIN_VALUE) continue

                // Strongly prefer tasks on the active display (DeX display 8 vs phone display 0)
                if (activeDisplay >= 0 && task.displayId >= 0 && task.displayId != activeDisplay) {
                    score -= 200
                    android.util.Log.d("DROIDOS_DEX", "resolveTaskIdsForResize DISPLAY_MISMATCH pkg=$requestPackage tid=${task.taskId} taskDisplay=${task.displayId} activeDisplay=$activeDisplay score=$score")
                }

                val shouldReplace =
                    score > bestScore ||
                        (score == bestScore && (bestTask == null || task.taskId > bestTask.taskId))
                if (shouldReplace) {
                    bestTask = task
                    bestScore = score
                }
            }

            if (bestTask != null) {
                resolved[i] = bestTask.taskId
                usedTaskIds.add(bestTask.taskId)
                continue
            }

            val fallbackTaskId = getTaskId(requestPackage, requestClass)
            if (fallbackTaskId > 0 && fallbackTaskId !in usedTaskIds) {
                resolved[i] = fallbackTaskId
                usedTaskIds.add(fallbackTaskId)
            }
        }

        return resolved
    }

    override fun batchResize(packages: List<String>, bounds: IntArray) {
        val emptyClasses = List(packages.size) { "" }
        batchResizeComponents(packages, emptyClasses, bounds)
    }

    override fun batchResizeComponents(packages: List<String>, classes: List<String>, bounds: IntArray) {
        if (packages.isEmpty() || bounds.isEmpty()) return

        android.util.Log.d("DROIDOS_DEX", "batchResizeComponents START count=${packages.size} boundsLen=${bounds.size} activeDisplay=$cachedDisplayId")

        val token = Binder.clearCallingIdentity()
        try {
            val normalizedPackages = packages.map { AppCompatibilityRegistry.normalizePackage(it) }
            val normalizedClasses = normalizedPackages.indices.map { index ->
                normalizeResizeClass(normalizedPackages[index], classes.getOrNull(index))
            }
            val taskIds = resolveTaskIdsForResize(normalizedPackages, normalizedClasses)

            for (i in normalizedPackages.indices) {
                android.util.Log.d("DROIDOS_DEX", "batchResizeComponents [$i] pkg=${normalizedPackages[i]} cls=${normalizedClasses.getOrNull(i)} tid=${taskIds.getOrNull(i) ?: -1}")
            }

            // Always try ATM resizeTask first (works on DeX + non-DeX), shell fallback
            var resizedCount = 0
            for (i in normalizedPackages.indices) {
                val tid = taskIds.getOrNull(i) ?: -1
                if (tid <= 0) {
                    android.util.Log.w("DROIDOS_DEX", "batchResizeComponents SKIP [$i] pkg=${normalizedPackages[i]} tid=$tid")
                    continue
                }

                val off = i * 4
                if (off + 3 >= bounds.size) {
                    android.util.Log.w("DROIDOS_DEX", "batchResizeComponents BOUNDS_OVERFLOW [$i] off=$off boundsLen=${bounds.size}")
                    break
                }

                val left = bounds[off]; val top = bounds[off + 1]; val right = bounds[off + 2]; val bottom = bounds[off + 3]
                val atmOk = resizeTaskViaATM(tid, left, top, right, bottom)
                if (!atmOk) {
                    // ATM reflection unavailable — fall back to shell command
                    val cmd = "am task resize $tid $left $top $right $bottom"
                    val proc = Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
                    val exitCode = proc.waitFor()
                    android.util.Log.d("DROIDOS_DEX", "batchResizeComponents SHELL_FALLBACK [$i] tid=$tid exit=$exitCode")
                }
                resizedCount++
            }

            android.util.Log.d("DROIDOS_DEX", "batchResizeComponents DONE resized=$resizedCount/${packages.size}")
        } catch (e: Exception) {
            android.util.Log.e("DROIDOS_DEX", "batchResizeComponents EXCEPTION", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    override fun getTaskDebugSnapshot(packageName: String): String {
        val normalized = AppCompatibilityRegistry.normalizePackage(packageName)
        if (normalized.isEmpty()) return "empty-package"

        val token = Binder.clearCallingIdentity()
        return try {
            val relatedPackages = AppCompatibilityRegistry.taskMatchPackagesFor(normalized)
            val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "am stack list"))
            val lines = BufferedReader(InputStreamReader(p.inputStream)).readLines()
            p.waitFor()

            val matched = mutableListOf<String>()
            for (line in lines) {
                val trimmed = line.trim()
                if (!trimmed.contains("taskId=")) continue

                if (relatedPackages.any { candidate -> trimmed.contains("$candidate/") }) {
                    matched.add(trimmed)
                    if (matched.size >= 24) break
                }
            }

            if (matched.isEmpty()) {
                "no-task-lines"
            } else {
                matched.joinToString("\n")
            }
        } catch (e: Exception) {
            "snapshot-error:${e.message}"
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
