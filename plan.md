FILE_UPDATE: Cover-Screen-Launcher/app/build.gradle.kts
REASON: Update namespace and applicationId to DroidOSFOSSLauncher
SEARCH_BLOCK:
```
    namespace = "com.example.quadrantlauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSLauncher"
```
REPLACE_BLOCK:
```
    namespace = "com.katsuyamaki.DroidOSFOSSLauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSFOSSLauncher"
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/build.gradle.kts
REASON: Update namespace and applicationId to DroidOSFOSSKeyboardTrackpad
SEARCH_BLOCK:
```
    namespace = "com.example.coverscreentester"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSTrackpadKeyboard"
```
REPLACE_BLOCK:
```
    namespace = "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad"
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/proguard-rules.pro
REASON: Update proguard keep rules to new FOSS package name
SEARCH_BLOCK:
```
-keep class com.example.quadrantlauncher.ShellUserService { *; }
-keep class com.example.quadrantlauncher.IShellService { *; }
-keep interface com.example.quadrantlauncher.IShellService { *; }
```
REPLACE_BLOCK:
```
-keep class com.katsuyamaki.DroidOSFOSSLauncher.ShellUserService { *; }
-keep class com.katsuyamaki.DroidOSFOSSLauncher.IShellService { *; }
-keep interface com.katsuyamaki.DroidOSFOSSLauncher.IShellService { *; }
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/aidl/com/example/quadrantlauncher/IShellService.aidl
REASON: Update AIDL package declaration to new FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher;
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher;
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/aidl/com/example/coverscreentester/IShellService.aidl
REASON: Update AIDL package declaration to new FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester;
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad;
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/AndroidManifest.xml
REASON: Add permission declaration, cross-app permission usage, and queries entry for FOSS keyboard trackpad package
SEARCH_BLOCK:
```
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```
REPLACE_BLOCK:
```
    <permission
        android:name="com.katsuyamaki.DroidOSFOSSLauncher.permission.INTERNAL_COMMAND"
        android:protectionLevel="signature" />

    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/AndroidManifest.xml
REASON: Add queries entry for new FOSS keyboard trackpad package (Android 11+ visibility)
SEARCH_BLOCK:
```
    <queries>
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
    </queries>
```
REPLACE_BLOCK:
```
    <queries>
        <package android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad" />
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
    </queries>
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/AndroidManifest.xml
REASON: Add permission declaration and cross-app permission/queries for FOSS launcher; update all action strings from old package names to FOSS names
SEARCH_BLOCK:
```
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <queries>
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
```
REPLACE_BLOCK:
```
    <permission
        android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.permission.INTERNAL_COMMAND"
        android:protectionLevel="signature" />

    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="com.katsuyamaki.DroidOSFOSSLauncher.permission.INTERNAL_COMMAND" />

    <queries>
        <package android:name="com.katsuyamaki.DroidOSFOSSLauncher" />
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/AndroidManifest.xml
REASON: Update InterAppCommandReceiver action strings and ADB comment from old package names to FOSS names; add permission guard
SEARCH_BLOCK:
```
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
```
REPLACE_BLOCK:
```
        <!-- =================================================================================
             INTER-APP COMMAND RECEIVER
             SUMMARY: Static receiver to handle commands from DroidOS Launcher and ADB.
                      Allows soft restart, z-order fixes, and virtual display coordination
                      without requiring the Activity to be in foreground.
             USAGE (ADB): adb shell am broadcast -a com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.SOFT_RESTART
             ================================================================================= -->
                <receiver
                    android:name=".InterAppCommandReceiver"
                    android:enabled="true"
                    android:exported="true"
                    android:permission="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.permission.INTERNAL_COMMAND">
                    <intent-filter>
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.SOFT_RESTART" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.MOVE_TO_VIRTUAL" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.RETURN_TO_PHYSICAL" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.ENFORCE_ZORDER" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.TOGGLE_VIRTUAL_MIRROR" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.OPEN_DRAWER" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.GET_STATUS" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.STOP_SERVICE" />
                    </intent-filter>
                </receiver>
        <!-- END BLOCK: INTER-APP COMMAND RECEIVER -->
```
END_OF_UPDATE_BLOCK


FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/AppPreferences.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/IconPickerActivity.kt
REASON: Update package declaration and broadcast action to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/IconPickerActivity.kt
REASON: Update broadcast action string to FOSS launcher package
SEARCH_BLOCK:
```
                sendBroadcast(Intent("com.example.quadrantlauncher.UPDATE_ICON"))
```
REPLACE_BLOCK:
```
                sendBroadcast(Intent("com.katsuyamaki.DroidOSFOSSLauncher.UPDATE_ICON"))
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/MainActivity.kt
REASON: Update package declaration and constant to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/MainActivity.kt
REASON: Update SELECTED_APP_PACKAGE constant to FOSS launcher package
SEARCH_BLOCK:
```
        const val SELECTED_APP_PACKAGE = "com.example.quadrantlauncher.SELECTED_APP_PACKAGE"
```
REPLACE_BLOCK:
```
        const val SELECTED_APP_PACKAGE = "com.katsuyamaki.DroidOSFOSSLauncher.SELECTED_APP_PACKAGE"
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/MenuActivity.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/PermissionActivity.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/QuadrantActivity.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShizukuHelper.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/SplitActivity.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/TriSplitActivity.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK


FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
REASON: Update all cross-app references from old coverscreentester package to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
REASON: Update all action strings from old DroidOSTrackpadKeyboard to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSTrackpadKeyboard
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
REASON: Update all launcher action strings from old DroidOSLauncher to FOSS launcher package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSLauncher
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShellUserService.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShellUserService.kt
REASON: Update coverscreentester cross-app reference to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShellUserService.kt
REASON: Update DroidOSLauncher self-reference to FOSS launcher package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSLauncher
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK


FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/InterAppCommandReceiver.kt
REASON: Update package declaration and all action string constants from old coverscreentester package to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardManager.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Update cross-app launcher references to FOSS launcher package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSLauncher
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardPickerActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardUtils.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardView.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardView.kt
REASON: Update cross-app launcher references to FOSS launcher package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSLauncher
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/MainActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ManualAdjustActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/NullInputMethodService.kt
REASON: Update package declaration and all inject action strings from old coverscreentester to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Update package declaration and all self-references from old coverscreentester to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Update cross-app launcher references to FOSS launcher package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSLauncher
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Update old DroidOSTrackpadKeyboard references to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSTrackpadKeyboard
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/PredictionEngine.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ProfilesActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/SettingsActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ShellUserService.kt
REASON: Update package declaration and IShellService import from old coverscreentester to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ShizukuInputHandler.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/SwipeTrailView.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadMenuAdapter.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadMenuManager.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadPrefs.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadService.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK


FILE_CREATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/DroidOSConstants.kt
REASON: Centralized package names and action strings for easy future migration (mirrors DroidOSPro pattern)
CONTENT_BLOCK:
```kotlin
package com.katsuyamaki.DroidOSFOSSLauncher

/**
 * Centralized constants for DroidOS FOSS inter-app communication.
 * All package names and broadcast action strings live here so a future
 * package rename only requires changing this file + manifests + gradle.
 */
object DroidOSConstants {

    // ── Package identifiers ──
    const val LAUNCHER_PACKAGE = "com.katsuyamaki.DroidOSFOSSLauncher"
    const val KEYBOARD_PACKAGE = "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad"

    // ── Launcher action strings ──
    const val ACTION_TOGGLE_VIRTUAL_DISPLAY = "$LAUNCHER_PACKAGE.TOGGLE_VIRTUAL_DISPLAY"
    const val ACTION_OPEN_DRAWER           = "$LAUNCHER_PACKAGE.OPEN_DRAWER"
    const val ACTION_UPDATE_ICON           = "$LAUNCHER_PACKAGE.UPDATE_ICON"
    const val ACTION_CYCLE_DISPLAY         = "$LAUNCHER_PACKAGE.CYCLE_DISPLAY"
    const val ACTION_WINDOW_MANAGER        = "$LAUNCHER_PACKAGE.WINDOW_MANAGER"
    const val ACTION_REQUEST_CUSTOM_MOD_SYNC = "$LAUNCHER_PACKAGE.REQUEST_CUSTOM_MOD_SYNC"
    const val ACTION_REMOTE_KEY            = "$LAUNCHER_PACKAGE.REMOTE_KEY"
    const val ACTION_REQUEST_KEYBINDS      = "$LAUNCHER_PACKAGE.REQUEST_KEYBINDS"
    const val ACTION_SYNC_KEYBOARD_RATIO   = "$LAUNCHER_PACKAGE.SYNC_KEYBOARD_RATIO"

    // ── Launcher permission ──
    const val PERMISSION_LAUNCHER_INTERNAL = "$LAUNCHER_PACKAGE.permission.INTERNAL_COMMAND"

    // ── Keyboard action strings ──
    const val KB_ACTION_INJECT_TEXT        = "$KEYBOARD_PACKAGE.INJECT_TEXT"
    const val KB_ACTION_INJECT_KEY         = "$KEYBOARD_PACKAGE.INJECT_KEY"
    const val KB_ACTION_INJECT_DELETE      = "$KEYBOARD_PACKAGE.INJECT_DELETE"
    const val KB_ACTION_SOFT_RESTART       = "$KEYBOARD_PACKAGE.SOFT_RESTART"
    const val KB_ACTION_MOVE_TO_VIRTUAL    = "$KEYBOARD_PACKAGE.MOVE_TO_VIRTUAL"
    const val KB_ACTION_RETURN_TO_PHYSICAL = "$KEYBOARD_PACKAGE.RETURN_TO_PHYSICAL"
    const val KB_ACTION_ENFORCE_ZORDER     = "$KEYBOARD_PACKAGE.ENFORCE_ZORDER"
    const val KB_ACTION_TOGGLE_VIRTUAL_MIRROR = "$KEYBOARD_PACKAGE.TOGGLE_VIRTUAL_MIRROR"
    const val KB_ACTION_OPEN_DRAWER        = "$KEYBOARD_PACKAGE.OPEN_DRAWER"
    const val KB_ACTION_GET_STATUS         = "$KEYBOARD_PACKAGE.GET_STATUS"
    const val KB_ACTION_STOP_SERVICE       = "$KEYBOARD_PACKAGE.STOP_SERVICE"
    const val KB_ACTION_MOVE_TO_DISPLAY    = "$KEYBOARD_PACKAGE.MOVE_TO_DISPLAY"
    const val KB_ACTION_SET_INPUT_CAPTURE  = "$KEYBOARD_PACKAGE.SET_INPUT_CAPTURE"
    const val KB_ACTION_SET_NUM_LAYER      = "$KEYBOARD_PACKAGE.SET_NUM_LAYER"
    const val KB_ACTION_SET_CUSTOM_MOD     = "$KEYBOARD_PACKAGE.SET_CUSTOM_MOD"
    const val KB_ACTION_SET_TRACKPAD_VISIBILITY = "$KEYBOARD_PACKAGE.SET_TRACKPAD_VISIBILITY"
    const val KB_ACTION_UPDATE_KEYBINDS    = "$KEYBOARD_PACKAGE.UPDATE_KEYBINDS"

    // ── Keyboard permission ──
    const val PERMISSION_KEYBOARD_INTERNAL = "$KEYBOARD_PACKAGE.permission.INTERNAL_COMMAND"

    // ── IME component IDs ──
    const val NULL_IME_ID = "$KEYBOARD_PACKAGE.NullInputMethodService"
    const val OVERLAY_SERVICE_CLASS = "$KEYBOARD_PACKAGE.OverlayService"
}
```
END_OF_UPDATE_BLOCK

FILE_CREATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/DroidOSConstants.kt
REASON: Mirror constants file in keyboard app for cross-app communication (mirrors DroidOSPro pattern)
CONTENT_BLOCK:
```kotlin
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad

/**
 * Centralized constants for DroidOS FOSS inter-app communication.
 * All package names and broadcast action strings live here so a future
 * package rename only requires changing this file + manifests + gradle.
 */
object DroidOSConstants {

    // ── Package identifiers ──
    const val LAUNCHER_PACKAGE = "com.katsuyamaki.DroidOSFOSSLauncher"
    const val KEYBOARD_PACKAGE = "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad"

    // ── Launcher action strings ──
    const val ACTION_TOGGLE_VIRTUAL_DISPLAY = "$LAUNCHER_PACKAGE.TOGGLE_VIRTUAL_DISPLAY"
    const val ACTION_OPEN_DRAWER           = "$LAUNCHER_PACKAGE.OPEN_DRAWER"
    const val ACTION_UPDATE_ICON           = "$LAUNCHER_PACKAGE.UPDATE_ICON"
    const val ACTION_CYCLE_DISPLAY         = "$LAUNCHER_PACKAGE.CYCLE_DISPLAY"
    const val ACTION_WINDOW_MANAGER        = "$LAUNCHER_PACKAGE.WINDOW_MANAGER"
    const val ACTION_REQUEST_CUSTOM_MOD_SYNC = "$LAUNCHER_PACKAGE.REQUEST_CUSTOM_MOD_SYNC"
    const val ACTION_REMOTE_KEY            = "$LAUNCHER_PACKAGE.REMOTE_KEY"
    const val ACTION_REQUEST_KEYBINDS      = "$LAUNCHER_PACKAGE.REQUEST_KEYBINDS"
    const val ACTION_SYNC_KEYBOARD_RATIO   = "$LAUNCHER_PACKAGE.SYNC_KEYBOARD_RATIO"

    // ── Launcher permission ──
    const val PERMISSION_LAUNCHER_INTERNAL = "$LAUNCHER_PACKAGE.permission.INTERNAL_COMMAND"

    // ── Keyboard action strings ──
    const val KB_ACTION_INJECT_TEXT        = "$KEYBOARD_PACKAGE.INJECT_TEXT"
    const val KB_ACTION_INJECT_KEY         = "$KEYBOARD_PACKAGE.INJECT_KEY"
    const val KB_ACTION_INJECT_DELETE      = "$KEYBOARD_PACKAGE.INJECT_DELETE"
    const val KB_ACTION_SOFT_RESTART       = "$KEYBOARD_PACKAGE.SOFT_RESTART"
    const val KB_ACTION_MOVE_TO_VIRTUAL    = "$KEYBOARD_PACKAGE.MOVE_TO_VIRTUAL"
    const val KB_ACTION_RETURN_TO_PHYSICAL = "$KEYBOARD_PACKAGE.RETURN_TO_PHYSICAL"
    const val KB_ACTION_ENFORCE_ZORDER     = "$KEYBOARD_PACKAGE.ENFORCE_ZORDER"
    const val KB_ACTION_TOGGLE_VIRTUAL_MIRROR = "$KEYBOARD_PACKAGE.TOGGLE_VIRTUAL_MIRROR"
    const val KB_ACTION_OPEN_DRAWER        = "$KEYBOARD_PACKAGE.OPEN_DRAWER"
    const val KB_ACTION_GET_STATUS         = "$KEYBOARD_PACKAGE.GET_STATUS"
    const val KB_ACTION_STOP_SERVICE       = "$KEYBOARD_PACKAGE.STOP_SERVICE"
    const val KB_ACTION_MOVE_TO_DISPLAY    = "$KEYBOARD_PACKAGE.MOVE_TO_DISPLAY"
    const val KB_ACTION_SET_INPUT_CAPTURE  = "$KEYBOARD_PACKAGE.SET_INPUT_CAPTURE"
    const val KB_ACTION_SET_NUM_LAYER      = "$KEYBOARD_PACKAGE.SET_NUM_LAYER"
    const val KB_ACTION_SET_CUSTOM_MOD     = "$KEYBOARD_PACKAGE.SET_CUSTOM_MOD"
    const val KB_ACTION_SET_TRACKPAD_VISIBILITY = "$KEYBOARD_PACKAGE.SET_TRACKPAD_VISIBILITY"
    const val KB_ACTION_UPDATE_KEYBINDS    = "$KEYBOARD_PACKAGE.UPDATE_KEYBINDS"

    // ── Keyboard permission ──
    const val PERMISSION_KEYBOARD_INTERNAL = "$KEYBOARD_PACKAGE.permission.INTERNAL_COMMAND"

    // ── IME component IDs ──
    const val NULL_IME_ID = "$KEYBOARD_PACKAGE.NullInputMethodService"
    const val OVERLAY_SERVICE_CLASS = "$KEYBOARD_PACKAGE.OverlayService"
}
```
END_OF_UPDATE_BLOCK

FILE_CREATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShizukuBinder.kt
REASON: Convert ShizukuBinder from Java to Kotlin to avoid directory-package mismatch (mirrors DroidOSPro pattern)
CONTENT_BLOCK:
```kotlin
package com.katsuyamaki.DroidOSFOSSLauncher

import android.content.ComponentName
import android.content.ServiceConnection
import rikka.shizuku.Shizuku

object ShizukuBinder {

    fun bind(component: ComponentName, connection: ServiceConnection, debug: Boolean, version: Int) {
        val args = Shizuku.UserServiceArgs(component)
            .processNameSuffix("shell")
            .daemon(false)
            .debuggable(debug)
            .version(version)
        Shizuku.bindUserService(args, connection)
    }

    fun unbind(component: ComponentName, connection: ServiceConnection) {
        val args = Shizuku.UserServiceArgs(component)
            .processNameSuffix("shell")
            .daemon(false)
        Shizuku.unbindUserService(args, connection, true)
    }
}
```
END_OF_UPDATE_BLOCK

FILE_CREATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ShizukuBinder.kt
REASON: Convert ShizukuBinder from Java to Kotlin to avoid directory-package mismatch (mirrors DroidOSPro pattern)
CONTENT_BLOCK:
```kotlin
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad

import android.content.ComponentName
import android.content.ServiceConnection
import rikka.shizuku.Shizuku

object ShizukuBinder {

    fun bind(component: ComponentName, connection: ServiceConnection, debug: Boolean, version: Int) {
        val args = Shizuku.UserServiceArgs(component)
            .processNameSuffix("shell")
            .daemon(false)
            .debuggable(debug)
            .version(version)
        Shizuku.bindUserService(args, connection)
    }

    fun unbind(component: ComponentName, connection: ServiceConnection) {
        val args = Shizuku.UserServiceArgs(component)
            .processNameSuffix("shell")
            .daemon(false)
        Shizuku.unbindUserService(args, connection, true)
    }
}
```
END_OF_UPDATE_BLOCK


FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
REASON: Remove legacy fallback block that became redundant after package rename (duplicates Step 1)
SEARCH_BLOCK:
```
        // 3. Legacy Fallback (Old Package Name)
        try {
            val oldIntent = Intent()
            oldIntent.component = ComponentName(
                "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad", 
                "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.OverlayService"
            )
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(oldIntent) else startService(oldIntent)
            return
        } catch (e: Exception) {
            // Legacy Service failed -> Try Legacy Activity
            try {
                val oldLaunchIntent = packageManager.getLaunchIntentForPackage("com.katsuyamaki.DroidOSFOSSKeyboardTrackpad")
                if (oldLaunchIntent != null) {
                    oldLaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(oldLaunchIntent)
                    return
                }
            } catch (e2: Exception) {}
        }

        // 4. Final Resort: Broadcast (Only works if app is already alive)
```
REPLACE_BLOCK:
```
        // 3. Final Resort: Broadcast (Only works if app is already alive)
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Deduplicate prefix list in commandFilter registration (both became identical after rename)
SEARCH_BLOCK:
```
            val prefixes = listOf("com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.", "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.")
```
REPLACE_BLOCK:
```
            val prefixes = listOf("com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.")
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Deduplicate prefix list in accessibility event filter registration (both became identical after rename)
SEARCH_BLOCK:
```
                        val prefixes = listOf(
                            "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.",
                            "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad."
                        )
```
REPLACE_BLOCK:
```
                        val prefixes = listOf(
                            "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad."
                        )
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Update dual-prefix comment to reflect single FOSS prefix
SEARCH_BLOCK:
```
        // Register BOTH new and old prefixes to support all scripts/buttons
```
REPLACE_BLOCK:
```
        // Register FOSS package prefix for all command actions
```
END_OF_UPDATE_BLOCK

