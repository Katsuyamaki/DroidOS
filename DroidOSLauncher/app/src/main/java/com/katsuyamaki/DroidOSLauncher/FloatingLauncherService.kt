package com.katsuyamaki.DroidOSLauncher

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.app.Service
import android.view.accessibility.AccessibilityEvent
import android.content.res.Configuration
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
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rikka.shizuku.Shizuku
import java.text.SimpleDateFormat
import java.util.*
import java.lang.reflect.Method
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.max
import android.os.PowerManager

class FloatingLauncherService : AccessibilityService(), LauncherActionHandler {

    // [NEW] Debug Mode State
    private var isDebugMode = false
    private var debugClickCount = 0
    private var lastDebugClickTime = 0L

    private var virtualDisplay: android.hardware.display.VirtualDisplay? = null
    private var virtualImageReader: ImageReader? = null // Keeps surface alive
    private val ACTION_TOGGLE_VIRTUAL = "com.katsuyamaki.DroidOSLauncher.TOGGLE_VIRTUAL_DISPLAY"

    // === RECEIVER - START ===
    private val launcherReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.katsuyamaki.DroidOSTrackpadKeyboard.MOVE_TO_DISPLAY" -> {
                    val targetId = intent.getIntExtra("displayId", 0)
                    uiHandler.post {
                        // CLEANUP OLD VIEWS (Bubble & Drawer)
                        try {
                            val wm = attachedWindowManager ?: windowManager
                            if (bubbleView != null) {
                                try { wm.removeView(bubbleView) } catch(e: Exception) {}
                            }
                            if (drawerView != null && isExpanded) {
                                try { wm.removeView(drawerView) } catch(e: Exception) {}
                            }
                        } catch (e: Exception) { Log.e(TAG, "Receiver cleanup failed", e) }

                        currentDisplayId = targetId
                        initWindow()
                        isExpanded = false // Reset state for new display
                    }
                }
            }
        }
    }
    // === RECEIVER - END ===
// =================================================================================
    // WAKE LOCK - Prevents screen from turning off while Launcher is active
    // =================================================================================
    private var wakeLock: PowerManager.WakeLock? = null
    private var keepScreenOnEnabled = false
    // =================================================================================
    // END BLOCK: WAKE LOCK
    // =================================================================================
    private val TAG = "FloatingLauncherService"
    private val drawerKeyRouter = DrawerKeyRouter()

    private fun currentRouterArea(): DrawerKeyRouter.Area {
        return when (currentFocusArea) {
            FOCUS_SEARCH -> DrawerKeyRouter.Area.SEARCH
            FOCUS_QUEUE -> DrawerKeyRouter.Area.QUEUE
            FOCUS_LIST -> DrawerKeyRouter.Area.LIST
            else -> DrawerKeyRouter.Area.OTHER
        }
    }

    companion object {
        // === MODE CONSTANTS - START ===
        // Defines the different drawer modes/tabs
        const val MODE_SEARCH = 0      // App picker tab
        const val MODE_LAYOUTS = 2     // Layout selection (skips 1)
        const val MODE_RESOLUTION = 3  // Resolution settings
        const val MODE_DPI = 4         // DPI settings
        const val MODE_BLACKLIST = 5   // Blacklist management tab
        const val MODE_PROFILES = 6    // Profiles tab
        const val MODE_KEYBINDS = 7    // Keybinds tab
        const val MODE_SETTINGS = 8    // Settings tab
        const val MODE_REFRESH = 9     // [NEW] Refresh Rate Tab
        // === MODE CONSTANTS - END ===
        
        const val LAYOUT_FULL = 1
        const val LAYOUT_SIDE_BY_SIDE = 2
        const val LAYOUT_TOP_BOTTOM = 5
        const val LAYOUT_TRI_EVEN = 3
        const val LAYOUT_CORNERS = 4
        const val LAYOUT_TRI_SIDE_MAIN_SIDE = 6
        const val LAYOUT_QUAD_ROW_EVEN = 7
        const val LAYOUT_QUAD_TALL_SHORT = 8
        const val LAYOUT_HEX_TALL_SHORT = 9
        const val LAYOUT_CUSTOM_DYNAMIC = 99

        const val CHANNEL_ID = "OverlayServiceChannel"
        const val TAG = "FloatingService"
        const val DEBUG_TAG = "DROIDOS_DEBUG"
        const val ACTION_OPEN_DRAWER = "com.katsuyamaki.DroidOSLauncher.OPEN_DRAWER"
        const val ACTION_UPDATE_ICON = "com.katsuyamaki.DroidOSLauncher.UPDATE_ICON"
        const val ACTION_CYCLE_DISPLAY = "com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY"
        const val HIGHLIGHT_COLOR = 0xFF00A0E9.toInt()
    }

    private lateinit var windowManager: WindowManager
    // Track the specific WM used to add the bubble to ensure we can remove it later
    private var attachedWindowManager: WindowManager? = null 
    private var displayManager: DisplayManager? = null
    
    private var displayContext: Context? = null
    override var currentDisplayId = 0
    private var lastPhysicalDisplayId = Display.DEFAULT_DISPLAY
    // Tracks the currently focused app package for "Active Window" commands
    override var activePackageName: String? = null
    private var manualFocusLockUntil: Long = 0L  // Timestamp until A11Y should not override activePackageName
    private val minimizedAtTimestamps = mutableMapOf<String, Long>() // Track when apps were minimized (newest first)
    // History for focus restoration (ignoring overlays)
    private var lastValidPackageName: String? = null
    private var secondLastValidPackageName: String? = null

    // === KEYBIND SYSTEM ===
    private var visualQueueView: View? = null
    private var visualQueueParams: WindowManager.LayoutParams? = null
    private var visualQueueWindowManager: WindowManager? = null  // Track WM used to add view
    private var isVisualQueueVisible = false

    // Command State Machine
    private var pendingCommandId: String? = null
    private var vqCursorIndex: Int = 0  // Arrow-key cursor for visual queue HUD
    private var lastQueueNavTime: Long = 0L  // [FIX] Dedup timestamp for queue navigation
    private var lastEnterToggleTime: Long = 0L  // [FIX] Debounce for ENTER smart toggle
    private var vqTextWatcher: android.text.TextWatcher? = null
    
    // [FIX] Unified WM command queue with paced execution and retry handling
    private val wmCommandQueue: ArrayDeque<Intent> = ArrayDeque()
    private var isProcessingWmCommand = false
    private var pendingWmRetryScheduled = false
    private var lastWmBindAttemptAt = 0L
    private var consecutiveWmQueueFailures = 0
    private val wmQueueFlushRunnable = Runnable {
        pendingWmRetryScheduled = false
        flushWindowManagerCommandQueue()
    }
    private val wmQueueAdvanceRunnable = Runnable {
        isProcessingWmCommand = false
        if (wmCommandQueue.isNotEmpty()) {
            scheduleWindowManagerQueueFlush(0L)
        }
    }

// Custom Modifier State
    override val MOD_CUSTOM = -999 // Internal ID for custom modifier
    override var customModKey = 0

private var isSoftKeyboardSupport = false
    private var isCustomModLatched = false
    // No timer runnable needed - latch stays active until consumed by next key press

    override var pendingArg1: Int = -1
    private val commandTimeoutRunnable = Runnable { abortCommandMode() }
    private val COMMAND_TIMEOUT_MS = 5000L

    // Key Picker State
    private var keyPickerView: View? = null
    private var keyPickerParams: WindowManager.LayoutParams? = null

    // Data Classes for Configuration

    override val SUPPORTED_KEYS = linkedMapOf(
        "A" to KeyEvent.KEYCODE_A, "B" to KeyEvent.KEYCODE_B, "C" to KeyEvent.KEYCODE_C,
        "D" to KeyEvent.KEYCODE_D, "E" to KeyEvent.KEYCODE_E, "F" to KeyEvent.KEYCODE_F,
        "G" to KeyEvent.KEYCODE_G, "H" to KeyEvent.KEYCODE_H, "I" to KeyEvent.KEYCODE_I,
        "J" to KeyEvent.KEYCODE_J, "K" to KeyEvent.KEYCODE_K, "L" to KeyEvent.KEYCODE_L,
        "M" to KeyEvent.KEYCODE_M, "N" to KeyEvent.KEYCODE_N, "O" to KeyEvent.KEYCODE_O,
        "P" to KeyEvent.KEYCODE_P, "Q" to KeyEvent.KEYCODE_Q, "R" to KeyEvent.KEYCODE_R,
        "S" to KeyEvent.KEYCODE_S, "T" to KeyEvent.KEYCODE_T, "U" to KeyEvent.KEYCODE_U,
        "V" to KeyEvent.KEYCODE_V, "W" to KeyEvent.KEYCODE_W, "X" to KeyEvent.KEYCODE_X,
        "Y" to KeyEvent.KEYCODE_Y, "Z" to KeyEvent.KEYCODE_Z,
        "0" to KeyEvent.KEYCODE_0, "1" to KeyEvent.KEYCODE_1, "2" to KeyEvent.KEYCODE_2,
        "3" to KeyEvent.KEYCODE_3, "4" to KeyEvent.KEYCODE_4, "5" to KeyEvent.KEYCODE_5,
        "6" to KeyEvent.KEYCODE_6, "7" to KeyEvent.KEYCODE_7, "8" to KeyEvent.KEYCODE_8, "9" to KeyEvent.KEYCODE_9,
        "Left" to KeyEvent.KEYCODE_DPAD_LEFT, "Right" to KeyEvent.KEYCODE_DPAD_RIGHT,
        "Up" to KeyEvent.KEYCODE_DPAD_UP, "Down" to KeyEvent.KEYCODE_DPAD_DOWN,
        "Space" to KeyEvent.KEYCODE_SPACE, "Enter" to KeyEvent.KEYCODE_ENTER,
        "Tab" to KeyEvent.KEYCODE_TAB, "Esc" to KeyEvent.KEYCODE_ESCAPE,
        "Backspace" to KeyEvent.KEYCODE_DEL, "Delete" to KeyEvent.KEYCODE_FORWARD_DEL,
        "Home" to KeyEvent.KEYCODE_MOVE_HOME, "End" to KeyEvent.KEYCODE_MOVE_END,
        "PageUp" to KeyEvent.KEYCODE_PAGE_UP, "PageDn" to KeyEvent.KEYCODE_PAGE_DOWN,
        "F1" to KeyEvent.KEYCODE_F1, "F2" to KeyEvent.KEYCODE_F2, "F3" to KeyEvent.KEYCODE_F3,
        "F4" to KeyEvent.KEYCODE_F4, "F5" to KeyEvent.KEYCODE_F5, "F6" to KeyEvent.KEYCODE_F6,
        "F7" to KeyEvent.KEYCODE_F7, "F8" to KeyEvent.KEYCODE_F8, "F9" to KeyEvent.KEYCODE_F9,
        "F10" to KeyEvent.KEYCODE_F10, "F11" to KeyEvent.KEYCODE_F11, "F12" to KeyEvent.KEYCODE_F12
    )

    // ===================================================================================
    // BROADCAST COMMAND CHECKLIST - Adding a New Command
    // ===================================================================================
    // When creating a new broadcast command, update the following locations:
    //
    // 1. AVAILABLE_COMMANDS (below)
    //    - Add CommandDef("CMD_ID", "Label", argCount, "Description")
    //    - argCount: 0 = no args, 1 = single INDEX, 2 = INDEX_A + INDEX_B
    //
    // 2. AppPreferences.kt - getDefaultKeybind()
    //    - Add default keybind: "CMD_ID" -> Pair(modifier, keyCode)
    //    - Modifiers: 0=None, 2=Alt (matches META_ALT_ON), 1=Shift, etc.
    //    - NOTE: Modifier values must match Android KeyEvent.META_* flags for HW keyboard
    //
    // 3. handleWindowManagerCommand() - Command Handler
    //    - Add "CMD_ID" -> { ... } case in the when block
    //    - For drawer-opening commands (like OPEN_MOVE_TO), call triggerCommand()
    //
    // 4. buildAdbCommand() - ADB Command String
    //    - Add "CMD_ID" -> "adb shell am broadcast ..." for Settings display
    //
    // HARDWARE KEYBOARD FLOW:
    //    - onKeyEvent() loops through AVAILABLE_COMMANDS and checks keybinds
    //    - When matched, it calls triggerCommand(cmd)
    //    - For argCount=0: executes immediately OR handles special cases
    //    - For argCount>=1: enters visual queue input mode
    //
    // 5. FOR INTERACTIVE COMMANDS (like OPEN_MOVE_TO, OPEN_SWAP):
    //    These require additional state management and UI handling:
    //
    //    a) State Variables (near line 378):
    //       - Add isYourModeActive: Boolean = false
    //       - Reuse openMoveToApp and showSlotNumbersInQueue if similar flow
    //
    //    b) triggerCommand() - Mode Initialization (CRITICAL FOR HW KEYBOARD):
    //       - Add special case INSIDE the "if (cmd.argCount == 0)" block
    //       - MUST include ALL of these steps:
    //         1. Set your mode flag = true (reset other mode flags)
    //         2. Reset openMoveToApp = null
    //         3. Reset showSlotNumbersInQueue = false
    //         4. Open drawer: if (!isExpanded) toggleDrawer()
    //         5. Switch mode: switchMode(MODE_SEARCH)
    //         6. Set focus: currentFocusArea = FOCUS_SEARCH
    //         7. Setup search bar: et?.setText(""); et?.requestFocus()
    //         8. Show prompt: debugStatusView?.text = "Your Mode: Select an app"
    //         9. RETURN to prevent falling through to immediate execution
    //
    //    c) executeYourCommand() Function:
    //       - Create function to execute the command after user selection
    //       - Reset mode state, release keyboard capture, perform action
    //
    //    d) cancelOpenMoveToMode() - Mode Cancellation:
    //       - Add your mode flag reset
    //
    //    e) handleRemoteKeyEvent() - FOCUS_QUEUE Section:
    //       - DPAD_UP/DOWN: Add navigation block check for your mode
    //       - ENTER: Add confirmation handler for your mode
    //       - ESCAPE: Add cancellation handler for your mode
    //       - SPACE: Add confirmation handler for your mode
    //       - Number keys (else block): Add slot selection for your mode
    //       - Update debugStatusView visibility checks
    //
    //    f) searchBar setOnKeyListener:
    //       - DPAD_UP/DOWN in FOCUS_QUEUE: Add navigation block
    //       - ESCAPE: Add cancellation handler
    //       - Number keys section: Add slot selection
    //       - Update debugStatusView visibility checks
    //
    //    g) drawerView setOnKeyListener - FOCUS_QUEUE Section:
    //       - DPAD_UP/DOWN: Add navigation block
    //       - ENTER/SPACE: Add confirmation handler
    //       - Update debugStatusView visibility checks
    //       - Queue navigation text: Add mode check
    //
    //    h) toggleDrawer():
    //       - Add mode check to skip state reset when opening for your command
    //       - Add mode check to skip showQueueDebugState()
    //
    //    i) addToSelection() - App Selection Intercept:
    //       - Add your mode to the intercept condition
    //
    // REFERENCE: See OPEN_MOVE_TO and OPEN_SWAP implementations for examples
    // ===================================================================================
    val AVAILABLE_COMMANDS = listOf(
        CommandDef("OPEN_DRAWER", "Toggle Drawer", 0, "Show/Hide Launcher"),
        CommandDef("SET_FOCUS", "Set Focus", 1, "Focus app in slot #"),
        CommandDef("FOCUS_LAST", "Focus Last", 0, "Switch to previous app"),
        CommandDef("KILL", "Kill App", 1, "Force stop app in slot"),
        CommandDef("SWAP_ACTIVE_LEFT", "Move Active Left", 0, "Move focused window to prev slot"),
        CommandDef("SWAP_ACTIVE_RIGHT", "Move Active Right", 0, "Move focused window to next slot"),
        CommandDef("MINIMIZE", "Minimize", 1, "Minimize slot (shift others)"),
        CommandDef("UNMINIMIZE", "Restore", 1, "Restore app in slot"),
        CommandDef("HIDE", "Hide (Blank)", 1, "Replace slot with blank space"),
        CommandDef("SWAP", "Swap Slots", 2, "Swap app in Slot A with Slot B"),
        CommandDef("MOVE_TO", "Move To", 2, "Move app to slot # (shifts others)"),
        CommandDef("OPEN_MOVE_TO", "Open & Move To", 0, "Open app and place in slot #"),
        CommandDef("OPEN_SWAP", "Open & Swap", 0, "Open app and swap with slot #"),
        CommandDef("MINIMIZE_ALL", "Minimize All", 0, "Minimize all tiled apps"),
        CommandDef("RESTORE_ALL", "Restore All", 0, "Restore minimized apps to slots")
    )

    // Debounce for display switch to prevent flickering
    private var lastManualSwitchTime = 0L
    private var switchRunnable: Runnable? = null

    // === EXECUTION DEBOUNCE - START ===
    // Prevents multiple rapid executions
    private var lastExecuteTime = 0L
    private val EXECUTE_DEBOUNCE_MS = 500L // Reduced debounce check
    private var pendingLaunchRunnable: Runnable? = null

    // Single-threaded execution lock
    @Volatile private var isExecuting = false
    @Volatile private var pendingExecutionNeeded = false
    @Volatile private var pendingFocusPackage: String? = null
    @Volatile private var pendingHeadlessRetile = false
    private var pendingHeadlessRetileReason: String? = null
    private val headlessRetileRunnable = Runnable { runPendingHeadlessRetile() }
    // === EXECUTION DEBOUNCE - END ===

    private var manualRefreshRateSet = false // [NEW] Prevents auto-force from overwriting user choice

    private var activeRefreshRateLabel: String? = null // [NEW] Track active software limit
    // =================================================================================
    // DISPLAY LISTENER
    // SUMMARY: Monitors display state changes to handle fold/unfold events on foldable devices.
    //          Automatically switches the launcher between main screen (0) and cover screen (1)
    //          based on which display is active.
    //          CRITICAL: Skips auto-switch when on a virtual display (ID >= 2) to prevent
    //          the launcher from jumping back to physical screens during virtual display use.
    // =================================================================================
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) {}
        override fun onDisplayRemoved(displayId: Int) {
            // If a virtual display (ID >= 2) is removed, release wake lock
            if (displayId >= 2) {
                setKeepScreenOn(false)
            }
            if (displayId == currentDisplayId) {
                // If current display disconnects (e.g. glasses), revert to Default
                performDisplayChange(Display.DEFAULT_DISPLAY)
            }
        }
        override fun onDisplayChanged(displayId: Int) {
            // =================================================================================
            // VIRTUAL DISPLAY PROTECTION
            // SUMMARY: Skip auto-switch logic when targeting a virtual display (ID >= 2).
            //          This prevents the launcher from "crashing" back to physical screens
            //          when display states flicker during virtual display use (e.g., launching apps).
            // =================================================================================
            if (currentDisplayId >= 2) {
                return
            }
            // =================================================================================
            // END BLOCK: VIRTUAL DISPLAY PROTECTION
            // =================================================================================

            // ORIENTATION CHANGE: Full UI rebuild when screen dimensions change
            if (displayId == currentDisplayId) {
                val display = displayManager?.getDisplay(currentDisplayId) ?: return
                val metrics = DisplayMetrics()
                display.getRealMetrics(metrics)
                val w = metrics.widthPixels; val h = metrics.heightPixels
                if (lastKnownScreenW != 0 && lastKnownScreenH != 0 &&
                    (w != lastKnownScreenW || h != lastKnownScreenH)) {
                    val oldSuffix = orientSuffix()
                    lastKnownScreenW = w; lastKnownScreenH = h
                    uiHandler.post { refreshUIForOrientationChange(oldSuffix) }
                } else {
                    lastKnownScreenW = w; lastKnownScreenH = h
                }
            }

            // Logic to detect Fold/Unfold events monitoring Display 0 (Main)
            if (displayId == 0) {
                val display = displayManager?.getDisplay(0)
                // Only auto-switch if user hasn't manually switched recently
                val isDebounced = (System.currentTimeMillis() - lastManualSwitchTime > 2000)
                
                if (display != null && isDebounced) {
                    // Cancel any pending switch to prevent double-execution
                    if (switchRunnable != null) {
                        uiHandler.removeCallbacks(switchRunnable!!)
                    }

                    // CASE A: Phone Opened (Display 0 turned ON) -> Move to Main
                    if (display.state == Display.STATE_ON && currentDisplayId != 0) {
                        // [FIX] Dismiss visual queue before display change to prevent stuck state
                        if (isVisualQueueVisible) { hideVisualQueue(); pendingCommandId = null }
                        switchRunnable = Runnable { 
                            try { performDisplayChange(0) } catch(e: Exception) {} 
                        }
                        uiHandler.postDelayed(switchRunnable!!, 500)
                    } 
                    // CASE B: Phone Closed (Display 0 turned OFF/DOZE) -> Move to Cover (1)
                    else if (display.state != Display.STATE_ON && currentDisplayId == 0) {
                        // [FIX] Dismiss visual queue before display change to prevent stuck state
                        if (isVisualQueueVisible) { hideVisualQueue(); pendingCommandId = null }
                        switchRunnable = Runnable {
                            try { 
                                val d0 = displayManager?.getDisplay(0)
                                if (d0?.state != Display.STATE_ON) { 
                                    performDisplayChange(1) 
                                }
                            } catch(e: Exception) {}
                        }
                        uiHandler.postDelayed(switchRunnable!!, 500)
                    }
                }
            }
        }
    }
    // =================================================================================
    // END BLOCK: DISPLAY LISTENER
    // =================================================================================


    private var lastKnownScreenW = 0
    private var lastKnownScreenH = 0

    override fun orientSuffix(): String = if (lastKnownScreenW > lastKnownScreenH) "_L" else "_P"

    private var bubbleView: View? = null
    private var drawerView: View? = null
    private var debugStatusView: TextView? = null
    
    // [NEW] Persist tiling info for Watchdog recovery
    private val packageRectCache = java.util.concurrent.ConcurrentHashMap<String, Rect>()
    private val packageTaskIdCache = java.util.concurrent.ConcurrentHashMap<String, Int>()

    // [NEW] Prevent concurrent watchdog threads for the same package
    private val activeEnforcements = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()
    
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var drawerParams: WindowManager.LayoutParams

    private var isExpanded = false
    private var bubbleSizePercent = 100
    override val selectedAppsQueue = mutableListOf<MainActivity.AppInfo>()
    private val allAppsList = mutableListOf<MainActivity.AppInfo>()
    override val displayList = mutableListOf<Any>()
    
    override var activeProfileName: String? = null
    private var currentMode = MODE_SEARCH
    
    // === KEYBOARD NAVIGATION STATE ===
    override var selectedListIndex = 0
    
    // UI Focus Areas
    private val FOCUS_SEARCH = 0
    private val FOCUS_QUEUE = 1
    private val FOCUS_LIST = 2
    override var currentFocusArea = FOCUS_SEARCH
    
    // Queue Navigation State
    override var queueSelectedIndex = -1
    override var queueCommandPending: CommandDef? = null
    override var queueCommandSourceIndex = -1
    
    // OPEN_MOVE_TO / OPEN_SWAP State
    private var isOpenMoveToMode = false
    private var isOpenSwapMode = false
    private var openMoveToApp: MainActivity.AppInfo? = null  // Shared by both modes
    override var showSlotNumbersInQueue = false
    
    // Profile Save Mode State (0 = Layout + Apps, 1 = Layout Only, 2 = App Queue Only)
    private var currentProfileSaveMode = 0
    
    // [FIX] Map to track manual minimize toggles to prevent auto-refresh overwriting them
    private val manualStateOverrides = java.util.concurrent.ConcurrentHashMap<String, Long>()
    // [FULLSCREEN] Track when tiled apps are auto-minimized for a full-screen app
    private var tiledAppsAutoMinimized = false
    // [FULLSCREEN] Delayed re-check for apps that haven't finished expanding when first detected
    private var pendingFullscreenCheckRunnable: Runnable? = null
    // [FULLSCREEN] Cooldown to prevent TYPE_WINDOWS_CHANGED from re-minimizing right after restore
    private var tiledAppsRestoredAt = 0L
    // [FULLSCREEN] Track when user explicitly launched tiled apps - skip auto-minimize during cooldown
    private var lastExplicitTiledLaunchAt = 0L
    // [FIX] Track recently killed/hidden packages to prevent them from being re-added as fullscreen apps
    // Key = package name, Value = timestamp when removed. Entries older than 3s are ignored.
    private val recentlyRemovedPackages = mutableMapOf<String, Long>()
    private val REMOVED_PACKAGE_COOLDOWN_MS = 3000L



    private val TAB_ORDER = listOf(
        MODE_SEARCH, 
        MODE_LAYOUTS, 
        MODE_RESOLUTION, 
        MODE_REFRESH, 
        MODE_DPI, 
        MODE_BLACKLIST, // [FIX] Reordered to match visual icon order
        MODE_PROFILES, 
        MODE_KEYBINDS, 
        MODE_SETTINGS
    )

    private fun cycleTab(reverse: Boolean) {
        val currentIndex = TAB_ORDER.indexOf(currentMode)
        if (currentIndex == -1) return

        var nextIndex = if (reverse) currentIndex - 1 else currentIndex + 1
        
        // Loop around
        if (nextIndex >= TAB_ORDER.size) nextIndex = 0
        if (nextIndex < 0) nextIndex = TAB_ORDER.size - 1
        
        switchMode(TAB_ORDER[nextIndex])
    }
    // ================================

    override var selectedLayoutType = 2
    override var selectedResolutionIndex = 0
    private var currentOrientationMode = 0 // 0=System Default, 1=Portrait, 2=Landscape
    private var currentDpiSetting = -1
    // [FIX] State tracking to avoid redundant resolution calls/sleeps
    private var lastAppliedResIndex = -1
    private var lastAppliedDpi = -1
    override var currentFontSize = 16f
    
    private var activeCustomRects: List<Rect>? = null
    override var activeCustomLayoutName: String? = null
    
    // [DEPRECATED] Execute mode removed - instant mode is always active
    // private var killAppOnExecute = true
    private var autoRestartTrackpad = false // NEW VARIABLE
    private var targetDisplayIndex = 1 
    private var isScreenOffState = false
    private val isInstantMode = true // [DEPRECATED] Execute mode removed - always instant mode
    private var showShizukuWarning = true 
    private var useAltScreenOff = false
    
    private var isVirtualDisplayActive = false
    override var currentDrawerHeightPercent = 70
    override var currentDrawerWidthPercent = 90
    private var currentAspectRatio: String = "16_9"
    override var autoResizeEnabled = true
    override var bottomMarginPercent = 0
    private var autoAdjustMarginForIME = false
    private var imeMarginOverrideActive = false
    private var droidOsImeDetected = false // Set true when we receive IME_VISIBILITY from DroidOS IME
    private var imeRetileCooldownUntil = 0L
    private var lastAppliedEffectiveMargin = -1
    private var pendingImeRetileRunnable: Runnable? = null
    private var imeShowRetileCompleted = false // Tracks if show-retile finished (for hide logic)




    private fun effectiveBottomMarginPercent(): Int {
        return if (autoAdjustMarginForIME && !imeMarginOverrideActive) 0 else bottomMarginPercent
    }






    override var topMarginPercent = 0
    
    override var reorderSelectionIndex = -1
    private var isReorderDragEnabled = true
    override var isReorderTapEnabled = true
    
    // Interface implementation - handlerContext and handlerPackageManager
    override val handlerContext: Context get() = this
    override val handlerPackageManager: PackageManager get() = packageManager
    private val PACKAGE_TRACKPAD = "com.katsuyamaki.DroidOSTrackpadKeyboard"
    
    private var shellService: IShellService? = null
    private var isBound = false
    lateinit var uiHandler: Handler // Declare uiHandler here
    override fun onCreate() {
        super.onCreate()
        
        // Register ADB Receiver
        val filter = IntentFilter().apply {
            addAction("com.katsuyamaki.DroidOSTrackpadKeyboard.MOVE_TO_DISPLAY")
        }
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(launcherReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(launcherReceiver, filter)
        }

        uiHandler = Handler(Looper.getMainLooper())
    }

    private val shizukuBinderListener = Shizuku.OnBinderReceivedListener { if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() }
    private val shizukuPermissionListener = Shizuku.OnRequestPermissionResultListener { _, grantResult -> if (grantResult == PackageManager.PERMISSION_GRANTED) bindShizuku() }

    private val commandReceiver = object : BroadcastReceiver() {        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == ACTION_OPEN_DRAWER) { 
                if (isScreenOffState) wakeUp() else if (!isExpanded) toggleDrawer() 
            } 
            else if (action == ACTION_UPDATE_ICON) { 
                updateBubbleIcon()
                if (currentMode == MODE_SETTINGS) switchMode(MODE_SETTINGS) 
            }
            else if (action == ACTION_CYCLE_DISPLAY) {
                switchDisplay()
            }
            else if (action == Intent.ACTION_SCREEN_ON) {
                if (isScreenOffState) {
                    wakeUp()
                }
                // [FIX] Dismiss visual queue on screen on if stuck from screen off
                if (isVisualQueueVisible) {
                    hideVisualQueue()
                    pendingCommandId = null
                }
} else if (action == ACTION_TOGGLE_VIRTUAL) {
                toggleVirtualDisplay()
            } else if (action == "KEEP_SCREEN_ON" || action == "${packageName}.KEEP_SCREEN_ON") {
                val enable = intent?.getBooleanExtra("ENABLE", true) ?: true
                setKeepScreenOn(enable)
                safeToast(if (enable) "Screen: Always On" else "Screen: Normal Timeout")
            } else if (action == "com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER") {
                // [FIX] Copy intent extras to prevent recycling after onReceive returns
                intent?.let {
                    val cmd = it.getStringExtra("COMMAND") ?: return@let
                    val copy = Intent().putExtra("COMMAND", cmd)
                    if (it.hasExtra("INDEX")) copy.putExtra("INDEX", it.getIntExtra("INDEX", -1))
                    if (it.hasExtra("INDEX_A")) copy.putExtra("INDEX_A", it.getIntExtra("INDEX_A", -1))
                    if (it.hasExtra("INDEX_B")) copy.putExtra("INDEX_B", it.getIntExtra("INDEX_B", -1))
                    queueWindowManagerCommand(copy)
                }
} else if (action == "com.katsuyamaki.DroidOSLauncher.REQUEST_CUSTOM_MOD_SYNC") {
                // Trackpad is asking for the key, send it
                if (customModKey != 0) {
                    sendCustomModToTrackpad()
                }
            } else if (action == "com.katsuyamaki.DroidOSLauncher.REMOTE_KEY") {
                val keyCode = intent?.getIntExtra("keyCode", 0) ?: 0
                val metaState = intent?.getIntExtra("metaState", 0) ?: 0
                if (keyCode != 0) {
                    // Simulate the event passing through the same logic as hardware keys
                    handleRemoteKeyEvent(keyCode, metaState)
                }
            } else if (action == "com.katsuyamaki.DroidOSLauncher.REQUEST_KEYBINDS") {
                broadcastKeybindsToKeyboard()
            } else if (action == "com.katsuyamaki.DroidOSLauncher.IME_VISIBILITY") {
                // [FIX] Mark DroidOS IME as detected. This proves it's active even on cover screen
                // where system settings may not reflect the actual IME in use.
                if (!droidOsImeDetected) {
                    droidOsImeDetected = true
                    AppPreferences.setDroidOsImeDetected(this@FloatingLauncherService, true)
                }
                if (autoAdjustMarginForIME) {
                    val visible = intent?.getBooleanExtra("VISIBLE", false) ?: false
                    val isTiled = intent?.getBooleanExtra("IS_TILED", true) ?: true
                    val forceRetile = intent?.getBooleanExtra("FORCE_RETILE", false) ?: false

                    // [FIX] Ignore isTiled=false broadcasts - they come from OverlayService
                    if (!isTiled && !forceRetile) {
                        return@onReceive
                    }

                    // [FIX] Debounced show: delay retile 350ms on VISIBLE=true.
                    // If VISIBLE=false arrives before timer fires, cancel it.
                    // Prevents focus-loss loops on Zillow/Google Voice.
                    if (visible) {
                        pendingImeRetileRunnable?.let { uiHandler.removeCallbacks(it) }
                        imeMarginOverrideActive = true
                        val runnable = Runnable {
                            val newEffective = effectiveBottomMarginPercent()
                            if (newEffective != lastAppliedEffectiveMargin) {
                                lastAppliedEffectiveMargin = newEffective
                                imeRetileCooldownUntil = System.currentTimeMillis() + 500
                                setupVisualQueue()
                                retileExistingWindows()
                            }
                            pendingImeRetileRunnable = null
                            imeShowRetileCompleted = true
                        }
                        pendingImeRetileRunnable = runnable
                        imeShowRetileCompleted = false
                        uiHandler.postDelayed(runnable, 350)
                    } else {
                        // Keyboard hiding - cancel pending show-retile if exists
                        if (pendingImeRetileRunnable != null) {
                            uiHandler.removeCallbacks(pendingImeRetileRunnable!!)
                            pendingImeRetileRunnable = null
                            imeMarginOverrideActive = false
                            return@onReceive
                        }
                        imeMarginOverrideActive = false
                        val newEffective = effectiveBottomMarginPercent()
                        val shouldRetile = (newEffective != lastAppliedEffectiveMargin) || forceRetile
                        if (shouldRetile && imeShowRetileCompleted) {
                            val now = System.currentTimeMillis()
                            if (now >= imeRetileCooldownUntil) {
                                lastAppliedEffectiveMargin = newEffective
                                imeRetileCooldownUntil = now + 500
                                setupVisualQueue()
                                retileExistingWindows()
                            }
                        }
                        imeShowRetileCompleted = false
                    }
                }
            } else if (action == "com.katsuyamaki.DroidOSLauncher.FULLSCREEN_APP_OPENING") {
                // [FULLSCREEN] External request to minimize tiled apps (e.g. keyboard opening Settings).
                // Freeform/resized tiled windows float above regular fullscreen activities,
                // so the accessibility-based auto-minimize never triggers — the fullscreen app
                // opens behind the tiled windows and never gains focus.
                // Use MINIMIZE_ALL command through queue for consistency and race condition safety.
                if (selectedAppsQueue.any { !it.isMinimized } && !tiledAppsAutoMinimized) {
                    tiledAppsAutoMinimized = true
                    val minimizeIntent = Intent().putExtra("COMMAND", "MINIMIZE_ALL")
                    queueWindowManagerCommand(minimizeIntent)
                }
            } else if (action == "com.katsuyamaki.DroidOSLauncher.SET_AUTO_ADJUST_MARGIN") {
                val enabled = intent?.getBooleanExtra("ENABLED", false) ?: false
                autoAdjustMarginForIME = enabled
                AppPreferences.setAutoAdjustMarginForIME(this@FloatingLauncherService, enabled)
                AppPreferences.setAutoAdjustMarginForIME(this@FloatingLauncherService, enabled, orientSuffix())
                if (!enabled) imeMarginOverrideActive = false
            } else if (action == "com.katsuyamaki.DroidOSLauncher.SET_MARGIN_BOTTOM") {
                val percent = intent?.getIntExtra("PERCENT", 0) ?: 0
                bottomMarginPercent = percent
                AppPreferences.setBottomMarginPercent(this@FloatingLauncherService, currentDisplayId, percent)
                AppPreferences.setBottomMarginPercent(this@FloatingLauncherService, currentDisplayId, percent, orientSuffix())
                setupVisualQueue() // Recalc HUD pos
                // [FIX] Always retile when margin changes while IME is visible
                // This ensures slider changes in DockIME take effect immediately
                if (autoAdjustMarginForIME && imeMarginOverrideActive) {
                    retileExistingWindows()
                } else if (isInstantMode) {
                    applyLayoutImmediate()
                }
                
                // Update UI if in settings mode - TARGETED UPDATE
                if (currentMode == MODE_SETTINGS) {
                    uiHandler.post {
                        val adapter = drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter
                        if (adapter != null) {
                            for (i in displayList.indices) {
                                val item = displayList[i]
                                if (item is MarginOption && item.type == 1) { // 1 = Bottom Margin
                                    // Replace immutable data object
                                    displayList[i] = MarginOption(1, percent)
                                    adapter.notifyItemChanged(i)
                                    break
                                }
                            }
                        }
                    }
                }
                safeToast("Margin Updated: $percent%")
            }
        }
    }


    override fun broadcastKeybindsToKeyboard() {
        val binds = ArrayList<String>()
        // Iterate AVAILABLE_COMMANDS to ensure defaults are included
        for (cmd in AVAILABLE_COMMANDS) {
            val bind = AppPreferences.getKeybind(this, cmd.id)
            if (bind.second != 0) { // Valid keyCode
                // Format: "modifier|keyCode|argCount"
                // argCount tells the keyboard if this command needs input (opens visual queue)
                binds.add("${bind.first}|${bind.second}|${cmd.argCount}")
            }
        }

        val intent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.UPDATE_KEYBINDS")
        intent.setPackage("com.katsuyamaki.DroidOSTrackpadKeyboard")
        intent.putStringArrayListExtra("KEYBINDS", binds)
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        sendBroadcast(intent)
        Log.e(TAG, "Broadcasted ${binds.size} keybinds to Keyboard (inc. defaults)")
    }
    // === SWIPE CALLBACK - START ===
    // Handles swipe gestures for various modes including blacklist
    private val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun getMovementFlags(r: RecyclerView, v: RecyclerView.ViewHolder): Int {
            val pos = v.adapterPosition; if (pos == RecyclerView.NO_POSITION || pos >= displayList.size) return 0
            val item = displayList[pos]
            val isSwipeable = when (currentMode) {
                MODE_LAYOUTS -> (item is LayoutOption && item.type == LAYOUT_CUSTOM_DYNAMIC && item.isCustomSaved)
                MODE_RESOLUTION -> (item is ResolutionOption && item.index >= 100)
                MODE_PROFILES -> (item is ProfileOption && !item.isCurrent)
                MODE_SEARCH -> true
                MODE_BLACKLIST -> true
                else -> false
            }
            return if (isSwipeable) makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) else 0
        }
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
        override fun onSwiped(v: RecyclerView.ViewHolder, direction: Int) {
            val pos = v.adapterPosition; if (pos == RecyclerView.NO_POSITION) return
            dismissKeyboardAndRestore()
            if (currentMode == MODE_PROFILES) { val item = displayList.getOrNull(pos) as? ProfileOption ?: return; AppPreferences.deleteProfile(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_PROFILES); return }
            if (currentMode == MODE_LAYOUTS) { val item = displayList.getOrNull(pos) as? LayoutOption ?: return; AppPreferences.deleteCustomLayout(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_LAYOUTS); return }
            if (currentMode == MODE_RESOLUTION) { val item = displayList.getOrNull(pos) as? ResolutionOption ?: return; AppPreferences.deleteCustomResolution(this@FloatingLauncherService, item.name); safeToast("Deleted ${item.name}"); switchMode(MODE_RESOLUTION); return }
            if (currentMode == MODE_SEARCH) {
                val item = displayList.getOrNull(pos) as? MainActivity.AppInfo ?: return
                if (item.packageName == PACKAGE_BLANK) { (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos); return }
                // Left swipe = blacklist app
                if (direction == ItemTouchHelper.LEFT) {
                    val identifier = item.getIdentifier()
                    AppPreferences.addToBlacklist(this@FloatingLauncherService, identifier)
                    safeToast("${item.label} blacklisted")
                    loadInstalledApps()
                    refreshSearchList()
                }
                // Right swipe = toggle favorite (legacy behavior)
                else if (direction == ItemTouchHelper.RIGHT) {
                    toggleFavorite(item)
                    refreshSearchList()
                }
            }
            if (currentMode == MODE_BLACKLIST) {
                val item = displayList.getOrNull(pos) as? MainActivity.AppInfo ?: return
                // Left swipe = remove from blacklist
                if (direction == ItemTouchHelper.LEFT) {
                    val identifier = item.getIdentifier()
                    AppPreferences.removeFromBlacklist(this@FloatingLauncherService, identifier)
                    safeToast("${item.label} removed from blacklist")
                    loadInstalledApps()
                    loadBlacklistedApps()
                }
            }
        }
    }
    // === SWIPE CALLBACK - END ===

    private val selectedAppsDragCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
        override fun isLongPressDragEnabled(): Boolean = isReorderDragEnabled
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean { Collections.swap(selectedAppsQueue, v.adapterPosition, t.adapterPosition); r.adapter?.notifyItemMoved(v.adapterPosition, t.adapterPosition); return true }
        override fun onSwiped(v: RecyclerView.ViewHolder, direction: Int) { 
            dismissKeyboardAndRestore()
            val pos = v.adapterPosition
            if (pos != RecyclerView.NO_POSITION) { 
                val app = selectedAppsQueue[pos]
                if (app.packageName != PACKAGE_BLANK) { 
                    val basePkg = app.getBasePackage()
                    // Record killed package to prevent re-adding as fullscreen app
                    recentlyRemovedPackages[basePkg] = System.currentTimeMillis()
                    removeFromFocusHistory(basePkg) // Clean up history
                    Thread { try { shellService?.forceStop(basePkg) } catch(e: Exception) {} }.start()
                }
                selectedAppsQueue.removeAt(pos)
                if (reorderSelectionIndex == pos) reorderSelectionIndex = -1
                else if (reorderSelectionIndex > pos) reorderSelectionIndex--
                refreshQueueAndLayout("Closed ${app.label}")
            } 
        }
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) { super.clearView(recyclerView, viewHolder); val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveLastQueue(this@FloatingLauncherService, pkgs); if (isInstantMode) applyLayoutImmediate() }
    }

    // === SWIPE DETECTOR - START ===
    // Detects horizontal swipe gestures for blacklist/favorite actions
    // Left swipe = blacklist, Long press = favorite
    private inner class SwipeDetector : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null) return false

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX < 0) {
                        return true // Left swipe detected
                    }
                }
            }
            return false
        }
    }
    // === SWIPE DETECTOR - END ===

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            shellService = IShellService.Stub.asInterface(binder)
            isBound = true
            updateExecuteButtonColor(true)
            updateBubbleIcon()
            safeToast("Shizuku Connected")

            if (pendingHeadlessRetile) {
                uiHandler.postDelayed(headlessRetileRunnable, 200)
            }
            if (wmCommandQueue.isNotEmpty()) {
                scheduleWindowManagerQueueFlush(0L)
            }

            // NEW: Auto-Restart Trackpad if enabled
            if (autoRestartTrackpad) {
                uiHandler.postDelayed({ restartTrackpad() }, 1000) // Delay to ensure stability
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) { shellService = null; isBound = false; updateExecuteButtonColor(false); updateBubbleIcon() }
    }


    // === FUNCTION: restartTrackpad - START ===
    // [FIX] Uses Settings.Secure to read permissions (avoids build error) and Shell to write them




    private fun restartTrackpad() {
        safeToast("Restarting Trackpad...")
        Thread {
            try {
                // 1. Save Target Display
                val targetId = currentDisplayId
                shellService?.runCommand("settings put global droidos_target_display $targetId")
                
                val pkgName = "com.katsuyamaki.DroidOSTrackpadKeyboard"
                val legacyPkg = "com.katsuyamaki.DroidOSTrackpadKeyboard"
                val serviceComponent = "$pkgName/com.katsuyamaki.DroidOSTrackpadKeyboard.OverlayService"
                
                // 2. DISABLE SERVICE (Reset Crash Backoff)
                val currentList = android.provider.Settings.Secure.getString(
                    contentResolver, 
                    android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                ) ?: ""
                
                if (currentList.contains("OverlayService")) {
                    val newList = currentList.replace(":$serviceComponent", "")
                                           .replace("$serviceComponent:", "")
                                           .replace(serviceComponent, "")
                    shellService?.runCommand("settings put secure enabled_accessibility_services $newList")
                }

                // 3. FORCE STOP
                shellService?.runCommand("am force-stop $pkgName")
                shellService?.runCommand("am force-stop $legacyPkg")
                
                // [FIX] INCREASED WAIT: 500ms -> 2000ms
                // Give the system 2 full seconds to clean up the dead process.
                Thread.sleep(2000)

                // 4. RE-ENABLE SERVICE
                val cleanList = android.provider.Settings.Secure.getString(
                    contentResolver, 
                    android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                ) ?: ""

                val enableList = if (cleanList.isEmpty() || cleanList == "null") {
                    serviceComponent
                } else {
                    "$cleanList:$serviceComponent"
                }
                
                shellService?.runCommand("settings put secure enabled_accessibility_services $enableList")
                shellService?.runCommand("settings put secure accessibility_enabled 1")

                // [FIX] WAKE UP DELAY: 500ms -> 1500ms
                // Wait another 1.5s before sending the Wake-Up command
                Thread.sleep(1500) 
                
                val serviceCmd = "am start-foreground-service -n $serviceComponent --ez force_start true"
                shellService?.runCommand(serviceCmd)
                
                val legacyCmd = "am start-foreground-service -n $legacyPkg/com.katsuyamaki.DroidOSTrackpadKeyboard.OverlayService --ez force_start true"
                shellService?.runCommand(legacyCmd)

            } catch (e: Exception) {
                Log.e(TAG, "Shell Restart Failed", e)
                
                // [FIX] ADD FALLBACK DELAY
                // If the shell failed, we still want to wait before blindly launching the app
                Thread.sleep(2000)
                
                uiHandler.post { 
                    safeToast("Restart Failed: ${e.message}")
                    launchTrackpad() 
                }
            }
        }.start()
    }






    // === FUNCTION: restartTrackpad - END ===



// =================================================================================
    // FUNCTION: setKeepScreenOn
    // SUMMARY: Acquires or releases a wake lock to prevent screen timeout.
    //          Call with true when AR glasses/virtual display is active.
    //          Call with false when returning to normal use.
    // =================================================================================
    private fun setKeepScreenOn(enable: Boolean) {
        if (enable == keepScreenOnEnabled) return
        
        try {
            if (enable) {
                if (wakeLock == null) {
                    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                    wakeLock = powerManager.newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                        "DroidOSLauncher:KeepScreenOn"
                    )
                }
                wakeLock?.acquire(60 * 60 * 1000L) // 1 hour max, will re-acquire as needed
                keepScreenOnEnabled = true
                Log.i(TAG, "Wake lock ACQUIRED - screen will stay on")
            } else {
                wakeLock?.let {
                    if (it.isHeld) {
                        it.release()
                    }
                }
                keepScreenOnEnabled = false
                Log.i(TAG, "Wake lock RELEASED - normal screen timeout")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Wake lock error: ${e.message}")
        }
    }
    // =================================================================================
    // END BLOCK: setKeepScreenOn
    // =================================================================================




    private fun launchShizuku() { try { val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api"); if (intent != null) { intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(intent) } else { safeToast("Shizuku app not found") } } catch(e: Exception) { safeToast("Failed to launch Shizuku") } }

    private fun handleSoftKeyInput(event: AccessibilityEvent) {
        // Guard: Only run if feature enabled and custom key set
        if (!isSoftKeyboardSupport || customModKey == 0) return
        
        // Guard: Only care about text additions
        if (event.addedCount < 1) return
        
        val textList = event.text
        if (textList.isEmpty()) return
        
        val sequence = textList[0].toString()
        if (sequence.isEmpty()) return

        // Check the last typed character
        val typedChar = sequence.last()
        val typedCode = getKeyCodeFromChar(typedChar)

        
        if (typedCode == 0) return

        // 1. IS IT THE MODIFIER?
        if (typedCode == customModKey) {
            isCustomModLatched = true
            // No timer - stays latched until next key press
            
            // Delete the character
            Thread { 
                shellService?.injectKey(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_DOWN, 0, currentDisplayId, -1)
                Thread.sleep(10)
                shellService?.injectKey(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_UP, 0, currentDisplayId, -1)
            }.start()
            
            safeToast("Custom Mod Active (Soft)")
            return
        }

        // 2. IS IT A COMMAND (WHILE LATCHED)?
        if (isCustomModLatched) {
            // Scan commands for match
            for (cmd in AVAILABLE_COMMANDS) {
                val bind = AppPreferences.getKeybind(this, cmd.id)
                // If binding uses Custom Mod AND matches typed key
                if (bind.first == MOD_CUSTOM && bind.second == typedCode) {
                    
                    // Delete character
                    Thread { 
                        shellService?.injectKey(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_DOWN, 0, currentDisplayId, -1)
                        Thread.sleep(10)
                        shellService?.injectKey(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_UP, 0, currentDisplayId, -1)
                    }.start()

                    triggerCommand(cmd)

                    isCustomModLatched = false
                    return
                }
            }
        }
        
        // 3. NUMBER INPUT FOR HUD
        if (pendingCommandId != null) {
            if (Character.isDigit(typedChar)) {
                val num = typedChar - '0'
                // Delete character
                Thread {
                    shellService?.injectKey(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_DOWN, 0, currentDisplayId, -1)
                    Thread.sleep(10)
                    shellService?.injectKey(KeyEvent.KEYCODE_DEL, KeyEvent.ACTION_UP, 0, currentDisplayId, -1)
                }.start()

                handleCommandInput(num)
                return
            } else {
                // Non-digit typed -> Abort
                abortCommandMode()
                // Let the character stay - if they typed "a" to cancel, they probably want "a"
                return
            }
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return false

        val keyCode = event.keyCode
        val metaState = event.metaState

        // CHECK CUSTOM MODIFIER
        if (customModKey != 0 && keyCode == customModKey) {
            isCustomModLatched = true
            // No timer - stays latched until next key press // 5 second latch
            safeToast("Custom Mod Active...")
            return true // Consume the key
        }

        // DEBUG: Log every key press
        val keyName = KeyEvent.keyCodeToString(keyCode)
        // WORKAROUND: KeyEvent.metaStateToString may be unresolved in some environments.
        // Using toString() directly on the integer for debug output.
        val metaStr = if (metaState != 0) "Meta($metaState)" else "None"

        // [FIX] Name editor guard: while editing, don't route keys into command/hotkey paths.
        val focusedNameEditor = getFocusedNameEditor()
        if (focusedNameEditor != null) {
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                focusedNameEditor.onEditorAction(EditorInfo.IME_ACTION_DONE)
                return true
            }
            // Let EditText/system handle normal typing/backspace/navigation.
            return false
        }

        // 1. INPUT MODE (Entering Numbers or Arrow Navigation)
        if (pendingCommandId != null) {
            if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                abortCommandMode()
                return true
            }

            // Arrow key navigation in visual queue (left/up = prev, right/down = next)
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (vqCursorIndex > 0) {
                    vqCursorIndex--
                    showVisualQueue(visualQueueView?.findViewById<TextView>(R.id.visual_queue_prompt)?.text?.toString() ?: "", vqCursorIndex)
                }
                return true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (vqCursorIndex < selectedAppsQueue.size - 1) {
                    vqCursorIndex++
                    showVisualQueue(visualQueueView?.findViewById<TextView>(R.id.visual_queue_prompt)?.text?.toString() ?: "", vqCursorIndex)
                }
                return true
            }
            // Space or Enter confirms the arrow-key selection
            if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                handleCommandInput(vqCursorIndex + 1) // +1 because handleCommandInput expects 1-based
                return true
            }

            // Handle Numbers (Row 0-9 and Numpad 0-9)
            val num = keyEventToNumber(keyCode)
            if (num != -1) {
                handleCommandInput(num)
                return true
            }

            // If any other key is pressed while HUD is open, CANCEL IT.
            abortCommandMode()
            return true
        }

        // 2. TRIGGER MODE (Detecting Hotkeys)
        // Check if this key matches any stored bind
        var commandTriggered = false

        for (cmd in AVAILABLE_COMMANDS) {
            val bind = AppPreferences.getKeybind(this, cmd.id)

            // Check if key code matches
            if (bind.second != 0 && bind.second == keyCode) {

                if (checkModifiers(metaState, bind.first)) {
                    triggerCommand(cmd)
                    commandTriggered = true
                    break
                } else {
                }
            }
        }

        // AUTO-RESET LATCH: If we were latched, any key press (valid command or not) consumes the latch.
        if (isCustomModLatched) {
            isCustomModLatched = false
            safeToast(if (commandTriggered) "Command Executed" else "Command Cancelled")
            return true
        }

        if (commandTriggered) return true

        return super.onKeyEvent(event)
    }

    // Shared logic for both Hardware Keys (onKeyEvent) and Virtual Remote Keys (Broadcast)
    private fun handleRemoteKeyEvent(keyCode: Int, metaState: Int) {
        val keyName = KeyEvent.keyCodeToString(keyCode)

        // CHECK CUSTOM MODIFIER (Remote/Broadcast)
        if (customModKey != 0) {

            if (keyCode == customModKey) {
                isCustomModLatched = true
                // No timer - stays latched until next key press
                safeToast("Custom Mod Active (Remote)")
                return
            }
        }

        val metaStr = if (metaState != 0) "Meta($metaState)" else "None"

        // [FIX] Global name editor guard for REMOTE_KEY: never route typed keys into
        // launcher navigation/hotkey handlers while renaming.
        val focusedNameEditor = getFocusedNameEditor()
        if (focusedNameEditor != null) {
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                focusedNameEditor.onEditorAction(EditorInfo.IME_ACTION_DONE)
            }
            return
        }

        // 0. DRAWER SEARCH NAVIGATION (via REMOTE_KEY - transition to queue/list)
        if (isExpanded && currentFocusArea == FOCUS_SEARCH) {
            val now = System.currentTimeMillis()
            if (now - lastQueueNavTime < 80) return
            lastQueueNavTime = now
            
            // [FIX] Skip Enter/DPAD handling when editing names - let EditText handle cursor/text
            if ((isProfileNameEditMode || isLayoutNameEditMode) &&
                (keyCode == KeyEvent.KEYCODE_ENTER ||
                 keyCode == KeyEvent.KEYCODE_DEL ||
                 keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                 keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
                 keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                 keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                return
            }
            
            // [FIX] ENTER: Activate first filtered result (works for all menu types)
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (displayList.isNotEmpty()) {
                    val item = displayList[0]
                    when (item) {
                        is MainActivity.AppInfo -> addToSelection(item)
                        is LayoutOption -> selectLayout(item)
                        is ActionOption -> { dismissKeyboardAndRestore(); item.action() }
                        is ToggleOption -> { item.isEnabled = !item.isEnabled; item.onToggle(item.isEnabled); drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() }
                        is RefreshItemOption -> if (item.isAvailable) { dismissKeyboardAndRestore(); applyRefreshRate(item.targetRate) }
                        is ProfileOption -> loadProfile(item.name)
                        is ResolutionOption -> { dismissKeyboardAndRestore(); Thread { try { shellService?.runCommand(item.command) } catch(e: Exception){} }.start(); safeToast("Applied: ${item.name}") }
                    }
                    return
                }
            }
            
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                val selectedRecycler = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)
                if (selectedAppsQueue.isNotEmpty()) {
                    currentFocusArea = FOCUS_QUEUE
                    queueSelectedIndex = 0
                    // [FIX] Tell keyboard to capture all keys for queue navigation
                    val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                    captureIntent.setPackage(PACKAGE_TRACKPAD)
                    captureIntent.putExtra("CAPTURE", true)
                    sendBroadcast(captureIntent)
                    uiHandler.post {
                        selectedRecycler?.adapter?.notifyDataSetChanged()
                        debugStatusView?.visibility = View.VISIBLE
                        if (!isOpenMoveToMode && !isOpenSwapMode) {
                            debugStatusView?.text = "Queue Navigation: Use Arrows / Hotkeys"
                        }
                    }
                } else {
                    currentFocusArea = FOCUS_LIST
                    val mainRecycler = drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)
                    uiHandler.post {
                        if (displayList.isNotEmpty()) {
                            if (selectedListIndex >= displayList.size) selectedListIndex = 0
                            mainRecycler?.adapter?.notifyItemChanged(selectedListIndex)
                            mainRecycler?.scrollToPosition(selectedListIndex)
                        }
                    }
                }
                return
            }
        }

        // 0.1 DRAWER QUEUE NAVIGATION (via REMOTE_KEY for soft keyboard support on all displays)
        if (isExpanded && currentFocusArea == FOCUS_QUEUE) {
            // [FIX] Dedup to prevent double-processing from View listener + REMOTE_KEY
            val now = System.currentTimeMillis()
            if (now - lastQueueNavTime < 80) {
                return
            }
            lastQueueNavTime = now
            
            val selectedRecycler = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (queueSelectedIndex > 0) {
                        val old = queueSelectedIndex
                        queueSelectedIndex--
                        uiHandler.post {
                            selectedRecycler?.adapter?.notifyItemChanged(old)
                            selectedRecycler?.adapter?.notifyItemChanged(queueSelectedIndex)
                            selectedRecycler?.scrollToPosition(queueSelectedIndex)
                        }
                    }
                    return
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    if (queueSelectedIndex < selectedAppsQueue.size - 1) {
                        val old = queueSelectedIndex
                        queueSelectedIndex++
                        uiHandler.post {
                            selectedRecycler?.adapter?.notifyItemChanged(old)
                            selectedRecycler?.adapter?.notifyItemChanged(queueSelectedIndex)
                            selectedRecycler?.scrollToPosition(queueSelectedIndex)
                        }
                    }
                    return
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    // Block navigation away from queue during OPEN_MOVE_TO/OPEN_SWAP slot selection
                    if ((isOpenMoveToMode || isOpenSwapMode) && openMoveToApp != null) {
                        return
                    }
                    
                    currentFocusArea = FOCUS_SEARCH
                    queueSelectedIndex = -1
                    // [FIX] Release keyboard capture when leaving queue
                    val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                    captureIntent.setPackage(PACKAGE_TRACKPAD)
                    captureIntent.putExtra("CAPTURE", false)
                    sendBroadcast(captureIntent)
                    uiHandler.post {
                        selectedRecycler?.adapter?.notifyDataSetChanged()
                        if (!isOpenMoveToMode && !isOpenSwapMode) debugStatusView?.visibility = View.GONE
                        drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.requestFocus()
                    }
                    return
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    // Block navigation away from queue during OPEN_MOVE_TO/OPEN_SWAP slot selection
                    if ((isOpenMoveToMode || isOpenSwapMode) && openMoveToApp != null) {
                        return
                    }
                    
                    currentFocusArea = FOCUS_LIST
                    queueSelectedIndex = -1
                    // [FIX] Release keyboard capture when leaving queue
                    val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                    captureIntent.setPackage(PACKAGE_TRACKPAD)
                    captureIntent.putExtra("CAPTURE", false)
                    sendBroadcast(captureIntent)
                    uiHandler.post {
                        selectedRecycler?.adapter?.notifyDataSetChanged()
                        if (!isOpenMoveToMode && !isOpenSwapMode) debugStatusView?.visibility = View.GONE
                        val mainRecycler = drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)
                        if (displayList.isNotEmpty()) {
                            if (selectedListIndex >= displayList.size) selectedListIndex = 0
                            mainRecycler?.adapter?.notifyItemChanged(selectedListIndex)
                            mainRecycler?.scrollToPosition(selectedListIndex)
                        }
                    }
                    return
                }
                KeyEvent.KEYCODE_ENTER -> {
                    // OPEN_MOVE_TO/OPEN_SWAP: Enter confirms current selection
                    if (isOpenMoveToMode && openMoveToApp != null && queueSelectedIndex >= 0) {
                        executeOpenMoveTo(queueSelectedIndex + 1)
                        return
                    }
                    if (isOpenSwapMode && openMoveToApp != null && queueSelectedIndex >= 0) {
                        executeOpenSwap(queueSelectedIndex + 1)
                        return
                    }
                    
                    // [FIX] Complete pending 2-step command (like swap) if active
                    if (queueCommandPending != null) {
                        val intent = Intent()
                            .putExtra("COMMAND", queueCommandPending!!.id)
                            .putExtra("INDEX_A", queueCommandSourceIndex + 1)
                            .putExtra("INDEX_B", queueSelectedIndex + 1)
                        queueWindowManagerCommand(intent)
                        queueCommandPending = null
                        queueCommandSourceIndex = -1
                        uiHandler.post {
                            debugStatusView?.text = "Command Executed"
                            selectedRecycler?.adapter?.notifyDataSetChanged()
                        }
                    } else {
                        // [FIX] Determine command by queue position (active apps first, then minimized)
                        // This matches how K shortcut works - doesn't rely on isMinimized flag
                        if (queueSelectedIndex in selectedAppsQueue.indices) {
                            val activeCount = selectedAppsQueue.count { !it.isMinimized }
                            val cmd = if (queueSelectedIndex >= activeCount) "UNMINIMIZE" else "MINIMIZE"
                            val intent = Intent().putExtra("COMMAND", cmd).putExtra("INDEX", queueSelectedIndex + 1)
                            queueWindowManagerCommand(intent)
                        }
                    }
                    return
                }
                KeyEvent.KEYCODE_ESCAPE -> {
                    // OPEN_MOVE_TO/OPEN_SWAP: Cancel mode
                    if (isOpenMoveToMode || isOpenSwapMode) {
                        cancelOpenMoveToMode()
                        safeToast("Open & Move To cancelled")
                        // Release keyboard capture
                        val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                        captureIntent.setPackage(PACKAGE_TRACKPAD)
                        captureIntent.putExtra("CAPTURE", false)
                        sendBroadcast(captureIntent)
                        return
                    }
                    
                    currentFocusArea = FOCUS_SEARCH
                    queueSelectedIndex = -1
                    // [FIX] Release keyboard capture when leaving queue
                    val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                    captureIntent.setPackage(PACKAGE_TRACKPAD)
                    captureIntent.putExtra("CAPTURE", false)
                    sendBroadcast(captureIntent)
                    uiHandler.post {
                        selectedRecycler?.adapter?.notifyDataSetChanged()
                        if (!isOpenMoveToMode && !isOpenSwapMode) debugStatusView?.visibility = View.GONE
                    }
                    return
                }
                KeyEvent.KEYCODE_SPACE -> {
                    // OPEN_MOVE_TO/OPEN_SWAP: Space confirms current selection
                    if (isOpenMoveToMode && openMoveToApp != null && queueSelectedIndex >= 0) {
                        executeOpenMoveTo(queueSelectedIndex + 1)
                        return
                    }
                    if (isOpenSwapMode && openMoveToApp != null && queueSelectedIndex >= 0) {
                        executeOpenSwap(queueSelectedIndex + 1)
                        return
                    }
                    
                    // [FIX] SPACE: Toggle Internal Focus (Green Underline) - same as hardware keyboard
                    if (queueCommandPending == null && queueSelectedIndex in selectedAppsQueue.indices) {
                        val app = selectedAppsQueue[queueSelectedIndex]
                        if (app.packageName != PACKAGE_BLANK) {
                            val pkg = app.packageName
                            val isGemini = pkg == "com.google.android.apps.bard"
                            val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"
                            val isActive = (activePackageName == pkg) || (isGemini && activeIsGoogle)
                            if (isActive) {
                                activePackageName = null
                            } else {
                                if (activePackageName != null) lastValidPackageName = activePackageName
                                activePackageName = pkg
                            }
                            uiHandler.post { updateAllUIs() }
                        }
                    }
                    return
                }
                else -> {
                    // OPEN_MOVE_TO/OPEN_SWAP: Handle number keys for slot selection
                    if ((isOpenMoveToMode || isOpenSwapMode) && openMoveToApp != null) {
                        val num = when (keyCode) {
                            KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_NUMPAD_1 -> 1
                            KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_NUMPAD_2 -> 2
                            KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_NUMPAD_3 -> 3
                            KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_NUMPAD_4 -> 4
                            KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_NUMPAD_5 -> 5
                            KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_NUMPAD_6 -> 6
                            KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_NUMPAD_7 -> 7
                            KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_NUMPAD_8 -> 8
                            KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_NUMPAD_9 -> 9
                            else -> -1
                        }
                        if (num in 1..(selectedAppsQueue.size + 1)) {
                            if (isOpenMoveToMode) executeOpenMoveTo(num) else executeOpenSwap(num)
                            return
                        }
                    }
                    
                    for (cmd in AVAILABLE_COMMANDS) {
                        val bind = AppPreferences.getKeybind(this, cmd.id)
                        if (bind.second == keyCode && keyCode != KeyEvent.KEYCODE_SPACE) {
                            if (cmd.argCount == 2) {
                                queueCommandPending = cmd
                                queueCommandSourceIndex = queueSelectedIndex
                                uiHandler.post {
                                    debugStatusView?.text = "${cmd.label}: Select Target & Press Enter"
                                    selectedRecycler?.adapter?.notifyDataSetChanged()
                                }
                            } else {
                                val intent = Intent().putExtra("COMMAND", cmd.id).putExtra("INDEX", queueSelectedIndex + 1)
                                queueWindowManagerCommand(intent)
                            }
                            return
                        }
                    }
                }
            }
        }

        // 0.5 DRAWER LIST NAVIGATION (via REMOTE_KEY for soft keyboard support on all displays)
        if (isExpanded && currentFocusArea == FOCUS_LIST) {
            val now = System.currentTimeMillis()
            if (now - lastQueueNavTime < 80) return
            lastQueueNavTime = now
            val mainRecycler = drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)
            val selectedRecycler = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)

            val routeCtx = DrawerKeyRouter.Context(
                source = DrawerKeyRouter.Source.REMOTE,
                area = currentRouterArea(),
                isExpanded = isExpanded,
                isNameEditorActive = getFocusedNameEditor() != null,
                pendingCommandActive = pendingCommandId != null,
                selectedListIndex = selectedListIndex,
                queueSelectedIndex = queueSelectedIndex,
                displayListSize = displayList.size,
                queueSize = selectedAppsQueue.size,
                nowMs = now,
                lastNavMs = lastQueueNavTime
            )

            val decision = drawerKeyRouter.routeListKey(routeCtx, keyCode)
            if (decision is DrawerKeyRouter.Decision.PassThroughToEditor) {
                return
            }
            if (applyDrawerRouterDecision(decision, DrawerKeyRouter.Source.REMOTE, mainRecycler, selectedRecycler)) {
                return
            }

            // REMOTE_KEY FOCUS_LIST navigation is handled by DrawerKeyRouter.
            // Unhandled keys intentionally fall through to shared pending-command/hotkey logic below.
        }

        // 1. INPUT MODE (Entering Numbers or Arrow Navigation for Visual Queue)
        if (pendingCommandId != null) {
            if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                abortCommandMode()
                return
            }

            // Arrow key navigation in visual queue (left/up = prev, right/down = next)
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (vqCursorIndex > 0) {
                    vqCursorIndex--
                    uiHandler.post { showVisualQueue(visualQueueView?.findViewById<TextView>(R.id.visual_queue_prompt)?.text?.toString() ?: "", vqCursorIndex) }
                }
                return
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (vqCursorIndex < selectedAppsQueue.size - 1) {
                    vqCursorIndex++
                    uiHandler.post { showVisualQueue(visualQueueView?.findViewById<TextView>(R.id.visual_queue_prompt)?.text?.toString() ?: "", vqCursorIndex) }
                }
                return
            }
            // Space or Enter confirms the arrow-key selection
            if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                handleCommandInput(vqCursorIndex + 1)
                return
            }

            val num = keyEventToNumber(keyCode)
            if (num != -1) {
                handleCommandInput(num)
                return
            }

            abortCommandMode()
            return
        }

        // 2. TRIGGER MODE (Detecting Hotkeys)
        var commandTriggered = false

        for (cmd in AVAILABLE_COMMANDS) {
            val bind = AppPreferences.getKeybind(this, cmd.id)

            if (bind.second != 0 && bind.second == keyCode) {
                // Check modifiers
                if (checkModifiers(metaState, bind.first)) {
                    triggerCommand(cmd)
                    commandTriggered = true
                    break
                }
            }
        }

        // AUTO-RESET LATCH: If we were latched, any key press (valid command or not) consumes the latch.
        if (isCustomModLatched) {
            isCustomModLatched = false
            safeToast(if (commandTriggered) "Command Executed" else "Command Cancelled")
            return
        }

        if (commandTriggered) return
    }

    private fun getFocusedNameEditor(): EditText? {
        if (!(isProfileNameEditMode || isLayoutNameEditMode)) return null
        if (!isExpanded) return null

        val focusedView = drawerView?.findFocus()
        if (focusedView !is EditText || !focusedView.isFocusableInTouchMode) return null

        return when (focusedView.id) {
            R.id.layout_name,
            R.id.profile_name_text -> focusedView
            else -> null
        }
    }

    private fun applyDrawerRouterDecision(
        decision: DrawerKeyRouter.Decision,
        source: DrawerKeyRouter.Source,
        mainRecycler: RecyclerView?,
        selectedRecycler: RecyclerView?
    ): Boolean {
        return when (decision) {
            is DrawerKeyRouter.Decision.NotHandled -> false
            is DrawerKeyRouter.Decision.Handled -> decision.consume

            is DrawerKeyRouter.Decision.MoveList -> {
                val old = selectedListIndex
                val next = (selectedListIndex + decision.delta).coerceIn(0, maxOf(displayList.size - 1, 0))
                if (old != next) {
                    selectedListIndex = next
                    uiHandler.post {
                        mainRecycler?.adapter?.notifyItemChanged(old)
                        mainRecycler?.adapter?.notifyItemChanged(next)
                        mainRecycler?.scrollToPosition(next)
                    }
                }
                true
            }

            is DrawerKeyRouter.Decision.MoveQueue -> {
                val old = queueSelectedIndex
                val next = (queueSelectedIndex + decision.delta).coerceIn(0, maxOf(selectedAppsQueue.size - 1, 0))
                if (old != next) {
                    queueSelectedIndex = next
                    uiHandler.post {
                        selectedRecycler?.adapter?.notifyItemChanged(old)
                        selectedRecycler?.adapter?.notifyItemChanged(next)
                        selectedRecycler?.scrollToPosition(next)
                    }
                }
                true
            }

            DrawerKeyRouter.Decision.ToSearch -> {
                currentFocusArea = FOCUS_SEARCH
                queueSelectedIndex = -1
                uiHandler.post {
                    drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.requestFocus()
                }
                true
            }

            DrawerKeyRouter.Decision.ToList -> {
                currentFocusArea = FOCUS_LIST
                queueSelectedIndex = -1
                true
            }

            DrawerKeyRouter.Decision.ToQueue -> {
                currentFocusArea = FOCUS_QUEUE
                queueSelectedIndex = 0

                val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                captureIntent.setPackage(PACKAGE_TRACKPAD)
                captureIntent.putExtra("CAPTURE", true)
                sendBroadcast(captureIntent)

                uiHandler.post {
                    drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
                    debugStatusView?.visibility = View.VISIBLE
                    if (!isOpenMoveToMode && !isOpenSwapMode) {
                        debugStatusView?.text = "Queue Navigation"
                    }
                }
                true
            }

            DrawerKeyRouter.Decision.ActivateSelectedListItem -> {
                uiHandler.post {
                    mainRecycler?.findViewHolderForAdapterPosition(selectedListIndex)?.itemView?.performClick()
                }
                true
            }

            DrawerKeyRouter.Decision.ActivateSelectedQueueItem -> {
                uiHandler.post {
                    selectedRecycler?.findViewHolderForAdapterPosition(queueSelectedIndex)?.itemView?.performClick()
                }
                true
            }

            DrawerKeyRouter.Decision.ConfirmPendingQueueCommand -> true

            DrawerKeyRouter.Decision.CommitNameEdit -> {
                getFocusedNameEditor()?.onEditorAction(EditorInfo.IME_ACTION_DONE)
                true
            }

            DrawerKeyRouter.Decision.PassThroughToEditor -> {
                source == DrawerKeyRouter.Source.DRAWER_LISTENER ||
                    source == DrawerKeyRouter.Source.SEARCH_LISTENER
            }

            DrawerKeyRouter.Decision.AbortPendingCommand -> {
                abortCommandMode()
                true
            }

            is DrawerKeyRouter.Decision.PendingCommandNumber -> {
                handleCommandInput(decision.number)
                true
            }
        }
    }

    private fun checkModifiers(currentMeta: Int, requiredMeta: Int): Boolean {
        if (requiredMeta == 0) return true
        
        // Custom Modifier Logic
        if (requiredMeta == MOD_CUSTOM) {
            return isCustomModLatched
        }
        
        // Standard Android Meta Flags
        return (currentMeta and requiredMeta) != 0
    }

    private fun keyEventToNumber(code: Int): Int {
        return when (code) {
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> code - KeyEvent.KEYCODE_0
            in KeyEvent.KEYCODE_NUMPAD_0..KeyEvent.KEYCODE_NUMPAD_9 -> code - KeyEvent.KEYCODE_NUMPAD_0
            // [FIX] QWERTY top-row fallback: When the overlay keyboard hasn't switched to the
            // number layer yet (SET_NUM_LAYER broadcast race), it sends letter keycodes.
            // Map QWERTY top row to their number equivalents so command input still works.
            KeyEvent.KEYCODE_Q -> 1
            KeyEvent.KEYCODE_W -> 2
            KeyEvent.KEYCODE_E -> 3
            KeyEvent.KEYCODE_R -> 4
            KeyEvent.KEYCODE_T -> 5
            KeyEvent.KEYCODE_Y -> 6
            KeyEvent.KEYCODE_U -> 7
            KeyEvent.KEYCODE_I -> 8
            KeyEvent.KEYCODE_O -> 9
            KeyEvent.KEYCODE_P -> 0
            else -> -1
        }
    }

    private fun triggerCommand(cmd: CommandDef) {
        if (cmd.argCount == 0) {
            // Special case: OPEN_MOVE_TO opens drawer in app selection mode
            if (cmd.id == "OPEN_MOVE_TO") {
                isOpenMoveToMode = true
                isOpenSwapMode = false
                openMoveToApp = null
                showSlotNumbersInQueue = false
                
                // Open drawer if not already open
                if (!isExpanded) {
                    toggleDrawer()
                }
                
                // Switch to search mode and focus search bar
                switchMode(MODE_SEARCH)
                currentFocusArea = FOCUS_SEARCH
                val et = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)
                et?.setText("")
                et?.requestFocus()
                
                // Show prompt
                debugStatusView?.visibility = View.VISIBLE
                debugStatusView?.text = "Open & Move To: Select an app"
                
                return
            
            // Special case: OPEN_SWAP opens drawer in app selection mode
            } else if (cmd.id == "OPEN_SWAP") {
                isOpenSwapMode = true
                isOpenMoveToMode = false
                openMoveToApp = null
                showSlotNumbersInQueue = false
                
                // Open drawer if not already open
                if (!isExpanded) {
                    toggleDrawer()
                }
                
                // Switch to search mode and focus search bar
                switchMode(MODE_SEARCH)
                currentFocusArea = FOCUS_SEARCH
                val et = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)
                et?.setText("")
                et?.requestFocus()
                
                // Show prompt
                debugStatusView?.visibility = View.VISIBLE
                debugStatusView?.text = "Open & Swap: Select an app"
                
                return
            }
            
            // Immediate
            val intent = Intent().putExtra("COMMAND", cmd.id)
            queueWindowManagerCommand(intent)
            safeToast("Executed: ${cmd.label}")
        } else {
            // Enter Input Mode
            pendingCommandId = cmd.id
            pendingArg1 = -1
            vqCursorIndex = 0
            showVisualQueue("${cmd.label}: Enter Slot #", highlightSlot0Based = 0)
        }
    }

    override fun handleCommandInput(number: Int) {
        val t0 = System.currentTimeMillis()
        // Convert input 1-based to 0-based
        if (number == 0) return // 0 is invalid for 1-based slot
        val slotIndex = number - 1

        val cmd = AVAILABLE_COMMANDS.find { it.id == pendingCommandId } ?: return

        if (pendingArg1 == -1) {
            // First Arg Received
            pendingArg1 = slotIndex

            if (cmd.argCount == 1) {
                // Done! Execute (1 Arg)
                val intent = Intent().putExtra("COMMAND", cmd.id).putExtra("INDEX", number)
                queueWindowManagerCommand(intent)
                // Prevent race: do not am-start old active app while new WM command is queued.
                hideVisualQueue(restoreFocusToActive = false)
                pendingCommandId = null
            } else {
                // Need Second Arg - reset cursor for second selection
                vqCursorIndex = slotIndex
                val secondPrompt = if (cmd.id == "MOVE_TO") "${cmd.label}: Move to slot?" else "${cmd.label}: Swap with?"
                showVisualQueue(secondPrompt, slotIndex)
            }
        } else {
            // Second Arg Received (only supported for 2-arg commands like SWAP)
            val intent = Intent()
                .putExtra("COMMAND", cmd.id)
                .putExtra("INDEX_A", pendingArg1 + 1) // Convert back to 1-based for handler
                .putExtra("INDEX_B", number)

            queueWindowManagerCommand(intent)
            // Prevent race: do not am-start old active app while new WM command is queued.
            hideVisualQueue(restoreFocusToActive = false)
            pendingCommandId = null
        }
    }

    // AccessibilityService required overrides
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // [DEBUG] Log all events briefly
        val eventPkg = event.packageName?.toString() ?: "null"
        if (eventPkg != "com.android.systemui" && !eventPkg.contains("inputmethod")) {
        }

        // [EFFICIENCY] IMMEDIATE FILTER
        // Ignore high-frequency events that we don't use
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            return
        }

        // 1. WATCHDOG LOGIC (Direct Event Handling)
        // If we are targeting a Virtual Display (ID >= 2), monitor Display 1 (Cover) for escapes.
        if (currentDisplayId >= 2 && event.displayId == 1) {
            val pkg = event.packageName?.toString()
            
            // Filter out safe system apps
            if (!pkg.isNullOrEmpty() && 
                pkg != packageName && 
                pkg != PACKAGE_TRACKPAD && 
                pkg != "com.android.systemui" && 
                pkg != "com.sec.android.app.launcher" && 
                !pkg.contains("inputmethod")) {
                
                // Only act on state changes (app opening/resuming)
                if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    Log.w("DROIDOS_WATCHDOG", "ESCAPE DETECTED: $pkg on Cover (D1). Triggering Recovery to D$currentDisplayId")
                    forceAppToDisplay(pkg, currentDisplayId)
                }
            }
        }

        // --- IMPROVED ACTIVE WINDOW TRACKING (Display Aware) ---
        // Prioritizes events from the current display (Virtual Display for Glasses).
        // Filters getWindows() by displayId to avoid false positives from Phone Screen.
        try {
            if (event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED ||
                event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED) {

                var detectedPkg: String? = null
                var foundOnTargetDisplay = false

                // 1. Check Event Source (Most accurate for user interaction)
                // If the event comes from the display we are managing, trust it.
                if (Build.VERSION.SDK_INT >= 30) {
                    if (event.displayId == currentDisplayId && !event.packageName.isNullOrEmpty()) {
                        detectedPkg = event.packageName.toString()
                        foundOnTargetDisplay = true
                    }
                }

                // 2. Try Native Window API (Poll state)
                // Only if event didn't give us a definitive answer for this display
                if (!foundOnTargetDisplay && windows != null && windows.isNotEmpty()) {
                    var fallbackPkg: String? = null

                    for (window in windows) {
                        if (window.type == android.view.accessibility.AccessibilityWindowInfo.TYPE_APPLICATION && window.isFocused) {
                            val node = window.root
                            if (node != null) {
                                val pkg = node.packageName?.toString()
                                node.recycle()

                                // API 30+: Check if this window is on our target display
                                if (Build.VERSION.SDK_INT >= 30) {
                                    if (window.displayId == currentDisplayId) {
                                        detectedPkg = pkg
                                        foundOnTargetDisplay = true
                                        break
                                    }
                                }
                                // Capture focused window from ANY display as fallback
                                if (fallbackPkg == null) fallbackPkg = pkg
                            }
                        }
                    }

                    // If we didn't find a window on the target display, use the system-wide focused one
                    if (detectedPkg == null) detectedPkg = fallbackPkg
                }

                // 3. Last Resort Fallback (Pre-API 30 or empty windows)
                if (detectedPkg == null) {
                     detectedPkg = event.packageName?.toString()
                }

                // Filter & Update History
                if (!detectedPkg.isNullOrEmpty() &&
                    detectedPkg != packageName &&
                    detectedPkg != "com.android.systemui" &&
                    detectedPkg != PACKAGE_TRACKPAD &&
                    !detectedPkg.contains("inputmethod")) {

                    // [FULLSCREEN] Handle TYPE_WINDOWS_CHANGED for fullscreen detection even when package hasn't changed
                    // This catches Settings and system menus that fire TYPE_VIEW_FOCUSED first (which sets activePackageName)
                    // then TYPE_WINDOWS_CHANGED later (which would be skipped because package is same)
                    if (event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED &&
                        activePackageName == detectedPkg &&
                        selectedAppsQueue.any { !it.isMinimized } &&
                        !tiledAppsAutoMinimized) {
                        val activeNonMinimized = selectedAppsQueue.filter { !it.isMinimized }
                        val isGeminiDetect = detectedPkg == "com.google.android.googlequicksearchbox"
                        val isManagedApp = activeNonMinimized.any { 
                            val base = it.getBasePackage()
                            base == detectedPkg || it.packageName == detectedPkg || (isGeminiDetect && base == "com.google.android.apps.bard")
                        }
                        val isSystemOverlay = detectedPkg.contains("systemui") ||
                            detectedPkg.contains("launcher") ||
                            detectedPkg.contains("cocktail") ||
                            detectedPkg.contains("edge") ||
                            detectedPkg == "android"
                        
                        if (!isSystemOverlay && !isManagedApp) {
                            // Check if this app covers the screen
                            var coversScreen = false
                            try {
                                val dm = android.util.DisplayMetrics()
                                windowManager.defaultDisplay.getRealMetrics(dm)
                                val screenArea = dm.widthPixels.toLong() * dm.heightPixels.toLong()
                                val screenHeight = dm.heightPixels
                                val boundsRect = android.graphics.Rect()
                                for (window in windows) {
                                    val windowType = window.type
                                    val isRelevantType = windowType == android.view.accessibility.AccessibilityWindowInfo.TYPE_APPLICATION ||
                                                         windowType == android.view.accessibility.AccessibilityWindowInfo.TYPE_SYSTEM
                                    if (!isRelevantType) continue
                                    val node = window.root ?: continue
                                    val windowPkg = node.packageName?.toString()
                                    node.recycle()
                                    if (windowPkg == detectedPkg) {
                                        window.getBoundsInScreen(boundsRect)
                                        val windowArea = boundsRect.width().toLong() * boundsRect.height().toLong()
                                        val startsAtTop = boundsRect.top < screenHeight * 5 / 100
                                        if (windowArea >= screenArea * 85 / 100 && startsAtTop) {
                                            coversScreen = true
                                        }
                                        break
                                    }
                                }
                            } catch (e: Exception) { Log.e(TAG, "FULLSCREEN_DEBUG: Error", e) }
                            
                            if (coversScreen) {
                                tiledAppsAutoMinimized = true
                                val minimizeIntent = Intent().putExtra("COMMAND", "MINIMIZE_ALL")
                                queueWindowManagerCommand(minimizeIntent)
                            }
                        }
                    }
                    
                    // Update history if package changed
                    // Skip if we recently manually set focus via SET_FOCUS command
                    if (activePackageName != detectedPkg && System.currentTimeMillis() > manualFocusLockUntil) {
                        secondLastValidPackageName = lastValidPackageName
                        // [FIX] Don't overwrite history with null if we manually cleared activePackageName
                        // This preserves "Focus Last" functionality after minimizing an app
                        if (activePackageName != null) {
                            lastValidPackageName = activePackageName
                        }
                        activePackageName = detectedPkg

                        // [FULLSCREEN] Auto-minimize/restore tiled apps
                        // Only react to actual app window changes, not system overlays
                        val isSystemOverlay = detectedPkg.contains("systemui") ||
                            detectedPkg.contains("launcher") ||
                            detectedPkg.contains("cocktail") ||
                            detectedPkg.contains("edge") ||
                            detectedPkg.contains("samsung.android.app.routines") ||
                            detectedPkg.contains("android.providers") ||
                            detectedPkg.contains("permissioncontroller") ||
                            detectedPkg.contains("DroidOSTrackpadKeyboard") ||
                            detectedPkg.contains("inputmethod") ||
                            detectedPkg == "android"
                        // [FIX] Managed state: if app is in queue and not minimized, DroidOS is managing its bounds.
                        // We notify the IME to suppress insets for ANY managed app (even single apps) 
                        // to prevent double-resizing/pushing.
                        val activeNonMinimized = selectedAppsQueue.filter { !it.isMinimized }
                        val isGeminiDetect = detectedPkg == "com.google.android.googlequicksearchbox"
                        val isManagedApp = activeNonMinimized.any { 
                            val base = it.getBasePackage()
                            base == detectedPkg || it.packageName == detectedPkg || (isGeminiDetect && base == "com.google.android.apps.bard")
                        }
                        
                        // Traditional Tiled state (multi-window) for auto-minimize logic
                        val isTiledApp = activeNonMinimized.size > 1 && isManagedApp

                        // ===================================================================================
                        // CRITICAL: TILED vs FULLSCREEN INSET BEHAVIOR
                        // ===================================================================================
                        // - TILED (2+ non-minimized apps): Launcher resizes apps, DockIME suppresses insets
                        // - FULLSCREEN (0-1 app visible): Android resizes via insets, DockIME uses normal insets
                        //
                        // Key insight: An app in the queue running ALONE should behave like fullscreen,
                        // not tiled. Only when multiple apps are actively tiled should we suppress insets.
                        //
                        // TEST PROCEDURE:
                        // 1. Open 2 tiled apps (top/bottom layout), tap text field - NO blank gap
                        // 2. Open 1 app (even if in queue), tap text field - app should resize for keyboard
                        // ===================================================================================
                        val isActuallyTiled = activeNonMinimized.size > 1 && isManagedApp
                        if (!isSystemOverlay) {
                            sendBroadcast(Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.TILED_STATE")
                                .setPackage("com.katsuyamaki.DroidOSTrackpadKeyboard")
                                .putExtra("TILED_ACTIVE", isActuallyTiled))
                        }
                        // [FULLSCREEN] Skip auto-minimize during cooldown after explicit tiled app launch.
                        // This prevents newly launched tiled apps from being immediately hidden.
                        val inExplicitLaunchCooldown = System.currentTimeMillis() - lastExplicitTiledLaunchAt < 2000
                        
                        // [DEBUG] Log fullscreen detection state
                        val hasNonMinimized = selectedAppsQueue.any { !it.isMinimized }
                        
                        // [FULLSCREEN] Key insight: Only auto-minimize if the FOCUSED app is NOT managed.
                        // If the focused app IS managed (user opened a tiled app via hotkey/sidebar),
                        // we should tile together, not minimize the tiled app.
                        // Allow both TYPE_WINDOW_STATE_CHANGED (32) and TYPE_WINDOWS_CHANGED (4194304)
                        // Settings and some system apps only fire TYPE_WINDOWS_CHANGED
                        val isFullscreenTriggerEvent = event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                                                       event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED
                        if (!isSystemOverlay && !isManagedApp && !isTiledApp && selectedAppsQueue.any { !it.isMinimized } && !tiledAppsAutoMinimized &&
                            !inExplicitLaunchCooldown &&
                            isFullscreenTriggerEvent) {
                            // Full-screen app opened — minimize all tiled windows
                            // But first verify it actually covers the screen (skip small freeform/popup windows)
                            // [FIX] Also check that window starts at top of screen (y near 0).
                            // One UI popup/sidebar apps have offset from top and shouldn't trigger auto-minimize.
                            // [FIX] System packages (Settings) may open as side panels on Samsung One UI - use lower threshold.
                            val systemPanelPackages = setOf("com.android.settings", "com.samsung.android.settings", "com.android.systemui")
                            val isSystemPanel = systemPanelPackages.contains(detectedPkg)
                            var coversScreen = false
                            try {
                                val dm = android.util.DisplayMetrics()
                                windowManager.defaultDisplay.getRealMetrics(dm)
                                val screenArea = dm.widthPixels.toLong() * dm.heightPixels.toLong()
                                val screenHeight = dm.heightPixels
                                val boundsRect = android.graphics.Rect()
                                var foundWindow = false
                                for (window in windows) {
                                    val windowType = window.type
                                    // Check both TYPE_APPLICATION and TYPE_SYSTEM to catch Android system menus
                                    val isRelevantType = windowType == android.view.accessibility.AccessibilityWindowInfo.TYPE_APPLICATION ||
                                                         windowType == android.view.accessibility.AccessibilityWindowInfo.TYPE_SYSTEM
                                    val node = window.root
                                    val windowPkg = node?.packageName?.toString()
                                    node?.recycle()
                                    if (!isRelevantType) continue
                                    if (node == null) continue
                                    if (windowPkg == detectedPkg) {
                                        foundWindow = true
                                        window.getBoundsInScreen(boundsRect)
                                        val windowArea = boundsRect.width().toLong() * boundsRect.height().toLong()
                                        val coveragePercent = (windowArea * 100 / screenArea).toInt()
                                        // Must cover threshold% of screen area AND start near top (within 5% of screen height)
                                        // This filters out One UI popup/freeform windows which have offset from top
                                        // System panels (Settings) use lower 50% threshold since Samsung opens them as side panels
                                        val coverageThreshold = if (isSystemPanel) 50 else 95
                                        val startsAtTop = boundsRect.top < screenHeight * 5 / 100
                                        if (coveragePercent >= coverageThreshold && startsAtTop) {
                                            coversScreen = true
                                        }
                                        break
                                    }
                                }

                            } catch (e: Exception) { Log.e(TAG, "FULLSCREEN_DEBUG: Error checking coversScreen", e) }

                            if (coversScreen) {
                            pendingFullscreenCheckRunnable?.let { uiHandler.removeCallbacks(it) }
                            pendingFullscreenCheckRunnable = null
                            tiledAppsAutoMinimized = true

                            // Use MINIMIZE_ALL command for consistency and proper state tracking
                            val minimizeIntent = Intent().putExtra("COMMAND", "MINIMIZE_ALL")
                            queueWindowManagerCommand(minimizeIntent)
                            } else {
                            // [FULLSCREEN] Window doesn't cover screen yet (may still be animating).
                            // Schedule a delayed re-check to catch apps like Android Settings that
                            // expand to fullscreen after the initial accessibility event fires.
                            val recheckPkg = detectedPkg
                            val recheckIsSystemPanel = isSystemPanel
                            pendingFullscreenCheckRunnable?.let { uiHandler.removeCallbacks(it) }
                            val recheckRunnable = Runnable {
                                if (tiledAppsAutoMinimized || selectedAppsQueue.none { !it.isMinimized }) return@Runnable
                                var nowCoversScreen = false
                                try {
                                    val dm = android.util.DisplayMetrics()
                                    windowManager.defaultDisplay.getRealMetrics(dm)
                                    val screenArea = dm.widthPixels.toLong() * dm.heightPixels.toLong()
                                    val screenHeight = dm.heightPixels
                                    val boundsRect = android.graphics.Rect()
                                    // [FIX] Get fresh windows - the captured 'windows' reference is stale after 500ms
                                    val freshWindows = this@FloatingLauncherService.windows
                                    for (window in freshWindows) {
                                        // Check both TYPE_APPLICATION and TYPE_SYSTEM to catch Android system menus
                                        val isRelevantType = window.type == android.view.accessibility.AccessibilityWindowInfo.TYPE_APPLICATION ||
                                                             window.type == android.view.accessibility.AccessibilityWindowInfo.TYPE_SYSTEM
                                        if (!isRelevantType) continue
                                        val node = window.root ?: continue
                                        val windowPkg = node.packageName?.toString()
                                        node.recycle()
                                        if (windowPkg == recheckPkg) {
                                            window.getBoundsInScreen(boundsRect)
                                            val windowArea = boundsRect.width().toLong() * boundsRect.height().toLong()
                                            val coveragePercent = (windowArea * 100 / screenArea).toInt()
                                            val recheckThreshold = if (recheckIsSystemPanel) 50 else 85
                                            val startsAtTop = boundsRect.top < screenHeight * 5 / 100
                                            if (coveragePercent >= recheckThreshold && startsAtTop) {
                                                nowCoversScreen = true
                                            }
                                            break
                                        }
                                    }
                                } catch (e: Exception) { Log.e(TAG, "FULLSCREEN re-check failed", e) }
                                if (nowCoversScreen) {
                                    tiledAppsAutoMinimized = true
                                    // Use MINIMIZE_ALL command for consistency and proper state tracking
                                    val minimizeIntent = Intent().putExtra("COMMAND", "MINIMIZE_ALL")
                                    queueWindowManagerCommand(minimizeIntent)
                                }
                                pendingFullscreenCheckRunnable = null
                            }
                            pendingFullscreenCheckRunnable = recheckRunnable
                            uiHandler.postDelayed(recheckRunnable, 500)
                            } // end if (coversScreen) else
                        // [REMOVED] Auto-restore when managed app gains focus.
                        // Fullscreen apps should STAY fullscreen until user explicitly:
                        // 1. Clicks a tiled app (handled by isTiledApp && tiledAppsAutoMinimized below)
                        // 2. Uses RESTORE_ALL or UNMINIMIZE commands
                        // 3. Opens a tiled app via drawer/hotkey
                        } else if (isManagedApp && !isTiledApp && !tiledAppsAutoMinimized &&
                            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                            // [FULLSCREEN] Managed app opened while fullscreen app is visible (not previously auto-minimized).
                            // Check if there's a fullscreen app covering the screen - if so, add it to queue and tile together.
                            var fullscreenPkg: String? = null
                            try {
                                val dm = android.util.DisplayMetrics()
                                windowManager.defaultDisplay.getRealMetrics(dm)
                                val screenArea = dm.widthPixels.toLong() * dm.heightPixels.toLong()
                                val boundsRect = android.graphics.Rect()
                                val queuePackages = selectedAppsQueue.map { it.getBasePackage() }.toSet()
                                for (window in windows) {
                                    if (window.type != android.view.accessibility.AccessibilityWindowInfo.TYPE_APPLICATION) continue
                                    val node = window.root ?: continue
                                    val windowPkg = node.packageName?.toString()
                                    node.recycle()
                                    if (windowPkg != null && windowPkg !in queuePackages && windowPkg != detectedPkg &&
                                        windowPkg != packageName && !windowPkg.contains("systemui")) {
                                        window.getBoundsInScreen(boundsRect)
                                        val windowArea = boundsRect.width().toLong() * boundsRect.height().toLong()
                                        if (windowArea >= screenArea * 85 / 100) {
                                            fullscreenPkg = windowPkg
                                            break
                                        }
                                    }
                                }
                            } catch (e: Exception) { /* ignore */ }
                            
                            if (fullscreenPkg != null) {
                                // Found a fullscreen app not in queue - add at slot 1 and tile together
                                val fsAppInfo = allAppsList.find { it.packageName == fullscreenPkg || it.getBasePackage() == fullscreenPkg }
                                if (fsAppInfo != null) {
                                    selectedAppsQueue.add(0, fsAppInfo.copy())  // Add at position 0 (slot 1)
                                    uiHandler.post { updateAllUIs() }
                                    applyLayoutImmediate()  // Force freeform mode and tile all apps
                                }
                            }
                        } else if (isTiledApp && tiledAppsAutoMinimized) {
                            // Returning to a tiled app — restore all
                            // Detect fullscreen app, put at slot 1, then tile everything with applyLayoutImmediate.
                            tiledAppsAutoMinimized = false
                            tiledAppsRestoredAt = System.currentTimeMillis()
                            ensureFullscreenAppAtSlot1()  // Same logic as launcher app queue
                            applyLayoutImmediate()  // Force freeform mode and tile all apps
                        }



                        // Update UI to show underline for new focus
                        uiHandler.post { updateAllUIs() }
                    }
                }
            }

            // [FULLSCREEN] Detect tiled app maximized via native window toolbar
            // When TYPE_WINDOWS_CHANGED fires, check if any tiled app now covers ~full screen
            if (event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED && 
                selectedAppsQueue.size > 1 && selectedAppsQueue.any { !it.isMinimized } && !tiledAppsAutoMinimized &&
                System.currentTimeMillis() - tiledAppsRestoredAt > 1500) {
                try {
                    val dm = android.util.DisplayMetrics()
                    windowManager.defaultDisplay.getRealMetrics(dm)
                    val screenArea = dm.widthPixels.toLong() * dm.heightPixels.toLong()
                    val boundsRect = android.graphics.Rect()
                    
                    for (window in windows) {
                        if (window.type != android.view.accessibility.AccessibilityWindowInfo.TYPE_APPLICATION) continue
                        val node = window.root ?: continue
                        val windowPkg = node.packageName?.toString()
                        node.recycle()
                        if (windowPkg == null) continue
                        
                        val isTiled = selectedAppsQueue.any { it.getBasePackage() == windowPkg || it.packageName == windowPkg }
                        if (!isTiled) continue
                        
                        window.getBoundsInScreen(boundsRect)
                        val windowArea = boundsRect.width().toLong() * boundsRect.height().toLong()
                        if (windowArea >= screenArea * 85 / 100) {
                            // This tiled app has been maximized — minimize all others
                            tiledAppsAutoMinimized = true
                            val maximizedPkg = windowPkg
                            Thread {
                                try {
                                    for (app in selectedAppsQueue) {
                                        val basePkg = app.getBasePackage()
                                        if (basePkg == maximizedPkg || app.packageName == maximizedPkg) continue
                                        if (!app.isMinimized) {
                                            val tid = shellService?.getTaskId(basePkg, null) ?: -1
                                            if (tid != -1) shellService?.moveTaskToBack(tid)
                                        }
                                    }
                                } catch (e: Exception) { Log.e(TAG, "Auto-minimize others failed", e) }
                            }.start()
                            break
                        }
                    }
                    // Also detect non-tiled (e.g. One UI freeform/popup) apps maximized via toolbar
                    if (!tiledAppsAutoMinimized) {
                        for (window in windows) {
                            if (window.type != android.view.accessibility.AccessibilityWindowInfo.TYPE_APPLICATION) continue
                            val node = window.root ?: continue
                            val windowPkg = node.packageName?.toString()
                            node.recycle()
                            if (windowPkg == null) continue

                            val isTiled = selectedAppsQueue.any { it.getBasePackage() == windowPkg || it.packageName == windowPkg }
                            if (isTiled) continue

                            val isSystemOverlay = windowPkg.contains("systemui") ||
                                windowPkg.contains("launcher") ||
                                windowPkg.contains("cocktail") ||
                                windowPkg.contains("edge") ||
                                windowPkg.contains("samsung.android.app.routines") ||
                                windowPkg.contains("android.providers") ||
                                windowPkg.contains("permissioncontroller")
                            if (isSystemOverlay) continue

                            window.getBoundsInScreen(boundsRect)
                            val windowArea = boundsRect.width().toLong() * boundsRect.height().toLong()
                            if (windowArea >= screenArea * 85 / 100) {
                                tiledAppsAutoMinimized = true
                                // [FIX] Ignore fullscreen-driven focus writes while WM queue is active.
                                if (!isProcessingWmCommand && wmCommandQueue.isEmpty()) {
                                    activePackageName = windowPkg
                                }
                                Thread {
                                    try {
                                        for (app in selectedAppsQueue) {
                                            if (!app.isMinimized) {
                                                val tid = shellService?.getTaskId(app.getBasePackage(), null) ?: -1
                                                if (tid != -1) shellService?.moveTaskToBack(tid)
                                            }
                                        }
                                    } catch (e: Exception) { Log.e(TAG, "Auto-minimize for external fullscreen failed", e) }
                                }.start()
                                break
                            }
                        }
                    }
                } catch (e: Exception) { /* ignore bounds check errors */ }
            }
        } catch (e: Exception) {
            // Fallback: Event-based detection if windows API fails
             val pkg = event.packageName?.toString()
            if (!pkg.isNullOrEmpty() &&
                pkg != packageName &&
                pkg != "com.android.systemui" &&
                pkg != PACKAGE_TRACKPAD &&
                (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                 event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED)) {

                // [FIX] Ignore fallback focus writes while WM queue is active.
                if (isProcessingWmCommand || wmCommandQueue.isNotEmpty()) {
                    Log.d(TAG, "Accessibility focus skipped during WM command: $pkg")
                } else {
                    activePackageName = pkg
                }
            }
        }

        // [FOCUS_GUARD] Accessibility can race with WM queue close/hide/minimize.
        // Scrub stale focus/history after all event-driven focus mutations.
        sanitizeFocusState("onAccessibilityEvent")

        // 2. SOFT KEYBOARD TRIGGER SUPPORT (Toggleable)
        if (isSoftKeyboardSupport && event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            handleSoftKeyInput(event)
        }
    }
    override fun onInterrupt() {}

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val display = displayManager?.getDisplay(currentDisplayId) ?: return
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)
        val w = metrics.widthPixels; val h = metrics.heightPixels
        if (lastKnownScreenW != 0 && lastKnownScreenH != 0 &&
            (w != lastKnownScreenW || h != lastKnownScreenH)) {
            val oldSuffix = orientSuffix()
            lastKnownScreenW = w; lastKnownScreenH = h
            uiHandler.post { refreshUIForOrientationChange(oldSuffix) }
        } else {
            lastKnownScreenW = w; lastKnownScreenH = h
        }
    }

    // AccessibilityService entry point - called when user enables service in Settings
    override fun onServiceConnected() {
        super.onServiceConnected()
        
        // [STATE SYNC] Reset DockIMEPrefs on startup to prevent stale state.
        // The launcher will send correct TILED_STATE broadcasts as apps gain focus.
        getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE).edit()
            .putBoolean("launcher_has_managed_apps", false)
            .apply()
        // Also broadcast initial TILED_STATE(false) to reset keyboard
        sendBroadcast(Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.TILED_STATE")
            .setPackage("com.katsuyamaki.DroidOSTrackpadKeyboard")
            .putExtra("TILED_ACTIVE", false))

        // Initialize WindowManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager?.registerDisplayListener(displayListener, uiHandler)

        // Register receivers
        val filter = IntentFilter().apply {
            addAction(ACTION_OPEN_DRAWER)
            addAction(ACTION_UPDATE_ICON)
            addAction(ACTION_CYCLE_DISPLAY)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction("KEEP_SCREEN_ON")
            addAction("com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER")
            addAction("com.katsuyamaki.DroidOSLauncher.REQUEST_CUSTOM_MOD_SYNC")
            addAction("com.katsuyamaki.DroidOSLauncher.REQUEST_KEYBINDS") // [FIX] Added this
            addAction("com.katsuyamaki.DroidOSLauncher.REMOTE_KEY")
            addAction("com.katsuyamaki.DroidOSLauncher.SET_MARGIN_BOTTOM") // [FIX] Sync Margin
            addAction("com.katsuyamaki.DroidOSLauncher.IME_VISIBILITY") // Auto-adjust margin
            addAction("com.katsuyamaki.DroidOSLauncher.SET_AUTO_ADJUST_MARGIN") // Sync from DockIME
            addAction("com.katsuyamaki.DroidOSLauncher.FULLSCREEN_APP_OPENING") // Auto-minimize tiled apps

        }


        if (Build.VERSION.SDK_INT >= 33) registerReceiver(commandReceiver, filter, Context.RECEIVER_EXPORTED) else registerReceiver(commandReceiver, filter)


        // Shizuku setup
        try { Shizuku.addBinderReceivedListener(shizukuBinderListener); Shizuku.addRequestPermissionResultListener(shizukuPermissionListener) } catch (e: Exception) {}
        try { if (rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() } catch (e: Exception) {}

        // Load preferences
        loadInstalledApps(); currentFontSize = AppPreferences.getFontSize(this)
        // [DEPRECATED] killAppOnExecute = AppPreferences.getKillOnExecute(this)
        targetDisplayIndex = AppPreferences.getTargetDisplayIndex(this)
        autoRestartTrackpad = AppPreferences.getAutoRestartTrackpad(this) // NEW LOAD
        // [DEPRECATED] isInstantMode = AppPreferences.getInstantMode(this) - now always true
        showShizukuWarning = AppPreferences.getShowShizukuWarning(this)
        useAltScreenOff = AppPreferences.getUseAltScreenOff(this); isReorderDragEnabled = AppPreferences.getReorderDrag(this)
        isReorderTapEnabled = AppPreferences.getReorderTap(this); autoResizeEnabled = AppPreferences.getAutoResizeKeyboard(this)
        // bubbleSizePercent, currentDrawerHeightPercent, currentDrawerWidthPercent now loaded per-config in loadDisplaySettings()
        autoAdjustMarginForIME = AppPreferences.getAutoAdjustMarginForIME(this, orientSuffix())
            ?: AppPreferences.getAutoAdjustMarginForIME(this)
        droidOsImeDetected = AppPreferences.getDroidOsImeDetected(this)
        // Margins now loaded in loadDisplaySettings()



        // Load Custom Mod
        customModKey = AppPreferences.getCustomModKey(this)

        isSoftKeyboardSupport = AppPreferences.getSoftKeyboardSupport(this)

        // Sync Custom Mod to Trackpad
        sendCustomModToTrackpad()

        // Sync Keybinds to Trackpad
        broadcastKeybindsToKeyboard()

        // Debug Loaded Keys
        logSavedKeybinds()

        // Build UI
        val targetDisplayId = targetDisplayIndex
        setupDisplayContext(targetDisplayId)
        setupBubble()
        setupDrawer()

        // --- IMMEDIATE RESTORE ---
        // Ensure Visual Queue is populated before user interaction
        restoreQueueFromPrefs()
        // -------------------------

        updateGlobalFontSize()
        updateBubbleIcon()
        loadDisplaySettings(currentDisplayId) // Layout loading is now inside here

        safeToast("Launcher Ready")
    }

    override fun sendCustomModToTrackpad() {
        val intent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_CUSTOM_MOD")
        intent.setPackage(PACKAGE_TRACKPAD)
        intent.putExtra("KEYCODE", customModKey)
        sendBroadcast(intent)
    }

    /* * FUNCTION: onStartCommand
     * SUMMARY: Updated to strictly handle display migration. If an ID is passed,
     * it forces the bubble to move to that display context immediately.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Priority: 1. Explicit ID from Intent (Triggered by Icon Click) 2. Last saved Physical ID
        val targetDisplayId = intent?.getIntExtra("DISPLAY_ID", currentDisplayId) ?: currentDisplayId


        if (bubbleView != null) {
            // If we are already running but the target display changed, move the bubble
            if (targetDisplayId != currentDisplayId) {
                try {
                    windowManager.removeView(bubbleView)
                    if (isExpanded) windowManager.removeView(drawerView)
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing views for migration", e)
                }
                setupDisplayContext(targetDisplayId)
                setupBubble()
                setupDrawer()
                updateBubbleIcon()
                loadDisplaySettings(currentDisplayId)
                isExpanded = false
                safeToast("Recalled to Display $targetDisplayId")

                // [FIX] Apply layout immediately if in Instant Mode
                if (isInstantMode) {
                    uiHandler.postDelayed({ applyLayoutImmediate() }, 500)
                }
            }
        } else {
            // First time initialization
            try {
                setupDisplayContext(targetDisplayId)
                setupBubble()
                setupDrawer()
                
                updateGlobalFontSize()
                updateBubbleIcon()
                loadDisplaySettings(currentDisplayId) // Layout loading is now inside here

                if (rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku()
            } catch (e: Exception) {
                Log.e(TAG, "Setup failed", e)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }
    


    private fun loadDisplaySettings(displayId: Int) { 
        // 0. Compute current aspect ratio for per-config settings
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
        currentAspectRatio = AppPreferences.getAspectRatioCategory(dm.widthPixels, dm.heightPixels)
        
        // Load per-config drawer/bubble settings (with global fallback)
        currentDrawerHeightPercent = AppPreferences.getDrawerHeightPercentForConfig(this, displayId, currentAspectRatio)
            ?: AppPreferences.getDrawerHeightPercent(this)
        currentDrawerWidthPercent = AppPreferences.getDrawerWidthPercentForConfig(this, displayId, currentAspectRatio)
            ?: AppPreferences.getDrawerWidthPercent(this)
        bubbleSizePercent = AppPreferences.getBubbleSizeForConfig(this, displayId, currentAspectRatio)
            ?: AppPreferences.getBubbleSize(this)
        
        // 1. Margins (Per Display + Orientation)
        val os = orientSuffix()
        topMarginPercent = AppPreferences.getTopMarginPercent(this, displayId, os)
            ?: AppPreferences.getTopMarginPercent(this, displayId)
        bottomMarginPercent = AppPreferences.getBottomMarginPercent(this, displayId, os)
            ?: AppPreferences.getBottomMarginPercent(this, displayId)

        
        // 2. Layout & Custom Rects (Per Display + Orientation)
        selectedLayoutType = AppPreferences.getLastLayout(this, displayId, os)
            ?: AppPreferences.getLastLayout(this, displayId)
        activeCustomLayoutName = AppPreferences.getLastCustomLayoutName(this, displayId, os)
            ?: AppPreferences.getLastCustomLayoutName(this, displayId)
        
        if (selectedLayoutType == LAYOUT_CUSTOM_DYNAMIC && activeCustomLayoutName != null) {
            val data = AppPreferences.getCustomLayoutData(this, activeCustomLayoutName!!)
            if (data != null) {
                try {
                    val rects = mutableListOf<Rect>()
                    val rectParts = data.split("|")
                    for (rp in rectParts) {
                        val coords = rp.split(",")
                        if (coords.size == 4) rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt()))
                    }
                    activeCustomRects = rects
                } catch(e: Exception) {
                    Log.e(TAG, "Failed to load custom rects", e)
                }
            }
        }
        
        selectedResolutionIndex = AppPreferences.getDisplayResolution(this, displayId)
        currentOrientationMode = AppPreferences.getOrientationMode(this, displayId)
        
        // [FIX] On fresh install, grab the CURRENT system DPI instead of defaulting to -1 (Reset).
        // This prevents the "everything got huge" issue where "reset" reverts to a factory default
        // that is different from what the user was actually using.
        val savedDpi = AppPreferences.getDisplayDpi(this, displayId)
        
        if (savedDpi == -1) {
            val currentSystemDpi = displayContext?.resources?.configuration?.densityDpi ?: 0
            if (currentSystemDpi > 0) {
                currentDpiSetting = currentSystemDpi
                // Save it immediately so it becomes the baseline preference
                AppPreferences.saveDisplayDpi(this, displayId, currentDpiSetting)

            } else {
                currentDpiSetting = -1
            }
        } else {
            currentDpiSetting = savedDpi
        }
        
        // FIX: Sync state tracking to prevent unnecessary sleep on first execution
        lastAppliedResIndex = selectedResolutionIndex
        lastAppliedDpi = currentDpiSetting

        // [REMOVED] Auto-force disabled to prevent getting stuck on 120Hz
        // checkAndForceHighRefreshRate(displayId)
    }


    // =================================================================================
    // REFRESH RATE MANAGER
    // SUMMARY: Scans supported modes for the current display. If 120Hz (or higher) is found,
    //          it attempts to force it via Window Attributes (for this app) and Shell (System-wide).
    // =================================================================================


    // =================================================================================
    // REFRESH RATE MANAGER
    // =================================================================================
    private fun checkAndForceHighRefreshRate(displayId: Int) {
        if (displayId == 0) return 
        if (manualRefreshRateSet) return // [FIX] Don't override user's manual setting

        try {
            val display = displayManager?.getDisplay(displayId) ?: return
            val supportedModes = display.supportedModes
            
            var maxRate = 60f
            var bestModeId = -1
            
            for (mode in supportedModes) {
                if (mode.refreshRate > maxRate) {
                    maxRate = mode.refreshRate
                    bestModeId = mode.modeId
                }
            }

            // Only auto-force if we found a high refresh rate (>60)
            if (maxRate > 60f) {
                Log.i(TAG, "Auto-Force $maxRate Hz (Mode $bestModeId)")

                uiHandler.post {
                    try {
                        if (bubbleView != null && bubbleView?.isAttachedToWindow == true) {
                            bubbleParams.preferredDisplayModeId = bestModeId
                            val wm = attachedWindowManager ?: windowManager
                            wm.updateViewLayout(bubbleView, bubbleParams)
                        }
                    } catch (e: Exception) {}
                }
                
                val mode = supportedModes.find { it.modeId == bestModeId }
                if (mode != null && shellService != null) {
                    val cmd = "cmd display set-user-preferred-display-mode $displayId ${mode.physicalWidth} ${mode.physicalHeight} ${mode.refreshRate}"
                    Thread { try { shellService?.runCommand(cmd) } catch(e: Exception) {} }.start()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Refresh Rate Error", e)
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(launcherReceiver) } catch(e: Exception) {}
        isScreenOffState = false
        wakeUp()
        try { Shizuku.removeBinderReceivedListener(shizukuBinderListener); Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener); unregisterReceiver(commandReceiver) } catch (e: Exception) {}
        
        // Robust cleanup using attached manager
        try { 
            if (bubbleView != null) {
                val wm = attachedWindowManager ?: windowManager
                wm.removeView(bubbleView) 
            }
        } catch (e: Exception) {}
        
        try { 
            if (isExpanded) windowManager.removeView(drawerView) 
        } catch (e: Exception) {}
        
        // [FIX] Clean up visual queue on destroy to prevent orphaned views
        if (visualQueueView != null) {
            try { visualQueueWindowManager?.removeView(visualQueueView) } catch (e: Exception) {}
            try { windowManager.removeView(visualQueueView) } catch (e: Exception) {}
            visualQueueView = null
            isVisualQueueVisible = false
            visualQueueWindowManager = null
        }

        // [FIX] Clear WM queue callbacks/state to avoid leaking delayed runnables
        uiHandler.removeCallbacks(wmQueueFlushRunnable)
        uiHandler.removeCallbacks(wmQueueAdvanceRunnable)
        wmCommandQueue.clear()
        pendingWmRetryScheduled = false
        isProcessingWmCommand = false
        consecutiveWmQueueFailures = 0

        if (isBound) { try { ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection); isBound = false } catch (e: Exception) {} }
    setKeepScreenOn(false)
    wakeLock = null
    }
    
    // === SAFE TOAST FUNCTION - START ===
    // Displays toast message and updates debug status view
    override fun safeToast(msg: String) { 
        uiHandler.post { 
            try { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() } catch(e: Exception) { }
            if (debugStatusView != null) debugStatusView?.text = msg 
        }
    }
    // === SAFE TOAST FUNCTION - END ===

    // === DEBUG APP IDENTIFICATION - START ===
    // Visual debug function to show package/activity when apps are opened/modified/identified
    // This displays in the bright green text area above the app queue in the launcher drawer
    private fun debugShowAppIdentification(action: String, pkg: String, className: String?) {
        // [FIX] Only run if Debug Mode is enabled
        if (!isDebugMode) return

        val basePkg = if (pkg.contains(":")) pkg.substringBefore(":") else pkg
        val suffix = if (pkg.contains(":")) pkg.substringAfter(":") else null
        
        // Get short class name for display
        val shortCls = when {
            className.isNullOrEmpty() -> "NO_CLASS"
            className == "null" -> "NO_CLASS"
            else -> className.substringAfterLast(".")
        }
        
        val debugText = buildString {
            append("[$action] ")
            append("pkg=${basePkg.substringAfterLast(".")}")  // Show only last part of package
            if (suffix != null) append(":$suffix")
            append(" cls=$shortCls")
        }
        
        uiHandler.post {
            debugStatusView?.text = debugText
            // Also log full details
        }
    }
    // === DEBUG APP IDENTIFICATION - END ===
    
    private fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= 31) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(50)
            }
        } catch (e: Exception) {}
    }

    private fun setupDisplayContext(displayId: Int) {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = dm.getDisplay(displayId)
        if (display == null) { 
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            return 
        }
        currentDisplayId = displayId
        
        val baseContext = createDisplayContext(display)
        // Use TYPE_ACCESSIBILITY_OVERLAY (2032) for AccessibilityService
        displayContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            baseContext.createWindowContext(2032, null)
        } else {
            baseContext
        }
        
        windowManager = displayContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private fun refreshDisplayId() { val id = displayContext?.display?.displayId ?: Display.DEFAULT_DISPLAY; currentDisplayId = id }
    private fun startForegroundService() { val channelId = if (android.os.Build.VERSION.SDK_INT >= 26) { val channel = android.app.NotificationChannel(CHANNEL_ID, "Floating Launcher", android.app.NotificationManager.IMPORTANCE_LOW); getSystemService(android.app.NotificationManager::class.java).createNotificationChannel(channel); CHANNEL_ID } else ""; val notification = NotificationCompat.Builder(this, channelId).setContentTitle("CoverScreen Launcher Active").setSmallIcon(R.drawable.ic_launcher_bubble).setPriority(NotificationCompat.PRIORITY_MIN).build(); if (android.os.Build.VERSION.SDK_INT >= 34) startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(1, notification) }
    private fun bindShizuku() { try { val component = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(component, userServiceConnection, true, 1) } catch (e: Exception) { Log.e(TAG, "Bind Shizuku Failed", e) } }
    private fun updateExecuteButtonColor(isReady: Boolean) { uiHandler.post { val executeBtn = drawerView?.findViewById<ImageView>(R.id.icon_execute); if (isReady) executeBtn?.setColorFilter(Color.GREEN) else executeBtn?.setColorFilter(Color.RED) } }

    private fun saveOrientationState(os: String = orientSuffix()) {
        AppPreferences.setTopMarginPercent(this, currentDisplayId, topMarginPercent, os)
        AppPreferences.setBottomMarginPercent(this, currentDisplayId, bottomMarginPercent, os)
        AppPreferences.setAutoAdjustMarginForIME(this, autoAdjustMarginForIME, os)
        AppPreferences.saveLastLayout(this, selectedLayoutType, currentDisplayId, os)
        AppPreferences.saveLastCustomLayoutName(this, activeCustomLayoutName, currentDisplayId, os)
        AppPreferences.setDrawerHeightPercentForConfig(this, currentDisplayId, currentAspectRatio, currentDrawerHeightPercent, os)
        AppPreferences.setDrawerWidthPercentForConfig(this, currentDisplayId, currentAspectRatio, currentDrawerWidthPercent, os)
        AppPreferences.setBubbleSizeForConfig(this, currentDisplayId, currentAspectRatio, bubbleSizePercent, os)
    }

    private fun loadOrientationState() {
        val os = orientSuffix()
        topMarginPercent = AppPreferences.getTopMarginPercent(this, currentDisplayId, os)
            ?: AppPreferences.getTopMarginPercent(this, currentDisplayId)
        bottomMarginPercent = AppPreferences.getBottomMarginPercent(this, currentDisplayId, os)
            ?: AppPreferences.getBottomMarginPercent(this, currentDisplayId)
        autoAdjustMarginForIME = AppPreferences.getAutoAdjustMarginForIME(this, os)
            ?: AppPreferences.getAutoAdjustMarginForIME(this)
        selectedLayoutType = AppPreferences.getLastLayout(this, currentDisplayId, os)
            ?: AppPreferences.getLastLayout(this, currentDisplayId)
        activeCustomLayoutName = AppPreferences.getLastCustomLayoutName(this, currentDisplayId, os)
            ?: AppPreferences.getLastCustomLayoutName(this, currentDisplayId)
        currentDrawerHeightPercent = AppPreferences.getDrawerHeightPercentForConfig(this, currentDisplayId, currentAspectRatio, os)
            ?: AppPreferences.getDrawerHeightPercentForConfig(this, currentDisplayId, currentAspectRatio)
            ?: AppPreferences.getDrawerHeightPercent(this)
        currentDrawerWidthPercent = AppPreferences.getDrawerWidthPercentForConfig(this, currentDisplayId, currentAspectRatio, os)
            ?: AppPreferences.getDrawerWidthPercentForConfig(this, currentDisplayId, currentAspectRatio)
            ?: AppPreferences.getDrawerWidthPercent(this)
        bubbleSizePercent = AppPreferences.getBubbleSizeForConfig(this, currentDisplayId, currentAspectRatio, os)
            ?: AppPreferences.getBubbleSizeForConfig(this, currentDisplayId, currentAspectRatio)
            ?: AppPreferences.getBubbleSize(this)
        if (selectedLayoutType == LAYOUT_CUSTOM_DYNAMIC && activeCustomLayoutName != null) {
            val data = AppPreferences.getCustomLayoutData(this, activeCustomLayoutName!!)
            if (data != null) {
                try {
                    val rects = mutableListOf<Rect>()
                    for (rp in data.split("|")) {
                        val c = rp.split(",")
                        if (c.size == 4) rects.add(Rect(c[0].toInt(), c[1].toInt(), c[2].toInt(), c[3].toInt()))
                    }
                    activeCustomRects = rects
                } catch (e: Exception) { Log.e(TAG, "Failed to load orient custom rects", e) }
            }
        }
    }

    private fun refreshUIForOrientationChange(oldSuffix: String) {
        try {
            // Save state for OLD orientation using the suffix captured before dims updated
            saveOrientationState(oldSuffix)

            val wm = attachedWindowManager ?: windowManager
            if (bubbleView != null) { try { wm.removeView(bubbleView) } catch(e: Exception) {} }
            if (drawerView != null && isExpanded) { try { wm.removeView(drawerView) } catch(e: Exception) {} }
            setupDisplayContext(currentDisplayId)

            // lastKnownScreenW/H are now updated by the caller — load NEW orientation state
            loadOrientationState()

            // [FIX] Minimize apps that exceed the new orientation's layout slot count
            // This prevents extra apps from appearing in front of tiled apps due to z-order
            val newSlotCount = getLayoutRects().size
            val activeApps = selectedAppsQueue.filter { !it.isMinimized && it.packageName != PACKAGE_BLANK }
            if (activeApps.size > newSlotCount) {
                val appsToMinimize = activeApps.drop(newSlotCount)
                for (app in appsToMinimize) {
                    app.isMinimized = true
                }
                Thread {
                    for (app in appsToMinimize) {
                        try {
                            val tid = shellService?.getTaskId(app.getBasePackage(), null) ?: -1
                            if (tid != -1) shellService?.moveTaskToBack(tid)
                        } catch (e: Exception) {}
                    }
                }.start()
            }

            setupBubble()
            setupDrawer()
            updateBubbleIcon()
            isExpanded = false
            // Retile all tiled windows to fit the new screen dimensions
            requestHeadlessRetile("orientation-change", 400L)
        } catch (e: Exception) { Log.e(TAG, "refreshUIForOrientationChange failed", e) }
    }

    private fun setupBubble() {
        val context = displayContext ?: this
        val themeContext = ContextThemeWrapper(context, R.style.Theme_QuadrantLauncher)
        bubbleView = LayoutInflater.from(themeContext).inflate(R.layout.layout_bubble, null)
        bubbleView?.isClickable = true; bubbleView?.isFocusable = true 
        // [FIX] Disable system focus highlight to prevent ugly borders/grey overlay
        if (Build.VERSION.SDK_INT >= 26) bubbleView?.defaultFocusHighlightEnabled = false
        
        // Z-ORDER UPDATE: Try ACCESSIBILITY_OVERLAY (2032) + FLAG_LAYOUT_NO_LIMITS
        val targetType = if (Build.VERSION.SDK_INT >= 26) 2032 else WindowManager.LayoutParams.TYPE_PHONE 
        
        bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, 
            WindowManager.LayoutParams.WRAP_CONTENT, 
            targetType, 
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or 
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or 
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, 
            PixelFormat.TRANSLUCENT
        )
        
        // Calculate center Y considering margin
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)
        val h = metrics.heightPixels
        
        val topPx = (h * topMarginPercent / 100f).toInt()
        val bottomPx = (h * effectiveBottomMarginPercent() / 100f).toInt()
        val effectiveH = h - topPx - bottomPx


        
        // Absolute Y position = TopMargin + Half Effective Height
        val centerY = topPx + (effectiveH / 2)
        
        val screenW = metrics.widthPixels
        bubbleParams.gravity = Gravity.TOP or Gravity.START
        // Load saved position for this display+resolution, or use default
        val savedPos = AppPreferences.getBubblePositionForConfig(this, currentDisplayId, currentAspectRatio, screenW, h)
        if (savedPos != null) { bubbleParams.x = savedPos.first; bubbleParams.y = savedPos.second }
        else { bubbleParams.x = (screenW / 2) - 80; bubbleParams.y = centerY }
        
        // ... (Keep existing OnTouchListener logic here) ...
        var velocityTracker: VelocityTracker? = null
        bubbleView?.setOnTouchListener(object : View.OnTouchListener {
            var initialX = 0; var initialY = 0; var initialTouchX = 0f; var initialTouchY = 0f; var isDrag = false
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (velocityTracker == null) velocityTracker = VelocityTracker.obtain(); velocityTracker?.addMovement(event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { initialX = bubbleParams.x; initialY = bubbleParams.y; initialTouchX = event.rawX; initialTouchY = event.rawY; isDrag = false; return true }
                    MotionEvent.ACTION_MOVE -> { if (Math.abs(event.rawX - initialTouchX) > 10 || Math.abs(event.rawY - initialTouchY) > 10) isDrag = true; if (isDrag) { bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt(); bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt(); windowManager.updateViewLayout(bubbleView, bubbleParams) }; return true }
                    MotionEvent.ACTION_UP -> {
                        velocityTracker?.computeCurrentVelocity(1000)
                        val vX = velocityTracker?.xVelocity ?: 0f
                        val vY = velocityTracker?.yVelocity ?: 0f
                        val totalVel = hypot(vX.toDouble(), vY.toDouble())

                        // [SAFETY] FLING RESET: Harder threshold (4000) to prevent accidental flings
                        if (isDrag && totalVel > 4000) {
                            safeToast("Force Closing Launcher...")

                            // 1. Force Screen On
                            isScreenOffState = false
                            wakeUp()

                            // 2. Kill Process
                            stopSelf()
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                android.os.Process.killProcess(android.os.Process.myPid())
                                java.lang.System.exit(0)
                            }, 200)

                            return true
                        }

                        if (isDrag) {
                            // Save bubble position for this display+resolution
                            val dm = DisplayMetrics(); windowManager.defaultDisplay.getRealMetrics(dm)
                            AppPreferences.setBubblePositionForConfig(this@FloatingLauncherService, currentDisplayId, currentAspectRatio, bubbleParams.x, bubbleParams.y, dm.widthPixels, dm.heightPixels)
                        } else {
                            if (!isBound && showShizukuWarning) {
                                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                                    bindShizuku()
                                } else {
                                    safeToast("Shizuku NOT Connected. Opening Shizuku...")
                                    launchShizuku()
                                }
                            } else {
                                toggleDrawer()
                            }
                        }
                        velocityTracker?.recycle()
                        velocityTracker = null
                        return true
                    }
                    MotionEvent.ACTION_CANCEL -> { velocityTracker?.recycle(); velocityTracker = null }
                }
                return false
            }
        })

        // Z-ORDER UPDATE: Try High Z-Order, Fallback to App Overlay if denied
        try {
            windowManager.addView(bubbleView, bubbleParams)
            attachedWindowManager = windowManager
        } catch (e: Exception) {
            try {
                bubbleParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                windowManager.addView(bubbleView, bubbleParams)
                attachedWindowManager = windowManager
            } catch (e2: Exception) {
                Log.e(TAG, "Error adding bubble", e2)
            }
        }
        // [FIX] Apply saved bubble size after view is added
        if (bubbleSizePercent != 100) applyBubbleSize()
    }
    
    override fun changeBubbleSize(delta: Int) {
        bubbleSizePercent = (bubbleSizePercent + delta).coerceIn(50, 200)
        AppPreferences.setBubbleSizeForConfig(this, currentDisplayId, currentAspectRatio, bubbleSizePercent)
        AppPreferences.saveBubbleSize(this, bubbleSizePercent) // Global fallback
        applyBubbleSize()
        if (currentMode == MODE_SETTINGS) switchMode(MODE_SETTINGS)
    }

    private fun applyBubbleSize() {
        if (bubbleView == null) return
        val scale = bubbleSizePercent / 100f
        val density = resources.displayMetrics.density
        bubbleParams.width = (60 * scale * density).toInt()
        bubbleParams.height = (60 * scale * density).toInt()
        try { 
            val wm = attachedWindowManager ?: windowManager
            wm.updateViewLayout(bubbleView, bubbleParams) 
        } catch (e: Exception) {}
        val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon)
        iconView?.let {
            val lp = it.layoutParams
            lp.width = (52 * scale * density).toInt()
            lp.height = (52 * scale * density).toInt()
            it.layoutParams = lp
        }
    }

    private fun updateBubbleIcon() { val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon) ?: return; if (!isBound && showShizukuWarning) { uiHandler.post { iconView.setImageResource(android.R.drawable.ic_dialog_alert); iconView.setColorFilter(Color.RED); iconView.imageTintList = null }; return }; uiHandler.post { try { val uriStr = AppPreferences.getIconUri(this); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); if (bitmap != null) { iconView.setImageBitmap(bitmap); iconView.imageTintList = null; iconView.clearColorFilter() } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } catch (e: Exception) { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } }
    override fun dismissKeyboardAndRestore() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); if (searchBar != null && searchBar.hasFocus()) { searchBar.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(searchBar.windowToken, 0) }; val dpiInput = drawerView?.findViewById<EditText>(R.id.input_dpi_value); if (dpiInput != null && dpiInput.hasFocus()) { dpiInput.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(dpiInput.windowToken, 0) }; updateDrawerHeight(false) }

    // [NEW] Brings the Black Wallpaper to front.
    // This effectively minimizes whatever app is currently top, preventing the "Last App" freeze.
    private fun showWallpaper() {
        try {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("WALLPAPER_MODE", true)
            val options = android.app.ActivityOptions.makeBasic()
            options.setLaunchDisplayId(currentDisplayId)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show wallpaper", e)
        }
    }

    private fun setupDrawer() {
        val context = displayContext ?: this
        val themeContext = ContextThemeWrapper(context, R.style.Theme_QuadrantLauncher)
        drawerView = LayoutInflater.from(themeContext).inflate(R.layout.layout_rofi_drawer, null)
        drawerView!!.fitsSystemWindows = true 
        // [FIX] Disable system focus highlight (prevents screen turning grey when drawer is focused)
        if (Build.VERSION.SDK_INT >= 26) drawerView!!.defaultFocusHighlightEnabled = false
        
        // Z-ORDER UPDATE: Match Bubble settings
        val targetType = if (Build.VERSION.SDK_INT >= 26) 2032 else WindowManager.LayoutParams.TYPE_PHONE 

        drawerParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, 
            WindowManager.LayoutParams.MATCH_PARENT, 
            targetType, 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or 
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, 
            PixelFormat.TRANSLUCENT
        )
        drawerParams.gravity = Gravity.TOP or Gravity.START; drawerParams.x = 0; drawerParams.y = 0
        drawerParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        
        // FIXED: Ensure container is defined and logical
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container)
        if (container != null) {
            val lp = container.layoutParams as? FrameLayout.LayoutParams
            if (lp != null) { lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; lp.topMargin = 100; container.layoutParams = lp }

            debugStatusView = TextView(context)
            debugStatusView?.text = "Ready"
            debugStatusView?.setTextColor(Color.GREEN)
            debugStatusView?.textSize = currentFontSize * 0.8f // Scale down for debug view
            debugStatusView?.gravity = Gravity.CENTER
            
            // [FIX] Hide Debug View by Default
            debugStatusView?.visibility = View.GONE
            
            container.addView(debugStatusView, 0)
        }

        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val mainRecycler = drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view); val selectedRecycler = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        // [DEPRECATED] Execute button always hidden - instant mode only
        // if (isBound) executeBtn.setColorFilter(Color.GREEN) else executeBtn.setColorFilter(Color.RED)
        executeBtn.visibility = View.GONE
        // === MODE ICON CLICK LISTENERS - START ===
        // Sets up click listeners for mode switching icons in drawer
        drawerView!!.findViewById<ImageView>(R.id.icon_search_mode).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_SEARCH) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_window).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_LAYOUTS) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_resolution).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_RESOLUTION) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_refresh)?.setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_REFRESH) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_dpi).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_DPI) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_blacklist)?.setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_BLACKLIST) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_profiles).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_PROFILES) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_keybinds)?.setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_KEYBINDS) }

        // [FIX] SETTINGS ICON - DEBUG TRIGGER (5 Clicks)
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_settings).setOnClickListener { 
            dismissKeyboardAndRestore()
            
            // Check for 5 clicks
            val now = System.currentTimeMillis()
            if (now - lastDebugClickTime < 500) {
                debugClickCount++
            } else {
                debugClickCount = 1
            }
            lastDebugClickTime = now

            if (debugClickCount >= 5) {
                isDebugMode = !isDebugMode
                debugClickCount = 0
                val status = if (isDebugMode) "ON" else "OFF"
                
                uiHandler.post {
                    Toast.makeText(context, "Debug Mode: $status", Toast.LENGTH_SHORT).show()
                    debugStatusView?.visibility = if (isDebugMode) View.VISIBLE else View.GONE
                    if (currentMode == MODE_SETTINGS) switchMode(MODE_SETTINGS)
                }
            }

            switchMode(MODE_SETTINGS) 
        }
        // === MODE ICON CLICK LISTENERS - END ===
        // [DEPRECATED] Execute button removed - instant mode only
        // executeBtn.setOnClickListener { executeLaunch(selectedLayoutType, closeDrawer = true) }
        searchBar.addTextChangedListener(object : TextWatcher { 
            override fun afterTextChanged(s: Editable?) { 
                // [FIX] In FOCUS_QUEUE mode, intercept typed characters and check for keybinds
                if (currentFocusArea == FOCUS_QUEUE && s != null && s.isNotEmpty()) {
                    val typed = s.toString()
                    val lastChar = typed.lastOrNull()
                    if (lastChar != null) {
                        // Get keyCode for this character
                        val keyCode = KeyEvent.keyCodeFromString("KEYCODE_${lastChar.uppercaseChar()}")
                        if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
                            // Check if this matches any keybind
                            for (cmd in AVAILABLE_COMMANDS) {
                                val bind = AppPreferences.getKeybind(this@FloatingLauncherService, cmd.id)
                                if (bind.second == keyCode) {
                                    // Clear the typed character
                                    s.clear()
                                    // Execute the command
                                    if (cmd.argCount == 2) {
                                        queueCommandPending = cmd
                                        queueCommandSourceIndex = queueSelectedIndex
                                        debugStatusView?.text = "${cmd.label}: Select Target & Press Enter"
                                        drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
                                    } else {
                                        // [FIX] Use command queue for sequential execution
                                        val intent = Intent().putExtra("COMMAND", cmd.id).putExtra("INDEX", queueSelectedIndex + 1)
                                        queueWindowManagerCommand(intent)
                                    }
                                    return
                                }
                            }
                        }
                    }
                    // Not a keybind, clear and stay in queue mode
                    s.clear()
                    return
                }
                filterList(s.toString()) 
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {} 
        })
        searchBar.imeOptions = EditorInfo.IME_ACTION_DONE
        searchBar.setOnEditorActionListener { v, actionId, event -> if (actionId == EditorInfo.IME_ACTION_DONE) { dismissKeyboardAndRestore(); return@setOnEditorActionListener true }; false }
        
        // --- SEARCH BAR INPUT LOGIC ---
        searchBar.setOnKeyListener { _, keyCode, event -> 
            if (event.action == KeyEvent.ACTION_DOWN) {
                // [FIX] Intercept TAB to cycle tabs instead of changing focus
                if (keyCode == KeyEvent.KEYCODE_TAB) {
                    cycleTab(event.isShiftPressed)
                    return@setOnKeyListener true
                }

                // ESC: Exit input mode, give focus to drawer for navigation
                if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                    dismissKeyboardAndRestore()
                    drawerView?.requestFocus()
                    return@setOnKeyListener true
                }

                // [FIX] DOWN: Search -> Queue (if exists) -> List
                // Skip if REMOTE_KEY handler already processed this
                val now = System.currentTimeMillis()
                if (now - lastQueueNavTime < 150) {
                    return@setOnKeyListener true
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    dismissKeyboardAndRestore()
                    drawerView?.requestFocus()
                    
                    if (selectedAppsQueue.isNotEmpty()) {
                        // Go to Queue
                        currentFocusArea = FOCUS_QUEUE
                        queueSelectedIndex = 0
                        // [FIX] Tell keyboard to capture all keys for queue navigation
                        val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                        captureIntent.setPackage(PACKAGE_TRACKPAD)
                        captureIntent.putExtra("CAPTURE", true)
                        sendBroadcast(captureIntent)
                        selectedRecycler.adapter?.notifyDataSetChanged()
                        debugStatusView?.visibility = View.VISIBLE
                        if (!isOpenMoveToMode && !isOpenSwapMode) {
                            debugStatusView?.text = "Queue Navigation: Use Arrows / Hotkeys"
                        }
                    } else {
                        // Go to List
                        currentFocusArea = FOCUS_LIST
                        if (displayList.isNotEmpty()) {
                            if (selectedListIndex >= displayList.size) selectedListIndex = 0
                            mainRecycler.adapter?.notifyItemChanged(selectedListIndex)
                            mainRecycler.scrollToPosition(selectedListIndex)
                        }
                    }
                    return@setOnKeyListener true
                }
                // [FIX] Handle queue navigation from searchBar for soft keyboard support (display 1)
                // When focus stays on searchBar but currentFocusArea is FOCUS_QUEUE, handle queue keys here
                if (currentFocusArea == FOCUS_QUEUE) {
                    // Skip if REMOTE_KEY handler already processed this
                    if (System.currentTimeMillis() - lastQueueNavTime < 150) {
                        return@setOnKeyListener true
                    }
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            if (queueSelectedIndex > 0) {
                                val old = queueSelectedIndex
                                queueSelectedIndex--
                                selectedRecycler.adapter?.notifyItemChanged(old)
                                selectedRecycler.adapter?.notifyItemChanged(queueSelectedIndex)
                                selectedRecycler.scrollToPosition(queueSelectedIndex)
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            if (queueSelectedIndex < selectedAppsQueue.size - 1) {
                                val old = queueSelectedIndex
                                queueSelectedIndex++
                                selectedRecycler.adapter?.notifyItemChanged(old)
                                selectedRecycler.adapter?.notifyItemChanged(queueSelectedIndex)
                                selectedRecycler.scrollToPosition(queueSelectedIndex)
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            // Block navigation during OPEN_MOVE_TO/OPEN_SWAP slot selection
                            if ((isOpenMoveToMode || isOpenSwapMode) && openMoveToApp != null) {
                                return@setOnKeyListener true
                            }
                            // Exit queue back to search
                            currentFocusArea = FOCUS_SEARCH
                            queueSelectedIndex = -1
                            selectedRecycler.adapter?.notifyDataSetChanged()
                            if (!isOpenMoveToMode && !isOpenSwapMode) debugStatusView?.visibility = View.GONE
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            // Block navigation during OPEN_MOVE_TO/OPEN_SWAP slot selection
                            if ((isOpenMoveToMode || isOpenSwapMode) && openMoveToApp != null) {
                                return@setOnKeyListener true
                            }
                            // Exit to list
                            dismissKeyboardAndRestore()
                            drawerView?.requestFocus()
                            currentFocusArea = FOCUS_LIST
                            queueSelectedIndex = -1
                            selectedRecycler.adapter?.notifyDataSetChanged()
                            if (!isOpenMoveToMode && !isOpenSwapMode) debugStatusView?.visibility = View.GONE
                            if (displayList.isNotEmpty()) {
                                if (selectedListIndex >= displayList.size) selectedListIndex = 0
                                mainRecycler.adapter?.notifyItemChanged(selectedListIndex)
                                mainRecycler.scrollToPosition(selectedListIndex)
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_ENTER -> {
                            // [FIX] Skip - let REMOTE_KEY handler process ENTER exclusively
                            // This avoids race conditions between both handlers
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_ESCAPE -> {
                            // Cancel OPEN_MOVE_TO/OPEN_SWAP if active
                            if (isOpenMoveToMode || isOpenSwapMode) {
                                val wasSwap = isOpenSwapMode
                                cancelOpenMoveToMode()
                                safeToast(if (wasSwap) "Open & Swap cancelled" else "Open & Move To cancelled")
                                return@setOnKeyListener true
                            }
                            // Exit queue
                            currentFocusArea = FOCUS_SEARCH
                            queueSelectedIndex = -1
                            selectedRecycler.adapter?.notifyDataSetChanged()
                            debugStatusView?.visibility = View.GONE
                            return@setOnKeyListener true
                        }
                        else -> {
                            // Check for hotkey commands (j, k, x, etc.)
                            for (cmd in AVAILABLE_COMMANDS) {
                                val bind = AppPreferences.getKeybind(this, cmd.id)
                                if (bind.second == keyCode && keyCode != KeyEvent.KEYCODE_SPACE) {
                                    if (cmd.argCount == 2) {
                                        queueCommandPending = cmd
                                        queueCommandSourceIndex = queueSelectedIndex
                                        debugStatusView?.text = "${cmd.label}: Select Target & Press Enter"
                                        selectedRecycler.adapter?.notifyDataSetChanged()
                                    } else {
                                        // [FIX] Use command queue for sequential execution
                                        val intent = Intent().putExtra("COMMAND", cmd.id).putExtra("INDEX", queueSelectedIndex + 1)
                                        queueWindowManagerCommand(intent)
                                    }
                                    return@setOnKeyListener true
                                }
                            }
                        }
                    }
                }
                // ENTER: Launch top result immediately
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (displayList.isNotEmpty()) {
                        val item = displayList[0]
                        if (item is MainActivity.AppInfo) {
                            addToSelection(item)
                            return@setOnKeyListener true
                        }
                    }
                }
                // DEL: Remove last from queue if empty
                if (keyCode == KeyEvent.KEYCODE_DEL && searchBar.text.isEmpty() && selectedAppsQueue.isNotEmpty()) {
                    val lastIndex = selectedAppsQueue.size - 1
                    selectedAppsQueue.removeAt(lastIndex)
                    updateSelectedAppsDock()
                    mainRecycler.adapter?.notifyDataSetChanged()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
        
        searchBar.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) { updateDrawerHeight(hasFocus) } }
        mainRecycler.layoutManager = LinearLayoutManager(themeContext); mainRecycler.adapter = RofiAdapter(this); val itemTouchHelper = ItemTouchHelper(swipeCallback); itemTouchHelper.attachToRecyclerView(mainRecycler)
        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() { override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) { if (newState == RecyclerView.SCROLL_STATE_DRAGGING) { dismissKeyboardAndRestore() } } })
        mainRecycler.setOnTouchListener { v, event -> if (event.action == MotionEvent.ACTION_DOWN) { dismissKeyboardAndRestore() }; false }
        selectedRecycler.layoutManager = LinearLayoutManager(themeContext, LinearLayoutManager.HORIZONTAL, false); selectedRecycler.adapter = SelectedAppsAdapter(this); val dockTouchHelper = ItemTouchHelper(selectedAppsDragCallback); dockTouchHelper.attachToRecyclerView(selectedRecycler)
        drawerView!!.setOnClickListener { toggleDrawer() }
        
        // --- DRAWER ROOT NAVIGATION LOGIC ---
        drawerView!!.isFocusable = true
        drawerView!!.isFocusableInTouchMode = true
        drawerView!!.setOnKeyListener { _, keyCode, event -> 
            if (event.action == KeyEvent.ACTION_DOWN) {
                
                // GLOBAL: ESCAPE (Cancel/Close)
                if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                    if (isOpenMoveToMode) {
                        // Cancel OPEN_MOVE_TO mode
                        cancelOpenMoveToMode()
                        safeToast("Open & Move To cancelled")
                        return@setOnKeyListener true
                    }
                    if (queueCommandPending != null) {
                        // Cancel pending command
                        queueCommandPending = null
                        queueCommandSourceIndex = -1
                        debugStatusView?.text = "Command Cancelled"
                        // Refresh to clear highlighting
                        selectedRecycler.adapter?.notifyDataSetChanged()
                    } else {
                        // Close Drawer
                        toggleDrawer()
                    }
                    return@setOnKeyListener true
                }
                
                // OPEN_MOVE_TO/OPEN_SWAP: Handle number keys for slot selection
                if ((isOpenMoveToMode || isOpenSwapMode) && openMoveToApp != null) {
                    val num = when (keyCode) {
                        KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_NUMPAD_1 -> 1
                        KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_NUMPAD_2 -> 2
                        KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_NUMPAD_3 -> 3
                        KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_NUMPAD_4 -> 4
                        KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_NUMPAD_5 -> 5
                        KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_NUMPAD_6 -> 6
                        KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_NUMPAD_7 -> 7
                        KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_NUMPAD_8 -> 8
                        KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_NUMPAD_9 -> 9
                        else -> -1
                    }
                    if (num in 1..(selectedAppsQueue.size + 1)) {
                        if (isOpenMoveToMode) executeOpenMoveTo(num) else executeOpenSwap(num)
                        return@setOnKeyListener true
                    }
                }

                // GLOBAL: TAB (Switch Modes)
                if (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_ALT_RIGHT) {
                    cycleTab(event.isShiftPressed)
                    return@setOnKeyListener true
                }

                // === QUEUE NAVIGATION MODE ===
                if (currentFocusArea == FOCUS_QUEUE) {
                    // [FIX] Skip if REMOTE_KEY handler already processed this key
                    val now = System.currentTimeMillis()
                    if (now - lastQueueNavTime < 150) {
                        return@setOnKeyListener true // Already handled by REMOTE_KEY
                    }
                    
                    // 1. HOTKEY CHECK (Priority: Overrides Enter/Space defaults if bound)
                    // We check custom bindings first. If a key matches, execute it.
                    for (cmd in AVAILABLE_COMMANDS) {
                        val bind = AppPreferences.getKeybind(this, cmd.id)
                        
                        // [FIX] Ignore modifiers BUT SKIP Enter/Space to preserve default nav behavior
                        // This prevents "Toggle Drawer" (Space) from overriding "Set Focus" (Space)
                        if (bind.second == keyCode && keyCode != KeyEvent.KEYCODE_SPACE && keyCode != KeyEvent.KEYCODE_ENTER) {
                            
                            if (cmd.argCount == 2) {
                                // 2-Step Command (e.g. Swap)
                                queueCommandPending = cmd
                                queueCommandSourceIndex = queueSelectedIndex
                                debugStatusView?.visibility = View.VISIBLE
                                debugStatusView?.text = "${cmd.label}: Select Target & Press Enter"
                                selectedRecycler.adapter?.notifyDataSetChanged()
                            } else {
                                // 1-Step Command (e.g. Kill, Minimize, Focus Last)
                                // [FIX] Use command queue for sequential execution
                                val intent = Intent().putExtra("COMMAND", cmd.id).putExtra("INDEX", queueSelectedIndex + 1)
                                queueWindowManagerCommand(intent)
                            }
                            return@setOnKeyListener true
                        }
                    }

                    // 2. NAVIGATION & DEFAULT ACTIONS
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            if (queueSelectedIndex > 0) {
                                val old = queueSelectedIndex
                                queueSelectedIndex--
                                selectedRecycler.adapter?.notifyItemChanged(old)
                                selectedRecycler.adapter?.notifyItemChanged(queueSelectedIndex)
                                selectedRecycler.scrollToPosition(queueSelectedIndex)
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            if (queueSelectedIndex < selectedAppsQueue.size - 1) {
                                val old = queueSelectedIndex
                                queueSelectedIndex++
                                selectedRecycler.adapter?.notifyItemChanged(old)
                                selectedRecycler.adapter?.notifyItemChanged(queueSelectedIndex)
                                selectedRecycler.scrollToPosition(queueSelectedIndex)
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            // Block navigation during OPEN_MOVE_TO/OPEN_SWAP slot selection
                            if ((isOpenMoveToMode || isOpenSwapMode) && openMoveToApp != null) {
                                return@setOnKeyListener true
                            }
                            // Exit to Search
                            currentFocusArea = FOCUS_SEARCH
                            queueSelectedIndex = -1
                            selectedRecycler.adapter?.notifyDataSetChanged()
                            if (!isOpenMoveToMode && !isOpenSwapMode) debugStatusView?.visibility = View.GONE
                            searchBar.requestFocus()
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            // Block navigation during OPEN_MOVE_TO/OPEN_SWAP slot selection
                            if ((isOpenMoveToMode || isOpenSwapMode) && openMoveToApp != null) {
                                return@setOnKeyListener true
                            }
                            // Exit to List
                            currentFocusArea = FOCUS_LIST
                            queueSelectedIndex = -1
                            selectedRecycler.adapter?.notifyDataSetChanged()
                            if (!isOpenMoveToMode && !isOpenSwapMode) debugStatusView?.visibility = View.GONE
                            if (displayList.isNotEmpty()) {
                                if (selectedListIndex >= displayList.size) selectedListIndex = 0
                                mainRecycler.adapter?.notifyItemChanged(selectedListIndex)
                                mainRecycler.scrollToPosition(selectedListIndex)
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_ENTER -> {
                            // OPEN_MOVE_TO/OPEN_SWAP: Enter confirms current selection
                            if (isOpenMoveToMode && openMoveToApp != null && queueSelectedIndex >= 0) {
                                executeOpenMoveTo(queueSelectedIndex + 1)
                                return@setOnKeyListener true
                            }
                            if (isOpenSwapMode && openMoveToApp != null && queueSelectedIndex >= 0) {
                                executeOpenSwap(queueSelectedIndex + 1)
                                return@setOnKeyListener true
                            }
                            
                            if (queueCommandPending != null) {
                                // Complete 2-Step Command
                                // [FIX] Use command queue for sequential execution
                                val intent = Intent()
                                    .putExtra("COMMAND", queueCommandPending!!.id)
                                    .putExtra("INDEX_A", queueCommandSourceIndex + 1)
                                    .putExtra("INDEX_B", queueSelectedIndex + 1)
                                queueWindowManagerCommand(intent)
                                queueCommandPending = null
                                queueCommandSourceIndex = -1
                                debugStatusView?.text = "Command Executed"
                            } else {
                                // [FIX] Determine command by queue position (active apps first, then minimized)
                                if (queueSelectedIndex in selectedAppsQueue.indices) {
                                    val activeCount = selectedAppsQueue.count { !it.isMinimized }
                                    val cmd = if (queueSelectedIndex >= activeCount) "UNMINIMIZE" else "MINIMIZE"
                                    val intent = Intent().putExtra("COMMAND", cmd).putExtra("INDEX", queueSelectedIndex + 1)
                                    queueWindowManagerCommand(intent)
                                }
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_SPACE -> {
                            // OPEN_MOVE_TO/OPEN_SWAP: Space confirms current selection
                            if (isOpenMoveToMode && openMoveToApp != null && queueSelectedIndex >= 0) {
                                executeOpenMoveTo(queueSelectedIndex + 1)
                                return@setOnKeyListener true
                            }
                            if (isOpenSwapMode && openMoveToApp != null && queueSelectedIndex >= 0) {
                                executeOpenSwap(queueSelectedIndex + 1)
                                return@setOnKeyListener true
                            }
                            
                            if (queueCommandPending == null && queueSelectedIndex in selectedAppsQueue.indices) {
                                // [FIX] DEFAULT SPACE: Toggle Internal Focus (Green Underline)
                                val app = selectedAppsQueue[queueSelectedIndex]
                                if (app.packageName != PACKAGE_BLANK) {
                                    val pkg = app.packageName
                                    
                                    // Handle Gemini alias for matching
                                    val isGemini = pkg == "com.google.android.apps.bard"
                                    val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"
                                    val isActive = (activePackageName == pkg) || (isGemini && activeIsGoogle)

                                    if (isActive) {
                                        // CLEAR Focus
                                        activePackageName = null
                                    } else {
                                        // SET Focus
                                        if (activePackageName != null) lastValidPackageName = activePackageName
                                        activePackageName = pkg
                                    }
                                    updateAllUIs()
                                }
                            }
                            return@setOnKeyListener true
                        }
                    }
                    return@setOnKeyListener true
                }

                // === LIST NAVIGATION MODE ===
                if (currentFocusArea == FOCUS_LIST) {
                    // [FIX] Skip if REMOTE_KEY handler already processed this key
                    if (System.currentTimeMillis() - lastQueueNavTime < 150) {
                        return@setOnKeyListener true
                    }
                    // [FIX] Name editor guard: while actively editing, skip all launcher list actions.
                    val focusedNameEditor = getFocusedNameEditor()
                    if (focusedNameEditor != null) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                            focusedNameEditor.onEditorAction(EditorInfo.IME_ACTION_DONE)
                            return@setOnKeyListener true
                        }
                        return@setOnKeyListener false
                    }
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (selectedListIndex > 0) {
                                val old = selectedListIndex
                                selectedListIndex--
                                mainRecycler.adapter?.notifyItemChanged(old)
                                mainRecycler.adapter?.notifyItemChanged(selectedListIndex)
                                mainRecycler.scrollToPosition(selectedListIndex)
                            } else {
                                // Top of list reached -> Go to Queue (if exists) or Search
                                if (selectedAppsQueue.isNotEmpty()) {
                                    currentFocusArea = FOCUS_QUEUE
                                    queueSelectedIndex = 0
                                    // [FIX] Tell keyboard to capture all keys for queue navigation
                                    val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                                    captureIntent.setPackage(PACKAGE_TRACKPAD)
                                    captureIntent.putExtra("CAPTURE", true)
                                    sendBroadcast(captureIntent)
                                    selectedRecycler.adapter?.notifyDataSetChanged()
                                    debugStatusView?.visibility = View.VISIBLE
                                    if (!isOpenMoveToMode && !isOpenSwapMode) {
                                        debugStatusView?.text = "Queue Navigation"
                                    }
                                    
                                    // Clear list highlight
                                    val oldList = selectedListIndex
                                    // selectedListIndex = -1 // Optional: keep position memory?
                                    mainRecycler.adapter?.notifyItemChanged(oldList)
                                } else {
                                    currentFocusArea = FOCUS_SEARCH
                                    searchBar.requestFocus()
                                }
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (selectedListIndex < displayList.size - 1) {
                                val old = selectedListIndex
                                selectedListIndex++
                                mainRecycler.adapter?.notifyItemChanged(old)
                                mainRecycler.adapter?.notifyItemChanged(selectedListIndex)
                                mainRecycler.scrollToPosition(selectedListIndex)
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SPACE -> {
                             // Activate selected item
                             val holder = mainRecycler.findViewHolderForAdapterPosition(selectedListIndex)
                             holder?.itemView?.performClick()
                             return@setOnKeyListener true
                        }
                    }
                }

                // Global Fallbacks
                if (keyCode == KeyEvent.KEYCODE_BACK) { toggleDrawer(); return@setOnKeyListener true }
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) { if(isScreenOffState) wakeUp(); return@setOnKeyListener true }
            }
            false 
        }
    }

    // === VISUAL QUEUE (HUD) ===

    override fun setupVisualQueue() {
        // [FIX] Remove existing visual queue view BEFORE creating new one to prevent orphaned views
        if (isVisualQueueVisible && visualQueueView != null) {
            try { visualQueueWindowManager?.removeView(visualQueueView) } catch (e: Exception) {}
            try { (displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.removeView(visualQueueView) } catch (e: Exception) {}
            try { windowManager.removeView(visualQueueView) } catch (e: Exception) {}
            isVisualQueueVisible = false
            visualQueueWindowManager = null
        }
        val themeContext = ContextThemeWrapper(displayContext ?: this, R.style.Theme_QuadrantLauncher)
        visualQueueView = LayoutInflater.from(themeContext).inflate(R.layout.layout_visual_queue, null)

        // CRITICAL: Ensure we use the WindowManager for the CURRENT display context
        val targetWM = displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: windowManager

        // Use Type 2032 (Accessibility) or Phone, consistent with other views
        val targetType = if (Build.VERSION.SDK_INT >= 26) 2032 else WindowManager.LayoutParams.TYPE_PHONE

        visualQueueParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            targetType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        
        // MARGIN ADJUSTMENT: Center in the effective safe area
        val display = (displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay 
                      ?: windowManager.defaultDisplay
        val h = display.height
        
        val topPx = (h * topMarginPercent / 100f).toInt()
        val bottomPx = (h * effectiveBottomMarginPercent() / 100f).toInt()
        
        // Standard CENTER is at h/2.


        // Effective Center is at (topPx + (h - topPx - bottomPx)/2) = topPx + h/2 - topPx/2 - bottomPx/2
        // = h/2 + (topPx - bottomPx)/2
        // Offset from standard center = (topPx - bottomPx) / 2
        
        val yShift = (topPx - bottomPx) / 2
        
        visualQueueParams?.gravity = Gravity.CENTER
        visualQueueParams?.y = yShift 

        val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
        recycler?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // DUMMY INPUT to keep Gboard alive
        val dummyInput = visualQueueView?.findViewById<EditText>(R.id.vq_dummy_input)
        dummyInput?.showSoftInputOnFocus = true
    }

    private fun showVisualQueue(prompt: String, highlightSlot0Based: Int = -1) {
        // FAST PATH: If already visible, just update adapter & prompt without removing/re-adding the window.
        // This prevents the blink caused by the remove+add cycle during arrow key navigation.
        if (isVisualQueueVisible && visualQueueView != null) {
            val promptView = visualQueueView?.findViewById<TextView>(R.id.visual_queue_prompt)
            promptView?.text = prompt
            val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
            recycler?.adapter = VisualQueueAdapter(this, highlightSlot0Based)
            // Background thread to sync visible packages (same as full path)
            Thread {
                val visible = shellService?.getVisiblePackages(currentDisplayId) ?: emptyList()
                uiHandler.post {
                    (recycler?.adapter as? VisualQueueAdapter)?.updateVisibility(visible)
                }
            }.start()
            return
        }

        // DEFENSIVE CLEANUP: Force-remove orphaned views that are flagged invisible but still attached
        if (visualQueueView != null) {
            Log.w(TAG, "Visual Queue cleanup: removing stale view before showing new one")
            try { visualQueueWindowManager?.removeView(visualQueueView) } catch (e: Exception) {}
            try { (displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.removeView(visualQueueView) } catch (e: Exception) {}
            try { windowManager.removeView(visualQueueView) } catch (e: Exception) {}
            isVisualQueueVisible = false
            visualQueueWindowManager = null
        }
        
        if (visualQueueView == null) setupVisualQueue()

        // [FIX] Use same queue sync logic as drawer (fetchRunningApps) instead of separate detection.
        // This ensures visual queue and drawer queue always show the same data.
        if (shellService != null) {
            fetchRunningApps()  // Syncs queue with actual visible apps, adds new visible apps, etc.
        } else {
            sortAppQueue()  // Fallback: just sort existing queue
        }

        // Background thread to update adapter with visibility info
        Thread {
            val visible = shellService?.getVisiblePackages(currentDisplayId) ?: emptyList()
            uiHandler.post {
                val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
                recycler?.adapter = VisualQueueAdapter(this, highlightSlot0Based)
                (recycler?.adapter as? VisualQueueAdapter)?.updateVisibility(visible)
            }
        }.start()

        val promptView = visualQueueView?.findViewById<TextView>(R.id.visual_queue_prompt)
        promptView?.text = prompt

        // Set placeholder adapter immediately (will be replaced by thread above)
        val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
        recycler?.adapter = VisualQueueAdapter(this, highlightSlot0Based)

        // Force focus to keep keyboard up
        val dummy = visualQueueView?.findViewById<EditText>(R.id.vq_dummy_input)
        dummy?.requestFocus()

        if (!isVisualQueueVisible) {
            try {
                // Use display-specific WM and store reference for reliable removal
                val targetWM = displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: windowManager
                visualQueueWindowManager = targetWM
                targetWM.addView(visualQueueView, visualQueueParams)
                isVisualQueueVisible = true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add Visual Queue", e)
                e.printStackTrace()
            }
        } else {
            // Just update adapter if already visible
            recycler?.adapter?.notifyDataSetChanged()
        }

        // [FIX] Safety timeout to prevent visual queue from getting stuck forever
        uiHandler.removeCallbacks(commandTimeoutRunnable)
        uiHandler.postDelayed(commandTimeoutRunnable, 30000L) // 30 second safety net

        // Tell Trackpad to redirect input to us
        val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
        captureIntent.setPackage(PACKAGE_TRACKPAD)
        captureIntent.putExtra("CAPTURE", true)
        sendBroadcast(captureIntent)

        // NEW: Auto-switch keyboard to Number Layer
        val layerIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_NUM_LAYER")
        layerIntent.setPackage(PACKAGE_TRACKPAD)
        layerIntent.putExtra("ACTIVE", true)
        sendBroadcast(layerIntent)
    }

    private fun hideVisualQueue(restoreFocusToActive: Boolean = true) {
        if (isVisualQueueVisible && visualQueueView != null) {
            var removed = false
            // Try stored WM first (most reliable)
            visualQueueWindowManager?.let { wm ->
                try {
                    wm.removeView(visualQueueView)
                    removed = true
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to remove VQ from stored WM: ${e.message}")
                }
            }
            // Fallback: try displayContext WM
            if (!removed) {
                try {
                    val targetWM = displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
                    targetWM?.removeView(visualQueueView)
                    removed = true
                } catch (e: Exception) {}
            }
            // Last resort: try base windowManager
            if (!removed) {
                try {
                    windowManager.removeView(visualQueueView)
                    removed = true
                } catch (e: Exception) {}
            }
            if (removed) {
                isVisualQueueVisible = false
                visualQueueWindowManager = null
            } else {
                Log.e(TAG, "CRITICAL: Failed to remove Visual Queue from ALL WindowManagers!")
                // Force cleanup to prevent stuck state
                visualQueueView = null
                isVisualQueueVisible = false
                visualQueueWindowManager = null
            }
        }
        uiHandler.removeCallbacks(commandTimeoutRunnable)

        // [FIX] Force focus back to active app BEFORE releasing keyboard capture
        // This ensures Android gives input focus to the app even while keyboard processes CAPTURE=false
        if (restoreFocusToActive && activePackageName != null && !isExpanded) {
            val basePkg = if (activePackageName!!.contains(":")) activePackageName!!.substringBefore(":") else activePackageName!!
            val appEntry = selectedAppsQueue.find { it.getBasePackage() == basePkg }
            val className = appEntry?.className
            val component = if (!className.isNullOrEmpty() && className != "null" && className != "default") {
                "$basePkg/$className"
            } else {
                null
            }
            val cmd = if (component != null) {
                "am start -n $component --activity-brought-to-front --display $currentDisplayId --windowingMode 5 --user 0"
            } else {
                "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --activity-brought-to-front --display $currentDisplayId --windowingMode 5 --user 0"
            }
            Thread { shellService?.runCommand(cmd) }.start()
        }

        // Release Trackpad input
        val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
        captureIntent.setPackage(PACKAGE_TRACKPAD)
        captureIntent.putExtra("CAPTURE", false)
        sendBroadcast(captureIntent)

        // NEW: Restore previous keyboard layer
        val layerIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_NUM_LAYER")
        layerIntent.setPackage(PACKAGE_TRACKPAD)
        layerIntent.putExtra("ACTIVE", false)
        sendBroadcast(layerIntent)
    }

    private fun abortCommandMode() {
        if (pendingCommandId != null) {
            safeToast("Command Cancelled")
            pendingCommandId = null
            pendingArg1 = -1
            hideVisualQueue()
        }
    }
    
    private fun cancelOpenMoveToMode() {
        isOpenMoveToMode = false
        isOpenSwapMode = false
        openMoveToApp = null
        showSlotNumbersInQueue = false
        currentFocusArea = FOCUS_SEARCH
        queueSelectedIndex = -1
        debugStatusView?.visibility = View.GONE
        drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
        
        // Release keyboard capture
        val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
        captureIntent.setPackage(PACKAGE_TRACKPAD)
        captureIntent.putExtra("CAPTURE", false)
        sendBroadcast(captureIntent)
        
        // Return focus to search
        drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.requestFocus()
    }
    
    private fun executeOpenMoveTo(targetSlot: Int) {
        val app = openMoveToApp ?: return
        
        // Reset mode state
        isOpenMoveToMode = false
        showSlotNumbersInQueue = false
        val savedApp = app
        openMoveToApp = null
        
        // Release keyboard capture
        val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
        captureIntent.setPackage(PACKAGE_TRACKPAD)
        captureIntent.putExtra("CAPTURE", false)
        sendBroadcast(captureIntent)
        
        // Get active slot count from current layout
        val rects = getLayoutRects()
        val activeSlotCount = rects.size
        val targetIdx = targetSlot - 1  // Convert to 0-based index
        val isTargetActive = targetIdx < activeSlotCount
        
        // Check if app is already in queue
        val existingIdx = selectedAppsQueue.indexOfFirst { 
            it.packageName == savedApp.packageName && it.className == savedApp.className 
        }
        
        if (existingIdx != -1) {
            // App already in queue - just do MOVE_TO
            val intent = Intent()
                .putExtra("COMMAND", "MOVE_TO")
                .putExtra("INDEX_A", existingIdx + 1)
                .putExtra("INDEX_B", targetSlot)
            queueWindowManagerCommand(intent)
        } else {
            // App not in queue - add it
            // Set minimized state based on target position
            savedApp.isMinimized = !isTargetActive
            
            // Launch the app (using both API and Shell for reliability)
            launchViaApi(savedApp.packageName, savedApp.className, null)
            launchViaShell(savedApp.packageName, savedApp.className, null)
            
            // Insert directly at target position (clamped to valid range)
            val insertIdx = targetIdx.coerceIn(0, selectedAppsQueue.size)
            selectedAppsQueue.add(insertIdx, savedApp)
            
            // If minimized, move to back immediately after launch
            if (!isTargetActive) {
                val basePkg = savedApp.getBasePackage()
                val cls = savedApp.className
                manualStateOverrides[basePkg] = System.currentTimeMillis()
                minimizedAtTimestamps[basePkg] = System.currentTimeMillis()
                uiHandler.postDelayed({
                    Thread {
                        try {
                            val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                            if (tid != -1) shellService?.moveTaskToBack(tid)
                        } catch (e: Exception) {}
                    }.start()
                }, 500)  // Delay to let app start first
            }
            
            // Update UI without triggering cascade restore
            updateSelectedAppsDock()
            if (isTargetActive) {
                uiHandler.postDelayed({ applyLayoutImmediate() }, 300)
            }
        }
        
        // Close drawer
        debugStatusView?.visibility = View.GONE
        if (isExpanded) {
            toggleDrawer()
        }
        
        safeToast("Opened ${savedApp.label} in slot $targetSlot")
    }
    
    private fun executeOpenSwap(targetSlot: Int) {
        val app = openMoveToApp ?: return
        
        // Reset mode state
        isOpenSwapMode = false
        isOpenMoveToMode = false
        showSlotNumbersInQueue = false
        val savedApp = app
        openMoveToApp = null
        
        // Release keyboard capture
        val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
        captureIntent.setPackage(PACKAGE_TRACKPAD)
        captureIntent.putExtra("CAPTURE", false)
        sendBroadcast(captureIntent)
        
        // Get active slot count from current layout
        val rects = getLayoutRects()
        val activeSlotCount = rects.size
        val targetIdx = targetSlot - 1  // Convert to 0-based index
        val isTargetActive = targetIdx < activeSlotCount
        
        // Check if app is already in queue
        val existingIdx = selectedAppsQueue.indexOfFirst { 
            it.packageName == savedApp.packageName && it.className == savedApp.className 
        }
        
        if (existingIdx != -1) {
            // App already in queue - just do SWAP
            val intent = Intent()
                .putExtra("COMMAND", "SWAP")
                .putExtra("INDEX_A", existingIdx + 1)
                .putExtra("INDEX_B", targetSlot)
            queueWindowManagerCommand(intent)
        } else {
            // App not in queue - add it first, then swap
            savedApp.isMinimized = !isTargetActive
            
            // Launch the app
            launchViaApi(savedApp.packageName, savedApp.className, null)
            launchViaShell(savedApp.packageName, savedApp.className, null)
            
            // Add to end of queue first
            selectedAppsQueue.add(savedApp)
            val newIdx = selectedAppsQueue.size - 1
            
            // If target is different from where we added, do swap
            if (newIdx != targetIdx && targetIdx in selectedAppsQueue.indices) {
                val intent = Intent()
                    .putExtra("COMMAND", "SWAP")
                    .putExtra("INDEX_A", newIdx + 1)
                    .putExtra("INDEX_B", targetSlot)
                queueWindowManagerCommand(intent)
            } else {
                // Already in correct position or invalid target
                updateSelectedAppsDock()
                if (isTargetActive) {
                    uiHandler.postDelayed({ applyLayoutImmediate() }, 300)
                }
            }
            
            // If minimized, move to back after launch
            if (!isTargetActive) {
                val basePkg = savedApp.getBasePackage()
                val cls = savedApp.className
                manualStateOverrides[basePkg] = System.currentTimeMillis()
                minimizedAtTimestamps[basePkg] = System.currentTimeMillis()
                uiHandler.postDelayed({
                    Thread {
                        try {
                            val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                            if (tid != -1) shellService?.moveTaskToBack(tid)
                        } catch (e: Exception) {}
                    }.start()
                }, 500)
            }
        }
        
        // Close drawer
        debugStatusView?.visibility = View.GONE
        if (isExpanded) {
            toggleDrawer()
        }
        
        safeToast("Opened ${savedApp.label} swapped to slot $targetSlot")
    }
    
    // [FIX] Queue commands for sequential execution to avoid race conditions
    private fun queueWindowManagerCommand(intent: Intent) {
        val copy = Intent(intent)
        if (Looper.myLooper() != Looper.getMainLooper()) {
            uiHandler.post { enqueueWindowManagerCommandOnMain(copy) }
            return
        }
        enqueueWindowManagerCommandOnMain(copy)
    }

    private fun enqueueWindowManagerCommandOnMain(intent: Intent) {
        if (wmCommandQueue.size >= 20) {
            val dropped = wmCommandQueue.removeFirst()
            val droppedCmd = dropped.getStringExtra("COMMAND") ?: "UNKNOWN"
            Log.w(TAG, "WM queue overflow, dropping oldest command: $droppedCmd")
        }
        wmCommandQueue.addLast(intent)
        val delay = if (isBound && shellService != null) 0L else 400L
        scheduleWindowManagerQueueFlush(delay)
    }

    private fun scheduleWindowManagerQueueFlush(delayMs: Long) {
        if (pendingWmRetryScheduled) return
        pendingWmRetryScheduled = true
        uiHandler.postDelayed(wmQueueFlushRunnable, delayMs)
    }

    private fun flushWindowManagerCommandQueue() {
        if (isProcessingWmCommand) {
            if (wmCommandQueue.isNotEmpty()) {
                scheduleWindowManagerQueueFlush(450L)
            }
            return
        }

        if (wmCommandQueue.isEmpty()) {
            return
        }

        if (!isBound || shellService == null) {
            val now = System.currentTimeMillis()
            if (now - lastWmBindAttemptAt > 1000L) {
                lastWmBindAttemptAt = now
                tryBindShizukuIfPermitted()
            }
            scheduleWindowManagerQueueFlush(600L)
            return
        }

        val nextIntent = wmCommandQueue.removeFirst()
        isProcessingWmCommand = true
        try {
            handleWindowManagerCommand(nextIntent)
            consecutiveWmQueueFailures = 0
        } catch (e: Exception) {
            val failedCmd = nextIntent.getStringExtra("COMMAND") ?: "UNKNOWN"
            Log.e(TAG, "WM queue command execution failed: $failedCmd", e)
            isProcessingWmCommand = false
            consecutiveWmQueueFailures = (consecutiveWmQueueFailures + 1).coerceAtMost(5)
            val backoffMs = 300L + (consecutiveWmQueueFailures * 150L)
            scheduleWindowManagerQueueFlush(backoffMs)
            return
        }

        uiHandler.postDelayed(wmQueueAdvanceRunnable, 400L)
    }

// [NEW] Robust Move Logic: Finds original slot and forces move with retries
    private fun forceAppToDisplay(pkg: String, targetDisplayId: Int) {
        // [LOCK] Prevent duplicate threads
        if (activeEnforcements.contains(pkg)) {
            return
        }
        activeEnforcements.add(pkg)

        Thread {
            try {
                // 1. Get Task ID & Class Name
                var tid = shellService?.getTaskId(pkg, null) ?: -1
                if (tid == -1) {
                    Thread.sleep(200) 
                    tid = shellService?.getTaskId(pkg, null) ?: -1
                }

                if (tid != -1) {
                    Log.w("DROIDOS_WATCHDOG", "Targeting Task $tid for $pkg. Starting Locked Loop...")

                    // 2. Lookup Cache / AppInfo
                    var bounds: Rect? = packageRectCache[pkg]
                    var className: String? = null
                    
                    // Fallback to queue + get Class Name
                    val appEntry = selectedAppsQueue.find { it.packageName == pkg || it.getBasePackage() == pkg }
                    if (appEntry != null) {
                         className = appEntry.className
                         if (bounds == null) {
                             val appIndex = selectedAppsQueue.indexOf(appEntry)
                             val rects = getLayoutRects()
                             if (appIndex >= 0 && appIndex < rects.size) bounds = rects[appIndex]
                         }
                    }

                    // 3. AGGRESSIVE Loop (10 attempts)
                    for (i in 1..10) {
                        // A. Try Task Move (Standard)
                        shellService?.runCommand("am task move-task-to-display $tid $targetDisplayId")
                        shellService?.runCommand("am task set-windowing-mode $tid 5")
                        
                        // B. Escalation Levels
                        // Level 1 (Attempts 3-6): Force Relaunch with NEW_TASK flag
                        if (i > 3 && i <= 6 && className != null) {
                             val cmd = "am start -n $pkg/$className --display $targetDisplayId --windowingMode 5 -f 0x10000000 --user 0"
                             Log.w("DROIDOS_WATCHDOG", "Escalating L1 (Relaunch): $cmd")
                             shellService?.runCommand(cmd)
                        }
                        
                        // Level 2 (Attempts 7+): NUCLEAR OPTION (Kill & Restart)
                        // If it refuses to move after 6 tries, the process is likely stuck with bad display affinity.
                        if (i > 6 && className != null) {
                             Log.e("DROIDOS_WATCHDOG", "Escalating L2 (Nuclear): Killing $pkg to break display affinity")
                             shellService?.forceStop(pkg)
                             Thread.sleep(300) // Wait for death
                             
                             val cmd = "am start -n $pkg/$className --display $targetDisplayId --windowingMode 5 -f 0x10000000 --user 0"
                             shellService?.runCommand(cmd)
                        }
                        
                        // Apply Bounds
                        if (bounds != null) {
                            shellService?.runCommand("am task resize $tid ${bounds.left} ${bounds.top} ${bounds.right} ${bounds.bottom}")
                        } else {
                            shellService?.runCommand("am task resize $tid 0 0 1000 1000")
                        }

                        // Check success
                        // We check BOTH display ID and visibility
                        val visibleOnTarget = shellService?.getVisiblePackages(targetDisplayId)?.contains(pkg) == true
                        if (visibleOnTarget) {
                            Log.w("DROIDOS_WATCHDOG", "SUCCESS: App moved on attempt $i")
                            // One final resize to ensure it stuck
                            if (bounds != null) shellService?.runCommand("am task resize $tid ${bounds.left} ${bounds.top} ${bounds.right} ${bounds.bottom}")
                            break
                        }
                        
                        Thread.sleep(300) // Slightly slower retry to let system process
                    }
                } else {
                    Log.e("DROIDOS_WATCHDOG", "Could not find Task ID for $pkg")
                }
            } catch (e: Exception) {
                Log.e("DROIDOS_WATCHDOG", "Force move failed", e)
            } finally {
                // [UNLOCK] Release lock
                activeEnforcements.remove(pkg)
            }
        }.start()
    }

    // === KEY PICKER (POPUP) ===
    override fun showKeyPicker(cmdId: String, currentMod: Int) {
        // Remove existing if any
        if (keyPickerView != null) {
            try { windowManager.removeView(keyPickerView) } catch(e: Exception){}
            keyPickerView = null
        }

        val themeContext = ContextThemeWrapper(displayContext ?: this, R.style.Theme_QuadrantLauncher)
        keyPickerView = LayoutInflater.from(themeContext).inflate(R.layout.layout_key_picker, null)

        val targetType = if (Build.VERSION.SDK_INT >= 26) 2032 else WindowManager.LayoutParams.TYPE_PHONE

        keyPickerParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            targetType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            PixelFormat.TRANSLUCENT
        )
        keyPickerParams?.dimAmount = 0.5f
        keyPickerParams?.gravity = Gravity.CENTER

        val recycler = keyPickerView?.findViewById<RecyclerView>(R.id.picker_recycler)
        recycler?.layoutManager = LinearLayoutManager(this)

        val keys = SUPPORTED_KEYS.toList() // Convert to list of pairs

        recycler?.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_simple_text, parent, false)
                ) {}
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val (label, code) = keys[position]
                val tv = holder.itemView.findViewById<TextView>(R.id.text1)
                tv.text = label

                holder.itemView.setOnClickListener {
                    // SAVE BINDING
                    if (cmdId == "CUSTOM_MOD_SETUP") {
                        AppPreferences.saveCustomModKey(this@FloatingLauncherService, code)
                        customModKey = code // Update live var
                    } else {
                        AppPreferences.saveKeybind(this@FloatingLauncherService, cmdId, currentMod, code)
                        broadcastKeybindsToKeyboard() // Update keyboard immediately
                    }
                    safeToast("Bound to $label")

                    // Close picker
                    if (keyPickerView != null) {
                        try { windowManager.removeView(keyPickerView) } catch(e: Exception){}
                        keyPickerView = null
                    }
                    // Refresh main list
                    uiHandler.post { switchMode(MODE_KEYBINDS) }
                }
            }
            override fun getItemCount() = keys.size
        }

        keyPickerView?.findViewById<View>(R.id.btn_cancel_picker)?.setOnClickListener {
            if (keyPickerView != null) {
                try { windowManager.removeView(keyPickerView) } catch(e: Exception){}
                keyPickerView = null
            }
        }

        try {
            // Add ON TOP of everything
            windowManager.addView(keyPickerView, keyPickerParams)
        } catch (e: Exception) { e.printStackTrace() }
    }

    // ADAPTER for the HUD
    // VisualQueueAdapter extracted to VisualQueueAdapter.kt

    /**
     * Initializes or re-initializes the window and all its components.
     * This is called when the service starts or when the target display changes.
     */
    private fun initWindow() {
        setupDisplayContext(currentDisplayId)
        setupBubble()
        setupDrawer()
        updateBubbleIcon()
        loadDisplaySettings(currentDisplayId)

        // [FIX] Apply layout immediately if in Instant Mode
        if (isInstantMode) {
            uiHandler.postDelayed({ applyLayoutImmediate() }, 500)
        }
    }
    
    override fun startReorderMode(index: Int) { if (!isReorderTapEnabled) return; if (index < 0 || index >= selectedAppsQueue.size) return; val prevIndex = reorderSelectionIndex; reorderSelectionIndex = index; val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; if (prevIndex != -1) adapter?.notifyItemChanged(prevIndex); adapter?.notifyItemChanged(reorderSelectionIndex); safeToast("Tap another app to Swap") }
    override fun swapReorderItem(targetIndex: Int) { if (reorderSelectionIndex == -1) return; Collections.swap(selectedAppsQueue, reorderSelectionIndex, targetIndex); val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; adapter?.notifyItemChanged(reorderSelectionIndex); adapter?.notifyItemChanged(targetIndex); endReorderMode(true) }
    override fun endReorderMode(triggerInstantMode: Boolean) { val prevIndex = reorderSelectionIndex; reorderSelectionIndex = -1; val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; if (prevIndex != -1) adapter?.notifyItemChanged(prevIndex); if (triggerInstantMode && isInstantMode) applyLayoutImmediate() }
    
    override fun updateDrawerHeight(isKeyboardMode: Boolean) {
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container) ?: return
        val dm = DisplayMetrics(); windowManager.defaultDisplay.getRealMetrics(dm); val screenH = dm.heightPixels; val screenW = dm.widthPixels
        val lp = container.layoutParams as? FrameLayout.LayoutParams; val topMargin = lp?.topMargin ?: 100
        var finalHeight = (screenH * (currentDrawerHeightPercent / 100f)).toInt()
        if (isKeyboardMode) { finalHeight = (screenH * 0.40f).toInt(); val maxAvailable = screenH - topMargin - 20; if (finalHeight > maxAvailable) finalHeight = maxAvailable }
        val newW = (screenW * (currentDrawerWidthPercent / 100f)).toInt()
        if (container.layoutParams.height != finalHeight || container.layoutParams.width != newW) { container.layoutParams.width = newW; container.layoutParams.height = finalHeight; container.requestLayout(); if (drawerParams.y != 0) { drawerParams.y = 0; windowManager.updateViewLayout(drawerView, drawerParams) } }
    }

    // === TOGGLE DRAWER - START ===
    // Opens/closes the launcher drawer overlay
    // Updates debug display with queue state when opening
    private fun toggleDrawer() {
        if (isExpanded) {
            try { windowManager.removeView(drawerView) } catch(e: Exception) {}
            // bubbleView?.visibility = View.VISIBLE // No need to toggle if always visible
            isExpanded = false

            // Auto-Restore Focus
            if (activePackageName != null) {
                // Focus the app that was active BEFORE we opened the drawer
                // (Or the current active one if we didn't switch away)
                val target = activePackageName
                Thread {
                    val app = selectedAppsQueue.find { it.packageName == target }
                    if (app != null) {
                        try {
                            val rects = getLayoutRects()
                            val idx = selectedAppsQueue.indexOf(app)
                            val bounds = if (idx >= 0 && idx < rects.size) rects[idx] else null
                            launchViaShell(app.getBasePackage(), app.className, bounds)
                        } catch(e: Exception) {}
                    }
                }.start()
            }
        } else {
            updateDrawerHeight(false)
            
            // Z-ORDER UPDATE: Try adding with High Priority, Fallback if fails
            try {
                windowManager.addView(drawerView, drawerParams)
            } catch(e: Exception) {
                Log.e(TAG, "Failed to add drawer with high priority: ${e.message}")
                try {
                    drawerParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    windowManager.addView(drawerView, drawerParams)
                } catch(e2: Exception) {
                    Log.e(TAG, "Failed to add drawer with fallback: ${e2.message}")
                }
            }

            // bubbleView?.visibility = View.GONE // Keep bubble visible
            isExpanded = true
            
            // [FIX] Reset all navigation/focus state to prevent ghost highlights
            currentFocusArea = FOCUS_SEARCH
            queueSelectedIndex = -1
            queueCommandPending = null
            queueCommandSourceIndex = -1
            
            // Reset OPEN_MOVE_TO/OPEN_SWAP state (unless we're opening for that purpose)
            if (!isOpenMoveToMode && !isOpenSwapMode) {
                openMoveToApp = null
                showSlotNumbersInQueue = false
                debugStatusView?.visibility = View.GONE
            }
            
            switchMode(MODE_SEARCH)
            
            val et = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)
            et?.setText("")
            et?.requestFocus() // Auto-focus for immediate typing
            et?.post {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
            }
            
            // [FIX] Ensure overlay keyboard stays visible when drawer opens.
            // The drawer's focus change can cause the DockIME to cycle (hidden->shown),
            // which triggers FORCE_HIDE on the overlay. Send FORCE_SHOW after a delay
            // to beat the race and update the debounce timestamp in OverlayService.
            uiHandler.postDelayed({
                val keepKbIntent = Intent("TOGGLE_CUSTOM_KEYBOARD")
                keepKbIntent.setPackage(PACKAGE_TRACKPAD)
                keepKbIntent.putExtra("FORCE_SHOW", true)
                sendBroadcast(keepKbIntent)
            }, 300)
            
            updateSelectedAppsDock()
            
            // Show current queue state in debug view when drawer opens (skip for OPEN_MOVE_TO/OPEN_SWAP)
            if (!isOpenMoveToMode && !isOpenSwapMode) {
                showQueueDebugState()
            }
            
            if (isInstantMode) fetchRunningApps() 
        }
    }
    // === TOGGLE DRAWER - END ===
    
    // === SHOW QUEUE DEBUG STATE - START ===
    private fun showQueueDebugState() {
        if (selectedAppsQueue.isEmpty()) {
            debugStatusView?.text = "[DRAWER] Queue empty"
        } else {
            val queueInfo = selectedAppsQueue.mapIndexed { i, app ->
                val shortCls = when {
                    app.className.isNullOrEmpty() -> "NO_CLS"
                    app.className == "null" -> "NO_CLS"
                    app.className == "default" -> "BAD_CLS"
                    else -> app.className!!.substringAfterLast(".")
                }
                val shortPkg = app.packageName.substringAfterLast(".")
                "$i:$shortPkg($shortCls)"
            }.joinToString(" | ")
            debugStatusView?.text = "[Q] $queueInfo"
            
            // Also log full details
            selectedAppsQueue.forEachIndexed { i, app ->
            }
        }
    }
    // === SHOW QUEUE DEBUG STATE - END ===
    private fun updateGlobalFontSize() { 
        val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)
        searchBar?.setScaledTextSize(currentFontSize, 1.0f) // Match rest of UI (16sp base)
        debugStatusView?.setScaledTextSize(currentFontSize, 0.8f) // Update debug view as well
        drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() 
    }

// This ensures that the internal list always has two separate entries for the Google package.
// We force the standard one to be "Google" and the assistant one to be "Gemini".

    // === LOAD INSTALLED APPS - START ===
    // Loads all launcher apps with proper className capture
    private fun loadInstalledApps() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val riList = pm.queryIntentActivities(intent, 0)
        allAppsList.clear()
        allAppsList.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null))

        val blacklist = AppPreferences.getBlacklist(this)

        for (ri in riList) {
            val pkg = ri.activityInfo.packageName
            // Get the FULL activity class name - this is critical for tiling
            val cls = ri.activityInfo.name
            
            if (pkg == PACKAGE_TRACKPAD || pkg == packageName) continue

            var label = ri.loadLabel(pm).toString()
            
            // Standalone Gemini app (com.google.android.apps.bard)
            if (pkg == "com.google.android.apps.bard") {
                label = "Gemini"
            }
            // Google Quick Search Box with Gemini/Assistant activity
            else if (pkg == "com.google.android.googlequicksearchbox" &&
                (cls.lowercase().contains("assistant") || 
                 cls.lowercase().contains("gemini") ||
                 cls.lowercase().contains("bard"))) {
                label = "Gemini (Google)"
            }

            // Validate className - must not be null, empty, or "default"
            val validClassName = if (cls.isNullOrEmpty() || cls == "default") {
                Log.w(DEBUG_TAG, "App $label ($pkg) has invalid className: $cls")
                null
            } else {
                cls
            }

            val app = MainActivity.AppInfo(
                label = label, 
                packageName = pkg, 
                className = validClassName,
                isFavorite = AppPreferences.isFavorite(this, pkg)
            )

            if (!blacklist.contains(app.getIdentifier())) {
                allAppsList.add(app)
            }
            
        }
        allAppsList.sortBy { it.label.lowercase() }
    }
    // === LOAD INSTALLED APPS - END ===

    // === LOAD BLACKLISTED APPS - START ===
    // Loads all blacklisted apps for display in blacklist tab
    // Reconstructs AppInfo objects from blacklist identifiers
    private fun loadBlacklistedApps() {
        displayList.clear()

        val pm = packageManager
        val blacklist = AppPreferences.getBlacklist(this)

        for (identifier in blacklist) {
            try {
                val parts = identifier.split(":")
                val pkg = parts[0]
                val activity = if (parts.size > 1) parts[1] else null

                // Determine label
                val label = when {
                    identifier.contains("gemini") -> "Gemini"
                    pkg == "com.google.android.googlequicksearchbox" -> "Google"
                    else -> {
                        try {
                            pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
                        } catch (e: Exception) {
                            identifier
                        }
                    }
                }

                val app = MainActivity.AppInfo(label, pkg, activity, false, false)
                displayList.add(app)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load blacklisted app: $identifier", e)
            }
        }

        displayList.sortBy { (it as? MainActivity.AppInfo)?.label?.lowercase() }
        drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    // === LOAD BLACKLISTED APPS - END ===


    private fun launchTrackpad() {
        // [FIX] Robust Launch Strategy: Service -> Activity -> Broadcast
        // 1. Try to start the Service directly (Fastest, maintains state)
        try {
            val serviceIntent = Intent()
            serviceIntent.component = ComponentName(
                "com.katsuyamaki.DroidOSTrackpadKeyboard", 
                "com.katsuyamaki.DroidOSTrackpadKeyboard.OverlayService" 
            )
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(serviceIntent) else startService(serviceIntent)
            safeToast("Starting Trackpad Service...")
            return
        } catch (e: Exception) {
            // Service start failed (likely Background Restrictions) -> Proceed to Step 2
        }

        // 2. Try to Launch the Main Activity (Guaranteed to wake app)
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage("com.katsuyamaki.DroidOSTrackpadKeyboard")
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)
                safeToast("Launching Trackpad App...")
                return
            }
        } catch (e: Exception) {
            // New package not found -> Proceed to Step 3
        }

        // 3. Legacy Fallback (Old Package Name)
        try {
            val oldIntent = Intent()
            oldIntent.component = ComponentName(
                "com.katsuyamaki.DroidOSTrackpadKeyboard", 
                "com.katsuyamaki.DroidOSTrackpadKeyboard.OverlayService"
            )
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(oldIntent) else startService(oldIntent)
            return
        } catch (e: Exception) {
            // Legacy Service failed -> Try Legacy Activity
            try {
                val oldLaunchIntent = packageManager.getLaunchIntentForPackage("com.katsuyamaki.DroidOSTrackpadKeyboard")
                if (oldLaunchIntent != null) {
                    oldLaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(oldLaunchIntent)
                    return
                }
            } catch (e2: Exception) {}
        }

        // 4. Final Resort: Broadcast (Only works if app is already alive)
        val broadcastNew = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_TRACKPAD_VISIBILITY")
        broadcastNew.setPackage("com.katsuyamaki.DroidOSTrackpadKeyboard")
        broadcastNew.putExtra("VISIBLE", true)
        sendBroadcast(broadcastNew)
        
        safeToast("Trackpad not found or failed to launch.")
    }

    private fun isTrackpadRunning(): Boolean { try { val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager; val runningApps = am.runningAppProcesses; if (runningApps != null) { for (info in runningApps) { if (info.processName == PACKAGE_TRACKPAD) return true } } } catch (e: Exception) {}; return false }
    override fun getLayoutName(type: Int): String { 
        // Check for user-renamed default
        if (type != LAYOUT_CUSTOM_DYNAMIC) {
            val custom = AppPreferences.getDefaultLayoutName(this, type)
            // [FIX] Auto-Repair: Ignore names that look like Settings toggles
            if (custom != null && !custom.contains("Virtual Display") && !custom.contains("Switch Display")) {
                return custom
            }
        }
        
        return when(type) { 
            LAYOUT_FULL -> "1 App - 1x1"
            LAYOUT_SIDE_BY_SIDE -> "2 Apps - 1x2"
            LAYOUT_TOP_BOTTOM -> "2 Apps - 2x1"
            LAYOUT_TRI_EVEN -> "3 Apps - 1x3"
            LAYOUT_CORNERS -> "4 Apps - 2x2"
            LAYOUT_TRI_SIDE_MAIN_SIDE -> "3 Apps - 1x3 [1:2:1]"
            LAYOUT_QUAD_ROW_EVEN -> "4 Apps - 1x4"
            LAYOUT_QUAD_TALL_SHORT -> "4 Apps - 2x2 [R 3:1]"
            LAYOUT_HEX_TALL_SHORT -> "6 Apps - 2x3 [R 3:1, C 1:2:1]"
            LAYOUT_CUSTOM_DYNAMIC -> "Custom"
            else -> "Unknown" 
        } 
    }
    override fun getRatioName(index: Int): String { return when(index) { 1 -> "1:1"; 2 -> "16:9"; 4 -> "16:10"; 3 -> "32:9"; else -> "Default" } }
    private fun getTargetDimensions(index: Int): Pair<Int, Int>? { return when(index) { 1 -> 1422 to 1500; 2 -> 1920 to 1080; 4 -> 1920 to 1200; 3 -> 3840 to 1080; else -> null } }
    private fun getResolutionCommand(index: Int): String { return when(index) { 1 -> "wm size 1422x1500 -d $currentDisplayId"; 2 -> "wm size 1920x1080 -d $currentDisplayId"; 4 -> "wm size 1920x1200 -d $currentDisplayId"; 3 -> "wm size 3840x1080 -d $currentDisplayId"; else -> "wm size reset -d $currentDisplayId" } }

// [FULLSCREEN] Detect any app covering >90% screen and ensure it's at queue position 0 (slot 1).
// Uses same logic as launcher app queue. Returns true if a fullscreen app was found and positioned.
            private fun ensureFullscreenAppAtSlot1(): Boolean {
                try {
                    val dm = android.util.DisplayMetrics()
                    windowManager.defaultDisplay.getRealMetrics(dm)
                    val screenArea = dm.widthPixels.toLong() * dm.heightPixels.toLong()
                    val boundsRect = android.graphics.Rect()
                    
                    for (window in windows) {
                        if (window.type != android.view.accessibility.AccessibilityWindowInfo.TYPE_APPLICATION) continue
                        val node = window.root ?: continue
                        val windowPkg = node.packageName?.toString()
                        node.recycle()
                        if (windowPkg == null) continue
                        
                        // Skip system packages
                        if (windowPkg == packageName || windowPkg == PACKAGE_TRACKPAD ||
                            windowPkg.contains("systemui") || windowPkg.contains("launcher") ||
                            windowPkg.contains("home") || windowPkg.contains("wallpaper") ||
                            windowPkg.contains("inputmethod") || windowPkg.contains("NavigationBar")) continue
                        
                        window.getBoundsInScreen(boundsRect)
                        val windowArea = boundsRect.width().toLong() * boundsRect.height().toLong()
                        
                        // If this app covers >90% of screen, ensure it's at position 0
                        if (windowArea >= screenArea * 90 / 100) {
                            val existingIdx = selectedAppsQueue.indexOfFirst { 
                                it.getBasePackage() == windowPkg || it.packageName == windowPkg 
                            }
                            if (existingIdx > 0) {
                                // Already in queue but not at position 0 - move it
                                val app = selectedAppsQueue.removeAt(existingIdx)
                                selectedAppsQueue.add(0, app)
                            } else if (existingIdx == -1) {
                                // Not in queue - add at position 0
                                var appInfo = allAppsList.find { it.getBasePackage() == windowPkg || it.packageName == windowPkg }
                                if (appInfo == null) {
                                    val label = try {
                                        packageManager.getApplicationLabel(packageManager.getApplicationInfo(windowPkg, 0)).toString()
                                    } catch (e: Exception) { windowPkg }
                                    appInfo = MainActivity.AppInfo(label, windowPkg, null, false, false)
                                }
                                selectedAppsQueue.add(0, appInfo.copy())
                            }
                            return true  // Found and positioned fullscreen app
                        }
                    }
                } catch (e: Exception) { 
                    Log.e(TAG, "ensureFullscreenAppAtSlot1 failed", e)
                }
                return false
            }

// Sorts queue: fullscreen first, tiled by layout position (top-left to bottom-right), minimized newest-to-oldest
            private fun sortAppQueue() {
                val rects = getLayoutRects()
                
                // [FIX] Track currently selected app to preserve selection across sort
                val selectedApp = if (queueSelectedIndex in selectedAppsQueue.indices) {
                    selectedAppsQueue[queueSelectedIndex]
                } else null
                
                val activeApps = selectedAppsQueue.filter { !it.isMinimized }
                val minimizedApps = selectedAppsQueue.filter { it.isMinimized }

                // Sort minimized apps: newest first (highest timestamp), oldest last
                val sortedMinimized = minimizedApps.sortedByDescending { 
                    minimizedAtTimestamps[it.getBasePackage()] ?: 0L 
                }

                // Active apps maintain their queue position (which defines their layout rect)
                // No re-sorting needed for active apps - their queue index IS their screen position
                selectedAppsQueue.clear()
                selectedAppsQueue.addAll(activeApps)
                selectedAppsQueue.addAll(sortedMinimized)
                
                // [FIX] Restore selection to same app after sort
                if (selectedApp != null) {
                    val newIndex = selectedAppsQueue.indexOfFirst { 
                        it.packageName == selectedApp.packageName && it.className == selectedApp.className 
                    }
                    if (newIndex >= 0) {
                        queueSelectedIndex = newIndex
                    }
                }
            }

            private fun updateAllUIs() {
                Log.e(TAG, "INSIDE_UPDATE_ALL_UIS vq=${visualQueueView != null}")
                // 1. Update Drawer Dock
                updateSelectedAppsDock()
                drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
                
                // 2. Update Visual Queue HUD (if exists)
                if (visualQueueView != null) {
                    val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
                    recycler?.adapter?.notifyDataSetChanged()
                }
            }    private fun updateSelectedAppsDock() { val dock = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler) ?: return; if (selectedAppsQueue.isEmpty() || currentMode != MODE_SEARCH) { dock.visibility = View.GONE } else { dock.visibility = View.VISIBLE; dock.adapter?.notifyDataSetChanged(); dock.scrollToPosition(selectedAppsQueue.size - 1) } }
    override fun refreshSearchList() { val query = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString() ?: ""; filterList(query) }

    private fun updateAllUIs2() {
        if (visualQueueView != null) {
            val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
            val adapter = recycler?.adapter
            val count = adapter?.itemCount ?: 0
            if (count > 0) {
                adapter?.notifyItemRangeChanged(0, count)
            }
        }
        val dockAdapter = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)?.adapter
        if ((dockAdapter?.itemCount ?: 0) > 0) dockAdapter?.notifyItemRangeChanged(0, dockAdapter.itemCount)
        val rofiAdapter = drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter
        if ((rofiAdapter?.itemCount ?: 0) > 0) rofiAdapter?.notifyItemRangeChanged(0, rofiAdapter.itemCount)
    }

    private fun testFocusUpdate() {
        safeToast("TEST_FOCUS_UPDATE CALLED")
    }
    
    // Helper to get searchable text from any option type
    private fun getOptionSearchText(item: Any): String {
        return when (item) {
            is LayoutOption -> item.name
            is ResolutionOption -> item.name
            is ActionOption -> item.name
            is ToggleOption -> item.name
            is RefreshHeaderOption -> item.text
            is RefreshItemOption -> item.label
            is ProfileOption -> item.name
            is LegendOption -> item.text
            is KeybindOption -> "${item.def.label} ${item.def.description}"
            is DpiOption -> "DPI Density"
            is FontSizeOption -> "Font Size Text"
            is HeightOption -> "Drawer Height"
            is WidthOption -> "Drawer Width"
            is BubbleSizeOption -> "Bubble Icon Size"
            is MarginOption -> if (item.type == 0) "Top Margin" else "Bottom Margin"
            is IconOption -> item.name
            is CustomModConfigOption -> "Custom Modifier Key"
            is MainActivity.AppInfo -> item.label
            else -> ""
        }
    }
    
    private fun filterList(query: String) {
        val actualQuery = query.trim()
        
        if (currentMode == MODE_SEARCH) {
            // App search mode - use allAppsList
            displayList.clear()
            val filtered = if (actualQuery.isEmpty()) { allAppsList } else { allAppsList.filter { it.label.contains(actualQuery, ignoreCase = true) } }
            val sorted = filtered.sortedWith(compareBy<MainActivity.AppInfo> { it.packageName != PACKAGE_BLANK }.thenByDescending { it.isFavorite }.thenBy { it.label.lowercase() })
            displayList.addAll(sorted)
        } else {
            // Menu filtering mode - use unfilteredDisplayList
            displayList.clear()
            if (actualQuery.isEmpty()) {
                displayList.addAll(unfilteredDisplayList)
            } else {
                val filtered = unfilteredDisplayList.filter { item ->
                    getOptionSearchText(item).contains(actualQuery, ignoreCase = true)
                }
                displayList.addAll(filtered)
            }
        }
        
        // Reset selection to first item
        selectedListIndex = 0
        drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }

    // Helper to scrub dead apps from focus history so we don't accidentally re-launch them
    private fun removeFromFocusHistory(pkg: String) {
        val basePkg = if (pkg.contains(":")) pkg.substringBefore(":") else pkg
        val alias = if (basePkg == "com.google.android.apps.bard") "com.google.android.googlequicksearchbox" else null

        // Shift history down if the killed app was the active one
        if (activePackageName == basePkg || activePackageName == alias) {
            activePackageName = lastValidPackageName
            lastValidPackageName = secondLastValidPackageName
            secondLastValidPackageName = null
        }
        // Also scrub from other positions if it's lingering there
        else {
            if (lastValidPackageName == basePkg || lastValidPackageName == alias) {
                lastValidPackageName = secondLastValidPackageName
                secondLastValidPackageName = null
            } else if (secondLastValidPackageName == basePkg || secondLastValidPackageName == alias) {
                secondLastValidPackageName = null
            }
        }
    }

    // [FOCUS_GUARD] Keep DroidOS focus/history consistent with visible queue apps.
    // If a focus entry points to a queue app that is now hidden/minimized/removed,
    // clear/scrub it so stale focus cannot resurrect dead windows.
    private fun sanitizeFocusState(reason: String) {
        fun shouldDrop(focusPkg: String?): Boolean {
            if (focusPkg.isNullOrEmpty()) return false

            val focusBase = focusPkg.substringBefore(":")
            val matches = selectedAppsQueue.filter { app ->
                val appBase = app.getBasePackage()
                appBase == focusBase ||
                    app.packageName == focusPkg ||
                    app.packageName == focusBase ||
                    (focusBase == "com.google.android.googlequicksearchbox" && appBase == "com.google.android.apps.bard")
            }

            // Not a managed queue app -> leave focus untouched (external fullscreen/system apps).
            if (matches.isEmpty()) return false

            // Managed queue app has no visible non-minimized instance -> stale focus.
            return matches.none { !it.isMinimized && it.packageName != PACKAGE_BLANK }
        }

        var changed = false

        if (shouldDrop(activePackageName)) {
            activePackageName = null
            changed = true
        }

        if (shouldDrop(lastValidPackageName)) {
            lastValidPackageName = secondLastValidPackageName?.takeUnless { shouldDrop(it) }
            secondLastValidPackageName = null
            changed = true
        } else if (shouldDrop(secondLastValidPackageName)) {
            secondLastValidPackageName = null
            changed = true
        }

        if (activePackageName != null && activePackageName == lastValidPackageName) {
            lastValidPackageName = secondLastValidPackageName
            secondLastValidPackageName = null
            changed = true
        }
        if (lastValidPackageName != null && lastValidPackageName == secondLastValidPackageName) {
            secondLastValidPackageName = null
            changed = true
        }

        if (changed) {
            Log.d(TAG, "sanitizeFocusState: removed stale focus/history ($reason)")
        }
    }

    // === ADD TO SELECTION - START ===
    // Adds app to the selection queue, handles removal if already selected
    // Uses proper package name extraction for force-stop and launch operations
    override fun addToSelection(app: MainActivity.AppInfo) {
        dismissKeyboardAndRestore()
        val et = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar)
        
        // OPEN_MOVE_TO/OPEN_SWAP MODE: Intercept app selection
        if ((isOpenMoveToMode || isOpenSwapMode) && openMoveToApp == null) {
            if (app.packageName == PACKAGE_BLANK) {
                safeToast("Cannot open a blank spacer")
                return
            }
            
            // Store the selected app
            openMoveToApp = app
            
            // Switch focus to queue for slot selection
            currentFocusArea = FOCUS_QUEUE
            queueSelectedIndex = 0
            showSlotNumbersInQueue = true
            
            // Tell keyboard to capture all keys for slot selection
            val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
            captureIntent.setPackage(PACKAGE_TRACKPAD)
            captureIntent.putExtra("CAPTURE", true)
            sendBroadcast(captureIntent)
            
            // Update UI
            debugStatusView?.visibility = View.VISIBLE
            debugStatusView?.text = "Open & Move To: Select slot # (1-${selectedAppsQueue.size + 1})"
            updateSelectedAppsDock()
            drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
            
            // Request focus on queue area
            val selectedRecycler = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)
            selectedRecycler?.requestFocus()
            
            return
        }
        
        // Handle blank spacer
        if (app.packageName == PACKAGE_BLANK) { 
            selectedAppsQueue.add(app)
            sortAppQueue()
            updateSelectedAppsDock()
            drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
            if (isInstantMode) applyLayoutImmediate()
            return 
        }
        
        // Debug: show what app is being selected
        debugShowAppIdentification("SELECT", app.packageName, app.className)
        
        // Check if app is already in queue (by matching both package and class for precision)
        val existing = selectedAppsQueue.find { 
            it.packageName == app.packageName && it.className == app.className 
        }
        
        if (existing != null) { 
            // Remove from queue
            selectedAppsQueue.remove(existing)
            
            // Force stop using BASE package name (shell commands need base pkg)
            val basePkg = if (app.packageName.contains(":")) app.packageName.substringBefore(":") else app.packageName
            Thread { 
                try { 
                    shellService?.forceStop(basePkg) 
                } catch(e: Exception) {
                    Log.e(TAG, "forceStop failed for $basePkg", e)
                } 
            }.start()
            
            safeToast("Removed ${app.label}")
            sortAppQueue()
            updateAllUIs()
            et.setText("")
            if (isInstantMode) applyLayoutImmediate() 
        } else { 
            // Add to queue and launch if instant mode
            app.isMinimized = false
            selectedAppsQueue.add(app)
            sortAppQueue()
            updateAllUIs()
            et.setText("")
            
            if (isInstantMode) { 
                // Launch using both API and Shell for reliability
                launchViaApi(app.packageName, app.className, null)
                launchViaShell(app.packageName, app.className, null)
                
                // Delayed layout application to allow app to start
                uiHandler.postDelayed({ applyLayoutImmediate() }, 200)
                uiHandler.postDelayed({ applyLayoutImmediate() }, 800) 
            } 
            
            // [FIX] Close drawer after successful add
            toggleDrawer()
        }
    }
    // === ADD TO SELECTION - END ===

    override fun toggleFavorite(app: MainActivity.AppInfo) { val newState = AppPreferences.toggleFavorite(this, app.packageName); app.isFavorite = newState; allAppsList.find { it.packageName == app.packageName }?.isFavorite = newState }


    // === LAUNCH VIA API - START ===
    // Launches app using Android API with launch bounds
    private fun launchViaApi(pkg: String, className: String?, bounds: Rect?) {
        try {
            val basePkg = if (pkg.contains(":")) pkg.substringBefore(":") else pkg

            debugShowAppIdentification("LAUNCH_API", basePkg, className)

            val intent: Intent?

            if (!className.isNullOrEmpty() && className != "null" && className != "default") {
                intent = Intent()
                intent.setClassName(basePkg, className)
                intent.action = Intent.ACTION_MAIN
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
            } else {
                intent = packageManager.getLaunchIntentForPackage(basePkg)
            }

            if (intent == null) {
                Log.w(TAG, "launchViaApi: No intent for $basePkg, trying shell")
                launchViaShell(basePkg, className, bounds)
                return
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val options = android.app.ActivityOptions.makeBasic()
            options.setLaunchDisplayId(currentDisplayId)

            if (bounds != null) {
                options.setLaunchBounds(bounds)
            }

            startActivity(intent, options.toBundle())

        } catch (e: Exception) {
            Log.e(TAG, "launchViaApi FAILED, trying shell", e)
            launchViaShell(pkg, className, bounds)
        }
    }
    // === LAUNCH VIA API - END ===


    // === LAUNCH VIA SHELL - START ===
    // Launches app via shell with freeform windowing mode
    private fun launchViaShell(pkg: String, className: String?, bounds: Rect?) {
        try {
            val basePkg = if (pkg.contains(":")) pkg.substringBefore(":") else pkg

            debugShowAppIdentification("LAUNCH_SHELL", basePkg, className)

            val component = if (!className.isNullOrEmpty() && className != "null" && className != "default") {
                "$basePkg/$className"
            } else {
                null
            }

            // Build launch command with freeform mode (--windowingMode 5)
            val cmd = if (component != null) {
                "am start -n $component --display $currentDisplayId --windowingMode 5 --user 0"
            } else {
                "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"
            }


            Thread {
                try {
                    shellService?.runCommand(cmd)
                } catch (e: Exception) {
                    Log.e(TAG, "launchViaShell: FAILED", e)
                }
            }.start()

        } catch (e: Exception) {
            Log.e(TAG, "launchViaShell FAILED: $pkg", e)
        }
    }
    // === LAUNCH VIA SHELL - END ===

    // === FOCUS VIA TASK - START ===
    // Focuses an already-visible freeform window using am start --activity-brought-to-front.
    // Executed inline from WM command queue to preserve command ordering.
    private fun focusViaTask(pkg: String, bounds: Rect?) {
        try {
            val basePkg = if (pkg.contains(":")) pkg.substringBefore(":") else pkg
            val appEntry = selectedAppsQueue.find { it.getBasePackage() == basePkg }

            // Skip stale focus targets (already removed/minimized/hidden)
            if (appEntry == null || appEntry.packageName == PACKAGE_BLANK || appEntry.isMinimized) {
                Log.w(TAG, "focusViaTask skipped stale target: $basePkg")
                return
            }

            val className = appEntry.className
            val component = if (!className.isNullOrEmpty() && className != "null" && className != "default") {
                "$basePkg/$className"
            } else {
                null
            }

            val cmd = if (component != null) {
                "am start -n $component --activity-brought-to-front --display $currentDisplayId --windowingMode 5 --user 0"
            } else {
                "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --activity-brought-to-front --display $currentDisplayId --windowingMode 5 --user 0"
            }

            shellService?.runCommand(cmd)
        } catch (e: Exception) {
            Log.e(TAG, "focusViaTask FAILED: $pkg", e)
        }
    }
    // === FOCUS VIA TASK - END ===

    // [CURSOR] Send cursor to center of app bounds when focusing via DroidOS
    // Bounds from getLayoutRects() are in layout-relative coordinates (top margin = y:0),
    // so we add the top margin offset to get actual screen coordinates.
    private fun sendCursorToAppCenter(bounds: Rect?) {
        if (bounds == null) return
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = dm.getDisplay(currentDisplayId) ?: return
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)
        val h = metrics.heightPixels
        val topPx = (h * topMarginPercent / 100f).toInt()
        val centerX = (bounds.left + bounds.right) / 2f
        val centerY = (bounds.top + bounds.bottom) / 2f + topPx
        val intent = Intent("SET_CURSOR_POS")
        intent.setPackage(PACKAGE_TRACKPAD)
        intent.putExtra("X", centerX)
        intent.putExtra("Y", centerY)
        sendBroadcast(intent)
    }

    
    private fun toggleVirtualDisplay() {
        if (virtualDisplay == null) {
            setKeepScreenOn(true)
            val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            
            // 1. Create ImageReader to act as the screen buffer
            virtualImageReader = ImageReader.newInstance(1920, 1080, PixelFormat.RGBA_8888, 2)
            
            // 2. Set flags (Public allows other apps/system to see it; Presentation allows secondary content)
            val flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
            
            // 3. Create Display with Surface
            virtualDisplay = dm.createVirtualDisplay(
                "DroidOS-Virtual", 
                1920, 1080, 320, 
                virtualImageReader?.surface, 
                flags
            )
            
            Toast.makeText(this, "Virtual Display Created (1080p)", Toast.LENGTH_SHORT).show()
        } else {
            // Optional: Destroy if toggled again? For now, just warn.
            Toast.makeText(this, "Virtual Display Already Active", Toast.LENGTH_SHORT).show()
        }
    }

    fun switchDisplay() {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        
        // 1. Determine where the Bubble actually is (Source of Truth)
        var actualCurrentId = currentDisplayId
        // Safe check for view location
        if (bubbleView != null && bubbleView?.isAttachedToWindow == true) {
             bubbleView?.display?.let { actualCurrentId = it.displayId }
        }

        var targetId = actualCurrentId

        // 2. Logic: Physical <-> Virtual
        if (actualCurrentId == 0 || actualCurrentId == 1) {
            // CASE: We are on a Physical Display -> Go to Virtual
            
            // Find the first display that ISN'T 0 or 1
            val virtualDisplay = dm.displays.firstOrNull { it.displayId != 0 && it.displayId != 1 }
            
            if (virtualDisplay != null) {
                targetId = virtualDisplay.displayId
                setKeepScreenOn(true)  // Keep screen on when switching TO virtual display
            } else {
                // Fallback: If no virtual display exists, just toggle normally so button works
                targetId = if (actualCurrentId == 0) 1 else 0
                Toast.makeText(this, "No Virtual Display Active", Toast.LENGTH_SHORT).show()
            }
        } else {
            // CASE: We are on Virtual -> Go back to ACTIVE Physical Display
            
            val d0 = dm.getDisplay(0)
            val d1 = dm.getDisplay(1)
            setKeepScreenOn(false)
            
            // Check states to see which screen is actually awake
            val isZeroOn = d0?.state == Display.STATE_ON
            val isOneOn = d1?.state == Display.STATE_ON
            
            if (isZeroOn) {
                targetId = 0
            } else if (isOneOn) {
                targetId = 1
            } else {
                // If both are reported off (unlikely while using phone), default to Main
                targetId = 0
            }
        }

        // 3. Execute Switch if target is different
        if (targetId != actualCurrentId) {
            performDisplayChange(targetId)
        }
    }
    private fun performDisplayChange(newId: Int) {
        lastManualSwitchTime = System.currentTimeMillis()
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val targetDisplay = dm.getDisplay(newId) ?: return
        
        // 1. CLEANUP using the captured manager (Robust)
        try { 
            val wm = attachedWindowManager ?: windowManager
            
            if (bubbleView != null) {
                try { wm.removeView(bubbleView) } catch(e: Exception) {}
            }
            
            if (drawerView != null && isExpanded) {
                try { wm.removeView(drawerView) } catch(e: Exception) {}
            }
        } catch (e: Exception) { Log.e(TAG, "Cleanup failed", e) }

        // Clear cached auxiliary views using CURRENT (Old) WM before switching context
        if (visualQueueView != null) {
            // Try stored WM first, then fallback
            try { visualQueueWindowManager?.removeView(visualQueueView) } catch(e: Exception){
                try { windowManager.removeView(visualQueueView) } catch(e2: Exception){}
            }
            visualQueueView = null
            isVisualQueueVisible = false
            visualQueueWindowManager = null
        }
        if (keyPickerView != null) {
            try { windowManager.removeView(keyPickerView) } catch(e: Exception){}
            keyPickerView = null
        }

        // 2. SWITCH
        currentDisplayId = newId
        setupDisplayContext(currentDisplayId)
        targetDisplayIndex = currentDisplayId
        AppPreferences.setTargetDisplayIndex(this, targetDisplayIndex)
        
        // WATCHDOG STATE LOG
        if (currentDisplayId >= 2) {
            Log.w("DROIDOS_WATCHDOG", ">>> WATCHDOG ENABLED (Targeting Virtual D$currentDisplayId) <<<")
        } else {
            Log.w("DROIDOS_WATCHDOG", ">>> WATCHDOG DISABLED (Targeting Physical D$currentDisplayId) <<<")
        }
        
        // [NEW] Launch Wallpaper if on Virtual Display
        // This ensures there is always a window at the bottom of the stack to hold focus.
        if (currentDisplayId >= 2) {
            try {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("WALLPAPER_MODE", true)
                
                val options = android.app.ActivityOptions.makeBasic()
                options.setLaunchDisplayId(currentDisplayId)
                
                startActivity(intent, options.toBundle())
                
                // Optional: Move it to back after a short delay so it doesn't cover existing apps
                // (If you are switching to a display that already has apps open)
                uiHandler.postDelayed({
                    try {
                        val tid = shellService?.getTaskId(packageName, null) ?: -1
                        if (tid != -1) shellService?.moveTaskToBack(tid)
                    } catch(e: Exception){}
                }, 500)
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to launch wallpaper", e)
            }
        }

        // 3. REBUILD
        setupBubble()
        setupDrawer()
        
        loadDisplaySettings(currentDisplayId)
        updateBubbleIcon()
        isExpanded = false
        safeToast("Switched to Display $currentDisplayId (${targetDisplay.name})")

        // [FIX] Apply layout immediately if in Instant Mode
        if (isInstantMode) {
            uiHandler.postDelayed({ applyLayoutImmediate() }, 500)
        }
    }
    private fun toggleVirtualDisplay(enable: Boolean) { isVirtualDisplayActive = enable; Thread { try { if (enable) { shellService?.runCommand("settings put global overlay_display_devices \"1920x1080/320\""); uiHandler.post { safeToast("Creating Virtual Display... Wait a moment, then Switch Display.") } } else { shellService?.runCommand("settings delete global overlay_display_devices"); uiHandler.post { safeToast("Destroying Virtual Display...") } } } catch (e: Exception) { Log.e(TAG, "Virtual Display Toggle Failed", e) } }.start(); if (currentMode == MODE_SETTINGS) uiHandler.postDelayed({ switchMode(MODE_SETTINGS) }, 500) }

    // --- v2.0 SCREEN OFF LOGIC ---
    private fun performScreenOff() {
        vibrate()
        isScreenOffState = true
        // [FIX] Dismiss visual queue on screen off to prevent it getting stuck
        if (isVisualQueueVisible) {
            hideVisualQueue()
            pendingCommandId = null
        }
        safeToast("Screen Off: Double press Power Button to turn on")
        
        if (useAltScreenOff) {
             Thread {
                 try {
                     if (shellService != null) {
                         shellService?.setBrightness(0, -1)
                         uiHandler.post { safeToast("Pixels OFF (Alternate Mode)") }
                     } else {
                         safeToast("Service Disconnected!")
                     }
                 } catch (e: Exception) {
                     Log.e(TAG, "Binder Call Failed", e)
                     safeToast("Error: ${e.message}")
                 }
            }.start()
        } else {
            Thread { try { shellService?.setScreenOff(0, true) } catch (e: Exception) {} }.start()
            safeToast("Screen OFF (SurfaceControl)")
        }
    }
    
    private fun wakeUp() {
        vibrate()
        isScreenOffState = false
        
        Thread { try { shellService?.setBrightness(0, 128) } catch (e: Exception) {} }.start()
        Thread { try { shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start()

        safeToast("Screen On")
        if (currentMode == MODE_SETTINGS) drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }

    override fun applyLayoutImmediate(focusPackage: String?) { executeLaunch(selectedLayoutType, closeDrawer = false, focusPackage = focusPackage) }
    private fun applyLayoutImmediate() { applyLayoutImmediate(null) }

    private fun retileExistingWindows() {
        if (!isBound || shellService == null) return
        Thread {
            try {
                val rects = getLayoutRects()
                val activeApps = selectedAppsQueue.filter { !it.isMinimized }
                val packages = mutableListOf<String>()
                val boundsList = mutableListOf<Int>()
                for (i in 0 until minOf(activeApps.size, rects.size)) {
                    val app = activeApps[i]
                    if (app.packageName == PACKAGE_BLANK) continue
                    val basePkg = app.getBasePackage()
                    val bounds = rects[i]
                    packageRectCache[basePkg] = bounds
                    packages.add(basePkg)
                    boundsList.addAll(listOf(bounds.left, bounds.top, bounds.right, bounds.bottom))
                }
                if (packages.isNotEmpty()) {
                    shellService?.batchResize(packages, boundsList.toIntArray())
                }
            } catch (e: Exception) {
                Log.e(TAG, "retileExistingWindows failed", e)
            }
        }.start()
    }

    private fun ensureQueueLoadedForCommands() {
        if (selectedAppsQueue.isNotEmpty()) return
        val lastQueue = AppPreferences.getLastQueue(this)
        if (lastQueue.isEmpty()) return
        if (allAppsList.isEmpty()) loadInstalledApps()
        restoreQueueFromPrefs()
    }

    private fun requestHeadlessRetile(reason: String, delayMs: Long = 200L) {
        pendingHeadlessRetile = true
        pendingHeadlessRetileReason = reason
        uiHandler.removeCallbacks(headlessRetileRunnable)
        uiHandler.postDelayed(headlessRetileRunnable, delayMs)
    }

    private fun runPendingHeadlessRetile() {
        if (!pendingHeadlessRetile) return
        if (!isBound || shellService == null) {
            return
        }
        if (isExecuting) {
            uiHandler.postDelayed(headlessRetileRunnable, 200)
            return
        }
        pendingHeadlessRetile = false
        pendingHeadlessRetileReason = null
        refreshDisplayId()
        retileExistingWindows()
    }

    private fun tryBindShizukuIfPermitted() {
        try {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                bindShizuku()
            }
        } catch (e: Exception) {}
    }



    // === RESTORE QUEUE IMMEDIATE - START ===
    // Loads the saved queue from preferences immediately without checking shell/running state.
    // Ensures Visual Queue is populated even if the Drawer hasn't been opened yet.
    private fun restoreQueueFromPrefs() {
        val lastQueue = AppPreferences.getLastQueue(this)
        val minimizedPkgs = AppPreferences.getMinimizedPackages(this)
        selectedAppsQueue.clear()
        
        for (identifier in lastQueue) {
            if (identifier == PACKAGE_BLANK) {
                selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null))
            } else {
                val appInfo = findAppByIdentifier(identifier)
                if (appInfo != null) {
                    // [FIX] Restore minimized state from saved preferences
                    appInfo.isMinimized = minimizedPkgs.contains(appInfo.getBasePackage())
                    selectedAppsQueue.add(appInfo)
                }
            }
        }
        
        // Preserve sorting (Active -> Minimized)
        sortAppQueue()
        updateAllUIs()
    }
    // === RESTORE QUEUE IMMEDIATE - END ===

    // === FETCH RUNNING APPS - START ===
    // Fetches visible and running apps from system, merges with saved queue
    // Handles Gemini/Google trampoline case where Gemini runs inside Google Quick Search Box task
    // Key insight: Gemini (com.google.android.apps.bard) shows up as com.google.android.googlequicksearchbox
    // in system visibility reports because it trampolines through Google's SearchActivity
    private fun fetchRunningApps() {
        if (shellService == null) return

        Thread {
            try {
                val visiblePackages = shellService!!.getVisiblePackages(currentDisplayId)
                val allRunning = shellService!!.getAllRunningPackages()
                val lastQueue = AppPreferences.getLastQueue(this)


                uiHandler.post {
                    selectedAppsQueue.clear()

                    // === PHASE 1: Restore apps from saved queue ===
                    for (identifier in lastQueue) {
                        if (identifier == PACKAGE_BLANK) {
                            selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null))
                        } else {
                            // Find by identifier (handles both package-only and package:suffix formats)
                            val appInfo = findAppByIdentifier(identifier)

                            if (appInfo != null) {
                                val basePkg = appInfo.getBasePackage()

                                // Check if running - handle Gemini special case
                                val isRunning = if (basePkg == "com.google.android.apps.bard") {
                                    // Gemini trampolines through Google Quick Search Box
                                    allRunning.contains(basePkg) ||
                                    allRunning.contains("com.google.android.googlequicksearchbox")
                                } else {
                                    allRunning.contains(basePkg)
                                }

                                if (isRunning) {
                                    // Check visibility - handle Gemini special case
                                    val isVisible = if (basePkg == "com.google.android.apps.bard") {
                                        visiblePackages.contains(basePkg) ||
                                        visiblePackages.contains("com.google.android.googlequicksearchbox")
                                    } else {
                                        visiblePackages.contains(basePkg)
                                    }

                                    // [FIX] Check for recent manual override (within 5 seconds)
                                    // Prevents app from flickering grey/hidden immediately after unminimizing
                                    val lastManualTime = manualStateOverrides[basePkg] ?: 0L
                                    val isRecentOverride = (System.currentTimeMillis() - lastManualTime) < 5000

                                    if (!isRecentOverride) {
                                        appInfo.isMinimized = !isVisible
                                    } else {
                                    }

                                    selectedAppsQueue.add(appInfo)
                                }
                            } else {
                                Log.w(DEBUG_TAG, "fetchRunningApps: Could not find app for identifier=$identifier")
                            }
                        }
                    }

                    // === PHASE 2: Add newly visible apps not already in queue ===
                    for (pkg in visiblePackages) {
                        // Skip if it's Google Quick Search Box - we handle Gemini specifically
                        // and don't want to auto-add Google if the user has Gemini in queue
                        val isGoogleQSB = pkg == "com.google.android.googlequicksearchbox"

                        // Check if already in queue by package or related package
                        val alreadyInQueue = selectedAppsQueue.any { queuedApp ->
                            val queuedBasePkg = queuedApp.getBasePackage()
                            when {
                                // Direct match
                                queuedBasePkg == pkg -> true
                                // Gemini is in queue, and we see Google QSB (trampoline case)
                                isGoogleQSB && queuedBasePkg == "com.google.android.apps.bard" -> true
                                // Google QSB is in queue, and we see Google QSB
                                isGoogleQSB && queuedBasePkg == "com.google.android.googlequicksearchbox" -> true
                                else -> false
                            }
                        }

                        if (!alreadyInQueue) {
                            val appInfo = allAppsList.find { it.packageName == pkg }
                            if (appInfo != null) {
                                appInfo.isMinimized = false
                                selectedAppsQueue.add(appInfo)
                            }
                        }
                    }

                    sortAppQueue() // Now empty/disabled
                    updateAllUIs()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fetch failed", e)
            }
        }.start()
    }
    // === FETCH RUNNING APPS - END ===

    // === FIND APP BY IDENTIFIER - START ===
    // Finds an AppInfo from allAppsList by its identifier
    // Handles both simple package names and compound identifiers (package:suffix)
    // Also handles the getIdentifier() format used for saving
    private fun findAppByIdentifier(identifier: String): MainActivity.AppInfo? {
        // First, try exact getIdentifier() match
        val exactMatch = allAppsList.find { it.getIdentifier() == identifier }
        if (exactMatch != null) return exactMatch

        // If identifier contains ":", try matching the components
        if (identifier.contains(":")) {
            val basePkg = identifier.substringBefore(":")
            val suffix = identifier.substringAfter(":")

            // Special case: "com.google.android.googlequicksearchbox:gemini" -> find Gemini app
            if (basePkg == "com.google.android.googlequicksearchbox" && suffix == "gemini") {
                // Look for the standalone Gemini app first
                val geminiStandalone = allAppsList.find { it.packageName == "com.google.android.apps.bard" }
                if (geminiStandalone != null) return geminiStandalone

                // Fall back to Google QSB with Gemini activity
                val geminiInGoogle = allAppsList.find {
                    it.packageName == basePkg &&
                    (it.className?.lowercase()?.contains("gemini") == true ||
                     it.className?.lowercase()?.contains("assistant") == true ||
                     it.className?.lowercase()?.contains("bard") == true)
                }
                if (geminiInGoogle != null) return geminiInGoogle
            }

            // Try matching by base package
            val byBasePkg = allAppsList.find { it.packageName == basePkg }
            if (byBasePkg != null) return byBasePkg
        }

        // Simple package name match
        return allAppsList.find { it.packageName == identifier }
    }
    // === FIND APP BY IDENTIFIER - END ===


    override fun selectLayout(opt: LayoutOption) { 
        dismissKeyboardAndRestore()
        selectedLayoutType = opt.type
        activeCustomRects = opt.customRects
        val os = orientSuffix()
        
        if (opt.type == LAYOUT_CUSTOM_DYNAMIC) { 
            activeCustomLayoutName = opt.name
            AppPreferences.saveLastCustomLayoutName(this, opt.name, currentDisplayId)
            AppPreferences.saveLastCustomLayoutName(this, opt.name, currentDisplayId, os)
        } else { 
            activeCustomLayoutName = null
            AppPreferences.saveLastCustomLayoutName(this, null, currentDisplayId)
            AppPreferences.saveLastCustomLayoutName(this, null, currentDisplayId, os)
        }
        
        AppPreferences.saveLastLayout(this, opt.type, currentDisplayId)
        AppPreferences.saveLastLayout(this, opt.type, currentDisplayId, os)
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        
        if (isInstantMode) applyLayoutImmediate() 
    }
    private fun saveCurrentAsCustom() { Thread { try { val rawLayouts = shellService!!.getWindowLayouts(currentDisplayId); if (rawLayouts.isEmpty()) { safeToast("Found 0 active app windows"); return@Thread }; val rectStrings = mutableListOf<String>(); for (line in rawLayouts) { val parts = line.split("|"); if (parts.size == 2) { rectStrings.add(parts[1]) } }; if (rectStrings.isEmpty()) { safeToast("Found 0 valid frames"); return@Thread }; val count = rectStrings.size; var baseName = "$count Apps - Custom"; val existingNames = AppPreferences.getCustomLayoutNames(this); var counter = 1; var finalName = "$baseName $counter"; while (existingNames.contains(finalName)) { counter++; finalName = "$baseName $counter" }; AppPreferences.saveCustomLayout(this, finalName, rectStrings.joinToString("|")); safeToast("Saved: $finalName"); uiHandler.post { switchMode(MODE_LAYOUTS) } } catch (e: Exception) { Log.e(TAG, "Failed to save custom layout", e); safeToast("Error saving: ${e.message}") } }.start() }


// =================================================================================
    // FUNCTION: applyRefreshRate
    // SUMMARY: Forces a refresh rate. If hardware doesn't support the rate (e.g. 60Hz 
    //          on 120Hz glasses), attempts multiple software throttling methods.
    //          For displays with only 120Hz (like XReal), uses frame rate throttling
    //          via SurfaceFlinger which limits app rendering (frames duplicate on HW).
    // =================================================================================
    override fun applyRefreshRate(targetRate: Float) {
        manualRefreshRateSet = true 
        safeToast("Applying ${targetRate.toInt()}Hz...")
        
        if (shellService == null) {
            safeToast("Error: Shizuku Disconnected!")
            return
        }
        
        Thread {
            try {
                val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                val display = dm.getDisplay(currentDisplayId)
                val modes = display?.supportedModes ?: emptyArray()
                val currentMode = display?.mode
                
                // Log available modes for debugging
                modes.forEach { mode ->
                }
                
                // Check if hardware supports target rate
                var bestModeId = -1
                var hasHardwareSupport = false
                for (mode in modes) {
                    if (Math.abs(mode.refreshRate - targetRate) < 1.0f) {
                        bestModeId = mode.modeId
                        hasHardwareSupport = true
                        break
                    }
                }
                
                val width = currentMode?.physicalWidth ?: 1920
                val height = currentMode?.physicalHeight ?: 1080
                val rateStr = String.format("%.1f", targetRate)
                val rateInt = targetRate.toInt()
                
                if (hasHardwareSupport) {
                    // === METHOD A: Hardware mode exists - use standard approach ===
                    
                    // 1. App-Level Window Override
                    uiHandler.post {
                        try {
                            if (bubbleView != null) {
                                bubbleParams.preferredDisplayModeId = bestModeId
                                val wm = attachedWindowManager ?: windowManager
                                wm.updateViewLayout(bubbleView, bubbleParams)
                            }
                        } catch(e: Exception) {
                            Log.e(TAG, "Window mode override failed", e)
                        }
                    }
                    
                    // 2. System settings
                    shellService?.runCommand("settings put system peak_refresh_rate $rateStr")
                    shellService?.runCommand("settings put system min_refresh_rate $rateStr")
                    
                    // 3. Force display mode
                    val forceCmd = "cmd display set-user-preferred-display-mode $currentDisplayId $width $height $targetRate"
                    shellService?.runCommand(forceCmd)
                    
                    activeRefreshRateLabel = "${rateInt}Hz"
                    
                } else {
                    // === METHOD B: No hardware support - use software throttling ===
                    Log.w(TAG, "Hardware does NOT support ${targetRate}Hz - using software throttling")
                    
                    // Method B1: SurfaceFlinger frame rate override for the display
                    // This tells SF to throttle frame composition for this display
                    val sfThrottleCmd = "service call SurfaceFlinger 1035 i32 $currentDisplayId f $targetRate"
                    val sfResult = shellService?.runCommand(sfThrottleCmd)
                    
                    // Method B2: Set frame rate policy via display service
                    // This affects the render frame rate policy
                    val policyCmd = "cmd display set-match-content-frame-rate-pref 1"
                    shellService?.runCommand(policyCmd)
                    
                    // Method B3: Use render frame rate limit via SurfaceFlinger properties
                    // Note: These may require root or special permissions
                    shellService?.runCommand("service call SurfaceFlinger 1008 i32 ${rateInt}")
                    
                    // Method B4: Set the desired display mode specs with clamped range
                    // This requests SF to limit the render rate even if HW is higher
                    val specCmd = "cmd display set-desired-display-mode-specs $currentDisplayId -p $targetRate $targetRate -r $targetRate $targetRate"
                    val specResult = shellService?.runCommand(specCmd)
                    
                    // Method B5: Try forcing the base/peak refresh for this display specifically
                    shellService?.runCommand("settings put system min_refresh_rate_$currentDisplayId $rateStr")
                    shellService?.runCommand("settings put system peak_refresh_rate_$currentDisplayId $rateStr")
                    
                    // Method B6: Global settings (may not affect external displays but worth trying)
                    shellService?.runCommand("settings put system peak_refresh_rate $rateStr")
                    shellService?.runCommand("settings put system min_refresh_rate $rateStr")
                    
                    // Method B7: Attempt user-preferred mode anyway (sometimes kicks SF into action)
                    val forceCmd = "cmd display set-user-preferred-display-mode $currentDisplayId $width $height $targetRate"
                    shellService?.runCommand(forceCmd)
                    
                    activeRefreshRateLabel = "Limit: ${rateInt}Hz (SW)"
                    
                    Log.i(TAG, "Software throttle applied - display still at HW rate but rendering limited")
                }
                
                Thread.sleep(800)
                
                // Re-query to see what actually happened
                val newDisplay = dm.getDisplay(currentDisplayId)
                val actualRate = newDisplay?.refreshRate ?: 0f
                
                uiHandler.post { 
                    switchMode(MODE_REFRESH)
                    if (hasHardwareSupport) {
                        safeToast("Applied: ${rateInt}Hz")
                    } else {
                        safeToast("SW Limit: ${rateInt}Hz (HW: ${actualRate.toInt()}Hz)")
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set refresh rate", e)
                uiHandler.post { safeToast("Error: ${e.message}") }
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: applyRefreshRate
    // =================================================================================




    private fun applyOrientation() { Thread { try { when (currentOrientationMode) { 1 -> { shellService?.runCommand("settings put system accelerometer_rotation 0"); shellService?.runCommand("settings put system user_rotation 0") }; 2 -> { shellService?.runCommand("settings put system accelerometer_rotation 0"); shellService?.runCommand("settings put system user_rotation 1") }; else -> { shellService?.runCommand("settings put system accelerometer_rotation 1") } } } catch (e: Exception) { e.printStackTrace() } }.start() }
    override fun applyResolution(opt: ResolutionOption) { dismissKeyboardAndRestore(); if (opt.index != -1) { selectedResolutionIndex = opt.index; AppPreferences.saveDisplayResolution(this, currentDisplayId, opt.index) }; drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode && opt.index != -1) { Thread {     if (currentOrientationMode != 0) { shellService?.runCommand("settings put system accelerometer_rotation 0"); shellService?.runCommand(when (currentOrientationMode) { 1 -> "settings put system user_rotation 0"; 2 -> "settings put system user_rotation 1"; else -> "" }) }; val resCmd = getResolutionCommand(selectedResolutionIndex); shellService?.runCommand(resCmd); Thread.sleep(1500); uiHandler.post { applyLayoutImmediate() } }.start() } }
    override fun selectDpi(value: Int) { currentDpiSetting = if (value == -1) -1 else value.coerceIn(50, 600); AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); Thread { try { if (currentDpiSetting == -1) { shellService?.runCommand("wm density reset -d $currentDisplayId") } else { val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; shellService?.runCommand(dpiCmd) } } catch(e: Exception) { e.printStackTrace() } }.start() }
    override fun changeFontSize(newSize: Float) { currentFontSize = newSize.coerceIn(10f, 30f); AppPreferences.saveFontSize(this, currentFontSize); updateGlobalFontSize(); if (currentMode == MODE_SETTINGS) { switchMode(MODE_SETTINGS) } }
    override fun changeDrawerHeight(delta: Int) { currentDrawerHeightPercent = (currentDrawerHeightPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerHeightPercentForConfig(this, currentDisplayId, currentAspectRatio, currentDrawerHeightPercent); AppPreferences.setDrawerHeightPercent(this, currentDrawerHeightPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    override fun changeDrawerWidth(delta: Int) { currentDrawerWidthPercent = (currentDrawerWidthPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerWidthPercentForConfig(this, currentDisplayId, currentAspectRatio, currentDrawerWidthPercent); AppPreferences.setDrawerWidthPercent(this, currentDrawerWidthPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    override fun pickIcon() { toggleDrawer(); try { refreshDisplayId(); val intent = Intent(this, IconPickerActivity::class.java); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); val metrics = windowManager.maximumWindowMetrics; val w = 1000; val h = (metrics.bounds.height() * 0.7).toInt(); val x = (metrics.bounds.width() - w) / 2; val y = (metrics.bounds.height() - h) / 2; val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(Rect(x, y, x+w, y+h)); startActivity(intent, options.toBundle()) } catch (e: Exception) { safeToast("Error launching picker: ${e.message}") } }
    override fun saveProfile() { 
        var name = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString()?.trim()
        if (name.isNullOrEmpty()) { 
            val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val modePrefix = when (currentProfileSaveMode) {
                1 -> "Layout_"
                2 -> "Queue_"
                else -> "Profile_"
            }
            name = "$modePrefix$timestamp" 
        }
        
        when (currentProfileSaveMode) {
            0 -> {
                // Layout + Apps: Save everything including margins + orientation
                val pkgs = selectedAppsQueue.map { it.packageName }
                AppPreferences.saveProfile(this, name, selectedLayoutType, selectedResolutionIndex, currentDpiSetting, pkgs, 0, topMarginPercent, bottomMarginPercent, autoAdjustMarginForIME, currentOrientationMode)
            }
            1 -> {
                // Layout Only: Save settings including margins + orientation, no apps
                AppPreferences.saveProfile(this, name, selectedLayoutType, selectedResolutionIndex, currentDpiSetting, emptyList(), 1, topMarginPercent, bottomMarginPercent, autoAdjustMarginForIME, currentOrientationMode)
            }
            2 -> {
                // App Queue Only: Save apps, use -1 for layout settings to indicate "don't change"
                val pkgs = selectedAppsQueue.map { it.packageName }
                AppPreferences.saveProfile(this, name, -1, -1, -1, pkgs, 2, -1, -1, false, 0)
            }
        }
        
        val modeLabel = when (currentProfileSaveMode) {
            1 -> "Layout"
            2 -> "Queue"
            else -> "Profile"
        }
        safeToast("Saved $modeLabel: $name")
        drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText("")
        switchMode(MODE_PROFILES) 
    }
    override fun loadProfile(name: String) { 
        val data = AppPreferences.getProfileData(this, name) ?: return
        try { 
            val parts = data.split("|")
            val profileType = parts[0].toInt()
            val layoutType = parts[1].toInt()
            val resIndex = parts[2].toInt()
            val dpiSetting = parts[3].toInt()
            
            // Parse margin settings if available (new format has 8+ parts)
            val topMar = if (parts.size >= 8) parts[4].toInt() else 0
            val botMar = if (parts.size >= 8) parts[5].toInt() else 0
            val autoMar = if (parts.size >= 8) parts[6] == "1" else false
            val pkgList = if (parts.size >= 8) {
                parts[7].split(",").filter { it.isNotEmpty() }
            } else if (parts.size > 4) {
                parts[4].split(",").filter { it.isNotEmpty() }
            } else emptyList()
            // Parse orientation mode (index 8 in newest format: 9+ parts)
            val orientMode = if (parts.size >= 9) parts[8].toIntOrNull() ?: 0 else 0
            
            when (profileType) {
                0 -> loadProfileLayoutAndApps(name, layoutType, resIndex, dpiSetting, pkgList, topMar, botMar, autoMar, orientMode)
                1 -> loadProfileLayoutOnly(name, layoutType, resIndex, dpiSetting, topMar, botMar, autoMar, orientMode)
                2 -> loadProfileQueueOnly(name, pkgList)
                else -> loadProfileLayoutAndApps(name, layoutType, resIndex, dpiSetting, pkgList, topMar, botMar, autoMar, orientMode)
            }
        } catch (e: Exception) { 
            // Fallback for old profile format (no type prefix)
            try {
                val parts = data.split("|")
                val layoutType = parts[0].toInt()
                val resIndex = parts[1].toInt()
                val dpiSetting = parts[2].toInt()
                val pkgList = parts[3].split(",").filter { it.isNotEmpty() }
                loadProfileLayoutAndApps(name, layoutType, resIndex, dpiSetting, pkgList, 0, 0, false)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to load profile", e2) 
            }
        } 
    }
    
    // Profile Type 0: Layout + Apps - Opens apps from profile, minimizes others
    private fun loadProfileLayoutAndApps(name: String, layoutType: Int, resIndex: Int, dpiSetting: Int, pkgList: List<String>, topMar: Int = 0, botMar: Int = 0, autoMar: Boolean = false, orientMode: Int = 0) {
        // Apply layout settings
        selectedLayoutType = layoutType
        selectedResolutionIndex = resIndex
        currentDpiSetting = dpiSetting
        
        // Apply orientation from profile
        currentOrientationMode = orientMode
        AppPreferences.saveOrientationMode(this, currentDisplayId, currentOrientationMode)
        applyOrientation()
        
        // Apply margin settings
        topMarginPercent = topMar
        bottomMarginPercent = botMar
        autoAdjustMarginForIME = autoMar
        val os = orientSuffix()
        AppPreferences.setTopMarginPercent(this, currentDisplayId, topMar)
        AppPreferences.setTopMarginPercent(this, currentDisplayId, topMar, os)
        AppPreferences.setBottomMarginPercent(this, currentDisplayId, botMar)
        AppPreferences.setBottomMarginPercent(this, currentDisplayId, botMar, os)
        AppPreferences.setAutoAdjustMarginForIME(this, autoMar)
        AppPreferences.setAutoAdjustMarginForIME(this, autoMar, os)
        
        AppPreferences.saveLastLayout(this, selectedLayoutType, currentDisplayId)
        AppPreferences.saveLastLayout(this, selectedLayoutType, currentDisplayId, os)
        if (selectedLayoutType != LAYOUT_CUSTOM_DYNAMIC) {
            activeCustomLayoutName = null
            AppPreferences.saveLastCustomLayoutName(this, null, currentDisplayId)
            AppPreferences.saveLastCustomLayoutName(this, null, currentDisplayId, os)
        }
        AppPreferences.saveDisplayResolution(this, currentDisplayId, selectedResolutionIndex)
        AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting)
        
        // Get current queue packages for comparison
        val currentPkgs = selectedAppsQueue.map { it.packageName }.toSet()
        val profilePkgs = pkgList.toSet()
        
        // Minimize apps not in profile (don't kill - preserve user work)
        val appsToMinimize = selectedAppsQueue.filter { 
            !it.isMinimized && it.packageName != PACKAGE_BLANK && it.packageName !in profilePkgs 
        }
        for (app in appsToMinimize) {
            app.isMinimized = true
        }
        if (appsToMinimize.isNotEmpty()) {
            Thread {
                for (app in appsToMinimize) {
                    try {
                        val tid = shellService?.getTaskId(app.getBasePackage(), null) ?: -1
                        if (tid != -1) shellService?.moveTaskToBack(tid)
                    } catch (e: Exception) {}
                }
            }.start()
        }
        
        // Build new queue from profile
        val newQueue = mutableListOf<MainActivity.AppInfo>()
        for (pkg in pkgList) {
            if (pkg == PACKAGE_BLANK) {
                newQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null))
            } else {
                // Check if app is already in queue
                val existingApp = selectedAppsQueue.find { it.packageName == pkg }
                if (existingApp != null) {
                    existingApp.isMinimized = false
                    newQueue.add(existingApp)
                } else {
                    // App not in queue - find in app list and add
                    val app = allAppsList.find { it.packageName == pkg }
                    if (app != null) {
                        app.isMinimized = false
                        newQueue.add(app)
                    }
                }
            }
        }
        
        selectedAppsQueue.clear()
        selectedAppsQueue.addAll(newQueue)
        
        activeProfileName = name
        updateSelectedAppsDock()
        // Force refresh both recyclers to prevent visual duplicates
        drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
        drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        safeToast("Loaded: $name")
        
        if (isInstantMode) applyLayoutImmediate()
    }
    
    // Profile Type 1: Layout Only - Applies layout settings, keeps current apps
    private fun loadProfileLayoutOnly(name: String, layoutType: Int, resIndex: Int, dpiSetting: Int, topMar: Int = 0, botMar: Int = 0, autoMar: Boolean = false, orientMode: Int = 0) {
        // Apply layout settings
        selectedLayoutType = layoutType
        selectedResolutionIndex = resIndex
        currentDpiSetting = dpiSetting
        
        // Apply orientation from profile
        currentOrientationMode = orientMode
        AppPreferences.saveOrientationMode(this, currentDisplayId, currentOrientationMode)
        applyOrientation()
        
        // Apply margin settings
        topMarginPercent = topMar
        bottomMarginPercent = botMar
        autoAdjustMarginForIME = autoMar
        val os = orientSuffix()
        AppPreferences.setTopMarginPercent(this, currentDisplayId, topMar)
        AppPreferences.setTopMarginPercent(this, currentDisplayId, topMar, os)
        AppPreferences.setBottomMarginPercent(this, currentDisplayId, botMar)
        AppPreferences.setBottomMarginPercent(this, currentDisplayId, botMar, os)
        AppPreferences.setAutoAdjustMarginForIME(this, autoMar)
        AppPreferences.setAutoAdjustMarginForIME(this, autoMar, os)
        
        AppPreferences.saveLastLayout(this, selectedLayoutType, currentDisplayId)
        AppPreferences.saveLastLayout(this, selectedLayoutType, currentDisplayId, os)
        if (selectedLayoutType != LAYOUT_CUSTOM_DYNAMIC) {
            activeCustomLayoutName = null
            AppPreferences.saveLastCustomLayoutName(this, null, currentDisplayId)
            AppPreferences.saveLastCustomLayoutName(this, null, currentDisplayId, os)
        }
        AppPreferences.saveDisplayResolution(this, currentDisplayId, selectedResolutionIndex)
        AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting)
        
        // Get layout slot count
        val layoutRects = getLayoutRects()
        val maxSlots = layoutRects.size
        
        // If more active apps than slots, minimize excess
        val activeApps = selectedAppsQueue.filter { !it.isMinimized && it.packageName != PACKAGE_BLANK }
        if (activeApps.size > maxSlots) {
            val appsToMinimize = activeApps.drop(maxSlots)
            for (app in appsToMinimize) {
                app.isMinimized = true
            }
            Thread {
                for (app in appsToMinimize) {
                    try {
                        val tid = shellService?.getTaskId(app.getBasePackage(), null) ?: -1
                        if (tid != -1) shellService?.moveTaskToBack(tid)
                    } catch (e: Exception) {}
                }
            }.start()
        }
        
        activeProfileName = name
        updateSelectedAppsDock()
        // Force refresh both recyclers
        drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
        drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        safeToast("Loaded Layout: $name")
        
        if (isInstantMode) applyLayoutImmediate()
    }
    
    // Profile Type 2: App Queue Only - Applies app queue, keeps current layout settings
    private fun loadProfileQueueOnly(name: String, pkgList: List<String>) {
        val profilePkgs = pkgList.toSet()
        
        // Minimize apps not in profile (don't kill - preserve user work)
        val appsToMinimize = selectedAppsQueue.filter { 
            !it.isMinimized && it.packageName != PACKAGE_BLANK && it.packageName !in profilePkgs 
        }
        for (app in appsToMinimize) {
            app.isMinimized = true
        }
        if (appsToMinimize.isNotEmpty()) {
            Thread {
                for (app in appsToMinimize) {
                    try {
                        val tid = shellService?.getTaskId(app.getBasePackage(), null) ?: -1
                        if (tid != -1) shellService?.moveTaskToBack(tid)
                    } catch (e: Exception) {}
                }
            }.start()
        }
        
        // Build new queue from profile
        val newQueue = mutableListOf<MainActivity.AppInfo>()
        for (pkg in pkgList) {
            if (pkg == PACKAGE_BLANK) {
                newQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null))
            } else {
                // Check if app is already in queue
                val existingApp = selectedAppsQueue.find { it.packageName == pkg }
                if (existingApp != null) {
                    existingApp.isMinimized = false
                    newQueue.add(existingApp)
                } else {
                    // App not in queue - find in app list and add
                    val app = allAppsList.find { it.packageName == pkg }
                    if (app != null) {
                        app.isMinimized = false
                        newQueue.add(app)
                    }
                }
            }
        }
        
        selectedAppsQueue.clear()
        selectedAppsQueue.addAll(newQueue)
        
        activeProfileName = name
        updateSelectedAppsDock()
        // Force refresh both recyclers to prevent visual duplicates
        drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
        drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        safeToast("Loaded Queue: $name")
        
        if (isInstantMode) applyLayoutImmediate()
    }
    

    // === EXECUTE LAUNCH - START ===
    // Main execution function that launches and tiles all selected apps
    // Uses a locking mechanism to prevent parallel shell commands causing race conditions.
    private fun executeLaunch(layoutType: Int, closeDrawer: Boolean, focusPackage: String? = null) {
        // Cancel any pending runnable
        if (pendingLaunchRunnable != null) {
            uiHandler.removeCallbacks(pendingLaunchRunnable!!)
            pendingLaunchRunnable = null
        }

        // If already executing, queue this request
        if (isExecuting) {
            pendingExecutionNeeded = true
            if (focusPackage != null) pendingFocusPackage = focusPackage
            return
        }

        // [FULLSCREEN] When user explicitly launches tiled apps, reset auto-minimize state
        // and set cooldown to prevent fullscreen detection from immediately re-hiding them.
        tiledAppsAutoMinimized = false
        lastExplicitTiledLaunchAt = System.currentTimeMillis()

        isExecuting = true
        pendingExecutionNeeded = false // Reset pending flag for THIS run

        // Get currently visible apps on this display, excluding ourselves and the trackpad
        val activeApps = shellService?.getVisiblePackages(currentDisplayId)
            ?.mapNotNull { pkgName -> allAppsList.find { it.packageName == pkgName } }
            ?.filter { it.packageName != packageName && it.packageName != PACKAGE_TRACKPAD }
            ?: emptyList()

        // [FULLSCREEN] If there's a visible app not in the queue (fullscreen app), add it to the queue.
        // This converts the fullscreen app into a tiled app, placing the user's selected apps on top.
        // Without this, the fullscreen app detection would minimize the newly launched tiled apps.
        val queuePackages = selectedAppsQueue.map { it.getBasePackage() }.toSet()
        // [FIX] Clean up old entries and exclude recently removed packages from fullscreen detection
        val now = System.currentTimeMillis()
        recentlyRemovedPackages.entries.removeIf { now - it.value > REMOVED_PACKAGE_COOLDOWN_MS }
        val fullscreenApps = activeApps.filter { 
            val basePkg = it.getBasePackage()
            basePkg !in queuePackages && 
            !it.isMinimized && 
            !recentlyRemovedPackages.containsKey(basePkg)
        }
        if (fullscreenApps.isNotEmpty() && selectedAppsQueue.any { !it.isMinimized }) {
            for (fsApp in fullscreenApps) {
                // Add fullscreen app to END of queue (bottom position in layout)
                // The user's selected apps will be on top
                selectedAppsQueue.add(fsApp.copy())
            }
            // Update UI to show the new queue
            uiHandler.post { updateAllUIs() }
        }

        if (closeDrawer) toggleDrawer()
        refreshDisplayId()

        // Save queue
        val identifiers = selectedAppsQueue.map { it.getIdentifier() }
        AppPreferences.saveLastQueue(this, identifiers)
        
        Thread { 
            try { 
                var configChanged = false

                // Apply orientation if not System Default
                if (currentOrientationMode != 0) {
                    shellService?.runCommand("settings put system accelerometer_rotation 0")
                    shellService?.runCommand(when (currentOrientationMode) { 1 -> "settings put system user_rotation 0"; 2 -> "settings put system user_rotation 1"; else -> "" })
                    configChanged = true
                }

                // Apply resolution only if changed
                if (selectedResolutionIndex != lastAppliedResIndex) {
                    val resCmd = getResolutionCommand(selectedResolutionIndex)
                    shellService?.runCommand(resCmd)
                    lastAppliedResIndex = selectedResolutionIndex
                    configChanged = true
                }
                
                // Apply DPI only if changed
                if (currentDpiSetting != lastAppliedDpi) {
                    if (currentDpiSetting > 0) { 
                        shellService?.runCommand("wm density $currentDpiSetting -d $currentDisplayId")
                    } else if (currentDpiSetting == -1) { 
                        shellService?.runCommand("wm density reset -d $currentDisplayId")
                    }
                    lastAppliedDpi = currentDpiSetting
                    configChanged = true
                }
                
                // [FIX] Only sleep if we actually changed system configuration
                if (configChanged) {
                    Thread.sleep(800)
                }
                
                // Get screen dimensions
                
                                // [FIX] Use getLayoutRects() which contains the Bottom Margin logic.
                // We use the member variable 'selectedLayoutType' (which matches the passed 'layoutType' 99% of the time).
                val rects = getLayoutRects()
                
                
                if (selectedAppsQueue.isEmpty()) {
                    uiHandler.post { safeToast("No apps in queue") }
                    return@Thread
                }
                
                // Handle minimized apps
                val minimizedApps = selectedAppsQueue.filter { it.isMinimized }
                
                // If on Virtual Display and NO active apps (Show Desktop), just launch Wallpaper
                if (currentDisplayId >= 2 && activeApps.isEmpty() && minimizedApps.isNotEmpty()) {
                    showWallpaper()
                } else {
                    // Standard Loop
                    for (app in minimizedApps) { 
                        if (app.packageName != PACKAGE_BLANK) { 
                            try { 
                                val basePkg = app.getBasePackage()
                                val tid = shellService?.getTaskId(basePkg, app.className) ?: -1
                                if (tid != -1) shellService?.moveTaskToBack(tid) 
                            } catch (e: Exception) {} 
                        } 
                    }
                }
                
                val activeApps = selectedAppsQueue.filter { !it.isMinimized }
                
                // Kill/Prep Logic - Only wait if we actually have apps to launch
                // [FIX] If activeApps is empty (we are just minimizing), SKIP THE SLEEP.
                if (activeApps.isNotEmpty()) {
                    // [DEPRECATED] killAppOnExecute removed
                    if (false) { // killAppOnExecute
                        for (app in activeApps) { 
                            if (app.packageName != PACKAGE_BLANK) { 
                                val basePkg = app.getBasePackage()
                                shellService?.forceStop(basePkg)
                            } 
                        }
                        Thread.sleep(400) 
                    } else { 
                        Thread.sleep(100) 
                    }
                }
                
// === LAUNCH AND TILE APPS (Robust Background Loop) ===
                // [FIX] We use Thread.sleep inside this background thread instead of uiHandler.postDelayed.
                // This ensures the sequence continues executing even if the UI thread is throttled/closed.
                for (i in 0 until minOf(activeApps.size, rects.size)) {
                    val app = activeApps[i]
                    val bounds = rects[i]

                    if (app.packageName == PACKAGE_BLANK) continue

                    val basePkg = app.getBasePackage()
                    val cls = app.className

                    // UI Update must be posted
                    uiHandler.post {
                        debugShowAppIdentification("TILE[$i]", basePkg, cls)
                    }
                    
                    // [CACHE] Store intended bounds for Watchdog recovery
                    packageRectCache[basePkg] = bounds

                    // 1. Launch App (SYNCHRONOUSLY)
                    val component = if (!cls.isNullOrEmpty() && cls != "null" && cls != "default") "$basePkg/$cls" else null
                    val cmd = if (component != null) {
                        "am start -n $component --display $currentDisplayId --windowingMode 5 --user 0"
                    } else {
                        "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"
                    }

                    try {
                        shellService?.runCommand(cmd)
                    } catch (e: Exception) {
                        // Log.e(TAG, "Tile[$i]: Launch failed", e)
                    }

                    val isGeminiApp = basePkg.contains("bard") || basePkg.contains("gemini")

                    // 2. WAIT AND RESIZE (Sequential/Blocking)
                    // We block the loop here until THIS window is ready and resized.
                    // This ensures App 1 is fully positioned before we even touch App 2.
                    try {
                        // SMART POLLING:
                        // Active Window (Swap): Returns almost instantly.
                        // Cold Boot: Waits up to 3s.
                        var tid = -1
                        val maxWait = if (isGeminiApp) 8000L else 3000L
                        val startPoll = System.currentTimeMillis()

                        // Fast poll (50ms) for snappiness
                        while (System.currentTimeMillis() - startPoll < maxWait) {
                            tid = shellService?.getTaskId(basePkg, cls) ?: -1
                            if (tid != -1) break
                            Thread.sleep(50)
                        }

                        // If we found it instantly (already running), delay is minimal (50ms).
                        // If it took time, we wait a tiny bit for the window surface to be ready.
                        val wasInstant = (System.currentTimeMillis() - startPoll < 150)
                        if (!wasInstant) Thread.sleep(200)

                        // PASS 1: Set Mode & Resize
                        shellService?.repositionTask(basePkg, cls, bounds.left, bounds.top, bounds.right, bounds.bottom)

                        // PASS 2: Redundant Resize for Samsung (Only if not instant swap)
                        // If we are just swapping active windows, Pass 1 is usually enough.
                        // We do a quick check-up resize.
                        if (!wasInstant) Thread.sleep(200)

                        val finalTid = shellService?.getTaskId(basePkg, cls) ?: -1
                        if (finalTid != -1) {
                            shellService?.runCommand("am task resize $finalTid ${bounds.left} ${bounds.top} ${bounds.right} ${bounds.bottom}")
                            packageTaskIdCache[basePkg] = finalTid
                        }



                    } catch (e: Exception) {
                        Log.e(TAG, "Tile[$i]: Reposition failed", e)
                    }

                    // 3. Buffer before next app
                    // Increased to 150ms to ensure WindowManager state settles before next 'am start'
                    Thread.sleep(150)
                }
                // === LAUNCH AND TILE APPS - END ===

                // [FIX] Auto-minimize apps that exceed the slot count
                // When switching from 4-slot to 2-slot layout, apps 3 and 4 should be minimized
                if (activeApps.size > rects.size) {
                    val packagesToMinimize = mutableListOf<String>()
                    for (i in rects.size until activeApps.size) {
                        val app = activeApps[i]
                        if (app.packageName == PACKAGE_BLANK) continue
                        val basePkg = app.getBasePackage()
                        packagesToMinimize.add(basePkg)
                        try {
                            val tid = shellService?.getTaskId(basePkg, app.className) ?: -1
                            if (tid != -1) shellService?.moveTaskToBack(tid)
                        } catch (e: Exception) {}
                        // Mark as minimized in the queue
                        val queueIndex = selectedAppsQueue.indexOfFirst { 
                            it.packageName == app.packageName && it.className == app.className 
                        }
                        if (queueIndex != -1) {
                            selectedAppsQueue[queueIndex].isMinimized = true
                        }
                    }
                    // Clear focus on UI thread to prevent apps from popping back
                    uiHandler.post {
                        for (pkg in packagesToMinimize) {
                            val isGemini = pkg == "com.google.android.apps.bard"
                            val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"
                            if (activePackageName == pkg || (isGemini && activeIsGoogle)) {
                                activePackageName = null
                            }
                            removeFromFocusHistory(pkg)
                        }
                        updateAllUIs()
                    }
                }

                // === REFOCUS LOGIC ===
                // After swapping/moving windows, re-launch the active app to bring it back to focus
                if (focusPackage != null) {
                    val focusIndex = activeApps.indexOfFirst {
                        it.packageName == focusPackage ||
                        (it.packageName == "com.google.android.apps.bard" && focusPackage == "com.google.android.googlequicksearchbox")
                    }

                    if (focusIndex != -1 && focusIndex < rects.size) {
                        val app = activeApps[focusIndex]
                        val bounds = rects[focusIndex]
                        Thread.sleep(200)
                        launchViaShell(app.getBasePackage(), app.className, bounds)
                    }
                }
                // === REFOCUS LOGIC - END ===

                if (closeDrawer) { 
                    uiHandler.post { 
                        selectedAppsQueue.clear()
                        updateSelectedAppsDock() 
                    } 
                }
                
                uiHandler.post {
                     safeToast("Tiling Sequence Complete")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Execute Failed", e)
                uiHandler.post { safeToast("Execute Failed: ${e.message}") }
            } finally {
                // [EFFICIENCY] Invalidate cache so "Active Window" updates instantly
                (shellService as? ShellUserService)?.invalidateVisibleCache()

                isExecuting = false

                // If a request came in while we were running, trigger it now
                if (pendingExecutionNeeded) {
                    val nextFocus = pendingFocusPackage
                    pendingFocusPackage = null
                    uiHandler.post {
                        executeLaunch(selectedLayoutType, false, nextFocus)
                    }
                }
            }
        }.start()

        drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText("")
    }

    // === EXECUTE LAUNCH - END ===
    
    private fun calculateGCD(a: Int, b: Int): Int { return if (b == 0) a else calculateGCD(b, a % b) }

    // === SWITCH MODE - START ===
    // Switches between different drawer tabs/modes
    // Handles UI updates for search bar, icons, and list content
    override fun switchMode(mode: Int) {
        currentMode = mode
        selectedListIndex = 0 // Reset selection on tab switch
        
        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val searchIcon = drawerView!!.findViewById<ImageView>(R.id.icon_search_mode); val iconWin = drawerView!!.findViewById<ImageView>(R.id.icon_mode_window);        val iconRes = drawerView!!.findViewById<ImageView>(R.id.icon_mode_resolution);
        val iconRefresh = drawerView!!.findViewById<ImageView>(R.id.icon_mode_refresh); val iconDpi = drawerView!!.findViewById<ImageView>(R.id.icon_mode_dpi); val iconBlacklist = drawerView!!.findViewById<ImageView>(R.id.icon_mode_blacklist); val iconProf = drawerView!!.findViewById<ImageView>(R.id.icon_mode_profiles); val iconKeybinds = drawerView!!.findViewById<ImageView>(R.id.icon_mode_keybinds); val iconSet = drawerView!!.findViewById<ImageView>(R.id.icon_mode_settings); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        searchIcon.setColorFilter(if(mode==MODE_SEARCH) Color.WHITE else Color.GRAY); iconWin.setColorFilter(if(mode==MODE_LAYOUTS) Color.WHITE else Color.GRAY);        iconRes.setColorFilter(if(mode==MODE_RESOLUTION) Color.WHITE else Color.GRAY);
        iconRefresh?.setColorFilter(if(mode==MODE_REFRESH) Color.WHITE else Color.GRAY); iconDpi.setColorFilter(if(mode==MODE_DPI) Color.WHITE else Color.GRAY); iconBlacklist?.setColorFilter(if(mode==MODE_BLACKLIST) Color.WHITE else Color.GRAY); iconProf.setColorFilter(if(mode==MODE_PROFILES) Color.WHITE else Color.GRAY); iconKeybinds?.setColorFilter(if(mode==MODE_KEYBINDS) Color.WHITE else Color.GRAY); iconSet.setColorFilter(if(mode==MODE_SETTINGS) Color.WHITE else Color.GRAY)
        executeBtn.visibility = View.GONE; displayList.clear(); val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); dock.visibility = if (mode == MODE_SEARCH && selectedAppsQueue.isNotEmpty()) View.VISIBLE else View.GONE

        when (mode) {
            MODE_SEARCH -> { searchBar.hint = "Search apps..."; refreshSearchList() }
            MODE_LAYOUTS -> { 
                searchBar.hint = "Select Layout"
                isLayoutNameEditMode = false // Reset edit mode when entering tab
                displayList.add(ActionOption("Save Current Arrangement") { saveCurrentAsCustom() })
                
                // Name Editor Mode toggle
                displayList.add(ToggleOption("Name Editor Mode", isLayoutNameEditMode) { enabled ->
                    isLayoutNameEditMode = enabled
                    drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
                })
                
                // [FIX] Add Reset Button to fix corrupted names
                displayList.add(ActionOption("Reset Default Names") { 
                    AppPreferences.clearDefaultLayoutNames(this)
                    safeToast("Layout names reset")
                    switchMode(MODE_LAYOUTS) // Refresh
                })
                
                // Add all default layouts (Labels are fetched via getLayoutName which supports renaming)
                val defaults = listOf(
                    LAYOUT_FULL, LAYOUT_SIDE_BY_SIDE, LAYOUT_TOP_BOTTOM,
                    LAYOUT_TRI_EVEN, LAYOUT_TRI_SIDE_MAIN_SIDE,
                    LAYOUT_CORNERS, LAYOUT_QUAD_ROW_EVEN,
                    LAYOUT_QUAD_TALL_SHORT, LAYOUT_HEX_TALL_SHORT
                )
                
                for (type in defaults) {
                    displayList.add(LayoutOption(getLayoutName(type), type))
                }

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
                // Legend for grid notation (editable via Name Editor Mode)
                displayList.add(LegendOption(AppPreferences.getLegendText(this)))
            }
            MODE_RESOLUTION -> {
                searchBar.hint = "Select Resolution"
                val orientLabel = when (currentOrientationMode) { 1 -> "Portrait"; 2 -> "Landscape"; else -> "System Default" }
                displayList.add(ActionOption("Orientation: $orientLabel (tap to cycle)") { currentOrientationMode = (currentOrientationMode + 1) % 3; AppPreferences.saveOrientationMode(this@FloatingLauncherService, currentDisplayId, currentOrientationMode); applyOrientation(); switchMode(MODE_RESOLUTION) })
                displayList.add(CustomResInputOption)
                val savedResNames = AppPreferences.getCustomResolutionNames(this).sorted()
                for (name in savedResNames) { val value = AppPreferences.getCustomResolutionValue(this, name) ?: continue; displayList.add(ResolutionOption(name, "wm size $value -d $currentDisplayId", 100 + savedResNames.indexOf(name))) }
                val dmSvc = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val disp = dmSvc.getDisplay(currentDisplayId); val dMode = disp?.mode; val physW = dMode?.physicalWidth ?: 0; val physH = dMode?.physicalHeight ?: 0; val physLabel = if (physW > 0 && physH > 0) " — Device: ${physW}x${physH}" else ""
                displayList.add(ResolutionOption("Default (Reset)$physLabel", "wm size reset -d $currentDisplayId", 0))
                displayList.add(ResolutionOption("1:1 Square (1422x1500)", "wm size 1422x1500 -d $currentDisplayId", 1))
                displayList.add(ResolutionOption("16:9 Landscape (1920x1080)", "wm size 1920x1080 -d $currentDisplayId", 2))
                displayList.add(ResolutionOption("16:10 Landscape (1920x1200)", "wm size 1920x1200 -d $currentDisplayId", 4))
                displayList.add(ResolutionOption("32:9 Ultrawide (3840x1080)", "wm size 3840x1080 -d $currentDisplayId", 3))
            }

// =================================================================================
            // MODE_REFRESH UI BUILDER
            // SUMMARY: Builds the refresh rate selection menu. Queries hardware for supported
            //          modes and marks unavailable rates as greyed out with "(N/A)" suffix.
            //          Single-mode displays (like XReal) show clear "Only XXHz" messaging.
            // =================================================================================
            MODE_REFRESH -> {
                searchBar.hint = "Refresh Rate"
                
                // 1. Get display info and supported modes
                val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                val display = dm.getDisplay(currentDisplayId)
                val modes = display?.supportedModes ?: emptyArray()
                val currentRate = display?.refreshRate ?: 60f
                val roundedRate = String.format("%.0f", currentRate)
                
                // 2. Build set of hardware-supported rates (within 1Hz tolerance)
                val supportedRates = mutableSetOf<Int>()
                modes.forEach { mode ->
                    supportedRates.add(Math.round(mode.refreshRate).toInt())
                }
                
                
                // 3. Build header text based on available modes
                val headerText = when {
                    supportedRates.size == 1 -> {
                        // Single mode display (like XReal glasses)
                        val onlyRate = supportedRates.first()
                        if (activeRefreshRateLabel != null) {
                            "Fixed: ${onlyRate}Hz | $activeRefreshRateLabel"
                        } else {
                            "⚠️ Display only supports ${onlyRate}Hz"
                        }
                    }
                    activeRefreshRateLabel != null -> {
                        "HW: ${roundedRate}Hz | $activeRefreshRateLabel"
                    }
                    else -> {
                        "Current: ${roundedRate}Hz (${supportedRates.size} modes)"
                    }
                }
                displayList.add(RefreshHeaderOption(headerText))
                
                // 4. Add rate options with availability status
                val rates = listOf(30f, 60f, 72f, 90f, 120f)
                for (rate in rates) {
                    val rateInt = rate.toInt()
                    val isHardwareSupported = supportedRates.any { Math.abs(it - rateInt) <= 1 }
                    val isCurrentlyActive = Math.abs(currentRate - rate) < 1.0f
                    
                    // Build label with availability indicator
                    val label = if (isHardwareSupported) {
                        "${rateInt}Hz"
                    } else {
                        "${rateInt}Hz (N/A)"
                    }
                    
                    displayList.add(RefreshItemOption(
                        label = label,
                        targetRate = rate,
                        isSelected = isCurrentlyActive,
                        isAvailable = isHardwareSupported
                    ))
                }
                
                // 5. If only one mode, add info item explaining the limitation
                if (supportedRates.size == 1) {
                    displayList.add(ActionOption("ℹ️ Hardware Limitation") {
                        safeToast("This display only supports ${supportedRates.first()}Hz. Software limiting not possible.")
                    })
                }
            }
            // =================================================================================
            // END MODE_REFRESH UI BUILDER  
            // =================================================================================
            MODE_DPI -> { searchBar.hint = "Adjust Density (DPI)"; displayList.add(ActionOption("Reset Density (Default)") { selectDpi(-1) }); var savedDpi = currentDpiSetting; if (savedDpi <= 0) { savedDpi = displayContext?.resources?.configuration?.densityDpi ?: 160 }; displayList.add(DpiOption(savedDpi)) }
            MODE_BLACKLIST -> { searchBar.hint = "Blacklisted Apps"; loadBlacklistedApps(); executeBtn.visibility = View.GONE }
            MODE_PROFILES -> { 
                isProfileNameEditMode = false // Reset edit mode when entering tab
                val modeLabel = when (currentProfileSaveMode) {
                    1 -> "Layout Only"
                    2 -> "App Queue Only"
                    else -> "Layout + Apps"
                }
                searchBar.hint = "Enter Profile Name..."
                
                // Mode toggle action
                displayList.add(ActionOption("Mode: $modeLabel (tap to change)") {
                    currentProfileSaveMode = (currentProfileSaveMode + 1) % 3
                    switchMode(MODE_PROFILES)
                })
                
                // Name Editor Mode toggle
                displayList.add(ToggleOption("Name Editor Mode", isProfileNameEditMode) { enabled ->
                    isProfileNameEditMode = enabled
                    drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
                })
                
                // Save new profile option
                val saveLabel = when (currentProfileSaveMode) {
                    1 -> "Save Layout as New"
                    2 -> "Save Queue as New"
                    else -> "Save Profile as New"
                }
                displayList.add(ProfileOption(saveLabel, true, 0, 0, 0, emptyList(), currentProfileSaveMode))
                
                // Load existing profiles
                val profileNames = AppPreferences.getProfileNames(this).sorted()
                for (pName in profileNames) { 
                    val data = AppPreferences.getProfileData(this, pName)
                    if (data != null) { 
                        try { 
                            val parts = data.split("|")
                            // Check format by size
                            val profileType: Int
                            val lay: Int
                            val res: Int
                            val d: Int
                            val topMar: Int
                            val botMar: Int
                            val autoMar: Boolean
                            val pkgs: List<String>
                            
                            var orient = 0
                            if (parts.size >= 8) {
                                // New format: type|layout|res|dpi|topMargin|bottomMargin|autoAdjust|apps[|orient]
                                profileType = parts[0].toInt()
                                lay = parts[1].toInt()
                                res = parts[2].toInt()
                                d = parts[3].toInt()
                                topMar = parts[4].toInt()
                                botMar = parts[5].toInt()
                                autoMar = parts[6] == "1"
                                pkgs = parts[7].split(",").filter { it.isNotEmpty() }
                                orient = if (parts.size >= 9) parts[8].toIntOrNull() ?: 0 else 0
                            } else if (parts.size >= 5) {
                                // Intermediate format: type|layout|res|dpi|apps
                                profileType = parts[0].toInt()
                                lay = parts[1].toInt()
                                res = parts[2].toInt()
                                d = parts[3].toInt()
                                topMar = 0
                                botMar = 0
                                autoMar = false
                                pkgs = parts[4].split(",").filter { it.isNotEmpty() }
                            } else {
                                // Old format: layout|res|dpi|apps
                                profileType = 0
                                lay = parts[0].toInt()
                                res = parts[1].toInt()
                                d = parts[2].toInt()
                                topMar = 0
                                botMar = 0
                                autoMar = false
                                pkgs = parts[3].split(",").filter { it.isNotEmpty() }
                            }
                            displayList.add(ProfileOption(pName, false, lay, res, d, pkgs, profileType, topMar, botMar, autoMar, orient)) 
                        } catch(e: Exception) {} 
                    } 
                } 
            }
            MODE_KEYBINDS -> {
                searchBar.hint = "Configure Hotkeys"
                displayList.add(ActionOption("How to use: Set modifier + key. Press to trigger.") {})
                
                // Add Custom Modifier Config Row
                // displayList.add(CustomModConfigOption(customModKey))

                // TextWatcher Toggle
                // displayList.add(ToggleOption("Gboard/Soft-Key Support", isSoftKeyboardSupport) { enabled ->
                //    isSoftKeyboardSupport = enabled
                //    AppPreferences.setSoftKeyboardSupport(this, enabled)
                //    if (enabled) {
                //        safeToast("Warning: Monitors text changes. Slight performance cost.")
                //    }
                // })

                for (cmd in AVAILABLE_COMMANDS) {
                    val bind = AppPreferences.getKeybind(this, cmd.id)
                    displayList.add(KeybindOption(cmd, bind.first, bind.second))
                }
            }
            MODE_SETTINGS -> {
                searchBar.hint = "Settings"
                // [FIX] Use restartTrackpad() (Hard Kill) to ensure Z-Order is reset properly.
                // launchTrackpad() alone is too gentle and doesn't reset the window stack.
                displayList.add(ActionOption("Launch/Reset Trackpad") { 
                    if (isBound && shellService != null) {
                        restartTrackpad() 
                    } else {
                        launchTrackpad()
                    }
                })


                displayList.add(ActionOption("Switch Display (Current $currentDisplayId)") { switchDisplay() })
                displayList.add(ToggleOption("Virtual Display (1080p)", isVirtualDisplayActive) { toggleVirtualDisplay(it) })
                displayList.add(HeightOption(currentDrawerHeightPercent))
                displayList.add(WidthOption(currentDrawerWidthPercent))
                displayList.add(MarginOption(0, topMarginPercent)) // 0 = Top
                displayList.add(MarginOption(1, bottomMarginPercent)) // 1 = Bottom
                // [FIX] Improved DroidOS IME detection for Flip 7 cover screen compatibility.
                // On Samsung Flip cover screen, One UI manages IME per-display and DEFAULT_INPUT_METHOD
                // may not reflect the actual IME. We use multiple detection methods:
                // 1. droidOsImeDetected: Set when we receive IME_VISIBILITY broadcast (most reliable)
                // 2. System default IME check (works on main screen)
                // 3. Enabled IME check (fallback)
                val isDroidOsImeActive = droidOsImeDetected || try {
                    val currentIme = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.DEFAULT_INPUT_METHOD)
                    val enabledImes = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ENABLED_INPUT_METHODS)
                    val isDefault = currentIme?.contains("DroidOSTrackpadKeyboard") == true || currentIme?.contains("DockInputMethodService") == true
                    val isEnabled = enabledImes?.contains("DroidOSTrackpadKeyboard") == true
                    isDefault || isEnabled
                } catch (e: Exception) { false }
                
                if (isDroidOsImeActive) {
                    displayList.add(ToggleOption("Auto-Adjust Margin for IME", autoAdjustMarginForIME) {
                        autoAdjustMarginForIME = it
                        AppPreferences.setAutoAdjustMarginForIME(this, it)
                        AppPreferences.setAutoAdjustMarginForIME(this, it, orientSuffix())
                        if (!it) imeMarginOverrideActive = false
                    })
                } else {
                    // Greyed out option when DroidOS IME not detected
                    displayList.add(ToggleOption("Auto-Adjust Margin for IME (Requires DroidOS IME)", false) {
                        safeToast("Enable DroidOS IME in system settings, then show keyboard once")
                    })
                }


                displayList.add(ToggleOption("Auto-Shrink for Keyboard", autoResizeEnabled) { autoResizeEnabled = it; AppPreferences.setAutoResizeKeyboard(this, it) })


                displayList.add(FontSizeOption(currentFontSize))
                displayList.add(IconOption("Launcher Icon (Tap to Change)"))
                displayList.add(BubbleSizeOption(bubbleSizePercent))
                displayList.add(ToggleOption("Reorder: Drag & Drop", isReorderDragEnabled) { isReorderDragEnabled = it; AppPreferences.setReorderDrag(this, it) })
                displayList.add(ToggleOption("Reorder: Tap to Swap (Long Press)", isReorderTapEnabled) { isReorderTapEnabled = it; AppPreferences.setReorderTap(this, it) })
                // [DEPRECATED] Execute mode removed - instant mode is always active
                // displayList.add(ToggleOption("Instant Mode (Live Changes)", isInstantMode) { isInstantMode = it; AppPreferences.setInstantMode(this, it); executeBtn.visibility = if (it) View.GONE else View.VISIBLE; if (it) fetchRunningApps() })
                // displayList.add(ToggleOption("Kill App on Execute", killAppOnExecute) { killAppOnExecute = it; AppPreferences.setKillOnExecute(this, it) })
                
                // --- V2.0 MENU ITEMS RESTORED ---
                
                
                // STANDARD MODE TOGGLE
                displayList.add(ToggleOption("Screen Off (Standard)", isScreenOffState && !useAltScreenOff) { 
                    if (it) {
                        if (isScreenOffState) wakeUp() // Reset if already off
                        useAltScreenOff = false
                        AppPreferences.setUseAltScreenOff(this, false)
                        performScreenOff()
                    } else {
                        wakeUp()
                    }
                })

                // ALTERNATE MODE TOGGLE
                displayList.add(ToggleOption("Screen Off (Alternate)", isScreenOffState && useAltScreenOff) { 
                    if (it) {
                        if (isScreenOffState) wakeUp() // Reset if already off
                        useAltScreenOff = true
                        AppPreferences.setUseAltScreenOff(this, true)
                        performScreenOff()
                    } else {
                        wakeUp()
                    }
                })
                
                displayList.add(ToggleOption("Auto-Start Trackpad", autoRestartTrackpad) { autoRestartTrackpad = it; AppPreferences.setAutoRestartTrackpad(this, it); if (it) safeToast("Trackpad will restart on next Launcher startup") })
                displayList.add(ToggleOption("Shizuku Warning (Icon Alert)", showShizukuWarning) { showShizukuWarning = it; AppPreferences.setShowShizukuWarning(this, it); updateBubbleIcon() })

                // Restart Button (Process Kill - Auto Restarts Service)
                displayList.add(ActionOption("Restart DroidOS Launcher") {
                    safeToast("Restarting Launcher...")
                    stopSelf()
                    android.os.Process.killProcess(android.os.Process.myPid())
                })

                // Terminate Button (Disables Accessibility Service - Requires Manual Re-enable)
                displayList.add(ActionOption("Terminate DroidOS Service") {
                    safeToast("Terminating Service...")
                    if (Build.VERSION.SDK_INT >= 24) {
                        disableSelf()
                    } else {
                        stopSelf()
                    }
                })
            }
        }
        // Store unfiltered list for menu search/filter
        unfilteredDisplayList.clear()
        unfilteredDisplayList.addAll(displayList)
        // Clear search bar when switching modes (except SEARCH which has its own query)
        if (mode != MODE_SEARCH) {
            drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText("")
        }
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    // === SWITCH MODE - END ===


    override var isLayoutNameEditMode = false
    override var isProfileNameEditMode = false
    override val unfilteredDisplayList = mutableListOf<Any>() // Store full list for menu filtering

    // SelectedAppsAdapter extracted to SelectedAppsAdapter.kt

    // =================================================================================
    // WINDOW MANAGER COMMAND PROCESSOR (v2)
    // SUMMARY: Handles headless commands with 1-BASED INDEXING.
    //          Supports Active Window swapping and Blank Space hiding.
    // =================================================================================
    override fun handleWindowManagerCommand(intent: Intent) {
        val cmd = intent.getStringExtra("COMMAND")?.uppercase(Locale.ROOT) ?: return

        if (!isBound || shellService == null) {
            Log.w(TAG, "WM Command $cmd queued (Shizuku not bound)")
            tryBindShizukuIfPermitted()
            queueWindowManagerCommand(intent)
            return
        }
        
        // [FIX] Sync display context before executing commands to prevent stale state
        refreshDisplayId()
        ensureQueueLoadedForCommands()
        sanitizeFocusState("handleWindowManagerCommand:$cmd")
        
        // CONVERT 1-BASED INDEX TO 0-BASED INTERNAL INDEX
        // If user sends 1, we get 0. If user sends 0 or nothing (-1), it stays invalid (-1).
        val rawIndex = intent.getIntExtra("INDEX", -1)
        val index = if (rawIndex > 0) rawIndex - 1 else -1


        when (cmd) {
            "OPEN_SWAP" -> {
                // Trigger the OPEN_SWAP flow via triggerCommand
                val cmdDef = AVAILABLE_COMMANDS.find { it.id == "OPEN_SWAP" }
                if (cmdDef != null) triggerCommand(cmdDef)
            }
            "OPEN_MOVE_TO" -> {
                // Trigger the OPEN_MOVE_TO flow via triggerCommand
                val cmdDef = AVAILABLE_COMMANDS.find { it.id == "OPEN_MOVE_TO" }
                if (cmdDef != null) {
                    triggerCommand(cmdDef)
                }
            }
            "SWAP" -> {
                // Convert both A and B from 1-based
                val rawA = intent.getIntExtra("INDEX_A", -1)
                val rawB = intent.getIntExtra("INDEX_B", -1)
                val idxA = if (rawA > 0) rawA - 1 else -1
                val idxB = if (rawB > 0) rawB - 1 else -1
                
                if (idxA in selectedAppsQueue.indices && idxB in selectedAppsQueue.indices) {
                    val appA = selectedAppsQueue[idxA]
                    val appB = selectedAppsQueue[idxB]

                    // Track apps that need window state changes
                    val appsToMinimize = mutableListOf<MainActivity.AppInfo>()
                    val appsToRestore = mutableListOf<MainActivity.AppInfo>()

                    val aIsBlank = appA.packageName == PACKAGE_BLANK
                    val bIsBlank = appB.packageName == PACKAGE_BLANK
                    
                    // [FIX] Special case: swapping active app with active blank
                    // When an active real app swaps with an active blank, the app should be minimized
                    if (!appA.isMinimized && !appB.isMinimized && (aIsBlank || bIsBlank)) {
                        // One is blank, one is real app - minimize the real app
                        if (!aIsBlank) {
                            appsToMinimize.add(appA)
                            appA.isMinimized = true
                        }
                        if (!bIsBlank) {
                            appsToMinimize.add(appB)
                            appB.isMinimized = true
                        }
                    } else if (appA.isMinimized != appB.isMinimized) {
                        // LOGIC: State Swap if mixed Active/Inactive
                        // This allows inactive apps to "take the place" of active ones
                        if (!appA.isMinimized && !aIsBlank) {
                            appsToMinimize.add(appA)
                        }
                        if (!appB.isMinimized && !bIsBlank) {
                            appsToMinimize.add(appB)
                        }
                        if (appA.isMinimized && !aIsBlank) {
                            appsToRestore.add(appA)
                        }
                        if (appB.isMinimized && !bIsBlank) {
                            appsToRestore.add(appB)
                        }

                        val stateA = appA.isMinimized
                        appA.isMinimized = appB.isMinimized
                        appB.isMinimized = stateA
                    }

                    Collections.swap(selectedAppsQueue, idxA, idxB)

                    // [FIX] Actually minimize windows
                    for (app in appsToMinimize) {
                        val basePkg = app.getBasePackage()
                        val cls = app.className
                        
                        // Clear focus if minimizing the active app
                        val isGemini = basePkg == "com.google.android.apps.bard"
                        val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"
                        if (activePackageName == basePkg || 
                            activePackageName == app.packageName ||
                            (isGemini && activeIsGoogle)) {
                            activePackageName = null
                        }
                        
                        manualStateOverrides[basePkg] = System.currentTimeMillis()
                        minimizedAtTimestamps[basePkg] = System.currentTimeMillis()
                        
                        Thread {
                            try {
                                if (currentDisplayId >= 2) {
                                    val visibleCount = shellService?.getVisiblePackages(currentDisplayId)?.size ?: 0
                                    if (visibleCount <= 1) {
                                        showWallpaper()
                                    } else {
                                        val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                        if (tid != -1) shellService?.moveTaskToBack(tid)
                                    }
                                } else {
                                    val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                    if (tid != -1) shellService?.moveTaskToBack(tid)
                                }
                            } catch (e: Exception) {}
                        }.start()
                    }
                    
                    // [FIX] Actually restore windows
                    for (app in appsToRestore) {
                        val basePkg = app.getBasePackage()
                        val cls = app.className
                        
                        manualStateOverrides[basePkg] = System.currentTimeMillis()
                        minimizedAtTimestamps.remove(basePkg)
                        
                        Thread {
                            try {
                                val component = if (!cls.isNullOrEmpty() && cls != "null" && cls != "default") "$basePkg/$cls" else null
                                val cmd = if (component != null) {
                                    "am start -n $component --display $currentDisplayId --windowingMode 5 --user 0"
                                } else {
                                    "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"
                                }
                                shellService?.runCommand(cmd)
                            } catch (e: Exception) {}
                        }.start()
                    }

                    // LOGIC: Remove Inactive Blanks (Auto-Delete)
                    // [FIX] Remove by index - all blanks are equals() so removeAll removes all of them
                    val indicesToRemove = mutableListOf<Int>()
                    if (appA.packageName == PACKAGE_BLANK && appA.isMinimized) indicesToRemove.add(idxB)
                    if (appB.packageName == PACKAGE_BLANK && appB.isMinimized) indicesToRemove.add(idxA)
                    
                    for (i in indicesToRemove.sortedDescending()) {
                        selectedAppsQueue.removeAt(i)
                    }

                    refreshQueueAndLayout("Swapped slots $rawA & $rawB", forceRetile = true, retileDelayMs = 300L)
                }
            }
            // =====================================================================
            // MOVE_TO: Move app from slot A to slot B, shifting others
            // Example: 4 active apps + 1 minimized. MOVE_TO minimized app to slot 3:
            // - Apps in slots 3,4 shift to 4,5 (slot 5 becomes minimized)
            // - Previously minimized app is restored and placed in slot 3
            // =====================================================================
            "MOVE_TO" -> {
                val rawA = intent.getIntExtra("INDEX_A", -1)
                val rawB = intent.getIntExtra("INDEX_B", -1)
                val srcIdx = if (rawA > 0) rawA - 1 else -1
                val dstIdx = if (rawB > 0) rawB - 1 else -1
                
                if (srcIdx == dstIdx) {
                    safeToast("Source and destination are the same")
                    return
                }
                
                if (srcIdx in selectedAppsQueue.indices && dstIdx in selectedAppsQueue.indices) {
                    val movingApp = selectedAppsQueue[srcIdx]
                    val movingIsBlank = movingApp.packageName == PACKAGE_BLANK
                    
                    // Get active slot count from current layout
                    val rects = getLayoutRects()
                    val activeSlotCount = rects.size
                    
                    // Track if moving app was minimized (to restore it if moving to active slot)
                    val wasMinimized = movingApp.isMinimized
                    
                    // Track apps that need window state changes
                    val appsToMinimize = mutableListOf<MainActivity.AppInfo>()
                    val appsToRestore = mutableListOf<MainActivity.AppInfo>()
                    
                    // Remove app from source position
                    selectedAppsQueue.removeAt(srcIdx)
                    
                    // Adjust destination index if source was before it
                    val adjustedDstIdx = if (srcIdx < dstIdx) dstIdx - 1 else dstIdx
                    
                    // Insert at destination
                    selectedAppsQueue.add(adjustedDstIdx, movingApp)
                    
                    // Now update minimized states based on new positions
                    for (i in selectedAppsQueue.indices) {
                        val app = selectedAppsQueue[i]
                        if (app.packageName == PACKAGE_BLANK) continue
                        
                        val isInActiveSlot = i < activeSlotCount
                        val wasActive = !app.isMinimized
                        
                        if (isInActiveSlot && app.isMinimized) {
                            // App moved into active slot - needs to be restored
                            app.isMinimized = false
                            appsToRestore.add(app)
                        } else if (!isInActiveSlot && !app.isMinimized) {
                            // App pushed out of active slots - needs to be minimized
                            app.isMinimized = true
                            appsToMinimize.add(app)
                        }
                    }
                    
                    // Execute minimize operations
                    for (app in appsToMinimize) {
                        val basePkg = app.getBasePackage()
                        val cls = app.className
                        
                        // Clear focus if minimizing the active app
                        val isGemini = basePkg == "com.google.android.apps.bard"
                        val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"
                        if (activePackageName == basePkg || 
                            activePackageName == app.packageName ||
                            (isGemini && activeIsGoogle)) {
                            activePackageName = null
                        }
                        
                        manualStateOverrides[basePkg] = System.currentTimeMillis()
                        minimizedAtTimestamps[basePkg] = System.currentTimeMillis()
                        
                        Thread {
                            try {
                                if (currentDisplayId >= 2) {
                                    val visibleCount = shellService?.getVisiblePackages(currentDisplayId)?.size ?: 0
                                    if (visibleCount <= 1) {
                                        showWallpaper()
                                    } else {
                                        val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                        if (tid != -1) shellService?.moveTaskToBack(tid)
                                    }
                                } else {
                                    val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                    if (tid != -1) shellService?.moveTaskToBack(tid)
                                }
                            } catch (e: Exception) {}
                        }.start()
                    }
                    
                    // Execute restore operations
                    for (app in appsToRestore) {
                        val basePkg = app.getBasePackage()
                        val cls = app.className
                        
                        manualStateOverrides[basePkg] = System.currentTimeMillis()
                        minimizedAtTimestamps.remove(basePkg)
                        
                        Thread {
                            try {
                                val component = if (!cls.isNullOrEmpty() && cls != "null" && cls != "default") "$basePkg/$cls" else null
                                val cmd = if (component != null) {
                                    "am start -n $component --display $currentDisplayId --windowingMode 5 --user 0"
                                } else {
                                    "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"
                                }
                                shellService?.runCommand(cmd)
                            } catch (e: Exception) {}
                        }.start()
                    }
                    
                    // Remove inactive blanks (same cleanup as SWAP)
                    val indicesToRemove = mutableListOf<Int>()
                    for (i in selectedAppsQueue.indices) {
                        val app = selectedAppsQueue[i]
                        if (app.packageName == PACKAGE_BLANK && app.isMinimized) {
                            indicesToRemove.add(i)
                        }
                    }
                    for (i in indicesToRemove.sortedDescending()) {
                        selectedAppsQueue.removeAt(i)
                    }
                    
                    val appName = if (movingIsBlank) "Blank" else movingApp.getBasePackage().substringAfterLast('.')
                    refreshQueueAndLayout("Moved $appName to slot $rawB", forceRetile = true, retileDelayMs = 300L)
                }
            }
            // =====================================================================
            // ACTIVE WINDOW COMMANDS (PC Style)
            // Finds the currently focused app in the queue and moves it.
            // =====================================================================
            "SWAP_ACTIVE_LEFT", "SWAP_ACTIVE_RIGHT" -> {
                if (activePackageName == null) {
                    safeToast("No active window detected")
                    return
                }

                // Only swap within visible tiled apps (non-minimized, non-blank).
                // Swapping by raw queue index can cross minimized/blank entries and desync layout.
                val activeIndices = selectedAppsQueue.withIndex()
                    .filter { (_, app) -> !app.isMinimized && app.packageName != PACKAGE_BLANK }
                    .map { it.index }

                if (activeIndices.isEmpty()) {
                    safeToast("No active tiled apps")
                    return
                }

                val activeVisiblePos = activeIndices.indexOfFirst { idx ->
                    val app = selectedAppsQueue[idx]
                    app.packageName == activePackageName ||
                        (app.packageName == "com.google.android.apps.bard" && activePackageName == "com.google.android.googlequicksearchbox")
                }

                if (activeVisiblePos != -1) {
                    val dir = if (cmd == "SWAP_ACTIVE_LEFT") -1 else 1
                    val targetVisiblePos = activeVisiblePos + dir

                    if (targetVisiblePos in activeIndices.indices) {
                        val activeIdx = activeIndices[activeVisiblePos]
                        val targetIdx = activeIndices[targetVisiblePos]
                        val focusPkg = selectedAppsQueue[activeIdx].packageName

                        Collections.swap(selectedAppsQueue, activeIdx, targetIdx)
                        refreshQueueAndLayout(
                            "Moved Active Window ${if (dir < 0) "Left" else "Right"}",
                            focusPackage = focusPkg,
                            forceRetile = true,
                            retileDelayMs = 300L
                        )
                    } else {
                        safeToast("Edge of layout reached")
                    }
                } else {
                    safeToast("Active app not in layout")
                }
            }
            // =====================================================================
            // MINIMIZE_ALL - Minimize all tiled (non-minimized) apps
            // Leaves blank spaces as-is (minimized state) so layout can be restored
            // =====================================================================
            "MINIMIZE_ALL" -> {
                val appsToMinimize = selectedAppsQueue.filter { 
                    !it.isMinimized && it.packageName != PACKAGE_BLANK 
                }
                
                if (appsToMinimize.isEmpty()) {
                    safeToast("No active tiled apps")
                    return
                }
                
                // Clear active focus
                activePackageName = null
                
                for (app in appsToMinimize) {
                    app.isMinimized = true
                    val basePkg = app.getBasePackage()
                    manualStateOverrides[basePkg] = System.currentTimeMillis()
                    minimizedAtTimestamps[basePkg] = System.currentTimeMillis()
                }
                
                // Move all to back in background thread
                Thread {
                    try {
                        for (app in appsToMinimize) {
                            val basePkg = app.getBasePackage()
                            val cls = app.className
                            if (currentDisplayId >= 2) {
                                val visibleCount = shellService?.getVisiblePackages(currentDisplayId)?.size ?: 0
                                if (visibleCount <= 1) {
                                    showWallpaper()
                                } else {
                                    val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                    if (tid != -1) shellService?.moveTaskToBack(tid)
                                }
                            } else {
                                val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                if (tid != -1) shellService?.moveTaskToBack(tid)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "MINIMIZE_ALL failed", e)
                    }
                }.start()
                
                // Also mark blanks as minimized to preserve layout positions
                for (app in selectedAppsQueue) {
                    if (app.packageName == PACKAGE_BLANK && !app.isMinimized) {
                        app.isMinimized = true
                    }
                }
                
                refreshQueueAndLayout("Minimized ${appsToMinimize.size} apps")
            }
            // =====================================================================
            // RESTORE_ALL - Restore minimized apps up to available layout slots
            // Respects current layout slot count (e.g., 2-app layout = max 2 restored)
            // =====================================================================
            "RESTORE_ALL" -> {
                val layoutRects = getLayoutRects()
                val maxSlots = layoutRects.size
                
                // Count currently active (non-minimized) apps and blanks
                val currentActiveCount = selectedAppsQueue.count { !it.isMinimized }
                val availableSlots = maxSlots - currentActiveCount
                
                if (availableSlots <= 0) {
                    safeToast("No available slots (layout: $maxSlots)")
                    return
                }
                
                // Get minimized apps (excluding blanks), sorted by most recently minimized first
                val minimizedApps = selectedAppsQueue.filter { 
                    it.isMinimized && it.packageName != PACKAGE_BLANK 
                }.sortedByDescending { minimizedAtTimestamps[it.getBasePackage()] ?: 0L }
                
                if (minimizedApps.isEmpty()) {
                    safeToast("No minimized apps to restore")
                    return
                }
                
                // Restore up to available slots
                val appsToRestore = minimizedApps.take(availableSlots)
                
                for (app in appsToRestore) {
                    app.isMinimized = false
                    val basePkg = app.getBasePackage()
                    manualStateOverrides[basePkg] = System.currentTimeMillis()
                    minimizedAtTimestamps.remove(basePkg)
                }
                
                // Also restore minimized blanks up to remaining available slots
                val remainingSlots = availableSlots - appsToRestore.size
                if (remainingSlots > 0) {
                    var blanksRestored = 0
                    for (app in selectedAppsQueue) {
                        if (app.packageName == PACKAGE_BLANK && app.isMinimized && blanksRestored < remainingSlots) {
                            app.isMinimized = false
                            blanksRestored++
                        }
                    }
                }
                
                // Clear auto-minimized flag since we're explicitly restoring
                tiledAppsAutoMinimized = false
                lastExplicitTiledLaunchAt = System.currentTimeMillis()
                
                refreshQueueAndLayout("Restored ${appsToRestore.size} apps", forceRetile = true, retileDelayMs = 300L)
            }
            "KILL" -> {
                if (index in selectedAppsQueue.indices) {
                    val app = selectedAppsQueue[index]
                    if (app.packageName != PACKAGE_BLANK) {
                        val basePkg = app.getBasePackage()
                        // [FIX] Record killed package to prevent re-adding as fullscreen app
                        recentlyRemovedPackages[basePkg] = System.currentTimeMillis()
                        removeFromFocusHistory(basePkg) // Clean up history
                        Thread { try { shellService?.forceStop(basePkg) } catch(e: Exception){} }.start()
                    }
                    selectedAppsQueue.removeAt(index)
                    
                    if (reorderSelectionIndex == index) reorderSelectionIndex = -1
                    else if (reorderSelectionIndex > index) reorderSelectionIndex--
                    
                    refreshQueueAndLayout("Closed ${app.label}")
                }
            }
            // =====================================================================
            // MINIMIZE (Formerly Hide) - Toggles 'isMinimized' flag
            // =====================================================================
            // =====================================================================
            // MINIMIZE (Shifting Hide)
            // SUMMARY: Toggles the 'isMinimized' flag. The app remains in queue
            //          but is skipped by tiling, causing neighbors to SHIFT to fill gap.
            // =====================================================================
            "MINIMIZE", "UNMINIMIZE", "TOGGLE_MINIMIZE" -> {
                if (index in selectedAppsQueue.indices) {
                    val app = selectedAppsQueue[index]

                    // SPECIAL BLANK LOGIC: Hide/Minimize = Delete
                    if (app.packageName == PACKAGE_BLANK) {
                        if (cmd == "MINIMIZE" || (cmd == "TOGGLE_MINIMIZE" && !app.isMinimized)) {
                            selectedAppsQueue.removeAt(index)
                            refreshQueueAndLayout("Removed Blank Space")
                            return
                        }
                    }

                    // [FIX] If apps were auto-minimized by fullscreen detection but isMinimized
                    // was never set, UNMINIMIZE would see isMinimized=false and skip.
                    // Detect this state: mark all OTHER apps as truly minimized so the
                    // restored app gets slot 1. Then fall through to normal restore logic.
                    if (tiledAppsAutoMinimized && (cmd == "UNMINIMIZE" || cmd == "TOGGLE_MINIMIZE") && !app.isMinimized) {
                        tiledAppsAutoMinimized = false
                        lastExplicitTiledLaunchAt = System.currentTimeMillis()
                        // Mark all OTHER queue apps as minimized (they were already moved to back)
                        for (other in selectedAppsQueue) {
                            if (other !== app && !other.isMinimized && other.packageName != PACKAGE_BLANK) {
                                other.isMinimized = true
                                minimizedAtTimestamps[other.getBasePackage()] = System.currentTimeMillis()
                            }
                        }
                        // The target app stays isMinimized=false → it will be the only active app → slot 1
                        // Now retile to launch only this app in slot 1
                        refreshQueueAndLayout("Restored ${app.label}", forceRetile = true, retileDelayMs = 300L)
                        return
                    }

                    // [FIX] If UNMINIMIZE is called but app.isMinimized is already false,
                    // still launch the app to bring it to front. This handles cases where
                    // the app was closed/crashed/covered without updating minimized state.
                    // NOTE: Only apply to explicit UNMINIMIZE, not TOGGLE_MINIMIZE (which should toggle)
                    if (cmd == "UNMINIMIZE" && !app.isMinimized) {
                        lastExplicitTiledLaunchAt = System.currentTimeMillis()
                        val basePkg = app.getBasePackage()
                        val cls = app.className
                        Thread {
                            try {
                                val component = if (!cls.isNullOrEmpty() && cls != "null" && cls != "default") "$basePkg/$cls" else null
                                val cmd = if (component != null) {
                                    "am start -n $component --display $currentDisplayId --windowingMode 5 --user 0"
                                } else {
                                    "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"
                                }
                                shellService?.runCommand(cmd)
                            } catch (e: Exception) {
                                Log.e(TAG, "Force bring-to-front failed", e)
                            }
                        }.start()
                        refreshQueueAndLayout("Restored ${app.label}", forceRetile = true, retileDelayMs = 350L)
                        return
                    }

                    val newState = when (cmd) {
                        "MINIMIZE" -> true
                        "UNMINIMIZE" -> false
                        else -> !app.isMinimized
                    }

                                        if (app.isMinimized != newState) {
                                                                app.isMinimized = newState
                                                                
                                                                val basePkg = app.getBasePackage()
                                                                // [FIX] Record manual override timestamp
                                                                manualStateOverrides[basePkg] = System.currentTimeMillis()
                                                                
                                                                // Track minimization timestamp for queue ordering (newest first)
                                                                if (newState) {
                                                                    minimizedAtTimestamps[basePkg] = System.currentTimeMillis()
                                                                } else {
                                                                    minimizedAtTimestamps.remove(basePkg)
                                                                }
                                                                
                                                                val cls = app.className
                                                                
                                                                if (newState) {
                                                                     // [FIX] Clear focus if minimizing the active app
                                                                     // Handles Gemini alias (app=bard, active=google)
                                                                     val isGemini = basePkg == "com.google.android.apps.bard"
                                                                     val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"
                                                                     
                                                                     if (activePackageName == basePkg || 
                                                                         activePackageName == app.packageName ||
                                                                         (isGemini && activeIsGoogle)) {
                                                                         activePackageName = null
                                                                     }
                                                                     
    // MINIMIZING: Move to Back
                                     Thread {
                                         try {
                                             // Virtual Display Strategy
                                             if (currentDisplayId >= 2) {
                                                 // Check if this is the ONLY visible app
                                                 val visibleCount = shellService?.getVisiblePackages(currentDisplayId)?.size ?: 0
                                                 val isLastApp = visibleCount <= 1 

                                                 if (isLastApp) {
                                                     // LAST APP: "Launch" the wallpaper to cover it.
                                                     // This avoids the system hanging while searching for a Home screen.
                                                     showWallpaper()
                                                 } else {
                                                     // NOT LAST: Standard minimize works fine (focus transfers to app behind)
                                                     val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                                     if (tid != -1) shellService?.moveTaskToBack(tid)
                                                 }
                                             } else {
                                                 // Standard Display (Phone/Cover)
                                                 val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                                 if (tid != -1) shellService?.moveTaskToBack(tid)
                                             }
                                         } catch(e: Exception){}
                                     }.start()
                                                                     // We removed the redundant thread here to prevent race conditions and double-execution lag.

                                                                } else {
                             // RESTORING: Bring to Front on Current Display
                             // We reuse the launch logic which handles "Bring to Front" if already running.
                             // We run this in background to avoid UI stutter.

                             tiledAppsAutoMinimized = false
                             lastExplicitTiledLaunchAt = System.currentTimeMillis()
                             minimizedAtTimestamps.remove(app.getBasePackage()) // Clear minimized timestamp

                             // [FULLSCREEN] If there's a visible fullscreen app not in the queue, add it to position 0.
                             // This converts the fullscreen app into a tiled app so both stay open together.
                             // The fullscreen app goes to position 0 (first tiling slot).
                             try {
                                 val visiblePkgs = shellService?.getVisiblePackages(currentDisplayId) ?: emptyList()
                                 val userVisibleApps = visiblePkgs.mapNotNull { pkgName ->
                                     val pkg = if (pkgName.contains(":")) pkgName.substringBefore(":") else pkgName
                                     if (pkg == packageName || pkg == PACKAGE_TRACKPAD ||
                                         pkg.contains("systemui") || pkg.contains("inputmethod") ||
                                         pkg.startsWith("com.android.") || pkg.startsWith("com.samsung.")) null
                                     else allAppsList.find { it.getBasePackage() == pkg }
                                 }.filter { it.packageName != packageName && it.packageName != PACKAGE_TRACKPAD }
                                 
                                 val queuePkgs = selectedAppsQueue.map { it.getBasePackage() }.toSet()
                                 
                                 // If exactly one visible app (fullscreen) not in queue, add to position 0
                                 if (userVisibleApps.size == 1) {
                                     val fullscreenApp = userVisibleApps.first()
                                     if (!queuePkgs.contains(fullscreenApp.getBasePackage())) {
                                         fullscreenApp.isMinimized = false
                                         selectedAppsQueue.add(0, fullscreenApp.copy())
                                     }
                                 }
                             } catch (e: Exception) { Log.e(TAG, "Failed to detect fullscreen app", e) }
                             
                             // Continue with existing logic for multiple visible apps
                             try {
                                 val activeApps = shellService?.getVisiblePackages(currentDisplayId)
                                     ?.mapNotNull { pkgName -> allAppsList.find { it.packageName == pkgName } }
                                     ?.filter { it.packageName != packageName && it.packageName != PACKAGE_TRACKPAD }
                                     ?: emptyList()
                                 val queuePkgs = selectedAppsQueue.map { it.getBasePackage() }.toSet()
                                 val fsApps = activeApps.filter { it.getBasePackage() !in queuePkgs && !it.isMinimized }
                                 if (fsApps.isNotEmpty() && selectedAppsQueue.any { !it.isMinimized || it == app }) {
                                     for (fsApp in fsApps) {
                                         selectedAppsQueue.add(fsApp.copy())
                                     }
                                     uiHandler.post { updateAllUIs() }
                                 }
                             } catch (e: Exception) { Log.e(TAG, "UNMINIMIZE: Failed to detect fullscreen apps", e) }

                             Thread {
                                 try {
                                     // Use launchViaShell which forces display ID and windowing mode
                                     // We calculate the target bounds based on current layout to ensure it snaps back correctly
                                     val rects = getLayoutRects()
                                     val bounds = if (index < rects.size) rects[index] else null

                                     // This command forces the activity to the top of the stack on current display
                                     val component = if (!cls.isNullOrEmpty() && cls != "null" && cls != "default") "$basePkg/$cls" else null
                                     val cmd = if (component != null) {
                                         "am start -n $component --display $currentDisplayId --windowingMode 5 --user 0"
                                     } else {
                                         "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"
                                     }
                                     shellService?.runCommand(cmd)

                                     // If we have bounds, apply them immediately
                                     if (bounds != null) {
                                         Thread.sleep(300) // Wait for start
                                         val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                         if (tid != -1) {
                                             shellService?.runCommand("am task resize $tid ${bounds.left} ${bounds.top} ${bounds.right} ${bounds.bottom}")
                                         }
                                     }
                                 } catch(e: Exception) {
                                     Log.e(TAG, "Restore failed", e)
                                 }
                             }.start()
                        }

                        val retileDelay = if (newState) 200L else 350L
                        refreshQueueAndLayout(
                            if (newState) "Minimized ${app.label}" else "Restored ${app.label}",
                            forceRetile = true,
                            retileDelayMs = retileDelay
                        )
                    }
                }
            }

                        // =====================================================================
                        // HIDE (Swap & Minimize)
                        // SUMMARY: 1. Minimizes target app window.
                        //          2. Adds Blank Space to end of queue.
                        //          3. Swaps Target App with Blank Space.
                        //          Result: Slot becomes empty. Target App moves to end of dock (saved).
                        // =====================================================================
                        "HIDE" -> {
                             if (index in selectedAppsQueue.indices) {
                                val targetApp = selectedAppsQueue[index]
                                
                                // If already blank, remove it
                                if (targetApp.packageName == PACKAGE_BLANK) {
                                    selectedAppsQueue.removeAt(index)
                                    refreshQueueAndLayout("Removed Blank Space")
                                    return
                                }
                                
                                // 1. Force Minimize the visual window
                                val basePkg = targetApp.getBasePackage()
                                val cls = targetApp.className
                                
                                // [FIX] Record hidden package to prevent re-adding as fullscreen app
                                recentlyRemovedPackages[basePkg] = System.currentTimeMillis()
                                removeFromFocusHistory(basePkg)
                                
                                // [FIX] Clear focus if hiding the active app
                                val isGemini = basePkg == "com.google.android.apps.bard"
                                val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"

                                if (activePackageName == basePkg || 
                                    activePackageName == targetApp.packageName ||
                                    (isGemini && activeIsGoogle)) {
                                    activePackageName = null
                                }

                                Thread { 
                                    try { 
                                        val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                        if (tid != -1) shellService?.moveTaskToBack(tid)
                                        // [DEPRECATED] if (killAppOnExecute) shellService?.forceStop(basePkg)
                                    } catch(e: Exception){}
                                }.start()
                                
                                // 2. Mark target as minimized (Grey out)
                                targetApp.isMinimized = true
                                
                                // 3. Create Blank App
                                val blankApp = MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null)
                                
                                // 4. Add Blank to end of queue
                                selectedAppsQueue.add(blankApp)
                                
                                // 5. Swap Target (at index) with Blank (at end)
                                // Layout: [A, B, C] -> HIDE B -> [A, Blank, C, B(min)]
                                Collections.swap(selectedAppsQueue, index, selectedAppsQueue.lastIndex)
                                
                                refreshQueueAndLayout(
                                    "Hidden Slot ${index + 1}",
                                    forceRetile = true,
                                    retileDelayMs = 200L
                                )
                             }
                        }            "LAYOUT" -> {
                val type = intent.getIntExtra("TYPE", -1)
                if (type != -1) {
                    selectedLayoutType = type
                    AppPreferences.saveLastLayout(this, type, currentDisplayId)
                    AppPreferences.saveLastLayout(this, type, currentDisplayId, orientSuffix())
                    refreshQueueAndLayout("Layout: ${getLayoutName(type)}")
                }
            }
            "CLEAR_ALL" -> {
                // [DEPRECATED] killAppOnExecute removed - apps are not force stopped on clear
                // if (killAppOnExecute) {
                //     val activeApps = selectedAppsQueue.filter { !it.isMinimized && it.packageName != PACKAGE_BLANK }
                //     Thread {
                //         for (app in activeApps) {
                //             try { shellService?.forceStop(app.getBasePackage()) } catch(e: Exception){}
                //         }
                //     }.start()
                // }
                // Wipe all history
                activePackageName = null
                lastValidPackageName = null
                secondLastValidPackageName = null
                
                selectedAppsQueue.clear()
                refreshQueueAndLayout("Cleared All")
            }
            "OPEN_DRAWER" -> {
                toggleDrawer()
                refreshQueueAndLayout("Toggled Drawer", skipTiling = true)
            }
            "SET_FOCUS" -> {
                // 'index' is 0-based here
                if (index in selectedAppsQueue.indices) {
                    val app = selectedAppsQueue[index]
                    if (app.packageName != PACKAGE_BLANK) {
                        
                        // [FIX] Internal Focus vs System Launch
                        if (isExpanded) {
                            // Drawer Open: Update Internal Variable Only (Green Underline)
                            if (activePackageName != app.packageName) {
                                if (activePackageName != null) lastValidPackageName = activePackageName
                                activePackageName = app.packageName
                                updateAllUIs()
                            }
                        } else {
                            // Drawer Closed: Focus or Launch
                            val rects = getLayoutRects()
                            // Layout rects only include non-minimized slots, so find the layout index
                            val layoutIdx = selectedAppsQueue.take(index).count { !it.isMinimized }
                            val bounds = if (layoutIdx < rects.size) rects[layoutIdx] else null
                            
                            // [FIX] Skip focusViaTask if app is already focused - avoids 3-5 second Android delay
                            val alreadyFocused = (activePackageName == app.packageName) || 
                                (app.packageName == "com.google.android.apps.bard" && activePackageName == "com.google.android.googlequicksearchbox")
                            
                            val targetStillInQueue = selectedAppsQueue.any {
                                it.packageName == app.packageName && it.className == app.className
                            }

                            if (!targetStillInQueue) {
                                Log.w(TAG, "SET_FOCUS skipped stale target: ${app.packageName}")
                                return
                            }
                            
                            if (alreadyFocused) {
                                // App already focused - am start is no-op, use input tap to force focus back
                                if (bounds != null) {
                                    val centerX = bounds.centerX()
                                    val centerY = bounds.centerY()
                                    try {
                                        shellService?.runCommand("input tap $centerX $centerY")
                                    } catch (e: Exception) {
                                        Log.e(TAG, "SET_FOCUS tap-back failed", e)
                                    }
                                }
                            } else if (!app.isMinimized) {
                                focusViaTask(app.getBasePackage(), bounds)
                            } else {
                                launchViaShell(app.getBasePackage(), app.className, bounds)
                            }
                            sendCursorToAppCenter(bounds)
                            
                            if (!alreadyFocused) {
                                // Update focus state only when changing apps
                                if (activePackageName != null) lastValidPackageName = activePackageName
                                activePackageName = app.packageName
                                manualFocusLockUntil = System.currentTimeMillis() + 2000L  // Lock for 2 seconds
                                try {
                                    updateAllUIs2()
                                } catch (e: Exception) {
                                    Log.e(TAG, "SET_FOCUS: updateAllUIs CRASHED", e)
                                }
                                safeToast("Focused: ${app.label}")
                            }
                        }
                    }
                }
            }
            "FOCUS_LAST" -> {
                // Switch to previous valid app
                val target = if (lastValidPackageName == activePackageName) secondLastValidPackageName else lastValidPackageName

                if (target != null) {
                    val app = selectedAppsQueue.find { it.packageName == target }
                    if (app != null) {
                        if (app.packageName == PACKAGE_BLANK || app.isMinimized) {
                            safeToast("Last app is hidden/minimized")
                            return
                        }

                        // [FIX] Internal Focus vs System Launch
                        if (isExpanded) {
                            // Drawer Open: Update Internal Variable Only
                            if (activePackageName != app.packageName) {
                                activePackageName = app.packageName
                                // Swap logic for history
                                lastValidPackageName = target
                                updateAllUIs()
                            }
                        } else {
                            // Drawer Closed: Focus or Launch
                            val idx = selectedAppsQueue.indexOf(app)
                            val rects = getLayoutRects()
                            // Layout rects only include non-minimized slots, so find the layout index
                            val layoutIdx = if (idx >= 0) selectedAppsQueue.take(idx).count { !it.isMinimized } else -1
                            val bounds = if (layoutIdx >= 0 && layoutIdx < rects.size) rects[layoutIdx] else null
                            focusViaTask(app.getBasePackage(), bounds)
                            sendCursorToAppCenter(bounds)
                            
                            // [FIX] Manually update focus state since moveTaskToFront doesn't trigger accessibility events
                            if (activePackageName != app.packageName) {
                                if (activePackageName != null) lastValidPackageName = activePackageName
                                activePackageName = app.packageName
                                manualFocusLockUntil = System.currentTimeMillis() + 2000L  // Lock for 2 seconds
                                try {
                                    updateAllUIs2()
                                } catch (e: Exception) {
                                    Log.e(TAG, "SET_FOCUS: updateAllUIs CRASHED", e)
                                }
                            }
                            safeToast("Focused: ${app.label}")
                        }
                    } else {
                        safeToast("Last app not in layout")
                    }
                } else {
                    safeToast("No history found")
                }
            }
        }
    }

    // Helper to calculate current layout rectangles without executing launch
    private fun getLayoutRects(): List<Rect> {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = dm.getDisplay(currentDisplayId) ?: return emptyList()
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)
        var w = metrics.widthPixels
        var h = metrics.heightPixels

        // Apply Resolution Override (if any)
        val targetDim = getTargetDimensions(selectedResolutionIndex)
        if (targetDim != null) {
             w = targetDim.first
             h = targetDim.second
        }

        // === MARGIN LOGIC ===
        // Calculate effective height based on Top and Bottom margins
        val topPx = (h * topMarginPercent / 100f).toInt()
        val bottomPx = (h * effectiveBottomMarginPercent() / 100f).toInt()
        val effectiveH = max(100, h - topPx - bottomPx) // Safety floor


        // ====================

        val rects = mutableListOf<Rect>()
        when (selectedLayoutType) {
            LAYOUT_FULL -> rects.add(Rect(0, 0, w, effectiveH))
            LAYOUT_SIDE_BY_SIDE -> {
                rects.add(Rect(0, 0, w/2, effectiveH))
                rects.add(Rect(w/2, 0, w, effectiveH))
            }
            LAYOUT_TOP_BOTTOM -> {
                // Split effective space in half
                val mid = effectiveH / 2
                rects.add(Rect(0, 0, w, mid))
                rects.add(Rect(0, mid, w, effectiveH))
            }
            LAYOUT_TRI_EVEN -> {
                val third = w / 3
                rects.add(Rect(0, 0, third, effectiveH))
                rects.add(Rect(third, 0, third * 2, effectiveH))
                rects.add(Rect(third * 2, 0, w, effectiveH))
            }
            LAYOUT_CORNERS -> {
                val midH = effectiveH / 2
                val midW = w / 2
                rects.add(Rect(0, 0, midW, midH))
                rects.add(Rect(midW, 0, w, midH))
                rects.add(Rect(0, midH, midW, effectiveH))
                rects.add(Rect(midW, midH, w, effectiveH))
            }
            LAYOUT_TRI_SIDE_MAIN_SIDE -> {
                val quarter = w / 4
                rects.add(Rect(0, 0, quarter, effectiveH))
                rects.add(Rect(quarter, 0, quarter * 3, effectiveH))
                rects.add(Rect(quarter * 3, 0, w, effectiveH))
            }
            LAYOUT_QUAD_ROW_EVEN -> {
                val quarter = w / 4
                rects.add(Rect(0, 0, quarter, effectiveH))
                rects.add(Rect(quarter, 0, quarter * 2, effectiveH))
                rects.add(Rect(quarter * 2, 0, quarter * 3, effectiveH))
                rects.add(Rect(quarter * 3, 0, w, effectiveH))
            }
            LAYOUT_QUAD_TALL_SHORT -> {
                // Top Row: 75% H, 2 Apps (50% W each)
                // Bottom Row: 25% H, 2 Apps (50% W each)
                val splitY = (effectiveH * 0.75f).toInt()
                val midW = w / 2
                
                rects.add(Rect(0, 0, midW, splitY))
                rects.add(Rect(midW, 0, w, splitY))
                rects.add(Rect(0, splitY, midW, effectiveH))
                rects.add(Rect(midW, splitY, w, effectiveH))
            }
            LAYOUT_HEX_TALL_SHORT -> {
                // Top Row: 75% H, 3 Apps (Side/Main/Side 25/50/25)
                // Bottom Row: 25% H, 3 Apps (Same widths)
                val splitY = (effectiveH * 0.75f).toInt()
                val q = w / 4
                
                rects.add(Rect(0, 0, q, splitY))
                rects.add(Rect(q, 0, q * 3, splitY))
                rects.add(Rect(q * 3, 0, w, splitY))
                
                rects.add(Rect(0, splitY, q, effectiveH))
                rects.add(Rect(q, splitY, q * 3, effectiveH))
                rects.add(Rect(q * 3, splitY, w, effectiveH))
            }
            LAYOUT_CUSTOM_DYNAMIC -> {
                if (activeCustomRects != null) {
                    // For custom layouts, we assume they were saved WITH the desired geometry.
                    // However, if the user turns on margin, we should probably clamp them?
                    // For now, let's respect the saved rects exactly as they define absolute pixels.
                    rects.addAll(activeCustomRects!!)
                } else {
                    // Fallback if custom data missing
                    rects.add(Rect(0, 0, w/2, effectiveH))
                    rects.add(Rect(w/2, 0, w, effectiveH))
                }
            }
        }
        
        // SHIFT ALL RECTS DOWN BY TOP MARGIN
        // [FIX] Skip offset for Custom Dynamic layouts (they are absolute snapshots)
        // This prevents double-margin application when loading saved profiles.
        if (topPx > 0 && selectedLayoutType != LAYOUT_CUSTOM_DYNAMIC) {
            for (r in rects) {
                r.offset(0, topPx)
            }
        }
        
        return rects
    }

    private fun refreshQueueAndLayout(
        msg: String,
        focusPackage: String? = null,
        skipTiling: Boolean = false,
        forceRetile: Boolean = false,
        retileDelayMs: Long = 200L
    ) {
        uiHandler.post {
            sortAppQueue() // Ensure active apps are at the front
            sanitizeFocusState("refreshQueueAndLayout:$msg")
            updateAllUIs()

            // Auto-save queue state including minimized packages
            val identifiers = selectedAppsQueue.map { it.getIdentifier() }
            AppPreferences.saveLastQueue(this, identifiers)
            val minimizedPkgs = selectedAppsQueue.filter { it.isMinimized }.map { it.getBasePackage() }.toSet()
            AppPreferences.saveMinimizedPackages(this, minimizedPkgs)

            // ===================================================================================
            // CRITICAL: DOUBLE-MARGIN BUG FIX - DO NOT MODIFY WITHOUT UNDERSTANDING
            // ===================================================================================
            // PROBLEM: When IME shows with auto-adjust margin enabled, apps were getting resized
            // TWICE - once by Launcher (via retileExistingWindows) and once by Android's
            // ADJUST_RESIZE (via IME insets). This left a blank gap between the bottom app
            // and the keyboard.
            //
            // WHY BROADCASTS FAILED: The original fix used TILED_STATE broadcasts to tell DockIME
            // whether to suppress insets. But broadcasts have timing issues - accessibility events
            // fire rapidly during IME transitions, and focus can briefly go to non-managed elements
            // (IME itself, popups, etc.), causing launcherTiledActive to flip to false momentarily.
            // Even a 200ms false state causes Android to apply insets = double margin.
            //
            // SOLUTION: Write the managed-apps state to SharedPreferences (DockIMEPrefs).
            // DockIME reads this directly in onComputeInsets(). SharedPreferences are persistent
            // and not subject to broadcast timing issues.
            //
            // KEY INVARIANT: When autoAdjustMarginForIME=true AND there are non-minimized managed
            // apps, DockIME MUST suppress insets. The Launcher handles ALL app resizing.
            // ===================================================================================
            val activeNonMinimized = selectedAppsQueue.filter { !it.isMinimized }
            val hasManagedApps = autoAdjustMarginForIME && activeNonMinimized.isNotEmpty()
            getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE).edit()
                .putBoolean("launcher_has_managed_apps", hasManagedApps)
                .apply()

            // Broadcast tells DockIME if app is actively TILED (2+ apps visible).
            // Single app (even in queue) should behave as fullscreen for proper inset handling.
            if (activePackageName != null) {
                val isGeminiFocused = activePackageName == "com.google.android.googlequicksearchbox"
                val isManaged = activeNonMinimized.any { it.getBasePackage() == activePackageName || it.packageName == activePackageName || (isGeminiFocused && it.getBasePackage() == "com.google.android.apps.bard") }
                val isActuallyTiled = activeNonMinimized.size > 1 && isManaged
                sendBroadcast(Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.TILED_STATE")
                    .setPackage("com.katsuyamaki.DroidOSTrackpadKeyboard")
                    .putExtra("TILED_ACTIVE", isActuallyTiled))
            }

            safeToast(msg)

            // Trigger Tiling (skip when just opening drawer/visual queue to prevent restoring minimized apps)
            if (isInstantMode && !skipTiling) {
                applyLayoutImmediate(focusPackage)
            } else if (forceRetile && !skipTiling) {
                requestHeadlessRetile("refreshQueue:$msg", retileDelayMs)
            }
        }
    }
    // =================================================================================
    // END WINDOW MANAGER COMMAND PROCESSOR
    // =================================================================================

    private fun logSavedKeybinds() {
        for (cmd in AVAILABLE_COMMANDS) {
            val bind = AppPreferences.getKeybind(this, cmd.id)
            if (bind.second != 0) {
                val keyName = KeyEvent.keyCodeToString(bind.second)
                // WORKAROUND: KeyEvent.metaStateToString may be unresolved in some environments.
                // Using toString() directly on the integer for debug output.
                val modName = if (bind.first != 0) "Meta(${bind.first})" else "None"
            }
        }
    }

    override fun buildAdbCommand(cmdId: String): String? {
        return when (cmdId) {
            "OPEN_DRAWER" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.OPEN_DRAWER"
            "SET_FOCUS" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND SET_FOCUS --ei INDEX 1"
            "FOCUS_LAST" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND FOCUS_LAST"
            "KILL" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND KILL --ei INDEX 1"
            "SWAP_ACTIVE_LEFT" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND SWAP_ACTIVE_LEFT"
            "SWAP_ACTIVE_RIGHT" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND SWAP_ACTIVE_RIGHT"
            "MINIMIZE" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND MINIMIZE --ei INDEX 1"
            "UNMINIMIZE" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND UNMINIMIZE --ei INDEX 1"
            "HIDE" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND HIDE --ei INDEX 1"
            "SWAP" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND SWAP --ei INDEX_A 1 --ei INDEX_B 2"
            "MOVE_TO" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND MOVE_TO --ei INDEX_A 1 --ei INDEX_B 2"
            "OPEN_MOVE_TO" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND OPEN_MOVE_TO"
            "OPEN_SWAP" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND OPEN_SWAP"
            "MINIMIZE_ALL" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND MINIMIZE_ALL"
            "RESTORE_ALL" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND RESTORE_ALL"
            else -> null
        }
    }

    // RofiAdapter extracted to RofiAdapter.kt

    override fun getKeyCodeFromChar(c: Char): Int {
        val char = c.lowercaseChar()
        return when (char) {
            in 'a'..'z' -> KeyEvent.keyCodeFromString("KEYCODE_${char.uppercase()}")
            in '0'..'9' -> KeyEvent.keyCodeFromString("KEYCODE_$char")
            '`', '~' -> KeyEvent.KEYCODE_GRAVE
            '-', '_' -> KeyEvent.KEYCODE_MINUS
            '=', '+' -> KeyEvent.KEYCODE_EQUALS
            '[', '{' -> KeyEvent.KEYCODE_LEFT_BRACKET
            ']', '}' -> KeyEvent.KEYCODE_RIGHT_BRACKET
            '\\', '|' -> KeyEvent.KEYCODE_BACKSLASH
            ';', ':' -> KeyEvent.KEYCODE_SEMICOLON
            '\'', '"' -> KeyEvent.KEYCODE_APOSTROPHE
            ',', '<' -> KeyEvent.KEYCODE_COMMA
            '.', '>' -> KeyEvent.KEYCODE_PERIOD
            '/', '?' -> KeyEvent.KEYCODE_SLASH
            ' ' -> KeyEvent.KEYCODE_SPACE
            else -> 0
        }
    }
    
    override fun getCharFromKeyCode(code: Int): String {
        // Reverse mapping for display
        if (code == KeyEvent.KEYCODE_GRAVE) return "~"
        if (code == 0) return ""
        val str = KeyEvent.keyCodeToString(code)
        return str.replace("KEYCODE_", "")
    }

    // Helper extension function to set scaled text size
    private fun TextView.setScaledTextSize(baseFontSize: Float, scaleFactor: Float = 1.0f) {
        this.textSize = baseFontSize * scaleFactor
    }
}
