package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
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
        Log.d(TAG, "Display updated to $displayId")
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
                    Log.d(TAG, "System key detected ($keyCode), using shell injection")
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_DOWN, metaState, displayId, 1)
                    Thread.sleep(10)
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_UP, metaState, displayId, 1)
                    return@execute
                }

                val currentIme = android.provider.Settings.Secure.getString(context.contentResolver, "default_input_method") ?: ""
                val isDockActive = currentIme.contains(context.packageName) &&
                    (currentIme.contains("DockInputMethodService") || currentIme.contains("NullInputMethodService"))

                if (isDockActive) {
                    val intent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.INJECT_KEY")
                    intent.setPackage(context.packageName)
                    intent.putExtra("keyCode", keyCode)
                    intent.putExtra("metaState", metaState)
                    context.sendBroadcast(intent)
                } else {
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_DOWN, metaState, displayId, 1)
                    Thread.sleep(10)
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_UP, metaState, displayId, 1)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Key injection failed", e)
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
                Log.e(TAG, "Bulk delete failed", e)
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
                Log.e(TAG, "Text injection failed", e)
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
