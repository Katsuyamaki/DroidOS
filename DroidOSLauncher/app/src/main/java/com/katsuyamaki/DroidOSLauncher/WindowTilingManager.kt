package com.katsuyamaki.DroidOSLauncher

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import kotlin.math.max

/**
 * Manages window tiling, positioning, and launching via Shizuku shell service.
 * Extracted from FloatingLauncherService to separate window management logic.
 */
class WindowTilingManager(
    private val context: Context,
    private val displayManager: DisplayManager,
    private val uiHandler: Handler
) {
    companion object {
        private const val TAG = "WindowTilingManager"
        private const val REMOVED_PACKAGE_COOLDOWN_MS = 3000L
    }
    
    // Shell Service
    private var shellService: IShellService? = null
    
    // Caches
    private val packageRectCache = java.util.concurrent.ConcurrentHashMap<String, Rect>()
    private val packageTaskIdCache = java.util.concurrent.ConcurrentHashMap<String, Int>()
    private val activeEnforcements = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()
    private val recentlyRemovedPackages = mutableMapOf<String, Long>()
    
    // State
    var tiledAppsAutoMinimized = false
        private set
    var lastExplicitTiledLaunchAt = 0L
        private set
    
    // Config (set via updateConfig)
    private var currentDisplayId = 0
    private var topMarginPercent = 0
    private var bottomMarginPercent = 0
    private var effectiveBottomMarginPercent = 0
    private var selectedLayoutType = LAYOUT_FULL
    private var selectedResolutionIndex = 0
    private var currentOrientationMode = 0
    private var currentDpiSetting = -1
    private var lastAppliedResIndex = -1
    private var lastAppliedDpi = -1
    private var activeCustomRects: List<Rect>? = null
    
    // Callback interface for UI updates
    interface TilingCallback {
        fun onToast(msg: String)
        fun onUpdateAllUIs()
        fun getSelectedAppsQueue(): MutableList<MainActivity.AppInfo>
        fun getAllAppsList(): List<MainActivity.AppInfo>
        fun getTargetDimensions(resIndex: Int): Pair<Int, Int>?
        fun getResolutionCommand(resIndex: Int): String
        fun toggleDrawer()
        fun refreshDisplayId()
        fun saveQueueToPrefs(identifiers: List<String>)
        fun updateSelectedAppsDock()
        fun debugShowAppIdentification(tag: String, pkg: String, cls: String?)
        fun removeFromFocusHistory(pkg: String)
        fun getActivePackageName(): String?
        fun setActivePackageName(pkg: String?)
        fun getPackageBlank(): String
        fun getPackageTrackpad(): String
        fun getPackageName(): String
        fun isBound(): Boolean
    }
    
    private var callback: TilingCallback? = null
    
    fun setCallback(cb: TilingCallback) {
        callback = cb
    }
    
    fun setShellService(service: IShellService?) {
        shellService = service
    }
    
    fun updateConfig(
        displayId: Int,
        topMargin: Int,
        bottomMargin: Int,
        effectiveBottomMargin: Int,
        layoutType: Int,
        resIndex: Int,
        orientMode: Int,
        dpi: Int,
        customRects: List<Rect>?
    ) {
        currentDisplayId = displayId
        topMarginPercent = topMargin
        bottomMarginPercent = bottomMargin
        effectiveBottomMarginPercent = effectiveBottomMargin
        selectedLayoutType = layoutType
        selectedResolutionIndex = resIndex
        currentOrientationMode = orientMode
        currentDpiSetting = dpi
        activeCustomRects = customRects
    }
    
    fun markPackageRemoved(pkg: String) {
        recentlyRemovedPackages[pkg] = System.currentTimeMillis()
    }
    
    fun resetAutoMinimizedState() {
        tiledAppsAutoMinimized = false
        lastExplicitTiledLaunchAt = System.currentTimeMillis()
    }
    
    fun setAutoMinimized(value: Boolean) {
        tiledAppsAutoMinimized = value
    }
    
    fun getCachedRect(pkg: String): Rect? = packageRectCache[pkg]
    fun getCachedTaskId(pkg: String): Int? = packageTaskIdCache[pkg]
    
    // === LAYOUT RECTS CALCULATION ===
    fun getLayoutRects(): List<Rect> {
        val display = displayManager.getDisplay(currentDisplayId) ?: return emptyList()
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)
        var w = metrics.widthPixels
        var h = metrics.heightPixels
        
        val targetDim = callback?.getTargetDimensions(selectedResolutionIndex)
        if (targetDim != null) {
            w = targetDim.first
            h = targetDim.second
        }
        
        val topPx = (h * topMarginPercent / 100f).toInt()
        val bottomPx = (h * effectiveBottomMarginPercent / 100f).toInt()
        val effectiveH = max(100, h - topPx - bottomPx)
        
        val rects = mutableListOf<Rect>()
        when (selectedLayoutType) {
            LAYOUT_FULL -> rects.add(Rect(0, 0, w, effectiveH))
            LAYOUT_SIDE_BY_SIDE -> {
                rects.add(Rect(0, 0, w/2, effectiveH))
                rects.add(Rect(w/2, 0, w, effectiveH))
            }
            LAYOUT_TOP_BOTTOM -> {
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
                val splitY = (effectiveH * 0.75f).toInt()
                val midW = w / 2
                rects.add(Rect(0, 0, midW, splitY))
                rects.add(Rect(midW, 0, w, splitY))
                rects.add(Rect(0, splitY, midW, effectiveH))
                rects.add(Rect(midW, splitY, w, effectiveH))
            }
            LAYOUT_HEX_TALL_SHORT -> {
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
                    rects.addAll(activeCustomRects!!)
                } else {
                    rects.add(Rect(0, 0, w/2, effectiveH))
                    rects.add(Rect(w/2, 0, w, effectiveH))
                }
            }
        }
        
        if (topPx > 0 && selectedLayoutType != LAYOUT_CUSTOM_DYNAMIC) {
            for (r in rects) {
                r.offset(0, topPx)
            }
        }
        
        return rects
    }
    
    // === LAUNCH VIA SHELL ===
    fun launchViaShell(pkg: String, className: String?, bounds: Rect?) {
        try {
            val basePkg = if (pkg.contains(":")) pkg.substringBefore(":") else pkg
            callback?.debugShowAppIdentification("LAUNCH_SHELL", basePkg, className)
            
            val component = if (!className.isNullOrEmpty() && className != "null" && className != "default") {
                "$basePkg/$className"
            } else null
            
            val cmd = if (component != null) {
                "am start -n $component --display $currentDisplayId --windowingMode 5 --user 0"
            } else {
                "am start -p $basePkg -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"
            }
            
            Log.d(TAG, "launchViaShell: $cmd")
            Thread { try { shellService?.runCommand(cmd) } catch (e: Exception) { Log.e(TAG, "launchViaShell FAILED", e) } }.start()
        } catch (e: Exception) {
            Log.e(TAG, "launchViaShell FAILED: $pkg", e)
        }
    }
    
    // === FOCUS VIA TASK ===
    fun focusViaTask(pkg: String, bounds: Rect?) {
        Thread {
            try {
                val basePkg = if (pkg.contains(":")) pkg.substringBefore(":") else pkg
                val tid = shellService?.getTaskId(basePkg, null) ?: -1
                if (tid != -1) {
                    shellService?.moveTaskToFront(tid)
                    Log.d(TAG, "focusViaTask: SUCCESS tid=$tid pkg=$basePkg")
                } else {
                    Log.w(TAG, "focusViaTask: task not found, falling back to launchViaShell")
                    launchViaShell(basePkg, null, bounds)
                }
            } catch (e: Exception) {
                Log.e(TAG, "focusViaTask FAILED: $pkg", e)
            }
        }.start()
    }
    
    // === RETILE EXISTING WINDOWS ===
    fun retileExistingWindows() {
        if (callback?.isBound() != true || shellService == null) return
        Thread {
            try {
                val rects = getLayoutRects()
                val queue = callback?.getSelectedAppsQueue() ?: return@Thread
                val activeApps = queue.filter { !it.isMinimized }
                val packages = mutableListOf<String>()
                val boundsList = mutableListOf<Int>()
                
                for (i in 0 until minOf(activeApps.size, rects.size)) {
                    val app = activeApps[i]
                    if (app.packageName == callback?.getPackageBlank()) continue
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
    
    // === FORCE APP TO DISPLAY (Watchdog) ===
    fun forceAppToDisplay(pkg: String, targetDisplayId: Int) {
        if (activeEnforcements.contains(pkg)) {
            Log.d("DROIDOS_WATCHDOG", "SKIP: Enforcement already active for $pkg")
            return
        }
        activeEnforcements.add(pkg)
        
        Thread {
            try {
                var tid = shellService?.getTaskId(pkg, null) ?: -1
                if (tid == -1) {
                    Thread.sleep(200)
                    tid = shellService?.getTaskId(pkg, null) ?: -1
                }
                
                if (tid != -1) {
                    Log.w("DROIDOS_WATCHDOG", "Targeting Task $tid for $pkg")
                    
                    var bounds: Rect? = packageRectCache[pkg]
                    var className: String? = null
                    
                    val queue = callback?.getSelectedAppsQueue()
                    val appEntry = queue?.find { it.packageName == pkg || it.getBasePackage() == pkg }
                    if (appEntry != null) {
                        className = appEntry.className
                        if (bounds == null) {
                            val appIndex = queue.indexOf(appEntry)
                            val rects = getLayoutRects()
                            if (appIndex >= 0 && appIndex < rects.size) bounds = rects[appIndex]
                        }
                    }
                    
                    for (i in 1..10) {
                        shellService?.runCommand("am task move-task-to-display $tid $targetDisplayId")
                        shellService?.runCommand("am task set-windowing-mode $tid 5")
                        
                        if (i > 3 && i <= 6 && className != null) {
                            val cmd = "am start -n $pkg/$className --display $targetDisplayId --windowingMode 5 -f 0x10000000 --user 0"
                            Log.w("DROIDOS_WATCHDOG", "Escalating L1: $cmd")
                            shellService?.runCommand(cmd)
                        }
                        
                        if (i > 6 && className != null) {
                            Log.e("DROIDOS_WATCHDOG", "Escalating L2 (Nuclear): Killing $pkg")
                            shellService?.forceStop(pkg)
                            Thread.sleep(300)
                            val cmd = "am start -n $pkg/$className --display $targetDisplayId --windowingMode 5 -f 0x10000000 --user 0"
                            shellService?.runCommand(cmd)
                        }
                        
                        if (bounds != null) {
                            shellService?.runCommand("am task resize $tid ${bounds.left} ${bounds.top} ${bounds.right} ${bounds.bottom}")
                        } else {
                            shellService?.runCommand("am task resize $tid 0 0 1000 1000")
                        }
                        
                        val visibleOnTarget = shellService?.getVisiblePackages(targetDisplayId)?.contains(pkg) == true
                        if (visibleOnTarget) {
                            Log.w("DROIDOS_WATCHDOG", "SUCCESS on attempt $i")
                            if (bounds != null) shellService?.runCommand("am task resize $tid ${bounds.left} ${bounds.top} ${bounds.right} ${bounds.bottom}")
                            break
                        }
                        
                        Thread.sleep(250)
                    }
                }
            } catch (e: Exception) {
                Log.e("DROIDOS_WATCHDOG", "forceAppToDisplay failed for $pkg", e)
            } finally {
                activeEnforcements.remove(pkg)
            }
        }.start()
    }
    
    // === SHOW WALLPAPER ===
    fun showWallpaper() {
        Thread {
            try {
                shellService?.runCommand("am start -n com.android.wallpaper/.picker.WallpaperPickerActivity --display $currentDisplayId --windowingMode 5 --user 0")
            } catch (e: Exception) {
                Log.e(TAG, "showWallpaper failed", e)
            }
        }.start()
    }
    
    // === GET VISIBLE PACKAGES ===
    fun getVisiblePackages(displayId: Int): List<String>? {
        return shellService?.getVisiblePackages(displayId)
    }
    
    // === MOVE TASK TO BACK ===
    fun moveTaskToBack(pkg: String, className: String?) {
        Thread {
            try {
                val basePkg = if (pkg.contains(":")) pkg.substringBefore(":") else pkg
                val tid = shellService?.getTaskId(basePkg, className) ?: -1
                if (tid != -1) shellService?.moveTaskToBack(tid)
            } catch (e: Exception) {}
        }.start()
    }
    
    // === FORCE STOP ===
    fun forceStop(pkg: String) {
        Thread { try { shellService?.forceStop(pkg) } catch (e: Exception) {} }.start()
    }
    
    // === RUN COMMAND ===
    fun runCommand(cmd: String) {
        Thread { try { shellService?.runCommand(cmd) } catch (e: Exception) {} }.start()
    }
    
    // === GET TASK ID ===
    fun getTaskId(pkg: String, className: String?): Int {
        return shellService?.getTaskId(pkg, className) ?: -1
    }
    
    // === REPOSITION TASK ===
    fun repositionTask(pkg: String, className: String?, left: Int, top: Int, right: Int, bottom: Int) {
        shellService?.repositionTask(pkg, className, left, top, right, bottom)
    }
}