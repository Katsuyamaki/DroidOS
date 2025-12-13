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
- Files matching these patterns are excluded: **/.gradle/**, **/build/**, **/.idea/**, **/*.iml, **/local.properties, **/build_log.txt, **/*.png, **/*.webp, **/*.jar, **/*.aar, **/captures/**, **/*Repomix*.md
- Files matching patterns in .gitignore are excluded
- Files matching default ignore patterns are excluded
- Files are sorted by Git change count (files with more changes are at the bottom)

# Directory Structure
```
.claude/
  settings.local.json
Cover-Screen-Launcher/
  app/
    src/
      main/
        aidl/
          com/
            example/
              quadrantlauncher/
                IShellService.aidl
                IShellService.aidl.minimize
        java/
          com/
            example/
              quadrantlauncher/
                AppPreferences.kt
                FloatingLauncherService.kt
                FloatingLauncherService.kt.displayoff
                FloatingLauncherService.kt.minimize
                IconPickerActivity.kt
                MainActivity.kt
                MenuActivity.kt
                QuadrantActivity.kt
                ShellUserService.kt
                ShellUserService.kt.displayoff
                ShellUserService.kt.minimize
                ShizukuBinder.java
                ShizukuHelper.kt
                SplitActivity.kt
                TriSplitActivity.kt
        res/
          drawable/
            bg_bubble.xml
            bg_drawer.xml
            bg_item_active.xml
            bg_item_press.xml
            ic_box_outline.xml
            ic_cover_final_scale.xml
            ic_launcher_foreground_scaled.xml
            ic_lock_closed.xml
            ic_lock_open.xml
            ic_mode_dpi.xml
            ic_mode_profiles.xml
            ic_mode_resolution.xml
            ic_scaler_bubble.xml
            ic_scaler_cover_final.xml
            ic_scaler_cover_tiny.xml
            ic_star_border.xml
            ic_star_filled.xml
            ic_window_split.xml
            ic_wolf_cover.xml
            ic_wolf_main.xml
            scaler_bubble.xml
            scaler_cover.xml
            scaler_main.xml
          layout/
            activity_main.xml
            activity_menu.xml
            activity_quadrant.xml
            activity_split.xml
            activity_tri_split.xml
            item_app_rofi.xml
            item_custom_resolution.xml
            item_dpi_custom.xml
            item_font_size.xml
            item_height_setting.xml
            item_icon_setting.xml
            item_layout_option.xml
            item_profile_rich.xml
            item_selected_app.xml
            item_width_setting.xml
            layout_bubble.xml
            layout_rofi_drawer.xml
            list_item_app.xml
          mipmap-anydpi-v26/
            ic_bubble_icon.xml
            ic_bubble.xml
            ic_cover_tiny.xml
            ic_cover_v2.xml
            ic_launcher_adaptive.xml
            ic_launcher_cover_final.xml
            ic_launcher_round.xml
            ic_launcher.xml
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
    .gitignore
    build.gradle.kts
    proguard-rules.pro
  gradle/
    wrapper/
      gradle-wrapper.properties
    libs.versions.toml
  .gitignore
  build.gradle.kts
  gradle.properties
  gradlew
  gradlew.bat
  multidex-keep.txt
  README.md
  settings.gradle.kts
  ShellUserService.kt
Cover-Screen-Trackpad/
  app/
    src/
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
                TrackpadMenuAdapter.kt
                TrackpadMenuManager.kt
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
  Compilation
  crash_log.txt
  gradle.properties
  gradlew
  gradlew.bat
  README.md
  settings.gradle.kts
CHANGELOG.md
cover_recording.mp4
GEMINI.md
README.md
```

# Files

## File: .claude/settings.local.json
```json
{
  "permissions": {
    "allow": [
      "Bash(./gradlew compileDebugKotlin:*)",
      "Bash(SHELL=/bin/bash /data/data/com.termux/files/home/projects/DroidOS/Cover-Screen-Trackpad/gradlew:*)"
    ]
  }
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/IconPickerActivity.kt
```kotlin
package com.example.quadrantlauncher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class IconPickerActivity : ComponentActivity() {

    private val pickImage = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                // Persist permission so we can read this after reboot
                contentResolver.takePersistableUriPermission(
                    uri, 
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                AppPreferences.saveIconUri(this, uri.toString())
                
                // Notify Service to update
                sendBroadcast(Intent("com.example.quadrantlauncher.UPDATE_ICON"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Invisible activity
        
        try {
            pickImage.launch(arrayOf("image/*"))
        } catch (e: Exception) {
            finish()
        }
    }
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/MainActivity.kt
```kotlin
package com.example.quadrantlauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    companion object {
        const val SELECTED_APP_PACKAGE = "SELECTED_APP_PACKAGE"
    }

    data class AppInfo(
        val label: String, 
        val packageName: String, 
        var isFavorite: Boolean = false,
        var isMinimized: Boolean = false // New field
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Please grant Overlay Permission", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            finish()
            return
        }

        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(0)
        }

        val currentDisplayId = display?.displayId ?: android.view.Display.DEFAULT_DISPLAY
        val intent = Intent(this, FloatingLauncherService::class.java).apply {
            putExtra("DISPLAY_ID", currentDisplayId)
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        
        Toast.makeText(this, "Launcher Bubble Started!", Toast.LENGTH_SHORT).show()
        finish() 
    }
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/MenuActivity.kt
```kotlin
package com.example.quadrantlauncher

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import rikka.shizuku.Shizuku

class MenuActivity : Activity(), Shizuku.OnRequestPermissionResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Shizuku Permission Check
        checkShizukuPermission()

        // 2. Main Layout Container (Dark Theme)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setPadding(40, 60, 40, 40)
            setBackgroundColor(Color.parseColor("#1E1E1E")) // Dark Background
            gravity = Gravity.TOP
        }

        // --- TITLE HEADER ---
        val headerText = TextView(this).apply {
            text = "CoverScreen Launcher"
            textSize = 22f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                bottomMargin = 50
            }
        }
        mainLayout.addView(headerText)

        // --- PROFILE ROW (Horizontal: Text + Save Icon) ---
        val profileRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                bottomMargin = 60
            }
            gravity = Gravity.CENTER_VERTICAL
            // Add a subtle background to the row to make it distinct
            setBackgroundColor(Color.parseColor("#2D2D2D"))
            setPadding(20, 20, 20, 20)
        }

        // Profile Text (Left side)
        val profileText = TextView(this).apply {
            text = "Current: Default" 
            textSize = 18f
            setTextColor(Color.WHITE)
            // Weight 1 pushes the icon to the far right. 
            // Change to 0 and WRAP_CONTENT if you want icon immediately next to text.
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f) 
        }

        // Save Icon (Right side)
        val saveBtn = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_save)
            setBackgroundColor(Color.TRANSPARENT) // Transparent bg
            setColorFilter(Color.CYAN) // Cyan tint to make it pop
            setPadding(20, 0, 0, 0)
            setOnClickListener {
                Toast.makeText(this@MenuActivity, "Profile Saved (Placeholder)", Toast.LENGTH_SHORT).show()
                // TODO: Connect to AppPreferences.saveProfile logic
            }
        }

        profileRow.addView(profileText)
        profileRow.addView(saveBtn)
        mainLayout.addView(profileRow)

        // --- LAUNCHER BUTTONS ---
        
        // Button 1: 4-Quadrant
        val btnQuad = Button(this).apply {
            text = "Launch 4-Quadrant"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#444444"))
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                bottomMargin = 30
            }
            setOnClickListener {
                launchActivity(QuadrantActivity::class.java)
            }
        }

        // Button 2: Split-Screen
        val btnSplit = Button(this).apply {
            text = "Launch Split-Screen"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#444444"))
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            setOnClickListener {
                launchActivity(TriSplitActivity::class.java)
            }
        }

        mainLayout.addView(btnQuad)
        mainLayout.addView(btnSplit)

        setContentView(mainLayout)
    }

    private fun checkShizukuPermission() {
        if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
            // Shizuku not running
        } else if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(0)
            Shizuku.addRequestPermissionResultListener(this)
        }
    }

    private fun launchActivity(cls: Class<*>) {
        try {
            val intent = Intent(this, cls)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Shizuku Granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(this)
    }
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/QuadrantActivity.kt
```kotlin
package com.example.quadrantlauncher

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class QuadrantActivity : AppCompatActivity() {

    companion object {
        const val Q1_KEY = "Q1_PACKAGE"
        const val Q2_KEY = "Q2_PACKAGE"
        const val Q3_KEY = "Q3_PACKAGE"
        const val Q4_KEY = "Q4_PACKAGE"
    }

    private var q1Package: String? = null
    private var q2Package: String? = null
    private var q3Package: String? = null
    private var q4Package: String? = null

    private lateinit var q1Button: Button
    private lateinit var q2Button: Button
    private lateinit var q3Button: Button
    private lateinit var q4Button: Button
    private lateinit var launchButton: Button

    private var currentQuadrant = -1
    private var hasShizukuPermission = false

    private val REQUEST_PERMISSION_RESULT_LISTENER =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == ShizukuHelper.SHIZUKU_PERMISSION_REQUEST_CODE) {
                hasShizukuPermission = grantResult == PackageManager.PERMISSION_GRANTED
                checkShizukuPermission() // Update status
            }
        }

    private val appSelectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val packageName = result.data?.getStringExtra(MainActivity.SELECTED_APP_PACKAGE)
            if (packageName != null) {
                val simple = AppPreferences.getSimpleName(packageName)
                when (currentQuadrant) {
                    1 -> {
                        q1Package = packageName
                        q1Button.text = "Q1: $simple"
                        AppPreferences.savePackage(this, Q1_KEY, packageName)
                    }
                    2 -> {
                        q2Package = packageName
                        q2Button.text = "Q2: $simple"
                        AppPreferences.savePackage(this, Q2_KEY, packageName)
                    }
                    3 -> {
                        q3Package = packageName
                        q3Button.text = "Q3: $simple"
                        AppPreferences.savePackage(this, Q3_KEY, packageName)
                    }
                    4 -> {
                        q4Package = packageName
                        q4Button.text = "Q4: $simple"
                        AppPreferences.savePackage(this, Q4_KEY, packageName)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
        setContentView(R.layout.activity_quadrant)

        q1Button = findViewById(R.id.q1_button)
        q2Button = findViewById(R.id.q2_button)
        q3Button = findViewById(R.id.q3_button)
        q4Button = findViewById(R.id.q4_button)
        launchButton = findViewById(R.id.launch_button)

        loadSavedApps()

        q1Button.setOnClickListener { currentQuadrant = 1; launchAppPicker() }
        q2Button.setOnClickListener { currentQuadrant = 2; launchAppPicker() }
        q3Button.setOnClickListener { currentQuadrant = 3; launchAppPicker() }
        q4Button.setOnClickListener { currentQuadrant = 4; launchAppPicker() }

        launchButton.setOnClickListener {
            if (q1Package != null && q2Package != null &&
                q3Package != null && q4Package != null) {
                launchQuadrantApps()
            } else {
                Toast.makeText(this, "Select all 4 apps.", Toast.LENGTH_SHORT).show()
            }
        }
        checkShizukuPermission()
    }

    private fun loadSavedApps() {
        q1Package = AppPreferences.loadPackage(this, Q1_KEY)
        q1Button.text = "Q1: ${AppPreferences.getSimpleName(q1Package)}"
        q2Package = AppPreferences.loadPackage(this, Q2_KEY)
        q2Button.text = "Q2: ${AppPreferences.getSimpleName(q2Package)}"
        q3Package = AppPreferences.loadPackage(this, Q3_KEY)
        q3Button.text = "Q3: ${AppPreferences.getSimpleName(q3Package)}"
        q4Package = AppPreferences.loadPackage(this, Q4_KEY)
        q4Button.text = "Q4: ${AppPreferences.getSimpleName(q4Package)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
    }

    private fun checkShizukuPermission() {
        if (ShizukuHelper.isShizukuAvailable()) {
            if (ShizukuHelper.hasPermission()) {
                hasShizukuPermission = true
            } else {
                ShizukuHelper.requestPermission()
            }
        }
    }

    private fun launchAppPicker() {
        appSelectLauncher.launch(Intent(this, MainActivity::class.java))
    }

    private fun launchQuadrantApps() {
        val metrics = windowManager.maximumWindowMetrics
        val w = metrics.bounds.width()
        val h = metrics.bounds.height()

        val q1 = Rect(0, 0, w / 2, h / 2)
        val q2 = Rect(w / 2, 0, w, h / 2)
        val q3 = Rect(0, h / 2, w / 2, h)
        val q4 = Rect(w / 2, h / 2, w, h)

        // Kill all first (Synchronously wait for shell command)
        if (hasShizukuPermission) {
            // Launch in background thread to avoid freezing UI if kill takes time
            Thread {
                ShizukuHelper.killApp(q1Package!!)
                ShizukuHelper.killApp(q2Package!!)
                ShizukuHelper.killApp(q3Package!!)
                ShizukuHelper.killApp(q4Package!!)
                
                // IMPORTANT: Wait for OS to clean up windows
                try { Thread.sleep(400) } catch (e: InterruptedException) {}

                // Back to UI thread to launch
                runOnUiThread {
                    launchAppIntent(q1Package!!, q1)
                    launchAppIntent(q2Package!!, q2)
                    launchAppIntent(q3Package!!, q3)
                    launchAppIntent(q4Package!!, q4)
                }
            }.start()
        } else {
            // Fallback (wont work well for moving windows)
            launchAppIntent(q1Package!!, q1)
            launchAppIntent(q2Package!!, q2)
            launchAppIntent(q3Package!!, q3)
            launchAppIntent(q4Package!!, q4)
        }
    }

    private fun launchAppIntent(packageName: String, bounds: Rect) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent == null) return

            // CLEAR_TOP helps reset the task state
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            
            val options = ActivityOptions.makeBasic().setLaunchBounds(bounds)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
            Log.e("Quadrant", "Launch failed for $packageName", e)
        }
    }
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShizukuBinder.java
```java
package com.example.quadrantlauncher;

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

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShizukuHelper.kt
```kotlin
package com.example.quadrantlauncher

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
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/SplitActivity.kt
```kotlin
package com.example.quadrantlauncher

import android.app.Activity
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class SplitActivity : AppCompatActivity() {

    companion object {
        const val SPLIT1_KEY = "SPLIT1_PACKAGE"
        const val SPLIT2_KEY = "SPLIT2_PACKAGE"
        const val TAG = "SplitActivity"
    }

    private var app1Package: String? = null
    private var app2Package: String? = null
    private lateinit var app1Button: Button
    private lateinit var app2Button: Button
    private lateinit var launchButton: Button
    
    private var currentApp = -1
    private var shellService: IShellService? = null
    private var isBound = false

    // Connection to the Shell Service (Running inside Shizuku)
    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Log.i(TAG, "Shizuku Service Connected")
            shellService = IShellService.Stub.asInterface(binder)
            isBound = true
            launchButton.text = "LAUNCH SPLIT (Ready)"
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.w(TAG, "Shizuku Service Disconnected")
            shellService = null
            isBound = false
            launchButton.text = "LAUNCH SPLIT (Disconnected)"
        }
    }

    // Listener 1: When Shizuku itself starts/connects
    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        checkShizukuStatus()
    }

    // Listener 2: When user grants permission
    private val requestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
        checkShizukuStatus()
    }

    private val appSelectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val pkg = result.data?.getStringExtra(MainActivity.SELECTED_APP_PACKAGE)
            if (pkg != null) {
                val simple = AppPreferences.getSimpleName(pkg)
                if (currentApp == 1) {
                    app1Package = pkg
                    app1Button.text = "App 1: $simple"
                    AppPreferences.savePackage(this, SPLIT1_KEY, pkg)
                } else if (currentApp == 2) {
                    app2Package = pkg
                    app2Button.text = "App 2: $simple"
                    AppPreferences.savePackage(this, SPLIT2_KEY, pkg)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split)

        app1Button = findViewById(R.id.app1_button)
        app2Button = findViewById(R.id.app2_button)
        launchButton = findViewById(R.id.launch_button_split)
        
        loadSavedApps()

        app1Button.setOnClickListener { currentApp = 1; launchAppPicker() }
        app2Button.setOnClickListener { currentApp = 2; launchAppPicker() }

        launchButton.setOnClickListener {
            if (app1Package != null && app2Package != null) {
                launchSplitApps()
            } else {
                Toast.makeText(this, "Select two apps.", Toast.LENGTH_SHORT).show()
            }
        }

        // Add Listeners
        Shizuku.addBinderReceivedListener(binderReceivedListener)
        Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        
        // Initial Check
        checkShizukuStatus()
    }

    private fun checkShizukuStatus() {
        if (Shizuku.getBinder() == null) {
            // Shizuku not attached yet
            launchButton.text = "Waiting for Shizuku..."
            return
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            bindShizukuService()
        } else {
            launchButton.text = "Requesting Permission..."
            Shizuku.requestPermission(0)
        }
    }

    private fun bindShizukuService() {
        if (isBound) return
        
        try {
            val component = ComponentName(packageName, ShellUserService::class.java.name)
            // VERSION CODE 1 (Matches BuildConfig.VERSION_CODE usually)
            ShizukuBinder.bind(component, userServiceConnection, true, 1)
            launchButton.text = "Binding..."
        } catch (e: Exception) {
            Log.e(TAG, "Bind Failed", e)
            launchButton.text = "Bind Failed"
        }
    }

    private fun loadSavedApps() {
        app1Package = AppPreferences.loadPackage(this, SPLIT1_KEY)
        app1Button.text = "App 1: ${AppPreferences.getSimpleName(app1Package)}"
        app2Package = AppPreferences.loadPackage(this, SPLIT2_KEY)
        app2Button.text = "App 2: ${AppPreferences.getSimpleName(app2Package)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
        
        if (isBound) {
            val component = ComponentName(packageName, ShellUserService::class.java.name)
            ShizukuBinder.unbind(component, userServiceConnection)
            isBound = false
        }
    }

    private fun launchAppPicker() {
        appSelectLauncher.launch(Intent(this, MainActivity::class.java))
    }

    private fun launchSplitApps() {
        val metrics = windowManager.maximumWindowMetrics
        val w = metrics.bounds.width()
        val h = metrics.bounds.height()
        val left = Rect(0, 0, w / 2, h)
        val right = Rect(w / 2, 0, w, h)

        if (isBound && shellService != null) {
            launchButton.text = "LAUNCHING..."
            Thread {
                try {
                    shellService?.forceStop(app1Package)
                    shellService?.forceStop(app2Package)
                    Thread.sleep(400)
                    runOnUiThread {
                        launchApp(app1Package!!, left)
                        launchApp(app2Package!!, right)
                        launchButton.text = "LAUNCH SPLIT"
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Remote Call Failed", e)
                }
            }.start()
        } else {
            Toast.makeText(this, "Shizuku NOT READY. Launching anyway.", Toast.LENGTH_LONG).show()
            checkShizukuStatus() // Try connecting again
            launchApp(app1Package!!, left)
            launchApp(app2Package!!, right)
        }
    }

    private fun launchApp(packageName: String, bounds: Rect) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent == null) return
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val options = ActivityOptions.makeBasic().setLaunchBounds(bounds)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
            Log.e(TAG, "Launch error", e)
        }
    }
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/TriSplitActivity.kt
```kotlin
package com.example.quadrantlauncher

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class TriSplitActivity : AppCompatActivity() {

    companion object {
        const val TRI1_KEY = "TRI1_PACKAGE"
        const val TRI2_KEY = "TRI2_PACKAGE"
        const val TRI3_KEY = "TRI3_PACKAGE"
    }

    private var app1Package: String? = null
    private var app2Package: String? = null
    private var app3Package: String? = null

    private lateinit var app1Button: Button
    private lateinit var app2Button: Button
    private lateinit var app3Button: Button
    private lateinit var launchButton: Button

    private var currentApp = -1
    private var hasShizukuPermission = false

    private val REQUEST_PERMISSION_RESULT_LISTENER =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == ShizukuHelper.SHIZUKU_PERMISSION_REQUEST_CODE) {
                hasShizukuPermission = grantResult == PackageManager.PERMISSION_GRANTED
                checkShizukuPermission()
            }
        }

    private val appSelectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val pkg = result.data?.getStringExtra(MainActivity.SELECTED_APP_PACKAGE)
            if (pkg != null) {
                val simple = AppPreferences.getSimpleName(pkg)
                when (currentApp) {
                    1 -> {
                        app1Package = pkg
                        app1Button.text = "App 1: $simple"
                        AppPreferences.savePackage(this, TRI1_KEY, pkg)
                    }
                    2 -> {
                        app2Package = pkg
                        app2Button.text = "App 2: $simple"
                        AppPreferences.savePackage(this, TRI2_KEY, pkg)
                    }
                    3 -> {
                        app3Package = pkg
                        app3Button.text = "App 3: $simple"
                        AppPreferences.savePackage(this, TRI3_KEY, pkg)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
        setContentView(R.layout.activity_tri_split)

        app1Button = findViewById(R.id.app1_button_tri)
        app2Button = findViewById(R.id.app2_button_tri)
        app3Button = findViewById(R.id.app3_button_tri)
        launchButton = findViewById(R.id.launch_button_tri_split)

        loadSavedApps()

        app1Button.setOnClickListener { currentApp = 1; pickApp() }
        app2Button.setOnClickListener { currentApp = 2; pickApp() }
        app3Button.setOnClickListener { currentApp = 3; pickApp() }

        launchButton.setOnClickListener {
            if (app1Package != null && app2Package != null && app3Package != null) {
                launchSplitApps()
            } else {
                Toast.makeText(this, "Select three apps.", Toast.LENGTH_SHORT).show()
            }
        }
        checkShizukuPermission()
    }

    private fun loadSavedApps() {
        app1Package = AppPreferences.loadPackage(this, TRI1_KEY)
        app1Button.text = "App 1: ${AppPreferences.getSimpleName(app1Package)}"
        app2Package = AppPreferences.loadPackage(this, TRI2_KEY)
        app2Button.text = "App 2: ${AppPreferences.getSimpleName(app2Package)}"
        app3Package = AppPreferences.loadPackage(this, TRI3_KEY)
        app3Button.text = "App 3: ${AppPreferences.getSimpleName(app3Package)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
    }

    private fun checkShizukuPermission() {
        if (ShizukuHelper.isShizukuAvailable()) {
            if (ShizukuHelper.hasPermission()) {
                hasShizukuPermission = true
            } else {
                ShizukuHelper.requestPermission()
            }
        }
    }

    private fun pickApp() {
        appSelectLauncher.launch(Intent(this, MainActivity::class.java))
    }

    private fun launchSplitApps() {
        val metrics = windowManager.maximumWindowMetrics
        val w = metrics.bounds.width()
        val h = metrics.bounds.height()
        val colWidth = w / 3

        val left = Rect(0, 0, colWidth, h)
        val middle = Rect(colWidth, 0, colWidth * 2, h)
        val right = Rect(colWidth * 2, 0, w, h)

        if (hasShizukuPermission) {
            Thread {
                ShizukuHelper.killApp(app1Package!!)
                ShizukuHelper.killApp(app2Package!!)
                ShizukuHelper.killApp(app3Package!!)
                try { Thread.sleep(400) } catch (e: InterruptedException) {}
                runOnUiThread {
                    launchAppIntent(app1Package!!, left)
                    launchAppIntent(app2Package!!, middle)
                    launchAppIntent(app3Package!!, right)
                }
            }.start()
        } else {
            launchAppIntent(app1Package!!, left)
            launchAppIntent(app2Package!!, middle)
            launchAppIntent(app3Package!!, right)
        }
    }

    private fun launchAppIntent(packageName: String, bounds: Rect) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent == null) return

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val options = ActivityOptions.makeBasic().setLaunchBounds(bounds)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
            Log.e("TriSplitActivity", "Failed to launch $packageName", e)
        }
    }
}
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/bg_bubble.xml
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="oval">
    <solid android:color="#444444"/>
    <stroke android:width="2dp" android:color="#888888"/>
</shape>
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/bg_drawer.xml
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#2D2D2D"/>
    <corners android:radius="16dp"/>
    <stroke android:width="1dp" android:color="#444444"/>
</shape>
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/bg_item_active.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#00A0E9"/>
    <corners android:radius="8dp"/>
</shape>
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/bg_item_press.xml
```xml
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

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_box_outline.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#00000000"
        android:strokeColor="#AAAAAA"
        android:strokeWidth="2"
        android:pathData="M3,3h18v18H3z"/>
</vector>
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_cover_final_scale.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/wolf_cover_icon"
    android:inset="70dp" />
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_launcher_foreground_scaled.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/ic_launcher"
    android:insetLeft="15dp"
    android:insetRight="15dp"
    android:insetTop="15dp"
    android:insetBottom="15dp" />
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_lock_closed.xml
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

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_lock_open.xml
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

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_mode_dpi.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M17,2H7C5.9,2 5,2.9 5,4v16c0,1.1 0.9,2 2,2h10c1.1,0 2,-0.9 2,-2V4C19,2.9 18.1,2 17,2z M17,20H7V4h10V20z M9,6h6v2H9V6z M9,10h6v2H9V10z M9,14h6v2H9V14z"/>
</vector>
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_mode_profiles.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zM12,5c1.66,0 3,1.34 3,3s-1.34,3 -3,3 -3,-1.34 -3,-3 1.34,-3 3,-3zM12,19.2c-2.5,0 -4.71,-1.28 -6,-3.22 0.03,-1.99 4,-3.08 6,-3.08 1.99,0 5.97,1.09 6,3.08 -1.29,1.94 -3.5,3.22 -6,3.22z"/>
</vector>
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_mode_resolution.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M21,4H3C2.45,4 2,4.45 2,5v14c0,0.55 0.45,1 1,1h18c0.55,0 1,-0.45 1,-1V5C22,4.45 21.55,4 21,4z M20,18H4V6h16V18z M5.5,7h2v2h-2V7z M8.5,7h2v2h-2V7z M5.5,10h2v2h-2V10z M8.5,10h2v2h-2V10z"/>
</vector>
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_scaler_bubble.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="35dp" />
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_scaler_cover_final.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/wolf_cover_icon"
    android:inset="80dp" />
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_scaler_cover_tiny.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="65dp" />
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_star_border.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#888888">
    <path
        android:fillColor="#888888"
        android:pathData="M22,9.24l-7.19,-0.62L12,2 9.19,8.63 2,9.24l5.46,4.73L5.82,21 12,17.27 18.18,21l-1.63,-7.03L22,9.24zM12,15.4l-3.76,2.27 1,-4.28 -3.32,-2.88 4.38,-0.38L12,6.1l1.71,4.01 4.38,0.38 -3.32,2.88 1,4.28L12,15.4z"/>
</vector>
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_star_filled.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#FFD700">
    <path
        android:fillColor="#FFD700"
        android:pathData="M12,17.27L18.18,21l-1.64,-7.03L22,9.24l-7.19,-0.61L12,2 9.19,8.63 2,9.24l5.46,4.73L5.82,21z"/>
</vector>
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_window_split.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M4,4h16v16H4V4z M12,4v16 M4,12h16"/>
</vector>
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_wolf_cover.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="80dp" />
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/ic_wolf_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="25dp" />
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/scaler_bubble.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="120dp" />
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/scaler_cover.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="120dp" />
```

## File: Cover-Screen-Launcher/app/src/main/res/drawable/scaler_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="20dp" />
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="#121212">

    <com.google.android.material.tabs.TabLayout
        android:id="@+id/tab_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#1E1E1E"
        app:tabTextColor="#AAAAAA"
        app:tabSelectedTextColor="#FFFFFF"
        app:tabIndicatorColor="#3DDC84"
        app:tabMode="fixed"
        app:tabGravity="fill">
        <com.google.android.material.tabs.TabItem
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Favorites" />
        <com.google.android.material.tabs.TabItem
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Search" />
        <com.google.android.material.tabs.TabItem
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="All Apps" />
    </com.google.android.material.tabs.TabLayout>

    <EditText
        android:id="@+id/search_bar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Search Apps..."
        android:textColor="#FFFFFF"
        android:textColorHint="#888888"
        android:padding="12dp"
        android:layout_margin="8dp"
        android:background="@android:drawable/editbox_background"
        android:drawableStart="@android:drawable/ic_menu_search"
        android:drawablePadding="8dp"
        android:singleLine="true"
        android:visibility="gone" />

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/app_list_recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:clipToPadding="false"
            android:paddingBottom="16dp"/>

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/az_recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#333333"
            android:visibility="gone" />
            
        <TextView
            android:id="@+id/empty_view"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:text="No apps found"
            android:visibility="gone"
            android:textSize="18sp"
            android:textColor="#AAAAAA"/>

    </FrameLayout>

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/activity_menu.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:gravity="center">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Select Launch Mode"
        android:textSize="20sp"
        android:layout_marginBottom="24dp" />

    <Button
        android:id="@+id/button_quadrant"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="4-Quadrant Launcher" />

    <Button
        android:id="@+id/button_split"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="2-App Split-Screen" />

    <Button
        android:id="@+id/button_tri_split"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="3-App Split-Screen (Row)" />

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/activity_quadrant.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".QuadrantActivity">

    <Button
        android:id="@+id/q1_button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select App for Quadrant 1" />

    <Button
        android:id="@+id/q2_button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select App for Quadrant 2" />

    <Button
        android:id="@+id/q3_button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select App for Quadrant 3" />

    <Button
        android:id="@+id/q4_button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select App for Quadrant 4" />

    <View
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <Button
        android:id="@+id/launch_button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="LAUNCH"
        style="@style/Widget.AppCompat.Button.Colored" /> </LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/activity_split.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/root_layout_split"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".SplitActivity">

    <Button
        android:id="@+id/app1_button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select App 1 (Left)" />

    <Button
        android:id="@+id/app2_button"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select App 2 (Right)" />

    <View
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <Button
        android:id="@+id/launch_button_split"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="LAUNCH SPLIT"
        style="@style/Widget.AppCompat.Button.Colored" />

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/activity_tri_split.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".TriSplitActivity">

    <Button
        android:id="@+id/app1_button_tri"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select App 1 (Left)" />

    <Button
        android:id="@+id/app2_button_tri"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select App 2 (Middle)" />

    <Button
        android:id="@+id/app3_button_tri"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Select App 3 (Right)" />

    <View
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <Button
        android:id="@+id/launch_button_tri_split"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="LAUNCH 3-APP SPLIT"
        style="@style/Widget.AppCompat.Button.Colored" />

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/item_app_rofi.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="10dp"
    android:gravity="center_vertical"
    android:background="@drawable/bg_item_press">

    <ImageView
        android:id="@+id/rofi_app_icon"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:src="@mipmap/ic_launcher_round"
        android:layout_marginEnd="12dp" />

    <TextView
        android:id="@+id/rofi_app_text"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="App Name"
        android:textColor="#FFFFFF"
        android:textSize="16sp" />

    <ImageView
        android:id="@+id/rofi_app_star"
        android:layout_width="16dp"
        android:layout_height="16dp"
        android:src="@drawable/ic_star_filled"
        android:visibility="gone" />

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/item_custom_resolution.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="8dp"
    android:gravity="center_vertical"
    android:background="@drawable/bg_item_press">

    <EditText
        android:id="@+id/input_res_w"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:hint="W"
        android:inputType="number"
        android:textColor="#FFFFFF"
        android:textColorHint="#888888"
        android:gravity="center"
        android:background="@null"
        android:imeOptions="actionNext"
        android:textSize="16sp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="x"
        android:textColor="#AAAAAA"
        android:paddingStart="8dp"
        android:paddingEnd="8dp"/>

    <EditText
        android:id="@+id/input_res_h"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:hint="H"
        android:inputType="number"
        android:textColor="#FFFFFF"
        android:textColorHint="#888888"
        android:gravity="center"
        android:background="@null"
        android:imeOptions="actionDone"
        android:textSize="16sp"/>

    <ImageView
        android:id="@+id/btn_save_res"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:padding="10dp"
        android:src="@android:drawable/ic_menu_save"
        android:tint="#00A0E9"
        android:background="?attr/selectableItemBackgroundBorderless"/>

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/item_font_size.xml
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
        android:text="Font Size:"
        android:textColor="#FFFFFF"
        android:textSize="16sp"
        android:layout_marginEnd="8dp"/>

    <TextView
        android:id="@+id/text_font_value"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="16"
        android:textColor="#FFFFFF"
        android:textSize="18sp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="sp"
        android:textColor="#888888"
        android:textSize="12sp"
        android:paddingEnd="8dp"/>

    <ImageView
        android:id="@+id/btn_font_minus"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:padding="10dp"
        android:src="@android:drawable/ic_input_delete"
        android:tint="#FF5555"
        android:background="?attr/selectableItemBackgroundBorderless"/>

    <ImageView
        android:id="@+id/btn_font_plus"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:padding="10dp"
        android:src="@android:drawable/ic_input_add"
        android:tint="#3DDC84"
        android:background="?attr/selectableItemBackgroundBorderless"/>

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/item_height_setting.xml
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
        android:text="Height:"
        android:textColor="#FFFFFF"
        android:textSize="16sp"
        android:layout_marginEnd="8dp"/>

    <TextView
        android:id="@+id/text_height_value"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="70"
        android:textColor="#FFFFFF"
        android:textSize="18sp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="%"
        android:textColor="#888888"
        android:textSize="12sp"
        android:paddingEnd="8dp"/>

    <ImageView
        android:id="@+id/btn_height_minus"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:padding="10dp"
        android:src="@android:drawable/ic_input_delete"
        android:tint="#FF5555"
        android:background="?attr/selectableItemBackgroundBorderless"/>

    <ImageView
        android:id="@+id/btn_height_plus"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:padding="10dp"
        android:src="@android:drawable/ic_input_add"
        android:tint="#3DDC84"
        android:background="?attr/selectableItemBackgroundBorderless"/>

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/item_icon_setting.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="12dp"
    android:gravity="center_vertical"
    android:background="@drawable/bg_item_press">

    <TextView
        android:id="@+id/icon_setting_text"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="Launcher Icon"
        android:textColor="#FFFFFF"
        android:textSize="16sp" />

    <ImageView
        android:id="@+id/icon_setting_preview"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:src="@mipmap/ic_launcher_round"
        android:background="@drawable/bg_item_active"
        android:padding="2dp"
        android:clipToOutline="true"/>

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/item_layout_option.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/layout_option_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="12dp"
    android:gravity="center_vertical"
    android:background="@drawable/bg_item_press">

    <EditText
        android:id="@+id/layout_name"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="Layout Name"
        android:textColor="#FFFFFF"
        android:textSize="16sp"
        android:background="@null"
        android:inputType="text"
        android:imeOptions="actionDone"
        android:singleLine="true"
        android:clickable="false"
        android:focusable="false"
        android:longClickable="false"
        android:focusableInTouchMode="false" />

    <ImageView
        android:id="@+id/btn_save_profile"
        android:layout_width="32dp"
        android:layout_height="32dp"
        android:padding="4dp"
        android:src="@android:drawable/ic_menu_save"
        android:tint="#00AAFF"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:visibility="gone"
        android:clickable="true"
        android:focusable="true" />

    <ImageView
        android:id="@+id/btn_extinguish_item"
        android:layout_width="32dp"
        android:layout_height="32dp"
        android:padding="4dp"
        android:src="@android:drawable/ic_lock_power_off"
        android:tint="#FF5555"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:visibility="gone"
        android:clickable="true"
        android:focusable="true" />

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/item_profile_rich.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="10dp"
    android:layout_marginBottom="4dp"
    android:background="@drawable/bg_item_press">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:layout_marginBottom="4dp">

        <EditText
            android:id="@+id/profile_name_text"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Profile Name"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:textSize="16sp"
            android:background="@null"
            android:singleLine="true"
            android:imeOptions="actionDone"
            android:padding="4dp"
            android:clickable="false"
            android:focusable="false"
            android:longClickable="false"
            android:focusableInTouchMode="false"/>

        <ImageView
            android:id="@+id/btn_save_profile_rich"
            android:layout_width="28dp"
            android:layout_height="28dp"
            android:src="@android:drawable/ic_menu_save"
            android:tint="#00A0E9"
            android:visibility="gone"/>
    </LinearLayout>

    <LinearLayout
        android:id="@+id/profile_icons_container"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginBottom="4dp" />

    <TextView
        android:id="@+id/profile_details_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Details"
        android:textColor="#AAAAAA"
        android:textSize="12sp" />

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/item_selected_app.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="48dp"
    android:layout_height="48dp"
    android:padding="4dp">

    <ImageView
        android:id="@+id/selected_app_icon"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@mipmap/ic_launcher_round"
        android:background="@null"
        android:padding="2dp"
        android:clipToOutline="true"/>
</FrameLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/item_width_setting.xml
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
        android:text="Width:"
        android:textColor="#FFFFFF"
        android:textSize="16sp"
        android:layout_marginEnd="8dp"/>

    <TextView
        android:id="@+id/text_width_value"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="90"
        android:textColor="#FFFFFF"
        android:textSize="18sp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="%"
        android:textColor="#888888"
        android:textSize="12sp"
        android:paddingEnd="8dp"/>

    <ImageView
        android:id="@+id/btn_width_minus"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:padding="10dp"
        android:src="@android:drawable/ic_input_delete"
        android:tint="#FF5555"
        android:background="?attr/selectableItemBackgroundBorderless"/>

    <ImageView
        android:id="@+id/btn_width_plus"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:padding="10dp"
        android:src="@android:drawable/ic_input_add"
        android:tint="#3DDC84"
        android:background="?attr/selectableItemBackgroundBorderless"/>

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/layout_bubble.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="60dp"
    android:layout_height="60dp"
    android:background="@drawable/bg_bubble"
    android:filterTouchesWhenObscured="false"> 
    
    <ImageView
        android:id="@+id/bubble_icon"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_gravity="center"
        android:src="@mipmap/ic_launcher_round" 
        android:clipToOutline="true"/>
</FrameLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/layout_rofi_drawer.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#80000000"
    android:clickable="true"
    android:focusable="true"
    android:filterTouchesWhenObscured="false">

    <LinearLayout
        android:id="@+id/drawer_container"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:orientation="vertical"
        android:padding="12dp"
        android:background="@drawable/bg_drawer"
        android:elevation="10dp"
        android:clickable="true"
        android:focusable="true"
        android:filterTouchesWhenObscured="false">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <ImageView
                android:id="@+id/icon_search_mode"
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:padding="6dp"
                android:src="@android:drawable/ic_menu_search"
                android:tint="#AAAAAA"
                android:background="@drawable/bg_item_press"
                android:tooltipText="App List"/>

            <EditText
                android:id="@+id/rofi_search_bar"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:background="@null"
                android:hint="Search apps..."
                android:textColor="#FFFFFF"
                android:textColorHint="#666666"
                android:paddingStart="8dp"
                android:paddingEnd="8dp"
                android:singleLine="true"
                android:textSize="16sp"
                android:imeOptions="actionDone"/>

            <ImageView
                android:id="@+id/icon_execute"
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:padding="6dp"
                android:src="@android:drawable/ic_media_play"
                android:tint="#00FF00"
                android:background="@drawable/bg_item_press"
                android:tooltipText="Launch"/>

            <ImageView
                android:id="@+id/icon_mode_window"
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:padding="6dp"
                android:src="@drawable/ic_window_split" 
                android:tint="#AAAAAA"
                android:background="@drawable/bg_item_press"
                android:tooltipText="Layouts"/>

            <ImageView
                android:id="@+id/icon_mode_resolution"
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:padding="6dp"
                android:src="@drawable/ic_mode_resolution"
                android:tint="#AAAAAA"
                android:background="@drawable/bg_item_press"
                android:tooltipText="Resolution"/>

            <ImageView
                android:id="@+id/icon_mode_dpi"
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:padding="6dp"
                android:src="@drawable/ic_mode_dpi"
                android:tint="#AAAAAA"
                android:background="@drawable/bg_item_press"
                android:tooltipText="DPI"/>

            <ImageView
                android:id="@+id/icon_mode_profiles"
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:padding="6dp"
                android:src="@drawable/ic_mode_profiles"
                android:tint="#AAAAAA"
                android:background="@drawable/bg_item_press"
                android:tooltipText="Profiles"/>

            <ImageView
                android:id="@+id/icon_mode_settings"
                android:layout_width="32dp"
                android:layout_height="32dp"
                android:padding="6dp"
                android:src="@android:drawable/ic_menu_preferences"
                android:tint="#AAAAAA"
                android:background="@drawable/bg_item_press"
                android:tooltipText="Settings"/>
        </LinearLayout>

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/selected_apps_recycler"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:minHeight="50dp"
            android:paddingTop="4dp"
            android:paddingBottom="4dp"
            android:visibility="gone"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:layout_marginTop="4dp"
            android:layout_marginBottom="8dp"
            android:background="#444444" />

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rofi_recycler_view"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:scrollbars="vertical" />

    </LinearLayout>
</FrameLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/list_item_app.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="12dp"
    android:gravity="center_vertical"
    android:background="?attr/selectableItemBackground">

    <ImageView
        android:id="@+id/icon_star"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:src="@drawable/ic_star_border"
        android:layout_marginEnd="16dp"
        android:contentDescription="Favorite Star"
        android:tint="#FFFFFF" />

    <TextView
        android:id="@+id/app_name_text"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:textSize="18sp"
        android:textColor="#FFFFFF"
        android:text="App Name" />

</LinearLayout>
```

## File: Cover-Screen-Launcher/app/src/main/res/mipmap-anydpi-v26/ic_bubble_icon.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/ic_scaler_bubble" />
</adaptive-icon>
```

## File: Cover-Screen-Launcher/app/src/main/res/mipmap-anydpi-v26/ic_bubble.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/scaler_bubble" />
</adaptive-icon>
```

## File: Cover-Screen-Launcher/app/src/main/res/mipmap-anydpi-v26/ic_cover_tiny.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/ic_scaler_cover_tiny" />
</adaptive-icon>
```

## File: Cover-Screen-Launcher/app/src/main/res/mipmap-anydpi-v26/ic_cover_v2.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/scaler_cover" />
</adaptive-icon>
```

## File: Cover-Screen-Launcher/app/src/main/res/mipmap-anydpi-v26/ic_launcher_adaptive.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@android:color/black" />
    <foreground android:drawable="@drawable/ic_launcher_foreground_scaled" />
</adaptive-icon>
```

## File: Cover-Screen-Launcher/app/src/main/res/mipmap-anydpi-v26/ic_launcher_cover_final.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/ic_scaler_cover_final" />
</adaptive-icon>
```

## File: Cover-Screen-Launcher/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/ic_wolf_cover" />
</adaptive-icon>
```

## File: Cover-Screen-Launcher/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/ic_wolf_main" />
</adaptive-icon>
```

## File: Cover-Screen-Launcher/app/src/main/res/values/colors.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
```

## File: Cover-Screen-Launcher/app/src/main/res/values/themes.xml
```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Base.Theme.QuadrantLauncher" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your light theme here. -->
        <!-- <item name="colorPrimary">@color/my_light_primary</item> -->
    </style>

    <style name="Theme.QuadrantLauncher" parent="Base.Theme.QuadrantLauncher" />
</resources>
```

## File: Cover-Screen-Launcher/app/src/main/res/values-night/themes.xml
```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Base.Theme.QuadrantLauncher" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your dark theme here. -->
        <!-- <item name="colorPrimary">@color/my_dark_primary</item> -->
    </style>
</resources>
```

## File: Cover-Screen-Launcher/app/src/main/res/xml/accessibility_service_config.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:description="@string/accessibility_service_description"
    android:accessibilityEventTypes="typeAllMask"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:notificationTimeout="100"
    android:canRetrieveWindowContent="true"
    android:canPerformGestures="true" 
    android:canRequestFilterKeyEvents="true" 
    android:accessibilityFlags="flagRequestTouchExplorationMode|flagRequestFilterKeyEvents|flagRetrieveInteractiveWindows" />
```

## File: Cover-Screen-Launcher/app/src/main/res/xml/backup_rules.xml
```xml
<?xml version="1.0" encoding="utf-8"?><!--
   Sample backup rules file; uncomment and customize as necessary.
   See https://developer.android.com/guide/topics/data/autobackup
   for details.
   Note: This file is ignored for devices older than API 31
   See https://developer.android.com/about/versions/12/backup-restore
-->
<full-backup-content>
    <!--
   <include domain="sharedpref" path="."/>
   <exclude domain="sharedpref" path="device.xml"/>
-->
</full-backup-content>
```

## File: Cover-Screen-Launcher/app/src/main/res/xml/data_extraction_rules.xml
```xml
<?xml version="1.0" encoding="utf-8"?><!--
   Sample data extraction rules file; uncomment and customize as necessary.
   See https://developer.android.com/about/versions/12/backup-restore#xml-changes
   for details.
-->
<data-extraction-rules>
    <cloud-backup>
        <!-- TODO: Use <include> and <exclude> to control what is backed up.
        <include .../>
        <exclude .../>
        -->
    </cloud-backup>
    <!--
    <device-transfer>
        <include .../>
        <exclude .../>
    </device-transfer>
    -->
</data-extraction-rules>
```

## File: Cover-Screen-Launcher/app/.gitignore
```
/build
```

## File: Cover-Screen-Launcher/app/proguard-rules.pro
```
-keep class com.example.quadrantlauncher.ShellUserService { *; }
-keep class com.example.quadrantlauncher.IShellService { *; }
-keep interface com.example.quadrantlauncher.IShellService { *; }
```

## File: Cover-Screen-Launcher/gradle/wrapper/gradle-wrapper.properties
```
#Fri Nov 14 06:51:56 EST 2025
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.13-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

## File: Cover-Screen-Launcher/gradle/libs.versions.toml
```toml
[versions]
agp = "8.13.1"
kotlin = "2.0.21"
coreKtx = "1.10.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
appcompat = "1.6.1"
material = "1.10.0"
activity = "1.8.0"
constraintlayout = "2.1.4"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-activity = { group = "androidx.activity", name = "activity", version.ref = "activity" }
androidx-constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```

## File: Cover-Screen-Launcher/.gitignore
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
*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
```

## File: Cover-Screen-Launcher/build.gradle.kts
```
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
```

## File: Cover-Screen-Launcher/gradle.properties
```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2
```

## File: Cover-Screen-Launcher/gradlew
```
#!/bin/sh

#
# Copyright © 2015 the original authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
#

##############################################################################
#
#   Gradle start up script for POSIX generated by Gradle.
#
#   Important for running:
#
#   (1) You need a POSIX-compliant shell to run this script. If your /bin/sh is
#       noncompliant, but you have some other compliant shell such as ksh or
#       bash, then to run this script, type that shell name before the whole
#       command line, like:
#
#           ksh Gradle
#
#       Busybox and similar reduced shells will NOT work, because this script
#       requires all of these POSIX shell features:
#         * functions;
#         * expansions «$var», «${var}», «${var:-default}», «${var+SET}»,
#           «${var#prefix}», «${var%suffix}», and «$( cmd )»;
#         * compound commands having a testable exit status, especially «case»;
#         * various built-in commands including «command», «set», and «ulimit».
#
#   Important for patching:
#
#   (2) This script targets any POSIX shell, so it avoids extensions provided
#       by Bash, Ksh, etc; in particular arrays are avoided.
#
#       The "traditional" practice of packing multiple parameters into a
#       space-separated string is a well documented source of bugs and security
#       problems, so this is (mostly) avoided, by progressively accumulating
#       options in "$@", and eventually passing that to Java.
#
#       Where the inherited environment variables (DEFAULT_JVM_OPTS, JAVA_OPTS,
#       and GRADLE_OPTS) rely on word-splitting, this is performed explicitly;
#       see the in-line comments for details.
#
#       There are tweaks for specific operating systems such as AIX, CygWin,
#       Darwin, MinGW, and NonStop.
#
#   (3) This script is generated from the Groovy template
#       https://github.com/gradle/gradle/blob/HEAD/platforms/jvm/plugins-application/src/main/resources/org/gradle/api/internal/plugins/unixStartScript.txt
#       within the Gradle project.
#
#       You can find Gradle at https://github.com/gradle/gradle/.
#
##############################################################################

# Attempt to set APP_HOME

# Resolve links: $0 may be a link
app_path=$0

# Need this for daisy-chained symlinks.
while
    APP_HOME=${app_path%"${app_path##*/}"}  # leaves a trailing /; empty if no leading path
    [ -h "$app_path" ]
do
    ls=$( ls -ld "$app_path" )
    link=${ls#*' -> '}
    case $link in             #(
      /*)   app_path=$link ;; #(
      *)    app_path=$APP_HOME$link ;;
    esac
done

# This is normally unused
# shellcheck disable=SC2034
APP_BASE_NAME=${0##*/}
# Discard cd standard output in case $CDPATH is set (https://github.com/gradle/gradle/issues/25036)
APP_HOME=$( cd -P "${APP_HOME:-./}" > /dev/null && printf '%s\n' "$PWD" ) || exit

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD=maximum

warn () {
    echo "$*"
} >&2

die () {
    echo
    echo "$*"
    echo
    exit 1
} >&2

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "$( uname )" in                #(
  CYGWIN* )         cygwin=true  ;; #(
  Darwin* )         darwin=true  ;; #(
  MSYS* | MINGW* )  msys=true    ;; #(
  NONSTOP* )        nonstop=true ;;
esac

CLASSPATH="\\\"\\\""


# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD=java
    if ! command -v java >/dev/null 2>&1
    then
        die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
fi

# Increase the maximum file descriptors if we can.
if ! "$cygwin" && ! "$darwin" && ! "$nonstop" ; then
    case $MAX_FD in #(
      max*)
        # In POSIX sh, ulimit -H is undefined. That's why the result is checked to see if it worked.
        # shellcheck disable=SC2039,SC3045
        MAX_FD=$( ulimit -H -n ) ||
            warn "Could not query maximum file descriptor limit"
    esac
    case $MAX_FD in  #(
      '' | soft) :;; #(
      *)
        # In POSIX sh, ulimit -n is undefined. That's why the result is checked to see if it worked.
        # shellcheck disable=SC2039,SC3045
        ulimit -n "$MAX_FD" ||
            warn "Could not set maximum file descriptor limit to $MAX_FD"
    esac
fi

# Collect all arguments for the java command, stacking in reverse order:
#   * args from the command line
#   * the main class name
#   * -classpath
#   * -D...appname settings
#   * --module-path (only if needed)
#   * DEFAULT_JVM_OPTS, JAVA_OPTS, and GRADLE_OPTS environment variables.

# For Cygwin or MSYS, switch paths to Windows format before running java
if "$cygwin" || "$msys" ; then
    APP_HOME=$( cygpath --path --mixed "$APP_HOME" )
    CLASSPATH=$( cygpath --path --mixed "$CLASSPATH" )

    JAVACMD=$( cygpath --unix "$JAVACMD" )

    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    for arg do
        if
            case $arg in                                #(
              -*)   false ;;                            # don't mess with options #(
              /?*)  t=${arg#/} t=/${t%%/*}              # looks like a POSIX filepath
                    [ -e "$t" ] ;;                      #(
              *)    false ;;
            esac
        then
            arg=$( cygpath --path --ignore --mixed "$arg" )
        fi
        # Roll the args list around exactly as many times as the number of
        # args, so each arg winds up back in the position where it started, but
        # possibly modified.
        #
        # NB: a `for` loop captures its iteration list before it begins, so
        # changing the positional parameters here affects neither the number of
        # iterations, nor the values presented in `arg`.
        shift                   # remove old arg
        set -- "$@" "$arg"      # push replacement arg
    done
fi


# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Collect all arguments for the java command:
#   * DEFAULT_JVM_OPTS, JAVA_OPTS, and optsEnvironmentVar are not allowed to contain shell fragments,
#     and any embedded shellness will be escaped.
#   * For example: A user cannot expect ${Hostname} to be expanded, as it is an environment variable and will be
#     treated as '${Hostname}' itself on the command line.

set -- \
        "-Dorg.gradle.appname=$APP_BASE_NAME" \
        -classpath "$CLASSPATH" \
        -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
        "$@"

# Stop when "xargs" is not available.
if ! command -v xargs >/dev/null 2>&1
then
    die "xargs is not available"
fi

# Use "xargs" to parse quoted args.
#
# With -n1 it outputs one arg per line, with the quotes and backslashes removed.
#
# In Bash we could simply go:
#
#   readarray ARGS < <( xargs -n1 <<<"$var" ) &&
#   set -- "${ARGS[@]}" "$@"
#
# but POSIX shell has neither arrays nor command substitution, so instead we
# post-process each arg (as a line of input to sed) to backslash-escape any
# character that might be a shell metacharacter, then use eval to reverse
# that process (while maintaining the separation between arguments), and wrap
# the whole thing up as a single "set" statement.
#
# This will of course break if any of these variables contains a newline or
# an unmatched quote.
#

eval "set -- $(
        printf '%s\n' "$DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS" |
        xargs -n1 |
        sed ' s~[^-[:alnum:]+,./:=@_]~\\&~g; ' |
        tr '\n' ' '
    )" '"$@"'

exec "$JAVACMD" "$@"
```

## File: Cover-Screen-Launcher/gradlew.bat
```
@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" -jar "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%GRADLE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
```

## File: Cover-Screen-Launcher/multidex-keep.txt
```
rikka/shizuku/
```

## File: Cover-Screen-Launcher/README.md
```markdown
# CoverScreen Launcher & Tiling Manager

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg) ![Platform](https://img.shields.io/badge/Platform-Android%2014%2B-green.svg) ![Status](https://img.shields.io/badge/Status-Experimental-orange.svg)

**CoverScreen Launcher** is an advanced window manager and app launcher designed primarily for Samsung Galaxy Z Flip devices (and other foldables/external displays). 

It bypasses system restrictions to launch **any** application on the Cover Screen and provides desktop-like window tiling capabilities (Quadrants, Split Screen) that the stock OS does not support. It is also optimized for **AR Glasses** (XREAL, Rokid, Viture) by allowing the phone screen to be turned off while keeping the system running.

## 🚀 Key Features

### 🔓 Unrestricted Launching
- Bypasses the "Good Lock" / System Allowlist restriction.
- Launches *any* installed application directly on the Cover Screen (Display 1) using ADB Shell commands via Shizuku.

### 🔲 Advanced Window Tiling
Android's native split-screen on cover screens is limited. This app forces windows into specific geometries:
- **2 Apps (Side-by-Side):** Perfect vertical split.
- **2 Apps (Top-Bottom):** Horizontal split.
- **3 Apps (Tri-Split):** Evenly divided vertical columns.
- **4 Apps (Quadrant):** A 2x2 grid for maximum multitasking.

### 🕶️ AR Glasses Mode ("Extinguish")
Designed for users of AR Glasses who use the phone as a computing unit/trackpad:
- **Screen Off, System On:** Turns off the physical display panel (to save battery and heat) without putting the CPU to sleep.
- **Targeted Control:** Can target specific displays (Main vs. Cover).
- **Trackpad Support:** Integrated toggle to reset/launch a specific Trackpad application when executing layouts.

### 💾 Profiles
- Save your favorite app combinations and layout configurations.
- Quickly load specific workspaces (e.g., "Work" with Docs/Chrome, "Social" with Discord/Reddit).

---

## 🛠️ Prerequisites

This app requires **Shizuku** to function, as it relies on elevated Shell permissions to manage windows and power states.

1.  **Shizuku:** Must be installed and running. [Download Shizuku](https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api)
2.  **Developer Options Enabled:**
    * `Settings > About Phone > Software Information > Tap "Build Number" 7 times`.
3.  **Freeform Windows Enabled:**
    * In Developer Options, enable: **"Force activities to be resizable"**.
    * In Developer Options, enable: **"Enable freeform windows"**.
    * *Recommended:* Reboot device after changing these settings.

---

## ⚙️ How It Works (Technical)

Standard Android APIs (`startActivity`) are blocked by Samsung on the Cover Screen for unapproved apps. This project uses a hybrid approach:

1.  **The Shotgun Launch:**
    * It attempts a standard API launch (for reliability on the main screen).
    * Simultaneously, it executes an `am start` shell command via Shizuku to force the activity onto `Display 1` (Cover Screen).

2.  **Post-Launch Resize (The "Magic" Fix):**
    * Android 14+ removed the ability to set window bounds during the launch command.
    * This app launches the app first, then scans the system using `dumpsys activity activities` to find the specific **Task ID** of the new window.
    * It then issues an `am task resize [taskId] [rect]` command to snap the window into the desired tile position.

3.  **Extinguish Mode:**
    * Uses Java Reflection to access the hidden `SurfaceControl` or `DisplayControl` system APIs.
    * Calls `setDisplayPowerMode(token, 0)` to cut power to the display panel hardware while leaving the OS active.

---

## 📥 Installation

1.  Clone the repo or download the latest APK (check Releases).
2.  Install the APK on your Samsung Z Flip.
3.  Open **Shizuku** and start the service (via Wireless Debugging or Root).
4.  Open **CoverScreen Launcher** and grant Shizuku permission when prompted.
5.  Grant "Overlay Permission" if prompted (required for the floating menu).

---

## 🎮 Usage

1.  **Open the Menu:** Click the floating bubble to open the launcher drawer.
2.  **Select Apps:** Use the search bar to find apps. Click them to add to the launch queue.
3.  **Choose Layout:** Click the "Window" icon and select a layout (e.g., 4-Quadrant).
4.  **Launch:** Click the Green Play button.
    * *Note:* The app will cycle through your selected apps, launching them and then resizing them into position.
5.  **Extinguish (AR Mode):**
    * Go to Settings (Gear Icon).
    * Toggle "Target: Cover Screen".
    * Click the **Power Off** icon next to the toggle.
    * *To Wake:* Press Volume Up or the physical Power button.

---

## 🤝 Contributing

Pull requests are welcome!
* **Architecture:** Kotlin, Android Service, AIDL (for Shizuku IPC).
* **Key Files:** `FloatingLauncherService.kt` (Logic), `ShellUserService.kt` (Shizuku Commands).

## 📄 License

[MIT License](LICENSE)
```

## File: Cover-Screen-Launcher/settings.gradle.kts
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
    }
}
// ... (pluginManagement block is fine)
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
        maven { url = uri("https://maven.rikka.dev/versioned") }
    }
}
rootProject.name = "CoverScreenLauncher"
include(":app")

// DELETE ALL LINES BELOW THIS ONE
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardActivity.kt
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

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardUtils.kt
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

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ProfilesActivity.kt
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

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ShizukuBinder.java
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

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/bg_item_active.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#00A0E9"/>
    <corners android:radius="8dp"/>
</shape>
```

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/bg_item_press.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_cursor.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_launcher_background.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_launcher_foreground.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_lock_closed.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_lock_open.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/red_border.xml
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <stroke
        android:width="4dp"
        android:color="#80FF0000" />
    <solid android:color="#00000000" />
</shape>
```

## File: Cover-Screen-Trackpad/app/src/main/res/layout/activity_keyboard.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/layout/activity_menu.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/layout/activity_profiles.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/layout/layout_trackpad.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/layout/service_overlay.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

## File: Cover-Screen-Trackpad/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

## File: Cover-Screen-Trackpad/app/src/main/res/mipmap-anydpi-v26/ic_trackpad_adaptive.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@android:color/black" />
    <foreground android:drawable="@drawable/ic_trackpad_foreground_scaled" />
</adaptive-icon>
```

## File: Cover-Screen-Trackpad/app/src/main/res/values/colors.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
```

## File: Cover-Screen-Trackpad/app/src/main/res/values/strings.xml
```xml
<resources>
    <string name="app_name">DroidOS Trackpad</string>
    <string name="accessibility_service_description">DroidOS Input Service</string>
</resources>
```

## File: Cover-Screen-Trackpad/app/src/main/res/values/themes.xml
```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Base.Theme.CoverScreenTester" parent="Theme.Material3.DayNight.NoActionBar">
    </style>

    <style name="Theme.CoverScreenTester" parent="Base.Theme.CoverScreenTester" />
</resources>
```

## File: Cover-Screen-Trackpad/app/src/main/res/values-night/themes.xml
```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Base.Theme.CoverScreenTester" parent="Theme.Material3.DayNight.NoActionBar">
        </style>
</resources>
```

## File: Cover-Screen-Trackpad/app/src/main/res/xml/accessibility_service_config.xml
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

## File: Cover-Screen-Trackpad/app/src/main/res/xml/backup_rules.xml
```xml
<?xml version="1.0" encoding="utf-8"?><full-backup-content>
    </full-backup-content>
```

## File: Cover-Screen-Trackpad/app/src/main/res/xml/data_extraction_rules.xml
```xml
<?xml version="1.0" encoding="utf-8"?><data-extraction-rules>
    <cloud-backup>
        </cloud-backup>
    </data-extraction-rules>
```

## File: Cover-Screen-Trackpad/app/.gitignore
```
/build
```

## File: Cover-Screen-Trackpad/app/proguard-rules.pro
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

## File: Cover-Screen-Trackpad/gradle/wrapper/gradle-wrapper.properties
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

## File: Cover-Screen-Trackpad/gradle/libs.versions.toml
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

## File: Cover-Screen-Trackpad/.gitignore
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

## File: Cover-Screen-Trackpad/build.gradle.kts
```
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
```

## File: Cover-Screen-Trackpad/gradle.properties
```
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2
```

## File: Cover-Screen-Trackpad/gradlew
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

## File: Cover-Screen-Trackpad/gradlew.bat
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

## File: Cover-Screen-Trackpad/README.md
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

## File: Cover-Screen-Trackpad/settings.gradle.kts
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

## File: Cover-Screen-Launcher/app/src/main/aidl/com/example/quadrantlauncher/IShellService.aidl.minimize
```
package com.example.quadrantlauncher;

interface IShellService {
    void forceStop(String packageName);
    void runCommand(String command);
    void setScreenOff(int displayIndex, boolean turnOff);
    void repositionTask(String packageName, int left, int top, int right, int bottom);
    List<String> getVisiblePackages(int displayId);
    List<String> getWindowLayouts(int displayId);
    List<String> getAllRunningPackages();
    int getTaskId(String packageName);
    void moveTaskToBack(int taskId);
    void setSystemBrightness(int brightness);
    int getSystemBrightness();
    float getSystemBrightnessFloat();
    void setAutoBrightness(boolean enabled);
    boolean isAutoBrightness();
    boolean setBrightnessViaDisplayManager(int displayId, float brightness);
    void setBrightness(int value);
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt.displayoff
```
package com.example.quadrantlauncher

import android.app.ActivityManager
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
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rikka.shizuku.Shizuku
import java.text.SimpleDateFormat
import java.util.*
import java.lang.reflect.Method
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.hypot
import kotlin.math.min

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
        const val LAYOUT_TOP_BOTTOM = 5
        const val LAYOUT_TRI_EVEN = 3
        const val LAYOUT_CORNERS = 4
        const val LAYOUT_TRI_SIDE_MAIN_SIDE = 6
        const val LAYOUT_QUAD_ROW_EVEN = 7
        const val LAYOUT_CUSTOM_DYNAMIC = 99

        const val CHANNEL_ID = "OverlayServiceChannel"
        const val TAG = "FloatingService"
        const val DEBUG_TAG = "DROIDOS_DEBUG"
        const val ACTION_OPEN_DRAWER = "com.example.quadrantlauncher.OPEN_DRAWER"
        const val ACTION_UPDATE_ICON = "com.example.quadrantlauncher.UPDATE_ICON"
        const val HIGHLIGHT_COLOR = 0xFF00A0E9.toInt()
    }

    private lateinit var windowManager: WindowManager
    private var displayContext: Context? = null
    private var currentDisplayId = 0
    private var lastPhysicalDisplayId = Display.DEFAULT_DISPLAY 

    private var bubbleView: View? = null
    private var drawerView: View? = null
    private var debugStatusView: TextView? = null
    
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
    private var isScreenOffState = false
    private var isInstantMode = true 
    private var showShizukuWarning = true 
    private var useAltScreenOff = false
    
    private var savedBrightness: Int = -1
    private var savedAutoBrightness: Boolean = true
    
    private var isVirtualDisplayActive = false
    private var currentDrawerHeightPercent = 70
    private var currentDrawerWidthPercent = 90
    private var autoResizeEnabled = true
    
    private var reorderSelectionIndex = -1
    private var isReorderDragEnabled = true
    private var isReorderTapEnabled = true
    
    private val PACKAGE_BLANK = "internal.blank.spacer"
    private val PACKAGE_TRACKPAD = "com.katsuyamaki.DroidOSTrackpadKeyboard"
    
    private var shellService: IShellService? = null
    private var isBound = false
    private val uiHandler = Handler(Looper.getMainLooper())

    private val shizukuBinderListener = Shizuku.OnBinderReceivedListener { if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() }
    private val shizukuPermissionListener = Shizuku.OnRequestPermissionResultListener { _, grantResult -> if (grantResult == PackageManager.PERMISSION_GRANTED) bindShizuku() }

    private val commandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == ACTION_OPEN_DRAWER) { 
                if (isScreenOffState) wakeUp() else if (!isExpanded) toggleDrawer() 
            } 
            else if (action == ACTION_UPDATE_ICON) { 
                updateBubbleIcon()
                if (currentMode == MODE_SETTINGS) switchMode(MODE_SETTINGS) 
            }
            else if (action == Intent.ACTION_SCREEN_ON) {
                if (isScreenOffState) {
                    wakeUp()
                }
            }
        }
    }
    
    private val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun getMovementFlags(r: RecyclerView, v: RecyclerView.ViewHolder): Int {
            val pos = v.adapterPosition; if (pos == RecyclerView.NO_POSITION || pos >= displayList.size) return 0
            val item = displayList[pos]
            val isSwipeable = when (currentMode) {
                MODE_LAYOUTS -> (item is LayoutOption && item.type == LAYOUT_CUSTOM_DYNAMIC && item.isCustomSaved)
                MODE_RESOLUTION -> (item is ResolutionOption && item.index >= 100)
                MODE_PROFILES -> (item is ProfileOption && !item.isCurrent)
                MODE_SEARCH -> true
                else -> false
            }
            return if (isSwipeable) makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) else 0
        }
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
        override fun onSwiped(v: RecyclerView.ViewHolder, direction: Int) {
            val pos = v.adapterPosition; if (pos == RecyclerView.NO_POSITION) return
            dismissKeyboardAndRestore()
            if (currentMode == MODE_PROFILES) { val item = displayList.getOrNull(pos) as? ProfileOption ?: return; AppPreferences.deleteProfile(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_PROFILES); return }
            if (currentMode == MODE_LAYOUTS) { val item = displayList.getOrNull(pos) as? LayoutOption ?: return; AppPreferences.deleteCustomLayout(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_LAYOUTS); return }
            if (currentMode == MODE_RESOLUTION) { val item = displayList.getOrNull(pos) as? ResolutionOption ?: return; AppPreferences.deleteCustomResolution(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_RESOLUTION); return }
            if (currentMode == MODE_SEARCH) { val item = displayList.getOrNull(pos) as? MainActivity.AppInfo ?: return; if (item.packageName == PACKAGE_BLANK) { (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos); return }; if (direction == ItemTouchHelper.LEFT && !item.isFavorite) toggleFavorite(item) else if (direction == ItemTouchHelper.RIGHT && item.isFavorite) toggleFavorite(item); refreshSearchList() }
        }
    }

    private val selectedAppsDragCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
        override fun isLongPressDragEnabled(): Boolean = isReorderDragEnabled
        
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean { 
            Collections.swap(selectedAppsQueue, v.adapterPosition, t.adapterPosition)
            r.adapter?.notifyItemMoved(v.adapterPosition, t.adapterPosition)
            return true 
        }

        override fun onSwiped(v: RecyclerView.ViewHolder, direction: Int) { 
            dismissKeyboardAndRestore()
            val pos = v.adapterPosition
            if (pos != RecyclerView.NO_POSITION) { 
                val app = selectedAppsQueue[pos]
                if (app.packageName != PACKAGE_BLANK) { 
                    Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start()
                    safeToast("Killed ${app.label}") 
                }
                selectedAppsQueue.removeAt(pos)
                if (reorderSelectionIndex != -1) endReorderMode(false)
                updateSelectedAppsDock()
                drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
                if (isInstantMode) applyLayoutImmediate() 
            } 
        }

        // Fix 1: Detect drag completion to trigger update
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            // Save the new order to prefs
            val pkgs = selectedAppsQueue.map { it.packageName }
            AppPreferences.saveLastQueue(this@FloatingLauncherService, pkgs)
            
            // Trigger layout update immediately if in Instant Mode
            if (isInstantMode) applyLayoutImmediate()
        }
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) { shellService = IShellService.Stub.asInterface(binder); isBound = true; updateExecuteButtonColor(true); updateBubbleIcon(); safeToast("Shizuku Connected") }
        override fun onServiceDisconnected(name: ComponentName?) { shellService = null; isBound = false; updateExecuteButtonColor(false); updateBubbleIcon() }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        try { Shizuku.addBinderReceivedListener(shizukuBinderListener); Shizuku.addRequestPermissionResultListener(shizukuPermissionListener) } catch (e: Exception) {}
        
        val filter = IntentFilter().apply { 
            addAction(ACTION_OPEN_DRAWER)
            addAction(ACTION_UPDATE_ICON)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(commandReceiver, filter, Context.RECEIVER_EXPORTED) else registerReceiver(commandReceiver, filter)
        try { if (rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() } catch (e: Exception) {}
        
        loadInstalledApps(); currentFontSize = AppPreferences.getFontSize(this)
        killAppOnExecute = AppPreferences.getKillOnExecute(this); targetDisplayIndex = AppPreferences.getTargetDisplayIndex(this)
        isInstantMode = AppPreferences.getInstantMode(this); showShizukuWarning = AppPreferences.getShowShizukuWarning(this)
        useAltScreenOff = AppPreferences.getUseAltScreenOff(this); isReorderDragEnabled = AppPreferences.getReorderDrag(this)
        isReorderTapEnabled = AppPreferences.getReorderTap(this); currentDrawerHeightPercent = AppPreferences.getDrawerHeightPercent(this)
        currentDrawerWidthPercent = AppPreferences.getDrawerWidthPercent(this); autoResizeEnabled = AppPreferences.getAutoResizeKeyboard(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val targetDisplayId = intent?.getIntExtra("DISPLAY_ID", Display.DEFAULT_DISPLAY) ?: Display.DEFAULT_DISPLAY
        if (bubbleView != null && targetDisplayId != currentDisplayId) { try { windowManager.removeView(bubbleView); if (isExpanded) windowManager.removeView(drawerView) } catch (e: Exception) {}; setupDisplayContext(targetDisplayId); setupBubble(); setupDrawer(); updateBubbleIcon(); loadDisplaySettings(currentDisplayId); isExpanded = false; safeToast("Moved to Display $targetDisplayId") } 
        else if (bubbleView == null) { try { setupDisplayContext(targetDisplayId); setupBubble(); setupDrawer(); selectedLayoutType = AppPreferences.getLastLayout(this); activeCustomLayoutName = AppPreferences.getLastCustomLayoutName(this); updateGlobalFontSize(); updateBubbleIcon(); loadDisplaySettings(currentDisplayId); if (selectedLayoutType == LAYOUT_CUSTOM_DYNAMIC && activeCustomLayoutName != null) { val data = AppPreferences.getCustomLayoutData(this, activeCustomLayoutName!!); if (data != null) { val rects = mutableListOf<Rect>(); val rectParts = data.split("|"); for (rp in rectParts) { val coords = rp.split(","); if (coords.size == 4) rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt())) }; activeCustomRects = rects } }; try { if (shellService == null && rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() } catch (e: Exception) {} } catch (e: Exception) { stopSelf() } }
        return START_NOT_STICKY
    }
    
    private fun loadDisplaySettings(displayId: Int) { selectedResolutionIndex = AppPreferences.getDisplayResolution(this, displayId); currentDpiSetting = AppPreferences.getDisplayDpi(this, displayId) }

    override fun onDestroy() {
        super.onDestroy()
        isScreenOffState = false
        wakeUp()
        try { Shizuku.removeBinderReceivedListener(shizukuBinderListener); Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener); unregisterReceiver(commandReceiver) } catch (e: Exception) {}
        try { if (bubbleView != null) windowManager.removeView(bubbleView); if (isExpanded) windowManager.removeView(drawerView) } catch (e: Exception) {}
        if (isBound) { try { ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection); isBound = false } catch (e: Exception) {} }
    }
    
    private fun safeToast(msg: String) { 
        uiHandler.post { 
            try { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() } catch(e: Exception) { }
            if (debugStatusView != null) debugStatusView?.text = msg 
        }
    }
    
    private fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= 31) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(50)
            }
        } catch (e: Exception) {}
    }

    private fun setupDisplayContext(displayId: Int) {
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val display = displayManager.getDisplay(displayId)
        if (display == null) { windowManager = getSystemService(WINDOW_SERVICE) as WindowManager; return }
        currentDisplayId = displayId; displayContext = createDisplayContext(display); windowManager = displayContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private fun refreshDisplayId() { val id = displayContext?.display?.displayId ?: Display.DEFAULT_DISPLAY; currentDisplayId = id }
    private fun startForegroundService() { val channelId = if (android.os.Build.VERSION.SDK_INT >= 26) { val channel = android.app.NotificationChannel(CHANNEL_ID, "Floating Launcher", android.app.NotificationManager.IMPORTANCE_LOW); getSystemService(android.app.NotificationManager::class.java).createNotificationChannel(channel); CHANNEL_ID } else ""; val notification = NotificationCompat.Builder(this, channelId).setContentTitle("CoverScreen Launcher Active").setSmallIcon(R.drawable.ic_launcher_bubble).setPriority(NotificationCompat.PRIORITY_MIN).build(); if (android.os.Build.VERSION.SDK_INT >= 34) startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(1, notification) }
    private fun bindShizuku() { try { val component = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(component, userServiceConnection, true, 1) } catch (e: Exception) { Log.e(TAG, "Bind Shizuku Failed", e) } }
    private fun updateExecuteButtonColor(isReady: Boolean) { uiHandler.post { val executeBtn = drawerView?.findViewById<ImageView>(R.id.icon_execute); if (isReady) executeBtn?.setColorFilter(Color.GREEN) else executeBtn?.setColorFilter(Color.RED) } }

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
                if (velocityTracker == null) velocityTracker = VelocityTracker.obtain(); velocityTracker?.addMovement(event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { initialX = bubbleParams.x; initialY = bubbleParams.y; initialTouchX = event.rawX; initialTouchY = event.rawY; isDrag = false; return true }
                    MotionEvent.ACTION_MOVE -> { if (Math.abs(event.rawX - initialTouchX) > 10 || Math.abs(event.rawY - initialTouchY) > 10) isDrag = true; if (isDrag) { bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt(); bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt(); windowManager.updateViewLayout(bubbleView, bubbleParams) }; return true }
                    MotionEvent.ACTION_UP -> { velocityTracker?.computeCurrentVelocity(1000); val vX = velocityTracker?.xVelocity ?: 0f; val vY = velocityTracker?.yVelocity ?: 0f; val totalVel = hypot(vX.toDouble(), vY.toDouble()); if (isDrag && totalVel > 2500) { safeToast("Closing..."); stopSelf(); return true }; if (!isDrag) { if (!isBound && showShizukuWarning) { if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) { bindShizuku() } else { safeToast("Shizuku NOT Connected. Opening Shizuku..."); launchShizuku() } } else { toggleDrawer() } }; velocityTracker?.recycle(); velocityTracker = null; return true }
                    MotionEvent.ACTION_CANCEL -> { velocityTracker?.recycle(); velocityTracker = null }
                }
                return false
            }
        })
        windowManager.addView(bubbleView, bubbleParams)
    }
    
    private fun launchShizuku() { try { val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api"); if (intent != null) { intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(intent) } else { safeToast("Shizuku app not found") } } catch(e: Exception) { safeToast("Failed to launch Shizuku") } }
    private fun updateBubbleIcon() { val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon) ?: return; if (!isBound && showShizukuWarning) { uiHandler.post { iconView.setImageResource(android.R.drawable.ic_dialog_alert); iconView.setColorFilter(Color.RED); iconView.imageTintList = null }; return }; uiHandler.post { try { val uriStr = AppPreferences.getIconUri(this); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); if (bitmap != null) { iconView.setImageBitmap(bitmap); iconView.imageTintList = null; iconView.clearColorFilter() } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } catch (e: Exception) { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } }
    private fun dismissKeyboardAndRestore() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); if (searchBar != null && searchBar.hasFocus()) { searchBar.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(searchBar.windowToken, 0) }; val dpiInput = drawerView?.findViewById<EditText>(R.id.input_dpi_value); if (dpiInput != null && dpiInput.hasFocus()) { dpiInput.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(dpiInput.windowToken, 0) }; updateDrawerHeight(false) }

    private fun setupDrawer() {
        val context = displayContext ?: this
        val themeContext = ContextThemeWrapper(context, R.style.Theme_QuadrantLauncher)
        drawerView = LayoutInflater.from(themeContext).inflate(R.layout.layout_rofi_drawer, null)
        drawerView!!.fitsSystemWindows = true 
        drawerParams = WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT)
        drawerParams.gravity = Gravity.TOP or Gravity.START; drawerParams.x = 0; drawerParams.y = 0
        drawerParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container)
        if (container != null) { 
            val lp = container.layoutParams as? FrameLayout.LayoutParams
            if (lp != null) { lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; lp.topMargin = 100; container.layoutParams = lp }
            
            debugStatusView = TextView(context)
            debugStatusView?.text = "Ready"
            debugStatusView?.setTextColor(Color.GREEN)
            debugStatusView?.textSize = 10f
            debugStatusView?.gravity = Gravity.CENTER
            container.addView(debugStatusView, 0)
        }

        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val mainRecycler = drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view); val selectedRecycler = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
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
        searchBar.setOnKeyListener { _, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) { if (searchBar.text.isEmpty() && selectedAppsQueue.isNotEmpty()) { val lastIndex = selectedAppsQueue.size - 1; selectedAppsQueue.removeAt(lastIndex); updateSelectedAppsDock(); mainRecycler.adapter?.notifyDataSetChanged(); return@setOnKeyListener true } }; if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { if (searchBar.hasFocus()) { dismissKeyboardAndRestore(); return@setOnKeyListener true } }; return@setOnKeyListener false }
        searchBar.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) { updateDrawerHeight(hasFocus) } }
        mainRecycler.layoutManager = LinearLayoutManager(themeContext); mainRecycler.adapter = RofiAdapter(); val itemTouchHelper = ItemTouchHelper(swipeCallback); itemTouchHelper.attachToRecyclerView(mainRecycler)
        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() { override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) { if (newState == RecyclerView.SCROLL_STATE_DRAGGING) { dismissKeyboardAndRestore() } } })
        mainRecycler.setOnTouchListener { v, event -> if (event.action == MotionEvent.ACTION_DOWN) { dismissKeyboardAndRestore() }; false }
        selectedRecycler.layoutManager = LinearLayoutManager(themeContext, LinearLayoutManager.HORIZONTAL, false); selectedRecycler.adapter = SelectedAppsAdapter(); val dockTouchHelper = ItemTouchHelper(selectedAppsDragCallback); dockTouchHelper.attachToRecyclerView(selectedRecycler)
        drawerView!!.setOnClickListener { toggleDrawer() }
        drawerView!!.isFocusableInTouchMode = true
        drawerView!!.setOnKeyListener { _, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { toggleDrawer(); true } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && isScreenOffState) { wakeUp(); true } else false }
    }
    
    private fun startReorderMode(index: Int) { if (!isReorderTapEnabled) return; if (index < 0 || index >= selectedAppsQueue.size) return; val prevIndex = reorderSelectionIndex; reorderSelectionIndex = index; val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; if (prevIndex != -1) adapter?.notifyItemChanged(prevIndex); adapter?.notifyItemChanged(reorderSelectionIndex); safeToast("Tap another app to Swap") }
    private fun swapReorderItem(targetIndex: Int) { if (reorderSelectionIndex == -1) return; Collections.swap(selectedAppsQueue, reorderSelectionIndex, targetIndex); val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; adapter?.notifyItemChanged(reorderSelectionIndex); adapter?.notifyItemChanged(targetIndex); endReorderMode(true) }
    private fun endReorderMode(triggerInstantMode: Boolean) { val prevIndex = reorderSelectionIndex; reorderSelectionIndex = -1; val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; if (prevIndex != -1) adapter?.notifyItemChanged(prevIndex); if (triggerInstantMode && isInstantMode) applyLayoutImmediate() }
    
    private fun updateDrawerHeight(isKeyboardMode: Boolean) {
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container) ?: return
        val dm = DisplayMetrics(); windowManager.defaultDisplay.getRealMetrics(dm); val screenH = dm.heightPixels; val screenW = dm.widthPixels
        val lp = container.layoutParams as? FrameLayout.LayoutParams; val topMargin = lp?.topMargin ?: 100
        var finalHeight = (screenH * (currentDrawerHeightPercent / 100f)).toInt()
        if (isKeyboardMode) { finalHeight = (screenH * 0.40f).toInt(); val maxAvailable = screenH - topMargin - 20; if (finalHeight > maxAvailable) finalHeight = maxAvailable }
        val newW = (screenW * (currentDrawerWidthPercent / 100f)).toInt()
        if (container.layoutParams.height != finalHeight || container.layoutParams.width != newW) { container.layoutParams.width = newW; container.layoutParams.height = finalHeight; container.requestLayout(); if (drawerParams.y != 0) { drawerParams.y = 0; windowManager.updateViewLayout(drawerView, drawerParams) } }
    }

    private fun toggleDrawer() {
        if (isExpanded) { try { windowManager.removeView(drawerView) } catch(e: Exception) {}; bubbleView?.visibility = View.VISIBLE; isExpanded = false } else { setupDisplayContext(currentDisplayId); updateDrawerHeight(false); try { windowManager.addView(drawerView, drawerParams) } catch(e: Exception) {}; bubbleView?.visibility = View.GONE; isExpanded = true; switchMode(MODE_SEARCH); val et = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); et?.setText(""); et?.clearFocus(); updateSelectedAppsDock(); if (isInstantMode) fetchRunningApps() }
    }
    private fun updateGlobalFontSize() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); searchBar?.textSize = currentFontSize; drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() }
    private fun loadInstalledApps() { val pm = packageManager; val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }; val riList = pm.queryIntentActivities(intent, 0); allAppsList.clear(); allAppsList.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)); for (ri in riList) { val pkg = ri.activityInfo.packageName; if (pkg == PACKAGE_TRACKPAD) continue; val app = MainActivity.AppInfo(ri.loadLabel(pm).toString(), pkg, AppPreferences.isFavorite(this, pkg)); allAppsList.add(app) }; allAppsList.sortBy { it.label.lowercase() } }
    
    private fun launchTrackpad() {
        if (isTrackpadRunning()) { safeToast("Trackpad is already active"); return }
        try { val intent = packageManager.getLaunchIntentForPackage(PACKAGE_TRACKPAD); if (intent != null) { intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP); val dm = DisplayMetrics(); val display = displayContext?.display ?: windowManager.defaultDisplay; display.getRealMetrics(dm); val w = dm.widthPixels; val h = dm.heightPixels; val targetW = (w * 0.5f).toInt(); val targetH = (h * 0.5f).toInt(); val left = (w - targetW) / 2; val top = (h - targetH) / 2; val bounds = Rect(left, top, left + targetW, top + targetH); val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(Rect(left, top, left + targetW, top + targetH)); try { val method = android.app.ActivityOptions::class.java.getMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType); method.invoke(options, 5) } catch (e: Exception) {}; startActivity(intent, options.toBundle()); toggleDrawer(); if (shellService != null) { uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(PACKAGE_TRACKPAD, left, top, left+targetW, top+targetH) } catch(e: Exception) { Log.e(TAG, "Shell launch failed", e) } }.start() }, 400) } } else { safeToast("Trackpad App not found") } } catch (e: Exception) { safeToast("Error launching Trackpad") }
    }

    private fun isTrackpadRunning(): Boolean { try { val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager; val runningApps = am.runningAppProcesses; if (runningApps != null) { for (info in runningApps) { if (info.processName == PACKAGE_TRACKPAD) return true } } } catch (e: Exception) {}; return false }
    private fun getLayoutName(type: Int): String { return when(type) { LAYOUT_FULL -> "1 App - Full"; LAYOUT_SIDE_BY_SIDE -> "Split"; LAYOUT_TOP_BOTTOM -> "Top/Bot"; LAYOUT_TRI_EVEN -> "Tri-Split"; LAYOUT_CORNERS -> "Quadrant"; LAYOUT_TRI_SIDE_MAIN_SIDE -> "3 Apps - Side/Main/Side"; LAYOUT_QUAD_ROW_EVEN -> "4 Apps - Row"; LAYOUT_CUSTOM_DYNAMIC -> "Custom"; else -> "Unknown" } }
    private fun getRatioName(index: Int): String { return when(index) { 1 -> "1:1"; 2 -> "16:9"; 3 -> "32:9"; else -> "Default" } }
    private fun getTargetDimensions(index: Int): Pair<Int, Int>? { return when(index) { 1 -> 1422 to 1500; 2 -> 1920 to 1080; 3 -> 3840 to 1080; else -> null } }
    private fun getResolutionCommand(index: Int): String { return when(index) { 1 -> "wm size 1422x1500 -d $currentDisplayId"; 2 -> "wm size 1920x1080 -d $currentDisplayId"; 3 -> "wm size 3840x1080 -d $currentDisplayId"; else -> "wm size reset -d $currentDisplayId" } }
    private fun sortAppQueue() { selectedAppsQueue.sortWith(compareBy { it.isMinimized }) }
    private fun updateSelectedAppsDock() { val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); if (selectedAppsQueue.isEmpty()) { dock.visibility = View.GONE } else { dock.visibility = View.VISIBLE; dock.adapter?.notifyDataSetChanged(); dock.scrollToPosition(selectedAppsQueue.size - 1) } }
    private fun refreshSearchList() { val query = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString() ?: ""; filterList(query) }
    private fun filterList(query: String) {
        if (currentMode != MODE_SEARCH) return; val actualQuery = query.substringAfterLast(",").trim(); displayList.clear()
        val filtered = if (actualQuery.isEmpty()) { allAppsList } else { allAppsList.filter { it.label.contains(actualQuery, ignoreCase = true) } }
        val sorted = filtered.sortedWith(compareBy<MainActivity.AppInfo> { it.packageName != PACKAGE_BLANK }.thenByDescending { it.isFavorite }.thenBy { it.label.lowercase() }); displayList.addAll(sorted); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    private fun addToSelection(app: MainActivity.AppInfo) {
        dismissKeyboardAndRestore(); val et = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar)
        if (app.packageName == PACKAGE_BLANK) { selectedAppsQueue.add(app); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate(); return }
        val existing = selectedAppsQueue.find { it.packageName == app.packageName }; if (existing != null) { selectedAppsQueue.remove(existing); Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start(); safeToast("Removed ${app.label}"); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); et.setText(""); if (isInstantMode) applyLayoutImmediate() } 
        else { app.isMinimized = false; selectedAppsQueue.add(app); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); et.setText(""); if (isInstantMode) { launchViaApi(app.packageName, null); launchViaShell(app.packageName); uiHandler.postDelayed({ applyLayoutImmediate() }, 200); uiHandler.postDelayed({ applyLayoutImmediate() }, 800) } }
    }
    private fun toggleFavorite(app: MainActivity.AppInfo) { val newState = AppPreferences.toggleFavorite(this, app.packageName); app.isFavorite = newState; allAppsList.find { it.packageName == app.packageName }?.isFavorite = newState }
    private fun launchViaApi(pkg: String, bounds: Rect?) { try { val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return; intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP); val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); if (bounds != null) options.setLaunchBounds(bounds); startActivity(intent, options.toBundle()) } catch (e: Exception) {} }
    private fun launchViaShell(pkg: String) { try { val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return; if (shellService != null) { val component = intent.component?.flattenToShortString() ?: pkg; val cmd = "am start -n $component -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"; Thread { shellService?.runCommand(cmd) }.start() } } catch (e: Exception) {} }
    
    private fun cycleDisplay() {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val displays = dm.displays
        if (isVirtualDisplayActive) { val virtualDisp = displays.firstOrNull { it.displayId >= 2 }; if (virtualDisp != null) { val targetId = if (currentDisplayId == virtualDisp.displayId) { if (displays.any { it.displayId == lastPhysicalDisplayId }) lastPhysicalDisplayId else Display.DEFAULT_DISPLAY } else { lastPhysicalDisplayId = currentDisplayId; virtualDisp.displayId }; performDisplayChange(targetId); return } }
        val currentIdx = displays.indexOfFirst { it.displayId == currentDisplayId }; val nextIdx = if (currentIdx == -1) 0 else (currentIdx + 1) % displays.size; performDisplayChange(displays[nextIdx].displayId)
    }
    private fun performDisplayChange(newId: Int) {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val targetDisplay = dm.getDisplay(newId) ?: return; try { if (bubbleView != null && bubbleView!!.isAttachedToWindow) windowManager.removeView(bubbleView); if (drawerView != null && drawerView!!.isAttachedToWindow) windowManager.removeView(drawerView) } catch (e: Exception) {}; currentDisplayId = newId; setupDisplayContext(currentDisplayId); targetDisplayIndex = currentDisplayId; AppPreferences.setTargetDisplayIndex(this, targetDisplayIndex); setupBubble(); setupDrawer(); loadDisplaySettings(currentDisplayId); updateBubbleIcon(); isExpanded = false; safeToast("Switched to Display $currentDisplayId (${targetDisplay.name})")
    }
    private fun toggleVirtualDisplay(enable: Boolean) { isVirtualDisplayActive = enable; Thread { try { if (enable) { shellService?.runCommand("settings put global overlay_display_devices \"1920x1080/320\""); uiHandler.post { safeToast("Creating Virtual Display... Wait a moment, then Switch Display.") } } else { shellService?.runCommand("settings delete global overlay_display_devices"); uiHandler.post { safeToast("Destroying Virtual Display...") } } } catch (e: Exception) { Log.e(TAG, "Virtual Display Toggle Failed", e) } }.start(); if (currentMode == MODE_SETTINGS) uiHandler.postDelayed({ switchMode(MODE_SETTINGS) }, 500) }

    private fun performScreenOff() {
        vibrate()
        isScreenOffState = true
        safeToast("Screen Off: Double press Power Button to turn on")
        
        if (useAltScreenOff) {
             Thread {
                 try {
                     if (shellService != null) {
                         shellService?.setBrightness(0, -1)
                         uiHandler.post { safeToast("Pixels OFF (Alternate Mode)") }
                     } else {
                         safeToast("Service Disconnected!")
                     }
                 } catch (e: Exception) {
                     Log.e(TAG, "Binder Call Failed", e)
                     safeToast("Error: ${e.message}")
                 }
            }.start()
        } else {
            Thread { try { shellService?.setScreenOff(0, true) } catch (e: Exception) {} }.start()
            safeToast("Screen OFF (SurfaceControl)")
        }
    }
    
    private fun wakeUp() {
        vibrate()
        isScreenOffState = false
        
        Thread { try { shellService?.setBrightness(0, 128) } catch (e: Exception) {} }.start()
        Thread { try { shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start()

        safeToast("Screen On")
        if (currentMode == MODE_SETTINGS) drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }

    private fun applyLayoutImmediate() { executeLaunch(selectedLayoutType, closeDrawer = false) }
    private fun fetchRunningApps() { if (shellService == null) return; Thread { try { val visiblePackages = shellService!!.getVisiblePackages(currentDisplayId); val allRunning = shellService!!.getAllRunningPackages(); val lastQueue = AppPreferences.getLastQueue(this); uiHandler.post { selectedAppsQueue.clear(); for (pkg in lastQueue) { if (pkg == PACKAGE_BLANK) { selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)) } else if (allRunning.contains(pkg)) { val appInfo = allAppsList.find { it.packageName == pkg }; if (appInfo != null) { appInfo.isMinimized = !visiblePackages.contains(pkg); selectedAppsQueue.add(appInfo) } } }; for (pkg in visiblePackages) { if (!lastQueue.contains(pkg)) { val appInfo = allAppsList.find { it.packageName == pkg }; if (appInfo != null) { appInfo.isMinimized = false; selectedAppsQueue.add(appInfo) } } }; sortAppQueue(); updateSelectedAppsDock(); drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); safeToast("Instant Mode: Active") } } catch (e: Exception) { Log.e(TAG, "Error fetching apps", e) } }.start() }
    private fun selectLayout(opt: LayoutOption) { dismissKeyboardAndRestore(); selectedLayoutType = opt.type; activeCustomRects = opt.customRects; if (opt.type == LAYOUT_CUSTOM_DYNAMIC) { activeCustomLayoutName = opt.name; AppPreferences.saveLastCustomLayoutName(this, opt.name) } else { activeCustomLayoutName = null; AppPreferences.saveLastCustomLayoutName(this, null) }; AppPreferences.saveLastLayout(this, opt.type); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() }
    private fun saveCurrentAsCustom() { Thread { try { val rawLayouts = shellService!!.getWindowLayouts(currentDisplayId); if (rawLayouts.isEmpty()) { safeToast("Found 0 active app windows"); return@Thread }; val rectStrings = mutableListOf<String>(); for (line in rawLayouts) { val parts = line.split("|"); if (parts.size == 2) { rectStrings.add(parts[1]) } }; if (rectStrings.isEmpty()) { safeToast("Found 0 valid frames"); return@Thread }; val count = rectStrings.size; var baseName = "$count Apps - Custom"; val existingNames = AppPreferences.getCustomLayoutNames(this); var counter = 1; var finalName = "$baseName $counter"; while (existingNames.contains(finalName)) { counter++; finalName = "$baseName $counter" }; AppPreferences.saveCustomLayout(this, finalName, rectStrings.joinToString("|")); safeToast("Saved: $finalName"); uiHandler.post { switchMode(MODE_LAYOUTS) } } catch (e: Exception) { Log.e(TAG, "Failed to save custom layout", e); safeToast("Error saving: ${e.message}") } }.start() }
    private fun applyResolution(opt: ResolutionOption) { dismissKeyboardAndRestore(); if (opt.index != -1) { selectedResolutionIndex = opt.index; AppPreferences.saveDisplayResolution(this, currentDisplayId, opt.index) }; drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode && opt.index != -1) { Thread { val resCmd = getResolutionCommand(selectedResolutionIndex); shellService?.runCommand(resCmd); Thread.sleep(1500); uiHandler.post { applyLayoutImmediate() } }.start() } }
    private fun selectDpi(value: Int) { currentDpiSetting = if (value == -1) -1 else value.coerceIn(50, 600); AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); Thread { try { if (currentDpiSetting == -1) { shellService?.runCommand("wm density reset -d $currentDisplayId") } else { val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; shellService?.runCommand(dpiCmd) } } catch(e: Exception) { e.printStackTrace() } }.start() }
    private fun changeFontSize(newSize: Float) { currentFontSize = newSize.coerceIn(10f, 30f); AppPreferences.saveFontSize(this, currentFontSize); updateGlobalFontSize(); if (currentMode == MODE_SETTINGS) { switchMode(MODE_SETTINGS) } }
    private fun changeDrawerHeight(delta: Int) { currentDrawerHeightPercent = (currentDrawerHeightPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerHeightPercent(this, currentDrawerHeightPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun changeDrawerWidth(delta: Int) { currentDrawerWidthPercent = (currentDrawerWidthPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerWidthPercent(this, currentDrawerWidthPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun pickIcon() { toggleDrawer(); try { refreshDisplayId(); val intent = Intent(this, IconPickerActivity::class.java); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); val metrics = windowManager.maximumWindowMetrics; val w = 1000; val h = (metrics.bounds.height() * 0.7).toInt(); val x = (metrics.bounds.width() - w) / 2; val y = (metrics.bounds.height() - h) / 2; val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(Rect(x, y, x+w, y+h)); startActivity(intent, options.toBundle()) } catch (e: Exception) { safeToast("Error launching picker: ${e.message}") } }
    private fun saveProfile() { var name = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString()?.trim(); if (name.isNullOrEmpty()) { val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()); name = "Profile_$timestamp" }; val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveProfile(this, name, selectedLayoutType, selectedResolutionIndex, currentDpiSetting, pkgs); safeToast("Saved: $name"); drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText(""); switchMode(MODE_PROFILES) }
    private fun loadProfile(name: String) { val data = AppPreferences.getProfileData(this, name) ?: return; try { val parts = data.split("|"); selectedLayoutType = parts[0].toInt(); selectedResolutionIndex = parts[1].toInt(); currentDpiSetting = parts[2].toInt(); val pkgList = parts[3].split(","); selectedAppsQueue.clear(); for (pkg in pkgList) { if (pkg.isNotEmpty()) { if (pkg == PACKAGE_BLANK) { selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)) } else { val app = allAppsList.find { it.packageName == pkg }; if (app != null) selectedAppsQueue.add(app) } } }; AppPreferences.saveLastLayout(this, selectedLayoutType); AppPreferences.saveDisplayResolution(this, currentDisplayId, selectedResolutionIndex); AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); activeProfileName = name; updateSelectedAppsDock(); safeToast("Loaded: $name"); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() } catch (e: Exception) { Log.e(TAG, "Failed to load profile", e) } }
    
    private fun executeLaunch(layoutType: Int, closeDrawer: Boolean) { 
        if (closeDrawer) toggleDrawer(); refreshDisplayId(); val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveLastQueue(this, pkgs)
        Thread { try { val resCmd = getResolutionCommand(selectedResolutionIndex); shellService?.runCommand(resCmd); if (currentDpiSetting > 0) { val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; shellService?.runCommand(dpiCmd) } else { if (currentDpiSetting == -1) shellService?.runCommand("wm density reset -d $currentDisplayId") }; Thread.sleep(800); val targetDim = getTargetDimensions(selectedResolutionIndex); var w = 0; var h = 0; if (targetDim != null) { w = targetDim.first; h = targetDim.second } else { val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val display = dm.getDisplay(currentDisplayId); if (display != null) { val metrics = DisplayMetrics(); display.getRealMetrics(metrics); w = metrics.widthPixels; h = metrics.heightPixels } else { val bounds = windowManager.maximumWindowMetrics.bounds; w = bounds.width(); h = bounds.height() } }; val rects = mutableListOf<Rect>(); when (layoutType) { LAYOUT_FULL -> { rects.add(Rect(0, 0, w, h)) }; LAYOUT_SIDE_BY_SIDE -> { rects.add(Rect(0, 0, w/2, h)); rects.add(Rect(w/2, 0, w, h)) }; LAYOUT_TOP_BOTTOM -> { rects.add(Rect(0, 0, w, h/2)); rects.add(Rect(0, h/2, w, h)) }; LAYOUT_TRI_EVEN -> { val third = w / 3; rects.add(Rect(0, 0, third, h)); rects.add(Rect(third, 0, third * 2, h)); rects.add(Rect(third * 2, 0, w, h)) }; LAYOUT_CORNERS -> { rects.add(Rect(0, 0, w/2, h/2)); rects.add(Rect(w/2, 0, w, h/2)); rects.add(Rect(0, h/2, w/2, h)); rects.add(Rect(w/2, h/2, w, h)) }; LAYOUT_TRI_SIDE_MAIN_SIDE -> { val quarter = w / 4; rects.add(Rect(0, 0, quarter, h)); rects.add(Rect(quarter, 0, quarter * 3, h)); rects.add(Rect(quarter * 3, 0, w, h)) }; LAYOUT_QUAD_ROW_EVEN -> { val quarter = w / 4; rects.add(Rect(0, 0, quarter, h)); rects.add(Rect(quarter, 0, quarter * 2, h)); rects.add(Rect(quarter * 2, 0, quarter * 3, h)); rects.add(Rect(quarter * 3, 0, w, h)) }; LAYOUT_CUSTOM_DYNAMIC -> { if (activeCustomRects != null) { rects.addAll(activeCustomRects!!) } else { rects.add(Rect(0, 0, w/2, h)); rects.add(Rect(w/2, 0, w, h)) } } }; if (selectedAppsQueue.isNotEmpty()) { val minimizedApps = selectedAppsQueue.filter { it.isMinimized }; for (app in minimizedApps) { if (app.packageName != PACKAGE_BLANK) { try { val tid = shellService?.getTaskId(app.packageName) ?: -1; if (tid != -1) shellService?.moveTaskToBack(tid) } catch (e: Exception) { Log.e(TAG, "Failed to minimize ${app.packageName}", e) } } }; val activeApps = selectedAppsQueue.filter { !it.isMinimized }; if (killAppOnExecute) { for (app in activeApps) { if (app.packageName != PACKAGE_BLANK) { shellService?.forceStop(app.packageName) } }; Thread.sleep(400) } else { Thread.sleep(100) }; val count = Math.min(activeApps.size, rects.size); for (i in 0 until count) { val pkg = activeApps[i].packageName; val bounds = rects[i]; if (pkg == PACKAGE_BLANK) continue; uiHandler.postDelayed({ launchViaApi(pkg, bounds) }, (i * 150).toLong()); uiHandler.postDelayed({ launchViaShell(pkg) }, (i * 150 + 50).toLong()); if (!killAppOnExecute) { uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {} }.start() }, (i * 150 + 150).toLong()) }; uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {} }.start() }, (i * 150 + 800).toLong()) }; if (closeDrawer) { uiHandler.post { selectedAppsQueue.clear(); updateSelectedAppsDock() } } } } catch (e: Exception) { Log.e(TAG, "Execute Failed", e); safeToast("Execute Failed: ${e.message}") } }.start(); drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText("") 
    }
 
    
    private fun calculateGCD(a: Int, b: Int): Int { return if (b == 0) a else calculateGCD(b, a % b) }

    private fun switchMode(mode: Int) {
        currentMode = mode
        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val searchIcon = drawerView!!.findViewById<ImageView>(R.id.icon_search_mode); val iconWin = drawerView!!.findViewById<ImageView>(R.id.icon_mode_window); val iconRes = drawerView!!.findViewById<ImageView>(R.id.icon_mode_resolution); val iconDpi = drawerView!!.findViewById<ImageView>(R.id.icon_mode_dpi); val iconProf = drawerView!!.findViewById<ImageView>(R.id.icon_mode_profiles); val iconSet = drawerView!!.findViewById<ImageView>(R.id.icon_mode_settings); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        searchIcon.setColorFilter(if(mode==MODE_SEARCH) Color.WHITE else Color.GRAY); iconWin.setColorFilter(if(mode==MODE_LAYOUTS) Color.WHITE else Color.GRAY); iconRes.setColorFilter(if(mode==MODE_RESOLUTION) Color.WHITE else Color.GRAY); iconDpi.setColorFilter(if(mode==MODE_DPI) Color.WHITE else Color.GRAY); iconProf.setColorFilter(if(mode==MODE_PROFILES) Color.WHITE else Color.GRAY); iconSet.setColorFilter(if(mode==MODE_SETTINGS) Color.WHITE else Color.GRAY)
        executeBtn.visibility = if (isInstantMode) View.GONE else View.VISIBLE; displayList.clear(); val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); dock.visibility = if (mode == MODE_SEARCH && selectedAppsQueue.isNotEmpty()) View.VISIBLE else View.GONE
        
        when (mode) {
            MODE_SEARCH -> { searchBar.hint = "Search apps..."; refreshSearchList() }
            MODE_LAYOUTS -> { 
                searchBar.hint = "Select Layout"; displayList.add(ActionOption("Save Current Arrangement") { saveCurrentAsCustom() }); displayList.add(LayoutOption("1 App - Full Screen", LAYOUT_FULL)); displayList.add(LayoutOption("2 Apps - Side by Side", LAYOUT_SIDE_BY_SIDE)); displayList.add(LayoutOption("2 Apps - Top & Bottom", LAYOUT_TOP_BOTTOM)); displayList.add(LayoutOption("3 Apps - Even", LAYOUT_TRI_EVEN)); displayList.add(LayoutOption("3 Apps - Side/Main/Side (25/50/25)", LAYOUT_TRI_SIDE_MAIN_SIDE)); displayList.add(LayoutOption("4 Apps - Corners", LAYOUT_CORNERS)); displayList.add(LayoutOption("4 Apps - Row (Even)", LAYOUT_QUAD_ROW_EVEN));
                val customNames = AppPreferences.getCustomLayoutNames(this).sorted(); for (name in customNames) { val data = AppPreferences.getCustomLayoutData(this, name); if (data != null) { try { val rects = mutableListOf<Rect>(); val rectParts = data.split("|"); for (rp in rectParts) { val coords = rp.split(","); if (coords.size == 4) { rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt())) } }; displayList.add(LayoutOption(name, LAYOUT_CUSTOM_DYNAMIC, true, rects)) } catch(e: Exception) {} } } 
            }
            MODE_RESOLUTION -> {
                searchBar.hint = "Select Resolution"; displayList.add(CustomResInputOption); val savedResNames = AppPreferences.getCustomResolutionNames(this).sorted(); for (name in savedResNames) { val value = AppPreferences.getCustomResolutionValue(this, name) ?: continue; displayList.add(ResolutionOption(name, "wm size  -d $currentDisplayId", 100 + savedResNames.indexOf(name))) }; displayList.add(ResolutionOption("Default (Reset)", "wm size reset -d $currentDisplayId", 0)); displayList.add(ResolutionOption("1:1 Square (1422x1500)", "wm size 1422x1500 -d $currentDisplayId", 1)); displayList.add(ResolutionOption("16:9 Landscape (1920x1080)", "wm size 1920x1080 -d $currentDisplayId", 2)); displayList.add(ResolutionOption("32:9 Ultrawide (3840x1080)", "wm size 3840x1080 -d $currentDisplayId", 3))
            }
            MODE_DPI -> { searchBar.hint = "Adjust Density (DPI)"; displayList.add(ActionOption("Reset Density (Default)") { selectDpi(-1) }); var savedDpi = currentDpiSetting; if (savedDpi <= 0) { savedDpi = displayContext?.resources?.configuration?.densityDpi ?: 160 }; displayList.add(DpiOption(savedDpi)) }
            MODE_PROFILES -> { searchBar.hint = "Enter Profile Name..."; displayList.add(ProfileOption("Save Current as New", true, 0,0,0, emptyList())); val profileNames = AppPreferences.getProfileNames(this).sorted(); for (pName in profileNames) { val data = AppPreferences.getProfileData(this, pName); if (data != null) { try { val parts = data.split("|"); val lay = parts[0].toInt(); val res = parts[1].toInt(); val d = parts[2].toInt(); val pkgs = parts[3].split(",").filter { it.isNotEmpty() }; displayList.add(ProfileOption(pName, false, lay, res, d, pkgs)) } catch(e: Exception) {} } } }
            MODE_SETTINGS -> {
                searchBar.hint = "Settings"
                displayList.add(ActionOption("Launch DroidOS Trackpad") { launchTrackpad() }) 
                displayList.add(ActionOption("Switch Display (Current $currentDisplayId)") { cycleDisplay() })
                displayList.add(ToggleOption("Virtual Display (1080p)", isVirtualDisplayActive) { toggleVirtualDisplay(it) })
                displayList.add(HeightOption(currentDrawerHeightPercent))
                displayList.add(WidthOption(currentDrawerWidthPercent))
                displayList.add(ToggleOption("Auto-Shrink for Keyboard", autoResizeEnabled) { autoResizeEnabled = it; AppPreferences.setAutoResizeKeyboard(this, it) })
                displayList.add(FontSizeOption(currentFontSize))
                displayList.add(IconOption("Launcher Icon (Tap to Change)"))
                displayList.add(ToggleOption("Reorder: Drag & Drop", isReorderDragEnabled) { isReorderDragEnabled = it; AppPreferences.setReorderDrag(this, it) })
                displayList.add(ToggleOption("Reorder: Tap to Swap (Long Press)", isReorderTapEnabled) { isReorderTapEnabled = it; AppPreferences.setReorderTap(this, it) })
                displayList.add(ToggleOption("Instant Mode (Live Changes)", isInstantMode) { isInstantMode = it; AppPreferences.setInstantMode(this, it); executeBtn.visibility = if (it) View.GONE else View.VISIBLE; if (it) fetchRunningApps() })
                displayList.add(ToggleOption("Kill App on Execute", killAppOnExecute) { killAppOnExecute = it; AppPreferences.setKillOnExecute(this, it) })
                
                // STANDARD MODE TOGGLE
                displayList.add(ToggleOption("Screen Off (Standard)", isScreenOffState && !useAltScreenOff) { 
                    if (it) {
                        if (isScreenOffState) wakeUp() // Reset if already off
                        useAltScreenOff = false
                        AppPreferences.setUseAltScreenOff(this, false)
                        performScreenOff()
                    } else {
                        wakeUp()
                    }
                })

                // ALTERNATE MODE TOGGLE
                displayList.add(ToggleOption("Screen Off (Alternate)", isScreenOffState && useAltScreenOff) { 
                    if (it) {
                        if (isScreenOffState) wakeUp() // Reset if already off
                        useAltScreenOff = true
                        AppPreferences.setUseAltScreenOff(this, true)
                        performScreenOff()
                    } else {
                        wakeUp()
                    }
                })
                
                displayList.add(ToggleOption("Shizuku Warning (Icon Alert)", showShizukuWarning) { showShizukuWarning = it; AppPreferences.setShowShizukuWarning(this, it); updateBubbleIcon() })
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
    data class TimeoutOption(val seconds: Int)

    inner class SelectedAppsAdapter : RecyclerView.Adapter<SelectedAppsAdapter.Holder>() {
        inner class Holder(v: View) : RecyclerView.ViewHolder(v) { val icon: ImageView = v.findViewById(R.id.selected_app_icon) }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder { return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_app, parent, false)) }
        override fun onBindViewHolder(holder: Holder, position: Int) { 
            val app = selectedAppsQueue[position]; if (position == reorderSelectionIndex) { holder.icon.setColorFilter(HIGHLIGHT_COLOR); holder.icon.alpha = 1.0f; holder.itemView.scaleX = 1.1f; holder.itemView.scaleY = 1.1f; holder.itemView.background = null } else { holder.icon.clearColorFilter(); holder.itemView.scaleX = 1.0f; holder.itemView.scaleY = 1.0f; holder.itemView.background = null; if (app.packageName == PACKAGE_BLANK) { holder.icon.setImageResource(R.drawable.ic_box_outline); holder.icon.alpha = 1.0f } else { try { holder.icon.setImageDrawable(packageManager.getApplicationIcon(app.packageName)) } catch (e: Exception) { holder.icon.setImageResource(R.drawable.ic_launcher_bubble) }; holder.icon.alpha = if (app.isMinimized) 0.4f else 1.0f } }
            holder.itemView.setOnClickListener { try { dismissKeyboardAndRestore(); if (reorderSelectionIndex != -1) { if (position == reorderSelectionIndex) { endReorderMode(false) } else { swapReorderItem(position) } } else { if (app.packageName != PACKAGE_BLANK) { app.isMinimized = !app.isMinimized; notifyItemChanged(position); if (isInstantMode) applyLayoutImmediate() } } } catch(e: Exception) {} }
            
            // Fix 2: Conditional Long Click Handler
            holder.itemView.setOnLongClickListener { 
                if (isReorderDragEnabled) {
                    false // Return false to let ItemTouchHelper take over for Drag
                } else if (isReorderTapEnabled) {
                    startReorderMode(position)
                    true // Consume event for Tap-to-Swap
                } else {
                    false
                }
            }
        }
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
            else if (holder is ProfileRichHolder && item is ProfileOption) { holder.name.setText(item.name); holder.iconsContainer.removeAllViews(); if (!item.isCurrent) { for (pkg in item.apps.take(5)) { val iv = ImageView(holder.itemView.context); val lp = LinearLayout.LayoutParams(60, 60); lp.marginEnd = 8; iv.layoutParams = lp; if (pkg == PACKAGE_BLANK) { iv.setImageResource(R.drawable.ic_box_outline) } else { try { iv.setImageDrawable(packageManager.getApplicationIcon(pkg)) } catch (e: Exception) { iv.setImageResource(R.drawable.ic_launcher_bubble) } }; holder.iconsContainer.addView(iv) }; val info = "${getLayoutName(item.layout)} | ${getRatioName(item.resIndex)} | ${item.dpi}dpi"; holder.details.text = info; holder.details.visibility = View.VISIBLE; holder.btnSave.visibility = View.GONE; if (activeProfileName == item.name) { holder.itemView.setBackgroundResource(R.drawable.bg_item_active) } else { holder.itemView.setBackgroundResource(0) }; holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); loadProfile(item.name) }; holder.itemView.setOnLongClickListener { startRename(holder.name); true }; val saveProfileName = { val newName = holder.name.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameProfile(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); switchMode(MODE_PROFILES) } }; endRename(holder.name) }; holder.name.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveProfileName(); holder.name.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(holder.name.windowToken, 0); updateDrawerHeight(false); true } else false }; holder.name.setOnFocusChangeListener { v, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus); if (!hasFocus) saveProfileName() } } else { holder.iconsContainer.removeAllViews(); holder.details.visibility = View.GONE; holder.btnSave.visibility = View.VISIBLE; holder.itemView.setBackgroundResource(0); holder.name.isEnabled = true; holder.name.isFocusable = true; holder.name.isFocusableInTouchMode = true; holder.itemView.setOnClickListener { saveProfile() }; holder.btnSave.setOnClickListener { saveProfile() } } }
            else if (holder is LayoutHolder) {
                holder.btnSave.visibility = View.GONE; holder.btnExtinguish.visibility = View.GONE
                if (item is LayoutOption) { holder.nameInput.setText(item.name); val isSelected = if (item.type == LAYOUT_CUSTOM_DYNAMIC) { item.type == selectedLayoutType && item.name == activeCustomLayoutName } else { item.type == selectedLayoutType && activeCustomLayoutName == null }; if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { selectLayout(item) }; if (item.isCustomSaved) { holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }; val saveLayoutName = { val newName = holder.nameInput.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameCustomLayout(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); if (activeCustomLayoutName == item.name) { activeCustomLayoutName = newName; AppPreferences.saveLastCustomLayoutName(holder.itemView.context, newName) }; switchMode(MODE_LAYOUTS) } }; endRename(holder.nameInput) }; holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveLayoutName(); true } else false }; holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveLayoutName() } } else { holder.nameInput.isEnabled = false; holder.nameInput.isFocusable = false; holder.nameInput.setTextColor(Color.WHITE) } }
                else if (item is ResolutionOption) { 
                    holder.nameInput.setText(item.name); if (item.index >= 100) { holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }; val saveResName = { val newName = holder.nameInput.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameCustomResolution(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); switchMode(MODE_RESOLUTION) } }; endRename(holder.nameInput) }; holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveResName(); true } else false }; holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveResName() } } else { holder.nameInput.isEnabled = false; holder.nameInput.isFocusable = false; holder.nameInput.setTextColor(Color.WHITE) }; val isSelected = (item.index == selectedResolutionIndex); if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { applyResolution(item) } 
                }
                else if (item is IconOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { pickIcon() } }
                else if (item is ToggleOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); if (item.isEnabled) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.isEnabled = !item.isEnabled; item.onToggle(item.isEnabled); notifyItemChanged(position) } } 
                else if (item is ActionOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.action() } }
            }
            else if (holder is CustomResInputHolder) {
                holder.btnSave.setOnClickListener { val wStr = holder.inputW.text.toString().trim(); val hStr = holder.inputH.text.toString().trim(); if (wStr.isNotEmpty() && hStr.isNotEmpty()) { val w = wStr.toIntOrNull(); val h = hStr.toIntOrNull(); if (w != null && h != null && w > 0 && h > 0) { val gcdVal = calculateGCD(w, h); val wRatio = w / gcdVal; val hRatio = h / gcdVal; val resString = "${w}x${h}"; val name = "$wRatio:$hRatio Custom ($resString)"; AppPreferences.saveCustomResolution(holder.itemView.context, name, resString); safeToast("Added $name"); dismissKeyboardAndRestore(); switchMode(MODE_RESOLUTION) } else { safeToast("Invalid numbers") } } else { safeToast("Input W and H") } }
                holder.inputW.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }; holder.inputH.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }
            }
            else if (holder is IconSettingHolder && item is IconOption) { try { val uriStr = AppPreferences.getIconUri(holder.itemView.context); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); holder.preview.setImageBitmap(bitmap) } else { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) } } catch(e: Exception) { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) }; holder.itemView.setOnClickListener { pickIcon() } }
            else if (holder is DpiHolder && item is DpiOption) { 
                holder.input.setText(item.currentDpi.toString()); holder.input.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { val valInt = v.text.toString().toIntOrNull(); if (valInt != null) { selectDpi(valInt); safeToast("DPI set to $valInt") }; dismissKeyboardAndRestore(); true } else false }; holder.input.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus); if (!hasFocus) { val valInt = holder.input.text.toString().toIntOrNull(); if (valInt != null && valInt != item.currentDpi) { selectDpi(valInt) } } }; holder.btnMinus.setOnClickListener { val v = holder.input.text.toString().toIntOrNull() ?: 160; val newVal = (v - 5).coerceAtLeast(50); holder.input.setText(newVal.toString()); selectDpi(newVal) }; holder.btnPlus.setOnClickListener { val v = holder.input.text.toString().toIntOrNull() ?: 160; val newVal = (v + 5).coerceAtMost(600); holder.input.setText(newVal.toString()); selectDpi(newVal) } 
            }
            else if (holder is FontSizeHolder && item is FontSizeOption) { holder.textVal.text = item.currentSize.toInt().toString(); holder.btnMinus.setOnClickListener { changeFontSize(item.currentSize - 1) }; holder.btnPlus.setOnClickListener { changeFontSize(item.currentSize + 1) } }
            else if (holder is HeightHolder && item is HeightOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerHeight(-5) }; holder.btnPlus.setOnClickListener { changeDrawerHeight(5) } }
            else if (holder is WidthHolder && item is WidthOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerWidth(-5) }; holder.btnPlus.setOnClickListener { changeDrawerWidth(5) } }
        }
        override fun getItemCount() = displayList.size
    }
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt.minimize
```
package com.example.quadrantlauncher

import android.app.ActivityManager
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
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rikka.shizuku.Shizuku
import java.text.SimpleDateFormat
import java.util.*
import java.lang.reflect.Method
import java.io.BufferedReader
import java.io.InputStreamReader
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
        const val LAYOUT_TOP_BOTTOM = 5
        const val LAYOUT_TRI_EVEN = 3
        const val LAYOUT_CORNERS = 4
        const val LAYOUT_TRI_SIDE_MAIN_SIDE = 6
        const val LAYOUT_QUAD_ROW_EVEN = 7
        const val LAYOUT_CUSTOM_DYNAMIC = 99

        const val CHANNEL_ID = "OverlayServiceChannel"
        const val TAG = "FloatingService"
        const val DEBUG_TAG = "DROIDOS_DEBUG"
        const val ACTION_OPEN_DRAWER = "com.example.quadrantlauncher.OPEN_DRAWER"
        const val ACTION_UPDATE_ICON = "com.example.quadrantlauncher.UPDATE_ICON"
        const val HIGHLIGHT_COLOR = 0xFF00A0E9.toInt()
    }

    private lateinit var windowManager: WindowManager
    private var displayContext: Context? = null
    private var currentDisplayId = 0
    private var lastPhysicalDisplayId = Display.DEFAULT_DISPLAY 

    private var bubbleView: View? = null
    private var drawerView: View? = null
    private var debugStatusView: TextView? = null
    
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
    private var isScreenOffState = false
    private var isInstantMode = true 
    private var showShizukuWarning = true 
    private var useAltScreenOff = false
    
    private var isVirtualDisplayActive = false
    private var currentDrawerHeightPercent = 70
    private var currentDrawerWidthPercent = 90
    private var autoResizeEnabled = true
    
    private var reorderSelectionIndex = -1
    private var isReorderDragEnabled = true
    private var isReorderTapEnabled = true
    
    private val PACKAGE_BLANK = "internal.blank.spacer"
    private val PACKAGE_TRACKPAD = "com.katsuyamaki.DroidOSTrackpadKeyboard"
    
    private var shellService: IShellService? = null
    private var isBound = false
    private val uiHandler = Handler(Looper.getMainLooper())

    private val shizukuBinderListener = Shizuku.OnBinderReceivedListener { if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() }
    private val shizukuPermissionListener = Shizuku.OnRequestPermissionResultListener { _, grantResult -> if (grantResult == PackageManager.PERMISSION_GRANTED) bindShizuku() }

    private val commandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == ACTION_OPEN_DRAWER) { 
                if (isScreenOffState) wakeUp() else if (!isExpanded) toggleDrawer() 
            } 
            else if (action == ACTION_UPDATE_ICON) { 
                updateBubbleIcon()
                if (currentMode == MODE_SETTINGS) switchMode(MODE_SETTINGS) 
            }
            else if (action == Intent.ACTION_SCREEN_ON) {
                if (isScreenOffState) {
                    wakeUp()
                }
            }
        }
    }
    private val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun getMovementFlags(r: RecyclerView, v: RecyclerView.ViewHolder): Int {
            val pos = v.adapterPosition; if (pos == RecyclerView.NO_POSITION || pos >= displayList.size) return 0
            val item = displayList[pos]
            val isSwipeable = when (currentMode) {
                MODE_LAYOUTS -> (item is LayoutOption && item.type == LAYOUT_CUSTOM_DYNAMIC && item.isCustomSaved)
                MODE_RESOLUTION -> (item is ResolutionOption && item.index >= 100)
                MODE_PROFILES -> (item is ProfileOption && !item.isCurrent)
                MODE_SEARCH -> true
                else -> false
            }
            return if (isSwipeable) makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) else 0
        }
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
        override fun onSwiped(v: RecyclerView.ViewHolder, direction: Int) {
            val pos = v.adapterPosition; if (pos == RecyclerView.NO_POSITION) return
            dismissKeyboardAndRestore()
            if (currentMode == MODE_PROFILES) { val item = displayList.getOrNull(pos) as? ProfileOption ?: return; AppPreferences.deleteProfile(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_PROFILES); return }
            if (currentMode == MODE_LAYOUTS) { val item = displayList.getOrNull(pos) as? LayoutOption ?: return; AppPreferences.deleteCustomLayout(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_LAYOUTS); return }
            if (currentMode == MODE_RESOLUTION) { val item = displayList.getOrNull(pos) as? ResolutionOption ?: return; AppPreferences.deleteCustomResolution(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_RESOLUTION); return }
            if (currentMode == MODE_SEARCH) { val item = displayList.getOrNull(pos) as? MainActivity.AppInfo ?: return; if (item.packageName == PACKAGE_BLANK) { (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos); return }; if (direction == ItemTouchHelper.LEFT && !item.isFavorite) toggleFavorite(item) else if (direction == ItemTouchHelper.RIGHT && item.isFavorite) toggleFavorite(item); refreshSearchList() }
        }
    }

    private val selectedAppsDragCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
        override fun isLongPressDragEnabled(): Boolean = isReorderDragEnabled
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean { Collections.swap(selectedAppsQueue, v.adapterPosition, t.adapterPosition); r.adapter?.notifyItemMoved(v.adapterPosition, t.adapterPosition); return true }
        override fun onSwiped(v: RecyclerView.ViewHolder, direction: Int) { dismissKeyboardAndRestore(); val pos = v.adapterPosition; if (pos != RecyclerView.NO_POSITION) { val app = selectedAppsQueue[pos]; if (app.packageName != PACKAGE_BLANK) { Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start(); safeToast("Killed ${app.label}") }; selectedAppsQueue.removeAt(pos); if (reorderSelectionIndex != -1) endReorderMode(false); updateSelectedAppsDock(); drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() } }
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) { super.clearView(recyclerView, viewHolder); val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveLastQueue(this@FloatingLauncherService, pkgs); if (isInstantMode) applyLayoutImmediate() }
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) { shellService = IShellService.Stub.asInterface(binder); isBound = true; updateExecuteButtonColor(true); updateBubbleIcon(); safeToast("Shizuku Connected") }
        override fun onServiceDisconnected(name: ComponentName?) { shellService = null; isBound = false; updateExecuteButtonColor(false); updateBubbleIcon() }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        try { Shizuku.addBinderReceivedListener(shizukuBinderListener); Shizuku.addRequestPermissionResultListener(shizukuPermissionListener) } catch (e: Exception) {}
        val filter = IntentFilter().apply { 
            addAction(ACTION_OPEN_DRAWER)
            addAction(ACTION_UPDATE_ICON)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(commandReceiver, filter, Context.RECEIVER_EXPORTED) else registerReceiver(commandReceiver, filter)
        try { if (rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() } catch (e: Exception) {}
        
        loadInstalledApps(); currentFontSize = AppPreferences.getFontSize(this)
        killAppOnExecute = AppPreferences.getKillOnExecute(this); targetDisplayIndex = AppPreferences.getTargetDisplayIndex(this)
        isInstantMode = AppPreferences.getInstantMode(this); showShizukuWarning = AppPreferences.getShowShizukuWarning(this)
        useAltScreenOff = AppPreferences.getUseAltScreenOff(this); isReorderDragEnabled = AppPreferences.getReorderDrag(this)
        isReorderTapEnabled = AppPreferences.getReorderTap(this); currentDrawerHeightPercent = AppPreferences.getDrawerHeightPercent(this)
        currentDrawerWidthPercent = AppPreferences.getDrawerWidthPercent(this); autoResizeEnabled = AppPreferences.getAutoResizeKeyboard(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val targetDisplayId = intent?.getIntExtra("DISPLAY_ID", Display.DEFAULT_DISPLAY) ?: Display.DEFAULT_DISPLAY
        if (bubbleView != null && targetDisplayId != currentDisplayId) { try { windowManager.removeView(bubbleView); if (isExpanded) windowManager.removeView(drawerView) } catch (e: Exception) {}; setupDisplayContext(targetDisplayId); setupBubble(); setupDrawer(); updateBubbleIcon(); loadDisplaySettings(currentDisplayId); isExpanded = false; safeToast("Moved to Display $targetDisplayId") } 
        else if (bubbleView == null) { try { setupDisplayContext(targetDisplayId); setupBubble(); setupDrawer(); selectedLayoutType = AppPreferences.getLastLayout(this); activeCustomLayoutName = AppPreferences.getLastCustomLayoutName(this); updateGlobalFontSize(); updateBubbleIcon(); loadDisplaySettings(currentDisplayId); if (selectedLayoutType == LAYOUT_CUSTOM_DYNAMIC && activeCustomLayoutName != null) { val data = AppPreferences.getCustomLayoutData(this, activeCustomLayoutName!!); if (data != null) { val rects = mutableListOf<Rect>(); val rectParts = data.split("|"); for (rp in rectParts) { val coords = rp.split(","); if (coords.size == 4) rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt())) }; activeCustomRects = rects } }; try { if (shellService == null && rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() } catch (e: Exception) {} } catch (e: Exception) { stopSelf() } }
        return START_NOT_STICKY
    }
    
    private fun loadDisplaySettings(displayId: Int) { selectedResolutionIndex = AppPreferences.getDisplayResolution(this, displayId); currentDpiSetting = AppPreferences.getDisplayDpi(this, displayId) }

    override fun onDestroy() {
        super.onDestroy()
        isScreenOffState = false
        wakeUp()
        try { Shizuku.removeBinderReceivedListener(shizukuBinderListener); Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener); unregisterReceiver(commandReceiver) } catch (e: Exception) {}
        try { if (bubbleView != null) windowManager.removeView(bubbleView); if (isExpanded) windowManager.removeView(drawerView) } catch (e: Exception) {}
        if (isBound) { try { ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection); isBound = false } catch (e: Exception) {} }
    }
    
    private fun safeToast(msg: String) { 
        uiHandler.post { 
            try { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() } catch(e: Exception) { }
            if (debugStatusView != null) debugStatusView?.text = msg 
        }
    }
    
    private fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= 31) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(50)
            }
        } catch (e: Exception) {}
    }

    private fun setupDisplayContext(displayId: Int) {
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val display = displayManager.getDisplay(displayId)
        if (display == null) { windowManager = getSystemService(WINDOW_SERVICE) as WindowManager; return }
        currentDisplayId = displayId; displayContext = createDisplayContext(display); windowManager = displayContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private fun refreshDisplayId() { val id = displayContext?.display?.displayId ?: Display.DEFAULT_DISPLAY; currentDisplayId = id }
    private fun startForegroundService() { val channelId = if (android.os.Build.VERSION.SDK_INT >= 26) { val channel = android.app.NotificationChannel(CHANNEL_ID, "Floating Launcher", android.app.NotificationManager.IMPORTANCE_LOW); getSystemService(android.app.NotificationManager::class.java).createNotificationChannel(channel); CHANNEL_ID } else ""; val notification = NotificationCompat.Builder(this, channelId).setContentTitle("CoverScreen Launcher Active").setSmallIcon(R.drawable.ic_launcher_bubble).setPriority(NotificationCompat.PRIORITY_MIN).build(); if (android.os.Build.VERSION.SDK_INT >= 34) startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(1, notification) }
    private fun bindShizuku() { try { val component = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(component, userServiceConnection, true, 1) } catch (e: Exception) { Log.e(TAG, "Bind Shizuku Failed", e) } }
    private fun updateExecuteButtonColor(isReady: Boolean) { uiHandler.post { val executeBtn = drawerView?.findViewById<ImageView>(R.id.icon_execute); if (isReady) executeBtn?.setColorFilter(Color.GREEN) else executeBtn?.setColorFilter(Color.RED) } }

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
                if (velocityTracker == null) velocityTracker = VelocityTracker.obtain(); velocityTracker?.addMovement(event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { initialX = bubbleParams.x; initialY = bubbleParams.y; initialTouchX = event.rawX; initialTouchY = event.rawY; isDrag = false; return true }
                    MotionEvent.ACTION_MOVE -> { if (Math.abs(event.rawX - initialTouchX) > 10 || Math.abs(event.rawY - initialTouchY) > 10) isDrag = true; if (isDrag) { bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt(); bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt(); windowManager.updateViewLayout(bubbleView, bubbleParams) }; return true }
                    MotionEvent.ACTION_UP -> { velocityTracker?.computeCurrentVelocity(1000); val vX = velocityTracker?.xVelocity ?: 0f; val vY = velocityTracker?.yVelocity ?: 0f; val totalVel = hypot(vX.toDouble(), vY.toDouble()); if (isDrag && totalVel > 2500) { safeToast("Closing..."); stopSelf(); return true }; if (!isDrag) { if (!isBound && showShizukuWarning) { if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) { bindShizuku() } else { safeToast("Shizuku NOT Connected. Opening Shizuku..."); launchShizuku() } } else { toggleDrawer() } }; velocityTracker?.recycle(); velocityTracker = null; return true }
                    MotionEvent.ACTION_CANCEL -> { velocityTracker?.recycle(); velocityTracker = null }
                }
                return false
            }
        })
        windowManager.addView(bubbleView, bubbleParams)
    }
    
    private fun launchShizuku() { try { val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api"); if (intent != null) { intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(intent) } else { safeToast("Shizuku app not found") } } catch(e: Exception) { safeToast("Failed to launch Shizuku") } }
    private fun updateBubbleIcon() { val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon) ?: return; if (!isBound && showShizukuWarning) { uiHandler.post { iconView.setImageResource(android.R.drawable.ic_dialog_alert); iconView.setColorFilter(Color.RED); iconView.imageTintList = null }; return }; uiHandler.post { try { val uriStr = AppPreferences.getIconUri(this); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); if (bitmap != null) { iconView.setImageBitmap(bitmap); iconView.imageTintList = null; iconView.clearColorFilter() } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } catch (e: Exception) { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } }
    private fun dismissKeyboardAndRestore() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); if (searchBar != null && searchBar.hasFocus()) { searchBar.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(searchBar.windowToken, 0) }; val dpiInput = drawerView?.findViewById<EditText>(R.id.input_dpi_value); if (dpiInput != null && dpiInput.hasFocus()) { dpiInput.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(dpiInput.windowToken, 0) }; updateDrawerHeight(false) }

    private fun setupDrawer() {
        val context = displayContext ?: this
        val themeContext = ContextThemeWrapper(context, R.style.Theme_QuadrantLauncher)
        drawerView = LayoutInflater.from(themeContext).inflate(R.layout.layout_rofi_drawer, null)
        drawerView!!.fitsSystemWindows = true 
        drawerParams = WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT)
        drawerParams.gravity = Gravity.TOP or Gravity.START; drawerParams.x = 0; drawerParams.y = 0
        drawerParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container)
        if (container != null) { 
            val lp = container.layoutParams as? FrameLayout.LayoutParams
            if (lp != null) { lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; lp.topMargin = 100; container.layoutParams = lp }
            
            debugStatusView = TextView(context)
            debugStatusView?.text = "Ready"
            debugStatusView?.setTextColor(Color.GREEN)
            debugStatusView?.textSize = 10f
            debugStatusView?.gravity = Gravity.CENTER
            container.addView(debugStatusView, 0)
        }

        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val mainRecycler = drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view); val selectedRecycler = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
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
        searchBar.setOnKeyListener { _, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) { if (searchBar.text.isEmpty() && selectedAppsQueue.isNotEmpty()) { val lastIndex = selectedAppsQueue.size - 1; selectedAppsQueue.removeAt(lastIndex); updateSelectedAppsDock(); mainRecycler.adapter?.notifyDataSetChanged(); return@setOnKeyListener true } }; if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { if (searchBar.hasFocus()) { dismissKeyboardAndRestore(); return@setOnKeyListener true } }; return@setOnKeyListener false }
        searchBar.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) { updateDrawerHeight(hasFocus) } }
        mainRecycler.layoutManager = LinearLayoutManager(themeContext); mainRecycler.adapter = RofiAdapter(); val itemTouchHelper = ItemTouchHelper(swipeCallback); itemTouchHelper.attachToRecyclerView(mainRecycler)
        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() { override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) { if (newState == RecyclerView.SCROLL_STATE_DRAGGING) { dismissKeyboardAndRestore() } } })
        mainRecycler.setOnTouchListener { v, event -> if (event.action == MotionEvent.ACTION_DOWN) { dismissKeyboardAndRestore() }; false }
        selectedRecycler.layoutManager = LinearLayoutManager(themeContext, LinearLayoutManager.HORIZONTAL, false); selectedRecycler.adapter = SelectedAppsAdapter(); val dockTouchHelper = ItemTouchHelper(selectedAppsDragCallback); dockTouchHelper.attachToRecyclerView(selectedRecycler)
        drawerView!!.setOnClickListener { toggleDrawer() }
        drawerView!!.isFocusableInTouchMode = true
        drawerView!!.setOnKeyListener { _, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { toggleDrawer(); true } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && isScreenOffState) { wakeUp(); true } else false }
    }
    
    private fun startReorderMode(index: Int) { if (!isReorderTapEnabled) return; if (index < 0 || index >= selectedAppsQueue.size) return; val prevIndex = reorderSelectionIndex; reorderSelectionIndex = index; val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; if (prevIndex != -1) adapter?.notifyItemChanged(prevIndex); adapter?.notifyItemChanged(reorderSelectionIndex); safeToast("Tap another app to Swap") }
    private fun swapReorderItem(targetIndex: Int) { if (reorderSelectionIndex == -1) return; Collections.swap(selectedAppsQueue, reorderSelectionIndex, targetIndex); val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; adapter?.notifyItemChanged(reorderSelectionIndex); adapter?.notifyItemChanged(targetIndex); endReorderMode(true) }
    private fun endReorderMode(triggerInstantMode: Boolean) { val prevIndex = reorderSelectionIndex; reorderSelectionIndex = -1; val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; if (prevIndex != -1) adapter?.notifyItemChanged(prevIndex); if (triggerInstantMode && isInstantMode) applyLayoutImmediate() }
    
    private fun updateDrawerHeight(isKeyboardMode: Boolean) {
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container) ?: return
        val dm = DisplayMetrics(); windowManager.defaultDisplay.getRealMetrics(dm); val screenH = dm.heightPixels; val screenW = dm.widthPixels
        val lp = container.layoutParams as? FrameLayout.LayoutParams; val topMargin = lp?.topMargin ?: 100
        var finalHeight = (screenH * (currentDrawerHeightPercent / 100f)).toInt()
        if (isKeyboardMode) { finalHeight = (screenH * 0.40f).toInt(); val maxAvailable = screenH - topMargin - 20; if (finalHeight > maxAvailable) finalHeight = maxAvailable }
        val newW = (screenW * (currentDrawerWidthPercent / 100f)).toInt()
        if (container.layoutParams.height != finalHeight || container.layoutParams.width != newW) { container.layoutParams.width = newW; container.layoutParams.height = finalHeight; container.requestLayout(); if (drawerParams.y != 0) { drawerParams.y = 0; windowManager.updateViewLayout(drawerView, drawerParams) } }
    }

    private fun toggleDrawer() {
        if (isExpanded) { try { windowManager.removeView(drawerView) } catch(e: Exception) {}; bubbleView?.visibility = View.VISIBLE; isExpanded = false } else { setupDisplayContext(currentDisplayId); updateDrawerHeight(false); try { windowManager.addView(drawerView, drawerParams) } catch(e: Exception) {}; bubbleView?.visibility = View.GONE; isExpanded = true; switchMode(MODE_SEARCH); val et = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); et?.setText(""); et?.clearFocus(); updateSelectedAppsDock(); if (isInstantMode) fetchRunningApps() }
    }
    private fun updateGlobalFontSize() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); searchBar?.textSize = currentFontSize; drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() }
    private fun loadInstalledApps() { 
        val pm = packageManager; 
        val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }; 
        val riList = pm.queryIntentActivities(intent, 0); 
        allAppsList.clear(); 
        allAppsList.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)); 
        for (ri in riList) { 
            val pkg = ri.activityInfo.packageName; 
            if (pkg == PACKAGE_TRACKPAD || pkg == packageName) continue; 
            val app = MainActivity.AppInfo(ri.loadLabel(pm).toString(), pkg, AppPreferences.isFavorite(this, pkg)); 
            allAppsList.add(app) 
        }; 
        allAppsList.sortBy { it.label.lowercase() } 
    }
    
    private fun launchTrackpad() {
        if (isTrackpadRunning()) { safeToast("Trackpad is already active"); return }
        try { val intent = packageManager.getLaunchIntentForPackage(PACKAGE_TRACKPAD); if (intent != null) { intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP); val dm = DisplayMetrics(); val display = displayContext?.display ?: windowManager.defaultDisplay; display.getRealMetrics(dm); val w = dm.widthPixels; val h = dm.heightPixels; val targetW = (w * 0.5f).toInt(); val targetH = (h * 0.5f).toInt(); val left = (w - targetW) / 2; val top = (h - targetH) / 2; val bounds = Rect(left, top, left + targetW, top + targetH); val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(bounds); try { val method = android.app.ActivityOptions::class.java.getMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType); method.invoke(options, 5) } catch (e: Exception) {}; startActivity(intent, options.toBundle()); toggleDrawer(); if (shellService != null) { uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(PACKAGE_TRACKPAD, left, top, left+targetW, top+targetH) } catch(e: Exception) { Log.e(TAG, "Shell launch failed", e) } }.start() }, 400) } } else { safeToast("Trackpad App not found") } } catch (e: Exception) { safeToast("Error launching Trackpad") }
    }

    private fun isTrackpadRunning(): Boolean { try { val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager; val runningApps = am.runningAppProcesses; if (runningApps != null) { for (info in runningApps) { if (info.processName == PACKAGE_TRACKPAD) return true } } } catch (e: Exception) {}; return false }
    private fun getLayoutName(type: Int): String { return when(type) { LAYOUT_FULL -> "1 App - Full"; LAYOUT_SIDE_BY_SIDE -> "Split"; LAYOUT_TOP_BOTTOM -> "Top/Bot"; LAYOUT_TRI_EVEN -> "Tri-Split"; LAYOUT_CORNERS -> "Quadrant"; LAYOUT_TRI_SIDE_MAIN_SIDE -> "3 Apps - Side/Main/Side"; LAYOUT_QUAD_ROW_EVEN -> "4 Apps - Row"; LAYOUT_CUSTOM_DYNAMIC -> "Custom"; else -> "Unknown" } }
    private fun getRatioName(index: Int): String { return when(index) { 1 -> "1:1"; 2 -> "16:9"; 3 -> "32:9"; else -> "Default" } }
    private fun getTargetDimensions(index: Int): Pair<Int, Int>? { return when(index) { 1 -> 1422 to 1500; 2 -> 1920 to 1080; 3 -> 3840 to 1080; else -> null } }
    private fun getResolutionCommand(index: Int): String { return when(index) { 1 -> "wm size 1422x1500 -d $currentDisplayId"; 2 -> "wm size 1920x1080 -d $currentDisplayId"; 3 -> "wm size 3840x1080 -d $currentDisplayId"; else -> "wm size reset -d $currentDisplayId" } }
    private fun sortAppQueue() { selectedAppsQueue.sortWith(compareBy { it.isMinimized }) }
    private fun updateSelectedAppsDock() { val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); if (selectedAppsQueue.isEmpty()) { dock.visibility = View.GONE } else { dock.visibility = View.VISIBLE; dock.adapter?.notifyDataSetChanged(); dock.scrollToPosition(selectedAppsQueue.size - 1) } }
    private fun refreshSearchList() { val query = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString() ?: ""; filterList(query) }
    private fun filterList(query: String) {
        if (currentMode != MODE_SEARCH) return; val actualQuery = query.substringAfterLast(",").trim(); displayList.clear()
        val filtered = if (actualQuery.isEmpty()) { allAppsList } else { allAppsList.filter { it.label.contains(actualQuery, ignoreCase = true) } }
        val sorted = filtered.sortedWith(compareBy<MainActivity.AppInfo> { it.packageName != PACKAGE_BLANK }.thenByDescending { it.isFavorite }.thenBy { it.label.lowercase() }); displayList.addAll(sorted); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    private fun addToSelection(app: MainActivity.AppInfo) {
        dismissKeyboardAndRestore(); val et = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar)
        if (app.packageName == PACKAGE_BLANK) { selectedAppsQueue.add(app); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate(); return }
        val existing = selectedAppsQueue.find { it.packageName == app.packageName }; if (existing != null) { selectedAppsQueue.remove(existing); Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start(); safeToast("Removed ${app.label}"); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); et.setText(""); if (isInstantMode) applyLayoutImmediate() } 
        else { app.isMinimized = false; selectedAppsQueue.add(app); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); et.setText(""); if (isInstantMode) { launchViaApi(app.packageName, null); launchViaShell(app.packageName); uiHandler.postDelayed({ applyLayoutImmediate() }, 200); uiHandler.postDelayed({ applyLayoutImmediate() }, 800) } }
    }
    private fun toggleFavorite(app: MainActivity.AppInfo) { val newState = AppPreferences.toggleFavorite(this, app.packageName); app.isFavorite = newState; allAppsList.find { it.packageName == app.packageName }?.isFavorite = newState }
    private fun launchViaApi(pkg: String, bounds: Rect?) { try { val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return; intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP); val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); if (bounds != null) options.setLaunchBounds(bounds); startActivity(intent, options.toBundle()) } catch (e: Exception) {} }
    private fun launchViaShell(pkg: String) { try { val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return; if (shellService != null) { val component = intent.component?.flattenToShortString() ?: pkg; val cmd = "am start -n $component -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"; Thread { shellService?.runCommand(cmd) }.start() } } catch (e: Exception) {} }
    
    private fun cycleDisplay() {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val displays = dm.displays
        if (isVirtualDisplayActive) { val virtualDisp = displays.firstOrNull { it.displayId >= 2 }; if (virtualDisp != null) { val targetId = if (currentDisplayId == virtualDisp.displayId) { if (displays.any { it.displayId == lastPhysicalDisplayId }) lastPhysicalDisplayId else Display.DEFAULT_DISPLAY } else { lastPhysicalDisplayId = currentDisplayId; virtualDisp.displayId }; performDisplayChange(targetId); return } }
        val currentIdx = displays.indexOfFirst { it.displayId == currentDisplayId }; val nextIdx = if (currentIdx == -1) 0 else (currentIdx + 1) % displays.size; performDisplayChange(displays[nextIdx].displayId)
    }
    private fun performDisplayChange(newId: Int) {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val targetDisplay = dm.getDisplay(newId) ?: return; try { if (bubbleView != null && bubbleView!!.isAttachedToWindow) windowManager.removeView(bubbleView); if (drawerView != null && drawerView!!.isAttachedToWindow) windowManager.removeView(drawerView) } catch (e: Exception) {}; currentDisplayId = newId; setupDisplayContext(currentDisplayId); targetDisplayIndex = currentDisplayId; AppPreferences.setTargetDisplayIndex(this, targetDisplayIndex); setupBubble(); setupDrawer(); loadDisplaySettings(currentDisplayId); updateBubbleIcon(); isExpanded = false; safeToast("Switched to Display $currentDisplayId (${targetDisplay.name})")
    }
    private fun toggleVirtualDisplay(enable: Boolean) { isVirtualDisplayActive = enable; Thread { try { if (enable) { shellService?.runCommand("settings put global overlay_display_devices \"1920x1080/320\""); uiHandler.post { safeToast("Creating Virtual Display... Wait a moment, then Switch Display.") } } else { shellService?.runCommand("settings delete global overlay_display_devices"); uiHandler.post { safeToast("Destroying Virtual Display...") } } } catch (e: Exception) { Log.e(TAG, "Virtual Display Toggle Failed", e) } }.start(); if (currentMode == MODE_SETTINGS) uiHandler.postDelayed({ switchMode(MODE_SETTINGS) }, 500) }

    // --- v2.0 SCREEN OFF LOGIC ---
    private fun performScreenOff() {
        vibrate()
        isScreenOffState = true
        safeToast("Screen Off: Double press Power Button to turn on")
        
        if (useAltScreenOff) {
             // New Methodology: Fast Binder Call via Shizuku (v2.0)
             Thread {
                 try {
                     if (shellService != null) {
                         // -1 triggers the specific Extinguish logic in ShellUserService (v2.0: Single Arg)
                         shellService?.setBrightness(-1)
                         uiHandler.post { safeToast("Pixels OFF (Alternate Mode)") }
                     } else {
                         safeToast("Service Disconnected!")
                     }
                 } catch (e: Exception) {
                     Log.e(TAG, "Binder Call Failed", e)
                     safeToast("Error: ${e.message}")
                 }
            }.start()
        } else {
            // Default: SurfaceControl Power Off
            Thread { try { shellService?.setScreenOff(0, true) } catch (e: Exception) {} }.start()
            safeToast("Screen OFF (SurfaceControl)")
        }
    }
    
    private fun wakeUp() {
        vibrate()
        isScreenOffState = false
        
        // Restore brightness and power
        Thread { try { shellService?.setBrightness(128) } catch (e: Exception) {} }.start()
        Thread { try { shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start()

        safeToast("Screen On")
        if (currentMode == MODE_SETTINGS) drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }

    private fun applyLayoutImmediate() { executeLaunch(selectedLayoutType, closeDrawer = false) }
    private fun fetchRunningApps() { if (shellService == null) return; Thread { try { val visiblePackages = shellService!!.getVisiblePackages(currentDisplayId); val allRunning = shellService!!.getAllRunningPackages(); val lastQueue = AppPreferences.getLastQueue(this); uiHandler.post { selectedAppsQueue.clear(); for (pkg in lastQueue) { if (pkg == PACKAGE_BLANK) { selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)) } else if (allRunning.contains(pkg)) { val appInfo = allAppsList.find { it.packageName == pkg }; if (appInfo != null) { appInfo.isMinimized = !visiblePackages.contains(pkg); selectedAppsQueue.add(appInfo) } } }; for (pkg in visiblePackages) { if (!lastQueue.contains(pkg)) { val appInfo = allAppsList.find { it.packageName == pkg }; if (appInfo != null) { appInfo.isMinimized = false; selectedAppsQueue.add(appInfo) } } }; sortAppQueue(); updateSelectedAppsDock(); drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); safeToast("Instant Mode: Active") } } catch (e: Exception) { Log.e(TAG, "Error fetching apps", e) } }.start() }
    private fun selectLayout(opt: LayoutOption) { dismissKeyboardAndRestore(); selectedLayoutType = opt.type; activeCustomRects = opt.customRects; if (opt.type == LAYOUT_CUSTOM_DYNAMIC) { activeCustomLayoutName = opt.name; AppPreferences.saveLastCustomLayoutName(this, opt.name) } else { activeCustomLayoutName = null; AppPreferences.saveLastCustomLayoutName(this, null) }; AppPreferences.saveLastLayout(this, opt.type); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() }
    private fun saveCurrentAsCustom() { Thread { try { val rawLayouts = shellService!!.getWindowLayouts(currentDisplayId); if (rawLayouts.isEmpty()) { safeToast("Found 0 active app windows"); return@Thread }; val rectStrings = mutableListOf<String>(); for (line in rawLayouts) { val parts = line.split("|"); if (parts.size == 2) { rectStrings.add(parts[1]) } }; if (rectStrings.isEmpty()) { safeToast("Found 0 valid frames"); return@Thread }; val count = rectStrings.size; var baseName = "$count Apps - Custom"; val existingNames = AppPreferences.getCustomLayoutNames(this); var counter = 1; var finalName = "$baseName $counter"; while (existingNames.contains(finalName)) { counter++; finalName = "$baseName $counter" }; AppPreferences.saveCustomLayout(this, finalName, rectStrings.joinToString("|")); safeToast("Saved: $finalName"); uiHandler.post { switchMode(MODE_LAYOUTS) } } catch (e: Exception) { Log.e(TAG, "Failed to save custom layout", e); safeToast("Error saving: ${e.message}") } }.start() }
    private fun applyResolution(opt: ResolutionOption) { dismissKeyboardAndRestore(); if (opt.index != -1) { selectedResolutionIndex = opt.index; AppPreferences.saveDisplayResolution(this, currentDisplayId, opt.index) }; drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode && opt.index != -1) { Thread { val resCmd = getResolutionCommand(selectedResolutionIndex); shellService?.runCommand(resCmd); Thread.sleep(1500); uiHandler.post { applyLayoutImmediate() } }.start() } }
    private fun selectDpi(value: Int) { currentDpiSetting = if (value == -1) -1 else value.coerceIn(50, 600); AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); Thread { try { if (currentDpiSetting == -1) { shellService?.runCommand("wm density reset -d $currentDisplayId") } else { val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; shellService?.runCommand(dpiCmd) } } catch(e: Exception) { e.printStackTrace() } }.start() }
    private fun changeFontSize(newSize: Float) { currentFontSize = newSize.coerceIn(10f, 30f); AppPreferences.saveFontSize(this, currentFontSize); updateGlobalFontSize(); if (currentMode == MODE_SETTINGS) { switchMode(MODE_SETTINGS) } }
    private fun changeDrawerHeight(delta: Int) { currentDrawerHeightPercent = (currentDrawerHeightPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerHeightPercent(this, currentDrawerHeightPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun changeDrawerWidth(delta: Int) { currentDrawerWidthPercent = (currentDrawerWidthPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerWidthPercent(this, currentDrawerWidthPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun pickIcon() { toggleDrawer(); try { refreshDisplayId(); val intent = Intent(this, IconPickerActivity::class.java); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); val metrics = windowManager.maximumWindowMetrics; val w = 1000; val h = (metrics.bounds.height() * 0.7).toInt(); val x = (metrics.bounds.width() - w) / 2; val y = (metrics.bounds.height() - h) / 2; val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(Rect(x, y, x+w, y+h)); startActivity(intent, options.toBundle()) } catch (e: Exception) { safeToast("Error launching picker: ${e.message}") } }
    private fun saveProfile() { var name = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString()?.trim(); if (name.isNullOrEmpty()) { val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()); name = "Profile_$timestamp" }; val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveProfile(this, name, selectedLayoutType, selectedResolutionIndex, currentDpiSetting, pkgs); safeToast("Saved: $name"); drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText(""); switchMode(MODE_PROFILES) }
    private fun loadProfile(name: String) { val data = AppPreferences.getProfileData(this, name) ?: return; try { val parts = data.split("|"); selectedLayoutType = parts[0].toInt(); selectedResolutionIndex = parts[1].toInt(); currentDpiSetting = parts[2].toInt(); val pkgList = parts[3].split(","); selectedAppsQueue.clear(); for (pkg in pkgList) { if (pkg.isNotEmpty()) { if (pkg == PACKAGE_BLANK) { selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)) } else { val app = allAppsList.find { it.packageName == pkg }; if (app != null) selectedAppsQueue.add(app) } } }; AppPreferences.saveLastLayout(this, selectedLayoutType); AppPreferences.saveDisplayResolution(this, currentDisplayId, selectedResolutionIndex); AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); activeProfileName = name; updateSelectedAppsDock(); safeToast("Loaded: $name"); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() } catch (e: Exception) { Log.e(TAG, "Failed to load profile", e) } }
    
    private fun executeLaunch(layoutType: Int, closeDrawer: Boolean) { 
        if (closeDrawer) toggleDrawer(); refreshDisplayId(); val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveLastQueue(this, pkgs)
        Thread { try { val resCmd = getResolutionCommand(selectedResolutionIndex); shellService?.runCommand(resCmd); if (currentDpiSetting > 0) { val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; shellService?.runCommand(dpiCmd) } else { if (currentDpiSetting == -1) shellService?.runCommand("wm density reset -d $currentDisplayId") }; Thread.sleep(800); val targetDim = getTargetDimensions(selectedResolutionIndex); var w = 0; var h = 0; if (targetDim != null) { w = targetDim.first; h = targetDim.second } else { val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val display = dm.getDisplay(currentDisplayId); if (display != null) { val metrics = DisplayMetrics(); display.getRealMetrics(metrics); w = metrics.widthPixels; h = metrics.heightPixels } else { val bounds = windowManager.maximumWindowMetrics.bounds; w = bounds.width(); h = bounds.height() } }; val rects = mutableListOf<Rect>(); when (layoutType) { LAYOUT_FULL -> { rects.add(Rect(0, 0, w, h)) }; LAYOUT_SIDE_BY_SIDE -> { rects.add(Rect(0, 0, w/2, h)); rects.add(Rect(w/2, 0, w, h)) }; LAYOUT_TOP_BOTTOM -> { rects.add(Rect(0, 0, w, h/2)); rects.add(Rect(0, h/2, w, h)) }; LAYOUT_TRI_EVEN -> { val third = w / 3; rects.add(Rect(0, 0, third, h)); rects.add(Rect(third, 0, third * 2, h)); rects.add(Rect(third * 2, 0, w, h)) }; LAYOUT_CORNERS -> { rects.add(Rect(0, 0, w/2, h/2)); rects.add(Rect(w/2, 0, w, h/2)); rects.add(Rect(0, h/2, w/2, h)); rects.add(Rect(w/2, h/2, w, h)) }; LAYOUT_TRI_SIDE_MAIN_SIDE -> { val quarter = w / 4; rects.add(Rect(0, 0, quarter, h)); rects.add(Rect(quarter, 0, quarter * 3, h)); rects.add(Rect(quarter * 3, 0, w, h)) }; LAYOUT_QUAD_ROW_EVEN -> { val quarter = w / 4; rects.add(Rect(0, 0, quarter, h)); rects.add(Rect(quarter, 0, quarter * 2, h)); rects.add(Rect(quarter * 2, 0, quarter * 3, h)); rects.add(Rect(quarter * 3, 0, w, h)) }; LAYOUT_CUSTOM_DYNAMIC -> { if (activeCustomRects != null) { rects.addAll(activeCustomRects!!) } else { rects.add(Rect(0, 0, w/2, h)); rects.add(Rect(w/2, 0, w, h)) } } }; if (selectedAppsQueue.isNotEmpty()) { val minimizedApps = selectedAppsQueue.filter { it.isMinimized }; for (app in minimizedApps) { if (app.packageName != PACKAGE_BLANK) { try { val tid = shellService?.getTaskId(app.packageName) ?: -1; if (tid != -1) shellService?.moveTaskToBack(tid) } catch (e: Exception) { Log.e(TAG, "Failed to minimize ${app.packageName}", e) } } }; val activeApps = selectedAppsQueue.filter { !it.isMinimized }; if (killAppOnExecute) { for (app in activeApps) { if (app.packageName != PACKAGE_BLANK) { shellService?.forceStop(app.packageName) } }; Thread.sleep(400) } else { Thread.sleep(100) }; val count = Math.min(activeApps.size, rects.size); for (i in 0 until count) { val pkg = activeApps[i].packageName; val bounds = rects[i]; if (pkg == PACKAGE_BLANK) continue; uiHandler.postDelayed({ launchViaApi(pkg, bounds) }, (i * 150).toLong()); uiHandler.postDelayed({ launchViaShell(pkg) }, (i * 150 + 50).toLong()); if (!killAppOnExecute) { uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {} }.start() }, (i * 150 + 150).toLong()) }; uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {} }.start() }, (i * 150 + 800).toLong()) }; if (closeDrawer) { uiHandler.post { selectedAppsQueue.clear(); updateSelectedAppsDock() } } } } catch (e: Exception) { Log.e(TAG, "Execute Failed", e); safeToast("Execute Failed: ${e.message}") } }.start(); drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText("") 
    }
    
    private fun calculateGCD(a: Int, b: Int): Int { return if (b == 0) a else calculateGCD(b, a % b) }

    private fun switchMode(mode: Int) {
        currentMode = mode
        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val searchIcon = drawerView!!.findViewById<ImageView>(R.id.icon_search_mode); val iconWin = drawerView!!.findViewById<ImageView>(R.id.icon_mode_window); val iconRes = drawerView!!.findViewById<ImageView>(R.id.icon_mode_resolution); val iconDpi = drawerView!!.findViewById<ImageView>(R.id.icon_mode_dpi); val iconProf = drawerView!!.findViewById<ImageView>(R.id.icon_mode_profiles); val iconSet = drawerView!!.findViewById<ImageView>(R.id.icon_mode_settings); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        searchIcon.setColorFilter(if(mode==MODE_SEARCH) Color.WHITE else Color.GRAY); iconWin.setColorFilter(if(mode==MODE_LAYOUTS) Color.WHITE else Color.GRAY); iconRes.setColorFilter(if(mode==MODE_RESOLUTION) Color.WHITE else Color.GRAY); iconDpi.setColorFilter(if(mode==MODE_DPI) Color.WHITE else Color.GRAY); iconProf.setColorFilter(if(mode==MODE_PROFILES) Color.WHITE else Color.GRAY); iconSet.setColorFilter(if(mode==MODE_SETTINGS) Color.WHITE else Color.GRAY)
        executeBtn.visibility = if (isInstantMode) View.GONE else View.VISIBLE; displayList.clear(); val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); dock.visibility = if (mode == MODE_SEARCH && selectedAppsQueue.isNotEmpty()) View.VISIBLE else View.GONE
        
        when (mode) {
            MODE_SEARCH -> { searchBar.hint = "Search apps..."; refreshSearchList() }
            MODE_LAYOUTS -> { 
                searchBar.hint = "Select Layout"; displayList.add(ActionOption("Save Current Arrangement") { saveCurrentAsCustom() }); displayList.add(LayoutOption("1 App - Full Screen", LAYOUT_FULL)); displayList.add(LayoutOption("2 Apps - Side by Side", LAYOUT_SIDE_BY_SIDE)); displayList.add(LayoutOption("2 Apps - Top & Bottom", LAYOUT_TOP_BOTTOM)); displayList.add(LayoutOption("3 Apps - Even", LAYOUT_TRI_EVEN)); displayList.add(LayoutOption("3 Apps - Side/Main/Side (25/50/25)", LAYOUT_TRI_SIDE_MAIN_SIDE)); displayList.add(LayoutOption("4 Apps - Corners", LAYOUT_CORNERS)); displayList.add(LayoutOption("4 Apps - Row (Even)", LAYOUT_QUAD_ROW_EVEN));
                val customNames = AppPreferences.getCustomLayoutNames(this).sorted(); for (name in customNames) { val data = AppPreferences.getCustomLayoutData(this, name); if (data != null) { try { val rects = mutableListOf<Rect>(); val rectParts = data.split("|"); for (rp in rectParts) { val coords = rp.split(","); if (coords.size == 4) { rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt())) } }; displayList.add(LayoutOption(name, LAYOUT_CUSTOM_DYNAMIC, true, rects)) } catch(e: Exception) {} } } 
            }
            MODE_RESOLUTION -> {
                searchBar.hint = "Select Resolution"; displayList.add(CustomResInputOption); val savedResNames = AppPreferences.getCustomResolutionNames(this).sorted(); for (name in savedResNames) { val value = AppPreferences.getCustomResolutionValue(this, name) ?: continue; displayList.add(ResolutionOption(name, "wm size  -d $currentDisplayId", 100 + savedResNames.indexOf(name))) }; displayList.add(ResolutionOption("Default (Reset)", "wm size reset -d $currentDisplayId", 0)); displayList.add(ResolutionOption("1:1 Square (1422x1500)", "wm size 1422x1500 -d $currentDisplayId", 1)); displayList.add(ResolutionOption("16:9 Landscape (1920x1080)", "wm size 1920x1080 -d $currentDisplayId", 2)); displayList.add(ResolutionOption("32:9 Ultrawide (3840x1080)", "wm size 3840x1080 -d $currentDisplayId", 3))
            }
            MODE_DPI -> { searchBar.hint = "Adjust Density (DPI)"; displayList.add(ActionOption("Reset Density (Default)") { selectDpi(-1) }); var savedDpi = currentDpiSetting; if (savedDpi <= 0) { savedDpi = displayContext?.resources?.configuration?.densityDpi ?: 160 }; displayList.add(DpiOption(savedDpi)) }
            MODE_PROFILES -> { searchBar.hint = "Enter Profile Name..."; displayList.add(ProfileOption("Save Current as New", true, 0,0,0, emptyList())); val profileNames = AppPreferences.getProfileNames(this).sorted(); for (pName in profileNames) { val data = AppPreferences.getProfileData(this, pName); if (data != null) { try { val parts = data.split("|"); val lay = parts[0].toInt(); val res = parts[1].toInt(); val d = parts[2].toInt(); val pkgs = parts[3].split(",").filter { it.isNotEmpty() }; displayList.add(ProfileOption(pName, false, lay, res, d, pkgs)) } catch(e: Exception) {} } } }
            MODE_SETTINGS -> {
                searchBar.hint = "Settings"
                displayList.add(ActionOption("Launch DroidOS Trackpad") { launchTrackpad() }) 
                displayList.add(ActionOption("Switch Display (Current $currentDisplayId)") { cycleDisplay() })
                displayList.add(ToggleOption("Virtual Display (1080p)", isVirtualDisplayActive) { toggleVirtualDisplay(it) })
                displayList.add(HeightOption(currentDrawerHeightPercent))
                displayList.add(WidthOption(currentDrawerWidthPercent))
                displayList.add(ToggleOption("Auto-Shrink for Keyboard", autoResizeEnabled) { autoResizeEnabled = it; AppPreferences.setAutoResizeKeyboard(this, it) })
                displayList.add(FontSizeOption(currentFontSize))
                displayList.add(IconOption("Launcher Icon (Tap to Change)"))
                displayList.add(ToggleOption("Reorder: Drag & Drop", isReorderDragEnabled) { isReorderDragEnabled = it; AppPreferences.setReorderDrag(this, it) })
                displayList.add(ToggleOption("Reorder: Tap to Swap (Long Press)", isReorderTapEnabled) { isReorderTapEnabled = it; AppPreferences.setReorderTap(this, it) })
                displayList.add(ToggleOption("Instant Mode (Live Changes)", isInstantMode) { isInstantMode = it; AppPreferences.setInstantMode(this, it); executeBtn.visibility = if (it) View.GONE else View.VISIBLE; if (it) fetchRunningApps() })
                displayList.add(ToggleOption("Kill App on Execute", killAppOnExecute) { killAppOnExecute = it; AppPreferences.setKillOnExecute(this, it) })
                
                // --- V2.0 MENU ITEMS RESTORED ---
                
                // STANDARD MODE TOGGLE
                displayList.add(ToggleOption("Screen Off (Standard)", isScreenOffState && !useAltScreenOff) { 
                    if (it) {
                        if (isScreenOffState) wakeUp() // Reset if already off
                        useAltScreenOff = false
                        AppPreferences.setUseAltScreenOff(this, false)
                        performScreenOff()
                    } else {
                        wakeUp()
                    }
                })

                // ALTERNATE MODE TOGGLE
                displayList.add(ToggleOption("Screen Off (Alternate)", isScreenOffState && useAltScreenOff) { 
                    if (it) {
                        if (isScreenOffState) wakeUp() // Reset if already off
                        useAltScreenOff = true
                        AppPreferences.setUseAltScreenOff(this, true)
                        performScreenOff()
                    } else {
                        wakeUp()
                    }
                })
                
                displayList.add(ToggleOption("Shizuku Warning (Icon Alert)", showShizukuWarning) { showShizukuWarning = it; AppPreferences.setShowShizukuWarning(this, it); updateBubbleIcon() })
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
    data class TimeoutOption(val seconds: Int)

    inner class SelectedAppsAdapter : RecyclerView.Adapter<SelectedAppsAdapter.Holder>() {
        inner class Holder(v: View) : RecyclerView.ViewHolder(v) { val icon: ImageView = v.findViewById(R.id.selected_app_icon) }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder { return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_app, parent, false)) }
        override fun onBindViewHolder(holder: Holder, position: Int) { 
            val app = selectedAppsQueue[position]; if (position == reorderSelectionIndex) { holder.icon.setColorFilter(HIGHLIGHT_COLOR); holder.icon.alpha = 1.0f; holder.itemView.scaleX = 1.1f; holder.itemView.scaleY = 1.1f; holder.itemView.background = null } else { holder.icon.clearColorFilter(); holder.itemView.scaleX = 1.0f; holder.itemView.scaleY = 1.0f; holder.itemView.background = null; if (app.packageName == PACKAGE_BLANK) { holder.icon.setImageResource(R.drawable.ic_box_outline); holder.icon.alpha = 1.0f } else { try { holder.icon.setImageDrawable(packageManager.getApplicationIcon(app.packageName)) } catch (e: Exception) { holder.icon.setImageResource(R.drawable.ic_launcher_bubble) }; holder.icon.alpha = if (app.isMinimized) 0.4f else 1.0f } }
            holder.itemView.setOnClickListener { try { dismissKeyboardAndRestore(); if (reorderSelectionIndex != -1) { if (position == reorderSelectionIndex) { endReorderMode(false) } else { swapReorderItem(position) } } else { if (app.packageName != PACKAGE_BLANK) { app.isMinimized = !app.isMinimized; notifyItemChanged(position); if (isInstantMode) applyLayoutImmediate() } } } catch(e: Exception) {} }
            holder.itemView.setOnLongClickListener { if (isReorderTapEnabled) { startReorderMode(position); true } else { false } }
        }
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
            else if (holder is ProfileRichHolder && item is ProfileOption) { holder.name.setText(item.name); holder.iconsContainer.removeAllViews(); if (!item.isCurrent) { for (pkg in item.apps.take(5)) { val iv = ImageView(holder.itemView.context); val lp = LinearLayout.LayoutParams(60, 60); lp.marginEnd = 8; iv.layoutParams = lp; if (pkg == PACKAGE_BLANK) { iv.setImageResource(R.drawable.ic_box_outline) } else { try { iv.setImageDrawable(packageManager.getApplicationIcon(pkg)) } catch (e: Exception) { iv.setImageResource(R.drawable.ic_launcher_bubble) } }; holder.iconsContainer.addView(iv) }; val info = "${getLayoutName(item.layout)} | ${getRatioName(item.resIndex)} | ${item.dpi}dpi"; holder.details.text = info; holder.details.visibility = View.VISIBLE; holder.btnSave.visibility = View.GONE; if (activeProfileName == item.name) { holder.itemView.setBackgroundResource(R.drawable.bg_item_active) } else { holder.itemView.setBackgroundResource(0) }; holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); loadProfile(item.name) }; holder.itemView.setOnLongClickListener { startRename(holder.name); true }; val saveProfileName = { val newName = holder.name.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameProfile(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); switchMode(MODE_PROFILES) } }; endRename(holder.name) }; holder.name.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveProfileName(); holder.name.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(holder.name.windowToken, 0); updateDrawerHeight(false); true } else false }; holder.name.setOnFocusChangeListener { v, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus); if (!hasFocus) saveProfileName() } } else { holder.iconsContainer.removeAllViews(); holder.details.visibility = View.GONE; holder.btnSave.visibility = View.VISIBLE; holder.itemView.setBackgroundResource(0); holder.name.isEnabled = true; holder.name.isFocusable = true; holder.name.isFocusableInTouchMode = true; holder.itemView.setOnClickListener { saveProfile() }; holder.btnSave.setOnClickListener { saveProfile() } } }
            else if (holder is LayoutHolder) {
                holder.btnSave.visibility = View.GONE; holder.btnExtinguish.visibility = View.GONE
                if (item is LayoutOption) { holder.nameInput.setText(item.name); val isSelected = if (item.type == LAYOUT_CUSTOM_DYNAMIC) { item.type == selectedLayoutType && item.name == activeCustomLayoutName } else { item.type == selectedLayoutType && activeCustomLayoutName == null }; if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { selectLayout(item) }; if (item.isCustomSaved) { holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }; val saveLayoutName = { val newName = holder.nameInput.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameCustomLayout(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); if (activeCustomLayoutName == item.name) { activeCustomLayoutName = newName; AppPreferences.saveLastCustomLayoutName(holder.itemView.context, newName) }; switchMode(MODE_LAYOUTS) } }; endRename(holder.nameInput) }; holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveLayoutName(); true } else false }; holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveLayoutName() } } else { holder.nameInput.isEnabled = false; holder.nameInput.isFocusable = false; holder.nameInput.setTextColor(Color.WHITE) } }
                else if (item is ResolutionOption) { 
                    holder.nameInput.setText(item.name); if (item.index >= 100) { holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }; val saveResName = { val newName = holder.nameInput.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameCustomResolution(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); switchMode(MODE_RESOLUTION) } }; endRename(holder.nameInput) }; holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveResName(); true } else false }; holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveResName() } } else { holder.nameInput.isEnabled = false; holder.nameInput.isFocusable = false; holder.nameInput.setTextColor(Color.WHITE) }; val isSelected = (item.index == selectedResolutionIndex); if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { applyResolution(item) } 
                }
                else if (item is IconOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { pickIcon() } }
                else if (item is ToggleOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); if (item.isEnabled) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.isEnabled = !item.isEnabled; item.onToggle(item.isEnabled); notifyItemChanged(position) } } 
                else if (item is ActionOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.action() } }
            }
            else if (holder is CustomResInputHolder) {
                holder.btnSave.setOnClickListener { val wStr = holder.inputW.text.toString().trim(); val hStr = holder.inputH.text.toString().trim(); if (wStr.isNotEmpty() && hStr.isNotEmpty()) { val w = wStr.toIntOrNull(); val h = hStr.toIntOrNull(); if (w != null && h != null && w > 0 && h > 0) { val gcdVal = calculateGCD(w, h); val wRatio = w / gcdVal; val hRatio = h / gcdVal; val resString = "${w}x${h}"; val name = "$wRatio:$hRatio Custom ($resString)"; AppPreferences.saveCustomResolution(holder.itemView.context, name, resString); safeToast("Added $name"); dismissKeyboardAndRestore(); switchMode(MODE_RESOLUTION) } else { safeToast("Invalid numbers") } } else { safeToast("Input W and H") } }
                holder.inputW.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }; holder.inputH.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }
            }
            else if (holder is IconSettingHolder && item is IconOption) { try { val uriStr = AppPreferences.getIconUri(holder.itemView.context); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); holder.preview.setImageBitmap(bitmap) } else { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) } } catch(e: Exception) { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) }; holder.itemView.setOnClickListener { pickIcon() } }
            else if (holder is DpiHolder && item is DpiOption) { 
                holder.input.setText(item.currentDpi.toString()); holder.input.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { val valInt = v.text.toString().toIntOrNull(); if (valInt != null) { selectDpi(valInt); safeToast("DPI set to $valInt") }; dismissKeyboardAndRestore(); true } else false }; holder.input.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus); if (!hasFocus) { val valInt = holder.input.text.toString().toIntOrNull(); if (valInt != null && valInt != item.currentDpi) { selectDpi(valInt) } } }; holder.btnMinus.setOnClickListener { val v = holder.input.text.toString().toIntOrNull() ?: 160; val newVal = (v - 5).coerceAtLeast(50); holder.input.setText(newVal.toString()); selectDpi(newVal) }; holder.btnPlus.setOnClickListener { val v = holder.input.text.toString().toIntOrNull() ?: 160; val newVal = (v + 5).coerceAtMost(600); holder.input.setText(newVal.toString()); selectDpi(newVal) } 
            }
            else if (holder is FontSizeHolder && item is FontSizeOption) { holder.textVal.text = item.currentSize.toInt().toString(); holder.btnMinus.setOnClickListener { changeFontSize(item.currentSize - 1) }; holder.btnPlus.setOnClickListener { changeFontSize(item.currentSize + 1) } }
            else if (holder is HeightHolder && item is HeightOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerHeight(-5) }; holder.btnPlus.setOnClickListener { changeDrawerHeight(5) } }
            else if (holder is WidthHolder && item is WidthOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerWidth(-5) }; holder.btnPlus.setOnClickListener { changeDrawerWidth(5) } }
        }
        override fun getItemCount() = displayList.size
    }
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShellUserService.kt.displayoff
```
package com.example.quadrantlauncher

import android.os.IBinder
import android.os.Binder
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    companion object {
        const val POWER_MODE_OFF = 0
        const val POWER_MODE_NORMAL = 2
        
        @Volatile private var displayControlClass: Class<*>? = null
        @Volatile private var displayControlClassLoaded = false
    }

    private val surfaceControlClass: Class<*> by lazy {
        Class.forName("android.view.SurfaceControl")
    }

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

    private fun setDisplayBrightnessInternal(displayId: Int, brightness: Float): Boolean {
        // Legacy shim for single-target calls
        val tokens = getAllPhysicalDisplayTokens()
        if (tokens.isNotEmpty()) return setDisplayBrightnessOnToken(tokens[0], brightness)
        return false
    }

    private val shLock = Object()
    private var _shProcess: Process? = null
    private val shProcess: Process
        get() = synchronized(shLock) {
            if (_shProcess?.isAlive == true) _shProcess!!
            else Runtime.getRuntime().exec(arrayOf("sh")).also { _shProcess = it }
        }

    private fun execShellCommand(command: String) {
        synchronized(shLock) {
            try {
                val output = shProcess.outputStream
                output.write("$command\n".toByteArray())
                output.flush()
            } catch (e: Exception) {
                Log.e(TAG, "Shell command failed", e)
            }
        }
    }

    // ============================================================
    // AIDL Interface Implementations
    // ============================================================

    override fun setBrightness(displayId: Int, brightness: Int) {
        Log.d(TAG, "setBrightness(Global Broadcast, Value: $brightness)")
        val token = Binder.clearCallingIdentity()
        try {
            if (brightness < 0) {
                // === SCREEN OFF ===
                execShellCommand("settings put system screen_brightness_mode 0")
                
                // Get ALL tokens, but ONLY apply to the first 2 (Main + Cover)
                // This prevents killing the Glasses (which would be index 2+)
                val tokens = getAllPhysicalDisplayTokens()
                val safeTokens = tokens.take(2)
                
                for (t in safeTokens) {
                    setDisplayBrightnessOnToken(t, -1.0f)
                }
                
                execShellCommand("settings put system screen_brightness_float -1.0")
                execShellCommand("settings put system screen_brightness -1")
            } else {
                // === SCREEN ON ===
                val floatVal = brightness.toFloat() / 255.0f
                
                // Restore ALL tokens (safety, in case user replugged glasses)
                val tokens = getAllPhysicalDisplayTokens()
                for (t in tokens) {
                    setDisplayBrightnessOnToken(t, floatVal)
                }
                
                execShellCommand("settings put system screen_brightness_float $floatVal")
                execShellCommand("settings put system screen_brightness $brightness")
            }
        } catch (e: Exception) {
            Log.e(TAG, "setBrightness failed", e)
        } finally {
             Binder.restoreCallingIdentity(token)
        }
    }

    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        Log.d(TAG, "setScreenOff(Global Broadcast, TurnOff: $turnOff)")
        val token = Binder.clearCallingIdentity()
        try {
            val mode = if (turnOff) POWER_MODE_OFF else POWER_MODE_NORMAL
            
            // Same safety limit: Only affect first 2 physical screens
            val tokens = getAllPhysicalDisplayTokens()
            val safeTokens = tokens.take(2)
            
            for (t in safeTokens) {
                setPowerModeOnToken(t, mode)
            }
        } catch (e: Exception) {
            Log.e(TAG, "setScreenOff failed", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    override fun forceStop(packageName: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec("am force-stop $packageName").waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun runCommand(command: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec(command).waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun repositionTask(packageName: String, left: Int, top: Int, right: Int, bottom: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            val cmd = arrayOf("sh", "-c", "dumpsys activity top | grep -E 'TASK.*id=|ACTIVITY.*$packageName'")
            val process = Runtime.getRuntime().exec(cmd)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var targetTaskId = -1
            
            while (reader.readLine().also { line = it } != null) {
                if (line!!.contains("TASK") && line!!.contains("id=")) {
                     val match = Regex("id=(\\d+)").find(line!!)
                     if (match != null) targetTaskId = match.groupValues[1].toInt()
                }
                if (targetTaskId != -1 && line!!.contains(packageName)) {
                    break
                }
            }
            reader.close()
            process.waitFor()
            
            if (targetTaskId != -1) {
                Runtime.getRuntime().exec("am task set-windowing-mode $targetTaskId 5").waitFor()
                Runtime.getRuntime().exec("am task resize $targetTaskId $left $top $right $bottom").waitFor()
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun getVisiblePackages(displayId: Int): List<String> {
        val packages = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val cmd = arrayOf("sh", "-c", "dumpsys activity activities | grep -E 'Display #|ActivityRecord'")
            val process = Runtime.getRuntime().exec(cmd)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var currentScanningDisplayId = -1
            
            val pkgRegex = Regex("u\\d+\\s+(\\S+?)/")
            val displayRegex = Regex("Display #(\\d+)")

            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.contains("Display #")) {
                    val displayMatch = displayRegex.find(l)
                    if (displayMatch != null) currentScanningDisplayId = displayMatch.groupValues[1].toInt()
                    continue
                }
                if (currentScanningDisplayId == displayId && l.contains("ActivityRecord{")) {
                    val matcher = pkgRegex.find(l)
                    if (matcher != null) {
                        val pkg = matcher.groupValues[1]
                        if (!packages.contains(pkg)) packages.add(pkg)
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "getVisiblePackages failed", e)
        } finally { 
            Binder.restoreCallingIdentity(token) 
        }
        return packages
    }

    override fun getAllRunningPackages(): List<String> {
        val packages = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val cmd = arrayOf("sh", "-c", "dumpsys activity activities | grep 'ActivityRecord'")
            val process = Runtime.getRuntime().exec(cmd)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val pkgRegex = Regex("u\\d+\\s+(\\S+?)/")
            
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.contains("ActivityRecord{")) {
                    val matcher = pkgRegex.find(l)
                    if (matcher != null) {
                        val pkg = matcher.groupValues[1]
                        if (!packages.contains(pkg)) packages.add(pkg)
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return packages
    }

    override fun getWindowLayouts(displayId: Int): List<String> = ArrayList()
    override fun getTaskId(packageName: String): Int {
        var taskId = -1
        val token = Binder.clearCallingIdentity()
        try {
            val cmd = arrayOf("sh", "-c", "dumpsys activity activities | grep -E 'Task id|$packageName'")
            val p = Runtime.getRuntime().exec(cmd)
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains(packageName)) {
                    if (line!!.startsWith("* Task{") || line!!.startsWith("Task{")) { val m = Regex("#(\\\\d+)").find(line!!); if (m != null) { taskId = m.groupValues[1].toInt(); break } }
                    if (line!!.contains("ActivityRecord")) { val m = Regex("t(\\\\d+)").find(line!!); if (m != null) { taskId = m.groupValues[1].toInt(); break } }
                }
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return taskId
    }
    override fun moveTaskToBack(taskId: Int) {
        val token = Binder.clearCallingIdentity()
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
            } catch (e2: Exception) {}
        } finally { Binder.restoreCallingIdentity(token) }
    }
    override fun setSystemBrightness(brightness: Int) { execShellCommand("settings put system screen_brightness $brightness") }
    override fun getSystemBrightness(): Int = 128
    override fun getSystemBrightnessFloat(): Float = 0.5f
    override fun setAutoBrightness(enabled: Boolean) { execShellCommand("settings put system screen_brightness_mode ${if (enabled) 1 else 0}") }
    override fun isAutoBrightness(): Boolean = true
    override fun setBrightnessViaDisplayManager(displayId: Int, brightness: Float): Boolean = setDisplayBrightnessInternal(displayId, brightness)
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShellUserService.kt.minimize
```
package com.example.quadrantlauncher

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    // --- v2.0 LOGIC: Shell Resolver for System Settings ---
    private val shellResolver: ContentResolver? by lazy {
        try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val systemContext = activityThreadClass.getMethod("getSystemContext").invoke(currentActivityThread) as Context
            val shellContext = object : ContextWrapper(systemContext) {
                override fun getPackageName(): String = "com.android.shell"
                override fun getOpPackageName(): String = "com.android.shell"
            }
            shellContext.contentResolver
        } catch (e: Exception) { 
            Log.e(TAG, "Failed to get ShellResolver", e)
            null 
        }
    }

    override fun setBrightness(value: Int) {
        val resolver = shellResolver ?: return
        try {
            // 1. Disable Auto Brightness
            Settings.System.putInt(resolver, "screen_brightness_mode", 0)

            if (value == -1) {
                // --- EXTINGUISH MODE (-1) ---
                // Remove minimum limit
                Settings.System.putInt(resolver, "screen_brightness_min", 0)
                
                // Set float to -1.0 (The magic signal for OLED off on some drivers)
                try {
                    Settings.System.putFloat(resolver, "screen_brightness_float", -1.0f)
                } catch (e: Exception) {
                    Settings.System.putString(resolver, "screen_brightness_float", "-1.0")
                }
                
                // Set int to -1
                Settings.System.putInt(resolver, "screen_brightness", -1)
                
                // Hardware override via DisplayManager
                setBrightnessViaDisplayManager(0, -1.0f)
            } else {
                // --- WAKE UP ---
                val safeVal = value.coerceIn(1, 255)
                val floatVal = safeVal / 255.0f
                
                Settings.System.putFloat(resolver, "screen_brightness_float", floatVal)
                Settings.System.putInt(resolver, "screen_brightness", safeVal)
                
                setBrightnessViaDisplayManager(0, floatVal)
            }
        } catch (e: Exception) {
            Log.e(TAG, "setBrightness failed", e)
        }
    }

    override fun setBrightnessViaDisplayManager(displayId: Int, brightness: Float): Boolean {
        try {
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, "display") as IBinder
            // Correctly escaped Stub for Kotlin string interpolation
            val iDisplayManagerClass = Class.forName("android.hardware.display.IDisplayManager\$Stub")
            val displayManager = iDisplayManagerClass.getMethod("asInterface", IBinder::class.java).invoke(null, binder)

            try {
                // Try 2-arg method first (Android 14+)
                val method = displayManager.javaClass.getMethod("setTemporaryBrightness", Int::class.javaPrimitiveType, Float::class.javaPrimitiveType)
                method.invoke(displayManager, displayId, brightness)
                return true
            } catch (e: NoSuchMethodException) {
                // Fallback to 1-arg method
                val method = displayManager.javaClass.getMethod("setTemporaryBrightness", Float::class.javaPrimitiveType)
                method.invoke(displayManager, brightness)
                return true
            }
        } catch (e: Exception) { 
            Log.e(TAG, "setBrightnessViaDisplayManager failed", e)
            return false 
        }
    }

    // --- v2.0 LOGIC: Screen Off (SurfaceControl) ---
    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        val token = Binder.clearCallingIdentity()
        try {
            val scClass = Class.forName("android.view.SurfaceControl")
            var serviceToken: IBinder? = null
            try { serviceToken = scClass.getDeclaredMethod("getInternalDisplayToken").apply { isAccessible = true }.invoke(null) as? IBinder } catch (e: Exception) {}
            
            if (serviceToken == null) {
                val ids = scClass.getMethod("getPhysicalDisplayIds").invoke(null) as LongArray
                if (ids.isNotEmpty()) {
                     val targetId = if (displayIndex >= 0 && displayIndex < ids.size) ids[displayIndex] else ids[0]
                     serviceToken = scClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType).invoke(null, targetId) as? IBinder
                }
            }
            
            if (serviceToken != null) {
                scClass.getMethod("setDisplayPowerMode", IBinder::class.java, Int::class.javaPrimitiveType)
                    .invoke(null, serviceToken, if (turnOff) 0 else 2)
            }
        } catch (e: Exception) {
            Log.e(TAG, "setScreenOff failed", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    // --- V1.0 LOGIC: Window Management (Retained for Tiling/Minimizing) ---
    
    override fun forceStop(packageName: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec("am force-stop $packageName").waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun runCommand(command: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec(command).waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun repositionTask(packageName: String, left: Int, top: Int, right: Int, bottom: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec("dumpsys activity top")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            var tid = -1
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains(packageName) && line!!.contains("TASK")) { 
                    val m = Regex("id=(\\d+)").find(line!!)
                    if (m != null) tid = m.groupValues[1].toInt() 
                }
            }
            if (tid != -1) { 
                Runtime.getRuntime().exec("am task set-windowing-mode $tid 5").waitFor()
                Runtime.getRuntime().exec("am task resize $tid $left $top $right $bottom").waitFor() 
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun getVisiblePackages(displayId: Int): List<String> {
        val list = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec("dumpsys window windows")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            var currentPkg: String? = null
            var isVisible = false
            var onCorrectDisplay = false
            val windowPattern = Pattern.compile("Window\\{[0-9a-f]+ u\\d+ ([^\\}/ ]+)")

            while (r.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.startsWith("Window #")) {
                    currentPkg = null; isVisible = false; onCorrectDisplay = false
                    val matcher = windowPattern.matcher(l)
                    if (matcher.find()) currentPkg = matcher.group(1)
                }
                if (l.contains("displayId=$displayId") || l.contains("mDisplayId=$displayId")) onCorrectDisplay = true
                if (l.contains("mViewVisibility=0x0")) isVisible = true

                if (currentPkg != null && isVisible && onCorrectDisplay) {
                    if (isUserApp(currentPkg!!) && !list.contains(currentPkg!!)) list.add(currentPkg!!)
                    currentPkg = null
                }
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return list
    }

    override fun getAllRunningPackages(): List<String> {
        val list = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec("dumpsys activity activities")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            val recordPattern = Pattern.compile("ActivityRecord\\{[0-9a-f]+ u\\d+ ([a-zA-Z0-9_.]+)/")
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains("ActivityRecord{")) {
                    val m = recordPattern.matcher(line!!)
                    if (m.find()) { val pkg = m.group(1); if (pkg != null && !list.contains(pkg) && isUserApp(pkg)) list.add(pkg) }
                }
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return list
    }

    override fun getWindowLayouts(displayId: Int): List<String> = ArrayList()

    override fun getTaskId(packageName: String): Int {
        var taskId = -1
        val token = Binder.clearCallingIdentity()
        try {
            val cmd = arrayOf("sh", "-c", "dumpsys activity activities | grep -E 'Task id|$packageName'")
            val p = Runtime.getRuntime().exec(cmd)
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains(packageName)) {
                    if (line!!.startsWith("* Task{") || line!!.startsWith("Task{")) { val m = Regex("#(\\d+)").find(line!!); if (m != null) { taskId = m.groupValues[1].toInt(); break } }
                    if (line!!.contains("ActivityRecord")) { val m = Regex("t(\\d+)").find(line!!); if (m != null) { taskId = m.groupValues[1].toInt(); break } }
                }
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return taskId
    }

    override fun moveTaskToBack(taskId: Int) {
        val token = Binder.clearCallingIdentity()
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
            } catch (e2: Exception) {}
        } finally { Binder.restoreCallingIdentity(token) }
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

    // Interface compliance stubs
    override fun setSystemBrightness(brightness: Int) { setBrightness(brightness) }
    override fun getSystemBrightness(): Int = 128
    override fun getSystemBrightnessFloat(): Float = 0.5f
    override fun setAutoBrightness(enabled: Boolean) { 
        val resolver = shellResolver ?: return
        try { Settings.System.putInt(resolver, "screen_brightness_mode", if (enabled) 1 else 0) } catch(e: Exception) {}
    }
    override fun isAutoBrightness(): Boolean = true
}
```

## File: Cover-Screen-Launcher/ShellUserService.kt
```kotlin
package com.example.quadrantlauncher

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.os.Binder
import android.os.IBinder
import android.os.Process
import android.provider.Settings
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    private fun setTemporaryBrightnessViaDisplayManager(brightness: Float): Boolean {
        try {
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, "display") as IBinder
            val iDisplayManagerClass = Class.forName("android.hardware.display.IDisplayManager$Stub")
            val displayManager = iDisplayManagerClass.getMethod("asInterface", IBinder::class.java).invoke(null, binder)

            val methods = displayManager.javaClass.methods
            var success = false
            
            for (m in methods) {
                if (m.name == "setTemporaryBrightness") {
                    try {
                        if (m.parameterCount == 2 && 
                            m.parameterTypes[0] == Int::class.javaPrimitiveType && 
                            m.parameterTypes[1] == Float::class.javaPrimitiveType) {
                            
                            // Target both potential IDs
                            m.invoke(displayManager, 0, brightness)
                            m.invoke(displayManager, 1, brightness)
                            
                            if (brightness < 0) {
                                m.invoke(displayManager, 0, Float.NaN)
                            }
                            Log.d(TAG, "Invoked setTemporaryBrightness(0/1, $brightness)")
                            success = true
                        }
                        else if (m.parameterCount == 1 && 
                            m.parameterTypes[0] == Float::class.javaPrimitiveType) {
                            m.invoke(displayManager, brightness)
                            if (brightness < 0) m.invoke(displayManager, Float.NaN)
                            Log.d(TAG, "Invoked setTemporaryBrightness($brightness)")
                            success = true
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Method invoke failed: ${m.name}", e)
                    }
                }
            }
            return success
        } catch (e: Exception) {
            Log.e(TAG, "setTemporaryBrightness reflection failed", e)
        }
        return false
    }

    private fun execShell(cmd: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "CMD Failed: $cmd", e)
        }
    }

    override fun setBrightness(value: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            Log.i(TAG, "setBrightness: $value | UID: ${Process.myUid()}")
            execShell("settings put system screen_brightness_mode 0")
            execShell("settings put system screen_auto_brightness_adj 0")

            if (value == -1) {
                execShell("settings put system screen_brightness_min 0")
                val uri = "content://settings/system"
                execShell("content insert --uri $uri --bind name:s:screen_brightness_float --bind value:f:-1.0")
                execShell("settings put system screen_brightness_float -1.0")
                execShell("settings put system screen_brightness -1")
                execShell("settings put system screen_brightness_float NaN")
                setTemporaryBrightnessViaDisplayManager(-1.0f)
            } else {
                val safeVal = value.coerceIn(1, 255)
                val floatVal = safeVal / 255.0f
                val uri = "content://settings/system"
                execShell("content insert --uri $uri --bind name:s:screen_brightness_float --bind value:f:$floatVal")
                execShell("content insert --uri $uri --bind name:s:screen_brightness --bind value:i:$safeVal")
                execShell("settings put system screen_brightness_float $floatVal")
                execShell("settings put system screen_brightness $safeVal")
                execShell("cmd display set-brightness $floatVal")
                setTemporaryBrightnessViaDisplayManager(floatVal)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setBrightness", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    override fun forceStop(packageName: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec("am force-stop $packageName").waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun runCommand(command: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec(command).waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        val token = Binder.clearCallingIdentity()
        try {
            val scClass = if (android.os.Build.VERSION.SDK_INT >= 34) {
                try { Class.forName("com.android.server.display.DisplayControl") } catch (e: Exception) { Class.forName("android.view.SurfaceControl") }
            } else { Class.forName("android.view.SurfaceControl") }
            
            var serviceToken: IBinder? = null
            
            // Try getInternalDisplayToken first (for Display 0)
            try {
                val method = scClass.getDeclaredMethod("getInternalDisplayToken")
                method.isAccessible = true
                serviceToken = method.invoke(null) as? IBinder
            } catch (e: Exception) {}

            val getIdsMethod = scClass.getMethod("getPhysicalDisplayIds")
            val getTokenMethod = scClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
            val setPowerMethod = scClass.getMethod("setDisplayPowerMode", IBinder::class.java, Int::class.javaPrimitiveType)
            
            val physicalIds = getIdsMethod.invoke(null) as LongArray
            val mode = if (turnOff) 0 else 2

            if (displayIndex == -1) {
                // WILDCARD MODE: Turn OFF/ON ALL DETECTED DISPLAYS
                Log.d(TAG, "setScreenOff: Targeting ALL ${physicalIds.size} displays")
                
                // 1. Try Internal Token
                if (serviceToken != null) {
                    try { setPowerMethod.invoke(null, serviceToken, mode) } catch(e: Exception){}
                }
                
                // 2. Loop all Physical IDs
                for (id in physicalIds) {
                    try {
                        val t = getTokenMethod.invoke(null, id) as? IBinder
                        if (t != null) setPowerMethod.invoke(null, t, mode)
                    } catch (e: Exception) {}
                }
            } else {
                // SPECIFIC INDEX MODE
                // Only target if index exists, DO NOT fallback to 0 arbitrarily
                if (displayIndex >= 0 && displayIndex < physicalIds.size) {
                    val targetId = physicalIds[displayIndex]
                    val t = getTokenMethod.invoke(null, targetId) as? IBinder
                    if (t != null) setPowerMethod.invoke(null, t, mode)
                } else if (displayIndex == 0 && serviceToken != null) {
                    // Fallback for index 0 if physicalIds array is empty/weird
                    setPowerMethod.invoke(null, serviceToken, mode)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "setScreenOff Failed", e)
        } finally { 
            Binder.restoreCallingIdentity(token) 
        }
    }

    override fun repositionTask(packageName: String, left: Int, top: Int, right: Int, bottom: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity top")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var targetTaskId = -1
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.startsWith("TASK") && l.contains(packageName)) {
                    val match = Regex("id=(\\d+)").find(l)
                    if (match != null) { targetTaskId = match.groupValues[1].toInt(); break }
                }
            }
            reader.close()
            process.waitFor()
            if (targetTaskId != -1) {
                Runtime.getRuntime().exec("am task set-windowing-mode $targetTaskId 5").waitFor()
                Runtime.getRuntime().exec("am task resize $targetTaskId $left $top $right $bottom").waitFor()
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun getVisiblePackages(displayId: Int): List<String> {
        val packages = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity activities")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var currentScanningDisplayId = -1
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
            reader.close()
            process.waitFor()
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return packages
    }

    override fun getAllRunningPackages(): List<String> {
        val packages = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity activities")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val recordPattern = Pattern.compile("ActivityRecord\\{[0-9a-f]+ u\\d+ ([a-zA-Z0-9_.]+)/")
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.contains("ActivityRecord{")) {
                    val matcher = recordPattern.matcher(l)
                    if (matcher.find()) { val pkg: String? = matcher.group(1); if (pkg != null && !packages.contains(pkg)) packages.add(pkg) }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return packages
    }

    override fun getWindowLayouts(displayId: Int): List<String> = ArrayList()
    override fun getTaskId(packageName: String): Int = -1
    override fun moveTaskToBack(taskId: Int) {}
}
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ManualAdjustActivity.kt
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
        val targetText = if (isKeyboardTarget) "KEYBOARD" else "TRACKPAD"
        val actionText = if (isMoveMode) "POSITION" else "SIZE"
        
        textMode.text = "$targetText: $actionText"
        
        if (isKeyboardTarget) {
            textMode.setTextColor(Color.MAGENTA)
            btnToggleTarget.text = "Switch to Trackpad"
        } else {
            textMode.setTextColor(if (isMoveMode) Color.GREEN else Color.CYAN)
            btnToggleTarget.text = "Switch to Keyboard"
        }
        
        btnToggle.text = if (isMoveMode) "Switch to Resize" else "Switch to Position"
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

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ShizukuInputHandler.kt
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
//             val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            // If strictly using Shizuku binding without 'su', replace above with:
            // Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inject input: ${e.message}")
        }
    }
}
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadService.kt
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
            trackpadView?.setOnTouchListener { _, _ ->
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

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/bg_trackpad_drawer.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#1E1E1E"/>
    <corners android:radius="16dp"/>
    <stroke android:width="1dp" android:color="#444444"/>
</shape>
```

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_tab_help.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zm1,17h-2v-2h2v2zm2.07,-7.75l-0.9,0.92C13.45,12.9 13,13.5 13,15h-2v-0.5c0,-1.1 0.45,-2.1 1.17,-2.83l1.24,-1.26c0.37,-0.36 0.59,-0.86 0.59,-1.41 0,-1.1 -0.9,-2 -2,-2s-2,0.9 -2,2H8c0,-2.21 1.79,-4 4,-4s4,1.79 4,4c0,0.88 -0.36,1.68 -0.93,2.25z"/>
</vector>
```

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_tab_keyboard.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M20,5H4c-1.1,0 -1.99,0.9 -1.99,2L2,17c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V7c0,-1.1 -0.9,-2 -2,-2zm-9,3h2v2h-2V8zm0,3h2v2h-2v-2zM8,8h2v2H8V8zm0,3h2v2H8v-2zm-1,2H5v-2h2v2zm0,-3H5V8h2v2zm9,7H8v-2h8v2zm0,-4h-2v-2h2v2zm0,-3h-2V8h2v2zm3,3h-2v-2h2v2zm0,-3h-2V8h2v2z"/>
</vector>
```

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_tab_main.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M10,20v-6h4v6h5v-8h3L12,3 2,12h3v8z"/>
</vector>
```

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_tab_profiles.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M17,3H5c-1.11,0 -2,0.9 -2,2v14c0,1.1 0.89,2 2,2h14c1.1,0 2,-0.9 2,-2V7l-4,-4zm-5,16c-1.66,0 -3,-1.34 -3,-3s1.34,-3 3,-3 3,1.34 3,3 -1.34,3 -3,3zm3,-10H5V5h10v4z"/>
</vector>
```

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_tab_settings.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M19.14,12.94c0.04,-0.3 0.06,-0.61 0.06,-0.94c0,-0.32 -0.02,-0.64 -0.07,-0.94l2.03,-1.58c0.18,-0.14 0.23,-0.41 0.12,-0.61l-1.92,-3.32c-0.12,-0.22 -0.37,-0.29 -0.59,-0.22l-2.39,0.96c-0.5,-0.38 -1.03,-0.7 -1.62,-0.94L14.4,2.81c-0.04,-0.24 -0.24,-0.41 -0.48,-0.41h-3.84c-0.24,0 -0.43,0.17 -0.47,0.41L9.25,5.35C8.66,5.59 8.12,5.92 7.63,6.29L5.24,5.33c-0.22,-0.08 -0.47,0 -0.59,0.22L2.74,8.87C2.62,9.08 2.66,9.34 2.86,9.48l2.03,1.58C4.84,11.36 4.8,11.69 4.8,12s0.02,0.64 0.07,0.94l-2.03,1.58c-0.18,0.14 -0.23,0.41 -0.12,0.61l1.92,3.32c0.12,0.22 0.37,0.29 0.59,0.22l2.39,-0.96c0.5,0.38 1.03,0.7 1.62,0.94l0.36,2.54c0.05,0.24 0.24,0.41 0.48,0.41h3.84c0.24,0 0.43,-0.17 0.47,-0.41l0.36,-2.54c0.59,-0.24 1.13,-0.56 1.62,-0.94l2.39,0.96c0.22,0.08 0.47,0 0.59,-0.22l1.92,-3.32c0.12,-0.22 0.07,-0.47 -0.12,-0.61L19.14,12.94zM12,15.6c-1.98,0 -3.6,-1.62 -3.6,-3.6s1.62,-3.6 3.6,-3.6s3.6,1.62 3.6,3.6S13.98,15.6 12,15.6z"/>
</vector>
```

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/ic_trackpad_foreground_scaled.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/ic_trackpad_logo"
    android:insetLeft="4dp"
    android:insetRight="4dp"
    android:insetTop="4dp"
    android:insetBottom="4dp" />
```

## File: Cover-Screen-Trackpad/app/src/main/res/layout/activity_manual_adjust.xml
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
        android:text="TRACKPAD: MOVE"
        android:textColor="#3DDC84"
        android:textSize="16sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center"
        android:layout_marginBottom="8dp">
        
        <Button
            android:id="@+id/btn_toggle_target"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Target: Trackpad"
            android:backgroundTint="#6200EE"
            android:textColor="#FFFFFF"/>
    </LinearLayout>

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

## File: Cover-Screen-Trackpad/app/src/main/res/layout/item_dpi_custom.xml
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

## File: Cover-Screen-Trackpad/app/build.gradle.kts
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
        versionCode = 2
        versionName = "2.0"
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

## File: GEMINI.md
```markdown
CLI AGENT CONTEXT INSTRUCTIONS

Role: You are an autonomous DevOps and Coding Agent operating in a Termux Android environment.
Project Root: /data/data/com.termux/files/home/projects/DroidOS
​Operational Guidelines:
​File Editing Strategy:
​Full File Replacement: If a code block is long and appears to contain the complete file content (imports, class declaration, closing braces), overwrite the entire file. This is the preferred method to avoid partial merge errors.
Operational Guidelines:
​ANCHOR & REPLACE: When adding new methods or variables, find a specific existing line (an "anchor") and replace that line with itself plus the new code.
​Block Replacement: If the code block is a specific function or section, find the matching section in the existing file and replace it entirely. Replace or include any code block proceeding and ending comments in the existing code with the new/updated proceeding and ending comment blocks, if any.
​Creation: If the file does not exist, create it including any necessary parent directories.
​Navigation & commands:
​Use nvim paths provided in the plan to open files.
​Use sed only if explicitly provided in a separate command block.
​Always ensure you are operating relative to the Project Root.
​Safety:
​If you see [OUTPUT NOT AVAILABLE], you must request the file content again.
​Preserve imports unless the new code block explicitly changes them.

​Input Format:
​The user will provide a plan either aw a .md plqn (will provode path) or give instructions directly within prompt window.

​Each section will contain: ### File: path/to/file followed by a code block.
​Execute the changes sequentially.

After making all instructed changes, build to check if errors. if errors, check if all updates applied correctly based on instructions. In particular check for syntax issues. If build errors seem to be out of scope of instructions, create a repomid with alias CleanBuildTrackpad if we are making updates in /data/data/com.termux/files/home/projects/DroidOS/Cover-Screen-Trackpad or use alias CleanBuildLauncher for updates in /data/data/com.termux/files/home/projects/DroidOS/Cover-Screen-Launcher

If build success, commit to local with appropriate description (refer to instructions for summary if existing)
```

## File: Cover-Screen-Launcher/app/src/main/res/layout/item_dpi_custom.xml
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

## File: Cover-Screen-Launcher/app/src/main/res/values/strings.xml
```xml
<resources>
    <string name="app_name">DroidOS Launcher</string>
    <string name="accessibility_service_description">DroidOS Launcher Service</string>
</resources>
```

## File: Cover-Screen-Launcher/app/src/main/AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

    <queries>
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
    </queries>

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:icon="@mipmap/ic_launcher_adaptive"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_adaptive"
        android:supportsRtl="true"
        android:theme="@style/Theme.QuadrantLauncher"
        tools:targetApi="31">

        <provider
            android:name="rikka.shizuku.ShizukuProvider"
            android:authorities="${applicationId}.shizuku"
            android:enabled="true"
            android:exported="true"
            android:multiprocess="false" />

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity android:name=".IconPickerActivity" 
                  android:theme="@android:style/Theme.Translucent.NoTitleBar"
                  android:exported="false" />

        <service 
            android:name=".FloatingLauncherService"
            android:enabled="true"
            android:exported="false"
            android:foregroundServiceType="specialUse">
            <property android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE" android:value="overlay_launcher"/>
        </service>

        <activity android:name=".SplitActivity" android:exported="false" />

    </application>
</manifest>
```

## File: Cover-Screen-Trackpad/app/src/main/aidl/com/example/coverscreentester/IShellService.aidl
```
package com.example.coverscreentester;

interface IShellService {
    void injectMouse(int action, float x, float y, int displayId, int source, int buttonState, long downTime);
    void injectScroll(float x, float y, float vDistance, float hDistance, int displayId);
    void execClick(float x, float y, int displayId);
    void execRightClick(float x, float y, int displayId);
    
    // Updated: Now accepts metaState (Ctrl/Alt) and displayId
    void injectKey(int keyCode, int action, int metaState, int displayId);
    
    void setWindowingMode(int taskId, int mode);
    void resizeTask(int taskId, int left, int top, int right, int bottom);
    String runCommand(String cmd);

    // Screen Control Methods (Ported from Launcher)
    void setScreenOff(int displayIndex, boolean turnOff);
    void setBrightness(int value);
    boolean setBrightnessViaDisplayManager(int displayId, float brightness);
}
```

## File: Cover-Screen-Trackpad/app/src/main/res/drawable/bg_trackpad_bubble.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="oval">
    <solid android:color="#444444"/>
    <stroke android:width="2dp" android:color="#888888"/>
</shape>
```

## File: Cover-Screen-Trackpad/app/src/main/res/layout/activity_settings.xml
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
            android:text="Scroll Distance (Swipe Length)"
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
            android:id="@+id/switchTapScroll"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Tap-to-Scroll Buttons"
            android:textColor="#FFFFFF"
            android:textSize="16sp"
            android:minHeight="48dp"
            android:checked="true"/>

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
            android:text="Reverse Buttons (Natural)"
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
            android:text="Keyboard Key Size (%)"
            android:textColor="#FFFFFF"/>
        <SeekBar
            android:id="@+id/seekBarKeyScale"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="200"
            android:progress="100"
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

## File: Cover-Screen-Trackpad/app/src/main/res/layout/item_trackpad_menu.xml
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

## File: Cover-Screen-Trackpad/app/src/main/AndroidManifest.xml
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

        <provider
            android:name="rikka.shizuku.ShizukuProvider"
            android:authorities="${applicationId}.shizuku"
            android:enabled="true"
            android:exported="true"
            android:multiprocess="false" />

    </application>

</manifest>
```

## File: Cover-Screen-Trackpad/crash_log.txt
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

## File: Cover-Screen-Launcher/app/build.gradle.kts
```
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.quadrantlauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSLauncher"
        minSdk = 30
        targetSdk = 34
        versionCode = 2
        versionName = "2.0"
        multiDexEnabled = true
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")
    implementation("dev.rikka.shizuku:aidl:13.1.5")
}
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/SettingsActivity.kt
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
        
        val swTapScroll = findViewById<Switch>(R.id.switchTapScroll)
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
        val seekKeyScale = findViewById<SeekBar>(R.id.seekBarKeyScale)
        
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Load Values
        val cSpeed = prefs.getFloat("cursor_speed", 2.5f)
        seekBarCursor.progress = (cSpeed * 10).toInt()
        tvCursor.text = "Cursor Speed: "

        val sSpeed = prefs.getFloat("scroll_speed", 3.0f)
        seekBarScroll.progress = (sSpeed * 10).toInt()
        tvScroll.text = "Scroll Distance: "

        swTapScroll.isChecked = prefs.getBoolean("tap_scroll", true)
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
        seekKeyScale.progress = prefs.getInt("keyboard_key_scale", 100)

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
                tvScroll.text = "Scroll Distance: "
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
        seekKeyScale.setOnSeekBarChangeListener(createPreviewListener("keyboard_scale"))

        btnSave.setOnClickListener {
            val cVal = if (seekBarCursor.progress < 1) 0.1f else seekBarCursor.progress / 10f
            val sVal = if (seekBarScroll.progress < 1) 0.1f else seekBarScroll.progress / 10f
            
            prefs.edit()
                .putFloat("cursor_speed", cVal)
                .putFloat("scroll_speed", sVal)
                .putBoolean("tap_scroll", swTapScroll.isChecked)
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
                .putInt("keyboard_key_scale", seekKeyScale.progress)
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

## File: Cover-Screen-Trackpad/app/src/main/res/layout/activity_main.xml
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

    </LinearLayout>
</ScrollView>
```

## File: Cover-Screen-Trackpad/app/src/main/res/layout/layout_trackpad_bubble.xml
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

## File: CHANGELOG.md
```markdown
# Changelog

## [2.1] - 2025-12-05

### Fixed
- **Overflow App Management**: Restored `repositionTask` functionality in `ShellUserService`. This allows the launcher to correctly resize and position "overflow" apps into freeform windows using shell commands (`am task resize`).
- **App Minimization & Stability**: Resolved critical compilation errors in `ShellUserService.kt` that prevented the shell service from running.
    - Restored `execShellCommand` for system-level operations.
    - Fixed `getAllPhysicalDisplayTokens` reflection logic for Android 14.
- **Display Control**: Fixed `setBrightness` and `setScreenOff` implementations to correctly target the Cover Screen (Display 1) without crashing the main interface.
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/AppPreferences.kt
```kotlin
package com.example.quadrantlauncher

import android.content.Context

object AppPreferences {

    private const val PREFS_NAME = "AppLauncherPrefs"
    private const val KEY_FAVORITES = "KEY_FAVORITES"
    private const val KEY_LAST_LAYOUT = "KEY_LAST_LAYOUT"
    private const val KEY_LAST_CUSTOM_LAYOUT_NAME = "KEY_LAST_CUSTOM_LAYOUT_NAME"
    private const val KEY_PROFILES = "KEY_PROFILES"
    private const val KEY_CUSTOM_LAYOUTS = "KEY_CUSTOM_LAYOUTS"
    private const val KEY_FONT_SIZE = "KEY_FONT_SIZE"
    private const val KEY_ICON_URI = "KEY_ICON_URI"
    
    // Settings
    private const val KEY_KILL_ON_EXECUTE = "KEY_KILL_ON_EXECUTE"
    private const val KEY_TARGET_DISPLAY_INDEX = "KEY_TARGET_DISPLAY_INDEX"
    private const val KEY_IS_INSTANT_MODE = "KEY_IS_INSTANT_MODE"
    private const val KEY_LAST_QUEUE = "KEY_LAST_QUEUE"
    private const val KEY_SHOW_SHIZUKU_WARNING = "KEY_SHOW_SHIZUKU_WARNING"
    private const val KEY_REORDER_TIMEOUT = "KEY_REORDER_TIMEOUT"
    private const val KEY_USE_ALT_SCREEN_OFF = "KEY_USE_ALT_SCREEN_OFF" // New
    
    // Reorder Methods
    private const val KEY_REORDER_METHOD_DRAG = "KEY_REORDER_METHOD_DRAG"
    private const val KEY_REORDER_METHOD_TAP = "KEY_REORDER_METHOD_TAP"
    private const val KEY_REORDER_METHOD_SCROLL = "KEY_REORDER_METHOD_SCROLL"
    
    // Drawer Geometry
    private const val KEY_DRAWER_HEIGHT = "KEY_DRAWER_HEIGHT"
    private const val KEY_DRAWER_WIDTH = "KEY_DRAWER_WIDTH"
    private const val KEY_AUTO_RESIZE_KEYBOARD = "KEY_AUTO_RESIZE_KEYBOARD"
    
    // Custom Resolutions
    private const val KEY_CUSTOM_RESOLUTION_NAMES = "KEY_CUSTOM_RESOLUTION_NAMES"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun savePackage(context: Context, key: String, packageName: String) {
        getPrefs(context).edit().putString(key, packageName).apply()
    }

    fun loadPackage(context: Context, key: String): String? {
        return getPrefs(context).getString(key, null)
    }

    fun getSimpleName(pkg: String?): String {
        if (pkg == null) return "Select App"
        val name = pkg.substringAfterLast('.')
        return if (name.isNotEmpty()) name else pkg
    }

    fun getFavorites(context: Context): MutableSet<String> {
        return getPrefs(context).getStringSet(KEY_FAVORITES, mutableSetOf()) ?: mutableSetOf()
    }

    fun isFavorite(context: Context, packageName: String): Boolean {
        return getFavorites(context).contains(packageName)
    }

    fun toggleFavorite(context: Context, packageName: String): Boolean {
        val favorites = getFavorites(context)
        val newSet = HashSet(favorites)
        val isAdded: Boolean
        if (newSet.contains(packageName)) {
            newSet.remove(packageName)
            isAdded = false
        } else {
            newSet.add(packageName)
            isAdded = true
        }
        getPrefs(context).edit().putStringSet(KEY_FAVORITES, newSet).apply()
        return isAdded
    }
    
    // --- GLOBAL LAYOUT PREFS ---
    fun saveLastLayout(context: Context, layoutId: Int) {
        getPrefs(context).edit().putInt(KEY_LAST_LAYOUT, layoutId).apply()
    }

    fun getLastLayout(context: Context): Int {
        return getPrefs(context).getInt(KEY_LAST_LAYOUT, 2)
    }
    
    fun saveLastCustomLayoutName(context: Context, name: String?) {
        getPrefs(context).edit().putString(KEY_LAST_CUSTOM_LAYOUT_NAME, name).apply()
    }

    fun getLastCustomLayoutName(context: Context): String? {
        return getPrefs(context).getString(KEY_LAST_CUSTOM_LAYOUT_NAME, null)
    }

    // --- PER-DISPLAY SETTINGS ---
    
    fun saveDisplayResolution(context: Context, displayId: Int, resIndex: Int) {
        getPrefs(context).edit().putInt("RES_D$displayId", resIndex).apply()
    }

    fun getDisplayResolution(context: Context, displayId: Int): Int {
        return getPrefs(context).getInt("RES_D$displayId", 0)
    }

    fun saveDisplayDpi(context: Context, displayId: Int, dpi: Int) {
        getPrefs(context).edit().putInt("DPI_D$displayId", dpi).apply()
    }

    fun getDisplayDpi(context: Context, displayId: Int): Int {
        return getPrefs(context).getInt("DPI_D$displayId", -1)
    }

    // --- PROFILES ---
    fun getProfileNames(context: Context): MutableSet<String> {
        return getPrefs(context).getStringSet(KEY_PROFILES, mutableSetOf()) ?: mutableSetOf()
    }

    fun saveProfile(context: Context, name: String, layout: Int, resIndex: Int, dpi: Int, apps: List<String>) {
        val names = getProfileNames(context)
        val newNames = HashSet(names)
        newNames.add(name)
        getPrefs(context).edit().putStringSet(KEY_PROFILES, newNames).apply()
        val appString = apps.joinToString(",")
        val data = "$layout|$resIndex|$dpi|$appString"
        getPrefs(context).edit().putString("PROFILE_$name", data).apply()
    }

    fun getProfileData(context: Context, name: String): String? {
        return getPrefs(context).getString("PROFILE_$name", null)
    }

    fun deleteProfile(context: Context, name: String) {
        val names = getProfileNames(context)
        val newNames = HashSet(names)
        newNames.remove(name)
        getPrefs(context).edit().putStringSet(KEY_PROFILES, newNames).remove("PROFILE_$name").apply()
    }

    fun renameProfile(context: Context, oldName: String, newName: String): Boolean {
        if (oldName == newName) return false
        if (newName.isEmpty()) return false
        val names = getProfileNames(context)
        if (!names.contains(oldName)) return false
        val data = getProfileData(context, oldName) ?: return false
        val newNames = HashSet(names)
        newNames.remove(oldName)
        newNames.add(newName)
        getPrefs(context).edit().putStringSet(KEY_PROFILES, newNames).apply()
        getPrefs(context).edit().putString("PROFILE_$newName", data).remove("PROFILE_$oldName").apply()
        return true
    }

    // --- CUSTOM LAYOUTS ---
    fun getCustomLayoutNames(context: Context): MutableSet<String> {
        return getPrefs(context).getStringSet(KEY_CUSTOM_LAYOUTS, mutableSetOf()) ?: mutableSetOf()
    }

    fun saveCustomLayout(context: Context, name: String, rectsData: String) {
        val names = getCustomLayoutNames(context)
        val newNames = HashSet(names)
        newNames.add(name)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_LAYOUTS, newNames).apply()
        getPrefs(context).edit().putString("LAYOUT_$name", rectsData).apply()
    }

    fun getCustomLayoutData(context: Context, name: String): String? {
        return getPrefs(context).getString("LAYOUT_$name", null)
    }
    
    fun deleteCustomLayout(context: Context, name: String) {
        val names = getCustomLayoutNames(context)
        val newNames = HashSet(names)
        newNames.remove(name)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_LAYOUTS, newNames).remove("LAYOUT_$name").apply()
    }
    
    fun renameCustomLayout(context: Context, oldName: String, newName: String): Boolean {
        if (oldName == newName) return false
        if (newName.isEmpty()) return false
        val names = getCustomLayoutNames(context)
        if (!names.contains(oldName)) return false
        val data = getCustomLayoutData(context, oldName) ?: return false
        val newNames = HashSet(names)
        newNames.remove(oldName)
        newNames.add(newName)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_LAYOUTS, newNames).apply()
        getPrefs(context).edit().putString("LAYOUT_$newName", data).remove("LAYOUT_$oldName").apply()
        return true
    }
    
    // --- CUSTOM RESOLUTIONS ---
    fun getCustomResolutionNames(context: Context): MutableSet<String> {
        return getPrefs(context).getStringSet(KEY_CUSTOM_RESOLUTION_NAMES, mutableSetOf()) ?: mutableSetOf()
    }

    fun saveCustomResolution(context: Context, name: String, value: String) {
        val names = getCustomResolutionNames(context)
        val newNames = HashSet(names)
        newNames.add(name)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_RESOLUTION_NAMES, newNames).apply()
        getPrefs(context).edit().putString("RES_$name", value).apply()
    }
    
    fun getCustomResolutionValue(context: Context, name: String): String? {
        return getPrefs(context).getString("RES_$name", null)
    }

    fun deleteCustomResolution(context: Context, name: String) {
        val names = getCustomResolutionNames(context)
        val newNames = HashSet(names)
        newNames.remove(name)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_RESOLUTION_NAMES, newNames).remove("RES_$name").apply()
    }
    
    fun renameCustomResolution(context: Context, oldName: String, newName: String): Boolean {
        if (oldName == newName) return false
        if (newName.isEmpty()) return false
        val names = getCustomResolutionNames(context)
        if (!names.contains(oldName)) return false
        val data = getCustomResolutionValue(context, oldName) ?: return false
        val newNames = HashSet(names)
        newNames.remove(oldName)
        newNames.add(newName)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_RESOLUTION_NAMES, newNames).apply()
        getPrefs(context).edit().putString("RES_$newName", data).remove("RES_$oldName").apply()
        return true
    }

    // --- FONT SIZE & ICONS & DRAWER ---
    fun saveFontSize(context: Context, size: Float) {
        getPrefs(context).edit().putFloat(KEY_FONT_SIZE, size).apply()
    }

    fun getFontSize(context: Context): Float {
        return getPrefs(context).getFloat(KEY_FONT_SIZE, 16f)
    }

    fun saveIconUri(context: Context, uri: String) {
        getPrefs(context).edit().putString(KEY_ICON_URI, uri).apply()
    }

    fun getIconUri(context: Context): String? {
        return getPrefs(context).getString(KEY_ICON_URI, null)
    }
    
    fun setDrawerHeightPercent(context: Context, percent: Int) {
        getPrefs(context).edit().putInt(KEY_DRAWER_HEIGHT, percent).apply()
    }
    
    fun getDrawerHeightPercent(context: Context): Int {
        return getPrefs(context).getInt(KEY_DRAWER_HEIGHT, 70)
    }
    
    fun setDrawerWidthPercent(context: Context, percent: Int) {
        getPrefs(context).edit().putInt(KEY_DRAWER_WIDTH, percent).apply()
    }
    
    fun getDrawerWidthPercent(context: Context): Int {
        return getPrefs(context).getInt(KEY_DRAWER_WIDTH, 90)
    }
    
    fun setAutoResizeKeyboard(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_RESIZE_KEYBOARD, enable).apply()
    }
    
    fun getAutoResizeKeyboard(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_RESIZE_KEYBOARD, true)
    }

    // --- SETTINGS ---
    fun setKillOnExecute(context: Context, kill: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_KILL_ON_EXECUTE, kill).apply()
    }

    fun getKillOnExecute(context: Context): Boolean {
        // Default is FALSE for Kill On Execute
        return getPrefs(context).getBoolean(KEY_KILL_ON_EXECUTE, false)
    }

    fun setTargetDisplayIndex(context: Context, index: Int) {
        getPrefs(context).edit().putInt(KEY_TARGET_DISPLAY_INDEX, index).apply()
    }

    fun getTargetDisplayIndex(context: Context): Int {
        return getPrefs(context).getInt(KEY_TARGET_DISPLAY_INDEX, 1)
    }

    fun setInstantMode(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_IS_INSTANT_MODE, enable).apply()
    }

    fun getInstantMode(context: Context): Boolean {
        // Default is TRUE for Instant Mode
        return getPrefs(context).getBoolean(KEY_IS_INSTANT_MODE, true)
    }
    
    fun saveLastQueue(context: Context, apps: List<String>) {
        val str = apps.joinToString(",")
        getPrefs(context).edit().putString(KEY_LAST_QUEUE, str).apply()
    }
    
    fun getLastQueue(context: Context): List<String> {
        val str = getPrefs(context).getString(KEY_LAST_QUEUE, "") ?: ""
        if (str.isEmpty()) return emptyList()
        return str.split(",").filter { it.isNotEmpty() }
    }
    
    fun setShowShizukuWarning(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SHOW_SHIZUKU_WARNING, enable).apply()
    }

    fun getShowShizukuWarning(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_SHOW_SHIZUKU_WARNING, true)
    }
    
    fun setUseAltScreenOff(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_USE_ALT_SCREEN_OFF, enable).apply()
    }

    fun getUseAltScreenOff(context: Context): Boolean {
        // Default false (use standard SurfaceControl method)
        return getPrefs(context).getBoolean(KEY_USE_ALT_SCREEN_OFF, false)
    }
    
    // --- REORDER PREFERENCES ---
    fun setReorderTimeout(context: Context, seconds: Int) {
        getPrefs(context).edit().putInt(KEY_REORDER_TIMEOUT, seconds).apply()
    }
    
    fun getReorderTimeout(context: Context): Int {
        return getPrefs(context).getInt(KEY_REORDER_TIMEOUT, 2) // Default 2 seconds
    }
    
    fun setReorderDrag(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_REORDER_METHOD_DRAG, enable).apply()
    }
    
    fun getReorderDrag(context: Context): Boolean {
        // CHANGED: Default to FALSE so Tap works out of box
        return getPrefs(context).getBoolean(KEY_REORDER_METHOD_DRAG, false)
    }
    
    fun setReorderTap(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_REORDER_METHOD_TAP, enable).apply()
    }
    
    fun getReorderTap(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REORDER_METHOD_TAP, true) // Default Enabled
    }
    
    fun setReorderScroll(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_REORDER_METHOD_SCROLL, enable).apply()
    }
    
    fun getReorderScroll(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REORDER_METHOD_SCROLL, true) // Default Enabled
    }
}
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardManager.kt
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
import kotlin.math.roundToInt

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
    private val BASE_KEY_HEIGHT = 45 // Base height in dp
    private var scaleFactor = 1.0f
    
    private val ROW_MARGIN = 2.dp
    private val KEY_MARGIN = 2.dp
    private var keyboardWidth = 450
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
        mainContainer.setPadding(10, 10, 10, 10)
        
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
        
        // FIX: Use scalable key height
        val dynamicHeight = (BASE_KEY_HEIGHT * scaleFactor).toInt().dp
        
        for (k in keys) {
            val btn = TextView(context)
            val label = if (!isSymbols && isShifted && k.label.length == 1) k.label.uppercase() else k.label
            
            btn.text = label
            btn.setTextColor(Color.WHITE)
            btn.textSize = 14f * scaleFactor // Scale font too
            btn.gravity = Gravity.CENTER
            btn.typeface = Typeface.DEFAULT_BOLD
            
            val keyBg = GradientDrawable()
            keyBg.setColor(if (k.isSpecial) Color.parseColor("#444444") else Color.parseColor("#2A2A2A"))
            keyBg.cornerRadius = 10f
            btn.background = keyBg
            
            val params = LinearLayout.LayoutParams(0, dynamicHeight)
            params.weight = k.weight
            params.setMargins(KEY_MARGIN, ROW_MARGIN, KEY_MARGIN, ROW_MARGIN)
            row.addView(btn, params)
            
            btn.setOnClickListener {
                handleKeyPress(k)
                btn.alpha = 0.5f
                btn.postDelayed({ btn.alpha = 1.0f }, 50)
            }
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

    // --- NEW: Calculate exact content height ---
    fun getContentHeight(scale: Float): Int {
        val rows = 5 // Fixed rows
        val rowHeight = (BASE_KEY_HEIGHT * scale).toInt().dp
        val verticalPadding = 20 + (rows * ROW_MARGIN * 2) // Container padding + row margins
        return (rows * rowHeight) + verticalPadding
    }
    
    fun setScale(scale: Float) {
        this.scaleFactor = scale.coerceIn(0.5f, 2.0f)
        refreshLayout()
    }

    fun show(width: Int, height: Int) {
        if (isVisible) return
        keyboardWidth = width
        keyboardHeight = height

        layoutParams = WindowManager.LayoutParams(
            keyboardWidth,
            WindowManager.LayoutParams.WRAP_CONTENT, 
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or 
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        )
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        layoutParams?.y = 50 

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

    // --- Helpers ---
    private val Int.dp: Int get() = (this * context.resources.displayMetrics.density).toInt()
    
    // Pass events up to Overlay for global handling, or handle local logic?
    // Since OverlayService manages window size, we delegate resize logic back to it via listeners if needed.
    // But this class is for standalone activity usage mostly. OverlayService uses KeyboardOverlay.kt wrapper.
    // We will leave local handlers for non-service usage but Overlay wrapper overrides them.
    
    private var initialX = 0
    private var initialTouchX = 0f
    
    // Local resize logic (if used standalone)
    private fun handleResize(event: MotionEvent): Boolean {
        return false // Handled by Overlay wrapper usually
    }

    private fun handleMove(event: MotionEvent): Boolean {
        return false // Handled by Overlay wrapper usually
    }
}
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardView.kt
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

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface KeyboardListener {
        fun onKeyPress(keyCode: Int, char: Char?, metaState: Int)
        fun onTextInput(text: String)
        fun onSpecialKey(key: SpecialKey, metaState: Int)
        fun onScreenToggle()
        fun onScreenModeChange()
    }

    enum class SpecialKey {
        BACKSPACE, ENTER, SPACE, SHIFT, CAPS_LOCK, SYMBOLS, ABC,
        TAB, ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT,
        HOME, END, DELETE, ESCAPE, CTRL, ALT,
        VOL_UP, VOL_DOWN, MUTE, BACK_NAV, FWD_NAV, VOICE_INPUT
    }

    enum class KeyboardState {
        LOWERCASE, UPPERCASE, CAPS_LOCK, SYMBOLS_1, SYMBOLS_2
    }

    private var listener: KeyboardListener? = null
    private var currentState = KeyboardState.LOWERCASE
    private var vibrationEnabled = true
    
    private var isCtrlActive = false
    private var isAltActive = false
    
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
    private val navRow = listOf("SCREEN", "MUTE", "VOL-", "VOL+", "BACK", "FWD", "MIC")

    // Keys allowed to repeat when held
    private val alwaysRepeatable = setOf(
        "BKSP", "SPACE", "←", "→", "↑", "↓", "VOL+", "VOL-", "FWD", "BACK", "DELETE"
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

    private fun buildKeyboard() {
        removeAllViews()
        
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val index = event.actionIndex
        val id = event.getPointerId(index)
        
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val x = event.getX(index)
                val y = event.getY(index)
                val keyView = findKeyView(x, y)
                if (keyView != null) {
                    val keyTag = keyView.tag as? String
                    if (keyTag != null) {
                        activePointers.put(id, keyView)
                        onKeyDown(keyTag, keyView)
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val keyView = activePointers.get(id)
                if (keyView != null) {
                    val keyTag = keyView.tag as? String
                    if (keyTag != null) onKeyUp(keyTag, keyView)
                    activePointers.remove(id)
                }
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                stopRepeat()
                for (i in 0 until activePointers.size()) {
                    val view = activePointers.valueAt(i)
                    val tag = view.tag as? String
                    if (tag != null) setKeyVisual(view, false, tag)
                }
                activePointers.clear()
            }
        }
        return super.onTouchEvent(event)
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

    private fun onKeyDown(key: String, view: View) {
        setKeyVisual(view, true, key)
        if (vibrationEnabled) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else { @Suppress("DEPRECATION") v?.vibrate(30) }
        }
        handleKeyPress(key, fromRepeat = false)
        if (isKeyRepeatable(key)) {
            currentRepeatKey = key
            isRepeating = true
            repeatHandler.postDelayed(repeatRunnable, REPEAT_INITIAL_DELAY)
        }
        if (key == "SHIFT") {
            capsLockPending = false
            capsHandler.postDelayed(capsLockRunnable, 500)
        }
    }

    private fun onKeyUp(key: String, view: View) {
        setKeyVisual(view, false, key)
        if (key == currentRepeatKey) stopRepeat()
        if (key == "SHIFT") {
            capsHandler.removeCallbacks(capsLockRunnable)
            if (!capsLockPending) toggleShift()
            capsLockPending = false
        }
    }

    private fun setKeyVisual(container: View, pressed: Boolean, key: String) {
        val tv = (container as? ViewGroup)?.getChildAt(0) as? TextView ?: return
        val bg = tv.background as? GradientDrawable ?: return
        if (pressed) bg.setColor(Color.parseColor("#3DDC84")) else bg.setColor(getKeyColor(key))
    }

    private fun stopRepeat() {
        isRepeating = false
        currentRepeatKey = null
        repeatHandler.removeCallbacks(repeatRunnable)
    }

    private fun isKeyRepeatable(key: String): Boolean {
        if (alwaysRepeatable.contains(key)) return true
        if (key.length == 1) return true
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
        else -> key
    }

    private fun getKeyColor(key: String): Int {
        if (key == "CTRL" && isCtrlActive) return Color.parseColor("#3DDC84")
        if (key == "ALT" && isAltActive) return Color.parseColor("#3DDC84")
        
        // Special color for Screen/Mode Key
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
        stopRepeat()
    }
}
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadMenuAdapter.kt
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

## File: Cover-Screen-Launcher/app/src/main/aidl/com/example/quadrantlauncher/IShellService.aidl
```
package com.example.quadrantlauncher;

interface IShellService {
    void forceStop(String packageName);
    void runCommand(String command);
    void setScreenOff(int displayIndex, boolean turnOff);
    void repositionTask(String packageName, int left, int top, int right, int bottom);
    List<String> getVisiblePackages(int displayId);
    List<String> getWindowLayouts(int displayId);
    List<String> getAllRunningPackages();
    int getTaskId(String packageName);
    void moveTaskToBack(int taskId);

    // Brightness Control
    void setSystemBrightness(int brightness);
    int getSystemBrightness();
    float getSystemBrightnessFloat();
    void setAutoBrightness(boolean enabled);
    boolean isAutoBrightness();
    
    // Legacy / Direct Hardware Control
    boolean setBrightnessViaDisplayManager(int displayId, float brightness);

    // NEW: Alternate Display Off Logic (Targeted)
    void setBrightness(int displayId, int value);
}
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ShellUserService.kt
```kotlin
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
```

## File: Cover-Screen-Trackpad/app/src/main/res/layout/layout_trackpad_drawer.xml
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

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/MainActivity.kt
```kotlin
package com.example.coverscreentester

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Display
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. SETUP UI ONLY. NO AUTOMATIC CHECKS.
        setupUI()

        // 2. Add Listener safely (optional)
        try {
            Shizuku.addBinderReceivedListener {
                runOnUiThread {
                    Toast.makeText(this, "Shizuku Connected!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Throwable) {
            // Ignore
        }
    }

    private fun setupUI() {
        // Button 1: App Info
        findViewById<Button>(R.id.btn_fix_restricted).setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                Toast.makeText(this, "Tap 3 dots (top-right) -> Allow Restricted Settings", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error opening App Info", Toast.LENGTH_SHORT).show()
            }
        }

        // Button 2: Accessibility
        findViewById<Button>(R.id.btn_open_accessibility).setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Error opening Accessibility", Toast.LENGTH_SHORT).show()
            }
        }

        // Button 3: Check & Start (The Safe Way)
        findViewById<Button>(R.id.btn_start_check).setOnClickListener {
            manualStartCheck()
        }
    }

    private fun manualStartCheck() {
        // 1. Check Overlay Permission
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "⚠️ Please Grant 'Display Over Apps'", Toast.LENGTH_LONG).show()
            try {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
            } catch(e: Exception) {}
            return
        }

        // 2. Check Shizuku (TRY-CATCH IS MANDATORY)
        try {
            if (!Shizuku.pingBinder()) {
                Toast.makeText(this, "⚠️ Shizuku Not Running!", Toast.LENGTH_SHORT).show()
                // Don't block launch, just warn
            } else if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(0)
                return
            }
        } catch (e: Throwable) {
            Toast.makeText(this, "⚠️ Shizuku Error: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // 3. Start Service
        startOverlayService()
        finish()
    }

    private fun startOverlayService() {
        var targetDisplayId = Display.DEFAULT_DISPLAY
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display?.let { targetDisplayId = it.displayId }
            } else {
                @Suppress("DEPRECATION")
                targetDisplayId = windowManager.defaultDisplay.displayId
            }
        } catch (e: Exception) {}

        val intent = Intent(this, OverlayService::class.java).apply {
            action = "OPEN_MENU"
            putExtra("DISPLAY_ID", targetDisplayId)
            putExtra("FORCE_MOVE", true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
```

## File: README.md
```markdown
DroidOS 📱🚀
<br>
<br>

---

DroidOS is a suite of advanced system tools designed to give "superpowers" to the standard Android experience.
It functions as a universal Samsung DeX replacement, a tiling window manager, and an unrestricted app launcher that works on any Android device. Whether you are using a Foldable, a Flip phone, AR Glasses, or a secondary monitor, DroidOS unlocks the full potential of your hardware.

![Screenshot_20251130_185618_Discord](https://github.com/user-attachments/assets/dca14a11-21e6-439c-b653-7ce9f8e73a87)

Video Demonstration : https://youtu.be/aOzkV3t7wFM

<br>
<br>

---

✨ Core Features

🖥️ Universal Desktop Mode (DeX Replacement)
Unlike proprietary solutions locked to specific brands, DroidOS provides a desktop-class experience on any Android phone:
 * Window Management: Force apps into specific tiling layouts (Split-screen, Quadrants, Tri-split) on external displays.
 * Input Control: Turn your phone screen into a fully functional trackpad and keyboard while viewing content on a larger screen.
🕶️ AR Glasses & Virtual Screens
Optimized for users of XREAL, Rokid, Viture, and other AR glasses:
 * "Headless" Mode: Turn off your phone's physical screen to save battery and reduce heat while the system continues running on the glasses.
 * Blind Navigation: The Trackpad module allows you to control the AR interface without looking at your phone.
📱 Foldable & Cover Screen Enhancements
Unleash the full power of your Galaxy Z Flip, Fold, or other foldable devices:
 * Unrestricted Launching: Launch any app on the cover screen, bypassing system "Good Lock" allowlists.
 * Orientation Control: Force landscape or portrait orientations on screens that don't natively support them.
🛠️ How It Works
DroidOS utilizes Shizuku to access elevated system APIs without requiring root access. This allows it to:
 * Inject raw input events (Mouse/Keyboard) directly into the system input stream.
 * Manage window sizes, positions, and display power states via hidden Android APIs (Reflection).
 * Launch activities on specific display IDs (Cover screens, Virtual displays).

<br>
<br>

---

🚀 Getting Started

Prerequisites
 * Shizuku: Must be installed and running on your device.
 * Developer Options: "Force activities to be resizable" and "Enable freeform windows" must be enabled.

Installation
You can download the latest APKs for both modules from the Releases page.
 * Install DroidOS Launcher to manage your apps and windows.
 * Install DroidOS Trackpad Keyboard to control the cursor.
 * Grant Shizuku permissions when prompted in each app.
 * I reccomend this fork of shizuku https://github.com/thedjchi/Shizuku once you set it up. It has a watchdog feature to autorestart whenever it gets turned off. Once you turn it on, even if you lose wireless adb you can still turn shizuku back on without it. Also has an auto start on boot feature. Does not require root.
 * Grant accessibility permissions to the trackpad when prompted.

 * DroidOS Launcher can be exited by swiping the bubble icon away.

<br>
<br>

---

🤝 Contributing

We welcome contributions! 

Please note that this is a Monorepo.
 * If you are fixing a bug in the Launcher, make your Pull Request against the Cover-Screen-Launcher directory.
 * If you are improving the Trackpad, work within the Cover-Screen-Trackpad directory.

<br>
<br>

---

🚀 DroidOS Launcher Usage Guide

The DroidOS Launcher is designed to manage multi-window tiling and control display resolutions, primarily using Shizuku for elevated permissions.
1. The Two Operational Modes
The Launcher operates primarily using an app queue combined with your selected window layout. The core difference lies in how aggressively the launcher manages apps after initialization.
>
| Mode | Key Feature | Execution Action | Ideal For |
|---|---|---|---|
| Instant Mode | Live, dynamic window management. | Windows are launched/resized automatically every time you adjust the queue (add/remove/hide apps). The Green Play/Execute button is hidden. | Quick adjustments, experimental resizing, or when fine-tuning a small layout. |
| Launcher Mode | Traditional "batch" execution. | Changes to the queue or layout only take effect when you explicitly press the Green Play/Execute button. | Large, complex setups (3+ apps) where manual timing is better, or minimizing system resource drain. |
> Switch Mode: Go to the Settings tab (Gear Icon) and toggle "Instant Mode (Live Changes)".
> 
2. Managing the App Queue (The Dock)
The App Queue (the horizontal list of icons at the top of the main drawer) determines which apps are launched and where they are placed in your chosen layout.
>
| Action | How To | Result |
|---|---|---|
| Adding an App | 1. Navigate to the Search tab. 2. Tap an app listed in the main recycler view. | The app is added to the right end of the App Queue. If in Instant Mode, the layout is applied immediately. |
| Adding a Spacer | Tap "(Blank Space)" in the search list. | Inserts a blank placeholder into the queue. This ensures an empty tile space in your final layout (e.g., in a 4-Quadrant layout, you can use 2 apps and 2 blanks). |
| Reordering/Moving | Drag and drop an app icon horizontally within the queue. | Changes the app's position in the queue, which dictates its screen placement (Tiling order). |
| Toggling Hide/Minimize | Tap an app icon in the App Queue. | The app's icon turns slightly transparent (minimized). The app is moved to the background using its Task ID. The app is skipped during subsequent tiling calculations. |
| Closing/Killing App | Swipe the app icon up or down in the App Queue. | The app is removed from the queue and a force-stop shell command is executed to kill the app. |
| Favoriting (Global) | Long-press an app in the main search list or swipe the app left/right in the search list. | Toggles the star icon and adds/removes the app from your global favorites list. |
3. Tiling Position & Order
Tiling positions are determined strictly from left-to-right in the App Queue to top-to-bottom, left-to-right in the selected screen layout.
 * The leftmost app in your queue corresponds to the first defined window tile in your layout.
 * The second app corresponds to the second tile, and so on.
Example: 4-Quadrant Layout
 * Tile 1 (Top-Left): Corresponds to the 1st app in the queue.
 * Tile 2 (Top-Right): Corresponds to the 2nd app in the queue.
 * Tile 3 (Bottom-Left): Corresponds to the 3rd app in the queue.
 * Tile 4 (Bottom-Right): Corresponds to the 4th app in the queue.
You can ensure an app lands in a specific tile by dragging it to the corresponding position in the App Queue.

<br>
<br>

---

🕶️ DroidOS Virtual Display Setup

This guide outlines the steps to activate and control a virtual, tiled desktop environment when using AR glasses (such as XREAL) connected to your Android device. This process relies on having Shizuku running with permissions granted to both the CoverScreen Launcher and CoverScreen Trackpad applications.

Part 1: Create and Switch to Virtual Display (Launcher App)
The goal of this phase is to create a new virtual screen and move the Launcher's target focus from your physical phone screen to that new screen.
 * Set Initial Resolution (Optional but Recommended):
   * Open the CoverScreen Launcher floating bubble.
   * Navigate to the Resolution Mode (Icon: Rectangle with dots) tab.
   * Select "Default (Reset)".
   * Note: The developer often sets a custom resolution (like 1080p) here to ensure the display from the glasses (e.g., Beam Pro) is usable, as the physical screen itself may be too tiny for the mirrored content.
 * Enable Virtual Display:
   * Navigate to the Settings Mode (Icon: Gear/Preferences) tab.
   * Toggle the "Virtual Display (1080p)" option.
 * Switch Launch Target:
   * Press the "Switch Display (Current [ID])" button immediately below the Virtual Display toggle.
   * Result: Your physical phone screen should become mostly blank, showing only the Launcher bubble (or the main screen if still open). The virtual screen on the glasses should now show the output.

Part 2: Gaining Cursor Control (Trackpad App)
Now that the system is outputting to the glasses, you must redirect your phone's touch input to control the cursor on the remote screen.
 * Launch Trackpad App:
   * Open the CoverScreen Trackpad application on your physical phone screen.
   * Note: The trackpad functions as an overlay on your physical phone screen, allowing you to use your phone's surface to control the larger remote display.
 * Redirect Input:
   * Press the "Target: Switch Local/Remote" button.
   * Result: You should now see a cursor moving on the glasses display corresponding to your touch input on the phone.
 * Activate Headless/Extinguish Mode (Optional):
   * Return to the CoverScreen Launcher (Settings Tab).
   * Toggle "Display Off (Touch on)" to turn off the physical screen entirely.
🖱️ Trackpad Overlay Controls
The Trackpad overlay provides dedicated controls accessible through its corners and edges:
| Control Point | Action | Result | Source |
|---|---|---|---|
| Top-Right Corner Handle | Drag finger | Moves (repositions) the trackpad overlay on the physical screen. |  |
| Bottom-Right Corner Handle | Drag finger | Resizes the trackpad overlay. |  |
| Bottom-Left Corner | Tap (Click) | Opens the manual adjust menu, allowing you to resize and reposition using a controller instead of dragging. |  |
| Edges (Top/Bottom) | Finger near edge + Move Up/Down | Performs Vertical Scrolling in the remote screen. |  |
| Edges (Left/Right) | Finger near edge + Move Left/Right | Performs Horizontal Scrolling in the remote screen. |  |
Scrolling Note: Ensure the trackpad overlay is not positioned too close to the edges of your phone's physical screen for the scrolling zones to work reliably.

<br>
<br>

---

Want to donate to support the development of this project? https://ko-fi.com/katsuyamaki

📂 Project Structure (Monorepo)
This repository is a Monorepo containing two distinct but complementary Android applications.
> ⚠️ Developer Note: Do not open this root folder directly in Android Studio. You must open each project folder individually.
> 
| Project | Description | Path |
|---|---|---|
| DroidOS Launcher | An advanced tiling window manager and app launcher. Bypasses cover screen restrictions and manages multi-window layouts. | /Cover-Screen-Launcher |
| DroidOS Trackpad Keyboard | A virtual mouse trackpad and custom keyboard overlay. Turns your phone into a precision input device for external displays. | /Cover-Screen-Trackpad |


📄 License
This project is licensed under the GNU General Public License v3.0 (GPLv3).
You are free to use, modify, and distribute this software, but all modifications must remain open source. See the LICENSE file for details.

<br>
<br>

---

![Screenshot_20251130_125934_Reddit](https://github.com/user-attachments/assets/a4644964-8371-4f39-9a03-df88e4a8524a)

![Screenshot_20251130_185403_Discord](https://github.com/user-attachments/assets/6a876a1e-67c5-4968-84ff-2ed36411c54a)

![Screenshot_20251130_185026_Discord](https://github.com/user-attachments/assets/c89110b9-0a0b-47ed-83e9-9d54a150beae)

![Screenshot_20251130_130005_Reddit](https://github.com/user-attachments/assets/24ceaf2f-5212-4fe6-b027-e5941164ca93)

![Screenshot_20251130_125958_Reddit](https://github.com/user-attachments/assets/3809df01-7d7a-4383-b5f3-f9aa85998685)

![Screenshot_20251130_125940_Reddit](https://github.com/user-attachments/assets/fd1ab37f-3158-4b01-a342-9c5f82423b89)

![Screenshot_20251130_125922_Reddit](https://github.com/user-attachments/assets/2af9a4f6-8dd2-48da-9551-943845a28613)

![Screenshot_20251130_125807_One UI Cover Home](https://github.com/user-attachments/assets/eb08e879-6c55-45f6-a175-a19791588337)

<br>
<br>

---
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
```kotlin
package com.example.quadrantlauncher

import android.app.ActivityManager
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
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rikka.shizuku.Shizuku
import java.text.SimpleDateFormat
import java.util.*
import java.lang.reflect.Method
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.hypot
import kotlin.math.min

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
        const val LAYOUT_TOP_BOTTOM = 5
        const val LAYOUT_TRI_EVEN = 3
        const val LAYOUT_CORNERS = 4
        const val LAYOUT_TRI_SIDE_MAIN_SIDE = 6
        const val LAYOUT_QUAD_ROW_EVEN = 7
        const val LAYOUT_CUSTOM_DYNAMIC = 99

        const val CHANNEL_ID = "OverlayServiceChannel"
        const val TAG = "FloatingService"
        const val DEBUG_TAG = "DROIDOS_DEBUG"
        const val ACTION_OPEN_DRAWER = "com.example.quadrantlauncher.OPEN_DRAWER"
        const val ACTION_UPDATE_ICON = "com.example.quadrantlauncher.UPDATE_ICON"
        const val HIGHLIGHT_COLOR = 0xFF00A0E9.toInt()
    }

    private lateinit var windowManager: WindowManager
    private var displayContext: Context? = null
    private var currentDisplayId = 0
    private var lastPhysicalDisplayId = Display.DEFAULT_DISPLAY 

    private var bubbleView: View? = null
    private var drawerView: View? = null
    private var debugStatusView: TextView? = null
    
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
    private var isScreenOffState = false
    private var isInstantMode = true 
    private var showShizukuWarning = true 
    private var useAltScreenOff = false
    
    private var isVirtualDisplayActive = false
    private var currentDrawerHeightPercent = 70
    private var currentDrawerWidthPercent = 90
    private var autoResizeEnabled = true
    
    private var reorderSelectionIndex = -1
    private var isReorderDragEnabled = true
    private var isReorderTapEnabled = true
    
    private val PACKAGE_BLANK = "internal.blank.spacer"
    private val PACKAGE_TRACKPAD = "com.katsuyamaki.DroidOSTrackpadKeyboard"
    
    private var shellService: IShellService? = null
    private var isBound = false
    private val uiHandler = Handler(Looper.getMainLooper())

    private val shizukuBinderListener = Shizuku.OnBinderReceivedListener { if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() }
    private val shizukuPermissionListener = Shizuku.OnRequestPermissionResultListener { _, grantResult -> if (grantResult == PackageManager.PERMISSION_GRANTED) bindShizuku() }

    private val commandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == ACTION_OPEN_DRAWER) { 
                if (isScreenOffState) wakeUp() else if (!isExpanded) toggleDrawer() 
            } 
            else if (action == ACTION_UPDATE_ICON) { 
                updateBubbleIcon()
                if (currentMode == MODE_SETTINGS) switchMode(MODE_SETTINGS) 
            }
            else if (action == Intent.ACTION_SCREEN_ON) {
                if (isScreenOffState) {
                    wakeUp()
                }
            }
        }
    }
    private val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun getMovementFlags(r: RecyclerView, v: RecyclerView.ViewHolder): Int {
            val pos = v.adapterPosition; if (pos == RecyclerView.NO_POSITION || pos >= displayList.size) return 0
            val item = displayList[pos]
            val isSwipeable = when (currentMode) {
                MODE_LAYOUTS -> (item is LayoutOption && item.type == LAYOUT_CUSTOM_DYNAMIC && item.isCustomSaved)
                MODE_RESOLUTION -> (item is ResolutionOption && item.index >= 100)
                MODE_PROFILES -> (item is ProfileOption && !item.isCurrent)
                MODE_SEARCH -> true
                else -> false
            }
            return if (isSwipeable) makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) else 0
        }
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
        override fun onSwiped(v: RecyclerView.ViewHolder, direction: Int) {
            val pos = v.adapterPosition; if (pos == RecyclerView.NO_POSITION) return
            dismissKeyboardAndRestore()
            if (currentMode == MODE_PROFILES) { val item = displayList.getOrNull(pos) as? ProfileOption ?: return; AppPreferences.deleteProfile(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_PROFILES); return }
            if (currentMode == MODE_LAYOUTS) { val item = displayList.getOrNull(pos) as? LayoutOption ?: return; AppPreferences.deleteCustomLayout(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_LAYOUTS); return }
            if (currentMode == MODE_RESOLUTION) { val item = displayList.getOrNull(pos) as? ResolutionOption ?: return; AppPreferences.deleteCustomResolution(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_RESOLUTION); return }
            if (currentMode == MODE_SEARCH) { val item = displayList.getOrNull(pos) as? MainActivity.AppInfo ?: return; if (item.packageName == PACKAGE_BLANK) { (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos); return }; if (direction == ItemTouchHelper.LEFT && !item.isFavorite) toggleFavorite(item) else if (direction == ItemTouchHelper.RIGHT && item.isFavorite) toggleFavorite(item); refreshSearchList() }
        }
    }

    private val selectedAppsDragCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
        override fun isLongPressDragEnabled(): Boolean = isReorderDragEnabled
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean { Collections.swap(selectedAppsQueue, v.adapterPosition, t.adapterPosition); r.adapter?.notifyItemMoved(v.adapterPosition, t.adapterPosition); return true }
        override fun onSwiped(v: RecyclerView.ViewHolder, direction: Int) { dismissKeyboardAndRestore(); val pos = v.adapterPosition; if (pos != RecyclerView.NO_POSITION) { val app = selectedAppsQueue[pos]; if (app.packageName != PACKAGE_BLANK) { Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start(); safeToast("Killed ${app.label}") }; selectedAppsQueue.removeAt(pos); if (reorderSelectionIndex != -1) endReorderMode(false); updateSelectedAppsDock(); drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() } }
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) { super.clearView(recyclerView, viewHolder); val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveLastQueue(this@FloatingLauncherService, pkgs); if (isInstantMode) applyLayoutImmediate() }
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) { shellService = IShellService.Stub.asInterface(binder); isBound = true; updateExecuteButtonColor(true); updateBubbleIcon(); safeToast("Shizuku Connected") }
        override fun onServiceDisconnected(name: ComponentName?) { shellService = null; isBound = false; updateExecuteButtonColor(false); updateBubbleIcon() }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        try { Shizuku.addBinderReceivedListener(shizukuBinderListener); Shizuku.addRequestPermissionResultListener(shizukuPermissionListener) } catch (e: Exception) {}
        val filter = IntentFilter().apply { 
            addAction(ACTION_OPEN_DRAWER)
            addAction(ACTION_UPDATE_ICON)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(commandReceiver, filter, Context.RECEIVER_EXPORTED) else registerReceiver(commandReceiver, filter)
        try { if (rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() } catch (e: Exception) {}
        
        loadInstalledApps(); currentFontSize = AppPreferences.getFontSize(this)
        killAppOnExecute = AppPreferences.getKillOnExecute(this); targetDisplayIndex = AppPreferences.getTargetDisplayIndex(this)
        isInstantMode = AppPreferences.getInstantMode(this); showShizukuWarning = AppPreferences.getShowShizukuWarning(this)
        useAltScreenOff = AppPreferences.getUseAltScreenOff(this); isReorderDragEnabled = AppPreferences.getReorderDrag(this)
        isReorderTapEnabled = AppPreferences.getReorderTap(this); currentDrawerHeightPercent = AppPreferences.getDrawerHeightPercent(this)
        currentDrawerWidthPercent = AppPreferences.getDrawerWidthPercent(this); autoResizeEnabled = AppPreferences.getAutoResizeKeyboard(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val targetDisplayId = intent?.getIntExtra("DISPLAY_ID", Display.DEFAULT_DISPLAY) ?: Display.DEFAULT_DISPLAY
        if (bubbleView != null && targetDisplayId != currentDisplayId) { try { windowManager.removeView(bubbleView); if (isExpanded) windowManager.removeView(drawerView) } catch (e: Exception) {}; setupDisplayContext(targetDisplayId); setupBubble(); setupDrawer(); updateBubbleIcon(); loadDisplaySettings(currentDisplayId); isExpanded = false; safeToast("Moved to Display $targetDisplayId") } 
        else if (bubbleView == null) { try { setupDisplayContext(targetDisplayId); setupBubble(); setupDrawer(); selectedLayoutType = AppPreferences.getLastLayout(this); activeCustomLayoutName = AppPreferences.getLastCustomLayoutName(this); updateGlobalFontSize(); updateBubbleIcon(); loadDisplaySettings(currentDisplayId); if (selectedLayoutType == LAYOUT_CUSTOM_DYNAMIC && activeCustomLayoutName != null) { val data = AppPreferences.getCustomLayoutData(this, activeCustomLayoutName!!); if (data != null) { val rects = mutableListOf<Rect>(); val rectParts = data.split("|"); for (rp in rectParts) { val coords = rp.split(","); if (coords.size == 4) rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt())) }; activeCustomRects = rects } }; try { if (shellService == null && rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() } catch (e: Exception) {} } catch (e: Exception) { stopSelf() } }
        return START_NOT_STICKY
    }
    
    private fun loadDisplaySettings(displayId: Int) { selectedResolutionIndex = AppPreferences.getDisplayResolution(this, displayId); currentDpiSetting = AppPreferences.getDisplayDpi(this, displayId) }

    override fun onDestroy() {
        super.onDestroy()
        isScreenOffState = false
        wakeUp()
        try { Shizuku.removeBinderReceivedListener(shizukuBinderListener); Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener); unregisterReceiver(commandReceiver) } catch (e: Exception) {}
        try { if (bubbleView != null) windowManager.removeView(bubbleView); if (isExpanded) windowManager.removeView(drawerView) } catch (e: Exception) {}
        if (isBound) { try { ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection); isBound = false } catch (e: Exception) {} }
    }
    
    private fun safeToast(msg: String) { 
        uiHandler.post { 
            try { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() } catch(e: Exception) { }
            if (debugStatusView != null) debugStatusView?.text = msg 
        }
    }
    
    private fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= 31) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(50)
            }
        } catch (e: Exception) {}
    }

    private fun setupDisplayContext(displayId: Int) {
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val display = displayManager.getDisplay(displayId)
        if (display == null) { windowManager = getSystemService(WINDOW_SERVICE) as WindowManager; return }
        currentDisplayId = displayId; displayContext = createDisplayContext(display); windowManager = displayContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private fun refreshDisplayId() { val id = displayContext?.display?.displayId ?: Display.DEFAULT_DISPLAY; currentDisplayId = id }
    private fun startForegroundService() { val channelId = if (android.os.Build.VERSION.SDK_INT >= 26) { val channel = android.app.NotificationChannel(CHANNEL_ID, "Floating Launcher", android.app.NotificationManager.IMPORTANCE_LOW); getSystemService(android.app.NotificationManager::class.java).createNotificationChannel(channel); CHANNEL_ID } else ""; val notification = NotificationCompat.Builder(this, channelId).setContentTitle("CoverScreen Launcher Active").setSmallIcon(R.drawable.ic_launcher_bubble).setPriority(NotificationCompat.PRIORITY_MIN).build(); if (android.os.Build.VERSION.SDK_INT >= 34) startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(1, notification) }
    private fun bindShizuku() { try { val component = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(component, userServiceConnection, true, 1) } catch (e: Exception) { Log.e(TAG, "Bind Shizuku Failed", e) } }
    private fun updateExecuteButtonColor(isReady: Boolean) { uiHandler.post { val executeBtn = drawerView?.findViewById<ImageView>(R.id.icon_execute); if (isReady) executeBtn?.setColorFilter(Color.GREEN) else executeBtn?.setColorFilter(Color.RED) } }

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
                if (velocityTracker == null) velocityTracker = VelocityTracker.obtain(); velocityTracker?.addMovement(event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { initialX = bubbleParams.x; initialY = bubbleParams.y; initialTouchX = event.rawX; initialTouchY = event.rawY; isDrag = false; return true }
                    MotionEvent.ACTION_MOVE -> { if (Math.abs(event.rawX - initialTouchX) > 10 || Math.abs(event.rawY - initialTouchY) > 10) isDrag = true; if (isDrag) { bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt(); bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt(); windowManager.updateViewLayout(bubbleView, bubbleParams) }; return true }
                    MotionEvent.ACTION_UP -> { velocityTracker?.computeCurrentVelocity(1000); val vX = velocityTracker?.xVelocity ?: 0f; val vY = velocityTracker?.yVelocity ?: 0f; val totalVel = hypot(vX.toDouble(), vY.toDouble()); if (isDrag && totalVel > 2500) { safeToast("Closing..."); stopSelf(); return true }; if (!isDrag) { if (!isBound && showShizukuWarning) { if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) { bindShizuku() } else { safeToast("Shizuku NOT Connected. Opening Shizuku..."); launchShizuku() } } else { toggleDrawer() } }; velocityTracker?.recycle(); velocityTracker = null; return true }
                    MotionEvent.ACTION_CANCEL -> { velocityTracker?.recycle(); velocityTracker = null }
                }
                return false
            }
        })
        windowManager.addView(bubbleView, bubbleParams)
    }
    
    private fun launchShizuku() { try { val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api"); if (intent != null) { intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(intent) } else { safeToast("Shizuku app not found") } } catch(e: Exception) { safeToast("Failed to launch Shizuku") } }
    private fun updateBubbleIcon() { val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon) ?: return; if (!isBound && showShizukuWarning) { uiHandler.post { iconView.setImageResource(android.R.drawable.ic_dialog_alert); iconView.setColorFilter(Color.RED); iconView.imageTintList = null }; return }; uiHandler.post { try { val uriStr = AppPreferences.getIconUri(this); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); if (bitmap != null) { iconView.setImageBitmap(bitmap); iconView.imageTintList = null; iconView.clearColorFilter() } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } catch (e: Exception) { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } }
    private fun dismissKeyboardAndRestore() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); if (searchBar != null && searchBar.hasFocus()) { searchBar.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(searchBar.windowToken, 0) }; val dpiInput = drawerView?.findViewById<EditText>(R.id.input_dpi_value); if (dpiInput != null && dpiInput.hasFocus()) { dpiInput.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(dpiInput.windowToken, 0) }; updateDrawerHeight(false) }

    private fun setupDrawer() {
        val context = displayContext ?: this
        val themeContext = ContextThemeWrapper(context, R.style.Theme_QuadrantLauncher)
        drawerView = LayoutInflater.from(themeContext).inflate(R.layout.layout_rofi_drawer, null)
        drawerView!!.fitsSystemWindows = true 
        drawerParams = WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT)
        drawerParams.gravity = Gravity.TOP or Gravity.START; drawerParams.x = 0; drawerParams.y = 0
        drawerParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container)
        if (container != null) { 
            val lp = container.layoutParams as? FrameLayout.LayoutParams
            if (lp != null) { lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; lp.topMargin = 100; container.layoutParams = lp }
            
            debugStatusView = TextView(context)
            debugStatusView?.text = "Ready"
            debugStatusView?.setTextColor(Color.GREEN)
            debugStatusView?.textSize = 10f
            debugStatusView?.gravity = Gravity.CENTER
            container.addView(debugStatusView, 0)
        }

        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val mainRecycler = drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view); val selectedRecycler = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
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
        searchBar.setOnKeyListener { _, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) { if (searchBar.text.isEmpty() && selectedAppsQueue.isNotEmpty()) { val lastIndex = selectedAppsQueue.size - 1; selectedAppsQueue.removeAt(lastIndex); updateSelectedAppsDock(); mainRecycler.adapter?.notifyDataSetChanged(); return@setOnKeyListener true } }; if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { if (searchBar.hasFocus()) { dismissKeyboardAndRestore(); return@setOnKeyListener true } }; return@setOnKeyListener false }
        searchBar.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) { updateDrawerHeight(hasFocus) } }
        mainRecycler.layoutManager = LinearLayoutManager(themeContext); mainRecycler.adapter = RofiAdapter(); val itemTouchHelper = ItemTouchHelper(swipeCallback); itemTouchHelper.attachToRecyclerView(mainRecycler)
        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() { override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) { if (newState == RecyclerView.SCROLL_STATE_DRAGGING) { dismissKeyboardAndRestore() } } })
        mainRecycler.setOnTouchListener { v, event -> if (event.action == MotionEvent.ACTION_DOWN) { dismissKeyboardAndRestore() }; false }
        selectedRecycler.layoutManager = LinearLayoutManager(themeContext, LinearLayoutManager.HORIZONTAL, false); selectedRecycler.adapter = SelectedAppsAdapter(); val dockTouchHelper = ItemTouchHelper(selectedAppsDragCallback); dockTouchHelper.attachToRecyclerView(selectedRecycler)
        drawerView!!.setOnClickListener { toggleDrawer() }
        drawerView!!.isFocusableInTouchMode = true
        drawerView!!.setOnKeyListener { _, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { toggleDrawer(); true } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && isScreenOffState) { wakeUp(); true } else false }
    }
    
    private fun startReorderMode(index: Int) { if (!isReorderTapEnabled) return; if (index < 0 || index >= selectedAppsQueue.size) return; val prevIndex = reorderSelectionIndex; reorderSelectionIndex = index; val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; if (prevIndex != -1) adapter?.notifyItemChanged(prevIndex); adapter?.notifyItemChanged(reorderSelectionIndex); safeToast("Tap another app to Swap") }
    private fun swapReorderItem(targetIndex: Int) { if (reorderSelectionIndex == -1) return; Collections.swap(selectedAppsQueue, reorderSelectionIndex, targetIndex); val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; adapter?.notifyItemChanged(reorderSelectionIndex); adapter?.notifyItemChanged(targetIndex); endReorderMode(true) }
    private fun endReorderMode(triggerInstantMode: Boolean) { val prevIndex = reorderSelectionIndex; reorderSelectionIndex = -1; val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; if (prevIndex != -1) adapter?.notifyItemChanged(prevIndex); if (triggerInstantMode && isInstantMode) applyLayoutImmediate() }
    
    private fun updateDrawerHeight(isKeyboardMode: Boolean) {
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container) ?: return
        val dm = DisplayMetrics(); windowManager.defaultDisplay.getRealMetrics(dm); val screenH = dm.heightPixels; val screenW = dm.widthPixels
        val lp = container.layoutParams as? FrameLayout.LayoutParams; val topMargin = lp?.topMargin ?: 100
        var finalHeight = (screenH * (currentDrawerHeightPercent / 100f)).toInt()
        if (isKeyboardMode) { finalHeight = (screenH * 0.40f).toInt(); val maxAvailable = screenH - topMargin - 20; if (finalHeight > maxAvailable) finalHeight = maxAvailable }
        val newW = (screenW * (currentDrawerWidthPercent / 100f)).toInt()
        if (container.layoutParams.height != finalHeight || container.layoutParams.width != newW) { container.layoutParams.width = newW; container.layoutParams.height = finalHeight; container.requestLayout(); if (drawerParams.y != 0) { drawerParams.y = 0; windowManager.updateViewLayout(drawerView, drawerParams) } }
    }

    private fun toggleDrawer() {
        if (isExpanded) { try { windowManager.removeView(drawerView) } catch(e: Exception) {}; bubbleView?.visibility = View.VISIBLE; isExpanded = false } else { setupDisplayContext(currentDisplayId); updateDrawerHeight(false); try { windowManager.addView(drawerView, drawerParams) } catch(e: Exception) {}; bubbleView?.visibility = View.GONE; isExpanded = true; switchMode(MODE_SEARCH); val et = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); et?.setText(""); et?.clearFocus(); updateSelectedAppsDock(); if (isInstantMode) fetchRunningApps() }
    }
    private fun updateGlobalFontSize() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); searchBar?.textSize = currentFontSize; drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() }
    private fun loadInstalledApps() { 
        val pm = packageManager; 
        val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }; 
        val riList = pm.queryIntentActivities(intent, 0); 
        allAppsList.clear(); 
        allAppsList.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)); 
        for (ri in riList) { 
            val pkg = ri.activityInfo.packageName; 
            if (pkg == PACKAGE_TRACKPAD || pkg == packageName) continue; 
            val app = MainActivity.AppInfo(ri.loadLabel(pm).toString(), pkg, AppPreferences.isFavorite(this, pkg)); 
            allAppsList.add(app) 
        }; 
        allAppsList.sortBy { it.label.lowercase() } 
    }
    
    private fun launchTrackpad() {
        if (isTrackpadRunning()) { safeToast("Trackpad is already active"); return }
        try { val intent = packageManager.getLaunchIntentForPackage(PACKAGE_TRACKPAD); if (intent != null) { intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP); val dm = DisplayMetrics(); val display = displayContext?.display ?: windowManager.defaultDisplay; display.getRealMetrics(dm); val w = dm.widthPixels; val h = dm.heightPixels; val targetW = (w * 0.5f).toInt(); val targetH = (h * 0.5f).toInt(); val left = (w - targetW) / 2; val top = (h - targetH) / 2; val bounds = Rect(left, top, left + targetW, top + targetH); val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(bounds); try { val method = android.app.ActivityOptions::class.java.getMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType); method.invoke(options, 5) } catch (e: Exception) {}; startActivity(intent, options.toBundle()); toggleDrawer(); if (shellService != null) { uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(PACKAGE_TRACKPAD, left, top, left+targetW, top+targetH) } catch(e: Exception) { Log.e(TAG, "Shell launch failed", e) } }.start() }, 400) } } else { safeToast("Trackpad App not found") } } catch (e: Exception) { safeToast("Error launching Trackpad") }
    }

    private fun isTrackpadRunning(): Boolean { try { val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager; val runningApps = am.runningAppProcesses; if (runningApps != null) { for (info in runningApps) { if (info.processName == PACKAGE_TRACKPAD) return true } } } catch (e: Exception) {}; return false }
    private fun getLayoutName(type: Int): String { return when(type) { LAYOUT_FULL -> "1 App - Full"; LAYOUT_SIDE_BY_SIDE -> "Split"; LAYOUT_TOP_BOTTOM -> "Top/Bot"; LAYOUT_TRI_EVEN -> "Tri-Split"; LAYOUT_CORNERS -> "Quadrant"; LAYOUT_TRI_SIDE_MAIN_SIDE -> "3 Apps - Side/Main/Side"; LAYOUT_QUAD_ROW_EVEN -> "4 Apps - Row"; LAYOUT_CUSTOM_DYNAMIC -> "Custom"; else -> "Unknown" } }
    private fun getRatioName(index: Int): String { return when(index) { 1 -> "1:1"; 2 -> "16:9"; 3 -> "32:9"; else -> "Default" } }
    private fun getTargetDimensions(index: Int): Pair<Int, Int>? { return when(index) { 1 -> 1422 to 1500; 2 -> 1920 to 1080; 3 -> 3840 to 1080; else -> null } }
    private fun getResolutionCommand(index: Int): String { return when(index) { 1 -> "wm size 1422x1500 -d $currentDisplayId"; 2 -> "wm size 1920x1080 -d $currentDisplayId"; 3 -> "wm size 3840x1080 -d $currentDisplayId"; else -> "wm size reset -d $currentDisplayId" } }
    private fun sortAppQueue() { selectedAppsQueue.sortWith(compareBy { it.isMinimized }) }
    private fun updateSelectedAppsDock() { val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); if (selectedAppsQueue.isEmpty()) { dock.visibility = View.GONE } else { dock.visibility = View.VISIBLE; dock.adapter?.notifyDataSetChanged(); dock.scrollToPosition(selectedAppsQueue.size - 1) } }
    private fun refreshSearchList() { val query = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString() ?: ""; filterList(query) }
    private fun filterList(query: String) {
        if (currentMode != MODE_SEARCH) return; val actualQuery = query.substringAfterLast(",").trim(); displayList.clear()
        val filtered = if (actualQuery.isEmpty()) { allAppsList } else { allAppsList.filter { it.label.contains(actualQuery, ignoreCase = true) } }
        val sorted = filtered.sortedWith(compareBy<MainActivity.AppInfo> { it.packageName != PACKAGE_BLANK }.thenByDescending { it.isFavorite }.thenBy { it.label.lowercase() }); displayList.addAll(sorted); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    private fun addToSelection(app: MainActivity.AppInfo) {
        dismissKeyboardAndRestore(); val et = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar)
        if (app.packageName == PACKAGE_BLANK) { selectedAppsQueue.add(app); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate(); return }
        val existing = selectedAppsQueue.find { it.packageName == app.packageName }; if (existing != null) { selectedAppsQueue.remove(existing); Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start(); safeToast("Removed ${app.label}"); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); et.setText(""); if (isInstantMode) applyLayoutImmediate() } 
        else { app.isMinimized = false; selectedAppsQueue.add(app); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); et.setText(""); if (isInstantMode) { launchViaApi(app.packageName, null); launchViaShell(app.packageName); uiHandler.postDelayed({ applyLayoutImmediate() }, 200); uiHandler.postDelayed({ applyLayoutImmediate() }, 800) } }
    }
    private fun toggleFavorite(app: MainActivity.AppInfo) { val newState = AppPreferences.toggleFavorite(this, app.packageName); app.isFavorite = newState; allAppsList.find { it.packageName == app.packageName }?.isFavorite = newState }
    private fun launchViaApi(pkg: String, bounds: Rect?) { try { val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return; intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP); val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); if (bounds != null) options.setLaunchBounds(bounds); startActivity(intent, options.toBundle()) } catch (e: Exception) {} }
    private fun launchViaShell(pkg: String) { try { val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return; if (shellService != null) { val component = intent.component?.flattenToShortString() ?: pkg; val cmd = "am start -n $component -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"; Thread { shellService?.runCommand(cmd) }.start() } } catch (e: Exception) {} }
    
    private fun cycleDisplay() {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val displays = dm.displays
        if (isVirtualDisplayActive) { val virtualDisp = displays.firstOrNull { it.displayId >= 2 }; if (virtualDisp != null) { val targetId = if (currentDisplayId == virtualDisp.displayId) { if (displays.any { it.displayId == lastPhysicalDisplayId }) lastPhysicalDisplayId else Display.DEFAULT_DISPLAY } else { lastPhysicalDisplayId = currentDisplayId; virtualDisp.displayId }; performDisplayChange(targetId); return } }
        val currentIdx = displays.indexOfFirst { it.displayId == currentDisplayId }; val nextIdx = if (currentIdx == -1) 0 else (currentIdx + 1) % displays.size; performDisplayChange(displays[nextIdx].displayId)
    }
    private fun performDisplayChange(newId: Int) {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val targetDisplay = dm.getDisplay(newId) ?: return; try { if (bubbleView != null && bubbleView!!.isAttachedToWindow) windowManager.removeView(bubbleView); if (drawerView != null && drawerView!!.isAttachedToWindow) windowManager.removeView(drawerView) } catch (e: Exception) {}; currentDisplayId = newId; setupDisplayContext(currentDisplayId); targetDisplayIndex = currentDisplayId; AppPreferences.setTargetDisplayIndex(this, targetDisplayIndex); setupBubble(); setupDrawer(); loadDisplaySettings(currentDisplayId); updateBubbleIcon(); isExpanded = false; safeToast("Switched to Display $currentDisplayId (${targetDisplay.name})")
    }
    private fun toggleVirtualDisplay(enable: Boolean) { isVirtualDisplayActive = enable; Thread { try { if (enable) { shellService?.runCommand("settings put global overlay_display_devices \"1920x1080/320\""); uiHandler.post { safeToast("Creating Virtual Display... Wait a moment, then Switch Display.") } } else { shellService?.runCommand("settings delete global overlay_display_devices"); uiHandler.post { safeToast("Destroying Virtual Display...") } } } catch (e: Exception) { Log.e(TAG, "Virtual Display Toggle Failed", e) } }.start(); if (currentMode == MODE_SETTINGS) uiHandler.postDelayed({ switchMode(MODE_SETTINGS) }, 500) }

    // --- v2.0 SCREEN OFF LOGIC ---
    private fun performScreenOff() {
        vibrate()
        isScreenOffState = true
        safeToast("Screen Off: Double press Power Button to turn on")
        
        if (useAltScreenOff) {
             Thread {
                 try {
                     if (shellService != null) {
                         shellService?.setBrightness(0, -1)
                         uiHandler.post { safeToast("Pixels OFF (Alternate Mode)") }
                     } else {
                         safeToast("Service Disconnected!")
                     }
                 } catch (e: Exception) {
                     Log.e(TAG, "Binder Call Failed", e)
                     safeToast("Error: ${e.message}")
                 }
            }.start()
        } else {
            Thread { try { shellService?.setScreenOff(0, true) } catch (e: Exception) {} }.start()
            safeToast("Screen OFF (SurfaceControl)")
        }
    }
    
    private fun wakeUp() {
        vibrate()
        isScreenOffState = false
        
        Thread { try { shellService?.setBrightness(0, 128) } catch (e: Exception) {} }.start()
        Thread { try { shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start()

        safeToast("Screen On")
        if (currentMode == MODE_SETTINGS) drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }

    private fun applyLayoutImmediate() { executeLaunch(selectedLayoutType, closeDrawer = false) }
    private fun fetchRunningApps() { if (shellService == null) return; Thread { try { val visiblePackages = shellService!!.getVisiblePackages(currentDisplayId); val allRunning = shellService!!.getAllRunningPackages(); val lastQueue = AppPreferences.getLastQueue(this); uiHandler.post { selectedAppsQueue.clear(); for (pkg in lastQueue) { if (pkg == PACKAGE_BLANK) { selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)) } else if (allRunning.contains(pkg)) { val appInfo = allAppsList.find { it.packageName == pkg }; if (appInfo != null) { appInfo.isMinimized = !visiblePackages.contains(pkg); selectedAppsQueue.add(appInfo) } } }; for (pkg in visiblePackages) { if (!lastQueue.contains(pkg)) { val appInfo = allAppsList.find { it.packageName == pkg }; if (appInfo != null) { appInfo.isMinimized = false; selectedAppsQueue.add(appInfo) } } }; sortAppQueue(); updateSelectedAppsDock(); drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); safeToast("Instant Mode: Active") } } catch (e: Exception) { Log.e(TAG, "Error fetching apps", e) } }.start() }
    private fun selectLayout(opt: LayoutOption) { dismissKeyboardAndRestore(); selectedLayoutType = opt.type; activeCustomRects = opt.customRects; if (opt.type == LAYOUT_CUSTOM_DYNAMIC) { activeCustomLayoutName = opt.name; AppPreferences.saveLastCustomLayoutName(this, opt.name) } else { activeCustomLayoutName = null; AppPreferences.saveLastCustomLayoutName(this, null) }; AppPreferences.saveLastLayout(this, opt.type); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() }
    private fun saveCurrentAsCustom() { Thread { try { val rawLayouts = shellService!!.getWindowLayouts(currentDisplayId); if (rawLayouts.isEmpty()) { safeToast("Found 0 active app windows"); return@Thread }; val rectStrings = mutableListOf<String>(); for (line in rawLayouts) { val parts = line.split("|"); if (parts.size == 2) { rectStrings.add(parts[1]) } }; if (rectStrings.isEmpty()) { safeToast("Found 0 valid frames"); return@Thread }; val count = rectStrings.size; var baseName = "$count Apps - Custom"; val existingNames = AppPreferences.getCustomLayoutNames(this); var counter = 1; var finalName = "$baseName $counter"; while (existingNames.contains(finalName)) { counter++; finalName = "$baseName $counter" }; AppPreferences.saveCustomLayout(this, finalName, rectStrings.joinToString("|")); safeToast("Saved: $finalName"); uiHandler.post { switchMode(MODE_LAYOUTS) } } catch (e: Exception) { Log.e(TAG, "Failed to save custom layout", e); safeToast("Error saving: ${e.message}") } }.start() }
    private fun applyResolution(opt: ResolutionOption) { dismissKeyboardAndRestore(); if (opt.index != -1) { selectedResolutionIndex = opt.index; AppPreferences.saveDisplayResolution(this, currentDisplayId, opt.index) }; drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode && opt.index != -1) { Thread { val resCmd = getResolutionCommand(selectedResolutionIndex); shellService?.runCommand(resCmd); Thread.sleep(1500); uiHandler.post { applyLayoutImmediate() } }.start() } }
    private fun selectDpi(value: Int) { currentDpiSetting = if (value == -1) -1 else value.coerceIn(50, 600); AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); Thread { try { if (currentDpiSetting == -1) { shellService?.runCommand("wm density reset -d $currentDisplayId") } else { val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; shellService?.runCommand(dpiCmd) } } catch(e: Exception) { e.printStackTrace() } }.start() }
    private fun changeFontSize(newSize: Float) { currentFontSize = newSize.coerceIn(10f, 30f); AppPreferences.saveFontSize(this, currentFontSize); updateGlobalFontSize(); if (currentMode == MODE_SETTINGS) { switchMode(MODE_SETTINGS) } }
    private fun changeDrawerHeight(delta: Int) { currentDrawerHeightPercent = (currentDrawerHeightPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerHeightPercent(this, currentDrawerHeightPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun changeDrawerWidth(delta: Int) { currentDrawerWidthPercent = (currentDrawerWidthPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerWidthPercent(this, currentDrawerWidthPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun pickIcon() { toggleDrawer(); try { refreshDisplayId(); val intent = Intent(this, IconPickerActivity::class.java); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); val metrics = windowManager.maximumWindowMetrics; val w = 1000; val h = (metrics.bounds.height() * 0.7).toInt(); val x = (metrics.bounds.width() - w) / 2; val y = (metrics.bounds.height() - h) / 2; val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(Rect(x, y, x+w, y+h)); startActivity(intent, options.toBundle()) } catch (e: Exception) { safeToast("Error launching picker: ${e.message}") } }
    private fun saveProfile() { var name = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString()?.trim(); if (name.isNullOrEmpty()) { val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()); name = "Profile_$timestamp" }; val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveProfile(this, name, selectedLayoutType, selectedResolutionIndex, currentDpiSetting, pkgs); safeToast("Saved: $name"); drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText(""); switchMode(MODE_PROFILES) }
    private fun loadProfile(name: String) { val data = AppPreferences.getProfileData(this, name) ?: return; try { val parts = data.split("|"); selectedLayoutType = parts[0].toInt(); selectedResolutionIndex = parts[1].toInt(); currentDpiSetting = parts[2].toInt(); val pkgList = parts[3].split(","); selectedAppsQueue.clear(); for (pkg in pkgList) { if (pkg.isNotEmpty()) { if (pkg == PACKAGE_BLANK) { selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)) } else { val app = allAppsList.find { it.packageName == pkg }; if (app != null) selectedAppsQueue.add(app) } } }; AppPreferences.saveLastLayout(this, selectedLayoutType); AppPreferences.saveDisplayResolution(this, currentDisplayId, selectedResolutionIndex); AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); activeProfileName = name; updateSelectedAppsDock(); safeToast("Loaded: $name"); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() } catch (e: Exception) { Log.e(TAG, "Failed to load profile", e) } }
    
    private fun executeLaunch(layoutType: Int, closeDrawer: Boolean) { 
        if (closeDrawer) toggleDrawer(); refreshDisplayId(); val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveLastQueue(this, pkgs)
        Thread { try { val resCmd = getResolutionCommand(selectedResolutionIndex); shellService?.runCommand(resCmd); if (currentDpiSetting > 0) { val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; shellService?.runCommand(dpiCmd) } else { if (currentDpiSetting == -1) shellService?.runCommand("wm density reset -d $currentDisplayId") }; Thread.sleep(800); val targetDim = getTargetDimensions(selectedResolutionIndex); var w = 0; var h = 0; if (targetDim != null) { w = targetDim.first; h = targetDim.second } else { val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val display = dm.getDisplay(currentDisplayId); if (display != null) { val metrics = DisplayMetrics(); display.getRealMetrics(metrics); w = metrics.widthPixels; h = metrics.heightPixels } else { val bounds = windowManager.maximumWindowMetrics.bounds; w = bounds.width(); h = bounds.height() } }; val rects = mutableListOf<Rect>(); when (layoutType) { LAYOUT_FULL -> { rects.add(Rect(0, 0, w, h)) }; LAYOUT_SIDE_BY_SIDE -> { rects.add(Rect(0, 0, w/2, h)); rects.add(Rect(w/2, 0, w, h)) }; LAYOUT_TOP_BOTTOM -> { rects.add(Rect(0, 0, w, h/2)); rects.add(Rect(0, h/2, w, h)) }; LAYOUT_TRI_EVEN -> { val third = w / 3; rects.add(Rect(0, 0, third, h)); rects.add(Rect(third, 0, third * 2, h)); rects.add(Rect(third * 2, 0, w, h)) }; LAYOUT_CORNERS -> { rects.add(Rect(0, 0, w/2, h/2)); rects.add(Rect(w/2, 0, w, h/2)); rects.add(Rect(0, h/2, w/2, h)); rects.add(Rect(w/2, h/2, w, h)) }; LAYOUT_TRI_SIDE_MAIN_SIDE -> { val quarter = w / 4; rects.add(Rect(0, 0, quarter, h)); rects.add(Rect(quarter, 0, quarter * 3, h)); rects.add(Rect(quarter * 3, 0, w, h)) }; LAYOUT_QUAD_ROW_EVEN -> { val quarter = w / 4; rects.add(Rect(0, 0, quarter, h)); rects.add(Rect(quarter, 0, quarter * 2, h)); rects.add(Rect(quarter * 2, 0, quarter * 3, h)); rects.add(Rect(quarter * 3, 0, w, h)) }; LAYOUT_CUSTOM_DYNAMIC -> { if (activeCustomRects != null) { rects.addAll(activeCustomRects!!) } else { rects.add(Rect(0, 0, w/2, h)); rects.add(Rect(w/2, 0, w, h)) } } }; if (selectedAppsQueue.isNotEmpty()) { val minimizedApps = selectedAppsQueue.filter { it.isMinimized }; for (app in minimizedApps) { if (app.packageName != PACKAGE_BLANK) { try { val tid = shellService?.getTaskId(app.packageName) ?: -1; if (tid != -1) shellService?.moveTaskToBack(tid) } catch (e: Exception) { Log.e(TAG, "Failed to minimize ${app.packageName}", e) } } }; val activeApps = selectedAppsQueue.filter { !it.isMinimized }; if (killAppOnExecute) { for (app in activeApps) { if (app.packageName != PACKAGE_BLANK) { shellService?.forceStop(app.packageName) } }; Thread.sleep(400) } else { Thread.sleep(100) }; val count = Math.min(activeApps.size, rects.size); for (i in 0 until count) { val pkg = activeApps[i].packageName; val bounds = rects[i]; if (pkg == PACKAGE_BLANK) continue; uiHandler.postDelayed({ launchViaApi(pkg, bounds) }, (i * 150).toLong()); uiHandler.postDelayed({ launchViaShell(pkg) }, (i * 150 + 50).toLong()); if (!killAppOnExecute) { uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {} }.start() }, (i * 150 + 150).toLong()) }; uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {} }.start() }, (i * 150 + 800).toLong()) }; if (closeDrawer) { uiHandler.post { selectedAppsQueue.clear(); updateSelectedAppsDock() } } } } catch (e: Exception) { Log.e(TAG, "Execute Failed", e); safeToast("Execute Failed: ${e.message}") } }.start(); drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText("") 
    }
    
    private fun calculateGCD(a: Int, b: Int): Int { return if (b == 0) a else calculateGCD(b, a % b) }

    private fun switchMode(mode: Int) {
        currentMode = mode
        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val searchIcon = drawerView!!.findViewById<ImageView>(R.id.icon_search_mode); val iconWin = drawerView!!.findViewById<ImageView>(R.id.icon_mode_window); val iconRes = drawerView!!.findViewById<ImageView>(R.id.icon_mode_resolution); val iconDpi = drawerView!!.findViewById<ImageView>(R.id.icon_mode_dpi); val iconProf = drawerView!!.findViewById<ImageView>(R.id.icon_mode_profiles); val iconSet = drawerView!!.findViewById<ImageView>(R.id.icon_mode_settings); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        searchIcon.setColorFilter(if(mode==MODE_SEARCH) Color.WHITE else Color.GRAY); iconWin.setColorFilter(if(mode==MODE_LAYOUTS) Color.WHITE else Color.GRAY); iconRes.setColorFilter(if(mode==MODE_RESOLUTION) Color.WHITE else Color.GRAY); iconDpi.setColorFilter(if(mode==MODE_DPI) Color.WHITE else Color.GRAY); iconProf.setColorFilter(if(mode==MODE_PROFILES) Color.WHITE else Color.GRAY); iconSet.setColorFilter(if(mode==MODE_SETTINGS) Color.WHITE else Color.GRAY)
        executeBtn.visibility = if (isInstantMode) View.GONE else View.VISIBLE; displayList.clear(); val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); dock.visibility = if (mode == MODE_SEARCH && selectedAppsQueue.isNotEmpty()) View.VISIBLE else View.GONE
        
        when (mode) {
            MODE_SEARCH -> { searchBar.hint = "Search apps..."; refreshSearchList() }
            MODE_LAYOUTS -> { 
                searchBar.hint = "Select Layout"; displayList.add(ActionOption("Save Current Arrangement") { saveCurrentAsCustom() }); displayList.add(LayoutOption("1 App - Full Screen", LAYOUT_FULL)); displayList.add(LayoutOption("2 Apps - Side by Side", LAYOUT_SIDE_BY_SIDE)); displayList.add(LayoutOption("2 Apps - Top & Bottom", LAYOUT_TOP_BOTTOM)); displayList.add(LayoutOption("3 Apps - Even", LAYOUT_TRI_EVEN)); displayList.add(LayoutOption("3 Apps - Side/Main/Side (25/50/25)", LAYOUT_TRI_SIDE_MAIN_SIDE)); displayList.add(LayoutOption("4 Apps - Corners", LAYOUT_CORNERS)); displayList.add(LayoutOption("4 Apps - Row (Even)", LAYOUT_QUAD_ROW_EVEN));
                val customNames = AppPreferences.getCustomLayoutNames(this).sorted(); for (name in customNames) { val data = AppPreferences.getCustomLayoutData(this, name); if (data != null) { try { val rects = mutableListOf<Rect>(); val rectParts = data.split("|"); for (rp in rectParts) { val coords = rp.split(","); if (coords.size == 4) { rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt())) } }; displayList.add(LayoutOption(name, LAYOUT_CUSTOM_DYNAMIC, true, rects)) } catch(e: Exception) {} } } 
            }
            MODE_RESOLUTION -> {
                searchBar.hint = "Select Resolution"; displayList.add(CustomResInputOption); val savedResNames = AppPreferences.getCustomResolutionNames(this).sorted(); for (name in savedResNames) { val value = AppPreferences.getCustomResolutionValue(this, name) ?: continue; displayList.add(ResolutionOption(name, "wm size  -d $currentDisplayId", 100 + savedResNames.indexOf(name))) }; displayList.add(ResolutionOption("Default (Reset)", "wm size reset -d $currentDisplayId", 0)); displayList.add(ResolutionOption("1:1 Square (1422x1500)", "wm size 1422x1500 -d $currentDisplayId", 1)); displayList.add(ResolutionOption("16:9 Landscape (1920x1080)", "wm size 1920x1080 -d $currentDisplayId", 2)); displayList.add(ResolutionOption("32:9 Ultrawide (3840x1080)", "wm size 3840x1080 -d $currentDisplayId", 3))
            }
            MODE_DPI -> { searchBar.hint = "Adjust Density (DPI)"; displayList.add(ActionOption("Reset Density (Default)") { selectDpi(-1) }); var savedDpi = currentDpiSetting; if (savedDpi <= 0) { savedDpi = displayContext?.resources?.configuration?.densityDpi ?: 160 }; displayList.add(DpiOption(savedDpi)) }
            MODE_PROFILES -> { searchBar.hint = "Enter Profile Name..."; displayList.add(ProfileOption("Save Current as New", true, 0,0,0, emptyList())); val profileNames = AppPreferences.getProfileNames(this).sorted(); for (pName in profileNames) { val data = AppPreferences.getProfileData(this, pName); if (data != null) { try { val parts = data.split("|"); val lay = parts[0].toInt(); val res = parts[1].toInt(); val d = parts[2].toInt(); val pkgs = parts[3].split(",").filter { it.isNotEmpty() }; displayList.add(ProfileOption(pName, false, lay, res, d, pkgs)) } catch(e: Exception) {} } } }
            MODE_SETTINGS -> {
                searchBar.hint = "Settings"
                displayList.add(ActionOption("Launch DroidOS Trackpad") { launchTrackpad() }) 
                displayList.add(ActionOption("Switch Display (Current $currentDisplayId)") { cycleDisplay() })
                displayList.add(ToggleOption("Virtual Display (1080p)", isVirtualDisplayActive) { toggleVirtualDisplay(it) })
                displayList.add(HeightOption(currentDrawerHeightPercent))
                displayList.add(WidthOption(currentDrawerWidthPercent))
                displayList.add(ToggleOption("Auto-Shrink for Keyboard", autoResizeEnabled) { autoResizeEnabled = it; AppPreferences.setAutoResizeKeyboard(this, it) })
                displayList.add(FontSizeOption(currentFontSize))
                displayList.add(IconOption("Launcher Icon (Tap to Change)"))
                displayList.add(ToggleOption("Reorder: Drag & Drop", isReorderDragEnabled) { isReorderDragEnabled = it; AppPreferences.setReorderDrag(this, it) })
                displayList.add(ToggleOption("Reorder: Tap to Swap (Long Press)", isReorderTapEnabled) { isReorderTapEnabled = it; AppPreferences.setReorderTap(this, it) })
                displayList.add(ToggleOption("Instant Mode (Live Changes)", isInstantMode) { isInstantMode = it; AppPreferences.setInstantMode(this, it); executeBtn.visibility = if (it) View.GONE else View.VISIBLE; if (it) fetchRunningApps() })
                displayList.add(ToggleOption("Kill App on Execute", killAppOnExecute) { killAppOnExecute = it; AppPreferences.setKillOnExecute(this, it) })
                
                // --- V2.0 MENU ITEMS RESTORED ---
                
                
                // STANDARD MODE TOGGLE
                displayList.add(ToggleOption("Screen Off (Standard)", isScreenOffState && !useAltScreenOff) { 
                    if (it) {
                        if (isScreenOffState) wakeUp() // Reset if already off
                        useAltScreenOff = false
                        AppPreferences.setUseAltScreenOff(this, false)
                        performScreenOff()
                    } else {
                        wakeUp()
                    }
                })

                // ALTERNATE MODE TOGGLE
                displayList.add(ToggleOption("Screen Off (Alternate)", isScreenOffState && useAltScreenOff) { 
                    if (it) {
                        if (isScreenOffState) wakeUp() // Reset if already off
                        useAltScreenOff = true
                        AppPreferences.setUseAltScreenOff(this, true)
                        performScreenOff()
                    } else {
                        wakeUp()
                    }
                })
                
                displayList.add(ToggleOption("Shizuku Warning (Icon Alert)", showShizukuWarning) { showShizukuWarning = it; AppPreferences.setShowShizukuWarning(this, it); updateBubbleIcon() })
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
    data class TimeoutOption(val seconds: Int)

    inner class SelectedAppsAdapter : RecyclerView.Adapter<SelectedAppsAdapter.Holder>() {
        inner class Holder(v: View) : RecyclerView.ViewHolder(v) { val icon: ImageView = v.findViewById(R.id.selected_app_icon) }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder { return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_app, parent, false)) }
        override fun onBindViewHolder(holder: Holder, position: Int) { 
            val app = selectedAppsQueue[position]; if (position == reorderSelectionIndex) { holder.icon.setColorFilter(HIGHLIGHT_COLOR); holder.icon.alpha = 1.0f; holder.itemView.scaleX = 1.1f; holder.itemView.scaleY = 1.1f; holder.itemView.background = null } else { holder.icon.clearColorFilter(); holder.itemView.scaleX = 1.0f; holder.itemView.scaleY = 1.0f; holder.itemView.background = null; if (app.packageName == PACKAGE_BLANK) { holder.icon.setImageResource(R.drawable.ic_box_outline); holder.icon.alpha = 1.0f } else { try { holder.icon.setImageDrawable(packageManager.getApplicationIcon(app.packageName)) } catch (e: Exception) { holder.icon.setImageResource(R.drawable.ic_launcher_bubble) }; holder.icon.alpha = if (app.isMinimized) 0.4f else 1.0f } }
            holder.itemView.setOnClickListener { try { dismissKeyboardAndRestore(); if (reorderSelectionIndex != -1) { if (position == reorderSelectionIndex) { endReorderMode(false) } else { swapReorderItem(position) } } else { if (app.packageName != PACKAGE_BLANK) { app.isMinimized = !app.isMinimized; notifyItemChanged(position); if (isInstantMode) applyLayoutImmediate() } } } catch(e: Exception) {} }
            holder.itemView.setOnLongClickListener { if (isReorderTapEnabled) { startReorderMode(position); true } else { false } }
        }
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
            else if (holder is ProfileRichHolder && item is ProfileOption) { holder.name.setText(item.name); holder.iconsContainer.removeAllViews(); if (!item.isCurrent) { for (pkg in item.apps.take(5)) { val iv = ImageView(holder.itemView.context); val lp = LinearLayout.LayoutParams(60, 60); lp.marginEnd = 8; iv.layoutParams = lp; if (pkg == PACKAGE_BLANK) { iv.setImageResource(R.drawable.ic_box_outline) } else { try { iv.setImageDrawable(packageManager.getApplicationIcon(pkg)) } catch (e: Exception) { iv.setImageResource(R.drawable.ic_launcher_bubble) } }; holder.iconsContainer.addView(iv) }; val info = "${getLayoutName(item.layout)} | ${getRatioName(item.resIndex)} | ${item.dpi}dpi"; holder.details.text = info; holder.details.visibility = View.VISIBLE; holder.btnSave.visibility = View.GONE; if (activeProfileName == item.name) { holder.itemView.setBackgroundResource(R.drawable.bg_item_active) } else { holder.itemView.setBackgroundResource(0) }; holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); loadProfile(item.name) }; holder.itemView.setOnLongClickListener { startRename(holder.name); true }; val saveProfileName = { val newName = holder.name.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameProfile(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); switchMode(MODE_PROFILES) } }; endRename(holder.name) }; holder.name.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveProfileName(); holder.name.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(holder.name.windowToken, 0); updateDrawerHeight(false); true } else false }; holder.name.setOnFocusChangeListener { v, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus); if (!hasFocus) saveProfileName() } } else { holder.iconsContainer.removeAllViews(); holder.details.visibility = View.GONE; holder.btnSave.visibility = View.VISIBLE; holder.itemView.setBackgroundResource(0); holder.name.isEnabled = true; holder.name.isFocusable = true; holder.name.isFocusableInTouchMode = true; holder.itemView.setOnClickListener { saveProfile() }; holder.btnSave.setOnClickListener { saveProfile() } } }
            else if (holder is LayoutHolder) {
                holder.btnSave.visibility = View.GONE; holder.btnExtinguish.visibility = View.GONE
                if (item is LayoutOption) { holder.nameInput.setText(item.name); val isSelected = if (item.type == LAYOUT_CUSTOM_DYNAMIC) { item.type == selectedLayoutType && item.name == activeCustomLayoutName } else { item.type == selectedLayoutType && activeCustomLayoutName == null }; if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { selectLayout(item) }; if (item.isCustomSaved) { holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }; val saveLayoutName = { val newName = holder.nameInput.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameCustomLayout(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); if (activeCustomLayoutName == item.name) { activeCustomLayoutName = newName; AppPreferences.saveLastCustomLayoutName(holder.itemView.context, newName) }; switchMode(MODE_LAYOUTS) } }; endRename(holder.nameInput) }; holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveLayoutName(); true } else false }; holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveLayoutName() } } else { holder.nameInput.isEnabled = false; holder.nameInput.isFocusable = false; holder.nameInput.setTextColor(Color.WHITE) } }
                else if (item is ResolutionOption) { 
                    holder.nameInput.setText(item.name); if (item.index >= 100) { holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }; val saveResName = { val newName = holder.nameInput.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameCustomResolution(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); switchMode(MODE_RESOLUTION) } }; endRename(holder.nameInput) }; holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveResName(); true } else false }; holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveResName() } } else { holder.nameInput.isEnabled = false; holder.nameInput.isFocusable = false; holder.nameInput.setTextColor(Color.WHITE) }; val isSelected = (item.index == selectedResolutionIndex); if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { applyResolution(item) } 
                }
                else if (item is IconOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { pickIcon() } }
                else if (item is ToggleOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); if (item.isEnabled) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.isEnabled = !item.isEnabled; item.onToggle(item.isEnabled); notifyItemChanged(position) } } 
                else if (item is ActionOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.action() } }
            }
            else if (holder is CustomResInputHolder) {
                holder.btnSave.setOnClickListener { val wStr = holder.inputW.text.toString().trim(); val hStr = holder.inputH.text.toString().trim(); if (wStr.isNotEmpty() && hStr.isNotEmpty()) { val w = wStr.toIntOrNull(); val h = hStr.toIntOrNull(); if (w != null && h != null && w > 0 && h > 0) { val gcdVal = calculateGCD(w, h); val wRatio = w / gcdVal; val hRatio = h / gcdVal; val resString = "${w}x${h}"; val name = "$wRatio:$hRatio Custom ($resString)"; AppPreferences.saveCustomResolution(holder.itemView.context, name, resString); safeToast("Added $name"); dismissKeyboardAndRestore(); switchMode(MODE_RESOLUTION) } else { safeToast("Invalid numbers") } } else { safeToast("Input W and H") } }
                holder.inputW.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }; holder.inputH.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }
            }
            else if (holder is IconSettingHolder && item is IconOption) { try { val uriStr = AppPreferences.getIconUri(holder.itemView.context); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); holder.preview.setImageBitmap(bitmap) } else { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) } } catch(e: Exception) { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) }; holder.itemView.setOnClickListener { pickIcon() } }
            else if (holder is DpiHolder && item is DpiOption) { 
                holder.input.setText(item.currentDpi.toString()); holder.input.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { val valInt = v.text.toString().toIntOrNull(); if (valInt != null) { selectDpi(valInt); safeToast("DPI set to $valInt") }; dismissKeyboardAndRestore(); true } else false }; holder.input.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus); if (!hasFocus) { val valInt = holder.input.text.toString().toIntOrNull(); if (valInt != null && valInt != item.currentDpi) { selectDpi(valInt) } } }; holder.btnMinus.setOnClickListener { val v = holder.input.text.toString().toIntOrNull() ?: 160; val newVal = (v - 5).coerceAtLeast(50); holder.input.setText(newVal.toString()); selectDpi(newVal) }; holder.btnPlus.setOnClickListener { val v = holder.input.text.toString().toIntOrNull() ?: 160; val newVal = (v + 5).coerceAtMost(600); holder.input.setText(newVal.toString()); selectDpi(newVal) } 
            }
            else if (holder is FontSizeHolder && item is FontSizeOption) { holder.textVal.text = item.currentSize.toInt().toString(); holder.btnMinus.setOnClickListener { changeFontSize(item.currentSize - 1) }; holder.btnPlus.setOnClickListener { changeFontSize(item.currentSize + 1) } }
            else if (holder is HeightHolder && item is HeightOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerHeight(-5) }; holder.btnPlus.setOnClickListener { changeDrawerHeight(5) } }
            else if (holder is WidthHolder && item is WidthOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerWidth(-5) }; holder.btnPlus.setOnClickListener { changeDrawerWidth(5) } }
        }
        override fun getItemCount() = displayList.size
    }
}
```

## File: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShellUserService.kt
```kotlin
package com.example.quadrantlauncher

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern
import android.os.Build

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    companion object {
        const val POWER_MODE_OFF = 0
        const val POWER_MODE_NORMAL = 2
        
        @Volatile private var displayControlClass: Class<*>? = null
        @Volatile private var displayControlClassLoaded = false
    }

    private val surfaceControlClass: Class<*> by lazy {
        Class.forName("android.view.SurfaceControl")
    }

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

    private fun setDisplayBrightnessInternal(displayId: Int, brightness: Float): Boolean {
        // Legacy shim for single-target calls
        val tokens = getAllPhysicalDisplayTokens()
        if (tokens.isNotEmpty()) return setDisplayBrightnessOnToken(tokens[0], brightness)
        return false
    }

    private val shLock = Object()
    private var _shProcess: Process? = null
    private val shProcess: Process
        get() = synchronized(shLock) {
            if (_shProcess?.isAlive == true) _shProcess!!
            else Runtime.getRuntime().exec(arrayOf("sh")).also { _shProcess = it }
        }

    private fun execShellCommand(command: String) {
        synchronized(shLock) {
            try {
                val output = shProcess.outputStream
                output.write("$command\n".toByteArray())
                output.flush()
            } catch (e: Exception) {
                Log.e(TAG, "Shell command failed", e)
            }
        }
    }

    // ============================================================
    // AIDL Interface Implementations
    // ============================================================

    
override fun setBrightness(displayId: Int, brightness: Int) {
        Log.d(TAG, "setBrightness(Global Broadcast, Value: $brightness)")
        val token = Binder.clearCallingIdentity()
        try {
            if (brightness < 0) {
                // === SCREEN OFF ===
                execShellCommand("settings put system screen_brightness_mode 0")
                
                // Get ALL tokens, but ONLY apply to the first 2 (Main + Cover)
                // This prevents killing the Glasses (which would be index 2+)
                val tokens = getAllPhysicalDisplayTokens()
                val safeTokens = tokens.take(2)
                
                for (t in safeTokens) {
                    setDisplayBrightnessOnToken(t, -1.0f)
                }
                
                execShellCommand("settings put system screen_brightness_float -1.0")
                execShellCommand("settings put system screen_brightness -1")
            } else {
                // === SCREEN ON ===
                val floatVal = brightness.toFloat() / 255.0f
                
                // Restore ALL tokens (safety, in case user replugged glasses)
                val tokens = getAllPhysicalDisplayTokens()
                for (t in tokens) {
                    setDisplayBrightnessOnToken(t, floatVal)
                }
                
                execShellCommand("settings put system screen_brightness_float $floatVal")
                execShellCommand("settings put system screen_brightness $brightness")
            }
        } catch (e: Exception) {
            Log.e(TAG, "setBrightness failed", e)
        } finally {
             Binder.restoreCallingIdentity(token)
        }
    }

    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        Log.d(TAG, "setScreenOff(Global Broadcast, TurnOff: $turnOff)")
        val token = Binder.clearCallingIdentity()
        try {
            val mode = if (turnOff) POWER_MODE_OFF else POWER_MODE_NORMAL
            
            // Same safety limit: Only affect first 2 physical screens
            val tokens = getAllPhysicalDisplayTokens()
            val safeTokens = tokens.take(2)
            
            for (t in safeTokens) {
                setPowerModeOnToken(t, mode)
            }
        } catch (e: Exception) {
            Log.e(TAG, "setScreenOff failed", e)
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }
    // --- V1.0 LOGIC: Window Management (Retained for Tiling/Minimizing) ---
    
    override fun forceStop(packageName: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec("am force-stop $packageName").waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun runCommand(command: String) {
        val token = Binder.clearCallingIdentity()
        try { Runtime.getRuntime().exec(command).waitFor() } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun repositionTask(packageName: String, left: Int, top: Int, right: Int, bottom: Int) {
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec("dumpsys activity top")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            var tid = -1
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains(packageName) && line!!.contains("TASK")) { 
                    val m = Regex("id=(\\d+)").find(line!!)
                    if (m != null) tid = m.groupValues[1].toInt() 
                }
            }
            if (tid != -1) { 
                Runtime.getRuntime().exec("am task set-windowing-mode $tid 5").waitFor()
                Runtime.getRuntime().exec("am task resize $tid $left $top $right $bottom").waitFor() 
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
    }

    override fun getVisiblePackages(displayId: Int): List<String> {
        val list = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec("dumpsys window windows")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            var currentPkg: String? = null
            var isVisible = false
            var onCorrectDisplay = false
            val windowPattern = Pattern.compile("Window\\{[0-9a-f]+ u\\d+ ([^\\}/ ]+)")

            while (r.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.startsWith("Window #")) {
                    currentPkg = null; isVisible = false; onCorrectDisplay = false
                    val matcher = windowPattern.matcher(l)
                    if (matcher.find()) currentPkg = matcher.group(1)
                }
                if (l.contains("displayId=$displayId") || l.contains("mDisplayId=$displayId")) onCorrectDisplay = true
                if (l.contains("mViewVisibility=0x0")) isVisible = true

                if (currentPkg != null && isVisible && onCorrectDisplay) {
                    if (isUserApp(currentPkg!!) && !list.contains(currentPkg!!)) list.add(currentPkg!!)
                    currentPkg = null
                }
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return list
    }

    override fun getAllRunningPackages(): List<String> {
        val list = ArrayList<String>()
        val token = Binder.clearCallingIdentity()
        try {
            val p = Runtime.getRuntime().exec("dumpsys activity activities")
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            val recordPattern = Pattern.compile("ActivityRecord\\{[0-9a-f]+ u\\d+ ([a-zA-Z0-9_.]+)/")
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains("ActivityRecord{")) {
                    val m = recordPattern.matcher(line!!)
                    if (m.find()) { val pkg = m.group(1); if (pkg != null && !list.contains(pkg) && isUserApp(pkg)) list.add(pkg) }
                }
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return list
    }

override fun getWindowLayouts(displayId: Int): List<String> {
    val results = ArrayList<String>()
    val token = Binder.clearCallingIdentity()
    try {
        val p = Runtime.getRuntime().exec("dumpsys activity activities")
        val r = BufferedReader(InputStreamReader(p.inputStream))
        var line: String?
        
        var currentDisplayId = -1
        var currentTaskBounds: String? = null
        var foundPackages = mutableSetOf<String>()
        
        val displayPattern = Pattern.compile("Display #(\\d+)")
        val boundsPattern = Pattern.compile("bounds=\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]")
        val rectPattern = Pattern.compile("mBounds=Rect\\((\\d+), (\\d+) - (\\d+), (\\d+)\\)")
        val activityPattern = Pattern.compile("ActivityRecord\\{[0-9a-f]+ u\\d+ ([a-zA-Z0-9_.]+)/")

        while (r.readLine().also { line = it } != null) {
            val l = line!!
            
            val displayMatcher = displayPattern.matcher(l)
            if (displayMatcher.find()) {
                currentDisplayId = displayMatcher.group(1)?.toIntOrNull() ?: -1
            }
            
            if (currentDisplayId != displayId) continue
            
            val boundsMatcher = boundsPattern.matcher(l)
            if (boundsMatcher.find()) {
                val left = boundsMatcher.group(1)
                val top = boundsMatcher.group(2)
                val right = boundsMatcher.group(3)
                val bottom = boundsMatcher.group(4)
                currentTaskBounds = "$left,$top,$right,$bottom"
            }
            
            val rectMatcher = rectPattern.matcher(l)
            if (rectMatcher.find()) {
                val left = rectMatcher.group(1)
                val top = rectMatcher.group(2)
                val right = rectMatcher.group(3)
                val bottom = rectMatcher.group(4)
                currentTaskBounds = "$left,$top,$right,$bottom"
            }
            
            if (l.contains("ActivityRecord{") && currentTaskBounds != null) {
                val activityMatcher = activityPattern.matcher(l)
                if (activityMatcher.find()) {
                    val pkg = activityMatcher.group(1)
                    if (pkg != null && isUserApp(pkg) && !foundPackages.contains(pkg)) {
                        results.add("$pkg|$currentTaskBounds")
                        foundPackages.add(pkg)
                    }
                }
            }
        }
        
        r.close()
        p.waitFor()
    } catch (e: Exception) {
        Log.e(TAG, "getWindowLayouts failed", e)
    } finally {
        Binder.restoreCallingIdentity(token)
    }
    return results
}

    override fun getTaskId(packageName: String): Int {
        var taskId = -1
        val token = Binder.clearCallingIdentity()
        try {
            val cmd = arrayOf("sh", "-c", "dumpsys activity activities | grep -E 'Task id|$packageName'")
            val p = Runtime.getRuntime().exec(cmd)
            val r = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            while (r.readLine().also { line = it } != null) {
                if (line!!.contains(packageName)) {
                    if (line!!.startsWith("* Task{") || line!!.startsWith("Task{")) { val m = Regex("#(\\d+)").find(line!!); if (m != null) { taskId = m.groupValues[1].toInt(); break } }
                    if (line!!.contains("ActivityRecord")) { val m = Regex("t(\\d+)").find(line!!); if (m != null) { taskId = m.groupValues[1].toInt(); break } }
                }
            }
        } catch (e: Exception) {} finally { Binder.restoreCallingIdentity(token) }
        return taskId
    }

    override fun moveTaskToBack(taskId: Int) {
        val token = Binder.clearCallingIdentity()
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
            } catch (e2: Exception) {}
        } finally { Binder.restoreCallingIdentity(token) }
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

    // Interface compliance stubs
    override fun setSystemBrightness(brightness: Int) { execShellCommand("settings put system screen_brightness $brightness") }
    override fun getSystemBrightness(): Int = 128
    override fun getSystemBrightnessFloat(): Float = 0.5f
    override fun setAutoBrightness(enabled: Boolean) { execShellCommand("settings put system screen_brightness_mode ${if (enabled) 1 else 0}") }
    override fun isAutoBrightness(): Boolean = true
    override fun setBrightnessViaDisplayManager(displayId: Int, brightness: Float): Boolean = setDisplayBrightnessInternal(displayId, brightness)
}
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
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
    private val targetDisplayId: Int,
    private val onScreenToggleAction: () -> Unit,
    private val onScreenModeChangeAction: () -> Unit,
    private val onCloseAction: () -> Unit // New Parameter
) : KeyboardView.KeyboardListener {

    private var keyboardContainer: FrameLayout? = null
    private var keyboardView: KeyboardView? = null
    private var keyboardParams: WindowManager.LayoutParams? = null
    private var isVisible = false

    // State Variables
    private var isMoving = false
    private var isResizing = false
    private var isAnchored = false
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialWindowX = 0
    private var initialWindowY = 0
    private var initialWidth = 0
    private var initialHeight = 0

    private val TAG = "KeyboardOverlay"
    
    // FIX: Default height to WRAP_CONTENT (-2) to avoid cutting off rows
    private var keyboardWidth = 500
    private var keyboardHeight = WindowManager.LayoutParams.WRAP_CONTENT 
    
    private var screenWidth = 720
    private var screenHeight = 748
    private var currentRotation = 0
    private var currentAlpha = 200
    private var currentDisplayId = 0

    fun setScreenDimensions(width: Int, height: Int, displayId: Int = 0) {
        screenWidth = width
        screenHeight = height
        currentDisplayId = displayId
        
        loadKeyboardSizeForDisplay(displayId)
        
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        currentAlpha = prefs.getInt("keyboard_alpha", 200)
        
        // Only set defaults if nothing is saved
        if (!prefs.contains("keyboard_width_d$displayId")) {
            keyboardWidth = (width * 0.95f).toInt().coerceIn(300, 650)
            // Keep height as WRAP_CONTENT by default
            keyboardHeight = WindowManager.LayoutParams.WRAP_CONTENT
        }
    }

    fun updateScale(scale: Float) {
        if (keyboardView == null) return
        keyboardView?.setScale(scale)
        
        // If user scales, we might want to reset to WRAP_CONTENT to ensure fit,
        // or calculate a minimum. For now, WRAP_CONTENT is safest.
        keyboardHeight = WindowManager.LayoutParams.WRAP_CONTENT
        
        if (isVisible && keyboardParams != null) {
            keyboardParams?.height = keyboardHeight
            try { 
                windowManager.updateViewLayout(keyboardContainer, keyboardParams)
                saveKeyboardSize() 
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
    
    fun setRotation(angle: Int) {
        currentRotation = angle
        if (!isVisible || keyboardContainer == null || keyboardParams == null) return

        val isPortrait = (angle == 90 || angle == 270)
        // If rotated, we swap dimension constraints roughly
        val targetW = if (isPortrait) keyboardHeight else keyboardWidth
        val targetH = if (isPortrait) keyboardWidth else keyboardHeight
        
        // Use wrap content if dimensions are unset
        keyboardParams?.width = if (targetW == -2) WindowManager.LayoutParams.WRAP_CONTENT else targetW
        keyboardParams?.height = if (targetH == -2) WindowManager.LayoutParams.WRAP_CONTENT else targetH
        
        keyboardContainer?.rotation = angle.toFloat()
        
        // Adjust inner view translation if needed
        if (keyboardView != null) {
            val lp = keyboardView!!.layoutParams as FrameLayout.LayoutParams
            if (isPortrait) {
                // This logic assumes fixed sizes. With WRAP_CONTENT it's tricky.
                // Resetting to match parent is usually safest for rotation in this simple implementation
                lp.width = FrameLayout.LayoutParams.WRAP_CONTENT
                lp.height = FrameLayout.LayoutParams.WRAP_CONTENT
                keyboardView?.translationX = 0f
                keyboardView?.translationY = 0f
            } else {
                lp.width = FrameLayout.LayoutParams.MATCH_PARENT
                lp.height = FrameLayout.LayoutParams.MATCH_PARENT
                keyboardView?.translationX = 0f
                keyboardView?.translationY = 0f
            }
            keyboardView?.layoutParams = lp
        }

        try { windowManager.updateViewLayout(keyboardContainer, keyboardParams) } catch (e: Exception) {}
    }

    fun show() { 
        if (isVisible) return
        try { 
            val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            currentAlpha = prefs.getInt("keyboard_alpha", 200)

            createKeyboardWindow()
            isVisible = true
            if (currentRotation != 0) setRotation(currentRotation)
        } catch (e: Exception) { Log.e(TAG, "Failed to show keyboard", e) } 
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

    fun moveWindow(dx: Int, dy: Int) {
        if (!isVisible || keyboardParams == null) return
        keyboardParams!!.x += dx; keyboardParams!!.y += dy
        try { windowManager.updateViewLayout(keyboardContainer, keyboardParams); saveKeyboardPosition() } catch (e: Exception) {}
    }
    
    fun resizeWindow(dw: Int, dh: Int) {
         if (!isVisible || keyboardParams == null) return
         
         // If current height is WRAP_CONTENT (-2), start from current measured height
         var currentH = keyboardParams!!.height
         if (currentH == WindowManager.LayoutParams.WRAP_CONTENT) {
             currentH = keyboardContainer?.height ?: 260
         }
         
         var currentW = keyboardParams!!.width
         if (currentW == WindowManager.LayoutParams.WRAP_CONTENT) {
             currentW = keyboardContainer?.width ?: 500
         }

         keyboardParams!!.width = max(280, currentW + dw)
         keyboardParams!!.height = max(180, currentH + dh)
         
         keyboardWidth = keyboardParams!!.width
         keyboardHeight = keyboardParams!!.height
         
         try { windowManager.updateViewLayout(keyboardContainer, keyboardParams); saveKeyboardSize() } catch (e: Exception) {}
    }

    private fun createKeyboardWindow() {
        keyboardContainer = FrameLayout(context)
        val containerBg = GradientDrawable(); 
        val fillColor = (currentAlpha shl 24) or (0x1A1A1A)
        containerBg.setColor(fillColor)
        containerBg.cornerRadius = 16f
        containerBg.setStroke(2, Color.parseColor("#44FFFFFF"))
        keyboardContainer?.background = containerBg
        
        keyboardView = KeyboardView(context)
        keyboardView?.setKeyboardListener(this)
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        keyboardView?.setVibrationEnabled(prefs.getBoolean("vibrate", true))
        val scale = prefs.getInt("keyboard_key_scale", 100) / 100f; keyboardView?.setScale(scale)
        
        keyboardView?.alpha = currentAlpha / 255f
        
        val kbParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        kbParams.setMargins(6, 28, 6, 6)
        keyboardContainer?.addView(keyboardView, kbParams)
        addDragHandle(); addResizeHandle(); addCloseButton(); addTargetLabel()
        
        val savedX = prefs.getInt("keyboard_x_d$currentDisplayId", (screenWidth - keyboardWidth) / 2)
        val savedY = prefs.getInt("keyboard_y_d$currentDisplayId", screenHeight - 350 - 10)
        
        // FIX: Use variable keyboardHeight instead of hardcoded WRAP_CONTENT
        // This ensures loaded profiles with specific sizes are respected.
        keyboardParams = WindowManager.LayoutParams(
            keyboardWidth, 
            keyboardHeight, 
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, 
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, 
            PixelFormat.TRANSLUCENT
        )
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
             keyboardParams?.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        keyboardParams?.gravity = Gravity.TOP or Gravity.LEFT; keyboardParams?.x = savedX; keyboardParams?.y = savedY
        windowManager.addView(keyboardContainer, keyboardParams)
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

    private fun handleResize(event: MotionEvent): Boolean {
        if (isAnchored) return true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { 
                isResizing = true
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                initialWidth = keyboardParams?.width ?: keyboardWidth
                initialHeight = keyboardParams?.height ?: keyboardHeight 
            }
            MotionEvent.ACTION_MOVE -> { 
                if (isResizing) { 
                    val newW = max(280, initialWidth + (event.rawX - initialTouchX).toInt())
                    val newH = max(180, initialHeight + (event.rawY - initialTouchY).toInt())
                    
                    keyboardParams?.width = newW
                    keyboardParams?.height = newH
                    keyboardWidth = newW
                    keyboardHeight = newH
                    try { windowManager.updateViewLayout(keyboardContainer, keyboardParams) } catch (e: Exception) {} 
                } 
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { 
                isResizing = false
                saveKeyboardSize() 
            }
        }
        return true
    }

    private fun saveKeyboardSize() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_width_d$currentDisplayId", keyboardWidth).putInt("keyboard_height_d$currentDisplayId", keyboardHeight).apply() }
    private fun saveKeyboardPosition() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_x_d$currentDisplayId", keyboardParams?.x ?: 0).putInt("keyboard_y_d$currentDisplayId", keyboardParams?.y ?: 0).apply() }
    private fun loadKeyboardSizeForDisplay(displayId: Int) { val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); keyboardWidth = prefs.getInt("keyboard_width_d$displayId", keyboardWidth); keyboardHeight = prefs.getInt("keyboard_height_d$displayId", keyboardHeight) }

    override fun onKeyPress(keyCode: Int, char: Char?, metaState: Int) { injectKey(keyCode, metaState) }
    
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

    private fun triggerVoiceTyping() {
        if (shellService == null) return
        Thread {
            try {
                // FIXED: Now relies on shellService.runCommand returning output
                val output = shellService.runCommand("ime list -a -s")
                val voiceIme = output.lines().find { it.contains("google", true) && it.contains("voice", true) }
                    ?: output.lines().find { it.contains("voice", true) }
                if (voiceIme != null) {
                    shellService.runCommand("ime set $voiceIme")
                } else {
                    Log.w(TAG, "Voice IME not found in output: $output")
                }
            } catch (e: Exception) { Log.e(TAG, "Voice Switch Failed", e) }
        }.start()
    }

    private fun injectKey(keyCode: Int, metaState: Int) {
        if (shellService == null) return
        Thread {
            try {
                shellService.injectKey(keyCode, KeyEvent.ACTION_DOWN, metaState, targetDisplayId)
                Thread.sleep(20)
                shellService.injectKey(keyCode, KeyEvent.ACTION_UP, metaState, targetDisplayId)
            } catch (e: Exception) { Log.e(TAG, "Key injection failed", e) }
        }.start()
    }
}
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadMenuManager.kt
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
    private val TAB_CONFIG = 4
    private val TAB_TUNE = 5
    private val TAB_HARDKEYS = 6    // Hardkey bindings configuration
    private val TAB_BUBBLE = 7      // Bubble customization
    private val TAB_PROFILES = 8
    private val TAB_HELP = 9
    
    private var currentTab = TAB_MAIN

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
            R.id.tab_config to TAB_CONFIG,
            R.id.tab_tune to TAB_TUNE,
            R.id.tab_hardkeys to TAB_HARDKEYS,
            R.id.tab_bubble to TAB_BUBBLE,
            R.id.tab_profiles to TAB_PROFILES,
            R.id.tab_help to TAB_HELP
        )

        for ((id, index) in tabs) {
            drawerView?.findViewById<ImageView>(id)?.setOnClickListener { 
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
        val tabIds = listOf(R.id.tab_main, R.id.tab_presets, R.id.tab_move, R.id.tab_kb_move, R.id.tab_config, R.id.tab_tune, R.id.tab_hardkeys, R.id.tab_bubble, R.id.tab_profiles, R.id.tab_help)
        for ((i, id) in tabIds.withIndex()) {
            val view = drawerView?.findViewById<ImageView>(id)
            if (i == activeIdx) view?.setColorFilter(Color.parseColor("#3DDC84")) 
            else view?.setColorFilter(Color.GRAY)
        }
    }

    // =========================
    // GET MAIN ITEMS - Generates main menu items list
    // Includes Anchor toggle to lock trackpad/keyboard position
    // =========================
    private fun getMainItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("MAIN CONTROLS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem("Reset Bubble Position", android.R.drawable.ic_menu_myplaces, TrackpadMenuAdapter.Type.ACTION) { 
            service.resetBubblePosition()
            hide()
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Move Trackpad Here", R.drawable.ic_tab_move, TrackpadMenuAdapter.Type.ACTION) { service.forceMoveToCurrentDisplay(); hide() })
        list.add(TrackpadMenuAdapter.MenuItem("Target: ${if(service.inputTargetDisplayId == service.currentDisplayId) "Local" else "Remote"}", R.drawable.ic_cursor, TrackpadMenuAdapter.Type.ACTION) { service.cycleInputTarget(); loadTab(TAB_MAIN) })
        
        // --- ANCHOR TOGGLE: Locks trackpad and keyboard position/size ---
        list.add(TrackpadMenuAdapter.MenuItem("Anchor (Lock Position)", 
            if(p.prefAnchored) R.drawable.ic_lock_closed else R.drawable.ic_lock_open, 
            TrackpadMenuAdapter.Type.TOGGLE, 
            if(p.prefAnchored) 1 else 0) { v ->
            service.updatePref("anchored", v)
            loadTab(TAB_MAIN)  // Refresh to update icon
        })
        // --- END ANCHOR TOGGLE ---
        
        list.add(TrackpadMenuAdapter.MenuItem("Toggle Keyboard", R.drawable.ic_tab_keyboard, TrackpadMenuAdapter.Type.ACTION) { service.toggleCustomKeyboard() })
        list.add(TrackpadMenuAdapter.MenuItem("Reset Cursor", android.R.drawable.ic_menu_rotate, TrackpadMenuAdapter.Type.ACTION) { service.resetCursorCenter() })
        list.add(TrackpadMenuAdapter.MenuItem("Hide App", android.R.drawable.ic_menu_close_clear_cancel, TrackpadMenuAdapter.Type.ACTION) { service.hideApp() })
        list.add(TrackpadMenuAdapter.MenuItem("Force Kill Service", android.R.drawable.ic_delete, TrackpadMenuAdapter.Type.ACTION) { service.forceExit() })
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
                "CENTER" -> service.resetTrackpadPosition()
            }
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Rotate 90°", android.R.drawable.ic_menu_rotate, 
            TrackpadMenuAdapter.Type.ACTION) { service.performRotation() })
            
        if (isKeyboard) {
            val p = service.prefs
            list.add(TrackpadMenuAdapter.MenuItem("Keyboard Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefKeyboardAlpha) { v ->
                service.updatePref("keyboard_alpha", v)
            })
        }
            
        return list
    }

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
    // Contains Keyboard Opacity, Scale, and Auto Display Off
    // =========================
    private fun getTuneItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("KEYBOARD SETTINGS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // Max 255 for Alpha
        list.add(TrackpadMenuAdapter.MenuItem("Keyboard Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefKeyboardAlpha, 255) { v -> service.updatePref("keyboard_alpha", v) })
        // Max 200 for Scale (200%)
        list.add(TrackpadMenuAdapter.MenuItem("Keyboard Scale", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefKeyScale, 200) { v -> service.updatePref("keyboard_key_scale", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Auto Display Off", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefAutomationEnabled) 1 else 0) { v -> service.updatePref("automation_enabled", v) })
        return list
    }
    // =========================
    // END GET TUNE ITEMS
    // =========================

    // =========================
    // HARDKEY ACTIONS LIST
    // =========================
    private val hardkeyActions = listOf(
        "none" to "None",
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
        // NEW ACTIONS
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

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
```kotlin
package com.example.coverscreentester

/*
 * ======================================================================================
 * CRITICAL REGRESSION CHECKLIST
 * ======================================================================================
 * 1. SCROLL: Edge Zones (15%) + Tap/Hold logic preserved.
 * 2. DRAG: Long Press (400ms) + SOURCE_TOUCHSCREEN injection preserved.
 * 3. CURSOR: SOURCE_MOUSE for hover. No offsets.
 * 4. KEYS: Vol Up/Down preserved.
 * 5. SAFETY: Shell checks + Vibrate preserved.
 * 6. PREFS: Safe parsing preserved.
 * 7. REMOTE: cycleInputTarget creates remote cursor on secondary context.
 * 8. AUTOMATION: Keyboard Open = Screen ON. Keyboard Close = Screen OFF (Alt/Std).
 * 9. MENU ACTIONS: hideApp() minimizes, forceExit() kills process.
 * 10. ENTRY POINT: "OPEN_MENU" action triggers menuManager.show().
 * 11. FULL SCREEN: NO_LIMITS + SHORT_EDGES flags required for cursor to reach edges.
 * 12. PRESETS: applyLayoutPreset(1|2|0) supported. 0 is Freeform.
 * 13. LAUNCH: FORCE_MOVE extra forces UI refresh on current display.
 * 14. DISPLAY CONTEXT: WindowManager MUST be obtained from display-specific context.
 * 15. FOREGROUND: Safe startForeground with try-catch fallback.
 * 16. MULTI-DISPLAY: Teardown old views before switching displays.
 * 17. BUBBLE: resetBubblePosition() added.
 * 18. HANDLE: Size scaled 2x, Touch size synced.
 * 19. NO VIBRATE: Removed debug vibrations from menu/toggles.
 * 20. SCROLL FIX: performSwipe restored. ACTION_DOWN checks prefTapScroll.
 * 21. MANUAL ADJUST: manualAdjust() uses separate Move vs Resize logic via Toggle.
 * ======================================================================================
 */

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
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import java.util.ArrayList
import com.example.coverscreentester.BuildConfig

class OverlayService : AccessibilityService(), DisplayManager.DisplayListener {

    var windowManager: WindowManager? = null
    var displayManager: DisplayManager? = null
    var shellService: IShellService? = null
    private var appWindowManager: WindowManager? = null // Fix: Dedicated WM for App Overlays
    private var isBound = false
    private val handler = Handler(Looper.getMainLooper())

    private var bubbleView: View? = null
    private var trackpadLayout: FrameLayout? = null
    private var cursorLayout: FrameLayout? = null
    private var cursorView: ImageView? = null
    
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var trackpadParams: WindowManager.LayoutParams
    private lateinit var cursorParams: WindowManager.LayoutParams

    private var menuManager: TrackpadMenuManager? = null
    private var keyboardOverlay: KeyboardOverlay? = null

    var currentDisplayId = 0
    var inputTargetDisplayId = 0
    var isTrackpadVisible = true
    var isCustomKeyboardVisible = false
    var isScreenOff = false
    private var isPreviewMode = false
    
    // =========================
    // PREFS CLASS - Stores all user preferences
    // Contains anchor mode to lock trackpad/keyboard position
    // Contains hardkey binding mappings for Vol Up/Down and Power
    // =========================
    class Prefs {
        var cursorSpeed = 2.5f
        var scrollSpeed = 1.0f 
        var prefTapScroll = true 
        var prefVibrate = false
        var prefReverseScroll = true
        var prefAlpha = 200             // Used for Border/Stroke (in future updates) or general view alpha
        var prefBgAlpha = 0             // NEW: Background Fill Opacity (0-255)
        var prefKeyboardAlpha = 200
        var prefHandleSize = 60 
        var prefVPosLeft = false
        var prefHPosTop = false
        var prefLocked = false
        var prefHandleTouchSize = 80
        var prefScrollTouchSize = 80 
        var prefScrollVisualSize = 4
        var prefCursorSize = 50 
        var prefKeyScale = 100 
        var prefUseAltScreenOff = true
        var prefAutomationEnabled = true
        var prefBubbleX = 50
        var prefBubbleY = 300
        var prefAnchored = false 
        var prefBubbleSize = 100        
        var prefBubbleIconIndex = 0     
        var prefBubbleAlpha = 255       
        var prefPersistentService = false 
        
        // Hardkeys
        var hardkeyVolUpTap = "left_click"
        var hardkeyVolUpDouble = "none"
        var hardkeyVolUpHold = "left_click"
        var hardkeyVolDownTap = "right_click"
        var hardkeyVolDownDouble = "display_toggle"
        var hardkeyVolDownHold = "alt_position"
        var hardkeyPowerDouble = "none"
        var doubleTapMs = 300           
        var holdDurationMs = 400        
        var displayOffMode = "alternate" 
    }
    // =========================
    // END PREFS CLASS
    // =========================
  val prefs = Prefs()

    private var uiScreenWidth = 1080
    private var uiScreenHeight = 2640
    private var targetScreenWidth = 1920
    private var targetScreenHeight = 1080
    private var cursorX = 300f
    private var cursorY = 300f
    private var virtualScrollX = 0f
    private var virtualScrollY = 0f
    private var scrollAccumulatorX = 0f
    private var scrollAccumulatorY = 0f
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
    private var activeFingerDeviceId = -1
    private var pendingDragDownTime: Long = 0L
    private var ignoreTouchSequence = false
    private var isDebugMode = false
    private var isKeyboardMode = false
    private var savedWindowX = 0
    private var savedWindowY = 0
    private var currentBorderColor = 0xFFFFFFFF.toInt()
    private var highlightAlpha = false
    private var highlightHandles = false
    private var highlightScrolls = false
    // =========================
    // TOUCH TIMING VARIABLES - For click detection and release debounce
    // =========================
    private var touchDownTime: Long = 0L          // When finger touched down
    private var touchDownX: Float = 0f            // Where finger touched down (X)
    private var touchDownY: Float = 0f            // Where finger touched down (Y)
    private var isReleaseDebouncing = false       // True during post-release cooldown
    private val releaseDebounceRunnable = Runnable { isReleaseDebouncing = false }
    
    // === FINE TUNING: Adjust these for click sensitivity ===
    private val TAP_TIMEOUT_MS = 300L             // Max time for tap (ms) - longer = click
    private val TAP_SLOP_PX = 15f                 // Max movement for tap (px) - more = drag
    private val RELEASE_DEBOUNCE_MS = 50L         // Cooldown after release (ms)
    // === END FINE TUNING ===
    // =========================
    // END TOUCH TIMING VARIABLES
    // =========================

    // Scroll zone touch detection thickness (synced with prefScrollTouchSize)
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
    private var currentOverlayDisplayId = 0
    private var lastLoadedProfileKey = ""

    private val longPressRunnable = Runnable { startTouchDrag() }
    private var isResizing = false
    private val resizeLongPressRunnable = Runnable { startResize() }
    private var isMoving = false
    private val moveLongPressRunnable = Runnable { startMove() }
    private val voiceRunnable = Runnable { toggleKeyboardMode() }
    private var keyboardHandleDownTime = 0L
    private val keyboardLongPressRunnable = Runnable { toggleKeyboardMode() }
    private val clearHighlightsRunnable = Runnable { highlightAlpha = false; highlightHandles = false; highlightScrolls = false; updateBorderColor(currentBorderColor); updateLayoutSizes() }
    
    // --- KEY DRAG STATE ---
    private var isKeyDragging = false
    private var activeDragButton = MotionEvent.BUTTON_PRIMARY
    // ---------------------
    
    // =========================
    // HARDKEY DETECTION STATE - Variables for tap/double-tap/hold detection
    // Used by onKeyEvent() to distinguish between different gesture types
    // =========================
    private var lastVolUpTime: Long = 0L
    private var lastVolDownTime: Long = 0L
    private var lastPowerTime: Long = 0L
    private var volUpTapCount = 0
    private var volDownTapCount = 0
    private var powerTapCount = 0
    private var volUpHoldTriggered = false
    private var volDownHoldTriggered = false
    private var volUpDragActive = false   // Track if we're currently dragging
    private var volDownDragActive = false // Track if right-drag is active
    
    // Hold detection runnables - trigger hold action after delay
    private val volUpHoldRunnable = Runnable {
        android.util.Log.d("OverlayService", "volUpHoldRunnable FIRED! Setting volUpHoldTriggered=true, calling executeHardkeyAction(${prefs.hardkeyVolUpHold}, DOWN)")
        volUpHoldTriggered = true
        // Execute DOWN phase of the hold action (starts drag if mapped to left_click)
        executeHardkeyAction(prefs.hardkeyVolUpHold, KeyEvent.ACTION_DOWN)
    }
    private val volDownHoldRunnable = Runnable {
        android.util.Log.d("OverlayService", "volDownHoldRunnable FIRED! Setting volDownHoldTriggered=true")
        volDownHoldTriggered = true
        // Execute DOWN phase of the hold action
        executeHardkeyAction(prefs.hardkeyVolDownHold, KeyEvent.ACTION_DOWN)
    }
    
    // Double-tap detection runnables - execute single tap if no second tap arrives
    private val volUpDoubleTapRunnable = Runnable {
        if (volUpTapCount == 1) {
            executeHardkeyAction(prefs.hardkeyVolUpTap)
        }
        volUpTapCount = 0
    }
    private val volDownDoubleTapRunnable = Runnable {
        if (volDownTapCount == 1) {
            executeHardkeyAction(prefs.hardkeyVolDownTap)
        }
        volDownTapCount = 0
    }
    private val powerDoubleTapRunnable = Runnable {
        // Power single-tap is handled by system, we only care about double-tap
        powerTapCount = 0
    }
    // =========================
    // END HARDKEY DETECTION STATE
    // =========================

    
    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                "RESET_CURSOR" -> resetCursorCenter()
                "TOGGLE_DEBUG" -> toggleDebugMode()
                "FORCE_KEYBOARD" -> toggleCustomKeyboard()
                "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                "OPEN_MENU" -> {
                    menuManager?.show()
                    // Enforce Stack: Menu just added, so move Bubble/Cursor to top
                    enforceZOrder()
                }
                "SET_TRACKPAD_VISIBILITY" -> {
                    val visible = intent.getBooleanExtra("VISIBLE", true)
                    val menuDisplayId = intent.getIntExtra("MENU_DISPLAY_ID", -1)
                    if (visible) setTrackpadVisibility(true) 
                    else { if (menuDisplayId == -1 || menuDisplayId == currentDisplayId) setTrackpadVisibility(false) }
                }
                "SET_PREVIEW_MODE" -> {
                    val preview = intent.getBooleanExtra("PREVIEW_MODE", false)
                    setPreviewMode(preview)
                }
            }
        }
    }

    companion object {
        private const val TAG = "OverlayService"
        private const val BASE_SWIPE_DISTANCE = 200f
        private const val SCROLL_THRESHOLD = 5f
        private const val DRAG_MOVEMENT_THRESHOLD = 20
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
            Thread { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") }.start()
            initCustomKeyboard()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            shellService = null
            isBound = false
            updateBubbleStatus()
        }
    }

    override fun onCreate() {
        super.onCreate()
        try { displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; displayManager?.registerDisplayListener(this, handler) } catch (e: Exception) {}
        loadPrefs()
        val filter = IntentFilter().apply { 
            addAction("CYCLE_INPUT_TARGET")
            addAction("RESET_CURSOR")
            addAction("TOGGLE_DEBUG")
            addAction("FORCE_KEYBOARD")
            addAction("TOGGLE_CUSTOM_KEYBOARD")
            addAction("SET_TRACKPAD_VISIBILITY")
            addAction("SET_PREVIEW_MODE") 
            addAction("OPEN_MENU")
        }
        ContextCompat.registerReceiver(this, switchReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        bindShizuku()
        setupUI(Display.DEFAULT_DISPLAY)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try { createNotification() } catch(e: Exception){ e.printStackTrace() }
        
        try {
            when (intent?.action) {
                "RESET_POSITION" -> resetTrackpadPosition()
                "ROTATE" -> performRotation()
                "SAVE_LAYOUT" -> saveLayout()
                "LOAD_LAYOUT" -> loadLayout()
                "DELETE_PROFILE" -> deleteCurrentProfile()
                "MANUAL_ADJUST" -> handleManualAdjust(intent)
                "RELOAD_PREFS" -> { 
                    loadPrefs()
                    updateBorderColor(currentBorderColor)
                    updateLayoutSizes()
                    updateScrollPosition()
                    updateCursorSize()
                    keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha)
                    if (isCustomKeyboardVisible) { toggleCustomKeyboard(); toggleCustomKeyboard() } 
                }
                "PREVIEW_UPDATE" -> handlePreview(intent)
                "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                "RESET_CURSOR" -> resetCursorCenter()
                "TOGGLE_DEBUG" -> toggleDebugMode()
                "FORCE_KEYBOARD" -> toggleCustomKeyboard()
                "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                "OPEN_MENU" -> {
                    menuManager?.show()
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
        } catch (e: Exception) { e.printStackTrace() }
        return START_STICKY
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
    // =========================
    // ON KEY EVENT - Hardware key handler with configurable bindings
    // DEBUG VERSION - Added logging to trace flow
    // =========================
    override fun onKeyEvent(event: KeyEvent): Boolean {
        val TAG = "OverlayService"
        
        // DEBUG: Log every key event received
        android.util.Log.d(TAG, "onKeyEvent: keyCode=${event.keyCode}, action=${event.action}, isPreviewMode=$isPreviewMode, isTrackpadVisible=$isTrackpadVisible")
        
        if (isPreviewMode || !isTrackpadVisible) {
            android.util.Log.d(TAG, "onKeyEvent: BLOCKED - preview=$isPreviewMode visible=$isTrackpadVisible")
            return super.onKeyEvent(event)
        }
        
        val action = event.action
        val keyCode = event.keyCode
        
        // =========================
        // VOLUME UP
        // =========================
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            android.util.Log.d(TAG, "VOL_UP: action=$action, isLeftKeyHeld=$isLeftKeyHeld, volUpHoldTriggered=$volUpHoldTriggered")
            
            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    if (!isLeftKeyHeld) {
                        isLeftKeyHeld = true
                        volUpHoldTriggered = false
                        android.util.Log.d(TAG, "VOL_UP DOWN: Posting hold runnable, delay=${prefs.holdDurationMs}ms, action=${prefs.hardkeyVolUpHold}")
                        handler.postDelayed(volUpHoldRunnable, prefs.holdDurationMs.toLong())
                    } else {
                        android.util.Log.d(TAG, "VOL_UP DOWN: Already held, ignoring")
                    }
                }
                KeyEvent.ACTION_UP -> {
                    isLeftKeyHeld = false
                    handler.removeCallbacks(volUpHoldRunnable)
                    android.util.Log.d(TAG, "VOL_UP UP: volUpHoldTriggered=$volUpHoldTriggered")
                    
                    if (volUpHoldTriggered) {
                        android.util.Log.d(TAG, "VOL_UP UP: Hold was triggered, executing UP action")
                        executeHardkeyAction(prefs.hardkeyVolUpHold, KeyEvent.ACTION_UP)
                    } else {
                        android.util.Log.d(TAG, "VOL_UP UP: Was a tap, processing tap/double-tap")
                        val timeSinceLastTap = System.currentTimeMillis() - lastVolUpTime
                        lastVolUpTime = System.currentTimeMillis()
                        
                        if (timeSinceLastTap < prefs.doubleTapMs && volUpTapCount == 1) {
                            handler.removeCallbacks(volUpDoubleTapRunnable)
                            volUpTapCount = 0
                            android.util.Log.d(TAG, "VOL_UP: Double-tap detected")
                            executeHardkeyAction(prefs.hardkeyVolUpDouble, KeyEvent.ACTION_UP)
                        } else {
                            volUpTapCount = 1
                            handler.removeCallbacks(volUpDoubleTapRunnable)
                            handler.postDelayed(volUpDoubleTapRunnable, prefs.doubleTapMs.toLong())
                            android.util.Log.d(TAG, "VOL_UP: Single tap pending, waiting for double-tap window")
                        }
                    }
                }
            }
            return true
        }

        // =========================
        // VOLUME DOWN
        // =========================
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            android.util.Log.d(TAG, "VOL_DOWN: action=$action, isRightKeyHeld=$isRightKeyHeld, volDownHoldTriggered=$volDownHoldTriggered")
            
            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    if (!isRightKeyHeld) {
                        isRightKeyHeld = true
                        volDownHoldTriggered = false
                        android.util.Log.d(TAG, "VOL_DOWN DOWN: Posting hold runnable, delay=${prefs.holdDurationMs}ms")
                        handler.postDelayed(volDownHoldRunnable, prefs.holdDurationMs.toLong())
                    }
                }
                KeyEvent.ACTION_UP -> {
                    isRightKeyHeld = false
                    handler.removeCallbacks(volDownHoldRunnable)
                    android.util.Log.d(TAG, "VOL_DOWN UP: volDownHoldTriggered=$volDownHoldTriggered")
                    
                    if (volDownHoldTriggered) {
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
    // =========================
    // END ON KEY EVENT
    // =========================

    
    private fun setupUI(displayId: Int) {
        try {
            if (windowManager != null) {
                if (bubbleView != null) windowManager?.removeView(bubbleView)
                if (cursorLayout != null) windowManager?.removeView(cursorLayout)
            }
            // Use same manager for trackpad now
            if (trackpadLayout != null && windowManager != null) {
                 try { windowManager?.removeView(trackpadLayout) } catch(e: Exception){}
            }
            
            keyboardOverlay?.hide()
            keyboardOverlay = null
            menuManager?.hide()
            menuManager = null
        } catch (e: Exception) {}

        val display = displayManager?.getDisplay(displayId)
        if (display == null) {
            showToast("Error: Display $displayId not found")
            return
        }

        try {
            val displayContext = createDisplayContext(display)

            // CHANGED: Use Accessibility Overlay for EVERYTHING (High Z-Order)
            // This prevents system UI (Edge Panel, etc.) from drawing over the trackpad.
            val accessContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 displayContext.createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null)
            } else displayContext
            
            // Unified Window Manager
            windowManager = accessContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            appWindowManager = windowManager // Alias for compatibility

            currentDisplayId = displayId
            inputTargetDisplayId = displayId

            updateUiMetrics()

            // --- Z-ORDER SETUP (Bottom to Top) ---
            // Since all are Accessibility Overlays, order of adding matters.
            
            // 1. Trackpad (Bottom)
            setupTrackpad(accessContext)
            
            // 2. Keyboard (Initialized hidden) - Will pop to top when shown, requires re-stacking
            if (shellService != null) initCustomKeyboard()
            
            // 3. Menu (Initialized hidden)
            menuManager = TrackpadMenuManager(displayContext, windowManager!!, this)

            // 4. Bubble
            setupBubble(accessContext)
            
            // 5. Cursor
            setupCursor(accessContext)

            enforceZOrder()
            showToast("Trackpad active on Display $displayId")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup UI on display $displayId", e)
            showToast("Failed to launch on display $displayId")
        }
    }

    private fun setupBubble(context: Context) {
        bubbleView = LayoutInflater.from(context).inflate(R.layout.layout_trackpad_bubble, null)
        
        // XML background now handles the styling (Grey Border/Dark Grey Fill)
        // No programmatic override needed.
        
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
        
        // --- FIX: Long Press State Management ---
        var isLongPressHandled = false
        
        val bubbleLongPressRunnable = Runnable {
            if (!isDrag) {
                vibrate()
                menuManager?.toggle()
                isLongPressHandled = true
                
                // Z-ORDER FIX: Delay slightly to allow Menu to attach, then force Bubble to top
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
                    
                    // Z-ORDER SAFETY: Ensure Z-order is correct on release 
                    // (Fixes cases where WindowManager blocks updates during active touch)
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
            // CHANGED: Revert to Accessibility Overlay to block System UI interaction if needed
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

    // =========================
    // TOGGLE TRACKPAD - Hides/Shows the main trackpad overlay
    // Updated: Automatically hides keyboard (without screen off) when minimizing
    // =========================
    fun toggleTrackpad() { 
        isTrackpadVisible = !isTrackpadVisible
        trackpadLayout?.visibility = if (isTrackpadVisible) View.VISIBLE else View.GONE
        
        if (isTrackpadVisible) {
            updateBorderColor(currentBorderColor) 
        } else {
            // Hiding trackpad (Minimizing via Bubble)
            // If keyboard is visible, hide it too, but SUPPRESS the "Screen Off" automation
            // because we are just minimizing the UI, not finishing a task.
            if (isCustomKeyboardVisible) {
                toggleCustomKeyboard(suppressAutomation = true)
            }
        }
    }
    
    // ===============================================================
    // FUNCTION: handleBubbleTap
    // PURPOSE: Hides everything without triggering screen off automation
    // ===============================================================
    private fun handleBubbleTap() {
        var didHideSomething = false

        // 1. Hide Keyboard if visible (Bypass automation)
        if (isCustomKeyboardVisible) {
            toggleCustomKeyboard(suppressAutomation = true)
            didHideSomething = true
        }

        // 2. Toggle Trackpad normally
        // If trackpad was visible, we hide it. 
        // If trackpad was hidden, we show it (unless we just hid the keyboard, in which case we might want to just clear the screen? 
        // Standard behavior: Toggle Trackpad state.
        
        toggleTrackpad() 
    }
    
    // =========================
    // EXECUTE HARDKEY ACTION - Central dispatcher for all hardkey-triggered actions
    // Called by onKeyEvent() when a tap/double-tap/hold gesture is detected
    // Action IDs map to specific behaviors defined in HARDKEY_ACTIONS
    // =========================
    private fun executeHardkeyAction(actionId: String, keyEventAction: Int = KeyEvent.ACTION_UP) {
        // Only trigger single-shot actions on ACTION_UP to avoid repeating
        val isUp = (keyEventAction == KeyEvent.ACTION_UP)
        
        when (actionId) {
            "none" -> { /* Do nothing */ }
            
            "left_click" -> {
                if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volUpDragActive) { 
                        volUpDragActive = true
                        startKeyDrag(MotionEvent.BUTTON_PRIMARY)
                    }
                } else {
                    if (volUpDragActive) {
                        volUpDragActive = false
                        stopKeyDrag(MotionEvent.BUTTON_PRIMARY)
                    } else {
                        performClick(false) // Left Click
                    }
                }
            }
            
            "right_click" -> {
                 if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volDownDragActive) {
                        volDownDragActive = true
                        startKeyDrag(MotionEvent.BUTTON_SECONDARY)
                    }
                } else {
                    if (volDownDragActive) {
                        volDownDragActive = false
                        stopKeyDrag(MotionEvent.BUTTON_SECONDARY)
                    } else {
                        performClick(true) // Right Click
                    }
                }
            }
            
            // --- NEW NAVIGATION ACTIONS ---
            "action_back" -> {
                if (isUp) {
                    Thread { try { shellService?.injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN, 0, currentDisplayId); Thread.sleep(20); shellService?.injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP, 0, currentDisplayId) } catch(e: Exception){} }.start()
                }
            }
            "action_home" -> {
                if (isUp) {
                    Thread { try { shellService?.injectKey(KeyEvent.KEYCODE_HOME, KeyEvent.ACTION_DOWN, 0, currentDisplayId); Thread.sleep(20); shellService?.injectKey(KeyEvent.KEYCODE_HOME, KeyEvent.ACTION_UP, 0, currentDisplayId) } catch(e: Exception){} }.start()
                }
            }
            "action_forward" -> {
                if (isUp) {
                    Thread { try { shellService?.injectKey(KeyEvent.KEYCODE_FORWARD, KeyEvent.ACTION_DOWN, 0, currentDisplayId); Thread.sleep(20); shellService?.injectKey(KeyEvent.KEYCODE_FORWARD, KeyEvent.ACTION_UP, 0, currentDisplayId) } catch(e: Exception){} }.start()
                }
            }
            "action_vol_up" -> {
                if (isUp) {
                    Thread { try { shellService?.injectKey(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.ACTION_DOWN, 0, currentDisplayId); Thread.sleep(20); shellService?.injectKey(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.ACTION_UP, 0, currentDisplayId) } catch(e: Exception){} }.start()
                }
            }
            "action_vol_down" -> {
                if (isUp) {
                    Thread { try { shellService?.injectKey(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.ACTION_DOWN, 0, currentDisplayId); Thread.sleep(20); shellService?.injectKey(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.ACTION_UP, 0, currentDisplayId) } catch(e: Exception){} }.start()
                }
            }
            // --- END NEW ACTIONS ---

            "scroll_up" -> {
                if (isUp) {
                    val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed
                    performSwipe(0f, -dist)
                }
            }
            "scroll_down" -> {
                if (isUp) {
                    val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed
                    performSwipe(0f, dist)
                }
            }
            
            "display_toggle" -> {
                if (isUp) {
                    when (prefs.displayOffMode) {
                        "standard" -> {
                            isScreenOff = !isScreenOff
                            Thread { try { if (isScreenOff) shellService?.setScreenOff(0, true) else shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start()
                            showToast("Display ${if(isScreenOff) "Off" else "On"} (Std)")
                        }
                        else -> {
                            isScreenOff = !isScreenOff
                            Thread { try { if (isScreenOff) shellService?.setBrightness(-1) else shellService?.setBrightness(128) } catch (e: Exception) {} }.start()
                            showToast("Display ${if(isScreenOff) "Off" else "On"} (Alt)")
                        }
                    }
                }
            }
            
            "display_toggle_alt" -> {
                if (isUp) {
                    isScreenOff = !isScreenOff
                    Thread { try { if (isScreenOff) shellService?.setBrightness(-1) else shellService?.setBrightness(128) } catch (e: Exception) {} }.start()
                    showToast("Display ${if(isScreenOff) "Off" else "On"} (Alt)")
                }
            }
            
            "display_toggle_std" -> {
                if (isUp) {
                    isScreenOff = !isScreenOff
                    Thread { try { if (isScreenOff) shellService?.setScreenOff(0, true) else shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start()
                    showToast("Display ${if(isScreenOff) "Off" else "On"} (Std)")
                }
            }
            
            "alt_position" -> { if (isUp) toggleKeyboardMode() }
            "toggle_keyboard" -> { if (isUp) toggleCustomKeyboard() }
            "toggle_trackpad" -> { if (isUp) toggleTrackpad() }
            "open_menu" -> { if (isUp) menuManager?.toggle() }
            "reset_cursor" -> { if (isUp) resetCursorCenter() }
            "display_wake" -> { if (isUp && isScreenOff) { isScreenOff = false; Thread { try { shellService?.setBrightness(128); shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start(); showToast("Display Woken") } }
        }
    }
    // =========================
    // END EXECUTE HARDKEY ACTION
    // =========================

    fun toggleKeyboardMode() { vibrate(); isRightDragPending = false; if (!isKeyboardMode) { isKeyboardMode = true; savedWindowX = trackpadParams.x; savedWindowY = trackpadParams.y; trackpadParams.x = uiScreenWidth - trackpadParams.width; trackpadParams.y = 0; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(0xFFFF0000.toInt()) } else { isKeyboardMode = false; trackpadParams.x = savedWindowX; trackpadParams.y = savedWindowY; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(currentBorderColor) } }
    
    private fun toggleDebugMode() { isDebugMode = !isDebugMode; if (isDebugMode) { showToast("Debug ON"); updateBorderColor(0xFFFFFF00.toInt()); debugTextView?.visibility = View.VISIBLE } else { showToast("Debug OFF"); if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()); debugTextView?.visibility = View.GONE } }

    fun updateBubbleStatus() { val dot = bubbleView?.findViewById<ImageView>(R.id.status_dot); if (shellService != null) dot?.visibility = View.GONE else dot?.visibility = View.VISIBLE }

    // =========================
    // BUBBLE CUSTOMIZATION - Icon, Size, and Opacity
    // =========================
    
    // Available bubble icons (add more as needed)
    private val bubbleIcons = arrayOf(
        R.mipmap.ic_trackpad_adaptive,     // Default trackpad icon
        R.drawable.ic_cursor,               // Cursor icon
        R.drawable.ic_tab_main,             // Main tab icon
        R.drawable.ic_tab_keyboard,         // Keyboard icon
        android.R.drawable.ic_menu_compass, // System compass
        android.R.drawable.ic_menu_myplaces // System location
    )
    
    fun getBubbleIconCount(): Int = bubbleIcons.size
    
    fun updateBubbleSize(sizePercent: Int) {
        prefs.prefBubbleSize = sizePercent.coerceIn(50, 200)
        applyBubbleAppearance()
        savePrefs()
    }
    
    fun updateBubbleIcon(index: Int) {
        prefs.prefBubbleIconIndex = index.coerceIn(0, bubbleIcons.size - 1)
        applyBubbleAppearance()
        savePrefs()
    }
    
    fun cycleBubbleIcon() {
        val nextIndex = (prefs.prefBubbleIconIndex + 1) % bubbleIcons.size
        updateBubbleIcon(nextIndex)
    }
    
    fun updateBubbleAlpha(alpha: Int) {
        prefs.prefBubbleAlpha = alpha.coerceIn(50, 255)
        applyBubbleAppearance()
        savePrefs()
    }
    
    private fun applyBubbleAppearance() {
        if (bubbleView == null) return
        
        val scale = prefs.prefBubbleSize / 100f
        
        // Base sizes (Updated to match Launcher: 60dp container, 40dp icon)
        val baseContainerDp = 60
        val baseIconDp = 40
        
        val density = resources.displayMetrics.density
        val containerSize = (baseContainerDp * scale * density).toInt()
        val iconSize = (baseIconDp * scale * density).toInt()
        
        // Update container size via LayoutParams
        bubbleParams.width = containerSize
        bubbleParams.height = containerSize
        try {
            windowManager?.updateViewLayout(bubbleView, bubbleParams)
        } catch (e: Exception) {}
        
        // Update icon
        val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon)
        iconView?.let {
            val iconParams = it.layoutParams as? FrameLayout.LayoutParams
            // Force Gravity Center to ensure icon stays centered in the resized bubble
            iconParams?.gravity = Gravity.CENTER
            iconParams?.width = iconSize
            iconParams?.height = iconSize
            it.layoutParams = iconParams
            
            // Set icon drawable
            val iconRes = bubbleIcons.getOrElse(prefs.prefBubbleIconIndex) { bubbleIcons[0] }
            it.setImageResource(iconRes)
            
            // Set alpha (0.0-1.0)
            it.alpha = prefs.prefBubbleAlpha / 255f
        }
        
        // Also apply alpha to background
        bubbleView?.alpha = prefs.prefBubbleAlpha / 255f
    }
    // =========================
    // END BUBBLE CUSTOMIZATION
    // =========================


    fun forceMoveToCurrentDisplay() { setupUI(currentDisplayId) }
    fun forceMoveToDisplay(displayId: Int) { 
        if (displayId == currentDisplayId) return // Optimization: Don't teardown if already there
        setupUI(displayId) 
    }
    
    // --- UPDATED HIDE APP LOGIC ---
    fun hideApp() { 
        menuManager?.hide()
        if (isTrackpadVisible) toggleTrackpad()
    }
    
    fun getSavedProfileList(): List<String> {
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val allKeys = p.all.keys
        val profiles = java.util.HashSet<String>()
        val regex = Regex("X_P_(\\d+)_(\\d+)") // Matches X_P_1080_2640 (Position X key)
        
        for (key in allKeys) {
            val match = regex.find(key)
            if (match != null) {
                val w = match.groupValues[1]
                val h = match.groupValues[2]
                profiles.add("$w x $h")
            }
        }
        return profiles.sorted()
    }

    // =========================
    // ON TASK REMOVED - Handles app swipe-away behavior
    // If persistent mode is OFF, kill the service (and bubble)
    // =========================
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!prefs.prefPersistentService) {
            // "Auto Launch when Closed" is OFF -> Kill service
            Log.i(TAG, "Task removed and persistence OFF -> Stopping Service")
            forceExit()
        }
    }


    // --- NEW FORCE EXIT LOGIC ---
    fun forceExit() {
        try {
            stopSelf()
            Process.killProcess(Process.myPid())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // --- FIXED MANUAL ADJUST ---
    fun manualAdjust(isKeyboard: Boolean, isResize: Boolean, dx: Int, dy: Int) { 
        if (isKeyboard) { 
            if (isResize) keyboardOverlay?.resizeWindow(dx, dy) 
            else keyboardOverlay?.moveWindow(dx, dy) 
        } else { 
            if (trackpadLayout == null) return
            
            if (isResize) {
                // Resize: Only change width/height
                trackpadParams.width = max(200, trackpadParams.width + dx)
                trackpadParams.height = max(200, trackpadParams.height + dy)
            } else {
                // Move: Only change x/y
                trackpadParams.x += dx
                trackpadParams.y += dy
            }
            
            try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch (e: Exception) {}
            saveLayout() 
        } 
    }
    
    private fun parseBoolean(value: Any): Boolean { return when(value) { is Boolean -> value; is Int -> value == 1; is String -> value == "1" || value.equals("true", ignoreCase = true); else -> false } }
    
    // =========================
    // UPDATE PREF - Handles preference changes from menu
    // Includes anchor mode toggle and updates keyboard anchor state
    // =========================
    fun updatePref(key: String, value: Any) { 
        when(key) { 
            "cursor_speed" -> prefs.cursorSpeed = (value.toString().toFloatOrNull() ?: 2.5f)
            "scroll_speed" -> prefs.scrollSpeed = (value.toString().toFloatOrNull() ?: 1.0f)
            "tap_scroll" -> prefs.prefTapScroll = parseBoolean(value)
            "vibrate" -> prefs.prefVibrate = parseBoolean(value)
            "reverse_scroll" -> prefs.prefReverseScroll = parseBoolean(value)
            "alpha" -> { prefs.prefAlpha = (value.toString().toIntOrNull() ?: 200); updateBorderColor(currentBorderColor) }
            "bg_alpha" -> { prefs.prefBgAlpha = (value.toString().toIntOrNull() ?: 0); updateBorderColor(currentBorderColor) } // NEW
            "keyboard_alpha" -> { prefs.prefKeyboardAlpha = (value.toString().toIntOrNull() ?: 200); keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha) }
            "handle_size" -> { 
                val raw = (value.toString().toIntOrNull() ?: 60)
                val scaled = raw * 2 
                prefs.prefHandleSize = scaled
                prefs.prefHandleTouchSize = scaled + 20 
                updateHandleSize()
                updateLayoutSizes()
            }
            "scroll_size" -> {
                val touchSize = (value.toString().toIntOrNull() ?: 80)
                val visualSize = (touchSize * 8 / 80).coerceIn(4, 20)
                prefs.prefScrollTouchSize = touchSize
                prefs.prefScrollVisualSize = visualSize
                scrollZoneThickness = touchSize
                updateScrollSize()
            }
            "cursor_size" -> { prefs.prefCursorSize = (value.toString().toIntOrNull() ?: 50); updateCursorSize() }
            "keyboard_key_scale" -> { prefs.prefKeyScale = (value.toString().toIntOrNull() ?: 100); keyboardOverlay?.updateScale(prefs.prefKeyScale / 100f) }
            "use_alt_screen_off" -> prefs.prefUseAltScreenOff = parseBoolean(value) 
            "automation_enabled" -> prefs.prefAutomationEnabled = parseBoolean(value)
            "anchored" -> { 
                prefs.prefAnchored = parseBoolean(value)
                keyboardOverlay?.setAnchored(prefs.prefAnchored)
            }
            "bubble_size" -> { prefs.prefBubbleSize = (value.toString().toIntOrNull() ?: 100); applyBubbleAppearance() }
            "bubble_icon" -> { cycleBubbleIcon() }
            "bubble_alpha" -> { prefs.prefBubbleAlpha = (value.toString().toIntOrNull() ?: 255); applyBubbleAppearance() }
            "persistent_service" -> prefs.prefPersistentService = parseBoolean(value)
            
            // Hardkey Timing
            "double_tap_ms" -> { prefs.doubleTapMs = (value.toString().toIntOrNull() ?: 300).coerceIn(150, 500) }
            "hold_duration_ms" -> { prefs.holdDurationMs = (value.toString().toIntOrNull() ?: 400).coerceIn(200, 800) }
            "display_off_mode" -> { prefs.displayOffMode = value.toString() }
            
            // Hardkey Actions
            "hardkey_vol_up_tap" -> { prefs.hardkeyVolUpTap = value.toString() }
            "hardkey_vol_up_double" -> { prefs.hardkeyVolUpDouble = value.toString() }
            "hardkey_vol_up_hold" -> { prefs.hardkeyVolUpHold = value.toString() }
            "hardkey_vol_down_tap" -> { prefs.hardkeyVolDownTap = value.toString() }
            "hardkey_vol_down_double" -> { prefs.hardkeyVolDownDouble = value.toString() }
            "hardkey_vol_down_hold" -> { prefs.hardkeyVolDownHold = value.toString() }
            "hardkey_power_double" -> { prefs.hardkeyPowerDouble = value.toString() }
        }
        savePrefs() 
    }
    // =========================
    // END UPDATE PREF
    // =========================
    
    // =========================
    // PRESETS LOGIC - Apply split screen layouts
    // Type 0 = Freeform (loads saved profile)
    // Type 1 = KB Top / TP Bottom
    // Type 2 = TP Top / KB Bottom
    // =========================
    fun applyLayoutPreset(type: Int) {
        if (type == 0) { // Freeform
            loadLayout()
            showToast("Freeform Profile Loaded")
            return
        }

        val h = uiScreenHeight
        val w = uiScreenWidth
        val density = resources.displayMetrics.density
        
        // 1. Width Logic (96% Centered - leaving ~2% border on each side)
        val targetW = (w * 0.96f).toInt()
        val marginX = (w - targetW) / 2
        
        // 2. Height Logic (Dynamic Content-Based Calculation)
        // Instead of arbitrary %, we calculate exactly what the keyboard needs.
        // Base keys: 6 rows * 40dp = 240dp
        // Margins/Spacing/Handle: ~35dp
        // Total Base: ~275dp
        val scale = prefs.prefKeyScale / 100f
        val baseContentHeightDp = 275f 
        val calculatedKbHeight = (baseContentHeightDp * scale * density).toInt()
        
        // Safety: Clamp height to max 60% of screen to prevent it taking over completely on very tiny screens
        val kbHeight = calculatedKbHeight.coerceAtMost((h * 0.6f).toInt())
        
        // Trackpad takes whatever is left
        val tpHeight = h - kbHeight
        
        // Ensure keyboard exists and is visible so we can move it
        if (keyboardOverlay == null) initCustomKeyboard()
        if (!isCustomKeyboardVisible) toggleCustomKeyboard(suppressAutomation = true)
        
        // Ensure trackpad is visible
        if (!isTrackpadVisible) toggleTrackpad()
        
        // Reset scroll bar sizes for consistency
        prefs.prefScrollTouchSize = 80
        prefs.prefScrollVisualSize = 8

        when(type) {
            1 -> {
                // Preset 1: KB Top / TP Bottom
                
                // Keyboard: Top (0 to kbHeight)
                // Fits tightly to the content
                keyboardOverlay?.setWindowBounds(marginX, 0, targetW, kbHeight)
                
                // Trackpad: Bottom (kbHeight to Bottom)
                trackpadParams.width = targetW
                trackpadParams.height = tpHeight
                trackpadParams.x = marginX
                trackpadParams.y = kbHeight
            }
            2 -> {
                // Preset 2: TP Top / KB Bottom
                
                // Trackpad: Top (0 to tpHeight)
                trackpadParams.width = targetW
                trackpadParams.height = tpHeight
                trackpadParams.x = marginX
                trackpadParams.y = 0
                
                // Keyboard: Bottom (tpHeight to Bottom)
                // Fits tightly to the bottom edge
                keyboardOverlay?.setWindowBounds(marginX, tpHeight, targetW, kbHeight)
            }
        }
        
        // Apply updates to Trackpad Window
        try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(e: Exception){}
        
        // Refresh UI elements
        updateScrollSize()
        updateScrollPosition()
        updateHandleSize()
        updateLayoutSizes()
        
        // Save these temporary settings so they persist if app is restarted in this state
        savePrefs() 
        showToast("Preset Applied: ${w}x${h}")
    }
    // =========================
    // END PRESETS LOGIC
    // =========================
    
    fun resetBubblePosition() {
        bubbleParams.x = 50
        bubbleParams.y = uiScreenHeight / 2
        try {
            windowManager?.updateViewLayout(bubbleView, bubbleParams)
            prefs.prefBubbleX = bubbleParams.x
            prefs.prefBubbleY = bubbleParams.y
            savePrefs()
            showToast("Bubble Reset")
        } catch(e: Exception){}
    }

    // =========================
    // LOAD PREFS - Loads all preferences from SharedPreferences
    // Includes hardkey binding mappings and timing settings
    // =========================
    private fun loadPrefs() { 
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        prefs.cursorSpeed = p.getFloat("cursor_speed", 2.5f)
        prefs.scrollSpeed = p.getFloat("scroll_speed", 1.0f)
        prefs.prefTapScroll = p.getBoolean("tap_scroll", true)
        prefs.prefVibrate = p.getBoolean("vibrate", true)
        prefs.prefReverseScroll = p.getBoolean("reverse_scroll", true)
        prefs.prefAlpha = p.getInt("alpha", 200)
        prefs.prefBgAlpha = p.getInt("bg_alpha", 0) // NEW
        prefs.prefKeyboardAlpha = p.getInt("keyboard_alpha", 200)
        prefs.prefHandleSize = p.getInt("handle_size", 60)
        prefs.prefHandleTouchSize = p.getInt("handle_touch_size", 80)
        prefs.prefVPosLeft = p.getBoolean("v_pos_left", false)
        prefs.prefHPosTop = p.getBoolean("h_pos_top", false)
        prefs.prefLocked = p.getBoolean("lock_position", false)
        prefs.prefCursorSize = p.getInt("cursor_size", 50)
        prefs.prefKeyScale = p.getInt("keyboard_key_scale", 100)
        prefs.prefUseAltScreenOff = p.getBoolean("use_alt_screen_off", true)
        prefs.prefAutomationEnabled = p.getBoolean("automation_enabled", true)
        prefs.prefBubbleX = p.getInt("bubble_x", 50)
        prefs.prefBubbleY = p.getInt("bubble_y", 300)
        prefs.prefAnchored = p.getBoolean("anchored", false)
        prefs.prefBubbleSize = p.getInt("bubble_size", 100)
        prefs.prefBubbleIconIndex = p.getInt("bubble_icon_index", 0)
        prefs.prefBubbleAlpha = p.getInt("bubble_alpha", 255)
        prefs.prefScrollTouchSize = p.getInt("scroll_touch_size", 80)
        prefs.prefScrollVisualSize = p.getInt("scroll_visual_size", 8)
        prefs.prefPersistentService = p.getBoolean("persistent_service", false)
        
        // Hardkeys
        prefs.hardkeyVolUpTap = p.getString("hardkey_vol_up_tap", "left_click") ?: "left_click"
        prefs.hardkeyVolUpDouble = p.getString("hardkey_vol_up_double", "none") ?: "none"
        prefs.hardkeyVolUpHold = p.getString("hardkey_vol_up_hold", "left_click") ?: "left_click"
        prefs.hardkeyVolDownTap = p.getString("hardkey_vol_down_tap", "right_click") ?: "right_click"
        prefs.hardkeyVolDownDouble = p.getString("hardkey_vol_down_double", "display_toggle") ?: "display_toggle"
        prefs.hardkeyVolDownHold = p.getString("hardkey_vol_down_hold", "alt_position") ?: "alt_position"
        prefs.hardkeyPowerDouble = p.getString("hardkey_power_double", "none") ?: "none"
        prefs.doubleTapMs = p.getInt("double_tap_ms", 300)
        prefs.holdDurationMs = p.getInt("hold_duration_ms", 400)
        prefs.displayOffMode = p.getString("display_off_mode", "alternate") ?: "alternate"

        // MIGRATION (Keep existing logic)
        if (prefs.hardkeyVolUpHold == "left_drag") { prefs.hardkeyVolUpHold = "left_click"; p.edit().putString("hardkey_vol_up_hold", "left_click").apply() }
        if (prefs.hardkeyVolDownHold == "right_drag") { prefs.hardkeyVolDownHold = "right_click"; p.edit().putString("hardkey_vol_down_hold", "right_click").apply() }
        
        scrollZoneThickness = prefs.prefScrollTouchSize
        if (prefs.prefHandleSize > 300) prefs.prefHandleSize = 60
        if (prefs.prefHandleTouchSize > 300) prefs.prefHandleTouchSize = 80
    }
    // =========================
    // END LOAD PREFS
    // =========================
    private fun savePrefs() { 
        val e = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        e.putFloat("cursor_speed", prefs.cursorSpeed)
        e.putInt("alpha", prefs.prefAlpha)
        e.putInt("bg_alpha", prefs.prefBgAlpha) // NEW
        e.putInt("keyboard_alpha", prefs.prefKeyboardAlpha)
        e.putBoolean("use_alt_screen_off", prefs.prefUseAltScreenOff)
        e.putBoolean("automation_enabled", prefs.prefAutomationEnabled)
        e.putInt("bubble_x", prefs.prefBubbleX)
        e.putInt("bubble_y", prefs.prefBubbleY)
        e.putBoolean("anchored", prefs.prefAnchored)
        e.putInt("bubble_size", prefs.prefBubbleSize)
        e.putInt("bubble_icon_index", prefs.prefBubbleIconIndex)
        e.putInt("bubble_alpha", prefs.prefBubbleAlpha)
        e.putInt("scroll_touch_size", prefs.prefScrollTouchSize)
        e.putInt("scroll_visual_size", prefs.prefScrollVisualSize)
        e.putBoolean("persistent_service", prefs.prefPersistentService)
        e.putInt("keyboard_key_scale", prefs.prefKeyScale) // Fixed: Added missing persistence
        
        // Hardkeys
        e.putString("hardkey_vol_up_tap", prefs.hardkeyVolUpTap)
        e.putString("hardkey_vol_up_double", prefs.hardkeyVolUpDouble)
        e.putString("hardkey_vol_up_hold", prefs.hardkeyVolUpHold)
        e.putString("hardkey_vol_down_tap", prefs.hardkeyVolDownTap)
        e.putString("hardkey_vol_down_double", prefs.hardkeyVolDownDouble)
        e.putString("hardkey_vol_down_hold", prefs.hardkeyVolDownHold)
        e.putString("hardkey_power_double", prefs.hardkeyPowerDouble)
        e.putInt("double_tap_ms", prefs.doubleTapMs)
        e.putInt("hold_duration_ms", prefs.holdDurationMs)
        e.putString("display_off_mode", prefs.displayOffMode)
        
        e.apply() 
    }
    // =========================
    // END SAVE PREFS
    // =========================

    private fun bindShizuku() { try { val c = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(c, userServiceConnection, BuildConfig.DEBUG, BuildConfig.VERSION_CODE) } catch (e: Exception) { e.printStackTrace() } }
    
    // --- SAFE CREATE NOTIFICATION ---
    private fun createNotification() { 
        try {
            val channel = NotificationChannel("overlay_service", "Trackpad", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            val notif = Notification.Builder(this, "overlay_service")
                .setContentTitle("Trackpad Active")
                .setSmallIcon(R.drawable.ic_cursor)
                .build()
            
            // Try specific type first, fallback to generic if it crashes due to manifest mismatch
            try {
                if (Build.VERSION.SDK_INT >= 34) {
                    startForeground(1, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) 
                } else {
                    startForeground(1, notif)
                }
            } catch(e: Exception) {
                startForeground(1, notif)
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initCustomKeyboard() { 
        // FIX: Use appWindowManager for Keyboard (Application Overlay)
        if (appWindowManager == null || shellService == null) return
        // Updated Constructor: Pass toggleCustomKeyboard as the onCloseAction
        keyboardOverlay = KeyboardOverlay(this, appWindowManager!!, shellService, inputTargetDisplayId, { toggleScreen() }, { toggleScreenMode() }, { toggleCustomKeyboard() })
        keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentDisplayId) 
    }

    // --- KEYBOARD AUTOMATION FIX ---
    // =========================
    // TOGGLE CUSTOM KEYBOARD - Manages keyboard overlay visibility
    // Updated: Added suppressAutomation to prevent screen-off when minimizing app
    // =========================
    fun toggleCustomKeyboard(suppressAutomation: Boolean = false) {
        if (keyboardOverlay == null) initCustomKeyboard()

        val isNowVisible = if (keyboardOverlay?.isShowing() == true) {
            keyboardOverlay?.hide()
            false
        } else {
            keyboardOverlay?.show()
            true
        }

        isCustomKeyboardVisible = isNowVisible
        
        // --- IME TOGGLE ---
        // 0 = Hide soft keyboard (enable hard kb mode)
        // 1 = Show soft keyboard (disable hard kb mode)
        Thread { 
            val valStr = if (isNowVisible) "0" else "1"
            shellService?.runCommand("settings put secure show_ime_with_hard_keyboard $valStr") 
        }.start()
        // ------------------
        
        // Enforce Stack: Keyboard just added, so move Bubble/Cursor to top
        enforceZOrder()

        if (prefs.prefAutomationEnabled && !suppressAutomation) {
            if (isNowVisible) turnScreenOn() else turnScreenOff()
        }
    }

    // --- SCREEN CONTROL HELPERS ---
    private fun turnScreenOn() {
        isScreenOff = false
        Thread {
            try {
                shellService?.setBrightness(128)
                shellService?.setScreenOff(0, false)
            } catch(e: Exception) {}
        }.start()
        showToast("Screen On")
    }

    private fun turnScreenOff() {
        isScreenOff = true
        Thread {
            try {
                if (prefs.prefUseAltScreenOff) shellService?.setBrightness(-1) 
                else shellService?.setScreenOff(0, true)
            } catch(e: Exception) {}
        }.start()
        showToast("Screen Off (${if(prefs.prefUseAltScreenOff) "Alt" else "Std"})")
    }
    
    private fun toggleScreenMode() {
        prefs.prefUseAltScreenOff = !prefs.prefUseAltScreenOff
        savePrefs()
        showToast("Mode: ${if(prefs.prefUseAltScreenOff) "Alternate" else "Standard"}")
    }

    private fun toggleScreen() {
        if (isScreenOff) turnScreenOn() else turnScreenOff()
    }
    
    private fun updateUiMetrics() { val display = displayManager?.getDisplay(currentDisplayId) ?: return; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); uiScreenWidth = metrics.widthPixels; uiScreenHeight = metrics.heightPixels }
    private fun createTrackpadDisplayContext(display: Display): Context { return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) createDisplayContext(display).createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null) else createDisplayContext(display) }
    private fun addHandle(context: Context, gravity: Int, color: Int, onTouch: (View, MotionEvent) -> Boolean) { val container = FrameLayout(context); val p = FrameLayout.LayoutParams(prefs.prefHandleTouchSize, prefs.prefHandleTouchSize); p.gravity = gravity; val visual = View(context); val bg = GradientDrawable(); bg.setColor(color); bg.cornerRadius = 15f; visual.background = bg; val vp = FrameLayout.LayoutParams(prefs.prefHandleSize, prefs.prefHandleSize); vp.gravity = Gravity.CENTER; container.addView(visual, vp); handleContainers.add(container); handleVisuals.add(visual); trackpadLayout?.addView(container, p); container.setOnTouchListener { v, e -> onTouch(v, e) } }
    // =========================
    // ADD SCROLL BARS - Creates visual scroll bar overlays with touch handlers
    // Touch handlers consume events to prevent trackpad from receiving them
    // =========================
    private fun addScrollBars(context: Context) {
        val margin = prefs.prefHandleTouchSize + 10
        
        // --- Vertical Scroll Bar ---
        vScrollContainer = FrameLayout(context)
        val vp = FrameLayout.LayoutParams(prefs.prefScrollTouchSize, FrameLayout.LayoutParams.MATCH_PARENT)
        vp.gravity = if (prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT
        vp.setMargins(0, margin, 0, margin)
        trackpadLayout?.addView(vScrollContainer, vp)
        
        vScrollVisual = View(context)
        vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())
        vScrollContainer?.addView(vScrollVisual, FrameLayout.LayoutParams(prefs.prefScrollVisualSize, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER))
        
        // Touch listener for vertical scroll - MUST return true to consume event
        vScrollContainer?.setOnTouchListener { _, event ->
            handleVScrollTouch(event)
            true  // Always consume to prevent trackpad from getting the touch
        }
        
        // --- Horizontal Scroll Bar ---
        hScrollContainer = FrameLayout(context)
        val hp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefs.prefScrollTouchSize)
        hp.gravity = if (prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM
        hp.setMargins(margin, 0, margin, 0)
        trackpadLayout?.addView(hScrollContainer, hp)
        
        hScrollVisual = View(context)
        hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())
        hScrollContainer?.addView(hScrollVisual, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefs.prefScrollVisualSize, Gravity.CENTER))
        
        // Touch listener for horizontal scroll - MUST return true to consume event
        hScrollContainer?.setOnTouchListener { _, event ->
            handleHScrollTouch(event)
            true  // Always consume to prevent trackpad from getting the touch
        }
    }
    // =========================
    // END ADD SCROLL BARS
    // =========================

    // =========================
    // HANDLE V SCROLL TOUCH - Dedicated vertical scroll bar touch handler
    // Consumes all touches in the scroll bar area
    // =========================
    private fun handleVScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isVScrolling = true
                lastTouchY = event.y
                scrollAccumulatorY = 0f
                vScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt())  // Highlight active
                
                if (prefs.prefTapScroll) {
                    // Tap mode: instant scroll based on tap position
                    val viewHeight = vScrollContainer?.height ?: return
                    val isTopHalf = event.y < (viewHeight / 2)
                    val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed
                    val dy = if (isTopHalf) {
                        if (prefs.prefReverseScroll) -dist else dist
                    } else {
                        if (prefs.prefReverseScroll) dist else -dist
                    }
                    performSwipe(0f, dy)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isVScrolling && !prefs.prefTapScroll) {
                    // Drag mode: continuous scroll
                    val dy = event.y - lastTouchY
                    scrollAccumulatorY += dy * prefs.scrollSpeed
                    
                    val scrollThreshold = 30f
                    if (kotlin.math.abs(scrollAccumulatorY) > scrollThreshold) {
                        val scrollDist = scrollAccumulatorY * 2
                        val actualDy = if (prefs.prefReverseScroll) -scrollDist else scrollDist
                        performSwipe(0f, actualDy)
                        scrollAccumulatorY = 0f
                    }
                    lastTouchY = event.y
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isVScrolling = false
                scrollAccumulatorY = 0f
                vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())  // Reset color
            }
        }
    }
    // =========================
    // END HANDLE V SCROLL TOUCH
    // =========================
    
    // =========================
    // HANDLE H SCROLL TOUCH - Dedicated horizontal scroll bar touch handler
    // Consumes all touches in the scroll bar area
    // =========================
    private fun handleHScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isHScrolling = true
                lastTouchX = event.x
                scrollAccumulatorX = 0f
                hScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt())  // Highlight active
                
                if (prefs.prefTapScroll) {
                    // Tap mode: instant scroll based on tap position
                    val viewWidth = hScrollContainer?.width ?: return
                    val isLeftHalf = event.x < (viewWidth / 2)
                    val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed
                    val dx = if (isLeftHalf) {
                        if (prefs.prefReverseScroll) -dist else dist
                    } else {
                        if (prefs.prefReverseScroll) dist else -dist
                    }
                    performSwipe(dx, 0f)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isHScrolling && !prefs.prefTapScroll) {
                    // Drag mode: continuous scroll
                    val dx = event.x - lastTouchX
                    scrollAccumulatorX += dx * prefs.scrollSpeed
                    
                    val scrollThreshold = 30f
                    if (kotlin.math.abs(scrollAccumulatorX) > scrollThreshold) {
                        val scrollDist = scrollAccumulatorX * 2
                        val actualDx = if (prefs.prefReverseScroll) -scrollDist else scrollDist
                        performSwipe(actualDx, 0f)
                        scrollAccumulatorX = 0f
                    }
                    lastTouchX = event.x
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isHScrolling = false
                scrollAccumulatorX = 0f
                hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())  // Reset color
            }
        }
    }
    // =========================
    // END HANDLE H SCROLL TOUCH
    // =========================
    private fun updateScrollPosition() { val margin = prefs.prefHandleTouchSize + 10; vScrollContainer?.let { container -> val vp = container.layoutParams as FrameLayout.LayoutParams; vp.gravity = if (prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; vp.setMargins(0, margin, 0, margin); container.layoutParams = vp }; hScrollContainer?.let { container -> val hp = container.layoutParams as FrameLayout.LayoutParams; hp.gravity = if (prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM; hp.setMargins(margin, 0, margin, 0); container.layoutParams = hp } }
    private fun updateHandleSize() { for (v in handleVisuals) { val p = v.layoutParams; p.width = prefs.prefHandleSize; p.height = prefs.prefHandleSize; v.layoutParams = p } }
     // =========================
    // UPDATE SCROLL SIZE - Updates scrollbar touch and visual widths
    // Called when user adjusts the Scroll Bar Width slider
    // MIN/MAX values can be adjusted here for fine-tuning
    // =========================
    private fun updateScrollSize() {
        val SCROLL_TOUCH_MIN = 40
        val SCROLL_TOUCH_MAX = 180
        val SCROLL_VISUAL_MIN = 4
        val SCROLL_VISUAL_MAX = 20
        
        prefs.prefScrollTouchSize = prefs.prefScrollTouchSize.coerceIn(SCROLL_TOUCH_MIN, SCROLL_TOUCH_MAX)
        prefs.prefScrollVisualSize = prefs.prefScrollVisualSize.coerceIn(SCROLL_VISUAL_MIN, SCROLL_VISUAL_MAX)
        
        // === CRITICAL: Sync the touch detection variable ===
        scrollZoneThickness = prefs.prefScrollTouchSize
        // === END SYNC ===
        
        // Update vertical scroll bar
        vScrollContainer?.let { container ->
            val params = container.layoutParams as? FrameLayout.LayoutParams
            params?.width = prefs.prefScrollTouchSize
            container.layoutParams = params
        }

        vScrollVisual?.let { visual ->
            val params = visual.layoutParams as? FrameLayout.LayoutParams
            params?.width = prefs.prefScrollVisualSize
            visual.layoutParams = params
        }
        
        // Update horizontal scroll bar
        hScrollContainer?.let { container ->
            val params = container.layoutParams as? FrameLayout.LayoutParams
            params?.height = prefs.prefScrollTouchSize
            container.layoutParams = params
        }
        hScrollVisual?.let { visual ->
            val params = visual.layoutParams as? FrameLayout.LayoutParams
            params?.height = prefs.prefScrollVisualSize
            visual.layoutParams = params
        }
    }
    // =========================
    // END UPDATE SCROLL SIZE
    // =========================
   private fun updateLayoutSizes() { for (c in handleContainers) { val p = c.layoutParams; p.width = prefs.prefHandleTouchSize; p.height = prefs.prefHandleTouchSize; c.layoutParams = p } }
    private fun updateCursorSize() { val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; cursorView?.layoutParams?.let { it.width = size; it.height = size; cursorView?.layoutParams = it } }
    private fun updateBorderColor(strokeColor: Int) { 
        // Track internal state but DO NOT use it for drawing
        currentBorderColor = strokeColor 
        
        val bg = trackpadLayout?.background as? GradientDrawable ?: return
        
        // 1. Apply Background Fill (Black + User BG Alpha)
        val fillColor = (prefs.prefBgAlpha shl 24) or 0x000000
        bg.setColor(fillColor)
        
        // 2. Apply Border Stroke (FIX: Always White, Ignore State Colors)
        // This prevents Orange (Drag) and Red (Keyboard Mode) flashing.
        val finalStrokeColor = (prefs.prefAlpha shl 24) or 0xFFFFFF
        
        bg.setStroke(4, finalStrokeColor)
        
        trackpadLayout?.invalidate() 
    }
    
    private fun performSwipe(dx: Float, dy: Float) {
        Thread {
            val dId = if (inputTargetDisplayId != -1) inputTargetDisplayId else (cursorLayout?.display?.displayId ?: Display.DEFAULT_DISPLAY)
            val now = SystemClock.uptimeMillis()
            val startX = cursorX
            val startY = cursorY
            val endX = startX + dx
            val endY = startY + dy
            try { shellService?.injectMouse(MotionEvent.ACTION_DOWN, startX, startY, dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now) } catch(e: Exception) {}
            val steps = 5
            for (i in 1..steps) {
                val t = i.toFloat() / steps
                val cx = startX + (dx * t)
                val cy = startY + (dy * t)
                try { shellService?.injectMouse(MotionEvent.ACTION_MOVE, cx, cy, dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now + (i * 10)); Thread.sleep(10) } catch(e: Exception) {}
            }
            try { shellService?.injectMouse(MotionEvent.ACTION_UP, endX, endY, dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now + 100) } catch(e: Exception) {}
        }.start()
    }

    
    // =========================
    // HANDLE TRACKPAD TOUCH - Main touch event handler
    // Click logic: Only click on short tap with minimal movement
    // Drag logic: Long press OR significant movement starts drag
    // Release debounce: Brief cooldown prevents phantom cursor movement
    // =========================
    private fun handleTrackpadTouch(event: MotionEvent) {
        val viewWidth = trackpadLayout?.width ?: 0
        val viewHeight = trackpadLayout?.height ?: 0
        if (viewWidth == 0 || viewHeight == 0) return
        
        // Skip input during release debounce period
        if (isReleaseDebouncing && event.actionMasked != MotionEvent.ACTION_DOWN) {
            return
        }
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // Cancel any active debounce
                handler.removeCallbacks(releaseDebounceRunnable)
                isReleaseDebouncing = false
                
                // Record touch start position and time
                touchDownTime = SystemClock.uptimeMillis()
                touchDownX = event.x
                touchDownY = event.y
                lastTouchX = event.x
                lastTouchY = event.y
                isTouchDragging = false
                
                // Check scroll zones first
                val actualZoneV = kotlin.math.min(scrollZoneThickness, (viewWidth * 0.15f).toInt())
                val actualZoneH = kotlin.math.min(scrollZoneThickness, (viewHeight * 0.15f).toInt())
                val inVZone = if (prefs.prefVPosLeft) event.x < actualZoneV else event.x > (viewWidth - actualZoneV)
                val inHZone = if (prefs.prefHPosTop) event.y < actualZoneH else event.y > (viewHeight - actualZoneH)
                
                if (inVZone || inHZone) {
                    ignoreTouchSequence = true
                    return
                }
                
                // Schedule long press for DRAG (not click)
                handler.postDelayed(longPressRunnable, 400)
            }
            
            MotionEvent.ACTION_MOVE -> {
                if (ignoreTouchSequence) return
                
                // Calculate movement from touch DOWN position
                val totalDx = event.x - touchDownX
                val totalDy = event.y - touchDownY
                val totalDistance = kotlin.math.sqrt(totalDx * totalDx + totalDy * totalDy)
                
                // If moved beyond tap threshold, cancel long press
                if (totalDistance > TAP_SLOP_PX) {
                    handler.removeCallbacks(longPressRunnable)
                }
                
                // Calculate delta from LAST position for cursor movement
                val dx = (event.x - lastTouchX) * prefs.cursorSpeed
                val dy = (event.y - lastTouchY) * prefs.cursorSpeed
                
                val safeW = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth.toFloat() else uiScreenWidth.toFloat()
                val safeH = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight.toFloat() else uiScreenHeight.toFloat()
                
                var finalDx = dx
                var finalDy = dy
                when (rotationAngle) {
                    90 -> { finalDx = -dy; finalDy = dx }
                    180 -> { finalDx = -dx; finalDy = -dy }
                    270 -> { finalDx = dy; finalDy = -dx }
                }
                
                cursorX = (cursorX + finalDx).coerceIn(0f, safeW)
                cursorY = (cursorY + finalDy).coerceIn(0f, safeH)
                
                // Update cursor visual
                if (inputTargetDisplayId == currentDisplayId) {
                    cursorParams.x = cursorX.toInt()
                    cursorParams.y = cursorY.toInt()
                    try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {}
                } else {
                    remoteCursorParams.x = cursorX.toInt()
                    remoteCursorParams.y = cursorY.toInt()
                    try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {}
                }
                
                // Inject appropriate event
                // FIX: Support both Touch Drag (Long press) and Key Drag (Volume buttons)
                if (isTouchDragging || isKeyDragging) {
                    injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, SystemClock.uptimeMillis())
                } else {
                    injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
                }
                
                lastTouchX = event.x
                lastTouchY = event.y
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                
                if (!ignoreTouchSequence) {
                    val touchDuration = SystemClock.uptimeMillis() - touchDownTime
                    val totalDx = event.x - touchDownX
                    val totalDy = event.y - touchDownY
                    val totalDistance = kotlin.math.sqrt(totalDx * totalDx + totalDy * totalDy)
                    
                    if (isTouchDragging) {
                        // End Touch Drag (Finger lifted)
                        injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, SystemClock.uptimeMillis())
                        isTouchDragging = false
                        hasSentTouchDown = false
                    } 
                    else if (isKeyDragging) {
                        // Key Drag active: Do NOT send UP. 
                        // Just stop tracking movement. Drag continues until Key Release.
                    }
                    else if (touchDuration < TAP_TIMEOUT_MS && totalDistance < TAP_SLOP_PX) {
                        // This was a TAP - perform click
                        performClick(false)
                    }
                }
                
                // Start release debounce
                isReleaseDebouncing = true
                handler.postDelayed(releaseDebounceRunnable, RELEASE_DEBOUNCE_MS)
                
                // Reset visual state (unless key dragging)
                if (!isKeyDragging) {
                    isVScrolling = false
                    isHScrolling = false
                    updateBorderColor(currentBorderColor)
                }
                
                ignoreTouchSequence = false
            }
        }
    }
    // =========================
    // END HANDLE TRACKPAD TOUCH
    // =========================

    // =========================
    // MOVE WINDOW - Handles trackpad overlay position drag
    // Returns early if anchored to prevent accidental movement
    // =========================
    private fun moveWindow(event: MotionEvent): Boolean { 
        if (prefs.prefAnchored) return true  // Anchored: block movement
        if (event.action == MotionEvent.ACTION_MOVE) { 
            trackpadParams.x += (event.rawX - lastTouchX).toInt()
            trackpadParams.y += (event.rawY - lastTouchY).toInt()
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams) 
        }
        lastTouchX = event.rawX
        lastTouchY = event.rawY
        return true 
    }
    // =========================
    // END MOVE WINDOW
    // =========================
    
    // =========================
    // RESIZE WINDOW - Handles trackpad overlay size drag
    // Returns early if anchored to prevent accidental resizing
    // =========================
    private fun resizeWindow(event: MotionEvent): Boolean { 
        if (prefs.prefAnchored) return true  // Anchored: block resize
        if (event.action == MotionEvent.ACTION_MOVE) { 
            trackpadParams.width += (event.rawX - lastTouchX).toInt()
            trackpadParams.height += (event.rawY - lastTouchY).toInt()
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams) 
        }
        lastTouchX = event.rawX
        lastTouchY = event.rawY
        return true 
    }
    // =========================
    // END RESIZE WINDOW
    // =========================
   private fun keyboardHandle(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_UP) toggleCustomKeyboard(); return true }
    private fun openMenuHandle(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_DOWN) menuManager?.toggle(); return true }
    private fun injectAction(action: Int, source: Int, button: Int, time: Long) { if (shellService == null) return; Thread { try { shellService?.injectMouse(action, cursorX, cursorY, inputTargetDisplayId, source, button, time) } catch(e: Exception){} }.start() }
    private fun injectScroll(hScroll: Float, vScroll: Float) { if (shellService == null) return; Thread { try { shellService?.injectScroll(cursorX, cursorY, vScroll / 10f, hScroll / 10f, inputTargetDisplayId) } catch(e: Exception){} }.start() }
    private fun performClick(right: Boolean) { if (shellService == null) return; Thread { try { if (right) shellService?.execRightClick(cursorX, cursorY, inputTargetDisplayId) else shellService?.execClick(cursorX, cursorY, inputTargetDisplayId) } catch(e: Exception){} }.start() }
    fun resetCursorCenter() { 
        cursorX = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth/2f else uiScreenWidth/2f
        cursorY = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight/2f else uiScreenHeight/2f
        if (inputTargetDisplayId == currentDisplayId) { cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); windowManager?.updateViewLayout(cursorLayout, cursorParams) } 
        else { remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){} }
    }
    fun performRotation() { rotationAngle = (rotationAngle + 90) % 360; cursorView?.rotation = rotationAngle.toFloat() }
    fun getProfileKey(): String = "P_${uiScreenWidth}_${uiScreenHeight}"
    // --- UPDATED SAVE LAYOUT (Full Persistence) ---
    // [REPLACE FUNCTION] saveLayout
    fun saveLayout() { 
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        val key = getProfileKey()
        
        p.putInt("X_$key", trackpadParams.x)
        p.putInt("Y_$key", trackpadParams.y)
        p.putInt("W_$key", trackpadParams.width)
        p.putInt("H_$key", trackpadParams.height)
        
        // Save Keyboard Position & Size
        val kbX = keyboardOverlay?.getViewX() ?: 0
        val kbY = keyboardOverlay?.getViewY() ?: 0
        val kbW = keyboardOverlay?.getViewWidth() ?: 0
        val kbH = keyboardOverlay?.getViewHeight() ?: 0
        
        val s = StringBuilder()
        s.append("${prefs.cursorSpeed};") // 0
        s.append("${prefs.scrollSpeed};") // 1
        s.append("${if(prefs.prefTapScroll) 1 else 0};") // 2
        s.append("${if(prefs.prefReverseScroll) 1 else 0};") // 3
        s.append("${prefs.prefAlpha};") // 4
        s.append("${prefs.prefBgAlpha};") // 5
        s.append("${prefs.prefKeyboardAlpha};") // 6
        s.append("${prefs.prefHandleSize};") // 7
        s.append("${prefs.prefHandleTouchSize};") // 8
        s.append("${prefs.prefScrollTouchSize};") // 9
        s.append("${prefs.prefScrollVisualSize};") // 10
        s.append("${prefs.prefCursorSize};") // 11
        s.append("${prefs.prefKeyScale};") // 12
        s.append("${if(prefs.prefAutomationEnabled) 1 else 0};") // 13
        s.append("${if(prefs.prefAnchored) 1 else 0};") // 14
        s.append("${prefs.prefBubbleSize};") // 15
        s.append("${prefs.prefBubbleAlpha};") // 16
        s.append("${prefs.prefBubbleIconIndex};") // 17
        s.append("${prefs.prefBubbleX};") // 18
        s.append("${prefs.prefBubbleY};") // 19
        s.append("${prefs.hardkeyVolUpTap};") // 20
        s.append("${prefs.hardkeyVolUpDouble};") // 21
        s.append("${prefs.hardkeyVolUpHold};") // 22
        s.append("${prefs.hardkeyVolDownTap};") // 23
        s.append("${prefs.hardkeyVolDownDouble};") // 24
        s.append("${prefs.hardkeyVolDownHold};") // 25
        s.append("${prefs.hardkeyPowerDouble};") // 26
        s.append("$kbX;") // 27
        s.append("$kbY;") // 28
        s.append("$kbW;") // 29
        s.append("$kbH")  // 30
        
        p.putString("SETTINGS_$key", s.toString())
        p.apply()
        showToast("Layout & Presets Saved") 
    }

    fun loadLayout() { 
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val key = getProfileKey()
        
        trackpadParams.x = p.getInt("X_$key", 100)
        trackpadParams.y = p.getInt("Y_$key", 100)
        trackpadParams.width = p.getInt("W_$key", 400)
        trackpadParams.height = p.getInt("H_$key", 300)
        try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(e: Exception){} 
        
        val settings = p.getString("SETTINGS_$key", null)
        if (settings != null) {
            try {
                val parts = settings.split(";")
                if (parts.size >= 17) {
                    prefs.cursorSpeed = parts[0].toFloat()
                    prefs.scrollSpeed = parts[1].toFloat()
                    prefs.prefTapScroll = parseBoolean(parts[2])
                    prefs.prefReverseScroll = parseBoolean(parts[3])
                    prefs.prefAlpha = parts[4].toInt()
                    prefs.prefBgAlpha = parts[5].toInt()
                    prefs.prefKeyboardAlpha = parts[6].toInt()
                    prefs.prefHandleSize = parts[7].toInt()
                    prefs.prefHandleTouchSize = parts[8].toInt()
                    prefs.prefScrollTouchSize = parts[9].toInt()
                    prefs.prefScrollVisualSize = parts[10].toInt()
                    prefs.prefCursorSize = parts[11].toInt()
                    prefs.prefKeyScale = parts[12].toInt()
                    prefs.prefAutomationEnabled = parseBoolean(parts[13])
                    prefs.prefAnchored = parseBoolean(parts[14])
                    prefs.prefBubbleSize = parts[15].toInt()
                    prefs.prefBubbleAlpha = parts[16].toInt()
                    
                    if (parts.size >= 27) {
                        prefs.prefBubbleIconIndex = parts[17].toInt()
                        prefs.prefBubbleX = parts[18].toInt()
                        prefs.prefBubbleY = parts[19].toInt()
                        prefs.hardkeyVolUpTap = parts[20]
                        prefs.hardkeyVolUpDouble = parts[21]
                        prefs.hardkeyVolUpHold = parts[22]
                        prefs.hardkeyVolDownTap = parts[23]
                        prefs.hardkeyVolDownDouble = parts[24]
                        prefs.hardkeyVolDownHold = parts[25]
                        prefs.hardkeyPowerDouble = parts[26]
                    }
                    
                    // LOAD KEYBOARD POS & SIZE
                    if (parts.size >= 31) {
                        val kbX = parts[27].toInt()
                        val kbY = parts[28].toInt()
                        val kbW = parts[29].toInt()
                        val kbH = parts[30].toInt()
                        keyboardOverlay?.updatePosition(kbX, kbY)
                        keyboardOverlay?.updateSize(kbW, kbH)
                    } else if (parts.size >= 29) {
                        // Backward compatibility (Position only)
                        val kbX = parts[27].toInt()
                        val kbY = parts[28].toInt()
                        keyboardOverlay?.updatePosition(kbX, kbY)
                    }

                    // Apply Visuals
                    scrollZoneThickness = prefs.prefScrollTouchSize
                    updateBorderColor(currentBorderColor)
                    updateLayoutSizes()
                    updateScrollSize()
                    updateHandleSize()
                    updateCursorSize()
                    keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha)
                    keyboardOverlay?.updateScale(prefs.prefKeyScale / 100f)
                    keyboardOverlay?.setAnchored(prefs.prefAnchored)
                    
                    if (bubbleView != null) {
                        bubbleParams.x = prefs.prefBubbleX
                        bubbleParams.y = prefs.prefBubbleY
                        windowManager?.updateViewLayout(bubbleView, bubbleParams)
                        applyBubbleAppearance()
                    }
                    
                    showToast("Layout & Presets Loaded")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile settings", e)
            }
        }
    }
    fun deleteCurrentProfile() { /* Stub */ }
    fun resetTrackpadPosition() { trackpadParams.x = 100; trackpadParams.y = 100; trackpadParams.width = 400; trackpadParams.height = 300; windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }
    fun cycleInputTarget() { 
        if (displayManager == null) return; val displays = displayManager!!.displays; var nextId = -1
        for (d in displays) { if (d.displayId != currentDisplayId) { if (inputTargetDisplayId == currentDisplayId) { nextId = d.displayId; break } else if (inputTargetDisplayId == d.displayId) { continue } else { nextId = d.displayId } } }
        if (nextId == -1) { inputTargetDisplayId = currentDisplayId; targetScreenWidth = uiScreenWidth; targetScreenHeight = uiScreenHeight; removeRemoteCursor(); cursorX = uiScreenWidth / 2f; cursorY = uiScreenHeight / 2f; cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception){}; cursorView?.visibility = View.VISIBLE; updateBorderColor(0x55FFFFFF.toInt()); showToast("Target: Local (Display $currentDisplayId)") } 
        else { inputTargetDisplayId = nextId; updateTargetMetrics(nextId); createRemoteCursor(nextId); cursorX = targetScreenWidth / 2f; cursorY = targetScreenHeight / 2f; remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){}; cursorView?.visibility = View.GONE; updateBorderColor(0xFFFF00FF.toInt()); showToast("Target: Display $nextId") }
    }
    private fun createRemoteCursor(displayId: Int) { try { removeRemoteCursor(); val display = displayManager?.getDisplay(displayId) ?: return; val remoteContext = createTrackpadDisplayContext(display); remoteWindowManager = remoteContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager; remoteCursorLayout = FrameLayout(remoteContext); remoteCursorView = ImageView(remoteContext); remoteCursorView?.setImageResource(R.drawable.ic_cursor); val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; remoteCursorLayout?.addView(remoteCursorView, FrameLayout.LayoutParams(size, size)); remoteCursorParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT); remoteCursorParams.gravity = Gravity.TOP or Gravity.LEFT; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); remoteCursorParams.x = metrics.widthPixels / 2; remoteCursorParams.y = metrics.heightPixels / 2; remoteWindowManager?.addView(remoteCursorLayout, remoteCursorParams) } catch (e: Exception) { e.printStackTrace() } }
    private fun removeRemoteCursor() { try { if (remoteCursorLayout != null && remoteWindowManager != null) { remoteWindowManager?.removeView(remoteCursorLayout) } } catch (e: Exception) {}; remoteCursorLayout = null; remoteCursorView = null; remoteWindowManager = null }
    // =========================
    // START TOUCH DRAG - Called by longPressRunnable after 400ms hold
    // Initiates drag mode with touch injection
    // =========================
    private fun startTouchDrag() {
        if (ignoreTouchSequence || isTouchDragging) return
        
        isTouchDragging = true
        activeDragButton = MotionEvent.BUTTON_PRIMARY // Default to Left Click for touch drag
        dragDownTime = SystemClock.uptimeMillis()
        
        // Inject touch down at current cursor position
        injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, dragDownTime)
        hasSentTouchDown = true
        
        // Visual feedback
        if (prefs.prefVibrate) vibrate()
        updateBorderColor(0xFFFF9900.toInt())  // Orange = drag mode
    }
    // =========================
    // END START TOUCH DRAG
    // =========================

    private fun startResize() {}
    private fun startMove() {}
    private fun startKeyDrag(button: Int) { 
        vibrate()
        updateBorderColor(0xFF00FF00.toInt())
        
        isKeyDragging = true
        activeDragButton = button
        
        dragDownTime = SystemClock.uptimeMillis()
        injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime)
        hasSentMouseDown = true 
    }

    private fun stopKeyDrag(button: Int) { 
        if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
        else updateBorderColor(0x55FFFFFF.toInt())
        
        isKeyDragging = false
        
        if (hasSentMouseDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime)
        }
        hasSentMouseDown = false 
    }
    private fun handleManualAdjust(intent: Intent) {}
    private fun handlePreview(intent: Intent) {}
    private fun setTrackpadVisibility(visible: Boolean) { isTrackpadVisible = visible; if (trackpadLayout != null) trackpadLayout?.visibility = if (visible) View.VISIBLE else View.GONE }
    private fun setPreviewMode(preview: Boolean) { isPreviewMode = preview; trackpadLayout?.alpha = if (preview) 0.5f else 1.0f }
    override fun onDisplayAdded(displayId: Int) {}
    override fun onDisplayRemoved(displayId: Int) {}
    override fun onDisplayChanged(displayId: Int) {}
    override fun onDestroy() { 
        super.onDestroy()
        // Safety: Restore IME to default state
        Thread { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") }.start()
        try { unregisterReceiver(switchReceiver) } catch(e: Exception){}; 
        if (isBound) ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection) 
    }

    private fun showToast(msg: String) {
        handler.post { android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show() }
    }

    private fun updateTargetMetrics(displayId: Int) {
        val display = displayManager?.getDisplay(displayId) ?: return
        val metrics = android.util.DisplayMetrics()
        display.getRealMetrics(metrics)
        targetScreenWidth = metrics.widthPixels
        targetScreenHeight = metrics.heightPixels
    }

    // =========================
    // Z-ORDER ENFORCEMENT
    // Stack: Trackpad (Bottom) -> Keyboard -> Menu -> Bubble -> Cursor (Top)
    // =========================
    // =========================
    // ENFORCE Z-ORDER
    // Stack: Trackpad -> Keyboard -> Menu -> Bubble -> Cursor
    // This function must be called whenever a lower layer (Keyboard) is toggled
    // to ensure the upper layers (Menu, Bubble, Cursor) stay visible.
    // =========================
    fun enforceZOrder() {
        if (windowManager == null) return

        // 1. Menu (Must sit above Keyboard)
        menuManager?.bringToFront()

        // 2. Bubble (Above Menu)
        if (bubbleView != null && bubbleView!!.isAttachedToWindow) {
            try { 
                windowManager?.removeView(bubbleView)
                windowManager?.addView(bubbleView, bubbleParams) 
            } catch (e: Exception) {}
        }

        // 3. Cursor (Absolute Top)
        if (cursorLayout != null && cursorLayout!!.isAttachedToWindow) {
            try { 
                windowManager?.removeView(cursorLayout)
                windowManager?.addView(cursorLayout, cursorParams) 
            } catch (e: Exception) {}
        }
    }
    // =========================
    // END Z-ORDER ENFORCEMENT
    // =========================
}
```
