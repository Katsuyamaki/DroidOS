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
            quadrantlauncher/
              ExampleInstrumentedTest.kt
    main/
      aidl/
        com/
          example/
            quadrantlauncher/
              IShellService.aidl
      java/
        com/
          example/
            quadrantlauncher/
              AppPreferences.kt
              FloatingLauncherService.kt
              IconPickerActivity.kt
              MainActivity.kt
              MenuActivity.kt
              QuadrantActivity.kt
              ShellUserService.kt
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
          backup_rules.xml
          data_extraction_rules.xml
      AndroidManifest.xml
    test/
      java/
        com/
          example/
            quadrantlauncher/
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
build_log.txt
build.gradle.kts
gradle.properties
gradlew
gradlew.bat
multidex-keep.txt
README.md
settings.gradle.kts
```

# Files

## File: app/src/main/res/drawable/ic_cover_final_scale.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/wolf_cover_icon"
    android:inset="70dp" />
```

## File: app/src/main/res/drawable/ic_scaler_bubble.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="35dp" />
```

## File: app/src/main/res/drawable/ic_scaler_cover_final.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/wolf_cover_icon"
    android:inset="80dp" />
```

## File: app/src/main/res/drawable/ic_scaler_cover_tiny.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="65dp" />
```

## File: app/src/main/res/drawable/scaler_bubble.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="120dp" />
```

## File: app/src/main/res/drawable/scaler_cover.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="120dp" />
```

## File: app/src/main/res/drawable/scaler_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="20dp" />
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_bubble_icon.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/ic_scaler_bubble" />
</adaptive-icon>
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_bubble.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/scaler_bubble" />
</adaptive-icon>
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_cover_tiny.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/ic_scaler_cover_tiny" />
</adaptive-icon>
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_cover_v2.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/scaler_cover" />
</adaptive-icon>
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_launcher_cover_final.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/ic_scaler_cover_final" />
</adaptive-icon>
```

## File: app/src/androidTest/java/com/example/quadrantlauncher/ExampleInstrumentedTest.kt
```kotlin
package com.example.quadrantlauncher

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
        assertEquals("com.example.quadrantlauncher", appContext.packageName)
    }
}
```

## File: app/src/main/java/com/example/quadrantlauncher/IconPickerActivity.kt
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

## File: app/src/main/java/com/example/quadrantlauncher/ShizukuBinder.java
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

## File: app/src/main/res/drawable/bg_bubble.xml
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="oval">
    <solid android:color="#444444"/>
    <stroke android:width="2dp" android:color="#888888"/>
</shape>
```

## File: app/src/main/res/drawable/bg_drawer.xml
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#2D2D2D"/>
    <corners android:radius="16dp"/>
    <stroke android:width="1dp" android:color="#444444"/>
</shape>
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

## File: app/src/main/res/drawable/ic_box_outline.xml
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

## File: app/src/main/res/drawable/ic_mode_dpi.xml
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

## File: app/src/main/res/drawable/ic_mode_profiles.xml
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

## File: app/src/main/res/drawable/ic_mode_resolution.xml
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

## File: app/src/main/res/drawable/ic_star_border.xml
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

## File: app/src/main/res/drawable/ic_star_filled.xml
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

## File: app/src/main/res/drawable/ic_window_split.xml
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M4,4h16v16H4V4z M12,4v16 M4,12h16"/>
</vector>
```

## File: app/src/main/res/drawable/ic_wolf_cover.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="80dp" />
```

## File: app/src/main/res/drawable/ic_wolf_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_wolf_logo"
    android:inset="25dp" />
```

## File: app/src/main/res/layout/activity_menu.xml
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

## File: app/src/main/res/layout/activity_quadrant.xml
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

## File: app/src/main/res/layout/activity_tri_split.xml
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

## File: app/src/main/res/layout/item_app_rofi.xml
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

## File: app/src/main/res/layout/item_height_setting.xml
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

## File: app/src/main/res/layout/item_icon_setting.xml
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

## File: app/src/main/res/layout/item_width_setting.xml
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
    <string name="app_name">CoverScreenLauncher</string>
</resources>
```

## File: app/src/main/res/values/themes.xml
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

## File: app/src/main/res/values-night/themes.xml
```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Base.Theme.QuadrantLauncher" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your dark theme here. -->
        <!-- <item name="colorPrimary">@color/my_dark_primary</item> -->
    </style>
</resources>
```

## File: app/src/main/res/xml/backup_rules.xml
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

## File: app/src/main/res/xml/data_extraction_rules.xml
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

## File: app/src/test/java/com/example/quadrantlauncher/ExampleUnitTest.kt
```kotlin
package com.example.quadrantlauncher

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

## File: gradle/wrapper/gradle-wrapper.properties
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

## File: gradle/libs.versions.toml
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

## File: build_log.txt
```
> Task :app:preBuild UP-TO-DATE
> Task :app:preDebugBuild UP-TO-DATE
> Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
> Task :app:checkKotlinGradlePluginConfigurationErrors SKIPPED
> Task :app:checkDebugAarMetadata FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:checkDebugAarMetadata'.
> Could not resolve all files for configuration ':app:debugRuntimeClasspath'.
   > Could not resolve dev.rikka.shizuku:ktx:13.1.5.
     Required by:
         project :app
      > Could not resolve dev.rikka.shizuku:ktx:13.1.5.
         > Could not get resource 'https://maven.rikka.dev/versioned/dev/rikka/shizuku/ktx/13.1.5/ktx-13.1.5.pom'.
            > Could not GET 'https://maven.rikka.dev/versioned/dev/rikka/shizuku/ktx/13.1.5/ktx-13.1.5.pom'.
               > maven.rikka.dev: Name or service not known

* Try:
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

* Exception is:
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:checkDebugAarMetadata'.
	at org.gradle.api.internal.tasks.execution.CatchExceptionTaskExecuter.execute(CatchExceptionTaskExecuter.java:38)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.executeTask(EventFiringTaskExecuter.java:77)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:55)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:52)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:210)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:205)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:67)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:167)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:54)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter.execute(EventFiringTaskExecuter.java:52)
	at org.gradle.execution.plan.LocalTaskNodeExecutor.execute(LocalTaskNodeExecutor.java:42)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:331)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:318)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.lambda$execute$0(DefaultTaskExecutionGraph.java:314)
	at org.gradle.internal.operations.CurrentBuildOperationRef.with(CurrentBuildOperationRef.java:85)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:314)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:303)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.execute(DefaultPlanExecutor.java:459)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.run(DefaultPlanExecutor.java:376)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
	at org.gradle.internal.concurrent.AbstractManagedExecutor$1.run(AbstractManagedExecutor.java:48)
Caused by: org.gradle.api.internal.artifacts.ivyservice.TypedResolveException: Could not resolve all files for configuration ':app:debugRuntimeClasspath'.
	at org.gradle.api.internal.artifacts.ResolveExceptionMapper.mapFailure(ResolveExceptionMapper.java:70)
	at org.gradle.api.internal.artifacts.ResolveExceptionMapper.mapFailures(ResolveExceptionMapper.java:62)
	at org.gradle.api.internal.artifacts.configurations.DefaultConfiguration$DefaultResolutionHost.consolidateFailures(DefaultConfiguration.java:1928)
	at org.gradle.api.internal.artifacts.configurations.ResolutionHost.rethrowFailuresAndReportProblems(ResolutionHost.java:75)
	at org.gradle.api.internal.artifacts.configurations.ResolutionBackedFileCollection.maybeThrowResolutionFailures(ResolutionBackedFileCollection.java:86)
	at org.gradle.api.internal.artifacts.configurations.ResolutionBackedFileCollection.visitContents(ResolutionBackedFileCollection.java:76)
	at org.gradle.api.internal.file.AbstractFileCollection.visitStructure(AbstractFileCollection.java:360)
	at org.gradle.api.internal.file.CompositeFileCollection.lambda$visitContents$0(CompositeFileCollection.java:113)
	at org.gradle.api.internal.file.collections.UnpackingVisitor.add(UnpackingVisitor.java:67)
	at org.gradle.api.internal.file.collections.UnpackingVisitor.add(UnpackingVisitor.java:92)
	at org.gradle.api.internal.file.DefaultFileCollectionFactory$ResolvingFileCollection.visitChildren(DefaultFileCollectionFactory.java:306)
	at org.gradle.api.internal.file.CompositeFileCollection.visitContents(CompositeFileCollection.java:113)
	at org.gradle.api.internal.file.AbstractFileCollection.visitStructure(AbstractFileCollection.java:360)
	at org.gradle.api.internal.file.CompositeFileCollection.lambda$visitContents$0(CompositeFileCollection.java:113)
	at org.gradle.api.internal.tasks.PropertyFileCollection.visitChildren(PropertyFileCollection.java:48)
	at org.gradle.api.internal.file.CompositeFileCollection.visitContents(CompositeFileCollection.java:113)
	at org.gradle.api.internal.file.AbstractFileCollection.visitStructure(AbstractFileCollection.java:360)
	at org.gradle.internal.fingerprint.impl.DefaultFileCollectionSnapshotter.snapshot(DefaultFileCollectionSnapshotter.java:47)
	at org.gradle.internal.execution.impl.DefaultInputFingerprinter$InputCollectingVisitor.visitInputFileProperty(DefaultInputFingerprinter.java:133)
	at org.gradle.api.internal.tasks.execution.TaskExecution.visitRegularInputs(TaskExecution.java:324)
	at org.gradle.internal.execution.impl.DefaultInputFingerprinter.fingerprintInputProperties(DefaultInputFingerprinter.java:63)
	at org.gradle.internal.execution.steps.AbstractCaptureStateBeforeExecutionStep.captureExecutionStateWithOutputs(AbstractCaptureStateBeforeExecutionStep.java:109)
	at org.gradle.internal.execution.steps.AbstractCaptureStateBeforeExecutionStep.lambda$captureExecutionState$0(AbstractCaptureStateBeforeExecutionStep.java:74)
	at org.gradle.internal.execution.steps.BuildOperationStep$1.call(BuildOperationStep.java:37)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:210)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:205)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:67)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:167)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:54)
	at org.gradle.internal.execution.steps.BuildOperationStep.operation(BuildOperationStep.java:34)
	at org.gradle.internal.execution.steps.AbstractCaptureStateBeforeExecutionStep.captureExecutionState(AbstractCaptureStateBeforeExecutionStep.java:69)
	at org.gradle.internal.execution.steps.AbstractCaptureStateBeforeExecutionStep.execute(AbstractCaptureStateBeforeExecutionStep.java:62)
	at org.gradle.internal.execution.steps.AbstractCaptureStateBeforeExecutionStep.execute(AbstractCaptureStateBeforeExecutionStep.java:43)
	at org.gradle.internal.execution.steps.AbstractSkipEmptyWorkStep.executeWithNonEmptySources(AbstractSkipEmptyWorkStep.java:125)
	at org.gradle.internal.execution.steps.AbstractSkipEmptyWorkStep.execute(AbstractSkipEmptyWorkStep.java:56)
	at org.gradle.internal.execution.steps.AbstractSkipEmptyWorkStep.execute(AbstractSkipEmptyWorkStep.java:36)
	at org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsStartedStep.execute(MarkSnapshottingInputsStartedStep.java:38)
	at org.gradle.internal.execution.steps.LoadPreviousExecutionStateStep.execute(LoadPreviousExecutionStateStep.java:36)
	at org.gradle.internal.execution.steps.LoadPreviousExecutionStateStep.execute(LoadPreviousExecutionStateStep.java:23)
	at org.gradle.internal.execution.steps.HandleStaleOutputsStep.execute(HandleStaleOutputsStep.java:75)
	at org.gradle.internal.execution.steps.HandleStaleOutputsStep.execute(HandleStaleOutputsStep.java:41)
	at org.gradle.internal.execution.steps.AssignMutableWorkspaceStep.lambda$execute$0(AssignMutableWorkspaceStep.java:35)
	at org.gradle.api.internal.tasks.execution.TaskExecution$4.withWorkspace(TaskExecution.java:289)
	at org.gradle.internal.execution.steps.AssignMutableWorkspaceStep.execute(AssignMutableWorkspaceStep.java:31)
	at org.gradle.internal.execution.steps.AssignMutableWorkspaceStep.execute(AssignMutableWorkspaceStep.java:22)
	at org.gradle.internal.execution.steps.ChoosePipelineStep.execute(ChoosePipelineStep.java:40)
	at org.gradle.internal.execution.steps.ChoosePipelineStep.execute(ChoosePipelineStep.java:23)
	at org.gradle.internal.execution.steps.ExecuteWorkBuildOperationFiringStep.lambda$execute$2(ExecuteWorkBuildOperationFiringStep.java:67)
	at org.gradle.internal.execution.steps.ExecuteWorkBuildOperationFiringStep.execute(ExecuteWorkBuildOperationFiringStep.java:67)
	at org.gradle.internal.execution.steps.ExecuteWorkBuildOperationFiringStep.execute(ExecuteWorkBuildOperationFiringStep.java:39)
	at org.gradle.internal.execution.steps.IdentityCacheStep.execute(IdentityCacheStep.java:46)
	at org.gradle.internal.execution.steps.IdentityCacheStep.execute(IdentityCacheStep.java:34)
	at org.gradle.internal.execution.steps.IdentifyStep.execute(IdentifyStep.java:48)
	at org.gradle.internal.execution.steps.IdentifyStep.execute(IdentifyStep.java:35)
	at org.gradle.internal.execution.impl.DefaultExecutionEngine$1.execute(DefaultExecutionEngine.java:61)
	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeIfValid(ExecuteActionsTaskExecuter.java:127)
	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.execute(ExecuteActionsTaskExecuter.java:116)
	at org.gradle.api.internal.tasks.execution.ProblemsTaskPathTrackingTaskExecuter.execute(ProblemsTaskPathTrackingTaskExecuter.java:40)
	at org.gradle.api.internal.tasks.execution.FinalizePropertiesTaskExecuter.execute(FinalizePropertiesTaskExecuter.java:46)
	at org.gradle.api.internal.tasks.execution.ResolveTaskExecutionModeExecuter.execute(ResolveTaskExecutionModeExecuter.java:51)
	at org.gradle.api.internal.tasks.execution.SkipTaskWithNoActionsExecuter.execute(SkipTaskWithNoActionsExecuter.java:57)
	at org.gradle.api.internal.tasks.execution.SkipOnlyIfTaskExecuter.execute(SkipOnlyIfTaskExecuter.java:74)
	at org.gradle.api.internal.tasks.execution.CatchExceptionTaskExecuter.execute(CatchExceptionTaskExecuter.java:36)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.executeTask(EventFiringTaskExecuter.java:77)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:55)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:52)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:210)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:205)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:67)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:167)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:54)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter.execute(EventFiringTaskExecuter.java:52)
	at org.gradle.execution.plan.LocalTaskNodeExecutor.execute(LocalTaskNodeExecutor.java:42)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:331)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:318)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.lambda$execute$0(DefaultTaskExecutionGraph.java:314)
	at org.gradle.internal.operations.CurrentBuildOperationRef.with(CurrentBuildOperationRef.java:85)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:314)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:303)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.execute(DefaultPlanExecutor.java:459)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.run(DefaultPlanExecutor.java:376)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
	at org.gradle.internal.concurrent.AbstractManagedExecutor$1.run(AbstractManagedExecutor.java:48)
Caused by: org.gradle.internal.resolve.ModuleVersionResolveException: Could not resolve dev.rikka.shizuku:ktx:13.1.5.
Required by:
    project :app
Caused by: org.gradle.internal.resolve.ModuleVersionResolveException: Could not resolve dev.rikka.shizuku:ktx:13.1.5.
Caused by: org.gradle.api.resources.ResourceException: Could not get resource 'https://maven.rikka.dev/versioned/dev/rikka/shizuku/ktx/13.1.5/ktx-13.1.5.pom'.
	at org.gradle.internal.resource.ResourceExceptions.failure(ResourceExceptions.java:74)
	at org.gradle.internal.resource.ResourceExceptions.getFailed(ResourceExceptions.java:57)
	at org.gradle.api.internal.artifacts.repositories.resolver.DefaultExternalResourceArtifactResolver.downloadByCoords(DefaultExternalResourceArtifactResolver.java:154)
	at org.gradle.api.internal.artifacts.repositories.resolver.DefaultExternalResourceArtifactResolver.downloadStaticResource(DefaultExternalResourceArtifactResolver.java:94)
	at org.gradle.api.internal.artifacts.repositories.resolver.DefaultExternalResourceArtifactResolver.resolveArtifact(DefaultExternalResourceArtifactResolver.java:60)
	at org.gradle.api.internal.artifacts.repositories.metadata.AbstractRepositoryMetadataSource.parseMetaDataFromArtifact(AbstractRepositoryMetadataSource.java:79)
	at org.gradle.api.internal.artifacts.repositories.metadata.AbstractRepositoryMetadataSource.create(AbstractRepositoryMetadataSource.java:69)
	at org.gradle.api.internal.artifacts.repositories.metadata.DefaultMavenPomMetadataSource.create(DefaultMavenPomMetadataSource.java:40)
	at org.gradle.api.internal.artifacts.repositories.metadata.RedirectingGradleMetadataModuleMetadataSource.create(RedirectingGradleMetadataModuleMetadataSource.java:51)
	at org.gradle.api.internal.artifacts.repositories.resolver.ExternalResourceResolver.resolveStaticDependency(ExternalResourceResolver.java:235)
	at org.gradle.api.internal.artifacts.repositories.resolver.MavenResolver.doResolveComponentMetaData(MavenResolver.java:108)
	at org.gradle.api.internal.artifacts.repositories.resolver.ExternalResourceResolver$RemoteRepositoryAccess.resolveComponentMetaData(ExternalResourceResolver.java:427)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.CachingModuleComponentRepository$ResolveAndCacheRepositoryAccess.resolveComponentMetaDataAndCache(CachingModuleComponentRepository.java:393)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.CachingModuleComponentRepository$ResolveAndCacheRepositoryAccess.resolveComponentMetaData(CachingModuleComponentRepository.java:387)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ErrorHandlingModuleComponentRepository$ErrorHandlingModuleComponentRepositoryAccess.lambda$resolveComponentMetaData$5(ErrorHandlingModuleComponentRepository.java:147)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ErrorHandlingModuleComponentRepository$ErrorHandlingModuleComponentRepositoryAccess.lambda$tryResolveAndMaybeDisable$15(ErrorHandlingModuleComponentRepository.java:217)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ErrorHandlingModuleComponentRepository$ErrorHandlingModuleComponentRepositoryAccess.tryResolveAndMaybeDisable(ErrorHandlingModuleComponentRepository.java:233)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ErrorHandlingModuleComponentRepository$ErrorHandlingModuleComponentRepositoryAccess.tryResolveAndMaybeDisable(ErrorHandlingModuleComponentRepository.java:216)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ErrorHandlingModuleComponentRepository$ErrorHandlingModuleComponentRepositoryAccess.performOperationWithRetries(ErrorHandlingModuleComponentRepository.java:200)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ErrorHandlingModuleComponentRepository$ErrorHandlingModuleComponentRepositoryAccess.resolveComponentMetaData(ErrorHandlingModuleComponentRepository.java:146)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ComponentMetaDataResolveState.process(ComponentMetaDataResolveState.java:70)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ComponentMetaDataResolveState.resolve(ComponentMetaDataResolveState.java:62)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.RepositoryChainComponentMetaDataResolver.findBestMatch(RepositoryChainComponentMetaDataResolver.java:165)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.RepositoryChainComponentMetaDataResolver.findBestMatch(RepositoryChainComponentMetaDataResolver.java:145)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.RepositoryChainComponentMetaDataResolver.resolveModule(RepositoryChainComponentMetaDataResolver.java:115)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.RepositoryChainComponentMetaDataResolver.lambda$createValueContainerFor$1(RepositoryChainComponentMetaDataResolver.java:85)
	at org.gradle.internal.model.CalculatedValueContainerFactory$SupplierBackedCalculator.calculateValue(CalculatedValueContainerFactory.java:69)
	at org.gradle.internal.model.CalculatedValueContainer$CalculationState.lambda$attachValue$0(CalculatedValueContainer.java:229)
	at org.gradle.internal.Try.ofFailable(Try.java:50)
	at org.gradle.internal.model.CalculatedValueContainer$CalculationState.attachValue(CalculatedValueContainer.java:224)
	at org.gradle.internal.model.CalculatedValueContainer.finalizeIfNotAlready(CalculatedValueContainer.java:195)
	at org.gradle.internal.model.CalculatedValueContainer.finalizeIfNotAlready(CalculatedValueContainer.java:186)
	at org.gradle.api.internal.artifacts.ivyservice.ivyresolve.RepositoryChainComponentMetaDataResolver.resolve(RepositoryChainComponentMetaDataResolver.java:77)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.ComponentResolversChain$ComponentMetaDataResolverChain.resolve(ComponentResolversChain.java:90)
	at org.gradle.api.internal.artifacts.ivyservice.clientmodule.ClientModuleResolver.resolve(ClientModuleResolver.java:70)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.builder.ComponentState.resolve(ComponentState.java:224)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.builder.ComponentState.getResolveStateOrNull(ComponentState.java:168)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.builder.EdgeState.calculateTargetNodes(EdgeState.java:213)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.builder.EdgeState.attachToTargetNodes(EdgeState.java:148)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.builder.DependencyGraphBuilder.attachToTargetRevisionsSerially(DependencyGraphBuilder.java:390)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.builder.DependencyGraphBuilder.resolveEdges(DependencyGraphBuilder.java:280)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.builder.DependencyGraphBuilder.traverseGraph(DependencyGraphBuilder.java:205)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.builder.DependencyGraphBuilder.resolve(DependencyGraphBuilder.java:164)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.DependencyGraphResolver.resolve(DependencyGraphResolver.java:120)
	at org.gradle.api.internal.artifacts.ivyservice.ResolutionExecutor.doResolve(ResolutionExecutor.java:482)
	at org.gradle.api.internal.artifacts.ivyservice.ResolutionExecutor.resolveGraph(ResolutionExecutor.java:355)
	at org.gradle.api.internal.artifacts.ivyservice.ShortCircuitingResolutionExecutor.resolveGraph(ShortCircuitingResolutionExecutor.java:92)
	at org.gradle.api.internal.artifacts.ivyservice.DefaultConfigurationResolver.resolveGraph(DefaultConfigurationResolver.java:129)
	at org.gradle.api.internal.artifacts.configurations.DefaultConfiguration$1.call(DefaultConfiguration.java:764)
	at org.gradle.api.internal.artifacts.configurations.DefaultConfiguration$1.call(DefaultConfiguration.java:756)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:210)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:205)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:67)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:167)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:54)
	at org.gradle.api.internal.artifacts.configurations.DefaultConfiguration.resolveGraphInBuildOperation(DefaultConfiguration.java:756)
	at org.gradle.api.internal.artifacts.configurations.DefaultConfiguration.lambda$resolveExclusivelyIfRequired$5(DefaultConfiguration.java:748)
	at org.gradle.api.internal.project.DefaultProjectStateRegistry$CalculatedModelValueImpl.update(DefaultProjectStateRegistry.java:533)
	at org.gradle.api.internal.artifacts.configurations.DefaultConfiguration.resolveExclusivelyIfRequired(DefaultConfiguration.java:743)
	at org.gradle.api.internal.artifacts.configurations.DefaultConfiguration.resolveGraphIfRequired(DefaultConfiguration.java:736)
	at org.gradle.api.internal.artifacts.configurations.DefaultConfiguration.access$1300(DefaultConfiguration.java:150)
	at org.gradle.api.internal.artifacts.configurations.DefaultConfiguration$ResolverResultsResolutionResultProvider.getValue(DefaultConfiguration.java:709)
	at org.gradle.api.internal.artifacts.configurations.DefaultConfiguration$ResolverResultsResolutionResultProvider.getValue(DefaultConfiguration.java:695)
	at org.gradle.api.internal.artifacts.configurations.ResolutionResultProvider$1.getValue(ResolutionResultProvider.java:54)
	at org.gradle.api.internal.artifacts.configurations.ResolutionResultProviderBackedSelectedArtifactSet.visitArtifacts(ResolutionResultProviderBackedSelectedArtifactSet.java:50)
	at org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact.SelectedArtifactSet.visitFiles(SelectedArtifactSet.java:34)
	at org.gradle.api.internal.artifacts.configurations.ResolutionBackedFileCollection.visitContents(ResolutionBackedFileCollection.java:75)
	at org.gradle.api.internal.file.AbstractFileCollection.visitStructure(AbstractFileCollection.java:360)
	at org.gradle.api.internal.file.CompositeFileCollection.lambda$visitContents$0(CompositeFileCollection.java:113)
	at org.gradle.api.internal.file.collections.UnpackingVisitor.add(UnpackingVisitor.java:67)
	at org.gradle.api.internal.file.collections.UnpackingVisitor.add(UnpackingVisitor.java:92)
	at org.gradle.api.internal.file.DefaultFileCollectionFactory$ResolvingFileCollection.visitChildren(DefaultFileCollectionFactory.java:306)
	at org.gradle.api.internal.file.CompositeFileCollection.visitContents(CompositeFileCollection.java:113)
	at org.gradle.api.internal.file.AbstractFileCollection.visitStructure(AbstractFileCollection.java:360)
	at org.gradle.api.internal.file.CompositeFileCollection.lambda$visitContents$0(CompositeFileCollection.java:113)
	at org.gradle.api.internal.tasks.PropertyFileCollection.visitChildren(PropertyFileCollection.java:48)
	at org.gradle.api.internal.file.CompositeFileCollection.visitContents(CompositeFileCollection.java:113)
	at org.gradle.api.internal.file.AbstractFileCollection.visitStructure(AbstractFileCollection.java:360)
	at org.gradle.internal.fingerprint.impl.DefaultFileCollectionSnapshotter.snapshot(DefaultFileCollectionSnapshotter.java:47)
	at org.gradle.internal.execution.impl.DefaultInputFingerprinter$InputCollectingVisitor.visitInputFileProperty(DefaultInputFingerprinter.java:133)
	at org.gradle.api.internal.tasks.execution.TaskExecution.visitRegularInputs(TaskExecution.java:324)
	at org.gradle.internal.execution.impl.DefaultInputFingerprinter.fingerprintInputProperties(DefaultInputFingerprinter.java:63)
	at org.gradle.internal.execution.steps.AbstractCaptureStateBeforeExecutionStep.captureExecutionStateWithOutputs(AbstractCaptureStateBeforeExecutionStep.java:109)
	at org.gradle.internal.execution.steps.AbstractCaptureStateBeforeExecutionStep.lambda$captureExecutionState$0(AbstractCaptureStateBeforeExecutionStep.java:74)
	at org.gradle.internal.execution.steps.BuildOperationStep$1.call(BuildOperationStep.java:37)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:210)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:205)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:67)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:167)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:54)
	at org.gradle.internal.execution.steps.BuildOperationStep.operation(BuildOperationStep.java:34)
	at org.gradle.internal.execution.steps.AbstractCaptureStateBeforeExecutionStep.captureExecutionState(AbstractCaptureStateBeforeExecutionStep.java:69)
	at org.gradle.internal.execution.steps.AbstractCaptureStateBeforeExecutionStep.execute(AbstractCaptureStateBeforeExecutionStep.java:62)
	at org.gradle.internal.execution.steps.AbstractCaptureStateBeforeExecutionStep.execute(AbstractCaptureStateBeforeExecutionStep.java:43)
	at org.gradle.internal.execution.steps.AbstractSkipEmptyWorkStep.executeWithNonEmptySources(AbstractSkipEmptyWorkStep.java:125)
	at org.gradle.internal.execution.steps.AbstractSkipEmptyWorkStep.execute(AbstractSkipEmptyWorkStep.java:56)
	at org.gradle.internal.execution.steps.AbstractSkipEmptyWorkStep.execute(AbstractSkipEmptyWorkStep.java:36)
	at org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsStartedStep.execute(MarkSnapshottingInputsStartedStep.java:38)
	at org.gradle.internal.execution.steps.LoadPreviousExecutionStateStep.execute(LoadPreviousExecutionStateStep.java:36)
	at org.gradle.internal.execution.steps.LoadPreviousExecutionStateStep.execute(LoadPreviousExecutionStateStep.java:23)
	at org.gradle.internal.execution.steps.HandleStaleOutputsStep.execute(HandleStaleOutputsStep.java:75)
	at org.gradle.internal.execution.steps.HandleStaleOutputsStep.execute(HandleStaleOutputsStep.java:41)
	at org.gradle.internal.execution.steps.AssignMutableWorkspaceStep.lambda$execute$0(AssignMutableWorkspaceStep.java:35)
	at org.gradle.api.internal.tasks.execution.TaskExecution$4.withWorkspace(TaskExecution.java:289)
	at org.gradle.internal.execution.steps.AssignMutableWorkspaceStep.execute(AssignMutableWorkspaceStep.java:31)
	at org.gradle.internal.execution.steps.AssignMutableWorkspaceStep.execute(AssignMutableWorkspaceStep.java:22)
	at org.gradle.internal.execution.steps.ChoosePipelineStep.execute(ChoosePipelineStep.java:40)
	at org.gradle.internal.execution.steps.ChoosePipelineStep.execute(ChoosePipelineStep.java:23)
	at org.gradle.internal.execution.steps.ExecuteWorkBuildOperationFiringStep.lambda$execute$2(ExecuteWorkBuildOperationFiringStep.java:67)
	at org.gradle.internal.execution.steps.ExecuteWorkBuildOperationFiringStep.execute(ExecuteWorkBuildOperationFiringStep.java:67)
	at org.gradle.internal.execution.steps.ExecuteWorkBuildOperationFiringStep.execute(ExecuteWorkBuildOperationFiringStep.java:39)
	at org.gradle.internal.execution.steps.IdentityCacheStep.execute(IdentityCacheStep.java:46)
	at org.gradle.internal.execution.steps.IdentityCacheStep.execute(IdentityCacheStep.java:34)
	at org.gradle.internal.execution.steps.IdentifyStep.execute(IdentifyStep.java:48)
	at org.gradle.internal.execution.steps.IdentifyStep.execute(IdentifyStep.java:35)
	at org.gradle.internal.execution.impl.DefaultExecutionEngine$1.execute(DefaultExecutionEngine.java:61)
	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeIfValid(ExecuteActionsTaskExecuter.java:127)
	at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.execute(ExecuteActionsTaskExecuter.java:116)
	at org.gradle.api.internal.tasks.execution.ProblemsTaskPathTrackingTaskExecuter.execute(ProblemsTaskPathTrackingTaskExecuter.java:40)
	at org.gradle.api.internal.tasks.execution.FinalizePropertiesTaskExecuter.execute(FinalizePropertiesTaskExecuter.java:46)
	at org.gradle.api.internal.tasks.execution.ResolveTaskExecutionModeExecuter.execute(ResolveTaskExecutionModeExecuter.java:51)
	at org.gradle.api.internal.tasks.execution.SkipTaskWithNoActionsExecuter.execute(SkipTaskWithNoActionsExecuter.java:57)
	at org.gradle.api.internal.tasks.execution.SkipOnlyIfTaskExecuter.execute(SkipOnlyIfTaskExecuter.java:74)
	at org.gradle.api.internal.tasks.execution.CatchExceptionTaskExecuter.execute(CatchExceptionTaskExecuter.java:36)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.executeTask(EventFiringTaskExecuter.java:77)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:55)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:52)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:210)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:205)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:67)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:167)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:54)
	at org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter.execute(EventFiringTaskExecuter.java:52)
	at org.gradle.execution.plan.LocalTaskNodeExecutor.execute(LocalTaskNodeExecutor.java:42)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:331)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:318)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.lambda$execute$0(DefaultTaskExecutionGraph.java:314)
	at org.gradle.internal.operations.CurrentBuildOperationRef.with(CurrentBuildOperationRef.java:85)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:314)
	at org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:303)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.execute(DefaultPlanExecutor.java:459)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.run(DefaultPlanExecutor.java:376)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
	at org.gradle.internal.concurrent.AbstractManagedExecutor$1.run(AbstractManagedExecutor.java:48)
Caused by: org.gradle.internal.resource.transport.http.HttpRequestException: Could not GET 'https://maven.rikka.dev/versioned/dev/rikka/shizuku/ktx/13.1.5/ktx-13.1.5.pom'.
	at org.gradle.internal.resource.transport.http.HttpClientHelper.createHttpRequestException(HttpClientHelper.java:124)
	at org.gradle.internal.resource.transport.http.HttpClientHelper.performRequest(HttpClientHelper.java:118)
	at org.gradle.internal.resource.transport.http.HttpClientHelper.performRawGet(HttpClientHelper.java:100)
	at org.gradle.internal.resource.transport.http.HttpClientHelper.performGet(HttpClientHelper.java:105)
	at org.gradle.internal.resource.transport.http.HttpResourceAccessor.openResource(HttpResourceAccessor.java:45)
	at org.gradle.internal.resource.transport.http.HttpResourceAccessor.openResource(HttpResourceAccessor.java:30)
	at org.gradle.internal.resource.transfer.AbstractExternalResourceAccessor.withContent(AbstractExternalResourceAccessor.java:32)
	at org.gradle.internal.resource.transfer.DefaultExternalResourceConnector.withContent(DefaultExternalResourceConnector.java:59)
	at org.gradle.internal.resource.transfer.ProgressLoggingExternalResourceAccessor$DownloadOperation.call(ProgressLoggingExternalResourceAccessor.java:136)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:210)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:205)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:67)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:167)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:60)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:54)
	at org.gradle.internal.resource.transfer.ProgressLoggingExternalResourceAccessor.withContent(ProgressLoggingExternalResourceAccessor.java:49)
	at org.gradle.internal.resource.transfer.AccessorBackedExternalResource.withContentIfPresent(AccessorBackedExternalResource.java:103)
	at org.gradle.internal.resource.transfer.DefaultCacheAwareExternalResourceAccessor.copyToCache(DefaultCacheAwareExternalResourceAccessor.java:188)
	at org.gradle.internal.resource.transfer.DefaultCacheAwareExternalResourceAccessor.lambda$getResource$1(DefaultCacheAwareExternalResourceAccessor.java:86)
	at org.gradle.cache.internal.ProducerGuard$AdaptiveProducerGuard.guardByKey(ProducerGuard.java:97)
	at org.gradle.internal.resource.transfer.DefaultCacheAwareExternalResourceAccessor.getResource(DefaultCacheAwareExternalResourceAccessor.java:80)
	at org.gradle.api.internal.artifacts.repositories.resolver.DefaultExternalResourceArtifactResolver.downloadByCoords(DefaultExternalResourceArtifactResolver.java:149)
	... 147 more
Caused by: java.net.UnknownHostException: maven.rikka.dev: Name or service not known
	at org.apache.http.impl.conn.SystemDefaultDnsResolver.resolve(SystemDefaultDnsResolver.java:45)
	at org.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(DefaultHttpClientConnectionOperator.java:112)
	at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.connect(PoolingHttpClientConnectionManager.java:376)
	at org.apache.http.impl.execchain.MainClientExec.establishRoute(MainClientExec.java:393)
	at org.apache.http.impl.execchain.MainClientExec.execute(MainClientExec.java:236)
	at org.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:186)
	at org.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:89)
	at org.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:110)
	at org.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:185)
	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:83)
	at org.gradle.internal.resource.transport.http.HttpClientHelper.performHttpRequest(HttpClientHelper.java:201)
	at org.gradle.internal.resource.transport.http.HttpClientHelper.performHttpRequest(HttpClientHelper.java:177)
	at org.gradle.internal.resource.transport.http.HttpClientHelper.executeGetOrHead(HttpClientHelper.java:166)
	at org.gradle.internal.resource.transport.http.HttpClientHelper.performRequest(HttpClientHelper.java:114)
	... 168 more


BUILD FAILED in 2s
1 actionable task: 1 executed
```

## File: build.gradle.kts
```
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
```

## File: gradlew
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

## File: gradlew.bat
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

## File: multidex-keep.txt
```
rikka/shizuku/
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

## File: app/src/main/java/com/example/quadrantlauncher/QuadrantActivity.kt
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

## File: app/src/main/java/com/example/quadrantlauncher/SplitActivity.kt
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

## File: app/src/main/java/com/example/quadrantlauncher/TriSplitActivity.kt
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

## File: app/src/main/res/layout/activity_main.xml
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

## File: app/src/main/res/layout/activity_split.xml
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

## File: app/src/main/res/layout/item_font_size.xml
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

## File: app/src/main/res/layout/item_selected_app.xml
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

## File: app/src/main/res/layout/list_item_app.xml
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

## File: app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/ic_wolf_cover" />
</adaptive-icon>
```

## File: app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/black" />
    <foreground android:drawable="@drawable/ic_wolf_main" />
</adaptive-icon>
```

## File: app/build.gradle.kts
```
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.quadrantlauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.com.katsuyamaki.coverscreenlauncher"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        aidl = true
    }

    // THIS BLOCK IS CRITICAL FOR AIDL TO WORK WITH KOTLIN
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

## File: app/proguard-rules.pro
```
-keep class com.example.quadrantlauncher.ShellUserService { *; }
-keep class com.example.quadrantlauncher.IShellService { *; }
-keep interface com.example.quadrantlauncher.IShellService { *; }
```

## File: gradle.properties
```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2
```

## File: README.md
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

## File: app/src/main/java/com/example/quadrantlauncher/MenuActivity.kt
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

## File: app/src/main/java/com/example/quadrantlauncher/ShizukuHelper.kt
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

## File: app/src/main/res/layout/item_profile_rich.xml
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

## File: app/src/main/res/layout/layout_bubble.xml
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

## File: app/src/main/AndroidManifest.xml
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
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
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

## File: app/src/main/java/com/example/quadrantlauncher/MainActivity.kt
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

## File: app/src/main/res/layout/item_layout_option.xml
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

## File: app/src/main/res/layout/layout_rofi_drawer.xml
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

## File: app/src/main/aidl/com/example/quadrantlauncher/IShellService.aidl
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
    
    // --- NEW METHODS ---
    int getTaskId(String packageName);
    void moveTaskToBack(int taskId);
}
```

## File: app/src/main/java/com/example/quadrantlauncher/ShellUserService.kt
```kotlin
package com.example.quadrantlauncher

import android.os.IBinder
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"

    override fun forceStop(packageName: String) {
        try {
            val process = Runtime.getRuntime().exec("am force-stop $packageName")
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to kill", e)
        }
    }

    override fun runCommand(command: String) {
        try {
            Log.i(TAG, "Running: $command")
            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Command failed: $command", e)
        }
    }

    override fun setScreenOff(displayIndex: Int, turnOff: Boolean) {
        try {
            val scClass = if (Build.VERSION.SDK_INT >= 34) {
                try { Class.forName("com.android.server.display.DisplayControl") } catch (e: Exception) { Class.forName("android.view.SurfaceControl") }
            } else {
                Class.forName("android.view.SurfaceControl")
            }

            val getIdsMethod = scClass.getMethod("getPhysicalDisplayIds")
            val physicalIds = getIdsMethod.invoke(null) as LongArray

            if (physicalIds.isEmpty()) return

            val targetId = if (displayIndex >= 0 && displayIndex < physicalIds.size) {
                physicalIds[displayIndex]
            } else {
                physicalIds[0]
            }

            val getTokenMethod = scClass.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
            val token = getTokenMethod.invoke(null, targetId) as? IBinder ?: return

            val mode = if (turnOff) 0 else 2 
            val ctrlClass = Class.forName("android.view.SurfaceControl")
            val setPowerMethod = ctrlClass.getMethod("setDisplayPowerMode", IBinder::class.java, Int::class.javaPrimitiveType)
            setPowerMethod.invoke(null, token, mode)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set screen power", e)
        }
    }

    override fun repositionTask(packageName: String, left: Int, top: Int, right: Int, bottom: Int) {
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity top")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            
            var line: String?
            var targetTaskId = -1
            
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.startsWith("TASK") && l.contains(packageName)) {
                    val match = Regex("id=(\\d+)").find(l)
                    if (match != null) {
                        targetTaskId = match.groupValues[1].toInt()
                        break
                    }
                }
            }
            reader.close()
            process.waitFor()

            if (targetTaskId != -1) {
                Runtime.getRuntime().exec("am task set-windowing-mode $targetTaskId 5").waitFor()
                val cmd = "am task resize $targetTaskId $left $top $right $bottom"
                Runtime.getRuntime().exec(cmd).waitFor()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to reposition task", e)
        }
    }

    override fun getVisiblePackages(displayId: Int): List<String> {
        val packages = ArrayList<String>()
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
                    if (displayMatch != null) {
                        currentScanningDisplayId = displayMatch.groupValues[1].toInt()
                    }
                    continue
                }

                if (currentScanningDisplayId == displayId) {
                    if (l.contains("ActivityRecord{")) {
                        val matcher = recordPattern.matcher(l)
                        if (matcher.find()) {
                            val pkg = matcher.group(1)
                            if (pkg != null && !packages.contains(pkg) && isUserApp(pkg)) {
                                packages.add(pkg)
                            }
                        }
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get visible packages", e)
        }
        return packages
    }

    override fun getAllRunningPackages(): List<String> {
        val packages = ArrayList<String>()
        try {
            val process = Runtime.getRuntime().exec("dumpsys activity activities")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val recordPattern = Pattern.compile("ActivityRecord\\{[0-9a-f]+ u\\d+ ([a-zA-Z0-9_.]+)/")

            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                if (l.contains("ActivityRecord{")) {
                    val matcher = recordPattern.matcher(l)
                    if (matcher.find()) {
                        val pkg = matcher.group(1)
                        if (pkg != null && !packages.contains(pkg) && isUserApp(pkg)) {
                            packages.add(pkg)
                        }
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get running packages", e)
        }
        return packages
    }

    override fun getWindowLayouts(displayId: Int): List<String> {
        val results = ArrayList<String>()
        try {
            val process = Runtime.getRuntime().exec("dumpsys window windows")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            
            var line: String?
            var pendingPkg: String? = null
            var isVisible = false
            var isBaseApp = false
            
            val windowPattern = Pattern.compile("Window\\{[0-9a-f]+ u\\d+ ([^\\}/ ]+)")
            val framePattern = Pattern.compile("(?:frame|mFrame)=\\[(-?\\d+),(-?\\d+)\\]\\[(-?\\d+),(-?\\d+)\\]")

            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                
                if (l.startsWith("Window #")) {
                    pendingPkg = null
                    isVisible = false
                    isBaseApp = false
                    
                    val matcher = windowPattern.matcher(l)
                    if (matcher.find()) {
                        val pkg = matcher.group(1)
                        if (isUserApp(pkg)) {
                            pendingPkg = pkg
                        }
                    }
                    continue
                }
                
                if (pendingPkg == null) continue

                if (l.contains("mViewVisibility=0x0")) isVisible = true
                if (l.contains("ty=BASE_APPLICATION") || l.contains("type=BASE_APPLICATION")) isBaseApp = true
                
                if (isVisible && isBaseApp && (l.contains("frame=") || l.contains("mFrame="))) {
                    val matcher = framePattern.matcher(l)
                    if (matcher.find()) {
                        try {
                            val left = matcher.group(1).toInt()
                            val top = matcher.group(2).toInt()
                            val right = matcher.group(3).toInt()
                            val bottom = matcher.group(4).toInt()
                            
                            if ((right - left) > 10 && (bottom - top) > 10) {
                                results.add("$pendingPkg|$left,$top,$right,$bottom")
                                pendingPkg = null 
                            }
                        } catch (e: Exception) {}
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get window layouts", e)
        }
        return results
    }

    // --- NEW: Implement getTaskId ---
    override fun getTaskId(packageName: String): Int {
        var taskId = -1
        try {
            // grep -E 'Task id|com.package' logic
            val cmd = arrayOf("sh", "-c", "dumpsys activity activities | grep -E 'Task id|$packageName'")
            val process = Runtime.getRuntime().exec(cmd)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val l = line!!.trim()
                // Only parse lines containing the package name
                if (l.contains(packageName)) {
                    // 1. Check Task Header: "* Task{... #11390 ...}"
                    if (l.startsWith("* Task{") || l.startsWith("Task{")) {
                         val match = Regex("#(\\d+)").find(l)
                         if (match != null) {
                             taskId = match.groupValues[1].toInt()
                             break 
                         }
                    }
                    // 2. Check ActivityRecord: "... t11390}"
                    if (l.contains("ActivityRecord")) {
                         val match = Regex("t(\\d+)").find(l)
                         if (match != null) {
                             taskId = match.groupValues[1].toInt()
                             break
                         }
                    }
                }
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to find Task ID for $packageName", e)
        }
        return taskId
    }

    // --- NEW: Implement moveTaskToBack ---
    override fun moveTaskToBack(taskId: Int) {
        try {
            // Modern Android (10+)
            val atmClass = Class.forName("android.app.ActivityTaskManager")
            val serviceMethod = atmClass.getMethod("getService")
            val atm = serviceMethod.invoke(null)
            val moveMethod = atm.javaClass.getMethod("moveTaskToBack", Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
            moveMethod.invoke(atm, taskId, true)
        } catch (e: Exception) {
            // Fallback
            try {
                val am = Class.forName("android.app.ActivityManagerNative").getMethod("getDefault").invoke(null)
                val moveMethod = am.javaClass.getMethod("moveTaskToBack", Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
                moveMethod.invoke(am, taskId, true)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to move task to back", e2)
            }
        }
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
}
```

## File: app/src/main/java/com/example/quadrantlauncher/AppPreferences.kt
```kotlin
package com.example.quadrantlauncher

import android.content.Context

object AppPreferences {

    private const val PREFS_NAME = "AppLauncherPrefs"
    private const val KEY_FAVORITES = "KEY_FAVORITES"
    private const val KEY_LAST_LAYOUT = "KEY_LAST_LAYOUT"
    private const val KEY_LAST_CUSTOM_LAYOUT_NAME = "KEY_LAST_CUSTOM_LAYOUT_NAME"
    private const val KEY_LAST_RESOLUTION = "KEY_LAST_RESOLUTION"
    private const val KEY_LAST_DPI = "KEY_LAST_DPI"
    private const val KEY_PROFILES = "KEY_PROFILES"
    private const val KEY_CUSTOM_LAYOUTS = "KEY_CUSTOM_LAYOUTS"
    private const val KEY_FONT_SIZE = "KEY_FONT_SIZE"
    private const val KEY_ICON_URI = "KEY_ICON_URI"
    
    // Settings
    private const val KEY_KILL_ON_EXECUTE = "KEY_KILL_ON_EXECUTE"
    private const val KEY_TARGET_DISPLAY_INDEX = "KEY_TARGET_DISPLAY_INDEX"
    private const val KEY_IS_INSTANT_MODE = "KEY_IS_INSTANT_MODE"
    private const val KEY_LAST_QUEUE = "KEY_LAST_QUEUE"
    
    // Drawer Geometry
    private const val KEY_DRAWER_HEIGHT = "KEY_DRAWER_HEIGHT"
    private const val KEY_DRAWER_WIDTH = "KEY_DRAWER_WIDTH" // New
    private const val KEY_AUTO_RESIZE_KEYBOARD = "KEY_AUTO_RESIZE_KEYBOARD"

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

    fun saveLastResolution(context: Context, resIndex: Int) {
        getPrefs(context).edit().putInt(KEY_LAST_RESOLUTION, resIndex).apply()
    }

    fun getLastResolution(context: Context): Int {
        return getPrefs(context).getInt(KEY_LAST_RESOLUTION, 0)
    }

    fun saveLastDpi(context: Context, dpi: Int) {
        getPrefs(context).edit().putInt(KEY_LAST_DPI, dpi).apply()
    }

    fun getLastDpi(context: Context): Int {
        return getPrefs(context).getInt(KEY_LAST_DPI, -1)
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
    
    // NEW: Drawer Geometry
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
    
    // NEW: Auto Resize
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
        return getPrefs(context).getBoolean(KEY_KILL_ON_EXECUTE, true)
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
        return getPrefs(context).getBoolean(KEY_IS_INSTANT_MODE, false)
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
        const val LAYOUT_CUSTOM_DYNAMIC = 99

        const val CHANNEL_ID = "OverlayServiceChannel"
        const val TAG = "FloatingService"
        const val ACTION_OPEN_DRAWER = "com.example.quadrantlauncher.OPEN_DRAWER"
        const val ACTION_UPDATE_ICON = "com.example.quadrantlauncher.UPDATE_ICON"
    }

    private lateinit var windowManager: WindowManager
    private var displayContext: Context? = null
    private var currentDisplayId = 0
    
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
    private var isInstantMode = false 
    
    private var currentDrawerHeightPercent = 70
    private var currentDrawerWidthPercent = 90
    private var autoResizeEnabled = true
    
    private val PACKAGE_BLANK = "internal.blank.spacer"
    
    private var shellService: IShellService? = null
    private var isBound = false
    private val uiHandler = Handler(Looper.getMainLooper())

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
                if (item.isCurrent) {
                     (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos)
                     return
                }
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
                } else {
                    (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos)
                }
                return
            }

            if (currentMode == MODE_SEARCH) {
                val item = displayList.getOrNull(pos) as? MainActivity.AppInfo ?: return
                if (item.packageName == PACKAGE_BLANK) {
                    (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos)
                    return
                }
                if (direction == ItemTouchHelper.LEFT && !item.isFavorite) toggleFavorite(item)
                else if (direction == ItemTouchHelper.RIGHT && item.isFavorite) toggleFavorite(item)
                refreshSearchList()
            }
        }
    }

    private val selectedAppsDragCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 
        ItemTouchHelper.UP or ItemTouchHelper.DOWN 
    ) {
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean {
            val fromPos = v.adapterPosition
            val toPos = t.adapterPosition
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
                    Thread { 
                        try { shellService?.forceStop(app.packageName) } catch(e: Exception) {}
                    }.start()
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
            showToast("Shizuku Connected")
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            shellService = null
            isBound = false
            updateExecuteButtonColor(false)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
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
        
        currentDrawerHeightPercent = AppPreferences.getDrawerHeightPercent(this)
        currentDrawerWidthPercent = AppPreferences.getDrawerWidthPercent(this)
        autoResizeEnabled = AppPreferences.getAutoResizeKeyboard(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (bubbleView != null) return START_NOT_STICKY
        val displayId = intent?.getIntExtra("DISPLAY_ID", Display.DEFAULT_DISPLAY) ?: Display.DEFAULT_DISPLAY
        try {
            setupDisplayContext(displayId)
            setupBubble()
            setupDrawer()
            
            selectedLayoutType = AppPreferences.getLastLayout(this)
            activeCustomLayoutName = AppPreferences.getLastCustomLayoutName(this)
            
            selectedResolutionIndex = AppPreferences.getLastResolution(this)
            currentDpiSetting = AppPreferences.getLastDpi(this)
            updateGlobalFontSize()
            updateBubbleIcon()
            
            if (selectedLayoutType == LAYOUT_CUSTOM_DYNAMIC && activeCustomLayoutName != null) {
                val data = AppPreferences.getCustomLayoutData(this, activeCustomLayoutName!!)
                if (data != null) {
                    val rects = mutableListOf<Rect>()
                    val rectParts = data.split("|")
                    for (rp in rectParts) {
                        val coords = rp.split(",")
                        if (coords.size == 4) {
                            rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt()))
                        }
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
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(commandReceiver) } catch(e: Exception) {}
        try { if (bubbleView != null) windowManager.removeView(bubbleView) } catch(e: Exception) {}
        try { if (isExpanded) windowManager.removeView(drawerView) } catch(e: Exception) {}
        if (isBound) {
            try {
                ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection)
                isBound = false
            } catch (e: Exception) {}
        }
    }

    private fun showToast(msg: String) {
        uiHandler.post { Toast.makeText(this@FloatingLauncherService, msg, Toast.LENGTH_SHORT).show() }
    }

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
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("CoverScreen Launcher Active")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
        if (android.os.Build.VERSION.SDK_INT >= 34) {
            startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)
        }
    }

    private fun bindShizuku() {
        try {
            val component = ComponentName(packageName, ShellUserService::class.java.name)
            ShizukuBinder.bind(component, userServiceConnection, true, 1)
        } catch (e: Exception) {
            Log.e(TAG, "Bind Shizuku Failed", e)
        }
    }

    private fun updateExecuteButtonColor(isReady: Boolean) {
        uiHandler.post {
            val executeBtn = drawerView?.findViewById<ImageView>(R.id.icon_execute)
            if (isReady) executeBtn?.setColorFilter(Color.GREEN) else executeBtn?.setColorFilter(Color.RED)
        }
    }

    private fun setupBubble() {
        val context = displayContext ?: this
        val themeContext = ContextThemeWrapper(context, R.style.Theme_QuadrantLauncher)
        bubbleView = LayoutInflater.from(themeContext).inflate(R.layout.layout_bubble, null)
        bubbleView?.isClickable = true; bubbleView?.isFocusable = true 

        bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        bubbleParams.gravity = Gravity.TOP or Gravity.START
        bubbleParams.x = 50; bubbleParams.y = 200

        var velocityTracker: VelocityTracker? = null

        bubbleView?.setOnTouchListener(object : View.OnTouchListener {
            var initialX = 0; var initialY = 0; var initialTouchX = 0f; var initialTouchY = 0f; var isDrag = false
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = bubbleParams.x; initialY = bubbleParams.y
                        initialTouchX = event.rawX; initialTouchY = event.rawY; isDrag = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (Math.abs(event.rawX - initialTouchX) > 10 || Math.abs(event.rawY - initialTouchY) > 10) isDrag = true
                        if (isDrag) {
                            bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt()
                            bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt()
                            windowManager.updateViewLayout(bubbleView, bubbleParams)
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        velocityTracker?.computeCurrentVelocity(1000)
                        val vX = velocityTracker?.xVelocity ?: 0f
                        val vY = velocityTracker?.yVelocity ?: 0f
                        val totalVel = hypot(vX.toDouble(), vY.toDouble())
                        if (isDrag && totalVel > 2500) {
                            showToast("Closing...")
                            stopSelf()
                            return true
                        }
                        if (!isDrag) toggleDrawer()
                        velocityTracker?.recycle()
                        velocityTracker = null
                        return true
                    }
                    MotionEvent.ACTION_CANCEL -> { velocityTracker?.recycle(); velocityTracker = null }
                }
                return false
            }
        })
        windowManager.addView(bubbleView, bubbleParams)
    }

    private fun updateBubbleIcon() {
        try {
            val uriStr = AppPreferences.getIconUri(this)
            val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon)
            if (uriStr != null) {
                val uri = Uri.parse(uriStr)
                val input = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(input)
                input?.close()
                if (bitmap != null) {
                    iconView?.setImageBitmap(bitmap); iconView?.imageTintList = null; iconView?.clearColorFilter()   
                } else {
                    iconView?.setImageResource(R.mipmap.ic_launcher_round)
                }
            } else {
                iconView?.setImageResource(R.mipmap.ic_launcher_round)
            }
        } catch (e: Exception) {
            val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon)
            iconView?.setImageResource(R.mipmap.ic_launcher_round)
        }
    }

    // --- NEW HELPER: Dismiss Keyboard and Expand Drawer ---
    private fun dismissKeyboardAndRestore() {
        val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar) ?: return
        if (searchBar.hasFocus()) {
            searchBar.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchBar.windowToken, 0)
            updateDrawerHeight(false)
        }
    }

    private fun setupDrawer() {
        val context = displayContext ?: this
        val themeContext = ContextThemeWrapper(context, R.style.Theme_QuadrantLauncher)
        drawerView = LayoutInflater.from(themeContext).inflate(R.layout.layout_rofi_drawer, null)
        
        drawerView!!.fitsSystemWindows = true 
        
        drawerParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, 
            PixelFormat.TRANSLUCENT
        )
        
        drawerParams.gravity = Gravity.TOP or Gravity.START
        drawerParams.x = 0
        drawerParams.y = 0
        drawerParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container)
        if (container != null) {
            val lp = container.layoutParams as? FrameLayout.LayoutParams
            if (lp != null) {
                lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                lp.topMargin = 100
                container.layoutParams = lp
            }
        }
        
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
        
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filterList(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })

        searchBar.imeOptions = EditorInfo.IME_ACTION_DONE
        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dismissKeyboardAndRestore()
                return@setOnEditorActionListener true
            }
            false
        }

        searchBar.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (searchBar.text.isEmpty() && selectedAppsQueue.isNotEmpty()) {
                    val lastIndex = selectedAppsQueue.size - 1
                    selectedAppsQueue.removeAt(lastIndex)
                    updateSelectedAppsDock()
                    mainRecycler.adapter?.notifyDataSetChanged()
                    return@setOnKeyListener true
                }
            }
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (searchBar.hasFocus()) {
                    dismissKeyboardAndRestore()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
        
        searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (autoResizeEnabled) {
                updateDrawerHeight(hasFocus)
            }
        }

        mainRecycler.layoutManager = LinearLayoutManager(themeContext)
        mainRecycler.adapter = RofiAdapter()
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(mainRecycler)
        
        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                   dismissKeyboardAndRestore()
                }
            }
        })
        
        mainRecycler.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dismissKeyboardAndRestore()
            }
            false
        }
        
        selectedRecycler.layoutManager = LinearLayoutManager(themeContext, LinearLayoutManager.HORIZONTAL, false)
        selectedRecycler.adapter = SelectedAppsAdapter()
        val dockTouchHelper = ItemTouchHelper(selectedAppsDragCallback)
        dockTouchHelper.attachToRecyclerView(selectedRecycler)
        
        drawerView!!.setOnClickListener { toggleDrawer() }
        drawerView!!.isFocusableInTouchMode = true
        drawerView!!.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { toggleDrawer(); true } 
            else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && isExtinguished) { wakeUp(); true }
            else false
        }
    }
    
    private fun updateDrawerHeight(isKeyboardMode: Boolean) {
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container) ?: return
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
        val screenH = dm.heightPixels
        val screenW = dm.widthPixels
        
        val lp = container.layoutParams as? FrameLayout.LayoutParams
        val topMargin = lp?.topMargin ?: 100
        
        var finalHeight = (screenH * (currentDrawerHeightPercent / 100f)).toInt()
        
        if (isKeyboardMode) {
            finalHeight = (screenH * 0.40f).toInt()
            val maxAvailable = screenH - topMargin - 20
            if (finalHeight > maxAvailable) finalHeight = maxAvailable
        }
        
        val newW = (screenW * (currentDrawerWidthPercent / 100f)).toInt()
        
        if (container.layoutParams.height != finalHeight || container.layoutParams.width != newW) {
            container.layoutParams.width = newW
            container.layoutParams.height = finalHeight
            container.requestLayout()
            
            if (drawerParams.y != 0) {
                 drawerParams.y = 0
                 windowManager.updateViewLayout(drawerView, drawerParams)
            }
        }
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
            et?.setText("")
            
            et?.clearFocus()
            
            updateSelectedAppsDock()
            if (isInstantMode) fetchRunningApps()
        }
    }

    private fun updateGlobalFontSize() {
        val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)
        searchBar?.textSize = currentFontSize
        drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }

    private fun loadInstalledApps() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val riList = pm.queryIntentActivities(intent, 0)
        allAppsList.clear()
        allAppsList.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK))
        for (ri in riList) {
            val app = MainActivity.AppInfo(
                ri.loadLabel(pm).toString(),
                ri.activityInfo.packageName,
                AppPreferences.isFavorite(this, ri.activityInfo.packageName)
            )
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
            LAYOUT_CUSTOM_DYNAMIC -> "Custom"
            else -> "Unknown"
        }
    }

    private fun getRatioName(index: Int): String {
        return when(index) {
            1 -> "1:1"
            2 -> "16:9"
            3 -> "32:9"
            else -> "Default"
        }
    }

    private fun getTargetDimensions(index: Int): Pair<Int, Int>? {
        return when(index) {
            1 -> 1422 to 1500
            2 -> 1920 to 1080
            3 -> 3840 to 1080
            else -> null 
        }
    }
    
    private fun getResolutionCommand(index: Int): String {
        return when(index) {
            1 -> "wm size 1422x1500 -d $currentDisplayId"
            2 -> "wm size 1920x1080 -d $currentDisplayId"
            3 -> "wm size 3840x1080 -d $currentDisplayId"
            else -> "wm size reset -d $currentDisplayId"
        }
    }
    
    private fun sortAppQueue() {
        selectedAppsQueue.sortWith(compareBy { it.isMinimized })
    }
    
    private fun updateSelectedAppsDock() {
        val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler)
        if (selectedAppsQueue.isEmpty()) {
            dock.visibility = View.GONE
        } else {
            dock.visibility = View.VISIBLE
            dock.adapter?.notifyDataSetChanged()
            dock.scrollToPosition(selectedAppsQueue.size - 1)
        }
    }

    private fun refreshSearchList() {
        val query = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString() ?: ""
        filterList(query)
    }

    private fun filterList(query: String) {
        if (currentMode != MODE_SEARCH) return
        val actualQuery = query.substringAfterLast(",").trim()
        displayList.clear()
        val filtered = if (actualQuery.isEmpty()) {
            allAppsList
        } else {
            allAppsList.filter { it.label.contains(actualQuery, ignoreCase = true) }
        }
        val sorted = filtered.sortedWith(
            compareBy<MainActivity.AppInfo> { it.packageName != PACKAGE_BLANK }
            .thenByDescending { it.isFavorite }
            .thenBy { it.label.lowercase() }
        )
        displayList.addAll(sorted)
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }

    private fun addToSelection(app: MainActivity.AppInfo) {
        dismissKeyboardAndRestore()
        
        val et = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar)
        if (app.packageName == PACKAGE_BLANK) {
            selectedAppsQueue.add(app)
            sortAppQueue()
            updateSelectedAppsDock()
            drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
            if (isInstantMode) applyLayoutImmediate()
            return
        }
        val existing = selectedAppsQueue.find { it.packageName == app.packageName }
        if (existing != null) {
            selectedAppsQueue.remove(existing)
            Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start()
            showToast("Removed ${app.label}")
            sortAppQueue()
            updateSelectedAppsDock()
            drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
            et.setText("") 
            if (isInstantMode) applyLayoutImmediate()
        } else {
            app.isMinimized = false
            selectedAppsQueue.add(app)
            sortAppQueue()
            updateSelectedAppsDock()
            drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
            et.setText("") 
            if (isInstantMode) {
                launchViaApi(app.packageName, null)
                launchViaShell(app.packageName)
                uiHandler.postDelayed({ applyLayoutImmediate() }, 200)
                uiHandler.postDelayed({ applyLayoutImmediate() }, 800)
            }
        }
    }

    private fun toggleFavorite(app: MainActivity.AppInfo) {
        val newState = AppPreferences.toggleFavorite(this, app.packageName)
        app.isFavorite = newState
        allAppsList.find { it.packageName == app.packageName }?.isFavorite = newState
    }

    private fun launchViaApi(pkg: String, bounds: Rect?) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val options = android.app.ActivityOptions.makeBasic()
            if (bounds != null) options.setLaunchBounds(bounds)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {}
    }

    private fun launchViaShell(pkg: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return
            if (shellService != null) {
                val component = intent.component?.flattenToShortString() ?: pkg
                val cmd = "am start -n $component -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"
                Thread { shellService?.runCommand(cmd) }.start()
            }
        } catch (e: Exception) {}
    }
    
    private fun cycleDisplay() {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        var targetId = if (currentDisplayId == 0) 1 else 0
        var targetDisplay = dm.getDisplay(targetId)
        
        if (targetDisplay == null) {
            val displays = dm.displays
            val currentIdx = displays.indexOfFirst { it.displayId == currentDisplayId }
            val nextIdx = if (currentIdx == -1) 0 else (currentIdx + 1) % displays.size
            targetDisplay = displays[nextIdx]
        }
        if (targetDisplay == null) {
            showToast("Error: Target display unavailable.")
            return
        }
        val newId = targetDisplay.displayId
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
        updateBubbleIcon() 
        isExpanded = false
        showToast("Switched to Display $currentDisplayId")
    }

    private fun performExtinguish() {
        toggleDrawer()
        isExtinguished = true
        Thread {
            try {
                shellService?.setScreenOff(targetDisplayIndex, true)
            } catch (e: Exception) {}
        }.start()
        showToast("Screen OFF (Index ${targetDisplayIndex}). Vol+ to Wake.")
    }

    private fun wakeUp() {
        isExtinguished = false
        Thread { 
            shellService?.setScreenOff(0, false) 
            shellService?.setScreenOff(1, false)
        }.start()
        showToast("Screen Woke Up")
        if (currentMode == MODE_SETTINGS) drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    
    private fun applyLayoutImmediate() {
        executeLaunch(selectedLayoutType, closeDrawer = false)
    }
    
    private fun fetchRunningApps() {
        if (shellService == null) return
        Thread {
            try {
                val visiblePackages = shellService!!.getVisiblePackages(currentDisplayId)
                val allRunning = shellService!!.getAllRunningPackages()
                val lastQueue = AppPreferences.getLastQueue(this)
                
                uiHandler.post {
                    selectedAppsQueue.clear()
                    for (pkg in lastQueue) {
                        if (pkg == PACKAGE_BLANK) {
                            selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK))
                        } else if (allRunning.contains(pkg)) {
                            val appInfo = allAppsList.find { it.packageName == pkg }
                            if (appInfo != null) {
                                appInfo.isMinimized = !visiblePackages.contains(pkg)
                                selectedAppsQueue.add(appInfo)
                            }
                        }
                    }
                    for (pkg in visiblePackages) {
                        if (!lastQueue.contains(pkg)) {
                            val appInfo = allAppsList.find { it.packageName == pkg }
                            if (appInfo != null) {
                                appInfo.isMinimized = false
                                selectedAppsQueue.add(appInfo)
                            }
                        }
                    }
                    sortAppQueue()
                    updateSelectedAppsDock()
                    drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
                    showToast("Instant Mode: Active")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching apps", e)
            }
        }.start()
    }

    private fun selectLayout(opt: LayoutOption) {
        dismissKeyboardAndRestore()
        
        selectedLayoutType = opt.type
        activeCustomRects = opt.customRects
        
        if (opt.type == LAYOUT_CUSTOM_DYNAMIC) {
            activeCustomLayoutName = opt.name
            AppPreferences.saveLastCustomLayoutName(this, opt.name)
        } else {
            activeCustomLayoutName = null
            AppPreferences.saveLastCustomLayoutName(this, null)
        }
        
        AppPreferences.saveLastLayout(this, opt.type)
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        if (isInstantMode) applyLayoutImmediate()
    }
    
    private fun saveCurrentAsCustom() {
        Thread {
            try {
                val rawLayouts = shellService!!.getWindowLayouts(currentDisplayId)
                if (rawLayouts.isEmpty()) {
                    showToast("Found 0 active app windows")
                    return@Thread
                }
                
                val rectStrings = mutableListOf<String>()
                for (line in rawLayouts) {
                    val parts = line.split("|")
                    if (parts.size == 2) {
                        rectStrings.add(parts[1])
                    }
                }
                
                if (rectStrings.isEmpty()) {
                    showToast("Found 0 valid frames")
                    return@Thread
                }
                
                val count = rectStrings.size
                var baseName = "$count Apps - Custom"
                val existingNames = AppPreferences.getCustomLayoutNames(this)
                var counter = 1
                var finalName = "$baseName $counter"
                
                while (existingNames.contains(finalName)) {
                    counter++
                    finalName = "$baseName $counter"
                }
                
                AppPreferences.saveCustomLayout(this, finalName, rectStrings.joinToString("|"))
                showToast("Saved: $finalName")
                uiHandler.post { switchMode(MODE_LAYOUTS) }
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save custom layout", e)
                showToast("Error saving: ${e.message}")
            }
        }.start()
    }

    private fun applyResolution(opt: ResolutionOption) {
        dismissKeyboardAndRestore()
        
        if (opt.index != -1) { 
            selectedResolutionIndex = opt.index
            AppPreferences.saveLastResolution(this, opt.index)
        }
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        
        if (isInstantMode) {
            Thread {
                val resCmd = getResolutionCommand(selectedResolutionIndex)
                shellService?.runCommand(resCmd)
                Thread.sleep(1500)
                uiHandler.post { applyLayoutImmediate() }
            }.start()
        }
    }
    
    private fun selectDpi(value: Int) {
        currentDpiSetting = value.coerceIn(100, 400)
        AppPreferences.saveLastDpi(this, currentDpiSetting)
        if (isInstantMode) {
            Thread {
                val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"
                shellService?.runCommand(dpiCmd)
            }.start()
        }
    }
    
    private fun changeFontSize(newSize: Float) {
        currentFontSize = newSize.coerceIn(10f, 30f)
        AppPreferences.saveFontSize(this, currentFontSize)
        updateGlobalFontSize()
        if (currentMode == MODE_SETTINGS) {
            switchMode(MODE_SETTINGS)
        }
    }
    
    private fun changeDrawerHeight(delta: Int) {
        currentDrawerHeightPercent = (currentDrawerHeightPercent + delta).coerceIn(30, 100)
        AppPreferences.setDrawerHeightPercent(this, currentDrawerHeightPercent)
        updateDrawerHeight(false)
        if (currentMode == MODE_SETTINGS) {
            drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        }
    }
    
    private fun changeDrawerWidth(delta: Int) {
        currentDrawerWidthPercent = (currentDrawerWidthPercent + delta).coerceIn(30, 100)
        AppPreferences.setDrawerWidthPercent(this, currentDrawerWidthPercent)
        updateDrawerHeight(false) // Updates both dimensions
        if (currentMode == MODE_SETTINGS) {
            drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        }
    }
    
    private fun pickIcon() {
        toggleDrawer()
        try {
            refreshDisplayId()
            val intent = Intent(this, IconPickerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val metrics = windowManager.maximumWindowMetrics
            val w = 1000
            val h = (metrics.bounds.height() * 0.7).toInt()
            val x = (metrics.bounds.width() - w) / 2
            val y = (metrics.bounds.height() - h) / 2
            val options = android.app.ActivityOptions.makeBasic()
            options.setLaunchDisplayId(currentDisplayId)
            options.setLaunchBounds(Rect(x, y, x+w, y+h))
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
            showToast("Error launching picker: ${e.message}")
        }
    }

    private fun saveProfile() {
        var name = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString()?.trim()
        if (name.isNullOrEmpty()) {
            val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            name = "Profile_$timestamp"
        }
        val pkgs = selectedAppsQueue.map { it.packageName }
        AppPreferences.saveProfile(this, name, selectedLayoutType, selectedResolutionIndex, currentDpiSetting, pkgs)
        showToast("Saved: $name")
        drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText("")
        switchMode(MODE_PROFILES)
    }

    private fun loadProfile(name: String) {
        val data = AppPreferences.getProfileData(this, name) ?: return
        try {
            val parts = data.split("|")
            selectedLayoutType = parts[0].toInt()
            selectedResolutionIndex = parts[1].toInt()
            currentDpiSetting = parts[2].toInt()
            val pkgList = parts[3].split(",")
            selectedAppsQueue.clear()
            for (pkg in pkgList) {
                if (pkg.isNotEmpty()) {
                    if (pkg == PACKAGE_BLANK) {
                         selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK))
                    } else {
                        val app = allAppsList.find { it.packageName == pkg }
                        if (app != null) selectedAppsQueue.add(app)
                    }
                }
            }
            AppPreferences.saveLastLayout(this, selectedLayoutType)
            AppPreferences.saveLastResolution(this, selectedResolutionIndex)
            AppPreferences.saveLastDpi(this, currentDpiSetting)
            activeProfileName = name
            updateSelectedAppsDock()
            showToast("Loaded: $name")
            drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
            if (isInstantMode) applyLayoutImmediate()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load profile", e)
        }
    }

    private fun executeLaunch(layoutType: Int, closeDrawer: Boolean) {
        if (closeDrawer) toggleDrawer() 
        refreshDisplayId() 
        val pkgs = selectedAppsQueue.map { it.packageName }
        AppPreferences.saveLastQueue(this, pkgs)
        val targetDim = getTargetDimensions(selectedResolutionIndex)
        val w = targetDim?.first ?: windowManager.maximumWindowMetrics.bounds.width()
        val h = targetDim?.second ?: windowManager.maximumWindowMetrics.bounds.height()
        val rects = mutableListOf<Rect>()
        
        when (layoutType) {
            LAYOUT_FULL -> {
                 rects.add(Rect(0, 0, w, h))
            }
            LAYOUT_SIDE_BY_SIDE -> { 
                rects.add(Rect(0, 0, w/2, h))
                rects.add(Rect(w/2, 0, w, h)) 
            }
            LAYOUT_TOP_BOTTOM -> { 
                rects.add(Rect(0, 0, w, h/2))
                rects.add(Rect(0, h/2, w, h))
            }
            LAYOUT_TRI_EVEN -> { 
                val third = w / 3
                rects.add(Rect(0, 0, third, h))
                rects.add(Rect(third, 0, third * 2, h))
                rects.add(Rect(third * 2, 0, w, h))
            }
            LAYOUT_CORNERS -> { 
                rects.add(Rect(0, 0, w/2, h/2))
                rects.add(Rect(w/2, 0, w, h/2))
                rects.add(Rect(0, h/2, w/2, h))
                rects.add(Rect(w/2, h/2, w, h))
            }
            LAYOUT_CUSTOM_DYNAMIC -> {
                if (activeCustomRects != null) {
                    rects.addAll(activeCustomRects!!)
                } else {
                     rects.add(Rect(0, 0, w/2, h))
                     rects.add(Rect(w/2, 0, w, h)) 
                }
            }
        }
        
        Thread {
            try {
                val resCmd = getResolutionCommand(selectedResolutionIndex)
                shellService?.runCommand(resCmd)
                if (currentDpiSetting > 0) {
                     val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"
                     shellService?.runCommand(dpiCmd)
                } else {
                     if (currentDpiSetting == -1) shellService?.runCommand("wm density reset -d $currentDisplayId")
                }
                Thread.sleep(600)
                if (selectedAppsQueue.isNotEmpty()) {
                    
                    val minimizedApps = selectedAppsQueue.filter { it.isMinimized }
                    for (app in minimizedApps) {
                        if (app.packageName != PACKAGE_BLANK) {
                            try {
                                val tid = shellService?.getTaskId(app.packageName) ?: -1
                                if (tid != -1) shellService?.moveTaskToBack(tid)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to minimize ${app.packageName}", e)
                            }
                        }
                    }

                    val activeApps = selectedAppsQueue.filter { !it.isMinimized }
                    
                    if (killAppOnExecute) {
                        for (app in activeApps) {
                            if (app.packageName != PACKAGE_BLANK) {
                                shellService?.forceStop(app.packageName)
                            }
                        }
                        Thread.sleep(400)
                    } else {
                        Thread.sleep(100)
                    }
                    
                    val count = Math.min(activeApps.size, rects.size)
                    for (i in 0 until count) {
                        val pkg = activeApps[i].packageName
                        val bounds = rects[i]
                        if (pkg == PACKAGE_BLANK) continue 
                        uiHandler.postDelayed({ launchViaApi(pkg, bounds) }, (i * 150).toLong())
                        uiHandler.postDelayed({ launchViaShell(pkg) }, (i * 150 + 50).toLong())
                        if (!killAppOnExecute) {
                            uiHandler.postDelayed({
                                Thread { 
                                    try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {}
                                }.start()
                            }, (i * 150 + 150).toLong())
                        }
                        uiHandler.postDelayed({
                            Thread { 
                                try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {}
                            }.start()
                        }, (i * 150 + 800).toLong()) 
                    }
                    
                    if (closeDrawer) {
                        uiHandler.post { selectedAppsQueue.clear(); updateSelectedAppsDock() }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Execute Failed", e)
                showToast("Execute Failed: ${e.message}")
            }
        }.start()
        drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText("")
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
            MODE_SEARCH -> {
                searchBar.hint = "Search apps..."
                refreshSearchList()
            }
            MODE_LAYOUTS -> {
                searchBar.hint = "Select Layout"
                displayList.add(ActionOption("Save Current Arrangement") { saveCurrentAsCustom() })
                displayList.add(LayoutOption("1 App - Full Screen", LAYOUT_FULL))
                displayList.add(LayoutOption("2 Apps - Side by Side", LAYOUT_SIDE_BY_SIDE))
                displayList.add(LayoutOption("2 Apps - Top & Bottom", LAYOUT_TOP_BOTTOM))
                displayList.add(LayoutOption("3 Apps - Even", LAYOUT_TRI_EVEN))
                displayList.add(LayoutOption("4 Apps - Corners", LAYOUT_CORNERS))
                
                val customNames = AppPreferences.getCustomLayoutNames(this).sorted()
                for (name in customNames) {
                    val data = AppPreferences.getCustomLayoutData(this, name)
                    if (data != null) {
                        try {
                            val rects = mutableListOf<Rect>()
                            val rectParts = data.split("|")
                            for (rp in rectParts) {
                                val coords = rp.split(",")
                                if (coords.size == 4) {
                                    rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt()))
                                }
                            }
                            displayList.add(LayoutOption(name, LAYOUT_CUSTOM_DYNAMIC, true, rects))
                        } catch(e: Exception) {}
                    }
                }
            }
            MODE_RESOLUTION -> {
                searchBar.hint = "Select Resolution"
                displayList.add(ResolutionOption("Default (Reset)", "wm size reset -d $currentDisplayId", 0))
                displayList.add(ResolutionOption("1:1 Square (1422x1500)", "wm size 1422x1500 -d $currentDisplayId", 1))
                displayList.add(ResolutionOption("16:9 Landscape (1920x1080)", "wm size 1920x1080 -d $currentDisplayId", 2))
                displayList.add(ResolutionOption("32:9 Ultrawide (3840x1080)", "wm size 3840x1080 -d $currentDisplayId", 3))
            }
            MODE_DPI -> {
                searchBar.hint = "Adjust Density (DPI)"
                displayList.add(ResolutionOption("Reset Density (Default)", "wm density reset -d $currentDisplayId", -1))
                var savedDpi = currentDpiSetting
                if (savedDpi <= 0) {
                    savedDpi = displayContext?.resources?.configuration?.densityDpi ?: 160
                }
                displayList.add(DpiOption(savedDpi))
            }
            MODE_PROFILES -> {
                searchBar.hint = "Enter Profile Name..."
                displayList.add(ProfileOption("Save Current as New", true, 0,0,0, emptyList()))
                val profileNames = AppPreferences.getProfileNames(this).sorted()
                for (pName in profileNames) {
                    val data = AppPreferences.getProfileData(this, pName)
                    if (data != null) {
                        try {
                            val parts = data.split("|")
                            val lay = parts[0].toInt()
                            val res = parts[1].toInt()
                            val d = parts[2].toInt()
                            val pkgs = parts[3].split(",").filter { it.isNotEmpty() }
                            displayList.add(ProfileOption(pName, false, lay, res, d, pkgs))
                        } catch(e: Exception) {}
                    }
                }
            }
            MODE_SETTINGS -> {
                searchBar.hint = "Settings"
                displayList.add(ActionOption("Switch Display (Current $currentDisplayId)") { cycleDisplay() })
                displayList.add(HeightOption(currentDrawerHeightPercent)) // NEW
                displayList.add(WidthOption(currentDrawerWidthPercent)) // NEW
                displayList.add(ToggleOption("Auto-Shrink for Keyboard", autoResizeEnabled) {
                    autoResizeEnabled = it
                    AppPreferences.setAutoResizeKeyboard(this, it)
                })
                displayList.add(FontSizeOption(currentFontSize))
                displayList.add(IconOption("Launcher Icon (Tap to Change)"))
                displayList.add(ToggleOption("Instant Mode (Live Changes)", isInstantMode) {
                    isInstantMode = it
                    AppPreferences.setInstantMode(this, it)
                    executeBtn.visibility = if (it) View.GONE else View.VISIBLE
                    if (it) fetchRunningApps()
                })
                displayList.add(ToggleOption("Kill App on Execute", killAppOnExecute) { 
                    killAppOnExecute = it
                    AppPreferences.setKillOnExecute(this, it)
                })
                displayList.add(ToggleOption("Display Off (Touch on)", isExtinguished) { 
                    if (it) performExtinguish() else wakeUp()
                })
            }
        }
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }

    // =========================================
    // DATA CLASSES
    // =========================================
    data class LayoutOption(val name: String, val type: Int, val isCustomSaved: Boolean = false, val customRects: List<Rect>? = null)
    data class ResolutionOption(val name: String, val command: String, val index: Int)
    data class DpiOption(val currentDpi: Int)
    data class ProfileOption(val name: String, val isCurrent: Boolean, 
                             val layout: Int, val resIndex: Int, val dpi: Int, val apps: List<String>)
    data class FontSizeOption(val currentSize: Float)
    data class HeightOption(val currentPercent: Int) // NEW
    data class WidthOption(val currentPercent: Int) // NEW
    data class IconOption(val name: String)
    data class ActionOption(val name: String, val action: () -> Unit)
    data class ToggleOption(val name: String, var isEnabled: Boolean, val onToggle: (Boolean) -> Unit)

    // =========================================
    // ADAPTERS
    // =========================================
    inner class SelectedAppsAdapter : RecyclerView.Adapter<SelectedAppsAdapter.Holder>() {
        inner class Holder(v: View) : RecyclerView.ViewHolder(v) {
            val icon: ImageView = v.findViewById(R.id.selected_app_icon)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_app, parent, false))
        }
        override fun onBindViewHolder(holder: Holder, position: Int) {
            val app = selectedAppsQueue[position]
            if (app.packageName == PACKAGE_BLANK) {
                holder.icon.setImageResource(R.drawable.ic_box_outline)
                holder.icon.alpha = 1.0f
            } else {
                try {
                    holder.icon.setImageDrawable(packageManager.getApplicationIcon(app.packageName))
                } catch (e: Exception) {
                    holder.icon.setImageResource(R.mipmap.ic_launcher_round)
                }
                holder.icon.alpha = if (app.isMinimized) 0.4f else 1.0f
            }
            
            holder.itemView.setOnClickListener {
                dismissKeyboardAndRestore()
                if (app.packageName != PACKAGE_BLANK) {
                    app.isMinimized = !app.isMinimized
                    notifyItemChanged(position)
                    if (isInstantMode) applyLayoutImmediate()
                }
            }
        }
        override fun getItemCount() = selectedAppsQueue.size
    }

    inner class RofiAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        inner class AppHolder(v: View) : RecyclerView.ViewHolder(v) {
            val icon: ImageView = v.findViewById(R.id.rofi_app_icon)
            val text: TextView = v.findViewById(R.id.rofi_app_text)
            val star: ImageView = v.findViewById(R.id.rofi_app_star)
        }
        inner class LayoutHolder(v: View) : RecyclerView.ViewHolder(v) {
            val nameInput: EditText = v.findViewById(R.id.layout_name)
            val btnSave: ImageView = v.findViewById(R.id.btn_save_profile)
            val btnExtinguish: ImageView = v.findViewById(R.id.btn_extinguish_item)
        }
        inner class DpiHolder(v: View) : RecyclerView.ViewHolder(v) {
            val btnMinus: ImageView = v.findViewById(R.id.btn_dpi_minus)
            val btnPlus: ImageView = v.findViewById(R.id.btn_dpi_plus)
            val input: EditText = v.findViewById(R.id.input_dpi_value)
        }
        inner class FontSizeHolder(v: View) : RecyclerView.ViewHolder(v) {
            val btnMinus: ImageView = v.findViewById(R.id.btn_font_minus)
            val btnPlus: ImageView = v.findViewById(R.id.btn_font_plus)
            val textVal: TextView = v.findViewById(R.id.text_font_value)
        }
        inner class HeightHolder(v: View) : RecyclerView.ViewHolder(v) { // NEW Holder
            val btnMinus: ImageView = v.findViewById(R.id.btn_height_minus)
            val btnPlus: ImageView = v.findViewById(R.id.btn_height_plus)
            val textVal: TextView = v.findViewById(R.id.text_height_value)
        }
        inner class WidthHolder(v: View) : RecyclerView.ViewHolder(v) { // NEW Holder
            val btnMinus: ImageView = v.findViewById(R.id.btn_width_minus)
            val btnPlus: ImageView = v.findViewById(R.id.btn_width_plus)
            val textVal: TextView = v.findViewById(R.id.text_width_value)
        }
        inner class ProfileRichHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name: EditText = v.findViewById(R.id.profile_name_text)
            val details: TextView = v.findViewById(R.id.profile_details_text)
            val iconsContainer: LinearLayout = v.findViewById(R.id.profile_icons_container)
            val btnSave: ImageView = v.findViewById(R.id.btn_save_profile_rich)
        }
        inner class IconSettingHolder(v: View) : RecyclerView.ViewHolder(v) {
            val preview: ImageView = v.findViewById(R.id.icon_setting_preview)
        }

        override fun getItemViewType(position: Int): Int {
            return when (displayList[position]) {
                is MainActivity.AppInfo -> 0
                is LayoutOption -> 1
                is ResolutionOption -> 1 
                is DpiOption -> 2
                is ProfileOption -> 4 
                is FontSizeOption -> 3
                is IconOption -> 5 
                is ToggleOption -> 1
                is ActionOption -> 6 
                is HeightOption -> 7 // NEW Type
                is WidthOption -> 8 // NEW Type
                else -> 0
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                0 -> AppHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_app_rofi, parent, false))
                1 -> LayoutHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false))
                2 -> DpiHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dpi_custom, parent, false))
                3 -> FontSizeHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_font_size, parent, false))
                4 -> ProfileRichHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_rich, parent, false))
                5 -> IconSettingHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_icon_setting, parent, false))
                6 -> LayoutHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false))
                7 -> HeightHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_height_setting, parent, false)) // NEW
                8 -> WidthHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_width_setting, parent, false)) // NEW
                else -> AppHolder(View(parent.context))
            }
        }

        private fun startRename(editText: EditText) {
            editText.isEnabled = true
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }

        private fun endRename(editText: EditText) {
            editText.isFocusable = false
            editText.isFocusableInTouchMode = false
            editText.isEnabled = false 
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = displayList[position]
            
            if (holder is AppHolder) holder.text.textSize = currentFontSize
            if (holder is LayoutHolder) holder.nameInput.textSize = currentFontSize
            if (holder is ProfileRichHolder) holder.name.textSize = currentFontSize

            if (holder is AppHolder && item is MainActivity.AppInfo) {
                holder.text.text = item.label
                if (item.packageName == PACKAGE_BLANK) {
                    holder.icon.setImageResource(R.drawable.ic_box_outline)
                } else {
                    try {
                        holder.icon.setImageDrawable(packageManager.getApplicationIcon(item.packageName))
                    } catch (e: Exception) {
                        holder.icon.setImageResource(R.mipmap.ic_launcher_round)
                    }
                }
                val isSelected = selectedAppsQueue.any { it.packageName == item.packageName }
                if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
                else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                holder.star.visibility = if (item.isFavorite) View.VISIBLE else View.GONE
                holder.itemView.setOnClickListener { addToSelection(item) }
                holder.itemView.setOnLongClickListener { toggleFavorite(item); refreshSearchList(); true }
                
            } else if (holder is ProfileRichHolder && item is ProfileOption) {
                holder.name.setText(item.name)
                holder.iconsContainer.removeAllViews()
                if (!item.isCurrent) {
                    for (pkg in item.apps.take(5)) { 
                        val iv = ImageView(holder.itemView.context)
                        val lp = LinearLayout.LayoutParams(60, 60)
                        lp.marginEnd = 8
                        iv.layoutParams = lp
                        if (pkg == PACKAGE_BLANK) {
                            iv.setImageResource(R.drawable.ic_box_outline)
                        } else {
                            try {
                                iv.setImageDrawable(packageManager.getApplicationIcon(pkg))
                            } catch (e: Exception) {
                                iv.setImageResource(R.mipmap.ic_launcher_round)
                            }
                        }
                        holder.iconsContainer.addView(iv)
                    }
                    val info = "${getLayoutName(item.layout)} | ${getRatioName(item.resIndex)} | ${item.dpi}dpi"
                    holder.details.text = info
                    holder.details.visibility = View.VISIBLE
                    holder.btnSave.visibility = View.GONE
                    
                    if (activeProfileName == item.name) {
                        holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
                    } else {
                        holder.itemView.setBackgroundResource(0)
                    }
                    
                    holder.itemView.setOnClickListener { 
                        dismissKeyboardAndRestore() // FIX for profile load
                        loadProfile(item.name) 
                    }
                    holder.itemView.setOnLongClickListener { startRename(holder.name); true }
                    
                    val saveProfileName = {
                        val newName = holder.name.text.toString().trim()
                        if (newName.isNotEmpty() && newName != item.name) {
                            if (AppPreferences.renameProfile(holder.itemView.context, item.name, newName)) {
                                showToast("Renamed to $newName")
                                switchMode(MODE_PROFILES)
                            }
                        }
                        endRename(holder.name)
                    }

                    holder.name.setOnEditorActionListener { v, actionId, _ ->
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            saveProfileName()
                            holder.name.clearFocus()
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(holder.name.windowToken, 0)
                            updateDrawerHeight(false)
                            true
                        } else false
                    }
                    holder.name.setOnFocusChangeListener { v, hasFocus ->
                        if (autoResizeEnabled) updateDrawerHeight(hasFocus)
                        if (!hasFocus) saveProfileName()
                    }

                } else {
                    holder.iconsContainer.removeAllViews()
                    holder.details.visibility = View.GONE
                    holder.btnSave.visibility = View.VISIBLE
                    holder.itemView.setBackgroundResource(0)
                    
                    holder.name.isEnabled = true
                    holder.name.isFocusable = true
                    holder.name.isFocusableInTouchMode = true
                    
                    holder.itemView.setOnClickListener { saveProfile() }
                    holder.btnSave.setOnClickListener { saveProfile() }
                }

            } else if (holder is LayoutHolder) {
                holder.btnSave.visibility = View.GONE 
                holder.btnExtinguish.visibility = View.GONE

                if (item is LayoutOption) {
                    holder.nameInput.setText(item.name)
                    
                    val isSelected = if (item.type == LAYOUT_CUSTOM_DYNAMIC) {
                        item.type == selectedLayoutType && item.name == activeCustomLayoutName
                    } else {
                        item.type == selectedLayoutType && activeCustomLayoutName == null
                    }
                    
                    if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
                    else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                    
                    holder.itemView.setOnClickListener { selectLayout(item) }
                    
                    if (item.isCustomSaved) {
                        holder.itemView.setOnLongClickListener {
                            startRename(holder.nameInput)
                            true
                        }
                        
                        val saveLayoutName = {
                            val newName = holder.nameInput.text.toString().trim()
                            if (newName.isNotEmpty() && newName != item.name) {
                                if (AppPreferences.renameCustomLayout(holder.itemView.context, item.name, newName)) {
                                    showToast("Renamed to $newName")
                                    if (activeCustomLayoutName == item.name) {
                                        activeCustomLayoutName = newName
                                        AppPreferences.saveLastCustomLayoutName(holder.itemView.context, newName)
                                    }
                                    switchMode(MODE_LAYOUTS)
                                }
                            }
                            endRename(holder.nameInput)
                        }
                        
                        holder.nameInput.setOnEditorActionListener { v, actionId, _ ->
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                saveLayoutName()
                                true
                            } else false
                        }
                        holder.nameInput.setOnFocusChangeListener { v, hasFocus ->
                            if (!hasFocus) saveLayoutName()
                        }
                    } else {
                        holder.nameInput.isEnabled = false
                        holder.nameInput.isFocusable = false
                        holder.nameInput.setTextColor(Color.WHITE)
                    }
                    
                } else if (item is ResolutionOption) {
                    holder.nameInput.setText(item.name)
                    holder.nameInput.isEnabled = false
                    holder.nameInput.setTextColor(Color.WHITE)
                    val isSelected = (item.index == selectedResolutionIndex)
                    if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
                    else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                    holder.itemView.setOnClickListener { applyResolution(item) }
                    
                } else if (item is IconOption) {
                    holder.nameInput.setText(item.name)
                    holder.nameInput.isEnabled = false
                    holder.nameInput.setTextColor(Color.WHITE)
                    holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                    holder.itemView.setOnClickListener { pickIcon() }
                } else if (item is ToggleOption) {
                    holder.nameInput.setText(item.name)
                    holder.nameInput.isEnabled = false
                    holder.nameInput.setTextColor(Color.WHITE)
                    if (item.isEnabled) holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
                    else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                    holder.itemView.setOnClickListener {
                        dismissKeyboardAndRestore() // FIX: Toggle Click
                        item.isEnabled = !item.isEnabled
                        item.onToggle(item.isEnabled)
                        notifyItemChanged(position)
                    }
                } else if (item is ActionOption) {
                    holder.nameInput.setText(item.name)
                    holder.nameInput.isEnabled = false
                    holder.nameInput.setTextColor(Color.WHITE)
                    holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                    holder.itemView.setOnClickListener { 
                        dismissKeyboardAndRestore() // FIX: Action Click
                        item.action() 
                    }
                }
                
            } else if (holder is IconSettingHolder && item is IconOption) {
                try {
                    val uriStr = AppPreferences.getIconUri(holder.itemView.context)
                    if (uriStr != null) {
                        val uri = Uri.parse(uriStr)
                        val input = contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(input)
                        input?.close()
                        holder.preview.setImageBitmap(bitmap)
                    } else {
                        holder.preview.setImageResource(R.mipmap.ic_launcher_round)
                    }
                } catch(e: Exception) {
                    holder.preview.setImageResource(R.mipmap.ic_launcher_round)
                }
                holder.itemView.setOnClickListener { pickIcon() }

            } else if (holder is DpiHolder && item is DpiOption) {
                holder.input.setText(item.currentDpi.toString())
                holder.btnMinus.setOnClickListener {
                    val v = holder.input.text.toString().toIntOrNull() ?: 160
                    val newVal = (v - 5).coerceAtLeast(100)
                    holder.input.setText(newVal.toString())
                    selectDpi(newVal)
                }
                holder.btnPlus.setOnClickListener {
                    val v = holder.input.text.toString().toIntOrNull() ?: 160
                    val newVal = (v + 5).coerceAtMost(400)
                    holder.input.setText(newVal.toString())
                    selectDpi(newVal)
                }
            } else if (holder is FontSizeHolder && item is FontSizeOption) {
                holder.textVal.text = item.currentSize.toInt().toString()
                holder.btnMinus.setOnClickListener { changeFontSize(item.currentSize - 1) }
                holder.btnPlus.setOnClickListener { changeFontSize(item.currentSize + 1) }
            } else if (holder is HeightHolder && item is HeightOption) { // NEW
                holder.textVal.text = item.currentPercent.toString()
                holder.btnMinus.setOnClickListener { changeDrawerHeight(-5) }
                holder.btnPlus.setOnClickListener { changeDrawerHeight(5) }
            } else if (holder is WidthHolder && item is WidthOption) { // NEW Width
                holder.textVal.text = item.currentPercent.toString()
                holder.btnMinus.setOnClickListener { changeDrawerWidth(-5) }
                holder.btnPlus.setOnClickListener { changeDrawerWidth(5) }
            }
        }
        override fun getItemCount() = displayList.size
    }
}
```
