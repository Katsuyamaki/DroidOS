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
- Files matching these patterns are excluded: **/.gradle/**, **/build/**, **/.idea/**, **/*.iml, **/local.properties, **/**logcat**, **/build_log.txt, **/*.png, **/*.webp, **/**dictionary.txt, **/*.jar, **/*.aar, **/captures/**, **/*Repomix*.md
- Files matching patterns in .gitignore are excluded
- Files matching default ignore patterns are excluded
- Files are sorted by Git change count (files with more changes are at the bottom)

# Directory Structure
```
app/
  src/
    main/
      aidl/
        com/
          example/
            coverscreentester/
              IShellService.aidl
      assets/
        clean_dictionary.py
      java/
        com/
          example/
            coverscreentester/
              InterAppCommandReceiver.kt
              KeyboardActivity.kt
              KeyboardManager.kt
              KeyboardOverlay.kt
              KeyboardPickerActivity.kt
              KeyboardUtils.kt
              KeyboardView.kt
              MainActivity.kt
              ManualAdjustActivity.kt
              NullInputMethodService.kt
              OverlayService.kt
              PredictionEngine.kt
              ProfilesActivity.kt
              SettingsActivity.kt
              ShellUserService.kt
              ShizukuBinder.java
              ShizukuInputHandler.kt
              SwipeTrailView.kt
              TrackpadMenuAdapter.kt
              TrackpadMenuManager.kt
              TrackpadPrefs.kt
              TrackpadService.kt
      res/
        drawable/
          bg_item_active.xml
          bg_item_press.xml
          bg_trackpad_bubble.xml
          bg_trackpad_drawer.xml
          ic_cursor.xml
          ic_launcher_background.xml
          ic_launcher_foreground.xml
          ic_lock_closed.xml
          ic_lock_open.xml
          ic_tab_help.xml
          ic_tab_keyboard.xml
          ic_tab_main.xml
          ic_tab_profiles.xml
          ic_tab_settings.xml
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
          item_trackpad_menu.xml
          layout_trackpad_bubble.xml
          layout_trackpad_drawer.xml
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
          method.xml
      AndroidManifest.xml
  .gitignore
  build.gradle.kts
  proguard-rules.pro
gradle/
  wrapper/
    gradle-wrapper.properties
  libs.versions.toml
.gitignore
build.gradle.kts
crash_log.txt
gradle.properties
gradlew
gradlew.bat
README.md
settings.gradle.kts
```

# Files

## File: app/src/main/aidl/com/example/coverscreentester/IShellService.aidl
```
package com.example.coverscreentester;

interface IShellService {
    void injectMouse(int action, float x, float y, int displayId, int source, int buttonState, long downTime);
    void injectScroll(float x, float y, float vDistance, float hDistance, int displayId);
    void execClick(float x, float y, int displayId);
    void execRightClick(float x, float y, int displayId);
    
    // Updated to support deviceId parameter
    void injectKey(int keyCode, int action, int metaState, int displayId, int deviceId);
    
    // NEW: Trigger for "Hardware Keyboard" detection
    void injectDummyHardwareKey(int displayId);
    
    void setWindowingMode(int taskId, int mode);
    void resizeTask(int taskId, int left, int top, int right, int bottom);
    String runCommand(String cmd);

    // Screen Control Methods
    void setScreenOff(int displayIndex, boolean turnOff);
    void setBrightness(int value);
    boolean setBrightnessViaDisplayManager(int displayId, float brightness);
}
```

## File: app/src/main/assets/clean_dictionary.py
```python
import os
import re

# ==========================================
# CONFIGURATION
# ==========================================
INPUT_FILE = "dictionary.txt"
OUTPUT_FILE = "dictionary.txt" # Overwrites original

# 1. STRICT ALLOWLISTS (Short words are the noisiest in swipe)
# Only these 1-letter words are allowed
VALID_1_LETTER = {"a", "i"}

# Only these 2-letter words are allowed
VALID_2_LETTER = {
    "am", "an", "as", "at", "be", "by", "do", "go", "ha", "he", "hi", 
    "if", "in", "is", "it", "me", "my", "no", "of", "oh", "ok", "on", 
    "or", "ox", "so", "to", "up", "us", "we", "ye", "yo"
}

# Only these 3-letter words are allowed (Common English + standard abbreviations)
VALID_3_LETTER = {
    "act", "add", "ado", "age", "ago", "aid", "aim", "air", "ale", "all", "and", "ant", "any", "ape", "apt", "arc", "are", "ark", "arm", "art", "ash", "ask", "ate", "awe", "axe", "aye",
    "bad", "bag", "ban", "bar", "bat", "bay", "bed", "bee", "beg", "bet", "bib", "bid", "big", "bin", "bit", "bob", "bog", "boo", "bow", "box", "boy", "bra", "bud", "bug", "bum", "bun", "bus", "but", "buy", "bye",
    "cab", "cad", "cam", "can", "cap", "car", "cat", "cod", "cog", "con", "coo", "cop", "cot", "cow", "coy", "cry", "cub", "cue", "cup", "cut",
    "dab", "dad", "dam", "day", "den", "dew", "did", "die", "dig", "dim", "din", "dip", "doc", "doe", "dog", "don", "dot", "dry", "dub", "dud", "due", "dug", "duo", "dye",
    "ear", "eat", "ebb", "eel", "egg", "ego", "eke", "elf", "elk", "elm", "end", "era", "err", "eve", "ewe", "eye",
    "fad", "fan", "far", "fat", "fax", "fed", "fee", "few", "fib", "fig", "fin", "fit", "fix", "flu", "fly", "foe", "fog", "for", "fox", "fry", "fun", "fur",
    "gab", "gag", "gal", "gap", "gas", "gay", "gel", "gem", "get", "gig", "gin", "god", "got", "gum", "gun", "gut", "guy", "gym",
    "had", "hag", "ham", "has", "hat", "hay", "hem", "hen", "her", "hey", "hid", "him", "hip", "hit", "hoe", "hog", "hop", "hot", "how", "hub", "hue", "hug", "hum", "hut",
    "ice", "icy", "ill", "imp", "ink", "inn", "ion", "ire", "irk", "its", "ivy",
    "jab", "jam", "jar", "jaw", "jay", "jet", "jig", "job", "jog", "joy", "jug",
    "kea", "keg", "key", "kid", "kin", "kit",
    "lab", "lad", "lag", "lap", "law", "lay", "led", "lee", "leg", "let", "lid", "lie", "lip", "lit", "lob", "log", "loo", "lot", "low", "lug",
    "mad", "man", "map", "mat", "may", "men", "met", "mid", "mix", "mob", "mom", "mop", "mud", "mug", "mum",
    "nab", "nag", "nap", "nay", "net", "new", "nil", "nip", "nod", "nor", "not", "now", "nun", "nut",
    "oak", "oar", "oat", "odd", "off", "oft", "oil", "old", "one", "opt", "orb", "ore", "our", "out", "owl", "own",
    "pad", "pal", "pan", "par", "pat", "paw", "pay", "pea", "peg", "pen", "pet", "pew", "pie", "pig", "pin", "pit", "ply", "pod", "pop", "pot", "pro", "pry", "pub", "pun", "pup", "put",
    "rag", "ram", "ran", "rap", "rat", "raw", "ray", "red", "rib", "rid", "rig", "rim", "rip", "rob", "rod", "rot", "row", "rub", "rue", "rug", "rum", "run", "rut", "rye",
    "sad", "sag", "sap", "sat", "saw", "sax", "say", "sea", "see", "set", "sew", "sex", "she", "shy", "sin", "sip", "sir", "sit", "six", "ski", "sky", "sly", "sob", "sod", "son", "sop", "sow", "soy", "spa", "spy", "sub", "sue", "sum", "sun",
    "tab", "tag", "tan", "tap", "tar", "tat", "tax", "tea", "tee", "ten", "the", "thy", "tic", "tie", "tin", "tip", " toe", "tog", "ton", "too", "top", "tow", "toy", "try", "tub", "tug", "two",
    "urn", "use",
    "van", "vat", "vet", "via", "vow",
    "wad", "wag", "war", "was", "wax", "way", "web", "wed", "wee", "wet", "who", "why", "wig", "win", "wit", "woe", "won", "woo", "wow", "wry",
    "yak", "yam", "yap", "yes", "yet", "yew", "you",
    "zap", "zip", "zoo"
}

# 2. BLACKLIST
# Remove specific junk words or patterns
BLOCKED_PATTERNS = [
    r".*sex$",      # Ends in sex (animalsex, worldsex), unless it is 'sex' (handled by length check)
    r"^[^aeiouy]+$" # Words with NO vowels (e.g. 'tgp', 'mnt') - usually abbreviations
]
# Exceptions to the "ends with sex" rule (valid words)
SEX_EXCEPTIONS = {"sex", "unisex", "middlesex", "essex"}

def clean_dictionary():
    print(f"Reading {INPUT_FILE}...")
    
    if not os.path.exists(INPUT_FILE):
        print(f"Error: {INPUT_FILE} not found.")
        return

    with open(INPUT_FILE, 'r', encoding='utf-8') as f:
        raw_words = f.read().splitlines()

    cleaned_words = []
    removed_count = 0

    seen = set()

    for w in raw_words:
        w = w.strip().lower()
        
        # Filter 1: Basic Validity
        if not w or not w.isalpha():
            continue

        # Filter 2: Length-based strict allowlists
        if len(w) == 1:
            if w not in VALID_1_LETTER:
                removed_count += 1
                continue
        elif len(w) == 2:
            if w not in VALID_2_LETTER:
                # print(f"Removing 2-letter junk: {w}")
                removed_count += 1
                continue
        elif len(w) == 3:
            if w not in VALID_3_LETTER:
                # print(f"Removing 3-letter junk: {w}")
                removed_count += 1
                continue

        # Filter 3: Pattern Blocking
        is_blocked = False
        
        # Check "No Vowels" (junk abbreviations)
        if re.match(r"^[^aeiouy]+$", w):
            removed_count += 1
            continue

        # Check "sex" suffix spam
        if w.endswith("sex") and w not in SEX_EXCEPTIONS:
            print(f"Removing spam: {w}")
            removed_count += 1
            continue

        # Deduplicate
        if w in seen:
            continue
            
        seen.add(w)
        cleaned_words.append(w)

    # Sort alphabetically
    cleaned_words.sort()

    print(f"Original count: {len(raw_words)}")
    print(f"Removed: {removed_count}")
    print(f"New count: {len(cleaned_words)}")

    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write("\n".join(cleaned_words))
    
    print(f"Successfully cleaned dictionary saved to {OUTPUT_FILE}")

if __name__ == "__main__":
    clean_dictionary()
```

## File: app/src/main/java/com/example/coverscreentester/InterAppCommandReceiver.kt
```kotlin
package com.example.coverscreentester

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * =================================================================================
 * CLASS: InterAppCommandReceiver
 * SUMMARY: Static BroadcastReceiver for inter-app communication.
 *          Receives commands from DroidOS Launcher and ADB, then forwards them
 *          to the OverlayService. This ensures commands work even when the
 *          OverlayService's dynamic receiver isn't registered (background state).
 * 
 * USAGE (ADB):
 *   adb shell am broadcast -a com.example.coverscreentester.SOFT_RESTART
 *   adb shell am broadcast -a com.example.coverscreentester.MOVE_TO_VIRTUAL --ei DISPLAY_ID 2
 *   adb shell am broadcast -a com.example.coverscreentester.RETURN_TO_PHYSICAL --ei DISPLAY_ID 0
 *   adb shell am broadcast -a com.example.coverscreentester.ENFORCE_ZORDER
 *   adb shell am broadcast -a com.example.coverscreentester.TOGGLE_VIRTUAL_MIRROR
 *   adb shell am broadcast -a com.example.coverscreentester.GET_STATUS
 * =================================================================================
 */
class InterAppCommandReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "InterAppCmdReceiver"
        
        // Action constants
        const val ACTION_SOFT_RESTART = "com.example.coverscreentester.SOFT_RESTART"
        const val ACTION_MOVE_TO_VIRTUAL = "com.example.coverscreentester.MOVE_TO_VIRTUAL"
        const val ACTION_RETURN_TO_PHYSICAL = "com.example.coverscreentester.RETURN_TO_PHYSICAL"
        const val ACTION_ENFORCE_ZORDER = "com.example.coverscreentester.ENFORCE_ZORDER"
        const val ACTION_TOGGLE_VIRTUAL_MIRROR = "com.example.coverscreentester.TOGGLE_VIRTUAL_MIRROR"
        const val ACTION_GET_STATUS = "com.example.coverscreentester.GET_STATUS"
        
        // Extra keys
        const val EXTRA_DISPLAY_ID = "DISPLAY_ID"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        val action = intent.action ?: return
        Log.d(TAG, "Received inter-app command: $action")
        
        // Forward the command to OverlayService via startService
        // The OverlayService will handle the actual logic
        val serviceIntent = Intent(context, OverlayService::class.java).apply {
            this.action = action
            
            // Copy all extras from the original intent
            intent.extras?.let { extras ->
                putExtras(extras)
            }
        }
        
        try {
            // Start the service with the command
            // Using startService because AccessibilityService handles its own lifecycle
            context.startService(serviceIntent)
            Log.d(TAG, "Forwarded command to OverlayService: $action")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to forward command to OverlayService", e)
            
            // If service start fails, try broadcasting directly
            // The OverlayService's dynamic receiver might pick it up
            try {
                val broadcastIntent = Intent(action).apply {
                    setPackage(context.packageName)
                    intent.extras?.let { extras ->
                        putExtras(extras)
                    }
                }
                context.sendBroadcast(broadcastIntent)
                Log.d(TAG, "Sent internal broadcast as fallback: $action")
            } catch (e2: Exception) {
                Log.e(TAG, "Fallback broadcast also failed", e2)
            }
        }
    }
}
// =================================================================================
// END CLASS: InterAppCommandReceiver
// =================================================================================
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
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
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
    
    // Config
    private var currentWidth = 450
    // We use PROPORTIONAL spacing to ensure Aspect Ratio matches on all screens
    // 0 margin on container, slight margin on keys handled by spacing
    private val KEY_SPACING_RATIO = 0.005f 
    
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

    private val ROW_NUMS = listOf(
        KeyDef("1", KeyEvent.KEYCODE_1), KeyDef("2", KeyEvent.KEYCODE_2), KeyDef("3", KeyEvent.KEYCODE_3),
        KeyDef("4", KeyEvent.KEYCODE_4), KeyDef("5", KeyEvent.KEYCODE_5), KeyDef("6", KeyEvent.KEYCODE_6),
        KeyDef("7", KeyEvent.KEYCODE_7), KeyDef("8", KeyEvent.KEYCODE_8), KeyDef("9", KeyEvent.KEYCODE_9),
        KeyDef("0", KeyEvent.KEYCODE_0)
    )
    
    private val ROW_SYMS = listOf(
        KeyDef("@", KeyEvent.KEYCODE_AT), KeyDef("#", KeyEvent.KEYCODE_POUND), KeyDef("$", KeyEvent.KEYCODE_4), 
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
        // Zero padding - we fill the aspect ratio box completely
        mainContainer.setPadding(0, 0, 0, 0)
        
        mainContainer.addView(createRow(if (isSymbols) ROW_NUMS else ROW_1))
        mainContainer.addView(createRow(if (isSymbols) ROW_SYMS else ROW_2))
        mainContainer.addView(createRow(ROW_3))
        mainContainer.addView(createRow(ROW_4))
        mainContainer.addView(createRow(ARROWS))

        // Match Parent (which is strictly controlled by Window Size)
        root.addView(mainContainer, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        
        // --- SYNC LOCKER ---
        // Ensure Physical Keyboard strictly follows the 0.55 Ratio
        root.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
            val width = right - left
            val height = bottom - top
            if (width > 0) {
                val targetHeight = (width * 0.55f).toInt()
                if (abs(height - targetHeight) > 10) {
                    layoutParams?.height = targetHeight
                    try { windowManager.updateViewLayout(keyboardLayout, layoutParams) } catch (e: Exception) {}
                }
            }
        }
        
        return root
    }

    private fun createRow(keys: List<KeyDef>): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        
        // Vertical Weight 1.0 -> 5 Rows = 20% height each
        val rowParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
        rowParams.weight = 1.0f
        row.layoutParams = rowParams
        
        val MAX_ROW_WEIGHT = 10f
        val currentWeight = keys.map { it.weight }.sum()
        val missingWeight = MAX_ROW_WEIGHT - currentWeight
        
        if (missingWeight > 0.1f) {
            val spacer = View(context)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            params.weight = missingWeight / 2f
            row.addView(spacer, params)
        }
        
        // Font scaled to Width
        val fontSize = (currentWidth / 30f).coerceIn(10f, 22f)
        val marginPx = (currentWidth * KEY_SPACING_RATIO).toInt().coerceAtLeast(1)
        
        for (k in keys) {
            val btn = TextView(context)
            val label = if (!isSymbols && isShifted && k.label.length == 1) k.label.uppercase() else k.label
            
            btn.text = label
            btn.setTextColor(Color.WHITE)
            btn.textSize = fontSize
            btn.gravity = Gravity.CENTER
            btn.typeface = Typeface.DEFAULT_BOLD
            
            val keyBg = GradientDrawable()
            keyBg.setColor(if (k.isSpecial) Color.parseColor("#444444") else Color.parseColor("#2A2A2A"))
            keyBg.cornerRadius = 10f
            btn.background = keyBg
            
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            params.weight = k.weight
            params.setMargins(marginPx, marginPx, marginPx, marginPx)
            row.addView(btn, params)
            
            btn.setOnClickListener {
                handleKeyPress(k)
                btn.alpha = 0.5f
                btn.postDelayed({ btn.alpha = 1.0f }, 50)
            }
        }
        
        if (missingWeight > 0.1f) {
            val spacer = View(context)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            params.weight = missingWeight / 2f
            row.addView(spacer, params)
        }

        return row
    }

    private fun handleKeyPress(k: KeyDef) {
        when (k.code) {
            -1 -> { isShifted = !isShifted; refreshLayout() }
            -2 -> { isSymbols = !isSymbols; refreshLayout() }
            else -> {
                keyInjector(k.code)
                if (isShifted) { isShifted = false; refreshLayout() }
            }
        }
    }

    fun show(width: Int, height: Int) {
        if (isVisible) return
        
        currentWidth = width
        // Strict Start
        val targetHeight = (width * 0.55f).toInt()
        
        layoutParams = WindowManager.LayoutParams(
            currentWidth,
            targetHeight, 
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        )
        
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        layoutParams?.y = 0 

        keyboardLayout = createView() as FrameLayout
        windowManager.addView(keyboardLayout, layoutParams)
        isVisible = true
    }

    fun hide() {
        if (!isVisible) return
        try { windowManager.removeView(keyboardLayout) } catch (e: Exception) {}
        isVisible = false
        keyboardLayout = null
    }
    
    fun toggle(width: Int, height: Int) { if (isVisible) hide() else show(width, height) }

    private fun refreshLayout() {
        if (!isVisible) return
        val p = keyboardLayout?.layoutParams
        windowManager.removeView(keyboardLayout)
        keyboardLayout = createView() as FrameLayout
        windowManager.addView(keyboardLayout, p)
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
import android.media.AudioManager
import android.media.AudioRecordingConfiguration
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.InputDevice // <--- ADD THIS LINE
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
    private val targetDisplayId: Int,
    private val onScreenToggleAction: () -> Unit,
    private val onScreenModeChangeAction: () -> Unit,


    private val onCloseAction: () -> Unit // New Parameter
) : KeyboardView.KeyboardListener {

    private var keyboardContainer: FrameLayout? = null


    private var keyboardView: KeyboardView? = null
    private var keyboardParams: WindowManager.LayoutParams? = null
    private var isVisible = false
    private val predictionEngine = PredictionEngine.instance
    // State Variables
    private var isMoving = false

    private var isResizing = false
    // [NEW] Track scale internally to avoid slow SharedPreferences reads during drag
    private var internalScale = 1.0f 
    private var dragStartScale = 1.0f
    private var dragStartHeight = 0

    private var isAnchored = false
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialWindowX = 0
    private var initialWindowY = 0
    private var initialWidth = 0
    private var initialHeight = 0

    private val TAG = "KeyboardOverlay"

    // [FIX] Track the physical device ID to ignore injected events (Anti-Loop)
    private var activeFingerDeviceId = -1
    // =================================================================================
    // VIRTUAL MIRROR ORIENTATION MODE VARIABLES
    // SUMMARY: State for orientation mode when virtual mirror is active.
    //          During orientation mode, an orange trail is shown and key input is blocked
    //          until the finger stops moving for the configured delay.
    // =================================================================================
    private var isOrientationModeActive = false
    private var orientationTrailView: SwipeTrailView? = null
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR ORIENTATION MODE VARIABLES
    // =================================================================================

    // --- PREDICTION STATE ---

    private var currentComposingWord = StringBuilder()
    // =================================================================================
    // ORIGINAL CASE TRACKING
    // SUMMARY: Stores the word with original casing (e.g., "DroidOS", "don't")
    //          while currentComposingWord stores lowercase for dictionary lookup.
    // =================================================================================
    private var originalCaseWord = StringBuilder()
    // =================================================================================
    // END BLOCK: ORIGINAL CASE TRACKING
    // =================================================================================
    private val handler = Handler(Looper.getMainLooper())

    // NEW: Track sentence context and swipe history
    private var lastCommittedSwipeWord: String? = null
    private var isSentenceStart = true

    // Helper to inject text via OverlayService
    private fun injectText(text: String) {
        (context as? OverlayService)?.injectText(text)
    }

    // FIX Default height to WRAP_CONTENT (-2) to avoid cutting off rows
    private var keyboardWidth = 500
    private var keyboardHeight = WindowManager.LayoutParams.WRAP_CONTENT 
    
    private var screenWidth = 720
    private var screenHeight = 748
    private var currentRotation = 0
    private var currentAlpha = 200
    private var currentDisplayId = 0


    // Callbacks to talk back to OverlayService
    var onCursorMove: ((Float, Float, Boolean) -> Unit)? = null // dx, dy, isDragging
    var onCursorClick: ((Boolean) -> Unit)? = null
    var onTouchDown: (() -> Unit)? = null
    var onTouchUp: (() -> Unit)? = null
    var onTouchTap: (() -> Unit)? = null

    // =================================================================================
    // VIRTUAL MIRROR CALLBACK
    // SUMMARY: Callback to forward touch events to OverlayService for mirror sync.
    //          Returns true if touch should be consumed (orientation mode active).
    // =================================================================================
    var onMirrorTouch: ((Float, Float, Int) -> Boolean)? = null // x, y, action -> consumed
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR CALLBACK
    // =================================================================================

    // Layer change callback for syncing mirror keyboard
    var onLayerChanged: ((KeyboardView.KeyboardState) -> Unit)? = null

    // =================================================================================
    // CALLBACK: onSuggestionsChanged
    // SUMMARY: Called whenever the suggestion bar is updated. Used to sync mirror keyboard.
    // =================================================================================
    var onSuggestionsChanged: ((List<KeyboardView.Candidate>) -> Unit)? = null
    var onSizeChanged: (() -> Unit)? = null
    // =================================================================================
    // END BLOCK: onSuggestionsChanged
    // =================================================================================




    fun setScreenDimensions(width: Int, height: Int, displayId: Int) {
        // 1. Update Class-Level Screen Dimensions
        this.screenWidth = width
        this.screenHeight = height
        this.currentDisplayId = displayId

        // 2. Apply Dynamic Resize Logic (Same as Reset)
        // This ensures that when entering split-screen or rotating, the keyboard
        // recalculates its perfect size (90% width, 300dp height) instead of stretching.
        val newWidth = (width * 0.90f).toInt().coerceIn(300, 1200)
        
        // Calculate Height: 300dp * Scale * Density
        val density = context.resources.displayMetrics.density
        val scale = if (internalScale > 0f) internalScale else 0.69f
        val baseHeightDp = 300f
        val newHeight = (baseHeightDp * scale * density).toInt()

        // 3. Update Window Params
        keyboardWidth = newWidth
        keyboardHeight = newHeight
        
        keyboardParams?.let {
            it.width = newWidth
            it.height = newHeight
            
            // Optional: Re-center keyboard on screen change
            it.x = (width - newWidth) / 2
            it.y = height / 2

            try {
                windowManager.updateViewLayout(keyboardContainer, it)
                syncMirrorRatio(newWidth, newHeight)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }





    fun updateScale(scale: Float) {
        internalScale = scale // [FIX] Track scale state
        if (keyboardView == null) return
        keyboardView?.setScale(scale)
        
        // FIX: Removed forced reset of keyboardHeight to WRAP_CONTENT.
        // We now respect the existing keyboardHeight (whether it's fixed pixels from a manual resize
        // or WRAP_CONTENT from default).
        
        if (isVisible && keyboardParams != null) {
            // If the window is set to WRAP_CONTENT, we might need to poke the WM to re-measure
            // effectively, but we shouldn't change the param value itself if it's already -2.
            // If it is fixed pixels, we leave it alone.
            
            // We only need to update layout if we want to ensure constraints are met,
            // but simply invalidating the view is usually enough for internal changes.
            // To be safe, we update the view layout with the *current* params.
            try { 
                windowManager.updateViewLayout(keyboardContainer, keyboardParams)
                // Do NOT call saveKeyboardSize() here. Scaling shouldn't change the 
                // "Window Size Preference" (Container), only the content scale.
            } catch (e: Exception) {}
        }
    }
    
    fun updateAlpha(alpha: Int) {
        currentAlpha = alpha
        if (isVisible && keyboardContainer != null) {
            val bg = keyboardContainer?.background as? GradientDrawable
            if (bg != null) {
                val fillColor = (alpha shl 24) or (0x1A1A1A)
                bg.setColor(fillColor)
                bg.setStroke(2, Color.parseColor("#44FFFFFF"))
            }
            val normalizedAlpha = alpha / 255f
            keyboardView?.alpha = normalizedAlpha
            keyboardContainer?.invalidate()
        }
    }
    
    fun setWindowBounds(x: Int, y: Int, width: Int, height: Int) {
        keyboardWidth = width
        keyboardHeight = height
        if (isVisible && keyboardParams != null) {
            keyboardParams?.x = x
            keyboardParams?.y = y
            keyboardParams?.width = width
            keyboardParams?.height = height
            try { 
                windowManager.updateViewLayout(keyboardContainer, keyboardParams)
                saveKeyboardPosition()
                saveKeyboardSize()
            } catch (e: Exception) {}
        } else {
            // Even if hidden, save the new bounds so they apply on next show
            val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putInt("keyboard_x_d$currentDisplayId", x)
                .putInt("keyboard_y_d$currentDisplayId", y)
                .putInt("keyboard_width_d$currentDisplayId", width)
                .putInt("keyboard_height_d$currentDisplayId", height)
                .apply()
        }
    }
   
    fun setAnchored(anchored: Boolean) {
        isAnchored = anchored
    }

    fun setVibrationEnabled(enabled: Boolean) {
        keyboardView?.setVibrationEnabled(enabled)
    }
    // Helper for OverlayService Profile Load
    fun updatePosition(x: Int, y: Int) {
        if (keyboardContainer == null || keyboardParams == null) {
            // Save to prefs if hidden
            context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
                .putInt("keyboard_x_d$currentDisplayId", x)
                .putInt("keyboard_y_d$currentDisplayId", y)
                .apply()
            return
        }
        keyboardParams?.x = x
        keyboardParams?.y = y
        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
            saveKeyboardPosition()
        } catch (e: Exception) { e.printStackTrace() }
    }

    // Helper for OverlayService Profile Load
    fun updateSize(w: Int, h: Int) {
        keyboardWidth = w
        keyboardHeight = h
        
        if (keyboardContainer == null || keyboardParams == null) {
            saveKeyboardSize()
            return
        }
        keyboardParams?.width = w
        keyboardParams?.height = h
        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
            saveKeyboardSize()
        } catch (e: Exception) { e.printStackTrace() }
    }
    
    // Robust Getters: Return live values if visible, otherwise return saved Prefs
    fun getViewX(): Int {
        if (keyboardParams != null) return keyboardParams!!.x
        return context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .getInt("keyboard_x_d$currentDisplayId", 0)
    }
    

    fun getViewY(): Int {
        if (keyboardParams != null) return keyboardParams!!.y
        return context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .getInt("keyboard_y_d$currentDisplayId", 0)
    }
    
    fun getViewWidth(): Int = keyboardWidth
    fun getViewHeight(): Int = keyboardHeight
    fun getScale(): Float = internalScale // [FIX] Added accessor
    fun getKeyboardView(): KeyboardView? = keyboardView


    
    // [START ROTATION FIX]
    fun setRotation(angle: Int) {
        currentRotation = angle
        if (!isVisible || keyboardContainer == null || keyboardParams == null || keyboardView == null) return

        val isPortrait = (angle == 90 || angle == 270)

        // 1. Determine Logical Dimensions (Unrotated size)
        // We rely on keyboardWidth/Height being the canonical "Landscape" size.
        val baseW = keyboardWidth
        val baseH = keyboardHeight 

        // 2. Configure WINDOW Params (The touchable area on screen)
        // If rotated, we swap the dimensions passed to WindowManager
        if (isPortrait) {
            keyboardParams?.width = if (baseH == -2) WindowManager.LayoutParams.WRAP_CONTENT else baseH
            keyboardParams?.height = if (baseW == -2) WindowManager.LayoutParams.WRAP_CONTENT else baseW
        } else {
            keyboardParams?.width = if (baseW == -2) WindowManager.LayoutParams.WRAP_CONTENT else baseW
            keyboardParams?.height = if (baseH == -2) WindowManager.LayoutParams.WRAP_CONTENT else baseH
        }

        // 3. Configure VIEW Params (The Internal Content)
        // The View must ALWAYS be the logical size (e.g. Wide) to layout keys in rows correctly.
        val lp = keyboardView!!.layoutParams as FrameLayout.LayoutParams
        lp.width = if (baseW == -2) FrameLayout.LayoutParams.WRAP_CONTENT else baseW
        lp.height = if (baseH == -2) FrameLayout.LayoutParams.WRAP_CONTENT else baseH
        keyboardView!!.layoutParams = lp

        // 4. Apply Rotation to View (Not Container)
        keyboardView!!.rotation = angle.toFloat()
        keyboardContainer!!.rotation = 0f // Ensure container is NOT rotated

        // 5. Update Layout
        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
        } catch (e: Exception) {}

        // 6. Post-Layout Alignment
        // We must translate the view to re-center it because rotation happens around the pivot (center).
        // Since we swapped the Window dimensions, the centers might not align by default without this.
        keyboardView!!.post { alignRotatedView() }
    }

    private fun alignRotatedView() {
        if (keyboardView == null) return
        
        val angle = currentRotation
        val w = keyboardView!!.measuredWidth
        val h = keyboardView!!.measuredHeight
        
        // When rotated 90/270, the "Visual" width matches the View's Height, and vice versa.
        // We translate the view so its visual center matches the window's center.
        
        when (angle) {
            90, 270 -> {
                val tx = (h - w) / 2f
                val ty = (w - h) / 2f
                keyboardView!!.translationX = tx
                keyboardView!!.translationY = ty
            }
            else -> {
                keyboardView!!.translationX = 0f
                keyboardView!!.translationY = 0f
            }
        }
    }

    fun cycleRotation() {
        if (keyboardContainer == null) return
        val nextRotation = (currentRotation + 90) % 360
        setRotation(nextRotation)
    }




    // [Removed duplicate accessors to fix build error] 




    fun resetPosition() {
        if (keyboardParams == null) return
        
        // 1. Set Scale to 0.69f (Standard Reset Scale)
        val defaultScale = 0.69f
        internalScale = defaultScale
        keyboardView?.setScale(defaultScale)
        
        // Save preference
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
             .edit().putInt("keyboard_key_scale", 69).apply()

        // 2. Reset Rotation state
        currentRotation = 0
        keyboardContainer?.rotation = 0f
        keyboardView?.rotation = 0f
        keyboardView?.translationX = 0f
        keyboardView?.translationY = 0f

        // 3. Calculate Dimensions
        // Width: 90% of screen, capped at 1200px to prevent ultra-wide windows
        val defaultWidth = (screenWidth * 0.90f).toInt().coerceIn(300, 1200)
        
        // Height: Fixed Physical Height Calculation
        // Problem: Calculating height as % of width fails on wide screens (Beam Pro), creating huge empty space.
        // Solution: Use fixed DP height.
        // 300dp = Physical height of 7 rows (derived from your Flip 7 1080p setup).
        // This ensures the window is exactly tall enough for the keys on ANY device.
        val density = context.resources.displayMetrics.density
        val baseHeightDp = 300f
        val defaultHeight = (baseHeightDp * defaultScale * density).toInt()
        
        // Position: Center
        val defaultX = (screenWidth - defaultWidth) / 2
        val defaultY = (screenHeight / 2)

        // 4. Update Params
        keyboardWidth = defaultWidth
        keyboardHeight = defaultHeight
        
        // Reset Drag Anchors
        dragStartHeight = defaultHeight
        dragStartScale = defaultScale
        
        keyboardParams?.x = defaultX
        keyboardParams?.y = defaultY
        keyboardParams?.width = defaultWidth
        keyboardParams?.height = defaultHeight

        // 5. Force View to Fill Window
        if (keyboardView != null) {
            val lp = keyboardView!!.layoutParams as FrameLayout.LayoutParams
            lp.width = FrameLayout.LayoutParams.MATCH_PARENT
            lp.height = FrameLayout.LayoutParams.MATCH_PARENT 
            keyboardView!!.layoutParams = lp
        }

        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
        } catch (e: Exception) {}
        
        saveKeyboardPosition()
        saveKeyboardSize()
        
        // 6. Sync Mirror
        syncMirrorRatio(defaultWidth, defaultHeight)
    }





    // [END ROTATION FIX] 


    fun show() { 
        if (isVisible) return
        try { 
            val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            currentAlpha = prefs.getInt("keyboard_alpha", 200)


            createKeyboardWindow()

            // [NEW] Initialize internal scale from prefs once on show
            // Default to 69 (0.69f) if missing, to match resetPosition
            val savedScale = prefs.getInt("keyboard_key_scale", 69) / 100f
            internalScale = savedScale
            dragStartScale = savedScale // Init for safety



            // [MODIFIED] Removed forced 0.55 aspect ratio listener to allow independent resizing.
            // Sync is now handled via explicit broadcast in resize functions.


            isVisible = true
            if (currentRotation != 0) setRotation(currentRotation)
        } catch (e: Exception) { android.util.Log.e("KeyboardOverlay", "Failed to show keyboard", e) } 
    }


    
    fun hide() { 
        if (!isVisible) return
        try { 
            windowManager.removeView(keyboardContainer)
            keyboardContainer = null
            keyboardView = null
            isVisible = false 
        } catch (e: Exception) { Log.e(TAG, "Failed to hide keyboard", e) } 
    }
    
    fun toggle() { if (isVisible) hide() else show() }
    fun isShowing(): Boolean = isVisible

    fun setFocusable(focusable: Boolean) {
        try {
            if (keyboardContainer == null || keyboardParams == null) return

            if (focusable) {
                // Remove NOT_FOCUSABLE (Make it focusable)
                keyboardParams?.flags = keyboardParams?.flags?.and(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv())
            } else {
                // Add NOT_FOCUSABLE (Make it click-through for focus purposes)
                keyboardParams?.flags = keyboardParams?.flags?.or(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            }
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // =================================================================================
    // FUNCTION: setVoiceActive
    // SUMMARY: Passes the voice state down to the keyboard view.
    // =================================================================================
    fun setVoiceActive(active: Boolean) {
        keyboardView?.setVoiceActive(active)
    }
    // =================================================================================
    // END BLOCK: setVoiceActive
    // =================================================================================

    // =================================================================================
    // VIRTUAL MIRROR ORIENTATION MODE METHODS
    // SUMMARY: Methods for managing orientation mode during virtual mirror operation.
    //          These handle the orange trail that helps users locate their finger
    //          position on the physical keyboard without looking at the screen.
    // =================================================================================

    // =================================================================================
    // FUNCTION: setOrientationMode
    // SUMMARY: Enables or disables orientation mode. When enabled, an orange trail
    //          is shown and key input is blocked until the mode ends.
    // @param active - true to enable orientation mode, false to disable
    // =================================================================================
    fun setOrientationMode(active: Boolean) {
        isOrientationModeActive = active
        keyboardView?.setOrientationModeActive(active)

        if (!active) {
            // Clear the orange trail when exiting orientation mode
            orientationTrailView?.clear()
        }
    }
    // =================================================================================
    // END BLOCK: setOrientationMode
    // =================================================================================

    // =================================================================================
    // FUNCTION: startOrientationTrail
    // SUMMARY: Starts a new orange orientation trail at the specified position.
    // @param x - Starting X coordinate
    // @param y - Starting Y coordinate
    // =================================================================================
    fun startOrientationTrail(x: Float, y: Float) {
        orientationTrailView?.clear()
        orientationTrailView?.addPoint(x, y)
    }
    // =================================================================================
    // END BLOCK: startOrientationTrail
    // =================================================================================

    // =================================================================================
    // FUNCTION: addOrientationTrailPoint
    // SUMMARY: Adds a point to the orange orientation trail.
    // @param x - X coordinate of new point
    // @param y - Y coordinate of new point
    // =================================================================================
    fun addOrientationTrailPoint(x: Float, y: Float) {
        orientationTrailView?.addPoint(x, y)
    }
    // =================================================================================
    // END BLOCK: addOrientationTrailPoint
    // =================================================================================

    // =================================================================================
    // FUNCTION: clearOrientationTrail
    // SUMMARY: Clears the orange orientation trail.
    // =================================================================================
    fun clearOrientationTrail() {
        orientationTrailView?.clear()
    }
    // =================================================================================
    // END BLOCK: clearOrientationTrail
    // =================================================================================

    // =================================================================================
    // FUNCTION: setOrientationTrailColor
    // SUMMARY: Sets the color of the orientation trail on the physical display.
    // =================================================================================
    fun setOrientationTrailColor(color: Int) {
        orientationTrailView?.setTrailColor(color)
    }
    // =================================================================================
    // END BLOCK: setOrientationTrailColor
    // =================================================================================

    // =================================================================================
    // FUNCTION: startSwipeFromCurrentPosition
    // SUMMARY: Called when switching from orange to blue trail mid-gesture.
    //          Initializes swipe tracking so the path starts from the given position.
    // =================================================================================
    fun startSwipeFromCurrentPosition(x: Float, y: Float) {
        keyboardView?.startSwipeFromPosition(x, y)
    }
    // =================================================================================
    // END BLOCK: startSwipeFromCurrentPosition
    // =================================================================================

    // =================================================================================
    // FUNCTION: handleDeferredTap
    // SUMMARY: Forwards deferred tap to KeyboardView for single key press in mirror mode.
    // =================================================================================

    fun handleDeferredTap(x: Float, y: Float) {
        // CRITICAL: If a tap is detected, immediately stop any pending repeat logic.
        // This prevents the "stuck" state where the system thinks you are still holding the key.
        stopMirrorRepeat()
        keyboardView?.handleDeferredTap(x, y)
    }
// =================================================================================
    // FUNCTION: getKeyAtPosition
    // SUMMARY: Returns the key tag at the given position, or null if no key found.
    //          Used by mirror mode to check if finger is on a repeatable key.
    // =================================================================================
    fun getKeyAtPosition(x: Float, y: Float): String? {
        return keyboardView?.getKeyAtPosition(x, y)
    }
    // =================================================================================
    // END BLOCK: getKeyAtPosition
    // =================================================================================

    // =================================================================================

    // =================================================================================
    // FUNCTION: triggerKeyPress (Updated with Tap-Reset Fix)
    // SUMMARY: Triggers a key press by key tag for Mirror Mode.
    //          Includes 400ms initial delay + Watchdog timeout.
    //          Now robustly resets if the sequence is broken.
    // =================================================================================
    private var activeRepeatKey: String? = null
    private var lastMirrorKeyTime = 0L
    private val mirrorRepeatHandler = Handler(Looper.getMainLooper())
    private val REPEAT_START_DELAY = 400L
    private val REPEAT_INTERVAL = 50L 
    
    // Watchdog: If no input received for 150ms, assume key was released
    // Increased to 150ms to be more tolerant of input jitters
    private val MIRROR_INPUT_TIMEOUT = 150L 

    private val mirrorRepeatRunnable = object : Runnable {
        override fun run() {
            val key = activeRepeatKey ?: return
            val now = System.currentTimeMillis()
            
            // Watchdog Check
            if (now - lastMirrorKeyTime > MIRROR_INPUT_TIMEOUT) {
                stopMirrorRepeat()
                return
            }

            // Fire event
            keyboardView?.triggerKeyPress(key)
            mirrorRepeatHandler.postDelayed(this, REPEAT_INTERVAL)
        }
    }

    // Changed from private to private-but-accessible-internally (or keep private if handleDeferredTap is in same class)
    private fun stopMirrorRepeat() {
        activeRepeatKey = null
        mirrorRepeatHandler.removeCallbacks(mirrorRepeatRunnable)
    }


    fun blockPrediction(index: Int) {
        keyboardView?.blockPredictionAtIndex(index)
    }


    fun triggerKeyPress(keyTag: String) {
        val isRepeatable = keyTag in setOf("BKSP", "DEL", "◄", "▲", "▼", "►", "←", "↑", "↓", "→", "VOL+", "VOL-", "VOL_UP", "VOL_DOWN")

        if (!isRepeatable) {
            stopMirrorRepeat()
            keyboardView?.triggerKeyPress(keyTag)
            return
        }

        val now = System.currentTimeMillis()

        if (keyTag == activeRepeatKey) {
            // Update watchdog time
            lastMirrorKeyTime = now
            
            // ROBUSTNESS FIX: If for some reason the handler isn't running (race condition),
            // restart it to ensure we don't get stuck in a silent state.
            if (!mirrorRepeatHandler.hasCallbacks(mirrorRepeatRunnable)) {
                mirrorRepeatHandler.postDelayed(mirrorRepeatRunnable, REPEAT_START_DELAY)
            }
        } else {
            // New Key Sequence
            stopMirrorRepeat()
            
            activeRepeatKey = keyTag
            lastMirrorKeyTime = now
            
            // Start Delay (Wait 400ms before first fire)
            mirrorRepeatHandler.postDelayed(mirrorRepeatRunnable, REPEAT_START_DELAY)
        }
    }
   // FUNCTION: getKeyboardState
    // SUMMARY: Gets current keyboard state (layer) from KeyboardView.
    // =================================================================================
    fun getKeyboardState(): KeyboardView.KeyboardState? {
        return keyboardView?.getKeyboardState()
    }

    // =================================================================================
    // FUNCTION: setKeyboardState
    // SUMMARY: Sets keyboard state (layer) in KeyboardView.
    // =================================================================================
    fun setKeyboardState(state: KeyboardView.KeyboardState) {
        keyboardView?.setKeyboardState(state)
    }

    // =================================================================================
    // FUNCTION: getCtrlAltState
    // SUMMARY: Gets current Ctrl/Alt modifier states from KeyboardView.
    // =================================================================================
    fun getCtrlAltState(): Pair<Boolean, Boolean>? {
        return keyboardView?.getCtrlAltState()
    }

    // =================================================================================
    // FUNCTION: setCtrlAltState
    // SUMMARY: Sets Ctrl/Alt modifier states in KeyboardView.
    // =================================================================================
    fun setCtrlAltState(ctrl: Boolean, alt: Boolean) {
        keyboardView?.setCtrlAltState(ctrl, alt)
    }
    // =================================================================================
    // END BLOCK: State accessor functions for mirror sync
    // =================================================================================

    // =================================================================================
    // FUNCTION: updateSuggestionsWithSync
    // SUMMARY: Sets suggestions on the keyboard view AND notifies callback for mirror sync.
    // =================================================================================
    private fun updateSuggestionsWithSync(candidates: List<KeyboardView.Candidate>) {
        keyboardView?.setSuggestions(candidates)
        onSuggestionsChanged?.invoke(candidates)
    }
    // =================================================================================
    // END BLOCK: updateSuggestionsWithSync
    // =================================================================================

    // =================================================================================
    // FUNCTION: isInOrientationMode
    // SUMMARY: Returns whether orientation mode is currently active.
    // @return true if orientation mode is active
    // =================================================================================
    fun isInOrientationMode(): Boolean {
        return isOrientationModeActive
    }
    // =================================================================================
    // END BLOCK: isInOrientationMode
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR ORIENTATION MODE METHODS
    // =================================================================================

    fun moveWindow(dx: Int, dy: Int) {
        if (!isVisible || keyboardParams == null) return
        keyboardParams!!.x += dx; keyboardParams!!.y += dy
        try { 
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
            saveKeyboardPosition()
            onSizeChanged?.invoke()
        } catch (e: Exception) {}
    }
    

    // [MODIFIED] Legacy support: Redirects to the new smart resize logic
    fun resizeWindow(dw: Int, dh: Int) {
        if (!isVisible || keyboardParams == null) return
        
        // Calculate target dimensions based on current params
        val currentW = if (keyboardParams!!.width > 0) keyboardParams!!.width else keyboardContainer?.width ?: 300
        val currentH = if (keyboardParams!!.height > 0) keyboardParams!!.height else keyboardContainer?.height ?: 200

        // Pass to the new centralized function that handles Scaling + Sync
        applyWindowResize(currentW + dw, currentH + dh)
        
        // [FIX] Save state so it persists
        saveKeyboardSize()
        saveKeyboardScale()
    }


    // =================================================================================
    // NEW: Centralized Resize Logic with Auto-Scale and Mirror Sync
    // =================================================================================

    // [MODIFIED] Deterministic Resizing Logic (Fixes Lag & Mismatch)
    private fun applyWindowResize(width: Int, height: Int) {
        if (keyboardParams == null) return

        val newWidth = max(300, width)
        val newHeight = max(150, height)
        
        // 1. Calculate New Scale Deterministically
        // Formula: NewScale = StartScale * (NewHeight / StartHeight)
        // This ensures the keys grow exactly proportional to the window drag.
        if (dragStartHeight > 0 && newHeight != dragStartHeight) {
             val heightRatio = newHeight.toFloat() / dragStartHeight.toFloat()
             val targetScale = dragStartScale * heightRatio
             
             // Update Internal State
             internalScale = targetScale
             keyboardView?.setScale(internalScale)
        }

        // 2. Update Window Params
        keyboardParams!!.width = newWidth
        keyboardParams!!.height = newHeight
        keyboardWidth = newWidth
        keyboardHeight = newHeight

        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
            onSizeChanged?.invoke()
        } catch (e: Exception) {}

        // 3. Sync Mirror Aspect Ratio
        syncMirrorRatio(newWidth, newHeight)
    }


    fun handleResizeDpad(keyCode: Int): Boolean {
        if (!isVisible || keyboardParams == null) return false
        
        val STEP = 20
        var w = keyboardParams!!.width
        var h = keyboardParams!!.height
        var changed = false

        when (keyCode) {
            // Horizontal Only
            KeyEvent.KEYCODE_DPAD_LEFT -> { w -= STEP; changed = true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { w += STEP; changed = true }
            
            // Vertical Only
            KeyEvent.KEYCODE_DPAD_UP -> { h -= STEP; changed = true }   // Shrink Height
            KeyEvent.KEYCODE_DPAD_DOWN -> { h += STEP; changed = true } // Grow Height
        }

        if (changed) {
            applyWindowResize(w, h)
            saveKeyboardSize()  // [FIX] Save size immediately
            saveKeyboardScale() // [FIX] Save scale immediately
            return true
        }
        return false
    }

    private fun syncMirrorRatio(width: Int, height: Int) {
        try {
            val ratio = width.toFloat() / height.toFloat()
            val intent = android.content.Intent("com.katsuyamaki.DroidOSLauncher.SYNC_KEYBOARD_RATIO")
            intent.putExtra("ratio", ratio)
            intent.putExtra("width", width)
            intent.putExtra("height", height)
            intent.setPackage("com.katsuyamaki.DroidOSLauncher")
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            android.util.Log.e("KeyboardOverlay", "Failed to sync mirror ratio", e)
        }
    }





    private fun createKeyboardWindow() {
        // [FIX] Use custom FrameLayout to intercept ALL touches.
        keyboardContainer = object : FrameLayout(context) {
            
            // SHIELD 1: Block Mouse Hovers
            override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
                if (event.isFromSource(InputDevice.SOURCE_MOUSE) || 
                    event.isFromSource(InputDevice.SOURCE_MOUSE_RELATIVE)) {
                    return true 
                }
                return super.dispatchGenericMotionEvent(event)
            }

            // SHIELD 2: Block Injected/Obscured Touches
            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                
                // [CRITICAL FIX] OBSCURED FILTER
                // If the touch is passing through another window (like our Cursor Overlay),
                // Android flags it as OBSCURED. We reject these to prevent the cursor loop.
                if ((event.flags and MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0 ||
                    (event.flags and MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED) != 0) {
                    return true
                }

                // 1. Block Non-Fingers
                if (event.getToolType(0) != MotionEvent.TOOL_TYPE_FINGER) return true
                
                // 2. Block Virtual/Null Devices
                if (event.device == null || event.deviceId <= 0) return true
                
                // 3. Block Mouse Sources
                if (event.isFromSource(InputDevice.SOURCE_MOUSE)) return true

                val devId = event.deviceId
                val action = event.actionMasked

                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Lock onto the first physical finger. 
                        if (activeFingerDeviceId != -1 && activeFingerDeviceId != devId) {
                            return true
                        }
                        activeFingerDeviceId = devId
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // GHOST BLOCK: If no physical finger is locked, IGNORE ALL MOVES.
                        if (activeFingerDeviceId == -1) return true
                        
                        // Strict Lock: Only allow the specific locked physical device.
                        if (devId != activeFingerDeviceId) return true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        if (devId == activeFingerDeviceId) {
                            activeFingerDeviceId = -1
                        }
                    }
                }
                
                return super.dispatchTouchEvent(event)
            }
        }

        // [IMPORTANT] Enable security filter on the view itself as a backup
        keyboardContainer?.filterTouchesWhenObscured = true

        val containerBg = GradientDrawable()
        val fillColor = (currentAlpha shl 24) or (0x1A1A1A)
        containerBg.setColor(fillColor)
        containerBg.cornerRadius = 16f
        containerBg.setStroke(2, Color.parseColor("#44FFFFFF"))
        keyboardContainer?.background = containerBg
        
        keyboardContainer?.isFocusable = true
        keyboardContainer?.isFocusableInTouchMode = true
        keyboardContainer?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (handleResizeDpad(keyCode)) {
                    return@setOnKeyListener true
                }
            }
            false
        }

        // 1. The Keyboard Keys
        keyboardView = KeyboardView(context)

        // SHIELD 3: Backup Generic Motion Listener
        keyboardView?.setOnGenericMotionListener { _, event ->
            if (event.isFromSource(InputDevice.SOURCE_MOUSE) || 
                event.isFromSource(InputDevice.SOURCE_MOUSE_RELATIVE)) {
                return@setOnGenericMotionListener true
            }
            false
        }

// =================================================================================
        // SPACEBAR MOUSE CURSOR MOVE CALLBACK BINDING
        // SUMMARY: Forwards cursor movement from KeyboardView's spacebar trackpad feature
        //          to OverlayService. The isDragging parameter indicates whether user is
        //          performing a hold-to-drag operation (true) or just moving cursor (false).
        //          When isDragging=true, OverlayService injects ACTION_MOVE with TOUCHSCREEN
        //          source; when false, it injects HOVER_MOVE with MOUSE source.
        // =================================================================================
        keyboardView?.cursorMoveAction = { dx, dy, isDragging ->
            // Pass through isDragging state to enable hold-to-drag functionality
            onCursorMove?.invoke(dx, dy, isDragging)
        }
        // =================================================================================
        // END BLOCK: SPACEBAR MOUSE CURSOR MOVE CALLBACK BINDING
        // =================================================================================


        keyboardView?.cursorClickAction = { isRight ->
            onCursorClick?.invoke(isRight)
            // Fix: Reset swipe state on click to prevent accidental full-word delete
            lastCommittedSwipeWord = null
            resetComposition()
        }

        keyboardView?.touchDownAction = { 
            onTouchDown?.invoke()
            // Fix: Reset swipe state on drag start
            lastCommittedSwipeWord = null
            resetComposition()
        }
        
        keyboardView?.touchUpAction = { onTouchUp?.invoke() }
        
        keyboardView?.touchTapAction = { 
            onTouchTap?.invoke()
            // Fix: Reset swipe state on tap
            lastCommittedSwipeWord = null
            resetComposition() 
        }


        keyboardView?.mirrorTouchCallback = { x, y, action ->
            val cb = onMirrorTouch
            if (cb != null) cb.invoke(x, y, action) else false
        }

        keyboardView?.setKeyboardListener(this)
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        keyboardView?.setVibrationEnabled(prefs.getBoolean("vibrate", true))
        
        // [FIX] Load saved scale and update Internal State immediately
        // Use 69 as default to match resetPosition logic (prevent 1.0 mismatch)
        val scale = prefs.getInt("keyboard_key_scale", 69) / 100f
        internalScale = scale 
        keyboardView?.setScale(scale)
        
        keyboardView?.alpha = currentAlpha / 255f

        val kbParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        kbParams.setMargins(6, 28, 6, 6)
        keyboardContainer?.addView(keyboardView, kbParams)

        // 2. The Swipe Trail Overlay
        val trailView = SwipeTrailView(context)
        val trailParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        trailParams.setMargins(6, 28, 6, 6)
        keyboardContainer?.addView(trailView, trailParams)
        keyboardView?.attachTrailView(trailView)

        // ORIENTATION TRAIL VIEW
        orientationTrailView = SwipeTrailView(context)
        orientationTrailView?.setTrailColor(0xFFFF9900.toInt())
        val orientTrailParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        orientTrailParams.setMargins(6, 28, 6, 6)
        keyboardContainer?.addView(orientationTrailView, orientTrailParams)


        addDragHandle(); addResizeHandle(); addCloseButton(); addTargetLabel()

        // [FIX] Calculate Dynamic Defaults for "First Load"
        // This prevents the keyboard from loading with wrong aspect ratios (like WRAP_CONTENT)
        // if no preferences exist yet.
        
        // 1. Calculate Default Width (90% Screen)
        val defaultWidth = (screenWidth * 0.90f).toInt().coerceIn(300, 1200)
        
        // 2. Calculate Default Height (300dp * Scale)
        // If no scale is saved, we default to 0.69f (The "Reset" Scale)
        val density = context.resources.displayMetrics.density
        val savedScale = prefs.getInt("keyboard_key_scale", 69) / 100f
        val baseHeightDp = 300f
        val defaultHeight = (baseHeightDp * savedScale * density).toInt()
        
        val defaultX = (screenWidth - defaultWidth) / 2
        val defaultY = (screenHeight / 2)

        // 3. Load from Prefs (using our calculated defaults as fallback)
        val savedW = prefs.getInt("keyboard_width_d$currentDisplayId", defaultWidth)
        val savedH = prefs.getInt("keyboard_height_d$currentDisplayId", defaultHeight)
        val savedX = prefs.getInt("keyboard_x_d$currentDisplayId", defaultX)
        val savedY = prefs.getInt("keyboard_y_d$currentDisplayId", defaultY)

        // 4. Set Fields
        keyboardWidth = savedW
        keyboardHeight = savedH

        keyboardParams = WindowManager.LayoutParams(
            keyboardWidth,
            keyboardHeight,

            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        keyboardParams?.gravity = Gravity.TOP or Gravity.LEFT
        keyboardParams?.x = savedX
        keyboardParams?.y = savedY

        windowManager.addView(keyboardContainer, keyboardParams)
        updateAlpha(currentAlpha)
        
        // [FIX] Initialize Resize Anchors so D-pad/Scaling works immediately
        // This prevents the "Background resizes but Keys don't" bug on fresh load.
        dragStartHeight = keyboardHeight
        dragStartScale = internalScale
    }





    private fun addDragHandle() {
        val handle = FrameLayout(context); val handleParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 28); handleParams.gravity = Gravity.TOP
        val indicator = View(context); val indicatorBg = GradientDrawable(); indicatorBg.setColor(Color.parseColor("#555555")); indicatorBg.cornerRadius = 3f; indicator.background = indicatorBg
        val indicatorParams = FrameLayout.LayoutParams(50, 5); indicatorParams.gravity = Gravity.CENTER; indicatorParams.topMargin = 8
        handle.addView(indicator, indicatorParams); handle.setOnTouchListener { _, event -> handleDrag(event); true }
        keyboardContainer?.addView(handle, handleParams)
    }

    private fun addResizeHandle() {
        val handle = FrameLayout(context); val handleParams = FrameLayout.LayoutParams(36, 36); handleParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        val indicator = View(context); val indicatorBg = GradientDrawable(); indicatorBg.setColor(Color.parseColor("#3DDC84")); indicatorBg.cornerRadius = 4f; indicator.background = indicatorBg; indicator.alpha = 0.7f
        val indicatorParams = FrameLayout.LayoutParams(14, 14); indicatorParams.gravity = Gravity.BOTTOM or Gravity.RIGHT; indicatorParams.setMargins(0, 0, 6, 6)
        handle.addView(indicator, indicatorParams); handle.setOnTouchListener { _, event -> handleResize(event); true }
        keyboardContainer?.addView(handle, handleParams)
    }

    private fun addCloseButton() {
        val button = FrameLayout(context); val buttonParams = FrameLayout.LayoutParams(28, 28); buttonParams.gravity = Gravity.TOP or Gravity.RIGHT; buttonParams.setMargins(0, 2, 4, 0)
        val closeText = TextView(context); closeText.text = "X"; closeText.setTextColor(Color.parseColor("#FF5555")); closeText.textSize = 12f; closeText.gravity = Gravity.CENTER
        // CHANGED: Call onCloseAction to notify Service (handles IME toggle & automation)
        button.addView(closeText, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)); button.setOnClickListener { onCloseAction() }
        keyboardContainer?.addView(button, buttonParams)
    }

    private fun addTargetLabel() {
        val label = TextView(context); label.text = "Display $targetDisplayId"; label.setTextColor(Color.parseColor("#888888")); label.textSize = 9f
        val labelParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT); labelParams.gravity = Gravity.TOP or Gravity.LEFT; labelParams.setMargins(8, 6, 0, 0)
        keyboardContainer?.addView(label, labelParams)
    }

    private fun handleDrag(event: MotionEvent): Boolean {
        if (isAnchored) return true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { 
                isMoving = true
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                initialWindowX = keyboardParams?.x ?: 0
                initialWindowY = keyboardParams?.y ?: 0 
            }
            MotionEvent.ACTION_MOVE -> { 
                if (isMoving) { 
                    keyboardParams?.x = initialWindowX + (event.rawX - initialTouchX).toInt()
                    keyboardParams?.y = initialWindowY + (event.rawY - initialTouchY).toInt()
                    try { windowManager.updateViewLayout(keyboardContainer, keyboardParams) } catch (e: Exception) {} 
                } 
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { 
                isMoving = false
                saveKeyboardPosition() 
            }
        }
        return true
    }

    // [START RESIZE FIX]
    private fun handleResize(event: MotionEvent): Boolean {
        if (isAnchored) return true
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                isResizing = true
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                
                initialWidth = keyboardParams?.width ?: keyboardWidth
                initialHeight = keyboardParams?.height ?: keyboardHeight
                
                // Resolve WRAP_CONTENT to actual pixels
                if (initialWidth < 0) initialWidth = keyboardContainer?.width ?: 300
                if (initialHeight < 0) initialHeight = keyboardContainer?.height ?: 200
                
                // [FIX] Capture stable start values for deterministic scaling
                dragStartHeight = initialHeight
                dragStartScale = internalScale
            }


            MotionEvent.ACTION_MOVE -> {
                if (isResizing) {
                    val dX = (event.rawX - initialTouchX).toInt()
                    val dY = (event.rawY - initialTouchY).toInt()
                    
                    val newW = initialWidth + dX
                    val newH = initialHeight + dY
                    
                    // Use the shared function to handle visual update + scale + sync
                    // Note: We might want to throttle 'saveKeyboardSize' inside applyWindowResize during drag
                    // but for now this ensures visual consistency.
                    applyWindowResize(newW, newH)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isResizing = false
                saveKeyboardSize()
                saveKeyboardScale() // [FIX] Persist scale so aspect ratio survives hide/unhide
            }
        }
        return true
    }
    // [END RESIZE FIX]

    private fun saveKeyboardSize() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_width_d$currentDisplayId", keyboardWidth).putInt("keyboard_height_d$currentDisplayId", keyboardHeight).apply() }
    private fun saveKeyboardScale() { 
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .edit().putInt("keyboard_key_scale", (internalScale * 100).toInt()).apply() 
    }
    private fun saveKeyboardPosition() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_x_d$currentDisplayId", keyboardParams?.x ?: 0).putInt("keyboard_y_d$currentDisplayId", keyboardParams?.y ?: 0).apply() }
    private fun loadKeyboardSizeForDisplay(displayId: Int) { val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); keyboardWidth = prefs.getInt("keyboard_width_d$displayId", keyboardWidth); keyboardHeight = prefs.getInt("keyboard_height_d$displayId", keyboardHeight) }


    // =================================================================================
    // FUNCTION: onKeyPress
    // SUMMARY: Handles key press events from the keyboard. Manages composing word state,
    //          sentence start detection, and auto-learning. Special handling for
    //          punctuation after swiped words to remove the trailing space.
    // =================================================================================
    // =================================================================================
    // FUNCTION: onKeyPress
    // SUMMARY: Handles key press events from the keyboard. Manages composing word state,
    //          sentence start detection, and auto-learning.
    //          
    // UPDATED: 
    //   - Apostrophe (') is now part of words for "don't", "won't", etc.
    //   - Tracks originalCaseWord for "DroidOS", "iPhone" etc.
    //   - Shift keys don't clear composition
    //   - Properly clears prediction bar on space/enter
    // =================================================================================
    override fun onKeyPress(keyCode: Int, char: Char?, metaState: Int) {
        android.util.Log.d("DroidOS_Key", "Press: keyCode=$keyCode char='$char' meta=$metaState")

        // --- SHIFT KEY: Ignore completely, don't affect composition ---
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || 
            keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT ||
            keyCode == KeyEvent.KEYCODE_CAPS_LOCK) {
            // Don't inject shift as a key, just return
            // The keyboard handles shift state internally
            return
        }

        // --- PUNCTUATION AFTER SWIPE: Remove trailing space ---
        val isEndingPunctuation = char != null && (char == '.' || char == ',' || char == '!' ||
                                          char == '?' || char == ';' || char == ':' || char == '"')

        if (isEndingPunctuation && lastCommittedSwipeWord != null && lastCommittedSwipeWord!!.endsWith(" ")) {
            injectKey(KeyEvent.KEYCODE_DEL, 0)
            android.util.Log.d("DroidOS_Swipe", "PUNCTUATION: Removed trailing space before '$char'")
            lastCommittedSwipeWord = lastCommittedSwipeWord!!.trimEnd()
        }

        // 1. Inject the key event
        injectKey(keyCode, metaState)

// 2. Handle Backspace - delete from composing word
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (currentComposingWord.isNotEmpty()) {
                currentComposingWord.deleteCharAt(currentComposingWord.length - 1)
                originalCaseWord.deleteCharAt(originalCaseWord.length - 1)
                android.util.Log.d("DroidOS_Compose", "BACKSPACE: Now composing '$originalCaseWord'")
                updateSuggestions()
            } else {
                // Composing word is empty, clear prediction bar
                updateSuggestionsWithSync(emptyList())
            }
            return
        }

        // 3. Handle Enter - clears everything
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            isSentenceStart = true
            lastCommittedSwipeWord = null
            currentComposingWord.clear()
            originalCaseWord.clear()
            updateSuggestionsWithSync(emptyList())  // CLEAR prediction bar
            return
        }

        // 4. Handle Space - clears composition, clears prediction bar
        if (char != null && Character.isWhitespace(char)) {
            isSentenceStart = false
            lastCommittedSwipeWord = null
            currentComposingWord.clear()
            originalCaseWord.clear()
            updateSuggestionsWithSync(emptyList())  // CLEAR prediction bar
            return
        }

        // 5. Handle ending punctuation (. , ! ? ; : ") - clears composition
        if (isEndingPunctuation) {
            if (char == '.' || char == '!' || char == '?') {
                isSentenceStart = true
            }
            lastCommittedSwipeWord = null
            currentComposingWord.clear()
            originalCaseWord.clear()
            updateSuggestionsWithSync(emptyList())  // CLEAR prediction bar
            return
        }

        // 6. Handle letters, digits, and apostrophe - ADD to composition
        if (char != null && (Character.isLetterOrDigit(char) || char == '\'')) {
            // Clear swipe history when manually typing
            lastCommittedSwipeWord = null
            
            // Add to composition trackers
            currentComposingWord.append(char.lowercaseChar())  // Lowercase for lookup
            originalCaseWord.append(char)  // Original case for display/saving
            
            isSentenceStart = false
            updateSuggestions()
            android.util.Log.d("DroidOS_Compose", "Composing: '$originalCaseWord' (lookup: '$currentComposingWord')")
            return
        }

        // 7. Any other character - ignore, don't clear composition
        // This prevents random symbols from breaking the composition
        android.util.Log.d("DroidOS_Key", "Ignored char: '$char'")
    }
    // =================================================================================
    // END BLOCK: onKeyPress with proper clearing and case tracking
    // =================================================================================
    // =================================================================================
    // END BLOCK: onKeyPress with punctuation spacing fix
    // =================================================================================

    
    override fun onTextInput(text: String) {
        if (shellService == null) return
        Thread { try { val cmd = "input -d $targetDisplayId text \"$text\""; shellService.runCommand(cmd) } catch (e: Exception) { Log.e(TAG, "Text injection failed", e) } }.start()
    }

    override fun onScreenToggle() { onScreenToggleAction() }
    override fun onScreenModeChange() { onScreenModeChangeAction() }

    override fun onSpecialKey(key: KeyboardView.SpecialKey, metaState: Int) {
        if (key == KeyboardView.SpecialKey.VOICE_INPUT) {
            triggerVoiceTyping()
            return
        }
        if (key == KeyboardView.SpecialKey.HIDE_KEYBOARD) {
            onCloseAction() // Calls the close/hide action passed from Service
            return
        }

        // =================================================================================
        // BACKSPACE HANDLING - SWIPE WORD DELETE
        // SUMMARY: If the last action was a swipe word commit, backspace deletes the
        //          entire word (plus trailing space). Otherwise, normal backspace behavior.
        //          This allows quick correction of mis-swiped words.
        // =================================================================================
        if (key == KeyboardView.SpecialKey.BACKSPACE) {
            // CHECK: Was the last input a swiped word?

            if (lastCommittedSwipeWord != null && lastCommittedSwipeWord!!.isNotEmpty()) {
                // Delete the entire swiped word (including trailing space)
                val deleteCount = lastCommittedSwipeWord!!.length
                android.util.Log.d("DroidOS_Swipe", "BACKSPACE: Deleting swiped word '${lastCommittedSwipeWord}' ($deleteCount chars)")

                // [FIX] Use Bulk Delete for reliability (especially with KB Blocker)
                (context as? OverlayService)?.injectBulkDelete(deleteCount)

                // Clear the swipe history so next backspace is normal
                lastCommittedSwipeWord = null

                
                // Also clear composition state
                currentComposingWord.clear()
                originalCaseWord.clear()
                updateSuggestionsWithSync(emptyList())

                // Don't inject another backspace - we already deleted
                return
            }

            // Normal backspace: delete from composing word
            if (currentComposingWord.isNotEmpty()) {
                currentComposingWord.deleteCharAt(currentComposingWord.length - 1)
                originalCaseWord.deleteCharAt(originalCaseWord.length - 1)
                android.util.Log.d("DroidOS_Compose", "BACKSPACE (special): Now composing '$originalCaseWord'")
                updateSuggestions()
            } else {
                // Nothing to delete from composition, clear prediction bar
                updateSuggestionsWithSync(emptyList())
            }
        } else if (key == KeyboardView.SpecialKey.SPACE) {
            // Space clears swipe history (user is continuing to type)
            lastCommittedSwipeWord = null
            resetComposition()
        } else {
            // Enter, Tabs, Arrows all break the current word chain
            lastCommittedSwipeWord = null
            resetComposition()
        }
        // =================================================================================
        // END BLOCK: BACKSPACE HANDLING - SWIPE WORD DELETE
        // =================================================================================

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
            KeyboardView.SpecialKey.MUTE -> KeyEvent.KEYCODE_VOLUME_MUTE
            KeyboardView.SpecialKey.VOL_UP -> KeyEvent.KEYCODE_VOLUME_UP
            KeyboardView.SpecialKey.VOL_DOWN -> KeyEvent.KEYCODE_VOLUME_DOWN
            KeyboardView.SpecialKey.BACK_NAV -> KeyEvent.KEYCODE_BACK
            KeyboardView.SpecialKey.FWD_NAV -> KeyEvent.KEYCODE_FORWARD
            else -> return
        }
        injectKey(keyCode, metaState)
    }


    // =================================================================================
    // FUNCTION: onSuggestionClick
    // SUMMARY: Handles when user taps a word in the prediction bar.
    //          SCENARIO 1: Swipe Correction (Replaces last committed word)
    //          SCENARIO 2: Manual Typing (Replaces current composing characters)
    // =================================================================================
    override fun onSuggestionClick(text: String, isNew: Boolean) {
        android.util.Log.d("DroidOS_Prediction", "Suggestion clicked: '$text' (isNew=$isNew)")

// 1. Learn word if it was flagged as New
        // Pass isSentenceStart so learnWord knows whether to strip auto-capitalization
        if (isNew) {
            // Note: We use the CURRENT isSentenceStart state at time of click
            // If user typed "DroidOS" at sentence start, we strip the D
            // If user typed "DroidOS" mid-sentence, we keep DroidOS
            predictionEngine.learnWord(context, text, isSentenceStart)
            android.util.Log.d("DroidOS_Learn", "Learning new word: '$text' (sentenceStart=$isSentenceStart)")
        }

        // 2. Handle Deletion (Key Injection)
        if (!lastCommittedSwipeWord.isNullOrEmpty()) {
            // SCENARIO 1: Correcting a previously swiped word
            // We must delete the full word + the space we added
            val deleteCount = lastCommittedSwipeWord!!.length
            for (i in 0 until deleteCount) {
                injectKey(KeyEvent.KEYCODE_DEL, 0)
            }
        } else if (currentComposingWord.isNotEmpty()) {
            // SCENARIO 2: Completing a manually typed word (e.g. "partia" -> "partially")
            // We delete the characters typed so far
            val deleteCount = currentComposingWord.length
            for (i in 0 until deleteCount) {
                injectKey(KeyEvent.KEYCODE_DEL, 0)
            }
        }

        // 3. Insert new word (always add space for flow)
        val newText = "$text "
        injectText(newText)
        
        // 4. Update State
        lastCommittedSwipeWord = newText
        currentComposingWord.clear()
        originalCaseWord.clear()
        
        // Clear suggestions immediately since we just committed
        updateSuggestionsWithSync(emptyList()) 
        android.util.Log.d("DroidOS_Suggest", "Cleared suggestions after suggestion click")
    }


    // =================================================================================
    // END BLOCK: onSuggestionClick with reliable replacement
    // =================================================================================

    // =================================================================================
    // FUNCTION: onSuggestionDropped
    // SUMMARY: Called when user drags a suggestion to backspace to delete/block it.
    //          DEBUG: Logging to confirm this is being called.
    // =================================================================================
    override fun onSuggestionDropped(text: String) {
        android.util.Log.d("DroidOS_Drag", ">>> onSuggestionDropped CALLED: '$text'")

        // Block the word
        predictionEngine.blockWord(context, text)

        android.widget.Toast.makeText(context, "Removed: $text", android.widget.Toast.LENGTH_SHORT).show()

        // Refresh suggestions to remove the blocked word
        updateSuggestions()

        android.util.Log.d("DroidOS_Drag", "<<< onSuggestionDropped COMPLETE: '$text' blocked")
    }
    // =================================================================================
    // END BLOCK: onSuggestionDropped with debug logging
    // =================================================================================

    // Layer change notification for mirror keyboard sync
    override fun onLayerChanged(state: KeyboardView.KeyboardState) {
        onLayerChanged?.invoke(state)
    }

    // =================================================================================
    // FUNCTION: onSwipeDetected
    // SUMMARY: Handles swipe gesture completion. Runs decoding in background thread.
    //          OPTIMIZED: Reduced logging for better performance.
    // =================================================================================
    // =================================================================================
    // FUNCTION: onSwipeDetectedTimed (Time-Weighted Swipe Handler)
    // SUMMARY: Receives swipe path WITH timestamps for dwell-based word disambiguation.
    //          Calls PredictionEngine.decodeSwipeTimed for time-aware scoring.
    //          This allows users to linger on keys to select less common words.
    //          Example: Linger on "U" to get "four" instead of "for".
    // =================================================================================
    override fun onSwipeDetectedTimed(path: List<TimedPoint>) {
        if (keyboardView == null || path.size < 3) return

        val keyMap = keyboardView?.getKeyCenters()
        if (keyMap.isNullOrEmpty()) return

        // Run time-weighted prediction in background
        Thread {
            try {
                val suggestions = predictionEngine.decodeSwipeTimed(path, keyMap)
                
                if (suggestions.isEmpty()) {
                    android.util.Log.d("DroidOS_Swipe", "TIMED DECODE: No suggestions returned")
                    return@Thread
                }
                
                android.util.Log.d("DroidOS_Swipe", "TIMED DECODE: Got ${suggestions.size} suggestions: ${suggestions.joinToString(", ")}")

                handler.post {
                    var bestMatch = suggestions[0]
                    val isCap = isSentenceStart

                    if (isCap) {
                        bestMatch = bestMatch.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    }

val displaySuggestions = if (isCap) {
                        suggestions.map { it.replaceFirstChar { c -> c.titlecase() } }
                    } else {
                        suggestions
                    }.map { word -> 
                        KeyboardView.Candidate(
                            text = word, 
                            isNew = false,
                            isCustom = predictionEngine.isCustomWord(word)
                        )
                    }

                    updateSuggestionsWithSync(displaySuggestions)

                    // Commit text with proper spacing
                    var textToCommit = bestMatch
                    if (currentComposingWord.isNotEmpty()) {
                        textToCommit = " $bestMatch"
                        currentComposingWord.clear()
                    }
                    textToCommit = "$textToCommit "

                    injectText(textToCommit)
                    lastCommittedSwipeWord = textToCommit
                    isSentenceStart = false
                    
                    // Clear composition state since we just committed a swipe word
                    currentComposingWord.clear()
                    originalCaseWord.clear()
                    // NOTE: Don't clear suggestions here - we want to show alternatives
                }
            } catch (e: Exception) {
                android.util.Log.e("DroidOS_Swipe", "Timed swipe decode error: ${e.message}")
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: onSwipeDetectedTimed
    // =================================================================================

    // =================================================================================
    // FUNCTION: onSwipeDetected (Legacy - now just logs, actual work done in Timed version)
    // =================================================================================
    override fun onSwipeDetected(path: List<android.graphics.PointF>) {
        if (keyboardView == null) return
        
        val keyMap = keyboardView?.getKeyCenters()
        if (keyMap.isNullOrEmpty()) return

        Thread {
            try {
                val suggestions = predictionEngine.decodeSwipe(path, keyMap)

                if (suggestions.isNotEmpty()) {
                    handler.post {
                        var bestMatch = suggestions[0]
                        if (isSentenceStart) {
                            bestMatch = bestMatch.replaceFirstChar { 
                                if (it.isLowerCase()) it.titlecase() else it.toString() 
                            }
                        }

                        val isCap = Character.isUpperCase(bestMatch.firstOrNull() ?: ' ')
                        val displaySuggestions = if (isCap) {
                            suggestions.map { it.replaceFirstChar { c -> c.titlecase() } }
                        } else {
                            suggestions
                        }.map { KeyboardView.Candidate(it, isNew = false) }

                        updateSuggestionsWithSync(displaySuggestions)

                        // Commit text with proper spacing
                        var textToCommit = bestMatch
                        if (currentComposingWord.isNotEmpty()) {
                            textToCommit = " $bestMatch"
                            currentComposingWord.clear()
                        }
                        textToCommit = "$textToCommit "

                        injectText(textToCommit)
                        lastCommittedSwipeWord = textToCommit
                        isSentenceStart = false
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DroidOS_Swipe", "Swipe decode error: ${e.message}")
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: onSwipeDetected
    // =================================================================================

    // =================================================================================
    // FUNCTION: onSwipeProgress (LIVE SWIPE PREVIEW)
    // SUMMARY: Called during swipe to show real-time predictions as user swipes.
    //          Uses a lightweight prediction that doesn't commit text.
    //          This helps users see what word will be typed and helps debug.
    // =================================================================================
// =================================================================================
    // FUNCTION: onSwipeProgress (LIVE SWIPE PREVIEW - Single Prediction)
    // SUMMARY: Called during swipe to show real-time prediction as user swipes.
    //          Shows ONLY the top prediction (like GBoard) for cleaner UX.
    //          Full suggestions shown on swipe completion in onSwipeDetected.
    // =================================================================================
    override fun onSwipeProgress(path: List<android.graphics.PointF>) {
        if (keyboardView == null || path.size < 5) return
        
        val keyMap = keyboardView?.getKeyCenters()
        if (keyMap.isNullOrEmpty()) return

        // Run prediction in background to avoid UI lag
        Thread {
            try {
                val suggestions = predictionEngine.decodeSwipePreview(path, keyMap)

                if (suggestions.isNotEmpty()) {
                    handler.post {
                        // Get only the TOP prediction
                        var topPrediction = suggestions[0]
                        
                        // Apply capitalization if at sentence start
                        if (isSentenceStart) {
                            topPrediction = topPrediction.replaceFirstChar { 
                                if (it.isLowerCase()) it.titlecase() else it.toString() 
                            }
                        }

                        // Show ONLY the top prediction (single item list)
val singleSuggestion = listOf(KeyboardView.Candidate(
                            text = topPrediction, 
                            isNew = false,
                            isCustom = predictionEngine.isCustomWord(topPrediction)
                        ))
                        updateSuggestionsWithSync(singleSuggestion)
                    }
                }
            } catch (e: Exception) {
                // Silently ignore errors during preview
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: onSwipeProgress
    // =================================================================================
    // =================================================================================
    // END BLOCK: onSwipeProgress
    // =================================================================================


// =================================================================================
    // FUNCTION: updateSuggestions
    // SUMMARY: Updates the suggestion bar based on current composing word.
    //          
    // STYLING:
    //   - isNew=true (RED): Word not in any dictionary, can be added
    //   - isCustom=true (ITALIC): Word is in user dictionary  
    //   - Both false (WHITE BOLD): Word is in main dictionary
    // =================================================================================
    private fun updateSuggestions() {
        val prefix = currentComposingWord.toString()  // Lowercase for lookup
        val displayPrefix = originalCaseWord.toString()  // Original case for display
        
        if (prefix.isEmpty()) {
            updateSuggestionsWithSync(emptyList())
            return
        }

        // Strip apostrophe for dictionary lookup
        val lookupPrefix = prefix.replace("'", "")
        
        // Get dictionary suggestions
        val suggestions = if (lookupPrefix.isNotEmpty()) {
            predictionEngine.getSuggestions(lookupPrefix, 3)
        } else {
            emptyList()
        }
        
        val candidates = ArrayList<KeyboardView.Candidate>()

        // 1. Add Raw Input with ORIGINAL CASE as first option
        val rawExistsInMain = predictionEngine.hasWord(lookupPrefix) || predictionEngine.hasWord(prefix)
        val rawIsCustom = predictionEngine.isCustomWord(lookupPrefix) || predictionEngine.isCustomWord(prefix)
        val rawIsNew = !rawExistsInMain && !rawIsCustom
        
        candidates.add(KeyboardView.Candidate(
            text = displayPrefix, 
            isNew = rawIsNew,
            isCustom = rawIsCustom
        ))

// 2. Add dictionary suggestions (avoiding duplicates)
        // Apply display forms for proper capitalization (DroidOS, iPhone, etc.)
        for (s in suggestions) {
            val displayForm = predictionEngine.getDisplayForm(s)
            if (!displayForm.equals(lookupPrefix, ignoreCase = true) && 
                !displayForm.equals(prefix, ignoreCase = true) &&
                !displayForm.equals(displayPrefix, ignoreCase = true)) {
                // Check if this suggestion is a custom word
                val isCustom = predictionEngine.isCustomWord(s)
                candidates.add(KeyboardView.Candidate(
                    text = displayForm,  // Use display form with proper caps
                    isNew = false,
                    isCustom = isCustom
                ))
            }
        }

        updateSuggestionsWithSync(candidates.take(3))
        android.util.Log.d("DroidOS_Suggest", "Updated: ${candidates.map { "${it.text}(new=${it.isNew},custom=${it.isCustom})" }}")
    }
    // =================================================================================
    // END BLOCK: updateSuggestions with styling flags
    // =================================================================================

    // =================================================================================
    // FUNCTION: resetSwipeHistory
    // SUMMARY: Public access to reset swipe state. Called by OverlayService when
    //          external cursor movement (e.g. touching the app) is detected.
    // =================================================================================
    fun resetSwipeHistory() {
        if (lastCommittedSwipeWord != null) {
            android.util.Log.d("DroidOS_Swipe", "External cursor move detected -> Reset swipe history")
        }
        lastCommittedSwipeWord = null
        resetComposition()
    }

    private fun resetComposition() {
        currentComposingWord.clear()
        originalCaseWord.clear()
        updateSuggestionsWithSync(emptyList())
    }


    private fun injectKey(keyCode: Int, metaState: Int) {
        (context as? OverlayService)?.injectKeyFromKeyboard(keyCode, metaState)
    }

    // --- Voice Logic & Mic Check Loop ---
    
    // Handler for the 1-second loop
    private val micCheckHandler = Handler(Looper.getMainLooper())
    
    // Runnable that checks if the Microphone is currently recording
    private val micCheckRunnable = object : Runnable {
        override fun run() {
            try {
                val audioManager = context.getSystemService(android.content.Context.AUDIO_SERVICE) as AudioManager
                
                // Use activeRecordingConfigurations (API 24+) to check if any app is recording
                var isMicOn = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (audioManager.activeRecordingConfigurations.isNotEmpty()) {
                        isMicOn = true
                    }
                }
                
                if (isMicOn) {
                    // Still recording, check again in 1 second
                    micCheckHandler.postDelayed(this, 1000)
                } else {
                    // Mic stopped (or not supported), turn off the green light
                    keyboardView?.setVoiceActive(false)
                }
            } catch (e: Exception) {
                // If check fails, fail safe to off
                keyboardView?.setVoiceActive(false)
            }
        }
    }

    private fun triggerVoiceTyping() {
        if (shellService == null) return

        // 1. UI: Turn Button Green Immediately
        keyboardView?.setVoiceActive(true)
        
        // 2. Start Monitoring Loop
        // Delay 3 seconds to allow the Voice IME to open and start recording
        micCheckHandler.removeCallbacks(micCheckRunnable)
        micCheckHandler.postDelayed(micCheckRunnable, 3000)

        // 3. Notify Service to stop blocking touches
        val intent = android.content.Intent("VOICE_TYPE_TRIGGERED")
        intent.setPackage(context.packageName)
        context.sendBroadcast(intent)

        // 4. Perform IME Switch via Shell
        Thread {
            try {
                // Fetch IME list and find Google Voice Typing
                val output = shellService?.runCommand("ime list -a -s") ?: ""
                val voiceIme = output.lines().find { it.contains("google", true) && it.contains("voice", true) } 
                    ?: output.lines().find { it.contains("voice", true) }
                
                if (voiceIme != null) {
                    shellService?.runCommand("ime set $voiceIme")
                } else {
                    android.util.Log.w(TAG, "Voice IME not found")
                    // If IME missing, turn off light
                    micCheckHandler.post { keyboardView?.setVoiceActive(false) }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Voice Switch Failed", e)
                micCheckHandler.post { keyboardView?.setVoiceActive(false) }
            }
        }.start()
    }

    // [FIX] Helper to fix Z-Order relative to Trackpad
    fun bringToFront() {
        if (!isVisible || keyboardContainer == null) return
        try {
            // Remove and Re-add to move to the top of the WindowManager stack
            windowManager.removeView(keyboardContainer)
            windowManager.addView(keyboardContainer, keyboardParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/KeyboardPickerActivity.kt
```kotlin
package com.example.coverscreentester

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardPickerActivity : Activity() {
    
    private var hasLaunchedPicker = false
    private var pickerWasOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Transparent touchable view
        val view = View(this)
        view.setBackgroundColor(0x00000000) 
        view.isClickable = true
        
        // Safety Net: If logic fails, user can tap screen to close
        view.setOnClickListener { finish() }
        setContentView(view)
        
        // Launch picker after window is ready
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) launchPicker()
        }, 300)
    }

    private fun launchPicker() {
        if (hasLaunchedPicker) return
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
            hasLaunchedPicker = true
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        
        if (hasLaunchedPicker) {
            if (!hasFocus) {
                // We lost focus -> The Picker Dialog is now showing
                pickerWasOpened = true
            } else if (hasFocus && pickerWasOpened) {
                // We regained focus -> The Picker Dialog just closed
                // We are done.
                finish()
            }
        }
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
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt
import android.annotation.SuppressLint
import android.util.Log

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

// =================================================================================
    // DATA CLASS: Candidate
    // SUMMARY: Represents a word suggestion in the prediction bar.
    //   - text: The word to display
    //   - isNew: True if word is NOT in any dictionary (shown in RED, can be added)
    //   - isCustom: True if word is in USER dictionary (shown in ITALIC)
    // =================================================================================
    data class Candidate(
        val text: String, 
        val isNew: Boolean = false,
        val isCustom: Boolean = false
    )
    // =================================================================================
    // END BLOCK: Candidate data class
    // =================================================================================

    // =================================================================================
    // INTERFACE: KeyboardListener
    // SUMMARY: Callbacks for keyboard events including key presses, swipe gestures,
    //          and the NEW live swipe preview for real-time predictions.
    // =================================================================================
    interface KeyboardListener {
        fun onKeyPress(keyCode: Int, char: Char?, metaState: Int)
        fun onTextInput(text: String)
        fun onSpecialKey(key: SpecialKey, metaState: Int)
        fun onScreenToggle()
        fun onScreenModeChange()
        fun onSuggestionClick(text: String, isNew: Boolean) // Updated
        fun onSwipeDetected(path: List<android.graphics.PointF>)
        fun onSuggestionDropped(text: String) // New: Drag to Delete
        fun onLayerChanged(state: KeyboardState) // Sync to mirror keyboard

        // NEW: Live swipe preview - called during swipe to show predictions in real-time
        // This enables GBoard-style "predict as you swipe" functionality
        fun onSwipeProgress(path: List<android.graphics.PointF>) {}  // Default empty impl for backwards compat
        
        // =======================================================================
        // NEW: Time-weighted swipe detection for dwell-based disambiguation
        // Called with TimedPoints so PredictionEngine can detect key lingering
        // =======================================================================
        fun onSwipeDetectedTimed(path: List<TimedPoint>) {}  // Default empty for backwards compat
        // =======================================================================
        // END BLOCK: Time-weighted swipe detection
        // =======================================================================
    }
    // =================================================================================
    // END BLOCK: KeyboardListener interface
    // =================================================================================

    enum class SpecialKey {
        BACKSPACE, ENTER, SPACE, SHIFT, CAPS_LOCK, SYMBOLS, ABC,
        TAB, ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT,
        HOME, END, DELETE, ESCAPE, CTRL, ALT,
        VOL_UP, VOL_DOWN, MUTE, BACK_NAV, FWD_NAV, VOICE_INPUT, HIDE_KEYBOARD
    }

    enum class KeyboardState {
        LOWERCASE, UPPERCASE, CAPS_LOCK, SYMBOLS_1, SYMBOLS_2
    }

    private var listener: KeyboardListener? = null
    private var currentState = KeyboardState.LOWERCASE
    private var vibrationEnabled = true

    private var isCtrlActive = false
    private var isAltActive = false


    private var isVoiceActive = false

    // [NEW] Mirror Mode & Visual Helpers
    private var isMirrorMode = false
    fun setMirrorMode(active: Boolean) { isMirrorMode = active }




    fun highlightKey(tag: String, active: Boolean, color: Int) {
        val view = findViewWithTag<View>(tag) ?: return
        
        // TARGET THE VISUAL ELEMENT: 
        // 1. Try the container background
        var bg = view.background
        
        // 2. If container has no background, try the child TextView/ImageView
        if (bg == null && view is ViewGroup && view.childCount > 0) {
            bg = view.getChildAt(0).background
        }

        if (bg != null) {
            if (active) {
                // FORCE RED using PorterDuff (Works on any Drawable type)
                bg.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP)
            } else {
                // RESET: Clear the filter to restore original look
                bg.clearColorFilter()
            }
            view.invalidate()
        }
    }



    // [NEW] Helper for Mirror Keyboard to refresh itself locally
    fun removeCandidateAtIndex(index: Int) {
        if (index < 0 || index >= currentCandidates.size) return
        currentCandidates.removeAt(index)
        // Add empty placeholder to keep alignment
        while (currentCandidates.size < 3) currentCandidates.add(Candidate("", false))
        setSuggestions(ArrayList(currentCandidates))
    }



    // =================================================================================
    // LIVE SWIPE PREVIEW THROTTLING

    // SUMMARY: Variables to control how often we send live swipe previews.
    //          Too frequent = laggy, too slow = not responsive.
    // =================================================================================
    private var lastSwipePreviewTime = 0L
    private val SWIPE_PREVIEW_INTERVAL_MS = 150L  // Update predictions every 150ms
    private val SWIPE_PREVIEW_MIN_POINTS = 5      // Need at least 5 points before previewing
    // =================================================================================
    // END BLOCK: LIVE SWIPE PREVIEW THROTTLING
    // =================================================================================

    // =================================================================================
    // VIRTUAL MIRROR ORIENTATION MODE STATE
    // SUMMARY: When true, key input is blocked and touches are forwarded for
    //          orientation trail rendering. Set by KeyboardOverlay during mirror mode.
    // =================================================================================
    private var isOrientationModeActive = false
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR ORIENTATION MODE STATE
    // =================================================================================

    private val BASE_KEY_HEIGHT = 40
    private val BASE_FONT_SIZE = 14f
    private var scaleFactor = 1.0f
    
    private var keyHeight = BASE_KEY_HEIGHT
    private var keySpacing = 2
    private var fontSize = BASE_FONT_SIZE

    // --- REPEAT LOGIC ---
    private val repeatHandler = Handler(Looper.getMainLooper())
    private var currentRepeatKey: String? = null
    private var isRepeating = false
    private val REPEAT_INITIAL_DELAY = 400L
    private val REPEAT_INTERVAL = 50L 

    // --- MULTITOUCH STATE ---
    private val activePointers = SparseArray<View>()
    
    // Caps Lock Logic
    private var capsLockPending = false
    private val capsHandler = Handler(Looper.getMainLooper())

    private val repeatRunnable = object : Runnable {
        override fun run() {
            currentRepeatKey?.let { key ->
                if (isRepeating) {
                    handleKeyPress(key, fromRepeat = true)
                    repeatHandler.postDelayed(this, REPEAT_INTERVAL)
                }
            }
        }
    }
    
    private val capsLockRunnable = Runnable {
        capsLockPending = true
        toggleCapsLock()
    }

    // UI Elements for Suggestions
    private var suggestionStrip: LinearLayout? = null
    // Track current suggestions for blocking logic
    private var currentCandidates: MutableList<Candidate> = ArrayList()
    private var cand1: TextView? = null
    private var cand2: TextView? = null
    private var cand3: TextView? = null
    private var div1: View? = null
    private var div2: View? = null
    private val handler = Handler(Looper.getMainLooper())

// =================================================================================
    // GESTURE TYPING STATE
    // SUMMARY: Variables for swipe/gesture typing detection. Swipe is only tracked for
    //          single-finger gestures. Multitouch cancels swipe detection to prevent
    //          false triggers during fast two-thumb typing.
    // =================================================================================
    private var swipeTrail: SwipeTrailView? = null
    private val keyCenters = HashMap<String, android.graphics.PointF>()
    private var isSwiping = false
    private val SWIPE_THRESHOLD = 35f // pixels of movement to trigger swipe mode (increased from 20)
    private val SWIPE_MIN_DISTANCE = 80f // minimum start-to-end distance for valid swipe
    private val SWIPE_MIN_PATH_POINTS = 10 // minimum path points for valid swipe (increased from 5)
    private var startTouchX = 0f
    private var startTouchY = 0f
    private var swipePointerId = -1 // Track which pointer started the swipe (-1 = none)

    // Store the full path for the decoder WITH TIMESTAMPS for dwell detection
    private val currentPath = ArrayList<TimedPoint>()
    // =================================================================================
    // END BLOCK: GESTURE TYPING STATE
    // =================================================================================


    // --- SPACEBAR TRACKPAD VARIABLES ---

    var isPredictiveBarEmpty: Boolean = true

    
    // Actions triggered by Spacebar Trackpad
    var cursorMoveAction: ((Float, Float, Boolean) -> Unit)? = null // dx, dy, isDragging
    var cursorClickAction: ((Boolean) -> Unit)? = null // isRight

    var touchDownAction: (() -> Unit)? = null
    var touchUpAction: (() -> Unit)? = null
    var touchTapAction: (() -> Unit)? = null


    // =================================================================================
    // VIRTUAL MIRROR MODE CALLBACK
    // SUMMARY: Callback to forward touch events to OverlayService for mirror keyboard.
    //          Called at the START of every touch event. If it returns true, the touch
    //          is in orientation mode and normal key input should be blocked.
    // @param x - Touch X coordinate
    // @param y - Touch Y coordinate  
    // @param action - MotionEvent action (DOWN, MOVE, UP, CANCEL)
    // @return true if orientation mode is active (block key input), false otherwise
    // =================================================================================
    var mirrorTouchCallback: ((Float, Float, Int) -> Boolean)? = null
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE CALLBACK
    // =================================================================================

    private var spacebarPointerId = -1
    private var isSpaceTrackpadActive = false
    private var lastSpaceX = 0f
    private var lastSpaceY = 0f
    private val touchSlop = 15f
    private val cursorSensitivity = 2.5f
    private var currentActiveKey: View? = null

    // Touch Mode State
    private var isTrackpadTouchMode = false

    private val trackpadResetRunnable = Runnable { 
        isTrackpadTouchMode = false
        
        // Find Spacebar and turn off highlight
        val spaceView = findViewWithTag<View>("SPACE")
        if (spaceView != null) {
            setKeyVisual(spaceView, false, "SPACE")
            spaceView.invalidate() // Force redraw
        }
        this.invalidate() // Force keyboard redraw
    }


    // Hold / Drag Logic
    private var isDragging = false
    private var hasMovedWhileDown = false

    private val holdToDragRunnable = Runnable {
        if (isTrackpadTouchMode) {
            isDragging = true
            touchDownAction?.invoke() // Inject Down
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            startTrackpadTimer()
            android.util.Log.d("SpaceTrackpad", "Hold detected -> Touch DOWN injected, drag mode activated")
        }
    }


    private fun startTrackpadTimer() {
        isTrackpadTouchMode = true
        
        // FORCE VISUAL: Keep Spacebar Green while timer is active
        val spaceView = findViewWithTag<View>("SPACE")
        if (spaceView != null) {
            setKeyVisual(spaceView, true, "SPACE")
            spaceView.invalidate()
        }
        
        handler.removeCallbacks(trackpadResetRunnable)
        handler.postDelayed(trackpadResetRunnable, 1000)
    }



    fun blockPredictionAtIndex(index: Int) {
        if (index < 0 || index >= currentCandidates.size) return

        val candidate = currentCandidates[index]
        val wordToBlock = candidate.text
        
        Log.d("KeyboardView", "Blocking prediction: $wordToBlock")

        // 1. Block in Engine (Prevent it from appearing again)
        // FIX: Use 'PredictionEngine.instance' instead of 'predictionEngine'
        PredictionEngine.instance.blockWord(context, wordToBlock) 

        // 2. Remove from UI immediately
        currentCandidates.removeAt(index)
        
        // 3. Refresh the Suggestion Bar with the updated list
        setSuggestions(ArrayList(currentCandidates))
    }




    private fun handleSpacebarClick(xRelativeToKey: Float, keyWidth: Int) {
        val zone = xRelativeToKey / keyWidth
        // 0: Left (0-33%), 2: Middle (33-66%), 1: Right (66-100%)
        val isRightClick = zone > 0.66f
        
        // Trigger click in OverlayService
        cursorClickAction?.invoke(isRightClick)
        
        performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }

    private fun moveMouse(dx: Float, dy: Float) {
        if (dx == 0f && dy == 0f) return

        // Active movement triggers Touch Mode
        startTrackpadTimer()

        // Send delta to OverlayService to move the fake cursor
        // Pass dragging state to service
        cursorMoveAction?.invoke(dx * cursorSensitivity, dy * cursorSensitivity, isDragging)
    }



    // We attach the trail view externally from the Overlay
    fun attachTrailView(view: SwipeTrailView) {
        this.swipeTrail = view
    }

    // Expose key centers for the overlay to retrieve
    fun getKeyCenters(): Map<String, android.graphics.PointF> {
        return keyCenters
    }

    private val lowercaseRows = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m", "BKSP")
    )

    private val uppercaseRows = listOf(
        listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
        listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
        listOf("SHIFT", "Z", "X", "C", "V", "B", "N", "M", "BKSP")
    )

    private val symbols1Rows = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        listOf("@", "#", "\$", "%", "&", "-", "+", "(", ")"),
        listOf("SYM2", "*", "\"", "'", ":", ";", "!", "?", "BKSP")
    )

    private val symbols2Rows = listOf(
        listOf("~", "`", "|", "^", "=", "{", "}", "[", "]", "\\"),
        listOf("<", ">", "/", "_", "©", "®", "™", "°", "•"),
        // Replaced MODE with root to keep layout balanced, MODE is now contextual on SCREEN key
        listOf("√", "€", "£", "¥", "¢", "§", "¶", "∆", "BKSP")
    )

    // Row 4 
    private val row4Lower = listOf("SYM", ",", "SPACE", ".")
    private val row4Sym = listOf("ABC", ",", "SPACE", ".")

    // Row 5 
    private val arrowRow = listOf("TAB", "CTRL", "ALT", "←", "↑", "↓", "→", "ESC")
    
    // Row 6 (Moved SCREEN to far left)
    private val navRow = listOf("SCREEN", "HIDE_KB", "MUTE", "VOL-", "VOL+", "BACK", "FWD", "MIC")

    // Keys allowed to repeat when held

    private val alwaysRepeatable = setOf(
        "BKSP", "⌫", "DEL", "SPACE", "ENTER", 
        "◄", "▲", "▼", "►", 
        "VOL_UP", "VOL_DOWN", "FWD_DEL", "MUTE",
        "HOME", "END", "PGUP", "PGDN"
    )


    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#1A1A1A"))
        setPadding(4, 4, 4, 4)
        buildKeyboard()
    }

    fun setKeyboardListener(l: KeyboardListener) { listener = l }
    fun setVibrationEnabled(enabled: Boolean) { vibrationEnabled = enabled }
    
    fun setScale(scale: Float) {
        this.scaleFactor = scale.coerceIn(0.5f, 2.0f)
        this.keyHeight = (BASE_KEY_HEIGHT * scaleFactor).toInt()
        this.fontSize = BASE_FONT_SIZE * scaleFactor
        buildKeyboard()
    }

    fun setVoiceActive(active: Boolean) {
        if (isVoiceActive != active) {
            isVoiceActive = active
            // Try to find and update just the MIC key to save resources
            val micView = findViewWithTag<View>("MIC")
            if (micView != null) {
                setKeyVisual(micView, false, "MIC")
            } else {
                invalidate() // Fallback: Redraw full view
            }
        }
    }

    // =================================================================================
    // FUNCTION: setOrientationModeActive
    // SUMMARY: Sets whether orientation mode is active. When active, normal key input
    //          is blocked to allow the user to see the orientation trail and find
    //          their finger position on the physical keyboard.
    // @param active - true to block key input, false to resume normal operation
    // =================================================================================
    fun setOrientationModeActive(active: Boolean) {
        isOrientationModeActive = active

        // If exiting orientation mode, clear any pending key states
        if (!active) {
            currentActiveKey?.let {
                val tag = it.tag as? String
                if (tag != null) setKeyVisual(it, false, tag)
            }
            currentActiveKey = null
        }
    }
    // =================================================================================
    // END BLOCK: setOrientationModeActive
    // =================================================================================

    // =================================================================================
    // FUNCTION: startSwipeFromPosition
    // SUMMARY: Initializes swipe tracking from a given position mid-gesture.
    //          Called when switching from orange (orientation) to blue (typing) trail.
    //          Sets up all the swipe state so subsequent MOVE events are tracked.
    // =================================================================================
    fun startSwipeFromPosition(x: Float, y: Float) {
        Log.d("KeyboardView", "Starting swipe from position ($x, $y)")

        // Initialize swipe tracking as if this was the ACTION_DOWN point
        startTouchX = x
        startTouchY = y
        isSwiping = true  // Already swiping
        swipePointerId = 0  // Assume primary pointer

        // Clear and start the blue trail
        swipeTrail?.clear()
        swipeTrail?.visibility = View.VISIBLE
        swipeTrail?.addPoint(x, y)

        // Start the path collection with timestamp
        currentPath.clear()
        currentPath.add(TimedPoint(x, y, System.currentTimeMillis()))
    }
    // =================================================================================
    // END BLOCK: startSwipeFromPosition
    // =================================================================================

    // =================================================================================
    // FUNCTION: handleDeferredTap
    // SUMMARY: Called when a quick tap happens during mirror orientation mode.
    //          Handles all keys including spacebar for single character input.
    //          Also handles taps on prediction bar candidates.
    // =================================================================================
    fun handleDeferredTap(x: Float, y: Float) {
        // First, check if tap is on a prediction candidate
        val tappedCandidate = findCandidateAt(x, y)
        if (tappedCandidate != null) {
            Log.d("KeyboardView", "Deferred tap on prediction: '${tappedCandidate.first}'")
            listener?.onSuggestionClick(tappedCandidate.first, tappedCandidate.second)
            return
        }

        // Otherwise, check for keyboard key
        val touchedView = findKeyView(x, y)
        val keyTag = touchedView?.tag as? String

        if (touchedView != null && keyTag != null) {
            Log.d("KeyboardView", "Deferred tap on key: $keyTag")

            if (keyTag == "SPACE") {
                // For spacebar, trigger space character
                listener?.onSpecialKey(SpecialKey.SPACE, 0)
            } else {
                // For other keys, trigger the full key press sequence
                onKeyDown(keyTag, touchedView)
                onKeyUp(keyTag, touchedView)
            }
        }
    }
    // =================================================================================
    // END BLOCK: handleDeferredTap
    // =================================================================================
// =================================================================================
    // FUNCTION: getKeyAtPosition
    // SUMMARY: Returns the key tag at the given coordinates, or null if no key found.
    //          Used by mirror mode to check if finger is on a repeatable key.
    // =================================================================================
    fun getKeyAtPosition(x: Float, y: Float): String? {
        val touchedView = findKeyView(x, y)
        return touchedView?.tag as? String
    }
    // =================================================================================
    // END BLOCK: getKeyAtPosition
    // =================================================================================

    // =================================================================================
    // FUNCTION: triggerKeyPress
    // SUMMARY: Triggers a key press by key tag without going through touch events.
    //          Used by mirror mode key repeat for backspace/arrow key repetition.
    //          Directly calls handleKeyPress to inject the key event.
    // =================================================================================
    fun triggerKeyPress(keyTag: String) {
        Log.d("KeyboardView", "triggerKeyPress: $keyTag")
        handleKeyPress(keyTag, fromRepeat = true)
    }
    // =================================================================================
    // END BLOCK: triggerKeyPress
    // =================================================================================    // =================================================================================
    // FUNCTION: findCandidateAt
    // SUMMARY: Checks if the given coordinates are within one of the prediction
    //          candidates (cand1, cand2, cand3). Returns the text and isNew flag
    //          if found, null otherwise.
    // =================================================================================
    private fun findCandidateAt(x: Float, y: Float): Pair<String, Boolean>? {
        val candidates = listOf(cand1, cand2, cand3)

        for (candView in candidates) {
            if (candView == null || candView.visibility != View.VISIBLE) continue

            // Get the view's position relative to this KeyboardView
            val loc = IntArray(2)
            candView.getLocationInWindow(loc)

            val myLoc = IntArray(2)
            this.getLocationInWindow(myLoc)

            // Calculate relative position
            val relX = loc[0] - myLoc[0]
            val relY = loc[1] - myLoc[1]

            // Check if tap is within this candidate
            if (x >= relX && x < relX + candView.width &&
                y >= relY && y < relY + candView.height) {

                val text = candView.text?.toString() ?: continue
                // Check if it's a "new" word by text color (cyan = new)
                val isNew = candView.currentTextColor == Color.CYAN

                return Pair(text, isNew)
            }
        }

        return null
    }
    // =================================================================================
    // END BLOCK: findCandidateAt
    // =================================================================================

    // =================================================================================
    // FUNCTION: getKeyboardState / setKeyboardState
    // SUMMARY: Gets/sets the current keyboard layer state for syncing to mirror.
    // =================================================================================
    fun getKeyboardState(): KeyboardState {
        return currentState
    }

    fun setKeyboardState(state: KeyboardState) {
        if (currentState != state) {
            currentState = state
            buildKeyboard()
        }
    }

    fun getShiftState(): Pair<Boolean, Boolean> {
        // Returns (isShifted, isCapsLock)
        return Pair(
            currentState == KeyboardState.UPPERCASE,
            currentState == KeyboardState.CAPS_LOCK
        )
    }

    fun getCtrlAltState(): Pair<Boolean, Boolean> {
        return Pair(isCtrlActive, isAltActive)
    }

    fun setCtrlAltState(ctrl: Boolean, alt: Boolean) {
        if (isCtrlActive != ctrl || isAltActive != alt) {
            isCtrlActive = ctrl
            isAltActive = alt
            buildKeyboard()
        }
    }
    // =================================================================================
    // END BLOCK: getKeyboardState / setKeyboardState
    // =================================================================================

    // =================================================================================
    // FUNCTION: isOrientationModeActive
    // SUMMARY: Returns whether orientation mode is currently active.
    // @return true if orientation mode is blocking key input
    // =================================================================================
    fun isOrientationModeActive(): Boolean {
        return isOrientationModeActive
    }
    // =================================================================================
    // END BLOCK: isOrientationModeActive
    // =================================================================================

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) {
            mapKeys()
        }
    }

    private fun mapKeys() {
        keyCenters.clear()

        // 1. Get the absolute position of the KeyboardView itself
        val parentLoc = IntArray(2)
        this.getLocationOnScreen(parentLoc)
        val parentX = parentLoc[0]
        val parentY = parentLoc[1]

        // 2. Traverse all children to find tagged TextViews
        fun traverse(view: View) {
            if (view is android.view.ViewGroup) {
                for (i in 0 until view.childCount) {
                    traverse(view.getChildAt(i))
                }
            }

            // Check if this view (could be ViewGroup or TextView) has a tag
            if (view.tag is String) {
                val key = view.tag as String
                // We only care about single letters for swipe decoding (A-Z)
                if (key.length == 1 && Character.isLetter(key[0])) {
                    val loc = IntArray(2)
                    view.getLocationOnScreen(loc)

                    // Calculate center relative to the KeyboardView (0,0 is top-left of keyboard)
                    // This matches the MotionEvent coordinates we get in dispatchTouchEvent
                    val centerX = (loc[0] - parentX) + (view.width / 2f)
                    val centerY = (loc[1] - parentY) + (view.height / 2f)

                    keyCenters[key.uppercase()] = android.graphics.PointF(centerX, centerY)
                    // Also store lowercase for easier matching
                    keyCenters[key.lowercase()] = android.graphics.PointF(centerX, centerY)
                }
            }
        }
        traverse(this)

        android.util.Log.d("DroidOS_Swipe", "Keys mapped: ${keyCenters.size / 2} (Unique Letters)")
        if (keyCenters.isNotEmpty()) {
             android.util.Log.d("DroidOS_Swipe", "Example 'H': ${keyCenters["h"]}")
        }
    }





private fun buildKeyboard() {
        // =======================================================================
        // SAVE CURRENT CANDIDATES before rebuilding
        // This preserves the prediction bar when SHIFT/layout changes occur
        // =======================================================================
        val savedCandidates = ArrayList(currentCandidates)
        // =======================================================================
        // END BLOCK: Save candidates
        // =======================================================================
        
        removeAllViews()

        // --- 1. SUGGESTION STRIP ---
        suggestionStrip = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (35 * scaleFactor).toInt()) 
            setBackgroundColor(Color.parseColor("#222222"))
            setPadding(0, 0, 0, 4)
            
            // Mouse Click Handler for Empty Bar
            setOnTouchListener { v, event ->
                if (isPredictiveBarEmpty) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> true // Capture touch
                        MotionEvent.ACTION_UP -> {
                            val w = v.width.toFloat()
                            val x = event.x
                            if (x < w * 0.33f) {
                                // Left Click Section (Left 33%)
                                cursorClickAction?.invoke(false) 
                                performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            } else if (x > w * 0.66f) {
                                // Right Click Section (Right 33%)
                                cursorClickAction?.invoke(true)
                                performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            } else {
                                // Middle Section
                            }
                            v.performClick()
                            true
                        }
                        else -> false
                    }
                } else {
                    false // Pass through to candidates
                }
            }
        }

        
        // Check initial state (hide children if empty by default)
        if (isPredictiveBarEmpty) {
            cand1?.visibility = View.GONE; cand2?.visibility = View.GONE; cand3?.visibility = View.GONE
            div1?.visibility = View.GONE; div2?.visibility = View.GONE
        }
        // --- END SUGGESTION STRIP ---


        fun createCandidateView(): TextView {
            return TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                textSize = fontSize * 0.9f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setBackgroundResource(android.R.drawable.list_selector_background)
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
            }
        }

        cand1 = createCandidateView(); suggestionStrip?.addView(cand1)
        // Divider
        val div1 = View(context).apply { layoutParams = LinearLayout.LayoutParams(1, LayoutParams.MATCH_PARENT); setBackgroundColor(Color.GRAY) }
        suggestionStrip?.addView(div1)

        cand2 = createCandidateView(); suggestionStrip?.addView(cand2)
        // Divider
        val div2 = View(context).apply { layoutParams = LinearLayout.LayoutParams(1, LayoutParams.MATCH_PARENT); setBackgroundColor(Color.GRAY) }
        suggestionStrip?.addView(div2)

        cand3 = createCandidateView(); suggestionStrip?.addView(cand3)

        addView(suggestionStrip)
        // --- END SUGGESTION STRIP ---
        
        val topRows = when (currentState) {
            KeyboardState.LOWERCASE -> lowercaseRows
            KeyboardState.UPPERCASE, KeyboardState.CAPS_LOCK -> uppercaseRows
            KeyboardState.SYMBOLS_1 -> symbols1Rows
            KeyboardState.SYMBOLS_2 -> symbols2Rows
        }
        for ((index, rowKeys) in topRows.withIndex()) {
            addView(createRow(rowKeys, index))
        }
        
        val bottomContainer = LinearLayout(context)
        bottomContainer.orientation = HORIZONTAL
        bottomContainer.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        
        val leftCol = LinearLayout(context)
        leftCol.orientation = VERTICAL
        val leftParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 8.5f)
        leftCol.layoutParams = leftParams
        
        val r4Keys = if (currentState == KeyboardState.LOWERCASE || currentState == KeyboardState.UPPERCASE || currentState == KeyboardState.CAPS_LOCK) row4Lower else row4Sym
        leftCol.addView(createRow(r4Keys, 3))
        leftCol.addView(createRow(arrowRow, 4))
        
        bottomContainer.addView(leftCol)
        
        val enterContainer = FrameLayout(context)
        val enterParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 1.5f)
        enterParams.setMargins(dpToPx(keySpacing), dpToPx(keySpacing), 0, 0)
        enterContainer.layoutParams = enterParams
        
        val enterKey = createKey("ENTER", 1f)
        val kParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        enterKey.layoutParams = kParams
        
        enterContainer.addView(enterKey)
        bottomContainer.addView(enterContainer)

        addView(bottomContainer)

        addView(createRow(navRow, 5))

        // Notify listener of layer change for mirror sync
        listener?.onLayerChanged(currentState)
// =======================================================================
        // RESTORE SAVED CANDIDATES after rebuilding
        // This preserves the prediction bar when SHIFT/layout changes occur
        // =======================================================================
        if (savedCandidates.isNotEmpty()) {
            setSuggestions(savedCandidates)
        }
        // =======================================================================
        // END BLOCK: Restore candidates
        // =======================================================================
    }

    private fun createRow(keys: List<String>, rowIndex: Int): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        row.gravity = Gravity.CENTER
        row.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(keyHeight)).apply {
            setMargins(0, dpToPx(keySpacing), 0, 0)
        }
        if (rowIndex == 1) row.setPadding(dpToPx((12 * scaleFactor).toInt()), 0, dpToPx((12 * scaleFactor).toInt()), 0)
        
        for (key in keys) { 
            val weight = getKeyWeight(key, rowIndex)
            row.addView(createKey(key, weight)) 
        }
        return row
    }

    private fun getKeyWeight(key: String, rowIndex: Int): Float {
        if (rowIndex >= 4) return 1f 
        return when (key) {
            "SPACE" -> 4.0f
            "SHIFT", "BKSP" -> 1.5f
            "SYM", "SYM1", "SYM2", "ABC" -> 1.3f
            else -> 1f
        }
    }

    private fun createKey(key: String, weight: Float): View {
        val container = FrameLayout(context)
        val params = if (weight > 0) {
            LayoutParams(0, LayoutParams.MATCH_PARENT, weight)
        } else {
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        
        params.setMargins(dpToPx(keySpacing), 0, dpToPx(keySpacing), 0)
        container.layoutParams = params

        val keyView = TextView(context)
        keyView.gravity = Gravity.CENTER
        val rowFontSize = if (key in arrowRow || key in navRow) fontSize - 4 else fontSize
        keyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowFontSize)
        keyView.setTextColor(Color.WHITE)
        keyView.text = getDisplayText(key)

        val bg = GradientDrawable()
        bg.cornerRadius = dpToPx(6).toFloat()
        bg.setColor(getKeyColor(key))
        keyView.background = bg
        
        val viewParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        container.addView(keyView, viewParams)

        container.tag = key
        return container
    }

    // --- MULTITOUCH HANDLING ---

// =================================================================================
    // FUNCTION: dispatchTouchEvent
    // SUMMARY: Intercepts touch events to detect swipe/gesture typing. Key safeguards:
    //          1. Only tracks swipe for single-finger gestures (pointer index 0)
    //          2. Multitouch (second finger down) cancels any active swipe detection
    //          3. Requires minimum movement threshold AND minimum path distance
    //          4. Validates swipe has enough points and traveled enough distance
    //          This prevents false swipe triggers during fast two-thumb typing.
    // =================================================================================

    // =================================================================================
    // FUNCTION: dispatchTouchEvent
    // SUMMARY: Intercepts touch events to detect swipe/gesture typing. Key safeguards:
    //          1. Skips swipe detection if touch starts on SPACE (trackpad mode)
    //          2. Skips swipe detection if a candidate is being dragged to delete
    //          3. Only tracks swipe for single-finger gestures
    //          4. Validates swipe has enough points and distance
    // =================================================================================
    override fun dispatchTouchEvent(event: android.view.MotionEvent): Boolean {
        // =================================================================================
        // VIRTUAL MIRROR MODE - BLOCK SWIPE TYPING
        // SUMMARY: When orientation mode is active, we must block swipe typing here
        //          because dispatchTouchEvent runs BEFORE onTouchEvent. If we don't
        //          block here, swipe paths get collected and committed even though
        //          onTouchEvent blocks individual key presses.
        // =================================================================================
        val callback = mirrorTouchCallback
        if (callback != null) {
            val shouldBlock = callback.invoke(event.x, event.y, event.actionMasked)
            if (shouldBlock) {
                // Orientation mode - block ALL input including swipe
                isOrientationModeActive = true

                // Cancel any in-progress swipe
                if (isSwiping) {
                    isSwiping = false
                    swipeTrail?.clear()
                    swipeTrail?.visibility = View.INVISIBLE
                }
                currentPath.clear()
                swipePointerId = -1

                // Still call super so child views can process, but return true to consume
                super.dispatchTouchEvent(event)
                return true
            }
        }

        // Also check the flag directly (for when callback isn't active)
        if (isOrientationModeActive) {
            // Cancel any in-progress swipe
            if (isSwiping) {
                isSwiping = false
                swipeTrail?.clear()
                swipeTrail?.visibility = View.INVISIBLE
            }
            currentPath.clear()
            swipePointerId = -1

            super.dispatchTouchEvent(event)
            return true
        }
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE - BLOCK SWIPE TYPING
        // =================================================================================


        // [FIX] Block Swipe Logic when Trackpad Mode is active
        // This prevents the blue trail and swipe decoder from running while using the mouse
        if (isTrackpadTouchMode) {
            return super.dispatchTouchEvent(event)
        }

        // --- 1. PREVENT SWIPE TRAIL ON SPACEBAR ---

        // If the touch starts on the SPACE key, we skip the swipe detection logic entirely.
        if (event.actionMasked == android.view.MotionEvent.ACTION_DOWN) {
            val touchedView = findKeyView(event.x, event.y)
            if (touchedView?.tag == "SPACE") {
                return super.dispatchTouchEvent(event)
            }
        }

        // --- 2. CALL SUPER FIRST ---
        // This delivers touch events to child views (including suggestion candidates)
        // handleCandidateTouch will set activeDragCandidate/isCandidateDragging
        val superResult = super.dispatchTouchEvent(event)

        // --- 3. CHECK IF CANDIDATE IS BEING DRAGGED ---
        // If user is dragging a suggestion candidate, cancel any swipe tracking
        // and skip the swipe detection logic below
        if (activeDragCandidate != null) {
            // Cancel any active swipe tracking
            if (isSwiping) {
                isSwiping = false
                swipeTrail?.clear()
                swipeTrail?.visibility = View.INVISIBLE
            }
            currentPath.clear()
            swipePointerId = -1
            return superResult
        }

        // --- 4. SWIPE / GESTURE TRACKING LOGIC ---
        val action = event.actionMasked
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        when (action) {
            android.view.MotionEvent.ACTION_DOWN -> {
                // First finger down - initialize potential swipe tracking
                isSwiping = false
                swipePointerId = pointerId
                startTouchX = event.x
                startTouchY = event.y
                swipeTrail?.clear()
                swipeTrail?.addPoint(event.x, event.y)
                currentPath.clear()
                // NEW: Add with timestamp for dwell detection
                currentPath.add(TimedPoint(event.x, event.y, System.currentTimeMillis()))
            }

            android.view.MotionEvent.ACTION_POINTER_DOWN -> {
                // Second finger touched - CANCEL swipe detection (user is typing with two thumbs)
                if (isSwiping) {
                    isSwiping = false
                    swipeTrail?.clear()
                    swipeTrail?.visibility = View.INVISIBLE
                    currentPath.clear()
                }
                swipePointerId = -1 // Disable swipe tracking for this gesture
            }

            android.view.MotionEvent.ACTION_MOVE -> {
                // Only track movement for the original swipe pointer
                if (swipePointerId == -1) return superResult

                // Find the index of our tracked pointer
                val trackedIndex = event.findPointerIndex(swipePointerId)
                if (trackedIndex == -1) return superResult

                val currentX = event.getX(trackedIndex)
                val currentY = event.getY(trackedIndex)

                if (!isSwiping) {
                    val dx = Math.abs(currentX - startTouchX)
                    val dy = Math.abs(currentY - startTouchY)
                    // Require movement in BOTH axes or significant movement in one
                    val totalMovement = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                    if (totalMovement > SWIPE_THRESHOLD) {
                        isSwiping = true
                        currentRepeatKey = null
                        repeatHandler.removeCallbacks(repeatRunnable)
                        swipeTrail?.visibility = View.VISIBLE
                    }
                }

                if (isSwiping) {
                    swipeTrail?.addPoint(currentX, currentY)
                    // Sample historical points for smoother path WITH TIMESTAMPS
                    if (event.historySize > 0) {
                        for (h in 0 until event.historySize) {
                            val hx = event.getHistoricalX(trackedIndex, h)
                            val hy = event.getHistoricalY(trackedIndex, h)
                            val ht = event.getHistoricalEventTime(h)
                            currentPath.add(TimedPoint(hx, hy, ht))
                        }
                    }
                    currentPath.add(TimedPoint(currentX, currentY, event.eventTime))

                    // =======================================================================
                    // LIVE SWIPE PREVIEW
                    // SUMMARY: Send current path to listener for real-time predictions.
                    //          Throttled to avoid performance issues.
                    // =======================================================================
                    val now = System.currentTimeMillis()
                    if (currentPath.size >= SWIPE_PREVIEW_MIN_POINTS &&
                        now - lastSwipePreviewTime > SWIPE_PREVIEW_INTERVAL_MS) {
                        lastSwipePreviewTime = now
                        listener?.onSwipeProgress(currentPath.map { it.toPointF() })
                    }
                    // =======================================================================
                    // END BLOCK: LIVE SWIPE PREVIEW
                    // =======================================================================
                }
            }

            android.view.MotionEvent.ACTION_UP -> {
                if (isSwiping && pointerId == swipePointerId) {
                    swipeTrail?.clear()
                    swipeTrail?.visibility = View.INVISIBLE

                    // FIX: Clear any key highlight that may have been set during swipe
                    currentActiveKey?.let { key ->
                        val tag = key.tag as? String
                        if (tag != null) setKeyVisual(key, false, tag)
                    }
                    currentActiveKey = null

                    // Validate swipe before triggering decoder
                    val isValidSwipe = validateSwipe()

                    if (isValidSwipe) {
                        // LOG: Swipe passed validation, sending to decoder
                        android.util.Log.d("DroidOS_Swipe", "DISPATCH: Sending ${currentPath.size} points to onSwipeDetectedTimed")

                        // Check if listener exists
                        if (listener == null) {
                            android.util.Log.e("DroidOS_Swipe", "DISPATCH FAIL: listener is NULL!")
                        } else {
                            // NEW: Send timed path for dwell detection
                            listener?.onSwipeDetectedTimed(ArrayList(currentPath))
                        }
                    } else {
                        android.util.Log.d("DroidOS_Swipe", "DISPATCH SKIP: validateSwipe returned false")
                    }

                    isSwiping = false
                    swipePointerId = -1
                    currentPath.clear()
                    return true
                }
                // Clean up even if this wasn't our tracked pointer
                swipeTrail?.clear()
                swipePointerId = -1
            }

            android.view.MotionEvent.ACTION_POINTER_UP -> {
                // One finger lifted but another still down - just clean up if it was our pointer
                if (pointerId == swipePointerId) {
                    isSwiping = false
                    swipePointerId = -1
                    swipeTrail?.clear()
                    swipeTrail?.visibility = View.INVISIBLE
                    currentPath.clear()
                }
            }

            android.view.MotionEvent.ACTION_CANCEL -> {
                isSwiping = false
                swipePointerId = -1
                swipeTrail?.clear()
                swipeTrail?.visibility = View.INVISIBLE
                currentPath.clear()
            }
        }
        return superResult
    }
    // =================================================================================
    // END BLOCK: dispatchTouchEvent
    // =================================================================================


    // =================================================================================
    // FUNCTION: validateSwipe
    // SUMMARY: Checks if the recorded path qualifies as a valid swipe gesture.
    //          Requirements: minimum number of points AND minimum TOTAL PATH LENGTH.
    //          Uses total traveled distance (not start-to-end) to handle words that
    //          start and end on the same or nearby letters (e.g., "test", "that").
    //          LOGGING: Always logs validation result to diagnose failures.
    // =================================================================================
    private fun validateSwipe(): Boolean {
        // CHECK 1: Minimum path points
        if (currentPath.size < SWIPE_MIN_PATH_POINTS) {
            android.util.Log.d("DroidOS_Swipe", "VALIDATE FAIL: Path too short (${currentPath.size} < $SWIPE_MIN_PATH_POINTS points)")
            return false
        }

        // CHECK 2: Calculate TOTAL PATH LENGTH (not start-to-end distance)
        // This properly handles words like "test" where start and end keys are the same
        var totalPathLength = 0f
        for (i in 1 until currentPath.size) {
            val prev = currentPath[i - 1]
            val curr = currentPath[i]
            val dx = curr.x - prev.x
            val dy = curr.y - prev.y
            totalPathLength += Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        }

        if (totalPathLength < SWIPE_MIN_DISTANCE) {
            android.util.Log.d("DroidOS_Swipe", "VALIDATE FAIL: Path length too short (${totalPathLength.toInt()}px < ${SWIPE_MIN_DISTANCE.toInt()}px)")
            return false
        }

        android.util.Log.d("DroidOS_Swipe", "VALIDATE OK: ${currentPath.size} points, ${totalPathLength.toInt()}px total path length")
        return true
    }
    // =================================================================================
    // END BLOCK: validateSwipe with total path length check
    // =================================================================================





    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        val x = event.getX(pointerIndex)
        val y = event.getY(pointerIndex)

        // =================================================================================
        // BLOCK: VIRTUAL MIRROR MODE - INTERCEPT TOUCHES (EXCEPT PREDICTIONS)
        // SUMMARY: All key touches go through orientation mode, but prediction bar
        //          touches should work immediately so users can tap suggestions.
        // =================================================================================

        // Check if touch is in the prediction bar area (top portion of keyboard)
        val isPredictionBarTouch = suggestionStrip != null && y < (suggestionStrip?.bottom ?: 0)

        val touchedView = findKeyView(x, y)
        val keyTag = touchedView?.tag as? String

        val callback = mirrorTouchCallback
        if (callback != null && !isPredictionBarTouch) {
            val shouldBlock = callback.invoke(x, y, action)
            if (shouldBlock) {
                // Orientation mode is active - set flag and block ALL input
                isOrientationModeActive = true

                // Clear any active key highlight
                currentActiveKey?.let { key ->
                    val tag = key.tag as? String
                    if (tag != null) setKeyVisual(key, false, tag)
                }
                currentActiveKey = null

                // CRITICAL: Return immediately - do not process as key input
                return true
            }
        }
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE - INTERCEPT TOUCHES
        // =================================================================================

        // =================================================================================
        // ORIENTATION MODE CHECK (fallback, but skip for prediction bar)
        // =================================================================================
        if (isOrientationModeActive && !isPredictionBarTouch) {
            currentActiveKey?.let {
                val tag = it.tag as? String
                if (tag != null) setKeyVisual(it, false, tag)
            }
            currentActiveKey = null
            return true
        }
        // =================================================================================
        // END BLOCK: ORIENTATION MODE CHECK
        // =================================================================================




        // --- SPACEBAR TRACKPAD HANDLING ---
        // [MODIFIED] Check for Spacebar OR Active Trackpad Mode (Full Keyboard)
        // If mode is active (Green), ALL keyboard touches become mouse inputs.
        val isTrackpadStart = (keyTag == "SPACE" && action == MotionEvent.ACTION_DOWN)
        val isTrackpadContinue = (isTrackpadTouchMode && action == MotionEvent.ACTION_DOWN)

        if (isTrackpadStart || isTrackpadContinue || spacebarPointerId == pointerId) {
            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    if (isTrackpadStart || isTrackpadContinue) {
                        spacebarPointerId = pointerId
                        lastSpaceX = x
                        lastSpaceY = y

                        isSpaceTrackpadActive = false
                        isDragging = false
                        hasMovedWhileDown = false

                        // If in Touch Mode, start the "Hold to Drag" timer
                        // This allows hold-to-drag behavior from ANY key when active
                        if (isTrackpadTouchMode) {
                            handler.postDelayed(holdToDragRunnable, 300) // Wait 300ms to detect Hold
                            android.util.Log.d("SpaceTrackpad", "Touch Mode: Started hold-to-drag timer")
                        }

                        // Visual feedback: Always keep SPACE green, even if touching other keys
                        val spaceView = findViewWithTag<View>("SPACE")
                        if (spaceView != null) setKeyVisual(spaceView, true, "SPACE")
                        
                        // Return true to BLOCK normal key processing
                        return true
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (pointerId == spacebarPointerId) {
                        val dx = x - lastSpaceX
                        val dy = y - lastSpaceY

                        // Check if user moved significantly
                        if (kotlin.math.hypot(dx, dy) > touchSlop) {
                            hasMovedWhileDown = true

                            // If we moved BEFORE the hold timer fired, cancel the hold
                            // (Unless we are already dragging, in which case we continue dragging)
                            if (!isDragging) {
                                handler.removeCallbacks(holdToDragRunnable)
                            }

                            isSpaceTrackpadActive = true
                        }

                        // Move Cursor
                        if (isSpaceTrackpadActive) {
                            moveMouse(dx, dy)
                            lastSpaceX = x
                            lastSpaceY = y
                        }
                        return true
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    if (pointerId == spacebarPointerId) {
                        handler.removeCallbacks(holdToDragRunnable)

                        // VISUAL LOGIC:
                        // Only reset to Grey if we are NOT staying in Touch Mode.
                        // If we are in Touch Mode, startTrackpadTimer() will ensure it stays Green.
                        val stayingInMode = isTrackpadTouchMode || isSpaceTrackpadActive
                        
                        val spaceView = findViewWithTag<View>("SPACE")
                        if (spaceView != null && !stayingInMode) {
                            setKeyVisual(spaceView, false, "SPACE")
                        }

                        if (isTrackpadTouchMode) {
                            if (isDragging) {
                                touchUpAction?.invoke()
                                isDragging = false
                            } else if (!hasMovedWhileDown) {
                                touchTapAction?.invoke()
                            }
                            // Reset timer on lift (keeps mode alive for 1s after touch ends)
                            startTrackpadTimer()
                        } else {
                            if (!isSpaceTrackpadActive) {
                                // Normal Space Tap (Turns Grey immediately above)
                                if (isPredictiveBarEmpty) {
                                    listener?.onSpecialKey(SpecialKey.SPACE, 0)
                                    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                } else {
                                    listener?.onSpecialKey(SpecialKey.SPACE, 0)
                                    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                }
                            } else {
                                // Drag Finished -> Enter Touch Mode (Stays Green)
                                startTrackpadTimer()
                            }
                        }
                        
                        spacebarPointerId = -1
                        isSpaceTrackpadActive = false
                        return true
                    }
                }


                MotionEvent.ACTION_CANCEL -> {
                    if (pointerId == spacebarPointerId) {
                        handler.removeCallbacks(holdToDragRunnable)
                        if (isDragging) { touchUpAction?.invoke(); isDragging = false }
                        spacebarPointerId = -1
                        isSpaceTrackpadActive = false
                        
                        // Only turn off visual if timer isn't running
                        if (!isTrackpadTouchMode) {
                            val spaceView = findViewWithTag<View>("SPACE")
                            if (spaceView != null) setKeyVisual(spaceView, false, "SPACE")
                        }
                        return true
                    }
                }

            }
        }




        // --- STANDARD KEYBOARD HANDLING (Fixes Stuck Highlights) ---
        // We track the active key and update it as the finger slides.
        
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (touchedView != null && keyTag != null && keyTag != "SPACE") {
                    currentActiveKey = touchedView
                    onKeyDown(keyTag, touchedView)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // If we slid to a new key
                if (touchedView != currentActiveKey) {
                    // Deactivate old key
                    currentActiveKey?.let {
                        val oldTag = it.tag as? String
                        if (oldTag != null) {
                            setKeyVisual(it, false, oldTag)
                            // CRITICAL: Stop any repeat when sliding off a key
                            if (oldTag == currentRepeatKey) {
                                stopRepeat()
                            }
                        }
                    }

                    // Activate new
                    if (touchedView != null && keyTag != null && keyTag != "SPACE") {
                        currentActiveKey = touchedView
                        onKeyDown(keyTag, touchedView) // Visual on + Haptic
                    } else {
                        currentActiveKey = null
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                currentActiveKey?.let {
                    val tag = it.tag as? String
                    if (tag != null) {
                        // Commit the input
                        onKeyUp(tag, it)
                    }
                }
                currentActiveKey = null
            }
            MotionEvent.ACTION_CANCEL -> {
                currentActiveKey?.let {
                    val tag = it.tag as? String
                    if (tag != null) setKeyVisual(it, false, tag)
                }
                currentActiveKey = null
            }
        }
        
        return true
    }





    private fun findKeyView(targetX: Float, targetY: Float): View? {
        return findKeyRecursively(this, targetX, targetY)
    }

    private fun findKeyRecursively(parent: ViewGroup, targetX: Float, targetY: Float): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.visibility != View.VISIBLE) continue
            val cx = child.x
            val cy = child.y
            if (targetX >= cx && targetX < cx + child.width && targetY >= cy && targetY < cy + child.height) {
                if (child.tag != null) return child
                if (child is ViewGroup) return findKeyRecursively(child, targetX - cx, targetY - cy)
            }
        }
        return null
    }

// =================================================================================
    // FUNCTION: onKeyDown
    // SUMMARY: Handles initial touch on a key. For swipe-compatible keys (single letters),
    //          we ONLY provide visual/haptic feedback here. The actual character input is
    //          deferred to onKeyUp to prevent double-letters during swipe typing.
    //          Special/modifier keys still trigger immediately for responsiveness.
    // =================================================================================

    // =================================================================================
    // KEY HANDLING LOGIC (DEFERRED TOGGLES)
    // =================================================================================
    // Keys that trigger layout changes (?123, ABC) or state toggles (CTRL, ALT)
    // must fire on UP to prevent "Flickering" caused by immediate layout rebuilds
    // while the finger is still pressing the screen.
    private val deferredKeys = setOf("SHIFT", "?123", "ABC", "SYM", "SYM1", "SYM2", "CTRL", "ALT", "MODE", "SCREEN")

    // =================================================================================
    // FUNCTION: onKeyDown
    // SUMMARY: Handles initial touch on a key. For swipe-compatible keys (single letters),
    //          we ONLY provide visual/haptic feedback here. The actual character input is
    //          deferred to onKeyUp to prevent double-letters during swipe typing.
    //          Special/modifier keys still trigger immediately for responsiveness.
    //          FIX: ALL key presses are blocked during active swipe to match Gboard behavior.
    // =================================================================================
    private fun onKeyDown(key: String, view: View) {
        // FIX: Block ALL key presses during active swipe
        // This prevents SHIFT, ENTER, BACKSPACE etc from triggering mid-swipe
        if (isSwiping) {
            // Still provide visual feedback so user sees they touched something
            setKeyVisual(view, true, key)
            return
        }

        setKeyVisual(view, true, key)

        // Haptic Feedback
        if (vibrationEnabled) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION") v?.vibrate(30)
            }
        }

        val isSwipeableKey = key.length == 1 && Character.isLetter(key[0])
        val isDeferred = deferredKeys.contains(key)

        // FIRE IMMEDIATE: Navigation, Numbers, Punctuation, Backspace
        // But only if NOT a swipeable key and NOT a deferred key
        if (!isSwipeableKey && !isDeferred) {
            handleKeyPress(key, fromRepeat = false)

            if (isKeyRepeatable(key)) {
                currentRepeatKey = key
                isRepeating = true
                repeatHandler.postDelayed(repeatRunnable, REPEAT_INITIAL_DELAY)
            }
        }

        // SPECIAL: SHIFT Caps Lock Timer
        if (key == "SHIFT") {
            capsLockPending = false
            capsHandler.postDelayed(capsLockRunnable, 500)
        }
    }
    // =================================================================================
    // END BLOCK: onKeyDown
    // =================================================================================


    // =================================================================================
    // END BLOCK: KEY HANDLING LOGIC
    // =================================================================================

    // =================================================================================
    // END BLOCK: onKeyDown
    // =================================================================================

// =================================================================================
    // FUNCTION: onKeyUp
    // SUMMARY: Handles key release. For swipe-compatible keys (single letters), this is
    //          where we actually commit the character - BUT ONLY if we're not currently
    //          in a swipe gesture. This prevents double letters with swipe typing.
    //          FIX: ALL key presses are blocked during active swipe to match Gboard behavior.
    //          Also handles SHIFT toggle and repeat cancellation.
    // =================================================================================

    private fun onKeyUp(key: String, view: View) {
        setKeyVisual(view, false, key)

        // Stop any active key repeat
        if (key == currentRepeatKey) stopRepeat()

        // FIX: Block ALL key presses during active swipe
        // This includes letter keys, special keys, and deferred keys
        if (isSwiping) {
            // Cancel SHIFT caps lock timer if it was started
            if (key == "SHIFT") {
                capsHandler.removeCallbacks(capsLockRunnable)
                capsLockPending = false
            }
            return
        }

        // Determine if this is a swipeable key that was deferred
        val isSwipeableKey = key.length == 1 && Character.isLetter(key[0])

        if (isSwipeableKey) {
            // SWIPEABLE KEY + NOT SWIPING = Normal tap, commit the character now
            handleKeyPress(key, fromRepeat = false)
        }

        // --- Handle Deferred Keys (CTRL, ALT, SYM, etc) ---
        // These are skipped in onKeyDown to prevent rebuild loops.
        // We must fire them here on release.
        val isDeferred = deferredKeys.contains(key)
        if (isDeferred && key != "SHIFT") {
             handleKeyPress(key, fromRepeat = false)
        }

        // SHIFT toggle handling
        if (key == "SHIFT") {
            capsHandler.removeCallbacks(capsLockRunnable)
            if (!capsLockPending) toggleShift()
            capsLockPending = false
        }
    }

    // =================================================================================
    // END BLOCK: onKeyUp
    // =================================================================================


    // Changed to public to allow external highlighting (e.g. from OverlayService during drag)
    fun setKeyVisual(container: View, pressed: Boolean, key: String, overrideColor: Int? = null) {
        val tv = (container as? ViewGroup)?.getChildAt(0) as? TextView ?: return
        val bg = tv.background as? GradientDrawable ?: return

        if (overrideColor != null) {
            bg.setColor(overrideColor)
        } else if (pressed) {
            bg.setColor(Color.parseColor("#3DDC84"))
        } else {
            bg.setColor(getKeyColor(key))
        }
    }

    // New helper to highlight a specific key by tag
    fun highlightKey(tag: String, active: Boolean, color: Int? = null) {
        val view = findViewWithTag<View>(tag)
        if (view != null) {
            setKeyVisual(view, active, tag, color)
        }
    }


    private fun stopRepeat() {
        isRepeating = false
        currentRepeatKey = null
        repeatHandler.removeCallbacks(repeatRunnable)
    }

    private fun isKeyRepeatable(key: String): Boolean {
        // 1. Strict Whitelist Check (Nav & Deletion)
        if (alwaysRepeatable.contains(key)) return true
        
        // 2. Single letters/numbers (Standard typing) should repeat
        if (key.length == 1) return true
        
        // 3. Explicitly BLOCK everything else (SHIFT, ?123, CTRL, ALT, TAB, ESC)
        // This ensures they only trigger ONCE per press (Sticky/Toggle behavior)
        return false
    }

    private fun getDisplayText(key: String): String = when (key) {
        "SHIFT" -> if (currentState == KeyboardState.CAPS_LOCK) "⬆" else "⇧"
        "BKSP" -> "⌫"; "ENTER" -> "↵"; "SPACE" -> " "
        "SYM", "SYM1", "SYM2" -> "?123"; "ABC" -> "ABC"
        "TAB" -> "⇥"; "CTRL" -> "Ctrl"; "ALT" -> "Alt"; "ESC" -> "Esc"
        "←" -> "◀"; "→" -> "▶"; "↑" -> "▲"; "↓" -> "▼"
        "MUTE" -> "Mute"; "VOL-" -> "Vol-"; "VOL+" -> "Vol+"
        "BACK" -> "Back"; "FWD" -> "Fwd"; "MIC" -> "🎤"
        "SCREEN" -> if (isSymbolsActive()) "MODE" else "📺"
        "HIDE_KB" -> "▼"
        else -> key
    }

    private fun getKeyColor(key: String): Int {
        if (key == "CTRL" && isCtrlActive) return Color.parseColor("#3DDC84")
        if (key == "ALT" && isAltActive) return Color.parseColor("#3DDC84")
        
        // NEW: Voice Active Indicator
        // UPDATED: Voice Key Color
        // Green if active, standard dark gray if inactive (removed red alert color)
        if (key == "MIC") {
            return if (isVoiceActive) Color.parseColor("#3DDC84") else Color.parseColor("#252525")
        }

        if (key == "SCREEN") {
            return if (isSymbolsActive()) Color.parseColor("#FF9800") else Color.parseColor("#FF5555")
        }
        
        if (key in arrowRow || key in navRow) return Color.parseColor("#252525")
        
        return when (key) {
            "SHIFT" -> when (currentState) {
                KeyboardState.CAPS_LOCK -> Color.parseColor("#3DDC84")
                KeyboardState.UPPERCASE -> Color.parseColor("#4A90D9")
                else -> Color.parseColor("#3A3A3A")
            }
            "ENTER" -> Color.parseColor("#4A90D9")
            "BKSP", "SYM", "SYM1", "SYM2", "ABC" -> Color.parseColor("#3A3A3A")
            "SPACE" -> Color.parseColor("#2D2D2D")
            else -> Color.parseColor("#2D2D2D")
        }
    }
    
    private fun isSymbolsActive(): Boolean {
        return currentState == KeyboardState.SYMBOLS_1 || currentState == KeyboardState.SYMBOLS_2
    }

    private fun getMetaState(): Int {
        var meta = 0
        if (isCtrlActive) meta = meta or 0x1000 
        if (isAltActive) meta = meta or 0x02 
        return meta
    }


    private fun handleKeyPress(key: String, fromRepeat: Boolean = false) {
        if (isMirrorMode) return // STOP Ghost Typing
        var meta = getMetaState()

        if (fromRepeat && !isKeyRepeatable(key)) return

        when (key) {
            "CTRL" -> { if (!fromRepeat) { isCtrlActive = !isCtrlActive; buildKeyboard() } }
            "ALT" -> { if (!fromRepeat) { isAltActive = !isAltActive; buildKeyboard() } }
            "SHIFT" -> { /* Handled in onKeyUp/Down */ }
            
            "BKSP" -> listener?.onSpecialKey(SpecialKey.BACKSPACE, meta)
            "ENTER" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.ENTER, meta) }
            "SPACE" -> listener?.onSpecialKey(SpecialKey.SPACE, meta)
            "TAB" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.TAB, meta) }
            "ESC" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.ESCAPE, meta) }
            
            "←" -> listener?.onSpecialKey(SpecialKey.ARROW_LEFT, meta)
            "→" -> listener?.onSpecialKey(SpecialKey.ARROW_RIGHT, meta)
            "↑" -> listener?.onSpecialKey(SpecialKey.ARROW_UP, meta)
            "↓" -> listener?.onSpecialKey(SpecialKey.ARROW_DOWN, meta)
            
            "MUTE" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.MUTE, meta) }
            "VOL-" -> listener?.onSpecialKey(SpecialKey.VOL_DOWN, meta)
            "VOL+" -> listener?.onSpecialKey(SpecialKey.VOL_UP, meta)
            "BACK" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.BACK_NAV, meta) }
            "FWD" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.FWD_NAV, meta) }
            "MIC" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.VOICE_INPUT, meta) }
            
            "SCREEN" -> { 
                if (!fromRepeat) {
                    if (isSymbolsActive()) {
                        listener?.onScreenModeChange()
                    } else {
                        listener?.onScreenToggle()
                    }
                }
            }
            "HIDE_KB" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.HIDE_KEYBOARD, meta) }
            
            "SYM", "SYM1" -> { if (!fromRepeat) { currentState = KeyboardState.SYMBOLS_1; buildKeyboard() } }
            "SYM2" -> { if (!fromRepeat) { currentState = KeyboardState.SYMBOLS_2; buildKeyboard() } }
            "ABC" -> { if (!fromRepeat) { currentState = KeyboardState.LOWERCASE; buildKeyboard() } }
            
            else -> {
                if (key.length == 1) {
                    val char = key[0]
                    val pair = getSymbolKeyCode(char)
                    val code = pair.first
                    val shiftNeeded = pair.second
                    if (shiftNeeded) meta = meta or KeyEvent.META_SHIFT_ON
                    listener?.onKeyPress(code, char, meta)
                    if (!fromRepeat && currentState == KeyboardState.UPPERCASE) { 
                        currentState = KeyboardState.LOWERCASE
                        buildKeyboard()
                    }
                }
            }
        }
        if (!fromRepeat && key != "CTRL" && key != "ALT" && key != "SHIFT") {
            if (isCtrlActive || isAltActive) {
                isCtrlActive = false; isAltActive = false; buildKeyboard()
            }
        }
    }

    private fun getSymbolKeyCode(c: Char): Pair<Int, Boolean> {
        return when (c) {
            in 'a'..'z' -> KeyEvent.keyCodeFromString("KEYCODE_${c.uppercase()}") to false
            in 'A'..'Z' -> KeyEvent.keyCodeFromString("KEYCODE_${c}") to true
            in '0'..'9' -> KeyEvent.keyCodeFromString("KEYCODE_${c}") to false
            ' ' -> KeyEvent.KEYCODE_SPACE to false
            '.' -> KeyEvent.KEYCODE_PERIOD to false
            ',' -> KeyEvent.KEYCODE_COMMA to false
            ';' -> KeyEvent.KEYCODE_SEMICOLON to false
            ':' -> KeyEvent.KEYCODE_SEMICOLON to true
            '=' -> KeyEvent.KEYCODE_EQUALS to false
            '+' -> KeyEvent.KEYCODE_PLUS to false
            '-' -> KeyEvent.KEYCODE_MINUS to false
            '_' -> KeyEvent.KEYCODE_MINUS to true
            '/' -> KeyEvent.KEYCODE_SLASH to false
            '?' -> KeyEvent.KEYCODE_SLASH to true
            '`' -> KeyEvent.KEYCODE_GRAVE to false
            '~' -> KeyEvent.KEYCODE_GRAVE to true
            '[' -> KeyEvent.KEYCODE_LEFT_BRACKET to false
            '{' -> KeyEvent.KEYCODE_LEFT_BRACKET to true
            ']' -> KeyEvent.KEYCODE_RIGHT_BRACKET to false
            '}' -> KeyEvent.KEYCODE_RIGHT_BRACKET to true
            '\\' -> KeyEvent.KEYCODE_BACKSLASH to false
            '|' -> KeyEvent.KEYCODE_BACKSLASH to true
            '\'' -> KeyEvent.KEYCODE_APOSTROPHE to false
            '"' -> KeyEvent.KEYCODE_APOSTROPHE to true
            '!' -> KeyEvent.KEYCODE_1 to true
            '@' -> KeyEvent.KEYCODE_2 to true
            '#' -> KeyEvent.KEYCODE_3 to true
            '$' -> KeyEvent.KEYCODE_4 to true
            '%' -> KeyEvent.KEYCODE_5 to true
            '^' -> KeyEvent.KEYCODE_6 to true
            '&' -> KeyEvent.KEYCODE_7 to true
            '*' -> KeyEvent.KEYCODE_8 to true
            '(' -> KeyEvent.KEYCODE_9 to true
            ')' -> KeyEvent.KEYCODE_0 to true
            '√' -> KeyEvent.KEYCODE_UNKNOWN to false // Filler
            else -> KeyEvent.KEYCODE_UNKNOWN to false
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
        if (vibrationEnabled) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else { @Suppress("DEPRECATION") v?.vibrate(50) }
        }
        buildKeyboard()
    }

    private fun dpToPx(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
    ).roundToInt()


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(trackpadResetRunnable)
        handler.removeCallbacks(holdToDragRunnable)
        // ... (keep existing cleanup like stopRepeat)
    }

    // --- DRAG TO DELETE LOGIC ---
    private var dragStartX = 0f
    private var dragStartY = 0f
    private var isCandidateDragging = false
    private var activeDragCandidate: String? = null

    @SuppressLint("ClickableViewAccessibility")
    fun setSuggestions(candidates: List<Candidate>) {
        // [NEW] Sync local list
        currentCandidates.clear()
        currentCandidates.addAll(candidates)

        if (suggestionStrip == null) return
        // ... existing logic ...


        // Update empty state flag
        isPredictiveBarEmpty = candidates.isEmpty() || candidates.all { it.text.isEmpty() }

        if (isPredictiveBarEmpty) {
            cand1?.visibility = View.GONE
            cand2?.visibility = View.GONE
            cand3?.visibility = View.GONE
            div1?.visibility = View.GONE
            div2?.visibility = View.GONE
            return
        }

        div1?.visibility = View.VISIBLE
        div2?.visibility = View.VISIBLE

        val views = listOf(cand1, cand2, cand3)

for (i in 0 until 3) {
            val view = views[i] ?: continue
            if (i < candidates.size) {
                val item = candidates[i]
                view.text = item.text
                view.visibility = View.VISIBLE
                view.alpha = 1.0f

                // =======================================================================
                // VISUAL STYLING BASED ON WORD TYPE
                // - RED + BOLD: Word not in any dictionary (isNew=true) - can be added
                // - WHITE + ITALIC: Word is user-added custom word (isCustom=true)
                // - WHITE + BOLD: Word is in main dictionary (normal)
                // =======================================================================
                when {
                    item.isNew -> {
                        // NOT IN DICTIONARY - Red, bold (user can add it)
                        view.setTextColor(Color.RED)
                        view.typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    item.isCustom -> {
                        // USER DICTIONARY - White, italic
                        view.setTextColor(Color.WHITE)
                        view.typeface = android.graphics.Typeface.defaultFromStyle(android.graphics.Typeface.ITALIC)
                    }
                    else -> {
                        // MAIN DICTIONARY - White, bold
                        view.setTextColor(Color.WHITE)
                        view.typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                }
                // =======================================================================
                // END BLOCK: Visual styling
                // =======================================================================

                // TOUCH LISTENER: Handle Click vs Drag
                view.setOnTouchListener { v, event ->
                    handleCandidateTouch(v, event, item)
                }
            } else {
                view.visibility = View.INVISIBLE
                view.setOnTouchListener(null)
            }
        }
    }

    // =================================================================================
    // FUNCTION: handleCandidateTouch
    // SUMMARY: Handles touch events on suggestion candidates. Detects click vs drag.
    //          Dragging to backspace triggers word deletion (block from dictionary).
    //          DEBUG: Comprehensive logging to trace touch flow.
    // =================================================================================
    private fun handleCandidateTouch(view: View, event: MotionEvent, item: Candidate): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                android.util.Log.d("DroidOS_Drag", "CANDIDATE DOWN: '${item.text}' at (${event.rawX.toInt()}, ${event.rawY.toInt()})")
                dragStartX = event.rawX
                dragStartY = event.rawY
                isCandidateDragging = false
                activeDragCandidate = item.text
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - dragStartX
                val dy = event.rawY - dragStartY
                val dist = kotlin.math.hypot(dx.toDouble(), dy.toDouble())

                // Threshold to start dragging (20px)
if (!isCandidateDragging && dist > 20) {
                    isCandidateDragging = true
                    android.util.Log.d("DroidOS_Drag", "CANDIDATE DRAG START: '${item.text}' (moved ${dist.toInt()}px)")
                    // Visual feedback: Keep White (No Dimming)
                    view.alpha = 1.0f
                }

                if (isCandidateDragging) {
                    // Check if hovering over BACKSPACE
                    val bkspKey = findViewWithTag<View>("BKSP")
                    if (bkspKey != null) {
                        val loc = IntArray(2)
                        bkspKey.getLocationOnScreen(loc)
                        val kx = loc[0]
                        val ky = loc[1]
                        val kw = bkspKey.width
                        val kh = bkspKey.height

                        // Check intersection
                        val isOverBksp = event.rawX >= kx && event.rawX <= kx + kw &&
                                         event.rawY >= ky && event.rawY <= ky + kh

                        if (isOverBksp) {
                            // HOVERING: Turn Red
                            setKeyVisual(bkspKey, false, "BKSP", overrideColor = Color.RED)
                        } else {
                            // NORMAL
                            setKeyVisual(bkspKey, false, "BKSP")
                        }
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                android.util.Log.d("DroidOS_Drag", "CANDIDATE UP: '${item.text}' isCandidateDragging=$isCandidateDragging")
                view.alpha = 1.0f
                val bkspKey = findViewWithTag<View>("BKSP")
                if (bkspKey != null) setKeyVisual(bkspKey, false, "BKSP") // Reset color

                if (isCandidateDragging) {
                    // Check Drop Target
                    if (bkspKey != null) {
                        val loc = IntArray(2)
                        bkspKey.getLocationOnScreen(loc)
                        val isOverBksp = event.rawX >= loc[0] && event.rawX <= loc[0] + bkspKey.width &&
                                         event.rawY >= loc[1] && event.rawY <= loc[1] + bkspKey.height

                        android.util.Log.d("DroidOS_Drag", "DROP CHECK: rawX=${event.rawX.toInt()}, rawY=${event.rawY.toInt()}, bksp=(${loc[0]},${loc[1]},${bkspKey.width},${bkspKey.height}), isOver=$isOverBksp")

                        if (isOverBksp) {
                            // DROPPED ON BACKSPACE -> DELETE
                            android.util.Log.d("DroidOS_Drag", "DROP ON BKSP: Calling onSuggestionDropped('${item.text}')")
                            listener?.onSuggestionDropped(item.text)
                            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        }
                    } else {
                        android.util.Log.e("DroidOS_Drag", "ERROR: bkspKey is NULL!")
                    }
                } else {
                    // CLICK -> SELECT
                    android.util.Log.d("DroidOS_Drag", "CANDIDATE CLICK: '${item.text}'")
                    listener?.onSuggestionClick(item.text, item.isNew)
                    view.performClick()
                }
                isCandidateDragging = false
                activeDragCandidate = null
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                android.util.Log.d("DroidOS_Drag", "CANDIDATE CANCEL: '${item.text}'")
                view.alpha = 1.0f
                isCandidateDragging = false
                activeDragCandidate = null
                val bkspKey = findViewWithTag<View>("BKSP")
                if (bkspKey != null) setKeyVisual(bkspKey, false, "BKSP")
                return true
            }
        }
        return false
    }
    // =================================================================================
    // END BLOCK: handleCandidateTouch with debug logging
    // =================================================================================

}
```

## File: app/src/main/java/com/example/coverscreentester/MainActivity.kt
```kotlin
package com.example.coverscreentester

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

// =================================================================================
// MAINACTIVITY: Permission Landing Page and Service Launcher
// SUMMARY: This activity displays permission status and launches the OverlayService
//          once all required permissions are granted. On subsequent launches where
//          permissions are already granted, it skips the UI and goes directly to
//          recalling/launching the service.
// =================================================================================

class MainActivity : AppCompatActivity(), Shizuku.OnRequestPermissionResultListener {

    private lateinit var btnFixRestricted: Button
    private lateinit var btnOpenAccessibility: Button
    private lateinit var btnStartCheck: Button
    private lateinit var btnSwitchDisplay: Button
    
    // Track if we've already initialized the UI
    private var uiInitialized = false

override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // DEBUG LOG
        val dId = intent.getIntExtra("displayId", -999)
        val force = intent.getBooleanExtra("force_start", false)
        val isRestart = intent.getBooleanExtra("IS_RESTART", false)
        android.util.Log.w("MainActivity", ">>> ON CREATE | Display: $dId | Force: $force | IsRestart: $isRestart <<<")
        
        if (dId != -999) {
            Toast.makeText(this, "Activity Woke: D$dId", Toast.LENGTH_SHORT).show()
        }
        
        // [FIX] Check for Force Start flag from Launcher (Hard Restart)
        // If this flag is present, we assume the Launcher has already handled 
        // permissions/shizuku and we should just start the service immediately.
        val isForceStart = intent.getBooleanExtra("force_start", false)
        
        // Check permissions OR Force Start flag
        if (isForceStart || checkCriticalPermissions()) {
            // All permissions granted (or forced) - skip UI and launch/recall service
            launchOverlayServiceAndFinish()
            return
        }
        
        // Permissions not granted - show the permission landing page
        initializePermissionUI()
    }
    
    // =================================================================================
    // FUNCTION: initializePermissionUI
    // SUMMARY: Sets up the permission status UI with buttons matching the actual layout.
    //          Only called when permissions are missing on first launch.
    // =================================================================================
    private fun initializePermissionUI() {
        setContentView(R.layout.activity_main)
        uiInitialized = true
        
        btnFixRestricted = findViewById(R.id.btn_fix_restricted)
        btnOpenAccessibility = findViewById(R.id.btn_open_accessibility)
        btnStartCheck = findViewById(R.id.btn_start_check)
        btnSwitchDisplay = findViewById(R.id.btn_switch_display)
        
        // Register Shizuku listener
        Shizuku.addRequestPermissionResultListener(this)
        
        // Setup click listeners
        btnFixRestricted.setOnClickListener { 
            openAppInfo()
        }
        
        btnOpenAccessibility.setOnClickListener { 
            openAccessibilitySettings() 
        }
        
        btnStartCheck.setOnClickListener {
            // First check Shizuku
            if (!isShizukuReady()) {
                requestShizukuPermission()
                return@setOnClickListener
            }
            
            if (checkCriticalPermissions()) {
                launchOverlayServiceAndFinish()
            } else {
                showMissingPermissionsToast()
            }
        }
        
        btnSwitchDisplay.setOnClickListener {
            // Send broadcast to switch display if service is running
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "SWITCH_DISPLAY"
            startService(intent)
        }
        
        updateButtonStates()
    }
    // =================================================================================
    // END FUNCTION: initializePermissionUI
    // =================================================================================

    override fun onResume() {
        super.onResume()
        
        // Only update UI if we're showing the permission page
        if (uiInitialized) {
            // Check again in case user granted permissions and came back
            if (checkCriticalPermissions()) {
                launchOverlayServiceAndFinish()
                return
            }
            updateButtonStates()
        }
    }

    // =================================================================================
    // FUNCTION: updateButtonStates
    // SUMMARY: Updates button colors/states based on current permission status.
    // =================================================================================
    private fun updateButtonStates() {
        if (!uiInitialized) return
        
        val isAccessibilityReady = isAccessibilityEnabled()
        val isShizukuReady = isShizukuReady()
        
        // Standard Colors
        val colorGreen = android.graphics.Color.parseColor("#4CAF50")
        val colorRed = android.graphics.Color.parseColor("#FF5555")
        
        // 1. Restricted Settings Button
        // Note: There is no direct API to check Restricted status. 
        // We use isAccessibilityReady as a proxy: if Accessibility is ON, Restricted Settings must be ALLOWED.
        if (isAccessibilityReady) {
            btnFixRestricted.backgroundTintList = android.content.res.ColorStateList.valueOf(colorGreen)
            btnFixRestricted.text = "1. Restricted Settings ✓"
        } else {
            btnFixRestricted.backgroundTintList = android.content.res.ColorStateList.valueOf(colorRed)
            btnFixRestricted.text = "1. Allow Restricted Settings"
        }

        // 2. Accessibility Button
        // Turns Green independently if Accessibility is enabled
        if (isAccessibilityReady) {
            btnOpenAccessibility.backgroundTintList = android.content.res.ColorStateList.valueOf(colorGreen)
            btnOpenAccessibility.text = "2. Accessibility ✓"
        } else {
            btnOpenAccessibility.backgroundTintList = android.content.res.ColorStateList.valueOf(colorRed)
            btnOpenAccessibility.text = "2. Enable Accessibility"
        }
        
        // 3. Shizuku Button
        // Turns Green independently if Shizuku is granted (even if Accessibility is off)
        if (isShizukuReady) {
            btnStartCheck.backgroundTintList = android.content.res.ColorStateList.valueOf(colorGreen)
            btnStartCheck.text = "3. Shizuku Granted ✓"
        } else {
            btnStartCheck.backgroundTintList = android.content.res.ColorStateList.valueOf(colorRed)
            btnStartCheck.text = "3. Grant Shizuku"
        }

        // 4. Launch App Button
        // Only turns Green when ALL critical permissions are ready
        btnSwitchDisplay.text = "Launch App"
        if (isAccessibilityReady && isShizukuReady) {
            btnSwitchDisplay.backgroundTintList = android.content.res.ColorStateList.valueOf(colorGreen)
        } else {
            btnSwitchDisplay.backgroundTintList = android.content.res.ColorStateList.valueOf(colorRed)
        }
    }
    // =================================================================================
    // END FUNCTION: updateButtonStates
    // =================================================================================

    // =================================================================================
    // FUNCTION: launchOverlayServiceAndFinish
    // SUMMARY: Launches the OverlayService on the current display and finishes
    //          the activity. This is called when all permissions are granted.
    // =================================================================================



    private fun launchOverlayServiceAndFinish() {
        // 1. Create the Service Intent (Rename to 'serviceIntent' to avoid shadowing bug)
        val serviceIntent = Intent(this, OverlayService::class.java)
        
        // 2. Read from the Activity Intent (Use 'getIntent()' explicitly)
        val originIntent = getIntent()

        // [FIX] Forward the Display ID
        // We check 'originIntent' (from Launcher) and put into 'serviceIntent' (for Service)
        if (originIntent.hasExtra("displayId")) {
            val targetId = originIntent.getIntExtra("displayId", 0)
            serviceIntent.putExtra("displayId", targetId)
        }
        
        // Forward force_start flag
        if (originIntent.hasExtra("force_start")) {
             serviceIntent.putExtra("force_start", true)
        }

        // 3. Start Service
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // 4. Minimize to keep process alive
        moveTaskToBack(true)
        
        // 5. Finish after delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            finish()
        }, 1500)
    }



    // =================================================================================
    // END FUNCTION: launchOverlayServiceAndFinish
    // =================================================================================

    // =================================================================================
    // FUNCTION: checkCriticalPermissions
    // SUMMARY: Returns true only if ALL required permissions are granted.
    //          Does NOT show toasts - used for silent checking.
    // =================================================================================
    private fun checkCriticalPermissions(): Boolean {
        // [Fixed] Removed Overlay Permission check. 
        // Since we use TYPE_ACCESSIBILITY_OVERLAY, the separate 'Appear on Top' permission is not strictly required.
        
        // 1. Accessibility
        if (!isAccessibilityEnabled()) {
            return false
        }

        // 2. Shizuku
        if (!isShizukuReady()) {
            return false
        }

        return true
    }
    // =================================================================================
    // END FUNCTION: checkCriticalPermissions
    // =================================================================================

    // =================================================================================
    // FUNCTION: showMissingPermissionsToast
    // SUMMARY: Shows a specific toast about which permission is missing.
    // =================================================================================
    private fun showMissingPermissionsToast() {
        when {
            !isAccessibilityEnabled() -> {
                Toast.makeText(this, "Missing: Accessibility Service", Toast.LENGTH_SHORT).show()
            }
            !isShizukuReady() -> {
                Toast.makeText(this, "Missing: Shizuku Permission", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // =================================================================================
    // END FUNCTION: showMissingPermissionsToast
    // =================================================================================

    // =================================================================================
    // FUNCTION: isAccessibilityEnabled
    // SUMMARY: Checks if our accessibility service is enabled in system settings.
    // =================================================================================
    private fun isAccessibilityEnabled(): Boolean {
        // [FIX] Read Settings.Secure directly. 
        // AccessibilityManager only lists *running* services. After a force-stop, 
        // the service is not running yet, so AM returns false. 
        // But the *Permission* is still granted in Settings, so we check that source of truth.
        return try {
            val expectedServiceName = "$packageName/.OverlayService"
            val enabledServices = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: ""
            
            // Check if our service string exists in the setting
            val colonSplit = enabledServices.split(":")
            colonSplit.any { component ->
                component.equals(expectedServiceName, ignoreCase = true) || 
                component.contains("$packageName/")
            }
        } catch (e: Exception) {
            false
        }
    }
    // =================================================================================
    // END FUNCTION: isAccessibilityEnabled
    // =================================================================================
    
    // =================================================================================
    // FUNCTION: isShizukuReady
    // SUMMARY: Returns true if Shizuku is running and we have permission.
    // =================================================================================
    private fun isShizukuReady(): Boolean {
        return try {
            Shizuku.getBinder() != null && 
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }
    // =================================================================================
    // END FUNCTION: isShizukuReady
    // =================================================================================
    
    // =================================================================================
    // HELPER FUNCTIONS: Open Settings Pages
    // =================================================================================
    private fun openAccessibilitySettings() {
        try {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open Accessibility settings", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openAppInfo() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
            Toast.makeText(this, "Tap 3 dot hamburger menu for 'Allow Restricted Settings' at top right corner. If not visible, enable at next step and return here.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open App Info", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun requestShizukuPermission() {
        try {
            if (Shizuku.getBinder() == null) {
                Toast.makeText(this, "Shizuku is not running. Please start Shizuku first.", Toast.LENGTH_LONG).show()
                return
            }
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(0)
            } else {
                Toast.makeText(this, "Shizuku permission already granted", Toast.LENGTH_SHORT).show()
                updateButtonStates()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    // =================================================================================
    // END HELPER FUNCTIONS
    // =================================================================================

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Shizuku Granted!", Toast.LENGTH_SHORT).show()
            updateButtonStates()
            
            // Auto-launch if this was the last missing permission
            if (checkCriticalPermissions()) {
                launchOverlayServiceAndFinish()
            }
        } else {
            Toast.makeText(this, "Shizuku permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            Shizuku.removeRequestPermissionResultListener(this)
        } catch (e: Exception) {}
    }
}

// =================================================================================
// END FILE: MainActivity.kt
// =================================================================================
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
import android.widget.Toast

class ManualAdjustActivity : Activity() {

    private var isMoveMode = true
    private var isKeyboardTarget = false
    private val STEP_SIZE = 10

    private lateinit var textMode: TextView
    private lateinit var btnToggle: Button
    private lateinit var btnToggleTarget: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_adjust)

        textMode = findViewById(R.id.text_mode)
        btnToggle = findViewById(R.id.btn_toggle_mode)
        btnToggleTarget = findViewById(R.id.btn_toggle_target)

        updateModeUI()

        btnToggle.setOnClickListener {
            isMoveMode = !isMoveMode
            updateModeUI()
        }

        btnToggleTarget.setOnClickListener {
            isKeyboardTarget = !isKeyboardTarget
            updateModeUI()
        }

        findViewById<Button>(R.id.btn_up).setOnClickListener { sendAdjust(0, -STEP_SIZE) }
        findViewById<Button>(R.id.btn_down).setOnClickListener { sendAdjust(0, STEP_SIZE) }
        findViewById<Button>(R.id.btn_left).setOnClickListener { sendAdjust(-STEP_SIZE, 0) }
        findViewById<Button>(R.id.btn_right).setOnClickListener { sendAdjust(STEP_SIZE, 0) }

        // RESET BUTTON
        findViewById<Button>(R.id.btn_center).setOnClickListener {
            val targetStr = if (isKeyboardTarget) "KEYBOARD" else "TRACKPAD"
            Toast.makeText(this, "Resetting $targetStr", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, OverlayService::class.java)
            intent.action = "RESET_POSITION"
            intent.putExtra("TARGET", targetStr)
            startService(intent)
        }

        // ROTATE BUTTON
        findViewById<Button>(R.id.btn_rotate).setOnClickListener {
            val targetStr = if (isKeyboardTarget) "KEYBOARD" else "TRACKPAD"
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "ROTATE"
            intent.putExtra("TARGET", targetStr)
            startService(intent)
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
    }

    private fun updateModeUI() {
        val targetText = if (isKeyboardTarget) "KEYBOARD" else "TRACKPAD"
        val actionText = if (isMoveMode) "POSITION" else "SIZE"

        textMode.text = "$targetText: $actionText"

        if (isKeyboardTarget) {
            textMode.setTextColor(Color.MAGENTA)
            btnToggleTarget.text = "Target: KEYBOARD"
        } else {
            textMode.setTextColor(if (isMoveMode) Color.GREEN else Color.CYAN)
            btnToggleTarget.text = "Target: TRACKPAD"
        }

        btnToggle.text = if (isMoveMode) "Mode: Position" else "Mode: Size"
    }

    private fun sendAdjust(xChange: Int, yChange: Int) {
        val intent = Intent(this, OverlayService::class.java)
        intent.action = "MANUAL_ADJUST"
        intent.putExtra("TARGET", if (isKeyboardTarget) "KEYBOARD" else "TRACKPAD")

        if (isMoveMode) {
            intent.putExtra("DX", xChange)
            intent.putExtra("DY", yChange)
        } else {
            intent.putExtra("DW", xChange)
            intent.putExtra("DH", yChange)
        }
        startService(intent)
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/NullInputMethodService.kt
```kotlin
package com.example.coverscreentester

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.os.Build
import androidx.core.content.ContextCompat

class NullInputMethodService : InputMethodService() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                // COMMAND 1: SWITCH KEYBOARD (Restore Gboard)
                "com.example.coverscreentester.RESTORE_IME" -> {
                    val targetIme = intent.getStringExtra("target_ime")
                    if (!targetIme.isNullOrEmpty()) {
                        try {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            val token = window?.window?.attributes?.token
                            if (token != null) {
                                imm.setInputMethod(token, targetIme)
                            }
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }


                // COMMAND 3: INJECT TEXT (Swipe/Prediction Support)
                "com.example.coverscreentester.INJECT_TEXT" -> {
                    val text = intent.getStringExtra("text")
                    if (!text.isNullOrEmpty() && currentInputConnection != null) {
                        // commitText inserts the string at the cursor position
                        // '1' moves the cursor to the end of the inserted text
                        currentInputConnection.commitText(text, 1)
                    }
                }

                // COMMAND 4: BULK DELETE (Robust Swipe Undo)
                "com.example.coverscreentester.INJECT_DELETE" -> {
                    val length = intent.getIntExtra("length", 0)
                    if (length > 0 && currentInputConnection != null) {
                        // deleteSurroundingText(beforeLength, afterLength)
                        currentInputConnection.deleteSurroundingText(length, 0)
                    }
                }

            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        val filter = IntentFilter().apply {
            addAction("com.example.coverscreentester.RESTORE_IME")
            addAction("com.example.coverscreentester.INJECT_KEY")
            addAction("com.example.coverscreentester.INJECT_TEXT")
            addAction("com.example.coverscreentester.INJECT_DELETE")
        }

        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(receiver) } catch (e: Exception) {}
    }

    override fun onCreateInputView(): View {
        // Return a zero-sized, hidden view
        return View(this).apply { 
            layoutParams = android.view.ViewGroup.LayoutParams(0, 0)
            visibility = View.GONE
        }
    }
    
    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        // Crucial: Tell system NOT to allocate screen space for this keyboard
        return false 
    }
    
    override fun onEvaluateFullscreenMode(): Boolean = false // Important: Never take over full screen
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
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Process
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Display
import android.view.GestureDetector
import android.view.Gravity
import android.view.InputDevice
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import android.os.PowerManager
import java.util.ArrayList
import com.example.coverscreentester.BuildConfig

class OverlayService : AccessibilityService(), DisplayManager.DisplayListener {


    private var isAccessibilityReady = false
    private var pendingDisplayId = -1
    // Track last time we injected text to distinguish our events from user touches
    private var lastInjectionTime = 0L


    // === RECEIVER & ACTIONS - START ===
    private val commandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            
            // Helper to match both OLD and NEW package names
            fun matches(cmd: String): Boolean {
                return action.endsWith(cmd)
            }

            if (matches("SOFT_RESTART")) {
                Log.d("OverlayService", "Received SOFT_RESTART")
                performSoftRestart()
            } else if (matches("ENFORCE_ZORDER")) {
                Log.d("OverlayService", "Received ENFORCE_ZORDER")
                enforceZOrder()
            } else if (matches("MOVE_TO_DISPLAY")) {
                val targetId = intent.getIntExtra("displayId", 0)
                Log.d("OverlayService", "Moving to Display: $targetId")
                handler.post {
                    removeOldViews()
                    setupUI(targetId)
                    enforceZOrder()
                }
            } else if (matches("TOGGLE_MIRROR") || matches("TOGGLE_VIRTUAL_MIRROR")) {
                Log.d("OverlayService", "Toggling Mirror Mode")
                handler.post { toggleVirtualMirrorMode() }
            } else if (matches("OPEN_DRAWER")) {
                Log.d("OverlayService", "Opening Drawer")
                handler.post { toggleDrawer() }
            } else if (matches("STOP_SERVICE")) {
                Log.d("OverlayService", "Stopping Service")
                forceExit()
            }
        }
    }

    private fun performSoftRestart() {
        // [FIX] HARD RESET (Process Kill)
        // This is the ONLY method that fixes Z-order relative to the Launcher.
        // We rely on the updated scheduleRestart() (Activity Launch) to bring the app back alive.
        handler.post {
            Toast.makeText(this, "Restarting System...", Toast.LENGTH_SHORT).show()
            
            // 1. Schedule the wake-up alarm
            scheduleRestart()
            
            // 2. Kill the process after a short delay
            handler.postDelayed({
                forceExit()
            }, 500)
        }
    }



    fun enforceZOrder() {
        try {
            if (windowManager != null) {
                // LAYER 1: TRACKPAD (Base Layer)
                // Remove and Re-add to ensure it is on top of OTHER apps (like Launcher),
                // but we will stack our own Keyboard on top of this shortly.
                if (trackpadLayout != null && trackpadLayout?.isAttachedToWindow == true) {
                    try {
                        windowManager?.removeView(trackpadLayout)
                        windowManager?.addView(trackpadLayout, trackpadParams)
                    } catch(e: Exception) {
                        try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(z: Exception) {}
                    }
                }

                // LAYER 2: KEYBOARD (Middle Layer)
                // [FIX] Explicitly bring Keyboard to front so it sits ON TOP of the Trackpad
                if (keyboardOverlay != null && keyboardOverlay?.isShowing() == true) {
                     keyboardOverlay?.bringToFront()
                }

                // LAYER 3: MENU
                if (menuManager != null) {
                    try { menuManager?.bringToFront() } catch(e: Exception) {}
                }

                // LAYER 4: BUBBLE
                if (bubbleView != null && bubbleView?.isAttachedToWindow == true) {
                    try {
                        windowManager?.removeView(bubbleView)
                        windowManager?.addView(bubbleView, bubbleParams)
                    } catch (e: Exception) {
                         try { windowManager?.updateViewLayout(bubbleView, bubbleParams) } catch(z: Exception) {}
                    }
                }

                // LAYER 5: CURSOR (Top Layer)
                // Must be added LAST to float over everything
                if (cursorLayout != null && cursorLayout?.isAttachedToWindow == true) {
                    try {
                        windowManager?.removeView(cursorLayout)
                        windowManager?.addView(cursorLayout, cursorParams)
                    } catch (e: Exception) {
                        try { windowManager?.addView(cursorLayout, cursorParams) } catch(z: Exception) {}
                    }
                }
                
                Log.d("OverlayService", "Z-Order Enforced: Trackpad -> Keyboard -> Cursor")
            }
        } catch (e: Exception) {
            Log.e("OverlayService", "Z-Order failed", e)
        }
    }


    // === RECEIVER & ACTIONS - END ===

    private val TAG = "OverlayService"

    var windowManager: WindowManager? = null
    var displayManager: DisplayManager? = null
    var shellService: IShellService? = null
    private var appWindowManager: WindowManager? = null
    private var isBound = false
    private val handler = Handler(Looper.getMainLooper())

    // Create a single worker queue for all input events to prevent race conditions
    private val inputExecutor = java.util.concurrent.Executors.newSingleThreadExecutor()

    private var lastBlockTime: Long = 0

    // =================================================================================
    // VIRTUAL DISPLAY KEEP-ALIVE SYSTEM
    // SUMMARY: Prevents system from timing out the display when using trackpad on a
    //          remote/virtual display. The overlay consumes touch events so the system
    //          doesn't see "real" user activity. We solve this by:
    //          1. Holding a SCREEN_BRIGHT_WAKE_LOCK when targeting remote display
    //          2. Periodically calling userActivity() via shell during active touch
    // =================================================================================
    private var powerManager: PowerManager? = null
    private var displayWakeLock: PowerManager.WakeLock? = null
    private var lastUserActivityPing: Long = 0
    private val USER_ACTIVITY_PING_INTERVAL_MS = 30_000L // Ping every 30 seconds during active use
    // =================================================================================
    // END BLOCK: VIRTUAL DISPLAY KEEP-ALIVE SYSTEM VARIABLES
    // =================================================================================

    private var bubbleView: View? = null
    private var trackpadLayout: FrameLayout? = null
    private var cursorLayout: FrameLayout? = null
    private var cursorView: ImageView? = null
    
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var trackpadParams: WindowManager.LayoutParams
    private lateinit var cursorParams: WindowManager.LayoutParams

    private var menuManager: TrackpadMenuManager? = null
    private var savedKbX = 0
    private var savedKbY = 0
    private var savedKbW = 0
    private var savedKbH = 0
    private var keyboardOverlay: KeyboardOverlay? = null

    var currentDisplayId = 0
    var inputTargetDisplayId = 0
    var isTrackpadVisible = true
    var isCustomKeyboardVisible = false
    var isScreenOff = false
    private var isPreviewMode = false
    private var previousImeId: String? = null
    
    // --- SMART RESTORE STATE ---
    private var pendingRestoreTrackpad = false
    private var pendingRestoreKeyboard = false
    private var hasPendingRestore = false

    // =================================================================================
    // VIRTUAL MIRROR MODE STATE
    // SUMMARY: Tracks component visibility before entering mirror mode so we can
    //          restore to previous state when exiting. Also stores the display ID
    //          we were targeting before mirror mode was enabled.
    // =================================================================================
    private var preMirrorTrackpadVisible = false
    private var preMirrorKeyboardVisible = false
    private var preMirrorTargetDisplayId = 0
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE STATE
    // =================================================================================

    private var isVoiceActive = false
    
    
    
    // Heartbeat to keep hardware state alive AND enforce settings
    private val blockingHeartbeat = object : Runnable {
        override fun run() {
            // No-op: Null Keyboard handles blocking natively
        }
    }
    
    class Prefs {
        var cursorSpeed = 2.5f
        var scrollSpeed = 1.0f 
        var prefTapScroll = true 
        var prefVibrate = false
        var prefReverseScroll = true
        var prefAlpha = 200
        var prefBgAlpha = 0
        var prefKeyboardAlpha = 200
        var prefHandleSize = 60 
        var prefVPosLeft = false
        var prefHPosTop = false
        var prefLocked = false
        var prefHandleTouchSize = 80
        var prefScrollTouchSize = 80 
        var prefScrollVisualSize = 4
        var prefCursorSize = 50 
        var prefKeyScale = 69 // Default to 69% to match Reset Position (0.55 ratio)
        var prefUseAltScreenOff = true
        var prefAutomationEnabled = true
        var prefBubbleX = 50
        var prefBubbleY = 300
        var prefAnchored = false 
        var prefBubbleSize = 100        
        var prefBubbleIconIndex = 0     
        var prefBubbleAlpha = 255       
        var prefPersistentService = false 
        var prefBlockSoftKeyboard = false
        
        // Defaults set to "none" (System Default)
        var hardkeyVolUpTap = "none"
        var hardkeyVolUpDouble = "none"
        var hardkeyVolUpHold = "none"
        var hardkeyVolDownTap = "none"
        var hardkeyVolDownDouble = "none"
        var hardkeyVolDownHold = "none"
        var hardkeyPowerDouble = "none"
        var doubleTapMs = 300
        var holdDurationMs = 400
        var displayOffMode = "alternate"

        // =================================================================================
        // VIRTUAL MIRROR MODE PREFERENCES
        // SUMMARY: Settings for displaying a mirror keyboard on remote/AR display.
        //          When enabled, touching the physical keyboard shows an orange orientation
        //          trail on both displays. After finger stops for orientDelayMs, normal
        //          keyboard input resumes.
        // =================================================================================
        var prefVirtualMirrorMode = false
        var prefMirrorOrientDelayMs = 1000L  // Default 1 second orientation delay
        
        // Mirror Keyboard Prefs
        var prefMirrorAlpha = 200
        var prefMirrorX = -1      // -1 = auto center
        var prefMirrorY = 0
        var prefMirrorWidth = -1  // -1 = auto
        var prefMirrorHeight = -1 // -1 = auto
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE PREFERENCES
        // =================================================================================
    }
    val prefs = Prefs()

    // =========================
    // KEY INJECTION
    // =========================
    private fun injectKey(keyCode: Int, action: Int = KeyEvent.ACTION_DOWN, metaState: Int = 0) {
        // Dynamic Device ID:
        // Blocking ON: Use 1 (Physical) to maintain "Hardware Keyboard" state.
        // Blocking OFF: Use -1 (Virtual). ID 0 is often ignored by Gboard. -1 is standard software injection.
        val deviceId = if (prefs.prefBlockSoftKeyboard) 1 else -1
        shellService?.injectKey(keyCode, action, metaState, inputTargetDisplayId, deviceId)
    }

    // =========================
    // BLOCKING TRIGGER (Global)
    // =========================

// FILE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
// LOC: Around line 1550 (Search for 'fun triggerAggressiveBlocking')

// =================================================================================
// FUNCTION: triggerAggressiveBlocking
// SUMMARY: Enforces Soft Keyboard blocking.
// CHANGES: Commented out 'enforceZOrder()' to stop the UI Lag Loop. 
//          We now rely purely on 'softKeyboardController.showMode' which is faster.
// =================================================================================
    private fun triggerAggressiveBlocking() {
        // [PERFORMANCE FIX] 
        // Previously, this called 'enforceZOrder()' which removed/re-added views.
        // This caused massive lag/stutter on the Cover Screen.
        // Since we are already setting SHOW_MODE_HIDDEN below, the manual Z-order 
        // shuffle is unnecessary and harmful.
        
        // enforceZOrder() // <--- DISABLED TO FIX LAG

        // Rely on standard Android API to suppress keyboard
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                if (softKeyboardController.showMode != AccessibilityService.SHOW_MODE_HIDDEN) {
                    softKeyboardController.showMode = AccessibilityService.SHOW_MODE_HIDDEN
                }
            } catch (e: Exception) {
                // Controller might not be connected yet
            }
        }
    }
// =================================================================================
// END FUNCTION: triggerAggressiveBlocking
// =================================================================================




    private fun setSoftKeyboardBlocking(enabled: Boolean) {
        if (shellService == null) {
            showToast("Shizuku required for Keyboard Blocking")
            return
        }

        Thread {
            try {
                // 1. Find correct ID dynamically for OUR Null Keyboard
                val listOutput = shellService?.runCommand("ime list -a -s") ?: ""
                val myImeId = listOutput.lines().firstOrNull { 
                    it.contains(packageName) && it.contains("NullInputMethodService") 
                }?.trim()

                if (myImeId.isNullOrEmpty()) {
                    handler.post { showToast("Error: Null Keyboard not found.") }
                    return@Thread
                }

                if (enabled) {
                    // --- BLOCKING (Switch to Null Keyboard) ---
                    
                    // 1. Enable our Null Keyboard
                    shellService?.runCommand("ime enable $myImeId")
                    
                    // 2. Set it as active
                    shellService?.runCommand("ime set $myImeId")
                    
                    // 3. Ensure OS doesn't try to show popups
                    shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 0")
                    
                    handler.post { showToast("Keyboard Blocked") }
                    
                } else {
                    // --- UNBLOCKING (Force System Fallback) ---
                    
                    // 1. Reset settings
                    shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1")
                    
                    // 2. DISABLE our Null Keyboard
                    // This forces Android to immediately switch back to the User's System Default
                    // (Samsung, Gboard, etc.) because the active one (Null) is no longer valid.
                    shellService?.runCommand("ime disable $myImeId")
                    
                    handler.post { showToast("Keyboard Restored") }
                }
            } catch (e: Exception) {
                handler.post { showToast("Error: ${e.message}") }
            }
        }.start()
    }




    // =================================================================================
    // KEYBOARD RESTORATION HELPERS
    // =================================================================================
    private var lastMainCheck = 0L
    private var lastCoverCheck = 0L

    private fun ensureSystemKeyboardRestored() {
        // Throttle: Check max once every 2 seconds
        if (System.currentTimeMillis() - lastMainCheck < 2000) return
        lastMainCheck = System.currentTimeMillis()

        Thread {
            try {
                // Check if Null Keyboard is currently active
                val current = shellService?.runCommand("settings get secure default_input_method") ?: ""
                if (current.contains(packageName) && current.contains("NullInputMethodService")) {
                    android.util.Log.i(TAG, "Main Screen Detected: Restoring System Keyboard...")
                    handler.post { setSoftKeyboardBlocking(false) }
                }
            } catch(e: Exception) {}
        }.start()
    }

    private fun ensureKeyboardBlocked() {
        // Throttle: Check max once every 2 seconds
        if (System.currentTimeMillis() - lastCoverCheck < 2000) return
        lastCoverCheck = System.currentTimeMillis()

        Thread {
            try {
                // Check if Null Keyboard is NOT active
                val current = shellService?.runCommand("settings get secure default_input_method") ?: ""
                if (!current.contains("NullInputMethodService")) {
                    android.util.Log.i(TAG, "Cover Screen Detected: Enforcing Null Keyboard...")
                    handler.post { setSoftKeyboardBlocking(true) }
                }
            } catch(e: Exception) {}
        }.start()
    }

    // =================================================================================
    // FUNCTION: onAccessibilityEvent
    // SUMMARY: Monitors system events to manage Keyboard Blocking and Mode Switching.
    // UPDATED: Now explicitly UNBLOCKS the keyboard when Main Screen (0) is active.
    // =================================================================================
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // [FIX] MAIN SCREEN GUARD
        // If interaction is on the Main Display (0), we must ensure the keyboard works.
        // This overrides any blocking preferences intended for the Cover Screen.
        // Use windowId != -1 to ensure it's a real UI event.
        if (event.displayId == Display.DEFAULT_DISPLAY && event.windowId != -1) {
            
            // 1. Unlock Accessibility Soft Keyboard Mode (if it was hidden)
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    if (softKeyboardController.showMode == AccessibilityService.SHOW_MODE_HIDDEN) {
                        softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO
                    }
                } catch (e: Exception) {}
            }

            // 2. Restore System IME (if blocking preference is on, we temporarily disable it for Main Screen)
            if (prefs.prefBlockSoftKeyboard) {
                ensureSystemKeyboardRestored()
            }
            
            // CRITICAL: Stop processing. Do NOT run blocking logic for Main Screen events.
            return
        }

        // [FIX 2] Standard Multi-Display Filter
        // Ignore events from displays we aren't managing (unless it was Main Screen handled above)
        if (event.displayId != currentDisplayId) {
            return
        }

        val eventPkg = event.packageName?.toString() ?: ""

        // [FIX 3] Anti-Loop (Ignore Self)
        if (eventPkg == packageName) return

        // [FIX 4] Allow Voice Input
        if (eventPkg.contains("google.android.googlequicksearchbox") ||
            eventPkg.contains("com.google.android.voicesearch") ||
            eventPkg.contains("com.google.android.tts")) {

            if (prefs.prefBlockSoftKeyboard && shellService != null) {
                 Thread { try { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") } catch(e: Exception){} }.start()
            }
            return
        }
        
        // [FIX] External Cursor Movement Detection
        // If selection changes or user clicks text in the target app, reset swipe history.
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED || 
            event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            
            val timeSinceInjection = System.currentTimeMillis() - lastInjectionTime
            if (timeSinceInjection > 500) {
                keyboardOverlay?.resetSwipeHistory()
            }
        }

        // [FIX 5] Throttle & Execute Blocking (Only on Cover Screen)
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {

            if (prefs.prefBlockSoftKeyboard && !isVoiceActive) {
                 // 1. Ensure IME is switched to Null Keyboard
                 ensureKeyboardBlocked()

                 val currentTime = System.currentTimeMillis()
                 // 2. Throttle Aggressive Hidden Mode
                 if (currentTime - lastBlockTime > 500) {
                     lastBlockTime = currentTime

                     triggerAggressiveBlocking()

                     // Enforce Hidden Mode
                     if (Build.VERSION.SDK_INT >= 24) {
                         try {
                             if (softKeyboardController.showMode != AccessibilityService.SHOW_MODE_HIDDEN) {
                                 softKeyboardController.showMode = AccessibilityService.SHOW_MODE_HIDDEN
                             }
                         } catch(e: Exception) {}
                     }
                 }
            }
        }
    }

// =================================================================================
// END FUNCTION: onAccessibilityEvent
// =================================================================================


    // =========================
    // STANDARD OVERRIDES
    // =========================
    private var uiScreenWidth = 1080
    private var uiScreenHeight = 2640
    private var targetScreenWidth = 1920
    private var targetScreenHeight = 1080
    private var cursorX = 300f
    private var cursorY = 300f
    private var rotationAngle = 0
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isTouchDragging = false
    private var isLeftKeyHeld = false
    private var isRightKeyHeld = false
    private var isVScrolling = false
    private var isHScrolling = false
    private var dragDownTime: Long = 0L
    private var hasSentTouchDown = false
    private var hasSentMouseDown = false
    private var activeFingerDeviceId = -1
    private var ignoreTouchSequence = false
    private var isDebugMode = false
    private var isKeyboardMode = false
    private var savedWindowX = 0
    private var savedWindowY = 0
    private var currentBorderColor = 0xFFFFFFFF.toInt()
    private var scrollAccumulatorX = 0f
    private var scrollAccumulatorY = 0f

    private var touchDownTime: Long = 0L
    private var touchDownX: Float = 0f
    private var touchDownY: Float = 0f
    private var isReleaseDebouncing = false
    private val releaseDebounceRunnable = Runnable { isReleaseDebouncing = false }
    
    private val TAP_TIMEOUT_MS = 300L
    private val TAP_SLOP_PX = 15f
    private val RELEASE_DEBOUNCE_MS = 50L

    private var scrollZoneThickness = 80
    private val handleContainers = ArrayList<FrameLayout>()
    private val handleVisuals = ArrayList<View>()
    private var vScrollContainer: FrameLayout? = null
    private var hScrollContainer: FrameLayout? = null
    private var vScrollVisual: View? = null
    private var hScrollVisual: View? = null
    private var debugTextView: TextView? = null

    private var remoteWindowManager: WindowManager? = null
    private var remoteCursorLayout: FrameLayout? = null
    private var remoteCursorView: ImageView? = null
    private lateinit var remoteCursorParams: WindowManager.LayoutParams

    // =================================================================================
    // VIRTUAL MIRROR MODE VARIABLES
    // =================================================================================
    private var mirrorWindowManager: WindowManager? = null
    private var mirrorKeyboardContainer: FrameLayout? = null
    private var mirrorKeyboardView: KeyboardView? = null
    private var mirrorTrailView: SwipeTrailView? = null
    private var mirrorKeyboardParams: WindowManager.LayoutParams? = null

    // Dimensions for coordinate scaling between physical and mirror keyboards
    private var physicalKbWidth = 0f
    private var physicalKbHeight = 0f
    private var mirrorKbWidth = 0f
    private var mirrorKbHeight = 0f

    private var isInOrientationMode = false
    private var isMirrorDragActive = false
    private var isHoveringBackspace = false // [FIX] Add this variable
    private var draggedPredictionIndex = -1 // [FIX] Add this missing variable
    private var lastOrientX = 0f
    private var lastOrientY = 0f
    private val MOVEMENT_THRESHOLD = 15f  // Pixels - ignore movement smaller than this
    private var orientationModeHandler = Handler(Looper.getMainLooper())
    private var lastOrientationTouchTime = 0L

    // =================================================================================
    // MIRROR MODE KEY REPEAT VARIABLES
    // SUMMARY: Variables for repeating backspace/arrow keys when held during orange
    //          trail orientation mode. Only active in mirror mode, doesn't affect
    //          normal blue trail swipe typing.
    // =================================================================================
    private val mirrorRepeatHandler = Handler(Looper.getMainLooper())
    private var mirrorRepeatKey: String? = null
    private var isMirrorRepeating = false
    private val MIRROR_REPEAT_INITIAL_DELAY = 400L
    private val MIRROR_REPEAT_INTERVAL = 50L
    
    // Keys that can repeat in mirror orientation mode (backspace + arrows)
    private val mirrorRepeatableKeys = setOf("BKSP", "⌫", "←", "→", "↑", "↓", "◄", "▲", "▼", "►")
    

    private val mirrorRepeatRunnable = object : Runnable {
        override fun run() {
            mirrorRepeatKey?.let { key ->
                // FIXED: Removed '&& isInOrientationMode' check.
                // This allows repeat to work in Blue Mode (where isInOrientationMode is false).
                if (isMirrorRepeating) {
                    Log.d(TAG, "Mirror repeat: $key")
                    keyboardOverlay?.triggerKeyPress(key)
                    mirrorRepeatHandler.postDelayed(this, MIRROR_REPEAT_INTERVAL)
                }
            }
        }
    }

    // =================================================================================
    // END BLOCK: MIRROR MODE KEY REPEAT VARIABLES
    // =================================================================================    // =================================================================================
    // RUNNABLE: orientationModeTimeout
    // SUMMARY: Fires when finger has been still for delay period.
    //          Switches from orange trail to blue trail.
    //          Initializes swipe tracking so path collection starts NOW.
    // =================================================================================




    private val orientationModeTimeout = Runnable {
        // DYNAMIC HEIGHT CALCULATION
        val currentHeight = if (physicalKbHeight > 0) physicalKbHeight else 400f
        
        // ADJUSTMENT: Set to 19% (0.19f)
        // 0.23f blocked the top row. 0.17f caused ghost words. 0.19f is the sweet spot.
        val stripHeight = currentHeight * 0.12f

        // Check if finger is currently holding on the Prediction Bar
        if (lastOrientY < stripHeight) {

             // START DRAG MODE (Blue Trail)
             isInOrientationMode = false
             keyboardOverlay?.setOrientationMode(false)
             mirrorTrailView?.clear()
             keyboardOverlay?.clearOrientationTrail()
             
             isMirrorDragActive = true
             
             // Calculate index
             val width = if (physicalKbWidth > 0) physicalKbWidth else 1080f
             val slotWidth = width / 3f
             draggedPredictionIndex = (lastOrientX / slotWidth).toInt().coerceIn(0, 2)

             // START BLUE TRAIL IMMEDIATELY
             mirrorTrailView?.setTrailColor(0xFF4488FF.toInt())
             mirrorTrailView?.addPoint(lastOrientX, lastOrientY)
             
             val now = SystemClock.uptimeMillis()
             val scaleX = if (physicalKbWidth > 0) mirrorKbWidth / physicalKbWidth else 1f
             val scaleY = if (physicalKbHeight > 0) mirrorKbHeight / physicalKbHeight else 1f
             val mx = lastOrientX * scaleX
             val my = lastOrientY * scaleY
             
             val event = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, mx, my, 0)
             mirrorKeyboardView?.dispatchTouchEvent(event)
             event.recycle()
             
             return@Runnable
        }

        // CASE B: Holding on Keys -> START BLUE TRAIL
        isInOrientationMode = false
        mirrorTrailView?.clear()
        keyboardOverlay?.clearOrientationTrail()
        keyboardOverlay?.setOrientationMode(false)
        mirrorTrailView?.setTrailColor(0xFF4488FF.toInt()) 
        keyboardOverlay?.startSwipeFromCurrentPosition(lastOrientX, lastOrientY)

        val scaleX = if (physicalKbWidth > 0) mirrorKbWidth / physicalKbWidth else 1f
        val scaleY = if (physicalKbHeight > 0) mirrorKbHeight / physicalKbHeight else 1f
        mirrorTrailView?.addPoint(lastOrientX * scaleX, lastOrientY * scaleY)
        mirrorKeyboardView?.alpha = 0.7f

        // Immediate Repeat Check
        val currentKey = keyboardOverlay?.getKeyAtPosition(lastOrientX, lastOrientY)
        if (currentKey != null && mirrorRepeatableKeys.contains(currentKey)) {
            mirrorRepeatKey = currentKey
            isMirrorRepeating = true
            keyboardOverlay?.triggerKeyPress(currentKey)
            mirrorRepeatHandler.postDelayed(mirrorRepeatRunnable, MIRROR_REPEAT_INTERVAL)
        }
    }



    // =================================================================================
    // END BLOCK: orientationModeTimeout
    // =================================================================================

    // =================================================================================
    // MIRROR FADE OUT HANDLER
    // SUMMARY: Fades the mirror keyboard to fully transparent after inactivity.
    // =================================================================================
    private val mirrorFadeHandler = Handler(Looper.getMainLooper())
    private val mirrorFadeRunnable = Runnable {
        mirrorKeyboardView?.animate()?.alpha(0f)?.setDuration(300)?.start()
        mirrorKeyboardContainer?.animate()?.alpha(0f)?.setDuration(300)?.start()
    }
    // =================================================================================
    // END BLOCK: MIRROR FADE OUT HANDLER
    // =================================================================================
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE VARIABLES
    // =================================================================================

    private val longPressRunnable = Runnable { startTouchDrag() }
    private val clearHighlightsRunnable = Runnable { updateBorderColor(currentBorderColor); updateLayoutSizes() }
    
    private var isKeyDragging = false
    private var activeDragButton = MotionEvent.BUTTON_PRIMARY
    
    private var lastVolUpTime: Long = 0L
    private var lastVolDownTime: Long = 0L
    // HARDKEY STATE TRACKING
    private var volUpTapCount = 0
    private var volDownTapCount = 0
    private var volUpHoldTriggered = false
    private var volDownHoldTriggered = false
    private var volUpDragActive = false
    private var volDownDragActive = false
    private var lastManualSwitchTime: Long = 0L

    private val volUpHoldRunnable = Runnable {
        volUpHoldTriggered = true
        executeHardkeyAction(prefs.hardkeyVolUpHold, KeyEvent.ACTION_DOWN)
    }

    private val volDownHoldRunnable = Runnable {
        volDownHoldTriggered = true
        executeHardkeyAction(prefs.hardkeyVolDownHold, KeyEvent.ACTION_DOWN)
    }

    private val volUpDoubleTapRunnable = Runnable {
        if (volUpTapCount == 1) {
            executeHardkeyAction(prefs.hardkeyVolUpTap, KeyEvent.ACTION_UP)
        }
        volUpTapCount = 0
    }

    private val volDownDoubleTapRunnable = Runnable {
        if (volDownTapCount == 1) {
            executeHardkeyAction(prefs.hardkeyVolDownTap, KeyEvent.ACTION_UP)
        }
        volDownTapCount = 0
    }

    // =================================================================================
    // SECTION: BroadcastReceiver & Window Focus Logic
    // SUMMARY: Updates VOICE_TYPE_TRIGGERED.
    //          REMOVED the logic that restored 'setOverlayFocusable(true)'.
    //          The overlay must remain NOT_FOCUSABLE so Termux retains input focus.
    // =================================================================================
    // =================================================================================
    // SECTION: BroadcastReceiver (Voice Trigger)
    // SUMMARY: Turns the indicator GREEN when Voice is triggered.
    //          Sets isVoiceActive = true immediately.
    // =================================================================================
    // =================================================================================
    // INTER-APP COMMAND RECEIVER
    // SUMMARY: BroadcastReceiver for ADB commands and inter-app communication.
    //          Allows DroidOS Launcher to control Trackpad without killing permissions.
    //          Commands can be sent via ADB: adb shell am broadcast -a <ACTION>
    // =================================================================================
    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            
            // Helper to match both old (example) and new (katsuyamaki) package actions
            fun matches(suffix: String): Boolean = action.endsWith(suffix)

            when {
                // Internal Actions (Exact Match)
                action == "SWITCH_DISPLAY" -> switchDisplay()
                action == "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                action == "RESET_CURSOR" -> resetCursorCenter()
                action == "TOGGLE_DEBUG" -> toggleDebugMode()
                action == "FORCE_KEYBOARD" || action == "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                action == "OPEN_MENU" -> { menuManager?.show(); enforceZOrder() }
                action == "SET_TRACKPAD_VISIBILITY" -> {
                    val visible = intent.getBooleanExtra("VISIBLE", true)
                    val menuDisplayId = intent.getIntExtra("MENU_DISPLAY_ID", -1)
                    if (visible) setTrackpadVisibility(true) 
                    else { if (menuDisplayId == -1 || menuDisplayId == currentDisplayId) setTrackpadVisibility(false) }
                }
                action == "SET_PREVIEW_MODE" -> setPreviewMode(intent.getBooleanExtra("PREVIEW_MODE", false))
                action == "VOICE_TYPE_TRIGGERED" -> {
                    isVoiceActive = true
                    keyboardOverlay?.setVoiceActive(true)
                    setOverlayFocusable(false)
                    handler.postDelayed({ attemptRefocusInput() }, 300)
                }
                action == Intent.ACTION_SCREEN_ON -> {
                    // [FIX] Only trigger blocking if explicitly enabled in prefs.
                    // Previously, this ran unconditionally, causing the keyboard to vanish/block
                    // on the Beam Pro every time the screen turned on.
                    if (prefs.prefBlockSoftKeyboard) {
                        triggerAggressiveBlocking()
                    }
                }

                // Universal ADB/External Commands (Suffix Match)
                matches("SOFT_RESTART") -> {
                    Log.d(TAG, "Received SOFT_RESTART command")
                    val targetDisplayId = intent.getIntExtra("DISPLAY_ID", currentDisplayId)
                    handler.post {
                        removeOldViews()
                        handler.postDelayed({
                            setupUI(targetDisplayId)
                            enforceZOrder()
                            showToast("Trackpad Soft Restarted")
                        }, 200)
                    }
                }
                matches("MOVE_TO_VIRTUAL") -> {
                    Log.d(TAG, "Received MOVE_TO_VIRTUAL command")
                    val virtualDisplayId = intent.getIntExtra("DISPLAY_ID", 2)
                    handler.post { moveToVirtualDisplayAndEnableMirror(virtualDisplayId) }
                }
                matches("RETURN_TO_PHYSICAL") -> {
                    Log.d(TAG, "Received RETURN_TO_PHYSICAL command")
                    val physicalDisplayId = intent.getIntExtra("DISPLAY_ID", 0)
                    handler.post { returnToPhysicalDisplay(physicalDisplayId) }
                }
                matches("ENFORCE_ZORDER") -> {
                    Log.d(TAG, "Received ENFORCE_ZORDER command")
                    handler.post { enforceZOrder() }
                }
                matches("TOGGLE_VIRTUAL_MIRROR") -> {
                    Log.d(TAG, "Received TOGGLE_VIRTUAL_MIRROR command")
                    handler.post { toggleVirtualMirrorMode() }
                }
                matches("GET_STATUS") -> {
                    Log.d(TAG, "Received GET_STATUS command")
                    showToast("D:$currentDisplayId T:$inputTargetDisplayId M:${if(prefs.prefVirtualMirrorMode) "ON" else "OFF"}")
                }
            }
        }
    }

    // Helper to dynamically update window flags
    private fun setOverlayFocusable(focusable: Boolean) {
        try {
            keyboardOverlay?.setFocusable(focusable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // NEW FUNCTION: Finds the focused input field and performs a click
    private fun attemptRefocusInput() {
        try {
            // Requires canRetrieveWindowContent="true" in accessibility xml
            val root = rootInActiveWindow ?: return
            
            // Find the node that currently has input focus
            val focus = root.findFocus(android.view.accessibility.AccessibilityNodeInfo.FOCUS_INPUT)
            
            if (focus != null) {
                // Simulate a tap on the text box to refresh the InputConnection
                focus.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK)
                focus.recycle()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // =================================================================================
    // END BLOCK: BroadcastReceiver & Window Focus Logic
    // =================================================================================

    // =================================================================================
    // FUNCTION: checkAndDismissVoice
    // SUMMARY:  Dismisses Voice Input.
    //           FIX 1: Uses performGlobalAction(GLOBAL_ACTION_BACK) for reliable closing.
    //           FIX 2: Adds a small delay to ensure the action registers.
    // =================================================================================
    // =================================================================================
    // FUNCTION: checkAndDismissVoice
    // SUMMARY:  Called when user touches Trackpad/Keyboard.
    //           Turns the indicator OFF (Red) and Resets Flag.
    //           Injects BACK key to close Google Voice.
    // =================================================================================
    private fun checkAndDismissVoice() {
        if (isVoiceActive) {
            isVoiceActive = false 
            
            // IMMEDIATE UI UPDATE: Turn Mic Off
            keyboardOverlay?.setVoiceActive(false)
            
            // Standard Dismissal Logic (Back Button)
            Thread {
                try {
                    val success = performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                    if (!success) {
                        injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN)
                        Thread.sleep(50)
                        injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP)
                    }
                    if (prefs.prefBlockSoftKeyboard) {
                        triggerAggressiveBlocking()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    companion object {
        private const val TAG = "OverlayService"
        private const val BASE_SWIPE_DISTANCE = 200f
    }
    
    fun vibrate() { 
        if (!prefs.prefVibrate) return
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)) 
        else @Suppress("DEPRECATION") v.vibrate(50) 
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            shellService = IShellService.Stub.asInterface(binder)
            isBound = true
            updateBubbleStatus()
            showToast("Shizuku Connected") 
            initCustomKeyboard()
            
            // CRITICAL FIX: Only apply blocking if EXPLICITLY enabled. 
            // Do NOT reset to "1" here, as that unblocks the keyboard every time the app opens.
            if (prefs.prefBlockSoftKeyboard) {
                triggerAggressiveBlocking()
                handler.post(blockingHeartbeat)
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            shellService = null
            isBound = false
            updateBubbleStatus()
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Register BOTH new and old prefixes to support all scripts/buttons
        val commandFilter = IntentFilter().apply {
            val cmds = listOf("SOFT_RESTART", "ENFORCE_ZORDER", "MOVE_TO_DISPLAY", "TOGGLE_MIRROR", "TOGGLE_VIRTUAL_MIRROR", "OPEN_DRAWER", "STOP_SERVICE")
            val prefixes = listOf("com.katsuyamaki.DroidOSTrackpadKeyboard.", "com.example.coverscreentester.")
            
            for (p in prefixes) {
                for (c in cmds) {
                    addAction("$p$c")
                }
            }
        }
        // Export receiver so ADB and other apps can send these commands
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(commandReceiver, commandFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(commandReceiver, commandFilter)
        }
        try { displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; displayManager?.registerDisplayListener(this, handler) } catch (e: Exception) {}
        // =================================================================================
        // VIRTUAL DISPLAY KEEP-ALIVE: Initialize PowerManager
        // =================================================================================
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        // =================================================================================
        // END BLOCK: VIRTUAL DISPLAY KEEP-ALIVE PowerManager Init
        // =================================================================================

                    loadPrefs()
                    val filter = IntentFilter().apply { 
                        // Internal short commands
                        addAction("SWITCH_DISPLAY")
                        addAction("CYCLE_INPUT_TARGET")
                        addAction("RESET_CURSOR")
                        addAction("TOGGLE_DEBUG")
                        addAction("FORCE_KEYBOARD")
                        addAction("TOGGLE_CUSTOM_KEYBOARD")
                        addAction("SET_TRACKPAD_VISIBILITY")
                        addAction("SET_PREVIEW_MODE") 
                        addAction("OPEN_MENU")
                        addAction("VOICE_TYPE_TRIGGERED")
                        addAction(Intent.ACTION_SCREEN_ON)

                        // External commands (Old and New Prefixes)
                        val actions = listOf(
                            "SOFT_RESTART", "MOVE_TO_VIRTUAL", "RETURN_TO_PHYSICAL",
                            "ENFORCE_ZORDER", "TOGGLE_VIRTUAL_MIRROR", "GET_STATUS"
                        )
                        val prefixes = listOf(
                            "com.katsuyamaki.DroidOSTrackpadKeyboard.",
                            "com.example.coverscreentester."
                        )
                        for (prefix in prefixes) {
                            for (act in actions) {
                                addAction("$prefix$act")
                            }
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // For Android 13 (TIRAMISU) and above, receivers must explicitly specify exported state
                        ContextCompat.registerReceiver(this, switchReceiver, filter, Context.RECEIVER_EXPORTED)
                    } else {
                        ContextCompat.registerReceiver(this, switchReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
                    }
                    
                    if (Build.VERSION.SDK_INT >= 24) {
                        try { softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO } catch(e: Exception){}        }
    }



    // This is the Accessibility Service entry point
// =================================================================================
    // FUNCTION: onServiceConnected
    // SUMMARY: AccessibilityService entry point - called when user enables service.
    //          Initializes managers, loads dictionary, registers receivers, and builds UI.
    // =================================================================================
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility Service Connected")
        isAccessibilityReady = true

        // =================================================================================
        // CRITICAL: Initialize PredictionEngine Dictionary
        // SUMMARY: Load the dictionary at service start. Without this, swipe typing fails
        //          because decodeSwipe() has no words to match against.
        // =================================================================================
        PredictionEngine.instance.loadDictionary(this)
        // =================================================================================
        // END BLOCK: Initialize PredictionEngine Dictionary
        // =================================================================================

        // Initialize Managers
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        
        // [FIX] Correct variable names for OverlayService:
        // Use 'this' (because OverlayService implements DisplayListener)
        // Use 'handler' (not uiHandler)
        displayManager?.registerDisplayListener(this, handler)

        // Register receivers
        val filter = IntentFilter().apply {
            addAction("com.katsuyamaki.DroidOSLauncher.OPEN_DRAWER")
            addAction("com.katsuyamaki.DroidOSLauncher.UPDATE_ICON")
            addAction("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(commandReceiver, filter, Context.RECEIVER_EXPORTED) else registerReceiver(commandReceiver, filter)

        // [FIX] Removed incompatible Shizuku listeners that belong to Launcher
        
        // [FIX] USE RETRY LOGIC HERE
        checkAndBindShizuku()

        // Load preferences
        loadPrefs() 
        
        // [FIX] READ TARGET DISPLAY (Fixes "Wrong Display" on Race Condition)
        val globalTarget = try {
            android.provider.Settings.Global.getInt(contentResolver, "droidos_target_display", -1)
        } catch (e: Exception) { -1 }

        val finalTarget = if (globalTarget != -1) globalTarget else currentDisplayId
        
        Log.i(TAG, "Startup: Launching UI on Display $finalTarget (Global: $globalTarget)")
        
        // Build UI
        setupUI(finalTarget)
        
        // [FIX] Ensure bubble icon status is updated
        updateBubbleStatus()

        // Clear pending states
        pendingDisplayId = -1

        showToast("Trackpad Ready")
    }




   override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try { createNotification() } catch(e: Exception){ e.printStackTrace() }
        
        // === DEBUG LOGGING START ===
        if (intent != null) {
            val dId = intent.getIntExtra("displayId", -999)
            val action = intent.action
            val force = intent.getBooleanExtra("force_start", false)
            Log.w(TAG, ">>> SERVICE STARTED | Action: $action | DisplayID: $dId | Force: $force <<<")
            
            if (dId != -999) {
                handler.post { Toast.makeText(this, "Service Started on D:$dId", Toast.LENGTH_SHORT).show() }
            }
        } else {
            Log.e(TAG, ">>> SERVICE STARTED | INTENT IS NULL <<<")
        }
        // === DEBUG LOGGING END ===

        try {
            checkAndBindShizuku()

            // [FIX] Combined Startup Logic
            if (intent != null) {
                val dId = intent.getIntExtra("displayId", -1)
                
                if (dId != -1) {
                    if (isAccessibilityReady) {
                        // Safe to launch immediately
                        Log.d(TAG, "onStartCommand: Ready -> Setup UI Display $dId")
                        setupUI(dId)
                    } else {
                        // Too early! Queue it for onServiceConnected
                        Log.d(TAG, "onStartCommand: Not Ready -> Queueing Display $dId")
                        pendingDisplayId = dId
                    }
                    return START_STICKY
                }
            }

            // --- Original Logic for other intents/conditions ---
            // If no explicit displayId was passed, or if the AccessibilityService is not yet ready
            // for the initial setup, we fall back to the existing logic.
            val action = intent?.action
            fun matches(suffix: String): Boolean = action?.endsWith(suffix) == true
            
            // Handle commands robustly (keeping original intent handling)
            if (action != null) {
                when {
                    // Standard Actions
                    action == "SWITCH_DISPLAY" -> switchDisplay()
                    action == "RESET_POSITION" -> {
                        val target = intent.getStringExtra("TARGET") ?: "TRACKPAD"
                        if (target == "KEYBOARD") keyboardOverlay?.resetPosition() else resetTrackpadPosition()
                    }
                    action == "ROTATE" -> {
                        val target = intent.getStringExtra("TARGET") ?: "TRACKPAD"
                        if (target == "KEYBOARD") keyboardOverlay?.cycleRotation() else performRotation()
                    }
                    action == "SAVE_LAYOUT" -> saveLayout()
                    action == "LOAD_LAYOUT" -> loadLayout()
                    action == "DELETE_PROFILE" -> deleteCurrentProfile()
                    action == "MANUAL_ADJUST" -> handleManualAdjust(intent)
                    action == "RELOAD_PREFS" -> {
                        loadPrefs()
                        updateBorderColor(currentBorderColor)
                        updateLayoutSizes()
                        updateScrollPosition()
                        updateCursorSize()
                        keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha)
                        if (isCustomKeyboardVisible) { toggleCustomKeyboard(); toggleCustomKeyboard() }
                    }
                    action == "PREVIEW_UPDATE" -> handlePreview(intent)
                    action == "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                    action == "RESET_CURSOR" -> resetCursorCenter()
                    action == "TOGGLE_DEBUG" -> toggleDebugMode()
                    action == "FORCE_KEYBOARD" || action == "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                    action == "OPEN_MENU" -> menuManager?.show()
                    
                    // ADB / Launcher Commands (Suffix Matching)
                    matches("SOFT_RESTART") -> {
                        Log.d(TAG, "onStartCommand: SOFT_RESTART -> Delegating")
                        performSoftRestart()
                    }
                    matches("MOVE_TO_VIRTUAL") -> {
                        val vid = intent.getIntExtra("DISPLAY_ID", 2)
                        handler.post { moveToVirtualDisplayAndEnableMirror(vid) }
                    }
                    matches("RETURN_TO_PHYSICAL") -> {
                        val pid = intent.getIntExtra("DISPLAY_ID", 0)
                        handler.post { returnToPhysicalDisplay(pid) }
                    }
                    matches("ENFORCE_ZORDER") -> handler.post { enforceZOrder() }
                    matches("TOGGLE_VIRTUAL_MIRROR") -> handler.post { toggleVirtualMirrorMode() }
                    matches("GET_STATUS") -> showToast("Status: D=$currentDisplayId M=${prefs.prefVirtualMirrorMode}")
                }
            }

            if (intent?.hasExtra("DISPLAY_ID") == true) {
                val targetId = intent.getIntExtra("DISPLAY_ID", Display.DEFAULT_DISPLAY)
                val force = intent.getBooleanExtra("FORCE_MOVE", false)
                if (targetId >= 0 && (targetId != currentDisplayId || force)) {
                    forceMoveToDisplay(targetId)
                }
            } else if (windowManager == null) {
                setupUI(Display.DEFAULT_DISPLAY)
            }
            
            // [CRITICAL] Ensure UI is created if missing
            if (windowManager == null || bubbleView == null) {
                setupUI(Display.DEFAULT_DISPLAY)
            }

        } catch (e: Exception) {
            Log.e(TAG, "CRASH during onStartCommand", e)
            // Retry setup safely
            try { setupUI(Display.DEFAULT_DISPLAY) } catch(e2: Exception) {}
        }
        return START_STICKY
    }

    override fun onInterrupt() {}

    // Use dispatchKeyEvent to catch BOTH Down and Up events in one place
    override fun onKeyEvent(event: KeyEvent): Boolean {
        val isVolKey = event.keyCode == KeyEvent.KEYCODE_VOLUME_UP || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        
        if (isPreviewMode || (!isTrackpadVisible && !isVolKey)) {
            return super.onKeyEvent(event)
        }
        
        val action = event.action
        val keyCode = event.keyCode
        
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (prefs.hardkeyVolUpTap == "none" && prefs.hardkeyVolUpHold == "none") return super.onKeyEvent(event)
            
            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    if (!isLeftKeyHeld) {
                        isLeftKeyHeld = true
                        volUpHoldTriggered = false
                        handler.postDelayed(volUpHoldRunnable, prefs.holdDurationMs.toLong())
                    }
                }
                KeyEvent.ACTION_UP -> {
                    isLeftKeyHeld = false
                    handler.removeCallbacks(volUpHoldRunnable)
                    if (volUpHoldTriggered) {
                        // RELEASE the hold action (Crucial for drag/click release)
                        executeHardkeyAction(prefs.hardkeyVolUpHold, KeyEvent.ACTION_UP)
                    } else {
                        val timeSinceLastTap = System.currentTimeMillis() - lastVolUpTime
                        lastVolUpTime = System.currentTimeMillis()
                        if (timeSinceLastTap < prefs.doubleTapMs && volUpTapCount == 1) {
                            handler.removeCallbacks(volUpDoubleTapRunnable)
                            volUpTapCount = 0
                            executeHardkeyAction(prefs.hardkeyVolUpDouble, KeyEvent.ACTION_UP)
                        } else {
                            volUpTapCount = 1
                            handler.removeCallbacks(volUpDoubleTapRunnable)
                            handler.postDelayed(volUpDoubleTapRunnable, prefs.doubleTapMs.toLong())
                        }
                    }
                }
            }
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (prefs.hardkeyVolDownTap == "none" && prefs.hardkeyVolDownHold == "none") return super.onKeyEvent(event)
            
            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    if (!isRightKeyHeld) {
                        isRightKeyHeld = true
                        volDownHoldTriggered = false
                        handler.postDelayed(volDownHoldRunnable, prefs.holdDurationMs.toLong())
                    }
                }
                KeyEvent.ACTION_UP -> {
                    isRightKeyHeld = false
                    handler.removeCallbacks(volDownHoldRunnable)
                    if (volDownHoldTriggered) {
                        // RELEASE the hold action
                        executeHardkeyAction(prefs.hardkeyVolDownHold, KeyEvent.ACTION_UP)
                    } else {
                        val timeSinceLastTap = System.currentTimeMillis() - lastVolDownTime
                        lastVolDownTime = System.currentTimeMillis()
                        if (timeSinceLastTap < prefs.doubleTapMs && volDownTapCount == 1) {
                            handler.removeCallbacks(volDownDoubleTapRunnable)
                            volDownTapCount = 0
                            executeHardkeyAction(prefs.hardkeyVolDownDouble, KeyEvent.ACTION_UP)
                        } else {
                            volDownTapCount = 1
                            handler.removeCallbacks(volDownDoubleTapRunnable)
                            handler.postDelayed(volDownDoubleTapRunnable, prefs.doubleTapMs.toLong())
                        }
                    }
                }
            }
            return true
        }
        return super.onKeyEvent(event)
    }

    private fun removeOldViews() {
        val viewsToRemove = listOf(trackpadLayout, bubbleView, cursorLayout)
        for (view in viewsToRemove) {
            if (view != null && view.parent != null && windowManager != null) {
                try {
                    windowManager?.removeViewImmediate(view)
                    android.util.Log.d("OverlayService", "Successfully removed view: ${view.javaClass.simpleName}")
                } catch (e: Exception) {
                    android.util.Log.e("OverlayService", "Failed to remove view immediate", e)
                }
            }
        }
        // Clean up keyboard and menu
        try {
            keyboardOverlay?.hide()
            keyboardOverlay = null
            menuManager?.hide()
            menuManager = null
        } catch (e: Exception) {
            android.util.Log.e("OverlayService", "Failed to cleanup keyboard/menu", e)
        }
        // Nullify references to ensure setup functions create fresh instances
        trackpadLayout = null
        bubbleView = null
        cursorLayout = null
        cursorView = null
    }

    private fun setupUI(displayId: Int) {
        android.util.Log.d("OverlayService", "setupUI starting for Display $displayId")

        // 1. Force complete removal of all views using the current WindowManager
        removeOldViews()

        val display = displayManager?.getDisplay(displayId)
        if (display == null) {
            showToast("Error: Display $displayId not found")
            return
        }

        try {
            // 2. Create a new context strictly for the target physical display
            val displayContext = createDisplayContext(display)
            val accessContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 displayContext.createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null)
            } else displayContext

            // 3. RE-BIND WindowManager and Inflater to the new display context
            windowManager = accessContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            appWindowManager = windowManager

            currentDisplayId = displayId
            inputTargetDisplayId = displayId

            updateUiMetrics()

            // 4. Rebuild the UI components
            setupTrackpad(accessContext)
            if (shellService != null) initCustomKeyboard()
            menuManager = TrackpadMenuManager(displayContext, windowManager!!, this)
            setupBubble(accessContext)
            setupCursor(accessContext)

            enforceZOrder()
            showToast("Trackpad active on Display $displayId")

            if (prefs.prefBlockSoftKeyboard) triggerAggressiveBlocking()

            android.util.Log.d("OverlayService", "setupUI completed successfully on Display $displayId")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup UI on display $displayId", e)
            showToast("Failed to launch on display $displayId")
        }
    }

    private fun setupBubble(context: Context) {
        bubbleView = LayoutInflater.from(context).inflate(R.layout.layout_trackpad_bubble, null)
        bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or 
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
             bubbleParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        bubbleParams.gravity = Gravity.TOP or Gravity.START
        bubbleParams.x = prefs.prefBubbleX
        bubbleParams.y = prefs.prefBubbleY
        
        var initialX = 0; var initialY = 0; var initialTouchX = 0f; var initialTouchY = 0f; var isDrag = false
        var isLongPressHandled = false
        val bubbleLongPressRunnable = Runnable {
            if (!isDrag) {
                vibrate()
                menuManager?.toggle()
                isLongPressHandled = true
                handler.post { enforceZOrder() }
            }
        }

        bubbleView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> { 
                    initialX = bubbleParams.x; initialY = bubbleParams.y
                    initialTouchX = event.rawX; initialTouchY = event.rawY
                    isDrag = false
                    isLongPressHandled = false
                    handler.postDelayed(bubbleLongPressRunnable, 600)
                    true 
                }
                MotionEvent.ACTION_MOVE -> { 
                    if (abs(event.rawX - initialTouchX) > 10 || abs(event.rawY - initialTouchY) > 10) { 
                        isDrag = true
                        handler.removeCallbacks(bubbleLongPressRunnable)
                        bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(bubbleView, bubbleParams) 
                    }
                    true 
                }
                MotionEvent.ACTION_UP -> { 
                    handler.removeCallbacks(bubbleLongPressRunnable)
                    if (!isDrag && !isLongPressHandled) {
                        handleBubbleTap()
                    } else if (isDrag) {
                        prefs.prefBubbleX = bubbleParams.x
                        prefs.prefBubbleY = bubbleParams.y
                        savePrefs()
                    }
                    handler.post { enforceZOrder() }
                    true 
                }
                else -> false
            }
        }
        windowManager?.addView(bubbleView, bubbleParams); updateBubbleStatus()
        applyBubbleAppearance()
    }
    
    private fun setupTrackpad(context: Context) {
        trackpadLayout = FrameLayout(context)
        val bg = GradientDrawable(); bg.cornerRadius = 30f; bg.setColor(Color.TRANSPARENT); trackpadLayout?.background = bg
        val handleColor = 0x15FFFFFF.toInt(); handleContainers.clear(); handleVisuals.clear()
        addHandle(context, Gravity.TOP or Gravity.RIGHT, handleColor) { _, e -> moveWindow(e) }
        addHandle(context, Gravity.BOTTOM or Gravity.RIGHT, handleColor) { _, e -> resizeWindow(e) }
        addHandle(context, Gravity.TOP or Gravity.LEFT, handleColor) { _, e -> keyboardHandle(e) }
        addHandle(context, Gravity.BOTTOM or Gravity.LEFT, handleColor) { _, e -> openMenuHandle(e) }
        addScrollBars(context)
        debugTextView = TextView(context); debugTextView?.text = "DEBUG"; debugTextView?.setTextColor(Color.YELLOW); debugTextView?.setBackgroundColor(0xAA000000.toInt()); debugTextView?.textSize = 9f; debugTextView?.visibility = View.GONE; val debugParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT); debugParams.gravity = Gravity.CENTER; trackpadLayout?.addView(debugTextView, debugParams)
        
        trackpadParams = WindowManager.LayoutParams(
            400, 300, 
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, 
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or 
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or 
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, 
            PixelFormat.TRANSLUCENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
             trackpadParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        trackpadParams.gravity = Gravity.TOP or Gravity.LEFT; loadLayout()
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() { 
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean { return false }
        })
        
        trackpadLayout?.setOnTouchListener { _, event -> val devId = event.deviceId; val tool = event.getToolType(0); if (tool != MotionEvent.TOOL_TYPE_FINGER) return@setOnTouchListener false; when (event.actionMasked) { MotionEvent.ACTION_DOWN -> activeFingerDeviceId = devId; MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> { if (activeFingerDeviceId > 0 && devId != activeFingerDeviceId) return@setOnTouchListener false } }; gestureDetector.onTouchEvent(event); handleTrackpadTouch(event); true }
        trackpadLayout?.visibility = View.GONE
        windowManager?.addView(trackpadLayout, trackpadParams)
        updateBorderColor(currentBorderColor)
    }
    
    private fun setupCursor(context: Context) {
        cursorLayout = FrameLayout(context); cursorView = ImageView(context); cursorView?.setImageResource(R.drawable.ic_cursor); val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; cursorLayout?.addView(cursorView, FrameLayout.LayoutParams(size, size))
        cursorParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, 
            WindowManager.LayoutParams.WRAP_CONTENT, 
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, 
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or 
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or 
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
             cursorParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        cursorParams.gravity = Gravity.TOP or Gravity.LEFT; cursorParams.x = uiScreenWidth / 2; cursorParams.y = uiScreenHeight / 2; windowManager?.addView(cursorLayout, cursorParams)
    }

    fun toggleTrackpad() { 
        isTrackpadVisible = !isTrackpadVisible
        
        // Toggle Visibility
        trackpadLayout?.visibility = if (isTrackpadVisible) View.VISIBLE else View.GONE
        
        // Update Border if showing
        if (isTrackpadVisible) {
            updateBorderColor(currentBorderColor) 
        }
        
        // PREVIOUSLY: else if (isCustomKeyboardVisible) toggleCustomKeyboard(...)
        // We removed that line so the keyboard stays open.
    }
    
    // NEW FUNCTION: Toggles the visibility of the trackpad menu drawer
    private fun toggleDrawer() {
        menuManager?.toggle()
        enforceZOrder() // Ensure drawer is on top
    }
    
    private fun handleBubbleTap() {
        val anythingVisible = isTrackpadVisible || isCustomKeyboardVisible
        if (anythingVisible) {
            performSmartHide()
        } else {
            performSmartRestore()
        }
    }
    
    private fun executeHardkeyAction(actionId: String, keyEventAction: Int = KeyEvent.ACTION_UP) {
        val isUp = (keyEventAction == KeyEvent.ACTION_UP)
        when (actionId) {
            "none" -> { }
            "left_click" -> {
                if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volUpDragActive) { volUpDragActive = true; startKeyDrag(MotionEvent.BUTTON_PRIMARY) }
                } else {
                    if (volUpDragActive) { volUpDragActive = false; stopKeyDrag(MotionEvent.BUTTON_PRIMARY) } 
                    else performClick(false)
                }
            }
            "right_click" -> {
                 if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volDownDragActive) { volDownDragActive = true; startKeyDrag(MotionEvent.BUTTON_SECONDARY) }
                } else {
                    if (volDownDragActive) { volDownDragActive = false; stopKeyDrag(MotionEvent.BUTTON_SECONDARY) } 
                    else performClick(true)
                }
            }
            "action_back" -> if (isUp) Thread { try { injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN); Thread.sleep(20); injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP) } catch(e: Exception){} }.start()
            "action_home" -> if (isUp) Thread { try { injectKey(KeyEvent.KEYCODE_HOME, KeyEvent.ACTION_DOWN); Thread.sleep(20); injectKey(KeyEvent.KEYCODE_HOME, KeyEvent.ACTION_UP) } catch(e: Exception){} }.start()
            "action_forward" -> if (isUp) Thread { try { injectKey(KeyEvent.KEYCODE_FORWARD, KeyEvent.ACTION_DOWN); Thread.sleep(20); injectKey(KeyEvent.KEYCODE_FORWARD, KeyEvent.ACTION_UP) } catch(e: Exception){} }.start()
            "action_vol_up" -> if (isUp) Thread { try { injectKey(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.ACTION_DOWN); Thread.sleep(20); injectKey(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.ACTION_UP) } catch(e: Exception){} }.start()
            "action_vol_down" -> if (isUp) Thread { try { injectKey(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.ACTION_DOWN); Thread.sleep(20); injectKey(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.ACTION_UP) } catch(e: Exception){} }.start()
            "scroll_up" -> if (isUp) performSwipe(0f, -(BASE_SWIPE_DISTANCE * prefs.scrollSpeed))
            "scroll_down" -> if (isUp) performSwipe(0f, BASE_SWIPE_DISTANCE * prefs.scrollSpeed)
            "display_toggle" -> if (isUp) {
                if (prefs.displayOffMode == "standard") {
                    isScreenOff = !isScreenOff
                    Thread { try { if (isScreenOff) shellService?.setScreenOff(0, true) else shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start()
                    showToast("Display ${if(isScreenOff) "Off" else "On"} (Std)")
                } else {
                    isScreenOff = !isScreenOff
                    Thread { try { if (isScreenOff) shellService?.setBrightness(-1) else shellService?.setBrightness(128) } catch (e: Exception) {} }.start()
                    showToast("Display ${if(isScreenOff) "Off" else "On"} (Alt)")
                }
            }
            "display_toggle_alt" -> if (isUp) {
                isScreenOff = !isScreenOff
                Thread { try { if (isScreenOff) shellService?.setBrightness(-1) else shellService?.setBrightness(128) } catch (e: Exception) {} }.start()
                showToast("Display ${if(isScreenOff) "Off" else "On"} (Alt)")
            }
            "display_toggle_std" -> if (isUp) {
                isScreenOff = !isScreenOff
                Thread { try { if (isScreenOff) shellService?.setScreenOff(0, true) else shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start()
                showToast("Display ${if(isScreenOff) "Off" else "On"} (Std)")
            }
            "alt_position" -> if (isUp) toggleKeyboardMode()
            "toggle_keyboard" -> if (isUp) toggleCustomKeyboard()
            "toggle_trackpad" -> if (isUp) toggleTrackpad()
            "open_menu" -> if (isUp) menuManager?.toggle()
            "reset_cursor" -> if (isUp) resetCursorCenter()
            "display_wake" -> if (isUp && isScreenOff) { isScreenOff = false; Thread { try { shellService?.setBrightness(128); shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start(); showToast("Display Woken") }
            
            // "Launcher Bubble" Keybind Action - Force Toggle/Swap
            "toggle_bubble" -> if (isUp) {
                // Simply toggle between 0 and 1. 
                // If we are on 1, go to 0. If on 0, go to 1.
                // This guarantees movement if the user presses it.
                val targetId = if (currentDisplayId == 0) 1 else 0
                
                try {
                    showToast("Force Switch to $targetId")
                    setupUI(targetId)
                    resetBubblePosition()
                    menuManager?.show()
                    enforceZOrder()
                } catch (e: Exception) {
                    showToast("Error: ${e.message}")
                }
            }
        }
    }

    fun toggleKeyboardMode() { vibrate(); if (!isKeyboardMode) { isKeyboardMode = true; savedWindowX = trackpadParams.x; savedWindowY = trackpadParams.y; trackpadParams.x = uiScreenWidth - trackpadParams.width; trackpadParams.y = 0; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(0xFFFF0000.toInt()) } else { isKeyboardMode = false; trackpadParams.x = savedWindowX; trackpadParams.y = savedWindowY; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(currentBorderColor) } }
    
    // [FIX] Public so MenuManager can call it
    fun toggleDebugMode() { 
        isDebugMode = !isDebugMode 
        if (isDebugMode) { 
            showToast("Debug ON")
            updateBorderColor(0xFFFFFF00.toInt())
            debugTextView?.visibility = View.VISIBLE 
        } else { 
            showToast("Debug OFF")
            if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt())
            debugTextView?.visibility = View.GONE 
        } 
    }

    fun updateBubbleStatus() { val dot = bubbleView?.findViewById<ImageView>(R.id.status_dot); if (shellService != null) dot?.visibility = View.GONE else dot?.visibility = View.VISIBLE }

    private val bubbleIcons = arrayOf(R.mipmap.ic_trackpad_adaptive, R.drawable.ic_cursor, R.drawable.ic_tab_main, R.drawable.ic_tab_keyboard, android.R.drawable.ic_menu_compass, android.R.drawable.ic_menu_myplaces)
    
    fun updateBubbleSize(sizePercent: Int) { prefs.prefBubbleSize = sizePercent.coerceIn(50, 200); applyBubbleAppearance(); savePrefs() }
    fun updateBubbleIcon(index: Int) { prefs.prefBubbleIconIndex = index.coerceIn(0, bubbleIcons.size - 1); applyBubbleAppearance(); savePrefs() }
    fun cycleBubbleIcon() { updateBubbleIcon((prefs.prefBubbleIconIndex + 1) % bubbleIcons.size) }
    fun updateBubbleAlpha(alpha: Int) { prefs.prefBubbleAlpha = alpha.coerceIn(50, 255); applyBubbleAppearance(); savePrefs() }
    
    private fun applyBubbleAppearance() {
        if (bubbleView == null) return
        val scale = prefs.prefBubbleSize / 100f
        val density = resources.displayMetrics.density
        bubbleParams.width = (60 * scale * density).toInt()
        bubbleParams.height = (60 * scale * density).toInt()
        try { windowManager?.updateViewLayout(bubbleView, bubbleParams) } catch (e: Exception) {}
        val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon)
        iconView?.let {
            val iconParams = it.layoutParams as? FrameLayout.LayoutParams
            iconParams?.gravity = Gravity.CENTER
            iconParams?.width = (40 * scale * density).toInt()
            iconParams?.height = (40 * scale * density).toInt()
            it.layoutParams = iconParams
            it.setImageResource(bubbleIcons.getOrElse(prefs.prefBubbleIconIndex) { bubbleIcons[0] })
            it.alpha = prefs.prefBubbleAlpha / 255f
        }
        bubbleView?.alpha = prefs.prefBubbleAlpha / 255f
    }

    fun forceMoveToCurrentDisplay() { setupUI(currentDisplayId) }
    fun forceMoveToDisplay(displayId: Int) { if (displayId == currentDisplayId) return; setupUI(displayId) }
    fun hideApp() { menuManager?.hide(); if (isTrackpadVisible) toggleTrackpad() }


    // =================================================================================
    // INTER-APP COMMUNICATION HELPER FUNCTIONS
    // SUMMARY: Functions to support commands from DroidOS Launcher or ADB.
    //          These enable coordinated display switching and z-order management.
    // =================================================================================
    
    /**
     * Moves overlay to virtual display and enables Virtual Mirror Mode.
     * Called by MOVE_TO_VIRTUAL broadcast from Launcher or ADB.
     */
    private fun moveToVirtualDisplayAndEnableMirror(virtualDisplayId: Int) {
        try {
            Log.d(TAG, "Moving to virtual display $virtualDisplayId and enabling mirror mode")
            
            // Store current state for potential return
            preMirrorTrackpadVisible = isTrackpadVisible
            preMirrorKeyboardVisible = isCustomKeyboardVisible
            preMirrorTargetDisplayId = inputTargetDisplayId
            
            // Move UI to virtual display
            setupUI(virtualDisplayId)
            
            // Enable Virtual Mirror Mode
            if (!prefs.prefVirtualMirrorMode) {
                prefs.prefVirtualMirrorMode = true
                savePrefs()
            }
            
            // Set input target to virtual display
            inputTargetDisplayId = virtualDisplayId
            updateTargetMetrics(virtualDisplayId)
            
            // Show trackpad and keyboard
            if (!isTrackpadVisible) toggleTrackpad()
            if (!isCustomKeyboardVisible) toggleCustomKeyboard()
            
            // Create mirror keyboard on virtual display
            createMirrorKeyboard(virtualDisplayId)
            createRemoteCursor(virtualDisplayId)
            
            // Update visual indicators
            updateBorderColor(0xFFFF00FF.toInt()) // Purple for remote mode
            updateWakeLockState()
            
            showToast("Virtual Display Mode Active")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to move to virtual display", e)
            showToast("Error: ${e.message}")
        }
    }
    
    /**
     * Returns overlay to physical display and disables Virtual Mirror Mode.
     * Called by RETURN_TO_PHYSICAL broadcast from Launcher or ADB.
     */
    private fun returnToPhysicalDisplay(physicalDisplayId: Int) {
        try {
            Log.d(TAG, "Returning to physical display $physicalDisplayId")
            
            // Disable Virtual Mirror Mode
            if (prefs.prefVirtualMirrorMode) {
                prefs.prefVirtualMirrorMode = false
                savePrefs()
            }
            
            // Remove remote cursor and mirror keyboard
            removeRemoteCursor()
            removeMirrorKeyboard()
            
            // Move UI to physical display
            setupUI(physicalDisplayId)
            
            // Reset input target to local
            inputTargetDisplayId = physicalDisplayId
            cursorX = uiScreenWidth / 2f
            cursorY = uiScreenHeight / 2f
            cursorParams.x = cursorX.toInt()
            cursorParams.y = cursorY.toInt()
            try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception){}
            cursorView?.visibility = View.VISIBLE
            
            // Update visual indicators
            updateBorderColor(0x55FFFFFF.toInt()) // Default for local mode
            releaseDisplayWakeLock()
            
            showToast("Returned to Physical Display")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to return to physical display", e)
            showToast("Error: ${e.message}")
        }
    }
    // =================================================================================
    // END BLOCK: INTER-APP COMMUNICATION HELPER FUNCTIONS
    // =================================================================================

    


    // =================================================================================
    // PROFILE KEY GENERATION
    // SUMMARY: Generates a unique key based on Resolution + Mirror Mode State.
    //          Allows separate profiles for "Standard" and "Mirror" modes.
    // =================================================================================
    fun getProfileKey(): String {
        val mode = if (prefs.prefVirtualMirrorMode) "MIRROR" else "STD"
        return "P_${uiScreenWidth}_${uiScreenHeight}_$mode"
    }

    fun getSavedProfileList(): List<String> {

        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val allKeys = p.all.keys
        val profiles = java.util.HashSet<String>()
        
        // Regex matches: X_P_{width}_{height}_{suffix}
        // Group 1: Width
        // Group 2: Height
        // Group 3: Optional Suffix (MIRROR or STD)
        val regex = Regex("X_P_(\\d+)_(\\d+)(?:_([A-Z]+))?")
        
        for (key in allKeys) { 
            // We only care about X position keys to identify a profile exists
            if (!key.startsWith("X_P_")) continue

            val match = regex.matchEntire(key)
            if (match != null) {
                val w = match.groupValues[1]
                val h = match.groupValues[2]
                val suffix = match.groupValues.getOrNull(3) // Can be null, STD, or MIRROR
                
                var displayLabel = "$w x $h"
                
                // If it is a Mirror Profile, append VM
                if (suffix == "MIRROR") {
                    displayLabel += " VM"
                }
                
                profiles.add(displayLabel)
            }
        }
        return profiles.sorted()
    }








    private fun scheduleRestart() {
        try {
            // Keep the Smart Display Logic (it's safe to keep)
            var restartDisplayId = currentDisplayId
            try {
                val d0 = displayManager?.getDisplay(0)
                if (d0 != null && d0.state == Display.STATE_OFF) {
                     val d1 = displayManager?.getDisplay(1)
                     if (d1 != null && d1.state == Display.STATE_ON) {
                         restartDisplayId = 1
                     }
                }
            } catch(e: Exception) {}

            Log.i(TAG, ">>> SCHEDULING RESTART (SERVICE) | Display: $restartDisplayId <<<")
            
            // [FIX] Target the SERVICE directly, not the Activity.
            // This bypasses the "Background Activity Start" restriction that was blocking the restart.
            val restartIntent = Intent(applicationContext, OverlayService::class.java)
            restartIntent.putExtra("displayId", restartDisplayId)
            restartIntent.putExtra("force_start", true)
            restartIntent.putExtra("IS_RESTART", true)
            
            val flags = if (Build.VERSION.SDK_INT >= 23) 
                android.app.PendingIntent.FLAG_ONE_SHOT or android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
            else 
                android.app.PendingIntent.FLAG_ONE_SHOT or android.app.PendingIntent.FLAG_UPDATE_CURRENT

            // [FIX] Use getForegroundService (or getService) instead of getActivity
            val pendingIntent = if (Build.VERSION.SDK_INT >= 26) {
                android.app.PendingIntent.getForegroundService(applicationContext, 1, restartIntent, flags)
            } else {
                android.app.PendingIntent.getService(applicationContext, 1, restartIntent, flags)
            }
            
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val triggerTime = System.currentTimeMillis() + 800

            try {
                if (Build.VERSION.SDK_INT >= 23) {
                     alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                     alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } catch (e: SecurityException) {
                Log.w(TAG, "Exact Alarm permission missing, using standard alarm")
                alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule restart", e)
        }
    }







    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // [Fixed] If Keep Alive is ON, schedule restart BEFORE system kills us.
        // Many ROMs force-kill services on swipe, so we must register the alarm now.
        if (prefs.prefPersistentService) {
            scheduleRestart()
            // We do NOT call forceExit() here; we let the system decide if it wants to kill us.
            // If it does kill us, the alarm brings us back.
        } else {
            forceExit()
        }
    }

    fun forceExit() {
        Log.i(TAG, "forceExit called")
        try {
            removeOldViews()
            
            // Note: performSoftRestart calls scheduleRestart() explicitly before calling this.
            // But if called from elsewhere (Button), we ensure it schedules if persistent.
            // We duplicate the call here to be safe (AlarmManager overwrites existing pending intents).
            if (prefs.prefPersistentService) {
                scheduleRestart()
            }

            stopSelf()
            // Allow small window for Alarm registration IPC
            Thread.sleep(200) 
            Process.killProcess(Process.myPid())
            System.exit(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // =================================================================================
    // FUNCTION: syncMirrorWithPhysicalKeyboard
    // SUMMARY: Updates mirror keyboard dimensions to match physical keyboard.
    //          Called when physical keyboard is resized/moved.
    // =================================================================================
    fun syncMirrorWithPhysicalKeyboard() {
        if (!prefs.prefVirtualMirrorMode || mirrorKeyboardView == null) return
        
        // Update coordinate scaling variables
        physicalKbWidth = keyboardOverlay?.getViewWidth()?.toFloat() ?: 600f
        physicalKbHeight = keyboardOverlay?.getViewHeight()?.toFloat() ?: 400f
        
        Log.d(TAG, "Mirror sync: Physical KB now ${physicalKbWidth}x${physicalKbHeight}")
        
        // If mirror has custom size, keep it. Otherwise, could auto-scale here.
        // For now, just update the scaling ratios used for touch coordinate mapping.
    }
    // =================================================================================
    // END BLOCK: syncMirrorWithPhysicalKeyboard
    // =================================================================================
    
    fun manualAdjust(isKeyboard: Boolean, isResize: Boolean, dx: Int, dy: Int) { 
        if (isKeyboard) { 
            if (isResize) keyboardOverlay?.resizeWindow(dx, dy) else keyboardOverlay?.moveWindow(dx, dy)
            // Sync mirror keyboard with physical keyboard changes
            syncMirrorWithPhysicalKeyboard()
        } 
        else { 
            if (trackpadLayout == null) return
            if (isResize) { trackpadParams.width = max(200, trackpadParams.width + dx); trackpadParams.height = max(200, trackpadParams.height + dy) } 
            { trackpadParams.x += dx; trackpadParams.y += dy }
            try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch (e: Exception) {}; saveLayout() 
        } 
    }
    
    private fun parseBoolean(value: Any): Boolean { return when(value) { is Boolean -> value; is Int -> value == 1; is String -> value == "1" || value.equals("true", ignoreCase = true); else -> false } }
    
    fun updatePref(key: String, value: Any) { 
        when(key) { 
            "cursor_speed" -> prefs.cursorSpeed = (value as? Float) ?: 2.5f
            "scroll_speed" -> prefs.scrollSpeed = (value as? Float) ?: 1.0f
            "tap_scroll" -> prefs.prefTapScroll = parseBoolean(value)
            "vibrate" -> prefs.prefVibrate = parseBoolean(value)
            "reverse_scroll" -> prefs.prefReverseScroll = parseBoolean(value)
            "alpha" -> { prefs.prefAlpha = value as Int; updateBorderColor(currentBorderColor) }
            "bg_alpha" -> { prefs.prefBgAlpha = value as Int; updateBorderColor(currentBorderColor) }
            "handle_size" -> { prefs.prefHandleSize = value as Int; updateHandleSize() }
            "scroll_size" -> { prefs.prefScrollTouchSize = value as Int; updateScrollSize() }
            "scroll_visual" -> { prefs.prefScrollVisualSize = value as Int; updateScrollSize() }
            "cursor_size" -> { prefs.prefCursorSize = value as Int; updateCursorSize() }
"anchored" -> { prefs.prefAnchored = parseBoolean(value); keyboardOverlay?.setAnchored(prefs.prefAnchored) }            "automation_enabled" -> prefs.prefAutomationEnabled = parseBoolean(value)
            "bubble_size" -> updateBubbleSize(value as Int)
            "bubble_icon" -> cycleBubbleIcon()
            "bubble_alpha" -> updateBubbleAlpha(value as Int)
            "persistent_service" -> prefs.prefPersistentService = parseBoolean(value)
            "block_soft_kb" -> { prefs.prefBlockSoftKeyboard = parseBoolean(value); setSoftKeyboardBlocking(prefs.prefBlockSoftKeyboard) }
            "keyboard_alpha" -> { prefs.prefKeyboardAlpha = value as Int; keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha) }

            "hardkey_vol_up_tap" -> prefs.hardkeyVolUpTap = value as String
            "hardkey_vol_up_double" -> prefs.hardkeyVolUpDouble = value as String
            "hardkey_vol_up_hold" -> prefs.hardkeyVolUpHold = value as String
            "hardkey_vol_down_tap" -> prefs.hardkeyVolDownTap = value as String
            "hardkey_vol_down_double" -> prefs.hardkeyVolDownDouble = value as String
            "hardkey_vol_down_hold" -> prefs.hardkeyVolDownHold = value as String
            "double_tap_ms" -> prefs.doubleTapMs = value as Int
            "hold_duration_ms" -> prefs.holdDurationMs = value as Int
            "mirror_alpha" -> {
                val v = value as Int
                prefs.prefMirrorAlpha = v
                // Apply to BOTH container and keyboard view
                val alpha = v / 255f
                mirrorKeyboardContainer?.alpha = alpha
                mirrorKeyboardView?.alpha = alpha
            }
            "mirror_orient_delay" -> {
                prefs.prefMirrorOrientDelayMs = value as Long
            }
            // =================================================================================
            // VIRTUAL MIRROR MODE UPDATE HANDLERS
            // =================================================================================
            "virtual_mirror_mode" -> {
                prefs.prefVirtualMirrorMode = parseBoolean(value)
                updateVirtualMirrorMode()
            }
            "mirror_orient_delay_ms" -> prefs.prefMirrorOrientDelayMs = (value as? Long) ?: 1000L
            // =================================================================================
            // END BLOCK: VIRTUAL MIRROR MODE UPDATE HANDLERS
            // =================================================================================
        }
        savePrefs() 
    }
    
    fun applyLayoutPreset(type: Int) {
        if (type == 0) { loadLayout(); showToast("Freeform Profile Loaded"); return }
        val h = uiScreenHeight; val w = uiScreenWidth; val density = resources.displayMetrics.density
        val targetW = (w * 0.96f).toInt(); val marginX = (w - targetW) / 2
        // Added 20dp buffer to preset height calculation
        val kbHeight = ((275f * (prefs.prefKeyScale / 100f) * density) + (20 * density)).toInt().coerceAtMost((h * 0.6f).toInt())
        val tpHeight = h - kbHeight
        if (keyboardOverlay == null) initCustomKeyboard()
        if (!isCustomKeyboardVisible) toggleCustomKeyboard(suppressAutomation = true)
        if (!isTrackpadVisible) toggleTrackpad()
        prefs.prefScrollTouchSize = 80; prefs.prefScrollVisualSize = 8
        when(type) {
            1 -> { keyboardOverlay?.setWindowBounds(marginX, 0, targetW, kbHeight); trackpadParams.width = targetW; trackpadParams.height = tpHeight; trackpadParams.x = marginX; trackpadParams.y = kbHeight }
            2 -> { trackpadParams.width = targetW; trackpadParams.height = tpHeight; trackpadParams.x = marginX; trackpadParams.y = 0; keyboardOverlay?.setWindowBounds(marginX, tpHeight, targetW, kbHeight) }
        }
        try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(e: Exception){}
        updateScrollSize(); updateScrollPosition(); updateHandleSize(); updateLayoutSizes(); savePrefs(); showToast("Preset Applied")
    }
    
    fun resetBubblePosition() { bubbleParams.x = 50; bubbleParams.y = uiScreenHeight / 2; try { windowManager?.updateViewLayout(bubbleView, bubbleParams) } catch(e: Exception){}; prefs.prefBubbleX = bubbleParams.x; prefs.prefBubbleY = bubbleParams.y; savePrefs(); showToast("Bubble Reset") }

    private fun loadPrefs() { 
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        prefs.cursorSpeed = p.getFloat("cursor_speed", 2.5f)
        prefs.scrollSpeed = p.getFloat("scroll_speed", 0.6f) // CHANGED: Default 0.6f (Slider 6)
        prefs.prefTapScroll = p.getBoolean("tap_scroll", true)
        prefs.prefVibrate = p.getBoolean("vibrate", true)
        prefs.prefReverseScroll = p.getBoolean("reverse_scroll", false) // CHANGED: Default false
        prefs.prefAlpha = p.getInt("alpha", 50) // CHANGED: Default 50
        prefs.prefBgAlpha = p.getInt("bg_alpha", 220) // CHANGED: Default 220
        prefs.prefKeyboardAlpha = p.getInt("keyboard_alpha", 255) // CHANGED: Default 255
        prefs.prefHandleSize = p.getInt("handle_size", 14) // CHANGED: Default 14
        prefs.prefVPosLeft = p.getBoolean("v_pos_left", false)
        prefs.prefHPosTop = p.getBoolean("h_pos_top", false)
        prefs.prefAnchored = p.getBoolean("anchored", false)
        prefs.prefHandleTouchSize = p.getInt("handle_touch_size", 80)
        prefs.prefScrollTouchSize = p.getInt("scroll_touch_size", 80)
        prefs.prefScrollVisualSize = p.getInt("scroll_visual_size", 4)
                prefs.prefCursorSize = p.getInt("cursor_size", 50)
                prefs.prefKeyScale = p.getInt("keyboard_key_scale", 69) // Default 69 to match resetPosition
                prefs.prefUseAltScreenOff = p.getBoolean("use_alt_screen_off", true)
                prefs.prefAutomationEnabled = p.getBoolean("automation_enabled", false) 
                prefs.prefBubbleX = p.getInt("bubble_x", 50)
                prefs.prefBubbleY = p.getInt("bubble_y", 300)
                prefs.prefBubbleSize = p.getInt("bubble_size", 100)
                prefs.prefBubbleIconIndex = p.getInt("bubble_icon_index", 0)
                prefs.prefBubbleAlpha = p.getInt("bubble_alpha", 255)
                prefs.prefPersistentService = p.getBoolean("persistent_service", false)
                prefs.prefBlockSoftKeyboard = p.getBoolean("block_soft_kb", false)
                // Hardkey Defaults: Set to "none"
                prefs.hardkeyVolUpTap = p.getString("hardkey_vol_up_tap", "none") ?: "none"
                prefs.hardkeyVolUpDouble = p.getString("hardkey_vol_up_double", "none") ?: "none"
                prefs.hardkeyVolUpHold = p.getString("hardkey_vol_up_hold", "none") ?: "none"
                prefs.hardkeyVolDownTap = p.getString("hardkey_vol_down_tap", "none") ?: "none"
                prefs.hardkeyVolDownDouble = p.getString("hardkey_vol_down_double", "none") ?: "none"
                prefs.hardkeyVolDownHold = p.getString("hardkey_vol_down_hold", "none") ?: "none"
        prefs.hardkeyPowerDouble = p.getString("hardkey_power_double", "none") ?: "none"
        
        prefs.doubleTapMs = p.getInt("double_tap_ms", 300)
        prefs.holdDurationMs = p.getInt("hold_duration_ms", 400)

        // =================================================================================
        // VIRTUAL MIRROR MODE LOAD
        // =================================================================================
        // [Fixed] Always FORCE OFF on app restart/reload. Do not load saved state.
        prefs.prefVirtualMirrorMode = false 
        prefs.prefMirrorOrientDelayMs = p.getLong("mirror_orient_delay_ms", 1000L)

        // Load Mirror Keyboard Prefs
        prefs.prefMirrorAlpha = p.getInt("mirror_alpha", 200)
        prefs.prefMirrorX = p.getInt("mirror_x", -1)
        prefs.prefMirrorY = p.getInt("mirror_y", 0)
        prefs.prefMirrorWidth = p.getInt("mirror_width", -1)
        prefs.prefMirrorHeight = p.getInt("mirror_height", -1)
        // Note: No height pref, it's wrap_content
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE LOAD
        // =================================================================================
    }
    


    fun saveLayout() {
        // 1. FETCH LIVE VALUES FROM PHYSICAL KEYBOARD
        val currentKbX = keyboardOverlay?.getViewX() ?: savedKbX
        val currentKbY = keyboardOverlay?.getViewY() ?: savedKbY
        val currentKbW = keyboardOverlay?.getViewWidth() ?: savedKbW
        val currentKbH = keyboardOverlay?.getViewHeight() ?: savedKbH
        
        // Fetch live scale
        val liveScale = keyboardOverlay?.getScale() ?: (prefs.prefKeyScale / 100f)
        prefs.prefKeyScale = (liveScale * 100).toInt()

        savedKbX = currentKbX; savedKbY = currentKbY; savedKbW = currentKbW; savedKbH = currentKbH

        // 2. SAVE TO SHARED PREFS (Using Mode-Specific Key)
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        val key = getProfileKey() // Returns ..._STD or ..._MIRROR
        
        // Save Trackpad Window
        p.putInt("X_$key", trackpadParams.x)
        p.putInt("Y_$key", trackpadParams.y)
        p.putInt("W_$key", trackpadParams.width)
        p.putInt("H_$key", trackpadParams.height)

        // Save Settings String (Standard Settings)
        val settingsStr = StringBuilder()
        settingsStr.append("${prefs.cursorSpeed};${prefs.scrollSpeed};${if(prefs.prefTapScroll) 1 else 0};${if(prefs.prefReverseScroll) 1 else 0};${prefs.prefAlpha};${prefs.prefBgAlpha};${prefs.prefKeyboardAlpha};${prefs.prefHandleSize};${prefs.prefHandleTouchSize};${prefs.prefScrollTouchSize};${prefs.prefScrollVisualSize};${prefs.prefCursorSize};${prefs.prefKeyScale};${if(prefs.prefAutomationEnabled) 1 else 0};${if(prefs.prefAnchored) 1 else 0};${prefs.prefBubbleSize};${prefs.prefBubbleAlpha};${prefs.prefBubbleIconIndex};${prefs.prefBubbleX};${prefs.prefBubbleY};${prefs.hardkeyVolUpTap};${prefs.hardkeyVolUpDouble};${prefs.hardkeyVolUpHold};${prefs.hardkeyVolDownTap};${prefs.hardkeyVolDownDouble};${prefs.hardkeyVolDownHold};${prefs.hardkeyPowerDouble};")
        
        // New Settings (Vibrate, Position)
        settingsStr.append("${if(prefs.prefVibrate) 1 else 0};${if(prefs.prefVPosLeft) 1 else 0};${if(prefs.prefHPosTop) 1 else 0};")

        // Physical Keyboard Bounds
        settingsStr.append("$currentKbX;$currentKbY;$currentKbW;$currentKbH")

        p.putString("SETTINGS_$key", settingsStr.toString())

        // [FIX] SAVE MIRROR KEYBOARD PARAMS (If in Mirror Mode)
        if (prefs.prefVirtualMirrorMode) {
            // Get live values from window params if available, otherwise use prefs
            val mX = mirrorKeyboardParams?.x ?: prefs.prefMirrorX
            val mY = mirrorKeyboardParams?.y ?: prefs.prefMirrorY
            val mW = mirrorKeyboardParams?.width ?: prefs.prefMirrorWidth
            val mH = mirrorKeyboardParams?.height ?: prefs.prefMirrorHeight
            val mAlpha = prefs.prefMirrorAlpha

            p.putInt("MIRROR_X_$key", mX)
            p.putInt("MIRROR_Y_$key", mY)
            p.putInt("MIRROR_W_$key", mW)
            p.putInt("MIRROR_H_$key", mH)
            p.putInt("MIRROR_ALPHA_$key", mAlpha)
        }

        p.apply()
        showToast("Layout Saved (${if(prefs.prefVirtualMirrorMode) "Mirror" else "Std"})")
    }


    private fun savePrefs() { 
        val e = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        
        e.putFloat("cursor_speed", prefs.cursorSpeed)
        e.putFloat("scroll_speed", prefs.scrollSpeed)
        e.putBoolean("tap_scroll", prefs.prefTapScroll)
        e.putBoolean("vibrate", prefs.prefVibrate)
        e.putBoolean("reverse_scroll", prefs.prefReverseScroll)
        e.putInt("alpha", prefs.prefAlpha)
        e.putInt("bg_alpha", prefs.prefBgAlpha)
        e.putInt("keyboard_alpha", prefs.prefKeyboardAlpha)
        e.putInt("handle_size", prefs.prefHandleSize)
        e.putBoolean("v_pos_left", prefs.prefVPosLeft)
        e.putBoolean("h_pos_top", prefs.prefHPosTop)
        e.putBoolean("anchored", prefs.prefAnchored)
        e.putInt("handle_touch_size", prefs.prefHandleTouchSize)
        e.putInt("scroll_touch_size", prefs.prefScrollTouchSize)
        e.putInt("scroll_visual_size", prefs.prefScrollVisualSize)
        e.putInt("cursor_size", prefs.prefCursorSize)
        e.putInt("keyboard_key_scale", prefs.prefKeyScale)
        e.putBoolean("use_alt_screen_off", prefs.prefUseAltScreenOff)
        e.putBoolean("automation_enabled", prefs.prefAutomationEnabled)
        e.putInt("bubble_x", prefs.prefBubbleX)
        e.putInt("bubble_y", prefs.prefBubbleY)
        e.putInt("bubble_size", prefs.prefBubbleSize)
        e.putInt("bubble_icon_index", prefs.prefBubbleIconIndex)
        e.putInt("bubble_alpha", prefs.prefBubbleAlpha)
        
        e.putBoolean("persistent_service", prefs.prefPersistentService)
        e.putBoolean("block_soft_kb", prefs.prefBlockSoftKeyboard)
        
        e.putString("hardkey_vol_up_tap", prefs.hardkeyVolUpTap)
        e.putString("hardkey_vol_up_double", prefs.hardkeyVolUpDouble)
        e.putString("hardkey_vol_up_hold", prefs.hardkeyVolUpHold)
        e.putString("hardkey_vol_down_tap", prefs.hardkeyVolDownTap)
        e.putString("hardkey_vol_down_double", prefs.hardkeyVolDownDouble)
        e.putString("hardkey_vol_down_hold", prefs.hardkeyVolDownHold)
        e.putString("hardkey_power_double", prefs.hardkeyPowerDouble)
        
        e.putInt("double_tap_ms", prefs.doubleTapMs)
        e.putInt("hold_duration_ms", prefs.holdDurationMs)

        // =================================================================================
        // VIRTUAL MIRROR MODE SAVE
        // =================================================================================
        e.putBoolean("virtual_mirror_mode", prefs.prefVirtualMirrorMode)
        e.putLong("mirror_orient_delay_ms", prefs.prefMirrorOrientDelayMs)

        // Save Mirror Keyboard Prefs
        e.putInt("mirror_alpha", prefs.prefMirrorAlpha)
        e.putInt("mirror_x", prefs.prefMirrorX)
        e.putInt("mirror_y", prefs.prefMirrorY)
        e.putInt("mirror_width", prefs.prefMirrorWidth)
        e.putInt("mirror_height", prefs.prefMirrorHeight)
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE SAVE
        // =================================================================================

        e.apply() 
    }

    private fun bindShizuku() { try { val c = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(c, userServiceConnection, BuildConfig.DEBUG, BuildConfig.VERSION_CODE) } catch (e: Exception) { e.printStackTrace() } }

    // Helper to retry binding if connection is dead/null




    private fun checkAndBindShizuku() {
        // 1. If already bound and alive, do nothing
        if (shellService != null && shellService!!.asBinder().isBinderAlive) {
            return
        }

        // 2. If dead but not null, clear it
        if (shellService != null && !shellService!!.asBinder().isBinderAlive) {
            isBound = false
            shellService = null
        }

        Log.d(TAG, "Binding Shizuku: Attempt 1 (Immediate)")
        // Use existing safe bind method
        bindShizuku() 
        
        // 3. Retry after 2.5 seconds (The Critical Fix)
        // We use 'handler' here (not uiHandler)
        handler.postDelayed({
            if (shellService == null) {
                Log.w(TAG, "Binding Shizuku: Attempt 2 (Delayed 2.5s)")
                bindShizuku()
            }
        }, 2500)
    }





    private fun createNotification() { 
        try {
            val channel = NotificationChannel("overlay_service", "Trackpad", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            val notif = Notification.Builder(this, "overlay_service").setContentTitle("Trackpad Active").setSmallIcon(R.drawable.ic_cursor).build()
            try { if (Build.VERSION.SDK_INT >= 34) startForeground(1, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(1, notif) } catch(e: Exception) { startForeground(1, notif) }
        } catch(e: Exception) { e.printStackTrace() }
    }



    private fun initCustomKeyboard() { 
        if (appWindowManager == null || shellService == null) return
        
        keyboardOverlay = KeyboardOverlay(
            this, 
            appWindowManager!!, 
            shellService, 
            inputTargetDisplayId, 
            { toggleScreen() }, 
            { toggleScreenMode() }, 
            { toggleCustomKeyboard() }
        )
        
        // Wire up Trackpad Callbacks
        keyboardOverlay?.onCursorMove = { dx, dy, isDragging -> handleExternalMouseMove(dx, dy, isDragging) }
        keyboardOverlay?.onCursorClick = { isRight -> handleExternalMouseClick(isRight) }

        // Wire Touch Primitives
        keyboardOverlay?.onTouchDown = { handleExternalTouchDown() }
        keyboardOverlay?.onTouchUp = { handleExternalTouchUp() }
        keyboardOverlay?.onTouchTap = { handleExternalTouchTap() }

        // =================================================================================
        // VIRTUAL MIRROR TOUCH CALLBACK
        // SUMMARY: Wire up the mirror touch callback to forward touch events from the
        //          physical keyboard to the mirror keyboard on the remote display.
        //          Returns true if the touch should be consumed (orientation mode active).
        // =================================================================================
        keyboardOverlay?.onMirrorTouch = { x, y, action ->
            onMirrorKeyboardTouch(x, y, action)
        }
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR TOUCH CALLBACK
        // =================================================================================

        // Wire up layer change callback for mirror keyboard sync
        keyboardOverlay?.onLayerChanged = { state ->
            syncMirrorKeyboardLayer(state)
        }

        // =================================================================================
        // MIRROR SUGGESTIONS SYNC CALLBACK
        // SUMMARY: When suggestions change on physical keyboard, sync to mirror keyboard.
        //          This keeps the prediction bar in sync on both displays.
        // =================================================================================
        keyboardOverlay?.onSuggestionsChanged = { suggestions ->
            mirrorKeyboardView?.setSuggestions(suggestions)
        }
        // =================================================================================
        // END BLOCK: MIRROR SUGGESTIONS SYNC CALLBACK
        // =================================================================================

        // =================================================================================
        // PHYSICAL KEYBOARD SIZE/POSITION CHANGE CALLBACK
        // SUMMARY: When physical keyboard is moved/resized, sync mirror keyboard.
        // =================================================================================
        keyboardOverlay?.onSizeChanged = {
            syncMirrorWithPhysicalKeyboard()
        }
        // =================================================================================
        // END BLOCK: PHYSICAL KEYBOARD SIZE/POSITION CHANGE CALLBACK
        // =================================================================================

        // FIX: Restore Saved Layout (fixes reset/aspect ratio issue)
        if (savedKbW > 0 && savedKbH > 0) {
            keyboardOverlay?.updatePosition(savedKbX, savedKbY)
            keyboardOverlay?.updateSize(savedKbW, savedKbH)
        } else {
            // [Fixed] Default Size: Calculate height based on scale + 20dp buffer
            val density = resources.displayMetrics.density
            // Add 20dp padding so the scale fits comfortably without clipping
            val buffer = 20 * density
            val defaultH = ((275f * (prefs.prefKeyScale / 100f) * density) + buffer).toInt()
            
            keyboardOverlay?.updatePosition(0, uiScreenHeight - defaultH)
            keyboardOverlay?.updateSize(uiScreenWidth, defaultH)
        }

        // [REMOVED] Do not force scale here. 
        // KeyboardOverlay loads the fresh "keyboard_key_scale" from disk automatically.
        // Forcing 'prefs.prefKeyScale' here causes bugs because 'prefs' might be stale.
    }




    fun toggleCustomKeyboard(suppressAutomation: Boolean = false) {
        if (keyboardOverlay == null) initCustomKeyboard()
        
        val isNowVisible = if (keyboardOverlay?.isShowing() == true) { 
            keyboardOverlay?.hide()
            false 
        } else { 
            keyboardOverlay?.show()
            // [REMOVED] Stale scale enforcement. 
            // The show() method already loads the correct saved scale from disk.
            true 
        }
        
        isCustomKeyboardVisible = isNowVisible
        enforceZOrder()
        
        if (prefs.prefAutomationEnabled && !suppressAutomation) { 
            if (isNowVisible) turnScreenOn() else turnScreenOff() 
        }
    }

    private fun turnScreenOn() { isScreenOff = false; Thread { try { shellService?.setBrightness(128); shellService?.setScreenOff(0, false) } catch(e: Exception) {} }.start(); showToast("Screen On") }
    private fun turnScreenOff() { isScreenOff = true; Thread { try { if (prefs.prefUseAltScreenOff) shellService?.setBrightness(-1) else shellService?.setScreenOff(0, true) } catch(e: Exception) {} }.start(); showToast("Screen Off (${if(prefs.prefUseAltScreenOff) "Alt" else "Std"})") }
    private fun toggleScreenMode() { prefs.prefUseAltScreenOff = !prefs.prefUseAltScreenOff; savePrefs(); showToast("Mode: ${if(prefs.prefUseAltScreenOff) "Alternate" else "Standard"}") }
    private fun toggleScreen() { if (isScreenOff) turnScreenOn() else turnScreenOff() }
    
    private fun updateUiMetrics() { val display = displayManager?.getDisplay(currentDisplayId) ?: return; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); uiScreenWidth = metrics.widthPixels; uiScreenHeight = metrics.heightPixels }
    private fun createTrackpadDisplayContext(display: Display): Context { return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) createDisplayContext(display).createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null) else createDisplayContext(display) }
    private fun addHandle(context: Context, gravity: Int, color: Int, onTouch: (View, MotionEvent) -> Boolean) { val container = FrameLayout(context); val p = FrameLayout.LayoutParams(prefs.prefHandleTouchSize, prefs.prefHandleTouchSize); p.gravity = gravity; val visual = View(context); val bg = GradientDrawable(); bg.setColor(color); bg.cornerRadius = 15f; visual.background = bg; val vp = FrameLayout.LayoutParams(prefs.prefHandleSize, prefs.prefHandleSize); vp.gravity = Gravity.CENTER; container.addView(visual, vp); handleContainers.add(container); handleVisuals.add(visual); trackpadLayout?.addView(container, p); container.setOnTouchListener { v, e -> onTouch(v, e) } }
    
    private fun addScrollBars(context: Context) {
        val margin = prefs.prefHandleTouchSize + 10
        vScrollContainer = FrameLayout(context)
        val vp = FrameLayout.LayoutParams(prefs.prefScrollTouchSize, FrameLayout.LayoutParams.MATCH_PARENT); vp.gravity = if (prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; vp.setMargins(0, margin, 0, margin)
        trackpadLayout?.addView(vScrollContainer, vp)
        vScrollVisual = View(context); vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())
        vScrollContainer?.addView(vScrollVisual, FrameLayout.LayoutParams(prefs.prefScrollVisualSize, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER))
        vScrollContainer?.setOnTouchListener { _, event -> handleVScrollTouch(event); true }
        
        hScrollContainer = FrameLayout(context)
        val hp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefs.prefScrollTouchSize); hp.gravity = if (prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM; hp.setMargins(margin, 0, margin, 0)
        trackpadLayout?.addView(hScrollContainer, hp)
        hScrollVisual = View(context); hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())
        hScrollContainer?.addView(hScrollVisual, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefs.prefScrollVisualSize, Gravity.CENTER))
        hScrollContainer?.setOnTouchListener { _, event -> handleHScrollTouch(event); true }
    }

    private fun handleVScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { isVScrolling = true; lastTouchY = event.y; scrollAccumulatorY = 0f; vScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt()); if (prefs.prefTapScroll) { val h = vScrollContainer?.height ?: return; val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed; performSwipe(0f, if (event.y < h/2) (if (prefs.prefReverseScroll) -dist else dist) else (if (prefs.prefReverseScroll) dist else -dist)) } }
            MotionEvent.ACTION_MOVE -> { if (isVScrolling && !prefs.prefTapScroll) { val dy = event.y - lastTouchY; scrollAccumulatorY += dy * prefs.scrollSpeed; if (abs(scrollAccumulatorY) > 30f) { performSwipe(0f, if (prefs.prefReverseScroll) -scrollAccumulatorY * 2 else scrollAccumulatorY * 2); scrollAccumulatorY = 0f }; lastTouchY = event.y } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isVScrolling = false; scrollAccumulatorY = 0f; vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt()) }
        }
    }
    
    private fun handleHScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { isHScrolling = true; lastTouchX = event.x; scrollAccumulatorX = 0f; hScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt()); if (prefs.prefTapScroll) { val w = hScrollContainer?.width ?: return; val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed; performSwipe(if (event.x < w/2) (if (prefs.prefReverseScroll) -dist else dist) else (if (prefs.prefReverseScroll) dist else -dist), 0f) } }
            MotionEvent.ACTION_MOVE -> { if (isHScrolling && !prefs.prefTapScroll) { val dx = event.x - lastTouchX; scrollAccumulatorX += dx * prefs.scrollSpeed; if (abs(scrollAccumulatorX) > 30f) { performSwipe(if (prefs.prefReverseScroll) -scrollAccumulatorX * 2 else scrollAccumulatorX * 2, 0f); scrollAccumulatorX = 0f }; lastTouchX = event.x } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isHScrolling = false; scrollAccumulatorX = 0f; hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt()) }
        }
    }

    private fun updateScrollPosition() { val margin = prefs.prefHandleTouchSize + 10; vScrollContainer?.let { c -> val p = c.layoutParams as FrameLayout.LayoutParams; p.gravity = if (prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; p.setMargins(0, margin, 0, margin); c.layoutParams = p }; hScrollContainer?.let { c -> val p = c.layoutParams as FrameLayout.LayoutParams; p.gravity = if (prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM; p.setMargins(margin, 0, margin, 0); c.layoutParams = p } }
    private fun updateHandleSize() { for (v in handleVisuals) { val p = v.layoutParams; p.width = prefs.prefHandleSize; p.height = prefs.prefHandleSize; v.layoutParams = p } }
    private fun updateScrollSize() {
        prefs.prefScrollTouchSize = prefs.prefScrollTouchSize.coerceIn(40, 180); prefs.prefScrollVisualSize = prefs.prefScrollVisualSize.coerceIn(4, 20); scrollZoneThickness = prefs.prefScrollTouchSize
        vScrollContainer?.let { it.layoutParams.width = prefs.prefScrollTouchSize; it.requestLayout() }; vScrollVisual?.let { it.layoutParams.width = prefs.prefScrollVisualSize; it.requestLayout() }
        hScrollContainer?.let { it.layoutParams.height = prefs.prefScrollTouchSize; it.requestLayout() }; hScrollVisual?.let { it.layoutParams.height = prefs.prefScrollVisualSize; it.requestLayout() }
    }
    private fun updateLayoutSizes() { for (c in handleContainers) { val p = c.layoutParams; p.width = prefs.prefHandleTouchSize; p.height = prefs.prefHandleTouchSize; c.layoutParams = p } }
    private fun updateCursorSize() { val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; cursorView?.layoutParams?.let { it.width = size; it.height = size; cursorView?.layoutParams = it } }
    private fun updateBorderColor(strokeColor: Int) { currentBorderColor = strokeColor; val bg = trackpadLayout?.background as? GradientDrawable ?: return; bg.setColor((prefs.prefBgAlpha shl 24) or 0x000000); bg.setStroke(4, (prefs.prefAlpha shl 24) or 0xFFFFFF); trackpadLayout?.invalidate() }
    
    private fun performSwipe(dx: Float, dy: Float) {
        Thread {
            val dId = if (inputTargetDisplayId != -1) inputTargetDisplayId else (cursorLayout?.display?.displayId ?: Display.DEFAULT_DISPLAY)
            val now = SystemClock.uptimeMillis(); val startX = cursorX; val startY = cursorY; val endX = startX + dx; val endY = startY + dy
            try { shellService?.injectMouse(MotionEvent.ACTION_DOWN, startX, startY, dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now) } catch(e: Exception) {}
            for (i in 1..5) { val t = i / 5f; try { shellService?.injectMouse(MotionEvent.ACTION_MOVE, startX + (dx*t), startY + (dy*t), dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now + (i*10)); Thread.sleep(10) } catch(e: Exception) {} }
            try { shellService?.injectMouse(MotionEvent.ACTION_UP, endX, endY, dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now + 100) } catch(e: Exception) {}
        }.start()
    }

    private fun handleTrackpadTouch(event: MotionEvent) {
        val viewWidth = trackpadLayout?.width ?: 0; val viewHeight = trackpadLayout?.height ?: 0; if (viewWidth == 0 || viewHeight == 0) return
        if (isReleaseDebouncing && event.actionMasked != MotionEvent.ACTION_DOWN) return
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // NEW: dismiss Voice if active
                checkAndDismissVoice()

                handler.removeCallbacks(releaseDebounceRunnable); isReleaseDebouncing = false
                touchDownTime = SystemClock.uptimeMillis(); touchDownX = event.x; touchDownY = event.y; lastTouchX = event.x; lastTouchY = event.y; isTouchDragging = false
                val actualZoneV = min(scrollZoneThickness, (viewWidth * 0.15f).toInt()); val actualZoneH = min(scrollZoneThickness, (viewHeight * 0.15f).toInt())
                if ((prefs.prefVPosLeft && event.x < actualZoneV) || (!prefs.prefVPosLeft && event.x > viewWidth - actualZoneV) || (prefs.prefHPosTop && event.y < actualZoneH) || (!prefs.prefHPosTop && event.y > viewHeight - actualZoneH)) { ignoreTouchSequence = true; return }
                handler.postDelayed(longPressRunnable, 400)
            }
            MotionEvent.ACTION_MOVE -> {
                if (ignoreTouchSequence) return
                // VIRTUAL DISPLAY KEEP-ALIVE: Ping user activity during active touch on remote display
                if (inputTargetDisplayId != currentDisplayId) pingUserActivity()
                if (kotlin.math.sqrt((event.x - touchDownX) * (event.x - touchDownX) + (event.y - touchDownY) * (event.y - touchDownY)) > TAP_SLOP_PX) handler.removeCallbacks(longPressRunnable)
                val dx = (event.x - lastTouchX) * prefs.cursorSpeed; val dy = (event.y - lastTouchY) * prefs.cursorSpeed
                val safeW = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth.toFloat() else uiScreenWidth.toFloat()
                val safeH = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight.toFloat() else uiScreenHeight.toFloat()
                var fDx = dx; var fDy = dy
                when (rotationAngle) { 90 -> { fDx = -dy; fDy = dx }; 180 -> { fDx = -dx; fDy = -dy }; 270 -> { fDx = dy; fDy = -dx } }
                cursorX = (cursorX + fDx).coerceIn(0f, safeW); cursorY = (cursorY + fDy).coerceIn(0f, safeH)
                if (inputTargetDisplayId == currentDisplayId) { cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {} } 
                else { remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {} }
                if (isTouchDragging || isKeyDragging) injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, SystemClock.uptimeMillis()) else injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
                lastTouchX = event.x; lastTouchY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                if (!ignoreTouchSequence) {
                    if (isTouchDragging) { injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, SystemClock.uptimeMillis()); isTouchDragging = false }
                    else if (!isKeyDragging && SystemClock.uptimeMillis() - touchDownTime < TAP_TIMEOUT_MS && kotlin.math.sqrt((event.x - touchDownX) * (event.x - touchDownX) + (event.y - touchDownY) * (event.y - touchDownY)) < TAP_SLOP_PX) performClick(false)
                }
                isReleaseDebouncing = true; handler.postDelayed(releaseDebounceRunnable, RELEASE_DEBOUNCE_MS)
                if (!isKeyDragging) { isVScrolling = false; isHScrolling = false; updateBorderColor(currentBorderColor) }
                ignoreTouchSequence = false
            }
        }
    }

    // --- SMART VISIBILITY LOGIC ---
    fun performSmartHide() {
        pendingRestoreTrackpad = isTrackpadVisible
        pendingRestoreKeyboard = isCustomKeyboardVisible
        hasPendingRestore = true
        
        // Hide components (Automation logic inside toggleCustomKeyboard will handle screen off if enabled)
        if (isCustomKeyboardVisible) toggleCustomKeyboard()
        if (isTrackpadVisible) toggleTrackpad()
        
        handler.post { Toast.makeText(this, "Hidden (Tap Bubble to Restore)", Toast.LENGTH_SHORT).show() }
    }

    fun performSmartRestore() {
        if (!hasPendingRestore) {
            // Fallback: Just show Trackpad if no state saved
            if (!isTrackpadVisible) toggleTrackpad()
            return
        }
        
        if (pendingRestoreTrackpad && !isTrackpadVisible) toggleTrackpad()
        if (pendingRestoreKeyboard && !isCustomKeyboardVisible) toggleCustomKeyboard()
        
        hasPendingRestore = false
    }

    private fun moveWindow(event: MotionEvent): Boolean { if (prefs.prefAnchored) return true; if (event.action == MotionEvent.ACTION_MOVE) { trackpadParams.x += (event.rawX - lastTouchX).toInt(); trackpadParams.y += (event.rawY - lastTouchY).toInt(); windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }; lastTouchX = event.rawX; lastTouchY = event.rawY; return true }
    private fun resizeWindow(event: MotionEvent): Boolean { if (prefs.prefAnchored) return true; if (event.action == MotionEvent.ACTION_MOVE) { trackpadParams.width += (event.rawX - lastTouchX).toInt(); trackpadParams.height += (event.rawY - lastTouchY).toInt(); windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }; lastTouchX = event.rawX; lastTouchY = event.rawY; return true }
    private fun keyboardHandle(event: MotionEvent): Boolean { 
        if (event.action == MotionEvent.ACTION_UP) {
            // Reverted: Just toggle keyboard visibility
            toggleCustomKeyboard()
        } 
        return true 
    }
    private fun openMenuHandle(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_DOWN) menuManager?.toggle(); return true }
    private fun injectAction(action: Int, source: Int, button: Int, time: Long) { if (shellService == null) return; Thread { try { shellService?.injectMouse(action, cursorX, cursorY, inputTargetDisplayId, source, button, time) } catch(e: Exception){} }.start() }
    private fun injectScroll(hScroll: Float, vScroll: Float) { if (shellService == null) return; Thread { try { shellService?.injectScroll(cursorX, cursorY, vScroll / 10f, hScroll / 10f, inputTargetDisplayId) } catch(e: Exception){} }.start() }

    // Helper to allow external components (like Keyboard) to control the cursor
    // Added 'isDragging' to switch between Hover (Mouse) and Drag (Touch)
// =================================================================================
    // SPACEBAR MOUSE CURSOR MOVEMENT HANDLER
    // SUMMARY: Handles cursor movement from the spacebar trackpad feature.
    //          Updates cursor position and visual, then injects hover/drag events.
    //          CRITICAL: Skips hover injection when cursor is over keyboard bounds
    //          to prevent feedback loop that causes lag/freezing.
    // =================================================================================
    fun handleExternalMouseMove(dx: Float, dy: Float, isDragging: Boolean) {
        // Calculate safe bounds
        val safeW = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth.toFloat() else uiScreenWidth.toFloat()
        val safeH = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight.toFloat() else uiScreenHeight.toFloat()

        // Update position
        cursorX = (cursorX + dx).coerceIn(0f, safeW)
        cursorY = (cursorY + dy).coerceIn(0f, safeH)

        // Update Visuals (Redraw the cursor icon)
        if (inputTargetDisplayId == currentDisplayId) {
             cursorParams.x = cursorX.toInt()
             cursorParams.y = cursorY.toInt()
             try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {}
        } else {
             remoteCursorParams.x = cursorX.toInt()
             remoteCursorParams.y = cursorY.toInt()
             try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {}
        }

        // [ANTI-FEEDBACK-LOOP FIX]
        // Check if cursor is over the keyboard - if so, skip hover injection
        // This prevents the injected mouse events from creating a feedback loop
        // where the keyboard receives the hover, processes it, and triggers more events
        val isOverKeyboard = isCursorOverKeyboard()
        
        // Input Injection
        if (isDragging) {
             // TOUCH DRAG: SOURCE_TOUCHSCREEN + ACTION_MOVE
             // Always inject drag events - user explicitly initiated drag
             injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, 0, SystemClock.uptimeMillis())
        } else {
             // MOUSE HOVER: SOURCE_MOUSE + ACTION_HOVER_MOVE
             // Skip hover injection when over keyboard to prevent feedback loop
             if (!isOverKeyboard) {
                 injectAction(MotionEvent.ACTION_HOVER_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
             }
        }
    }
    // =================================================================================
    // END BLOCK: SPACEBAR MOUSE CURSOR MOVEMENT HANDLER
    // =================================================================================

    // =================================================================================
    // KEYBOARD BOUNDS CHECK FOR CURSOR
    // SUMMARY: Returns true if the cursor is currently positioned over the keyboard
    //          overlay window. Used to prevent feedback loops when the spacebar
    //          mouse feature moves the cursor over the keyboard itself.
    // =================================================================================
    private fun isCursorOverKeyboard(): Boolean {
        // Only check if keyboard is visible and we're on the same display
        if (!isCustomKeyboardVisible) return false
        if (inputTargetDisplayId != currentDisplayId) return false
        
        // Get keyboard bounds from the overlay
        val kbX = keyboardOverlay?.getViewX() ?: return false
        val kbY = keyboardOverlay?.getViewY() ?: return false
        val kbW = keyboardOverlay?.getViewWidth() ?: return false
        val kbH = keyboardOverlay?.getViewHeight() ?: return false
        
        // Add a small padding to the bounds to ensure we catch edge cases
        val padding = 10
        
        return cursorX >= (kbX - padding) && 
               cursorX <= (kbX + kbW + padding) &&
               cursorY >= (kbY - padding) && 
               cursorY <= (kbY + kbH + padding)
    }
    // =================================================================================
    // END BLOCK: KEYBOARD BOUNDS CHECK FOR CURSOR
    // ==============================================

    // Explicit Touch Down (Start Drag/Hold)
    fun handleExternalTouchDown() {
        injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, 0, SystemClock.uptimeMillis())
        android.util.Log.d("TouchInjection", "Touch DOWN at ($cursorX, $cursorY)")
    }

    // Explicit Touch Up (End Drag/Hold)
    fun handleExternalTouchUp() {
        injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, 0, SystemClock.uptimeMillis())
        android.util.Log.d("TouchInjection", "Touch UP at ($cursorX, $cursorY)")
    }

    // Quick Tap (Down + Up)
    fun handleExternalTouchTap() {
        if (shellService == null) return
        Thread {
            val now = SystemClock.uptimeMillis()
            try {
                shellService?.injectMouse(MotionEvent.ACTION_DOWN, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, 0, now)
                Thread.sleep(50)
                shellService?.injectMouse(MotionEvent.ACTION_UP, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, 0, now + 50)
                android.util.Log.d("TouchInjection", "Touch TAP at ($cursorX, $cursorY)")
            } catch (e: Exception) {
                android.util.Log.e("TouchInjection", "TAP failed", e)
            }
        }.start()
    }

    // Keep Right Click for the predictive bar if needed
    fun handleExternalMouseClick(isRight: Boolean) {
        performClick(isRight)
    }

    fun performClick(right: Boolean) { if (shellService == null) return; Thread { try { if (right) shellService?.execRightClick(cursorX, cursorY, inputTargetDisplayId) else shellService?.execClick(cursorX, cursorY, inputTargetDisplayId) } catch(e: Exception){} }.start() }
    fun resetCursorCenter() { cursorX = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth/2f else uiScreenWidth/2f; cursorY = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight/2f else uiScreenHeight/2f; if (inputTargetDisplayId == currentDisplayId) { cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); windowManager?.updateViewLayout(cursorLayout, cursorParams) } else { remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){} } }
    fun performRotation() { rotationAngle = (rotationAngle + 90) % 360; cursorView?.rotation = rotationAngle.toFloat() }

    // =================================================================================
    // PROFILE KEY GENERATION
    // SUMMARY: Generates a unique key based on Resolution + Mirror Mode State.
    //          Allows separate profiles for "Standard" and "Mirror" modes.
    // =================================================================================

    // =================================================================================
    // PROFILE KEY GENERATION
    // SUMMARY: Generates a unique key based on Resolution + Mirror Mode State.
    //          Allows separate profiles for "Standard" and "Mirror" modes.
    // =================================================================================




    // =================================================================================
    // VIRTUAL MIRROR MODE PROFILE KEY
    // SUMMARY: Returns a unique profile key for mirror mode, separate from normal
    //          display-based profiles. This allows mirror mode to have its own
    //          trackpad/keyboard layout that persists independently.
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE PROFILE KEY
    // =================================================================================

    // =================================================================================
    // VIRTUAL MIRROR MODE LAYOUT SAVE
    // SUMMARY: Saves the current layout to the mirror mode profile. Called when
    //          exiting mirror mode or when explicitly saving while in mirror mode.
    //          Includes both physical keyboard AND mirror keyboard positions/sizes.
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE LAYOUT SAVE
    // =================================================================================

    // =================================================================================
    // VIRTUAL MIRROR MODE LAYOUT LOAD
    // SUMMARY: Loads the mirror mode profile layout. Called when entering mirror mode.
    //          If no saved profile exists, uses sensible defaults.
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE LAYOUT LOAD
    // =================================================================================

    // =================================================================================
    // FUNCTION: applyMirrorKeyboardSettings
    // SUMMARY: Applies saved mirror keyboard position/size/alpha to the live mirror
    //          keyboard. Called after loading a mirror mode profile.
    // =================================================================================
    private fun applyMirrorKeyboardSettings() {
        if (mirrorKeyboardParams == null || mirrorKeyboardContainer == null) return

        // Apply saved position if valid
        if (prefs.prefMirrorX != -1) {
            mirrorKeyboardParams?.x = prefs.prefMirrorX
            mirrorKeyboardParams?.gravity = Gravity.TOP or Gravity.START
        }
        if (prefs.prefMirrorY != 0 || prefs.prefMirrorX != -1) {
            mirrorKeyboardParams?.y = prefs.prefMirrorY
        }
        if (prefs.prefMirrorWidth != -1 && prefs.prefMirrorWidth > 0) {
            mirrorKeyboardParams?.width = prefs.prefMirrorWidth
        }
        if (prefs.prefMirrorHeight != -1 && prefs.prefMirrorHeight > 0) {
            mirrorKeyboardParams?.height = prefs.prefMirrorHeight
        }

        // Apply alpha
        val alpha = prefs.prefMirrorAlpha / 255f
        mirrorKeyboardContainer?.alpha = alpha

        // Update the window
        try {
            mirrorWindowManager?.updateViewLayout(mirrorKeyboardContainer, mirrorKeyboardParams)

            // Update sync dimensions after layout change
            handler.postDelayed({
                updateMirrorSyncDimensions()
            }, 100)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply mirror keyboard settings", e)
        }

        Log.d(TAG, "Mirror keyboard settings applied")
    }
    // =================================================================================
    // END BLOCK: applyMirrorKeyboardSettings
    // =================================================================================



    // =================================================================================
    // FUNCTION: loadLayout
    // SUMMARY: Loads a display-specific profile containing trackpad window position/size,
    //          keyboard settings (scale, alpha, position), and visual preferences.
    //          The profile key is based on screen resolution + mirror mode state.
    //          CRITICAL: After loading profile scale, syncs it to global SharedPreferences
    //          so KeyboardOverlay.show() picks up the correct value.
    // =================================================================================
    fun loadLayout() {
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val key = getProfileKey()
        
        // 1. Load Trackpad Window
        trackpadParams.x = p.getInt("X_$key", 100)
        trackpadParams.y = p.getInt("Y_$key", 100)
        trackpadParams.width = p.getInt("W_$key", 400)
        trackpadParams.height = p.getInt("H_$key", 300)
        try {
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
        } catch(e: Exception){}
        
        // 2. Load Settings String
        val settings = p.getString("SETTINGS_$key", null)
        var keyboardUpdated = false

        if (settings != null) {
            val parts = settings.split(";")
            if (parts.size >= 15) {
                // ... (Parsing Basic Params) ...
                prefs.cursorSpeed = parts[0].toFloat(); prefs.scrollSpeed = parts[1].toFloat()
                prefs.prefTapScroll = parts[2] == "1"; prefs.prefReverseScroll = parts[3] == "1"
                prefs.prefAlpha = parts[4].toInt(); prefs.prefBgAlpha = parts[5].toInt()
                prefs.prefKeyboardAlpha = parts[6].toInt(); prefs.prefHandleSize = parts[7].toInt()
                prefs.prefHandleTouchSize = parts[8].toInt(); prefs.prefScrollTouchSize = parts[9].toInt()
                prefs.prefScrollVisualSize = parts[10].toInt(); prefs.prefCursorSize = parts[11].toInt()
                prefs.prefKeyScale = parts[12].toInt()
                prefs.prefAutomationEnabled = parts[13] == "1"; prefs.prefAnchored = parts[14] == "1"
                
                // =================================================================================
                // FIX: SYNC PROFILE SCALE TO GLOBAL SHAREDPREFS
                // SUMMARY: KeyboardOverlay.show() reads "keyboard_key_scale" from SharedPrefs directly.
                //          We must update this global key whenever we load a profile, otherwise
                //          the keyboard will use the scale from the previous display/profile.
                // =================================================================================
                p.edit().putInt("keyboard_key_scale", prefs.prefKeyScale).apply()
                // =================================================================================
                // END BLOCK: SYNC PROFILE SCALE TO GLOBAL SHAREDPREFS
                // =================================================================================
                
                if (parts.size >= 27) {
                    prefs.prefBubbleSize = parts[15].toInt(); prefs.prefBubbleAlpha = parts[16].toInt()
                    prefs.prefBubbleIconIndex = parts[17].toInt(); prefs.prefBubbleX = parts[18].toInt()
                    prefs.prefBubbleY = parts[19].toInt(); prefs.hardkeyVolUpTap = parts[20]
                    prefs.hardkeyVolUpDouble = parts[21]; prefs.hardkeyVolUpHold = parts[22]
                    prefs.hardkeyVolDownTap = parts[23]; prefs.hardkeyVolDownDouble = parts[24]
                    prefs.hardkeyVolDownHold = parts[25]; prefs.hardkeyPowerDouble = parts[26]
                }
                
                if (parts.size >= 30) {
                    prefs.prefVibrate = parts[27] == "1"
                    prefs.prefVPosLeft = parts[28] == "1"
                    prefs.prefHPosTop = parts[29] == "1"
                }

                // Load Physical Keyboard Bounds
                var kbIndex = 30
                if (parts.size < 34 && parts.size >= 31) kbIndex = 27 

                if (parts.size > kbIndex) {
                     savedKbX = parts[kbIndex].toInt(); savedKbY = parts[kbIndex+1].toInt()
                     savedKbW = parts[kbIndex+2].toInt(); savedKbH = parts[kbIndex+3].toInt()
                     
                     keyboardOverlay?.setWindowBounds(savedKbX, savedKbY, savedKbW, savedKbH)
                     keyboardUpdated = true
                }

                // Apply Visuals
                updateBorderColor(currentBorderColor); updateScrollSize(); updateHandleSize()
                updateCursorSize(); updateScrollPosition()
                keyboardOverlay?.updateScale(prefs.prefKeyScale / 100f)
                keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha)
                keyboardOverlay?.setAnchored(prefs.prefAnchored)
                keyboardOverlay?.setVibrationEnabled(prefs.prefVibrate)
                applyBubbleAppearance()
            }
        }

        // [FIX] LOAD MIRROR KEYBOARD PARAMS (If in Mirror Mode)
        if (prefs.prefVirtualMirrorMode) {
            if (p.contains("MIRROR_X_$key")) {
                prefs.prefMirrorX = p.getInt("MIRROR_X_$key", -1)
                prefs.prefMirrorY = p.getInt("MIRROR_Y_$key", 0)
                prefs.prefMirrorWidth = p.getInt("MIRROR_W_$key", -1)
                prefs.prefMirrorHeight = p.getInt("MIRROR_H_$key", -1)
                prefs.prefMirrorAlpha = p.getInt("MIRROR_ALPHA_$key", 200)
                
                // Update live window if it exists
                applyMirrorKeyboardSettings()
            }
        }

        // [FIX] GHOSTING PREVENTION
        if (!keyboardUpdated) {
            keyboardOverlay?.resetPosition()
            showToast("Defaults Loaded")
        } else {
            showToast("Profile Loaded: ${if(prefs.prefVirtualMirrorMode) "Mirror" else "Std"}")
        }
    }
    // =================================================================================
    // END BLOCK: loadLayout
    // =================================================================================




    fun deleteCurrentProfile() { /* Stub */ }
    fun resetKeyboardPosition() {
        keyboardOverlay?.resetPosition()
    }

    fun rotateKeyboard() {
        keyboardOverlay?.cycleRotation()
    }

    fun resetTrackpadPosition() { trackpadParams.x = 100; trackpadParams.y = 100; trackpadParams.width = 400; trackpadParams.height = 300; windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }    // =================================================================================
// =================================================================================
    // FUNCTION: showMirrorTemporarily
    // SUMMARY: Makes mirror keyboard visible temporarily, like when touched.
    //          Used during D-pad adjustments so user can see changes.
    // =================================================================================
    private fun showMirrorTemporarily() {
        if (mirrorKeyboardContainer == null || mirrorKeyboardView == null) return
        
        // Cancel any pending fade
        mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)
        
        // Show mirror - only adjust alpha, container is transparent
        val alpha = prefs.prefMirrorAlpha / 255f
        mirrorKeyboardContainer?.alpha = 1f
        mirrorKeyboardView?.alpha = alpha.coerceAtLeast(0.7f)
        
        // Schedule fade out after 2 seconds
        mirrorFadeHandler.postDelayed(mirrorFadeRunnable, 2000)
    }
    // =================================================================================
    // END BLOCK: showMirrorTemporarily
    // =================================================================================
    // =================================================================================
// =================================================================================
    // FUNCTION: adjustMirrorKeyboard
    // SUMMARY: Adjusts mirror keyboard position or size via D-pad controls.
    //          FIXED: Correct Y direction for both move and resize.
    //          FIXED: Handle WRAP_CONTENT properly for resize.
    // =================================================================================
    fun adjustMirrorKeyboard(isResize: Boolean, deltaX: Int, deltaY: Int) {
        // DEBUG: Show toast so we know function is called
        showToast("adjust: resize=$isResize dX=$deltaX dY=$deltaY")
        
        if (mirrorKeyboardParams == null || mirrorKeyboardContainer == null) {
            showToast("ERROR: params null")
            return
        }
        
        // Show mirror during adjustment
        showMirrorTemporarily()
        
if (isResize) {
            // =================================================================================
            // RESIZE MODE - adjust width/height AND scale the keyboard keys
            // SUMMARY: Changes container size and scales the inner KeyboardView to match.
            //          Also updates sync dimensions so touch coordinates map correctly.
            // =================================================================================
            
            // Get current dimensions from params
            var currentWidth = mirrorKeyboardParams?.width ?: 600
            var currentHeight = mirrorKeyboardParams?.height ?: 350
            
            // Handle WRAP_CONTENT (-2) - must get actual measured size from view
            if (currentWidth == WindowManager.LayoutParams.WRAP_CONTENT || currentWidth <= 0) {
                currentWidth = mirrorKeyboardContainer?.width ?: mirrorKeyboardView?.width ?: 600
                if (currentWidth <= 0) {
                    val display = displayManager?.getDisplay(inputTargetDisplayId)
                    if (display != null) {
                        val metrics = android.util.DisplayMetrics()
                        display.getRealMetrics(metrics)
                        currentWidth = (metrics.widthPixels * 0.9f).toInt()
                    } else {
                        currentWidth = 600
                    }
                }
                mirrorKeyboardParams?.width = currentWidth
            }
            
            if (currentHeight == WindowManager.LayoutParams.WRAP_CONTENT || currentHeight <= 0) {
                currentHeight = mirrorKeyboardContainer?.height ?: mirrorKeyboardView?.height ?: 350
                if (currentHeight <= 0) {
                    currentHeight = 350
                }
                mirrorKeyboardParams?.height = currentHeight
            }
            
            // Apply deltas: positive = grow, negative = shrink
            val newWidth = (currentWidth + deltaX).coerceIn(250, 1500)
            val newHeight = (currentHeight + deltaY).coerceIn(150, 1200)
            
            android.util.Log.d("MirrorResize", "Resize: ${currentWidth}x${currentHeight} -> ${newWidth}x${newHeight}")
            
            // Update container window params
            mirrorKeyboardParams?.width = newWidth
            mirrorKeyboardParams?.height = newHeight
            
            // Calculate new scale based on height ratio
            // Use physical keyboard as reference - mirror should scale proportionally
            val physicalHeight = keyboardOverlay?.getViewHeight()?.toFloat() ?: 350f
            val physicalScale = keyboardOverlay?.getScale() ?: 1.0f
            
            // Calculate what scale the mirror needs to fit in newHeight
            // Scale is proportional to height ratio
            val heightRatio = newHeight.toFloat() / physicalHeight
            val newScale = (physicalScale * heightRatio).coerceIn(0.5f, 2.0f)
            
            android.util.Log.d("MirrorResize", "Scale: physH=$physicalHeight, physScale=$physicalScale, ratio=$heightRatio, newScale=$newScale")
            
            // Apply scale to mirror keyboard - this resizes the actual keys
            mirrorKeyboardView?.setScale(newScale)
            
            // Update inner view layout params to match container
            mirrorKeyboardView?.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT  // Let it size naturally with new scale
            )
            
            // Save to prefs
            prefs.prefMirrorWidth = newWidth
            prefs.prefMirrorHeight = newHeight
            
            getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
                .putInt("mirror_width", newWidth)
                .putInt("mirror_height", newHeight)
                .apply()
        } else {
            // =================================================================================
            // MOVE MODE - adjust x/y position
            // For move: we want UP button to move keyboard UP on screen
            // UP sends deltaY = -20
            // With Gravity.BOTTOM: to move UP visually, Y offset must INCREASE
            // With Gravity.TOP: to move UP visually, Y offset must DECREASE
            // =================================================================================
            val currentX = mirrorKeyboardParams?.x ?: 0
            val currentY = mirrorKeyboardParams?.y ?: 0
            val gravity = mirrorKeyboardParams?.gravity ?: 0
            val isBottomGravity = (gravity and Gravity.BOTTOM) == Gravity.BOTTOM
            
            val newX = currentX + deltaX
            
            // CRITICAL FIX: Correct Y handling based on gravity
            val newY = if (isBottomGravity) {
                // Gravity.BOTTOM: positive Y = UP from bottom
                // UP button sends -20, we want to move UP, so negate: currentY - (-20) = currentY + 20
                currentY - deltaY
            } else {
                // Gravity.TOP: positive Y = DOWN from top  
                // UP button sends -20, we want to move UP, so add directly: currentY + (-20)
                currentY + deltaY
            }
            
            showToast("Move: ($currentX,$currentY)->($newX,$newY) btm=$isBottomGravity")
            
            mirrorKeyboardParams?.x = newX
            mirrorKeyboardParams?.y = newY
            
            // Switch to TOP gravity after first move for consistent behavior
            mirrorKeyboardParams?.gravity = Gravity.TOP or Gravity.START
            
            getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
                .putInt("mirror_x", newX)
                .putInt("mirror_y", newY)
                .apply()
        }
        
        try {
            mirrorWindowManager?.updateViewLayout(mirrorKeyboardContainer, mirrorKeyboardParams)
            
            if (isResize) {
                handler.postDelayed({ updateMirrorSyncDimensions() }, 100)
            }
        } catch (e: Exception) {
            showToast("Layout update failed: ${e.message}")
        }
    }
    // =================================================================================
    // END BLOCK: adjustMirrorKeyboard
    // =================================================================================
    // =================================================================================
    // END BLOCK: adjustMirrorKeyboard
    // =================================================================================
    // =================================================================================
// =================================================================================
    // FUNCTION: resetMirrorKeyboardPosition
    // SUMMARY: Resets mirror keyboard to default centered position at bottom.
    //          Uses WRAP_CONTENT height and triggers sync dimension update.
    // =================================================================================
    fun resetMirrorKeyboardPosition() {
        if (mirrorKeyboardParams == null || mirrorKeyboardContainer == null) return
        
        // Show during reset
        showMirrorTemporarily()
        
        // Get display metrics for auto-sizing
        val display = displayManager?.getDisplay(inputTargetDisplayId) ?: return
        val metrics = android.util.DisplayMetrics()
        display.getRealMetrics(metrics)
        
        val mirrorWidth = (metrics.widthPixels * 0.95f).toInt()
        
        // Reset to defaults
        mirrorKeyboardParams?.x = 0
        mirrorKeyboardParams?.y = 0
        mirrorKeyboardParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        mirrorKeyboardParams?.width = mirrorWidth
        mirrorKeyboardParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        
        // Clear saved prefs
        getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .remove("mirror_x")
            .remove("mirror_y")
            .remove("mirror_width")
            .remove("mirror_height")
            .apply()
        
        try {
            mirrorWindowManager?.updateViewLayout(mirrorKeyboardContainer, mirrorKeyboardParams)
            
            // FIX: Update sync dimensions after layout
            handler.postDelayed({
                updateMirrorSyncDimensions()
            }, 100)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reset mirror keyboard layout", e)
        }
        
        Log.d(TAG, "Mirror keyboard reset to defaults")
    }
    // =================================================================================
    // END BLOCK: resetMirrorKeyboardPosition
    // =================================================================================
    fun cycleInputTarget() {
        if (displayManager == null) return; val displays = displayManager!!.displays; var nextId = -1
        for (d in displays) { if (d.displayId != currentDisplayId) { if (inputTargetDisplayId == currentDisplayId) { nextId = d.displayId; break } else if (inputTargetDisplayId == d.displayId) { continue } else { nextId = d.displayId } } }
        if (nextId == -1) { inputTargetDisplayId = currentDisplayId; targetScreenWidth = uiScreenWidth; targetScreenHeight = uiScreenHeight; removeRemoteCursor(); removeMirrorKeyboard(); cursorX = uiScreenWidth / 2f; cursorY = uiScreenHeight / 2f; cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception){}; cursorView?.visibility = View.VISIBLE; updateBorderColor(0x55FFFFFF.toInt()); showToast("Target: Local (Display $currentDisplayId)"); updateWakeLockState() }
        else { inputTargetDisplayId = nextId; updateTargetMetrics(nextId); createRemoteCursor(nextId); updateVirtualMirrorMode(); cursorX = targetScreenWidth / 2f; cursorY = targetScreenHeight / 2f; remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){}; cursorView?.visibility = View.GONE; updateBorderColor(0xFFFF00FF.toInt()); showToast("Target: Display $nextId"); updateWakeLockState() }
    }

    // =================================================================================
    // VIRTUAL DISPLAY KEEP-ALIVE: Wake Lock Management
    // SUMMARY: Acquires/releases a SCREEN_BRIGHT wake lock when targeting a remote display.
    //          This prevents the system from timing out the display during active use.
    //          Called when cycling target display or when inputTargetDisplayId changes.
    // =================================================================================
    private fun acquireDisplayWakeLock() {
        if (displayWakeLock?.isHeld == true) return // Already held
        try {
            displayWakeLock = powerManager?.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "DroidOS:VirtualDisplayKeepAlive"
            )
            displayWakeLock?.acquire(60 * 60 * 1000L) // 1 hour max, will release manually
            Log.d(TAG, "Display wake lock ACQUIRED for remote display")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to acquire display wake lock", e)
        }
    }

    private fun releaseDisplayWakeLock() {
        try {
            if (displayWakeLock?.isHeld == true) {
                displayWakeLock?.release()
                Log.d(TAG, "Display wake lock RELEASED")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release display wake lock", e)
        }
        displayWakeLock = null
    }

    private fun pingUserActivity() {
        val now = SystemClock.uptimeMillis()
        if (now - lastUserActivityPing < USER_ACTIVITY_PING_INTERVAL_MS) return
        lastUserActivityPing = now

        // Ping the power manager via shell to reset screen timeout
        Thread {
            try {
                // This simulates user activity on display 0 (main display)
                // Even when using virtual display, we want to keep the physical display awake
                shellService?.runCommand("input keyevent --longpress 0") // KEYCODE_UNKNOWN - no visible effect
            } catch (e: Exception) {
                Log.e(TAG, "Failed to ping user activity", e)
            }
        }.start()
    }

    private fun updateWakeLockState() {
        if (inputTargetDisplayId != currentDisplayId && inputTargetDisplayId >= 0) {
            // Targeting remote/virtual display - acquire wake lock
            acquireDisplayWakeLock()
        } else {
            // Targeting local display - release wake lock
            releaseDisplayWakeLock()
        }
    }
    // =================================================================================
    // END BLOCK: VIRTUAL DISPLAY KEEP-ALIVE Wake Lock Management
    // =================================================================================

    private fun createRemoteCursor(displayId: Int) { try { removeRemoteCursor(); val display = displayManager?.getDisplay(displayId) ?: return; val remoteContext = createTrackpadDisplayContext(display); remoteWindowManager = remoteContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager; remoteCursorLayout = FrameLayout(remoteContext); remoteCursorView = ImageView(remoteContext); remoteCursorView?.setImageResource(R.drawable.ic_cursor); val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; remoteCursorLayout?.addView(remoteCursorView, FrameLayout.LayoutParams(size, size)); remoteCursorParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT); remoteCursorParams.gravity = Gravity.TOP or Gravity.LEFT; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); remoteCursorParams.x = metrics.widthPixels / 2; remoteCursorParams.y = metrics.heightPixels / 2; remoteWindowManager?.addView(remoteCursorLayout, remoteCursorParams) } catch (e: Exception) { e.printStackTrace() } }
    private fun removeRemoteCursor() { try { if (remoteCursorLayout != null && remoteWindowManager != null) { remoteWindowManager?.removeView(remoteCursorLayout) } } catch (e: Exception) {}; remoteCursorLayout = null; remoteCursorView = null; remoteWindowManager = null }

    // =================================================================================
    // VIRTUAL MIRROR MODE FUNCTIONS
    // =================================================================================

    /**
     * Creates or removes the mirror keyboard based on Virtual Mirror Mode preference.
     * Called when the preference changes or when switching displays.
     */
    private fun updateVirtualMirrorMode() {
        if (prefs.prefVirtualMirrorMode && inputTargetDisplayId != currentDisplayId) {
            createMirrorKeyboard(inputTargetDisplayId)
        } else {
            removeMirrorKeyboard()
        }
    }

// =================================================================================
    // FUNCTION: createMirrorKeyboard
    // SUMMARY: Creates a transparent keyboard mirror on the remote display.
    //          Stores dimensions for coordinate scaling between physical and mirror.
    //          FIX: Container has NO background - KeyboardView's own #1A1A1A background
    //          ensures tight wrapping. Uses OnLayoutChangeListener to track actual
    //          KeyboardView dimensions for accurate touch sync.
    // =================================================================================
    private fun createMirrorKeyboard(displayId: Int) {
        try {
            removeMirrorKeyboard()

            val display = displayManager?.getDisplay(displayId) ?: return
            val mirrorContext = createTrackpadDisplayContext(display)

            mirrorWindowManager = mirrorContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            mirrorKeyboardContainer = FrameLayout(mirrorContext)
            // FIX: NO background on container - let KeyboardView's own background show
            mirrorKeyboardContainer?.setBackgroundColor(Color.TRANSPARENT)
            mirrorKeyboardContainer?.alpha = 0f // Start fully invisible


            // Create KeyboardView for the mirror
            mirrorKeyboardView = KeyboardView(mirrorContext, null, 0)
            mirrorKeyboardView?.setMirrorMode(true) // Disable internal logic
            mirrorKeyboardView?.alpha = 0f // Start fully invisible


            // Apply same scale as physical keyboard
            val scale = prefs.prefKeyScale / 100f
            mirrorKeyboardView?.setScale(scale)

            // Create SwipeTrailView for orientation trail - ORANGE
            mirrorTrailView = SwipeTrailView(mirrorContext)
            mirrorTrailView?.setTrailColor(0xFFFF9900.toInt())

            // Layout params for views - KeyboardView uses WRAP_CONTENT to size naturally
            val kbParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            val trailParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

            mirrorKeyboardContainer?.addView(mirrorKeyboardView, kbParams)
            mirrorKeyboardContainer?.addView(mirrorTrailView, trailParams)

            // Get display metrics
            val metrics = android.util.DisplayMetrics()
            display.getRealMetrics(metrics)

            // Calculate mirror keyboard size - use saved or default width
            val savedWidth = prefs.prefMirrorWidth
            val mirrorWidth = if (savedWidth != -1 && savedWidth > 0) savedWidth else (metrics.widthPixels * 0.95f).toInt()

            // Initialize with placeholder dimensions (will be updated by OnLayoutChangeListener)
            mirrorKbWidth = mirrorWidth.toFloat()
            mirrorKbHeight = 400f

            // Get physical keyboard dimensions
            physicalKbWidth = keyboardOverlay?.getKeyboardView()?.width?.toFloat() ?: 600f
            physicalKbHeight = keyboardOverlay?.getKeyboardView()?.height?.toFloat() ?: 400f

            Log.d(TAG, "Mirror KB init: ${mirrorKbWidth}x${mirrorKbHeight}, Physical KB: ${physicalKbWidth}x${physicalKbHeight}")
// Window params - use saved height or WRAP_CONTENT
            val savedHeight = prefs.prefMirrorHeight
            val mirrorHeight = if (savedHeight != -1 && savedHeight > 0) savedHeight else WindowManager.LayoutParams.WRAP_CONTENT
            
            mirrorKeyboardParams = WindowManager.LayoutParams(
                mirrorWidth,
                mirrorHeight,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
            mirrorKeyboardParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            mirrorKeyboardParams?.y = 0
            
            // Apply saved position if available
            val savedX = prefs.prefMirrorX
            val savedY = prefs.prefMirrorY
            
            if (savedX != -1) {
                mirrorKeyboardParams?.x = savedX
                mirrorKeyboardParams?.gravity = Gravity.TOP or Gravity.START
            }
            if (savedY != -1) {
                mirrorKeyboardParams?.y = savedY
            }
            
            // Apply saved alpha
            val savedAlpha = prefs.prefMirrorAlpha / 255f
            mirrorKeyboardContainer?.alpha = savedAlpha

            mirrorWindowManager?.addView(mirrorKeyboardContainer, mirrorKeyboardParams)

            // FIX: Track actual KeyboardView dimensions for accurate touch sync
            // This listener fires after layout, giving us the real measured dimensions
            mirrorKeyboardView?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateMirrorSyncDimensions()
            }

            Log.d(TAG, "Mirror keyboard created on display $displayId")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to create mirror keyboard", e)
        }
    }
    // =================================================================================
    // END BLOCK: createMirrorKeyboard
    // =================================================================================

    /**
     * Removes the mirror keyboard overlay.
     */
    private fun removeMirrorKeyboard() {
        try {
            if (mirrorKeyboardContainer != null && mirrorWindowManager != null) {
                mirrorWindowManager?.removeView(mirrorKeyboardContainer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mirrorKeyboardContainer = null
        mirrorKeyboardView = null
        mirrorTrailView = null
        mirrorKeyboardParams = null
        mirrorWindowManager = null

        // Cancel any pending orientation mode timeout
        orientationModeHandler.removeCallbacks(orientationModeTimeout)
        mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)
        isInOrientationMode = false
    }

    // =================================================================================
    // FUNCTION: updateMirrorSyncDimensions
    // SUMMARY: Updates the scaling dimensions used for touch coordinate sync.
    //          Gets actual measured dimensions from both KeyboardViews to ensure
    //          accurate mapping regardless of container sizes or aspect ratios.
    //          Should be called after any layout change on either keyboard.
    // =================================================================================
    private fun updateMirrorSyncDimensions() {
        // Get physical keyboard's actual KeyboardView dimensions
        // Note: These are the dimensions where touch events are reported
        val physicalView = keyboardOverlay?.getKeyboardView()
        if (physicalView != null && physicalView.width > 0 && physicalView.height > 0) {
            physicalKbWidth = physicalView.width.toFloat()
            physicalKbHeight = physicalView.height.toFloat()
        }
        
        // Get mirror keyboard's actual KeyboardView dimensions
        val mirrorView = mirrorKeyboardView
        if (mirrorView != null && mirrorView.width > 0 && mirrorView.height > 0) {
            mirrorKbWidth = mirrorView.width.toFloat()
            mirrorKbHeight = mirrorView.height.toFloat()
        }
        
        Log.d(TAG, "Mirror sync updated: Physical=${physicalKbWidth}x${physicalKbHeight}, Mirror=${mirrorKbWidth}x${mirrorKbHeight}")
    }
    // =================================================================================
    // END BLOCK: updateMirrorSyncDimensions
    // =================================================================================

    // =================================================================================
    // FUNCTION: syncMirrorKeyboardLayer
    // SUMMARY: Syncs keyboard layer (state) from physical to mirror keyboard.
    //          Called when layer changes (shift, symbols, etc.)
    // =================================================================================
    private fun syncMirrorKeyboardLayer(state: KeyboardView.KeyboardState) {
        mirrorKeyboardView?.setKeyboardState(state)

        // Also sync Ctrl/Alt state
        keyboardOverlay?.getCtrlAltState()?.let { (ctrl, alt) ->
            mirrorKeyboardView?.setCtrlAltState(ctrl, alt)
        }
    }
    // =================================================================================
    // END BLOCK: syncMirrorKeyboardLayer
    // =================================================================================

// =================================================================================
    // FUNCTION: onMirrorKeyboardTouch
    // SUMMARY: Virtual Mirror Mode touch handling.
    //          - Every new touch: Show mirror + orange trail
    //          - After timeout: Switch to blue trail, allow typing
    //          - Single taps (quick touch) should also type after orientation
    //          - HOLD REPEAT: If finger stays on backspace/arrow key during orange
    //            trail, the key will repeat. The orientation timeout is CANCELLED
    //            when on a repeatable key so it stays orange and keeps repeating.
    // @return true to block input, false to allow input
    // =================================================================================

    // =================================================================================
    // FUNCTION: onMirrorKeyboardTouch
    // SUMMARY: Virtual Mirror Mode touch handling.
    // OPTIMIZED: Detects Prediction Bar touches on DOWN to start drag.
    // separates Drag logic from Orientation logic to prevent lag.
    // =================================================================================

    // =================================================================================
    // FUNCTION: onMirrorKeyboardTouch
    // SUMMARY: Virtual Mirror Mode touch handling.
    // FIXED: Restored 'handleDeferredTap' so typing works.
    // ADDED: Safe 'Prediction Bar Drag' detection that bypasses Orange logic.
    // =================================================================================


    // =================================================================================
    // FUNCTION: onMirrorKeyboardTouch
    // SUMMARY: Virtual Mirror Mode touch handling.
    // UPDATED: Added 400ms delay to Blue Mode key repeats to prevent mis-swipes.
    // =================================================================================


    fun onMirrorKeyboardTouch(x: Float, y: Float, action: Int): Boolean {
        if (!isVirtualMirrorModeActive()) return false

        val scaleX = if (physicalKbWidth > 0) mirrorKbWidth / physicalKbWidth else 1f
        val scaleY = if (physicalKbHeight > 0) mirrorKbHeight / physicalKbHeight else 1f
        val mirrorX = x * scaleX
        val mirrorY = y * scaleY

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                // ALWAYS START ORANGE
                isMirrorDragActive = false
                isInOrientationMode = true
                lastOrientX = x; lastOrientY = y

                // FIX: STOP PREVIOUS ANIMATIONS IMMEDIATELY
                // Prevents transparency fighting when typing fast
                mirrorKeyboardView?.animate()?.cancel()
                mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)
                
                // Force full visibility
                mirrorKeyboardView?.alpha = 0.9f

                mirrorKeyboardContainer?.alpha = 1f

                keyboardOverlay?.setOrientationMode(true)
                mirrorTrailView?.setTrailColor(0xFFFF9900.toInt())
                keyboardOverlay?.setOrientationTrailColor(0xFFFF9900.toInt())

                mirrorTrailView?.clear()
                keyboardOverlay?.clearOrientationTrail()
                keyboardOverlay?.startOrientationTrail(x, y)
                mirrorTrailView?.addPoint(mirrorX, mirrorY)

                orientationModeHandler.removeCallbacks(orientationModeTimeout)
                orientationModeHandler.postDelayed(orientationModeTimeout, prefs.prefMirrorOrientDelayMs)

                stopMirrorKeyRepeat()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                // 1. DRAG MODE (Blue Trail + Red Backspace)
                if (isMirrorDragActive) {
                     mirrorTrailView?.setTrailColor(0xFF4488FF.toInt())
                     mirrorTrailView?.addPoint(mirrorX, mirrorY)

                     val currentKey = keyboardOverlay?.getKeyAtPosition(x, y)
                     val isBackspace = (currentKey == "BKSP" || currentKey == "⌫" || currentKey == "BACKSPACE")
                     
                     if (isBackspace) isHoveringBackspace = true // Latch

                     if (isBackspace) {
                         mirrorKeyboardView?.highlightKey("BKSP", true, Color.RED)
                     } else {
                         mirrorKeyboardView?.highlightKey("BKSP", false, 0)
                     }

                     val now = SystemClock.uptimeMillis()
                     val event = MotionEvent.obtain(now, now, MotionEvent.ACTION_MOVE, mirrorX, mirrorY, 0)
                     mirrorKeyboardView?.dispatchTouchEvent(event)
                     event.recycle()
                     return true
                }

                // 2. ORANGE MODE
                if (isInOrientationMode) {
                    val dx = x - lastOrientX; val dy = y - lastOrientY
                    if (kotlin.math.sqrt(dx*dx + dy*dy) > MOVEMENT_THRESHOLD) {
                        lastOrientX = x; lastOrientY = y
                        orientationModeHandler.removeCallbacks(orientationModeTimeout)
                        orientationModeHandler.postDelayed(orientationModeTimeout, prefs.prefMirrorOrientDelayMs)
                    }
                    keyboardOverlay?.addOrientationTrailPoint(x, y)
                    mirrorTrailView?.addPoint(mirrorX, mirrorY)
                    return true
                } 
                
                // 3. BLUE MODE
                else {
                    mirrorTrailView?.addPoint(mirrorX, mirrorY)
                    val currentKey = keyboardOverlay?.getKeyAtPosition(x, y)
                    
                    if (currentKey != null && mirrorRepeatableKeys.contains(currentKey)) {
                         if (mirrorRepeatKey == currentKey) return false
                         stopMirrorKeyRepeat()
                         mirrorRepeatKey = currentKey
                         
                         mirrorRepeatHandler.postDelayed({
                             if (mirrorRepeatKey == currentKey && !isInOrientationMode) {
                                 isMirrorRepeating = true
                                 keyboardOverlay?.triggerKeyPress(currentKey)
                                 mirrorRepeatHandler.postDelayed(mirrorRepeatRunnable, MIRROR_REPEAT_INTERVAL)
                             }
                         }, 150) 
                    } else {
                        stopMirrorKeyRepeat()
                    }
                    return false
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 1. END DRAG (Robust Delete)
                if (isMirrorDragActive) {
                     mirrorKeyboardView?.highlightKey("BKSP", false, 0)

                     val upKey = keyboardOverlay?.getKeyAtPosition(x, y)
                     val isDroppedOnBksp = (upKey == "BKSP" || upKey == "⌫" || upKey == "BACKSPACE")
                     

                     if (isHoveringBackspace || isDroppedOnBksp) {
                         // 1. Update Main Logic
                         keyboardOverlay?.blockPrediction(draggedPredictionIndex)
                         
                         // 2. Update Mirror UI Instantly (Make word disappear)
                         mirrorKeyboardView?.removeCandidateAtIndex(draggedPredictionIndex)
                         
                         showToast("Blocked Prediction")
                     } else {

                         val now = SystemClock.uptimeMillis()
                         val event = MotionEvent.obtain(now, now, action, mirrorX, mirrorY, 0)
                         mirrorKeyboardView?.dispatchTouchEvent(event)
                         event.recycle()
                     }
                     
                     mirrorTrailView?.clear()
                     isMirrorDragActive = false
                     isHoveringBackspace = false
                     return true
                }

                // 2. END TOUCH
                orientationModeHandler.removeCallbacks(orientationModeTimeout)
                val wasRepeating = isMirrorRepeating
                stopMirrorKeyRepeat()

                if (isInOrientationMode) {
                    isInOrientationMode = false
                    keyboardOverlay?.setOrientationMode(false)
                    mirrorTrailView?.clear()
                    keyboardOverlay?.clearOrientationTrail()

                    if (!wasRepeating) {
                        keyboardOverlay?.handleDeferredTap(x, y)
                    }
                } else {
                    mirrorTrailView?.clear()
                }

                // Smoothly fade to dim state
                mirrorKeyboardView?.animate()?.alpha(0.3f)?.setDuration(200)?.start()
                mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)
                mirrorFadeHandler.postDelayed(mirrorFadeRunnable, 2000)


                return false
            }
        }
        return isInOrientationMode
    }






    // =================================================================================
    // END BLOCK: onMirrorKeyboardTouch
    // =================================================================================

    // =================================================================================
    // FUNCTION: stopMirrorKeyRepeat
    // SUMMARY: Stops any active mirror mode key repeat. Called on touch up or when
    //          finger moves off the repeatable key.
    // =================================================================================
    private fun stopMirrorKeyRepeat() {
        mirrorRepeatHandler.removeCallbacks(mirrorRepeatRunnable)
        mirrorRepeatKey = null
        isMirrorRepeating = false
    }
    // =================================================================================
    // END BLOCK: stopMirrorKeyRepeat
    // =================================================================================    // =================================================================================
    // FUNCTION: clearMirrorTrail
    // SUMMARY: Clears the orange orientation trail from the mirror keyboard display.
    // =================================================================================
    private fun clearMirrorTrail() {
        mirrorTrailView?.clear()
    }
    // =================================================================================
    // END BLOCK: clearMirrorTrail
    // =================================================================================

    /**
     * Returns true if Virtual Mirror Mode is currently active.
     */
    private fun isVirtualMirrorModeActive(): Boolean {
        return prefs.prefVirtualMirrorMode &&
               inputTargetDisplayId != currentDisplayId &&
               mirrorKeyboardView != null
    }

    /**
     * Returns true if currently in orientation mode (showing orange trail, blocking input).
     */
    fun isCurrentlyInOrientationMode(): Boolean {
        return isInOrientationMode
    }

    // =================================================================================
    // FUNCTION: toggleVirtualMirrorMode
    // SUMMARY: Enhanced toggle that automatically:
    //          - When ON: Saves current state, switches to virtual display, shows
    //            keyboard and trackpad, loads mirror mode profile
    //          - When OFF: Saves mirror mode profile, restores previous visibility
    //            state, switches back to local display, loads normal profile
    // =================================================================================


    fun toggleVirtualMirrorMode() {
        // 1. Save CURRENT state (before switching)
        // This correctly saves to the OLD profile (VM or STD) before we flip the switch.
        saveLayout() 

        val wasEnabled = prefs.prefVirtualMirrorMode
        prefs.prefVirtualMirrorMode = !wasEnabled

        if (prefs.prefVirtualMirrorMode) {
            // === ENTERING MIRROR MODE ===
            android.util.Log.d(TAG, "Entering Virtual Mirror Mode")

            // Store state for smart-toggle
            preMirrorTrackpadVisible = isTrackpadVisible
            preMirrorKeyboardVisible = isCustomKeyboardVisible
            preMirrorTargetDisplayId = inputTargetDisplayId

            // Switch to virtual display
            val displays = displayManager?.displays ?: emptyArray()
            var targetDisplay: Display? = null
            for (d in displays) { if (d.displayId != currentDisplayId && d.displayId >= 2) { targetDisplay = d; break } }
            if (targetDisplay == null) { for (d in displays) { if (d.displayId != currentDisplayId) { targetDisplay = d; break } } }

            if (targetDisplay != null) {
                inputTargetDisplayId = targetDisplay.displayId
                updateTargetMetrics(inputTargetDisplayId)
                createRemoteCursor(inputTargetDisplayId)
                cursorX = targetScreenWidth / 2f; cursorY = targetScreenHeight / 2f
                remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt()
                try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {}
                cursorView?.visibility = View.GONE
                updateBorderColor(0xFFFF00FF.toInt())

                // Ensure visibility
                if (!isTrackpadVisible) toggleTrackpad()
                if (!isCustomKeyboardVisible) toggleCustomKeyboard(suppressAutomation = true)

                // 2. Load MIRROR Profile (Pref is now true, so this loads VM profile)
                loadLayout() 
                updateVirtualMirrorMode()

                showToast("Mirror Mode ON")
                
                val intentCycle = Intent("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
                intentCycle.setPackage("com.katsuyamaki.DroidOSLauncher")
                sendBroadcast(intentCycle)
            } else {
                prefs.prefVirtualMirrorMode = false
                showToast("No virtual display found.")
            }

        } else {
            // === EXITING MIRROR MODE ===
            android.util.Log.d(TAG, "Exiting Virtual Mirror Mode")

            // [FIX] REMOVED the redundant saveLayout() here.
            // It was overwriting the Standard Profile with the active VM layout 
            // because prefVirtualMirrorMode was already set to false above.

            removeMirrorKeyboard()
            inputTargetDisplayId = currentDisplayId
            targetScreenWidth = uiScreenWidth
            targetScreenHeight = uiScreenHeight
            removeRemoteCursor()
            cursorX = uiScreenWidth / 2f; cursorY = uiScreenHeight / 2f
            cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt()
            try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {}
            cursorView?.visibility = View.VISIBLE
            updateBorderColor(0x55FFFFFF.toInt())
            
            // 3. Load STANDARD Profile (Pref is now false, so this loads STD profile)
            loadLayout() 

            showToast("Mirror Mode OFF")
            
            val intentCycle = Intent("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
            intentCycle.setPackage("com.katsuyamaki.DroidOSLauncher")
            sendBroadcast(intentCycle)
        }
        
        savePrefs()
    }


    // =================================================================================
    // END BLOCK: FUNCTION toggleVirtualMirrorMode
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE FUNCTIONS
    // =================================================================================

    private fun startTouchDrag() { if (ignoreTouchSequence || isTouchDragging) return; isTouchDragging = true; activeDragButton = MotionEvent.BUTTON_PRIMARY; dragDownTime = SystemClock.uptimeMillis(); injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, dragDownTime); hasSentTouchDown = true; if (prefs.prefVibrate) vibrate(); updateBorderColor(0xFFFF9900.toInt()) }
    private fun startResize() {}
    private fun startMove() {}
    private fun startKeyDrag(button: Int) { vibrate(); updateBorderColor(0xFF00FF00.toInt()); isKeyDragging = true; activeDragButton = button; dragDownTime = SystemClock.uptimeMillis(); injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime); hasSentMouseDown = true }
    private fun stopKeyDrag(button: Int) { if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()); isKeyDragging = false; if (hasSentMouseDown) { injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime) }; hasSentMouseDown = false }
    private fun handleManualAdjust(intent: Intent) {
        val target = intent.getStringExtra("TARGET") ?: "TRACKPAD"
        val isResize = intent.hasExtra("DW")
        val dx = intent.getIntExtra(if(isResize) "DW" else "DX", 0)
        val dy = intent.getIntExtra(if(isResize) "DH" else "DY", 0)
        manualAdjust(target == "KEYBOARD", isResize, dx, dy)
    }
    private fun handlePreview(intent: Intent) {
        val target = intent.getStringExtra("TARGET") ?: return
        val value = intent.getIntExtra("VALUE", 0)
        when(target) {
            "cursor_size" -> { prefs.prefCursorSize = value; updateCursorSize() }
            "alpha" -> { prefs.prefAlpha = value; updateBorderColor(currentBorderColor) }
            "handle_size" -> { prefs.prefHandleSize = value; updateHandleSize() }
            "scroll_visual" -> { prefs.prefScrollVisualSize = value; updateScrollSize() }
            "handle_touch" -> { prefs.prefHandleTouchSize = value; updateLayoutSizes() }
            "scroll_touch" -> { prefs.prefScrollTouchSize = value; updateScrollSize() }
            "keyboard_scale" -> { prefs.prefKeyScale = value; keyboardOverlay?.updateScale(value / 100f) }
        }
    }
    private fun setTrackpadVisibility(visible: Boolean) { isTrackpadVisible = visible; if (trackpadLayout != null) trackpadLayout?.visibility = if (visible) View.VISIBLE else View.GONE }
    private fun setPreviewMode(preview: Boolean) { isPreviewMode = preview; trackpadLayout?.alpha = if (preview) 0.5f else 1.0f }
    override fun onDisplayAdded(displayId: Int) {}
    override fun onDisplayRemoved(displayId: Int) {}
    override fun onDisplayChanged(displayId: Int) {
        // =================================================================================
        // VIRTUAL DISPLAY PROTECTION
        // SUMMARY: Skip auto-switch logic when targeting a virtual display (ID >= 2).
        //          This prevents the overlay from "crashing" back to physical screens
        //          when display states flicker during virtual display use.
        // =================================================================================
        if (inputTargetDisplayId >= 2) {
            Log.d(TAG, "onDisplayChanged: Ignoring - targeting virtual display $inputTargetDisplayId")
            return
        }
        // =================================================================================
        // END BLOCK: VIRTUAL DISPLAY PROTECTION
        // =================================================================================

        // We only monitor the Main Screen (0) state changes to determine "Open/Closed"
        if (displayId == 0) {
            val display = displayManager?.getDisplay(0)
            val isDebounced = (System.currentTimeMillis() - lastManualSwitchTime > 5000)
            
            if (display != null && isDebounced) {
                // CASE A: Phone Opened (Display 0 turned ON) -> Move to Main (0)
                if (display.state == Display.STATE_ON && currentDisplayId != 0) {
                    handler.postDelayed({
                        try {
                            if (System.currentTimeMillis() - lastManualSwitchTime > 5000) {
                                setupUI(0)
                                resetBubblePosition()
                                // showToast("Phone Opened: Moved to Main Screen") // Removed debug toast
                            }
                        } catch(e: Exception) {}
                    }, 500)
                }

                // CASE B: Phone Closed (Display 0 turned OFF/DOZE) -> Move to Cover (1)
                else if (display.state != Display.STATE_ON && currentDisplayId == 0) {
                    handler.postDelayed({
                        try {
                            // Double-check state (ensure it didn't just flicker)
                            val d0 = displayManager?.getDisplay(0)
                            if (d0?.state != Display.STATE_ON && 
                                System.currentTimeMillis() - lastManualSwitchTime > 5000) {
                                
                                // [FIX] Only switch if Display 1 actually exists!
                                // This prevents the UI from disappearing on single-screen devices (Beam Pro)
                                // where setupUI(1) would remove the views but fail to re-add them.
                                if (displayManager?.getDisplay(1) != null) {
                                    setupUI(1)
                                    // We don't reset bubble pos here to avoid it jumping if you just locked the screen
                                    // But we do ensure menu is hidden if it was open
                                    menuManager?.hide()
                                }
                            }
                        } catch(e: Exception) {}
                    }, 500)
                }

            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(commandReceiver)
        } catch (e: Exception) {
            // Ignore if not registered
        }

        // VIRTUAL DISPLAY KEEP-ALIVE: Release wake lock on destroy
        releaseDisplayWakeLock()
        // =================================================================================
        // END BLOCK: VIRTUAL DISPLAY KEEP-ALIVE onDestroy cleanup
        // =================================================================================
        inputExecutor.shutdownNow() // Stop the worker thread
        if (Build.VERSION.SDK_INT >= 24) { try { softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO } catch (e: Exception) {} }
        Thread { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") }.start()
        try { unregisterReceiver(switchReceiver) } catch(e: Exception){};
        if (isBound) ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection)
    }

    fun forceSystemKeyboardVisible() {
        Thread {
            try {
                // Force setting to 1 (Show)
                shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1")
            } catch(e: Exception) {}
        }.start()
    }

    private fun showToast(msg: String) { handler.post { android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show() } }
    private fun updateTargetMetrics(displayId: Int) { val display = displayManager?.getDisplay(displayId) ?: return; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); targetScreenWidth = metrics.widthPixels; targetScreenHeight = metrics.heightPixels }
    


    fun injectKeyFromKeyboard(keyCode: Int, metaState: Int) {
        // Update timestamp so we ignore the resulting AccessibilityEvent
        lastInjectionTime = System.currentTimeMillis()

        // NEW: dismiss Voice if active
        checkAndDismissVoice()
        // Submit to the sequential queue instead of spinning a random thread

        inputExecutor.execute {
            try {
                // 1. CHECK ACTUAL SYSTEM STATE
                val currentIme = android.provider.Settings.Secure.getString(contentResolver, "default_input_method") ?: ""
                val isNullKeyboardActive = currentIme.contains(packageName) && currentIme.contains("NullInputMethodService")

                if (isNullKeyboardActive) {
                    // STRATEGY A: NATIVE (Cleanest)
                    val intent = Intent("com.example.coverscreentester.INJECT_KEY")
                    intent.setPackage(packageName)
                    intent.putExtra("keyCode", keyCode)
                    intent.putExtra("metaState", metaState)
                    sendBroadcast(intent)
                } else {
                    // STRATEGY B: SHELL INJECTION (Fallback)
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_DOWN, metaState, inputTargetDisplayId, 1)
                    // Small delay only needed for shell to ensure UP registers after DOWN
                    Thread.sleep(10)
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_UP, metaState, inputTargetDisplayId, 1)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Key injection failed", e)
            }
        }
    }


    fun injectBulkDelete(length: Int) {
        if (length <= 0) return
        
        // Update timestamp
        lastInjectionTime = System.currentTimeMillis()

        inputExecutor.execute {
            try {
                // 1. CHECK ACTUAL SYSTEM STATE
                val currentIme = android.provider.Settings.Secure.getString(contentResolver, "default_input_method") ?: ""
                val isNullKeyboardActive = currentIme.contains(packageName) && currentIme.contains("NullInputMethodService")

                if (isNullKeyboardActive) {
                    // STRATEGY A: NATIVE (Clean & Fast)
                    val intent = Intent("com.example.coverscreentester.INJECT_DELETE")
                    intent.setPackage(packageName)
                    intent.putExtra("length", length)
                    sendBroadcast(intent)
                } else {
                    // STRATEGY B: SHELL INJECTION (Fallback Loop)
                    // Shell is slower, but robust enough if we aren't using the Broadcast method
                    for (i in 0 until length) {
                        shellService?.injectKey(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_DOWN, 0, inputTargetDisplayId, 1)
                        Thread.sleep(5) // Tiny delay for stability
                        shellService?.injectKey(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_UP, 0, inputTargetDisplayId, 1)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Bulk delete failed", e)
            }
        }
    }

    fun injectText(text: String) {
        // Update timestamp so we ignore the resulting AccessibilityEvent
        lastInjectionTime = System.currentTimeMillis()

        inputExecutor.execute {
            try {

                // 1. CHECK ACTUAL SYSTEM STATE
                val currentIme = android.provider.Settings.Secure.getString(contentResolver, "default_input_method") ?: ""
                val isNullKeyboardActive = currentIme.contains(packageName) && currentIme.contains("NullInputMethodService")

                if (isNullKeyboardActive) {
                    // STRATEGY A: NATIVE (Cleanest)
                    val intent = Intent("com.example.coverscreentester.INJECT_TEXT")
                    intent.setPackage(packageName)
                    intent.putExtra("text", text)
                    sendBroadcast(intent)
                } else {
                    // STRATEGY B: SHELL INJECTION (Fallback)
                    val escapedText = text.replace("\"", "\\\"")
                    val cmd = "input -d $inputTargetDisplayId text \"$escapedText\""
                    shellService?.runCommand(cmd)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Text injection failed", e)
            }
        }
    }

    fun switchDisplay() {
        // ACTION: Smart Toggle
        // If we are on Main (0) or Cover (1), we assume we want to go to Virtual.
        if (currentDisplayId == 0 || currentDisplayId == 1) {
            
            // 1. Broadcast: Create Virtual Display
            val intentToggle = Intent("com.katsuyamaki.DroidOSLauncher.TOGGLE_VIRTUAL_DISPLAY")
            intentToggle.setPackage("com.katsuyamaki.DroidOSLauncher")
            sendBroadcast(intentToggle)

            showToast("Initializing Virtual Display...")

            // 2. Wait for display creation, then Cycle
            handler.postDelayed({
                 val intentCycle = Intent("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
                 intentCycle.setPackage("com.katsuyamaki.DroidOSLauncher")
                 sendBroadcast(intentCycle)
            }, 1000) // Increased wait time to 1s to be safe
            
        } else {
            // If we are already on Virtual (or other), just cycle back to Main
            val intentCycle = Intent("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
            intentCycle.setPackage("com.katsuyamaki.DroidOSLauncher")
            sendBroadcast(intentCycle)
        }
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/PredictionEngine.kt
```kotlin
package com.example.coverscreentester

import android.content.Context
import android.graphics.PointF
import java.util.ArrayList
import java.util.Locale
import kotlin.math.hypot
import kotlin.math.sqrt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.ln
import kotlin.math.abs

// =================================================================================
// DATA CLASS: TimedPoint
// SUMMARY: A point with timestamp for dwell time analysis in swipe gestures.
//          Allows the prediction engine to detect when users linger on keys
//          to disambiguate similar words like "for" vs "four".
// =================================================================================
data class TimedPoint(
    val x: Float,
    val y: Float,
    val timestamp: Long  // System.currentTimeMillis() when this point was captured
) {
    fun toPointF(): android.graphics.PointF = android.graphics.PointF(x, y)
}
// =================================================================================
// END BLOCK: TimedPoint data class
// =================================================================================

/**
 * =================================================================================
 * CLASS: PredictionEngine
 * SUMMARY: Handles predictive text suggestions and swipe-to-type gesture decoding.
 *          Implements a SHARK2-inspired dual-channel algorithm with:
 *          1. Uniform path sampling (N points)
 *          2. Template generation for dictionary words
 *          3. Shape channel (scale-normalized pattern matching)
 *          4. Location channel (absolute position matching)
 *          5. Integration scoring with word frequency weighting
 *          
 * Based on: SHARK2 algorithm by Kristensson & Zhai (2004)
 * Reference: http://pokristensson.com/pubs/KristenssonZhaiUIST2004.pdf
 * =================================================================================
 */
class PredictionEngine {

    companion object {
        val instance = PredictionEngine()

        // Tuning Parameters
        private const val SAMPLE_POINTS = 64
        private const val NORMALIZATION_SIZE = 100f
        private const val SEARCH_RADIUS = 70f
        
        // Weights: 
        // Shape: 0.25 -> kept low to allow messy sizing
        // Location: 0.85 -> high trust in key hits
        // Direction: 0.5 -> kept low
        // Turn: 1.5 -> SIGNIFICANTLY INCREASED (was 0.9). Sharp corners are now king.
        // Dwell: 0.6 -> Weight for keys where user lingered longer
        private const val SHAPE_WEIGHT = 0.25f
        private const val DWELL_TIME_WEIGHT = 0.6f  // NEW: Weight for time-based key scoring
        private const val DWELL_THRESHOLD_MS = 80L  // Minimum ms to count as "lingering" on a key
        private const val LOCATION_WEIGHT = 0.85f
        private const val DIRECTION_WEIGHT = 0.5f   
        private const val TURN_WEIGHT = 1.5f        
        
        // Files
        private const val USER_STATS_FILE = "user_stats.json"
        private const val BLOCKED_DICT_FILE = "blocked_words.txt"
        private const val USER_DICT_FILE = "user_words.txt"
        private const val MIN_WORD_LENGTH = 2
    }

    // =================================================================================
    // END BLOCK: TUNING PARAMETERS
    // =================================================================================






    // ... (TrieNode class remains the same) ...

    // UPDATE: Add directionVectors to cache the flow of the word
    data class WordTemplate(
        val word: String,
        val rank: Int,
        val rawPoints: List<PointF>,
        var sampledPoints: List<PointF>? = null,
        var normalizedPoints: List<PointF>? = null,
        var directionVectors: List<PointF>? = null // NEW FIELD
    )




    // =================================================================================
    // DATA STRUCTURES
    // =================================================================================
    
    class TrieNode {
        val children = HashMap<Char, TrieNode>()
        var isEndOfWord = false
        var word: String? = null
        var rank: Int = Int.MAX_VALUE // 0 = Most Frequent
    }

    private val root = TrieNode()
    private val wordList = ArrayList<String>()



// --- USER STATS ---
    private val USER_STATS_FILE = "user_stats.json"
    private val userFrequencyMap = HashMap<String, Int>()
    
    // =================================================================================
    // OPTIMIZATION: Pre-indexed word lookup by first and last letter
    // SUMMARY: Instead of filtering all 10k words, we lookup by first letter then
    //          filter by last letter. This reduces candidate pool by ~96% immediately.
    // =================================================================================
    private val wordsByFirstLetter = HashMap<Char, ArrayList<String>>()
    private val wordsByFirstLastLetter = HashMap<String, ArrayList<String>>()
    // =================================================================================
    // END BLOCK: Pre-indexed word lookup
    // =================================================================================
    
    // Template cache - maps word to its template (lazy-computed per keyboard layout)
    private val templateCache = HashMap<String, WordTemplate>()
    private var lastKeyMapHash = 0  // Track if keyboard layout changed

    // --- CUSTOM DICTIONARY STORAGE ---
    private val blockedWords = java.util.HashSet<String>()
    private val customWords = java.util.HashSet<String>()
    // =================================================================================
    // CUSTOM WORD DISPLAY FORMS
    // SUMMARY: Maps lowercase lookup key to the display form with proper capitalization.
    //          E.g., "droidos" -> "DroidOS", "iphone" -> "iPhone"
    //          This allows swiping "droidos" to output "DroidOS".
    // =================================================================================
    private val customWordDisplayForms = HashMap<String, String>()
    // =================================================================================
    // END BLOCK: Custom word display forms
    // =================================================================================
    // Cache for the top 1000 words to make "Hail Mary" pass instant
    private val commonWordsCache = ArrayList<String>()
    // Throttle template failure logging
    private var lastTemplateMissLog = 0L
    
    // =================================================================================
    // END DATA STRUCTURES
    // =================================================================================



    init {
        loadDefaults()
    }



    private fun loadDefaults() {
        val commonWords = listOf(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
            "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
            "this", "but", "his", "by", "from", "they", "we", "say", "her",
            "she", "or", "an", "will", "my", "one", "all", "would", "there",
            "their", "what", "so", "up", "out", "if", "about", "who", "get",
            "which", "go", "me", "when", "make", "can", "like", "time", "no",
            "just", "him", "know", "take", "people", "into", "year", "your",
            "good", "some", "could", "them", "see", "other", "than", "then",
            "now", "look", "only", "come", "its", "over", "think", "also",
            "back", "after", "use", "two", "how", "our", "work", "first",
            "well", "way", "even", "new", "want", "because", "any", "these",
            "give", "day", "most", "us", "is", "was", "are", "been", "has",
            "more", "or", "had", "did", "said", "each", "she", "may", "find",
            "long", "down", "did", "get", "made", "live", "back", "little",
            "only", "round", "man", "year", "came", "show", "every", "good",
            "great", "help", "through", "much", "before", "line", "right", 
            "too", "old", "mean", "same", "tell", "boy", "follow", "very",
            "just", "why", "ask", "went", "men", "read", "need", "land",
            "here", "home", "big", "high", "such", "again", "turn", "hand",
            "play", "small", "end", "put", "while", "next", "sound", "below",
            // Common mobile/tech words
            "swipe", "keyboard", "trackpad", "android", "phone", "text", "type",
            "hello", "yes", "no", "ok", "okay", "thanks", "please", "sorry",
            "love", "like", "cool", "nice", "awesome", "great", "good", "bad"
        )
        for ((index, word) in commonWords.withIndex()) {
            insert(word, index)
        }
    }


// =================================================================================
    // USER STATS & PRIORITY LOGIC
    // =================================================================================
    
    private fun loadUserStats(context: Context) {
        try {
            val file = java.io.File(context.filesDir, USER_STATS_FILE)
            if (file.exists()) {
                val content = file.readText()
                // Simple parsing: "word":count
                content.replace("{", "").replace("}", "").split(",").forEach {
                    val parts = it.split(":")
                    if (parts.size == 2) {
                        val w = parts[0].trim().replace("\"", "")
                        val c = parts[1].trim().toIntOrNull() ?: 0
                        userFrequencyMap[w] = c
                    }
                }
                android.util.Log.d("DroidOS_Prediction", "Loaded stats for ${userFrequencyMap.size} words")
            }
        } catch (e: Exception) {
            android.util.Log.e("DroidOS_Prediction", "Failed to load user stats", e)
        }
    }

    private fun saveUserStats(context: Context) {
        Thread {
            try {
                val sb = StringBuilder("{")
                synchronized(userFrequencyMap) {
                    var first = true
                    for ((k, v) in userFrequencyMap) {
                        if (!first) sb.append(",")
                        sb.append("\"$k\":$v")
                        first = false
                    }
                }
                sb.append("}")
                val file = java.io.File(context.filesDir, USER_STATS_FILE)
                file.writeText(sb.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    /**
     * Call this when the user clicks a word in the suggestion bar.
     * Boosts the word's priority for future predictions.
     */

/**
     * Call this when the user clicks a word in the suggestion bar.
     * Boosts the word's priority for future predictions.
     * NOTE: This no longer auto-learns new words. Use learnWord() for that.
     */
    fun recordSelection(context: Context, word: String) {
        if (word.isBlank()) return
        val clean = word.lowercase(Locale.ROOT)
        
        synchronized(userFrequencyMap) {
            val count = userFrequencyMap.getOrDefault(clean, 0)
            userFrequencyMap[clean] = count + 1
        }
        
        // FIX: Removed auto-learning. Typos won't be added automatically.
        // The UI must call learnWord() explicitly when the user selects a "New Word".
        // if (!hasWord(clean)) {
        //    learnWord(context, clean)
        // }
        
        saveUserStats(context)
    }


    fun loadDictionary(context: Context) {
        Thread {
            try {
                loadUserStats(context)
                val start = System.currentTimeMillis()
                val newRoot = TrieNode()
                val newWordList = ArrayList<String>()
                val newBlocked = java.util.HashSet<String>()
                val newCustom = java.util.HashSet<String>()
                var lineCount = 0


                val newWordsByFirstLetter = HashMap<Char, ArrayList<String>>()
                val newWordsByFirstLastLetter = HashMap<String, ArrayList<String>>()
                // FIX: Declare here so it is accessible in the commit block
                val newDisplayForms = HashMap<String, String>()


                // =================================================================================
                // LOAD CUSTOM LISTS (User & Blocked)
                // SUMMARY: Loads user's custom words and blocked words from persistent storage.
                // =================================================================================
                try {
                    val blockFile = java.io.File(context.filesDir, BLOCKED_DICT_FILE)
                    if (blockFile.exists()) {
                        val blockedLines = blockFile.readLines().map { it.trim().lowercase(java.util.Locale.ROOT) }.filter { it.isNotEmpty() }
                        newBlocked.addAll(blockedLines)
                        android.util.Log.d("DroidOS_Prediction", "LOAD: Blocked words file found, ${blockedLines.size} words")
                    } else {
                        android.util.Log.d("DroidOS_Prediction", "LOAD: No blocked words file exists yet")
                    }

                    val userFile = java.io.File(context.filesDir, USER_DICT_FILE)
                    if (userFile.exists()) {
                        val userLines = userFile.readLines().map { it.trim() }.filter { it.isNotEmpty() }
                        
                        // FILTER: Check each user word against garbage filter on load
                        // Also build display forms map
                        for (originalForm in userLines) {
                            val lookupForm = originalForm.lowercase(java.util.Locale.ROOT)
                            val trieKey = lookupForm.replace("'", "")
                            
                            if (!looksLikeGarbage(trieKey)) {
                                newCustom.add(lookupForm)
                                // Store display form mapping
                                newDisplayForms[lookupForm] = originalForm
                                newDisplayForms[trieKey] = originalForm
                            } else {
                                android.util.Log.d("DroidOS_Prediction", "Pruned garbage from user dict: $originalForm")
                            }
                        }
                        android.util.Log.d("DroidOS_Prediction", "LOAD: User words file found, ${newCustom.size} valid words")
                    } else {
                        android.util.Log.d("DroidOS_Prediction", "LOAD: No user words file exists yet")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DroidOS_Prediction", "Failed to load user lists", e)
                }

                // 2. Load Main Dictionary (Assets) - Filtering Blocked words
                try {
                    context.assets.open("dictionary.txt").bufferedReader().useLines { lines ->
                        lines.forEachIndexed { index, line ->
                            val word = line.trim().lowercase(java.util.Locale.ROOT)
                            // SKIP if blocked
                            if (!newBlocked.contains(word) && word.isNotEmpty() && word.all { it.isLetter() } && word.length >= MIN_WORD_LENGTH) {
                                newWordList.add(word)
                                lineCount++

                                var current = newRoot
                                for (char in word) {
                                    current = current.children.computeIfAbsent(char) { TrieNode() }
                                }
                                current.isEndOfWord = true
                                current.word = word
                                if (index < current.rank) current.rank = index

                                if (word.length >= 2) {
                                    val firstChar = word.first()
                                    newWordsByFirstLetter.getOrPut(firstChar) { ArrayList() }.add(word)
                                    val key = "${word.first()}${word.last()}"
                                    newWordsByFirstLastLetter.getOrPut(key) { ArrayList() }.add(word)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DroidOS_Prediction", "Dictionary asset load failed: ${e.message}")
                }

                // 3. Merge Custom Words
                for (word in newCustom) {
                    if (!newWordList.contains(word)) {
                        newWordList.add(word)
                        var current = newRoot
                        for (char in word) {
                            current = current.children.computeIfAbsent(char) { TrieNode() }
                        }
                        current.isEndOfWord = true
                        current.word = word
                        current.rank = 0 // High priority

                        if (word.length >= 2) {
                            val firstChar = word.first()
                            newWordsByFirstLetter.getOrPut(firstChar) { ArrayList() }.add(word)
                            val key = "${word.first()}${word.last()}"
                            newWordsByFirstLastLetter.getOrPut(key) { ArrayList() }.add(word)
                        }
                    }
                }

                // 4. Merge Hardcoded Defaults
                val existingDefaults = synchronized(this) { ArrayList(wordList) }
                for (defaultWord in existingDefaults) {
                    val lower = defaultWord.lowercase(java.util.Locale.ROOT)
                    if (!newBlocked.contains(lower) && !newWordList.contains(lower)) {
                        newWordList.add(lower)
                        var current = newRoot
                        for (char in lower) {
                            current = current.children.computeIfAbsent(char) { TrieNode() }
                        }
                        current.isEndOfWord = true
                        current.word = lower
                        if (current.rank > 100) current.rank = 50
                    }
                }

                // 5. Commit Changes
                synchronized(this) {
                    wordList.clear()
                    wordList.addAll(newWordList)
                    root.children.clear()
                    root.children.putAll(newRoot.children)

                    wordsByFirstLetter.clear()
                    wordsByFirstLetter.putAll(newWordsByFirstLetter)
                    wordsByFirstLastLetter.clear()
                    wordsByFirstLastLetter.putAll(newWordsByFirstLastLetter)

                    blockedWords.clear()
                    blockedWords.addAll(newBlocked)
                    customWords.clear()
                    customWords.addAll(newCustom)
                    
                    // Load display forms
                    customWordDisplayForms.clear()
                    customWordDisplayForms.putAll(newDisplayForms)
                    customWords.addAll(newCustom)

                    templateCache.clear()

                    // Populate Common Words Cache (Top 1000)
                    commonWordsCache.clear()
                    commonWordsCache.addAll(
                        wordList.sortedBy { getWordRank(it) }.take(1000)
                    )
                }
                // =================================================================================
                // OPTIMIZATION: Pre-warm template cache for common words
                // SUMMARY: Pre-compute templates for top 500 words to eliminate lag on first swipe.
                //          This runs in background so doesn't block UI.
                // =================================================================================
                // Note: Templates require keyMap which we don't have here.
                // Templates will be created on first use, but the word indexes are ready.
                // =================================================================================
                android.util.Log.d("DroidOS_Prediction", "Dictionary Loaded: $lineCount asset + ${newCustom.size} user words + ${newBlocked.size} blocked. Common Cache: ${commonWordsCache.size}")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }




// =================================================================================
    // FUNCTION: learnWord
    // SUMMARY: Learns a new word and saves to user_words.txt
    //          
    // SMART CAPITALIZATION:
    //   - If at sentence start: strip first-letter cap, keep internal caps
    //     E.g., "DroidOS" at start → stored as "droidOS" 
    //   - If mid-sentence: keep all caps as typed
    //     E.g., "DroidOS" mid-sentence → stored as "DroidOS"
    //   - When suggesting, always use the stored display form
    //
    // @param isSentenceStart: true if word was typed at beginning of sentence
    // =================================================================================
    fun learnWord(context: Context, word: String, isSentenceStart: Boolean = false) {
        if (word.length < 2) return

        val originalWord = word.trim()
        
        // =======================================================================
        // SMART CAPITALIZATION
        // If at sentence start, the first letter being capital is automatic,
        // so we should store without that automatic cap to get the "true" form.
        // E.g., User types "DroidOS" at start → they want "droidOS" normally
        // But if typed mid-sentence as "DroidOS" → they want "DroidOS" always
        // =======================================================================
        val displayForm = if (isSentenceStart && originalWord.isNotEmpty() && originalWord[0].isUpperCase()) {
            // Strip the automatic sentence-start capitalization
            originalWord[0].lowercaseChar() + originalWord.substring(1)
        } else {
            // Keep as-is (mid-sentence or already lowercase)
            originalWord
        }
        // =======================================================================
        // END BLOCK: Smart capitalization
        // =======================================================================
        
        val lookupWord = displayForm.lowercase(java.util.Locale.ROOT)
        val trieKey = lookupWord.replace("'", "")  // For trie lookup

        // Don't learn garbage
        if (looksLikeGarbage(trieKey)) {
             android.util.Log.d("DroidOS_Prediction", "Ignored garbage input: $originalWord")
             return
        }

        // Don't relearn if already known (but allow updating display form)
        val alreadyKnown = hasWord(trieKey) || hasWord(lookupWord)
        
        Thread {
            try {
                synchronized(this) {
                    customWords.add(lookupWord)
                    blockedWords.remove(lookupWord)
                    blockedWords.remove(trieKey)
                    
                    // Store the display form for this word
                    customWordDisplayForms[lookupWord] = displayForm
                    customWordDisplayForms[trieKey] = displayForm
                    
                    // Insert base form (without apostrophe) for swipe matching
                    if (!alreadyKnown) {
                        insert(trieKey, 0)
                        
                        // Also insert with apostrophe if present
                        if (lookupWord.contains("'")) {
                            insert(lookupWord, 0)
                        }
                    }

                    // FIX: File I/O moved INSIDE synchronized block to prevent race conditions
                    val file = java.io.File(context.filesDir, USER_DICT_FILE)
                    
                    // If already known, we need to update the file (remove old, add new)
                    if (alreadyKnown) {
                        val existingLines = if (file.exists()) file.readLines() else emptyList()
                        val updatedLines = existingLines.filter { 
                            it.trim().lowercase(java.util.Locale.ROOT).replace("'", "") != trieKey 
                        } + displayForm
                        file.writeText(updatedLines.joinToString("\n") + "\n")
                        android.util.Log.d("DroidOS_Prediction", "Updated word: '$displayForm' (was in dict, updated display form)")
                    } else {
                        file.appendText("$displayForm\n")
                        android.util.Log.d("DroidOS_Prediction", "Learned word: '$displayForm' (lookup: '$trieKey', sentenceStart=$isSentenceStart)")
                    }

                    // Safe to call here (it uses synchronized data)
                    saveSetToFile(context, BLOCKED_DICT_FILE, blockedWords)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: learnWord with smart capitalization
    // =================================================================================

// =================================================================================
    // FUNCTION: getDisplayForm
    // SUMMARY: Returns the proper display form for a custom word.
    //          E.g., "droidos" -> "DroidOS", "iphone" -> "iPhone"
    //          Returns the input unchanged if not a custom word.
    // =================================================================================
    fun getDisplayForm(word: String): String {
        val lookup = word.lowercase(java.util.Locale.ROOT)
        val lookupNoApostrophe = lookup.replace("'", "")
        
        return customWordDisplayForms[lookup] 
            ?: customWordDisplayForms[lookupNoApostrophe]
            ?: word
    }
    // =================================================================================
    // END BLOCK: getDisplayForm
    // =================================================================================

    // =================================================================================
    // FUNCTION: saveSetToFile
    // SUMMARY: Saves a set of words to a file in the app's private storage.
    // =================================================================================
    private fun saveSetToFile(context: Context, filename: String, data: Set<String>) {
        try {
            val file = java.io.File(context.filesDir, filename)
            // FIX: Append newline to prevent next write from merging with last word (e.g. can'twon't)
            val content = data.filter { it.isNotEmpty() }.joinToString("\n") + "\n"
            file.writeText(content)
            android.util.Log.d("DroidOS_Prediction", "SAVEFILE: Wrote ${data.size} items to $filename")
        } catch (e: Exception) {
            android.util.Log.e("DroidOS_Prediction", "SAVEFILE FAILED: $filename - ${e.message}", e)
        }
    }
    // =================================================================================
    // END BLOCK: saveSetToFile
    // =================================================================================

    // =================================================================================
    // FILTER: GARBAGE DETECTION
    // Rule: Must have at least one vowel/y OR be in the whitelist.
    // =================================================================================
    private val VALID_VOWELLESS = setOf(
        "hmm", "shh", "psst", "brr", "pfft", "nth", "src", "jpg", "png", "gif",
        "txt", "xml", "pdf", "css", "html", "tv", "pc", "ok", "id", "cv", "ad", "ex", "vs", "mr", "dr", "ms"
    )

    // =================================================================================
    // FUNCTION: looksLikeGarbage
    // SUMMARY: Filters random letter combinations. Must have vowel or be whitelisted.
    // UPDATED: Strips apostrophes before checking (don't -> dont has vowel)
    // =================================================================================
    private fun looksLikeGarbage(word: String): Boolean {
        val checkWord = word.replace("'", "")
        if (checkWord.length > 1) {
            val hasVowel = checkWord.any { "aeiouyAEIOUY".contains(it) }
            if (!hasVowel) {
                if (VALID_VOWELLESS.contains(checkWord.lowercase(java.util.Locale.ROOT))) return false
                return true
            }
        }
        return false
    }
    // =================================================================================
    // END BLOCK: looksLikeGarbage
    // =================================================================================

    fun hasWord(word: String): Boolean {
        return wordList.contains(word.lowercase(Locale.ROOT))
    }

    // =================================================================================
    // FUNCTION: isWordBlocked
    // SUMMARY: Checks if a word is in the blocked list.
    // =================================================================================
    fun isWordBlocked(word: String): Boolean {
        return blockedWords.contains(word.lowercase(Locale.ROOT))
    }
// =================================================================================
    // END BLOCK: isWordBlocked
    // =================================================================================

    // =================================================================================
    // FUNCTION: isCustomWord
    // SUMMARY: Checks if a word is in the user's custom dictionary.
    //          Used to style user-added words differently (italic) in prediction bar.
    // =================================================================================
    fun isCustomWord(word: String): Boolean {
        val lower = word.lowercase(Locale.ROOT)
        val withoutApostrophe = lower.replace("'", "")
        return customWords.contains(lower) || customWords.contains(withoutApostrophe)
    }
    // =================================================================================
    // END BLOCK: isCustomWord
    // =================================================================================    // =================================================================================
    // FUNCTION: insert
    // SUMMARY: Inserts a word into the Trie and the first/last letter index.
    // =================================================================================
    fun insert(word: String, rank: Int) {
        val lower = word.lowercase(Locale.ROOT)
        if (lower.length < 2) return  // Skip single-letter words
        
        var node = root
        for (c in lower) {
            node = node.children.getOrPut(c) { TrieNode() }
        }
        node.isEndOfWord = true
        node.word = lower
        node.rank = rank
        
        // OPTIMIZATION: Add to first-letter index
        val firstChar = lower.first()
        wordsByFirstLetter.getOrPut(firstChar) { ArrayList() }.add(lower)
        
        // OPTIMIZATION: Add to first+last letter index for fast lookup
        val key = "${lower.first()}${lower.last()}"
        wordsByFirstLastLetter.getOrPut(key) { ArrayList() }.add(lower)
    }
    // =================================================================================
    // END BLOCK: insert
    // =================================================================================

    /**
     * Returns a list of suggested words for the given prefix, sorted by popularity.
     */
    // =================================================================================
    // FUNCTION: getSuggestions
    // SUMMARY: Returns suggested words for a given prefix, sorted by popularity.
    //          Filters out blocked words to prevent them from appearing in suggestions.

// =================================================================================
    // FUNCTION: getSuggestions (Updated for Priority Sort)
    // =================================================================================


    fun getSuggestions(prefix: String, maxResults: Int = 3): List<String> {
        if (prefix.isEmpty()) return emptyList()
        val cleanPrefix = prefix.lowercase(java.util.Locale.ROOT)

        var current = root
        for (char in cleanPrefix) {
            current = current.children[char] ?: return emptyList()
        }

        val candidates = ArrayList<Pair<String, Int>>()
        collectCandidates(current, candidates)
        
        val sortedCandidates = candidates.sortedWith(Comparator { a, b ->
            val wordA = a.first
            val wordB = b.first
            
            val countA = userFrequencyMap[wordA] ?: 0
            val countB = userFrequencyMap[wordB] ?: 0
            
            if (countA != countB) return@Comparator countB - countA
            val rankA = a.second
            val rankB = b.second
            if (rankA != rankB) return@Comparator rankA - rankB
            wordA.length - wordB.length
        })

        return sortedCandidates
            .filter { !blockedWords.contains(it.first.lowercase(java.util.Locale.ROOT)) }
            .distinctBy { it.first }
            .take(maxResults)
            .map { it.first }
    }



    // =================================================================================
    // FUNCTION: collectCandidates
    // SUMMARY: Recursively collects word candidates from trie nodes.
    //          Skips blocked words during traversal for efficiency.
    // =================================================================================
    private fun collectCandidates(node: TrieNode, results: MutableList<Pair<String, Int>>) {
        if (node.isEndOfWord) {
            node.word?.let { word ->
                // Skip blocked words
                if (!blockedWords.contains(word)) {
                    results.add(word to node.rank)
                }
            }
        }
        for (child in node.children.values) {
            collectCandidates(child, results)
        }
    }
    // =================================================================================
    // END BLOCK: collectCandidates
    // =================================================================================

    // =================================================================================
    // SHARK2-INSPIRED SWIPE DECODER LOGIC
    // =================================================================================
    
    /**
     * Main entry point for decoding a swipe gesture into word candidates.
     * Implements SHARK2-style dual-channel matching with:
     * 1. Path sampling to uniform N points
     * 2. Candidate pruning by start/end keys
     * 3. Shape channel scoring (normalized patterns)
     * 4. Location channel scoring (absolute positions)  
     * 5. Integration with frequency weighting
     */
    // =================================================================================
    // FUNCTION: decodeSwipe (Adaptive Length + Robust Neighbors)
    // =================================================================================
// =================================================================================
    // FUNCTION: decodeSwipe (v6 - Turn-Aware Scoring)
    // SUMMARY: Based on original working version with:
    //          - NEW turn detection for shortcut, android, circle
    //          - Conservative dwell for as/ass, to/too
    //          - Original candidate collection and length filtering
    // =================================================================================
    fun decodeSwipe(swipePath: List<PointF>, keyMap: Map<String, PointF>): List<String> {
        if (swipePath.size < 3 || keyMap.isEmpty()) return emptyList()
        if (wordList.isEmpty()) {
            loadDefaults()
            return emptyList()
        }

        val keyMapHash = keyMap.hashCode()
        if (keyMapHash != lastKeyMapHash) {
            templateCache.clear()
            lastKeyMapHash = keyMapHash
        }

        val inputLength = getPathLength(swipePath)
        if (inputLength < 10f) return emptyList()

        // =======================================================================
        // DWELL DETECTION (Conservative - for to/too, as/ass)
        // =======================================================================
        var dwellScore = 0f
        if (swipePath.size > 12) {
            val tailSize = maxOf(12, swipePath.size / 4)
            val tailStart = maxOf(0, swipePath.size - tailSize)
            val tail = swipePath.subList(tailStart, swipePath.size)
            
            var tailLength = 0f
            for (i in 1 until tail.size) {
                tailLength += hypot(tail[i].x - tail[i-1].x, tail[i].y - tail[i-1].y)
            }
            
            val avgMovementPerPoint = tailLength / tail.size
            dwellScore = when {
                avgMovementPerPoint < 2f -> 1.0f
                avgMovementPerPoint < 4f -> 0.6f
                avgMovementPerPoint < 7f -> 0.2f
                else -> 0f
            }
        }
        val isDwellingAtEnd = dwellScore >= 0.6f
        // =======================================================================
        // END DWELL DETECTION
        // =======================================================================

        val sampledInput = samplePath(swipePath, SAMPLE_POINTS)
        val normalizedInput = normalizePath(sampledInput)
        val inputDirections = calculateDirectionVectors(sampledInput)

        // NEW: Calculate turn points for input
        val inputTurns = detectTurns(inputDirections)

        // NEW: Extract sequence of keys the path passes through
        // This is CRITICAL for distinguishing "awake" vs "awesome"

        val pathKeys = extractPathKeys(sampledInput, keyMap, 8)

        val startPoint = sampledInput.first()
        val endPoint = sampledInput.last()

        // FIX: Define startKey and endKey (Missing in previous build)
        val startKey = findClosestKey(startPoint, keyMap)
        val endKey = findClosestKey(endPoint, keyMap)
        
// =======================================================================
        // CANDIDATE COLLECTION (Enhanced with Path Key Matching)
        // SUMMARY: Collects candidates using:
        //          1. Start/End neighbor search (original)
        //          2. PREFIX INJECTION (original)  
        //          3. PATH KEY INJECTION - NEW: Add words containing detected path keys
        //             This ensures "awake" is included when path shows a→w→...
        //          4. User History (original)
        // =======================================================================
        val candidates = HashSet<String>()
        
        // 1. Neighbor Search (original)
        val nearbyStart = findNearbyKeys(startPoint, keyMap, 80f)
        val nearbyEnd = findNearbyKeys(endPoint, keyMap, 80f)
        
        for (s in nearbyStart) {
            for (e in nearbyEnd) {
                wordsByFirstLastLetter["${s}${e}"]?.let { candidates.addAll(it) }
            }
        }
        
        // 2. PREFIX INJECTION (original)
        if (startKey != null) {
            wordsByFirstLetter[startKey.first()]?.let { words ->
                candidates.addAll(words.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(25))
            }
        }

                // 3. PATH KEY INJECTION (Enhanced)

                // If path shows specific intermediate keys, add words that contain those keys.

                // We increased limits (30->150) to ensure "awake" isn't pushed out by common words.

                if (pathKeys.size >= 2) {

                    val secondKey = pathKeys.getOrNull(1)?.firstOrNull()?.lowercaseChar()

                    if (secondKey != null && startKey != null) {

                        wordsByFirstLetter[startKey.first()]?.let { words ->

                            val matchingWords = words.filter { word ->

                                word.length >= 2 && word.drop(1).contains(secondKey)

                            }.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(150) // Increased from 30

                            candidates.addAll(matchingWords)

                        }

                    }

        

                    // Also check third key if present

                    val thirdKey = pathKeys.getOrNull(2)?.firstOrNull()?.lowercaseChar()

                    if (thirdKey != null && startKey != null && thirdKey != secondKey) {

                        wordsByFirstLetter[startKey.first()]?.let { words ->

                            val matchingWords = words.filter { word ->

                                word.length >= 3 && word.drop(1).contains(thirdKey)

                            }.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(150) // Increased from 20

                            candidates.addAll(matchingWords)

                        }

                    }

                    

                    // 3.5 STRICT SEQUENCE MATCH (New)

                    // If we have a complex path (e.g. a->w->a->e), specifically look for words

                    // that contain ALL these keys in relative order.

                    if (pathKeys.size >= 3 && startKey != null) {

                        wordsByFirstLetter[startKey.first()]?.let { words ->

                            // Get all intermediate keys (excluding start)

                            val requiredKeys = pathKeys.drop(1).map { it.firstOrNull()?.lowercaseChar() }.filterNotNull()

                            

                            val strictMatches = words.filter { word ->

                                var lastIdx = 0

                                var matches = true

                                for (rk in requiredKeys) {

                                    val idx = word.indexOf(rk, lastIdx)

                                    if (idx == -1) { 

                                        matches = false; break 

                                    }

                                    lastIdx = idx + 1

                                }

                                matches

                            }.take(50) // Force include these specific matches

                            

                            if (strictMatches.isNotEmpty()) {

                                candidates.addAll(strictMatches)

                                android.util.Log.d("DroidOS_PathKeys", "Strict Match found: ${strictMatches.take(5)}")

                            }

                        }

                    }

                }

        

                // 4. User History (original)

                synchronized(userFrequencyMap) {

                    candidates.addAll(userFrequencyMap.entries

                        .sortedByDescending { it.value }

                        .take(30) // Increased from 15

                        .map { it.key })

                }

        

                // Debug: Log total candidates

                android.util.Log.d("DroidOS_PathKeys", "Total candidates: ${candidates.size}")

        

                // =======================================================================

                // END CANDIDATE COLLECTION

                // =======================================================================

        

                // --- SCORING ---

                val scored = candidates

                    .filter { !isWordBlocked(it) && it.length >= MIN_WORD_LENGTH }

                    .sortedWith(compareByDescending<String> { userFrequencyMap[it] ?: 0 }.thenBy { getWordRank(it) })

                    .take(400) // Increased from 150 to prevent dropping valid low-frequency words

                    .mapNotNull { word ->
                val template = getOrCreateTemplate(word, keyMap) ?: return@mapNotNull null
                
                // --- ADAPTIVE LENGTH FILTER (Original) ---
                val tLen = getPathLength(template.rawPoints)
                val ratio = tLen / inputLength
                
                val maxRatio = if (inputLength < 150f) 1.5f else 5.0f
                if (ratio > maxRatio || ratio < 0.4f) return@mapNotNull null

                if (template.sampledPoints == null) {
                    template.sampledPoints = samplePath(template.rawPoints, SAMPLE_POINTS)
                    template.normalizedPoints = normalizePath(template.sampledPoints!!)
                    template.directionVectors = calculateDirectionVectors(template.sampledPoints!!)
                }
                
                val shapeScore = calculateShapeScore(normalizedInput, template.normalizedPoints!!)
                val locScore = calculateLocationScore(sampledInput, template.sampledPoints!!)
                val dirScore = calculateDirectionScore(inputDirections, template.directionVectors!!)

                // NEW: Turn matching score
                val templateTurns = detectTurns(template.directionVectors!!)
                val turnScore = calculateTurnScore(inputTurns, templateTurns)

                // NEW: Path key matching score - penalizes words where path doesn't match
                // This distinguishes "awake" (path goes through w) from "awesome" (path would need s)
                val pathKeyScore = calculatePathKeyScore(pathKeys, word)

                val integrationScore = (shapeScore * SHAPE_WEIGHT) +
                                       (locScore * LOCATION_WEIGHT) +
                                       (dirScore * DIRECTION_WEIGHT) +
                                       (turnScore * TURN_WEIGHT) +
                                       (pathKeyScore * 0.8f)  // NEW: Path key matching weight
                

                // --- BOOSTS (Original) ---
                val rank = template.rank
                val freqBonus = 1.0f / (1.0f + 0.15f * ln((rank + 1).toFloat()))
                
                // CHANGED: Reset user history boost to neutral (1.0f). 
                // This stops user-added words (like "texting") from overriding better geometric matches (like "testing").
                var userBoost = 1.0f
                
                // DOUBLE LETTER BOOST (Conservative - only 3+ letter words)
                val hasEndDouble = word.length >= 3 && 
                    word.last().lowercaseChar() == word[word.length - 2].lowercaseChar()
                
                if (hasEndDouble && isDwellingAtEnd) {
                    userBoost *= (1.10f + dwellScore * 0.15f)
                }
                
                // EXACT KEY MATCH BONUS
                if (startKey != null && word.startsWith(startKey, ignoreCase = true)) userBoost *= 1.15f
                if (endKey != null && word.endsWith(endKey, ignoreCase = true)) userBoost *= 1.15f
                
                // LONG WORD BONUS
                if (word.length >= 6) userBoost *= 1.15f



                val finalScore = (integrationScore * (1.0f - 0.5f * freqBonus)) / userBoost
                Pair(word, finalScore)
            }
        
        // FIX: Apply getDisplayForm to ensure capitalized words (e.g. Katsuya) are returned correctly
        val results = scored.sortedBy { it.second }.distinctBy { it.first }.take(3).map { it.first }
        return results.map { getDisplayForm(it) }
    }

    // =================================================================================
    // END BLOCK: Boost Calculation (and decodeSwipe)
    // =================================================================================

    // =================================================================================
    // FUNCTION: decodeSwipeTimed (Time-Weighted Swipe Decoding)
    // SUMMARY: Enhanced version of decodeSwipe that uses timestamp data to detect
    //          when users linger on specific keys. This allows disambiguation of
    //          similar words like "for" vs "four" - lingering on "u" boosts "four".
    //          
    // HOW IT WORKS:
    //   1. Converts TimedPoints to regular path for geometric analysis
    //   2. Calculates dwell time on each key the path crosses
    //   3. Keys with longer dwell times get boosted in scoring
    //   4. Final score combines geometric + frequency + dwell time weights
    // =================================================================================
    fun decodeSwipeTimed(timedPath: List<TimedPoint>, keyMap: Map<String, PointF>): List<String> {
        if (timedPath.size < 3 || keyMap.isEmpty()) return emptyList()
        
        // Convert to regular PointF path for existing geometric analysis
        val swipePath = timedPath.map { it.toPointF() }
        
        // Calculate dwell times per key
        val keyDwellTimes = calculateKeyDwellTimes(timedPath, keyMap)
        
        // Debug log dwell times
        if (keyDwellTimes.isNotEmpty()) {
            val dwellDebug = keyDwellTimes.entries
                .filter { it.value > DWELL_THRESHOLD_MS }
                .sortedByDescending { it.value }
                .take(5)
                .joinToString(", ") { "${it.key}:${it.value}ms" }
            if (dwellDebug.isNotEmpty()) {
                android.util.Log.d("DroidOS_Dwell", "Key dwell times: $dwellDebug")
            }
        }
        
        // Run normal decoding with dwell boost
        return decodeSwipeWithDwell(swipePath, keyMap, keyDwellTimes)
    }
    // =================================================================================
    // END BLOCK: decodeSwipeTimed
    // =================================================================================

    // =================================================================================
    // FUNCTION: calculateKeyDwellTimes
    // SUMMARY: Analyzes a timed swipe path to calculate how long the user spent
    //          near each key. Returns a map of key -> total milliseconds.
    //          Used to boost keys where user intentionally lingered.
    // =================================================================================
    private fun calculateKeyDwellTimes(
        timedPath: List<TimedPoint>, 
        keyMap: Map<String, PointF>
    ): Map<String, Long> {
        val dwellTimes = HashMap<String, Long>()
        if (timedPath.size < 2) return dwellTimes
        
        val KEY_PROXIMITY_RADIUS = 50f  // Pixels - consider "on key" if within this radius
        
        for (i in 1 until timedPath.size) {
            val prev = timedPath[i - 1]
            val curr = timedPath[i]
            val timeDelta = curr.timestamp - prev.timestamp
            
            // Skip if timestamp jump is too large (likely a pause/resume)
            if (timeDelta > 500) continue
            
            // Find which key this point is closest to
            val point = PointF(curr.x, curr.y)
            var closestKey: String? = null
            var closestDist = Float.MAX_VALUE
            
            for ((key, center) in keyMap) {
                val dist = hypot(point.x - center.x, point.y - center.y)
                if (dist < closestDist && dist < KEY_PROXIMITY_RADIUS) {
                    closestDist = dist
                    closestKey = key
                }
            }
            
            // Add time spent near this key
            if (closestKey != null) {
                dwellTimes[closestKey] = (dwellTimes[closestKey] ?: 0L) + timeDelta
            }
        }
        
        return dwellTimes
    }
    // =================================================================================
    // END BLOCK: calculateKeyDwellTimes
    // =================================================================================

    // =================================================================================
    // FUNCTION: decodeSwipeWithDwell
    // SUMMARY: Modified version of decodeSwipe that incorporates dwell time scoring.
    //          Words containing keys with high dwell times get boosted.
    //          This is the core logic that makes "lingering on U" select "four" over "for".
    // =================================================================================
    private fun decodeSwipeWithDwell(
        swipePath: List<PointF>, 
        keyMap: Map<String, PointF>,
        keyDwellTimes: Map<String, Long>
    ): List<String> {
        if (swipePath.size < 3 || keyMap.isEmpty()) return emptyList()
        if (wordList.isEmpty()) {
            loadDefaults()
            return emptyList()
        }

        val keyMapHash = keyMap.hashCode()
        if (keyMapHash != lastKeyMapHash) {
            templateCache.clear()
            lastKeyMapHash = keyMapHash
        }

        val inputLength = getPathLength(swipePath)
        if (inputLength < 10f) return emptyList()

        // =======================================================================
        // DWELL DETECTION (Conservative - for to/too, as/ass)
        // =======================================================================
        var dwellScore = 0f
        if (swipePath.size > 12) {
            val tailSize = maxOf(12, swipePath.size / 4)
            val tailStart = maxOf(0, swipePath.size - tailSize)
            val tail = swipePath.subList(tailStart, swipePath.size)
            
            var tailLength = 0f
            for (i in 1 until tail.size) {
                tailLength += hypot(tail[i].x - tail[i-1].x, tail[i].y - tail[i-1].y)
            }
            
            val avgMovementPerPoint = tailLength / tail.size
            dwellScore = when {
                avgMovementPerPoint < 2f -> 1.0f
                avgMovementPerPoint < 4f -> 0.6f
                avgMovementPerPoint < 7f -> 0.2f
                else -> 0f
            }
        }
        val isDwellingAtEnd = dwellScore >= 0.6f
        // =======================================================================
        // END DWELL DETECTION
        // =======================================================================

        val sampledInput = samplePath(swipePath, SAMPLE_POINTS)
        val normalizedInput = normalizePath(sampledInput)
        val inputDirections = calculateDirectionVectors(sampledInput)

        val inputTurns = detectTurns(inputDirections)
        val pathKeys = extractPathKeys(sampledInput, keyMap, 8)

        val startPoint = sampledInput.first()
        val endPoint = sampledInput.last()
        val startKey = findClosestKey(startPoint, keyMap)
        val endKey = findClosestKey(endPoint, keyMap)
        
        // =======================================================================
        // CANDIDATE COLLECTION (same as original)
        // =======================================================================
        val candidates = HashSet<String>()
        
        val nearbyStart = findNearbyKeys(startPoint, keyMap, 80f)
        val nearbyEnd = findNearbyKeys(endPoint, keyMap, 80f)
        
        for (s in nearbyStart) {
            for (e in nearbyEnd) {
                wordsByFirstLastLetter["${s}${e}"]?.let { candidates.addAll(it) }
            }
        }
        
        if (startKey != null) {
            wordsByFirstLetter[startKey.first()]?.let { words ->
                candidates.addAll(words.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(25))
            }
        }

        if (pathKeys.size >= 2) {
            val secondKey = pathKeys.getOrNull(1)?.firstOrNull()?.lowercaseChar()
            if (secondKey != null && startKey != null) {
                wordsByFirstLetter[startKey.first()]?.let { words ->
                    val matchingWords = words.filter { word ->
                        word.length >= 2 && word.drop(1).contains(secondKey)
                    }.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(150)
                    candidates.addAll(matchingWords)
                }
            }
            
            val thirdKey = pathKeys.getOrNull(2)?.firstOrNull()?.lowercaseChar()
            if (thirdKey != null && startKey != null && thirdKey != secondKey) {
                wordsByFirstLetter[startKey.first()]?.let { words ->
                    val matchingWords = words.filter { word ->
                        word.length >= 3 && word.drop(1).contains(thirdKey)
                    }.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(150)
                    candidates.addAll(matchingWords)
                }
            }
            
            if (pathKeys.size >= 3 && startKey != null) {
                wordsByFirstLetter[startKey.first()]?.let { words ->
                    val requiredKeys = pathKeys.drop(1).map { it.firstOrNull()?.lowercaseChar() }.filterNotNull()
                    val strictMatches = words.filter { word ->
                        var lastIdx = 0
                        var matches = true
                        for (rk in requiredKeys) {
                            val idx = word.indexOf(rk, lastIdx)
                            if (idx == -1) { 
                                matches = false; break 
                            }
                            lastIdx = idx + 1
                        }
                        matches
                    }.take(50)
                    
                    if (strictMatches.isNotEmpty()) {
                        candidates.addAll(strictMatches)
                    }
                }
            }
        }

        synchronized(userFrequencyMap) {
            candidates.addAll(userFrequencyMap.entries
                .sortedByDescending { it.value }
                .take(30)
                .map { it.key })
        }
        // =======================================================================
        // END CANDIDATE COLLECTION
        // =======================================================================

        // =======================================================================
        // SCORING WITH DWELL TIME BOOST
        // =======================================================================
        val scored = candidates
            .filter { !isWordBlocked(it) && it.length >= MIN_WORD_LENGTH }
            .sortedWith(compareByDescending<String> { userFrequencyMap[it] ?: 0 }.thenBy { getWordRank(it) })
            .take(400)
            .mapNotNull { word ->
                val template = getOrCreateTemplate(word, keyMap) ?: return@mapNotNull null
                
                val tLen = getPathLength(template.rawPoints)
                val ratio = tLen / inputLength
                
                val maxRatio = if (inputLength < 150f) 1.5f else 5.0f
                if (ratio > maxRatio || ratio < 0.4f) return@mapNotNull null

                if (template.sampledPoints == null) {
                    template.sampledPoints = samplePath(template.rawPoints, SAMPLE_POINTS)
                    template.normalizedPoints = normalizePath(template.sampledPoints!!)
                    template.directionVectors = calculateDirectionVectors(template.sampledPoints!!)
                }
                
                val shapeScore = calculateShapeScore(normalizedInput, template.normalizedPoints!!)
                val locScore = calculateLocationScore(sampledInput, template.sampledPoints!!)
                val dirScore = calculateDirectionScore(inputDirections, template.directionVectors!!)

                val templateTurns = detectTurns(template.directionVectors!!)
                val turnScore = calculateTurnScore(inputTurns, templateTurns)
                val pathKeyScore = calculatePathKeyScore(pathKeys, word)

                // =======================================================================
                // NEW: DWELL TIME SCORING
                // Calculate how much the user's dwell times match this word's letters.
                // If user lingered on "U", words containing "U" get boosted.
                // =======================================================================
                val dwellBoost = calculateDwellBoost(word, keyDwellTimes)
                // =======================================================================
                // END DWELL TIME SCORING
                // =======================================================================

                val integrationScore = (shapeScore * SHAPE_WEIGHT) +
                                       (locScore * LOCATION_WEIGHT) +
                                       (dirScore * DIRECTION_WEIGHT) +
                                       (turnScore * TURN_WEIGHT) +
                                       (pathKeyScore * 0.8f)
                
                val rank = template.rank
                val freqBonus = 1.0f / (1.0f + 0.15f * ln((rank + 1).toFloat()))
                
                var userBoost = 1.0f
                
                val hasEndDouble = word.length >= 3 && 
                    word.last().lowercaseChar() == word[word.length - 2].lowercaseChar()
                
                if (hasEndDouble && isDwellingAtEnd) {
                    userBoost *= (1.10f + dwellScore * 0.15f)
                }
                
                if (startKey != null && word.startsWith(startKey, ignoreCase = true)) userBoost *= 1.15f
                if (endKey != null && word.endsWith(endKey, ignoreCase = true)) userBoost *= 1.15f
                if (word.length >= 6) userBoost *= 1.15f

                // =======================================================================
                // APOSTROPHE VARIANT BOOST
                // If user has a custom word with apostrophe (don't) and we're matching
                // the base form (dont), boost the apostrophe version significantly.
                // This makes swiping "dont" return "don't" if learned.
                // =======================================================================
                val wordWithApostrophe = findApostropheVariant(word)
                if (wordWithApostrophe != null) {
                    userBoost *= 1.5f  // Strong boost for apostrophe variants
                    android.util.Log.d("DroidOS_Apostrophe", "Boosting '$word' -> '$wordWithApostrophe'")
                }
                // =======================================================================
                // END BLOCK: APOSTROPHE VARIANT BOOST
                // =======================================================================


                // Apply dwell boost - words matching user's lingered keys score better
                userBoost *= dwellBoost

                val finalScore = (integrationScore * (1.0f - 0.5f * freqBonus)) / userBoost
                Pair(word, finalScore)
            }
        
        // =======================================================================
        // POST-PROCESS: Apply display forms and apostrophe variants
        // =======================================================================
        val results = scored.sortedBy { it.second }.distinctBy { it.first }.take(3).map { it.first }
        
        return results.map { word ->
            val apostropheVariant = findApostropheVariant(word)
            val base = apostropheVariant ?: word
            // FIX: Apply display form to ensure proper capitalization (Katsuya, iPhone, etc.)
            getDisplayForm(base)
        }
    }

    // =================================================================================
    // END BLOCK: decodeSwipeWithDwell
    // =================================================================================

    // =================================================================================
    // FUNCTION: calculateDwellBoost
    // SUMMARY: Calculates a boost factor for a word based on how well it matches
    //          the user's key dwell times. Words containing keys the user lingered
    //          on get a higher boost (lower score = better in this system, so we
    //          return values > 1.0 to boost, < 1.0 to penalize).
    //          
    // EXAMPLE: User types "for" path but lingers on "U" for 150ms
    //          - "for" has no U -> dwellBoost = 1.0 (neutral)
    //          - "four" has U -> dwellBoost = 1.2 (boosted, wins!)
    // =================================================================================
    private fun calculateDwellBoost(word: String, keyDwellTimes: Map<String, Long>): Float {
        if (keyDwellTimes.isEmpty()) return 1.0f
        
        var boost = 1.0f
        val wordLower = word.lowercase()
        
        // Find keys with significant dwell time
        val significantDwells = keyDwellTimes.filter { it.value > DWELL_THRESHOLD_MS }
        
        for ((key, dwellMs) in significantDwells) {
            val keyChar = key.lowercase().firstOrNull() ?: continue
            
            // Calculate boost based on dwell time
            // 100ms = small boost (1.05), 200ms = medium (1.15), 300ms+ = strong (1.25)
            val dwellFactor = when {
                dwellMs > 300 -> 1.25f
                dwellMs > 200 -> 1.15f
                dwellMs > 150 -> 1.10f
                dwellMs > 100 -> 1.05f
                else -> 1.02f
            }
            
            if (wordLower.contains(keyChar)) {
                // Word contains this lingered-on key - BOOST it
                boost *= dwellFactor
                android.util.Log.d("DroidOS_Dwell", "BOOST '$word': contains '$keyChar' (${dwellMs}ms) -> boost $dwellFactor")
            } else {
                // Word does NOT contain this lingered-on key - slight penalty
                // This helps "four" beat "for" when user lingered on "u"
                val penalty = 1.0f / (dwellFactor * 0.5f + 0.5f)  // Gentler penalty
                boost *= penalty
                android.util.Log.d("DroidOS_Dwell", "PENALTY '$word': missing '$keyChar' (${dwellMs}ms) -> penalty $penalty")
            }
        }
        
        return boost.coerceIn(0.5f, 2.0f)  // Clamp to reasonable range
    }
    // =================================================================================
    // END BLOCK: calculateDwellBoost
    // =================================================================================

    // =================================================================================
    // FUNCTION: findApostropheVariant
    // SUMMARY: Checks if a user-learned word with apostrophe exists for this base word.
    //          E.g., "dont" -> "don't", "wont" -> "won't", "im" -> "i'm"
    //          Returns the apostrophe variant if found in customWords, null otherwise.
    // =================================================================================
    private fun findApostropheVariant(baseWord: String): String? {
        val lower = baseWord.lowercase(java.util.Locale.ROOT)
        
        // Common contraction patterns
        val patterns = listOf(
            // n't contractions
            Pair("nt$", "n't"),      // dont -> don't, wont -> won't, cant -> can't
            // 'm contractions  
            Pair("^im$", "i'm"),     // im -> i'm
            // 'll contractions
            Pair("ll$", "'ll"),      // well could match, so be careful
            // 're contractions
            Pair("re$", "'re"),      // youre -> you're, were -> we're
            // 've contractions
            Pair("ve$", "'ve"),      // wouldve -> would've
            // 's contractions
            Pair("s$", "'s"),        // its -> it's, thats -> that's
        )
        
        // Check common contractions first
        for ((pattern, replacement) in patterns) {
            val regex = Regex(pattern)
            if (regex.containsMatchIn(lower)) {
                val variant = lower.replace(regex, replacement)
                if (customWords.contains(variant)) {
                    return variant
                }
            }
        }
        
        // Also check if any custom word without apostrophe matches this word
        for (customWord in customWords) {
            if (customWord.contains("'")) {
                val withoutApostrophe = customWord.replace("'", "")
                if (withoutApostrophe == lower) {
                    return customWord
                }
            }
        }
        
        return null
    }
    // =================================================================================
    // END BLOCK: calculateDwellBoost
    // =================================================================================

    // =================================================================================
    // FUNCTION: decodeSwipePreview (FAST - for live updates)
    // SUMMARY: Lightweight version of decodeSwipe for real-time preview during swiping.
    //          Uses fewer candidates and simpler scoring for speed.
    //          Returns top 3 predictions based on current path (may be incomplete).
    // =================================================================================
    fun decodeSwipePreview(swipePath: List<PointF>, keyMap: Map<String, PointF>): List<String> {
        if (swipePath.size < 5 || keyMap.isEmpty()) return emptyList()
        if (wordList.isEmpty()) return emptyList()

        val keyMapHash = keyMap.hashCode()
        if (keyMapHash != lastKeyMapHash) {
            templateCache.clear()
            lastKeyMapHash = keyMapHash
        }

        val inputLength = getPathLength(swipePath)
        if (inputLength < 20f) return emptyList()

        val sampledInput = samplePath(swipePath, SAMPLE_POINTS)
        val inputDirections = calculateDirectionVectors(sampledInput)

        val startPoint = sampledInput.first()
        val endPoint = sampledInput.last()

        val startKey = findClosestKey(startPoint, keyMap)
        val endKey = findClosestKey(endPoint, keyMap)

        // Extract path keys for intermediate matching (fewer samples for speed)
        val pathKeys = extractPathKeys(sampledInput, keyMap, 6)

        // DEBUG: Log path keys for live preview
        if (pathKeys.isNotEmpty()) {
            android.util.Log.d("DroidOS_Preview", "PREVIEW Keys: ${pathKeys.joinToString("→")}")
        }

        // FAST CANDIDATE COLLECTION - fewer candidates for speed
        val candidates = HashSet<String>()

        // 1. Neighbor Search (smaller radius for speed)
        val nearbyStart = findNearbyKeys(startPoint, keyMap, 60f)
        val nearbyEnd = findNearbyKeys(endPoint, keyMap, 60f)

        for (s in nearbyStart) {
            for (e in nearbyEnd) {
                wordsByFirstLastLetter["${s}${e}"]?.let { candidates.addAll(it) }
            }
        }

        // 2. PREFIX INJECTION (fewer candidates)
        if (startKey != null) {
            wordsByFirstLetter[startKey.first()]?.let { words ->
                candidates.addAll(words.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(15))
            }
        }
        if (pathKeys.size >= 2) {
            val secondKey = pathKeys.getOrNull(1)?.firstOrNull()?.lowercaseChar()
            if (secondKey != null && startKey != null) {
                wordsByFirstLetter[startKey.first()]?.let { words ->
                    val matchingWords = words.filter { word ->
                        word.length >= 2 && word.drop(1).contains(secondKey)
                    }.sortedByDescending { userFrequencyMap[it] ?: 0 }.take(20)
                    candidates.addAll(matchingWords)
                }
            }
        }
        // FAST SCORING - simplified for speed
        val scored = candidates
            .filter { !isWordBlocked(it) && it.length >= MIN_WORD_LENGTH }
            .sortedWith(compareByDescending<String> { userFrequencyMap[it] ?: 0 }.thenBy { getWordRank(it) })
            .take(50)  // Fewer candidates for speed
            .mapNotNull { word ->
                val template = getOrCreateTemplate(word, keyMap) ?: return@mapNotNull null

                // Quick length filter
                val tLen = getPathLength(template.rawPoints)
                val ratio = tLen / inputLength
                if (ratio > 3.0f || ratio < 0.3f) return@mapNotNull null

                if (template.sampledPoints == null) {
                    template.sampledPoints = samplePath(template.rawPoints, SAMPLE_POINTS)
                    template.normalizedPoints = normalizePath(template.sampledPoints!!)
                    template.directionVectors = calculateDirectionVectors(template.sampledPoints!!)
                }

                // SIMPLIFIED SCORING - location, direction, and path key matching for speed
                val locScore = calculateLocationScore(sampledInput, template.sampledPoints!!)
                val dirScore = calculateDirectionScore(inputDirections, template.directionVectors!!)

                // Add path key score for better intermediate key matching
                val pathKeyScore = calculatePathKeyScore(pathKeys, word)

                val integrationScore = locScore * 0.4f + dirScore * 0.2f + pathKeyScore * 0.6f  // Path keys most important for preview

                // Basic boosts
                val rank = template.rank
                val freqBonus = 1.0f / (1.0f + 0.1f * ln((rank + 1).toFloat()))

                var boost = 1.0f
                if (startKey != null && word.startsWith(startKey, ignoreCase = true)) boost *= 1.2f
                if (endKey != null && word.endsWith(endKey, ignoreCase = true)) boost *= 1.2f
                if ((userFrequencyMap[word] ?: 0) > 0) boost *= 1.3f

                val finalScore = (integrationScore * (1.0f - 0.3f * freqBonus)) / boost
                Pair(word, finalScore)
            }

// =======================================================================
        // POST-PROCESS: Apply display forms and apostrophe variants
        // 1. First check for apostrophe variant (cant -> can't)
        // 2. Then apply display form for proper capitalization (droidos -> DroidOS)
        // =======================================================================
        val results = scored.sortedBy { it.second }.distinctBy { it.first }.take(3).map { it.first }
        
        return results.map { word ->
            val withApostrophe = findApostropheVariant(word)
            val baseWord = withApostrophe ?: word
            // Apply display form for custom words (preserves DroidOS, iPhone, etc.)
            getDisplayForm(baseWord)
        }
        // =======================================================================
        // END BLOCK: Display form application
        // =======================================================================
    }
    // =================================================================================
    // END BLOCK: decodeSwipeWithDwellPreview
    // =================================================================================    // =================================================================================
    // FUNCTION: findClosestKey
    // SUMMARY: Finds the single closest key to a point. Fast O(n) where n = key count.
    // =================================================================================
    private fun findClosestKey(point: PointF, keyMap: Map<String, PointF>): String? {
        var closestKey: String? = null
        var closestDist = Float.MAX_VALUE
        
        for ((key, pos) in keyMap) {
            if (key.length != 1 || !Character.isLetter(key[0])) continue
            val dist = hypot(point.x - pos.x, point.y - pos.y)
            if (dist < closestDist) {
                closestDist = dist
                closestKey = key
            }
        }
        return closestKey
    }
    // =================================================================================
    // END BLOCK: findClosestKey
    // =================================================================================

    // =================================================================================
    // FUNCTION: findNearbyKeys
    // SUMMARY: Finds all letter keys within a radius of a point.
    // =================================================================================
    private fun findNearbyKeys(point: PointF, keyMap: Map<String, PointF>, radius: Float): List<String> {
        return keyMap.entries
            .filter { (key, pos) -> 
                key.length == 1 && Character.isLetter(key[0]) &&
                hypot(point.x - pos.x, point.y - pos.y) <= radius
            }
            .map { it.key }
    }
    // =================================================================================
    // END BLOCK: findNearbyKeys
    // =================================================================================



    // =================================================================================
    // FUNCTION: extractPathKeys (v4 - Sharp Turns Only, No Sampling)
    // SUMMARY: Extracts ONLY the key waypoints: Start, Sharp Turns, and End.
    //          Does NOT sample intermediate points (which creates noise).
    //          A sharp turn is where direction changes significantly (dot < 0.4).
    //          This prevents picking up 's' when swiping diagonally from 'a' to 'k'.
    //
    //          Example: "awake" swipe path has sharp turns at w, a, k
    //          Result: a→w→a→k→e (not a→w→a→s→k→e)
    // =================================================================================
    private fun extractPathKeys(path: List<PointF>, keyMap: Map<String, PointF>, maxKeys: Int): List<String> {
        if (path.size < 3) return emptyList()

        val keys = ArrayList<String>()

        // 1. Always add Start Key
        val startKey = findClosestKey(path.first(), keyMap)
        if (startKey != null) {
            keys.add(startKey.lowercase())
        }

        // 2. Find ONLY sharp turns (significant direction changes)
        // Use a larger window (5 points) to avoid noise from jitter
        val windowSize = 5
        var lastTurnIdx = 0

        for (i in windowSize until path.size - windowSize) {
            // Vector from 5 points ago to current point
            val p1 = path[i - windowSize]
            val p2 = path[i]
            val p3 = path[i + windowSize]

            val v1x = p2.x - p1.x
            val v1y = p2.y - p1.y
            val v2x = p3.x - p2.x
            val v2y = p3.y - p2.y

            val len1 = kotlin.math.hypot(v1x, v1y)
            val len2 = kotlin.math.hypot(v2x, v2y)

            // Need significant movement to count as a direction
            if (len1 > 15f && len2 > 15f) {
                val dot = (v1x * v2x + v1y * v2y) / (len1 * len2)

                                    // SHARP turn only: dot < 0.6 means angle > ~53 degrees
                                    // Relaxed from 0.4 to catch 'k' in 'awake' and 'x' in 'expect'
                                    if (dot < 0.6f) {
                                        // Minimum distance from last turn to avoid duplicates
                                        if (i - lastTurnIdx > windowSize * 2) {                        val key = findClosestKey(p2, keyMap)?.lowercase()
                        if (key != null && (keys.isEmpty() || keys.last() != key)) {
                            keys.add(key)
                            lastTurnIdx = i
                        }
                    }
                }
            }
        }

        // 3. Always add End Key
        val endKey = findClosestKey(path.last(), keyMap)?.lowercase()
        if (endKey != null && (keys.isEmpty() || keys.last() != endKey)) {
            keys.add(endKey)
        }

        // DEBUG: Log extracted keys
        android.util.Log.d("DroidOS_PathKeys", "Extracted: ${keys.joinToString("→")} from ${path.size} pts (Sharp turns only)")

        return keys.take(maxKeys)
    }
    // =================================================================================
    // END BLOCK: extractPathKeys
    // =================================================================================

// =================================================================================
    // FUNCTION: calculatePathKeyScore (v5 - Early Keys Weighted Heavily)
    // SUMMARY: Penalizes words that don't match the path keys, with HEAVY emphasis on
    //          the first 2-3 keys. If path starts "a→w→...", words starting "aw..." 
    //          should be strongly preferred over words starting "as...".
    //          
    //          Key insight from debugging: When swiping "awake", the path often shows
    //          a→w at the start, but then picks up 's' on the way to 'k'. We need to
    //          prioritize the FIRST keys which are most reliable.
    // =================================================================================
    private fun calculatePathKeyScore(pathKeys: List<String>, word: String): Float {
        if (pathKeys.isEmpty()) return 0f
        
        val wordChars = word.lowercase()
        var penalty = 0f
        
        // CRITICAL: Check if the FIRST path keys match the FIRST word letters
        // This is the most reliable signal - the start of the swipe is intentional
        val firstPathKey = pathKeys.firstOrNull()?.firstOrNull()
        val firstWordChar = wordChars.firstOrNull()
        
        if (firstPathKey != null && firstWordChar != null && firstPathKey != firstWordChar) {
            // First key mismatch - BIG penalty
            penalty += 5.0f
        }
        
        // Check SECOND path key against second word letter (if exists)
        if (pathKeys.size >= 2 && wordChars.length >= 2) {
            val secondPathKey = pathKeys[1].firstOrNull()
            val secondWordChar = wordChars[1]
            
            if (secondPathKey != null && secondPathKey != secondWordChar) {
                // Second key mismatch - also big penalty
                // This catches "aw..." vs "as..." - if path shows 'w', penalize 's' words
                penalty += 4.0f
            }
        }
        
        // Check THIRD path key (if exists) - still important but less critical
        if (pathKeys.size >= 3 && wordChars.length >= 3) {
            val thirdPathKey = pathKeys[2].firstOrNull()
            val thirdWordChar = wordChars[2]
            
            if (thirdPathKey != null && thirdWordChar != null && thirdPathKey != thirdWordChar) {
                penalty += 2.0f
            }
        }
        
        // Now do subsequence matching for the rest of the path
        var pathIdx = 0
        var wordIdx = 0
        var matchedKeys = 0
        
        while (pathIdx < pathKeys.size && wordIdx < wordChars.length) {
            val pKey = pathKeys[pathIdx].firstOrNull() ?: continue
            val wChar = wordChars[wordIdx]
            
            if (pKey == wChar) {
                matchedKeys++
                pathIdx++
                wordIdx++
            } else {
                wordIdx++
            }
        }
        
        // Penalty for unmatched path keys (keys we swiped but aren't in the word)
        val unmatchedKeys = pathKeys.size - matchedKeys
        penalty += unmatchedKeys * 2.0f  // Reduced from 3.0f since we have early key penalties
        
        // Length penalty for very long words vs short paths
        if (wordChars.length > pathKeys.size * 2.5) {
            penalty += 0.5f
        }

        // DEBUG: Log scoring
        android.util.Log.d("DroidOS_PathKeys", "  '$word': path=${pathKeys.joinToString("-")} matched=$matchedKeys/${pathKeys.size} unmatched=$unmatchedKeys penalty=${"%.2f".format(penalty)}")
        
        return penalty
    }
    // =================================================================================
    // END BLOCK: calculatePathKeyScore
    // =================================================================================

    // =================================================================================
    // FUNCTION: areKeysAdjacent
    // SUMMARY: Checks if two keys are adjacent on a QWERTY keyboard.
    //          Used for typo tolerance - adjacent mismatches are less severe.
    // =================================================================================
    private fun areKeysAdjacent(key1: Char, key2: Char): Boolean {
        val adjacencyMap = mapOf(
            'q' to setOf('w', 'a'),
            'w' to setOf('q', 'e', 'a', 's'),
            'e' to setOf('w', 'r', 's', 'd'),
            'r' to setOf('e', 't', 'd', 'f'),
            't' to setOf('r', 'y', 'f', 'g'),
            'y' to setOf('t', 'u', 'g', 'h'),
            'u' to setOf('y', 'i', 'h', 'j'),
            'i' to setOf('u', 'o', 'j', 'k'),
            'o' to setOf('i', 'p', 'k', 'l'),
            'p' to setOf('o', 'l'),
            'a' to setOf('q', 'w', 's', 'z'),
            's' to setOf('a', 'w', 'e', 'd', 'z', 'x'),
            'd' to setOf('s', 'e', 'r', 'f', 'x', 'c'),
            'f' to setOf('d', 'r', 't', 'g', 'c', 'v'),
            'g' to setOf('f', 't', 'y', 'h', 'v', 'b'),
            'h' to setOf('g', 'y', 'u', 'j', 'b', 'n'),
            'j' to setOf('h', 'u', 'i', 'k', 'n', 'm'),
            'k' to setOf('j', 'i', 'o', 'l', 'm'),
            'l' to setOf('k', 'o', 'p'),
            'z' to setOf('a', 's', 'x'),
            'x' to setOf('z', 's', 'd', 'c'),
            'c' to setOf('x', 'd', 'f', 'v'),
            'v' to setOf('c', 'f', 'g', 'b'),
            'b' to setOf('v', 'g', 'h', 'n'),
            'n' to setOf('b', 'h', 'j', 'm'),
            'm' to setOf('n', 'j', 'k')
        )
        
        val adjacent1 = adjacencyMap[key1.lowercaseChar()] ?: emptySet()
        return key2.lowercaseChar() in adjacent1
    }
    // =================================================================================
    // END BLOCK: areKeysAdjacent
    // =================================================================================
    // =================================================================================
    // END BLOCK: calculatePathKeyScore
    // =================================================================================

    // =================================================================================
    // FUNCTION: getOrCreateTemplate
    // SUMMARY: Gets or creates a word template with key positions. Returns null if any
    //          character in the word is missing from the keyMap. Logs first failure per
    //          batch to avoid log spam while still providing diagnostic info.
    // =================================================================================

    // =================================================================================
    // FUNCTION: getOrCreateTemplate (With Micro-Loops for Double Letters)
    // =================================================================================
    private fun getOrCreateTemplate(word: String, keyMap: Map<String, PointF>): WordTemplate? {
        templateCache[word]?.let { return it }

        val rawPoints = ArrayList<PointF>()
        var lastKeyPos: PointF? = null
        
        for (char in word) {
            val keyPos = keyMap[char.toString().uppercase()] ?: keyMap[char.toString().lowercase()] ?: return null
            

            // DOUBLE LETTER LOGIC:
            if (lastKeyPos != null && keyPos.x == lastKeyPos.x && keyPos.y == lastKeyPos.y) {
                // INCREASED: 10f -> 15f. 
                // Makes the "circle" gesture easier to register.
                rawPoints.add(PointF(keyPos.x + 15f, keyPos.y + 15f))
            }
            
            rawPoints.add(PointF(keyPos.x, keyPos.y))
            lastKeyPos = keyPos
        }

        if (rawPoints.size < 2) return null

        val t = WordTemplate(word, getWordRank(word), rawPoints)
        templateCache[word] = t
        return t
    }

    // =================================================================================
    // END BLOCK: getOrCreateTemplate
    // =================================================================================

/**
     * Calculates the total absolute length of a path in pixels.
     */
    private fun getPathLength(points: List<PointF>): Float {
        if (points.size < 2) return 0f
        var length = 0f
        for (i in 0 until points.size - 1) {
            length += hypot(points[i+1].x - points[i].x, points[i+1].y - points[i].y)
        }
        return length
    }
    /**
     * Uniformly sample N points along a path.
     * This makes paths of different lengths comparable.
     */
    private fun samplePath(path: List<PointF>, numSamples: Int): List<PointF> {
        if (path.size < 2) return path
        if (path.size == numSamples) return path
        
        // Calculate total path length
        var totalLength = 0f
        for (i in 1 until path.size) {
            totalLength += hypot(path[i].x - path[i-1].x, path[i].y - path[i-1].y)
        }
        
        if (totalLength < 0.001f) {
            // Path is essentially a point - return copies of first point
            return List(numSamples) { PointF(path[0].x, path[0].y) }
        }
        
        val segmentLength = totalLength / (numSamples - 1)
        val sampled = ArrayList<PointF>(numSamples)
        sampled.add(PointF(path[0].x, path[0].y))
        
        var currentDist = 0f
        var pathIndex = 0
        var targetDist = segmentLength
        
        while (sampled.size < numSamples - 1 && pathIndex < path.size - 1) {
            val p1 = path[pathIndex]
            val p2 = path[pathIndex + 1]
            val segLen = hypot(p2.x - p1.x, p2.y - p1.y)
            
            while (currentDist + segLen >= targetDist && sampled.size < numSamples - 1) {
                val ratio = (targetDist - currentDist) / segLen
                val x = p1.x + ratio * (p2.x - p1.x)
                val y = p1.y + ratio * (p2.y - p1.y)
                sampled.add(PointF(x, y))
                targetDist += segmentLength
            }
            
            currentDist += segLen
            pathIndex++
        }
        
        // Ensure we have exactly numSamples by adding the last point
        while (sampled.size < numSamples) {
            sampled.add(PointF(path.last().x, path.last().y))
        }
        
        return sampled
    }
    
    /**
     * Normalize a path to fit within a square of size NORMALIZATION_SIZE.
     * This removes scale and translation differences for shape comparison.
     */
    private fun normalizePath(path: List<PointF>): List<PointF> {
        if (path.isEmpty()) return path
        
        // Find bounding box
        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE
        
        for (pt in path) {
            minX = min(minX, pt.x)
            maxX = max(maxX, pt.x)
            minY = min(minY, pt.y)
            maxY = max(maxY, pt.y)
        }
        
        val width = maxX - minX
        val height = maxY - minY
        val maxDim = max(width, height)
        
        if (maxDim < 0.001f) {
            // All points are the same - return centered points
            return path.map { PointF(NORMALIZATION_SIZE / 2, NORMALIZATION_SIZE / 2) }
        }
        
        // Scale to NORMALIZATION_SIZE and center at origin
        val scale = NORMALIZATION_SIZE / maxDim
        val centerX = (minX + maxX) / 2
        val centerY = (minY + maxY) / 2
        
        return path.map { pt ->
            PointF(
                (pt.x - centerX) * scale + NORMALIZATION_SIZE / 2,
                (pt.y - centerY) * scale + NORMALIZATION_SIZE / 2
            )
        }
    }
    
    /**
     * Calculate shape score between two normalized paths.
     * Uses average point-to-point distance.
     * Lower score = better match.
     */
    private fun calculateShapeScore(input: List<PointF>, template: List<PointF>): Float {
        if (input.size != template.size) return Float.MAX_VALUE
        
        var totalDist = 0f
        for (i in input.indices) {
            val dx = input[i].x - template[i].x
            val dy = input[i].y - template[i].y
            totalDist += sqrt(dx * dx + dy * dy)
        }
        
        return totalDist / input.size
    }
    
    /**
     * Calculate location score between two paths (absolute positions).
     * Uses average point-to-point distance with position weighting.
     * Points at the beginning and end are weighted more heavily.
     * Lower score = better match.
     */

    private fun calculateLocationScore(input: List<PointF>, template: List<PointF>): Float {
        var totalDist = 0f
        var totalWeight = 0f
        val size = input.size
        
        for (i in input.indices) {
            val dist = hypot(input[i].x - template[i].x, input[i].y - template[i].y)
            
            // --- ENDPOINT WEIGHTING (Strict) ---
            // Start: 3.0x
            // End:   5.0x (Crucial for "Swipe" vs "Swiped")
            // Middle: 1.0x
            val w = when {
                i < size * 0.15 -> 3.0f
                i > size * 0.85 -> 5.0f // Heavy penalty for missing the last key
                else -> 1.0f
            }
            
            totalDist += dist * w
            totalWeight += w
        }
        return totalDist / totalWeight
    }



    // =================================================================================
    // NEW: DIRECTION SCORING HELPERS
    // =================================================================================
    

    private fun calculateDirectionVectors(path: List<PointF>): List<PointF> {
        val vectors = ArrayList<PointF>()
        for (i in 0 until path.size - 1) {
            val dx = path[i+1].x - path[i].x
            val dy = path[i+1].y - path[i].y
            val len = hypot(dx, dy)
            if (len > 0.001f) {
                vectors.add(PointF(dx/len, dy/len))
            } else {
                // Return 0,0 for stationary segments (handled by Score function now)
                vectors.add(PointF(0f, 0f))
            }
        }
        return vectors
    }

    // =================================================================================
    // FUNCTION: calculateDirectionScore (Fixed for Double Letters & Pauses)
    // =================================================================================

    // =================================================================================
    // FUNCTION: calculateDirectionScore (Linear + Stationary Skip)
    // =================================================================================
    private fun calculateDirectionScore(input: List<PointF>, template: List<PointF>): Float {
        var totalScore = 0f
        var validPoints = 0
        
        val count = min(input.size, template.size)
        if (count == 0) return 0f

        for (i in 0 until count) {
            val v1 = input[i]
            val v2 = template[i]
            
            // Skip stationary segments (Pause/Tap)
            if ((v2.x == 0f && v2.y == 0f) || (v1.x == 0f && v1.y == 0f)) {
                continue
            }

            // Dot Product: 1.0 = aligned, -1.0 = opposite
            val dot = v1.x * v2.x + v1.y * v2.y
            
            // Penalty: Linear (Robust for complex words like "Either")
            // Reverted from Squared to forgive messy scribbles.
            totalScore += (1.0f - dot)
            validPoints++
        }
        
        return if (validPoints > 0) totalScore / validPoints else 0f
    }

// =================================================================================
    // FUNCTION: detectTurns
    // SUMMARY: Detects significant direction changes (turns/corners) in a path.
    //          Returns a list of (position, angle) pairs where position is 0.0-1.0
    //          along the path and angle is the turn magnitude in radians.
    //          A turn is detected when consecutive direction vectors differ significantly.
    // =================================================================================
// =================================================================================
    // FUNCTION: detectTurns (v2 - Sharp Corner Detection)
    // SUMMARY: Detects significant direction changes with emphasis on SHARP corners.
    //          Sharp corners = abrupt changes over 1-2 points (more intentional)
    //          Rounded corners = gradual changes over many points (less distinctive)
    //          Returns list of (position, sharpness) where sharpness indicates
    //          how abrupt the turn was (higher = sharper).
    // =================================================================================

    // =================================================================================
    // FUNCTION: detectTurns (v3 - Ultra-Sharp Emphasis)
    // =================================================================================
    private fun detectTurns(directions: List<PointF>): List<Pair<Float, Float>> {
        if (directions.size < 3) return emptyList()
        
        val turns = ArrayList<Pair<Float, Float>>()
        
        for (i in 0 until directions.size - 1) {
            val curr = directions[i]
            val next = directions[i + 1]
            
            if ((curr.x == 0f && curr.y == 0f) || (next.x == 0f && next.y == 0f)) continue
            
            // Dot product: 1.0 = straight, 0 = 90deg, -1 = U-turn
            val dot = curr.x * next.x + curr.y * next.y
            
            // ULTRA SHARP CORNER (e.g. "W", "Z", "M")
            // dot < 0.2 means angle > 78 degrees
            if (dot < 0.2f) {
                val position = i.toFloat() / directions.size.toFloat()
                // AMPLIFIED: Scale sharpness by 3.0x (was 1.5x)
                // This makes a sharp corner worth 3 "normal" curves
                val sharpness = (1.0f - dot) * 3.0f  
                turns.add(Pair(position, sharpness))
            }
            // MEDIUM CORNER (Standard curve)
            // dot < 0.6 means angle > 53 degrees
            else if (dot < 0.6f) {
                val position = i.toFloat() / directions.size.toFloat()
                val sharpness = (1.0f - dot) * 0.8f // Reduce weight of soft curves
                turns.add(Pair(position, sharpness))
            }
        }
        
        // Secondary pass for "spread out" turns (U-turns that take 3 points)
        for (i in 0 until directions.size - 3) {
            val curr = directions[i]
            val later = directions[i + 3]
            
            if ((curr.x == 0f && curr.y == 0f) || (later.x == 0f && later.y == 0f)) continue
            
            val dot = curr.x * later.x + curr.y * later.y
            
            if (dot < 0.1f) {  
                val position = (i + 1.5f) / directions.size.toFloat()
                
                val nearbyTurn = turns.any { abs(it.first - position) < 0.08f }
                if (!nearbyTurn) {
                    val sharpness = (1.0f - dot) * 2.0f // Boost U-turns too
                    turns.add(Pair(position, sharpness))
                }
            }
        }
        
        return turns.sortedBy { it.first }
    }

    // =================================================================================
    // END BLOCK: detectTurns
    // =================================================================================
    // =================================================================================
    // END BLOCK: detectTurns
    // =================================================================================

    // =================================================================================
    // FUNCTION: calculateTurnScore
    // SUMMARY: Compares turn patterns between input and template.
    //          Rewards matching turn counts and positions, penalizes mismatches.
    //          Lower score = better match.
    // =================================================================================
// =================================================================================
    // FUNCTION: calculateTurnScore (v2 - Sharp Corner Emphasis)
    // SUMMARY: Compares turn patterns with HEAVY emphasis on matching sharp corners.
    //          - Matching sharp corners = big reward (low score)
    //          - Missing sharp corners = big penalty (high score)
    //          - Turn count matters less than turn positions and sharpness
    // =================================================================================

    // =================================================================================
    // FUNCTION: calculateTurnScore (v3 - Sharp Corner Enforcement)
    // =================================================================================
    private fun calculateTurnScore(inputTurns: List<Pair<Float, Float>>, templateTurns: List<Pair<Float, Float>>): Float {
        if (inputTurns.isEmpty() && templateTurns.isEmpty()) return 0f
        
        var score = 0f
        
        // Filter for "Significant" turns (Sharpness > 1.5, which corresponds to our new 3.0x scale)
        val templateSharpTurns = templateTurns.filter { it.second > 1.5f }
        val inputSharpTurns = inputTurns.filter { it.second > 1.5f }
        
        // HEAVY PENALTY for mismatching sharp corners
        if (templateSharpTurns.isNotEmpty() && inputSharpTurns.isEmpty()) {
            // Template has a sharp corner (like "Android" 'N') but user missed it -> Fatal
            score += 1.5f 
        } else if (templateSharpTurns.isEmpty() && inputSharpTurns.isNotEmpty()) {
            // User made a sharp jerk where none belongs -> Fatal
            score += 1.0f
        }
        
        val usedTemplate = BooleanArray(templateTurns.size)
        var matchedTurns = 0
        var totalSharpnessMatch = 0f
        
        for (inputTurn in inputTurns) {
            var bestMatch = -1
            var bestScore = Float.MAX_VALUE
            
            for (j in templateTurns.indices) {
                if (usedTemplate[j]) continue
                
                val posDist = abs(inputTurn.first - templateTurns[j].first)
                val sharpnessDiff = abs(inputTurn.second - templateTurns[j].second)
                
                // Position tolerance: 20%
                if (posDist < 0.20f) {
                    val matchScore = posDist * 2f + sharpnessDiff * 0.5f
                    if (matchScore < bestScore) {
                        bestScore = matchScore
                        bestMatch = j
                    }
                }
            }
            
            if (bestMatch >= 0) {
                usedTemplate[bestMatch] = true
                matchedTurns++
                totalSharpnessMatch += bestScore
                
                // HUGE REWARD: If both are sharp and matched
                if (inputTurn.second > 1.5f && templateTurns[bestMatch].second > 1.5f) {
                    score -= 0.5f  // Massive bonus for hitting the corner
                }
            } else {
                // Penalty for extra turns in input
                score += 0.2f + inputTurn.second * 0.2f
            }
        }
        
        // Penalty for missing turns in template
        for (j in templateTurns.indices) {
            if (!usedTemplate[j]) {
                score += 0.2f + templateTurns[j].second * 0.2f
            }
        }
        
        if (matchedTurns > 0) {
            score += totalSharpnessMatch / matchedTurns * 0.3f
        }
        
        return max(0f, score)
    }

    // =================================================================================
    // END BLOCK: calculateTurnScore
    // =================================================================================
    // =================================================================================
    // END BLOCK: calculateTurnScore
    // =================================================================================



    /**
     * Find all keys within threshold distance of a point.
     * Returns a Map of Key -> Distance for O(1) lookups.
     */
    private fun findKeysWithDist(pt: PointF, keyMap: Map<String, PointF>, threshold: Float): Map<String, Float> {
        val keys = HashMap<String, Float>()
        for ((key, pos) in keyMap) {
            if (key.length != 1) continue
            val dist = hypot(pt.x - pos.x, pt.y - pos.y)
            if (dist < threshold) {
                // If key exists (e.g. from another case), keep smallest dist
                val existing = keys[key.lowercase()]
                if (existing == null || dist < existing) {
                    keys[key.lowercase()] = dist
                }
            }
        }
        return keys
    }
    
    private fun getWordRank(word: String): Int {
        var current = root
        for (char in word) {
            current = current.children[char] ?: return Int.MAX_VALUE
        }
        return if (current.isEndOfWord) current.rank else Int.MAX_VALUE
    }
    
    // =================================================================================
    // END BLOCK: SHARK2-INSPIRED SWIPE DECODER LOGIC
    // =================================================================================


    /**
     * Blocks a word permanently:
     * 1. Adds to memory.
     * 2. Removes from active lists.
     * 3. Removes from user stats. 
     * 4. Saves to file.
     */

    fun blockWord(context: Context, word: String) {
        val cleanWord = word.trim().lowercase(java.util.Locale.ROOT)
        if (cleanWord.isEmpty()) return

        Thread {
            try {
                synchronized(this) {
                    blockedWords.add(cleanWord)
                    customWords.remove(cleanWord)
                    wordList.remove(cleanWord)
                    
                    if (cleanWord.isNotEmpty()) {
                        wordsByFirstLetter[cleanWord.first()]?.remove(cleanWord)
                        if (cleanWord.length >= 2) {
                            wordsByFirstLastLetter["${cleanWord.first()}${cleanWord.last()}"]?.remove(cleanWord)
                        }
                    }
                    
                    synchronized(userFrequencyMap) {
                        userFrequencyMap.remove(cleanWord)
                    }
                    templateCache.remove(cleanWord)
                    
                    // FIX: Save files inside synchronized block to prevent ConcurrentModification and race conditions
                    saveSetToFile(context, BLOCKED_DICT_FILE, blockedWords)
                    saveSetToFile(context, USER_DICT_FILE, customWords)
                }
                
                saveUserStats(context)

                android.util.Log.d("DroidOS_Prediction", "BLOCKED: '$cleanWord'")
            } catch (e: Exception) {
                android.util.Log.e("DroidOS_Prediction", "Block failed", e)
            }
        }.start()
    }


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

class SettingsActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)

        // 1. Define UI Elements
        val swTapScroll = findViewById<Switch>(R.id.swTapScroll)
        val swVibrate = findViewById<Switch>(R.id.swVibrate)
        val swReverseScroll = findViewById<Switch>(R.id.swReverseScroll) // Matches XML
        val swVPos = findViewById<Switch>(R.id.swVPos)
        val swHPos = findViewById<Switch>(R.id.swHPos)
        
        // ... rest of the file ...

        
        val seekBarCursor = findViewById<SeekBar>(R.id.seekBarCursorSpeed)
        val seekBarScroll = findViewById<SeekBar>(R.id.seekBarScrollSpeed)
        val seekCursorSize = findViewById<SeekBar>(R.id.seekBarCursorSize)
        val seekAlpha = findViewById<SeekBar>(R.id.seekBarAlpha)
        val seekHandleSize = findViewById<SeekBar>(R.id.seekBarHandleSize)
        val seekScrollVisual = findViewById<SeekBar>(R.id.seekBarScrollVisual)
        
        val seekHandleTouch = findViewById<SeekBar>(R.id.seekBarHandleTouch)
        val seekScrollTouch = findViewById<SeekBar>(R.id.seekBarScrollTouch)
        
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // =================================================================================
        // 2. LOAD SAVED VALUES
        // =================================================================================
        swTapScroll.isChecked = prefs.getBoolean("tap_scroll", true)
        swVibrate.isChecked = prefs.getBoolean("vibrate", true)
        swReverseScroll.isChecked = prefs.getBoolean("reverse_scroll", false)
        swVPos.isChecked = prefs.getBoolean("v_pos_left", false)
        swHPos.isChecked = prefs.getBoolean("h_pos_top", false)
        
        seekBarCursor.progress = ((prefs.getFloat("cursor_speed", 2.5f)) * 10).toInt()
        seekBarScroll.progress = ((prefs.getFloat("scroll_speed", 1.0f)) * 10).toInt()
        
        seekCursorSize.progress = prefs.getInt("cursor_size", 50)
        seekAlpha.progress = prefs.getInt("alpha", 50)
        seekHandleSize.progress = prefs.getInt("handle_size", 14)
        seekScrollVisual.progress = prefs.getInt("scroll_visual_size", 4)
        
        seekHandleTouch.progress = prefs.getInt("handle_touch_size", 60)
        seekScrollTouch.progress = prefs.getInt("scroll_touch_size", 60)

        // =================================================================================
        // 3. LISTENERS
        // =================================================================================
        fun createPreviewListener(target: String): SeekBar.OnSeekBarChangeListener {
            return object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val intent = Intent(this@SettingsActivity, OverlayService::class.java)
                    intent.action = "PREVIEW_UPDATE"
                    intent.putExtra("TARGET", target)
                    intent.putExtra("VALUE", progress)
                    startService(intent)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        }

        seekCursorSize.setOnSeekBarChangeListener(createPreviewListener("cursor_size"))
        seekAlpha.setOnSeekBarChangeListener(createPreviewListener("alpha"))
        seekHandleSize.setOnSeekBarChangeListener(createPreviewListener("handle_size"))
        seekScrollVisual.setOnSeekBarChangeListener(createPreviewListener("scroll_visual"))
        seekHandleTouch.setOnSeekBarChangeListener(createPreviewListener("handle_touch"))
        seekScrollTouch.setOnSeekBarChangeListener(createPreviewListener("scroll_touch"))

        // =================================================================================
        // 4. SAVE BUTTON LOGIC
        // =================================================================================
        btnSave.setOnClickListener {
            val cVal = if (seekBarCursor.progress < 1) 0.1f else seekBarCursor.progress / 10f
            val sVal = if (seekBarScroll.progress < 1) 0.1f else seekBarScroll.progress / 10f
            
            prefs.edit()
                .putFloat("cursor_speed", cVal)
                .putFloat("scroll_speed", sVal)
                .putBoolean("tap_scroll", swTapScroll.isChecked)
                .putBoolean("vibrate", swVibrate.isChecked)
                .putBoolean("reverse_scroll", swReverseScroll.isChecked)
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
}
```

## File: app/src/main/java/com/example/coverscreentester/ShellUserService.kt
```kotlin
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
        return false
    }

    // --- INPUT INJECTION ---
    override fun injectKey(keyCode: Int, action: Int, metaState: Int, displayId: Int, deviceId: Int) {
        if (!this::inputManager.isInitialized) return
        val now = SystemClock.uptimeMillis()
        
        // CRITICAL CONFIGURATION:
        // Device ID = 1 (Mimics the "Hardware Keyboard" we use to block soft-kb)
        // Scan Code = 0 (Generic/Ignore). Setting this to 1 caused the buffering/reject issue.
        val forcedDeviceId = 1 
        val finalScanCode = 0 
        val finalFlags = 8 // FLAG_FROM_SYSTEM
        
        val event = KeyEvent(
            now, now, action, keyCode, 0, metaState, 
            forcedDeviceId, finalScanCode, finalFlags, 
            InputDevice.SOURCE_KEYBOARD
        )
        
        try {
            // Restore Display Targeting
            // We MUST target the display where the user is looking.
            val method = InputEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            method.invoke(event, displayId)
            
            injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
        } catch (e: Exception) {
            // Fallback
            if (action == KeyEvent.ACTION_DOWN) execShell("input keyevent $keyCode")
        }
    }

    // Trigger to force system to update "Hardware Keyboard" status immediately
    override fun injectDummyHardwareKey(displayId: Int) {
         if (!this::inputManager.isInitialized) return
         val now = SystemClock.uptimeMillis()
         
         // Use SAME ID (1) as the text injection to maintain consistency
         val eventDown = KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0, 1, 1, 8, InputDevice.SOURCE_KEYBOARD)
         val eventUp = KeyEvent(now, now, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0, 1, 1, 8, InputDevice.SOURCE_KEYBOARD)
         
         try {
            val method = InputEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            method.invoke(eventDown, displayId)
            method.invoke(eventUp, displayId)
            
            injectInputEventMethod.invoke(inputManager, eventDown, INJECT_MODE_ASYNC)
            injectInputEventMethod.invoke(inputManager, eventUp, INJECT_MODE_ASYNC)
         } catch(e: Exception) {}
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
        // Fix: Use SOURCE_TOUCHSCREEN to mimic a physical Finger tap
        // This is more reliable than Mouse events for Android UI elements
        injectInternal(MotionEvent.ACTION_DOWN, x, y, displayId, now, now, InputDevice.SOURCE_TOUCHSCREEN, 0)
        try { Thread.sleep(60) } catch (e: InterruptedException) {}
        injectInternal(MotionEvent.ACTION_UP, x, y, displayId, now, now+60, InputDevice.SOURCE_TOUCHSCREEN, 0)
    }

    override fun execRightClick(x: Float, y: Float, displayId: Int) {
        val now = SystemClock.uptimeMillis()
        // Right click must remain MOUSE (Android doesn't have right-click for touch)
        injectInternal(MotionEvent.ACTION_DOWN, x, y, displayId, now, now, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_SECONDARY)
        try { Thread.sleep(60) } catch (e: InterruptedException) {}
        injectInternal(MotionEvent.ACTION_UP, x, y, displayId, now, now+60, InputDevice.SOURCE_MOUSE, 0)
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
```

## File: app/src/main/java/com/example/coverscreentester/SwipeTrailView.kt
```kotlin
package com.example.coverscreentester

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View

// =================================================================================
// CLASS: SwipeTrailView
// SUMMARY: A custom View that renders a trail/path for swipe gestures.
//          Supports configurable trail color for:
//          - Blue (0x00A0E9) - Normal swipe typing trail
//          - Orange (0xFF9900) - Virtual mirror orientation trail
//          The view is non-interactive and passes all touches through.
// =================================================================================
class SwipeTrailView(context: Context) : View(context) {

    private val path = Path()
    private val paint = Paint().apply {
        color = Color.parseColor("#00A0E9") // DroidOS Blue (default)
        style = Paint.Style.STROKE
        strokeWidth = 12f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        alpha = 180
    }

    init {
        // Important: Pass touches through this view so the keyboard underneath gets them
        isClickable = false
        isFocusable = false
    }

    // =================================================================================
    // FUNCTION: setTrailColor
    // SUMMARY: Sets the color of the trail. Used to differentiate between:
    //          - Blue trail for normal swipe typing
    //          - Orange trail for virtual mirror orientation mode
    // @param color - ARGB color integer (e.g., 0xFFFF9900 for orange)
    // =================================================================================
    fun setTrailColor(color: Int) {
        paint.color = color
        // Preserve alpha transparency
        paint.alpha = 180
        invalidate()
    }
    // =================================================================================
    // END BLOCK: setTrailColor
    // =================================================================================

    // =================================================================================
    // FUNCTION: resetToDefaultColor
    // SUMMARY: Resets trail color back to DroidOS blue (default swipe color).
    // =================================================================================
    fun resetToDefaultColor() {
        paint.color = Color.parseColor("#00A0E9")
        paint.alpha = 180
        invalidate()
    }
    // =================================================================================
    // END BLOCK: resetToDefaultColor
    // =================================================================================

    // =================================================================================
    // FUNCTION: addPoint
    // SUMMARY: Adds a point to the trail path. Called during touch move events.
    // @param x - X coordinate relative to the view
    // @param y - Y coordinate relative to the view
    // =================================================================================
    fun addPoint(x: Float, y: Float) {
        if (path.isEmpty) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
        invalidate()
    }
    // =================================================================================
    // END BLOCK: addPoint
    // =================================================================================

    // =================================================================================
    // FUNCTION: clear
    // SUMMARY: Clears the trail path. Called when swipe ends or orientation mode exits.
    // =================================================================================
    fun clear() {
        path.reset()
        invalidate()
    }
    // =================================================================================
    // END BLOCK: clear
    // =================================================================================

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }
}
// =================================================================================
// END CLASS: SwipeTrailView
// =================================================================================
```

## File: app/src/main/java/com/example/coverscreentester/TrackpadMenuAdapter.kt
```kotlin
package com.example.coverscreentester

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrackpadMenuAdapter(private val items: List<MenuItem>) : 
    RecyclerView.Adapter<TrackpadMenuAdapter.Holder>() {

    data class MenuItem(
        val title: String,
        val iconRes: Int,
        val type: Type,
        val initValue: Int = 0, // For sliders (0-100) or Toggles (0/1)
        val max: Int = 100,     // NEW: Custom max value for sliders
        val action: ((Any) -> Unit)? = null // Callback
    )

    enum class Type {
        ACTION, // Simple Click
        TOGGLE, // Switch
        SLIDER, // SeekBar
        DPAD,   // Directional Pad
        INFO,   // Text only
        HEADER, // Main Section Header (Bold, White)
        SUBHEADER // Sub-section Header (Small, Grey)
    }

    inner class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.item_icon)
        val title: TextView = v.findViewById(R.id.item_title)
        val valueText: TextView = v.findViewById(R.id.item_value_text)
        val switch: Switch = v.findViewById(R.id.item_switch)
        val actionIcon: ImageView = v.findViewById(R.id.item_action_icon)
        val slider: SeekBar = v.findViewById(R.id.item_seekbar)
        val dpadGrid: GridLayout = v.findViewById(R.id.item_dpad_grid)
        val helpText: TextView = v.findViewById(R.id.item_help_text)

        // D-Pad Buttons
        val btnUp: Button = v.findViewById(R.id.btn_up)
        val btnDown: Button = v.findViewById(R.id.btn_down)
        val btnLeft: Button = v.findViewById(R.id.btn_left)
        val btnRight: Button = v.findViewById(R.id.btn_right)
        val btnCenter: Button = v.findViewById(R.id.btn_center)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trackpad_menu, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        
        // Reset Visibility & Style
        (holder.itemView as android.view.ViewGroup).getChildAt(0).visibility = View.VISIBLE
        holder.valueText.visibility = View.GONE
        holder.switch.visibility = View.GONE
        holder.actionIcon.visibility = View.GONE
        holder.slider.visibility = View.GONE
        holder.dpadGrid.visibility = View.GONE
        holder.helpText.visibility = View.GONE
        holder.title.visibility = View.VISIBLE
        holder.icon.visibility = View.VISIBLE
        
        holder.title.text = item.title
        holder.icon.setImageResource(item.iconRes)
        
        // Default text style (Reset)
        holder.title.setTypeface(null, android.graphics.Typeface.NORMAL)
        holder.title.setTextColor(Color.WHITE)
        holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        
        // Reset Item Background/Padding
        holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
        holder.itemView.setPadding(0,0,0,0) // Reset padding
        
        // Reset click listener
        holder.itemView.setOnClickListener(null)

        when (item.type) {
            Type.ACTION -> {
                holder.actionIcon.visibility = View.GONE
                holder.itemView.setOnClickListener { item.action?.invoke(true) }
            }
            Type.TOGGLE -> {
                holder.switch.visibility = View.VISIBLE
                holder.switch.isChecked = item.initValue == 1
                holder.switch.setOnCheckedChangeListener { _, isChecked ->
                    item.action?.invoke(isChecked)
                }
                holder.itemView.setOnClickListener { holder.switch.toggle() }
            }
            Type.SLIDER -> {
                holder.valueText.visibility = View.VISIBLE
                holder.slider.visibility = View.VISIBLE
                holder.slider.max = item.max
                holder.valueText.text = "${item.initValue}"
                holder.slider.progress = item.initValue
                
                holder.slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, v: Int, fromUser: Boolean) {
                        if (fromUser) {
                            holder.valueText.text = "$v"
                            item.action?.invoke(v)
                        }
                    }
                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) {}
                })
            }
            Type.DPAD -> {
                holder.dpadGrid.visibility = View.VISIBLE
                holder.btnUp.setOnClickListener { item.action?.invoke("UP") }
                holder.btnDown.setOnClickListener { item.action?.invoke("DOWN") }
                holder.btnLeft.setOnClickListener { item.action?.invoke("LEFT") }
                holder.btnRight.setOnClickListener { item.action?.invoke("RIGHT") }
                holder.btnCenter.setOnClickListener { item.action?.invoke("CENTER") }
            }
            Type.INFO -> {
                (holder.itemView as android.view.ViewGroup).getChildAt(0).visibility = View.GONE
                holder.helpText.visibility = View.VISIBLE
                holder.helpText.text = item.title 
                holder.title.visibility = View.GONE 
                holder.icon.visibility = View.GONE
                holder.itemView.setBackgroundResource(android.R.color.transparent) // No press effect for INFO
            }
            Type.HEADER -> {
                holder.title.visibility = View.VISIBLE
                holder.title.text = item.title
                holder.title.setTypeface(null, android.graphics.Typeface.BOLD)
                holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Larger
                holder.icon.visibility = View.GONE
                holder.itemView.setBackgroundResource(android.R.color.transparent) // No press effect
            }
            Type.SUBHEADER -> {
                holder.title.visibility = View.VISIBLE
                holder.title.text = item.title
                holder.title.setTypeface(null, android.graphics.Typeface.NORMAL)
                holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f) // Small
                holder.title.setTextColor(Color.parseColor("#AAAAAA")) // Grey
                holder.icon.visibility = View.GONE
                holder.itemView.setBackgroundResource(android.R.color.transparent) // No press effect
                // Optional: Add top margin if possible via params, or just rely on list order
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
```

## File: app/src/main/java/com/example/coverscreentester/TrackpadMenuManager.kt
```kotlin
package com.example.coverscreentester

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import android.view.ViewGroup
import android.view.MotionEvent
import java.util.ArrayList

class TrackpadMenuManager(
    private val context: Context,
    private val windowManager: WindowManager,
    private val service: OverlayService
) {
    private var drawerView: View? = null
    private var recyclerView: RecyclerView? = null
    private var drawerParams: WindowManager.LayoutParams? = null
    private var isVisible = false
    
    // Manual Adjust State
    private var isResizeMode = false // Default to Move Mode

    // Tab Constants - Order must match layout_trackpad_drawer.xml tab order
    private val TAB_MAIN = 0
    private val TAB_PRESETS = 1
    private val TAB_MOVE = 2
    private val TAB_KB_MOVE = 3
    private val TAB_MIRROR = 4      // NEW: Mirror keyboard config
    private val TAB_CONFIG = 5
    private val TAB_TUNE = 6
    private val TAB_HARDKEYS = 7
    private val TAB_BUBBLE = 8
    private val TAB_PROFILES = 9
    private val TAB_HELP = 10
    
    private var currentTab = TAB_MAIN
    
    // [NEW] Debug Trigger Vars
    private var helpClickCount = 0
    private var lastHelpClickTime = 0L

    fun show() {
        if (isVisible) return
        if (drawerView == null) setupDrawer()
        try {
            windowManager.addView(drawerView, drawerParams)
            isVisible = true
            loadTab(currentTab)
            
            // CRITICAL FIX: Force Cursor and Bubble to top of stack
            // Since Menu was just added, it is currently on top. 
            // We must re-add the others to cover it.
            service.enforceZOrder()
            
        } catch (e: SecurityException) {
            android.widget.Toast.makeText(context, "Missing Overlay Permission! Open App to Fix.", android.widget.Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } catch (e: Exception) { 
            e.printStackTrace() 
        }
    }

    fun hide() {
        if (!isVisible) return
        try {
            windowManager.removeView(drawerView)
            isVisible = false
        } catch (e: Exception) { }
    }

    fun toggle() {
        if (isVisible) hide() else show()
    }

    fun bringToFront() {
        if (!isVisible || drawerView == null) return
        try {
            // Detach and Re-attach to move to top of Z-Order stack
            windowManager.removeView(drawerView)
            windowManager.addView(drawerView, drawerParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupDrawer() {
        // Use ContextWrapper to ensure correct theme (Matches Launcher)
        val themedContext = android.view.ContextThemeWrapper(context, R.style.Theme_CoverScreenTester)
        val inflater = LayoutInflater.from(themedContext)
        drawerView = inflater.inflate(R.layout.layout_trackpad_drawer, null)

        // FUNCTION: Force Tab Icons
        // Manually set the Mirror Tab icon to the new custom asset
        drawerView?.findViewById<ImageView>(R.id.tab_mirror)?.setImageResource(R.drawable.mirrorkb)

        // Close button logic
        drawerView?.findViewById<View>(R.id.btn_close_menu)?.setOnClickListener { hide() }
        
        recyclerView = drawerView?.findViewById(R.id.menu_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        // Tab click listeners
        val tabs = listOf(
            R.id.tab_main to TAB_MAIN,
            R.id.tab_presets to TAB_PRESETS,
            R.id.tab_move to TAB_MOVE,
            R.id.tab_kb_move to TAB_KB_MOVE,
            R.id.tab_mirror to TAB_MIRROR,      // NEW
            R.id.tab_config to TAB_CONFIG,
            R.id.tab_tune to TAB_TUNE,
            R.id.tab_hardkeys to TAB_HARDKEYS,
            R.id.tab_bubble to TAB_BUBBLE,
            R.id.tab_profiles to TAB_PROFILES,
            R.id.tab_help to TAB_HELP
        )

        for ((id, index) in tabs) {
            drawerView?.findViewById<ImageView>(id)?.setOnClickListener { 
                // [FIX] INSTRUCTIONS TAB - DEBUG TRIGGER (5 Clicks)
                if (id == R.id.tab_help) {
                    val now = System.currentTimeMillis()
                    if (now - lastHelpClickTime < 500) {
                        helpClickCount++
                    } else {
                        helpClickCount = 1
                    }
                    lastHelpClickTime = now

                    if (helpClickCount >= 5) {
                        // Toggle debug mode in the main Service
                        service.toggleDebugMode()
                        helpClickCount = 0
                    }
                }
                
                loadTab(index) 
            }
        }

        // =========================
        // WINDOW CONFIG (MATCHING DROIDOS LAUNCHER)
        // =========================
        drawerParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, 
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or 
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, 
            PixelFormat.TRANSLUCENT
        )
        // Explicitly set Gravity to TOP|START for absolute positioning
        drawerParams?.gravity = Gravity.TOP or Gravity.START
        
        // Initial Center Calculation
        val metrics = context.resources.displayMetrics
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        
        // Approx Menu Size (320dp width + margins, ~400dp height)
        val density = metrics.density
        val menuW = (360 * density).toInt() 
        val menuH = (400 * density).toInt()
        
        drawerParams?.x = (screenWidth - menuW) / 2
        drawerParams?.y = (screenHeight - menuH) / 2
        
        // =========================
        // INTERACTION LOGIC
        // =========================
        // 1. Background Click -> Removed (Handled by FLAG_NOT_TOUCH_MODAL)
        
        // 2. Menu Card Click -> Block (Consume)
        drawerView?.findViewById<View>(R.id.menu_container)?.setOnClickListener { 
            // Do nothing
        }
        
        // 3. DRAG HANDLE LOGIC
        val dragHandle = drawerView?.findViewById<View>(R.id.menu_drag_handle)
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        dragHandle?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = drawerParams!!.x
                    initialY = drawerParams!!.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    drawerParams!!.x = initialX + (event.rawX - initialTouchX).toInt()
                    drawerParams!!.y = initialY + (event.rawY - initialTouchY).toInt()
                    
                    try {
                        windowManager.updateViewLayout(drawerView, drawerParams)
                    } catch (e: Exception) {}
                    true
                }
                else -> false
            }
        }
    }

    private fun loadTab(index: Int) {
        currentTab = index
        updateTabIcons(index)
        
        val items = when(index) {
            TAB_MAIN -> getMainItems()
            TAB_PRESETS -> getPresetItems()
            TAB_MOVE -> getMoveItems(false)
            TAB_KB_MOVE -> getMoveItems(true)
            TAB_MIRROR -> getMirrorItems()      // NEW
            TAB_CONFIG -> getConfigItems()
            TAB_TUNE -> getTuneItems()
            TAB_HARDKEYS -> getHardkeyItems()   // Hardkey bindings configuration
            TAB_BUBBLE -> getBubbleItems()
            TAB_PROFILES -> getProfileItems()
            TAB_HELP -> getHelpItems()
            else -> emptyList()
        }
        
        recyclerView?.adapter = TrackpadMenuAdapter(items)
    }

    private fun updateTabIcons(activeIdx: Int) {
        val tabIds = listOf(R.id.tab_main, R.id.tab_presets, R.id.tab_move, R.id.tab_kb_move, R.id.tab_mirror, R.id.tab_config, R.id.tab_tune, R.id.tab_hardkeys, R.id.tab_bubble, R.id.tab_profiles, R.id.tab_help)
        for ((i, id) in tabIds.withIndex()) {
            val view = drawerView?.findViewById<ImageView>(id)
            if (i == activeIdx) view?.setColorFilter(Color.parseColor("#3DDC84")) 
            else view?.setColorFilter(Color.GRAY)
        }
    }

    // =========================
    // GET MAIN ITEMS - Generates main menu items list
    // =========================
    private fun getMainItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("MAIN CONTROLS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // --- COMMENTED OUT PER REQUEST ---
        /*
        list.add(TrackpadMenuAdapter.MenuItem("Switch Screen (0 <-> 1)", android.R.drawable.ic_menu_rotate, TrackpadMenuAdapter.Type.ACTION) { 
            service.switchDisplay() 
            hide()
        })
        */
        // ---------------------------------

        list.add(TrackpadMenuAdapter.MenuItem("Reset Bubble Position", android.R.drawable.ic_menu_myplaces, TrackpadMenuAdapter.Type.ACTION) { 
            service.resetBubblePosition()
            hide()
        })
        
        // --- COMMENTED OUT PER REQUEST ---
        /*
        list.add(TrackpadMenuAdapter.MenuItem("Move Trackpad Here", R.drawable.ic_tab_move, TrackpadMenuAdapter.Type.ACTION) { service.forceMoveToCurrentDisplay(); hide() })
        */
        
        // Renamed: "Target: ..." -> "Toggle Remote Display"
        list.add(TrackpadMenuAdapter.MenuItem("Toggle Remote Display", R.drawable.ic_cursor, TrackpadMenuAdapter.Type.ACTION) { service.cycleInputTarget(); loadTab(TAB_MAIN) })

        // =================================================================================
        // VIRTUAL MIRROR MODE TOGGLE
        // SUMMARY: Enhanced toggle for AR glasses/remote displays.
        //          When enabled:
        //          - Auto-switches cursor to virtual display
        //          - Shows keyboard and trackpad
        //          - Loads mirror-mode specific profile
        //          When disabled:
        //          - Returns to local display
        //          - Restores previous visibility state
        //          - Saves/loads separate profile
        // =================================================================================
        // FUNCTION: Virtual Mirror Toggle
        // Updated to use R.drawable.mirrorkb
        list.add(TrackpadMenuAdapter.MenuItem(
            "Virtual Mirror Mode",
            R.drawable.mirrorkb,
            TrackpadMenuAdapter.Type.TOGGLE,
            if(p.prefVirtualMirrorMode) 1 else 0
        ) { _ ->
            service.toggleVirtualMirrorMode()
            hide()  // Close menu since display context may change
        })
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE TOGGLE
        // =================================================================================

        // --- ANCHOR TOGGLE: Locks trackpad and keyboard position/size ---
        list.add(TrackpadMenuAdapter.MenuItem("Anchor (Lock Position)", 
            if(p.prefAnchored) R.drawable.ic_lock_closed else R.drawable.ic_lock_open, 
            TrackpadMenuAdapter.Type.TOGGLE, 
            if(p.prefAnchored) 1 else 0) { v ->
            service.updatePref("anchored", v)
            loadTab(TAB_MAIN)  // Refresh to update icon
        })
        // --- END ANCHOR TOGGLE ---
        
        // Toggle Trackpad (Using correct icon
        // Toggle Trackpad
        list.add(TrackpadMenuAdapter.MenuItem("Toggle Trackpad", R.drawable.ic_cursor, TrackpadMenuAdapter.Type.ACTION) {
            service.toggleTrackpad()
            hide()
        })


        list.add(TrackpadMenuAdapter.MenuItem("Toggle Keyboard", R.drawable.ic_tab_keyboard, TrackpadMenuAdapter.Type.ACTION) { 
            if (service.isCustomKeyboardVisible) service.performSmartHide()
            else service.toggleCustomKeyboard()
        })
        list.add(TrackpadMenuAdapter.MenuItem("Reset Cursor", android.R.drawable.ic_menu_rotate, TrackpadMenuAdapter.Type.ACTION) { service.resetCursorCenter() })
        
        // Renamed: "Hide App" -> "Hide All"
        list.add(TrackpadMenuAdapter.MenuItem("Hide All", android.R.drawable.ic_menu_close_clear_cancel, TrackpadMenuAdapter.Type.ACTION) { service.hideApp() })
        
        // Renamed: "Force Kill Service" -> "Close/Restart App"
        list.add(TrackpadMenuAdapter.MenuItem("Close/Restart App", android.R.drawable.ic_delete, TrackpadMenuAdapter.Type.ACTION) { service.forceExit() })
        return list
    }
    // =========================
    // END GET MAIN ITEMS
    // =========================


    
// =========================
    // GET PRESET ITEMS - Layout presets for split screen modes
    // Freeform (type 0) loads saved profile, NOT a split preset
    // =========================
    private fun getPresetItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        list.add(TrackpadMenuAdapter.MenuItem("SPLIT SCREEN PRESETS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // Freeform FIRST - this loads the saved profile (not a split preset)
        list.add(TrackpadMenuAdapter.MenuItem("Freeform (Use Profile)", android.R.drawable.ic_menu_edit, TrackpadMenuAdapter.Type.ACTION) { 
            service.applyLayoutPreset(0)
            hide()
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("KB Top / TP Bottom", R.drawable.ic_tab_keyboard, TrackpadMenuAdapter.Type.ACTION) { 
            service.applyLayoutPreset(1)
            hide()
        })
        list.add(TrackpadMenuAdapter.MenuItem("TP Top / KB Bottom", R.drawable.ic_tab_move, TrackpadMenuAdapter.Type.ACTION) { 
            service.applyLayoutPreset(2)
            hide()
        })
        return list
    }
    // =========================
    // END GET PRESET ITEMS
    // =========================
    private fun getMoveItems(isKeyboard: Boolean): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val target = if (isKeyboard) "Keyboard" else "Trackpad"
        
        list.add(TrackpadMenuAdapter.MenuItem(if (isKeyboard) "KEYBOARD POSITION" else "TRACKPAD POSITION", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // 1. Mode Switcher (Toggle Item)
        val modeText = if (isResizeMode) "Resize (Size)" else "Position (Move)"
        val modeIcon = if (isResizeMode) android.R.drawable.ic_menu_crop else android.R.drawable.ic_menu_mylocation
        
        list.add(TrackpadMenuAdapter.MenuItem("Mode: $modeText", modeIcon, TrackpadMenuAdapter.Type.ACTION) {
            isResizeMode = !isResizeMode
            loadTab(currentTab) // Refresh UI to update text
        })
        
        // 2. The D-Pad
        val actionText = if (isResizeMode) "Resize" else "Move"
        list.add(TrackpadMenuAdapter.MenuItem("$target $actionText", R.drawable.ic_tab_move, TrackpadMenuAdapter.Type.DPAD) { cmd ->
            val step = 20
            val command = cmd as String
            
            // isResizeMode determines whether we move X/Y or change W/H
            when(command) {
                "UP" -> service.manualAdjust(isKeyboard, isResizeMode, 0, -step)
                "DOWN" -> service.manualAdjust(isKeyboard, isResizeMode, 0, step)
                "LEFT" -> service.manualAdjust(isKeyboard, isResizeMode, -step, 0)
                "RIGHT" -> service.manualAdjust(isKeyboard, isResizeMode, step, 0)
                "CENTER" -> {
                    if (isKeyboard) service.resetKeyboardPosition() else service.resetTrackpadPosition()
                }
            }
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Rotate 90°", android.R.drawable.ic_menu_rotate, TrackpadMenuAdapter.Type.ACTION) {
            if (isKeyboard) service.rotateKeyboard() else service.performRotation()
        })
            
        if (isKeyboard) {
            val p = service.prefs
            list.add(TrackpadMenuAdapter.MenuItem("Keyboard Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefKeyboardAlpha) { v ->
                service.updatePref("keyboard_alpha", v)
            })
        }
            
        return list
    }

    // =========================
    // GET MIRROR ITEMS - Mirror keyboard configuration
    // =========================
    private var isMirrorResizeMode = false
    

    private fun getMirrorItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("MIRROR KEYBOARD", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // [REMOVED] Virtual Mirror Mode Toggle (Redundant)
        
        list.add(TrackpadMenuAdapter.MenuItem("POSITION & SIZE", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        // Mode Switcher (Position vs Resize)
        val modeText = if (isMirrorResizeMode) "Resize (Size)" else "Position (Move)"
        val modeIcon = if (isMirrorResizeMode) android.R.drawable.ic_menu_crop else android.R.drawable.ic_menu_mylocation
        
        list.add(TrackpadMenuAdapter.MenuItem("Mode: $modeText", modeIcon, TrackpadMenuAdapter.Type.ACTION) {
            isMirrorResizeMode = !isMirrorResizeMode
            loadTab(currentTab) // Refresh UI
        })
        
        // D-Pad for position/size
        val actionText = if (isMirrorResizeMode) "Resize" else "Move"
        list.add(TrackpadMenuAdapter.MenuItem("Mirror $actionText", R.drawable.ic_tab_move, TrackpadMenuAdapter.Type.DPAD) { cmd ->
            val step = 20
            val command = cmd as String
            


// =================================================================================
            // MIRROR KEYBOARD DPAD CONTROLS  
            // MOVE MODE: UP = move up, DOWN = move down (intuitive)
            // RESIZE MODE: UP = grow taller, DOWN = shrink shorter (like dragging bottom edge)
            // =================================================================================
            when(command) {
                "UP" -> {
                    // Both modes: -step (move up / shrink)
                    android.util.Log.d("MirrorDpad", "UP pressed, isResize=$isMirrorResizeMode, sending deltaY=-$step")
                    service.adjustMirrorKeyboard(isMirrorResizeMode, 0, -step)
                }
                "DOWN" -> {
                    // Both modes: +step (move down / grow)
                    android.util.Log.d("MirrorDpad", "DOWN pressed, isResize=$isMirrorResizeMode, sending deltaY=+$step")
                    service.adjustMirrorKeyboard(isMirrorResizeMode, 0, step)
                }
                "LEFT" -> {
                    android.util.Log.d("MirrorDpad", "LEFT pressed, isResize=$isMirrorResizeMode")
                    service.adjustMirrorKeyboard(isMirrorResizeMode, -step, 0)
                }
                "RIGHT" -> {
                    android.util.Log.d("MirrorDpad", "RIGHT pressed, isResize=$isMirrorResizeMode")
                    service.adjustMirrorKeyboard(isMirrorResizeMode, step, 0)
                }
                "CENTER" -> service.resetMirrorKeyboardPosition()
            }
            // =================================================================================
            // END BLOCK: MIRROR KEYBOARD DPAD CONTROLS
            // =================================================================================


        })
        
        list.add(TrackpadMenuAdapter.MenuItem("APPEARANCE", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        // Mirror Keyboard Opacity Slider
        list.add(TrackpadMenuAdapter.MenuItem("Mirror Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefMirrorAlpha, 255) { v ->
            service.updatePref("mirror_alpha", v)
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("TIMING", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        // Orange Trail Delay Slider (100ms - 3000ms, show as 0.1s - 3.0s)
        val currentDelayMs = p.prefMirrorOrientDelayMs
        list.add(TrackpadMenuAdapter.MenuItem("Orient Delay: ${currentDelayMs}ms", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, (currentDelayMs / 100).toInt(), 30) { v ->
            val newDelayMs = (v as Int) * 100L
            service.updatePref("mirror_orient_delay", newDelayMs)
            loadTab(currentTab) // Refresh to show new value
        })
        
        return list
    }

    // =========================
    // END GET MIRROR ITEMS
    // =========================

    // =========================
    // GET CONFIG ITEMS - Trackpad configuration settings
    // FIXED: Tap to Scroll Boolean Logic
    // ========================= 
    private fun getConfigItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("TRACKPAD SETTINGS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem("SENSITIVITY", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        list.add(TrackpadMenuAdapter.MenuItem("Cursor Speed", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.SLIDER, (p.cursorSpeed * 10).toInt()) { v -> service.updatePref("cursor_speed", (v as Int) / 10f) })
        list.add(TrackpadMenuAdapter.MenuItem("Scroll Speed", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.SLIDER, (p.scrollSpeed * 10).toInt(), 50) { v -> service.updatePref("scroll_speed", (v as Int) / 10f) })
        
        list.add(TrackpadMenuAdapter.MenuItem("APPEARANCE", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        list.add(TrackpadMenuAdapter.MenuItem("Border Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefAlpha, 255) { v -> service.updatePref("alpha", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Background Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefBgAlpha, 255) { v -> service.updatePref("bg_alpha", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Handle Size", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefHandleSize / 2) { v -> service.updatePref("handle_size", v) })        
        list.add(TrackpadMenuAdapter.MenuItem("Scroll Bar Width", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefScrollTouchSize, 200) { v -> service.updatePref("scroll_size", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Cursor Size", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefCursorSize) { v -> service.updatePref("cursor_size", v) })
        
        list.add(TrackpadMenuAdapter.MenuItem("BEHAVIOR", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem("Reverse Scroll", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefReverseScroll) 1 else 0) { v -> service.updatePref("reverse_scroll", v) })
        
        // MODIFIED: Correct Boolean Check for Toast
        list.add(TrackpadMenuAdapter.MenuItem("Tap to Scroll", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefTapScroll) 1 else 0) { v -> 
            service.updatePref("tap_scroll", v)
            // Fix: Cast strictly to Boolean or check against false directly
            if (v == false) {
                android.widget.Toast.makeText(context, "Beta mouse scrolling is activated - warning - scroll slowly for optimal results", android.widget.Toast.LENGTH_LONG).show()
            }
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Haptic Feedback", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefVibrate) 1 else 0) { v -> service.updatePref("vibrate", v) })
        return list
    }
    // =========================
    // END GET CONFIG ITEMS
    // =========================

    // =========================
    // GET TUNE ITEMS - Keyboard configuration settings
    // =========================
    private fun getTuneItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("KEYBOARD SETTINGS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // NEW: Launch Proxy Activity for Picker
        list.add(TrackpadMenuAdapter.MenuItem("Keyboard Picker (Null KB to block default)", android.R.drawable.ic_menu_agenda, TrackpadMenuAdapter.Type.ACTION) { 
            service.forceSystemKeyboardVisible()
            hide() // Close menu
            
            try {
                val intent = android.content.Intent(context, KeyboardPickerActivity::class.java)
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch(e: Exception) {
                android.widget.Toast.makeText(context, "Error launching picker", android.widget.Toast.LENGTH_SHORT).show()
            }
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Keyboard Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefKeyboardAlpha, 255) { v -> service.updatePref("keyboard_alpha", v) })
                list.add(TrackpadMenuAdapter.MenuItem("Auto Display Off", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefAutomationEnabled) 1 else 0) {
                    v -> service.updatePref("automation_enabled", v as Boolean)
                })

        // MOVED & RENAMED: Cover Screen KB Blocker
        list.add(TrackpadMenuAdapter.MenuItem("Cover Screen KB blocker (restart app after reverting)", android.R.drawable.ic_lock_lock, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefBlockSoftKeyboard) 1 else 0) {
            v -> service.updatePref("block_soft_kb", v as Boolean)
        })

        return list
    }
    // =========================
    // END GET TUNE ITEMS
    // =========================

    // =========================
    // HARDKEY ACTIONS LIST
    // =========================
    private val hardkeyActions = listOf(
        "none" to "None (System Default)",
        "left_click" to "Left Click (Hold to Drag)",
        "right_click" to "Right Click (Hold to Drag)",
        "scroll_up" to "Scroll Up",
        "scroll_down" to "Scroll Down",
        "display_toggle_alt" to "Display (Alt Mode)",
        "display_toggle_std" to "Display (Std Mode)",
        "display_wake" to "Display Wake",
        "alt_position" to "Alt KB Position",
        "toggle_keyboard" to "Toggle Keyboard",
        "toggle_trackpad" to "Toggle Trackpad",
        "open_menu" to "Open Menu",
        "reset_cursor" to "Reset Cursor",
        "toggle_bubble" to "Launcher Bubble", // <--- NEW ITEM
        "action_back" to "Back",
        "action_home" to "Home",
        "action_forward" to "Forward (Browser)",
        "action_vol_up" to "Volume Up",
        "action_vol_down" to "Volume Down"
    )
    
    private fun getActionDisplayName(actionId: String): String {
        return hardkeyActions.find { it.first == actionId }?.second ?: actionId
    }
    // =========================
    // END HARDKEY ACTIONS LIST
    // =========================

    // =========================
    // SHOW ACTION PICKER - In-Menu Replacement for Dialog
    // Replaces the current menu list with selection options to avoid Service/Dialog crashes
    // =========================
    private fun showActionPicker(prefKey: String, currentValue: String) {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        
        // 1. Header
        list.add(TrackpadMenuAdapter.MenuItem("Select Action", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // 2. Cancel / Back Option
        list.add(TrackpadMenuAdapter.MenuItem("<< Go Back", android.R.drawable.ic_menu_revert, TrackpadMenuAdapter.Type.ACTION) {
            // Reload the previous tab to "go back"
            loadTab(TAB_HARDKEYS)
        })

        // 3. Action Options
        for ((id, name) in hardkeyActions) {
            // Show a "Check" icon if this is the currently selected value
            // Otherwise show 0 (no icon) or a generic dot
            val iconRes = if (id == currentValue) android.R.drawable.checkbox_on_background else 0
            
            list.add(TrackpadMenuAdapter.MenuItem(name, iconRes, TrackpadMenuAdapter.Type.ACTION) {
                // On Click: Update Pref and Go Back
                service.updatePref(prefKey, id)
                loadTab(TAB_HARDKEYS)
            })
        }
        
        // 4. Update the View
        recyclerView?.adapter = TrackpadMenuAdapter(list)
    }
    // =========================
    // END SHOW ACTION PICKER
    // =========================

    // =========================
    // GET HARDKEY ITEMS - Hardkey bindings configuration menu
    // Allows users to customize Vol Up/Down and Power button actions
    // =========================
    private fun getHardkeyItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        // NEW MAIN HEADER
        list.add(TrackpadMenuAdapter.MenuItem("KEYBINDS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // Subheader
        list.add(TrackpadMenuAdapter.MenuItem("VOLUME UP", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Tap: ${getActionDisplayName(p.hardkeyVolUpTap)}",
            android.R.drawable.ic_media_play,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_up_tap", p.hardkeyVolUpTap) })
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Double-Tap: ${getActionDisplayName(p.hardkeyVolUpDouble)}",
            android.R.drawable.ic_media_ff,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_up_double", p.hardkeyVolUpDouble) })
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Hold: ${getActionDisplayName(p.hardkeyVolUpHold)}",
            android.R.drawable.ic_menu_crop,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_up_hold", p.hardkeyVolUpHold) })
        
        // Subheader
        list.add(TrackpadMenuAdapter.MenuItem("VOLUME DOWN", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Tap: ${getActionDisplayName(p.hardkeyVolDownTap)}",
            android.R.drawable.ic_media_play,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_down_tap", p.hardkeyVolDownTap) })
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Double-Tap: ${getActionDisplayName(p.hardkeyVolDownDouble)}",
            android.R.drawable.ic_media_ff,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_down_double", p.hardkeyVolDownDouble) })
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Hold: ${getActionDisplayName(p.hardkeyVolDownHold)}",
            android.R.drawable.ic_menu_crop,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_down_hold", p.hardkeyVolDownHold) })
        
        // Subheader
        list.add(TrackpadMenuAdapter.MenuItem("TIMING", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        // Max 500ms
        list.add(TrackpadMenuAdapter.MenuItem(
            "Double-Tap Speed (ms)",
            android.R.drawable.ic_menu_recent_history,
            TrackpadMenuAdapter.Type.SLIDER,
            p.doubleTapMs,
            500 
        ) { v ->
            service.updatePref("double_tap_ms", v)
        })
        
        // Max 800ms
        list.add(TrackpadMenuAdapter.MenuItem(
            "Hold Duration (ms)",
            android.R.drawable.ic_menu_recent_history,
            TrackpadMenuAdapter.Type.SLIDER,
            p.holdDurationMs,
            800 
        ) { v ->
            service.updatePref("hold_duration_ms", v)
        })
        
        return list
    }
    // =========================
    // END GET HARDKEY ITEMS
    // =========================


    // =========================
    // GET BUBBLE ITEMS - Bubble launcher customization
    // Size slider, Icon cycle, Opacity slider
    // =========================
    private fun getBubbleItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("BUBBLE CUSTOMIZATION", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // Size slider: 50-200 (50=half, 100=standard, 200=double)
        list.add(TrackpadMenuAdapter.MenuItem("Bubble Size", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefBubbleSize, 200) { v ->
            service.updatePref("bubble_size", v)
        })
        
        // Icon cycle action
        val iconNames = arrayOf("Trackpad", "Cursor", "Main", "Keyboard", "Compass", "Location")
        val currentIconName = iconNames.getOrElse(p.prefBubbleIconIndex) { "Default" }
        list.add(TrackpadMenuAdapter.MenuItem("Icon: $currentIconName", android.R.drawable.ic_menu_gallery, TrackpadMenuAdapter.Type.ACTION) { 
            service.updatePref("bubble_icon", true)
            loadTab(TAB_BUBBLE) // Refresh to show new icon name
        })
        
        // Opacity slider: 50-255
        list.add(TrackpadMenuAdapter.MenuItem("Bubble Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefBubbleAlpha, 255) { v ->
            service.updatePref("bubble_alpha", v)
        })
        
        // Reset button
        list.add(TrackpadMenuAdapter.MenuItem("Reset Bubble Position", android.R.drawable.ic_menu_revert, TrackpadMenuAdapter.Type.ACTION) { 
            service.resetBubblePosition()
        })
        
        // --- PERSISTENCE TOGGLE ---
        list.add(TrackpadMenuAdapter.MenuItem("SERVICE BEHAVIOR", 0, TrackpadMenuAdapter.Type.HEADER))
        
        val persistHelp = if (p.prefPersistentService) "Bubble stays when app closes" else "Bubble closes with app"
        list.add(TrackpadMenuAdapter.MenuItem("Keep Alive (Background)", 
            android.R.drawable.ic_menu_manage, 
            TrackpadMenuAdapter.Type.TOGGLE, 
            if(p.prefPersistentService) 1 else 0) { v ->
            service.updatePref("persistent_service", v)
            loadTab(TAB_BUBBLE) // Refresh description
        })
        list.add(TrackpadMenuAdapter.MenuItem(persistHelp, 0, TrackpadMenuAdapter.Type.INFO))
        
        return list
    }
    // =========================
    // END GET BUBBLE ITEMS
    // =========================

    private fun getProfileItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val currentRes = "${service.currentDisplayId}: ${service.getProfileKey().replace("P_", "").replace("_", " x ")}"
        
        list.add(TrackpadMenuAdapter.MenuItem("LAYOUT PROFILES", 0, TrackpadMenuAdapter.Type.HEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem("Current: $currentRes", R.drawable.ic_tab_profiles, TrackpadMenuAdapter.Type.INFO))
        
        // CHANGED TITLE
        list.add(TrackpadMenuAdapter.MenuItem("Save Layout and Presets", android.R.drawable.ic_menu_save, TrackpadMenuAdapter.Type.ACTION) { 
            service.saveLayout()
            drawerView?.postDelayed({ loadTab(TAB_PROFILES) }, 200)
        })

        // NEW: Reload Profile
        list.add(TrackpadMenuAdapter.MenuItem("Reload Profile", android.R.drawable.ic_popup_sync, TrackpadMenuAdapter.Type.ACTION) { 
            service.loadLayout() // Reloads based on current resolution/display
            drawerView?.postDelayed({ loadTab(TAB_PROFILES) }, 200)
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Delete Profile", android.R.drawable.ic_menu_delete, TrackpadMenuAdapter.Type.ACTION) { 
            service.deleteCurrentProfile()
            drawerView?.postDelayed({ loadTab(TAB_PROFILES) }, 200)
        })
        
        // --- SAVED LAYOUTS LIST ---
        list.add(TrackpadMenuAdapter.MenuItem("SAVED LAYOUTS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        val saved = service.getSavedProfileList()
        if (saved.isEmpty()) {
            list.add(TrackpadMenuAdapter.MenuItem("No saved layouts found.", 0, TrackpadMenuAdapter.Type.INFO))
        } else {
            for (res in saved) {
                list.add(TrackpadMenuAdapter.MenuItem("• $res", 0, TrackpadMenuAdapter.Type.INFO))
            }
        }
        
        return list
    }

    private fun getHelpItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        
        list.add(TrackpadMenuAdapter.MenuItem("INSTRUCTIONS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        val text = 
            "TRACKPAD CONTROLS\n" +
            "• Tap: Left Click\n" +
            "• 2-Finger Tap: Right Click\n" +
            "• Hold + Slide: Drag & Drop\n" +
            "• Edge (Top/Bottom): V-Scroll\n" +
            "• Edge (Left/Right): H-Scroll\n\n" +
            "KEYBOARD OVERLAY\n" +
            "• Drag Top Bar: Move Window\n" +
            "• Drag Bottom-Right: Resize\n" +
            "• Hold Corner: Toggle Key/Mouse\n\n" +
            "HARDWARE KEYS\n" +
            "• Use the 'Hardkeys' tab to map\n" +
            "  Volume Up/Down to clicks,\n" +
            "  scrolling, or screen controls."
            
        list.add(TrackpadMenuAdapter.MenuItem(text, 0, TrackpadMenuAdapter.Type.INFO))
        
        list.add(TrackpadMenuAdapter.MenuItem("LAUNCHER & APP", 0, TrackpadMenuAdapter.Type.HEADER))
        val text2 = 
            "• Floating Bubble: Tap to open this menu. Drag to move.\n" +
            "• Setup App: Open 'DroidOS Trackpad' from your Android App Drawer to adjust permissions or restart the service."
        list.add(TrackpadMenuAdapter.MenuItem(text2, 0, TrackpadMenuAdapter.Type.INFO))
        
        return list
    }
}
```

## File: app/src/main/java/com/example/coverscreentester/TrackpadPrefs.kt
```kotlin
package com.example.coverscreentester

class TrackpadPrefs {
    var cursorSpeed = 2.5f
    var scrollSpeed = 6.0f
    var prefTapScroll = true
    var prefVibrate = false
    var prefReverseScroll = false
    var prefAlpha = 50
    var prefBgAlpha = 220
    var prefKeyboardAlpha = 255
    var prefHandleSize = 14
    var prefVPosLeft = false
    var prefHPosTop = false
    var prefLocked = false
    var prefHandleTouchSize = 80
    var prefScrollTouchSize = 80
    var prefScrollVisualSize = 4
    var prefCursorSize = 50
    var prefKeyScale = 135
    var prefUseAltScreenOff = true
    var prefAutomationEnabled = true
    var prefBubbleX = 50
    var prefBubbleY = 300
    var prefAnchored = false
    var prefBubbleSize = 100
    var prefBubbleIconIndex = 0
    var prefBubbleAlpha = 255
    var prefPersistentService = false
    var prefBlockSoftKeyboard = false

    // =================================================================================
    // VIRTUAL MIRROR MODE PREFERENCES
    // SUMMARY: Settings for displaying a mirror keyboard on remote/AR display.
    //          When enabled, touching the physical keyboard shows an orange orientation
    //          trail on both displays. After finger stops for orientDelayMs, normal
    //          keyboard input resumes.
    // =================================================================================
    var prefVirtualMirrorMode = false
    var prefMirrorOrientDelayMs = 1000L  // Default 1 second orientation delay
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE PREFERENCES
    // =================================================================================

    var hardkeyVolUpTap = "left_click"
    var hardkeyVolUpDouble = "left_click"
    var hardkeyVolUpHold = "left_click"
    var hardkeyVolDownTap = "toggle_keyboard"
    var hardkeyVolDownDouble = "open_menu"
    var hardkeyVolDownHold = "action_back"
    var hardkeyPowerDouble = "none"
    var doubleTapMs = 300
    var holdDurationMs = 400
    var displayOffMode = "alternate"
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
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.Log
import android.content.Context

class TrackpadService : Service() {

    private val TAG = "TrackpadService" // Define TAG

    private lateinit var windowManager: WindowManager
    private var trackpadView: View? = null 
    private var currentDisplayId: Int = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /* * FUNCTION: onStartCommand
     * SUMMARY: Handles the "Recall" logic. If the service is already running but 
     * receives a different display ID, it destroys the old view and recreates 
     * it on the new display context.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val targetDisplayId = intent?.getIntExtra("TARGET_DISPLAY_ID", currentDisplayId) ?: currentDisplayId
        android.util.Log.d("TrackpadService", "onStartCommand: Target=$targetDisplayId, Current=$currentDisplayId")

        if (trackpadView != null) {
            // If display has changed, migrate the view
            if (targetDisplayId != currentDisplayId) {
                android.util.Log.d("TrackpadService", "Migrating Trackpad to Display $targetDisplayId")
                try {
                    windowManager.removeView(trackpadView)
                } catch (e: Exception) {
                    android.util.Log.e("TrackpadService", "Migration removeView failed", e)
                }
                trackpadView = null
                setupUI(targetDisplayId)
            }
        } else {
            // First time initialization
            android.util.Log.d("TrackpadService", "Initial setup on Display $targetDisplayId")
            setupUI(targetDisplayId)
        }
        
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("TrackpadService", "Service Created")
        setupShizuku() // This will be defined later
    }

    /* * FUNCTION: setupUI
     * SUMMARY: Creates a WindowManager context bound specifically to the 
     * requested displayId. This forces the view to the physical screen.
     */
    private fun setupUI(displayId: Int) {
        currentDisplayId = displayId
        val dm = getSystemService(Context.DISPLAY_SERVICE) as android.hardware.display.DisplayManager
        val targetDisplay = dm.getDisplay(displayId) ?: dm.getDisplay(0)
        
        // CRITICAL: Create context for the specific physical display
        val displayContext = createDisplayContext(targetDisplay)
        windowManager = displayContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val inflater = android.view.LayoutInflater.from(displayContext)
        trackpadView = inflater.inflate(R.layout.layout_trackpad, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) 
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
            else 
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            android.graphics.PixelFormat.TRANSLUCENT
        )

        setupTrackpadLogic(trackpadView!!)
        windowManager.addView(trackpadView, params)
    }

    private fun setupTrackpadLogic(view: View) {
        // Setup Safety Exit Button
        val closeBtn = view.findViewById<Button>(R.id.btn_close)
        closeBtn?.setOnClickListener {
            stopSelf()
        }

        // Setup Trackpad Touch Listener
        val trackpadArea = view.findViewById<View>(R.id.view_trackpad)
        trackpadArea?.setOnTouchListener { _, _ ->
            // This consumes the touch event so it doesn't pass through to the launcher
            // We will add the Shizuku injection logic here next.
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (trackpadView != null) {
            try {
                windowManager.removeView(trackpadView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            trackpadView = null
        }
        Toast.makeText(this, "Trackpad Overlay Stopped", Toast.LENGTH_SHORT).show()
    }

    private fun setupShizuku() {
        // Placeholder for Shizuku setup logic
        // This will be implemented when Shizuku functionality is added.
        Log.d(TAG, "setupShizuku: Placeholder called.")
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

## File: app/src/main/res/drawable/bg_trackpad_bubble.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="oval">
    <solid android:color="#444444"/>
    <stroke android:width="2dp" android:color="#888888"/>
</shape>
```

## File: app/src/main/res/drawable/bg_trackpad_drawer.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#1E1E1E"/>
    <corners android:radius="16dp"/>
    <stroke android:width="1dp" android:color="#444444"/>
</shape>
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

## File: app/src/main/res/drawable/ic_tab_help.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zm1,17h-2v-2h2v2zm2.07,-7.75l-0.9,0.92C13.45,12.9 13,13.5 13,15h-2v-0.5c0,-1.1 0.45,-2.1 1.17,-2.83l1.24,-1.26c0.37,-0.36 0.59,-0.86 0.59,-1.41 0,-1.1 -0.9,-2 -2,-2s-2,0.9 -2,2H8c0,-2.21 1.79,-4 4,-4s4,1.79 4,4c0,0.88 -0.36,1.68 -0.93,2.25z"/>
</vector>
```

## File: app/src/main/res/drawable/ic_tab_keyboard.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M20,5H4c-1.1,0 -1.99,0.9 -1.99,2L2,17c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V7c0,-1.1 -0.9,-2 -2,-2zm-9,3h2v2h-2V8zm0,3h2v2h-2v-2zM8,8h2v2H8V8zm0,3h2v2H8v-2zm-1,2H5v-2h2v2zm0,-3H5V8h2v2zm9,7H8v-2h8v2zm0,-4h-2v-2h2v2zm0,-3h-2V8h2v2zm3,3h-2v-2h2v2zm0,-3h-2V8h2v2z"/>
</vector>
```

## File: app/src/main/res/drawable/ic_tab_main.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M10,20v-6h4v6h5v-8h3L12,3 2,12h3v8z"/>
</vector>
```

## File: app/src/main/res/drawable/ic_tab_profiles.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M17,3H5c-1.11,0 -2,0.9 -2,2v14c0,1.1 0.89,2 2,2h14c1.1,0 2,-0.9 2,-2V7l-4,-4zm-5,16c-1.66,0 -3,-1.34 -3,-3s1.34,-3 3,-3 3,1.34 3,3 -1.34,3 -3,3zm3,-10H5V5h10v4z"/>
</vector>
```

## File: app/src/main/res/drawable/ic_tab_settings.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M19.14,12.94c0.04,-0.3 0.06,-0.61 0.06,-0.94c0,-0.32 -0.02,-0.64 -0.07,-0.94l2.03,-1.58c0.18,-0.14 0.23,-0.41 0.12,-0.61l-1.92,-3.32c-0.12,-0.22 -0.37,-0.29 -0.59,-0.22l-2.39,0.96c-0.5,-0.38 -1.03,-0.7 -1.62,-0.94L14.4,2.81c-0.04,-0.24 -0.24,-0.41 -0.48,-0.41h-3.84c-0.24,0 -0.43,0.17 -0.47,0.41L9.25,5.35C8.66,5.59 8.12,5.92 7.63,6.29L5.24,5.33c-0.22,-0.08 -0.47,0 -0.59,0.22L2.74,8.87C2.62,9.08 2.66,9.34 2.86,9.48l2.03,1.58C4.84,11.36 4.8,11.69 4.8,12s0.02,0.64 0.07,0.94l-2.03,1.58c-0.18,0.14 -0.23,0.41 -0.12,0.61l1.92,3.32c0.12,0.22 0.37,0.29 0.59,0.22l2.39,-0.96c0.5,0.38 1.03,0.7 1.62,0.94l0.36,2.54c0.05,0.24 0.24,0.41 0.48,0.41h3.84c0.24,0 0.43,-0.17 0.47,-0.41l0.36,-2.54c0.59,-0.24 1.13,-0.56 1.62,-0.94l2.39,0.96c0.22,0.08 0.47,0 0.59,-0.22l1.92,-3.32c0.12,-0.22 0.07,-0.47 -0.12,-0.61L19.14,12.94zM12,15.6c-1.98,0 -3.6,-1.62 -3.6,-3.6s1.62,-3.6 3.6,-3.6s3.6,1.62 3.6,3.6S13.98,15.6 12,15.6z"/>
</vector>
```

## File: app/src/main/res/drawable/ic_trackpad_foreground_scaled.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/ic_trackpad_logo"
    android:insetLeft="4dp"
    android:insetRight="4dp"
    android:insetTop="4dp"
    android:insetBottom="4dp" />
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
        android:padding="32dp">

        <ImageView
            android:layout_width="80dp"
            android:layout_height="80dp"
            android:src="@mipmap/ic_trackpad_adaptive"
            android:layout_marginBottom="24dp"
            android:contentDescription="App Icon" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Setup Required"
            android:textSize="22sp"
            android:textStyle="bold"
            android:textColor="#FFFFFF"
            android:gravity="center"
            android:layout_marginBottom="24dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="To use the trackpad overlay, you must grant the following permissions.\n\nSince this is a system-level tool, these must be enabled manually."
            android:textColor="#DDDDDD"
            android:textSize="16sp"
            android:lineSpacingMultiplier="1.2"
            android:gravity="start"
            android:background="#1E1E1E"
            android:padding="16dp"
            android:layout_marginBottom="32dp"/>

        <Button
            android:id="@+id/btn_fix_restricted"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="1. Allow Restricted Settings"
            android:backgroundTint="#FF5722"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:layout_marginBottom="16dp"
            android:elevation="4dp"/>

        <Button
            android:id="@+id/btn_open_accessibility"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="2. Enable Accessibility"
            android:backgroundTint="#3DDC84"
            android:textColor="#000000"
            android:textStyle="bold"
            android:layout_marginBottom="16dp"
            android:elevation="4dp"/>

        <Button
            android:id="@+id/btn_start_check"
            android:layout_width="match_parent"
            android:layout_height="60dp"
            android:text="3. CHECK PERMISSIONS &amp; START"
            android:backgroundTint="#2196F3"
            android:textColor="#FFFFFF"
            android:textSize="16sp"
            android:textStyle="bold"
            android:elevation="6dp"/>

        <Button
            android:id="@+id/btn_switch_display"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="Switch Display"
            android:backgroundTint="#FFC107"
            android:textColor="#000000"
            android:textStyle="bold"
            android:layout_marginTop="16dp"
            android:elevation="4dp"/>

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
    android:padding="16dp"
    android:background="#121212"
    android:gravity="center">

    <TextView
        android:id="@+id/text_mode"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="TRACKPAD: POSITION"
        android:textColor="#00FF00"
        android:textSize="20sp"
        android:textStyle="bold"
        android:layout_marginBottom="24dp"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center"
        android:layout_marginBottom="16dp">

        <Button
            android:id="@+id/btn_toggle_target"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Edit: Trackpad"
            android:layout_marginEnd="8dp"/>

        <Button
            android:id="@+id/btn_toggle_mode"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Mode: Position"/>
    </LinearLayout>

    <Button
        android:id="@+id/btn_up"
        android:layout_width="80dp"
        android:layout_height="60dp"
        android:text="UP"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:layout_marginTop="8dp"
        android:layout_marginBottom="8dp">

        <Button
            android:id="@+id/btn_left"
            android:layout_width="80dp"
            android:layout_height="60dp"
            android:text="LEFT"
            android:layout_marginEnd="16dp"/>

        <Button
            android:id="@+id/btn_center"
            android:layout_width="80dp"
            android:layout_height="60dp"
            android:text="RESET"
            android:textColor="#FF9800"/>

        <Button
            android:id="@+id/btn_right"
            android:layout_width="80dp"
            android:layout_height="60dp"
            android:text="RIGHT"
            android:layout_marginStart="16dp"/>

    </LinearLayout>

    <Button
        android:id="@+id/btn_down"
        android:layout_width="80dp"
        android:layout_height="60dp"
        android:text="DOWN"/>

    <Button
        android:id="@+id/btn_rotate"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Rotate 90°"
        android:layout_marginTop="24dp"/>

    <Button
        android:id="@+id/btn_back"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Done / Back"
        android:backgroundTint="#555555"
        android:layout_marginTop="16dp"/>

</LinearLayout>
```

## File: app/src/main/res/layout/activity_menu.xml
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
        android:padding="24dp"
        android:gravity="center_horizontal">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Trackpad Setup"
            android:textColor="#FFFFFF"
            android:textSize="24sp"
            android:textStyle="bold"
            android:layout_marginBottom="24dp" />

        <TextView
            android:id="@+id/tvStep1Title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Step 1: Initialize Restriction"
            android:textColor="#3DDC84"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Disclosure: This app uses the AccessibilityService API to function as a trackpad. It simulates touch inputs (clicks and swipes) on the cover screen on your behalf. This app does not collect, store, or transmit any personal user data through this service."
            android:textColor="#DDDDDD"
            android:textSize="12sp"
            android:background="#222222"
            android:padding="12dp"
            android:layout_marginBottom="12dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Instructions: Find 'Cover Screen Trackpad' and tap it. If a 'Restricted Setting' dialog appears, click OK and proceed to Step 2. If it enables successfully, you can skip to Step 4."
            android:textColor="#AAAAAA"
            android:textSize="12sp"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btnStep1Trigger"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Open Accessibility"
            android:backgroundTint="#333333"
            android:textColor="#FFFFFF"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:background="#333333"
            android:layout_marginTop="16dp"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:id="@+id/tvStep2Title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Step 2: Allow Restricted Settings"
            android:textColor="#3DDC84"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="1. Click button below to open App Info.\n2. Tap the 3-dot menu (top right).\n3. Select 'Allow Restricted Settings'."
            android:textColor="#AAAAAA"
            android:textSize="12sp"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btnStep2Unblock"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Open App Info"
            android:backgroundTint="#333333"
            android:textColor="#FFFFFF"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:background="#333333"
            android:layout_marginTop="16dp"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:id="@+id/tvStep3Title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Step 3: Enable Accessibility"
            android:textColor="#3DDC84"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Go back to Accessibility and enable 'Cover Screen Trackpad'. It should now work."
            android:textColor="#AAAAAA"
            android:textSize="12sp"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btnStep3Enable"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Grant Accessibility"
            android:backgroundTint="#333333"
            android:textColor="#FFFFFF"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:background="#333333"
            android:layout_marginTop="16dp"
            android:layout_marginBottom="16dp"/>

        <!-- NEW: Step for Null Keyboard -->
        <TextView
            android:id="@+id/tvStepNullKeyboardTitle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Step 3.5: Enable Null Keyboard"
            android:textColor="#3DDC84"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Required for keyboard input handling without blocking the screen. Enable 'DroidOS Null Keyboard' in the next screen."
            android:textColor="#AAAAAA"
            android:textSize="12sp"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btnStepNullKeyboardEnable"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Enable Null Keyboard"
            android:backgroundTint="#333333"
            android:textColor="#FFFFFF"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:background="#333333"
            android:layout_marginTop="16dp"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:id="@+id/tvStep4Title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Step 4: Overlay Permission"
            android:textColor="#3DDC84"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Disclosure: This app requires the 'Display over other apps' permission to render the mouse cursor and trackpad controls on top of your screen."
            android:textColor="#DDDDDD"
            android:textSize="12sp"
            android:background="#222222"
            android:padding="8dp"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btnStep4Overlay"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Grant Overlay"
            android:backgroundTint="#333333"
            android:textColor="#FFFFFF"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:background="#333333"
            android:layout_marginTop="16dp"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:id="@+id/tvStep5Title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Step 5: Shizuku Permission"
            android:textColor="#3DDC84"
            android:textStyle="bold"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btnStep5Shizuku"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Grant Shizuku"
            android:backgroundTint="#333333"
            android:textColor="#FFFFFF"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Note: If Shizuku permissions are lost, a red dot may appear in the app."
            android:textColor="#AAAAAA"
            android:textSize="11sp"
            android:layout_marginTop="8dp"
            android:layout_marginBottom="32dp"/>

        <Button
            android:id="@+id/btnStartApp"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="LAUNCH DROIDOS"
            android:backgroundTint="#009688"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:minHeight="60dp"
            android:layout_marginTop="8dp"/>

        <Button
            android:id="@+id/btnOpenSettings"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="OPEN DROIDOS SETTINGS"
            android:backgroundTint="#444444"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:minHeight="60dp"
            android:layout_marginTop="16dp"/>

    </LinearLayout>
</ScrollView>
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
    android:background="#000000"
    android:padding="16dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Trackpad Settings"
            android:textColor="#FFFFFF"
            android:textSize="20sp"
            android:textStyle="bold"
            android:layout_marginBottom="16dp"/>

        <Switch
            android:id="@+id/swTapScroll"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Tap to Scroll"
            android:textColor="#FFFFFF"
            android:minHeight="48dp"
            android:layout_marginBottom="8dp"/>

        <Switch
            android:id="@+id/swVibrate"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Haptic Feedback"
            android:textColor="#FFFFFF"
            android:minHeight="48dp"
            android:layout_marginBottom="8dp"/>

        <Switch
            android:id="@+id/swReverseScroll"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Reverse Scrolling"
            android:textColor="#FFFFFF"
            android:minHeight="48dp"
            android:layout_marginBottom="8dp"/>

        <Switch
            android:id="@+id/swVPos"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Vertical Position (Left/Top)"
            android:textColor="#FFFFFF"
            android:minHeight="48dp"
            android:layout_marginBottom="8dp"/>

        <Switch
            android:id="@+id/swHPos"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Horizontal Position (Top/Left)"
            android:textColor="#FFFFFF"
            android:minHeight="48dp"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Cursor Speed"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarCursorSpeed"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="100"
            android:progress="25"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Scroll Speed"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarScrollSpeed"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="100"
            android:progress="10"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Cursor Size"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarCursorSize"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="100"
            android:progress="50"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Transparency"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarAlpha"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="255"
            android:progress="50"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Handle Size"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarHandleSize"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="50"
            android:progress="14"
            android:layout_marginBottom="16dp"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Scroll Visual Size"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarScrollVisual"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="20"
            android:progress="4"
            android:layout_marginBottom="16dp"/>
            
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Handle Touch Area"
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
            android:text="Scroll Touch Area"
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
            android:text="Save &amp; Reload"
            android:layout_marginBottom="8dp"/>

        <Button
            android:id="@+id/btnBack"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Back"/>

    </LinearLayout>
</ScrollView>
```

## File: app/src/main/res/layout/item_dpi_custom.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
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
        app:tint="#FF5555"
        android:background="?attr/selectableItemBackgroundBorderless"/>

    <ImageView
        android:id="@+id/btn_dpi_plus"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:padding="10dp"
        android:src="@android:drawable/ic_input_add"
        app:tint="#3DDC84"
        android:background="?attr/selectableItemBackgroundBorderless"/>

</LinearLayout>
```

## File: app/src/main/res/layout/item_trackpad_menu.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:background="@drawable/bg_item_press"
    android:padding="8dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:minHeight="40dp">

        <ImageView
            android:id="@+id/item_icon"
            android:layout_width="24dp"
            android:layout_height="24dp"
            android:src="@drawable/ic_cursor"
            app:tint="#AAAAAA"
            android:layout_marginEnd="12dp"/>

        <TextView
            android:id="@+id/item_title"
            android:layout_width="0dp"
            android:layout_weight="1"
            android:layout_height="wrap_content"
            android:text="Setting Name"
            android:textColor="#FFFFFF"
            android:textSize="16sp"/>

        <TextView
            android:id="@+id/item_value_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="100%"
            android:textColor="#3DDC84"
            android:visibility="gone"
            android:paddingEnd="8dp"/>

        <Switch
            android:id="@+id/item_switch"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:visibility="gone"/>
            
        <ImageView
            android:id="@+id/item_action_icon"
            android:layout_width="24dp"
            android:layout_height="24dp"
            android:src="@android:drawable/ic_media_play"
            app:tint="#3DDC84"
            android:visibility="gone"/>

    </LinearLayout>

    <SeekBar
        android:id="@+id/item_seekbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp"
        android:visibility="gone"/>

    <GridLayout
        android:id="@+id/item_dpad_grid"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:columnCount="3"
        android:rowCount="3"
        android:visibility="gone"
        android:padding="8dp">

        <Space android:layout_width="50dp" android:layout_height="50dp"/>
        <Button android:id="@+id/btn_up" android:text="▲" android:layout_width="50dp" android:layout_height="50dp" android:backgroundTint="#333333" android:textColor="#FFFFFF"/>
        <Space android:layout_width="50dp" android:layout_height="50dp"/>

        <Button android:id="@+id/btn_left" android:text="◀" android:layout_width="50dp" android:layout_height="50dp" android:backgroundTint="#333333" android:textColor="#FFFFFF"/>
        <Button android:id="@+id/btn_center" android:text="⟲" android:layout_width="50dp" android:layout_height="50dp" android:backgroundTint="#555555" android:textColor="#FFFFFF"/>
        <Button android:id="@+id/btn_right" android:text="▶" android:layout_width="50dp" android:layout_height="50dp" android:backgroundTint="#333333" android:textColor="#FFFFFF"/>

        <Space android:layout_width="50dp" android:layout_height="50dp"/>
        <Button android:id="@+id/btn_down" android:text="▼" android:layout_width="50dp" android:layout_height="50dp" android:backgroundTint="#333333" android:textColor="#FFFFFF"/>
        <Space android:layout_width="50dp" android:layout_height="50dp"/>
    </GridLayout>

    <TextView
        android:id="@+id/item_help_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Help text goes here..."
        android:textColor="#AAAAAA"
        android:textSize="14sp"
        android:visibility="gone"
        android:padding="4dp"/>

</LinearLayout>
```

## File: app/src/main/res/layout/layout_trackpad_bubble.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="60dp"
    android:layout_height="60dp"
    android:background="@drawable/bg_trackpad_bubble"
    android:clickable="true"
    android:focusable="true">

    <ImageView
        android:id="@+id/bubble_icon"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_gravity="center"
        android:src="@mipmap/ic_trackpad_adaptive"
        android:contentDescription="Trackpad Menu" 
        android:clipToOutline="true"/>
        
    <ImageView
        android:id="@+id/status_dot"
        android:layout_width="12dp"
        android:layout_height="12dp"
        android:layout_gravity="top|end"
        android:layout_margin="10dp"
        android:src="@android:drawable/ic_notification_overlay"
        app:tint="#FF0000"
        android:visibility="gone" />

</FrameLayout>
```

## File: app/src/main/res/layout/layout_trackpad_drawer.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@null"
    android:elevation="0dp">

    <LinearLayout
        android:id="@+id/menu_container"
        android:layout_width="320dp"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:background="@drawable/bg_trackpad_drawer"
        android:orientation="vertical"
        android:elevation="0dp"
        android:minHeight="300dp">

        <RelativeLayout
            android:id="@+id/menu_drag_handle"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="#00000000"
            android:paddingEnd="12dp"
            android:clickable="true"
            android:focusable="true">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerVertical="true"
                android:layout_marginStart="16dp"
                android:text="DroidOS Trackpad Keyboard"
                android:textColor="#FFFFFF"
                android:textStyle="bold"
                android:textSize="16sp" />

            <ImageView
                android:id="@+id/btn_close_menu"
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:layout_alignParentEnd="true"
                android:layout_centerVertical="true"
                android:background="?android:attr/selectableItemBackgroundBorderless"
                android:padding="6dp"
                android:src="@android:drawable/ic_menu_close_clear_cancel"
                app:tint="#FF5555" />
        </RelativeLayout>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="horizontal">

            <ScrollView
                android:layout_width="56dp"
                android:layout_height="match_parent"
                android:background="#222222">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:paddingTop="8dp"
                    android:paddingBottom="8dp">

                    <ImageView
                        android:id="@+id/tab_main"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_cursor"
                        app:tint="#888888" />

                    <ImageView
                        android:id="@+id/tab_presets"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_tab_presets"
                        app:tint="#888888" />

                    <ImageView
                        android:id="@+id/tab_move"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_tab_move"
                        app:tint="#888888" />

                    <ImageView
                        android:id="@+id/tab_kb_move"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_tab_kb_move"
                        app:tint="#888888" />

                    <ImageView
                        android:id="@+id/tab_mirror"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_tab_kb_move"
                        app:tint="#888888" />

                    <ImageView
                        android:id="@+id/tab_config"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_tab_config"
                        app:tint="#888888" />

                    <ImageView
                        android:id="@+id/tab_tune"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_tab_tune"
                        app:tint="#888888" />

                    <ImageView
                        android:id="@+id/tab_hardkeys"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_tab_hardkeys"
                        app:tint="#888888" />

                    <ImageView
                        android:id="@+id/tab_bubble"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_tab_bubble"
                        app:tint="#888888" />

                    <ImageView
                        android:id="@+id/tab_profiles"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_tab_profiles"
                        app:tint="#888888" />

                    <ImageView
                        android:id="@+id/tab_help"
                        android:layout_width="match_parent"
                        android:layout_height="48dp"
                        android:padding="12dp"
                        android:src="@drawable/ic_tab_help"
                        app:tint="#888888" />

                </LinearLayout>
            </ScrollView>

            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/menu_recycler"
                android:layout_width="match_parent"
                android:layout_height="350dp"
                android:padding="8dp"
                android:scrollbars="vertical" />

        </LinearLayout>

    </LinearLayout>
</FrameLayout>
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
    android:canRetrieveWindowContent="true"
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

## File: app/src/main/res/xml/method.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<input-method xmlns:android="http://schemas.android.com/apk/res/android" />
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
    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <queries>
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
        <intent>
            <action android:name="android.speech.action.RECOGNIZE_SPEECH" />
        </intent>
        <intent>
            <action android:name="android.intent.action.VOICE_COMMAND" />
        </intent>
        <package android:name="com.google.android.googlequicksearchbox" />
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
            
        <activity 
            android:name=".KeyboardActivity"
            android:exported="false" 
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:excludeFromRecents="true"
            android:noHistory="true" />

        <activity
            android:name=".KeyboardPickerActivity"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:excludeFromRecents="true"
            android:taskAffinity=""
            android:launchMode="singleInstance"
            android:exported="false" />

        <service
            android:name=".OverlayService"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
            android:exported="true"
            android:foregroundServiceType="specialUse">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
                <action android:name="PREVIEW_UPDATE" />
                <action android:name="RESET_POSITION" />
                <action android:name="ROTATE" />
                <action android:name="SAVE_LAYOUT" />
                <action android:name="LOAD_LAYOUT" />
                <action android:name="RELOAD_PREFS" />
                <action android:name="DELETE_PROFILE" />
                <action android:name="MANUAL_ADJUST" /> 
                <action android:name="CYCLE_INPUT_TARGET" />
                <action android:name="RESET_CURSOR" />
                <action android:name="TOGGLE_DEBUG" />
                <action android:name="FORCE_KEYBOARD" />
                <action android:name="TOGGLE_CUSTOM_KEYBOARD" />
                <action android:name="SET_TRACKPAD_VISIBILITY" />
                <action android:name="SET_PREVIEW_MODE" />
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/accessibility_service_config" />
        </service>

        <service
            android:name=".NullInputMethodService"
            android:label="DroidOS Null Keyboard"
            android:permission="android.permission.BIND_INPUT_METHOD"
            android:exported="true">
            <intent-filter>
                <action android:name="android.view.InputMethod" />
            </intent-filter>
            <meta-data
                android:name="android.view.im"
                android:resource="@xml/method" />
        </service>
        <!-- =================================================================================
             INTER-APP COMMAND RECEIVER
             SUMMARY: Static receiver to handle commands from DroidOS Launcher and ADB.
                      Allows soft restart, z-order fixes, and virtual display coordination
                      without requiring the Activity to be in foreground.
             USAGE (ADB): adb shell am broadcast -a com.example.coverscreentester.SOFT_RESTART
             ================================================================================= -->
                <receiver android:name=".InterAppCommandReceiver" android:enabled="true" android:exported="true">
                    <intent-filter>
                        <action android:name="com.example.coverscreentester.SOFT_RESTART" />
                        <action android:name="com.example.coverscreentester.MOVE_TO_VIRTUAL" />
                        <action android:name="com.example.coverscreentester.RETURN_TO_PHYSICAL" />
                        <action android:name="com.example.coverscreentester.ENFORCE_ZORDER" />
                        <action android:name="com.example.coverscreentester.TOGGLE_VIRTUAL_MIRROR" />
                        <action android:name="com.example.coverscreentester.OPEN_DRAWER" />
                        <action android:name="com.example.coverscreentester.GET_STATUS" />
                        <action android:name="com.example.coverscreentester.STOP_SERVICE" />
        
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.SOFT_RESTART" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.MOVE_TO_VIRTUAL" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.RETURN_TO_PHYSICAL" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.ENFORCE_ZORDER" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.TOGGLE_VIRTUAL_MIRROR" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.OPEN_DRAWER" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.GET_STATUS" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.STOP_SERVICE" />
                    </intent-filter>
                </receiver>
        <!-- END BLOCK: INTER-APP COMMAND RECEIVER -->

        <provider
            android:name="rikka.shizuku.ShizukuProvider"
            android:authorities="${applicationId}.shizuku"
            android:enabled="true"
            android:exported="true"
            android:multiprocess="false" />

    </application>

</manifest>
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
        versionCode = 3
        versionName = "3.0"

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
