# DroidOS Play Store Submission Notes

Use this as a copy/paste reference when filling Play Console forms.

## 1) Foreground Service (specialUse)

### DroidOS Launcher
- Service: `FloatingLauncherService`
- Manifest subtype: `floating_window_management`
- Copy/paste justification:

```
This app runs a persistent Accessibility overlay service to provide a floating launcher bubble and window management controls (tiling, display switching, and taskbar-style workflows). The service must remain active while the user is interacting with on-screen overlay controls.
```

### DroidOS Trackpad
- Service: `OverlayService`
- Manifest subtype: `input_interception_overlay`
- Copy/paste justification:

```
This app runs a persistent Accessibility overlay service to provide on-screen trackpad/cursor/keyboard controls and cross-display input routing automation. The service must remain active to keep the overlay and input-control workflow available during use.
```

---

## 2) Accessibility API Declaration

### Core user-facing purpose

```
DroidOS uses AccessibilityService for explicit user-triggered desktop-control workflows: floating overlays, active window detection, hotkey handling, and window/input automation needed for launcher tiling and trackpad control across displays.
```

### Why Accessibility is needed

```
The app relies on AccessibilityService to monitor window focus/state, receive key events, and perform controlled automation actions that cannot be implemented with regular app APIs alone.
```

### User benefit

```
Users get a desktop-like control layer on their device, including floating launcher controls, app tiling workflows, and trackpad/cursor input control for local or remote display scenarios.
```

### Data handling statement

```
DroidOS does not transmit personal data to external servers. Configuration and automation state are stored locally on-device. For keyboard prediction features, some user-input-derived data (such as learned dictionary words and local usage stats) is stored locally on-device.
```

---

## 3) Sensitive Permissions / Purpose Text

### `RECORD_AUDIO` (Trackpad app)
Use if Play Console asks why microphone access is requested.

```
Microphone access is used only for user-initiated voice input features. Audio is handled through system voice input flows. The app does not transmit personal data to external servers.
```

### `POST_NOTIFICATIONS` (Trackpad app)

```
Used to show foreground service / status notifications required for persistent overlay operation.
```

### `SYSTEM_ALERT_WINDOW`

```
Used for floating overlay controls (bubble, trackpad/cursor UI, and related control surfaces) as part of the app’s core workflow.
```

---

## 4) Data Safety (recommended answers based on current code)

- No in-app networking stack detected in current source/manifests (no `INTERNET` permission).
- No analytics/crash SDKs detected in current Gradle dependencies.
- Keyboard prediction stores learned words/stats locally in app-private storage.

Suggested summary text:

```
Data is processed locally on-device for app functionality. The app does not transmit personal data to external servers. Some user-input-derived data for prediction (learned words and local usage stats) is stored locally in app-private storage.
```

---

## 5) Package Visibility / Queries

Current manifests use scoped `<queries>` entries and do not use broad `QUERY_ALL_PACKAGES`.

Copy/paste statement:

```
The app does not request broad all-packages visibility. Package visibility is limited to specific integrations and intents required for core functionality.
```

---

## 6) Exported Commands / Automation Interop

If reviewer asks about broadcast command interop (Tasker/MacroDroid style):

```
The app intentionally supports user-driven automation via broadcast commands for interoperability with automation tools. Commands are for window/input workflow control and not for exfiltrating user data.
```

If you keep external command toggle:

```
Users can control external broadcast access in-app. Trusted first-party integration remains allowed for DroidOS companion components.
```

---

## 7) Final Pre-submit Checklist

- Foreground service declaration matches `specialUse` behavior for both apps.
- Accessibility disclosure text in permission flows matches actual behavior.
- Data statement matches implementation: local storage for prediction data, no external transmission.
- Sensitive permission justifications are present (`RECORD_AUDIO`, overlay, notifications).
- No `QUERY_ALL_PACKAGES` declaration.

