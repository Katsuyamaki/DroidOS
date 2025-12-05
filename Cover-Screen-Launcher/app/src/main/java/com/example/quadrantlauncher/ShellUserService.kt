package com.example.quadrantlauncher

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

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    // --- v2.0 LOGIC: Shell Resolver for System Settings ---
    private val shellResolver: ContentResolver? by lazy {
        try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val systemContext = activityThreadClass.getMethod("getSystemContext").invoke(currentActivityThread) as Context
            val shellContext = object : ContextWrapper(systemContext) {
                override fun getPackageName(): String = "com.android.shell"
                override fun getOpPackageName(): String = "com.android.shell"
            }
            shellContext.contentResolver
        } catch (e: Exception) { 
            Log.e(TAG, "Failed to get ShellResolver", e)
            null 
        }
    }

    override fun setBrightness(value: Int) {
        val resolver = shellResolver ?: return
        try {
            // 1. Disable Auto Brightness
            Settings.System.putInt(resolver, "screen_brightness_mode", 0)

            if (value == -1) {
                // --- EXTINGUISH MODE (-1) ---
                // Remove minimum limit
                Settings.System.putInt(resolver, "screen_brightness_min", 0)
                
                // Set float to -1.0 (The magic signal for OLED off on some drivers)
                try {
                    Settings.System.putFloat(resolver, "screen_brightness_float", -1.0f)
                } catch (e: Exception) {
                    Settings.System.putString(resolver, "screen_brightness_float", "-1.0")
                }
                
                // Set int to -1
                Settings.System.putInt(resolver, "screen_brightness", -1)
                
                // Hardware override via DisplayManager
                setBrightnessViaDisplayManager(0, -1.0f)
            } else {
                // --- WAKE UP ---
                val safeVal = value.coerceIn(1, 255)
                val floatVal = safeVal / 255.0f
                
                Settings.System.putFloat(resolver, "screen_brightness_float", floatVal)
                Settings.System.putInt(resolver, "screen_brightness", safeVal)
                
                setBrightnessViaDisplayManager(0, floatVal)
            }
        } catch (e: Exception) {
            Log.e(TAG, "setBrightness failed", e)
        }
    }

    override fun setBrightnessViaDisplayManager(displayId: Int, brightness: Float): Boolean {
        try {
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, "display") as IBinder
            // Correctly escaped Stub for Kotlin string interpolation
            val iDisplayManagerClass = Class.forName("android.hardware.display.IDisplayManager\$Stub")
            val displayManager = iDisplayManagerClass.getMethod("asInterface", IBinder::class.java).invoke(null, binder)

            try {
                // Try 2-arg method first (Android 14+)
                val method = displayManager.javaClass.getMethod("setTemporaryBrightness", Int::class.javaPrimitiveType, Float::class.javaPrimitiveType)
                method.invoke(displayManager, displayId, brightness)
                return true
            } catch (e: NoSuchMethodException) {
                // Fallback to 1-arg method
                val method = displayManager.javaClass.getMethod("setTemporaryBrightness", Float::class.javaPrimitiveType)
                method.invoke(displayManager, brightness)
                return true
            }
        } catch (e: Exception) { 
            Log.e(TAG, "setBrightnessViaDisplayManager failed", e)
            return false 
        }
    }

    // --- v2.0 LOGIC: Screen Off (SurfaceControl) ---
    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        val token = Binder.clearCallingIdentity()
        try {
            val scClass = Class.forName("android.view.SurfaceControl")
            var serviceToken: IBinder? = null
            try { serviceToken = scClass.getDeclaredMethod("getInternalDisplayToken").apply { isAccessible = true }.invoke(null) as? IBinder } catch (e: Exception) {}
            
            if (serviceToken == null) {
                val ids = scClass.getMethod("getPhysicalDisplayIds").invoke(null) as LongArray
                if (ids.isNotEmpty()) {
                     val targetId = if (displayIndex >= 0 && displayIndex < ids.size) ids[displayIndex] else ids[0]
                     serviceToken = scClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType).invoke(null, targetId) as? IBinder
                }
            }
            
            if (serviceToken != null) {
                scClass.getMethod("setDisplayPowerMode", IBinder::class.java, Int::class.javaPrimitiveType)
                    .invoke(null, serviceToken, if (turnOff) 0 else 2)
            }
        } catch (e: Exception) {
            Log.e(TAG, "setScreenOff failed", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    // --- V1.0 LOGIC: Window Management (Retained for Tiling/Minimizing) ---
    
    override fun forceStop(packageName: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec("am force-stop $packageName").waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun runCommand(command: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec(command).waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun repositionTask(packageName: String, left: Int, top: Int, right: Int, bottom: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec("dumpsys activity top")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            var tid = -1
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains(packageName) && line!!.contains("TASK")) { 
                    val m = Regex("id=(\\d+)").find(line!!)
                    if (m != null) tid = m.groupValues[1].toInt() 
                }
            }
            if (tid != -1) { 
                Runtime.getRuntime().exec("am task set-windowing-mode $tid 5").waitFor()
                Runtime.getRuntime().exec("am task resize $tid $left $top $right $bottom").waitFor() 
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun getVisiblePackages(displayId: Int): List<String> {
        val list = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec("dumpsys window windows")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            var currentPkg: String? = null
            var isVisible = false
            var onCorrectDisplay = false
            val windowPattern = Pattern.compile("Window\\{[0-9a-f]+ u\\d+ ([^\\}/ ]+)")

            while (r.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.startsWith("Window #")) {
                    currentPkg = null; isVisible = false; onCorrectDisplay = false
                    val matcher = windowPattern.matcher(l)
                    if (matcher.find()) currentPkg = matcher.group(1)
                }
                if (l.contains("displayId=$displayId") || l.contains("mDisplayId=$displayId")) onCorrectDisplay = true
                if (l.contains("mViewVisibility=0x0")) isVisible = true

                if (currentPkg != null && isVisible && onCorrectDisplay) {
                    if (isUserApp(currentPkg!!) && !list.contains(currentPkg!!)) list.add(currentPkg!!)
                    currentPkg = null
                }
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return list
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

    override fun getWindowLayouts(displayId: Int): List<String> = ArrayList()

    override fun getTaskId(packageName: String): Int {
        var taskId = -1
        val token = Binder.clearCallingIdentity()
        try {
            val cmd = arrayOf("sh", "-c", "dumpsys activity activities | grep -E 'Task id|$packageName'")
            val p = Runtime.getRuntime().exec(cmd)
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains(packageName)) {
                    if (line!!.startsWith("* Task{") || line!!.startsWith("Task{")) { val m = Regex("#(\\d+)").find(line!!); if (m != null) { taskId = m.groupValues[1].toInt(); break } }
                    if (line!!.contains("ActivityRecord")) { val m = Regex("t(\\d+)").find(line!!); if (m != null) { taskId = m.groupValues[1].toInt(); break } }
                }
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return taskId
    }

    override fun moveTaskToBack(taskId: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val serviceMethod = atmClass.getMethod("getService")
            val atm = serviceMethod.invoke(null)
            val moveMethod = atm.javaClass.getMethod("moveTaskToBack", Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
            moveMethod.invoke(atm, taskId, true)
        } catch (e: Exception) {
            try {
                val am = Class.forName("android.app.ActivityManagerNative").getMethod("getDefault").invoke(null)
                val moveMethod = am.javaClass.getMethod("moveTaskToBack", Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
                moveMethod.invoke(am, taskId, true)
            } catch (e2: Exception) {}
        } finally { Binder.restoreCallingIdentity(token) }
    }

    private fun isUserApp(pkg: String): Boolean {
        if (pkg == "com.android.systemui") return false
        if (pkg == "com.android.launcher3") return false 
        if (pkg == "com.sec.android.app.launcher") return false 
        if (pkg == "com.example.quadrantlauncher") return false
        if (pkg == "com.example.com.katsuyamaki.coverscreenlauncher") return false
        if (pkg == "com.example.coverscreentester") return false 
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

    // Interface compliance stubs
    override fun setSystemBrightness(brightness: Int) { setBrightness(brightness) }
    override fun getSystemBrightness(): Int = 128
    override fun getSystemBrightnessFloat(): Float = 0.5f
    override fun setAutoBrightness(enabled: Boolean) { 
        val resolver = shellResolver ?: return
        try { Settings.System.putInt(resolver, "screen_brightness_mode", if (enabled) 1 else 0) } catch(e: Exception) {}
    }
    override fun isAutoBrightness(): Boolean = true
}
