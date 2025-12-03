package com.example.quadrantlauncher

import android.content.Context
import android.os.IBinder
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    // FIX: Create a Context specifically for "com.android.shell" (UID 2000)
    // This matches our process UID and passes the security check.
    private fun getShellContext(): Context? {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread")
            val activityThread = currentActivityThreadMethod.invoke(null)
            
            // 1. Get System Context first
            val getSystemContextMethod = activityThreadClass.getMethod("getSystemContext")
            val systemContext = getSystemContextMethod.invoke(activityThread) as Context
            
            // 2. Create Shell Context from it
            systemContext.createPackageContext("com.android.shell", 0)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create Shell Context", e)
            null
        }
    }

    override fun setBrightness(brightness: Int) {
        // Use the fixed context
        val context = getShellContext() ?: return
        val resolver = context.contentResolver

        try {
            // 1. Disable Auto Brightness (API)
            Settings.System.putInt(resolver, "screen_brightness_mode", 0)

            // 2. Set Min to 0
            try { Settings.System.putInt(resolver, "screen_brightness_min", 0) } catch(e: Exception){}

            // 3. Zero out Float (Critical for Android 14)
            try { Settings.System.putFloat(resolver, "screen_brightness_float", 0.0f) } catch(e: Exception){}

            // 4. Set Integer to -1 (The Pixel Off Hack)
            // Direct API call bypasses CLI validation
            Settings.System.putInt(resolver, "screen_brightness", brightness)

            Log.d(TAG, "Applied brightness $brightness via Shell Context")
        } catch (e: Exception) {
            Log.e(TAG, "Java Brightness Error", e)
        }
    }

    override fun forceStop(packageName: String) {
        try { Runtime.getRuntime().exec("am force-stop $packageName").waitFor() } catch (e: Exception) {}
    }

    override fun runCommand(command: String) {
        try { Runtime.getRuntime().exec(command).waitFor() } catch (e: Exception) {}
    }

    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        try {
            val scClass = if (Build.VERSION.SDK_INT >= 34) {
                try { Class.forName("com.android.server.display.DisplayControl") } catch (e: Exception) { Class.forName("android.view.SurfaceControl") }
            } else { Class.forName("android.view.SurfaceControl") }
            val getIdsMethod = scClass.getMethod("getPhysicalDisplayIds")
            val physicalIds = getIdsMethod.invoke(null) as LongArray
            if (physicalIds.isEmpty()) return
            val targetId = if (displayIndex >= 0 && displayIndex < physicalIds.size) physicalIds[displayIndex] else physicalIds[0]
            val getTokenMethod = scClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
            val token = getTokenMethod.invoke(null, targetId) as? IBinder ?: return
            val mode = if (turnOff) 0 else 2 
            val ctrlClass = Class.forName("android.view.SurfaceControl")
            val setPowerMethod = ctrlClass.getMethod("setDisplayPowerMode", IBinder::class.java, Int::class.javaPrimitiveType)
            setPowerMethod.invoke(null, token, mode)
        } catch (e: Exception) {}
    }

    override fun repositionTask(packageName: String, left: Int, top: Int, right: Int, bottom: Int) {
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity top")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?; var targetTaskId = -1
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.startsWith("TASK") && l.contains(packageName)) {
                    val match = Regex("id=(\\d+)").find(l)
                    if (match != null) { targetTaskId = match.groupValues[1].toInt(); break }
                }
            }
            reader.close(); process.waitFor()
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
            var line: String?; var currentScanningDisplayId = -1
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
            reader.close(); process.waitFor()
        } catch (e: Exception) {}
        return packages
    }

    override fun getAllRunningPackages(): List<String> {
        val packages = ArrayList<String>()
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity activities")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?; val recordPattern = Pattern.compile("ActivityRecord\\{[0-9a-f]+ u\\d+ ([a-zA-Z0-9_.]+)/")
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.contains("ActivityRecord{")) {
                    val matcher = recordPattern.matcher(l)
                    if (matcher.find()) { val pkg: String? = matcher.group(1); if (pkg != null && !packages.contains(pkg)) packages.add(pkg) }
                }
            }
            reader.close(); process.waitFor()
        } catch (e: Exception) {}
        return packages
    }

    override fun getWindowLayouts(displayId: Int): List<String> { return ArrayList<String>() }
    override fun getTaskId(packageName: String): Int { return -1 }
    override fun moveTaskToBack(taskId: Int) {}
}
