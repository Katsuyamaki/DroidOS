package com.example.quadrantlauncher

import android.content.Context
import android.os.IBinder
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern

class ShellUserService(private val context: Context) : IShellService.Stub() {

    constructor() : this(null!!)

    private val TAG = "ShellUserService"

    override fun forceStop(packageName: String) {
        try {
            val process = Runtime.getRuntime().exec("am force-stop $packageName")
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to kill", e)
        }
    }

    override fun runCommand(command: String) {
        try {
            Log.i(TAG, "Running: $command")
            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Command failed: $command", e)
        }
    }

    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        try {
            val scClass = if (Build.VERSION.SDK_INT >= 34) {
                try { Class.forName("com.android.server.display.DisplayControl") } catch (e: Exception) { Class.forName("android.view.SurfaceControl") }
            } else {
                Class.forName("android.view.SurfaceControl")
            }

            val getIdsMethod = scClass.getMethod("getPhysicalDisplayIds")
            val physicalIds = getIdsMethod.invoke(null) as LongArray
            if (physicalIds.isEmpty()) return

            val targetId = if (displayIndex >= 0 && displayIndex < physicalIds.size) {
                physicalIds[displayIndex]
            } else {
                physicalIds[0]
            }

            val getTokenMethod = scClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
            val token = getTokenMethod.invoke(null, targetId) as? IBinder ?: return

            val mode = if (turnOff) 0 else 2 
            val ctrlClass = Class.forName("android.view.SurfaceControl")
            val setPowerMethod = ctrlClass.getMethod("setDisplayPowerMode", IBinder::class.java, Int::class.javaPrimitiveType)
            setPowerMethod.invoke(null, token, mode)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set screen power", e)
        }
    }

    // V2.0 Feature: Alt Screen Off (Pixels Off)
    override fun setBrightness(displayIndex: Int, brightness: Int) {
        try {
            if (brightness == -1) {
                setScreenOff(displayIndex, true)
            } else {
                setScreenOff(displayIndex, false)
                // Optional: Restore brightness via settings if needed
                runCommand("settings put system screen_brightness 128")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set brightness", e)
        }
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
                    if (match != null) {
                        targetTaskId = match.groupValues[1].toInt()
                        break
                    }
                }
            }
            reader.close()
            process.waitFor()

            if (targetTaskId != -1) {
                Runtime.getRuntime().exec("am task set-windowing-mode $targetTaskId 5").waitFor()
                val cmd = "am task resize $targetTaskId $left $top $right $bottom"
                Runtime.getRuntime().exec(cmd).waitFor()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to reposition task", e)
        }
    }

    // REVISED: Uses 'dumpsys window windows' to accurately detect VISIBLE apps
    override fun getVisiblePackages(displayId: Int): List<String> {
        val packages = ArrayList<String>()
        try {
            val process = Runtime.getRuntime().exec("dumpsys window windows")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            
            var line: String?
            var currentPkg: String? = null
            var isVisible = false
            var onCorrectDisplay = false
            
            // Regex to parse "Window{... u0 com.package ...}"
            val windowPattern = Pattern.compile("Window\\{[0-9a-f]+ u\\d+ ([^\\}/ ]+)")

            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                
                if (l.startsWith("Window #")) {
                    // Reset state for new window block
                    currentPkg = null
                    isVisible = false
                    onCorrectDisplay = false
                    
                    val matcher = windowPattern.matcher(l)
                    if (matcher.find()) {
                        currentPkg = matcher.group(1)
                    }
                }

                // Display Check (handles various dumpsys formats)
                if (l.contains("displayId=$displayId") || l.contains("mDisplayId=$displayId")) {
                    onCorrectDisplay = true
                }

                // Visibility Check: 0x0 means VISIBLE, 0x4/0x8 means HIDDEN/GONE
                if (l.contains("mViewVisibility=0x0")) {
                    isVisible = true
                }

                // If conditions met, add package
                if (currentPkg != null && isVisible && onCorrectDisplay) {
                    if (isUserApp(currentPkg!!) && !packages.contains(currentPkg!!)) {
                        packages.add(currentPkg!!)
                    }
                    currentPkg = null // Prevent duplicates for this window
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get visible packages", e)
        }
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
                        val pkg = matcher.group(1)
                        if (pkg != null && !packages.contains(pkg) && isUserApp(pkg)) {
                            packages.add(pkg)
                        }
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get running packages", e)
        }
        return packages
    }

    override fun getWindowLayouts(displayId: Int): List<String> {
        val results = ArrayList<String>()
        try {
            val process = Runtime.getRuntime().exec("dumpsys window windows")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            
            var line: String?
            var pendingPkg: String? = null
            var isVisible = false
            var isBaseApp = false
            
            val windowPattern = Pattern.compile("Window\\{[0-9a-f]+ u\\d+ ([^\\}/ ]+)")
            val framePattern = Pattern.compile("(?:frame|mFrame)=\\[(-?\\d+),(-?\\d+)\\]\\[(-?\\d+),(-?\\d+)\\]")

            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                
                if (l.startsWith("Window #")) {
                    pendingPkg = null
                    isVisible = false
                    isBaseApp = false
                    val matcher = windowPattern.matcher(l)
                    if (matcher.find()) {
                        val pkg = matcher.group(1)
                        if (isUserApp(pkg)) pendingPkg = pkg
                    }
                    continue
                }
                
                if (pendingPkg == null) continue
                if (l.contains("mViewVisibility=0x0")) isVisible = true
                if (l.contains("ty=BASE_APPLICATION") || l.contains("type=BASE_APPLICATION")) isBaseApp = true
                
                if (isVisible && isBaseApp && (l.contains("frame=") || l.contains("mFrame="))) {
                    val matcher = framePattern.matcher(l)
                    if (matcher.find()) {
                        val left = matcher.group(1).toInt()
                        val top = matcher.group(2).toInt()
                        val right = matcher.group(3).toInt()
                        val bottom = matcher.group(4).toInt()
                        if ((right - left) > 10 && (bottom - top) > 10) {
                            results.add("$pendingPkg|$left,$top,$right,$bottom")
                            pendingPkg = null 
                        }
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get window layouts", e)
        }
        return results
    }

    // --- V1.0 RESTORED LOGIC ---
    override fun getTaskId(packageName: String): Int {
        var taskId = -1
        try {
            // Using grep as requested for Task ID extraction
            val cmd = arrayOf("sh", "-c", "dumpsys activity activities | grep -E 'Task id|$packageName'")
            val process = Runtime.getRuntime().exec(cmd)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.contains(packageName)) {
                    if (l.startsWith("* Task{") || l.startsWith("Task{")) {
                         val match = Regex("#(\\d+)").find(l)
                         if (match != null) { taskId = match.groupValues[1].toInt(); break }
                    }
                    if (l.contains("ActivityRecord")) {
                         val match = Regex("t(\\d+)").find(l)
                         if (match != null) { taskId = match.groupValues[1].toInt(); break }
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to find Task ID", e)
        }
        return taskId
    }

    override fun moveTaskToBack(taskId: Int) {
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
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to move task to back", e2)
            }
        }
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
}
