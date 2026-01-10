projects/DroidOS main ⇡
❯ adb logcat -c && adb logcat OverlayService:W *:S
--------- beginning of main
01-10 16:20:33.479 12319 12319 W OverlayService: ╔══════════════════════════════════════════════════════════╗
01-10 16:20:33.479 12319 12319 W OverlayService: ║ KEYBOARD LOGIC START - setupUI(1)              ║
01-10 16:20:33.479 12319 12319 W OverlayService: ╚══════════════════════════════════════════════════════════╝
01-10 16:20:33.479 12319 12319 W OverlayService: ├─ PRE-STATE:
01-10 16:20:33.479 12319 12319 W OverlayService: │  ├─ displayId: 1
01-10 16:20:33.479 12319 12319 W OverlayService: │  ├─ currentDisplayId: 1
01-10 16:20:33.479 12319 12319 W OverlayService: │  ├─ prefBlockSoftKeyboard: false
01-10 16:20:33.479 12319 12319 W OverlayService: │  ├─ current IME: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:33.479 12319 12319 W OverlayService: │  └─ showMode: 0
01-10 16:20:33.479 12319 12319 W OverlayService: ├─ ACTION: COVER SCREEN - Checking if blocking needed
01-10 16:20:33.479 12319 12319 W OverlayService: │  └─ Blocking NOT enabled, skipping
01-10 16:20:33.479 12319 12319 W OverlayService: └─ KEYBOARD LOGIC END
01-10 16:20:33.519 12319 12319 W OverlayService: ╔══════════════════════════════════════════════════════════╗
01-10 16:20:33.519 12319 12319 W OverlayService: ║ KEYBOARD LOGIC START - setupUI(1)              ║
01-10 16:20:33.519 12319 12319 W OverlayService: ╚══════════════════════════════════════════════════════════╝
01-10 16:20:33.519 12319 12319 W OverlayService: ├─ PRE-STATE:
01-10 16:20:33.519 12319 12319 W OverlayService: │  ├─ displayId: 1
01-10 16:20:33.519 12319 12319 W OverlayService: │  ├─ currentDisplayId: 1
01-10 16:20:33.519 12319 12319 W OverlayService: │  ├─ prefBlockSoftKeyboard: false
01-10 16:20:33.519 12319 12319 W OverlayService: │  ├─ current IME: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:33.519 12319 12319 W OverlayService: │  └─ showMode: 0
01-10 16:20:33.519 12319 12319 W OverlayService: ├─ ACTION: COVER SCREEN - Checking if blocking needed
01-10 16:20:33.520 12319 12319 W OverlayService: │  └─ Blocking NOT enabled, skipping
01-10 16:20:33.520 12319 12319 W OverlayService: └─ KEYBOARD LOGIC END
01-10 16:20:33.557 12319 12319 W OverlayService: ╔══════════════════════════════════════════════════════════╗
01-10 16:20:33.557 12319 12319 W OverlayService: ║ KEYBOARD LOGIC START - setupUI(1)              ║
01-10 16:20:33.557 12319 12319 W OverlayService: ╚══════════════════════════════════════════════════════════╝
01-10 16:20:33.558 12319 12319 W OverlayService: ├─ PRE-STATE:
01-10 16:20:33.558 12319 12319 W OverlayService: │  ├─ displayId: 1
01-10 16:20:33.558 12319 12319 W OverlayService: │  ├─ currentDisplayId: 1
01-10 16:20:33.558 12319 12319 W OverlayService: │  ├─ prefBlockSoftKeyboard: false
01-10 16:20:33.558 12319 12319 W OverlayService: │  ├─ current IME: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:33.558 12319 12319 W OverlayService: │  └─ showMode: 0
01-10 16:20:33.558 12319 12319 W OverlayService: ├─ ACTION: COVER SCREEN - Checking if blocking needed
01-10 16:20:33.558 12319 12319 W OverlayService: │  └─ Blocking NOT enabled, skipping
01-10 16:20:33.558 12319 12319 W OverlayService: └─ KEYBOARD LOGIC END
01-10 16:20:40.695 12319 19567 W OverlayService: ┌──────────────────────────────────────────────────────────┐
01-10 16:20:40.695 12319 19567 W OverlayService: │ KEYBOARD RESTORATION - SAMSUNG WORKAROUND               │
01-10 16:20:40.695 12319 19567 W OverlayService: └──────────────────────────────────────────────────────────┘
01-10 16:20:40.756 12319 19567 W OverlayService: ├─ Initial IME: com.samsung.android.honeyboard/.service.HoneyBoardService
01-10 16:20:40.756 12319 19567 W OverlayService: ├─ Saved preference: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:40.820 12319 19567 W OverlayService: ├─ Gboard ID: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:40.820 12319 19567 W OverlayService: ├─ Samsung ID: com.samsung.android.honeyboard/.service.HoneyBoardService
01-10 16:20:40.820 12319 19567 W OverlayService: ├─ Target IME: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:40.820 12319 19567 W OverlayService: ├─ WORKAROUND: Temporarily disabling Samsung Keyboard...
01-10 16:20:40.863 12319 19567 W OverlayService: │  ├─ Disable result: Input method com.samsung.android.honeyboard/.service.HoneyBoardService: now disabled for user #0
01-10 16:20:41.063 12319 19567 W OverlayService: │  ├─ Set Gboard result: Input method com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME selected for user #0
01-10 16:20:41.619 12319 19567 W OverlayService: │  ├─ After set: com.samsung.android.honeyboard/.service.HoneyBoardService
01-10 16:20:41.619 12319 19567 W OverlayService: │  └─ Re-enabling Samsung Keyboard...
01-10 16:20:42.037 12319 19567 W OverlayService: ├─ FINAL IME: com.samsung.android.honeyboard/.service.HoneyBoardService
01-10 16:20:42.037 12319 19567 W OverlayService: ├─ SUCCESS: false
01-10 16:20:42.037 12319 19567 W OverlayService: ├─ First attempt failed, trying nuclear option...
01-10 16:20:42.931 12319 12319 W OverlayService: setSoftKeyboardBlocking: Already restoring, skipping duplicate call
01-10 16:20:45.439 12319 19567 W OverlayService: ├─ After nuclear: com.samsung.android.honeyboard/.service.HoneyBoardService
01-10 16:20:46.053 12319 19567 W OverlayService: ├─ Nuclear final: com.samsung.android.honeyboard/.service.HoneyBoardService
01-10 16:20:46.054 12319 19567 E OverlayService: ├─ NUCLEAR FAILED
01-10 16:20:46.054 12319 19567 W OverlayService: └─ KEYBOARD RESTORATION COMPLETE
01-10 16:20:47.386 12319 19870 W OverlayService: ┌──────────────────────────────────────────────────────────┐
01-10 16:20:47.387 12319 19870 W OverlayService: │ KEYBOARD RESTORATION - SAMSUNG WORKAROUND               │
01-10 16:20:47.387 12319 19870 W OverlayService: └──────────────────────────────────────────────────────────┘
01-10 16:20:47.469 12319 19870 W OverlayService: ├─ Initial IME: com.samsung.android.honeyboard/.service.HoneyBoardService
01-10 16:20:47.469 12319 19870 W OverlayService: ├─ Saved preference: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:47.519 12319 19870 W OverlayService: ├─ Gboard ID: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:47.519 12319 19870 W OverlayService: ├─ Samsung ID: com.samsung.android.honeyboard/.service.HoneyBoardService
01-10 16:20:47.519 12319 19870 W OverlayService: ├─ Target IME: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:47.519 12319 19870 W OverlayService: ├─ WORKAROUND: Temporarily disabling Samsung Keyboard...
01-10 16:20:47.561 12319 19870 W OverlayService: │  ├─ Disable result: Input method com.samsung.android.honeyboard/.service.HoneyBoardService: now disabled for user #0
01-10 16:20:47.819 12319 19870 W OverlayService: │  ├─ Set Gboard result: Input method com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME selected for user #0
01-10 16:20:48.379 12319 19870 W OverlayService: │  ├─ After set: com.samsung.android.honeyboard/.service.HoneyBoardService
01-10 16:20:48.380 12319 19870 W OverlayService: │  └─ Re-enabling Samsung Keyboard...
01-10 16:20:48.800 12319 19870 W OverlayService: ├─ FINAL IME: com.samsung.android.honeyboard/.service.HoneyBoardService
01-10 16:20:48.800 12319 19870 W OverlayService: ├─ SUCCESS: false
01-10 16:20:48.800 12319 19870 W OverlayService: ├─ First attempt failed, trying nuclear option...
01-10 16:20:49.721 12319 12319 W OverlayService: setSoftKeyboardBlocking: Already restoring, skipping duplicate call
01-10 16:20:50.272 12319 12319 W OverlayService: ╔══════════════════════════════════════════════════════════╗
01-10 16:20:50.272 12319 12319 W OverlayService: ║ PHONE OPENED DETECTED - onDisplayChanged                 ║
01-10 16:20:50.272 12319 12319 W OverlayService: ╚══════════════════════════════════════════════════════════╝
01-10 16:20:50.272 12319 12319 W OverlayService: ├─ display.state: 2 (STATE_ON=2)
01-10 16:20:50.272 12319 12319 W OverlayService: ├─ currentDisplayId: 1
01-10 16:20:50.272 12319 12319 W OverlayService: ├─ prefBlockSoftKeyboard: false
01-10 16:20:50.272 12319 12319 W OverlayService: ├─ Scheduling UI switch in 500ms...
01-10 16:20:50.773 12319 12319 W OverlayService: ├─ DELAYED HANDLER EXECUTING
01-10 16:20:50.773 12319 12319 W OverlayService: │  ├─ timeSinceManualSwitch: 1768080050773ms
01-10 16:20:50.773 12319 12319 W OverlayService: │  └─ Will execute: true
01-10 16:20:50.773 12319 12319 W OverlayService: ├─ Calling setupUI(0)...
01-10 16:20:50.814 12319 12319 W OverlayService: ╔══════════════════════════════════════════════════════════╗
01-10 16:20:50.814 12319 12319 W OverlayService: ║ KEYBOARD LOGIC START - setupUI(0)              ║
01-10 16:20:50.814 12319 12319 W OverlayService: ╚══════════════════════════════════════════════════════════╝
01-10 16:20:50.815 12319 12319 W OverlayService: ├─ PRE-STATE:
01-10 16:20:50.815 12319 12319 W OverlayService: │  ├─ displayId: 0
01-10 16:20:50.815 12319 12319 W OverlayService: │  ├─ currentDisplayId: 0
01-10 16:20:50.815 12319 12319 W OverlayService: │  ├─ prefBlockSoftKeyboard: false
01-10 16:20:50.815 12319 12319 W OverlayService: │  ├─ current IME: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:50.815 12319 12319 W OverlayService: │  └─ showMode: 0
01-10 16:20:50.815 12319 12319 W OverlayService: ├─ ACTION: MAIN SCREEN DETECTED - Attempting keyboard restoration
01-10 16:20:50.816 12319 12319 W OverlayService: │  ├─ showMode changed: 0 -> 0
01-10 16:20:50.816 12319 12319 W OverlayService: │  └─ Blocking NOT enabled, skipping restoration
01-10 16:20:50.816 12319 12319 W OverlayService: └─ KEYBOARD LOGIC END
01-10 16:20:50.816 12319 12319 W OverlayService: └─ Phone opened handling complete
01-10 16:20:52.229 12319 19870 W OverlayService: ├─ After nuclear: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:52.879 12319 19870 W OverlayService: ├─ Nuclear final: com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME
01-10 16:20:52.881 12319 19870 W OverlayService: └─ KEYBOARD RESTORATION COMPLETE
