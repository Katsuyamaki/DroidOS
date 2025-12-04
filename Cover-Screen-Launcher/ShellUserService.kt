package com.example.quadrantlauncher

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.os.Binder
import android.os.IBinder
import android.os.Process
import android.provider.Settings
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    private fun setTemporaryBrightnessViaDisplayManager(brightness: Float): Boolean {
        try {
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, "display") as IBinder
            val iDisplayManagerClass = Class.forName("android.hardware.display.IDisplayManager$Stub")
            val displayManager = iDisplayManagerClass.getMethod("asInterface", IBinder::class.java).invoke(null, binder)

            val methods = displayManager.javaClass.methods
            var success = false
            
            for (m in methods) {
                if (m.name == "setTemporaryBrightness") {
                    try {
                        if (m.parameterCount == 2 && 
                            m.parameterTypes[0] == Int::class.javaPrimitiveType && 
                            m.parameterTypes[1] == Float::class.javaPrimitiveType) {
                            
                            // Target both potential IDs
                            m.invoke(displayManager, 0, brightness)
                            m.invoke(displayManager, 1, brightness)
                            
                            if (brightness < 0) {
                                m.invoke(displayManager, 0, Float.NaN)
                            }
                            Log.d(TAG, "Invoked setTemporaryBrightness(0/1, $brightness)")
                            success = true
                        }
                        else if (m.parameterCount == 1 && 
                            m.parameterTypes[0] == Float::class.javaPrimitiveType) {
                            m.invoke(displayManager, brightness)
                            if (brightness < 0) m.invoke(displayManager, Float.NaN)
                            Log.d(TAG, "Invoked setTemporaryBrightness($brightness)")
                            success = true
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Method invoke failed: ${m.name}", e)
                    }
                }
            }
            return success
        } catch (e: Exception) {
            Log.e(TAG, "setTemporaryBrightness reflection failed", e)
        }
        return false
    }

    private fun execShell(cmd: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "CMD Failed: $cmd", e)
        }
    }

    override fun setBrightness(value: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            Log.i(TAG, "setBrightness: $value | UID: ${Process.myUid()}")
            execShell("settings put system screen_brightness_mode 0")
            execShell("settings put system screen_auto_brightness_adj 0")

            if (value == -1) {
                execShell("settings put system screen_brightness_min 0")
                val uri = "content://settings/system"
                execShell("content insert --uri $uri --bind name:s:screen_brightness_float --bind value:f:-1.0")
                execShell("settings put system screen_brightness_float -1.0")
                execShell("settings put system screen_brightness -1")
                execShell("settings put system screen_brightness_float NaN")
                setTemporaryBrightnessViaDisplayManager(-1.0f)
            } else {
                val safeVal = value.coerceIn(1, 255)
                val floatVal = safeVal / 255.0f
                val uri = "content://settings/system"
                execShell("content insert --uri $uri --bind name:s:screen_brightness_float --bind value:f:$floatVal")
                execShell("content insert --uri $uri --bind name:s:screen_brightness --bind value:i:$safeVal")
                execShell("settings put system screen_brightness_float $floatVal")
                execShell("settings put system screen_brightness $safeVal")
                execShell("cmd display set-brightness $floatVal")
                setTemporaryBrightnessViaDisplayManager(floatVal)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setBrightness", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    override fun forceStop(packageName: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec("am force-stop $packageName").waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun runCommand(command: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec(command).waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        val token = Binder.clearCallingIdentity()
        try {
            val scClass = if (android.os.Build.VERSION.SDK_INT >= 34) {
                try { Class.forName("com.android.server.display.DisplayControl") } catch (e: Exception) { Class.forName("android.view.SurfaceControl") }
            } else { Class.forName("android.view.SurfaceControl") }
            
            var serviceToken: IBinder? = null
            
            // Try getInternalDisplayToken first (for Display 0)
            try {
                val method = scClass.getDeclaredMethod("getInternalDisplayToken")
                method.isAccessible = true
                serviceToken = method.invoke(null) as? IBinder
            } catch (e: Exception) {}

            val getIdsMethod = scClass.getMethod("getPhysicalDisplayIds")
            val getTokenMethod = scClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
            val setPowerMethod = scClass.getMethod("setDisplayPowerMode", IBinder::class.java, Int::class.javaPrimitiveType)
            
            val physicalIds = getIdsMethod.invoke(null) as LongArray
            val mode = if (turnOff) 0 else 2

            if (displayIndex == -1) {
                // WILDCARD MODE: Turn OFF/ON ALL DETECTED DISPLAYS
                Log.d(TAG, "setScreenOff: Targeting ALL ${physicalIds.size} displays")
                
                // 1. Try Internal Token
                if (serviceToken != null) {
                    try { setPowerMethod.invoke(null, serviceToken, mode) } catch(e: Exception){}
                }
                
                // 2. Loop all Physical IDs
                for (id in physicalIds) {
                    try {
                        val t = getTokenMethod.invoke(null, id) as? IBinder
                        if (t != null) setPowerMethod.invoke(null, t, mode)
                    } catch (e: Exception) {}
                }
            } else {
                // SPECIFIC INDEX MODE
                // Only target if index exists, DO NOT fallback to 0 arbitrarily
                if (displayIndex >= 0 && displayIndex < physicalIds.size) {
                    val targetId = physicalIds[displayIndex]
                    val t = getTokenMethod.invoke(null, targetId) as? IBinder
                    if (t != null) setPowerMethod.invoke(null, t, mode)
                } else if (displayIndex == 0 && serviceToken != null) {
                    // Fallback for index 0 if physicalIds array is empty/weird
                    setPowerMethod.invoke(null, serviceToken, mode)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "setScreenOff Failed", e)
        } finally { 
            Binder.restoreCallingIdentity(token) 
        }
    }

    override fun repositionTask(packageName: String, left: Int, top: Int, right: Int, bottom: Int) {
        val token = Binder.clearCallingIdentity()
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
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun getVisiblePackages(displayId: Int): List<String> {
        val packages = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
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
                    if (matcher.find()) { val pkg: String? = matcher.group(1); if (pkg != null && !packages.contains(pkg)) packages.add(pkg) }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return packages
    }

    override fun getAllRunningPackages(): List<String> {
        val packages = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity activities")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val recordPattern = Pattern.compile("ActivityRecord\\{[0-9a-f]+ u\\d+ ([a-zA-Z0-9_.]+)/")
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.contains("ActivityRecord{")) {
                    val matcher = recordPattern.matcher(l)
                    if (matcher.find()) { val pkg: String? = matcher.group(1); if (pkg != null && !packages.contains(pkg)) packages.add(pkg) }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return packages
    }

    override fun getWindowLayouts(displayId: Int): List<String> = ArrayList()
    override fun getTaskId(packageName: String): Int = -1
    override fun moveTaskToBack(taskId: Int) {}
}
