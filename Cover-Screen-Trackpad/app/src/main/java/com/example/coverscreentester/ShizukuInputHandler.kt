package com.example.coverscreentester

import android.util.Log

object ShizukuInputHandler {
    private const val TAG = "ShizukuInputHandler"

    /**
     * Injects a relative mouse movement event to a specific display.
     * * @param dx The change in X.
     * @param dy The change in Y.
     * @param displayId The ID of the target display (0 is usually local, others are virtual/external).
     */
    fun injectMouseMovement(dx: Float, dy: Float, displayId: Int) {
        // Construct the command with the display flag (-d) explicitly placed before 'mouse'
        val command = "input -d $displayId mouse relative $dx $dy"
        executeCommand(command)
    }

    /**
     * Injects a mouse click event to a specific display.
     */
    fun injectMouseClick(displayId: Int) {
        val command = "input -d $displayId mouse tap 0 0" // Tap coordinates often ignored for mouse click, but required by syntax
        executeCommand(command)
    }

    private fun executeCommand(command: String) {
        try {
            // Using Shizuku to execute the shell command
//             val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            // If strictly using Shizuku binding without 'su', replace above with:
            // Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inject input: ${e.message}")
        }
    }
}
