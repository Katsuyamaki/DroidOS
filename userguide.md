# DroidOS Floating Launcher User Guide

## 1. Introduction

Welcome to the DroidOS Floating Launcher! This guide will walk you through the powerful features of this versatile tool, designed to enhance your multitasking and window management experience on Android. The Floating Launcher provides a persistent, movable "bubble" on your screen that gives you quick access to a feature-rich "drawer" for launching and organizing your applications.

## 2. Core Concepts

### The Bubble
The bubble is your main entry point to the launcher. It's a floating icon that stays on top of other applications.

- **Tap:** Opens the main Drawer.
- **Drag:** Move the bubble anywhere on the screen.
- **Fling:** A fast flick will force-close and restart the launcher service (for troubleshooting).

### The Drawer
The drawer is the central hub for all launcher functions. It's organized into several tabs, each with a specific purpose. You can cycle through tabs using the icons at the top or by using the `Tab` key on a hardware keyboard.

### The App Queue
The horizontal bar at the top of the drawer displays your currently managed applications. This is your "tiling queue". Apps in this queue are the ones that DroidOS will arrange into layouts.

- **Add an app:** Search for an app in the app list (Search tab) and tap it.
- **Remove an app:** Swipe an app up or down out of the queue to close it.
- **Reorder apps:** Long-press and drag an app to a new position in the queue. The order here determines the position in the layout (e.g., in a side-by-side layout, the first app is on the left, the second is on the right).

## 3. Window Management

DroidOS excels at arranging apps on your screen.

### Layouts
The **Layouts** tab (Window icon) allows you to choose how your apps are tiled.

- **Pre-defined Layouts:** Includes options like `1x2` (Side-by-Side), `2x1` (Top/Bottom), `2x2` (Corners), and more complex arrangements for 3, 4, or even 6 apps.
- **Custom Layouts:** You can save your current window arrangement as a custom layout. This is useful for creating personalized workspaces.

### Window Commands
You can manipulate windows using keybinds or the visual queue. Available actions include:

- **Minimize:** Hides an app from the screen but keeps it in the queue in a minimized state.
- **Restore:** Brings a minimized app back into the layout.
- **Swap:** Swaps the position of two apps in the layout.
- **Move:** Moves an app from one slot to another, shifting other apps.
- **Hide (Blank):** Replaces an app's slot with a blank spacer, effectively hiding it while preserving the layout.

## 4. App Management

### Launching Apps
- From the **Search** tab, simply find the app you want and tap it. It will be added to the queue and launched into the current layout.
- **Open & Move To / Open & Swap:** These powerful keybind commands let you select an app from the list and immediately place it into a specific slot, either by moving it or swapping with an existing app.

### Blacklisting & Favorites
- In the **Search** tab, swipe an app to the **left** to add it to the blacklist, hiding it from the app list.
- Swipe an app to the **right** to toggle its "Favorite" status, which pins it to the top of the search results.
- You can manage your blacklisted apps in the **Blacklist** tab.

## 5. Multi-Display Support

The launcher is designed for modern devices with multiple or unique displays.

- **Switch Display:** A dedicated keybind or settings option lets you move the entire launcher and your tiled apps between your phone's main screen, cover screen, or any connected external display.
- **Foldable Support:** The launcher can automatically switch between your main and cover screens as you fold and unfold your device.
- **Virtual Displays (AR/XR Glasses):** DroidOS can create a virtual display, allowing you to have a separate, persistent workspace on a connected external monitor or AR glasses. The "Watchdog" feature ensures that apps launched on the virtual display stay there and don't "escape" back to the phone screen.

## 6. Keybinds and Commands

The true power of the Floating Launcher is unlocked with keybinds. The **Keybinds** tab allows you to configure hotkeys for nearly every action.

### How to Use
- **Modifiers:** Most commands require a modifier key (`ALT`, `SHIFT`, `CTRL`, `META`, or a custom key).
- **Trigger:** Press the modifier and the bound key simultaneously to execute the command.

### Visual Queue (HUD)
For commands that require selecting an app (like `Minimize` or `Swap`), a **Visual Queue** or "HUD" will appear. This shows your currently tiled apps with a number for each slot.

- **Enter the slot number** to complete the command.
- Use **arrow keys** to navigate the selection and press **Enter** to confirm.
- Press **Escape** to cancel the command.

### List of Commands
The Keybinds tab provides a full list of available commands and their descriptions, including:
- `Toggle Drawer`
- `Set Focus`
- `Focus Last`
- `Kill App`
- `Swap Active Left/Right`
- `Minimize`, `Unminimize`, `Minimize All`, `Restore All`
- `Swap`, `Move To`, `Open & Move To`, `Open & Swap`

## 7. Settings and Customization

The **Settings** tab (Gear icon) provides extensive options to tailor the launcher to your needs.

- **Display Settings:** Adjust screen **Resolution**, **DPI** (density), and **Refresh Rate**.
- **UI Customization:** Change the **Drawer Height/Width**, **Bubble Size**, and UI **Font Size**.
- **Margins:** Set top and bottom margins to reserve screen space for status bars or other elements. The launcher can also auto-adjust for the on-screen keyboard.
- **Profiles:** Save and load entire configurations, including the layout, apps in the queue, and display settings. This lets you switch between different workspaces instantly.
- **Icon:** Change the look of the floating bubble.

## 8. Shizuku Integration

DroidOS uses **Shizuku** to gain the necessary permissions for advanced window management. Shizuku allows the launcher to execute shell commands with elevated privileges without requiring a full root of your device. It is essential for features like force-stopping apps, changing display settings, and reliably tiling windows.

If the bubble icon is red, it means Shizuku is not connected. Tap the bubble to open the Shizuku app and start the service.

## 9. Troubleshooting

- **Launcher is stuck or unresponsive:** Fling the bubble off-screen to force-restart the service.
- **Apps are not tiling correctly:** Ensure Shizuku is running and connected. Try restarting the DroidOS Launcher from the Settings tab.
- **Trackpad not working:** Use the "Launch/Reset Trackpad" button in Settings.
