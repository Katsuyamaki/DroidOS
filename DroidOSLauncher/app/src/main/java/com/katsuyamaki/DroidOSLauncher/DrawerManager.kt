package com.katsuyamaki.DroidOSLauncher

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Manages the Drawer UI including navigation, adapters, and remote key handling.
 * Extracted from FloatingLauncherService to separate UI concerns.
 */
class DrawerManager(
    private val context: Context,
    private val uiHandler: Handler
) {
    companion object {
        private const val TAG = "DrawerManager"
        
        // Focus area constants
        const val FOCUS_SEARCH = 0
        const val FOCUS_QUEUE = 1
        const val FOCUS_LIST = 2
    }

    // Callback interface for service communication
    interface Callback {
        // Window management
        fun getWindowManager(): WindowManager
        fun getDisplayContext(): Context?
        fun getAttachedWindowManager(): WindowManager?
        
        // State getters
        fun isExpanded(): Boolean
        fun setExpanded(value: Boolean)
        fun getSelectedAppsQueue(): MutableList<MainActivity.AppInfo>
        fun getAllAppsList(): MutableList<MainActivity.AppInfo>
        fun getDisplayList(): MutableList<Any>
        fun getActivePackageName(): String?
        fun setActivePackageName(pkg: String?)
        fun getLastValidPackageName(): String?
        fun setLastValidPackageName(pkg: String?)
        
        // Package constants
        fun getPackageBlank(): String
        fun getPackageTrackpad(): String
        
        // Mode flags
        fun isOpenMoveToMode(): Boolean
        fun setOpenMoveToMode(value: Boolean)
        fun isOpenSwapMode(): Boolean
        fun setOpenSwapMode(value: Boolean)
        fun getOpenMoveToApp(): MainActivity.AppInfo?
        fun setOpenMoveToApp(app: MainActivity.AppInfo?)
        fun isShowSlotNumbersInQueue(): Boolean
        fun setShowSlotNumbersInQueue(value: Boolean)
        fun isProfileNameEditMode(): Boolean
        fun isLayoutNameEditMode(): Boolean
        
        // Command state
        fun getQueueCommandPending(): CommandDef?
        fun setQueueCommandPending(cmd: CommandDef?)
        fun getQueueCommandSourceIndex(): Int
        fun setQueueCommandSourceIndex(index: Int)
        fun getPendingCommandId(): String?
        fun getVqCursorIndex(): Int
        fun setVqCursorIndex(index: Int)
        fun getCustomModKey(): Int
        fun isCustomModLatched(): Boolean
        fun setCustomModLatched(value: Boolean)
        
        // Actions
        fun onToast(msg: String)
        fun onSendBroadcast(intent: Intent)
        fun onAddToSelection(app: MainActivity.AppInfo)
        fun onSelectLayout(layout: LayoutOption)
        fun onDismissKeyboardAndRestore()
        fun onApplyRefreshRate(rate: Float)
        fun onLoadProfile(name: String)
        fun onExecuteOpenMoveTo(slotNum: Int)
        fun onExecuteOpenSwap(slotNum: Int)
        fun onCancelOpenMoveToMode()
        fun onQueueWindowManagerCommand(intent: Intent)
        fun onAbortCommandMode()
        fun onShowVisualQueue(prompt: String, highlightIndex: Int)
        fun onHandleCommandInput(num: Int)
        fun onTriggerCommand(cmd: CommandDef)
        fun onUpdateAllUIs()
        fun onRunShellCommand(command: String)
        
        // Input handler access
        fun keyEventToNumber(keyCode: Int): Int
        fun checkModifiers(meta: Int, required: Int, latched: Boolean): Boolean
        
        // Commands and keybinds
        fun getAvailableCommands(): List<CommandDef>
        fun getKeybind(cmdId: String): Pair<Int, Int>
        
        // Visual queue
        fun getVisualQueuePromptText(): String
        
        // Debounce
        fun getLastQueueNavTime(): Long
        fun setLastQueueNavTime(time: Long)
        
        // Focus state (single source of truth in service)
        fun getCurrentFocusArea(): Int
        fun setCurrentFocusArea(area: Int)
        fun getQueueSelectedIndex(): Int
        fun setQueueSelectedIndex(index: Int)
        fun getSelectedListIndex(): Int
        fun setSelectedListIndex(index: Int)
    }

    // View state
    var drawerView: View? = null
        private set
    private var drawerParams: WindowManager.LayoutParams? = null
    var debugStatusView: TextView? = null
        private set
    
    fun setDrawerView(view: View?) {
        drawerView = view
    }
    
    fun setDebugStatusView(view: TextView?) {
        debugStatusView = view
    }

    // Navigation state - delegated to service via callbacks
    val currentFocusArea: Int get() = callback?.getCurrentFocusArea() ?: FOCUS_SEARCH
    val queueSelectedIndex: Int get() = callback?.getQueueSelectedIndex() ?: -1
    val selectedListIndex: Int get() = callback?.getSelectedListIndex() ?: 0

    private var callback: Callback? = null

    fun setCallback(cb: Callback) {
        callback = cb
    }

    /**
     * Handle remote key events for drawer navigation
     */
    fun handleRemoteKeyEvent(keyCode: Int, metaState: Int) {
        val cb = callback ?: return
        
        val keyName = KeyEvent.keyCodeToString(keyCode)
        Log.d("DroidOS_Keys", "DRAWER: handleRemoteKeyEvent called with $keyCode (Meta: $metaState)")

        // CHECK CUSTOM MODIFIER (Remote/Broadcast)
        val customModKey = cb.getCustomModKey()
        if (customModKey != 0) {
            if (keyCode == customModKey) {
                cb.setCustomModLatched(true)
                cb.onToast("Custom Mod Active (Remote)")
                return
            }
        }

        // 0. DRAWER SEARCH NAVIGATION
        if (cb.isExpanded() && currentFocusArea == FOCUS_SEARCH) {
            val now = System.currentTimeMillis()
            if (now - cb.getLastQueueNavTime() < 80) return
            cb.setLastQueueNavTime(now)
            
            // Skip Enter/DPAD handling when editing names
            if ((cb.isProfileNameEditMode() || cb.isLayoutNameEditMode()) &&
                (keyCode == KeyEvent.KEYCODE_ENTER ||
                 keyCode == KeyEvent.KEYCODE_DEL ||
                 keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                 keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
                 keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                 keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                return
            }
            
            // ENTER: Activate first filtered result
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                val displayList = cb.getDisplayList()
                if (displayList.isNotEmpty()) {
                    val item = displayList[0]
                    when (item) {
                        is MainActivity.AppInfo -> cb.onAddToSelection(item)
                        is LayoutOption -> cb.onSelectLayout(item)
                        is ActionOption -> { cb.onDismissKeyboardAndRestore(); item.action() }
                        is ToggleOption -> { 
                            item.isEnabled = !item.isEnabled
                            item.onToggle(item.isEnabled)
                            drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
                        }
                        is RefreshItemOption -> if (item.isAvailable) { cb.onDismissKeyboardAndRestore(); cb.onApplyRefreshRate(item.targetRate) }
                        is ProfileOption -> cb.onLoadProfile(item.name)
                        is ResolutionOption -> { cb.onDismissKeyboardAndRestore(); cb.onRunShellCommand(item.command); cb.onToast("Applied: ${item.name}") }
                    }
                    return
                }
            }
            
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                val selectedRecycler = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)
                if (cb.getSelectedAppsQueue().isNotEmpty()) {
                    setCurrentFocusArea(FOCUS_QUEUE)
                    setQueueSelectedIndex(0)
                    val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                    captureIntent.setPackage(cb.getPackageTrackpad())
                    captureIntent.putExtra("CAPTURE", true)
                    cb.onSendBroadcast(captureIntent)
                    uiHandler.post {
                        selectedRecycler?.adapter?.notifyDataSetChanged()
                        debugStatusView?.visibility = View.VISIBLE
                        if (!cb.isOpenMoveToMode() && !cb.isOpenSwapMode()) {
                            debugStatusView?.text = "Queue Navigation: Use Arrows / Hotkeys"
                        }
                    }
                } else {
                    setCurrentFocusArea(FOCUS_LIST)
                    val mainRecycler = drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)
                    val displayList = cb.getDisplayList()
                    uiHandler.post {
                        if (displayList.isNotEmpty()) {
                            if (selectedListIndex >= displayList.size) setSelectedListIndex(0)
                            mainRecycler?.adapter?.notifyItemChanged(selectedListIndex)
                            mainRecycler?.scrollToPosition(selectedListIndex)
                        }
                    }
                }
                return
            }
        }

        // Continue with queue and list navigation...
        // (Will be filled in when we move the full method)
        
        // For now, delegate remaining logic back to service
        handleQueueNavigation(keyCode, metaState)
    }

    private fun handleQueueNavigation(keyCode: Int, metaState: Int) {
        val cb = callback ?: return
        
        // 0.1 DRAWER QUEUE NAVIGATION
        if (cb.isExpanded() && currentFocusArea == FOCUS_QUEUE) {
            val now = System.currentTimeMillis()
            if (now - cb.getLastQueueNavTime() < 80) return
            cb.setLastQueueNavTime(now)
            
            val selectedRecycler = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (queueSelectedIndex > 0) {
                        val old = queueSelectedIndex
                        setQueueSelectedIndex(queueSelectedIndex - 1)
                        uiHandler.post {
                            selectedRecycler?.adapter?.notifyItemChanged(old)
                            selectedRecycler?.adapter?.notifyItemChanged(queueSelectedIndex)
                            selectedRecycler?.scrollToPosition(queueSelectedIndex)
                        }
                    }
                    return
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    if (queueSelectedIndex < cb.getSelectedAppsQueue().size - 1) {
                        val old = queueSelectedIndex
                        setQueueSelectedIndex(queueSelectedIndex + 1)
                        uiHandler.post {
                            selectedRecycler?.adapter?.notifyItemChanged(old)
                            selectedRecycler?.adapter?.notifyItemChanged(queueSelectedIndex)
                            selectedRecycler?.scrollToPosition(queueSelectedIndex)
                        }
                    }
                    return
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if ((cb.isOpenMoveToMode() || cb.isOpenSwapMode()) && cb.getOpenMoveToApp() != null) {
                        return
                    }
                    setCurrentFocusArea(FOCUS_SEARCH)
                    setQueueSelectedIndex(-1)
                    val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                    captureIntent.setPackage(cb.getPackageTrackpad())
                    captureIntent.putExtra("CAPTURE", false)
                    cb.onSendBroadcast(captureIntent)
                    uiHandler.post {
                        selectedRecycler?.adapter?.notifyDataSetChanged()
                        if (!cb.isOpenMoveToMode() && !cb.isOpenSwapMode()) debugStatusView?.visibility = View.GONE
                        drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.requestFocus()
                    }
                    return
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if ((cb.isOpenMoveToMode() || cb.isOpenSwapMode()) && cb.getOpenMoveToApp() != null) {
                        return
                    }
                    setCurrentFocusArea(FOCUS_LIST)
                    setQueueSelectedIndex(-1)
                    val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                    captureIntent.setPackage(cb.getPackageTrackpad())
                    captureIntent.putExtra("CAPTURE", false)
                    cb.onSendBroadcast(captureIntent)
                    uiHandler.post {
                        selectedRecycler?.adapter?.notifyDataSetChanged()
                        if (!cb.isOpenMoveToMode() && !cb.isOpenSwapMode()) debugStatusView?.visibility = View.GONE
                        val mainRecycler = drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)
                        val displayList = cb.getDisplayList()
                        if (displayList.isNotEmpty()) {
                            if (selectedListIndex >= displayList.size) setSelectedListIndex(0)
                            mainRecycler?.adapter?.notifyItemChanged(selectedListIndex)
                            mainRecycler?.scrollToPosition(selectedListIndex)
                        }
                    }
                    return
                }
                KeyEvent.KEYCODE_ENTER -> {
                    handleQueueEnter()
                    return
                }
                KeyEvent.KEYCODE_ESCAPE -> {
                    handleQueueEscape()
                    return
                }
                KeyEvent.KEYCODE_SPACE -> {
                    handleQueueSpace()
                    return
                }
                else -> {
                    handleQueueOtherKeys(keyCode)
                    return
                }
            }
        }

        // 0.5 DRAWER LIST NAVIGATION
        handleListNavigation(keyCode, metaState)
    }

    private fun handleQueueEnter() {
        val cb = callback ?: return
        val selectedRecycler = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)
        
        if (cb.isOpenMoveToMode() && cb.getOpenMoveToApp() != null && queueSelectedIndex >= 0) {
            cb.onExecuteOpenMoveTo(queueSelectedIndex + 1)
            return
        }
        if (cb.isOpenSwapMode() && cb.getOpenMoveToApp() != null && queueSelectedIndex >= 0) {
            cb.onExecuteOpenSwap(queueSelectedIndex + 1)
            return
        }
        
        val queueCommandPending = cb.getQueueCommandPending()
        if (queueCommandPending != null) {
            val intent = Intent()
                .putExtra("COMMAND", queueCommandPending.id)
                .putExtra("INDEX_A", cb.getQueueCommandSourceIndex() + 1)
                .putExtra("INDEX_B", queueSelectedIndex + 1)
            cb.onQueueWindowManagerCommand(intent)
            cb.setQueueCommandPending(null)
            cb.setQueueCommandSourceIndex(-1)
            uiHandler.post {
                debugStatusView?.text = "Command Executed"
                selectedRecycler?.adapter?.notifyDataSetChanged()
            }
        } else {
            val selectedAppsQueue = cb.getSelectedAppsQueue()
            if (queueSelectedIndex in selectedAppsQueue.indices) {
                val activeCount = selectedAppsQueue.count { !it.isMinimized }
                val cmd = if (queueSelectedIndex >= activeCount) "UNMINIMIZE" else "MINIMIZE"
                val intent = Intent().putExtra("COMMAND", cmd).putExtra("INDEX", queueSelectedIndex + 1)
                cb.onQueueWindowManagerCommand(intent)
            }
        }
    }

    private fun handleQueueEscape() {
        val cb = callback ?: return
        val selectedRecycler = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)
        
        if (cb.isOpenMoveToMode() || cb.isOpenSwapMode()) {
            cb.onCancelOpenMoveToMode()
            cb.onToast("Open & Move To cancelled")
            val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
            captureIntent.setPackage(cb.getPackageTrackpad())
            captureIntent.putExtra("CAPTURE", false)
            cb.onSendBroadcast(captureIntent)
            return
        }
        
        setCurrentFocusArea(FOCUS_SEARCH)
        setQueueSelectedIndex(-1)
        val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
        captureIntent.setPackage(cb.getPackageTrackpad())
        captureIntent.putExtra("CAPTURE", false)
        cb.onSendBroadcast(captureIntent)
        uiHandler.post {
            selectedRecycler?.adapter?.notifyDataSetChanged()
            if (!cb.isOpenMoveToMode() && !cb.isOpenSwapMode()) debugStatusView?.visibility = View.GONE
        }
    }

    private fun handleQueueSpace() {
        val cb = callback ?: return
        
        if (cb.isOpenMoveToMode() && cb.getOpenMoveToApp() != null && queueSelectedIndex >= 0) {
            cb.onExecuteOpenMoveTo(queueSelectedIndex + 1)
            return
        }
        if (cb.isOpenSwapMode() && cb.getOpenMoveToApp() != null && queueSelectedIndex >= 0) {
            cb.onExecuteOpenSwap(queueSelectedIndex + 1)
            return
        }
        
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        if (cb.getQueueCommandPending() == null && queueSelectedIndex in selectedAppsQueue.indices) {
            val app = selectedAppsQueue[queueSelectedIndex]
            if (app.packageName != cb.getPackageBlank()) {
                val pkg = app.packageName
                val isGemini = pkg == "com.google.android.apps.bard"
                val activeIsGoogle = cb.getActivePackageName() == "com.google.android.googlequicksearchbox"
                val isActive = (cb.getActivePackageName() == pkg) || (isGemini && activeIsGoogle)
                if (isActive) {
                    cb.setActivePackageName(null)
                } else {
                    val active = cb.getActivePackageName()
                    if (active != null) cb.setLastValidPackageName(active)
                    cb.setActivePackageName(pkg)
                }
                uiHandler.post { cb.onUpdateAllUIs() }
            }
        }
    }

    private fun handleQueueOtherKeys(keyCode: Int) {
        val cb = callback ?: return
        val selectedRecycler = drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        
        // OPEN_MOVE_TO/OPEN_SWAP: Handle number keys
        if ((cb.isOpenMoveToMode() || cb.isOpenSwapMode()) && cb.getOpenMoveToApp() != null) {
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
                if (cb.isOpenMoveToMode()) cb.onExecuteOpenMoveTo(num) else cb.onExecuteOpenSwap(num)
                return
            }
        }
        
        // Check for command keybinds
        for (cmd in cb.getAvailableCommands()) {
            val bind = cb.getKeybind(cmd.id)
            if (bind.second == keyCode && keyCode != KeyEvent.KEYCODE_SPACE) {
                if (cmd.argCount == 2) {
                    cb.setQueueCommandPending(cmd)
                    cb.setQueueCommandSourceIndex(queueSelectedIndex)
                    uiHandler.post {
                        debugStatusView?.text = "${cmd.label}: Select Target & Press Enter"
                        selectedRecycler?.adapter?.notifyDataSetChanged()
                    }
                } else {
                    val intent = Intent().putExtra("COMMAND", cmd.id).putExtra("INDEX", queueSelectedIndex + 1)
                    cb.onQueueWindowManagerCommand(intent)
                }
                return
            }
        }
    }

    private fun handleListNavigation(keyCode: Int, metaState: Int) {
        val cb = callback ?: return
        
        if (!cb.isExpanded() || currentFocusArea != FOCUS_LIST) {
            // Handle visual queue and trigger modes
            handleVisualQueueAndTrigger(keyCode, metaState)
            return
        }
        
        val now = System.currentTimeMillis()
        if (now - cb.getLastQueueNavTime() < 80) return
        cb.setLastQueueNavTime(now)
        
        val mainRecycler = drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)
        val displayList = cb.getDisplayList()
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (selectedListIndex > 0) {
                    val old = selectedListIndex
                    setSelectedListIndex(selectedListIndex - 1)
                    uiHandler.post {
                        mainRecycler?.adapter?.notifyItemChanged(old)
                        mainRecycler?.adapter?.notifyItemChanged(selectedListIndex)
                        mainRecycler?.scrollToPosition(selectedListIndex)
                    }
                } else if (selectedAppsQueue.isNotEmpty()) {
                    setCurrentFocusArea(FOCUS_QUEUE)
                    setQueueSelectedIndex(0)
                    val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
                    captureIntent.setPackage(cb.getPackageTrackpad())
                    captureIntent.putExtra("CAPTURE", true)
                    cb.onSendBroadcast(captureIntent)
                    uiHandler.post {
                        drawerView?.findViewById<RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
                        debugStatusView?.visibility = View.VISIBLE
                        if (!cb.isOpenMoveToMode() && !cb.isOpenSwapMode()) {
                            debugStatusView?.text = "Queue Navigation"
                        }
                    }
                } else {
                    setCurrentFocusArea(FOCUS_SEARCH)
                    uiHandler.post {
                        drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.requestFocus()
                    }
                }
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (selectedListIndex < displayList.size - 1) {
                    val old = selectedListIndex
                    setSelectedListIndex(selectedListIndex + 1)
                    uiHandler.post {
                        mainRecycler?.adapter?.notifyItemChanged(old)
                        mainRecycler?.adapter?.notifyItemChanged(selectedListIndex)
                        mainRecycler?.scrollToPosition(selectedListIndex)
                    }
                }
            }
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SPACE -> {
                uiHandler.post {
                    val holder = mainRecycler?.findViewHolderForAdapterPosition(selectedListIndex)
                    holder?.itemView?.performClick()
                }
            }
        }
    }

    private fun handleVisualQueueAndTrigger(keyCode: Int, metaState: Int) {
        val cb = callback ?: return
        
        // 1. INPUT MODE (Visual Queue)
        if (cb.getPendingCommandId() != null) {
            if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                cb.onAbortCommandMode()
                return
            }

            val vqCursorIndex = cb.getVqCursorIndex()
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (vqCursorIndex > 0) {
                    cb.setVqCursorIndex(vqCursorIndex - 1)
                    uiHandler.post { cb.onShowVisualQueue(cb.getVisualQueuePromptText(), vqCursorIndex - 1) }
                }
                return
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (vqCursorIndex < cb.getSelectedAppsQueue().size - 1) {
                    cb.setVqCursorIndex(vqCursorIndex + 1)
                    uiHandler.post { cb.onShowVisualQueue(cb.getVisualQueuePromptText(), vqCursorIndex + 1) }
                }
                return
            }
            if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                cb.onHandleCommandInput(vqCursorIndex + 1)
                return
            }

            val num = cb.keyEventToNumber(keyCode)
            if (num != -1) {
                cb.onHandleCommandInput(num)
                return
            }

            cb.onAbortCommandMode()
            return
        }

        // 2. TRIGGER MODE (Detecting Hotkeys)
        var commandTriggered = false

        for (cmd in cb.getAvailableCommands()) {
            val bind = cb.getKeybind(cmd.id)
            if (bind.second != 0 && bind.second == keyCode) {
                if (cb.checkModifiers(metaState, bind.first, cb.isCustomModLatched())) {
                    cb.onTriggerCommand(cmd)
                    commandTriggered = true
                    break
                }
            }
        }

        if (cb.isCustomModLatched()) {
            cb.setCustomModLatched(false)
            cb.onToast(if (commandTriggered) "Command Executed" else "Command Cancelled")
        }
    }

    // State setters delegate to service
    fun setCurrentFocusArea(area: Int) {
        callback?.setCurrentFocusArea(area)
    }

    fun setQueueSelectedIndex(index: Int) {
        callback?.setQueueSelectedIndex(index)
    }

    fun setSelectedListIndex(index: Int) {
        callback?.setSelectedListIndex(index)
    }

    fun cleanup() {
        drawerView = null
        debugStatusView = null
    }
}
