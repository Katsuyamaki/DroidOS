This file is a merged representation of a subset of the codebase, containing files not matching ignore patterns, combined into a single document by Repomix.

# File Summary

## Purpose
This file contains a packed representation of a subset of the repository's contents that is considered the most important context.
It is designed to be easily consumable by AI systems for analysis, code review,
or other automated processes.

## File Format
The content is organized as follows:
1. This summary section
2. Repository information
3. Directory structure
4. Repository files (if enabled)
5. Multiple file entries, each consisting of:
  a. A header with the file path (## File: path/to/file)
  b. The full contents of the file in a code block

## Usage Guidelines
- This file should be treated as read-only. Any changes should be made to the
  original repository files, not this packed version.
- When processing this file, use the file path to distinguish
  between different files in the repository.
- Be aware that this file may contain sensitive information. Handle it with
  the same level of security as you would the original repository.

## Notes
- Some files may have been excluded based on .gitignore rules and Repomix's configuration
- Binary files are not included in this packed representation. Please refer to the Repository Structure section for a complete list of file paths, including binary files
- Files matching these patterns are excluded: **/.gradle/**, **/build/**, **/*.png, **/*.webp
- Files matching patterns in .gitignore are excluded
- Files matching default ignore patterns are excluded
- Files are sorted by Git change count (files with more changes are at the bottom)

# Directory Structure
```
app/
  src/
    androidTest/
      java/
        com/
          example/
            coverscreentester/
              ExampleInstrumentedTest.kt
    main/
      aidl/
        com/
          example/
            coverscreentester/
              IShellService.aidl
      java/
        com/
          example/
            coverscreentester/
              KeyboardActivity.kt
              KeyboardManager.kt
              KeyboardOverlay.kt
              KeyboardUtils.kt
              KeyboardView.kt
              MainActivity.kt
              ManualAdjustActivity.kt
              OverlayService.kt
              ProfilesActivity.kt
              SettingsActivity.kt
              ShellUserService.kt
              ShizukuBinder.java
              ShizukuInputHandler.kt
              TrackpadService.kt
            quadrantlauncher/
              FloatingLauncherService.kt
      res/
        drawable/
          bg_item_active.xml
          bg_item_press.xml
          ic_cursor.xml
          ic_launcher_background.xml
          ic_launcher_foreground.xml
          ic_lock_closed.xml
          ic_lock_open.xml
          ic_trackpad_foreground_scaled.xml
          red_border.xml
        layout/
          activity_keyboard.xml
          activity_main.xml
          activity_manual_adjust.xml
          activity_menu.xml
          activity_profiles.xml
          activity_settings.xml
          item_dpi_custom.xml
          layout_trackpad.xml
          service_overlay.xml
        mipmap-anydpi-v26/
          ic_launcher_round.xml
          ic_launcher.xml
          ic_trackpad_adaptive.xml
        values/
          colors.xml
          strings.xml
          themes.xml
        values-night/
          themes.xml
        xml/
          accessibility_service_config.xml
          backup_rules.xml
          data_extraction_rules.xml
      AndroidManifest.xml
    test/
      java/
        com/
          example/
            coverscreentester/
              ExampleUnitTest.kt
  .gitignore
  build.gradle.kts
  proguard-rules.pro
gradle/
  wrapper/
    gradle-wrapper.jar
    gradle-wrapper.properties
  libs.versions.toml
.gitignore
A
build.gradle.kts
Compilation
Configure
CoverScreenTrackpad.apk
crash_log.txt
Get
gradle.properties
gradlew
gradlew.bat
README.md
Run
settings.gradle.kts
Task
```

# Files

## File: app/src/androidTest/java/com/example/coverscreentester/ExampleInstrumentedTest.kt
```kotlin
package com.example.coverscreentester

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.coverscreentester", appContext.packageName)
    }
}
```

## File: app/src/main/aidl/com/example/coverscreentester/IShellService.aidl
```
package com.example.coverscreentester;

interface IShellService {
    void injectMouse(int action, float x, float y, int displayId, int source, int buttonState, long downTime);
    void injectScroll(float x, float y, float vDistance, float hDistance, int displayId);
    void execClick(float x, float y, int displayId);
    void execRightClick(float x, float y, int displayId);
    void injectKey(int keyCode, int action);
    
    // New Window Management Methods
    void setWindowingMode(int taskId, int mode);
    void resizeTask(int taskId, int left, int top, int right, int bottom);
    String runCommand(String cmd);
    
    // NEW: Key injection on specific display
    void injectKeyOnDisplay(int keyCode, int action, int displayId);
}
```

## File: app/src/main/java/com/example/coverscreentester/KeyboardActivity.kt
```kotlin
package com.example.coverscreentester

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.View

/**
 * A dedicated Activity to hold Keyboard Focus.
 * We use an Activity instead of a Service Overlay because Android 
 * aggressively kills keyboards attached to non-focusable windows 
 * or inactive displays.
 */
class KeyboardActivity : Activity() {

    private lateinit var inputField: EditText
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make window transparent
        window.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Ensure we get focus
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        
        setContentView(R.layout.activity_keyboard)

        inputField = findViewById(R.id.et_remote_input)
        statusText = findViewById(R.id.tv_status)
        
        // Dismiss if user taps the transparent top area
        findViewById<View>(R.id.view_touch_dismiss).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_close).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_clear).setOnClickListener {
            inputField.setText("")
        }

        inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0 && s != null) {
                    val char = s[start]
                    sendCharToService(char)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                // Do NOT clear text automatically. It causes keyboard flicker.
            }
        })
        
        inputField.requestFocus()
    }

    private fun sendCharToService(c: Char) {
        val intent = Intent("INJECT_CHAR")
        intent.setPackage(packageName)
        intent.putExtra("CHAR", c)
        sendBroadcast(intent)
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/KeyboardManager.kt
```kotlin
package com.example.coverscreentester

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.abs

class KeyboardManager(
    private val context: Context,
    private val windowManager: WindowManager,
    private val keyInjector: (Int) -> Unit
) {
    var keyboardLayout: FrameLayout? = null
    var layoutParams: WindowManager.LayoutParams? = null
    
    private var isShifted = false
    private var isSymbols = false
    private var isVisible = false
    
    // UI Constants
    private val KEY_HEIGHT = 45.dp
    private val ROW_MARGIN = 2.dp
    private val KEY_MARGIN = 2.dp
    private var keyboardWidth = 450 // Default, will resize
    private var keyboardHeight = 350
    
    // Data Classes
    data class KeyDef(val label: String, val code: Int, val weight: Float = 1f, val isSpecial: Boolean = false)

    private val ROW_1 = listOf(
        KeyDef("q", KeyEvent.KEYCODE_Q), KeyDef("w", KeyEvent.KEYCODE_W), KeyDef("e", KeyEvent.KEYCODE_E),
        KeyDef("r", KeyEvent.KEYCODE_R), KeyDef("t", KeyEvent.KEYCODE_T), KeyDef("y", KeyEvent.KEYCODE_Y),
        KeyDef("u", KeyEvent.KEYCODE_U), KeyDef("i", KeyEvent.KEYCODE_I), KeyDef("o", KeyEvent.KEYCODE_O),
        KeyDef("p", KeyEvent.KEYCODE_P)
    )
    
    private val ROW_2 = listOf(
        KeyDef("a", KeyEvent.KEYCODE_A), KeyDef("s", KeyEvent.KEYCODE_S), KeyDef("d", KeyEvent.KEYCODE_D),
        KeyDef("f", KeyEvent.KEYCODE_F), KeyDef("g", KeyEvent.KEYCODE_G), KeyDef("h", KeyEvent.KEYCODE_H),
        KeyDef("j", KeyEvent.KEYCODE_J), KeyDef("k", KeyEvent.KEYCODE_K), KeyDef("l", KeyEvent.KEYCODE_L)
    )
    
    private val ROW_3 = listOf(
        KeyDef("SHIFT", -1, 1.5f, true),
        KeyDef("z", KeyEvent.KEYCODE_Z), KeyDef("x", KeyEvent.KEYCODE_X), KeyDef("c", KeyEvent.KEYCODE_C),
        KeyDef("v", KeyEvent.KEYCODE_V), KeyDef("b", KeyEvent.KEYCODE_B), KeyDef("n", KeyEvent.KEYCODE_N),
        KeyDef("m", KeyEvent.KEYCODE_M),
        KeyDef("⌫", KeyEvent.KEYCODE_DEL, 1.5f, true)
    )
    
    private val ROW_4 = listOf(
        KeyDef("?123", -2, 1.5f, true),
        KeyDef(",", KeyEvent.KEYCODE_COMMA), 
        KeyDef("SPACE", KeyEvent.KEYCODE_SPACE, 4f), 
        KeyDef(".", KeyEvent.KEYCODE_PERIOD),
        KeyDef("ENTER", KeyEvent.KEYCODE_ENTER, 1.5f, true)
    )

    private val ARROWS = listOf(
        KeyDef("◄", KeyEvent.KEYCODE_DPAD_LEFT, 1f, true),
        KeyDef("▲", KeyEvent.KEYCODE_DPAD_UP, 1f, true),
        KeyDef("▼", KeyEvent.KEYCODE_DPAD_DOWN, 1f, true),
        KeyDef("►", KeyEvent.KEYCODE_DPAD_RIGHT, 1f, true)
    )

    // Number Row (Replaces Row 1 in Symbol Mode)
    private val ROW_NUMS = listOf(
        KeyDef("1", KeyEvent.KEYCODE_1), KeyDef("2", KeyEvent.KEYCODE_2), KeyDef("3", KeyEvent.KEYCODE_3),
        KeyDef("4", KeyEvent.KEYCODE_4), KeyDef("5", KeyEvent.KEYCODE_5), KeyDef("6", KeyEvent.KEYCODE_6),
        KeyDef("7", KeyEvent.KEYCODE_7), KeyDef("8", KeyEvent.KEYCODE_8), KeyDef("9", KeyEvent.KEYCODE_9),
        KeyDef("0", KeyEvent.KEYCODE_0)
    )
    
    private val ROW_SYMS = listOf(
        KeyDef("@", KeyEvent.KEYCODE_AT), KeyDef("#", KeyEvent.KEYCODE_POUND), KeyDef("$", KeyEvent.KEYCODE_4), // $ shares 4 shift usually, simplified here
        KeyDef("%", KeyEvent.KEYCODE_5), KeyDef("&", KeyEvent.KEYCODE_7), KeyDef("-", KeyEvent.KEYCODE_MINUS),
        KeyDef("+", KeyEvent.KEYCODE_PLUS), KeyDef("(", KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN), KeyDef(")", KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN)
    )

    fun createView(): View {
        val root = FrameLayout(context)
        val bg = GradientDrawable()
        bg.setColor(Color.parseColor("#EE121212"))
        bg.cornerRadius = 20f
        bg.setStroke(2, Color.parseColor("#44FFFFFF"))
        root.background = bg

        val mainContainer = LinearLayout(context)
        mainContainer.orientation = LinearLayout.VERTICAL
        mainContainer.setPadding(10, 20, 10, 10)
        
        // Add Rows
        mainContainer.addView(createRow(if (isSymbols) ROW_NUMS else ROW_1))
        mainContainer.addView(createRow(if (isSymbols) ROW_SYMS else ROW_2))
        mainContainer.addView(createRow(ROW_3))
        mainContainer.addView(createRow(ROW_4))
        mainContainer.addView(createRow(ARROWS))

        root.addView(mainContainer, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        
        // Add Resize Handle (Bottom Right)
        val resizeHandle = View(context)
        resizeHandle.setBackgroundColor(Color.parseColor("#803DDC84"))
        val rhParams = FrameLayout.LayoutParams(50, 50)
        rhParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        root.addView(resizeHandle, rhParams)
        
        resizeHandle.setOnTouchListener { _, event -> handleResize(event) }
        
        // Add Move Handle (Top Center)
        val moveHandle = View(context)
        moveHandle.setBackgroundColor(Color.parseColor("#40FFFFFF"))
        val mhParams = FrameLayout.LayoutParams(100, 15)
        mhParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        mhParams.topMargin = 5
        root.addView(moveHandle, mhParams)
        
        moveHandle.setOnTouchListener { _, event -> handleMove(event) }

        return root
    }

    private fun createRow(keys: List<KeyDef>): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        
        for (k in keys) {
            val btn = TextView(context)
            val label = if (!isSymbols && isShifted && k.label.length == 1) k.label.uppercase() else k.label
            
            btn.text = label
            btn.setTextColor(Color.WHITE)
            btn.textSize = 14f
            btn.gravity = Gravity.CENTER
            btn.typeface = Typeface.DEFAULT_BOLD
            
            val keyBg = GradientDrawable()
            keyBg.setColor(if (k.isSpecial) Color.parseColor("#444444") else Color.parseColor("#2A2A2A"))
            keyBg.cornerRadius = 10f
            btn.background = keyBg
            
            val params = LinearLayout.LayoutParams(0, KEY_HEIGHT)
            params.weight = k.weight
            params.setMargins(KEY_MARGIN, ROW_MARGIN, KEY_MARGIN, ROW_MARGIN)
            row.addView(btn, params)
            
            btn.setOnClickListener {
                handleKeyPress(k)
                // Visual feedback
                btn.alpha = 0.5f
                btn.postDelayed({ btn.alpha = 1.0f }, 50)
            }
        }
        return row
    }

    private fun handleKeyPress(k: KeyDef) {
        when (k.code) {
            -1 -> { // SHIFT
                isShifted = !isShifted
                refreshLayout()
            }
            -2 -> { // SYMBOLS
                isSymbols = !isSymbols
                refreshLayout()
            }
            else -> {
                // If it's a letter and shifted, we rely on the injected keycode being consistent
                // In a real key event injection, KeyCode_A + CapsLock/Shift state is needed.
                // For simplicity, Shizuku injects the raw keycode. 
                // To support true caps, we might need to inject CAPS_LOCK toggle or shift modifier.
                // Current simplified approach: Just inject the code.
                keyInjector(k.code)
                
                // Auto unshift after one char (standard mobile behavior)
                if (isShifted) {
                    isShifted = false
                    refreshLayout()
                }
            }
        }
    }

    fun show(width: Int, height: Int) {
        if (isVisible) return
        
        keyboardWidth = width
        keyboardHeight = height

        layoutParams = WindowManager.LayoutParams(
            keyboardWidth,
            WindowManager.LayoutParams.WRAP_CONTENT, // Height dynamic based on rows
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or 
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        )
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        layoutParams?.y = 50 // Margin from bottom

        keyboardLayout = createView() as FrameLayout
        windowManager.addView(keyboardLayout, layoutParams)
        isVisible = true
    }

    fun hide() {
        if (!isVisible) return
        try {
            windowManager.removeView(keyboardLayout)
        } catch (e: Exception) {}
        isVisible = false
        keyboardLayout = null
    }
    
    fun toggle(width: Int, height: Int) {
        if (isVisible) hide() else show(width, height)
    }

    private fun refreshLayout() {
        if (!isVisible) return
        val p = keyboardLayout?.layoutParams
        windowManager.removeView(keyboardLayout)
        keyboardLayout = createView() as FrameLayout
        windowManager.addView(keyboardLayout, p)
    }

    // --- Helpers ---
    private val Int.dp: Int get() = (this * context.resources.displayMetrics.density).toInt()
    
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    
    private fun handleResize(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                layoutParams?.let { initialX = it.width; initialY = it.height } // abusing x/y vars for w/h
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.rawX - initialTouchX).toInt()
                layoutParams?.width = (initialX + dx).coerceAtLeast(300)
                // Height updates automatically due to WRAP_CONTENT, but we could force scale
                windowManager.updateViewLayout(keyboardLayout, layoutParams)
                return true
            }
        }
        return false
    }

    private fun handleMove(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                layoutParams?.let { initialX = it.x; initialY = it.y }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (initialTouchX - event.rawX).toInt() // Inverse for bottom gravity sometimes depending on config
                val dy = (initialTouchY - event.rawY).toInt()
                // For Gravity.BOTTOM, positive Y moves UP
                layoutParams?.y = (initialY + dy)
                windowManager.updateViewLayout(keyboardLayout, layoutParams)
                return true
            }
        }
        return false
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
```kotlin
package com.example.coverscreentester

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import kotlin.math.max

class KeyboardOverlay(
    private val context: Context,
    private val windowManager: WindowManager,
    private val shellService: IShellService?,
    private val targetDisplayId: Int
) : KeyboardView.KeyboardListener {

    private var keyboardContainer: FrameLayout? = null
    private var keyboardView: KeyboardView? = null
    private var keyboardParams: WindowManager.LayoutParams? = null
    private var isVisible = false

    private var isMoving = false
    private var isResizing = false
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialWindowX = 0
    private var initialWindowY = 0
    private var initialWidth = 0
    private var initialHeight = 0

    private val TAG = "KeyboardOverlay"
    private var keyboardWidth = 500
    private var keyboardHeight = 260
    private var screenWidth = 720
    private var screenHeight = 748
    
    // Store which display we're on for per-display persistence
    private var currentDisplayId = 0

    fun setScreenDimensions(width: Int, height: Int, displayId: Int = 0) {
        screenWidth = width
        screenHeight = height
        currentDisplayId = displayId
        
        // Load saved size for this display, or use defaults
        loadKeyboardSizeForDisplay(displayId)
        
        // If no saved size, calculate defaults
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        if (!prefs.contains("keyboard_width_d$displayId")) {
            keyboardWidth = (width * 0.95f).toInt().coerceIn(300, 650)
            keyboardHeight = (height * 0.36f).toInt().coerceIn(180, 320)
        }
    }

    fun updateTargetDisplay(newTargetId: Int): KeyboardOverlay {
        return KeyboardOverlay(context, windowManager, shellService, newTargetId).also {
            it.screenWidth = screenWidth
            it.screenHeight = screenHeight
            it.currentDisplayId = currentDisplayId
            it.loadKeyboardSizeForDisplay(currentDisplayId)
        }
    }

    fun show() {
        if (isVisible) return
        try { createKeyboardWindow(); isVisible = true } 
        catch (e: Exception) { Log.e(TAG, "Failed to show keyboard", e) }
    }

    fun hide() {
        if (!isVisible) return
        try { windowManager.removeView(keyboardContainer); keyboardContainer = null; keyboardView = null; isVisible = false } 
        catch (e: Exception) { Log.e(TAG, "Failed to hide keyboard", e) }
    }

    fun toggle() { if (isVisible) hide() else show() }
    fun isShowing(): Boolean = isVisible

    private fun createKeyboardWindow() {
        keyboardContainer = FrameLayout(context)
        val containerBg = GradientDrawable()
        containerBg.setColor(Color.parseColor("#1A1A1A"))
        containerBg.cornerRadius = 16f
        containerBg.setStroke(2, Color.parseColor("#3DDC84"))
        keyboardContainer?.background = containerBg

        keyboardView = KeyboardView(context)
        keyboardView?.setKeyboardListener(this)
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        keyboardView?.setVibrationEnabled(prefs.getBoolean("vibrate", true))

        val kbParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        kbParams.setMargins(6, 28, 6, 6)
        keyboardContainer?.addView(keyboardView, kbParams)

        addDragHandle()
        addResizeHandle()
        addCloseButton()
        addTargetLabel()

        // Load saved position for this display
        val savedX = prefs.getInt("keyboard_x_d$currentDisplayId", (screenWidth - keyboardWidth) / 2)
        val savedY = prefs.getInt("keyboard_y_d$currentDisplayId", screenHeight - keyboardHeight - 10)

        keyboardParams = WindowManager.LayoutParams(
            keyboardWidth, keyboardHeight,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        keyboardParams?.gravity = Gravity.TOP or Gravity.LEFT
        keyboardParams?.x = savedX
        keyboardParams?.y = savedY
        windowManager.addView(keyboardContainer, keyboardParams)
    }

    private fun addDragHandle() {
        val handle = FrameLayout(context)
        val handleParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 28)
        handleParams.gravity = Gravity.TOP
        val indicator = View(context)
        val indicatorBg = GradientDrawable()
        indicatorBg.setColor(Color.parseColor("#555555"))
        indicatorBg.cornerRadius = 3f
        indicator.background = indicatorBg
        val indicatorParams = FrameLayout.LayoutParams(50, 5)
        indicatorParams.gravity = Gravity.CENTER
        indicatorParams.topMargin = 8
        handle.addView(indicator, indicatorParams)
        handle.setOnTouchListener { _, event -> handleDrag(event); true }
        keyboardContainer?.addView(handle, handleParams)
    }

    private fun addResizeHandle() {
        val handle = FrameLayout(context)
        val handleParams = FrameLayout.LayoutParams(36, 36)
        handleParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        val indicator = View(context)
        val indicatorBg = GradientDrawable()
        indicatorBg.setColor(Color.parseColor("#3DDC84"))
        indicatorBg.cornerRadius = 4f
        indicator.background = indicatorBg
        indicator.alpha = 0.7f
        val indicatorParams = FrameLayout.LayoutParams(14, 14)
        indicatorParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        indicatorParams.setMargins(0, 0, 6, 6)
        handle.addView(indicator, indicatorParams)
        handle.setOnTouchListener { _, event -> handleResize(event); true }
        keyboardContainer?.addView(handle, handleParams)
    }

    private fun addCloseButton() {
        val button = FrameLayout(context)
        val buttonParams = FrameLayout.LayoutParams(28, 28)
        buttonParams.gravity = Gravity.TOP or Gravity.RIGHT
        buttonParams.setMargins(0, 2, 4, 0)
        val closeText = TextView(context)
        closeText.text = "X"
        closeText.setTextColor(Color.parseColor("#FF5555"))
        closeText.textSize = 12f
        closeText.gravity = Gravity.CENTER
        button.addView(closeText, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        button.setOnClickListener { hide() }
        keyboardContainer?.addView(button, buttonParams)
    }

    private fun addTargetLabel() {
        val label = TextView(context)
        label.text = "Display $targetDisplayId"
        label.setTextColor(Color.parseColor("#888888"))
        label.textSize = 9f
        val labelParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        labelParams.gravity = Gravity.TOP or Gravity.LEFT
        labelParams.setMargins(8, 6, 0, 0)
        keyboardContainer?.addView(label, labelParams)
    }

    private fun handleDrag(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { isMoving = true; initialTouchX = event.rawX; initialTouchY = event.rawY; initialWindowX = keyboardParams?.x ?: 0; initialWindowY = keyboardParams?.y ?: 0 }
            MotionEvent.ACTION_MOVE -> { if (isMoving) { keyboardParams?.x = initialWindowX + (event.rawX - initialTouchX).toInt(); keyboardParams?.y = initialWindowY + (event.rawY - initialTouchY).toInt(); try { windowManager.updateViewLayout(keyboardContainer, keyboardParams) } catch (e: Exception) {} } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isMoving = false; saveKeyboardPosition() }
        }
        return true
    }

    private fun handleResize(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { isResizing = true; initialTouchX = event.rawX; initialTouchY = event.rawY; initialWidth = keyboardParams?.width ?: keyboardWidth; initialHeight = keyboardParams?.height ?: keyboardHeight }
            MotionEvent.ACTION_MOVE -> { if (isResizing) { keyboardParams?.width = max(280, initialWidth + (event.rawX - initialTouchX).toInt()); keyboardParams?.height = max(180, initialHeight + (event.rawY - initialTouchY).toInt()); keyboardWidth = keyboardParams?.width ?: keyboardWidth; keyboardHeight = keyboardParams?.height ?: keyboardHeight; try { windowManager.updateViewLayout(keyboardContainer, keyboardParams) } catch (e: Exception) {} } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isResizing = false; saveKeyboardSize() }
        }
        return true
    }

    private fun saveKeyboardSize() {
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .putInt("keyboard_width_d$currentDisplayId", keyboardWidth)
            .putInt("keyboard_height_d$currentDisplayId", keyboardHeight)
            .apply()
    }

    private fun saveKeyboardPosition() {
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .putInt("keyboard_x_d$currentDisplayId", keyboardParams?.x ?: 0)
            .putInt("keyboard_y_d$currentDisplayId", keyboardParams?.y ?: 0)
            .apply()
    }

    private fun loadKeyboardSizeForDisplay(displayId: Int) {
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        keyboardWidth = prefs.getInt("keyboard_width_d$displayId", keyboardWidth)
        keyboardHeight = prefs.getInt("keyboard_height_d$displayId", keyboardHeight)
    }

    override fun onKeyPress(keyCode: Int, char: Char?) { injectKey(keyCode) }
    
    override fun onTextInput(text: String) {
        if (shellService == null) { Log.w(TAG, "Shell service not available"); return }
        
        Thread {
            try {
                val escaped = escapeForShell(text)
                val cmd = "input -d $targetDisplayId text $escaped"
                shellService.runCommand(cmd)
            } catch (e: Exception) { 
                Log.e(TAG, "Text injection failed: $text", e) 
            }
        }.start()
    }

    override fun onSpecialKey(key: KeyboardView.SpecialKey) {
        val keyCode = when (key) {
            KeyboardView.SpecialKey.BACKSPACE -> KeyEvent.KEYCODE_DEL
            KeyboardView.SpecialKey.ENTER -> KeyEvent.KEYCODE_ENTER
            KeyboardView.SpecialKey.SPACE -> KeyEvent.KEYCODE_SPACE
            KeyboardView.SpecialKey.TAB -> KeyEvent.KEYCODE_TAB
            KeyboardView.SpecialKey.ESCAPE -> KeyEvent.KEYCODE_ESCAPE
            KeyboardView.SpecialKey.ARROW_UP -> KeyEvent.KEYCODE_DPAD_UP
            KeyboardView.SpecialKey.ARROW_DOWN -> KeyEvent.KEYCODE_DPAD_DOWN
            KeyboardView.SpecialKey.ARROW_LEFT -> KeyEvent.KEYCODE_DPAD_LEFT
            KeyboardView.SpecialKey.ARROW_RIGHT -> KeyEvent.KEYCODE_DPAD_RIGHT
            KeyboardView.SpecialKey.HOME -> KeyEvent.KEYCODE_MOVE_HOME
            KeyboardView.SpecialKey.END -> KeyEvent.KEYCODE_MOVE_END
            KeyboardView.SpecialKey.DELETE -> KeyEvent.KEYCODE_FORWARD_DEL
            KeyboardView.SpecialKey.SHIFT -> KeyEvent.KEYCODE_SHIFT_LEFT
            KeyboardView.SpecialKey.CTRL -> KeyEvent.KEYCODE_CTRL_LEFT
            KeyboardView.SpecialKey.ALT -> KeyEvent.KEYCODE_ALT_LEFT
            else -> return
        }
        injectKey(keyCode)
    }

    private fun injectKey(keyCode: Int) {
        if (shellService == null) { Log.w(TAG, "Shell service not available"); return }
        Thread {
            try {
                val cmd = "input -d $targetDisplayId keyevent $keyCode"
                shellService.runCommand(cmd)
            } catch (e: Exception) { Log.e(TAG, "Key injection failed", e) }
        }.start()
    }
    
    private fun escapeForShell(text: String): String {
        val sb = StringBuilder()
        for (c in text) {
            when (c) {
                ' ' -> sb.append("%s")
                '\'' -> sb.append("'")
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '`' -> sb.append("\\`")
                '$' -> sb.append("\\$")
                '&' -> sb.append("\\&")
                '|' -> sb.append("\\|")
                ';' -> sb.append("\\;")
                '(' -> sb.append("\\(")
                ')' -> sb.append("\\)")
                '<' -> sb.append("\\<")
                '>' -> sb.append("\\>")
                '!' -> sb.append("\\!")
                '?' -> sb.append("\\?")
                '*' -> sb.append("\\*")
                '[' -> sb.append("\\[")
                ']' -> sb.append("\\]")
                '{' -> sb.append("\\{")
                '}' -> sb.append("\\}")
                '#' -> sb.append("\\#")
                '~' -> sb.append("\\~")
                '^' -> sb.append("\\^")
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/KeyboardUtils.kt
```kotlin
package com.example.coverscreentester

import android.view.KeyEvent

object KeyboardUtils {

    // Special IDs for logic
    const val KEY_SHIFT = -100
    const val KEY_SYM = -101
    const val KEY_SPACE = KeyEvent.KEYCODE_SPACE
    const val KEY_DEL = KeyEvent.KEYCODE_DEL
    const val KEY_ENTER = KeyEvent.KEYCODE_ENTER
    
    // Arrows
    const val KEY_UP = KeyEvent.KEYCODE_DPAD_UP
    const val KEY_DOWN = KeyEvent.KEYCODE_DPAD_DOWN
    const val KEY_LEFT = KeyEvent.KEYCODE_DPAD_LEFT
    const val KEY_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT

    data class KeyDef(val label: String, val code: Int, val widthWeight: Float = 1.0f)

    val ROW_1_LOWER = listOf(
        KeyDef("q", KeyEvent.KEYCODE_Q), KeyDef("w", KeyEvent.KEYCODE_W), KeyDef("e", KeyEvent.KEYCODE_E),
        KeyDef("r", KeyEvent.KEYCODE_R), KeyDef("t", KeyEvent.KEYCODE_T), KeyDef("y", KeyEvent.KEYCODE_Y),
        KeyDef("u", KeyEvent.KEYCODE_U), KeyDef("i", KeyEvent.KEYCODE_I), KeyDef("o", KeyEvent.KEYCODE_O), KeyDef("p", KeyEvent.KEYCODE_P)
    )
    
    val ROW_2_LOWER = listOf(
        KeyDef("a", KeyEvent.KEYCODE_A), KeyDef("s", KeyEvent.KEYCODE_S), KeyDef("d", KeyEvent.KEYCODE_D),
        KeyDef("f", KeyEvent.KEYCODE_F), KeyDef("g", KeyEvent.KEYCODE_G), KeyDef("h", KeyEvent.KEYCODE_H),
        KeyDef("j", KeyEvent.KEYCODE_J), KeyDef("k", KeyEvent.KEYCODE_K), KeyDef("l", KeyEvent.KEYCODE_L)
    )

    val ROW_3_LOWER = listOf(
        KeyDef("⇧", KEY_SHIFT, 1.5f), KeyDef("z", KeyEvent.KEYCODE_Z), KeyDef("x", KeyEvent.KEYCODE_X),
        KeyDef("c", KeyEvent.KEYCODE_C), KeyDef("v", KeyEvent.KEYCODE_V), KeyDef("b", KeyEvent.KEYCODE_B),
        KeyDef("n", KeyEvent.KEYCODE_N), KeyDef("m", KeyEvent.KEYCODE_M), KeyDef("⌫", KEY_DEL, 1.5f)
    )

    // UPPERCASE 
    val ROW_1_UPPER = ROW_1_LOWER.map { it.copy(label = it.label.uppercase()) }
    val ROW_2_UPPER = ROW_2_LOWER.map { it.copy(label = it.label.uppercase()) }
    val ROW_3_UPPER = ROW_3_LOWER.map { 
        if(it.label.length == 1) it.copy(label = it.label.uppercase()) else it 
    }

    // SYMBOLS
    val ROW_1_SYM = listOf(
        KeyDef("1", KeyEvent.KEYCODE_1), KeyDef("2", KeyEvent.KEYCODE_2), KeyDef("3", KeyEvent.KEYCODE_3),
        KeyDef("4", KeyEvent.KEYCODE_4), KeyDef("5", KeyEvent.KEYCODE_5), KeyDef("6", KeyEvent.KEYCODE_6),
        KeyDef("7", KeyEvent.KEYCODE_7), KeyDef("8", KeyEvent.KEYCODE_8), KeyDef("9", KeyEvent.KEYCODE_9), KeyDef("0", KeyEvent.KEYCODE_0)
    )
    
    val ROW_2_SYM = listOf(
        KeyDef("@", KeyEvent.KEYCODE_AT), KeyDef("#", KeyEvent.KEYCODE_POUND), KeyDef("$", KeyEvent.KEYCODE_4), // $ shares 4 shift usually, but we map roughly
        KeyDef("%", KeyEvent.KEYCODE_5), KeyDef("&", KeyEvent.KEYCODE_7), KeyDef("-", KeyEvent.KEYCODE_MINUS),
        KeyDef("+", KeyEvent.KEYCODE_PLUS), KeyDef("(", KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN), KeyDef(")", KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN)
    )
    
    val ROW_3_SYM = listOf(
        KeyDef("ABC", KEY_SYM, 1.5f), KeyDef("!", KeyEvent.KEYCODE_1), KeyDef("\"", KeyEvent.KEYCODE_APOSTROPHE),
        KeyDef("'", KeyEvent.KEYCODE_APOSTROPHE), KeyDef(":", KeyEvent.KEYCODE_SEMICOLON), KeyDef(";", KeyEvent.KEYCODE_SEMICOLON),
        KeyDef("/", KeyEvent.KEYCODE_SLASH), KeyDef("?", KeyEvent.KEYCODE_SLASH), KeyDef("⌫", KEY_DEL, 1.5f)
    )

    val ROW_4 = listOf(
        KeyDef("?123", KEY_SYM, 1.5f),
        KeyDef(",", KeyEvent.KEYCODE_COMMA),
        KeyDef("SPACE", KEY_SPACE, 4.0f),
        KeyDef(".", KeyEvent.KEYCODE_PERIOD),
        KeyDef("⏎", KEY_ENTER, 1.5f)
    )
    
    val ROW_5_ARROWS = listOf(
        KeyDef("HIDE", -999, 2.0f), // Hide
        KeyDef("◄", KEY_LEFT),
        KeyDef("▲", KEY_UP),
        KeyDef("▼", KEY_DOWN),
        KeyDef("►", KEY_RIGHT)
    )
}
```

## File: app/src/main/java/com/example/coverscreentester/KeyboardView.kt
```kotlin
package com.example.coverscreentester

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface KeyboardListener {
        fun onKeyPress(keyCode: Int, char: Char?)
        fun onTextInput(text: String)
        fun onSpecialKey(key: SpecialKey)
    }

    enum class SpecialKey {
        BACKSPACE, ENTER, SPACE, SHIFT, CAPS_LOCK, SYMBOLS, ABC,
        TAB, ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT,
        HOME, END, DELETE, ESCAPE, CTRL, ALT
    }

    enum class KeyboardState {
        LOWERCASE, UPPERCASE, CAPS_LOCK, SYMBOLS_1, SYMBOLS_2
    }

    private var listener: KeyboardListener? = null
    private var currentState = KeyboardState.LOWERCASE
    private var vibrationEnabled = true
    private var keyHeight = 40
    private var keySpacing = 2
    private var fontSize = 13f

    private val lowercaseRows = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m", "BKSP"),
        listOf("SYM", ",", "SPACE", ".", "ENTER")
    )

    private val uppercaseRows = listOf(
        listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
        listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
        listOf("SHIFT", "Z", "X", "C", "V", "B", "N", "M", "BKSP"),
        listOf("SYM", ",", "SPACE", ".", "ENTER")
    )

    private val symbols1Rows = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        listOf("@", "#", "\$", "%", "&", "-", "+", "(", ")"),
        listOf("SYM2", "*", "\"", "'", ":", ";", "!", "?", "BKSP"),
        listOf("ABC", ",", "SPACE", ".", "ENTER")
    )

    private val symbols2Rows = listOf(
        listOf("~", "`", "|", "^", "=", "{", "}", "[", "]", "\\"),
        listOf("<", ">", "/", "_", "©", "®", "™", "°", "•"),
        listOf("SYM1", "€", "£", "¥", "¢", "§", "¶", "∆", "BKSP"),
        listOf("ABC", "_", "SPACE", "/", "ENTER")
    )

    private val arrowRow = listOf("TAB", "CTRL", "←", "↑", "↓", "→", "ESC")

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#1A1A1A"))
        setPadding(4, 4, 4, 4)
        buildKeyboard()
    }

    fun setKeyboardListener(l: KeyboardListener) { listener = l }
    fun setVibrationEnabled(enabled: Boolean) { vibrationEnabled = enabled }

    private fun buildKeyboard() {
        removeAllViews()
        val rows = when (currentState) {
            KeyboardState.LOWERCASE -> lowercaseRows
            KeyboardState.UPPERCASE, KeyboardState.CAPS_LOCK -> uppercaseRows
            KeyboardState.SYMBOLS_1 -> symbols1Rows
            KeyboardState.SYMBOLS_2 -> symbols2Rows
        }
        for ((rowIndex, row) in rows.withIndex()) { addView(createRow(row, rowIndex)) }
        addView(createArrowRow())
    }

    private fun createRow(keys: List<String>, rowIndex: Int): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        row.gravity = Gravity.CENTER
        row.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(keyHeight)).apply {
            setMargins(0, dpToPx(keySpacing), 0, 0)
        }
        if (rowIndex == 1) row.setPadding(dpToPx(12), 0, dpToPx(12), 0)
        for (key in keys) { row.addView(createKey(key, getKeyWeight(key))) }
        return row
    }

    private fun createArrowRow(): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        row.gravity = Gravity.CENTER
        row.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(keyHeight - 4)).apply {
            setMargins(0, dpToPx(keySpacing + 2), 0, 0)
        }
        row.setBackgroundColor(Color.parseColor("#0D0D0D"))
        for (key in arrowRow) { row.addView(createKey(key, getKeyWeight(key), isArrowRow = true)) }
        return row
    }

    private fun getKeyWeight(key: String): Float = when (key) {
        "SPACE" -> 4.5f
        "SHIFT", "BKSP", "ENTER" -> 1.5f
        "SYM", "SYM1", "SYM2", "ABC" -> 1.3f
        "TAB", "CTRL", "ESC" -> 1.2f
        else -> 1f
    }

    private fun createKey(key: String, weight: Float, isArrowRow: Boolean = false): View {
        val container = FrameLayout(context)
        val params = LayoutParams(0, LayoutParams.MATCH_PARENT, weight)
        params.setMargins(dpToPx(keySpacing), 0, dpToPx(keySpacing), 0)
        container.layoutParams = params

        val keyView = TextView(context)
        keyView.gravity = Gravity.CENTER
        keyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isArrowRow) fontSize - 2 else fontSize)
        keyView.setTextColor(Color.WHITE)
        keyView.text = getDisplayText(key)

        val bg = GradientDrawable()
        bg.cornerRadius = dpToPx(6).toFloat()
        bg.setColor(getKeyColor(key, isArrowRow))
        keyView.background = bg
        keyView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        container.addView(keyView)

        container.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    bg.setColor(Color.parseColor("#4A4A4A"))
                    keyView.background = bg
                    if (vibrationEnabled) v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    bg.setColor(getKeyColor(key, isArrowRow))
                    keyView.background = bg
                    if (event.action == MotionEvent.ACTION_UP) handleKeyPress(key)
                }
            }
            true
        }
        if (key == "SHIFT") container.setOnLongClickListener { toggleCapsLock(); true }
        return container
    }

    private fun getDisplayText(key: String): String = when (key) {
        "SHIFT" -> if (currentState == KeyboardState.CAPS_LOCK) "⬆" else "⇧"
        "BKSP" -> "⌫"; "ENTER" -> "↵"; "SPACE" -> "␣"
        "SYM", "SYM1", "SYM2" -> "?123"; "ABC" -> "ABC"
        "TAB" -> "⇥"; "CTRL" -> "Ctrl"; "ESC" -> "Esc"
        "←" -> "◀"; "→" -> "▶"; "↑" -> "▲"; "↓" -> "▼"
        else -> key
    }

    private fun getKeyColor(key: String, isArrowRow: Boolean): Int {
        if (isArrowRow) return Color.parseColor("#252525")
        return when (key) {
            "SHIFT" -> when (currentState) {
                KeyboardState.CAPS_LOCK -> Color.parseColor("#3DDC84")
                KeyboardState.UPPERCASE -> Color.parseColor("#4A90D9")
                else -> Color.parseColor("#3A3A3A")
            }
            "BKSP", "ENTER", "SYM", "SYM1", "SYM2", "ABC" -> Color.parseColor("#3A3A3A")
            "SPACE" -> Color.parseColor("#2D2D2D")
            else -> Color.parseColor("#2D2D2D")
        }
    }

    private fun handleKeyPress(key: String) {
        when (key) {
            "SHIFT" -> toggleShift()
            "BKSP" -> listener?.onSpecialKey(SpecialKey.BACKSPACE)
            "ENTER" -> listener?.onSpecialKey(SpecialKey.ENTER)
            "SPACE" -> listener?.onSpecialKey(SpecialKey.SPACE)
            "TAB" -> listener?.onSpecialKey(SpecialKey.TAB)
            "CTRL" -> listener?.onSpecialKey(SpecialKey.CTRL)
            "ESC" -> listener?.onSpecialKey(SpecialKey.ESCAPE)
            "←" -> listener?.onSpecialKey(SpecialKey.ARROW_LEFT)
            "→" -> listener?.onSpecialKey(SpecialKey.ARROW_RIGHT)
            "↑" -> listener?.onSpecialKey(SpecialKey.ARROW_UP)
            "↓" -> listener?.onSpecialKey(SpecialKey.ARROW_DOWN)
            "SYM", "SYM1" -> { currentState = KeyboardState.SYMBOLS_1; buildKeyboard() }
            "SYM2" -> { currentState = KeyboardState.SYMBOLS_2; buildKeyboard() }
            "ABC" -> { currentState = KeyboardState.LOWERCASE; buildKeyboard() }
            else -> {
                listener?.onTextInput(key)
                if (currentState == KeyboardState.UPPERCASE) { currentState = KeyboardState.LOWERCASE; buildKeyboard() }
            }
        }
    }

    private fun toggleShift() {
        currentState = when (currentState) {
            KeyboardState.LOWERCASE -> KeyboardState.UPPERCASE
            KeyboardState.UPPERCASE -> KeyboardState.LOWERCASE
            KeyboardState.CAPS_LOCK -> KeyboardState.LOWERCASE
            else -> currentState
        }
        buildKeyboard()
    }

    private fun toggleCapsLock() {
        currentState = when (currentState) {
            KeyboardState.LOWERCASE, KeyboardState.UPPERCASE -> KeyboardState.CAPS_LOCK
            KeyboardState.CAPS_LOCK -> KeyboardState.LOWERCASE
            else -> currentState
        }
        if (vibrationEnabled) vibrate()
        buildKeyboard()
    }

    private fun vibrate() {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else { @Suppress("DEPRECATION") v.vibrate(30) }
    }

    private fun dpToPx(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
    ).roundToInt()
}
```

## File: app/src/main/java/com/example/coverscreentester/MainActivity.kt
```kotlin
package com.example.coverscreentester

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button
    private lateinit var hideAppButton: Button
    private lateinit var lockButton: Button
    private lateinit var profilesButton: Button
    private lateinit var settingsButton: Button
    private lateinit var helpButton: Button
    private lateinit var closeButton: Button
    private lateinit var btnManualAdjust: Button
    private lateinit var btnTargetSwitch: Button
    private lateinit var btnResetCursor: Button
    private lateinit var btnForceKeyboard: Button
    
    private var lastKnownDisplayId = -1
    
    // For 5x tap debug mode toggle
    private var resetCursorTapCount = 0
    private var lastResetCursorTapTime = 0L
    private val TAP_TIMEOUT = 1500L // 1.5 seconds to complete 5 taps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.text_status)
        toggleButton = findViewById(R.id.btn_toggle)
        hideAppButton = findViewById(R.id.btn_hide_app)
        lockButton = findViewById(R.id.btn_lock)
        profilesButton = findViewById(R.id.btn_profiles)
        settingsButton = findViewById(R.id.btn_settings)
        helpButton = findViewById(R.id.btn_help)
        closeButton = findViewById(R.id.btn_close)
        btnManualAdjust = findViewById(R.id.btn_manual_adjust)
        
        btnTargetSwitch = findViewById(R.id.btn_target_switch)
        btnTargetSwitch.setOnClickListener {
            val intent = Intent("CYCLE_INPUT_TARGET")
            intent.setPackage(packageName) 
            sendBroadcast(intent)
        }
        
        btnResetCursor = findViewById(R.id.btn_reset_cursor)
        btnResetCursor.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            
            // Check if we're within the tap timeout window
            if (currentTime - lastResetCursorTapTime > TAP_TIMEOUT) {
                resetCursorTapCount = 0
            }
            
            resetCursorTapCount++
            lastResetCursorTapTime = currentTime
            
            if (resetCursorTapCount >= 5) {
                // Toggle debug mode on 5th tap
                val intent = Intent("TOGGLE_DEBUG")
                intent.setPackage(packageName)
                sendBroadcast(intent)
                Toast.makeText(this, "Debug Mode Toggled", Toast.LENGTH_SHORT).show()
                resetCursorTapCount = 0
            } else {
                // Normal reset cursor behavior
                val intent = Intent("RESET_CURSOR")
                intent.setPackage(packageName)
                sendBroadcast(intent)
            }
        }
        
        btnForceKeyboard = findViewById(R.id.btn_force_keyboard)
        btnForceKeyboard.setOnClickListener {
            val intent = Intent("TOGGLE_CUSTOM_KEYBOARD")
            intent.setPackage(packageName)
            sendBroadcast(intent)
            Toast.makeText(this, "Toggling Keyboard...", Toast.LENGTH_SHORT).show()
        }

        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(0)
        }

        toggleButton.setOnClickListener {
            if (!isAccessibilityEnabled()) {
                Toast.makeText(this, "Please Enable Accessibility Service", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } else {
                forceMoveCommand()
                Toast.makeText(this, "Moving Trackpad to this screen...", Toast.LENGTH_SHORT).show()
            }
        }
        
        hideAppButton.setOnClickListener {
            // Move app to background without closing
            moveTaskToBack(true)
        }

        settingsButton.setOnClickListener { 
            // Show trackpad in preview mode when entering settings
            val intent = Intent("SET_PREVIEW_MODE")
            intent.setPackage(packageName)
            intent.putExtra("PREVIEW_MODE", true)
            sendBroadcast(intent)
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        profilesButton.setOnClickListener {
            startActivity(Intent(this, ProfilesActivity::class.java))
        }
        
        btnManualAdjust.setOnClickListener {
            // Show trackpad in preview mode when entering manual adjust
            val intent = Intent("SET_PREVIEW_MODE")
            intent.setPackage(packageName)
            intent.putExtra("PREVIEW_MODE", true)
            sendBroadcast(intent)
            startActivity(Intent(this, ManualAdjustActivity::class.java))
        }

        helpButton.setOnClickListener { showHelpDialog() }
        lockButton.setOnClickListener { toggleLock() }

        closeButton.setOnClickListener {
            finishAffinity()
            System.exit(0)
        }

        if (savedInstanceState == null && isAccessibilityEnabled()) {
            checkAndMoveDisplay()
        }

        updateStatusUI()
        updateLockUI()
    }

    override fun onResume() {
        super.onResume()
        updateStatusUI()
        updateLockUI()
        if (isAccessibilityEnabled()) {
            checkAndMoveDisplay()
        }
        // Hide trackpad when main menu is visible
        val intent = Intent("SET_TRACKPAD_VISIBILITY")
        intent.setPackage(packageName)
        intent.putExtra("VISIBLE", false)
        sendBroadcast(intent)
    }
    
    override fun onPause() {
        super.onPause()
        // Show trackpad when leaving main menu
        val intent = Intent("SET_TRACKPAD_VISIBILITY")
        intent.setPackage(packageName)
        intent.putExtra("VISIBLE", true)
        sendBroadcast(intent)
        
        // Also disable preview mode
        val previewIntent = Intent("SET_PREVIEW_MODE")
        previewIntent.setPackage(packageName)
        previewIntent.putExtra("PREVIEW_MODE", false)
        sendBroadcast(previewIntent)
    }

    private fun checkAndMoveDisplay() {
        val currentDisplayId = display?.displayId ?: android.view.Display.DEFAULT_DISPLAY
        if (currentDisplayId != lastKnownDisplayId) {
            lastKnownDisplayId = currentDisplayId
            forceMoveCommand()
        }
    }

    private fun forceMoveCommand() {
        val currentDisplayId = display?.displayId ?: android.view.Display.DEFAULT_DISPLAY
        val intent = Intent(this, OverlayService::class.java)
        intent.putExtra("DISPLAY_ID", currentDisplayId)
        startService(intent)
    }

    private fun updateStatusUI() {
        if (Shizuku.getBinder() == null) {
            statusText.text = "Status: Shizuku Not Running"
            statusText.setTextColor(0xFFFF0000.toInt())
        } else if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            statusText.text = "Status: Shizuku Permission Needed"
            statusText.setTextColor(0xFFFFFF00.toInt())
        } else if (isAccessibilityEnabled()) {
            statusText.text = "Status: Active"
            statusText.setTextColor(0xFF00FF00.toInt())
            toggleButton.text = "Move Trackpad Here"
        } else {
            statusText.text = "Status: Accessibility Needed"
            statusText.setTextColor(0xFFFFFF00.toInt())
            toggleButton.text = "Enable Trackpad"
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val componentName = ComponentName(this, OverlayService::class.java)
        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        return enabledServices?.contains(componentName.flattenToString()) == true
    }

    private fun toggleLock() {
        val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val current = prefs.getBoolean("lock_position", false)
        prefs.edit().putBoolean("lock_position", !current).apply()
        updateLockUI()
        val intent = Intent(this, OverlayService::class.java)
        intent.action = "RELOAD_PREFS"
        startService(intent)
    }

    private fun updateLockUI() {
        val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val isLocked = prefs.getBoolean("lock_position", false)
        
        if (isLocked) {
            lockButton.text = "Position: Locked"
            lockButton.backgroundTintList = ColorStateList.valueOf(0xFFFF0000.toInt())
            lockButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_closed, 0, 0, 0)
        } else {
            lockButton.text = "Position: Unlocked"
            lockButton.backgroundTintList = ColorStateList.valueOf(0xFF3DDC84.toInt())
            lockButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_open, 0, 0, 0)
        }
    }

    private fun showHelpDialog() {
        val text = TextView(this)
        text.setPadding(50, 40, 50, 40)
        text.textSize = 14f
        text.text = """
            == TRACKPAD CONTROLS ==
            - Tap: Left Click
            - Vol Up + Drag: Drag/Select
            - Vol Down: Right Click (Back)
            - Vol Down (Hold 1s): Move trackpad
            
            == CORNER HANDLES ==
            - Top-Left TAP: Toggle Keyboard
            - Top-Left HOLD: Move trackpad
            - Top-Right HOLD: Move window
            - Bottom-Right HOLD: Resize window
            - Bottom-Left TAP: Open Menu
            
            == CUSTOM KEYBOARD ==
            - Full QWERTY layout
            - SHIFT tap = Single uppercase
            - SHIFT hold = Caps Lock (green)
            - ?123 = Symbol layers
            - Arrow/Tab/Esc keys at bottom
            - Drag top bar to move
            - Drag corner to resize
            
            == VIRTUAL DISPLAY ==
            1. Create Virtual Display
            2. Click 'Switch Local/Remote'
            3. PINK Border = Remote control
            
            == SECRET ==
            - Tap Reset Cursor 5x = Debug Mode
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Instructions")
            .setView(text)
            .setPositiveButton("Got it", null)
            .show()
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/ManualAdjustActivity.kt
```kotlin
package com.example.coverscreentester

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ManualAdjustActivity : Activity() {

    private var isMoveMode = true
    private val STEP_SIZE = 10

    private lateinit var textMode: TextView
    private lateinit var btnToggle: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_adjust)

        textMode = findViewById(R.id.text_mode)
        btnToggle = findViewById(R.id.btn_toggle_mode)
        
        updateModeUI()

        btnToggle.setOnClickListener {
            isMoveMode = !isMoveMode
            updateModeUI()
        }

        findViewById<Button>(R.id.btn_up).setOnClickListener { sendAdjust(0, -STEP_SIZE) }
        findViewById<Button>(R.id.btn_down).setOnClickListener { sendAdjust(0, STEP_SIZE) }
        findViewById<Button>(R.id.btn_left).setOnClickListener { sendAdjust(-STEP_SIZE, 0) }
        findViewById<Button>(R.id.btn_right).setOnClickListener { sendAdjust(STEP_SIZE, 0) }
        
        // New Center Reset
        findViewById<Button>(R.id.btn_center).setOnClickListener {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "RESET_POSITION"
            startService(intent)
        }

        // New Rotate
        findViewById<Button>(R.id.btn_rotate).setOnClickListener {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "ROTATE"
            startService(intent)
        }
        
        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
    }

    private fun updateModeUI() {
        if (isMoveMode) {
            textMode.text = "CURRENT MODE: POSITION"
            textMode.setTextColor(Color.GREEN)
            btnToggle.text = "Switch to Resize"
        } else {
            textMode.text = "CURRENT MODE: SIZE"
            textMode.setTextColor(Color.CYAN)
            btnToggle.text = "Switch to Position"
        }
    }

    private fun sendAdjust(xChange: Int, yChange: Int) {
        val intent = Intent(this, OverlayService::class.java)
        intent.action = "MANUAL_ADJUST"
        
        if (isMoveMode) {
            intent.putExtra("DX", xChange)
            intent.putExtra("DY", yChange)
        } else {
            // In resize mode:
            // UP/DOWN affects Height
            // LEFT/RIGHT affects Width
            intent.putExtra("DW", xChange)
            intent.putExtra("DH", yChange)
        }
        
        startService(intent)
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/OverlayService.kt
```kotlin
package com.example.coverscreentester

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Display
import android.view.GestureDetector
import android.view.Gravity
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.abs
import kotlin.math.max
import java.util.ArrayList
import com.example.coverscreentester.BuildConfig

class OverlayService : AccessibilityService(), DisplayManager.DisplayListener {

    private var windowManager: WindowManager? = null
    private var displayManager: DisplayManager? = null
    private var trackpadLayout: FrameLayout? = null
    private lateinit var trackpadParams: WindowManager.LayoutParams
    private var cursorLayout: FrameLayout? = null
    private var cursorView: ImageView? = null
    private lateinit var cursorParams: WindowManager.LayoutParams
    
    // DEBUG UI
    private var debugTextView: TextView? = null
    
    private var remoteWindowManager: WindowManager? = null
    private var remoteCursorLayout: FrameLayout? = null
    private var remoteCursorView: ImageView? = null
    private lateinit var remoteCursorParams: WindowManager.LayoutParams
    
    private var shellService: IShellService? = null
    private var isBound = false
    
    private var keyboardOverlay: KeyboardOverlay? = null
    private var isCustomKeyboardVisible = false
    
    private var isTrackpadVisible = true
    private var isPreviewMode = false
    
    private var currentOverlayDisplayId = 0
    private var currentDisplayId = -1 
    private var inputTargetDisplayId = -1
    private var lastLoadedProfileKey = ""

    private var uiScreenWidth = 1080
    private var uiScreenHeight = 2640
    private var targetScreenWidth = 1920
    private var targetScreenHeight = 1080

    private var cursorX = 300f
    private var cursorY = 300f
    private var virtualScrollX = 0f
    private var virtualScrollY = 0f
    private var rotationAngle = 0 
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    
    private var initialWindowX = 0
    private var initialWindowY = 0
    private var initialWindowWidth = 0
    private var initialWindowHeight = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    
    private var isTouchDragging = false
    private var isLeftKeyHeld = false
    private var isRightKeyHeld = false
    private var isRightDragPending = false 
    private var isVScrolling = false
    private var isHScrolling = false
    private var dragDownTime: Long = 0L
    
    private var isClicking = false

    private var hasSentTouchDown = false
    private var hasSentMouseDown = false
    private var hasSentScrollDown = false
    
    private var cursorSpeed = 2.5f
    private var scrollSpeed = 3.0f 
    private var scrollZoneThickness = 60 
    private var prefVibrate = true
    private var prefReverseScroll = true
    private var prefAlpha = 200
    private var prefHandleSize = 60 
    private var prefVPosLeft = false
    private var prefHPosTop = false
    private var prefLocked = false
    private var prefHandleTouchSize = 60
    private var prefScrollTouchSize = 60
    private var prefScrollVisualSize = 4
    private var prefCursorSize = 50 
    
    private var isDebugMode = false
    private var isKeyboardMode = false 
    private var savedWindowX = 0
    private var savedWindowY = 0
    
    private var currentBorderColor = 0xFFFFFFFF.toInt()
    private var highlightAlpha = false
    private var highlightHandles = false
    private var highlightScrolls = false
    
    private val handleContainers = ArrayList<FrameLayout>()
    private val handleVisuals = ArrayList<View>()
    private var vScrollContainer: FrameLayout? = null
    private var hScrollContainer: FrameLayout? = null
    private var vScrollVisual: View? = null
    private var hScrollVisual: View? = null
    
    private val handler = Handler(Looper.getMainLooper())
    private val longPressRunnable = Runnable { startTouchDrag() }
    private var isResizing = false
    private val resizeLongPressRunnable = Runnable { startResize() }
    private var isMoving = false
    private val moveLongPressRunnable = Runnable { startMove() }
    private val voiceRunnable = Runnable { toggleKeyboardMode() }
    
    private var keyboardHandleDownTime = 0L
    private val keyboardLongPressRunnable = Runnable { toggleKeyboardMode() }
    
    private val clearHighlightsRunnable = Runnable {
        highlightAlpha = false; highlightHandles = false; highlightScrolls = false
        updateBorderColor(currentBorderColor); updateLayoutSizes() 
    }
    
    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                "RESET_CURSOR" -> resetCursorCenter()
                "TOGGLE_DEBUG" -> toggleDebugMode()
                "FORCE_KEYBOARD" -> toggleCustomKeyboard()
                "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                "SET_TRACKPAD_VISIBILITY" -> {
                    val visible = intent.getBooleanExtra("VISIBLE", true)
                    setTrackpadVisibility(visible)
                }
                "SET_PREVIEW_MODE" -> {
                    val preview = intent.getBooleanExtra("PREVIEW_MODE", false)
                    setPreviewMode(preview)
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (isPreviewMode || !isTrackpadVisible) return super.onKeyEvent(event)
        
        val action = event.action; val keyCode = event.keyCode
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (action == KeyEvent.ACTION_DOWN) { if (!isLeftKeyHeld) { isLeftKeyHeld = true; startKeyDrag(MotionEvent.BUTTON_PRIMARY) } } 
            else if (action == KeyEvent.ACTION_UP) { isLeftKeyHeld = false; stopKeyDrag(MotionEvent.BUTTON_PRIMARY) }
            return true 
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (action == KeyEvent.ACTION_DOWN) { if (!isRightDragPending) { isRightDragPending = true; handler.postDelayed(voiceRunnable, 1000) } } 
            else if (action == KeyEvent.ACTION_UP) { handler.removeCallbacks(voiceRunnable); if (isRightDragPending) { performClick(true); isRightDragPending = false } }
            return true 
        }
        return super.onKeyEvent(event)
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) { 
            shellService = IShellService.Stub.asInterface(binder); isBound = true 
            Thread { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") }.start()
            initCustomKeyboard()
        }
        override fun onServiceDisconnected(name: ComponentName?) { shellService = null; isBound = false }
    }

    override fun onCreate() {
        super.onCreate()
        try { displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; displayManager?.registerDisplayListener(this, handler) } catch (e: Exception) { Log.e("OverlayService", "Failed to init DisplayManager", e) }
        val filter = IntentFilter().apply {
            addAction("CYCLE_INPUT_TARGET"); addAction("RESET_CURSOR"); addAction("TOGGLE_DEBUG")
            addAction("FORCE_KEYBOARD"); addAction("TOGGLE_CUSTOM_KEYBOARD")
            addAction("SET_TRACKPAD_VISIBILITY"); addAction("SET_PREVIEW_MODE")
        }
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(switchReceiver, filter, Context.RECEIVER_EXPORTED) else registerReceiver(switchReceiver, filter)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        try { createNotification(); bindShizuku(); loadPrefs()
            if (displayManager == null) { displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; displayManager?.registerDisplayListener(this, handler) }
            setupWindows(Display.DEFAULT_DISPLAY) 
        } catch (e: Exception) { Log.e("OverlayService", "Crash in onServiceConnected", e) }
    }

    override fun onDisplayAdded(displayId: Int) {}
    override fun onDisplayRemoved(displayId: Int) {}
    override fun onDisplayChanged(displayId: Int) { 
        if (displayId == currentDisplayId) { updateUiMetrics(displayId); val newKey = getProfileKey(); if (newKey != lastLoadedProfileKey) loadLayout() }
    }

    private fun updateUiMetrics(displayId: Int) {
        try {
            if (displayManager == null) displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val display = displayManager?.getDisplay(displayId) ?: return
            val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics)
            if (metrics.widthPixels > 0) { 
                uiScreenWidth = metrics.widthPixels; uiScreenHeight = metrics.heightPixels
                if (inputTargetDisplayId == -1 || inputTargetDisplayId == displayId) { targetScreenWidth = uiScreenWidth; targetScreenHeight = uiScreenHeight }
                keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentOverlayDisplayId)
            }
        } catch (e: Exception) {}
    }

    private fun updateTargetMetrics(displayId: Int) {
        try {
            if (displayManager == null) displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val display = displayManager?.getDisplay(displayId)
            if (display == null) { targetScreenWidth = 1920; targetScreenHeight = 1080; return }
            val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics)
            if (metrics.widthPixels > 0) { targetScreenWidth = metrics.widthPixels; targetScreenHeight = metrics.heightPixels }
            else { targetScreenWidth = 1920; targetScreenHeight = 1080 }
        } catch (e: Exception) { targetScreenWidth = 1920; targetScreenHeight = 1080 }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            when (intent?.action) {
                "RESET_POSITION" -> resetTrackpadPosition()
                "ROTATE" -> performRotation()
                "SAVE_LAYOUT" -> saveLayout()
                "LOAD_LAYOUT" -> loadLayout()
                "DELETE_PROFILE" -> deleteCurrentProfile()
                "MANUAL_ADJUST" -> handleManualAdjust(intent)
                "RELOAD_PREFS" -> { loadPrefs(); updateBorderColor(currentBorderColor); updateLayoutSizes(); updateScrollPosition(); updateCursorSize() }
                "PREVIEW_UPDATE" -> handlePreview(intent)
                "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                "RESET_CURSOR" -> resetCursorCenter()
                "TOGGLE_DEBUG" -> toggleDebugMode()
                "FORCE_KEYBOARD" -> toggleCustomKeyboard()
                "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
            }
            if (intent?.hasExtra("DISPLAY_ID") == true) {
                val targetDisplayId = intent.getIntExtra("DISPLAY_ID", Display.DEFAULT_DISPLAY)
                if (targetDisplayId != currentDisplayId || trackpadLayout == null) { removeOverlays(); setupWindows(targetDisplayId) }
            }
        } catch (e: Exception) { Log.e("OverlayService", "Crash in onStartCommand", e) }
        return START_STICKY
    }
    
    private fun setTrackpadVisibility(visible: Boolean) {
        if (!visible && isTrackpadVisible) {
            resetAllTouchStates()
        }
        
        isTrackpadVisible = visible
        trackpadLayout?.visibility = if (visible) View.VISIBLE else View.GONE
        cursorLayout?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setPreviewMode(preview: Boolean) {
        if (preview && !isPreviewMode) {
            resetAllTouchStates()
        }
        
        isPreviewMode = preview
        
        if (preview) {
            trackpadLayout?.visibility = View.VISIBLE
            cursorLayout?.visibility = View.VISIBLE
            trackpadParams.flags = trackpadParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            try {
                windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
            } catch (e: Exception) {}
            trackpadLayout?.alpha = 0.7f
        } else {
            trackpadParams.flags = trackpadParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
            try {
                windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
            } catch (e: Exception) {}
            trackpadLayout?.alpha = 1.0f
        }
    }
    
    private fun resetAllTouchStates() {
        handler.removeCallbacks(longPressRunnable)
        handler.removeCallbacks(resizeLongPressRunnable)
        handler.removeCallbacks(moveLongPressRunnable)
        handler.removeCallbacks(voiceRunnable)
        
        if (isTouchDragging && hasSentTouchDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY, dragDownTime)
        }
        isTouchDragging = false
        hasSentTouchDown = false
        
        if (isLeftKeyHeld && hasSentMouseDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY, dragDownTime)
        }
        isLeftKeyHeld = false
        
        if (isRightKeyHeld && hasSentMouseDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_SECONDARY, dragDownTime)
        }
        isRightKeyHeld = false
        hasSentMouseDown = false
        
        if ((isVScrolling || isHScrolling) && hasSentScrollDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
        }
        isVScrolling = false
        isHScrolling = false
        hasSentScrollDown = false
        
        isRightDragPending = false
        isMoving = false
        isResizing = false
        isClicking = false
        
        if (!isDebugMode) {
            if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
            else updateBorderColor(0x55FFFFFF.toInt())
        }
    }
    
    private fun initCustomKeyboard() {
        if (windowManager == null || shellService == null) return
        keyboardOverlay = KeyboardOverlay(this, windowManager!!, shellService, inputTargetDisplayId)
        keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentOverlayDisplayId)
    }
    
    private fun toggleCustomKeyboard() {
        if (keyboardOverlay == null && shellService != null) initCustomKeyboard()
        
        if (keyboardOverlay != null) {
            val wasVisible = keyboardOverlay?.isShowing() == true
            if (wasVisible) keyboardOverlay?.hide()
            keyboardOverlay = KeyboardOverlay(this, windowManager!!, shellService, inputTargetDisplayId)
            keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentOverlayDisplayId)
            if (!wasVisible) keyboardOverlay?.show()
        } else {
            keyboardOverlay?.toggle()
        }
        
        isCustomKeyboardVisible = keyboardOverlay?.isShowing() ?: false
        if (isCustomKeyboardVisible) { updateBorderColor(0xFF9C27B0.toInt()) }
        else { if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()) }
        vibrate()
    }

    private fun toggleDebugMode() { 
        isDebugMode = !isDebugMode
        if (isDebugMode) { 
            showToast("Debug ON"); 
            updateBorderColor(0xFFFFFF00.toInt()) 
            debugTextView?.visibility = View.VISIBLE
        } else { 
            showToast("Debug OFF"); 
            if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
            else updateBorderColor(0x55FFFFFF.toInt()) 
            debugTextView?.visibility = View.GONE
        } 
    }
    
    private fun resetCursorCenter() { cursorX = (targetScreenWidth / 2).toFloat(); cursorY = (targetScreenHeight / 2).toFloat(); injectAction(MotionEvent.ACTION_HOVER_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis()); if (inputTargetDisplayId == currentDisplayId) { cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {} } else { remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {} }; showToast("Reset to ${cursorX.toInt()}x${cursorY.toInt()}"); vibrate() }
    private fun handleManualAdjust(intent: Intent) { if (windowManager == null || trackpadLayout == null) return; val dx = intent.getIntExtra("DX", 0); val dy = intent.getIntExtra("DY", 0); val dw = intent.getIntExtra("DW", 0); val dh = intent.getIntExtra("DH", 0); trackpadParams.x += dx; trackpadParams.y += dy; trackpadParams.width = max(200, trackpadParams.width + dw); trackpadParams.height = max(200, trackpadParams.height + dh); try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams); saveLayout() } catch (e: Exception) {} }
    
    private fun removeOverlays() { 
        try { 
            keyboardOverlay?.hide()
            if (trackpadLayout != null) { windowManager?.removeView(trackpadLayout); trackpadLayout = null }
            if (cursorLayout != null) { windowManager?.removeView(cursorLayout); cursorLayout = null }
            removeRemoteCursor()
        } catch (e: Exception) {} 
    }

    private fun setupWindows(displayId: Int) {
        if (trackpadLayout != null && displayId == currentDisplayId) return
        try {
            updateUiMetrics(displayId); if (uiScreenWidth == 0) updateUiMetrics(displayId)
            val displayContext = createDisplayContext(displayManager!!.getDisplay(displayId))
            windowManager = displayContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            currentDisplayId = displayId; inputTargetDisplayId = displayId
            currentOverlayDisplayId = displayId
            targetScreenWidth = uiScreenWidth; targetScreenHeight = uiScreenHeight
            
            cursorLayout = FrameLayout(displayContext)
            cursorView = ImageView(displayContext); cursorView?.setImageResource(R.drawable.ic_cursor)
            val size = if (prefCursorSize > 0) prefCursorSize else 50
            cursorLayout?.addView(cursorView, FrameLayout.LayoutParams(size, size))
            cursorParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { cursorParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES }
            cursorParams.gravity = Gravity.TOP or Gravity.LEFT; cursorX = uiScreenWidth / 2f; cursorY = uiScreenHeight / 2f; cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt()
            windowManager?.addView(cursorLayout, cursorParams)

            trackpadLayout = object : FrameLayout(displayContext) {}; val bg = GradientDrawable(); bg.cornerRadius = 30f; trackpadLayout?.background = bg; updateBorderColor(0x55FFFFFF.toInt()); trackpadLayout?.isFocusable = true; trackpadLayout?.isFocusableInTouchMode = true
            handleContainers.clear(); handleVisuals.clear(); val handleColor = 0x15FFFFFF.toInt()
            addHandle(displayContext, Gravity.TOP or Gravity.RIGHT, handleColor) { _, e -> moveWindow(e) }
            addHandle(displayContext, Gravity.BOTTOM or Gravity.RIGHT, handleColor) { _, e -> resizeWindow(e) }
            addHandle(displayContext, Gravity.BOTTOM or Gravity.LEFT, handleColor) { _, e -> openMenuHandle(e) }
            addHandle(displayContext, Gravity.TOP or Gravity.LEFT, handleColor) { _, e -> keyboardHandle(e) }
            addScrollBars(displayContext)
            
            debugTextView = TextView(displayContext)
            debugTextView?.text = "DEBUG MODE"
            debugTextView?.setTextColor(Color.YELLOW)
            debugTextView?.setBackgroundColor(0xAA000000.toInt())
            debugTextView?.textSize = 10f
            debugTextView?.visibility = View.GONE
            val debugParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            debugParams.gravity = Gravity.CENTER
            trackpadLayout?.addView(debugTextView, debugParams)

            trackpadParams = WindowManager.LayoutParams(400, 300, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT)
            trackpadParams.gravity = Gravity.TOP or Gravity.LEFT; trackpadParams.title = "TrackpadInput"
            
            loadOverlayPositionForDisplay(displayId)
            
            val gestureDetector = GestureDetector(displayContext, object : GestureDetector.SimpleOnGestureListener() { override fun onSingleTapConfirmed(e: MotionEvent): Boolean { if (!isTouchDragging && !isLeftKeyHeld && !isRightKeyHeld && !isVScrolling && !isHScrolling && !isPreviewMode && isTrackpadVisible && !isClicking) performClick(false); return true } })
            
            trackpadLayout?.setOnTouchListener { _, event -> 
                if (isDebugMode) {
                    val src = event.source
                    val tool = event.getToolType(0)
                    val act = event.actionMasked
                    val devId = event.deviceId
                    val rawX = event.rawX.toInt()
                    val rawY = event.rawY.toInt()
                    var toolName = "UNK"
                    if (tool == MotionEvent.TOOL_TYPE_FINGER) toolName = "FINGER"
                    else if (tool == MotionEvent.TOOL_TYPE_MOUSE) toolName = "MOUSE"
                    val txt = "ACT:$act SRC:$src TOOL:$toolName ID:$devId\nRAW:$rawX,$rawY\nCUR:${cursorX.toInt()},${cursorY.toInt()}"
                    debugTextView?.text = txt
                }
            
                // CRITICAL STUCK FIX:
                // 1. Check tool type (Ignore mouse/stylus feedback)
                // 2. Check DeviceID (Ignore 0 or Virtual devices)
                val isFinger = event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER
                val isRealHardware = event.deviceId != 0 
                val isHover = event.actionMasked == MotionEvent.ACTION_HOVER_MOVE || event.actionMasked == MotionEvent.ACTION_HOVER_ENTER || event.actionMasked == MotionEvent.ACTION_HOVER_EXIT
                
                if (!isFinger || !isRealHardware || isHover) {
                    return@setOnTouchListener false 
                }

                if (isClicking) return@setOnTouchListener true
                
                if (!isPreviewMode && isTrackpadVisible) {
                    gestureDetector.onTouchEvent(event)
                    handleTrackpadTouch(event)
                }
                true 
            }
            windowManager?.addView(trackpadLayout, trackpadParams); loadLayout()
            
            if (shellService != null) initCustomKeyboard()
        } catch (e: Exception) { Log.e("OverlayService", "Setup Windows Crash", e) }
    }
    
    private fun loadOverlayPositionForDisplay(displayId: Int) {
        currentOverlayDisplayId = displayId
        val prefs = getSharedPreferences("TrackpadPrefs", MODE_PRIVATE)
        
        val defaultX = (uiScreenWidth - 400) / 2
        val defaultY = (uiScreenHeight - 300) / 2
        
        if (prefs.contains("overlay_x_d$displayId")) {
            trackpadParams.x = prefs.getInt("overlay_x_d$displayId", defaultX)
            trackpadParams.y = prefs.getInt("overlay_y_d$displayId", defaultY)
            trackpadParams.width = prefs.getInt("overlay_width_d$displayId", 400)
            trackpadParams.height = prefs.getInt("overlay_height_d$displayId", 300)
        } else {
            trackpadParams.x = defaultX
            trackpadParams.y = defaultY
            trackpadParams.width = 400
            trackpadParams.height = 300
        }
    }
    
    private fun saveOverlayPosition() {
        val prefs = getSharedPreferences("TrackpadPrefs", MODE_PRIVATE)
        prefs.edit()
            .putInt("overlay_x_d$currentOverlayDisplayId", trackpadParams.x)
            .putInt("overlay_y_d$currentOverlayDisplayId", trackpadParams.y)
            .apply()
    }
    
    private fun saveOverlaySize() {
        val prefs = getSharedPreferences("TrackpadPrefs", MODE_PRIVATE)
        prefs.edit()
            .putInt("overlay_width_d$currentOverlayDisplayId", trackpadParams.width)
            .putInt("overlay_height_d$currentOverlayDisplayId", trackpadParams.height)
            .apply()
    }
    
    private fun createRemoteCursor(displayId: Int) { try { removeRemoteCursor(); val display = displayManager?.getDisplay(displayId) ?: return; val remoteContext = createDisplayContext(display); remoteWindowManager = remoteContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager; remoteCursorLayout = FrameLayout(remoteContext); remoteCursorView = ImageView(remoteContext); remoteCursorView?.setImageResource(R.drawable.ic_cursor); val size = if (prefCursorSize > 0) prefCursorSize else 50; remoteCursorLayout?.addView(remoteCursorView, FrameLayout.LayoutParams(size, size)); remoteCursorParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT); remoteCursorParams.gravity = Gravity.TOP or Gravity.LEFT; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); remoteCursorParams.x = metrics.widthPixels / 2; remoteCursorParams.y = metrics.heightPixels / 2; remoteWindowManager?.addView(remoteCursorLayout, remoteCursorParams) } catch (e: Exception) { Log.e("OverlayService", "Failed to create remote cursor", e); showToast("Failed to draw on Display $displayId") } }
    private fun removeRemoteCursor() { try { if (remoteCursorLayout != null && remoteWindowManager != null) { remoteWindowManager?.removeView(remoteCursorLayout) } } catch(e: Exception) {} ; remoteCursorLayout = null; remoteCursorView = null; remoteWindowManager = null }
    private fun updateCursorSize() { val size = if (prefCursorSize > 0) prefCursorSize else 50; if (cursorView != null) { val lp = cursorView!!.layoutParams; lp.width = size; lp.height = size; cursorView!!.layoutParams = lp }; if (remoteCursorView != null) { val lp = remoteCursorView!!.layoutParams; lp.width = size; lp.height = size; remoteCursorView!!.layoutParams = lp } }
    private fun getProfileKey(): String { if (uiScreenHeight == 0) return "profile_1.0"; val ratio = uiScreenWidth.toFloat() / uiScreenHeight.toFloat(); return "profile_" + String.format("%.1f", ratio) }
    private fun saveLayout() { if (trackpadLayout == null || uiScreenWidth == 0 || uiScreenHeight == 0) return; val key = getProfileKey(); lastLoadedProfileKey = key; val xPct = trackpadParams.x.toFloat() / uiScreenWidth.toFloat(); val yPct = trackpadParams.y.toFloat() / uiScreenHeight.toFloat(); val wPct = trackpadParams.width.toFloat() / uiScreenWidth.toFloat(); val hPct = trackpadParams.height.toFloat() / uiScreenHeight.toFloat(); val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); p.edit().putFloat("${key}_xp", xPct).putFloat("${key}_yp", yPct).putFloat("${key}_wp", wPct).putFloat("${key}_hp", hPct).putBoolean("${key}_saved", true).apply(); saveOverlayPosition(); saveOverlaySize(); vibrate() }
    private fun loadLayout() { if (trackpadLayout == null || windowManager == null) return; val key = getProfileKey(); lastLoadedProfileKey = key; val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); if (p.getBoolean("${key}_saved", false)) { val xPct = p.getFloat("${key}_xp", 0.1f); val yPct = p.getFloat("${key}_yp", 0.1f); val wPct = p.getFloat("${key}_wp", 0.5f); val hPct = p.getFloat("${key}_hp", 0.4f); val calcW = (wPct * uiScreenWidth).toInt(); val calcH = (hPct * uiScreenHeight).toInt(); trackpadParams.width = calcW.coerceAtLeast(300); trackpadParams.height = calcH.coerceAtLeast(300); trackpadParams.x = (xPct * uiScreenWidth).toInt(); trackpadParams.y = (yPct * uiScreenHeight).toInt() } else { trackpadParams.width = 400; trackpadParams.height = 300; trackpadParams.x = (uiScreenWidth / 2) - 200; trackpadParams.y = (uiScreenHeight / 2) - 150 }; try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams); } catch (e: Exception) {} }
    private fun deleteCurrentProfile() { val key = getProfileKey(); getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().remove("${key}_saved").remove("${key}_xp").remove("${key}_yp").remove("${key}_wp").remove("${key}_hp").apply(); resetTrackpadPosition() }
    private fun resetTrackpadPosition() { if (windowManager == null || trackpadLayout == null) return; trackpadParams.width = 400; trackpadParams.height = 300; val centerX = (uiScreenWidth / 2) - 200; val centerY = (uiScreenHeight / 2) - 150; trackpadParams.x = if (centerX > 0) centerX else 100; trackpadParams.y = if (centerY > 0) centerY else 100; try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams); vibrate() } catch (e: Exception) {} }
    private fun moveWindow(event: MotionEvent): Boolean { if (prefLocked) return true; when (event.action) { MotionEvent.ACTION_DOWN -> { handler.postDelayed(moveLongPressRunnable, 1000); initialWindowX = trackpadParams.x; initialWindowY = trackpadParams.y; initialTouchX = event.rawX; initialTouchY = event.rawY; return true }; MotionEvent.ACTION_MOVE -> { if (isMoving) { trackpadParams.x = initialWindowX + (event.rawX - initialTouchX).toInt(); trackpadParams.y = initialWindowY + (event.rawY - initialTouchY).toInt(); windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } else if (abs(event.rawX - initialTouchX) > 20) handler.removeCallbacks(moveLongPressRunnable); return true }; MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { handler.removeCallbacks(moveLongPressRunnable); if (isMoving) stopMove(); return true } }; return false }
    private fun startMove() { isMoving = true; vibrate(); updateBorderColor(0xFF0000FF.toInt()) }
    private fun stopMove() { isMoving = false; saveOverlayPosition(); if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()) }
    private fun resizeWindow(event: MotionEvent): Boolean { if (prefLocked) return true; when (event.action) { MotionEvent.ACTION_DOWN -> { handler.postDelayed(resizeLongPressRunnable, 1000); initialWindowWidth = trackpadParams.width; initialWindowHeight = trackpadParams.height; initialTouchX = event.rawX; initialTouchY = event.rawY; return true }; MotionEvent.ACTION_MOVE -> { if (isResizing) { val deltaX = event.rawX - initialTouchX; val deltaY = event.rawY - initialTouchY; val newWidth = (initialWindowWidth + deltaX).toInt(); val newHeight = (initialWindowHeight + deltaY).toInt(); trackpadParams.width = max(300, newWidth); trackpadParams.height = max(300, newHeight); windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } else if (abs(event.rawX - initialTouchX) > 20) handler.removeCallbacks(resizeLongPressRunnable); return true }; MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { handler.removeCallbacks(resizeLongPressRunnable); if (isResizing) stopResize(); return true } }; return false }
    private fun startResize() { isResizing = true; vibrate(); updateBorderColor(0xFF0000FF.toInt()) }
    private fun stopResize() { isResizing = false; saveOverlaySize(); if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()) }
    
    private fun handleTrackpadTouch(event: MotionEvent) {
        val viewWidth = trackpadLayout?.width ?: 0
        val viewHeight = trackpadLayout?.height ?: 0
        
        if (viewWidth == 0 || viewHeight == 0) return

        when (event.actionMasked) { 
            MotionEvent.ACTION_DOWN -> { 
                isVScrolling = false
                isHScrolling = false
                isTouchDragging = false
                handler.removeCallbacks(longPressRunnable)
                
                lastTouchX = event.x
                lastTouchY = event.y
                
                val inVZone = if (prefVPosLeft) event.x < scrollZoneThickness else event.x > (viewWidth - scrollZoneThickness)
                val inHZone = if (prefHPosTop) event.y < scrollZoneThickness else event.y > (viewHeight - scrollZoneThickness)
                
                if (inVZone) { 
                    isVScrolling = true; vibrate(); updateBorderColor(0xFF00FFFF.toInt())
                    virtualScrollX = cursorX; virtualScrollY = cursorY
                    dragDownTime = SystemClock.uptimeMillis()
                    injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
                    hasSentScrollDown = true
                } 
                else if (inHZone) { 
                    isHScrolling = true; vibrate(); updateBorderColor(0xFF00FFFF.toInt())
                    virtualScrollX = cursorX; virtualScrollY = cursorY
                    dragDownTime = SystemClock.uptimeMillis()
                    injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
                    hasSentScrollDown = true
                } 
                else {
                    handler.postDelayed(longPressRunnable, 400) 
                }
            }
            MotionEvent.ACTION_MOVE -> { 
                val rawDx = (event.x - lastTouchX) * cursorSpeed
                val rawDy = (event.y - lastTouchY) * cursorSpeed
                
                if (isVScrolling) { 
                    val dist = (event.y - lastTouchY) * scrollSpeed
                    if (abs(dist) > 0) { 
                        if (prefReverseScroll) virtualScrollY += dist else virtualScrollY -= dist
                        injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY) 
                    } 
                } 
                else if (isHScrolling) { 
                    val dist = (event.x - lastTouchX) * scrollSpeed
                    if (abs(dist) > 0) { 
                        if (prefReverseScroll) virtualScrollX += dist else virtualScrollX -= dist
                        injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY) 
                    } 
                } 
                else { 
                    var finalDx = rawDx
                    var finalDy = rawDy
                    when (rotationAngle) { 
                        90 -> { finalDx = -rawDy; finalDy = rawDx }
                        180 -> { finalDx = -rawDx; finalDy = -rawDy } 
                        270 -> { finalDx = rawDy; finalDy = -rawDx } 
                    }
                    
                    if (!isTouchDragging && (abs(rawDx) > 5 || abs(rawDy) > 5)) { 
                        handler.removeCallbacks(longPressRunnable)
                        if (isRightDragPending) { 
                            isRightDragPending = false; handler.removeCallbacks(voiceRunnable)
                            isRightKeyHeld = true; startKeyDrag(MotionEvent.BUTTON_SECONDARY) 
                        } 
                    }
                    
                    val safeW = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth.toFloat() else uiScreenWidth.toFloat()
                    val safeH = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight.toFloat() else uiScreenHeight.toFloat()
                    
                    cursorX = (cursorX + finalDx).coerceIn(0f, safeW)
                    cursorY = (cursorY + finalDy).coerceIn(0f, safeH)
                    
                    if (inputTargetDisplayId == currentDisplayId) { 
                        cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt()
                        try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {} 
                    } 
                    else { 
                        remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt()
                        try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {} 
                    }
                    
                    // SELF-INTERSECTION CHECK (Modified)
                    var skipInjection = false
                    if (inputTargetDisplayId == currentDisplayId && trackpadLayout != null) {
                        val tX = trackpadParams.x.toFloat()
                        val tY = trackpadParams.y.toFloat()
                        val tW = trackpadParams.width.toFloat()
                        val tH = trackpadParams.height.toFloat()

                        // BUFFER ZONE: Add 100px margin to the detection box
                        // This prevents the injection from getting close enough to the window edge
                        // to cause the "fighting" interaction on the main screen.
                        val buffer = 100f 
                        
                        if (cursorX >= (tX - buffer) && cursorX <= (tX + tW + buffer) && 
                            cursorY >= (tY - buffer) && cursorY <= (tY + tH + buffer)) {
                            skipInjection = true
                        }
                    }

                    if (!skipInjection) {
                        if (isTouchDragging && hasSentTouchDown) {
                            injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY, dragDownTime)
                        } else if (isLeftKeyHeld && hasSentMouseDown) {
                            injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY, dragDownTime)
                        } else if (isRightKeyHeld && hasSentMouseDown) {
                            injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_SECONDARY, dragDownTime)
                        } else {
                            injectAction(MotionEvent.ACTION_HOVER_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
                        }
                    }
                }
                lastTouchX = event.x
                lastTouchY = event.y 
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { 
                handler.removeCallbacks(longPressRunnable)
                if (isTouchDragging) stopTouchDrag()
                
                if ((isVScrolling || isHScrolling) && hasSentScrollDown) { 
                    injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
                }
                
                isVScrolling = false
                isHScrolling = false
                hasSentScrollDown = false
                
                if (isDebugMode) { 
                    showToast("Disp:$inputTargetDisplayId | X:${cursorX.toInt()} Y:${cursorY.toInt()}")
                    updateBorderColor(0xFFFFFF00.toInt()) 
                } 
                else { 
                    if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
                    else updateBorderColor(0x55FFFFFF.toInt()) 
                } 
            } 
        } 
    }
    private fun performRotation() { rotationAngle = (rotationAngle + 90) % 360; cursorView?.rotation = rotationAngle.toFloat(); vibrate(); updateBorderColor(0xFFFFFF00.toInt()); }
    private fun startKeyDrag(b: Int) { vibrate(); updateBorderColor(0xFF00FF00.toInt()); dragDownTime = SystemClock.uptimeMillis(); injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_MOUSE, b, dragDownTime); hasSentMouseDown = true }
    private fun stopKeyDrag(b: Int) { 
        if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt())
        if (hasSentMouseDown) { injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_MOUSE, b, dragDownTime) }
        hasSentMouseDown = false
    }
    private fun startTouchDrag() { isTouchDragging = true; vibrate(); updateBorderColor(0xFF00FF00.toInt()); dragDownTime = SystemClock.uptimeMillis(); injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY, dragDownTime); hasSentTouchDown = true }
    private fun stopTouchDrag() { 
        isTouchDragging = false
        if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt())
        if (hasSentTouchDown) { injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY, dragDownTime) }
        hasSentTouchDown = false
    }
    
    private fun injectAction(a: Int, s: Int, b: Int, t: Long, x: Float = cursorX, y: Float = cursorY) { 
        if (shellService == null) return
        val dId = if (inputTargetDisplayId != -1) inputTargetDisplayId else (cursorLayout?.display?.displayId ?: Display.DEFAULT_DISPLAY)
        Thread { try { shellService?.injectMouse(a, x, y, dId, s, b, t) } catch (e: Exception) {} }.start() 
    }
    
    private fun injectScroll(v: Float, h: Float) { 
        if (shellService == null) return
        val dId = if (inputTargetDisplayId != -1) inputTargetDisplayId else (cursorLayout?.display?.displayId ?: Display.DEFAULT_DISPLAY)
        Thread { try { shellService?.injectScroll(cursorX, cursorY, v, h, dId) } catch (e: Exception) {} }.start() 
    }
    
    private fun performClick(r: Boolean) { 
        if (shellService == null) { bindShizuku(); return }
        Thread { try { if (r) shellService?.execRightClick(cursorX, cursorY, inputTargetDisplayId) else shellService?.execClick(cursorX, cursorY, inputTargetDisplayId) } catch (e: Exception) {} }.start()
    }
    
    private fun cycleInputTarget() { 
        if (displayManager == null) return
        try { 
            val displays = displayManager!!.displays; var nextId = -1
            for (d in displays) { if (d.displayId != currentDisplayId) { if (inputTargetDisplayId == currentDisplayId) { nextId = d.displayId; break } else if (inputTargetDisplayId == d.displayId) { continue } else { nextId = d.displayId } } }
            if (nextId == -1) { 
                inputTargetDisplayId = currentDisplayId; targetScreenWidth = uiScreenWidth; targetScreenHeight = uiScreenHeight
                removeRemoteCursor(); cursorX = (uiScreenWidth / 2).toFloat(); cursorY = (uiScreenHeight / 2).toFloat()
                cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); windowManager?.updateViewLayout(cursorLayout, cursorParams)
                currentBorderColor = 0x55FFFFFF.toInt(); updateBorderColor(currentBorderColor); cursorView?.visibility = View.VISIBLE
                showToast("Target: Local (Display )"); vibrate()
            } else { 
                inputTargetDisplayId = nextId; updateTargetMetrics(nextId); createRemoteCursor(nextId)
                cursorX = (targetScreenWidth / 2).toFloat(); cursorY = (targetScreenHeight / 2).toFloat()
                currentBorderColor = 0xFFFF00FF.toInt(); updateBorderColor(currentBorderColor); cursorView?.visibility = View.GONE
                showToast("Target: Display  (x)"); vibrate(); vibrate()
            }
            if (isCustomKeyboardVisible) { keyboardOverlay?.hide(); keyboardOverlay = KeyboardOverlay(this, windowManager!!, shellService, inputTargetDisplayId); keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentOverlayDisplayId); keyboardOverlay?.show() }
        } catch (e: Exception) { Log.e("OverlayService", "Cycle Error", e) } 
    }
    
    private fun showToast(msg: String) { handler.post { android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show() } }
    private fun vibrate() { if (!prefVibrate) return; val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator; if (Build.VERSION.SDK_INT >= 26) v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)) else v.vibrate(50) }
    private fun bindShizuku() { try { val c = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(c, userServiceConnection, BuildConfig.DEBUG, BuildConfig.VERSION_CODE) } catch (e: Exception) {} }
    private fun createNotification() { val c = NotificationChannel("overlay_service", "Trackpad Active", NotificationManager.IMPORTANCE_LOW); (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(c); val n = Notification.Builder(this, "overlay_service").setContentTitle("Trackpad Active").setSmallIcon(R.drawable.ic_launcher_bubble).build(); if (Build.VERSION.SDK_INT >= 34) startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(1, n) }
    override fun onDestroy() { super.onDestroy(); try { unregisterReceiver(switchReceiver) } catch(e: Exception) {}; displayManager?.unregisterDisplayListener(this); if (trackpadLayout != null) windowManager?.removeView(trackpadLayout); if (cursorLayout != null) windowManager?.removeView(cursorLayout); removeRemoteCursor(); if (isBound) ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection) }
    private fun toggleKeyboardMode() { vibrate(); isRightDragPending = false; if (!isKeyboardMode) { isKeyboardMode = true; savedWindowX = trackpadParams.x; savedWindowY = trackpadParams.y; trackpadParams.x = uiScreenWidth - trackpadParams.width; trackpadParams.y = 0; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(0xFFFF0000.toInt()) } else { isKeyboardMode = false; trackpadParams.x = savedWindowX; trackpadParams.y = savedWindowY; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(currentBorderColor) } }
    private fun openMenuHandle(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_DOWN) { vibrate(); val intent = Intent(this, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }; startActivity(intent); return true }; return false }
    
    private fun keyboardHandle(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { keyboardHandleDownTime = SystemClock.uptimeMillis(); handler.postDelayed(keyboardLongPressRunnable, 800); return true }
            MotionEvent.ACTION_UP -> { 
                handler.removeCallbacks(keyboardLongPressRunnable)
                if (SystemClock.uptimeMillis() - keyboardHandleDownTime < 800) { toggleCustomKeyboard() }
                return true 
            }
            MotionEvent.ACTION_CANCEL -> { handler.removeCallbacks(keyboardLongPressRunnable); return true }
        }
        return false
    }
    
    private fun voiceWindow(event: MotionEvent): Boolean { if(event.action == MotionEvent.ACTION_DOWN) { handler.postDelayed(voiceRunnable, 1000); return true } else if (event.action == MotionEvent.ACTION_UP) { handler.removeCallbacks(voiceRunnable); if(!isKeyboardMode) updateBorderColor(currentBorderColor); return true }; return false }
    private fun loadPrefs() { val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); cursorSpeed = p.getFloat("cursor_speed", 2.5f); scrollSpeed = p.getFloat("scroll_speed", 3.0f); prefVibrate = p.getBoolean("vibrate", true); prefReverseScroll = p.getBoolean("reverse_scroll", true); prefAlpha = p.getInt("alpha", 200); prefLocked = p.getBoolean("lock_position", false); prefVPosLeft = p.getBoolean("v_pos_left", false); prefHPosTop = p.getBoolean("h_pos_top", false); prefHandleTouchSize = p.getInt("handle_touch_size", 60); prefScrollTouchSize = p.getInt("scroll_touch_size", 60); prefHandleSize = p.getInt("handle_size", 60); prefScrollVisualSize = p.getInt("scroll_visual_size", 4); scrollZoneThickness = prefScrollTouchSize; prefCursorSize = p.getInt("cursor_size", 50) }
    private fun handlePreview(intent: Intent) { val t = intent.getStringExtra("TARGET"); val v = intent.getIntExtra("VALUE", 0); handler.removeCallbacks(clearHighlightsRunnable); when (t) { "alpha" -> { prefAlpha = v; highlightAlpha = true; updateBorderColor(currentBorderColor) }; "handle_touch" -> { prefHandleTouchSize = v; highlightHandles = true; updateLayoutSizes() }; "scroll_touch" -> { prefScrollTouchSize = v; scrollZoneThickness = v; highlightScrolls = true; updateLayoutSizes(); updateScrollPosition() }; "handle_size" -> { prefHandleSize = v; highlightHandles = true; updateHandleSize() }; "scroll_visual" -> { prefScrollVisualSize = v; highlightScrolls = true; updateLayoutSizes() }; "cursor_size" -> { prefCursorSize = v; updateCursorSize() } }; handler.postDelayed(clearHighlightsRunnable, 1500) }
    private fun addHandle(context: Context, gravity: Int, color: Int, onTouch: (View, MotionEvent) -> Boolean) { val c = FrameLayout(context); val cp = FrameLayout.LayoutParams(prefHandleTouchSize, prefHandleTouchSize); cp.gravity = gravity; val v = View(context); val bg = GradientDrawable(); bg.setColor(color); bg.cornerRadii = floatArrayOf(15f,15f, 15f,15f, 15f,15f, 15f,15f); v.background = bg; val vp = FrameLayout.LayoutParams(prefHandleSize, prefHandleSize); vp.gravity = Gravity.CENTER; c.addView(v, vp); handleVisuals.add(v); handleContainers.add(c); trackpadLayout?.addView(c, cp); c.setOnTouchListener { view, e -> onTouch(view, e) } }
    private fun updateHandleSize() { for (v in handleVisuals) { val p = v.layoutParams; p.width = prefHandleSize; p.height = prefHandleSize; v.layoutParams = p } }
    private fun updateLayoutSizes() { for (c in handleContainers) { val p = c.layoutParams; p.width = prefHandleTouchSize; p.height = prefHandleTouchSize; c.layoutParams = p }; updateScrollPosition() } 
    private fun addScrollBars(context: Context) { val m = prefHandleTouchSize + 10; vScrollContainer = FrameLayout(context); val vp = FrameLayout.LayoutParams(prefScrollTouchSize, FrameLayout.LayoutParams.MATCH_PARENT); vp.gravity = if (prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; vp.setMargins(0, m, 0, m); trackpadLayout?.addView(vScrollContainer, vp); vScrollVisual = View(context); vScrollVisual!!.setBackgroundColor(0x30FFFFFF.toInt()); val vvp = FrameLayout.LayoutParams(prefScrollVisualSize, FrameLayout.LayoutParams.MATCH_PARENT); vvp.gravity = Gravity.CENTER_HORIZONTAL; vScrollContainer?.addView(vScrollVisual, vvp); hScrollContainer = FrameLayout(context); val hp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefScrollTouchSize); hp.gravity = if (prefHPosTop) Gravity.TOP else Gravity.BOTTOM; hp.setMargins(m, 0, m, 0); trackpadLayout?.addView(hScrollContainer, hp); hScrollVisual = View(context); hScrollVisual!!.setBackgroundColor(0x30FFFFFF.toInt()); val hvp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefScrollVisualSize); hvp.gravity = Gravity.CENTER_VERTICAL; hScrollContainer?.addView(hScrollVisual, hvp) }
    private fun updateScrollPosition() { val m = prefHandleTouchSize + 10; if (vScrollContainer != null) { val vp = vScrollContainer!!.layoutParams as FrameLayout.LayoutParams; vp.gravity = if (prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; vp.setMargins(0, m, 0, m); vScrollContainer!!.layoutParams = vp }; if (hScrollContainer != null) { val hp = hScrollContainer!!.layoutParams as FrameLayout.LayoutParams; hp.gravity = if (prefHPosTop) Gravity.TOP else Gravity.BOTTOM; hp.setMargins(m, 0, m, 0); hScrollContainer!!.layoutParams = hp } }
    private fun updateBorderColor(strokeColor: Int) { currentBorderColor = strokeColor; val bg = trackpadLayout?.background as? GradientDrawable ?: return; bg.setColor(Color.TRANSPARENT); val colorWithAlpha = (strokeColor and 0x00FFFFFF) or (prefAlpha shl 24); bg.setStroke(4, if (highlightAlpha) 0xFF00FF00.toInt() else colorWithAlpha); trackpadLayout?.invalidate() }
}
```

## File: app/src/main/java/com/example/coverscreentester/ProfilesActivity.kt
```kotlin
package com.example.coverscreentester

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ProfilesActivity : Activity() {

    private lateinit var tvStats: TextView
    private lateinit var tvList: TextView
    private lateinit var btnSave: Button
    private lateinit var btnReset: Button
    private lateinit var btnClose: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles)

        // FORCE WINDOW SIZE
        try {
            val dm = resources.displayMetrics
            val targetWidth = (450 * dm.density).toInt().coerceAtMost(dm.widthPixels)
            window.setLayout(targetWidth, ViewGroup.LayoutParams.MATCH_PARENT)
            window.setGravity(Gravity.CENTER)
        } catch (e: Exception) {}

        tvStats = findViewById(R.id.tvCurrentStats)
        tvList = findViewById(R.id.tvProfileList)
        btnSave = findViewById(R.id.btnSaveCurrent)
        btnReset = findViewById(R.id.btnResetCurrent)
        btnClose = findViewById(R.id.btnClose)

        refreshUI()

        btnSave.setOnClickListener {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "SAVE_LAYOUT"
            startService(intent)
            Toast.makeText(this, "Saved for this Aspect Ratio!", Toast.LENGTH_SHORT).show()
            tvList.postDelayed({ refreshUI() }, 500)
        }

        btnReset.setOnClickListener {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "DELETE_PROFILE"
            startService(intent)
            Toast.makeText(this, "Profile Deleted. Resetting...", Toast.LENGTH_SHORT).show()
            tvList.postDelayed({ refreshUI() }, 500)
        }

        btnClose.setOnClickListener { finish() }
    }

    private fun refreshUI() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        
        val width = metrics.widthPixels.toFloat()
        val height = metrics.heightPixels.toFloat()
        val ratio = width / height
        val ratioKey = String.format("%.1f", ratio)
        
        tvStats.text = "Ratio: $ratioKey\nRes: ${metrics.widthPixels} x ${metrics.heightPixels}\nDensity: ${metrics.density}"

        val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val allEntries = prefs.all
        val sb = StringBuilder()
        
        val foundProfiles = HashSet<String>()
        
        for (key in allEntries.keys) {
            if (key.startsWith("profile_") && key.endsWith("_xp")) {
                val parts = key.split("_")
                if (parts.size >= 2) {
                    foundProfiles.add(parts[1])
                }
            }
        }
        
        if (foundProfiles.isEmpty()) {
            sb.append("No saved profiles.")
        } else {
            for (p in foundProfiles) {
                sb.append("• Ratio $p: Saved\n")
            }
        }
        
        tvList.text = sb.toString()
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/SettingsActivity.kt
```kotlin
package com.example.coverscreentester

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat

class SettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)

        // Views
        val seekBarCursor = findViewById<SeekBar>(R.id.seekBarCursorSpeed)
        val tvCursor = findViewById<TextView>(R.id.tvCursorSpeed)
        val seekBarScroll = findViewById<SeekBar>(R.id.seekBarScrollSpeed)
        val tvScroll = findViewById<TextView>(R.id.tvScrollSpeed)
        
        val swVibrate = findViewById<Switch>(R.id.switchVibrate)
        val swReverse = findViewById<Switch>(R.id.switchReverseScroll)
        val swVPos = findViewById<Switch>(R.id.switchVPosLeft)
        val swHPos = findViewById<Switch>(R.id.switchHPosTop)
        
        val seekCursorSize = findViewById<SeekBar>(R.id.seekBarCursorSize)
        val seekAlpha = findViewById<SeekBar>(R.id.seekBarAlpha)
        val seekHandleSize = findViewById<SeekBar>(R.id.seekBarHandleSize)
        val seekScrollVisual = findViewById<SeekBar>(R.id.seekBarScrollVisual)
        
        val seekHandleTouch = findViewById<SeekBar>(R.id.seekBarHandleTouch)
        val seekScrollTouch = findViewById<SeekBar>(R.id.seekBarScrollTouch)
        
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Load Values
        val cSpeed = prefs.getFloat("cursor_speed", 2.5f)
        seekBarCursor.progress = (cSpeed * 10).toInt()
        tvCursor.text = "Cursor Speed: "

        val sSpeed = prefs.getFloat("scroll_speed", 3.0f)
        seekBarScroll.progress = (sSpeed * 10).toInt()
        tvScroll.text = "Scroll Speed: "

        swVibrate.isChecked = prefs.getBoolean("vibrate", true)
        swReverse.isChecked = prefs.getBoolean("reverse_scroll", true)
        swVPos.isChecked = prefs.getBoolean("v_pos_left", false)
        swHPos.isChecked = prefs.getBoolean("h_pos_top", false)
        
        seekCursorSize.progress = prefs.getInt("cursor_size", 50)
        seekAlpha.progress = prefs.getInt("alpha", 200)
        seekHandleSize.progress = prefs.getInt("handle_size", 60)
        seekScrollVisual.progress = prefs.getInt("scroll_visual_size", 4)
        seekHandleTouch.progress = prefs.getInt("handle_touch_size", 60)
        seekScrollTouch.progress = prefs.getInt("scroll_touch_size", 60)

        // Listeners
        seekBarCursor.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar, v: Int, f: Boolean) {
                tvCursor.text = "Cursor Speed: "
            }
            override fun onStartTrackingTouch(s: SeekBar) {}
            override fun onStopTrackingTouch(s: SeekBar) {}
        })

        seekBarScroll.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar, v: Int, f: Boolean) {
                tvScroll.text = "Scroll Speed: "
            }
            override fun onStartTrackingTouch(s: SeekBar) {}
            override fun onStopTrackingTouch(s: SeekBar) {}
        })
        
        seekCursorSize.setOnSeekBarChangeListener(createPreviewListener("cursor_size"))
        seekAlpha.setOnSeekBarChangeListener(createPreviewListener("alpha"))
        seekHandleSize.setOnSeekBarChangeListener(createPreviewListener("handle_size"))
        seekScrollVisual.setOnSeekBarChangeListener(createPreviewListener("scroll_visual"))
        seekHandleTouch.setOnSeekBarChangeListener(createPreviewListener("handle_touch"))
        seekScrollTouch.setOnSeekBarChangeListener(createPreviewListener("scroll_touch"))

        btnSave.setOnClickListener {
            val cVal = if (seekBarCursor.progress < 1) 0.1f else seekBarCursor.progress / 10f
            val sVal = if (seekBarScroll.progress < 1) 0.1f else seekBarScroll.progress / 10f
            
            prefs.edit()
                .putFloat("cursor_speed", cVal)
                .putFloat("scroll_speed", sVal)
                .putBoolean("vibrate", swVibrate.isChecked)
                .putBoolean("reverse_scroll", swReverse.isChecked)
                .putBoolean("v_pos_left", swVPos.isChecked)
                .putBoolean("h_pos_top", swHPos.isChecked)
                .putInt("cursor_size", seekCursorSize.progress)
                .putInt("alpha", seekAlpha.progress)
                .putInt("handle_size", seekHandleSize.progress)
                .putInt("scroll_visual_size", seekScrollVisual.progress)
                .putInt("handle_touch_size", seekHandleTouch.progress)
                .putInt("scroll_touch_size", seekScrollTouch.progress)
                .apply()

            val intent = Intent(this, OverlayService::class.java)
            intent.action = "RELOAD_PREFS"
            startService(intent)
            finish()
        }
        
        btnBack.setOnClickListener { finish() }
    }
    
    private fun createPreviewListener(target: String) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(s: SeekBar, v: Int, f: Boolean) {
            val intent = Intent(this@SettingsActivity, OverlayService::class.java)
            intent.action = "PREVIEW_UPDATE"
            intent.putExtra("TARGET", target)
            intent.putExtra("VALUE", v)
            startService(intent)
        }
        override fun onStartTrackingTouch(s: SeekBar) {}
        override fun onStopTrackingTouch(s: SeekBar) {}
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/ShellUserService.kt
```kotlin
package com.example.coverscreentester

import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import com.example.coverscreentester.IShellService
import java.lang.reflect.Method

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"
    private lateinit var inputManager: Any
    private lateinit var injectInputEventMethod: Method
    private val INJECT_MODE_ASYNC = 0

    init {
        setupReflection()
    }

    private fun setupReflection() {
        try {
            val imClass = Class.forName("android.hardware.input.InputManager")
            val getInstance = imClass.getMethod("getInstance")
            inputManager = getInstance.invoke(null)!!

            injectInputEventMethod = imClass.getMethod(
                "injectInputEvent",
                InputEvent::class.java,
                Int::class.javaPrimitiveType
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup reflection", e)
        }
    }

    override fun setWindowingMode(taskId: Int, mode: Int) {
        try { Runtime.getRuntime().exec("am task set-windowing-mode $taskId $mode").waitFor() } catch (e: Exception) { Log.e(TAG, "Failed to set window mode", e) }
    }

    override fun resizeTask(taskId: Int, left: Int, top: Int, right: Int, bottom: Int) {
        try { Runtime.getRuntime().exec("am task resize $taskId $left $top $right $bottom") } catch (e: Exception) { Log.e(TAG, "Failed to resize task", e) }
    }

    override fun runCommand(cmd: String?): String {
        try { if (cmd != null) { Runtime.getRuntime().exec(cmd) } } catch (e: Exception) {}
        return "" 
    }

    override fun injectKey(keyCode: Int, action: Int) {
        if (action == KeyEvent.ACTION_DOWN) {
            try { Runtime.getRuntime().exec("input keyevent $keyCode") } catch (e: Exception) { Log.e(TAG, "Key injection failed", e) }
        }
    }

    override fun injectKeyOnDisplay(keyCode: Int, action: Int, displayId: Int) {
        if (!this::inputManager.isInitialized || !this::injectInputEventMethod.isInitialized) return
        val now = SystemClock.uptimeMillis()
        val event = KeyEvent(now, now, action, keyCode, 0, 0, -1, 0, 0, InputDevice.SOURCE_KEYBOARD)
        try {
            val method = InputEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            method.invoke(event, displayId)
            injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
        } catch (e: Exception) { Log.e(TAG, "Key Injection on Display Failed", e) }
    }

    override fun execClick(x: Float, y: Float, displayId: Int) {
        val downTime = SystemClock.uptimeMillis()
        injectInternal(MotionEvent.ACTION_DOWN, x, y, displayId, downTime, downTime, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY)
        try { Thread.sleep(50) } catch (e: InterruptedException) {}
        injectInternal(MotionEvent.ACTION_UP, x, y, displayId, downTime, SystemClock.uptimeMillis(), InputDevice.SOURCE_MOUSE, 0)
    }

    override fun execRightClick(x: Float, y: Float, displayId: Int) {
        val downTime = SystemClock.uptimeMillis()
        injectInternal(MotionEvent.ACTION_DOWN, x, y, displayId, downTime, downTime, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_SECONDARY)
        try { Thread.sleep(50) } catch (e: InterruptedException) {}
        injectInternal(MotionEvent.ACTION_UP, x, y, displayId, downTime, SystemClock.uptimeMillis(), InputDevice.SOURCE_MOUSE, 0)
    }

    override fun injectMouse(action: Int, x: Float, y: Float, displayId: Int, source: Int, buttonState: Int, downTime: Long) {
        injectInternal(action, x, y, displayId, downTime, SystemClock.uptimeMillis(), source, buttonState)
    }

    override fun injectScroll(x: Float, y: Float, vDistance: Float, hDistance: Float, displayId: Int) {
        if (!this::inputManager.isInitialized || !this::injectInputEventMethod.isInitialized) return
        val now = SystemClock.uptimeMillis()
        val props = MotionEvent.PointerProperties(); props.id = 0; props.toolType = MotionEvent.TOOL_TYPE_MOUSE
        val coords = MotionEvent.PointerCoords(); coords.x = x; coords.y = y; coords.pressure = 1.0f; coords.size = 1.0f
        coords.setAxisValue(MotionEvent.AXIS_VSCROLL, vDistance)
        coords.setAxisValue(MotionEvent.AXIS_HSCROLL, hDistance)
        var event: MotionEvent? = null
        try {
            event = MotionEvent.obtain(now, now, MotionEvent.ACTION_SCROLL, 1, arrayOf(props), arrayOf(coords), 0, 0, 1.0f, 1.0f, 0, 0, InputDevice.SOURCE_MOUSE, 0)
            setDisplayId(event, displayId)
            injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
        } catch (e: Exception) { Log.e(TAG, "Scroll Injection failed", e) } finally { event?.recycle() }
    }

    private fun setDisplayId(event: MotionEvent, displayId: Int) {
        try {
            val method = MotionEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            method.invoke(event, displayId)
        } catch (e: Exception) {}
    }

    private fun injectInternal(action: Int, x: Float, y: Float, displayId: Int, downTime: Long, eventTime: Long, source: Int, buttonState: Int) {
        if (!this::inputManager.isInitialized || !this::injectInputEventMethod.isInitialized) return
        val props = MotionEvent.PointerProperties(); props.id = 0
        props.toolType = if (source == InputDevice.SOURCE_MOUSE) MotionEvent.TOOL_TYPE_MOUSE else MotionEvent.TOOL_TYPE_FINGER
        val coords = MotionEvent.PointerCoords(); coords.x = x; coords.y = y
        coords.pressure = if (buttonState != 0 || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) 1.0f else 0.0f; coords.size = 1.0f
        var event: MotionEvent? = null
        try {
            event = MotionEvent.obtain(downTime, eventTime, action, 1, arrayOf(props), arrayOf(coords), 0, buttonState, 1.0f, 1.0f, 0, 0, source, 0)
            setDisplayId(event, displayId)
            injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
        } catch (e: Exception) { Log.e(TAG, "Injection failed", e) } finally { event?.recycle() }
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/ShizukuBinder.java
```java
package com.example.coverscreentester;

import android.content.ComponentName;
import android.content.ServiceConnection;
import rikka.shizuku.Shizuku;

public class ShizukuBinder {
    
    public static void bind(ComponentName component, ServiceConnection connection, boolean debug, int version) {
        Shizuku.UserServiceArgs args = new Shizuku.UserServiceArgs(component)
                .processNameSuffix("shell")
                .daemon(false)
                .debuggable(debug)
                .version(version);
        
        Shizuku.bindUserService(args, connection);
    }

    public static void unbind(ComponentName component, ServiceConnection connection) {
        Shizuku.UserServiceArgs args = new Shizuku.UserServiceArgs(component)
                .processNameSuffix("shell")
                .daemon(false);
        
        Shizuku.unbindUserService(args, connection, true); 
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/ShizukuInputHandler.kt
```kotlin
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
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            // If strictly using Shizuku binding without 'su', replace above with:
            // Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inject input: ${e.message}")
        }
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/TrackpadService.kt
```kotlin
package com.example.coverscreentester

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

class TrackpadService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Layout Params: TYPE_APPLICATION_OVERLAY is crucial for Android 8.0+
        // FLAG_NOT_FOCUSABLE ensures the overlay doesn't eat physical button presses (like Power/Vol)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        try {
            val inflater = LayoutInflater.from(this)
            overlayView = inflater.inflate(R.layout.service_overlay, null)

            // Setup Safety Exit Button
            val closeBtn = overlayView?.findViewById<Button>(R.id.btn_close)
            closeBtn?.setOnClickListener {
                stopSelf()
            }

            // Setup Trackpad Touch Listener
            val trackpadView = overlayView?.findViewById<View>(R.id.view_trackpad)
            trackpadView?.setOnTouchListener { _, event ->
                // This consumes the touch event so it doesn't pass through to the launcher
                // We will add the Shizuku injection logic here next.
                true
            }

            windowManager.addView(overlayView, params)
            Toast.makeText(this, "Trackpad Overlay Started", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (overlayView != null) {
            try {
                windowManager.removeView(overlayView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            overlayView = null
        }
        Toast.makeText(this, "Trackpad Overlay Stopped", Toast.LENGTH_SHORT).show()
    }
}
```

## File: app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
```kotlin
package com.example.quadrantlauncher

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Color
import android.net.Uri
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rikka.shizuku.Shizuku
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.hypot

class FloatingLauncherService : Service() {

    companion object {
        const val MODE_SEARCH = 0
        const val MODE_LAYOUTS = 2
        const val MODE_RESOLUTION = 3
        const val MODE_DPI = 4
        const val MODE_PROFILES = 5
        const val MODE_SETTINGS = 6
        
        const val LAYOUT_FULL = 1
        const val LAYOUT_SIDE_BY_SIDE = 2
        const val LAYOUT_TRI_EVEN = 3
        const val LAYOUT_CORNERS = 4
        const val LAYOUT_TOP_BOTTOM = 5
        const val LAYOUT_TRI_SIDE_MAIN_SIDE = 6
        const val LAYOUT_QUAD_ROW_EVEN = 7
        const val LAYOUT_CUSTOM_DYNAMIC = 99

        const val CHANNEL_ID = "OverlayServiceChannel"
        const val TAG = "FloatingService"
        const val ACTION_OPEN_DRAWER = "com.example.quadrantlauncher.OPEN_DRAWER"
        const val ACTION_UPDATE_ICON = "com.example.quadrantlauncher.UPDATE_ICON"
    }

    private lateinit var windowManager: WindowManager
    private var displayContext: Context? = null
    private var currentDisplayId = 0
    private var lastPhysicalDisplayId = Display.DEFAULT_DISPLAY 
    
    private var bubbleView: View? = null
    private var drawerView: View? = null
    
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var drawerParams: WindowManager.LayoutParams

    private var isExpanded = false
    private val selectedAppsQueue = mutableListOf<MainActivity.AppInfo>()
    private val allAppsList = mutableListOf<MainActivity.AppInfo>()
    private val displayList = mutableListOf<Any>()
    
    private var activeProfileName: String? = null
    private var currentMode = MODE_SEARCH
    private var selectedLayoutType = 2
    private var selectedResolutionIndex = 0
    private var currentDpiSetting = -1
    private var currentFontSize = 16f
    
    private var activeCustomRects: List<Rect>? = null
    private var activeCustomLayoutName: String? = null
    
    private var killAppOnExecute = true
    private var targetDisplayIndex = 1 
    private var resetTrackpad = false
    private var isExtinguished = false
    private var isInstantMode = true 
    private var showShizukuWarning = true 
    
    private var isVirtualDisplayActive = false
    
    private var currentDrawerHeightPercent = 70
    private var currentDrawerWidthPercent = 90
    private var autoResizeEnabled = true
    
    private val PACKAGE_BLANK = "internal.blank.spacer"
    
    private var shellService: IShellService? = null
    private var isBound = false
    private val uiHandler = Handler(Looper.getMainLooper())

    // --- SHIZUKU LISTENERS ---
    private val shizukuBinderListener = Shizuku.OnBinderReceivedListener {
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            bindShizuku()
        }
    }

    private val shizukuPermissionListener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            bindShizuku()
        }
    }

    private val commandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_OPEN_DRAWER) {
                if (isExtinguished) {
                    wakeUp()
                } else if (!isExpanded) {
                    toggleDrawer()
                }
            } else if (intent?.action == ACTION_UPDATE_ICON) {
                updateBubbleIcon()
                if (currentMode == MODE_SETTINGS) {
                    switchMode(MODE_SETTINGS)
                }
            }
        }
    }

    private val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return
            dismissKeyboardAndRestore()
            if (currentMode == MODE_PROFILES) {
                val item = displayList.getOrNull(pos) as? ProfileOption ?: return
                if (item.isCurrent) { (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos); return }
                AppPreferences.deleteProfile(this@FloatingLauncherService, item.name)
                showToast("Deleted ${item.name}")
                switchMode(MODE_PROFILES)
                return
            }
            if (currentMode == MODE_LAYOUTS) {
                val item = displayList.getOrNull(pos) as? LayoutOption ?: return
                if (item.type == LAYOUT_CUSTOM_DYNAMIC && item.isCustomSaved) {
                    AppPreferences.deleteCustomLayout(this@FloatingLauncherService, item.name)
                    showToast("Deleted ${item.name}")
                    switchMode(MODE_LAYOUTS)
                } else { (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos) }
                return
            }
            if (currentMode == MODE_RESOLUTION) {
                val item = displayList.getOrNull(pos) as? ResolutionOption ?: return
                if (item.index >= 100) {
                    AppPreferences.deleteCustomResolution(this@FloatingLauncherService, item.name)
                    showToast("Deleted ${item.name}")
                    switchMode(MODE_RESOLUTION)
                } else {
                    (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos)
                }
                return
            }
            if (currentMode == MODE_SEARCH) {
                val item = displayList.getOrNull(pos) as? MainActivity.AppInfo ?: return
                if (item.packageName == PACKAGE_BLANK) { (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos); return }
                if (direction == ItemTouchHelper.LEFT && !item.isFavorite) toggleFavorite(item)
                else if (direction == ItemTouchHelper.RIGHT && item.isFavorite) toggleFavorite(item)
                refreshSearchList()
            }
        }
    }

    private val selectedAppsDragCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean {
            val fromPos = v.adapterPosition; val toPos = t.adapterPosition
            Collections.swap(selectedAppsQueue, fromPos, toPos)
            r.adapter?.notifyItemMoved(fromPos, toPos)
            return true
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            dismissKeyboardAndRestore()
            val pos = viewHolder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                val app = selectedAppsQueue[pos]
                if (app.packageName != PACKAGE_BLANK) {
                    Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start()
                    showToast("Killed ${app.label}")
                }
                selectedAppsQueue.removeAt(pos)
                updateSelectedAppsDock()
                drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
                if (isInstantMode) applyLayoutImmediate()
            }
        }
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            if (isInstantMode) applyLayoutImmediate()
        }
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            shellService = IShellService.Stub.asInterface(binder)
            isBound = true
            updateExecuteButtonColor(true)
            updateBubbleIcon() 
            showToast("Shizuku Connected")
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            shellService = null
            isBound = false
            updateExecuteButtonColor(false)
            updateBubbleIcon()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        try {
            Shizuku.addBinderReceivedListener(shizukuBinderListener)
            Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)
        } catch (e: Exception) {}

        val filter = IntentFilter().apply {
            addAction(ACTION_OPEN_DRAWER)
            addAction(ACTION_UPDATE_ICON)
        }
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            registerReceiver(commandReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(commandReceiver, filter)
        }
        try {
            if (rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                bindShizuku()
            }
        } catch (e: Exception) {}
        
        loadInstalledApps()
        currentFontSize = AppPreferences.getFontSize(this)
        killAppOnExecute = AppPreferences.getKillOnExecute(this)
        targetDisplayIndex = AppPreferences.getTargetDisplayIndex(this)
        isInstantMode = AppPreferences.getInstantMode(this)
        showShizukuWarning = AppPreferences.getShowShizukuWarning(this)
        
        currentDrawerHeightPercent = AppPreferences.getDrawerHeightPercent(this)
        currentDrawerWidthPercent = AppPreferences.getDrawerWidthPercent(this)
        autoResizeEnabled = AppPreferences.getAutoResizeKeyboard(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val targetDisplayId = intent?.getIntExtra("DISPLAY_ID", Display.DEFAULT_DISPLAY) ?: Display.DEFAULT_DISPLAY

        if (bubbleView != null && targetDisplayId != currentDisplayId) {
            try { windowManager.removeView(bubbleView) } catch (e: Exception) {}
            try { if (isExpanded) windowManager.removeView(drawerView) } catch (e: Exception) {}
            setupDisplayContext(targetDisplayId)
            setupBubble()
            setupDrawer()
            updateBubbleIcon()
            
            loadDisplaySettings(currentDisplayId)
            
            isExpanded = false 
            showToast("Launcher moved to Display $targetDisplayId")
        } else if (bubbleView == null) {
            try {
                setupDisplayContext(targetDisplayId)
                setupBubble()
                setupDrawer()
                
                selectedLayoutType = AppPreferences.getLastLayout(this)
                activeCustomLayoutName = AppPreferences.getLastCustomLayoutName(this)
                updateGlobalFontSize()
                updateBubbleIcon()
                
                loadDisplaySettings(currentDisplayId)
                
                if (selectedLayoutType == LAYOUT_CUSTOM_DYNAMIC && activeCustomLayoutName != null) {
                    val data = AppPreferences.getCustomLayoutData(this, activeCustomLayoutName!!)
                    if (data != null) {
                        val rects = mutableListOf<Rect>()
                        val rectParts = data.split("|")
                        for (rp in rectParts) {
                            val coords = rp.split(",")
                            if (coords.size == 4) rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt()))
                        }
                        activeCustomRects = rects
                    }
                }

                try {
                    if (shellService == null && rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                        bindShizuku()
                    }
                } catch (e: Exception) {}
            } catch (e: Exception) {
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }
    
    private fun loadDisplaySettings(displayId: Int) {
        selectedResolutionIndex = AppPreferences.getDisplayResolution(this, displayId)
        currentDpiSetting = AppPreferences.getDisplayDpi(this, displayId)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            Shizuku.removeBinderReceivedListener(shizukuBinderListener)
            Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener)
        } catch (e: Exception) {}
        
        try { unregisterReceiver(commandReceiver) } catch(e: Exception) {}
        try { if (bubbleView != null) windowManager.removeView(bubbleView) } catch(e: Exception) {}
        try { if (isExpanded) windowManager.removeView(drawerView) } catch(e: Exception) {}
        if (isBound) {
            try { ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection); isBound = false } catch (e: Exception) {}
        }
    }
    
    private fun showToast(msg: String) { uiHandler.post { Toast.makeText(this@FloatingLauncherService, msg, Toast.LENGTH_SHORT).show() } }
    
    private fun setupDisplayContext(displayId: Int) {
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManager.getDisplay(displayId)
        if (display == null) {
             windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
             return
        }
        currentDisplayId = displayId
        displayContext = createDisplayContext(display)
        windowManager = displayContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private fun refreshDisplayId() {
        val id = displayContext?.display?.displayId ?: Display.DEFAULT_DISPLAY
        currentDisplayId = id
    }

    private fun startForegroundService() {
        val channelId = if (android.os.Build.VERSION.SDK_INT >= 26) {
            val channel = android.app.NotificationChannel(CHANNEL_ID, "Floating Launcher", android.app.NotificationManager.IMPORTANCE_LOW)
            getSystemService(android.app.NotificationManager::class.java).createNotificationChannel(channel)
            CHANNEL_ID
        } else ""
        val notification = NotificationCompat.Builder(this, channelId).setContentTitle("CoverScreen Launcher Active").setSmallIcon(R.drawable.ic_launcher_bubble).setPriority(NotificationCompat.PRIORITY_MIN).build()
        if (android.os.Build.VERSION.SDK_INT >= 34) startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(1, notification)
    }

    private fun bindShizuku() {
        try { val component = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(component, userServiceConnection, true, 1) } catch (e: Exception) { Log.e(TAG, "Bind Shizuku Failed", e) }
    }
    
    private fun updateExecuteButtonColor(isReady: Boolean) {
        uiHandler.post { val executeBtn = drawerView?.findViewById<ImageView>(R.id.icon_execute); if (isReady) executeBtn?.setColorFilter(Color.GREEN) else executeBtn?.setColorFilter(Color.RED) }
    }

    private fun setupBubble() {
        val context = displayContext ?: this
        val themeContext = ContextThemeWrapper(context, R.style.Theme_QuadrantLauncher)
        bubbleView = LayoutInflater.from(themeContext).inflate(R.layout.layout_bubble, null)
        bubbleView?.isClickable = true; bubbleView?.isFocusable = true 

        bubbleParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT)
        bubbleParams.gravity = Gravity.TOP or Gravity.START; bubbleParams.x = 50; bubbleParams.y = 200

        var velocityTracker: VelocityTracker? = null
        bubbleView?.setOnTouchListener(object : View.OnTouchListener {
            var initialX = 0; var initialY = 0; var initialTouchX = 0f; var initialTouchY = 0f; var isDrag = false
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { initialX = bubbleParams.x; initialY = bubbleParams.y; initialTouchX = event.rawX; initialTouchY = event.rawY; isDrag = false; return true }
                    MotionEvent.ACTION_MOVE -> { if (Math.abs(event.rawX - initialTouchX) > 10 || Math.abs(event.rawY - initialTouchY) > 10) isDrag = true; if (isDrag) { bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt(); bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt(); windowManager.updateViewLayout(bubbleView, bubbleParams) }; return true }
                    MotionEvent.ACTION_UP -> { 
                        velocityTracker?.computeCurrentVelocity(1000) 
                        val vX = velocityTracker?.xVelocity ?: 0f; val vY = velocityTracker?.yVelocity ?: 0f 
                        val totalVel = hypot(vX.toDouble(), vY.toDouble()) 
                        if (isDrag && totalVel > 2500) { showToast("Closing..."); stopSelf(); return true }
                        if (!isDrag) {
                            if (!isBound && showShizukuWarning) {
                                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                                     bindShizuku() // Retry bind if permission exists
                                } else {
                                     showToast("Shizuku NOT Connected. Opening Shizuku...")
                                     launchShizuku()
                                }
                            } else {
                                toggleDrawer()
                            }
                        }
                        velocityTracker?.recycle(); velocityTracker = null; return true 
                    }
                    MotionEvent.ACTION_CANCEL -> { velocityTracker?.recycle(); velocityTracker = null }
                }
                return false
            }
        })
        windowManager.addView(bubbleView, bubbleParams)
    }
    
    private fun launchShizuku() {
        try {
            val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                showToast("Shizuku app not found")
            }
        } catch(e: Exception) {
            showToast("Failed to launch Shizuku")
        }
    }

    private fun updateBubbleIcon() {
        val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon) ?: return
        
        if (!isBound && showShizukuWarning) {
            uiHandler.post {
                iconView.setImageResource(android.R.drawable.ic_dialog_alert)
                iconView.setColorFilter(Color.RED)
                iconView.imageTintList = null
            }
            return
        }
        
        uiHandler.post {
            try {
                val uriStr = AppPreferences.getIconUri(this)
                if (uriStr != null) {
                    val uri = Uri.parse(uriStr)
                    val input = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(input)
                    input?.close()
                    if (bitmap != null) { 
                        iconView.setImageBitmap(bitmap)
                        iconView.imageTintList = null
                        iconView.clearColorFilter() 
                    } else { 
                        iconView.setImageResource(R.drawable.ic_launcher_bubble) 
                        iconView.imageTintList = null
                        iconView.clearColorFilter()
                    }
                } else { 
                    iconView.setImageResource(R.drawable.ic_launcher_bubble) 
                    iconView.imageTintList = null
                    iconView.clearColorFilter()
                }
            } catch (e: Exception) { 
                iconView.setImageResource(R.drawable.ic_launcher_bubble) 
                iconView.imageTintList = null
                iconView.clearColorFilter()
            }
        }
    }

    private fun dismissKeyboardAndRestore() {
        val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)
        if (searchBar != null && searchBar.hasFocus()) {
            searchBar.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchBar.windowToken, 0)
        }
        val dpiInput = drawerView?.findViewById<EditText>(R.id.input_dpi_value)
        if (dpiInput != null && dpiInput.hasFocus()) {
            dpiInput.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(dpiInput.windowToken, 0)
        }
        updateDrawerHeight(false)
    }

    private fun setupDrawer() {
        val context = displayContext ?: this
        val themeContext = ContextThemeWrapper(context, R.style.Theme_QuadrantLauncher)
        drawerView = LayoutInflater.from(themeContext).inflate(R.layout.layout_rofi_drawer, null)
        drawerView!!.fitsSystemWindows = true 
        drawerParams = WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT)
        drawerParams.gravity = Gravity.TOP or Gravity.START; drawerParams.x = 0; drawerParams.y = 0
        drawerParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container)
        if (container != null) { val lp = container.layoutParams as? FrameLayout.LayoutParams; if (lp != null) { lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; lp.topMargin = 100; container.layoutParams = lp } }
        
        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar)
        val mainRecycler = drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)
        val selectedRecycler = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler)
        val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        
        if (isBound) executeBtn.setColorFilter(Color.GREEN) else executeBtn.setColorFilter(Color.RED)

        drawerView!!.findViewById<ImageView>(R.id.icon_search_mode).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_SEARCH) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_window).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_LAYOUTS) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_resolution).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_RESOLUTION) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_dpi).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_DPI) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_profiles).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_PROFILES) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_settings).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_SETTINGS) }
        
        executeBtn.setOnClickListener { executeLaunch(selectedLayoutType, closeDrawer = true) }
        
        searchBar.addTextChangedListener(object : TextWatcher { override fun afterTextChanged(s: Editable?) { filterList(s.toString()) }; override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}; override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {} })
        searchBar.imeOptions = EditorInfo.IME_ACTION_DONE
        searchBar.setOnEditorActionListener { v, actionId, event -> if (actionId == EditorInfo.IME_ACTION_DONE) { dismissKeyboardAndRestore(); return@setOnEditorActionListener true }; false }
        searchBar.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) { if (searchBar.text.isEmpty() && selectedAppsQueue.isNotEmpty()) { val lastIndex = selectedAppsQueue.size - 1; selectedAppsQueue.removeAt(lastIndex); updateSelectedAppsDock(); mainRecycler.adapter?.notifyDataSetChanged(); return@setOnKeyListener true } }
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { if (searchBar.hasFocus()) { dismissKeyboardAndRestore(); return@setOnKeyListener true } }
            return@setOnKeyListener false
        }
        searchBar.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) { updateDrawerHeight(hasFocus) } }

        mainRecycler.layoutManager = LinearLayoutManager(themeContext)
        mainRecycler.adapter = RofiAdapter()
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(mainRecycler)
        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() { override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) { if (newState == RecyclerView.SCROLL_STATE_DRAGGING) { dismissKeyboardAndRestore() } } })
        mainRecycler.setOnTouchListener { v, event -> if (event.action == MotionEvent.ACTION_DOWN) { dismissKeyboardAndRestore() }; false }
        
        selectedRecycler.layoutManager = LinearLayoutManager(themeContext, LinearLayoutManager.HORIZONTAL, false)
        selectedRecycler.adapter = SelectedAppsAdapter()
        val dockTouchHelper = ItemTouchHelper(selectedAppsDragCallback)
        dockTouchHelper.attachToRecyclerView(selectedRecycler)
        
        drawerView!!.setOnClickListener { toggleDrawer() }
        drawerView!!.isFocusableInTouchMode = true
        drawerView!!.setOnKeyListener { _, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { toggleDrawer(); true } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && isExtinguished) { wakeUp(); true } else false }
    }
    
    private fun updateDrawerHeight(isKeyboardMode: Boolean) {
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container) ?: return
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
        val screenH = dm.heightPixels; val screenW = dm.widthPixels
        val lp = container.layoutParams as? FrameLayout.LayoutParams; val topMargin = lp?.topMargin ?: 100
        var finalHeight = (screenH * (currentDrawerHeightPercent / 100f)).toInt()
        if (isKeyboardMode) { finalHeight = (screenH * 0.40f).toInt(); val maxAvailable = screenH - topMargin - 20; if (finalHeight > maxAvailable) finalHeight = maxAvailable }
        val newW = (screenW * (currentDrawerWidthPercent / 100f)).toInt()
        if (container.layoutParams.height != finalHeight || container.layoutParams.width != newW) { container.layoutParams.width = newW; container.layoutParams.height = finalHeight; container.requestLayout(); if (drawerParams.y != 0) { drawerParams.y = 0; windowManager.updateViewLayout(drawerView, drawerParams) } }
    }

    private fun toggleDrawer() {
        if (isExpanded) {
            try { windowManager.removeView(drawerView) } catch(e: Exception) {}
            bubbleView?.visibility = View.VISIBLE
            isExpanded = false
        } else {
            setupDisplayContext(currentDisplayId) 
            updateDrawerHeight(false)
            try { windowManager.addView(drawerView, drawerParams) } catch(e: Exception) {}
            bubbleView?.visibility = View.GONE
            isExpanded = true
            switchMode(MODE_SEARCH) 
            val et = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)
            et?.setText(""); et?.clearFocus()
            updateSelectedAppsDock()
            if (isInstantMode) fetchRunningApps()
        }
    }
    private fun updateGlobalFontSize() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); searchBar?.textSize = currentFontSize; drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() }

    private fun loadInstalledApps() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val riList = pm.queryIntentActivities(intent, 0)
        allAppsList.clear()
        allAppsList.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK))
        for (ri in riList) {
            val app = MainActivity.AppInfo(ri.loadLabel(pm).toString(), ri.activityInfo.packageName, AppPreferences.isFavorite(this, ri.activityInfo.packageName))
            allAppsList.add(app)
        }
        allAppsList.sortBy { it.label.lowercase() }
    }

    private fun getLayoutName(type: Int): String { 
        return when(type) { 
            LAYOUT_FULL -> "1 App - Full"
            LAYOUT_SIDE_BY_SIDE -> "Split"
            LAYOUT_TOP_BOTTOM -> "Top/Bot"
            LAYOUT_TRI_EVEN -> "Tri-Split"
            LAYOUT_CORNERS -> "Quadrant"
            LAYOUT_TRI_SIDE_MAIN_SIDE -> "3 Apps - Side/Main/Side"
            LAYOUT_QUAD_ROW_EVEN -> "4 Apps - Row"
            LAYOUT_CUSTOM_DYNAMIC -> "Custom"
            else -> "Unknown" 
        } 
    }
    
    private fun getRatioName(index: Int): String { return when(index) { 1 -> "1:1"; 2 -> "16:9"; 3 -> "32:9"; else -> "Default" } }
    private fun getTargetDimensions(index: Int): Pair<Int, Int>? { return when(index) { 1 -> 1422 to 1500; 2 -> 1920 to 1080; 3 -> 3840 to 1080; else -> null } }
    private fun getResolutionCommand(index: Int): String { return when(index) { 1 -> "wm size 1422x1500 -d $currentDisplayId"; 2 -> "wm size 1920x1080 -d $currentDisplayId"; 3 -> "wm size 3840x1080 -d $currentDisplayId"; else -> "wm size reset -d $currentDisplayId" } }
    private fun sortAppQueue() { selectedAppsQueue.sortWith(compareBy { it.isMinimized }) }
    private fun updateSelectedAppsDock() { val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); if (selectedAppsQueue.isEmpty()) { dock.visibility = View.GONE } else { dock.visibility = View.VISIBLE; dock.adapter?.notifyDataSetChanged(); dock.scrollToPosition(selectedAppsQueue.size - 1) } }
    private fun refreshSearchList() { val query = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString() ?: ""; filterList(query) }
    private fun filterList(query: String) {
        if (currentMode != MODE_SEARCH) return
        val actualQuery = query.substringAfterLast(",").trim()
        displayList.clear()
        val filtered = if (actualQuery.isEmpty()) { allAppsList } else { allAppsList.filter { it.label.contains(actualQuery, ignoreCase = true) } }
        val sorted = filtered.sortedWith(compareBy<MainActivity.AppInfo> { it.packageName != PACKAGE_BLANK }.thenByDescending { it.isFavorite }.thenBy { it.label.lowercase() })
        displayList.addAll(sorted)
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    private fun addToSelection(app: MainActivity.AppInfo) {
        dismissKeyboardAndRestore()
        val et = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar)
        if (app.packageName == PACKAGE_BLANK) { selectedAppsQueue.add(app); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate(); return }
        val existing = selectedAppsQueue.find { it.packageName == app.packageName }
        if (existing != null) { selectedAppsQueue.remove(existing); Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start(); showToast("Removed ${app.label}"); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); et.setText(""); if (isInstantMode) applyLayoutImmediate() } 
        else { app.isMinimized = false; selectedAppsQueue.add(app); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); et.setText(""); if (isInstantMode) { launchViaApi(app.packageName, null); launchViaShell(app.packageName); uiHandler.postDelayed({ applyLayoutImmediate() }, 200); uiHandler.postDelayed({ applyLayoutImmediate() }, 800) } }
    }
    private fun toggleFavorite(app: MainActivity.AppInfo) { val newState = AppPreferences.toggleFavorite(this, app.packageName); app.isFavorite = newState; allAppsList.find { it.packageName == app.packageName }?.isFavorite = newState }
    
    private fun launchViaApi(pkg: String, bounds: Rect?) { 
        try { 
            val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val options = android.app.ActivityOptions.makeBasic()
            options.setLaunchDisplayId(currentDisplayId) // FORCE DISPLAY ID
            if (bounds != null) options.setLaunchBounds(bounds)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {} 
    }
    private fun launchViaShell(pkg: String) { try { val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return; if (shellService != null) { val component = intent.component?.flattenToShortString() ?: pkg; val cmd = "am start -n $component -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"; Thread { shellService?.runCommand(cmd) }.start() } } catch (e: Exception) {} }
    
    private fun cycleDisplay() {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val displays = dm.displays
        
        if (isVirtualDisplayActive) {
            val virtualDisp = displays.firstOrNull { it.displayId >= 2 }
            if (virtualDisp != null) {
                val targetId = if (currentDisplayId == virtualDisp.displayId) {
                    if (displays.any { it.displayId == lastPhysicalDisplayId }) lastPhysicalDisplayId else Display.DEFAULT_DISPLAY
                } else {
                    lastPhysicalDisplayId = currentDisplayId
                    virtualDisp.displayId
                }
                performDisplayChange(targetId)
                return
            }
        }

        val currentIdx = displays.indexOfFirst { it.displayId == currentDisplayId }
        val nextIdx = if (currentIdx == -1) 0 else (currentIdx + 1) % displays.size
        performDisplayChange(displays[nextIdx].displayId)
    }
    
    private fun performDisplayChange(newId: Int) {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val targetDisplay = dm.getDisplay(newId) ?: return
        
        try {
            if (bubbleView != null && bubbleView!!.isAttachedToWindow) windowManager.removeView(bubbleView)
            if (drawerView != null && drawerView!!.isAttachedToWindow) windowManager.removeView(drawerView)
        } catch (e: Exception) {}

        currentDisplayId = newId
        setupDisplayContext(currentDisplayId)
        targetDisplayIndex = currentDisplayId
        AppPreferences.setTargetDisplayIndex(this, targetDisplayIndex)
        setupBubble()
        setupDrawer()
        
        loadDisplaySettings(currentDisplayId)
        
        updateBubbleIcon() 
        isExpanded = false
        showToast("Switched to Display $currentDisplayId (${targetDisplay.name})")
    }
    
    private fun toggleVirtualDisplay(enable: Boolean) {
        isVirtualDisplayActive = enable
        Thread {
            try {
                if (enable) {
                    shellService?.runCommand("settings put global overlay_display_devices \"1920x1080/320\"")
                    uiHandler.post { showToast("Creating Virtual Display... Wait a moment, then Switch Display.") }
                } else {
                    shellService?.runCommand("settings delete global overlay_display_devices")
                    uiHandler.post { showToast("Destroying Virtual Display...") }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Virtual Display Toggle Failed", e)
            }
        }.start()
        if (currentMode == MODE_SETTINGS) uiHandler.postDelayed({ switchMode(MODE_SETTINGS) }, 500)
    }

    private fun performExtinguish() { toggleDrawer(); isExtinguished = true; Thread { try { shellService?.setScreenOff(targetDisplayIndex, true) } catch (e: Exception) {} }.start(); showToast("Screen OFF (Index ${targetDisplayIndex}). Vol+ to Wake.") }
    private fun wakeUp() { isExtinguished = false; Thread { shellService?.setScreenOff(0, false); shellService?.setScreenOff(1, false) }.start(); showToast("Screen Woke Up"); if (currentMode == MODE_SETTINGS) drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() }
    private fun applyLayoutImmediate() { executeLaunch(selectedLayoutType, closeDrawer = false) }
    private fun fetchRunningApps() { if (shellService == null) return; Thread { try { val visiblePackages = shellService!!.getVisiblePackages(currentDisplayId); val allRunning = shellService!!.getAllRunningPackages(); val lastQueue = AppPreferences.getLastQueue(this); uiHandler.post { selectedAppsQueue.clear(); for (pkg in lastQueue) { if (pkg == PACKAGE_BLANK) { selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)) } else if (allRunning.contains(pkg)) { val appInfo = allAppsList.find { it.packageName == pkg }; if (appInfo != null) { appInfo.isMinimized = !visiblePackages.contains(pkg); selectedAppsQueue.add(appInfo) } } }; for (pkg in visiblePackages) { if (!lastQueue.contains(pkg)) { val appInfo = allAppsList.find { it.packageName == pkg }; if (appInfo != null) { appInfo.isMinimized = false; selectedAppsQueue.add(appInfo) } } }; sortAppQueue(); updateSelectedAppsDock(); drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); showToast("Instant Mode: Active") } } catch (e: Exception) { Log.e(TAG, "Error fetching apps", e) } }.start() }
    private fun selectLayout(opt: LayoutOption) { dismissKeyboardAndRestore(); selectedLayoutType = opt.type; activeCustomRects = opt.customRects; if (opt.type == LAYOUT_CUSTOM_DYNAMIC) { activeCustomLayoutName = opt.name; AppPreferences.saveLastCustomLayoutName(this, opt.name) } else { activeCustomLayoutName = null; AppPreferences.saveLastCustomLayoutName(this, null) }; AppPreferences.saveLastLayout(this, opt.type); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() }
    private fun saveCurrentAsCustom() { Thread { try { val rawLayouts = shellService!!.getWindowLayouts(currentDisplayId); if (rawLayouts.isEmpty()) { showToast("Found 0 active app windows"); return@Thread }; val rectStrings = mutableListOf<String>(); for (line in rawLayouts) { val parts = line.split("|"); if (parts.size == 2) { rectStrings.add(parts[1]) } }; if (rectStrings.isEmpty()) { showToast("Found 0 valid frames"); return@Thread }; val count = rectStrings.size; var baseName = "$count Apps - Custom"; val existingNames = AppPreferences.getCustomLayoutNames(this); var counter = 1; var finalName = "$baseName $counter"; while (existingNames.contains(finalName)) { counter++; finalName = "$baseName $counter" }; AppPreferences.saveCustomLayout(this, finalName, rectStrings.joinToString("|")); showToast("Saved: $finalName"); uiHandler.post { switchMode(MODE_LAYOUTS) } } catch (e: Exception) { Log.e(TAG, "Failed to save custom layout", e); showToast("Error saving: ${e.message}") } }.start() }
    
    private fun applyResolution(opt: ResolutionOption) { 
        dismissKeyboardAndRestore(); 
        if (opt.index != -1) { 
            selectedResolutionIndex = opt.index; 
            AppPreferences.saveDisplayResolution(this, currentDisplayId, opt.index)
        }
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        
        if (isInstantMode && opt.index != -1) { 
            Thread { 
                val resCmd = getResolutionCommand(selectedResolutionIndex)
                shellService?.runCommand(resCmd)
                Thread.sleep(1500)
                uiHandler.post { applyLayoutImmediate() } 
            }.start() 
        } 
    }
    
    private fun selectDpi(value: Int) { 
        currentDpiSetting = if (value == -1) -1 else value.coerceIn(50, 600)
        AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting)
        
        Thread { 
            try {
                if (currentDpiSetting == -1) {
                    shellService?.runCommand("wm density reset -d $currentDisplayId")
                } else {
                    val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"
                    shellService?.runCommand(dpiCmd)
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }.start() 
    }
    
    private fun changeFontSize(newSize: Float) { currentFontSize = newSize.coerceIn(10f, 30f); AppPreferences.saveFontSize(this, currentFontSize); updateGlobalFontSize(); if (currentMode == MODE_SETTINGS) { switchMode(MODE_SETTINGS) } }
    private fun changeDrawerHeight(delta: Int) { currentDrawerHeightPercent = (currentDrawerHeightPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerHeightPercent(this, currentDrawerHeightPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun changeDrawerWidth(delta: Int) { currentDrawerWidthPercent = (currentDrawerWidthPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerWidthPercent(this, currentDrawerWidthPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun pickIcon() { toggleDrawer(); try { refreshDisplayId(); val intent = Intent(this, IconPickerActivity::class.java); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); val metrics = windowManager.maximumWindowMetrics; val w = 1000; val h = (metrics.bounds.height() * 0.7).toInt(); val x = (metrics.bounds.width() - w) / 2; val y = (metrics.bounds.height() - h) / 2; val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(Rect(x, y, x+w, y+h)); startActivity(intent, options.toBundle()) } catch (e: Exception) { showToast("Error launching picker: ${e.message}") } }
    private fun saveProfile() { var name = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString()?.trim(); if (name.isNullOrEmpty()) { val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()); name = "Profile_$timestamp" }; val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveProfile(this, name, selectedLayoutType, selectedResolutionIndex, currentDpiSetting, pkgs); showToast("Saved: $name"); drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText(""); switchMode(MODE_PROFILES) }
    
    private fun loadProfile(name: String) { 
        val data = AppPreferences.getProfileData(this, name) ?: return; 
        try { 
            val parts = data.split("|"); 
            selectedLayoutType = parts[0].toInt(); 
            selectedResolutionIndex = parts[1].toInt(); 
            currentDpiSetting = parts[2].toInt(); 
            val pkgList = parts[3].split(","); 
            selectedAppsQueue.clear(); 
            for (pkg in pkgList) { 
                if (pkg.isNotEmpty()) { 
                    if (pkg == PACKAGE_BLANK) { 
                        selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)) 
                    } else { 
                        val app = allAppsList.find { it.packageName == pkg }; 
                        if (app != null) selectedAppsQueue.add(app) 
                    } 
                } 
            }; 
            AppPreferences.saveLastLayout(this, selectedLayoutType); 
            AppPreferences.saveDisplayResolution(this, currentDisplayId, selectedResolutionIndex); 
            AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); 
            activeProfileName = name; 
            updateSelectedAppsDock(); 
            showToast("Loaded: $name"); 
            drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); 
            if (isInstantMode) applyLayoutImmediate() 
        } catch (e: Exception) { 
            Log.e(TAG, "Failed to load profile", e) 
        } 
    }
    
    private fun executeLaunch(layoutType: Int, closeDrawer: Boolean) { 
        if (closeDrawer) toggleDrawer(); 
        refreshDisplayId(); 
        val pkgs = selectedAppsQueue.map { it.packageName }; 
        AppPreferences.saveLastQueue(this, pkgs); 
        
        Thread { 
            try { 
                val resCmd = getResolutionCommand(selectedResolutionIndex); 
                shellService?.runCommand(resCmd); 
                
                if (currentDpiSetting > 0) { 
                    val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; 
                    shellService?.runCommand(dpiCmd) 
                } else { 
                    if (currentDpiSetting == -1) shellService?.runCommand("wm density reset -d $currentDisplayId") 
                }; 
                
                Thread.sleep(800);

                // DYNAMICALLY CHECK RESOLUTION AFTER CHANGE
                val targetDim = getTargetDimensions(selectedResolutionIndex); 
                var w = 0
                var h = 0

                if (targetDim != null) {
                    w = targetDim.first
                    h = targetDim.second
                } else {
                    val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                    val display = dm.getDisplay(currentDisplayId)
                    if (display != null) {
                        val metrics = DisplayMetrics()
                        display.getRealMetrics(metrics)
                        w = metrics.widthPixels
                        h = metrics.heightPixels
                    } else {
                         val bounds = windowManager.maximumWindowMetrics.bounds
                         w = bounds.width()
                         h = bounds.height()
                    }
                }
                
                val rects = mutableListOf<Rect>(); 
                when (layoutType) { 
                    LAYOUT_FULL -> { rects.add(Rect(0, 0, w, h)) }
                    LAYOUT_SIDE_BY_SIDE -> { rects.add(Rect(0, 0, w/2, h)); rects.add(Rect(w/2, 0, w, h)) }
                    LAYOUT_TOP_BOTTOM -> { rects.add(Rect(0, 0, w, h/2)); rects.add(Rect(0, h/2, w, h)) }
                    LAYOUT_TRI_EVEN -> { val third = w / 3; rects.add(Rect(0, 0, third, h)); rects.add(Rect(third, 0, third * 2, h)); rects.add(Rect(third * 2, 0, w, h)) }
                    LAYOUT_CORNERS -> { rects.add(Rect(0, 0, w/2, h/2)); rects.add(Rect(w/2, 0, w, h/2)); rects.add(Rect(0, h/2, w/2, h)); rects.add(Rect(w/2, h/2, w, h)) }
                    LAYOUT_TRI_SIDE_MAIN_SIDE -> {
                        val quarter = w / 4
                        rects.add(Rect(0, 0, quarter, h))
                        rects.add(Rect(quarter, 0, quarter * 3, h))
                        rects.add(Rect(quarter * 3, 0, w, h))
                    }
                    LAYOUT_QUAD_ROW_EVEN -> {
                        val quarter = w / 4
                        rects.add(Rect(0, 0, quarter, h))
                        rects.add(Rect(quarter, 0, quarter * 2, h))
                        rects.add(Rect(quarter * 2, 0, quarter * 3, h))
                        rects.add(Rect(quarter * 3, 0, w, h))
                    }
                    LAYOUT_CUSTOM_DYNAMIC -> { if (activeCustomRects != null) { rects.addAll(activeCustomRects!!) } else { rects.add(Rect(0, 0, w/2, h)); rects.add(Rect(w/2, 0, w, h)) } } 
                } 

                if (selectedAppsQueue.isNotEmpty()) { 
                    val minimizedApps = selectedAppsQueue.filter { it.isMinimized }; 
                    for (app in minimizedApps) { 
                        if (app.packageName != PACKAGE_BLANK) { 
                            try { 
                                val tid = shellService?.getTaskId(app.packageName) ?: -1; 
                                if (tid != -1) shellService?.moveTaskToBack(tid) 
                            } catch (e: Exception) { 
                                Log.e(TAG, "Failed to minimize ${app.packageName}", e) 
                            } 
                        } 
                    }; 
                    val activeApps = selectedAppsQueue.filter { !it.isMinimized }; 
                    if (killAppOnExecute) { 
                        for (app in activeApps) { 
                            if (app.packageName != PACKAGE_BLANK) { 
                                shellService?.forceStop(app.packageName) 
                            } 
                        }; 
                        Thread.sleep(400) 
                    } else { 
                        Thread.sleep(100) 
                    }; 
                    val count = Math.min(activeApps.size, rects.size); 
                    for (i in 0 until count) { 
                        val pkg = activeApps[i].packageName; 
                        val bounds = rects[i]; 
                        if (pkg == PACKAGE_BLANK) continue; 
                        uiHandler.postDelayed({ launchViaApi(pkg, bounds) }, (i * 150).toLong()); 
                        uiHandler.postDelayed({ launchViaShell(pkg) }, (i * 150 + 50).toLong()); 
                        if (!killAppOnExecute) { 
                            uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {} }.start() }, (i * 150 + 150).toLong()) 
                        }; 
                        uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {} }.start() }, (i * 150 + 800).toLong()) 
                    }; 
                    if (closeDrawer) { 
                        uiHandler.post { selectedAppsQueue.clear(); updateSelectedAppsDock() } 
                    } 
                } 
            } catch (e: Exception) { 
                Log.e(TAG, "Execute Failed", e); 
                showToast("Execute Failed: ${e.message}") 
            } 
        }.start(); 
        drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText("") 
    }
    
    private fun calculateGCD(a: Int, b: Int): Int {
        return if (b == 0) a else calculateGCD(b, a % b)
    }

    private fun switchMode(mode: Int) {
        currentMode = mode
        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar)
        val searchIcon = drawerView!!.findViewById<ImageView>(R.id.icon_search_mode)
        val iconWin = drawerView!!.findViewById<ImageView>(R.id.icon_mode_window)
        val iconRes = drawerView!!.findViewById<ImageView>(R.id.icon_mode_resolution)
        val iconDpi = drawerView!!.findViewById<ImageView>(R.id.icon_mode_dpi)
        val iconProf = drawerView!!.findViewById<ImageView>(R.id.icon_mode_profiles)
        val iconSet = drawerView!!.findViewById<ImageView>(R.id.icon_mode_settings)
        val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        
        searchIcon.setColorFilter(if(mode==MODE_SEARCH) Color.WHITE else Color.GRAY)
        iconWin.setColorFilter(if(mode==MODE_LAYOUTS) Color.WHITE else Color.GRAY)
        iconRes.setColorFilter(if(mode==MODE_RESOLUTION) Color.WHITE else Color.GRAY)
        iconDpi.setColorFilter(if(mode==MODE_DPI) Color.WHITE else Color.GRAY)
        iconProf.setColorFilter(if(mode==MODE_PROFILES) Color.WHITE else Color.GRAY)
        iconSet.setColorFilter(if(mode==MODE_SETTINGS) Color.WHITE else Color.GRAY)

        executeBtn.visibility = if (isInstantMode) View.GONE else View.VISIBLE
        displayList.clear()
        val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler)
        dock.visibility = if (mode == MODE_SEARCH && selectedAppsQueue.isNotEmpty()) View.VISIBLE else View.GONE
        
        when (mode) {
            MODE_SEARCH -> { searchBar.hint = "Search apps..."; refreshSearchList() }
            MODE_LAYOUTS -> { 
                searchBar.hint = "Select Layout"; 
                displayList.add(ActionOption("Save Current Arrangement") { saveCurrentAsCustom() }); 
                displayList.add(LayoutOption("1 App - Full Screen", LAYOUT_FULL)); 
                displayList.add(LayoutOption("2 Apps - Side by Side", LAYOUT_SIDE_BY_SIDE)); 
                displayList.add(LayoutOption("2 Apps - Top & Bottom", LAYOUT_TOP_BOTTOM)); 
                displayList.add(LayoutOption("3 Apps - Even", LAYOUT_TRI_EVEN));
                displayList.add(LayoutOption("3 Apps - Side/Main/Side (25/50/25)", LAYOUT_TRI_SIDE_MAIN_SIDE));
                displayList.add(LayoutOption("4 Apps - Corners", LAYOUT_CORNERS));
                displayList.add(LayoutOption("4 Apps - Row (Even)", LAYOUT_QUAD_ROW_EVEN));
                
                val customNames = AppPreferences.getCustomLayoutNames(this).sorted(); 
                for (name in customNames) { val data = AppPreferences.getCustomLayoutData(this, name); if (data != null) { try { val rects = mutableListOf<Rect>(); val rectParts = data.split("|"); for (rp in rectParts) { val coords = rp.split(","); if (coords.size == 4) { rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt())) } }; displayList.add(LayoutOption(name, LAYOUT_CUSTOM_DYNAMIC, true, rects)) } catch(e: Exception) {} } } 
            }
            MODE_RESOLUTION -> {
                searchBar.hint = "Select Resolution"
                displayList.add(CustomResInputOption)
                
                val savedResNames = AppPreferences.getCustomResolutionNames(this).sorted()
                for (name in savedResNames) {
                    val value = AppPreferences.getCustomResolutionValue(this, name) ?: continue
                    displayList.add(ResolutionOption(name, "wm size  -d $currentDisplayId", 100 + savedResNames.indexOf(name)))
                }
                
                displayList.add(ResolutionOption("Default (Reset)", "wm size reset -d $currentDisplayId", 0))
                displayList.add(ResolutionOption("1:1 Square (1422x1500)", "wm size 1422x1500 -d $currentDisplayId", 1))
                displayList.add(ResolutionOption("16:9 Landscape (1920x1080)", "wm size 1920x1080 -d $currentDisplayId", 2))
                displayList.add(ResolutionOption("32:9 Ultrawide (3840x1080)", "wm size 3840x1080 -d $currentDisplayId", 3))
            }
            MODE_DPI -> { 
                searchBar.hint = "Adjust Density (DPI)"
                displayList.add(ActionOption("Reset Density (Default)") { selectDpi(-1) })
                var savedDpi = currentDpiSetting
                if (savedDpi <= 0) { savedDpi = displayContext?.resources?.configuration?.densityDpi ?: 160 }
                displayList.add(DpiOption(savedDpi)) 
            }
            MODE_PROFILES -> { searchBar.hint = "Enter Profile Name..."; displayList.add(ProfileOption("Save Current as New", true, 0,0,0, emptyList())); val profileNames = AppPreferences.getProfileNames(this).sorted(); for (pName in profileNames) { val data = AppPreferences.getProfileData(this, pName); if (data != null) { try { val parts = data.split("|"); val lay = parts[0].toInt(); val res = parts[1].toInt(); val d = parts[2].toInt(); val pkgs = parts[3].split(",").filter { it.isNotEmpty() }; displayList.add(ProfileOption(pName, false, lay, res, d, pkgs)) } catch(e: Exception) {} } } }
            MODE_SETTINGS -> {
                searchBar.hint = "Settings"
                displayList.add(ActionOption("Switch Display (Current $currentDisplayId)") { cycleDisplay() })
                displayList.add(ToggleOption("Virtual Display (1080p)", isVirtualDisplayActive) { toggleVirtualDisplay(it) })
                displayList.add(HeightOption(currentDrawerHeightPercent))
                displayList.add(WidthOption(currentDrawerWidthPercent))
                displayList.add(ToggleOption("Auto-Shrink for Keyboard", autoResizeEnabled) { autoResizeEnabled = it; AppPreferences.setAutoResizeKeyboard(this, it) })
                displayList.add(FontSizeOption(currentFontSize))
                displayList.add(IconOption("Launcher Icon (Tap to Change)"))
                displayList.add(ToggleOption("Instant Mode (Live Changes)", isInstantMode) { isInstantMode = it; AppPreferences.setInstantMode(this, it); executeBtn.visibility = if (it) View.GONE else View.VISIBLE; if (it) fetchRunningApps() })
                displayList.add(ToggleOption("Kill App on Execute", killAppOnExecute) { killAppOnExecute = it; AppPreferences.setKillOnExecute(this, it) })
                displayList.add(ToggleOption("Display Off (Touch on)", isExtinguished) { if (it) performExtinguish() else wakeUp() })
                
                displayList.add(ToggleOption("Shizuku Warning (Icon Alert)", showShizukuWarning) { 
                    showShizukuWarning = it
                    AppPreferences.setShowShizukuWarning(this, it)
                    updateBubbleIcon() 
                })
            }
        }
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    
    object CustomResInputOption
    
    data class LayoutOption(val name: String, val type: Int, val isCustomSaved: Boolean = false, val customRects: List<Rect>? = null)
    data class ResolutionOption(val name: String, val command: String, val index: Int)
    data class DpiOption(val currentDpi: Int)
    data class ProfileOption(val name: String, val isCurrent: Boolean, val layout: Int, val resIndex: Int, val dpi: Int, val apps: List<String>)
    data class FontSizeOption(val currentSize: Float)
    data class HeightOption(val currentPercent: Int)
    data class WidthOption(val currentPercent: Int)
    data class IconOption(val name: String)
    data class ActionOption(val name: String, val action: () -> Unit)
    data class ToggleOption(val name: String, var isEnabled: Boolean, val onToggle: (Boolean) -> Unit)

    inner class SelectedAppsAdapter : RecyclerView.Adapter<SelectedAppsAdapter.Holder>() {
        inner class Holder(v: View) : RecyclerView.ViewHolder(v) { val icon: ImageView = v.findViewById(R.id.selected_app_icon) }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder { return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_app, parent, false)) }
        override fun onBindViewHolder(holder: Holder, position: Int) { val app = selectedAppsQueue[position]; if (app.packageName == PACKAGE_BLANK) { holder.icon.setImageResource(R.drawable.ic_box_outline); holder.icon.alpha = 1.0f } else { try { holder.icon.setImageDrawable(packageManager.getApplicationIcon(app.packageName)) } catch (e: Exception) { holder.icon.setImageResource(R.drawable.ic_launcher_bubble) }; holder.icon.alpha = if (app.isMinimized) 0.4f else 1.0f }; holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); if (app.packageName != PACKAGE_BLANK) { app.isMinimized = !app.isMinimized; notifyItemChanged(position); if (isInstantMode) applyLayoutImmediate() } } }
        override fun getItemCount() = selectedAppsQueue.size
    }

    inner class RofiAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        inner class AppHolder(v: View) : RecyclerView.ViewHolder(v) { val icon: ImageView = v.findViewById(R.id.rofi_app_icon); val text: TextView = v.findViewById(R.id.rofi_app_text); val star: ImageView = v.findViewById(R.id.rofi_app_star) }
        inner class LayoutHolder(v: View) : RecyclerView.ViewHolder(v) { val nameInput: EditText = v.findViewById(R.id.layout_name); val btnSave: ImageView = v.findViewById(R.id.btn_save_profile); val btnExtinguish: ImageView = v.findViewById(R.id.btn_extinguish_item) }
        inner class DpiHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_dpi_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_dpi_plus); val input: EditText = v.findViewById(R.id.input_dpi_value) }
        inner class FontSizeHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_font_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_font_plus); val textVal: TextView = v.findViewById(R.id.text_font_value) }
        inner class HeightHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_height_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_height_plus); val textVal: TextView = v.findViewById(R.id.text_height_value) }
        inner class WidthHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_width_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_width_plus); val textVal: TextView = v.findViewById(R.id.text_width_value) }
        inner class ProfileRichHolder(v: View) : RecyclerView.ViewHolder(v) { val name: EditText = v.findViewById(R.id.profile_name_text); val details: TextView = v.findViewById(R.id.profile_details_text); val iconsContainer: LinearLayout = v.findViewById(R.id.profile_icons_container); val btnSave: ImageView = v.findViewById(R.id.btn_save_profile_rich) }
        inner class IconSettingHolder(v: View) : RecyclerView.ViewHolder(v) { val preview: ImageView = v.findViewById(R.id.icon_setting_preview) }
        inner class CustomResInputHolder(v: View) : RecyclerView.ViewHolder(v) { val inputW: EditText = v.findViewById(R.id.input_res_w); val inputH: EditText = v.findViewById(R.id.input_res_h); val btnSave: ImageView = v.findViewById(R.id.btn_save_res) }

        override fun getItemViewType(position: Int): Int { return when (displayList[position]) { is MainActivity.AppInfo -> 0; is LayoutOption -> 1; is ResolutionOption -> 1; is DpiOption -> 2; is ProfileOption -> 4; is FontSizeOption -> 3; is IconOption -> 5; is ToggleOption -> 1; is ActionOption -> 6; is HeightOption -> 7; is WidthOption -> 8; is CustomResInputOption -> 9; else -> 0 } }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder { return when (viewType) { 0 -> AppHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_app_rofi, parent, false)); 1 -> LayoutHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false)); 2 -> DpiHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dpi_custom, parent, false)); 3 -> FontSizeHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_font_size, parent, false)); 4 -> ProfileRichHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_rich, parent, false)); 5 -> IconSettingHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_icon_setting, parent, false)); 6 -> LayoutHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false)); 7 -> HeightHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_height_setting, parent, false)); 8 -> WidthHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_width_setting, parent, false)); 9 -> CustomResInputHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_custom_resolution, parent, false)); else -> AppHolder(View(parent.context)) } }

        private fun startRename(editText: EditText) { editText.isEnabled = true; editText.isFocusable = true; editText.isFocusableInTouchMode = true; editText.requestFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT) }
        private fun endRename(editText: EditText) { editText.isFocusable = false; editText.isFocusableInTouchMode = false; editText.isEnabled = false; val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(editText.windowToken, 0) }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = displayList[position]
            if (holder is AppHolder) holder.text.textSize = currentFontSize
            if (holder is LayoutHolder) holder.nameInput.textSize = currentFontSize
            if (holder is ProfileRichHolder) holder.name.textSize = currentFontSize

            if (holder is AppHolder && item is MainActivity.AppInfo) { holder.text.text = item.label; if (item.packageName == PACKAGE_BLANK) { holder.icon.setImageResource(R.drawable.ic_box_outline) } else { try { holder.icon.setImageDrawable(packageManager.getApplicationIcon(item.packageName)) } catch (e: Exception) { holder.icon.setImageResource(R.drawable.ic_launcher_bubble) } }; val isSelected = selectedAppsQueue.any { it.packageName == item.packageName }; if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.star.visibility = if (item.isFavorite) View.VISIBLE else View.GONE; holder.itemView.setOnClickListener { addToSelection(item) }; holder.itemView.setOnLongClickListener { toggleFavorite(item); refreshSearchList(); true } }
            else if (holder is ProfileRichHolder && item is ProfileOption) { holder.name.setText(item.name); holder.iconsContainer.removeAllViews(); if (!item.isCurrent) { for (pkg in item.apps.take(5)) { val iv = ImageView(holder.itemView.context); val lp = LinearLayout.LayoutParams(60, 60); lp.marginEnd = 8; iv.layoutParams = lp; if (pkg == PACKAGE_BLANK) { iv.setImageResource(R.drawable.ic_box_outline) } else { try { iv.setImageDrawable(packageManager.getApplicationIcon(pkg)) } catch (e: Exception) { iv.setImageResource(R.drawable.ic_launcher_bubble) } }; holder.iconsContainer.addView(iv) }; val info = "${getLayoutName(item.layout)} | ${getRatioName(item.resIndex)} | ${item.dpi}dpi"; holder.details.text = info; holder.details.visibility = View.VISIBLE; holder.btnSave.visibility = View.GONE; if (activeProfileName == item.name) { holder.itemView.setBackgroundResource(R.drawable.bg_item_active) } else { holder.itemView.setBackgroundResource(0) }; holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); loadProfile(item.name) }; holder.itemView.setOnLongClickListener { startRename(holder.name); true }; val saveProfileName = { val newName = holder.name.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameProfile(holder.itemView.context, item.name, newName)) { showToast("Renamed to $newName"); switchMode(MODE_PROFILES) } }; endRename(holder.name) }; holder.name.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveProfileName(); holder.name.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(holder.name.windowToken, 0); updateDrawerHeight(false); true } else false }; holder.name.setOnFocusChangeListener { v, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus); if (!hasFocus) saveProfileName() } } else { holder.iconsContainer.removeAllViews(); holder.details.visibility = View.GONE; holder.btnSave.visibility = View.VISIBLE; holder.itemView.setBackgroundResource(0); holder.name.isEnabled = true; holder.name.isFocusable = true; holder.name.isFocusableInTouchMode = true; holder.itemView.setOnClickListener { saveProfile() }; holder.btnSave.setOnClickListener { saveProfile() } } }
            else if (holder is LayoutHolder) {
                holder.btnSave.visibility = View.GONE; holder.btnExtinguish.visibility = View.GONE
                if (item is LayoutOption) { holder.nameInput.setText(item.name); val isSelected = if (item.type == LAYOUT_CUSTOM_DYNAMIC) { item.type == selectedLayoutType && item.name == activeCustomLayoutName } else { item.type == selectedLayoutType && activeCustomLayoutName == null }; if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { selectLayout(item) }; if (item.isCustomSaved) { holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }; val saveLayoutName = { val newName = holder.nameInput.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameCustomLayout(holder.itemView.context, item.name, newName)) { showToast("Renamed to $newName"); if (activeCustomLayoutName == item.name) { activeCustomLayoutName = newName; AppPreferences.saveLastCustomLayoutName(holder.itemView.context, newName) }; switchMode(MODE_LAYOUTS) } }; endRename(holder.nameInput) }; holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveLayoutName(); true } else false }; holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveLayoutName() } } else { holder.nameInput.isEnabled = false; holder.nameInput.isFocusable = false; holder.nameInput.setTextColor(Color.WHITE) } }
                else if (item is ResolutionOption) { 
                    holder.nameInput.setText(item.name)
                    
                    if (item.index >= 100) {
                        holder.nameInput.isEnabled = false 
                        holder.nameInput.setTextColor(Color.WHITE)
                        holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }
                        
                        val saveResName = {
                            val newName = holder.nameInput.text.toString().trim()
                            if (newName.isNotEmpty() && newName != item.name) {
                                if (AppPreferences.renameCustomResolution(holder.itemView.context, item.name, newName)) {
                                    showToast("Renamed to $newName")
                                    switchMode(MODE_RESOLUTION)
                                }
                            }
                            endRename(holder.nameInput)
                        }
                        holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveResName(); true } else false }
                        holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveResName() }
                    } else {
                        holder.nameInput.isEnabled = false
                        holder.nameInput.isFocusable = false
                        holder.nameInput.setTextColor(Color.WHITE)
                    }

                    val isSelected = (item.index == selectedResolutionIndex)
                    if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                    holder.itemView.setOnClickListener { applyResolution(item) } 
                }
                else if (item is IconOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { pickIcon() } }
                else if (item is ToggleOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); if (item.isEnabled) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.isEnabled = !item.isEnabled; item.onToggle(item.isEnabled); notifyItemChanged(position) } } 
                else if (item is ActionOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.action() } }
            }
            else if (holder is CustomResInputHolder) {
                holder.btnSave.setOnClickListener {
                    val wStr = holder.inputW.text.toString().trim()
                    val hStr = holder.inputH.text.toString().trim()
                    if (wStr.isNotEmpty() && hStr.isNotEmpty()) {
                        val w = wStr.toIntOrNull()
                        val h = hStr.toIntOrNull()
                        
                        if (w != null && h != null && w > 0 && h > 0) {
                            val gcdVal = calculateGCD(w, h)
                            val wRatio = w / gcdVal
                            val hRatio = h / gcdVal
                            
                            val resString = "${w}x${h}"
                            val name = "$wRatio:$hRatio Custom ($resString)"
                            
                            AppPreferences.saveCustomResolution(holder.itemView.context, name, resString)
                            showToast("Added $name")
                            dismissKeyboardAndRestore()
                            switchMode(MODE_RESOLUTION)
                        } else {
                             showToast("Invalid numbers")
                        }
                    } else {
                        showToast("Input W and H")
                    }
                }
                holder.inputW.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }
                holder.inputH.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }
            }
            else if (holder is IconSettingHolder && item is IconOption) { try { val uriStr = AppPreferences.getIconUri(holder.itemView.context); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); holder.preview.setImageBitmap(bitmap) } else { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) } } catch(e: Exception) { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) }; holder.itemView.setOnClickListener { pickIcon() } }
            
            else if (holder is DpiHolder && item is DpiOption) { 
                holder.input.setText(item.currentDpi.toString()); 
                
                // Handle Manual Entry (Enter Key)
                holder.input.setOnEditorActionListener { v, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        val valInt = v.text.toString().toIntOrNull()
                        if (valInt != null) {
                            selectDpi(valInt)
                            showToast("DPI set to $valInt")
                        }
                        dismissKeyboardAndRestore()
                        true
                    } else false
                }

                // Handle Manual Entry (Focus Loss)
                holder.input.setOnFocusChangeListener { _, hasFocus ->
                    if (autoResizeEnabled) updateDrawerHeight(hasFocus)
                    if (!hasFocus) {
                        val valInt = holder.input.text.toString().toIntOrNull()
                        if (valInt != null && valInt != item.currentDpi) {
                            selectDpi(valInt)
                        }
                    }
                }

                holder.btnMinus.setOnClickListener { 
                    val v = holder.input.text.toString().toIntOrNull() ?: 160; 
                    val newVal = (v - 5).coerceAtLeast(50); 
                    holder.input.setText(newVal.toString()); 
                    selectDpi(newVal) 
                }; 
                
                holder.btnPlus.setOnClickListener { 
                    val v = holder.input.text.toString().toIntOrNull() ?: 160; 
                    val newVal = (v + 5).coerceAtMost(600); 
                    holder.input.setText(newVal.toString()); 
                    selectDpi(newVal) 
                } 
            }
            else if (holder is FontSizeHolder && item is FontSizeOption) { holder.textVal.text = item.currentSize.toInt().toString(); holder.btnMinus.setOnClickListener { changeFontSize(item.currentSize - 1) }; holder.btnPlus.setOnClickListener { changeFontSize(item.currentSize + 1) } }
            else if (holder is HeightHolder && item is HeightOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerHeight(-5) }; holder.btnPlus.setOnClickListener { changeDrawerHeight(5) } }
            else if (holder is WidthHolder && item is WidthOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerWidth(-5) }; holder.btnPlus.setOnClickListener { changeDrawerWidth(5) } }
        }
        override fun getItemCount() = displayList.size
    }
}
```

## File: app/src/main/res/drawable/bg_item_active.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#00A0E9"/>
    <corners android:radius="8dp"/>
</shape>
```

## File: app/src/main/res/drawable/bg_item_press.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true">
        <shape android:shape="rectangle">
            <solid android:color="#44FFFFFF"/>
            <corners android:radius="8dp"/>
        </shape>
    </item>
    <item>
        <shape android:shape="rectangle">
            <solid android:color="#00000000"/>
        </shape>
    </item>
</selector>
```

## File: app/src/main/res/drawable/ic_cursor.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#000000"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="1"
        android:pathData="M7,2L19,13L12,13L11,20L7,2Z" />
</vector>
```

## File: app/src/main/res/drawable/ic_launcher_background.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#3DDC84"
        android:pathData="M0,0h108v108h-108z" />
    <path
        android:fillColor="#00000000"
        android:pathData="M9,0L9,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,0L19,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M29,0L29,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M39,0L39,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M49,0L49,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M59,0L59,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M69,0L69,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M79,0L79,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M89,0L89,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M99,0L99,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,9L108,9"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,19L108,19"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,29L108,29"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,39L108,39"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,49L108,49"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,59L108,59"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,69L108,69"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,79L108,79"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,89L108,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M0,99L108,99"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,29L89,29"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,39L89,39"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,49L89,49"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,59L89,59"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,69L89,69"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M19,79L89,79"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M29,19L29,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M39,19L39,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M49,19L49,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M59,19L59,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M69,19L69,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
    <path
        android:fillColor="#00000000"
        android:pathData="M79,19L79,89"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
</vector>
```

## File: app/src/main/res/drawable/ic_launcher_foreground.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path android:pathData="M31,63.928c0,0 6.4,-11 12.1,-13.1c7.2,-2.6 26,-1.4 26,-1.4l38.1,38.1L107,108.928l-32,-1L31,63.928z">
        <aapt:attr name="android:fillColor">
            <gradient
                android:endX="85.84757"
                android:endY="92.4963"
                android:startX="42.9492"
                android:startY="49.59793"
                android:type="linear">
                <item
                    android:color="#44000000"
                    android:offset="0.0" />
                <item
                    android:color="#00000000"
                    android:offset="1.0" />
            </gradient>
        </aapt:attr>
    </path>
    <path
        android:fillColor="#FFFFFF"
        android:fillType="nonZero"
        android:pathData="M65.3,45.828l3.8,-6.6c0.2,-0.4 0.1,-0.9 -0.3,-1.1c-0.4,-0.2 -0.9,-0.1 -1.1,0.3l-3.9,6.7c-6.3,-2.8 -13.4,-2.8 -19.7,0l-3.9,-6.7c-0.2,-0.4 -0.7,-0.5 -1.1,-0.3C38.8,38.328 38.7,38.828 38.9,39.228l3.8,6.6C36.2,49.428 31.7,56.028 31,63.928h46C76.3,56.028 71.8,49.428 65.3,45.828zM43.4,57.328c-0.8,0 -1.5,-0.5 -1.8,-1.2c-0.3,-0.7 -0.1,-1.5 0.4,-2.1c0.5,-0.5 1.4,-0.7 2.1,-0.4c0.7,0.3 1.2,1 1.2,1.8C45.3,56.528 44.5,57.328 43.4,57.328L43.4,57.328zM64.6,57.328c-0.8,0 -1.5,-0.5 -1.8,-1.2s-0.1,-1.5 0.4,-2.1c0.5,-0.5 1.4,-0.7 2.1,-0.4c0.7,0.3 1.2,1 1.2,1.8C66.5,56.528 65.6,57.328 64.6,57.328L64.6,57.328z"
        android:strokeWidth="1"
        android:strokeColor="#00000000" />
</vector>
```

## File: app/src/main/res/drawable/ic_lock_closed.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#FFFFFF">
  <path
      android:fillColor="#FFFFFFFF"
      android:pathData="M18,8h-1V6c0,-2.76 -2.24,-5 -5,-5S7,3.24 7,6v2H6c-1.1,0 -2,0.9 -2,2v10c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V10C20,8.9 19.1,8 18,8zM12,17c-1.1,0 -2,-0.9 -2,-2c0,-1.1 0.9,-2 2,-2s2,0.9 2,2C14,16.1 13.1,17 12,17zM9,8V6c0,-1.66 1.34,-3 3,-3s3,1.34 3,3v2H9z"/>
</vector>
```

## File: app/src/main/res/drawable/ic_lock_open.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#FFFFFF">
  <path
      android:fillColor="#FFFFFFFF"
      android:pathData="M12,17c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2s-2,0.9 -2,2S10.9,17 12,17zM18,8h-1V6c0,-2.76 -2.24,-5 -5,-5S7,3.24 7,6h1.9c0,-1.71 1.39,-3.1 3.1,-3.1c1.71,0 3.1,1.39 3.1,3.1v2H6c-1.1,0 -2,0.9 -2,2v10c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V10C20,8.9 19.1,8 18,8z"/>
</vector>
```

## File: app/src/main/res/drawable/ic_trackpad_foreground_scaled.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/ic_trackpad_logo"
    android:insetLeft="15dp"
    android:insetRight="15dp"
    android:insetTop="15dp"
    android:insetBottom="15dp" />
```

## File: app/src/main/res/drawable/red_border.xml
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <stroke
        android:width="4dp"
        android:color="#80FF0000" />
    <solid android:color="#00000000" />
</shape>
```

## File: app/src/main/res/layout/activity_keyboard.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="bottom"
    android:background="#00000000">

    <View
        android:id="@+id/view_touch_dismiss"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="#00000000" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:background="#222222"
        android:padding="12dp">

        <TextView
            android:id="@+id/tv_status"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Remote Input Active"
            android:textColor="#AAAAAA"
            android:textSize="12sp"
            android:layout_marginBottom="8dp"/>

        <EditText
            android:id="@+id/et_remote_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#333333"
            android:padding="12dp"
            android:textColor="#FFFFFF"
            android:hint="Type here..."
            android:textColorHint="#888888"
            android:inputType="textNoSuggestions|textVisiblePassword"
            android:imeOptions="flagNoExtractUi"
            android:minHeight="48dp"/>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginTop="8dp">

            <Button
                android:id="@+id/btn_clear"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Clear"
                android:backgroundTint="#FF9800"
                android:layout_marginEnd="4dp"/>

            <Button
                android:id="@+id/btn_close"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Close"
                android:backgroundTint="#990000"
                android:layout_marginStart="4dp"/>
        </LinearLayout>
    </LinearLayout>
</LinearLayout>
```

## File: app/src/main/res/layout/activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#121212"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:gravity="center"
        android:padding="24dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="CoverScreen Trackpad"
            android:textSize="24sp"
            android:textStyle="bold"
            android:textColor="#FFFFFF"
            android:layout_marginBottom="24dp"/>

        <TextView
            android:id="@+id/text_status"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Checking Shizuku..."
            android:textColor="#AAAAAA"
            android:layout_marginBottom="16dp"/>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginBottom="8dp">
            
            <Button
                android:id="@+id/btn_toggle"
                android:layout_width="0dp"
                android:layout_height="50dp"
                android:layout_weight="1"
                android:text="Start Trackpad"
                android:backgroundTint="#3DDC84"
                android:textColor="#000000"
                android:textStyle="bold"
                android:layout_marginEnd="4dp"/>
                
            <Button
                android:id="@+id/btn_hide_app"
                android:layout_width="0dp"
                android:layout_height="50dp"
                android:layout_weight="1"
                android:text="Hide App"
                android:backgroundTint="#607D8B"
                android:textColor="#FFFFFF"
                android:textStyle="bold"
                android:layout_marginStart="4dp"/>
        </LinearLayout>

        <Button
            android:id="@+id/btn_lock"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:text="Position: Unlocked"
            android:drawableStart="@drawable/ic_lock_open"
            android:drawablePadding="10dp"
            android:paddingStart="20dp"
            android:paddingEnd="20dp"
            android:backgroundTint="#3DDC84"
            android:textColor="#000000"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_target_switch"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:text="Target: Switch Local/Remote"
            android:backgroundTint="#6200EE"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>
            
        <Button
            android:id="@+id/btn_reset_cursor"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:text="Reset Cursor Position"
            android:backgroundTint="#D32F2F"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_force_keyboard"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:text="Toggle Custom Keyboard"
            android:backgroundTint="#9C27B0"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_profiles"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:text="Layout Profiles (Auto-Resize)"
            android:backgroundTint="#FF9800"
            android:textColor="#000000"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_manual_adjust"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:text="Manual Adjustment (Fine Tune)"
            android:backgroundTint="#00ACC1"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_settings"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:text="Configuration / Settings"
            android:backgroundTint="#444444"
            android:textColor="#FFFFFF"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btn_help"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:text="Help / Instructions"
            android:backgroundTint="#0066CC"
            android:textColor="#FFFFFF"
            android:layout_marginBottom="24dp"/>

        <Button
            android:id="@+id/btn_close"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:text="Close App"
            android:backgroundTint="#990000"
            android:textColor="#FFFFFF"/>

    </LinearLayout>
</ScrollView>
```

## File: app/src/main/res/layout/activity_manual_adjust.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:background="#121212"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Manual Adjustment"
        android:textColor="#FFFFFF"
        android:textSize="20sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp"/>

    <TextView
        android:id="@+id/text_mode"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="CURRENT MODE: MOVE"
        android:textColor="#3DDC84"
        android:textSize="16sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center"
        android:layout_marginBottom="24dp">

        <Button
            android:id="@+id/btn_toggle_mode"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Switch Mode"
            android:backgroundTint="#444444"
            android:textColor="#FFFFFF"
            android:layout_marginEnd="8dp"/>

        <Button
            android:id="@+id/btn_rotate"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Rotate 90°"
            android:backgroundTint="#333333"
            android:textColor="#FFFFFF"
            android:layout_marginStart="8dp"/>
    </LinearLayout>

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:gravity="center">

        <Button
            android:id="@+id/btn_up"
            android:layout_width="80dp"
            android:layout_height="60dp"
            android:text="▲"
            android:textSize="20sp"
            android:backgroundTint="#333333"
            android:textColor="#FFFFFF"
            android:layout_marginBottom="4dp"/>

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <Button
                android:id="@+id/btn_left"
                android:layout_width="80dp"
                android:layout_height="60dp"
                android:text="◄"
                android:textSize="20sp"
                android:backgroundTint="#333333"
                android:textColor="#FFFFFF"
                android:layout_marginEnd="4dp"/>

            <Button
                android:id="@+id/btn_center"
                android:layout_width="80dp"
                android:layout_height="60dp"
                android:text="⟲"
                android:textSize="24sp"
                android:backgroundTint="#555555"
                android:textColor="#FFFFFF"
                android:tooltipText="Reset to Center"/>

            <Button
                android:id="@+id/btn_right"
                android:layout_width="80dp"
                android:layout_height="60dp"
                android:text="►"
                android:textSize="20sp"
                android:backgroundTint="#333333"
                android:textColor="#FFFFFF"
                android:layout_marginStart="4dp"/>
        </LinearLayout>

        <Button
            android:id="@+id/btn_down"
            android:layout_width="80dp"
            android:layout_height="60dp"
            android:text="▼"
            android:textSize="20sp"
            android:backgroundTint="#333333"
            android:textColor="#FFFFFF"
            android:layout_marginTop="4dp"/>

    </LinearLayout>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Center Button resets position/size"
        android:textColor="#666666"
        android:textSize="12sp"
        android:layout_marginTop="16dp"/>

    <View
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>

    <Button
        android:id="@+id/btn_back"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Back to Main Menu"
        android:backgroundTint="#3DDC84"
        android:textColor="#000000"
        android:textStyle="bold"/>

</LinearLayout>
```

## File: app/src/main/res/layout/activity_menu.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/statusText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Checking Shizuku..."
        android:layout_marginBottom="24dp"
        android:textSize="16sp" />

    <Button
        android:id="@+id/btnQuadrant"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="4-App Quadrant"
        android:enabled="false" />

    <Button
        android:id="@+id/btnSplit"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:text="2-App Split"
        android:enabled="false" />

</LinearLayout>
```

## File: app/src/main/res/layout/activity_profiles.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#121212"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="24dp">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Layout Profiles"
            android:textColor="#FFFFFF"
            android:textSize="24sp"
            android:textStyle="bold"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="The app automatically detects screen changes (Cover Screen vs AR Glasses) and loads the matching layout."
            android:textColor="#AAAAAA"
            android:layout_marginBottom="24dp"/>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:background="#222222"
            android:padding="16dp"
            android:layout_marginBottom="24dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="CURRENT SCREEN"
                android:textColor="#FF9800"
                android:textStyle="bold"
                android:layout_marginBottom="8dp"/>

            <TextView
                android:id="@+id/tvCurrentStats"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Ratio: 1.0"
                android:textColor="#FFFFFF"
                android:fontFamily="monospace"
                android:layout_marginBottom="16dp"/>

            <Button
                android:id="@+id/btnSaveCurrent"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Save Current Position"
                android:backgroundTint="#3DDC84"
                android:textColor="#000000"
                android:textStyle="bold"
                android:layout_marginBottom="8dp"/>
                
            <Button
                android:id="@+id/btnResetCurrent"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Delete Profile (Reset)"
                android:backgroundTint="#990000"
                android:textColor="#FFFFFF"/>
        </LinearLayout>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="ALL SAVED PROFILES"
            android:textColor="#AAAAAA"
            android:layout_marginBottom="8dp"/>
            
        <TextView
            android:id="@+id/tvProfileList"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="..."
            android:textColor="#FFFFFF"
            android:fontFamily="monospace"
            android:layout_marginBottom="24dp"/>

        <Button
            android:id="@+id/btnClose"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Back"
            android:backgroundTint="#444444"
            android:layout_marginBottom="40dp"/>

    </LinearLayout>
</ScrollView>
```

## File: app/src/main/res/layout/activity_settings.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#121212"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="20dp">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Configuration"
            android:textColor="#FFFFFF"
            android:textSize="24sp"
            android:textStyle="bold"
            android:layout_marginBottom="20dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="--- Input Speed ---"
            android:textColor="#AAAAAA"
            android:layout_gravity="center"
            android:layout_marginBottom="10dp"/>

        <TextView
            android:id="@+id/tvCursorSpeed"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Cursor Speed: 2.5"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarCursorSpeed"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="100"
            android:progress="25"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:id="@+id/tvScrollSpeed"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Scroll Speed: 3.0"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarScrollSpeed"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="100"
            android:progress="30"
            android:layout_marginBottom="24dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="--- Behavior ---"
            android:textColor="#AAAAAA"
            android:layout_gravity="center"
            android:layout_marginBottom="10dp"/>

        <Switch
            android:id="@+id/switchVibrate"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Haptic Feedback"
            android:textColor="#FFFFFF"
            android:textSize="16sp"
            android:minHeight="48dp"/>

        <Switch
            android:id="@+id/switchReverseScroll"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Natural Scrolling (Reverse)"
            android:textColor="#FFFFFF"
            android:textSize="16sp"
            android:minHeight="48dp"/>

        <Switch
            android:id="@+id/switchVPosLeft"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Vertical Scroll on Left"
            android:textColor="#FFFFFF"
            android:textSize="16sp"
            android:minHeight="48dp"/>

        <Switch
            android:id="@+id/switchHPosTop"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Horizontal Scroll on Top"
            android:textColor="#FFFFFF"
            android:textSize="16sp"
            android:minHeight="48dp"
            android:layout_marginBottom="24dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="--- Visuals ---"
            android:textColor="#AAAAAA"
            android:layout_gravity="center"
            android:layout_marginBottom="10dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Cursor Size"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarCursorSize"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="150"
            android:progress="50"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Border Visibility (Alpha)"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarAlpha"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="255"
            android:progress="200"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Handle Icon Size"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarHandleSize"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="100"
            android:progress="60"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Scroll Bar Thickness (Visual)"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarScrollVisual"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="20"
            android:progress="4"
            android:layout_marginBottom="24dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="--- Touch Areas ---"
            android:textColor="#AAAAAA"
            android:layout_gravity="center"
            android:layout_marginBottom="10dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Corner Handle Touch Area"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarHandleTouch"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="150"
            android:progress="60"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Scroll Bar Touch Width"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarScrollTouch"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="150"
            android:progress="60"
            android:layout_marginBottom="24dp"/>

        <Button
            android:id="@+id/btnSave"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Save &amp; Apply"
            android:backgroundTint="#3DDC84"
            android:textColor="#000000"
            android:textStyle="bold"
            android:layout_marginBottom="12dp"/>

        <Button
            android:id="@+id/btnBack"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Back to Main Menu"
            android:backgroundTint="#444444"
            android:textColor="#FFFFFF"/>

    </LinearLayout>
</ScrollView>
```

## File: app/src/main/res/layout/item_dpi_custom.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="8dp"
    android:gravity="center_vertical"
    android:background="@drawable/bg_item_press">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="DPI:"
        android:textColor="#888888"
        android:textSize="14sp"
        android:paddingEnd="8dp"/>

    <EditText
        android:id="@+id/input_dpi_value"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:inputType="number"
        android:maxLength="3"
        android:text="120"
        android:textColor="#FFFFFF"
        android:textSize="18sp"
        android:imeOptions="actionDone"
        android:background="@null"/>

    <ImageView
        android:id="@+id/btn_dpi_minus"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:padding="10dp"
        android:src="@android:drawable/ic_input_delete"
        android:tint="#FF5555"
        android:background="?attr/selectableItemBackgroundBorderless"/>

    <ImageView
        android:id="@+id/btn_dpi_plus"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:padding="10dp"
        android:src="@android:drawable/ic_input_add"
        android:tint="#3DDC84"
        android:background="?attr/selectableItemBackgroundBorderless"/>

</LinearLayout>
```

## File: app/src/main/res/layout/layout_trackpad.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#99000000">

    <View
        android:id="@+id/touchArea"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:clickable="true"
        android:focusable="true" />

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp"
        android:layout_gravity="top|start"
        android:background="#CC000000"
        android:layout_margin="16dp"
        android:elevation="10dp">

        <TextView
            android:id="@+id/statusText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Target: Local"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:layout_marginBottom="8dp" />

        <Button
            android:id="@+id/btnSwitchTarget"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Switch Display"
            android:textSize="12sp"
            android:padding="8dp" />

    </LinearLayout>
</FrameLayout>
```

## File: app/src/main/res/layout/service_overlay.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#20000000">

    <View
        android:id="@+id/view_trackpad"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:clickable="true"
        android:focusable="true" />

    <Button
        android:id="@+id/btn_close"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="top|end"
        android:layout_margin="16dp"
        android:backgroundTint="#FF0000"
        android:text="EXIT"
        android:textColor="#FFFFFF" />

</FrameLayout>
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_trackpad_adaptive.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@android:color/black" />
    <foreground android:drawable="@drawable/ic_trackpad_foreground_scaled" />
</adaptive-icon>
```

## File: app/src/main/res/values/colors.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
```

## File: app/src/main/res/values/strings.xml
```xml
<resources>
    <string name="app_name">DroidOS Trackpad</string>
    <string name="accessibility_service_description">DroidOS Input Service</string>
</resources>
```

## File: app/src/main/res/values/themes.xml
```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Base.Theme.CoverScreenTester" parent="Theme.Material3.DayNight.NoActionBar">
    </style>

    <style name="Theme.CoverScreenTester" parent="Base.Theme.CoverScreenTester" />
</resources>
```

## File: app/src/main/res/values-night/themes.xml
```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Base.Theme.CoverScreenTester" parent="Theme.Material3.DayNight.NoActionBar">
        </style>
</resources>
```

## File: app/src/main/res/xml/accessibility_service_config.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:description="@string/accessibility_service_description"
    android:accessibilityEventTypes="typeAllMask"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:notificationTimeout="100"
    android:canRetrieveWindowContent="false"
    android:canPerformGestures="false" 
    android:canRequestFilterKeyEvents="true" 
    android:accessibilityFlags="flagRequestTouchExplorationMode|flagRequestFilterKeyEvents" />
```

## File: app/src/main/res/xml/backup_rules.xml
```xml
<?xml version="1.0" encoding="utf-8"?><full-backup-content>
    </full-backup-content>
```

## File: app/src/main/res/xml/data_extraction_rules.xml
```xml
<?xml version="1.0" encoding="utf-8"?><data-extraction-rules>
    <cloud-backup>
        </cloud-backup>
    </data-extraction-rules>
```

## File: app/src/main/AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <queries>
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
    </queries>

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_trackpad_adaptive"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_trackpad_adaptive"
        android:supportsRtl="true"
        android:theme="@style/Theme.CoverScreenTester"
        android:resizeableActivity="true"> 
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize|screenLayout|density|smallestScreenSize"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            
            <meta-data android:name="android.max_aspect" android:value="4.0" />
        </activity>

        <activity 
            android:name=".SettingsActivity"
            android:exported="false" 
            android:theme="@style/Theme.CoverScreenTester" />

        <activity 
            android:name=".ProfilesActivity"
            android:exported="false" 
            android:theme="@style/Theme.CoverScreenTester" />

        <activity 
            android:name=".ManualAdjustActivity"
            android:exported="false" 
            android:theme="@style/Theme.CoverScreenTester" />

        <service
            android:name=".OverlayService"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
            android:exported="true">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
                <action android:name="PREVIEW_UPDATE" />
                <action android:name="RESET_POSITION" />
                <action android:name="ROTATE" />
                <action android:name="SAVE_LAYOUT" />
                <action android:name="LOAD_LAYOUT" />
                <action android:name="RELOAD_PREFS" />
                <action android:name="DELETE_PROFILE" />
                <action android:name="MANUAL_ADJUST" /> </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/accessibility_service_config" />
        </service>

        <provider
            android:name="rikka.shizuku.ShizukuProvider"
            android:authorities="${applicationId}.shizuku"
            android:enabled="true"
            android:exported="true"
            android:multiprocess="false" />

    </application>

</manifest>
```

## File: app/src/test/java/com/example/coverscreentester/ExampleUnitTest.kt
```kotlin
package com.example.coverscreentester

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}
```

## File: app/.gitignore
```
/build
```

## File: app/build.gradle.kts
```
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.coverscreentester"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSTrackpadKeyboard"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        aidl = true
    }

    sourceSets {
        getByName("main") {
            aidl.srcDirs(listOf("src/main/aidl"))
            java.srcDirs(layout.buildDirectory.dir("generated/source/aidl/debug"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")
    implementation("dev.rikka.shizuku:aidl:13.1.5")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

## File: app/proguard-rules.pro
```
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
```

## File: gradle/wrapper/gradle-wrapper.properties
```
#Fri Nov 21 07:45:00 EST 2025
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.13-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

## File: gradle/libs.versions.toml
```toml
[versions]
agp = "8.13.1"
kotlin = "1.9.23"
coreKtx = "1.13.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
appcompat = "1.6.1"
material = "1.11.0"
activity = "1.9.0"
constraintlayout = "2.1.4"
shizuku = "13.1.5" # <--- REVERTED TO LATEST VERSION

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-activity = { group = "androidx.activity", name = "activity", version.ref = "activity" }
androidx-constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }
shizuku-api = { group = "dev.rikka.shizuku", name = "api", version.ref = "shizuku" }
shizuku-provider = { group = "dev.rikka.shizuku", name = "provider", version.ref = "shizuku" }
shizuku-aidl = { group = "dev.rikka.shizuku", name = "aidl", version.ref = "shizuku" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```

## File: .gitignore
```
*.iml
.gradle
/local.properties
/.idea/caches
/.idea/libraries
/.idea/modules.xml
/.idea/workspace.xml
/.idea/navEditor.xml
/.idea/assetWizardSettings.xml
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties
```

## File: build.gradle.kts
```
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
```

## File: crash_log.txt
```
11-28 13:41:08.240 14842 14842 E AndroidRuntime: FATAL EXCEPTION: main
11-28 13:41:08.240 14842 14842 E AndroidRuntime: Process: com.example.com.katsuyamaki.coverscreenlauncher, PID: 14842
11-28 13:41:08.240 14842 14842 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.com.katsuyamaki.coverscreenlauncher/com.example.quadrantlauncher.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at com.example.quadrantlauncher.MainActivity.onCreate(MainActivity.kt:35)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 13:41:08.240 14842 14842 E AndroidRuntime: 	... 13 more
11-28 13:41:09.350  4765  4765 E AndroidRuntime: FATAL EXCEPTION: main
11-28 13:41:09.350  4765  4765 E AndroidRuntime: Process: com.example.coverscreentester, PID: 4765
11-28 13:41:09.350  4765  4765 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 13:41:09.350  4765  4765 E AndroidRuntime: 	... 13 more
11-28 19:13:43.939  4377  4377 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:13:43.939  4377  4377 E AndroidRuntime: Process: com.example.coverscreentester, PID: 4377
11-28 19:13:43.939  4377  4377 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:13:43.939  4377  4377 E AndroidRuntime: 	... 13 more
11-28 19:13:47.230  5743  5743 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:13:47.230  5743  5743 E AndroidRuntime: Process: com.example.coverscreentester, PID: 5743
11-28 19:13:47.230  5743  5743 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:13:47.230  5743  5743 E AndroidRuntime: 	... 13 more
11-28 19:13:49.449  5808  5808 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:13:49.449  5808  5808 E AndroidRuntime: Process: com.example.coverscreentester, PID: 5808
11-28 19:13:49.449  5808  5808 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:13:49.449  5808  5808 E AndroidRuntime: 	... 13 more
11-28 19:13:57.441  6400  6400 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:13:57.441  6400  6400 E AndroidRuntime: Process: com.example.coverscreentester, PID: 6400
11-28 19:13:57.441  6400  6400 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:13:57.441  6400  6400 E AndroidRuntime: 	... 13 more
11-28 19:17:57.229 11723 11723 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:17:57.229 11723 11723 E AndroidRuntime: Process: com.example.coverscreentester, PID: 11723
11-28 19:17:57.229 11723 11723 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:17:57.229 11723 11723 E AndroidRuntime: 	... 13 more
11-28 19:17:58.875 12994 12994 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:17:58.875 12994 12994 E AndroidRuntime: Process: com.example.coverscreentester, PID: 12994
11-28 19:17:58.875 12994 12994 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:17:58.875 12994 12994 E AndroidRuntime: 	... 13 more
11-28 19:18:02.621 13308 13308 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:18:02.621 13308 13308 E AndroidRuntime: Process: com.example.coverscreentester, PID: 13308
11-28 19:18:02.621 13308 13308 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:18:02.621 13308 13308 E AndroidRuntime: 	... 13 more
11-28 19:18:03.631  1434  2290 E WindowManager: win=Window{417f265 u0 com.example.com.katsuyamaki.coverscreenlauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
11-28 19:18:03.657  1434  2362 E NotificationService: Suppressing toast from package com.example.com.katsuyamaki.coverscreenlauncher by user request.
11-28 19:18:07.439  1434  2290 E NotificationService: Suppressing toast from package com.example.com.katsuyamaki.coverscreenlauncher by user request.
11-28 19:18:11.597  1434  3124 E WindowManager: win=Window{d318130 u0 com.example.com.katsuyamaki.coverscreenlauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
11-28 19:18:11.663  1434  3124 E NotificationService: Suppressing toast from package com.example.com.katsuyamaki.coverscreenlauncher by user request.
11-28 19:18:16.286 13867 13867 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:18:16.286 13867 13867 E AndroidRuntime: Process: com.example.coverscreentester, PID: 13867
11-28 19:18:16.286 13867 13867 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:18:16.286 13867 13867 E AndroidRuntime: 	... 13 more
11-28 19:18:27.082 15003 15003 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:18:27.082 15003 15003 E AndroidRuntime: Process: com.example.coverscreentester, PID: 15003
11-28 19:18:27.082 15003 15003 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:18:27.082 15003 15003 E AndroidRuntime: 	... 13 more
11-28 19:18:28.888 15121 15121 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:18:28.888 15121 15121 E AndroidRuntime: Process: com.example.coverscreentester, PID: 15121
11-28 19:18:28.888 15121 15121 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:18:28.888 15121 15121 E AndroidRuntime: 	... 13 more
11-28 19:24:01.160  1434  2282 E WindowManager: win=Window{d318130 u0 com.example.com.katsuyamaki.coverscreenlauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
11-28 19:24:01.267  1434  1641 E NotificationService: Suppressing toast from package com.example.com.katsuyamaki.coverscreenlauncher by user request.
11-28 19:24:14.076  1434  2282 E WindowManager: win=Window{d318130 u0 com.example.com.katsuyamaki.coverscreenlauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
11-28 19:24:14.194  1434  4777 E NotificationService: Suppressing toast from package com.example.com.katsuyamaki.coverscreenlauncher by user request.
11-28 19:25:39.560  1434  2297 E WindowManager: win=Window{d318130 u0 com.example.com.katsuyamaki.coverscreenlauncher} destroySurfaces: appStopped=true cleanupOnResume=false win.mWindowRemovalAllowed=false win.mRemoveOnExit=false win.mViewVisibility=8 caller=com.android.server.wm.WindowManagerService.tryStartExitingAnimation:134 com.android.server.wm.WindowManagerService.relayoutWindow:1147 com.android.server.wm.Session.relayout:27 android.view.IWindowSession$Stub.onTransact:829 com.android.server.wm.Session.onTransact:1 android.os.Binder.execTransactInternal:1457 android.os.Binder.execTransact:1401 
11-28 19:25:39.640  1434  4027 E NotificationService: Suppressing toast from package com.example.com.katsuyamaki.coverscreenlauncher by user request.
11-28 19:25:44.231  1434  2079 E NotificationService: Suppressing toast from package com.example.com.katsuyamaki.coverscreenlauncher by user request.
11-28 19:25:54.114 22260 22260 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:25:54.114 22260 22260 E AndroidRuntime: Process: com.example.coverscreentester, PID: 22260
11-28 19:25:54.114 22260 22260 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:25:54.114 22260 22260 E AndroidRuntime: 	... 13 more
11-28 19:25:55.580 22395 22395 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:25:55.580 22395 22395 E AndroidRuntime: Process: com.example.coverscreentester, PID: 22395
11-28 19:25:55.580 22395 22395 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:25:55.580 22395 22395 E AndroidRuntime: 	... 13 more
11-28 19:25:57.096 22446 22446 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:25:57.096 22446 22446 E AndroidRuntime: Process: com.example.coverscreentester, PID: 22446
11-28 19:25:57.096 22446 22446 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:25:57.096 22446 22446 E AndroidRuntime: 	... 13 more
11-28 19:25:59.532 22521 22521 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:25:59.532 22521 22521 E AndroidRuntime: Process: com.example.coverscreentester, PID: 22521
11-28 19:25:59.532 22521 22521 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:25:59.532 22521 22521 E AndroidRuntime: 	... 13 more
11-28 19:30:45.965 27492 27492 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:30:45.965 27492 27492 E AndroidRuntime: Process: com.example.coverscreentester, PID: 27492
11-28 19:30:45.965 27492 27492 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:30:45.965 27492 27492 E AndroidRuntime: 	... 13 more
11-28 19:31:35.891 27747 27747 E AndroidRuntime: FATAL EXCEPTION: main
11-28 19:31:35.891 27747 27747 E AndroidRuntime: Process: com.example.coverscreentester, PID: 27747
11-28 19:31:35.891 27747 27747 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.coverscreentester/com.example.coverscreentester.MainActivity}: java.lang.IllegalStateException: binder haven't been received
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4640)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4871)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:3103)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:110)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.os.Looper.loopOnce(Looper.java:273)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:363)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:9939)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:632)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: Caused by: java.lang.IllegalStateException: binder haven't been received
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at rikka.shizuku.Shizuku.requireService(Shizuku.java:430)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at rikka.shizuku.Shizuku.checkSelfPermission(Shizuku.java:868)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at com.example.coverscreentester.MainActivity.onCreate(MainActivity.kt:76)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9519)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:9488)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1524)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4622)
11-28 19:31:35.891 27747 27747 E AndroidRuntime: 	... 13 more
```

## File: gradle.properties
```
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2
```

## File: gradlew
```
#!/bin/sh
APP_HOME=${0%/*}
APP_NAME=$(basename $0)
APP_BASE_NAME=${APP_NAME%.*}
if [ "$APP_HOME" = "$APP_NAME" ]; then APP_HOME=.; fi
if [ -n "$JAVA_HOME" ] ; then
    JAVA_EXE="$JAVA_HOME/bin/java"
else
    JAVA_EXE="java"
fi
if [ ! -x "$JAVA_EXE" ] ; then
    echo "Error: JAVA_HOME is not set or $JAVA_EXE does not exist." >&2
    exit 1
fi
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
exec "$JAVA_EXE" -Dorg.gradle.appname="$APP_BASE_NAME" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
```

## File: gradlew.bat
```
@if "%DEBUG%"=="" @echo off
setlocal
set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_HOME=%DIRNAME%
set JAVA_EXE=java.exe
if defined JAVA_HOME set JAVA_EXE="%JAVA_HOME%\bin\java.exe"
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
"%JAVA_EXE%" -Dorg.gradle.appname=%~n0 -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
endlocal
```

## File: README.md
```markdown
# CoverScreen Trackpad 🖱️

**Turn your Samsung Flip cover screen into a fully functional mouse trackpad.**

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Shizuku](https://img.shields.io/badge/Shizuku-Required-blue?style=for-the-badge)

## 📖 About
**CoverScreen Trackpad** is a specialized utility designed for the Samsung Galaxy Z Flip series (and similar foldables). It creates a transparent, always-on-top trackpad overlay on your cover screen, allowing you to control apps that are difficult to use on the small display.

This app solves the "fat finger" problem on tiny screens by giving you a precise cursor, similar to using a laptop touchpad. It uses **Shizuku** to perform clicks and gestures without Accessibility Services, ensuring better battery life and privacy.

## ✨ Key Features
* **Precision Cursor:** Navigate tiny UI elements with a mouse pointer.
* **Smart Input:** Toggle between "Mouse Mode" and "Keyboard Mode" by holding the corner (prevents the trackpad from blocking your typing).
* **Scroll Bars:** Dedicated vertical and horizontal scroll zones on the edges.
* **Customizable:** Adjust transparency, scroll direction, handle sizes, and scrollbar placement (Left/Right, Top/Bottom).
* **No Accessibility Service:** Uses ADB/Shizuku for cleaner input injection.

## 🛠️ Requirements
1.  **Android 11+**
2.  **[Shizuku](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api)** (Must be installed and running).

## 🚀 Setup Instructions (Critical)
1.  Install **Shizuku** from the Play Store and start it (via Wireless Debugging).
2.  Install the **CoverScreen Trackpad** APK (from Releases).
3.  **OPEN THE APP ON YOUR MAIN SCREEN FIRST!** 🚨
4.  Grant **"Draw Over Other Apps"** and **"Shizuku"** permissions when prompted.
5.  Once the status says **"Ready"**, you can close the phone and launch the app on your cover screen.

## 🎮 Controls
| Action | Gesture / Button |
| :--- | :--- |
| **Left Click** | Tap anywhere on trackpad |
| **Right Click (Back)** | Press **Volume Down** |
| **Drag / Scroll** | Hold **Volume Up** + Swipe |
| **Toggle Keyboard** | Hold **Top-Left Corner** (1s) |
| **Move Window** | Drag **Top-Right Handle** |
| **Resize Window** | Hold **Bottom-Right Handle** (1s) then drag |
| **Open Menu** | Tap **Bottom-Left Handle** |

## ⚙️ Configuration
Open the app menu (Bottom-Left handle) to configure:
* Haptic Feedback
* Scroll Direction (Natural vs Standard)
* Scrollbar Placement
* Visual Transparency
* Handle Size

## ⚠️ Disclaimer
This project is currently in **Alpha**. It is intended for testing and development purposes. Use at your own risk.
```

## File: settings.gradle.kts
```
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "CoverScreenTrackpad"
include(":app")
```
