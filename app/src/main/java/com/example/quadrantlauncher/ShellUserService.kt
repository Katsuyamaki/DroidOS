package com.example.quadrantlauncher

import android.content.ContentResolver
import android.os.Binder
import android.os.IBinder
import android.os.Process
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    // --- RAW HARDWARE CONTROL (No Context Required) ---
    private fun setTemporaryBrightnessViaDisplayManager(brightness: Float): Boolean {
        try {
            // Get the raw Binder for the Display Manager Service
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, "display") as IBinder
            
            // Wrap it in the Stub to get the Interface
            val iDisplayManagerClass = Class.forName("android.hardware.display.IDisplayManager$Stub")
            val displayManager = iDisplayManagerClass.getMethod("asInterface", IBinder::class.java).invoke(null, binder)

            // Search for the method signature (Beam Pro might vary)
            val methods = displayManager.javaClass.methods
            var success = false
            
            for (m in methods) {
                if (m.name == "setTemporaryBrightness") {
                    try {
                        // Signature A: (int displayId, float brightness)
                        if (m.parameterCount == 2 && 
                            m.parameterTypes[0] == Int::class.javaPrimitiveType && 
                            m.parameterTypes[1] == Float::class.javaPrimitiveType) {
                            
                            // BRUTE FORCE: Try IDs 0, 1, 2, 3, 4
                            // Beam Pro glasses might be a secondary display ID
                            for (id in 0..4) {
                                m.invoke(displayManager, id, brightness)
                            }
                            Log.d(TAG, "Invoked setTemporaryBrightness(0..4, $brightness)")
                            success = true
                        }
                        // Signature B: (float brightness) - Default Display
                        else if (m.parameterCount == 1 && 
                            m.parameterTypes[0] == Float::class.javaPrimitiveType) {
                            m.invoke(displayManager, brightness)
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

    // --- SHELL COMMAND EXECUTOR ---
    private fun execShell(cmd: String) {
        try {
            // Use 'cmd' binary where possible as it's faster than 'settings' script
            val finalCmd = if (cmd.startsWith("settings ")) "cmd $cmd" else cmd
            
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", finalCmd))
            process.waitFor()
            
            // Log errors if any (Debugging)
            val errReader = BufferedReader(InputStreamReader(process.errorStream))
            val errSb = StringBuilder()
            var line: String?
            while (errReader.readLine().also { line = it } != null) errSb.append(line)
            
            if (errSb.isNotEmpty()) Log.e(TAG, "CMD Error [$finalCmd]: $errSb")
            else Log.d(TAG, "CMD Success [$finalCmd]")
            
        } catch (e: Exception) {
            Log.e(TAG, "CMD Exception [$cmd]", e)
        }
    }

    override fun setBrightness(value: Int) {
        // 1. IDENTITY: Become the Shell User (UID 2000)
        val token = Binder.clearCallingIdentity()
        
        try {
            Log.i(TAG, "Extinguish: setBrightness($value) | UID: ${Process.myUid()}")

            // 2. ORDER OF OPERATIONS (CRITICAL)
            // We must unlock the limits BEFORE setting the "illegal" value.
            
            // A. Disable Auto-Brightness
            execShell("settings put system screen_brightness_mode 0")
            
            if (value == -1) {
                // --- PIXELS OFF SEQUENCE ---
                
                // B. Uncap the Floor (Allow 0/Negative)
                // If this is set to 10, setting -1 will just clamp to 10.
                execShell("settings put system screen_brightness_min 0")

                // C. Set the "Magic" Float Signal (-1.0)
                // We use 'cmd settings' to write directly to the provider
                execShell("settings put system screen_brightness_float -1.0")
                
                // D. Set Legacy Int (-1)
                execShell("settings put system screen_brightness -1")
                
                // E. Hardware Override (Hidden API)
                // This forces the driver to update immediately
                setTemporaryBrightnessViaDisplayManager(-1.0f)
                
                // F. NaN Backup (Some drivers interpret NaN as "Off")
                setTemporaryBrightnessViaDisplayManager(Float.NaN)

            } else {
                // --- WAKE UP SEQUENCE ---
                val floatVal = value.coerceIn(1, 255) / 255.0f
                val intVal = value.coerceIn(1, 255)
                
                execShell("settings put system screen_brightness_float $floatVal")
                execShell("settings put system screen_brightness $intVal")
                
                // Wake hardware immediately
                setTemporaryBrightnessViaDisplayManager(floatVal)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Critical Error in setBrightness", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    // --- BOILERPLATE SHELL METHODS (Unchanged) ---
    
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
            try {
                val method = scClass.getDeclaredMethod("getInternalDisplayToken")
                method.isAccessible = true
                serviceToken = method.invoke(null) as? IBinder
            } catch (e: Exception) {}

            if (serviceToken == null) {
                val getIdsMethod = scClass.getMethod("getPhysicalDisplayIds")
                val physicalIds = getIdsMethod.invoke(null) as LongArray
                if (physicalIds.isNotEmpty()) {
                     val targetId = if (displayIndex >= 0 && displayIndex < physicalIds.size) physicalIds[displayIndex] else physicalIds[0]
                     val getTokenMethod = scClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
                     serviceToken = getTokenMethod.invoke(null, targetId) as? IBinder
                }
            }

            if (serviceToken != null) {
                val mode = if (turnOff) 0 else 2
                val setPowerMethod = scClass.getMethod("setDisplayPowerMode", IBinder::class.java, Int::class.javaPrimitiveType)
                setPowerMethod.invoke(null, serviceToken, mode)
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
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
