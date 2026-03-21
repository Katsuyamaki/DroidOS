* Launcher: FloatingLauncherService.kt: Bump bubble z-order above keyboard by nudging it 1px when IME becomes visible

* Launcher: FloatingLauncherService.kt: Replace store-specific upgrade/auth actions with one generic community upgrade link in launcher keybind menu
* Trackpad: TrackpadMenuManager.kt: Add a single upgrade entry in keyboard menu that links users to the community GitHub page

* (New) DroidOSConstants.kt: Centralized package names and action strings for easy future migration (mirrors DroidOSPro pattern)
* (New) DroidOSConstants.kt: Mirror constants file in keyboard app for cross-app communication (mirrors DroidOSPro pattern)
* (New) ShizukuBinder.kt: Convert ShizukuBinder from Java to Kotlin to avoid directory-package mismatch (mirrors DroidOSPro pattern)
* (New) ShizukuBinder.kt: Convert ShizukuBinder from Java to Kotlin to avoid directory-package mismatch (mirrors DroidOSPro pattern)
* build.gradle.kts: Update namespace and applicationId to DroidOSFOSSLauncher
* build.gradle.kts: Update namespace and applicationId to DroidOSFOSSKeyboardTrackpad
* proguard-rules.pro: Update proguard keep rules to new FOSS package name
* IShellService.aidl: Update AIDL package declaration to new FOSS launcher package
* IShellService.aidl: Update AIDL package declaration to new FOSS keyboard trackpad package
* AndroidManifest.xml: Add permission declaration, cross-app permission usage, and queries entry for FOSS keyboard trackpad package
* AndroidManifest.xml: Add queries entry for new FOSS keyboard trackpad package (Android 11+ visibility)
* AndroidManifest.xml: Add permission declaration and cross-app permission/queries for FOSS launcher; update all action strings from old package names to FOSS names
* AndroidManifest.xml: Update InterAppCommandReceiver action strings and ADB comment from old package names to FOSS names; add permission guard
* AppPreferences.kt: Update package declaration to FOSS launcher package
* IconPickerActivity.kt: Update package declaration and broadcast action to FOSS launcher package
* IconPickerActivity.kt: Update broadcast action string to FOSS launcher package
* MainActivity.kt: Update package declaration and constant to FOSS launcher package
* MainActivity.kt: Update SELECTED_APP_PACKAGE constant to FOSS launcher package
* MenuActivity.kt: Update package declaration to FOSS launcher package
* PermissionActivity.kt: Update package declaration to FOSS launcher package
* QuadrantActivity.kt: Update package declaration to FOSS launcher package
* ShizukuHelper.kt: Update package declaration to FOSS launcher package
* SplitActivity.kt: Update package declaration to FOSS launcher package
* TriSplitActivity.kt: Update package declaration to FOSS launcher package
* FloatingLauncherService.kt: Update package declaration to FOSS launcher package
* FloatingLauncherService.kt: Update all cross-app references from old coverscreentester package to FOSS keyboard trackpad package
* FloatingLauncherService.kt: Update all action strings from old DroidOSTrackpadKeyboard to FOSS keyboard trackpad package
* FloatingLauncherService.kt: Update all launcher action strings from old DroidOSLauncher to FOSS launcher package
* ShellUserService.kt: Update package declaration to FOSS launcher package
* ShellUserService.kt: Update coverscreentester cross-app reference to FOSS keyboard trackpad package
* ShellUserService.kt: Update DroidOSLauncher self-reference to FOSS launcher package
* InterAppCommandReceiver.kt: Update package declaration and all action string constants from old coverscreentester package to FOSS keyboard trackpad package
* KeyboardActivity.kt: Update package declaration to FOSS keyboard trackpad package
* KeyboardManager.kt: Update package declaration to FOSS keyboard trackpad package
* KeyboardOverlay.kt: Update package declaration to FOSS keyboard trackpad package
* KeyboardOverlay.kt: Update cross-app launcher references to FOSS launcher package
* KeyboardPickerActivity.kt: Update package declaration to FOSS keyboard trackpad package
* KeyboardUtils.kt: Update package declaration to FOSS keyboard trackpad package
* KeyboardView.kt: Update package declaration to FOSS keyboard trackpad package
* KeyboardView.kt: Update cross-app launcher references to FOSS launcher package
* MainActivity.kt: Update package declaration to FOSS keyboard trackpad package
* ManualAdjustActivity.kt: Update package declaration to FOSS keyboard trackpad package
* NullInputMethodService.kt: Update package declaration and all inject action strings from old coverscreentester to FOSS keyboard trackpad package
* OverlayService.kt: Update package declaration and all self-references from old coverscreentester to FOSS keyboard trackpad package
* OverlayService.kt: Update cross-app launcher references to FOSS launcher package
* OverlayService.kt: Update old DroidOSTrackpadKeyboard references to FOSS keyboard trackpad package
* PredictionEngine.kt: Update package declaration to FOSS keyboard trackpad package
* ProfilesActivity.kt: Update package declaration to FOSS keyboard trackpad package
* SettingsActivity.kt: Update package declaration to FOSS keyboard trackpad package
* ShellUserService.kt: Update package declaration and IShellService import from old coverscreentester to FOSS keyboard trackpad package
* ShizukuInputHandler.kt: Update package declaration to FOSS keyboard trackpad package
* SwipeTrailView.kt: Update package declaration to FOSS keyboard trackpad package
* TrackpadMenuAdapter.kt: Update package declaration to FOSS keyboard trackpad package
* TrackpadMenuManager.kt: Update package declaration to FOSS keyboard trackpad package
* TrackpadPrefs.kt: Update package declaration to FOSS keyboard trackpad package
* TrackpadService.kt: Update package declaration to FOSS keyboard trackpad package
* FloatingLauncherService.kt: Remove legacy fallback block that became redundant after package rename (duplicates Step 1)
* OverlayService.kt: Deduplicate prefix list in commandFilter registration (both became identical after rename)
* OverlayService.kt: Deduplicate prefix list in accessibility event filter registration (both became identical after rename)
* OverlayService.kt: Update dual-prefix comment to reflect single FOSS prefix
