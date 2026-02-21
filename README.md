# DroidOS

DroidOS is a two-app Android productivity suite:
- `DroidOS Launcher`: floating launcher, queue-based tiling window manager, display control, and automation hotkeys.
- `DroidOS Trackpad Keyboard`: overlay trackpad + keyboard + Dock IME toolbar for keyboard/mouse control and typing workflows.

It is designed for phones, foldables, cover displays, external monitors, and AR glasses.

![Screenshot_20251130_185618_Discord](https://github.com/user-attachments/assets/dca14a11-21e6-439c-b653-7ce9f8e73a87)

Video demo: https://youtu.be/aOzkV3t7wFM

---

## Project Layout (Monorepo)

- `DroidOSLauncher`: Launcher app (window/display management)
- `DroidOSKeyboardTrackpad`: Trackpad + keyboard app (input and typing)

Important: open each app folder separately in Android Studio. Do not open the monorepo root as a single Android Studio project.

---

## Core Requirements

- Shizuku installed and running
- Accessibility permissions granted to both apps as prompted
- Recommended Android developer options:
  - Force activities to be resizable
  - Enable freeform windows

---

## Install and First Run

1. Install both APKs from Releases.
2. Open `DroidOS Launcher` and grant required permissions.
3. Open `DroidOS Trackpad Keyboard` and complete the setup steps (Accessibility, overlay, keyboard/IME related steps).
4. Use Launcher `Launch/Reset Trackpad` in Settings if the input overlay needs to be reattached.

---

## Launcher Overview

### Bubble and Drawer

- Tap bubble: open launcher drawer
- Drag bubble: reposition
- Fling bubble: close/restart launcher service

### Queue-Based Tiling (Always Instant)

Launcher now runs in instant behavior only.

There is no separate Execute mode anymore. Queue/layout changes apply immediately.

### Queue Operations

- Add app: tap app in search list
- Reorder: drag in queue
- Minimize toggle: tap queue icon
- Remove/close: swipe queue icon
- Spacer item: use blank slot entry when needed to keep layout gaps

### Queue Order -> Tile Placement

Tile placement follows queue order.

- Apps are mapped left-to-right from the queue into the active layout tiles in layout order.
- The 1st queue item goes to tile 1, the 2nd goes to tile 2, and so on.
- If you want a specific app in a specific tile, drag that app to the matching queue position.

Example with a 4-tile layout:
- tile 1 = queue item 1
- tile 2 = queue item 2
- tile 3 = queue item 3
- tile 4 = queue item 4

### Favorites and Blacklist

- In Search tab:
  - Swipe left on app to add to blacklist
  - Favorite controls remain available from app list interactions
- In Blacklist tab:
  - Review blacklisted entries
  - Swipe to remove from blacklist

---

## Display and Window Management

### Display Controls

Launcher Settings includes:
- `Screen Off (Standard)`
- `Screen Off (Alternate)`
- `Switch Display (Current X)`
- `Virtual Display (1080p)`
- resolution controls
- DPI controls
- orientation controls
- refresh rate controls

### Virtual Display Quick Setup (Recommended)

1. Enable `Virtual Display (1080p)` in Launcher Settings.
2. Tap `Switch Display` to move launcher targeting to the virtual display.
3. Start/reset Trackpad from launcher if needed.
4. Use trackpad keyboard mirror features if running headless or on AR glasses.

### Manual Virtual Display Path

Advanced/manual users can still use shell-style display workflows, but the in-app toggle flow above is the easiest and most reliable path for normal use.

### Tiled and Full Screen Integration

DroidOS coordinates tiled windows with normal full-screen apps:
- Tiled apps can auto-minimize when a full-screen app takes focus.
- Tiled apps restore when returning to managed/tiled context.
- This avoids tiled overlays blocking normal full-screen workflows.

---

## Keyboard and Margin Integration

### Margin Controls (Launcher)

Launcher Settings contains:
- top/bottom manual margins
- `Auto-Adjust Margin for Keyboard (Tiled Apps)`
- `Auto-Shrink for Keyboard`

When DroidOS toolbar keyboard/IME is active, launcher can adjust tiled app space dynamically based on keyboard visibility and dock/toolbar state.

### Why This Matters

This integration prevents overlap between:
- tiled app windows
- keyboard UI
- dock/toolbar area

Result: more stable typing layouts in multi-window use.

---

## Trackpad Keyboard Overview

### Core Input Features

- On-screen trackpad with tap, right-click, drag, and edge scrolling
- Overlay keyboard for typing on local or remote displays
- Dock IME toolbar mode for bottom-docked keyboard workflows

### Mirror Mode

Mirror mode is for remote/virtual usage where you need to type without directly viewing the phone display.

Typical use cases:
- external monitor
- AR glasses
- screen-off/headless workflows

### Spacebar Mouse Modes

- Normal: hold spacebar to move cursor, release to return to typing
- Extended: toggle `Spacebar Mouse Extended Mode` to keep mouse mode active until turned off
- Click actions come from trackpad gestures or mapped keys/hardkeys

### Keyboard Blocking

Keyboard blocking is for devices that force OEM keyboards in unwanted situations (for example some cover-screen Samsung flows).

Use it when the system keyboard interrupts DroidOS keyboard workflows.

### Prediction and Swipe Typing

The keyboard supports:
- swipe prediction
- tap prediction
- learned/custom dictionary words
- prediction updates in both normal and mirror workflows

### User Dictionary

Add words:
- tap new-word predictions to learn/save into custom dictionary

Delete learned words:
- touch and hold a prediction, then use trash/delete action

Persistence behavior:
- kept during app upgrades
- deleted if app storage is cleared or app is uninstalled

---

## Virtual Mirror + Display Switching Behavior

Trackpad keyboard and launcher coordinate display state with broadcasts.

In virtual workflows, display/mode-aware settings (position/size and related layout preferences) are remembered by display/resolution/orientation/mode, with shared handling for remote display IDs (display `2+`) to keep behavior consistent across reconnects.

---

## Automation and Broadcast Integration

### Launcher Hotkey Security Toggle

In Launcher Hotkeys tab:
- `Allow External App Broadcast Access`

When ON, third-party automation tools can trigger launcher commands.
When OFF, external launcher command access is blocked (except trusted internal app coordination as designed).

### Inter-App Coordination

Launcher and Keyboard exchange broadcast state for:
- keybind updates
- remote key forwarding
- IME visibility/tiled state sync
- margin and display coordination
- virtual/physical display transitions

### Third-Party Automation

DroidOS can be integrated with automation apps (for example Tasker or MacroDroid) and with ADB broadcast commands.

This supports advanced workflows such as:
- one-tap virtual workspace startup
- mirror/display mode switching
- scripted launcher command execution

---

## Other Useful Notes

- If trackpad/cursor appears on wrong display after big display changes, use Launcher `Launch/Reset Trackpad`.
- If UI overlays feel out of order, restart the relevant service from app settings.
- For launcher recovery, fling the launcher bubble.

---

## Contributing

Contributions are welcome.

- Launcher changes: submit against `DroidOSLauncher`
- Keyboard/trackpad changes: submit against `DroidOSKeyboardTrackpad`

---

## License

GPLv3. See `LICENSE.txt`.

---

## Support

If you want to support development: https://ko-fi.com/katsuyamaki


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


