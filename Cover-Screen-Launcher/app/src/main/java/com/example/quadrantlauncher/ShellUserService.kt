package com.example.quadrantlauncher

import android.os.IBinder
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    companion object {
        const val POWER_MODE_OFF = 0
        const val POWER_MODE_NORMAL = 2
        
        // Static cache to persist across method calls within the same process
        @Volatile private var displayControlClass: Class<*>? = null
        @Volatile private var displayControlClassLoaded = false
        @Volatile private var displayControlClassFailed = false
        
        @Volatile private var cachedDisplayToken: IBinder? = null
        @Volatile private var displayTokenFailed = false
    }

    // ============================================================
    // SurfaceControl Hidden API Access (Extinguish Methodology)
    // ============================================================

    private val surfaceControlClass: Class<*> by lazy {
        Class.forName("android.view.SurfaceControl")
    }

    /**
     * For Android 14+ (API 34+), we need to use DisplayControl from services.jar
     * to get the physical display token. For older versions, SurfaceControl works.
     * 
     * CACHING: This now caches the result to prevent repeated library loading failures.
     */
    private fun getDisplayControlClass(): Class<*>? {
        // If we already tried and failed, don't try again in this process
        if (displayControlClassFailed) {
            return null
        }
        
        // If already loaded successfully, return cached class
        if (displayControlClassLoaded && displayControlClass != null) {
            return displayControlClass
        }
        
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
            
            // Cache the successful result
            displayControlClass = loadedClass
            displayControlClassLoaded = true
            Log.i(TAG, "DisplayControl class loaded successfully")
            loadedClass
        } catch (e: Exception) {
            Log.w(TAG, "DisplayControl not available, falling back to SurfaceControl", e)
            displayControlClassFailed = true
            null
        }
    }

    /**
     * Get the primary physical display token.
     * CACHING: Caches the token to avoid repeated reflection failures.
     */
    private fun getPrimaryPhysicalDisplayToken(): IBinder? {
        // If we already have a cached token, use it
        cachedDisplayToken?.let { return it }
        
        // If we already tried and failed, don't try again
        if (displayTokenFailed) {
            return null
        }
        
        return try {
            val token: IBinder? = if (Build.VERSION.SDK_INT >= 34) {
                val controlClass = getDisplayControlClass()
                if (controlClass != null) {
                    val getIdsMethod = controlClass.getMethod("getPhysicalDisplayIds")
                    val physicalIds = getIdsMethod.invoke(null) as LongArray
                    if (physicalIds.isEmpty()) {
                        Log.e(TAG, "No physical displays found")
                        null
                    } else {
                        val getTokenMethod = controlClass.getMethod(
                            "getPhysicalDisplayToken",
                            Long::class.javaPrimitiveType
                        )
                        getTokenMethod.invoke(null, physicalIds[0]) as IBinder
                    }
                } else {
                    getSurfaceControlDisplayToken()
                }
            } else if (Build.VERSION.SDK_INT >= 29) {
                val method = surfaceControlClass.getMethod("getInternalDisplayToken")
                method.invoke(null) as IBinder
            } else {
                val method = surfaceControlClass.getMethod("getBuiltInDisplay", Int::class.java)
                method.invoke(null, 0) as IBinder
            }
            
            if (token != null) {
                cachedDisplayToken = token
                Log.i(TAG, "Display token obtained and cached successfully")
            } else {
                displayTokenFailed = true
            }
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get display token", e)
            displayTokenFailed = true
            null
        }
    }

    private fun getSurfaceControlDisplayToken(): IBinder? {
        return try {
            val getIdsMethod = surfaceControlClass.getMethod("getPhysicalDisplayIds")
            val physicalIds = getIdsMethod.invoke(null) as LongArray
            if (physicalIds.isEmpty()) {
                Log.e(TAG, "No physical displays found via SurfaceControl")
                return null
            }
            val getTokenMethod = surfaceControlClass.getMethod(
                "getPhysicalDisplayToken",
                Long::class.javaPrimitiveType
            )
            getTokenMethod.invoke(null, physicalIds[0]) as IBinder
        } catch (e: Exception) {
            Log.e(TAG, "SurfaceControl display token methods failed", e)
            null
        }
    }

    /**
     * Set display power mode via SurfaceControl.
     */
    private fun setDisplayPowerMode(mode: Int): Boolean {
        return try {
            val displayToken = getPrimaryPhysicalDisplayToken()
            if (displayToken == null) {
                Log.e(TAG, "Cannot set power mode: no display token")
                return false
            }
            
            val method = surfaceControlClass.getMethod(
                "setDisplayPowerMode",
                IBinder::class.java,
                Int::class.javaPrimitiveType
            )
            method.invoke(null, displayToken, mode)
            Log.d(TAG, "setDisplayPowerMode($mode) SUCCESS")
            true
        } catch (e: Exception) {
            Log.e(TAG, "setDisplayPowerMode($mode) FAILED", e)
            false
        }
    }

    /**
     * Set display brightness via SurfaceControl.setDisplayBrightness()
     * THIS IS THE KEY EXTINGUISH METHOD!
     * 
     * @param brightness: 0.0f to 1.0f for normal brightness, -1.0f to turn off backlight
     */
    private fun setDisplayBrightnessInternal(brightness: Float): Boolean {
        return try {
            val displayToken = getPrimaryPhysicalDisplayToken()
            if (displayToken == null) {
                Log.e(TAG, "Cannot set brightness: no display token")
                return false
            }
            
            // Try the newer 5-parameter API first (Android 12+)
            try {
                val method = surfaceControlClass.getMethod(
                    "setDisplayBrightness",
                    IBinder::class.java,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType
                )
                val result = method.invoke(null, displayToken, brightness, -1f, brightness, -1f) as Boolean
                Log.d(TAG, "setDisplayBrightness(5-param, $brightness) = $result")
                return result
            } catch (e: NoSuchMethodException) {
                Log.d(TAG, "5-param setDisplayBrightness not available, trying 2-param")
            }
            
            // Fall back to 2-parameter API
            val method = surfaceControlClass.getMethod(
                "setDisplayBrightness",
                IBinder::class.java,
                Float::class.javaPrimitiveType
            )
            val result = method.invoke(null, displayToken, brightness) as Boolean
            Log.d(TAG, "setDisplayBrightness(2-param, $brightness) = $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "setDisplayBrightness($brightness) FAILED", e)
            false
        }
    }

    // ============================================================
    // Shell Command Helper
    // ============================================================

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
                Log.d(TAG, "Shell: $command")
            } catch (e: Exception) {
                Log.e(TAG, "Shell command failed: $command", e)
            }
        }
    }

    // ============================================================
    // AIDL Interface Implementations
    // ============================================================

    /**
     * Main brightness control method.
     * 
     * For -1 (or any negative): Turn screen OFF using Extinguish methodology
     * For 0-255: Set normal brightness
     */
    override fun setBrightness(brightness: Int) {
        Log.d(TAG, "setBrightness($brightness) called")
        
        try {
            if (brightness < 0) {
                // SCREEN OFF MODE - Use Extinguish methodology
                Log.i(TAG, "=== SCREEN OFF MODE (Extinguish Method) ===")
                
                // Step 1: Disable auto brightness
                execShellCommand("settings put system screen_brightness_mode 0")
                
                // Step 2: Try SurfaceControl.setDisplayBrightness(-1.0f) first
                // This is the Extinguish "ShizukuScreenBrightnessNeg1" solution
                val brightnessResult = setDisplayBrightnessInternal(-1.0f)
                Log.d(TAG, "SurfaceControl.setDisplayBrightness(-1.0f) = $brightnessResult")
                
                // Step 3: Also set settings values (belt and suspenders)
                execShellCommand("settings put system screen_brightness_float -1.0")
                execShellCommand("settings put system screen_brightness -1")
                
                Log.i(TAG, "=== SCREEN OFF COMPLETE ===")
            } else {
                // SCREEN ON / NORMAL BRIGHTNESS
                Log.i(TAG, "=== RESTORING BRIGHTNESS: $brightness ===")
                
                val floatVal = brightness.toFloat() / 255.0f
                
                // Step 1: Use SurfaceControl to set brightness
                setDisplayBrightnessInternal(floatVal)
                
                // Step 2: Set settings values
                execShellCommand("settings put system screen_brightness_float $floatVal")
                execShellCommand("settings put system screen_brightness $brightness")
                
                Log.i(TAG, "=== BRIGHTNESS RESTORED ===")
            }
        } catch (e: Exception) {
            Log.e(TAG, "setBrightness($brightness) FAILED", e)
        }
    }

    /**
     * Screen on/off using SurfaceControl.setDisplayPowerMode()
     * This is the Extinguish "ShizukuPowerOffScreen" solution.
     */
    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        Log.d(TAG, "setScreenOff(displayIndex=$displayIndex, turnOff=$turnOff)")
        
        try {
            val mode = if (turnOff) POWER_MODE_OFF else POWER_MODE_NORMAL
            val result = setDisplayPowerMode(mode)
            Log.d(TAG, "setDisplayPowerMode($mode) = $result")
        } catch (e: Exception) {
            Log.e(TAG, "setScreenOff failed", e)
        }
    }

    // ============================================================
    // Other AIDL Methods
    // ============================================================

    override fun forceStop(packageName: String) {
        try { Runtime.getRuntime().exec("am force-stop $packageName").waitFor() } catch (e: Exception) {}
    }

    override fun runCommand(command: String) {
        try { Runtime.getRuntime().exec(command).waitFor() } catch (e: Exception) {}
    }

    override fun repositionTask(packageName: String, left: Int, top: Int, right: Int, bottom: Int) {
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity top")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var targetTaskId = -1
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.startsWith("TASK") && l.contains(packageName)) {
                    val match = Regex("id=(\\d+)").find(l)
                    if (match != null) { targetTaskId = match.groupValues[1].toInt(); break }
                }
            }
            reader.close()
            process.waitFor()
            if (targetTaskId != -1) {
                Runtime.getRuntime().exec("am task set-windowing-mode $targetTaskId 5").waitFor()
                Runtime.getRuntime().exec("am task resize $targetTaskId $left $top $right $bottom").waitFor()
            }
        } catch (e: Exception) {}
    }

    override fun getVisiblePackages(displayId: Int): List<String> {
        val packages = ArrayList<String>()
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity activities")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var currentScanningDisplayId = -1
            val recordPattern = Pattern.compile("u\\d+\\s+([a-zA-Z0-9_.]+)/")
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.startsWith("Display #")) {
                    val displayMatch = Regex("Display #(\\d+)").find(l)
                    if (displayMatch != null) currentScanningDisplayId = displayMatch.groupValues[1].toInt()
                    continue
                }
                if (currentScanningDisplayId == displayId && l.contains("ActivityRecord{")) {
                    val matcher = recordPattern.matcher(l)
                    if (matcher.find()) {
                        val pkg: String? = matcher.group(1)
                        if (pkg != null && !packages.contains(pkg)) packages.add(pkg)
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {}
        return packages
    }

    override fun getAllRunningPackages(): List<String> {
        val packages = ArrayList<String>()
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity activities")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val recordPattern = Pattern.compile("ActivityRecord\\{[0-9a-f]+ u\\d+ ([a-zA-Z0-9_.]+)/")
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.contains("ActivityRecord{")) {
                    val matcher = recordPattern.matcher(l)
                    if (matcher.find()) {
                        val pkg: String? = matcher.group(1)
                        if (pkg != null && !packages.contains(pkg)) packages.add(pkg)
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {}
        return packages
    }

    override fun getWindowLayouts(displayId: Int): List<String> { return ArrayList<String>() }
    override fun getTaskId(packageName: String): Int { return -1 }
    override fun moveTaskToBack(taskId: Int) {}
}
