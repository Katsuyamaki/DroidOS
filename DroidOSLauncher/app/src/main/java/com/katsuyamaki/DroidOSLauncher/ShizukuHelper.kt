package com.katsuyamaki.DroidOSLauncher

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import java.lang.reflect.Method

object ShizukuHelper {
    const val SHIZUKU_PERMISSION_REQUEST_CODE = 1001

    // Reflection to access hidden 'newProcess' method
    private val newProcessMethod: Method by lazy {
        val clazz = Class.forName("rikka.shizuku.Shizuku")
        val method = clazz.getDeclaredMethod(
            "newProcess",
            Array<String>::class.java,
            Array<String>::class.java,
            String::class.java
        )
        method.isAccessible = true
        method
    }

    /**
     * Checks if Shizuku is available (Service bound and version correct).
     */
    fun isShizukuAvailable(): Boolean {
        return try {
            !Shizuku.isPreV11() && Shizuku.getVersion() >= 11
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks if we already have the permission.
     */
    fun hasPermission(): Boolean {
        return if (isShizukuAvailable()) {
            try {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }

    /**
     * Requests permission. Default code used if none provided.
     */
    fun requestPermission(requestCode: Int = SHIZUKU_PERMISSION_REQUEST_CODE) {
        if (isShizukuAvailable()) {
            Shizuku.requestPermission(requestCode)
        }
    }

    /**
     * Executes a generic shell command using Shizuku.
     */
    fun runShellCommand(commandString: String) {
        val command = arrayOf("sh", "-c", commandString)
        val process = newProcessMethod.invoke(null, command, null, null) as Process
        process.waitFor()
    }

    /**
     * Kills the target app to force a window reset.
     */
    fun killApp(packageName: String) {
        runShellCommand("am force-stop $packageName")
    }
}
