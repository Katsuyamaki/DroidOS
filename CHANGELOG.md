# DroidOS v1.1 - Compatibility & Customization Update

## 🚀 Key Highlights

### 📱 Launcher: Adaptive App Queue Reordering (Android 14 Fix)
We have introduced a new default method for reordering apps in the launcher dock to ensure compatibility with Android 14 devices where standard Drag-and-Drop gestures often conflict with system navigation or behave inconsistently.

* **Long-Tap to Swap (Default):**
    * **How it works:** Long-press an icon in your App Queue (Dock). You will see a highlight and a prompt: *"Tap another app to Swap"*. Simply tap the target position, and the apps will swap places instantly.
    * **Why:** This bypasses the complex drag-shadow APIs that cause crashes or UI freezes on certain Samsung OneUI 6.0 / Android 14 builds.

* **Drag-and-Drop (Toggle):**
    * For users on Android 15/16 or devices with robust drag support, the traditional "Hold and Drag" method remains available.
    * You can enable this in **Settings > Reorder: Drag & Drop**.

### ⌨️ Trackpad: Keyboard Resizing
The virtual keyboard overlay is no longer one-size-fits-all.
* **Dynamic Scaling:** Added a slider in Settings to scale the keyboard key size from **50% to 200%**.
* **Auto-Shrink:** The Launcher drawer now intelligently detects when the keyboard is active and shrinks itself to prevent visual overlap, ensuring you can see what you are typing.

### 🛠️ Optimizations & Bug Fixes
* **Cursor "Stuck" Fix:** Resolved a critical issue where the mouse cursor would freeze or fight against user input. Implemented a 100px buffer zone around the trackpad overlay to prevent OS input loop conflicts.
* **Virtual Display Logic:** Fixed an issue where the Trackpad would close or reset incorrectly when launching applications onto a virtual screen (AR Glasses). The service now intelligently detects if it should remain on the cover screen or follow the app.
* **Launch Stability:** Improved the specific `am start` shell commands used by Shizuku to ensure apps launch in Freeform mode consistently across different Android versions.
