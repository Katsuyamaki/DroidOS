/*
 * ======================================================================================
 * CRITICAL REGRESSION CHECKLIST - SHELL SERVICE
 * ======================================================================================
 * 1. REFLECTION LOGIC (Android 14+ Support):
 * - `getDisplayControlClass()` MUST use `ClassLoaderFactory` to load `com.android.server.display.DisplayControl`.
 * - Do NOT simplify this to just `Class.forName()` or it will break on Android 14.
 *
 * 2. BRIGHTNESS CONTROL (Alternate Mode):
 * - `setBrightness(-1)` MUST set `screen_brightness_float` to `-1.0f`.
 * - It MUST call `setBrightnessViaDisplayManager(id, -1.0f)` to trigger the OLED off driver state.
 *
 * 3. SCREEN OFF (Standard Mode):
 * - `setScreenOff` MUST use `SurfaceControl.setDisplayPowerMode`.
 * - It MUST iterate `getAllPhysicalDisplayTokens()` to find the correct display (Cover vs Main).
 *
 * 4. INPUT INJECTION:
 * - Primary method: `InputManager.injectInputEvent` (Reflection) for low latency.
 * - Fallback method: `Runtime.getRuntime().exec("input ...")` must remain in catch blocks.
 * - `injectScroll` requires `MotionEvent.obtain` with valid pointer properties.
 *
 * 5. WINDOW MANAGEMENT:
 * - `repositionTask` must use `am task resize` shell commands.
 * ======================================================================================
 */

package com.example.coverscreentester

import android.os.Binder
import android.os.IBinder
import android.os.Process
import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import com.example.coverscreentester.IShellService
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Method
import java.util.ArrayList
import android.os.Build
import android.annotation.SuppressLint

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"
    private lateinit var inputManager: Any
    private lateinit var injectInputEventMethod: Method
    private val INJECT_MODE_ASYNC = 0
    private var isReflectionBroken = false

    // --- Screen Control Reflection ---
    companion object {
        const val POWER_MODE_OFF = 0
        const val POWER_MODE_NORMAL = 2
        
        @Volatile private var displayControlClass: Class<*>? = null
        @Volatile private var displayControlClassLoaded = false
    }

    private val surfaceControlClass: Class<*> by lazy {
        Class.forName("android.view.SurfaceControl")
    }

    init {
        setupReflection()
    }

    private fun setupReflection() {
        try {
            val imClass = Class.forName("android.hardware.input.InputManager")
            val getInstance = imClass.getMethod("getInstance")
            inputManager = getInstance.invoke(null)!!
            injectInputEventMethod = imClass.getMethod("injectInputEvent", InputEvent::class.java, Int::class.javaPrimitiveType)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup reflection", e)
            isReflectionBroken = true
        }
    }

    // --- HELPER: Display Control Class (Android 14+) ---
    @SuppressLint("BlockedPrivateApi")
    private fun getDisplayControlClass(): Class<*>? {
        if (displayControlClassLoaded && displayControlClass != null) return displayControlClass
        
        return try {
            val classLoaderFactoryClass = Class.forName("com.android.internal.os.ClassLoaderFactory")
            val createClassLoaderMethod = classLoaderFactoryClass.getDeclaredMethod(
                "createClassLoader",
                String::class.java,
                String::class.java,
                String::class.java,
                ClassLoader::class.java,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                String::class.java
            )
            val classLoader = createClassLoaderMethod.invoke(
                null, "/system/framework/services.jar", null, null,
                ClassLoader.getSystemClassLoader(), 0, true, null
            ) as ClassLoader

            val loadedClass = classLoader.loadClass("com.android.server.display.DisplayControl").also {
                val loadMethod = Runtime::class.java.getDeclaredMethod(
                    "loadLibrary0",
                    Class::class.java,
                    String::class.java
                )
                loadMethod.isAccessible = true
                loadMethod.invoke(Runtime.getRuntime(), it, "android_servers")
            }
            
            displayControlClass = loadedClass
            displayControlClassLoaded = true
            loadedClass
        } catch (e: Exception) {
            Log.w(TAG, "DisplayControl not available", e)
            null
        }
    }

    // --- HELPER: Get Physical Display Tokens ---
    private fun getAllPhysicalDisplayTokens(): List<IBinder> {
        val tokens = ArrayList<IBinder>()
        try {
            val physicalIds: LongArray = if (Build.VERSION.SDK_INT >= 34) {
                val controlClass = getDisplayControlClass()
                if (controlClass != null) {
                    controlClass.getMethod("getPhysicalDisplayIds").invoke(null) as LongArray
                } else {
                     try {
                        surfaceControlClass.getMethod("getPhysicalDisplayIds").invoke(null) as LongArray
                     } catch (e: Exception) { LongArray(0) }
                }
            } else {
                surfaceControlClass.getMethod("getPhysicalDisplayIds").invoke(null) as LongArray
            }

            if (physicalIds.isEmpty()) {
                getSurfaceControlInternalToken()?.let { tokens.add(it) }
                return tokens
            }

            for (id in physicalIds) {
                try {
                    val token: IBinder? = if (Build.VERSION.SDK_INT >= 34) {
                        val controlClass = getDisplayControlClass()
                        if (controlClass != null) {
                             controlClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
                                .invoke(null, id) as? IBinder
                        } else {
                            surfaceControlClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
                                .invoke(null, id) as? IBinder
                        }
                    } else {
                        surfaceControlClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
                            .invoke(null, id) as? IBinder
                    }
                    
                    if (token != null) tokens.add(token)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to get token for physical ID $id", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Critical failure getting display tokens", e)
        }
        return tokens
    }

    private fun getSurfaceControlInternalToken(): IBinder? {
        return try {
            if (Build.VERSION.SDK_INT < 29) {
                surfaceControlClass.getMethod("getBuiltInDisplay", Int::class.java).invoke(null, 0) as IBinder
            } else {
                surfaceControlClass.getMethod("getInternalDisplayToken").invoke(null) as IBinder
            }
        } catch (e: Exception) { null }
    }

    private fun setPowerModeOnToken(token: IBinder, mode: Int) {
        try {
            val method = surfaceControlClass.getMethod(
                "setDisplayPowerMode",
                IBinder::class.java,
                Int::class.javaPrimitiveType
            )
            method.invoke(null, token, mode)
        } catch (e: Exception) {
            Log.e(TAG, "setDisplayPowerMode failed for token $token", e)
        }
    }

    private fun setDisplayBrightnessOnToken(token: IBinder, brightness: Float): Boolean {
        try {
            val method = surfaceControlClass.getMethod(
                "setDisplayBrightness",
                IBinder::class.java,
                Float::class.javaPrimitiveType
            )
            method.invoke(null, token, brightness)
            return true
        } catch (e: Exception) {
             try {
                val method = surfaceControlClass.getMethod(
                    "setDisplayBrightness",
                    IBinder::class.java,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType
                )
                method.invoke(null, token, brightness, brightness, brightness, brightness)
                return true
            } catch (e2: Exception) {
                return false
            }
        }
    }

    // --- SHELL COMMAND HELPER ---
    private fun execShell(cmd: String) {
        try {
            Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd)).waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Shell command failed", e)
        }
    }

    // --- IMPLEMENTATION: BRIGHTNESS (ALTERNATE MODE) ---
    override fun setBrightness(value: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            Log.i(TAG, "setBrightness: $value")
            execShell("settings put system screen_brightness_mode 0")

            if (value == -1) {
                // Alternate Mode (Extinguish)
                execShell("settings put system screen_brightness_min 0")
                execShell("settings put system screen_brightness_float -1.0")
                execShell("settings put system screen_brightness -1")
                
                val tokens = getAllPhysicalDisplayTokens()
                val safeTokens = tokens.take(2)
                for (t in safeTokens) {
                    setDisplayBrightnessOnToken(t, -1.0f)
                }
            } else {
                // Wake Up
                val safeVal = value.coerceIn(1, 255)
                val floatVal = safeVal / 255.0f
                
                execShell("settings put system screen_brightness_float $floatVal")
                execShell("settings put system screen_brightness $safeVal")
                
                val tokens = getAllPhysicalDisplayTokens()
                for (t in tokens) {
                    setDisplayBrightnessOnToken(t, floatVal)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setBrightness", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    // --- IMPLEMENTATION: SCREEN OFF (STANDARD MODE) ---
    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        val token = Binder.clearCallingIdentity()
        try {
            val mode = if (turnOff) POWER_MODE_OFF else POWER_MODE_NORMAL
            
            val tokens = getAllPhysicalDisplayTokens()
            val safeTokens = tokens.take(2) // Safety: Only target first 2 displays
            
            for (t in safeTokens) {
                setPowerModeOnToken(t, mode)
            }
        } catch (e: Exception) {
            Log.e(TAG, "setScreenOff failed", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    
    override fun setBrightnessViaDisplayManager(displayId: Int, brightness: Float): Boolean {
        // Fallback interface compliance
        return false
    }

    // --- INPUT INJECTION (UNCHANGED) ---
    override fun injectKey(keyCode: Int, action: Int, metaState: Int, displayId: Int) {
        if (!this::inputManager.isInitialized) return
        val now = SystemClock.uptimeMillis()
        // REVERT: Back to deviceId = -1 (Virtual). 
        // Spoofing '1' didn't help hide the keyboard and might cause driver confusion.
        val event = KeyEvent(now, now, action, keyCode, 0, metaState, -1, 0, 0, InputDevice.SOURCE_KEYBOARD)
        try {
            val method = InputEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            method.invoke(event, displayId)
            injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
        } catch (e: Exception) {
            if (action == KeyEvent.ACTION_DOWN) execShell("input keyevent $keyCode")
        }
    }

    override fun injectMouse(action: Int, x: Float, y: Float, displayId: Int, source: Int, buttonState: Int, downTime: Long) {
        injectInternal(action, x, y, displayId, downTime, SystemClock.uptimeMillis(), source, buttonState)
    }
    
    override fun injectScroll(x: Float, y: Float, vDistance: Float, hDistance: Float, displayId: Int) {
         if (!this::inputManager.isInitialized) return
         val now = SystemClock.uptimeMillis()
         val props = MotionEvent.PointerProperties(); props.id = 0; props.toolType = MotionEvent.TOOL_TYPE_MOUSE
         val coords = MotionEvent.PointerCoords(); coords.x = x; coords.y = y; coords.pressure = 1.0f; coords.size = 1.0f
         coords.setAxisValue(MotionEvent.AXIS_VSCROLL, vDistance)
         coords.setAxisValue(MotionEvent.AXIS_HSCROLL, hDistance)
         try {
             val event = MotionEvent.obtain(now, now, MotionEvent.ACTION_SCROLL, 1, arrayOf(props), arrayOf(coords), 0, 0, 1.0f, 1.0f, 0, 0, InputDevice.SOURCE_MOUSE, 0)
             val method = MotionEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
             method.invoke(event, displayId)
             injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
             event.recycle()
         } catch(e: Exception){}
    }

    override fun execClick(x: Float, y: Float, displayId: Int) {
        val now = SystemClock.uptimeMillis()
        injectInternal(MotionEvent.ACTION_DOWN, x, y, displayId, now, now, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY)
        injectInternal(MotionEvent.ACTION_UP, x, y, displayId, now, now+50, InputDevice.SOURCE_MOUSE, 0)
    }

    override fun execRightClick(x: Float, y: Float, displayId: Int) {
        val now = SystemClock.uptimeMillis()
        injectInternal(MotionEvent.ACTION_DOWN, x, y, displayId, now, now, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_SECONDARY)
        injectInternal(MotionEvent.ACTION_UP, x, y, displayId, now, now+50, InputDevice.SOURCE_MOUSE, 0)
    }

    private fun injectInternal(action: Int, x: Float, y: Float, displayId: Int, downTime: Long, eventTime: Long, source: Int, buttonState: Int) {
        if (!this::inputManager.isInitialized) return
        val props = MotionEvent.PointerProperties(); props.id = 0
        props.toolType = if (source == InputDevice.SOURCE_MOUSE) MotionEvent.TOOL_TYPE_MOUSE else MotionEvent.TOOL_TYPE_FINGER
        val coords = MotionEvent.PointerCoords(); coords.x = x; coords.y = y
        coords.pressure = if (buttonState != 0 || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) 1.0f else 0.0f; coords.size = 1.0f
        try {
            val event = MotionEvent.obtain(downTime, eventTime, action, 1, arrayOf(props), arrayOf(coords), 0, buttonState, 1.0f, 1.0f, 0, 0, source, 0)
            val method = MotionEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            method.invoke(event, displayId)
            injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
            event.recycle()
        } catch (e: Exception) { Log.e(TAG, "Injection failed", e) }
    }

    override fun setWindowingMode(taskId: Int, mode: Int) {}
    override fun resizeTask(taskId: Int, left: Int, top: Int, right: Int, bottom: Int) {}
    override fun runCommand(cmd: String): String {
        val token = Binder.clearCallingIdentity()
        val output = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "runCommand failed: $cmd", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
        return output.toString()
    }
}