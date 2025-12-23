
package com.example.coverscreentester

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent

class ShizukuInputHandler(
    private val shellService: IShellService?,
    private var displayId: Int
) {
    fun updateDisplay(newDisplayId: Int) {
        this.displayId = newDisplayId
    }

    fun moveMouseRelative(dx: Float, dy: Float) {
        if (shellService == null) return
        
        // Convert to Int for shell command
        val dxInt = dx.toInt()
        val dyInt = dy.toInt()
        
        if (dxInt == 0 && dyInt == 0) return

        Thread {
            try {
                // Use input command for relative movement
                shellService.runCommand("input -d $displayId mouse relative $dxInt $dyInt")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun injectKey(keyCode: Int, metaState: Int) {
        if (shellService == null) return
        Thread {
            try {
                // Inject Down and Up events
                shellService.injectKey(keyCode, android.view.KeyEvent.ACTION_DOWN, metaState, displayId, -1)
                Thread.sleep(10)
                shellService.injectKey(keyCode, android.view.KeyEvent.ACTION_UP, metaState, displayId, -1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}
