package com.katsuyamaki.droidoslauncher

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

class ShellUserService : IShellService.Stub() {
    private val TAG = "ShellUserService"

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
            null 
        }
    }

    override fun setBrightness(value: Int) {
        val resolver = shellResolver ?: return
        try {
            Settings.System.putInt(resolver, "screen_brightness_mode", 0)

            if (value == -1) {
                Settings.System.putInt(resolver, "screen_brightness_min", 0)
                try {
                    Settings.System.putFloat(resolver, "screen_brightness_float", -1.0f)
                } catch (e: Exception) {
                    Settings.System.putString(resolver, "screen_brightness_float", "-1.0")
                }
                Settings.System.putInt(resolver, "screen_brightness", -1)
                setBrightnessViaDisplayManager(0, -1.0f)
            } else {
                val safeVal = value.coerceIn(1, 255)
                val floatVal = safeVal / 255.0f
                Settings.System.putFloat(resolver, "screen_brightness_float", floatVal)
                Settings.System.putInt(resolver, "screen_brightness", safeVal)
                setBrightnessViaDisplayManager(0, floatVal)
            }
        } catch (e: Exception) {
        }
    }

    override fun setBrightnessViaDisplayManager(displayId: Int, brightness: Float): Boolean {
        try {
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, "display") as IBinder
            val iDisplayManagerClass = Class.forName("android.hardware.display.IDisplayManager\$Stub")
            val displayManager = iDisplayManagerClass.getMethod("asInterface", IBinder::class.java).invoke(null, binder)

            try {
                val method = displayManager.javaClass.getMethod("setTemporaryBrightness", Int::class.javaPrimitiveType, Float::class.javaPrimitiveType)
                method.invoke(displayManager, displayId, brightness)
                return true
            } catch (e: NoSuchMethodException) {
                val method = displayManager.javaClass.getMethod("setTemporaryBrightness", Float::class.javaPrimitiveType)
                method.invoke(displayManager, brightness)
                return true
            }
        } catch (e: Exception) { return false }
    }

    override fun forceStop(packageName: String) { try { Runtime.getRuntime().exec("am force-stop $packageName").waitFor() } catch (e: Exception) {} }
    override fun runCommand(command: String) { try { Runtime.getRuntime().exec(command).waitFor() } catch (e: Exception) {} }
    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        try {
            val scClass = Class.forName("android.view.SurfaceControl")
            var token: IBinder? = null
            try { token = scClass.getDeclaredMethod("getInternalDisplayToken").apply { isAccessible = true }.invoke(null) as? IBinder } catch (e: Exception) {}
            if (token == null) {
                val ids = scClass.getMethod("getPhysicalDisplayIds").invoke(null) as LongArray
                if (ids.isNotEmpty()) token = scClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType).invoke(null, ids[0]) as? IBinder
            }
            if (token != null) scClass.getMethod("setDisplayPowerMode", IBinder::class.java, Int::class.javaPrimitiveType).invoke(null, token, if (turnOff) 0 else 2)
        } catch (e: Exception) {}
    }
    override fun repositionTask(packageName: String, left: Int, top: Int, right: Int, bottom: Int) {
        try {
            val p = Runtime.getRuntime().exec("dumpsys activity top"); val r = BufferedReader(InputStreamReader(p.inputStream)); var line: String?; var tid = -1
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains(packageName) && line!!.contains("TASK")) { val m = Regex("id=(\\d+)").find(line!!); if (m != null) tid = m.groupValues[1].toInt() }
            }
            if (tid != -1) { Runtime.getRuntime().exec("am task set-windowing-mode $tid 5").waitFor(); Runtime.getRuntime().exec("am task resize $tid $left $top $right $bottom").waitFor() }
        } catch (e: Exception) {}
    }
    override fun getVisiblePackages(displayId: Int): List<String> {
        val list = ArrayList<String>()
        try {
            val p = Runtime.getRuntime().exec("dumpsys activity activities"); val r = BufferedReader(InputStreamReader(p.inputStream)); var line: String?; var curDisp = -1
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains("Display #")) { val m = Regex("Display #(\\d+)").find(line!!); if (m != null) curDisp = m.groupValues[1].toInt() }
                if (curDisp == displayId && line!!.contains("ActivityRecord{")) { val m = Regex("u\\d+\\s+([a-zA-Z0-9_.]+)/").find(line!!); if (m != null) list.add(m.groupValues[1]) }
            }
        } catch (e: Exception) {}
        return list.distinct()
    }
    override fun getWindowLayouts(displayId: Int): List<String> { return ArrayList() }
    override fun getAllRunningPackages(): List<String> { return ArrayList() }
    override fun getTaskId(packageName: String): Int { return -1 }
    override fun moveTaskToBack(taskId: Int) {}
    override fun setSystemBrightness(brightness: Int) {}
    override fun getSystemBrightness(): Int { return 128 }
    override fun getSystemBrightnessFloat(): Float { return 0.5f }
    override fun setAutoBrightness(enabled: Boolean) {}
    override fun isAutoBrightness(): Boolean { return true }
}
