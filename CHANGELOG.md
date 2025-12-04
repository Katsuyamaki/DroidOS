# DroidOS v2.0 - The "Beam Pro" & Android 14 Update

**Release Date:** December 2025
**Codename:** Compatibility

This major release focuses on stabilizing the DroidOS ecosystem for **Android 14** devices, specifically optimizing for the **Xreal Beam Pro** and modern Samsung Foldables. It introduces new input paradigms to bypass system restrictions on touch-and-hold gestures and refines the "Headless" (Screen Off) experience.

## 🌟 Major Features

### 🕶️ Xreal Beam Pro & Android 14 Compatibility
* **Fixed Hidden API Reflection:** Updated `ShellUserService` to correctly handle `DisplayControl` vs `SurfaceControl` classes on Android 14, ensuring proper token acquisition for secondary displays.
* **Alternate Screen Off Mode:** Introduced a specific "Alternate Mode" for devices (like Beam Pro) where the standard `setDisplayPowerMode` API fails or sleeps the CPU.
    * *Mechanism:* Uses a specific `setBrightness(-1)` implementation via the legacy 2-argument hidden API, which successfully cuts backlight power on A14 without triggering system sleep.
    * *Behavior:* Configurable in Settings. Includes a "Wake on Power Button" listener to ensure the screen doesn't get stuck in a dark state.

### 🖱️ Input & Interaction Overhaul
Android 14 introduced stricter gesture interception, breaking traditional drag-and-drop on some cover screens/overlays.
* **Tap-to-Swap (Launcher):** Replaced the buggy drag-and-drop reordering with a robust "Tap-to-Swap" mode.
    * *Usage:* Long-press an app -> Icon highlights -> Tap destination to swap.
    * *Benefit:* Works reliably on all touchscreens and trackpads where "hold-and-drag" is intercepted by system gestures.
* **Trackpad Gestures:**
    * **Edge Scrolling:** dedicated scroll zones on the edges of the trackpad overlay.
    * **Touchpad Swipe:** Improved swipe detection logic for scrolling lists remotely.

### 📱 Launcher Improvements
* **Targeted Display Control:** The Launcher now intelligently passes the exact `displayId` to the Shell Service, ensuring that Screen Off/Brightness commands only affect the intended screen (Cover vs Main) on foldables.
* **Power Button Intercept:** Added a BroadcastReceiver for `ACTION_SCREEN_ON`. If you physically press the power button while in "Extinguish" mode, DroidOS now immediately clears the black screen override, preventing "panic" situations.

### 🛠️ Bug Fixes
* **Fixed:** "Binder haven't been received" crashes on startup by making the Shell Service connection more robust.
* **Fixed:** Cursor getting stuck in "drag" mode when connection flickered.
* **Fixed:** `setSystemBrightness` crash on Android 14 due to mismatched method signatures (reverted to legacy safe method).

---

## 📦 Module Versioning
* **CoverScreen Launcher:** v2.0
* **CoverScreen Trackpad:** v2.0
