package com.katsuyamaki.DroidOSLauncher

import android.content.Intent
import android.graphics.Rect
import android.util.Log
import java.util.Collections
import java.util.Locale

/**
 * Processes window manager commands (SWAP, MINIMIZE, KILL, etc).
 * Extracts handleWindowManagerCommand from FloatingLauncherService.
 */
class LauncherCommandProcessor {
    companion object {
        private const val TAG = "LauncherCommandProcessor"
    }

    interface Callback {
        // State getters
        fun getSelectedAppsQueue(): MutableList<MainActivity.AppInfo>
        fun getAllAppsList(): List<MainActivity.AppInfo>
        fun getActivePackageName(): String?
        fun setActivePackageName(pkg: String?)
        fun getLastValidPackageName(): String?
        fun setLastValidPackageName(pkg: String?)
        fun getSecondLastValidPackageName(): String?
        fun setSecondLastValidPackageName(pkg: String?)
        fun getCurrentDisplayId(): Int
        fun getShellService(): IShellService?
        fun getPackageName(): String
        fun getPackageBlank(): String
        fun getPackageTrackpad(): String
        fun isBound(): Boolean
        fun isExpanded(): Boolean
        fun getReorderSelectionIndex(): Int
        fun setReorderSelectionIndex(index: Int)
        fun getManualStateOverrides(): MutableMap<String, Long>
        fun getMinimizedAtTimestamps(): MutableMap<String, Long>
        fun getRecentlyRemovedPackages(): MutableMap<String, Long>
        fun isTiledAppsAutoMinimized(): Boolean
        fun setTiledAppsAutoMinimized(value: Boolean)
        fun getLastExplicitTiledLaunchAt(): Long
        fun setLastExplicitTiledLaunchAt(value: Long)
        fun getAvailableCommands(): List<CommandDef>
        
        // Actions
        fun onToast(msg: String)
        fun onTryBindShizukuIfPermitted()
        fun onQueueWindowManagerCommand(intent: Intent, cmd: String)
        fun onRefreshDisplayId()
        fun onEnsureQueueLoadedForCommands()
        fun onTriggerCommand(cmd: CommandDef)
        fun onGetLayoutRects(): List<Rect>
        fun onSetLayoutType(type: Int)
        fun onGetLayoutName(type: Int): String
        fun onRefreshQueueAndLayout(msg: String, focusPackage: String? = null, skipTiling: Boolean = false, forceRetile: Boolean = false, retileDelayMs: Long = 200L)
        fun onShowWallpaper()
        fun onToggleDrawer()
        fun onUpdateAllUIs()
        fun onRemoveFromFocusHistory(pkg: String)
        fun onFocusViaTask(pkg: String, bounds: Rect?)
        fun onLaunchViaShell(pkg: String, cls: String?, bounds: Rect?)
        fun onSendCursorToAppCenter(bounds: Rect?)
    }

    private var callback: Callback? = null

    fun setCallback(cb: Callback) {
        callback = cb
    }

    fun handleWindowManagerCommand(intent: Intent) {
        val cb = callback ?: return
        val cmd = intent.getStringExtra("COMMAND")?.uppercase(Locale.ROOT) ?: return

        if (!cb.isBound() || cb.getShellService() == null) {
            Log.w(TAG, "WM Command $cmd queued (Shizuku not bound)")
            cb.onTryBindShizukuIfPermitted()
            cb.onQueueWindowManagerCommand(intent, cmd)
            return
        }
        
        // [FIX] Sync display context before executing commands to prevent stale state
        cb.onRefreshDisplayId()
        cb.onEnsureQueueLoadedForCommands()
        
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        val PACKAGE_BLANK = cb.getPackageBlank()
        val currentDisplayId = cb.getCurrentDisplayId()
        val shellService = cb.getShellService()
        
        // CONVERT 1-BASED INDEX TO 0-BASED INTERNAL INDEX
        val rawIndex = intent.getIntExtra("INDEX", -1)
        val index = if (rawIndex > 0) rawIndex - 1 else -1

        Log.d(TAG, "WM Command: $cmd RawIdx: $rawIndex (Internal: $index)")

        when (cmd) {
            "OPEN_SWAP" -> {
                val cmdDef = cb.getAvailableCommands().find { it.id == "OPEN_SWAP" }
                if (cmdDef != null) cb.onTriggerCommand(cmdDef)
            }
            "OPEN_MOVE_TO" -> {
                val cmdDef = cb.getAvailableCommands().find { it.id == "OPEN_MOVE_TO" }
                if (cmdDef != null) cb.onTriggerCommand(cmdDef)
            }
            "SWAP" -> {
                val rawA = intent.getIntExtra("INDEX_A", -1)
                val rawB = intent.getIntExtra("INDEX_B", -1)
                val idxA = if (rawA > 0) rawA - 1 else -1
                val idxB = if (rawB > 0) rawB - 1 else -1
                
                if (idxA in selectedAppsQueue.indices && idxB in selectedAppsQueue.indices) {
                    val appA = selectedAppsQueue[idxA]
                    val appB = selectedAppsQueue[idxB]

                    val appsToMinimize = mutableListOf<MainActivity.AppInfo>()
                    val appsToRestore = mutableListOf<MainActivity.AppInfo>()

                    val aIsBlank = appA.packageName == PACKAGE_BLANK
                    val bIsBlank = appB.packageName == PACKAGE_BLANK
                    
                    if (!appA.isMinimized && !appB.isMinimized && (aIsBlank || bIsBlank)) {
                        if (!aIsBlank) {
                            appsToMinimize.add(appA)
                            appA.isMinimized = true
                        }
                        if (!bIsBlank) {
                            appsToMinimize.add(appB)
                            appB.isMinimized = true
                        }
                    } else if (appA.isMinimized != appB.isMinimized) {
                        if (!appA.isMinimized && !aIsBlank) appsToMinimize.add(appA)
                        if (!appB.isMinimized && !bIsBlank) appsToMinimize.add(appB)
                        if (appA.isMinimized && !aIsBlank) appsToRestore.add(appA)
                        if (appB.isMinimized && !bIsBlank) appsToRestore.add(appB)

                        val stateA = appA.isMinimized
                        appA.isMinimized = appB.isMinimized
                        appB.isMinimized = stateA
                    }

                    Collections.swap(selectedAppsQueue, idxA, idxB)

                    executeMinimize(appsToMinimize, cb)
                    executeRestore(appsToRestore, cb)

                    // Remove Inactive Blanks
                    val indicesToRemove = mutableListOf<Int>()
                    if (appA.packageName == PACKAGE_BLANK && appA.isMinimized) indicesToRemove.add(idxB)
                    if (appB.packageName == PACKAGE_BLANK && appB.isMinimized) indicesToRemove.add(idxA)
                    
                    for (i in indicesToRemove.sortedDescending()) {
                        selectedAppsQueue.removeAt(i)
                    }

                    cb.onRefreshQueueAndLayout("Swapped slots $rawA & $rawB", forceRetile = true, retileDelayMs = 300L)
                }
            }
            "MOVE_TO" -> {
                val rawA = intent.getIntExtra("INDEX_A", -1)
                val rawB = intent.getIntExtra("INDEX_B", -1)
                val srcIdx = if (rawA > 0) rawA - 1 else -1
                val dstIdx = if (rawB > 0) rawB - 1 else -1
                
                if (srcIdx == dstIdx) {
                    cb.onToast("Source and destination are the same")
                    return
                }
                
                if (srcIdx in selectedAppsQueue.indices && dstIdx in selectedAppsQueue.indices) {
                    val movingApp = selectedAppsQueue[srcIdx]
                    val movingIsBlank = movingApp.packageName == PACKAGE_BLANK
                    
                    val rects = cb.onGetLayoutRects()
                    val activeSlotCount = rects.size
                    
                    val appsToMinimize = mutableListOf<MainActivity.AppInfo>()
                    val appsToRestore = mutableListOf<MainActivity.AppInfo>()
                    
                    selectedAppsQueue.removeAt(srcIdx)
                    val adjustedDstIdx = if (srcIdx < dstIdx) dstIdx - 1 else dstIdx
                    selectedAppsQueue.add(adjustedDstIdx, movingApp)
                    
                    for (i in selectedAppsQueue.indices) {
                        val app = selectedAppsQueue[i]
                        if (app.packageName == PACKAGE_BLANK) continue
                        
                        val isInActiveSlot = i < activeSlotCount
                        
                        if (isInActiveSlot && app.isMinimized) {
                            app.isMinimized = false
                            appsToRestore.add(app)
                        } else if (!isInActiveSlot && !app.isMinimized) {
                            app.isMinimized = true
                            appsToMinimize.add(app)
                        }
                    }
                    
                    executeMinimize(appsToMinimize, cb)
                    executeRestore(appsToRestore, cb)
                    
                    // Remove inactive blanks
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
                    cb.onRefreshQueueAndLayout("Moved $appName to slot $rawB", forceRetile = true, retileDelayMs = 300L)
                }
            }
            "SWAP_ACTIVE_LEFT", "SWAP_ACTIVE_RIGHT" -> {
                val activePackageName = cb.getActivePackageName()
                if (activePackageName == null) {
                    cb.onToast("No active window detected")
                    return
                }
                
                val activeIdx = selectedAppsQueue.indexOfFirst { 
                    it.packageName == activePackageName || 
                    (it.packageName == "com.google.android.apps.bard" && activePackageName == "com.google.android.googlequicksearchbox") 
                }

                if (activeIdx != -1) {
                    val dir = if (cmd == "SWAP_ACTIVE_LEFT") -1 else 1
                    val targetIdx = activeIdx + dir

                    if (targetIdx in selectedAppsQueue.indices) {
                        Collections.swap(selectedAppsQueue, activeIdx, targetIdx)
                        cb.onRefreshQueueAndLayout("Moved Active Window ${if(dir<0) "Left" else "Right"}", activePackageName)
                    } else {
                        cb.onToast("Edge of layout reached")
                    }
                } else {
                    cb.onToast("Active app not in layout")
                }
            }
            "MINIMIZE_ALL" -> {
                val appsToMinimize = selectedAppsQueue.filter { 
                    !it.isMinimized && it.packageName != PACKAGE_BLANK 
                }
                
                if (appsToMinimize.isEmpty()) {
                    cb.onToast("No active tiled apps")
                    return
                }
                
                cb.setActivePackageName(null)
                
                for (app in appsToMinimize) {
                    app.isMinimized = true
                    val basePkg = app.getBasePackage()
                    cb.getManualStateOverrides()[basePkg] = System.currentTimeMillis()
                    cb.getMinimizedAtTimestamps()[basePkg] = System.currentTimeMillis()
                }
                
                executeMinimize(appsToMinimize, cb)
                
                for (app in selectedAppsQueue) {
                    if (app.packageName == PACKAGE_BLANK && !app.isMinimized) {
                        app.isMinimized = true
                    }
                }
                
                cb.onRefreshQueueAndLayout("Minimized ${appsToMinimize.size} apps")
            }
            "RESTORE_ALL" -> {
                val layoutRects = cb.onGetLayoutRects()
                val maxSlots = layoutRects.size
                
                val currentActiveCount = selectedAppsQueue.count { !it.isMinimized }
                val availableSlots = maxSlots - currentActiveCount
                
                if (availableSlots <= 0) {
                    cb.onToast("No available slots (layout: $maxSlots)")
                    return
                }
                
                val minimizedApps = selectedAppsQueue.filter { 
                    it.isMinimized && it.packageName != PACKAGE_BLANK 
                }.sortedByDescending { cb.getMinimizedAtTimestamps()[it.getBasePackage()] ?: 0L }
                
                if (minimizedApps.isEmpty()) {
                    cb.onToast("No minimized apps to restore")
                    return
                }
                
                val appsToRestore = minimizedApps.take(availableSlots)
                
                for (app in appsToRestore) {
                    app.isMinimized = false
                    val basePkg = app.getBasePackage()
                    cb.getManualStateOverrides()[basePkg] = System.currentTimeMillis()
                    cb.getMinimizedAtTimestamps().remove(basePkg)
                }
                
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
                
                cb.setTiledAppsAutoMinimized(false)
                cb.setLastExplicitTiledLaunchAt(System.currentTimeMillis())
                
                cb.onRefreshQueueAndLayout("Restored ${appsToRestore.size} apps", forceRetile = true, retileDelayMs = 300L)
            }
            "KILL" -> {
                if (index in selectedAppsQueue.indices) {
                    val app = selectedAppsQueue[index]
                    if (app.packageName != PACKAGE_BLANK) {
                        val basePkg = app.getBasePackage()
                        cb.getRecentlyRemovedPackages()[basePkg] = System.currentTimeMillis()
                        cb.onRemoveFromFocusHistory(basePkg)
                        Thread { try { cb.getShellService()?.forceStop(basePkg) } catch(e: Exception){} }.start()
                    }
                    selectedAppsQueue.removeAt(index)
                    
                    val reorderIdx = cb.getReorderSelectionIndex()
                    if (reorderIdx == index) cb.setReorderSelectionIndex(-1)
                    else if (reorderIdx > index) cb.setReorderSelectionIndex(reorderIdx - 1)
                    
                    cb.onRefreshQueueAndLayout("Closed ${app.label}")
                }
            }
            "MINIMIZE", "UNMINIMIZE", "TOGGLE_MINIMIZE" -> {
                handleMinimizeCommand(cmd, index, cb)
            }
            "HIDE" -> {
                handleHideCommand(index, cb)
            }
            "LAYOUT" -> {
                val type = intent.getIntExtra("TYPE", -1)
                if (type != -1) {
                    cb.onSetLayoutType(type)
                    cb.onRefreshQueueAndLayout("Layout: ${cb.onGetLayoutName(type)}")
                }
            }
            "CLEAR_ALL" -> {
                cb.setActivePackageName(null)
                cb.setLastValidPackageName(null)
                cb.setSecondLastValidPackageName(null)
                selectedAppsQueue.clear()
                cb.onRefreshQueueAndLayout("Cleared All")
            }
            "OPEN_DRAWER" -> {
                Log.d(TAG, "OPEN_DRAWER command received, calling toggleDrawer()")
                cb.onToggleDrawer()
                cb.onRefreshQueueAndLayout("Toggled Drawer", skipTiling = true)
            }
            "SET_FOCUS" -> {
                handleSetFocusCommand(index, cb)
            }
            "FOCUS_LAST" -> {
                handleFocusLastCommand(cb)
            }
        }
    }

    private fun handleMinimizeCommand(cmd: String, index: Int, cb: Callback) {
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        val PACKAGE_BLANK = cb.getPackageBlank()
        val currentDisplayId = cb.getCurrentDisplayId()
        val shellService = cb.getShellService()
        val allAppsList = cb.getAllAppsList()
        
        if (index !in selectedAppsQueue.indices) return
        
        val app = selectedAppsQueue[index]

        // SPECIAL BLANK LOGIC: Hide/Minimize = Delete
        if (app.packageName == PACKAGE_BLANK) {
            if (cmd == "MINIMIZE" || (cmd == "TOGGLE_MINIMIZE" && !app.isMinimized)) {
                selectedAppsQueue.removeAt(index)
                cb.onRefreshQueueAndLayout("Removed Blank Space")
                return
            }
        }

        // Auto-minimized state sync
        if (cb.isTiledAppsAutoMinimized() && (cmd == "UNMINIMIZE" || cmd == "TOGGLE_MINIMIZE") && !app.isMinimized) {
            cb.setTiledAppsAutoMinimized(false)
            cb.setLastExplicitTiledLaunchAt(System.currentTimeMillis())
            for (other in selectedAppsQueue) {
                if (other !== app && !other.isMinimized && other.packageName != PACKAGE_BLANK) {
                    other.isMinimized = true
                    cb.getMinimizedAtTimestamps()[other.getBasePackage()] = System.currentTimeMillis()
                }
            }
            Log.d(TAG, "WM Command: Auto-minimize state synced, restoring ${app.label} to slot 1")
            cb.onRefreshQueueAndLayout("Restored ${app.label}", forceRetile = true, retileDelayMs = 300L)
            return
        }

        // Force bring-to-front for UNMINIMIZE on already non-minimized app
        if (cmd == "UNMINIMIZE" && !app.isMinimized) {
            Log.d(TAG, "WM Command: ${app.label} already not minimized, forcing bring-to-front")
            cb.setLastExplicitTiledLaunchAt(System.currentTimeMillis())
            val basePkg = app.getBasePackage()
            val cls = app.className
            Thread {
                try {
                    val component = if (!cls.isNullOrEmpty() && cls != "null" && cls != "default") "$basePkg/$cls" else null
                    val cmdStr = if (component != null) {
                        "am start -n $component --display $currentDisplayId --windowingMode 5 --user 0"
                    } else {
                        "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"
                    }
                    shellService?.runCommand(cmdStr)
                } catch (e: Exception) {
                    Log.e(TAG, "Force bring-to-front failed", e)
                }
            }.start()
            cb.onRefreshQueueAndLayout("Restored ${app.label}", forceRetile = true, retileDelayMs = 350L)
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
            cb.getManualStateOverrides()[basePkg] = System.currentTimeMillis()
            
            if (newState) {
                cb.getMinimizedAtTimestamps()[basePkg] = System.currentTimeMillis()
            } else {
                cb.getMinimizedAtTimestamps().remove(basePkg)
            }
            
            val cls = app.className
            
            if (newState) {
                // Clear focus if minimizing active app
                val activePackageName = cb.getActivePackageName()
                val isGemini = basePkg == "com.google.android.apps.bard"
                val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"
                
                if (activePackageName == basePkg || 
                    activePackageName == app.packageName ||
                    (isGemini && activeIsGoogle)) {
                    cb.setActivePackageName(null)
                }
                
                // MINIMIZING: Move to Back
                Thread {
                    try {
                        if (currentDisplayId >= 2) {
                            val visibleCount = shellService?.getVisiblePackages(currentDisplayId)?.size ?: 0
                            val isLastApp = visibleCount <= 1 
                            if (isLastApp) {
                                cb.onShowWallpaper()
                            } else {
                                val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                                if (tid != -1) shellService?.moveTaskToBack(tid)
                            }
                        } else {
                            val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                            if (tid != -1) shellService?.moveTaskToBack(tid)
                        }
                    } catch(e: Exception){}
                }.start()
            } else {
                // RESTORING: Bring to Front
                cb.setTiledAppsAutoMinimized(false)
                cb.setLastExplicitTiledLaunchAt(System.currentTimeMillis())
                cb.getMinimizedAtTimestamps().remove(app.getBasePackage())

                // Detect fullscreen apps not in queue
                try {
                    val visiblePkgs = shellService?.getVisiblePackages(currentDisplayId) ?: emptyList()
                    val userVisibleApps = visiblePkgs.mapNotNull { pkgName ->
                        val pkg = if (pkgName.contains(":")) pkgName.substringBefore(":") else pkgName
                        if (pkg == cb.getPackageName() || pkg == cb.getPackageTrackpad() ||
                            pkg.contains("systemui") || pkg.contains("inputmethod") ||
                            pkg.startsWith("com.android.") || pkg.startsWith("com.samsung.")) null
                        else allAppsList.find { it.getBasePackage() == pkg }
                    }.filter { it.packageName != cb.getPackageName() && it.packageName != cb.getPackageTrackpad() }
                    
                    val queuePkgs = selectedAppsQueue.map { it.getBasePackage() }.toSet()
                    
                    if (userVisibleApps.size == 1) {
                        val fullscreenApp = userVisibleApps.first()
                        if (!queuePkgs.contains(fullscreenApp.getBasePackage())) {
                            fullscreenApp.isMinimized = false
                            selectedAppsQueue.add(0, fullscreenApp.copy())
                        }
                    }
                } catch (e: Exception) { Log.e(TAG, "Failed to detect fullscreen app", e) }
                
                // Multiple visible apps detection
                try {
                    val activeApps = shellService?.getVisiblePackages(currentDisplayId)
                        ?.mapNotNull { pkgName -> allAppsList.find { it.packageName == pkgName } }
                        ?.filter { it.packageName != cb.getPackageName() && it.packageName != cb.getPackageTrackpad() }
                        ?: emptyList()
                    val queuePkgs = selectedAppsQueue.map { it.getBasePackage() }.toSet()
                    val fsApps = activeApps.filter { it.getBasePackage() !in queuePkgs && !it.isMinimized }
                    if (fsApps.isNotEmpty() && selectedAppsQueue.any { !it.isMinimized || it == app }) {
                        for (fsApp in fsApps) {
                            selectedAppsQueue.add(fsApp.copy())
                            Log.d(TAG, "UNMINIMIZE: Added fullscreen app ${fsApp.packageName} to queue for tiling")
                        }
                        cb.onUpdateAllUIs()
                    }
                } catch (e: Exception) { Log.e(TAG, "UNMINIMIZE: Failed to detect fullscreen apps", e) }

                Thread {
                    try {
                        val rects = cb.onGetLayoutRects()
                        val bounds = if (index < rects.size) rects[index] else null

                        val component = if (!cls.isNullOrEmpty() && cls != "null" && cls != "default") "$basePkg/$cls" else null
                        val cmdStr = if (component != null) {
                            "am start -n $component --display $currentDisplayId --windowingMode 5 --user 0"
                        } else {
                            "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"
                        }
                        shellService?.runCommand(cmdStr)

                        if (bounds != null) {
                            Thread.sleep(300)
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
            cb.onRefreshQueueAndLayout(
                if (newState) "Minimized ${app.label}" else "Restored ${app.label}",
                forceRetile = true,
                retileDelayMs = retileDelay
            )
        }
    }

    private fun handleHideCommand(index: Int, cb: Callback) {
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        val PACKAGE_BLANK = cb.getPackageBlank()
        val shellService = cb.getShellService()
        
        if (index !in selectedAppsQueue.indices) return
        
        val targetApp = selectedAppsQueue[index]
        
        if (targetApp.packageName == PACKAGE_BLANK) {
            selectedAppsQueue.removeAt(index)
            cb.onRefreshQueueAndLayout("Removed Blank Space")
            return
        }
        
        val basePkg = targetApp.getBasePackage()
        val cls = targetApp.className
        
        cb.getRecentlyRemovedPackages()[basePkg] = System.currentTimeMillis()
        
        val activePackageName = cb.getActivePackageName()
        val isGemini = basePkg == "com.google.android.apps.bard"
        val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"

        if (activePackageName == basePkg || 
            activePackageName == targetApp.packageName ||
            (isGemini && activeIsGoogle)) {
            cb.setActivePackageName(null)
            Log.d(TAG, "WM Command: Cleared focus for hidden app: $basePkg")
        }

        Thread { 
            try { 
                val tid = shellService?.getTaskId(basePkg, cls) ?: -1
                if (tid != -1) shellService?.moveTaskToBack(tid)
            } catch(e: Exception){}
        }.start()
        
        targetApp.isMinimized = true
        
        val blankApp = MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null)
        selectedAppsQueue.add(blankApp)
        Collections.swap(selectedAppsQueue, index, selectedAppsQueue.lastIndex)
        
        cb.onRefreshQueueAndLayout("Hidden Slot ${index + 1}", forceRetile = true, retileDelayMs = 200L)
    }

    private fun handleSetFocusCommand(index: Int, cb: Callback) {
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        val PACKAGE_BLANK = cb.getPackageBlank()
        
        if (index !in selectedAppsQueue.indices) return
        
        val app = selectedAppsQueue[index]
        if (app.packageName == PACKAGE_BLANK) return
        
        if (cb.isExpanded()) {
            val activePackageName = cb.getActivePackageName()
            if (activePackageName != app.packageName) {
                if (activePackageName != null) cb.setLastValidPackageName(activePackageName)
                cb.setActivePackageName(app.packageName)
                cb.onUpdateAllUIs()
            }
        } else {
            val rects = cb.onGetLayoutRects()
            val layoutIdx = selectedAppsQueue.take(index).count { !it.isMinimized }
            val bounds = if (layoutIdx < rects.size) rects[layoutIdx] else null
            
            if (!app.isMinimized) {
                cb.onFocusViaTask(app.getBasePackage(), bounds)
            } else {
                Thread {
                    cb.onLaunchViaShell(app.getBasePackage(), app.className, bounds)
                }.start()
            }
            cb.onSendCursorToAppCenter(bounds)
            cb.onToast("Focused: ${app.label}")
        }
    }

    private fun handleFocusLastCommand(cb: Callback) {
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        val activePackageName = cb.getActivePackageName()
        val lastValidPackageName = cb.getLastValidPackageName()
        val secondLastValidPackageName = cb.getSecondLastValidPackageName()
        
        val target = if (lastValidPackageName == activePackageName) secondLastValidPackageName else lastValidPackageName

        if (target != null) {
            val app = selectedAppsQueue.find { it.packageName == target }
            if (app != null) {
                if (cb.isExpanded()) {
                    if (activePackageName != app.packageName) {
                        cb.setActivePackageName(app.packageName)
                        cb.setLastValidPackageName(target)
                        cb.onUpdateAllUIs()
                    }
                } else {
                    val idx = selectedAppsQueue.indexOf(app)
                    val rects = cb.onGetLayoutRects()
                    val layoutIdx = if (idx >= 0) selectedAppsQueue.take(idx).count { !it.isMinimized } else -1
                    val bounds = if (layoutIdx >= 0 && layoutIdx < rects.size) rects[layoutIdx] else null
                    
                    if (!app.isMinimized) {
                        cb.onFocusViaTask(app.getBasePackage(), bounds)
                    } else {
                        Thread {
                            cb.onLaunchViaShell(app.getBasePackage(), app.className, bounds)
                        }.start()
                    }
                    cb.onSendCursorToAppCenter(bounds)
                    cb.onToast("Focused: ${app.label}")
                }
            } else {
                cb.onToast("Last app not in layout")
            }
        } else {
            cb.onToast("No history found")
        }
    }

    private fun executeMinimize(apps: List<MainActivity.AppInfo>, cb: Callback) {
        if (apps.isEmpty()) return
        val currentDisplayId = cb.getCurrentDisplayId()
        val shellService = cb.getShellService()
        val activePackageName = cb.getActivePackageName()
        
        for (app in apps) {
            val basePkg = app.getBasePackage()
            val cls = app.className
            
            val isGemini = basePkg == "com.google.android.apps.bard"
            val activeIsGoogle = activePackageName == "com.google.android.googlequicksearchbox"
            if (activePackageName == basePkg || 
                activePackageName == app.packageName ||
                (isGemini && activeIsGoogle)) {
                cb.setActivePackageName(null)
            }
            
            cb.getManualStateOverrides()[basePkg] = System.currentTimeMillis()
            cb.getMinimizedAtTimestamps()[basePkg] = System.currentTimeMillis()
            
            Thread {
                try {
                    if (currentDisplayId >= 2) {
                        val visibleCount = shellService?.getVisiblePackages(currentDisplayId)?.size ?: 0
                        if (visibleCount <= 1) {
                            cb.onShowWallpaper()
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
    }
    
    private fun executeRestore(apps: List<MainActivity.AppInfo>, cb: Callback) {
        if (apps.isEmpty()) return
        val currentDisplayId = cb.getCurrentDisplayId()
        val shellService = cb.getShellService()
        
        for (app in apps) {
            val basePkg = app.getBasePackage()
            val cls = app.className
            
            cb.getManualStateOverrides()[basePkg] = System.currentTimeMillis()
            cb.getMinimizedAtTimestamps().remove(basePkg)
            
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
    }
}