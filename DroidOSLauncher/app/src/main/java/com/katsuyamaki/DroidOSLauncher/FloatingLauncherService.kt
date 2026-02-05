package com.katsuyamaki.DroidOSLauncher

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.app.Service
import android.view.accessibility.AccessibilityEvent
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

class FloatingLauncherService : AccessibilityService() {

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
                    Log.d(TAG, "Launcher moving to Display: $targetId")
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
    private var currentDisplayId = 0
    private var lastPhysicalDisplayId = Display.DEFAULT_DISPLAY
    // Tracks the currently focused app package for "Active Window" commands
    private var activePackageName: String? = null
    // History for focus restoration (ignoring overlays)
    private var lastValidPackageName: String? = null
    private var secondLastValidPackageName: String? = null

    // === KEYBIND SYSTEM ===
    private var visualQueueView: View? = null
    private var visualQueueParams: WindowManager.LayoutParams? = null
    private var isVisualQueueVisible = false

    // Command State Machine
    private var pendingCommandId: String? = null

// Custom Modifier State
    private val MOD_CUSTOM = -999 // Internal ID for custom modifier
    private var customModKey = 0

private var isSoftKeyboardSupport = false
    private var isCustomModLatched = false
    // No timer runnable needed - latch stays active until consumed by next key press

    // UI Option Wrapper
    data class CustomModConfigOption(val currentKeyCode: Int)
    private var pendingArg1: Int = -1
    private val commandTimeoutRunnable = Runnable { abortCommandMode() }
    private val COMMAND_TIMEOUT_MS = 5000L

    // Key Picker State
    private var keyPickerView: View? = null
    private var keyPickerParams: WindowManager.LayoutParams? = null

    // Data Classes for Configuration
    data class CommandDef(val id: String, val label: String, val argCount: Int, val description: String)
    data class KeybindOption(val def: CommandDef, var modifier: Int, var keyCode: Int)

    val SUPPORTED_KEYS = linkedMapOf(
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
        CommandDef("SWAP", "Swap Slots", 2, "Swap app in Slot A with Slot B")
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
                Log.d(TAG, "onDisplayChanged: Ignoring - targeting virtual display $currentDisplayId")
                return
            }
            // =================================================================================
            // END BLOCK: VIRTUAL DISPLAY PROTECTION
            // =================================================================================

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
                        switchRunnable = Runnable { 
                            try { performDisplayChange(0) } catch(e: Exception) {} 
                        }
                        uiHandler.postDelayed(switchRunnable!!, 500)
                    } 
                    // CASE B: Phone Closed (Display 0 turned OFF/DOZE) -> Move to Cover (1)
                    else if (display.state != Display.STATE_ON && currentDisplayId == 0) {
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
    private val selectedAppsQueue = mutableListOf<MainActivity.AppInfo>()
    private val allAppsList = mutableListOf<MainActivity.AppInfo>()
    private val displayList = mutableListOf<Any>()
    
    private var activeProfileName: String? = null
    private var currentMode = MODE_SEARCH
    
    // === KEYBOARD NAVIGATION STATE ===
    private var selectedListIndex = 0
    
    // UI Focus Areas
    private val FOCUS_SEARCH = 0
    private val FOCUS_QUEUE = 1
    private val FOCUS_LIST = 2
    private var currentFocusArea = FOCUS_SEARCH
    
    // Queue Navigation State
    private var queueSelectedIndex = -1
    private var queueCommandPending: CommandDef? = null
    private var queueCommandSourceIndex = -1
    
    // [FIX] Map to track manual minimize toggles to prevent auto-refresh overwriting them
    private val manualStateOverrides = java.util.concurrent.ConcurrentHashMap<String, Long>()
    // [FULLSCREEN] Track when tiled apps are auto-minimized for a full-screen app
    private var tiledAppsAutoMinimized = false
    // [FULLSCREEN] Cooldown to prevent TYPE_WINDOWS_CHANGED from re-minimizing right after restore
    private var tiledAppsRestoredAt = 0L



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

    private var selectedLayoutType = 2
    private var selectedResolutionIndex = 0
    private var currentDpiSetting = -1
    // [FIX] State tracking to avoid redundant resolution calls/sleeps
    private var lastAppliedResIndex = -1
    private var lastAppliedDpi = -1
    private var currentFontSize = 16f
    
    private var activeCustomRects: List<Rect>? = null
    private var activeCustomLayoutName: String? = null
    
    private var killAppOnExecute = true
    private var autoRestartTrackpad = false // NEW VARIABLE
    private var targetDisplayIndex = 1 
    private var isScreenOffState = false
    private var isInstantMode = true 
    private var showShizukuWarning = true 
    private var useAltScreenOff = false
    
    private var isVirtualDisplayActive = false
    private var currentDrawerHeightPercent = 70
    private var currentDrawerWidthPercent = 90
    private var autoResizeEnabled = true
    private var bottomMarginPercent = 0
    private var autoAdjustMarginForIME = false
    private var imeMarginOverrideActive = false
    private var droidOsImeDetected = false // Set true when we receive IME_VISIBILITY from DroidOS IME
    private var imeRetileCooldownUntil = 0L
    private var lastAppliedEffectiveMargin = -1
    private var pendingImeRetileRunnable: Runnable? = null




    private fun effectiveBottomMarginPercent(): Int {
        return if (autoAdjustMarginForIME && !imeMarginOverrideActive) 0 else bottomMarginPercent
    }






    private var topMarginPercent = 0
    
    private var reorderSelectionIndex = -1
    private var isReorderDragEnabled = true
    private var isReorderTapEnabled = true
    
    private val PACKAGE_BLANK = "internal.blank.spacer"
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
} else if (action == ACTION_TOGGLE_VIRTUAL) {
                toggleVirtualDisplay()
            } else if (action == "KEEP_SCREEN_ON" || action == "${packageName}.KEEP_SCREEN_ON") {
                val enable = intent?.getBooleanExtra("ENABLE", true) ?: true
                setKeepScreenOn(enable)
                safeToast(if (enable) "Screen: Always On" else "Screen: Normal Timeout")
            } else if (action == "com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER") {
                handleWindowManagerCommand(intent)
} else if (action == "com.katsuyamaki.DroidOSLauncher.REQUEST_CUSTOM_MOD_SYNC") {
                // Trackpad is asking for the key, send it
                if (customModKey != 0) {
                    sendCustomModToTrackpad()
                    Log.d(TAG, "Synced Custom Mod ($customModKey) to Trackpad")
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
                    Log.d(TAG, "DroidOS IME detected via IME_VISIBILITY broadcast")
                }
                if (autoAdjustMarginForIME) {
                    val visible = intent?.getBooleanExtra("VISIBLE", false) ?: false
                    val isTiled = intent?.getBooleanExtra("IS_TILED", true) ?: true
                    val forceRetile = intent?.getBooleanExtra("FORCE_RETILE", false) ?: false
                    imeMarginOverrideActive = visible
                    val newEffective = effectiveBottomMarginPercent()
                    // [FIX] Always process for tiled apps, force retile for fullscreen apps, 
                    // and honor FORCE_RETILE flag for manual hide to ensure apps resize
                    val shouldRetile = (newEffective != lastAppliedEffectiveMargin) || (!isTiled && visible) || forceRetile
                    if (shouldRetile) {
                        val now = System.currentTimeMillis()
                        // Cancel any stale deferred retile
                        pendingImeRetileRunnable?.let { uiHandler.removeCallbacks(it) }
                        pendingImeRetileRunnable = null
                        if (now >= imeRetileCooldownUntil) {
                            lastAppliedEffectiveMargin = newEffective
                            imeRetileCooldownUntil = now + 500
                            setupVisualQueue()
                            // [FIX] For fullscreen apps, use longer delay to let Android handle insets first
                            if (!isTiled) {
                                uiHandler.postDelayed({ retileExistingWindows() }, 100)
                            } else {
                                retileExistingWindows()
                            }
                        } else {
                            Log.d(TAG, "IME_VISIBILITY ($visible) deferred (cooldown)")
                            val runnable = Runnable {
                                val eff = effectiveBottomMarginPercent()
                                if (eff != lastAppliedEffectiveMargin) {
                                    lastAppliedEffectiveMargin = eff
                                    imeRetileCooldownUntil = System.currentTimeMillis() + 500
                                    setupVisualQueue()
                                    retileExistingWindows()
                                }
                                pendingImeRetileRunnable = null
                            }
                            pendingImeRetileRunnable = runnable
                            uiHandler.postDelayed(runnable, imeRetileCooldownUntil - now + 50)
                        }
                    }
                }
            } else if (action == "com.katsuyamaki.DroidOSLauncher.SET_AUTO_ADJUST_MARGIN") {
                val enabled = intent?.getBooleanExtra("ENABLED", false) ?: false
                autoAdjustMarginForIME = enabled
                AppPreferences.setAutoAdjustMarginForIME(this@FloatingLauncherService, enabled)
                if (!enabled) imeMarginOverrideActive = false
            } else if (action == "com.katsuyamaki.DroidOSLauncher.SET_MARGIN_BOTTOM") {
                val percent = intent?.getIntExtra("PERCENT", 0) ?: 0
                bottomMarginPercent = percent
                AppPreferences.setBottomMarginPercent(this@FloatingLauncherService, currentDisplayId, percent)
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


    private fun broadcastKeybindsToKeyboard() {
        val binds = ArrayList<String>()
        // Iterate AVAILABLE_COMMANDS to ensure defaults are included
        for (cmd in AVAILABLE_COMMANDS) {
            val bind = AppPreferences.getKeybind(this, cmd.id)
            if (bind.second != 0) { // Valid keyCode
                // Format: "modifier|keyCode"
                binds.add("${bind.first}|${bind.second}")
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
                    removeFromFocusHistory(app.packageName) // Clean up history
                    Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start()
                    safeToast("Killed ${app.label}") 
                }
                selectedAppsQueue.removeAt(pos)
                if (reorderSelectionIndex != -1) endReorderMode(false)
                updateAllUIs()
                if (isInstantMode) applyLayoutImmediate() 
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
                Log.d(TAG, "Saving Target Display ID: $targetId")
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
                    Log.d(TAG, "Service Disabled")
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
                
                Log.d(TAG, "Enabling Service...")
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

Log.d(TAG, "SoftKey: Typed '$typedChar' -> Code $typedCode. CustomMod: $customModKey. Latched: $isCustomModLatched")
        
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
        Log.d("DroidOS_Keys", "INPUT: Key=$keyName($keyCode) Meta=$metaStr($metaState)")

        // 1. INPUT MODE (Entering Numbers)
        if (pendingCommandId != null) {
            if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                abortCommandMode()
                return true
            }

            // Handle Numbers (Row 0-9 and Numpad 0-9)
            val num = keyEventToNumber(keyCode)
            if (num != -1) {
                handleCommandInput(num)
                return true
            }

            // NEW: If any non-number key is pressed while HUD is open, CANCEL IT.
            // This prevents the HUD from getting stuck if the user changes their mind
            // and starts typing a sentence.
            abortCommandMode()
            return true // Consume the "cancel" key so it doesn't type into app
        }

        // 2. TRIGGER MODE (Detecting Hotkeys)
        // Check if this key matches any stored bind
        var commandTriggered = false

        for (cmd in AVAILABLE_COMMANDS) {
            val bind = AppPreferences.getKeybind(this, cmd.id)

            // Check if key code matches
            if (bind.second != 0 && bind.second == keyCode) {
                Log.d("DroidOS_Keys", " -> Key Match for '${cmd.label}'. Checking modifiers...")
                Log.d("DroidOS_Keys", "    Required: ${bind.first} | Current: $metaState")

                if (checkModifiers(metaState, bind.first)) {
                    Log.d("DroidOS_Keys", "    MATCH! Triggering...")
                    triggerCommand(cmd)
                    commandTriggered = true
                    break
                } else {
                    Log.d("DroidOS_Keys", "    Modifier Mismatch.")
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
        Log.d("DroidOS_Keys", "RECEIVER: handleRemoteKeyEvent called with $keyCode (Meta: $metaState)")

        // CHECK CUSTOM MODIFIER (Remote/Broadcast)
        if (customModKey != 0) {
            Log.d("DroidOS_Keys", "Checking Custom Mod: Recv($keyCode) vs Saved($customModKey)")

            if (keyCode == customModKey) {
                isCustomModLatched = true
                // No timer - stays latched until next key press
                safeToast("Custom Mod Active (Remote)")
                Log.d("DroidOS_Keys", "Custom Mod LATCHED via Broadcast")
                return
            }
        }

        val metaStr = if (metaState != 0) "Meta($metaState)" else "None"
        Log.d("DroidOS_Keys", "REMOTE INPUT: Key=$keyName($keyCode) Meta=$metaStr($metaState)")

        // 1. INPUT MODE (Entering Numbers for Visual Queue)
        if (pendingCommandId != null) {
            if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                abortCommandMode()
                return
            }
            val num = keyEventToNumber(keyCode)
            if (num != -1) {
                handleCommandInput(num)
                return
            }

            // NEW: If any non-number key is pressed while HUD is open, CANCEL IT.
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
                    Log.d("DroidOS_Keys", "    MATCH! Triggering via Remote...")
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

    private fun checkModifiers(currentMeta: Int, requiredMeta: Int): Boolean {
        if (requiredMeta == 0) return true
        
        // Custom Modifier Logic
        if (requiredMeta == MOD_CUSTOM) {
            // Log.d(TAG, "Checking CSTM: latched=$isCustomModLatched")
            return isCustomModLatched
        }
        
        // Standard Android Meta Flags
        return (currentMeta and requiredMeta) != 0
    }

    private fun keyEventToNumber(code: Int): Int {
        return when (code) {
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> code - KeyEvent.KEYCODE_0
            in KeyEvent.KEYCODE_NUMPAD_0..KeyEvent.KEYCODE_NUMPAD_9 -> code - KeyEvent.KEYCODE_NUMPAD_0
            else -> -1
        }
    }

    private fun triggerCommand(cmd: CommandDef) {
        if (cmd.argCount == 0) {
            // Immediate
            Log.d("DroidOS_Keys", "Executing Immediate Command: ${cmd.id}")
            val intent = Intent().putExtra("COMMAND", cmd.id)
            handleWindowManagerCommand(intent)
            safeToast("Executed: ${cmd.label}")
        } else {
            // Enter Input Mode
            pendingCommandId = cmd.id
            pendingArg1 = -1
            showVisualQueue("${cmd.label}: Enter Slot #")
        }
    }

    private fun handleCommandInput(number: Int) {
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
                handleWindowManagerCommand(intent)
                hideVisualQueue()
                pendingCommandId = null
            } else {
                // Need Second Arg
                showVisualQueue("${cmd.label}: Swap with?", slotIndex)
            }
        } else {
            // Second Arg Received (only supported for 2-arg commands like SWAP)
            val intent = Intent()
                .putExtra("COMMAND", cmd.id)
                .putExtra("INDEX_A", pendingArg1 + 1) // Convert back to 1-based for handler
                .putExtra("INDEX_B", number)

            handleWindowManagerCommand(intent)
            hideVisualQueue()
            pendingCommandId = null
        }
    }

    // AccessibilityService required overrides
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

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

                    // Update history if package changed
                    if (activePackageName != detectedPkg) {
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
                            detectedPkg.contains("permissioncontroller")
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
                        // WARNING: DOUBLE-MARGIN BUG - SEE processWindowManagerCommand() FOR FULL EXPLANATION
                        // ===================================================================================
                        // This broadcast is SUPPLEMENTARY. The primary fix is in processWindowManagerCommand()
                        // where we write launcher_has_managed_apps to SharedPreferences.
                        //
                        // DO NOT change shouldSuppressInsets logic without testing:
                        // 1. Open 2 tiled apps (top/bottom layout)
                        // 2. Tap text field to show IME
                        // 3. Verify NO blank gap between bottom app and keyboard
                        // 4. Hide/show IME multiple times rapidly
                        // 5. Switch focus between the two apps while IME is visible
                        // ===================================================================================
                        val shouldSuppressInsets = (autoAdjustMarginForIME && activeNonMinimized.isNotEmpty()) || isManagedApp
                        if (!isSystemOverlay) {
                            sendBroadcast(Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.TILED_STATE")
                                .setPackage("com.katsuyamaki.DroidOSTrackpadKeyboard")
                                .putExtra("TILED_ACTIVE", shouldSuppressInsets))
                        }
                        if (!isSystemOverlay && !isTiledApp && selectedAppsQueue.any { !it.isMinimized } && !tiledAppsAutoMinimized &&
                            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                            // Full-screen app opened — minimize all tiled windows
                            // But first verify it actually covers the screen (skip small freeform/popup windows)
                            var coversScreen = false
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
                                    if (windowPkg == detectedPkg) {
                                        window.getBoundsInScreen(boundsRect)
                                        val windowArea = boundsRect.width().toLong() * boundsRect.height().toLong()
                                        if (windowArea >= screenArea * 85 / 100) {
                                            coversScreen = true
                                        }
                                        break
                                    }
                                }
                            } catch (e: Exception) { coversScreen = true } // fallback to old behavior on error

                            if (coversScreen) {
                            tiledAppsAutoMinimized = true

                            Thread {
                                try {
                                    for (app in selectedAppsQueue) {
                                        if (!app.isMinimized) {
                                            val tid = shellService?.getTaskId(app.getBasePackage(), null) ?: -1
                                            if (tid != -1) shellService?.moveTaskToBack(tid)
                                        }
                                    }
                                } catch (e: Exception) { Log.e(TAG, "Auto-minimize failed", e) }
                            }.start()
                            Log.d(TAG, "FULLSCREEN: Auto-minimized tiled apps for $detectedPkg")
                            } // end if (coversScreen)
                        } else if (isTiledApp && tiledAppsAutoMinimized) {
                            // Returning to a tiled app — restore all
                            tiledAppsAutoMinimized = false
                            tiledAppsRestoredAt = System.currentTimeMillis()
                            retileExistingWindows()
                            Log.d(TAG, "FULLSCREEN: Auto-restored tiled apps for $detectedPkg")
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
                            Log.d(TAG, "FULLSCREEN: Tiled app $windowPkg maximized (${boundsRect.width()}x${boundsRect.height()} vs ${dm.widthPixels}x${dm.heightPixels})")
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
                                Log.d(TAG, "FULLSCREEN: External app $windowPkg maximized (${boundsRect.width()}x${boundsRect.height()} vs ${dm.widthPixels}x${dm.heightPixels})")
                                tiledAppsAutoMinimized = true
                                activePackageName = windowPkg
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

                activePackageName = pkg
            }
        }

        // 2. SOFT KEYBOARD TRIGGER SUPPORT (Toggleable)
        if (isSoftKeyboardSupport && event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            handleSoftKeyInput(event)
        }
    }
    override fun onInterrupt() {}

    // AccessibilityService entry point - called when user enables service in Settings
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility Service Connected")

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


        }


        if (Build.VERSION.SDK_INT >= 33) registerReceiver(commandReceiver, filter, Context.RECEIVER_EXPORTED) else registerReceiver(commandReceiver, filter)


        // Shizuku setup
        try { Shizuku.addBinderReceivedListener(shizukuBinderListener); Shizuku.addRequestPermissionResultListener(shizukuPermissionListener) } catch (e: Exception) {}
        try { if (rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() } catch (e: Exception) {}

        // Load preferences
        loadInstalledApps(); currentFontSize = AppPreferences.getFontSize(this)
        killAppOnExecute = AppPreferences.getKillOnExecute(this); targetDisplayIndex = AppPreferences.getTargetDisplayIndex(this)
        autoRestartTrackpad = AppPreferences.getAutoRestartTrackpad(this) // NEW LOAD
        isInstantMode = AppPreferences.getInstantMode(this); showShizukuWarning = AppPreferences.getShowShizukuWarning(this)
        useAltScreenOff = AppPreferences.getUseAltScreenOff(this); isReorderDragEnabled = AppPreferences.getReorderDrag(this)
        isReorderTapEnabled = AppPreferences.getReorderTap(this); currentDrawerHeightPercent = AppPreferences.getDrawerHeightPercent(this)
        currentDrawerWidthPercent = AppPreferences.getDrawerWidthPercent(this); autoResizeEnabled = AppPreferences.getAutoResizeKeyboard(this)
        autoAdjustMarginForIME = AppPreferences.getAutoAdjustMarginForIME(this)
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

    private fun sendCustomModToTrackpad() {
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

        Log.d(TAG, "onStartCommand: Target Display $targetDisplayId (Current: $currentDisplayId)")

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
        // 1. Margins (Per Display)
        topMarginPercent = AppPreferences.getTopMarginPercent(this, displayId)
        bottomMarginPercent = AppPreferences.getBottomMarginPercent(this, displayId)




        
        // 2. Layout & Custom Rects (Per Display)
        selectedLayoutType = AppPreferences.getLastLayout(this, displayId)
        activeCustomLayoutName = AppPreferences.getLastCustomLayoutName(this, displayId)
        
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

                Log.d(TAG, "Initialized DPI from System: $currentDpiSetting")
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

        if (isBound) { try { ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection); isBound = false } catch (e: Exception) {} }
    setKeepScreenOn(false)
    wakeLock = null
    }
    
    // === SAFE TOAST FUNCTION - START ===
    // Displays toast message and updates debug status view
    private fun safeToast(msg: String) { 
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
            Log.d(DEBUG_TAG, "[$action] FULL: pkg=$pkg cls=$className")
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
        
        bubbleParams.gravity = Gravity.TOP or Gravity.START; bubbleParams.x = 50; bubbleParams.y = centerY
        
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

                        if (!isDrag) {
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
    }
    
    private fun updateBubbleIcon() { val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon) ?: return; if (!isBound && showShizukuWarning) { uiHandler.post { iconView.setImageResource(android.R.drawable.ic_dialog_alert); iconView.setColorFilter(Color.RED); iconView.imageTintList = null }; return }; uiHandler.post { try { val uriStr = AppPreferences.getIconUri(this); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); if (bitmap != null) { iconView.setImageBitmap(bitmap); iconView.imageTintList = null; iconView.clearColorFilter() } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } catch (e: Exception) { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } }
    private fun dismissKeyboardAndRestore() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); if (searchBar != null && searchBar.hasFocus()) { searchBar.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(searchBar.windowToken, 0) }; val dpiInput = drawerView?.findViewById<EditText>(R.id.input_dpi_value); if (dpiInput != null && dpiInput.hasFocus()) { dpiInput.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(dpiInput.windowToken, 0) }; updateDrawerHeight(false) }

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
            debugStatusView?.textSize = 10f
            debugStatusView?.gravity = Gravity.CENTER
            
            // [FIX] Hide Debug View by Default
            debugStatusView?.visibility = View.GONE
            
            container.addView(debugStatusView, 0)
        }

        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val mainRecycler = drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view); val selectedRecycler = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        if (isBound) executeBtn.setColorFilter(Color.GREEN) else executeBtn.setColorFilter(Color.RED)
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
        executeBtn.setOnClickListener { executeLaunch(selectedLayoutType, closeDrawer = true) }
        searchBar.addTextChangedListener(object : TextWatcher { override fun afterTextChanged(s: Editable?) { filterList(s.toString()) }; override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}; override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {} })
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
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    dismissKeyboardAndRestore()
                    drawerView?.requestFocus()
                    
                    if (selectedAppsQueue.isNotEmpty()) {
                        // Go to Queue
                        currentFocusArea = FOCUS_QUEUE
                        queueSelectedIndex = 0
                        selectedRecycler.adapter?.notifyDataSetChanged()
                        debugStatusView?.visibility = View.VISIBLE
                        debugStatusView?.text = "Queue Navigation: Use Arrows / Hotkeys"
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
        mainRecycler.layoutManager = LinearLayoutManager(themeContext); mainRecycler.adapter = RofiAdapter(); val itemTouchHelper = ItemTouchHelper(swipeCallback); itemTouchHelper.attachToRecyclerView(mainRecycler)
        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() { override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) { if (newState == RecyclerView.SCROLL_STATE_DRAGGING) { dismissKeyboardAndRestore() } } })
        mainRecycler.setOnTouchListener { v, event -> if (event.action == MotionEvent.ACTION_DOWN) { dismissKeyboardAndRestore() }; false }
        selectedRecycler.layoutManager = LinearLayoutManager(themeContext, LinearLayoutManager.HORIZONTAL, false); selectedRecycler.adapter = SelectedAppsAdapter(); val dockTouchHelper = ItemTouchHelper(selectedAppsDragCallback); dockTouchHelper.attachToRecyclerView(selectedRecycler)
        drawerView!!.setOnClickListener { toggleDrawer() }
        
        // --- DRAWER ROOT NAVIGATION LOGIC ---
        drawerView!!.isFocusable = true
        drawerView!!.isFocusableInTouchMode = true
        drawerView!!.setOnKeyListener { _, keyCode, event -> 
            if (event.action == KeyEvent.ACTION_DOWN) {
                
                // GLOBAL: ESCAPE (Cancel/Close)
                if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
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

                // GLOBAL: TAB (Switch Modes)
                if (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_ALT_RIGHT) {
                    cycleTab(event.isShiftPressed)
                    return@setOnKeyListener true
                }

                // === QUEUE NAVIGATION MODE ===
                if (currentFocusArea == FOCUS_QUEUE) {
                    
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
                                val intent = Intent().putExtra("COMMAND", cmd.id).putExtra("INDEX", queueSelectedIndex + 1)
                                handleWindowManagerCommand(intent)
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
                            // Exit to Search
                            currentFocusArea = FOCUS_SEARCH
                            queueSelectedIndex = -1
                            selectedRecycler.adapter?.notifyDataSetChanged()
                            debugStatusView?.visibility = View.GONE
                            searchBar.requestFocus()
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            // Exit to List
                            currentFocusArea = FOCUS_LIST
                            queueSelectedIndex = -1
                            selectedRecycler.adapter?.notifyDataSetChanged()
                            debugStatusView?.visibility = View.GONE
                            if (displayList.isNotEmpty()) {
                                if (selectedListIndex >= displayList.size) selectedListIndex = 0
                                mainRecycler.adapter?.notifyItemChanged(selectedListIndex)
                                mainRecycler.scrollToPosition(selectedListIndex)
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_ENTER -> {
                            if (queueCommandPending != null) {
                                // Complete 2-Step Command
                                val intent = Intent()
                                    .putExtra("COMMAND", queueCommandPending!!.id)
                                    .putExtra("INDEX_A", queueCommandSourceIndex + 1)
                                    .putExtra("INDEX_B", queueSelectedIndex + 1)
                                handleWindowManagerCommand(intent)
                                queueCommandPending = null
                                queueCommandSourceIndex = -1
                                debugStatusView?.text = "Command Executed"
                            } else {
                                // DEFAULT ENTER: TOGGLE MINIMIZE (Hide/Unhide)
                                val intent = Intent().putExtra("COMMAND", "TOGGLE_MINIMIZE").putExtra("INDEX", queueSelectedIndex + 1)
                                handleWindowManagerCommand(intent)
                            }
                            return@setOnKeyListener true
                        }
                        KeyEvent.KEYCODE_SPACE -> {
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
                                    selectedRecycler.adapter?.notifyDataSetChanged()
                                    debugStatusView?.visibility = View.VISIBLE
                                    debugStatusView?.text = "Queue Navigation"
                                    
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

    private fun setupVisualQueue() {
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
        if (visualQueueView == null) setupVisualQueue()

        // Ensure list is sorted active-first before showing
        sortAppQueue()

        // FAST SYNC CHECK: Get visible packages for THIS display immediately
        // This ensures the HUD reflects reality even if the background poller hasn't run.
        val visiblePkgs = if (shellService != null) {
            try {
                // Determine if we should block or use cached. 
                // Since this is user-initiated HUD, we want accuracy.
                // We'll spawn a quick thread to get it and update the adapter.
                emptyList<String>() 
            } catch (e: Exception) { emptyList() }
        } else {
            emptyList()
        }
        
        // We will update the adapter with the live list asynchronously to avoid UI freeze
        Thread {
            val visible = shellService?.getVisiblePackages(currentDisplayId) ?: emptyList()
            uiHandler.post {
                val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
                (recycler?.adapter as? VisualQueueAdapter)?.updateVisibility(visible)
            }
        }.start()

        val promptView = visualQueueView?.findViewById<TextView>(R.id.visual_queue_prompt)
        promptView?.text = prompt

        val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
        recycler?.adapter = VisualQueueAdapter(highlightSlot0Based)

        // Force focus to keep keyboard up
        val dummy = visualQueueView?.findViewById<EditText>(R.id.vq_dummy_input)
        dummy?.requestFocus()

        if (!isVisualQueueVisible) {
            try {
                // Use display-specific WM
                val targetWM = displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: windowManager
                targetWM.addView(visualQueueView, visualQueueParams)
                isVisualQueueVisible = true
                Log.d(TAG, "Visual Queue Added to Display $currentDisplayId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add Visual Queue", e)
                e.printStackTrace()
            }
        } else {
            // Just update adapter if already visible
            recycler?.adapter?.notifyDataSetChanged()
        }

        // NO TIMEOUT - Removed postDelayed (stays open until command completes or ESC)
        uiHandler.removeCallbacks(commandTimeoutRunnable)

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

    private fun hideVisualQueue() {
        if (isVisualQueueVisible && visualQueueView != null) {
            try {
                val targetWM = displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: windowManager
                targetWM.removeView(visualQueueView)
                isVisualQueueVisible = false
            } catch (e: Exception) {}
        }
        uiHandler.removeCallbacks(commandTimeoutRunnable)

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

// [NEW] Robust Move Logic: Finds original slot and forces move with retries
    private fun forceAppToDisplay(pkg: String, targetDisplayId: Int) {
        // [LOCK] Prevent duplicate threads
        if (activeEnforcements.contains(pkg)) {
            Log.d("DROIDOS_WATCHDOG", "SKIP: Enforcement already active for $pkg")
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
                            Log.d("DROIDOS_WATCHDOG", "Attempt $i: D$targetDisplayId @ $bounds")
                        } else {
                            shellService?.runCommand("am task resize $tid 0 0 1000 1000")
                            Log.d("DROIDOS_WATCHDOG", "Attempt $i: D$targetDisplayId (Default)")
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
    private fun showKeyPicker(cmdId: String, currentMod: Int) {
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
    inner class VisualQueueAdapter(private val highlightIndex: Int) : RecyclerView.Adapter<VisualQueueAdapter.Holder>() {
        
        private var visiblePackages: List<String> = emptyList()
        
        fun updateVisibility(visible: List<String>) {
            visiblePackages = visible
            notifyDataSetChanged()
        }
        inner class Holder(v: View) : RecyclerView.ViewHolder(v) {
            val icon: ImageView = v.findViewById(R.id.vq_app_icon)
            val badge: TextView = v.findViewById(R.id.vq_slot_number)
            val highlight: View = v.findViewById(R.id.vq_highlight)
            val underline: View = v.findViewById(R.id.focus_underline)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_visual_queue_app, parent, false))
        }
        override fun onBindViewHolder(holder: Holder, position: Int) {
            val app = selectedAppsQueue[position]
            val slotNum = position + 1 // 1-Based Display

            // Show Focus Underline if matches activePackageName
            // Handle Gemini/Google alias logic
            val isFocused = (app.packageName == activePackageName) ||
                            (app.packageName == "com.google.android.apps.bard" && activePackageName == "com.google.android.googlequicksearchbox")

            holder.underline.visibility = if (isFocused) View.VISIBLE else View.GONE
            holder.badge.text = slotNum.toString()

            if (app.packageName == PACKAGE_BLANK) {
                holder.icon.setImageResource(R.drawable.ic_box_outline)
                holder.icon.alpha = 0.5f
            } else {
                try {
                    val basePkg = if (app.packageName.contains(":")) app.packageName.substringBefore(":") else app.packageName
                    holder.icon.setImageDrawable(packageManager.getApplicationIcon(basePkg))
                } catch (e: Exception) {
                    holder.icon.setImageResource(R.drawable.ic_launcher_bubble)
                }
                
                // VISIBILITY LOGIC:
                // 1. Is it explicitly minimized by user? -> Inactive
                // 2. Is it visible on THIS screen? -> Active
                // 3. Otherwise -> Inactive (e.g. open on other screen)
                
                val isVisibleOnScreen = visiblePackages.contains(app.getBasePackage()) || 
                                      (app.getBasePackage() == "com.google.android.apps.bard" && visiblePackages.contains("com.google.android.googlequicksearchbox"))

                // If visible packages list is empty (loading), fall back to stored state
                val isActuallyActive = if (visiblePackages.isNotEmpty()) {
                    isVisibleOnScreen
                } else {
                    !app.isMinimized
                }
                
                holder.icon.alpha = if (isActuallyActive) 1.0f else 0.4f
            }

            // Highlight logic
            if (position == highlightIndex) {
                holder.highlight.visibility = View.VISIBLE
                holder.highlight.setBackgroundResource(R.drawable.bg_item_active)
            } else {
                holder.highlight.visibility = View.GONE
            }
        }
        override fun getItemCount() = selectedAppsQueue.size
    }

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
    
    private fun startReorderMode(index: Int) { if (!isReorderTapEnabled) return; if (index < 0 || index >= selectedAppsQueue.size) return; val prevIndex = reorderSelectionIndex; reorderSelectionIndex = index; val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; if (prevIndex != -1) adapter?.notifyItemChanged(prevIndex); adapter?.notifyItemChanged(reorderSelectionIndex); safeToast("Tap another app to Swap") }
    private fun swapReorderItem(targetIndex: Int) { if (reorderSelectionIndex == -1) return; Collections.swap(selectedAppsQueue, reorderSelectionIndex, targetIndex); val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; adapter?.notifyItemChanged(reorderSelectionIndex); adapter?.notifyItemChanged(targetIndex); endReorderMode(true) }
    private fun endReorderMode(triggerInstantMode: Boolean) { val prevIndex = reorderSelectionIndex; reorderSelectionIndex = -1; val adapter = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler).adapter; if (prevIndex != -1) adapter?.notifyItemChanged(prevIndex); if (triggerInstantMode && isInstantMode) applyLayoutImmediate() }
    
    private fun updateDrawerHeight(isKeyboardMode: Boolean) {
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
        Log.d(TAG, "toggleDrawer called. isExpanded=$isExpanded")
        if (isExpanded) {
            Log.d(TAG, "Closing drawer")
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
            Log.d(TAG, "Opening drawer")
            updateDrawerHeight(false)
            
            // Z-ORDER UPDATE: Try adding with High Priority, Fallback if fails
            try {
                Log.d(TAG, "Attempting to add drawer view")
                windowManager.addView(drawerView, drawerParams)
                Log.d(TAG, "Drawer view added successfully")
            } catch(e: Exception) {
                Log.e(TAG, "Failed to add drawer with high priority: ${e.message}")
                try {
                    drawerParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    windowManager.addView(drawerView, drawerParams)
                    Log.d(TAG, "Drawer added with fallback type")
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
            debugStatusView?.visibility = View.GONE
            
            switchMode(MODE_SEARCH)
            
            val et = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)
            et?.setText("")
            et?.requestFocus() // Auto-focus for immediate typing
            et?.post {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
            }
            updateSelectedAppsDock()
            
            // Show current queue state in debug view when drawer opens
            showQueueDebugState()
            
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
                Log.d(DEBUG_TAG, "Queue[$i]: ${app.label} pkg=${app.packageName} cls=${app.className}")
            }
        }
    }
    // === SHOW QUEUE DEBUG STATE - END ===
    private fun updateGlobalFontSize() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); searchBar?.textSize = currentFontSize; drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() }

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
            
            Log.d(DEBUG_TAG, "Loaded: $label pkg=$pkg cls=$validClassName")
        }
        allAppsList.sortBy { it.label.lowercase() }
        Log.d(TAG, "Loaded ${allAppsList.size} apps total")
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
    private fun getLayoutName(type: Int): String { 
        // Check for user-renamed default
        if (type != LAYOUT_CUSTOM_DYNAMIC) {
            val custom = AppPreferences.getDefaultLayoutName(this, type)
            // [FIX] Auto-Repair: Ignore names that look like Settings toggles
            if (custom != null && !custom.contains("Virtual Display") && !custom.contains("Switch Display")) {
                return custom
            }
        }
        
        return when(type) { 
            LAYOUT_FULL -> "1 App - Full"
            LAYOUT_SIDE_BY_SIDE -> "2 Apps - Split"
            LAYOUT_TOP_BOTTOM -> "2 Apps - Top/Bot"
            LAYOUT_TRI_EVEN -> "3 Apps - Even"
            LAYOUT_CORNERS -> "4 Apps - Quadrant"
            LAYOUT_TRI_SIDE_MAIN_SIDE -> "3 Apps - Side/Main/Side"
            LAYOUT_QUAD_ROW_EVEN -> "4 Apps - Row"
            LAYOUT_QUAD_TALL_SHORT -> "4 Apps - 2 Tall / 2 Short"
            LAYOUT_HEX_TALL_SHORT -> "6 Apps - 3 Tall / 3 Short"
            LAYOUT_CUSTOM_DYNAMIC -> "Custom"
            else -> "Unknown" 
        } 
    }
    private fun getRatioName(index: Int): String { return when(index) { 1 -> "1:1"; 2 -> "16:9"; 3 -> "32:9"; else -> "Default" } }
    private fun getTargetDimensions(index: Int): Pair<Int, Int>? { return when(index) { 1 -> 1422 to 1500; 2 -> 1920 to 1080; 3 -> 3840 to 1080; else -> null } }
    private fun getResolutionCommand(index: Int): String { return when(index) { 1 -> "wm size 1422x1500 -d $currentDisplayId"; 2 -> "wm size 1920x1080 -d $currentDisplayId"; 3 -> "wm size 3840x1080 -d $currentDisplayId"; else -> "wm size reset -d $currentDisplayId" } }

// Sorts active apps to front and minimized to back.
            // Maintains relative order (Stable Sort), ensuring newly minimized apps
            // appear at the front of the inactive group (Left-to-Right).
            private fun sortAppQueue() {
                selectedAppsQueue.sortWith(compareBy { it.isMinimized })
            }

            private fun updateAllUIs() {
                // 1. Update Drawer Dock
                updateSelectedAppsDock()
                drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
                
                // 2. Update Visual Queue HUD (if exists)
                if (visualQueueView != null) {
                    val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
                    recycler?.adapter?.notifyDataSetChanged()
                }
            }    private fun updateSelectedAppsDock() { val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); if (selectedAppsQueue.isEmpty()) { dock.visibility = View.GONE } else { dock.visibility = View.VISIBLE; dock.adapter?.notifyDataSetChanged(); dock.scrollToPosition(selectedAppsQueue.size - 1) } }
    private fun refreshSearchList() { val query = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString() ?: ""; filterList(query) }
    private fun filterList(query: String) {
        if (currentMode != MODE_SEARCH) return; val actualQuery = query.substringAfterLast(",").trim(); displayList.clear()
        val filtered = if (actualQuery.isEmpty()) { allAppsList } else { allAppsList.filter { it.label.contains(actualQuery, ignoreCase = true) } }
        val sorted = filtered.sortedWith(compareBy<MainActivity.AppInfo> { it.packageName != PACKAGE_BLANK }.thenByDescending { it.isFavorite }.thenBy { it.label.lowercase() }); displayList.addAll(sorted); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
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

    // === ADD TO SELECTION - START ===
    // Adds app to the selection queue, handles removal if already selected
    // Uses proper package name extraction for force-stop and launch operations
    private fun addToSelection(app: MainActivity.AppInfo) {
        dismissKeyboardAndRestore()
        val et = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar)
        
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

    private fun toggleFavorite(app: MainActivity.AppInfo) { val newState = AppPreferences.toggleFavorite(this, app.packageName); app.isFavorite = newState; allAppsList.find { it.packageName == app.packageName }?.isFavorite = newState }


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
                Log.d(TAG, "launchViaApi: explicit component $basePkg/$className")
            } else {
                intent = packageManager.getLaunchIntentForPackage(basePkg)
                Log.d(TAG, "launchViaApi: default intent for $basePkg")
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
                Log.d(TAG, "launchViaApi: bounds=$bounds")
            }

            startActivity(intent, options.toBundle())
            Log.d(TAG, "launchViaApi: SUCCESS $basePkg")

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

            Log.d(TAG, "launchViaShell: $cmd")

            Thread {
                try {
                    shellService?.runCommand(cmd)
                    Log.d(TAG, "launchViaShell: SUCCESS")
                } catch (e: Exception) {
                    Log.e(TAG, "launchViaShell: FAILED", e)
                }
            }.start()

        } catch (e: Exception) {
            Log.e(TAG, "launchViaShell FAILED: $pkg", e)
        }
    }
    // === LAUNCH VIA SHELL - END ===

    
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
            try { windowManager.removeView(visualQueueView) } catch(e: Exception){}
            visualQueueView = null
            isVisualQueueVisible = false
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

    private fun applyLayoutImmediate(focusPackage: String? = null) { executeLaunch(selectedLayoutType, closeDrawer = false, focusPackage = focusPackage) }

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



    // === RESTORE QUEUE IMMEDIATE - START ===
    // Loads the saved queue from preferences immediately without checking shell/running state.
    // Ensures Visual Queue is populated even if the Drawer hasn't been opened yet.
    private fun restoreQueueFromPrefs() {
        val lastQueue = AppPreferences.getLastQueue(this)
        selectedAppsQueue.clear()
        
        for (identifier in lastQueue) {
            if (identifier == PACKAGE_BLANK) {
                selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null))
            } else {
                val appInfo = findAppByIdentifier(identifier)
                if (appInfo != null) {
                    selectedAppsQueue.add(appInfo)
                }
            }
        }
        
        // Preserve sorting (Active -> Minimized)
        sortAppQueue()
        updateAllUIs()
        Log.d(TAG, "Restored ${selectedAppsQueue.size} apps from prefs")
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

                // Log.d(DEBUG_TAG, "fetchRunningApps: visible=${visiblePackages.joinToString()}")
                // Log.d(DEBUG_TAG, "fetchRunningApps: lastQueue=${lastQueue.joinToString()}")

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
                                        Log.d(DEBUG_TAG, "fetchRunningApps: Ignoring system visibility for ${appInfo.label} (Manual Override Active)")
                                    }

                                    selectedAppsQueue.add(appInfo)
                                    Log.d(DEBUG_TAG, "fetchRunningApps: Restored ${appInfo.label} minimized=${appInfo.isMinimized}")
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
                                Log.d(DEBUG_TAG, "fetchRunningApps: Added new visible ${appInfo.label}")
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


    private fun selectLayout(opt: LayoutOption) { 
        dismissKeyboardAndRestore()
        selectedLayoutType = opt.type
        activeCustomRects = opt.customRects
        
        if (opt.type == LAYOUT_CUSTOM_DYNAMIC) { 
            activeCustomLayoutName = opt.name
            AppPreferences.saveLastCustomLayoutName(this, opt.name, currentDisplayId)
        } else { 
            activeCustomLayoutName = null
            AppPreferences.saveLastCustomLayoutName(this, null, currentDisplayId)
        }
        
        AppPreferences.saveLastLayout(this, opt.type, currentDisplayId)
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
    private fun applyRefreshRate(targetRate: Float) {
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
                Log.d(TAG, "=== REFRESH RATE DEBUG ===")
                Log.d(TAG, "Target rate: $targetRate Hz")
                Log.d(TAG, "Display $currentDisplayId has ${modes.size} mode(s):")
                modes.forEach { mode ->
                    Log.d(TAG, "  Mode ${mode.modeId}: ${mode.physicalWidth}x${mode.physicalHeight} @ ${mode.refreshRate}Hz")
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
                    Log.d(TAG, "Hardware supports ${targetRate}Hz (Mode $bestModeId)")
                    
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
                    Log.d(TAG, "Applied HW mode: $forceCmd")
                    
                    activeRefreshRateLabel = "${rateInt}Hz"
                    
                } else {
                    // === METHOD B: No hardware support - use software throttling ===
                    Log.w(TAG, "Hardware does NOT support ${targetRate}Hz - using software throttling")
                    
                    // Method B1: SurfaceFlinger frame rate override for the display
                    // This tells SF to throttle frame composition for this display
                    val sfThrottleCmd = "service call SurfaceFlinger 1035 i32 $currentDisplayId f $targetRate"
                    val sfResult = shellService?.runCommand(sfThrottleCmd)
                    Log.d(TAG, "SF throttle result: $sfResult")
                    
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
                    Log.d(TAG, "Mode specs result: $specResult")
                    
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
                Log.d(TAG, "Post-apply rate: $actualRate Hz")
                
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




    private fun applyResolution(opt: ResolutionOption) { dismissKeyboardAndRestore(); if (opt.index != -1) { selectedResolutionIndex = opt.index; AppPreferences.saveDisplayResolution(this, currentDisplayId, opt.index) }; drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode && opt.index != -1) { Thread { val resCmd = getResolutionCommand(selectedResolutionIndex); shellService?.runCommand(resCmd); Thread.sleep(1500); uiHandler.post { applyLayoutImmediate() } }.start() } }
    private fun selectDpi(value: Int) { currentDpiSetting = if (value == -1) -1 else value.coerceIn(50, 600); AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); Thread { try { if (currentDpiSetting == -1) { shellService?.runCommand("wm density reset -d $currentDisplayId") } else { val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; shellService?.runCommand(dpiCmd) } } catch(e: Exception) { e.printStackTrace() } }.start() }
    private fun changeFontSize(newSize: Float) { currentFontSize = newSize.coerceIn(10f, 30f); AppPreferences.saveFontSize(this, currentFontSize); updateGlobalFontSize(); if (currentMode == MODE_SETTINGS) { switchMode(MODE_SETTINGS) } }
    private fun changeDrawerHeight(delta: Int) { currentDrawerHeightPercent = (currentDrawerHeightPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerHeightPercent(this, currentDrawerHeightPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun changeDrawerWidth(delta: Int) { currentDrawerWidthPercent = (currentDrawerWidthPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerWidthPercent(this, currentDrawerWidthPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun pickIcon() { toggleDrawer(); try { refreshDisplayId(); val intent = Intent(this, IconPickerActivity::class.java); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); val metrics = windowManager.maximumWindowMetrics; val w = 1000; val h = (metrics.bounds.height() * 0.7).toInt(); val x = (metrics.bounds.width() - w) / 2; val y = (metrics.bounds.height() - h) / 2; val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(Rect(x, y, x+w, y+h)); startActivity(intent, options.toBundle()) } catch (e: Exception) { safeToast("Error launching picker: ${e.message}") } }
    private fun saveProfile() { var name = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString()?.trim(); if (name.isNullOrEmpty()) { val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()); name = "Profile_$timestamp" }; val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveProfile(this, name, selectedLayoutType, selectedResolutionIndex, currentDpiSetting, pkgs); safeToast("Saved: $name"); drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText(""); switchMode(MODE_PROFILES) }
    private fun loadProfile(name: String) { 
        val data = AppPreferences.getProfileData(this, name) ?: return
        try { 
            val parts = data.split("|")
            selectedLayoutType = parts[0].toInt()
            selectedResolutionIndex = parts[1].toInt()
            currentDpiSetting = parts[2].toInt()
            val pkgList = parts[3].split(",")
            selectedAppsQueue.clear()
            for (pkg in pkgList) { 
                if (pkg.isNotEmpty()) { 
                    if (pkg == PACKAGE_BLANK) { 
                        selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null)) 
                    } else { 
                        val app = allAppsList.find { it.packageName == pkg }
                        if (app != null) selectedAppsQueue.add(app) 
                    } 
                } 
            }
            // Save settings for CURRENT DISPLAY
            AppPreferences.saveLastLayout(this, selectedLayoutType, currentDisplayId)
            // Note: Profile loading doesn't inherently set a specific CUSTOM layout name unless we inferred it,
            // so we might want to clear the custom layout name to avoid mismatches, or keep as is.
            // For safety, let's clear custom layout name to prevent stale rects if profile used standard layout.
            if (selectedLayoutType != LAYOUT_CUSTOM_DYNAMIC) {
                activeCustomLayoutName = null
                AppPreferences.saveLastCustomLayoutName(this, null, currentDisplayId)
            }
            
            AppPreferences.saveDisplayResolution(this, currentDisplayId, selectedResolutionIndex)
            AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting)
            
            activeProfileName = name
            updateSelectedAppsDock()
            safeToast("Loaded: $name")
            drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
            
            if (isInstantMode) applyLayoutImmediate() 
        } catch (e: Exception) { 
            Log.e(TAG, "Failed to load profile", e) 
        } 
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
            Log.d(TAG, "executeLaunch: Already running. Queueing next run.")
            pendingExecutionNeeded = true
            if (focusPackage != null) pendingFocusPackage = focusPackage
            return
        }

        isExecuting = true
        pendingExecutionNeeded = false // Reset pending flag for THIS run

        // Get currently visible apps on this display, excluding ourselves and the trackpad
        val activeApps = shellService?.getVisiblePackages(currentDisplayId)
            ?.mapNotNull { pkgName -> allAppsList.find { it.packageName == pkgName } }
            ?.filter { it.packageName != packageName && it.packageName != PACKAGE_TRACKPAD }
            ?: emptyList()

        if (closeDrawer) toggleDrawer()
        refreshDisplayId()

        // Save queue
        val identifiers = selectedAppsQueue.map { it.getIdentifier() }
        AppPreferences.saveLastQueue(this, identifiers)
        
        Thread { 
            try { 
                var configChanged = false

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
                
                Log.d(TAG, "executeLaunch: Generated ${rects.size} tiles with Margin $bottomMarginPercent%")
                
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
                    if (killAppOnExecute) { 
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
                        // Log.d(TAG, "Tile[$i]: Executing Launch: $cmd")
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
                        // Log.d(TAG, "Tile[$i]: Repositioning ${app.label} (TID: $tid)")
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
                        Log.d(TAG, "Refocusing Active Window: ${app.label}")
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
                    Log.d(TAG, "executeLaunch: Triggering pending execution")
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
    private fun switchMode(mode: Int) {
        currentMode = mode
        selectedListIndex = 0 // Reset selection on tab switch
        
        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val searchIcon = drawerView!!.findViewById<ImageView>(R.id.icon_search_mode); val iconWin = drawerView!!.findViewById<ImageView>(R.id.icon_mode_window);        val iconRes = drawerView!!.findViewById<ImageView>(R.id.icon_mode_resolution);
        val iconRefresh = drawerView!!.findViewById<ImageView>(R.id.icon_mode_refresh); val iconDpi = drawerView!!.findViewById<ImageView>(R.id.icon_mode_dpi); val iconBlacklist = drawerView!!.findViewById<ImageView>(R.id.icon_mode_blacklist); val iconProf = drawerView!!.findViewById<ImageView>(R.id.icon_mode_profiles); val iconKeybinds = drawerView!!.findViewById<ImageView>(R.id.icon_mode_keybinds); val iconSet = drawerView!!.findViewById<ImageView>(R.id.icon_mode_settings); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        searchIcon.setColorFilter(if(mode==MODE_SEARCH) Color.WHITE else Color.GRAY); iconWin.setColorFilter(if(mode==MODE_LAYOUTS) Color.WHITE else Color.GRAY);        iconRes.setColorFilter(if(mode==MODE_RESOLUTION) Color.WHITE else Color.GRAY);
        iconRefresh?.setColorFilter(if(mode==MODE_REFRESH) Color.WHITE else Color.GRAY); iconDpi.setColorFilter(if(mode==MODE_DPI) Color.WHITE else Color.GRAY); iconBlacklist?.setColorFilter(if(mode==MODE_BLACKLIST) Color.WHITE else Color.GRAY); iconProf.setColorFilter(if(mode==MODE_PROFILES) Color.WHITE else Color.GRAY); iconKeybinds?.setColorFilter(if(mode==MODE_KEYBINDS) Color.WHITE else Color.GRAY); iconSet.setColorFilter(if(mode==MODE_SETTINGS) Color.WHITE else Color.GRAY)
        executeBtn.visibility = if (isInstantMode) View.GONE else View.VISIBLE; displayList.clear(); val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); dock.visibility = if (mode == MODE_SEARCH && selectedAppsQueue.isNotEmpty()) View.VISIBLE else View.GONE

        when (mode) {
            MODE_SEARCH -> { searchBar.hint = "Search apps..."; refreshSearchList() }
            MODE_LAYOUTS -> { 
                searchBar.hint = "Select Layout"
                displayList.add(ActionOption("Save Current Arrangement") { saveCurrentAsCustom() })
                
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
            }
            MODE_RESOLUTION -> {
                searchBar.hint = "Select Resolution"; displayList.add(CustomResInputOption); val savedResNames = AppPreferences.getCustomResolutionNames(this).sorted(); for (name in savedResNames) { val value = AppPreferences.getCustomResolutionValue(this, name) ?: continue; displayList.add(ResolutionOption(name, "wm size  -d $currentDisplayId", 100 + savedResNames.indexOf(name))) }; displayList.add(ResolutionOption("Default (Reset)", "wm size reset -d $currentDisplayId", 0)); displayList.add(ResolutionOption("1:1 Square (1422x1500)", "wm size 1422x1500 -d $currentDisplayId", 1)); displayList.add(ResolutionOption("16:9 Landscape (1920x1080)", "wm size 1920x1080 -d $currentDisplayId", 2)); displayList.add(ResolutionOption("32:9 Ultrawide (3840x1080)", "wm size 3840x1080 -d $currentDisplayId", 3))
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
                
                Log.d(TAG, "Display $currentDisplayId supported rates: $supportedRates")
                
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
                val rates = listOf(30f, 60f, 90f, 120f)
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
            MODE_PROFILES -> { searchBar.hint = "Enter Profile Name..."; displayList.add(ProfileOption("Save Current as New", true, 0,0,0, emptyList())); val profileNames = AppPreferences.getProfileNames(this).sorted(); for (pName in profileNames) { val data = AppPreferences.getProfileData(this, pName); if (data != null) { try { val parts = data.split("|"); val lay = parts[0].toInt(); val res = parts[1].toInt(); val d = parts[2].toInt(); val pkgs = parts[3].split(",").filter { it.isNotEmpty() }; displayList.add(ProfileOption(pName, false, lay, res, d, pkgs)) } catch(e: Exception) {} } } }
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
                displayList.add(ToggleOption("Reorder: Drag & Drop", isReorderDragEnabled) { isReorderDragEnabled = it; AppPreferences.setReorderDrag(this, it) })
                displayList.add(ToggleOption("Reorder: Tap to Swap (Long Press)", isReorderTapEnabled) { isReorderTapEnabled = it; AppPreferences.setReorderTap(this, it) })
                displayList.add(ToggleOption("Instant Mode (Live Changes)", isInstantMode) { isInstantMode = it; AppPreferences.setInstantMode(this, it); executeBtn.visibility = if (it) View.GONE else View.VISIBLE; if (it) fetchRunningApps() })
                displayList.add(ToggleOption("Kill App on Execute", killAppOnExecute) { killAppOnExecute = it; AppPreferences.setKillOnExecute(this, it) })
                
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
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    // === SWITCH MODE - END ===

    object CustomResInputOption
    data class RefreshHeaderOption(val text: String)
    // =================================================================================
    // DATA CLASS: RefreshItemOption
    // SUMMARY: Represents a refresh rate option in the menu. isAvailable indicates
    //          if the hardware supports this rate. Unavailable rates are greyed out.
    // =================================================================================
    data class RefreshItemOption(
        val label: String, 
        val targetRate: Float, 
        val isSelected: Boolean,

    val isAvailable: Boolean = true  // NEW: false if hardware doesn't support this rate
    )
    // =================================================================================
    // END DATA CLASS: RefreshItemOption
    // =================================================================================

    data class LayoutOption(val name: String, val type: Int, val isCustomSaved: Boolean = false, val customRects: List<Rect>? = null)
    data class ResolutionOption(val name: String, val command: String, val index: Int)
    data class DpiOption(val currentDpi: Int)
    data class ProfileOption(val name: String, val isCurrent: Boolean, val layout: Int, val resIndex: Int, val dpi: Int, val apps: List<String>)
    data class FontSizeOption(val currentSize: Float)
    data class HeightOption(val currentPercent: Int)
    data class WidthOption(val currentPercent: Int)
    data class MarginOption(val type: Int, val currentPercent: Int) // type: 0=Top, 1=Bottom
    data class IconOption(val name: String)
    data class ActionOption(val name: String, val action: () -> Unit)
    data class ToggleOption(val name: String, var isEnabled: Boolean, val onToggle: (Boolean) -> Unit)
    data class TimeoutOption(val seconds: Int)

    inner class SelectedAppsAdapter : RecyclerView.Adapter<SelectedAppsAdapter.Holder>() {
        inner class Holder(v: View) : RecyclerView.ViewHolder(v) {
            val icon: ImageView = v.findViewById(R.id.selected_app_icon)
            val underline: View = v.findViewById(R.id.focus_underline)
            val frame: View = itemView // Use root frame for border
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder { return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_app, parent, false)) }

        // === SELECTED APPS ADAPTER BIND - START ===
        override fun onBindViewHolder(holder: Holder, position: Int) {
            val app = selectedAppsQueue[position]

            // Show Focus Underline
            val isFocused = (app.packageName == activePackageName) ||
                            (app.packageName == "com.google.android.apps.bard" && activePackageName == "com.google.android.googlequicksearchbox")
            holder.underline.visibility = if (isFocused) View.VISIBLE else View.GONE

            // === KEYBOARD NAVIGATION HIGHLIGHT ===
            // 1. Is this the currently selected slot?
            val isNavSelected = (currentFocusArea == FOCUS_QUEUE && position == queueSelectedIndex)
            
            // 2. Is this the source slot for a pending command (e.g. Swap)?
            val isCommandSource = (queueCommandPending != null && position == queueCommandSourceIndex)

            if (isNavSelected) {
                // Bright White Border for Selection
                val bg = GradientDrawable()
                bg.setStroke(4, Color.WHITE)
                bg.cornerRadius = 8f
                bg.setColor(Color.parseColor("#44FFFFFF")) // Semi-transparent fill
                holder.frame.background = bg
            } else if (isCommandSource) {
                // Dashed Yellow/Green Border for Source
                val bg = GradientDrawable()
                bg.setStroke(4, Color.GREEN, 10f, 5f) // Dashed
                bg.cornerRadius = 8f
                holder.frame.background = bg
            } else {
                holder.frame.background = null
            }
            // =====================================

            holder.icon.clearColorFilter()
            
            if (app.packageName == PACKAGE_BLANK) { 
                holder.icon.setImageResource(R.drawable.ic_box_outline) 
            } else { 
                try { 
                    // Use packageName directly - it should be the real package, not a modified identifier
                    val iconPkg = app.packageName
                    // Log.d(DEBUG_TAG, "Loading icon for position $position: ${app.label} pkg=$iconPkg") // SILENCED
                    holder.icon.setImageDrawable(packageManager.getApplicationIcon(iconPkg)) 
                } catch (e: Exception) { 
                    // Log.e(DEBUG_TAG, "Failed to load icon for ${app.packageName}", e) // SILENCED
                    holder.icon.setImageResource(R.drawable.ic_launcher_bubble) 
                }
                holder.icon.alpha = if (app.isMinimized) 0.4f else 1.0f 
            }
            
            holder.itemView.setOnClickListener { 
                try { 
                    dismissKeyboardAndRestore()
                    if (reorderSelectionIndex != -1) { 
                        if (position == reorderSelectionIndex) { 
                            endReorderMode(false) 
                        } else { 
                            swapReorderItem(position) 
                        } 
                    } else { 
                        if (app.packageName != PACKAGE_BLANK) { 
                            // [FIX] Use centralized command to ensure focus logic runs
                            // This routes the click through handleWindowManagerCommand which contains the logic
                            // to clear activePackageName if the minimized app was the focused one.
                            val intent = Intent().apply {
                                putExtra("COMMAND", "TOGGLE_MINIMIZE")
                                putExtra("INDEX", position + 1) // Handler expects 1-based index
                            }
                            handleWindowManagerCommand(intent)
                        } 
                    } 
                } catch(e: Exception) {
                    // Log.e(DEBUG_TAG, "Click handler error", e) // SILENCED
                } 
            }
            
            holder.itemView.setOnLongClickListener { 
                if (isReorderTapEnabled) { 
                    startReorderMode(position)
                    true 
                } else { 
                    false 
                } 
            }
        }
        // === SELECTED APPS ADAPTER BIND - END ===
        override fun getItemCount() = selectedAppsQueue.size
    }

    // =================================================================================
    // WINDOW MANAGER COMMAND PROCESSOR (v2)
    // SUMMARY: Handles headless commands with 1-BASED INDEXING.
    //          Supports Active Window swapping and Blank Space hiding.
    // =================================================================================
    private fun handleWindowManagerCommand(intent: Intent) {
        val cmd = intent.getStringExtra("COMMAND")?.uppercase(Locale.ROOT) ?: return
        
        // CONVERT 1-BASED INDEX TO 0-BASED INTERNAL INDEX
        // If user sends 1, we get 0. If user sends 0 or nothing (-1), it stays invalid (-1).
        val rawIndex = intent.getIntExtra("INDEX", -1)
        val index = if (rawIndex > 0) rawIndex - 1 else -1

        Log.d(TAG, "WM Command: $cmd RawIdx: $rawIndex (Internal: $index)")

        when (cmd) {
            "SWAP" -> {
                // Convert both A and B from 1-based
                val rawA = intent.getIntExtra("INDEX_A", -1)
                val rawB = intent.getIntExtra("INDEX_B", -1)
                val idxA = if (rawA > 0) rawA - 1 else -1
                val idxB = if (rawB > 0) rawB - 1 else -1
                
                if (idxA in selectedAppsQueue.indices && idxB in selectedAppsQueue.indices) {
                    val appA = selectedAppsQueue[idxA]
                    val appB = selectedAppsQueue[idxB]

                    // LOGIC: State Swap if mixed Active/Inactive
                    // This allows inactive apps to "take the place" of active ones
                    if (appA.isMinimized != appB.isMinimized) {
                        val stateA = appA.isMinimized
                        appA.isMinimized = appB.isMinimized
                        appB.isMinimized = stateA
                    }

                    Collections.swap(selectedAppsQueue, idxA, idxB)

                    // LOGIC: Remove Inactive Blanks (Auto-Delete)
                    // If a blank space was swapped into an inactive state, delete it
                    val toRemove = mutableListOf<MainActivity.AppInfo>()
                    if (appA.packageName == PACKAGE_BLANK && appA.isMinimized) toRemove.add(appA)
                    if (appB.packageName == PACKAGE_BLANK && appB.isMinimized) toRemove.add(appB)
                    
                    if (toRemove.isNotEmpty()) {
                        selectedAppsQueue.removeAll(toRemove)
                    }

                    refreshQueueAndLayout("Swapped slots $rawA & $rawB")
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
                
                // Find index of active app
                // We match by package name (simplest)
                val activeIdx = selectedAppsQueue.indexOfFirst { 
                    it.packageName == activePackageName || 
                    (it.packageName == "com.google.android.apps.bard" && activePackageName == "com.google.android.googlequicksearchbox") 
                }

                if (activeIdx != -1) {
                    val dir = if (cmd == "SWAP_ACTIVE_LEFT") -1 else 1
                    val targetIdx = activeIdx + dir

                    if (targetIdx in selectedAppsQueue.indices) {
                        Collections.swap(selectedAppsQueue, activeIdx, targetIdx)
                        refreshQueueAndLayout("Moved Active Window ${if(dir<0) "Left" else "Right"}", activePackageName)
                    } else {
                        safeToast("Edge of layout reached")
                    }
                } else {
                    safeToast("Active app not in layout")
                }
            }
            "KILL" -> {
                if (index in selectedAppsQueue.indices) {
                    val app = selectedAppsQueue[index]
                    if (app.packageName != PACKAGE_BLANK) {
                        val basePkg = app.getBasePackage()
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

                        refreshQueueAndLayout(if (newState) "Minimized ${app.label}" else "Restored ${app.label}")
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
                                
                                // [FIX] Clear focus if hiding the active app
                                val isGemini = basePkg == "com.google.android.apps.bard"
                                val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"

                                if (activePackageName == basePkg || 
                                    activePackageName == targetApp.packageName ||
                                    (isGemini && activeIsGoogle)) {
                                    activePackageName = null
                                    Log.d(TAG, "WM Command: Cleared focus for hidden app: $basePkg")
                                }

                                Thread { 
                                    try { 
                                        val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                        if (tid != -1) shellService?.moveTaskToBack(tid)
                                        if (killAppOnExecute) shellService?.forceStop(basePkg)
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
                                
                                refreshQueueAndLayout("Hidden Slot ${index + 1}")
                             }
                        }            "LAYOUT" -> {
                val type = intent.getIntExtra("TYPE", -1)
                if (type != -1) {
                    selectedLayoutType = type
                    AppPreferences.saveLastLayout(this, type, currentDisplayId)
                    refreshQueueAndLayout("Layout: ${getLayoutName(type)}")
                }
            }
            "CLEAR_ALL" -> {
                if (killAppOnExecute) {
                    val activeApps = selectedAppsQueue.filter { !it.isMinimized && it.packageName != PACKAGE_BLANK }
                    Thread {
                        for (app in activeApps) {
                            try { shellService?.forceStop(app.getBasePackage()) } catch(e: Exception){}
                        }
                    }.start()
                }
                // Wipe all history
                activePackageName = null
                lastValidPackageName = null
                secondLastValidPackageName = null
                
                selectedAppsQueue.clear()
                refreshQueueAndLayout("Cleared All")
            }
            "OPEN_DRAWER" -> {
                Log.d(TAG, "OPEN_DRAWER command received, calling toggleDrawer()")
                toggleDrawer()
                refreshQueueAndLayout("Toggled Drawer")
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
                            // Drawer Closed: Perform Actual Launch
                            val rects = getLayoutRects()
                            val bounds = if (index < rects.size) rects[index] else null
                            Thread {
                                 launchViaShell(app.getBasePackage(), app.className, bounds)
                            }.start()
                            safeToast("Focused: ${app.label}")
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
                            // Drawer Closed: Perform Actual Launch
                            val idx = selectedAppsQueue.indexOf(app)
                            val rects = getLayoutRects()
                            val bounds = if (idx >= 0 && idx < rects.size) rects[idx] else null
                            Thread {
                                 launchViaShell(app.getBasePackage(), app.className, bounds)
                            }.start()
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

    private fun refreshQueueAndLayout(msg: String, focusPackage: String? = null) {
        uiHandler.post {
            sortAppQueue() // Ensure active apps are at the front
            updateAllUIs()

            // Auto-save queue state
            val identifiers = selectedAppsQueue.map { it.getIdentifier() }
            AppPreferences.saveLastQueue(this, identifiers)

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

            // Broadcast is kept for backward compatibility but DockIME primarily uses SharedPrefs
            if (activePackageName != null) {
                val isGeminiFocused = activePackageName == "com.google.android.googlequicksearchbox"
                val isManaged = activeNonMinimized.any { it.getBasePackage() == activePackageName || it.packageName == activePackageName || (isGeminiFocused && it.getBasePackage() == "com.google.android.apps.bard") }
                val shouldSuppressInsets = hasManagedApps || isManaged
                sendBroadcast(Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.TILED_STATE")
                    .setPackage("com.katsuyamaki.DroidOSTrackpadKeyboard")
                    .putExtra("TILED_ACTIVE", shouldSuppressInsets))
            }

            safeToast(msg)

            // Trigger Tiling
            if (isInstantMode) {
                applyLayoutImmediate(focusPackage)
            }
        }
    }
    // =================================================================================
    // END WINDOW MANAGER COMMAND PROCESSOR
    // =================================================================================

    private fun logSavedKeybinds() {
        Log.d("DroidOS_Keys", "=== SAVED KEYBINDS ===")
        for (cmd in AVAILABLE_COMMANDS) {
            val bind = AppPreferences.getKeybind(this, cmd.id)
            if (bind.second != 0) {
                val keyName = KeyEvent.keyCodeToString(bind.second)
                // WORKAROUND: KeyEvent.metaStateToString may be unresolved in some environments.
                // Using toString() directly on the integer for debug output.
                val modName = if (bind.first != 0) "Meta(${bind.first})" else "None"
                Log.d("DroidOS_Keys", "CMD: ${cmd.label} -> [$modName] + [$keyName] (Mod:${bind.first}, Key:${bind.second})")
            }
        }
        Log.d("DroidOS_Keys", "======================")
    }

    private fun buildAdbCommand(cmdId: String): String? {
        return when (cmdId) {
            "SWAP" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND SWAP --ei INDEX_A 1 --ei INDEX_B 2"
            "SWAP_ACTIVE_LEFT" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND SWAP_ACTIVE_LEFT"
            "SWAP_ACTIVE_RIGHT" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND SWAP_ACTIVE_RIGHT"
            "HIDE" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND HIDE --ei INDEX 1"
            "MINIMIZE" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND MINIMIZE --ei INDEX 1"
            "UNMINIMIZE" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND UNMINIMIZE --ei INDEX 1"
            "KILL" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER --es COMMAND KILL --ei INDEX 1"
            "OPEN_DRAWER" -> "adb shell am broadcast -a com.katsuyamaki.DroidOSLauncher.OPEN_DRAWER"
            else -> null
        }
    }

    inner class RofiAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        inner class AppHolder(v: View) : RecyclerView.ViewHolder(v) { val icon: ImageView = v.findViewById(R.id.rofi_app_icon); val text: TextView = v.findViewById(R.id.rofi_app_text); val star: ImageView = v.findViewById(R.id.rofi_app_star) }
        // [FIX] Include all buttons in Holder to support legacy logic and prevent crashes
        inner class LayoutHolder(v: View) : RecyclerView.ViewHolder(v) { 
            val nameInput: EditText = v.findViewById(R.id.layout_name)
            val btnEdit: ImageView = v.findViewById(R.id.btn_edit_layout_name)
            val btnSave: ImageView = v.findViewById(R.id.btn_save_profile)
            val btnExtinguish: ImageView = v.findViewById(R.id.btn_extinguish_item) 
        }

        inner class DpiHolder(v: View) : RecyclerView.ViewHolder(v) { val slider: android.widget.SeekBar = v.findViewById(R.id.sb_dpi_slider); val input: EditText = v.findViewById(R.id.input_dpi_value) }

        inner class FontSizeHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_font_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_font_plus); val textVal: TextView = v.findViewById(R.id.text_font_value) }
        inner class HeightHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_height_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_height_plus); val textVal: TextView = v.findViewById(R.id.text_height_value) }
        inner class WidthHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_width_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_width_plus); val textVal: TextView = v.findViewById(R.id.text_width_value) }
        inner class ProfileRichHolder(v: View) : RecyclerView.ViewHolder(v) { val name: EditText = v.findViewById(R.id.profile_name_text); val details: TextView = v.findViewById(R.id.profile_details_text); val iconsContainer: LinearLayout = v.findViewById(R.id.profile_icons_container); val btnSave: ImageView = v.findViewById(R.id.btn_save_profile_rich) }
        inner class IconSettingHolder(v: View) : RecyclerView.ViewHolder(v) { val preview: ImageView = v.findViewById(R.id.icon_setting_preview) }
        inner class CustomResInputHolder(v: View) : RecyclerView.ViewHolder(v) { val inputW: EditText = v.findViewById(R.id.input_res_w); val inputH: EditText = v.findViewById(R.id.input_res_h); val btnSave: ImageView = v.findViewById(R.id.btn_save_res) }
        inner class KeybindHolder(v: View) : RecyclerView.ViewHolder(v) { val title: TextView = v.findViewById(R.id.kb_title); val desc: TextView = v.findViewById(R.id.kb_desc); val btnMod: android.widget.Button = v.findViewById(R.id.btn_mod); val btnKey: android.widget.Button = v.findViewById(R.id.btn_key) }
        inner class CustomModHolder(v: View) : RecyclerView.ViewHolder(v) { 
            val input: EditText = v.findViewById(R.id.input_custom_mod) 
        }
        inner class MarginHolder(v: View) : RecyclerView.ViewHolder(v) { 
            val label: TextView = v.findViewById(R.id.text_margin_label)
            val slider: android.widget.SeekBar = v.findViewById(R.id.sb_margin_slider)
            val text: TextView = v.findViewById(R.id.text_margin_value)
        }
        // Reuse item_layout_option logic for simplicity
        inner class HeaderHolder(v: View) : RecyclerView.ViewHolder(v) { val nameInput: EditText = v.findViewById(R.id.layout_name); val btnSave: View = v.findViewById(R.id.btn_save_profile); val btnExtinguish: View = v.findViewById(R.id.btn_extinguish_item) }
        inner class ActionHolder(v: View) : RecyclerView.ViewHolder(v) { val nameInput: EditText = v.findViewById(R.id.layout_name); val btnSave: View = v.findViewById(R.id.btn_save_profile); val btnExtinguish: View = v.findViewById(R.id.btn_extinguish_item) }

        override fun getItemViewType(position: Int): Int { return when (displayList[position]) { is MainActivity.AppInfo -> 0; is LayoutOption -> 1; is ResolutionOption -> 1; is DpiOption -> 2; is ProfileOption -> 4; is FontSizeOption -> 3; is IconOption -> 5; is ToggleOption -> 1; is ActionOption -> 6; is HeightOption -> 7; is WidthOption -> 8;         is CustomResInputOption -> 9; is RefreshHeaderOption -> 10; is RefreshItemOption -> 11; is KeybindOption -> 12; is CustomModConfigOption -> 13; is MarginOption -> 14; else -> 0 } }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder { return when (viewType) { 0 -> AppHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_app_rofi, parent, false)); 1 -> LayoutHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false)); 2 -> DpiHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dpi_custom, parent, false)); 3 -> FontSizeHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_font_size, parent, false)); 4 -> ProfileRichHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_rich, parent, false)); 5 -> IconSettingHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_icon_setting, parent, false)); 6 -> LayoutHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false)); 7 -> HeightHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_height_setting, parent, false)); 8 -> WidthHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_width_setting, parent, false));             9 -> CustomResInputHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_custom_resolution, parent, false));
            10 -> HeaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false));
            11 -> ActionHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false)); 12 -> KeybindHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_keybind, parent, false)); 
13 -> CustomModHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_custom_mod, parent, false));
14 -> MarginHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_margin_setting, parent, false))
else -> AppHolder(View(parent.context)) } }
        private fun startRename(editText: EditText) { editText.isEnabled = true; editText.isFocusable = true; editText.isFocusableInTouchMode = true; editText.requestFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT) }
        private fun endRename(editText: EditText) { editText.isFocusable = false; editText.isFocusableInTouchMode = false; editText.isEnabled = false; val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(editText.windowToken, 0) }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = displayList[position]
            if (holder is AppHolder) holder.text.textSize = currentFontSize
            if (holder is LayoutHolder) holder.nameInput.textSize = currentFontSize
            if (holder is ProfileRichHolder) holder.name.textSize = currentFontSize

            // --- VISUAL HIGHLIGHT LOGIC ---
            // If row is selected via keyboard, force active background.
            // Otherwise, use standard pressable background.
            val isKeyboardSelected = (position == selectedListIndex)
            val bgRes = if (isKeyboardSelected) R.drawable.bg_item_active else R.drawable.bg_item_press
            
            // Only apply generic background here if not overridden by specific types below
            if (holder !is ProfileRichHolder && holder !is ActionHolder && holder !is LayoutHolder) {
                 holder.itemView.setBackgroundResource(bgRes)
            }
            // ------------------------------

            // === APP HOLDER BINDING - START ===
            // Handles app item display with proper package name extraction for icons
            if (holder is AppHolder && item is MainActivity.AppInfo) {
                holder.text.text = item.label
                if (item.packageName == PACKAGE_BLANK) {
                    holder.icon.setImageResource(R.drawable.ic_box_outline)
                } else {
                    try {
                        // Extract base package name (remove ":suffix" if present)
                        val basePkg = if (item.packageName.contains(":")) item.packageName.substringBefore(":") else item.packageName
                        holder.icon.setImageDrawable(packageManager.getApplicationIcon(basePkg))
                    } catch (e: Exception) {
                        holder.icon.setImageResource(R.drawable.ic_launcher_bubble)
                    }
                }
                // Highlight if selected in queue OR if selected via keyboard navigation
                val isSelectedInQueue = selectedAppsQueue.any { it.packageName == item.packageName }
                
                if (isSelectedInQueue || isKeyboardSelected) {
                     holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
                } else {
                     holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                }
                
                holder.star.visibility = if (item.isFavorite) View.VISIBLE else View.GONE
                holder.itemView.setOnClickListener { addToSelection(item) }
                holder.itemView.setOnLongClickListener { toggleFavorite(item); refreshSearchList(); true }
            }
            // === APP HOLDER BINDING - END ===
            else if (holder is ProfileRichHolder && item is ProfileOption) { holder.name.setText(item.name); holder.iconsContainer.removeAllViews(); if (!item.isCurrent) { for (pkg in item.apps.take(5)) { val iv = ImageView(holder.itemView.context); val lp = LinearLayout.LayoutParams(60, 60); lp.marginEnd = 8; iv.layoutParams = lp; if (pkg == PACKAGE_BLANK) { iv.setImageResource(R.drawable.ic_box_outline) } else { try { iv.setImageDrawable(packageManager.getApplicationIcon(pkg)) } catch (e: Exception) { iv.setImageResource(R.drawable.ic_launcher_bubble) } }; holder.iconsContainer.addView(iv) }; val info = "${getLayoutName(item.layout)} | ${getRatioName(item.resIndex)} | ${item.dpi}dpi"; holder.details.text = info; holder.details.visibility = View.VISIBLE; holder.btnSave.visibility = View.GONE; if (activeProfileName == item.name) { holder.itemView.setBackgroundResource(R.drawable.bg_item_active) } else { holder.itemView.setBackgroundResource(0) }; holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); loadProfile(item.name) }; holder.itemView.setOnLongClickListener { startRename(holder.name); true }; val saveProfileName = { val newName = holder.name.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameProfile(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); switchMode(MODE_PROFILES) } }; endRename(holder.name) }; holder.name.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveProfileName(); holder.name.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(holder.name.windowToken, 0); updateDrawerHeight(false); true } else false }; holder.name.setOnFocusChangeListener { v, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus); if (!hasFocus) saveProfileName() } } else { holder.iconsContainer.removeAllViews(); holder.details.visibility = View.GONE; holder.btnSave.visibility = View.VISIBLE; holder.itemView.setBackgroundResource(0); holder.name.isEnabled = true; holder.name.isFocusable = true; holder.name.isFocusableInTouchMode = true; holder.itemView.setOnClickListener { saveProfile() }; holder.btnSave.setOnClickListener { saveProfile() } } }
            else if (holder is LayoutHolder) {
                // --- APPLY KEYBOARD HIGHLIGHT ---
                if (isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
                else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                
                // [FIX] Explicitly hide all buttons by default
                holder.btnEdit.visibility = View.GONE
                holder.btnSave.visibility = View.GONE
                holder.btnExtinguish.visibility = View.GONE

                // [CRITICAL FIX] CLEAR ALL LISTENERS
                // This prevents "Ghost Saves" where listeners from a previous LayoutOption
                // persist when the holder is reused for a Toggle/Action option.
                holder.nameInput.onFocusChangeListener = null
                holder.nameInput.setOnEditorActionListener(null)
                holder.itemView.setOnLongClickListener(null)
                holder.itemView.setOnClickListener(null)
                holder.nameInput.setOnClickListener(null)

                // [FIX] Reset interactivity to prevent ghost touches blocking clicks
                // LayoutOption enables these explicitly, but other types need them OFF
                holder.nameInput.isClickable = false
                holder.nameInput.isFocusable = false
                holder.nameInput.isFocusableInTouchMode = false
                holder.nameInput.background = null
                // --------------------------------
                
                                if (item is LayoutOption) { 
                                    holder.nameInput.setText(item.name)
                                    val isSelected = if (item.type == LAYOUT_CUSTOM_DYNAMIC) { item.type == selectedLayoutType && item.name == activeCustomLayoutName } else { item.type == selectedLayoutType && activeCustomLayoutName == null }
                                    
                                    if (isSelected || isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) 
                                    else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                                    
                                    // SELECTION CLICK
                                    val selectAction = View.OnClickListener { selectLayout(item) }
                                    holder.itemView.setOnClickListener(selectAction)
                                    holder.nameInput.setOnClickListener(selectAction)
                                    
                                    // RESET STATE (Default)
                                    holder.nameInput.apply {
                                        isEnabled = true
                                        setTextColor(Color.WHITE)
                                        background = null
                                        isFocusable = false
                                        isFocusableInTouchMode = false
                                        isClickable = true
                                        isLongClickable = false
                                        inputType = 0
                                    }
                                    
                                    // EDIT BUTTON LOGIC (Renaming)
                                    // Works for BOTH Custom and Default layouts now
                                    holder.btnEdit.visibility = View.VISIBLE
                                    holder.btnEdit.setOnClickListener {
                                        // Enable Editing
                                        holder.nameInput.isFocusable = true
                                        holder.nameInput.isFocusableInTouchMode = true
                                        holder.nameInput.isClickable = true
                                        holder.nameInput.inputType = android.text.InputType.TYPE_CLASS_TEXT
                                        
                                        startRename(holder.nameInput)
                                    }
                
                                    // SAVE LOGIC
                                    val saveAction = { 
                                        // [FIX] Post to handler to avoid crashing during focus change/layout pass
                                        uiHandler.post {
                                            val newName = holder.nameInput.text.toString().trim()
                                            var changed = false
                                            
                                            if (newName.isNotEmpty() && newName != item.name) { 
                                                if (item.isCustomSaved) {
                                                    // Rename CUSTOM
                                                    if (AppPreferences.renameCustomLayout(holder.itemView.context, item.name, newName)) { 
                                                        safeToast("Renamed to $newName")
                                                        if (activeCustomLayoutName == item.name) { 
                                                            activeCustomLayoutName = newName
                                                            AppPreferences.saveLastCustomLayoutName(holder.itemView.context, newName, currentDisplayId)
                                                        }
                                                        switchMode(MODE_LAYOUTS) 
                                                        changed = true
                                                    } 
                                                } else {
                                                    // Rename DEFAULT
                                                    AppPreferences.saveDefaultLayoutName(holder.itemView.context, item.type, newName)
                                                    safeToast("Renamed to $newName")
                                                    switchMode(MODE_LAYOUTS)
                                                    changed = true
                                                }
                                            }
                                            
                                            // Only reset UI locally if we didn't refresh the whole list via switchMode
                                            if (!changed) {
                                                endRename(holder.nameInput)
                                                holder.nameInput.isEnabled = true // Keep text white
                                                holder.nameInput.isFocusable = false
                                                holder.nameInput.isClickable = true
                                                holder.nameInput.inputType = 0
                                            }
                                        }
                                    }
                                    
                                    holder.nameInput.setOnEditorActionListener { _, actionId, _ -> 
                                        if (actionId == EditorInfo.IME_ACTION_DONE) { saveAction(); true } else false 
                                    }
                                    holder.nameInput.setOnFocusChangeListener { _, hasFocus -> 
                                        if (!hasFocus) saveAction() 
                                    } 
                                }                else if (item is ResolutionOption) { 
                    holder.nameInput.setText(item.name); if (item.index >= 100) { holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }; val saveResName = { val newName = holder.nameInput.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameCustomResolution(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); switchMode(MODE_RESOLUTION) } }; endRename(holder.nameInput) }; holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveResName(); true } else false }; holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveResName() } } else { holder.nameInput.isEnabled = false; holder.nameInput.isFocusable = false; holder.nameInput.setTextColor(Color.WHITE) }; val isSelected = (item.index == selectedResolutionIndex); if (isSelected || isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { applyResolution(item) } 
                }
                else if (item is IconOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); if(isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { pickIcon() } }
                else if (item is ToggleOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); if (item.isEnabled || isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.isEnabled = !item.isEnabled; item.onToggle(item.isEnabled); notifyItemChanged(position) } } 
                else if (item is ActionOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); if(isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.action() } }
            }
            else if (holder is HeaderHolder && item is RefreshHeaderOption) {
                holder.nameInput.setText(item.text)
                holder.nameInput.isEnabled = false
                holder.nameInput.setTextColor(Color.GREEN)
                holder.nameInput.textSize = 16f
                holder.nameInput.gravity = Gravity.CENTER
                holder.itemView.setBackgroundResource(0)
                holder.btnSave.visibility = View.GONE
                holder.btnExtinguish.visibility = View.GONE
            }
// =================================================================================
            // REFRESH ITEM OPTION BINDING
            // SUMMARY: Binds refresh rate options. Available rates show white text and are
            //          clickable. Unavailable rates are greyed out and show explanation toast.
            // =================================================================================
            else if (holder is ActionHolder && item is RefreshItemOption) {
                holder.nameInput.setText(item.label)
                holder.nameInput.isEnabled = false
                holder.btnSave.visibility = View.GONE
                holder.btnExtinguish.visibility = View.GONE
                
                if (item.isAvailable) {
                    // Available rate - normal styling
                    if (item.isSelected) {
                        holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
                        holder.nameInput.setTextColor(Color.WHITE)
                        holder.nameInput.setTypeface(null, android.graphics.Typeface.BOLD)
                    } else {
                        holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                        holder.nameInput.setTextColor(Color.WHITE)
                        holder.nameInput.setTypeface(null, android.graphics.Typeface.NORMAL)
                    }
                    
                    holder.itemView.alpha = 1.0f
                    holder.itemView.setOnClickListener {
                        dismissKeyboardAndRestore()
                        applyRefreshRate(item.targetRate)
                    }
                } else {
                    // Unavailable rate - greyed out styling
                    holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                    holder.nameInput.setTextColor(Color.GRAY)
                    holder.nameInput.setTypeface(null, android.graphics.Typeface.ITALIC)
                    holder.itemView.alpha = 0.5f
                    
                    holder.itemView.setOnClickListener {
                        dismissKeyboardAndRestore()
                        safeToast("${item.targetRate.toInt()}Hz not supported by this display")
                    }
                }
            }
            // =================================================================================
            // END REFRESH ITEM OPTION BINDING
            // =================================================================================
            else if (holder is CustomResInputHolder) {
                holder.btnSave.setOnClickListener { val wStr = holder.inputW.text.toString().trim(); val hStr = holder.inputH.text.toString().trim(); if (wStr.isNotEmpty() && hStr.isNotEmpty()) { val w = wStr.toIntOrNull(); val h = hStr.toIntOrNull(); if (w != null && h != null && w > 0 && h > 0) { val gcdVal = calculateGCD(w, h); val wRatio = w / gcdVal; val hRatio = h / gcdVal; val resString = "${w}x${h}"; val name = "$wRatio:$hRatio Custom ($resString)"; AppPreferences.saveCustomResolution(holder.itemView.context, name, resString); safeToast("Added $name"); dismissKeyboardAndRestore(); switchMode(MODE_RESOLUTION) } else { safeToast("Invalid numbers") } } else { safeToast("Input W and H") } }
                holder.inputW.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }; holder.inputH.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }
            }
            else if (holder is IconSettingHolder && item is IconOption) { try { val uriStr = AppPreferences.getIconUri(holder.itemView.context); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); holder.preview.setImageBitmap(bitmap) } else { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) } } catch(e: Exception) { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) }; holder.itemView.setOnClickListener { pickIcon() } }

            else if (holder is DpiHolder && item is DpiOption) { 
                // Set initial values
                val safeDpi = if (item.currentDpi > 0) item.currentDpi else 0
                holder.input.setText(safeDpi.toString())
                holder.slider.progress = safeDpi

                // Slider Listener
                holder.slider.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            // Snap to 5
                            val snapped = (progress / 5) * 5
                            val finalDpi = snapped.coerceAtLeast(72) // Minimum safe DPI
                            
                            if (holder.input.text.toString() != finalDpi.toString()) {
                                holder.input.setText(finalDpi.toString())
                                holder.input.setSelection(holder.input.text.length)
                            }
                            // Debounce actual execution slightly if needed, or run direct
                            // For DPI, usually better to wait for "StopTracking", but "Live" is requested usually
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                        // Apply DPI only when user lets go to avoid spamming the system
                        val valInt = holder.input.text.toString().toIntOrNull()
                        if (valInt != null) selectDpi(valInt)
                    }
                })

                // Input Listener
                holder.input.setOnEditorActionListener { v, actionId, _ -> 
                    if (actionId == EditorInfo.IME_ACTION_DONE) { 
                        val valInt = v.text.toString().toIntOrNull()
                        if (valInt != null) { 
                            holder.slider.progress = valInt
                            selectDpi(valInt)
                            safeToast("DPI set to $valInt") 
                        }
                        dismissKeyboardAndRestore()
                        true 
                    } else false 
                }
                
                holder.input.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val valInt = s.toString().toIntOrNull()
                        if (valInt != null && holder.slider.progress != valInt) {
                            holder.slider.progress = valInt
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })

                holder.input.setOnFocusChangeListener { _, hasFocus -> 
                    if (autoResizeEnabled) updateDrawerHeight(hasFocus)
                }
            }

            else if (holder is FontSizeHolder && item is FontSizeOption) { holder.textVal.text = item.currentSize.toInt().toString(); holder.btnMinus.setOnClickListener { changeFontSize(item.currentSize - 1) }; holder.btnPlus.setOnClickListener { changeFontSize(item.currentSize + 1) } }
            else if (holder is HeightHolder && item is HeightOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerHeight(-5) }; holder.btnPlus.setOnClickListener { changeDrawerHeight(5) } }
            else if (holder is WidthHolder && item is WidthOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerWidth(-5) }; holder.btnPlus.setOnClickListener { changeDrawerWidth(5) } }
            else if (holder is MarginHolder && item is MarginOption) {
                holder.label.text = if (item.type == 0) "Top Margin:" else "Bottom Margin:"
                holder.text.text = "${item.currentPercent}%"
                holder.slider.progress = item.currentPercent
                
                holder.slider.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            holder.text.text = "$progress%"
                        }
                    }
                    override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                        val progress = seekBar?.progress ?: 0
                        
                        if (item.type == 0) {
                            topMarginPercent = progress
                            AppPreferences.setTopMarginPercent(holder.itemView.context, currentDisplayId, progress)
                            safeToast("Top Margin: $progress% (Display $currentDisplayId)")
                        } else {
                            bottomMarginPercent = progress
                            AppPreferences.setBottomMarginPercent(holder.itemView.context, currentDisplayId, progress)


                            safeToast("Bottom Margin: $progress% (Display $currentDisplayId)")


                            
                            // Broadcast change to Dock/Trackpad
                            val intent = Intent("com.katsuyamaki.DroidOSLauncher.MARGIN_CHANGED")
                            intent.putExtra("PERCENT", progress)
                            intent.setPackage("com.katsuyamaki.DroidOSTrackpadKeyboard") // Explicit target
                            sendBroadcast(intent)
                        }
                        
                        // Recalculate visual queue pos
                        setupVisualQueue()
                        
                        // Apply layout
                        if (isInstantMode) {
                            applyLayoutImmediate()
                        }
                    }
                })
            }
            else if (holder is CustomModHolder && item is CustomModConfigOption) {
                // Set current value
                val currentStr = getCharFromKeyCode(item.currentKeyCode)
                if (holder.input.text.toString() != currentStr) {
                    holder.input.setText(currentStr)
                }

                // Save on Text Change
                holder.input.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s != null && s.isNotEmpty()) {
                            val char = s[0]
                            val code = getKeyCodeFromChar(char)
                            if (code != 0) {
                                AppPreferences.saveCustomModKey(this@FloatingLauncherService, code)
                                customModKey = code
                                safeToast("Custom Mod set to: $char (Code $code)")

sendCustomModToTrackpad() // Sync immediately
                            } else {
                                safeToast("Unsupported Key Character")
                            }
                        } else {
                            // Cleared
                            AppPreferences.saveCustomModKey(this@FloatingLauncherService, 0)
                            customModKey = 0
                            sendCustomModToTrackpad()
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
                    override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
                })
            }
            else if (holder is KeybindHolder && item is KeybindOption) {
                holder.title.text = item.def.label
                holder.desc.text = item.def.description

                // Modifier Button (FORCE AT LEAST ONE)
                val modText = when (item.modifier) {
                    KeyEvent.META_ALT_ON -> "ALT"
                    KeyEvent.META_SHIFT_ON -> "SHIFT"
                    KeyEvent.META_CTRL_ON -> "CTRL"
                    KeyEvent.META_META_ON -> "META"
                    MOD_CUSTOM -> "CSTM" // New Custom State
                    0 -> "NONE"
                    else -> "MOD"
                }
                
                if (item.modifier == 0) holder.btnMod.setTextColor(Color.RED)
                else if (item.modifier == MOD_CUSTOM) holder.btnMod.setTextColor(Color.CYAN)
                else holder.btnMod.setTextColor(Color.GREEN)
                
                holder.btnMod.text = modText
                holder.btnMod.setOnClickListener {
                    item.modifier = when (item.modifier) {
                        0 -> KeyEvent.META_ALT_ON
                        KeyEvent.META_ALT_ON -> KeyEvent.META_SHIFT_ON
                        KeyEvent.META_SHIFT_ON -> KeyEvent.META_CTRL_ON
                        KeyEvent.META_CTRL_ON -> KeyEvent.META_META_ON
                        KeyEvent.META_META_ON -> MOD_CUSTOM // Cycle to Custom
                        MOD_CUSTOM -> KeyEvent.META_ALT_ON  // Loop back
                        else -> KeyEvent.META_ALT_ON
                    }
                    AppPreferences.saveKeybind(this@FloatingLauncherService, item.def.id, item.modifier, item.keyCode)
                    // Notify Trackpad of keybind changes
                    broadcastKeybindsToKeyboard()
                    notifyItemChanged(position)
                }

                // Key Button (Opens Picker)
                val keyName = SUPPORTED_KEYS.entries.find { it.value == item.keyCode }?.key ?: "?"
                holder.btnKey.text = keyName
                holder.btnKey.setOnClickListener {
                    // If modifier is NONE, force ALT before picking key
                    var safeMod = item.modifier
                    if (safeMod == 0) {
                        safeMod = KeyEvent.META_ALT_ON
                        AppPreferences.saveKeybind(this@FloatingLauncherService, item.def.id, safeMod, item.keyCode)
                        // Notify Trackpad of keybind changes
                        broadcastKeybindsToKeyboard()
                        safeToast("Safety: Modifier set to ALT")
                    }
                    showKeyPicker(item.def.id, safeMod)
                }

                // Long press on entire item to copy ADB command
                holder.itemView.setOnLongClickListener {
                    val adbCmd = buildAdbCommand(item.def.id)
                    if (adbCmd != null) {
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("ADB Command", adbCmd)
                        clipboard.setPrimaryClip(clip)
                        safeToast("ADB command copied!")
                    }
                    true
                }
            }
        }
        override fun getItemCount() = displayList.size
    }

    private fun getKeyCodeFromChar(c: Char): Int {
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
    
    private fun getCharFromKeyCode(code: Int): String {
        // Reverse mapping for display
        if (code == KeyEvent.KEYCODE_GRAVE) return "~"
        if (code == 0) return ""
        val str = KeyEvent.keyCodeToString(code)
        return str.replace("KEYCODE_", "")
    }
}
