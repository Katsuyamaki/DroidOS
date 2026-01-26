# Changelog

## [v4.0] - DroidOS Update (2026-01-25)
### Major Features
- **Custom Keybinding System:** Comprehensive overhaul allowing users to define and reorder hotkeys with specialized swap/hide logic.
- **Hardware Integration:** Integration for Hard Keyboards and Mice, including a Bluetooth Mouse Mirror Mode with corrected Z-order rendering.
- **Keyboard Navigation:** Full support for navigating the DroidOS Launcher interface using hardware keyboard inputs (Tab and directional arrows, also use your keybind cmds without modifier on launcher drawer app queue).
- **App Focus System:** New internal logic to manage window focus more reliably when switching between apps or displays.
- **Custom Margin Adjustment:** Added the ability to fine-tune display margins for better layout precision on different screen types.

### Bug Fixes & UI Improvements
- **Process Management:** Fixed bugs related to Closing and Restarting apps; separated Terminate and Restart buttons for better control.
- **Display Stability:** Resolved "Display Confusion" issues within the watchdog timer and fixed crashes during layout renaming.
- **Layout Refinements:** Edit Layout Names and implemented per-display layout configurations.
- **Cleanup:** Sanitized internal dictionaries and disabled debug logging for the release build to improve performance.


## [v3.0] - DroidOS Trackpad (2025-12-18)
### Major Features
- **Hardkey Mapping:** Map Volume Up/Down keys to actions (Click, Scroll, Toggle UI) with Single Tap, Double Tap, and Hold gestures.
- **Profile Management:** Save and load specific layout/scaling profiles for different scenarios (e.g., Cover Screen vs. AR Glasses).
- **Bubble Customization:**
  - Adjust Floating Bubble size (50% - 200%).
  - Change Opacity.
  - Cycle through different icon styles.
- **Smart Keyboard Overlay:**
  - "Block Soft Keyboard" mode to prevent system keyboard from taking over the cover screen.
  - Adjustable Scale and Opacity.
  - Auto-hide logic when switching modes.
- **Screen Off Modes:**
  - **Standard:** Uses SurfaceControl (Root-like behavior via Shizuku).
  - **Alternate:** Uses Brightness Extinguish (-1) for devices where SurfaceControl freezes the display.
- **UI & UX:**
  - **Anchor Mode:** Lock the trackpad position to prevent accidental drags.
  - **Z-Order Enforcement:** Ensures trackpad remains visible over other system overlays.
  - **Manual Tune:** D-Pad interface for pixel-perfect window positioning.

## [v2.1] - DroidOS Launcher
### Updates
- **Compatibility:** Updated Intent targeting to match Trackpad v3.0 package ID.
- **Stability:** improved `setBrightness` handling for Alternate Screen Off mode.

---

## [v2.0] - Initial Monorepo Structure
- Split project into Launcher and Trackpad applications.
- Added Shizuku integration for non-root shell execution.
