package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import java.util.concurrent.Executors

/**
 * ShizukuInputHandler
 * Handles all key injection logic using the Shizuku shell service.
 * Separates input handling from the main OverlayService.
 */
class ShizukuInputHandler(
    private val context: Context,
    private var shellService: IShellService?,
    private var displayId: Int
) {
    companion object {
        private const val TAG = "ShizukuInputHandler"
    }

    private val executor = Executors.newSingleThreadExecutor()

    // Timestamp for tracking injection timing
    var lastInjectionTime: Long = 0L
        private set

    fun updateDisplay(displayId: Int) {
        this.displayId = displayId

    }

    fun updateShellService(shellService: IShellService?) {
        this.shellService = shellService
    }

    /**
     * Injects a single key event.
     * @param keyCode The Android KeyEvent keycode
     * @param action KeyEvent.ACTION_DOWN or ACTION_UP (default: ACTION_DOWN)
     * @param metaState Meta key state (default: 0)
     * @param blockSoftKeyboard Whether soft keyboard blocking is enabled (affects deviceId)
     */
    fun injectKey(keyCode: Int, action: Int = KeyEvent.ACTION_DOWN, metaState: Int = 0, blockSoftKeyboard: Boolean = false) {
        // Dynamic Device ID:
        // Blocking ON: Use 1 (Physical) to maintain "Hardware Keyboard" state.
        // Blocking OFF: Use -1 (Virtual). ID 0 is often ignored by Gboard. -1 is standard software injection.
        val deviceId = if (blockSoftKeyboard) 1 else -1
        shellService?.injectKey(keyCode, action, metaState, displayId, deviceId)
    }

    /**
     * Injects a key from the keyboard overlay.
     * Routes system keys through shell injection and text keys based on active IME.
     */
    fun injectKeyFromKeyboard(keyCode: Int, metaState: Int) {
        lastInjectionTime = System.currentTimeMillis()

        val systemKeys = setOf(
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_FORWARD,
            KeyEvent.KEYCODE_HOME,
            KeyEvent.KEYCODE_APP_SWITCH,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_MUTE,
            KeyEvent.KEYCODE_POWER,
            KeyEvent.KEYCODE_SLEEP,
            KeyEvent.KEYCODE_WAKEUP,
            KeyEvent.KEYCODE_MEDIA_PLAY,
            KeyEvent.KEYCODE_MEDIA_PAUSE,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_MEDIA_STOP,
            KeyEvent.KEYCODE_MEDIA_NEXT,
            KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            KeyEvent.KEYCODE_BRIGHTNESS_UP,
            KeyEvent.KEYCODE_BRIGHTNESS_DOWN
        )

        executor.execute {
            try {
                if (keyCode in systemKeys) {

                    shellService?.injectKey(keyCode, KeyEvent.ACTION_DOWN, metaState, displayId, 1)
                    Thread.sleep(10)
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_UP, metaState, displayId, 1)
                    return@execute
                }

                val currentIme = android.provider.Settings.Secure.getString(context.contentResolver, "default_input_method") ?: ""
                val isDockActive = currentIme.contains(context.packageName) &&
                    (currentIme.contains("DockInputMethodService") || currentIme.contains("NullInputMethodService"))

                // =================================================================================
                // COVER SCREEN (Display 1) FLASH ISSUE - DOCUMENTED TESTING RESULTS
                // =================================================================================
                // PROBLEM: On Samsung cover screen, UI elements flash on every keystroke.
                // ROOT CAUSE: Samsung's AOD service reacts to InputManager.injectInputEvent().
                //
                // WHAT WORKS (no flash):
                // - Display 0 with DroidOS IME: Uses NullIME InputConnection.sendKeyEvent()
                // - Display 0 with Gboard/Samsung KB: Shell injection with device ID 0
                //
                // WHAT DOESN'T WORK (still flashes on display 1):
                // - Device ID 0: AOD still reacts
                // - Device ID 1: AOD still reacts  
                // - Device ID -1: AOD still reacts
                // - FLAG_SOFT_KEYBOARD (2): AOD still reacts
                // - FLAG_FROM_SYSTEM (8): AOD still reacts
                // - SOURCE_TOUCHSCREEN: AOD still reacts
                // - SOURCE_KEYBOARD: AOD still reacts
                // - Forcing IME back with "ime set": Samsung immediately switches back to HoneyBoard
                //   and NullIME loses InputConnection before broadcast arrives
                //
                // CONCLUSION: Samsung's AOD service on cover screen reacts to ANY 
                // InputManager.injectInputEvent() call. The only flash-free path is
                // InputConnection.sendKeyEvent() via NullIME, but Samsung aggressively
                // switches IME to HoneyBoard on cover screen, making this path unavailable.
                //
                // CURRENT BEHAVIOR: Typing works on cover screen but with visual flash.
                // =================================================================================

                if (isDockActive) {
                    // DroidOS IME active - use broadcast path (no flash)
                    val intent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.INJECT_KEY")
                    intent.setPackage(context.packageName)
                    intent.putExtra("keyCode", keyCode)
                    intent.putExtra("metaState", metaState)
                    context.sendBroadcast(intent)
                } else {
                    // Shell injection - works but flashes on cover screen (Samsung AOD limitation)
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_DOWN, metaState, displayId, 1)
                    Thread.sleep(10)
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_UP, metaState, displayId, 1)
                }
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Injects multiple delete key presses for bulk deletion.
     */
    fun injectBulkDelete(length: Int) {
        if (length <= 0) return

        lastInjectionTime = System.currentTimeMillis()

        executor.execute {
            try {
                val currentIme = android.provider.Settings.Secure.getString(context.contentResolver, "default_input_method") ?: ""
                val isDockActive = currentIme.contains(context.packageName) &&
                    (currentIme.contains("DockInputMethodService") || currentIme.contains("NullInputMethodService"))

                if (isDockActive) {
                    val intent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.INJECT_DELETE")
                    intent.setPackage(context.packageName)
                    intent.putExtra("length", length)
                    context.sendBroadcast(intent)
                } else {
                    for (i in 0 until length) {
                        shellService?.injectKey(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_DOWN, 0, displayId, 1)
                        Thread.sleep(5)
                        shellService?.injectKey(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_UP, 0, displayId, 1)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Injects text input.
     */
    fun injectText(text: String) {
        lastInjectionTime = System.currentTimeMillis()

        executor.execute {
            try {
                val currentIme = android.provider.Settings.Secure.getString(context.contentResolver, "default_input_method") ?: ""
                val isDockActive = currentIme.contains(context.packageName) &&
                    (currentIme.contains("DockInputMethodService") || currentIme.contains("NullInputMethodService"))

                if (isDockActive) {
                    val intent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.INJECT_TEXT")
                    intent.setPackage(context.packageName)
                    intent.putExtra("text", text)
                    context.sendBroadcast(intent)
                } else {
                    val escapedText = text.replace("\"", "\\\"")
                    val cmd = "input -d $displayId text \"$escapedText\""
                    shellService?.runCommand(cmd)
                }
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Injects a mouse event (move, hover, down, up) at specified coordinates.
     * @param action MotionEvent action (ACTION_MOVE, ACTION_DOWN, ACTION_UP, ACTION_HOVER_MOVE)
     * @param x Cursor X position
     * @param y Cursor Y position
     * @param targetDisplayId Display to inject to
     * @param source InputDevice source (SOURCE_MOUSE, SOURCE_TOUCHSCREEN)
     * @param button Button state (BUTTON_PRIMARY, BUTTON_SECONDARY, etc.)
     * @param time Event timestamp
     */
    fun injectMouse(action: Int, x: Float, y: Float, targetDisplayId: Int, source: Int, button: Int, time: Long) {
        if (shellService == null) return
        executor.execute {
            try {
                shellService?.injectMouse(action, x, y, targetDisplayId, source, button, time)
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Injects a scroll event at specified coordinates.
     * @param x Cursor X position
     * @param y Cursor Y position
     * @param hScroll Horizontal scroll amount
     * @param vScroll Vertical scroll amount
     * @param targetDisplayId Display to inject to
     */
    fun injectScroll(x: Float, y: Float, hScroll: Float, vScroll: Float, targetDisplayId: Int) {
        if (shellService == null) return
        executor.execute {
            try {
                shellService?.injectScroll(x, y, vScroll / 10f, hScroll / 10f, targetDisplayId)
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Performs a click at specified coordinates.
     * @param x Cursor X position
     * @param y Cursor Y position
     * @param targetDisplayId Display to inject to
     * @param isRightClick True for right click, false for left click
     */
    fun performClick(x: Float, y: Float, targetDisplayId: Int, isRightClick: Boolean) {
        if (shellService == null) return
        executor.execute {
            try {
                if (isRightClick) {
                    shellService?.execRightClick(x, y, targetDisplayId)
                } else {
                    shellService?.execClick(x, y, targetDisplayId)
                }
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Performs a swipe gesture from current position.
     * @param startX Starting X position
     * @param startY Starting Y position
     * @param dx Delta X (swipe distance)
     * @param dy Delta Y (swipe distance)
     * @param targetDisplayId Display to inject to
     */
    fun performSwipe(startX: Float, startY: Float, dx: Float, dy: Float, targetDisplayId: Int) {
        if (shellService == null) return
        executor.execute {
            try {
                val now = SystemClock.uptimeMillis()
                val endX = startX + dx
                val endY = startY + dy
                shellService?.injectMouse(MotionEvent.ACTION_DOWN, startX, startY, targetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now)
                for (i in 1..5) {
                    val t = i / 5f
                    shellService?.injectMouse(MotionEvent.ACTION_MOVE, startX + (dx * t), startY + (dy * t), targetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now + (i * 10))
                    Thread.sleep(10)
                }
                shellService?.injectMouse(MotionEvent.ACTION_UP, endX, endY, targetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now + 100)
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Shuts down the executor thread.
     */
    fun shutdown() {
        executor.shutdownNow()
    }
}
