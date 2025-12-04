package com.example.quadrantlauncher

import android.app.ActivityManager
import android.app.Service
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

class FloatingLauncherService : Service() {

    companion object {
        const val MODE_SEARCH = 0
        const val MODE_LAYOUTS = 2
        const val MODE_RESOLUTION = 3
        const val MODE_DPI = 4
        const val MODE_PROFILES = 5
        const val MODE_SETTINGS = 6
        
        const val LAYOUT_FULL = 1
        const val LAYOUT_SIDE_BY_SIDE = 2
        const val LAYOUT_TOP_BOTTOM = 5
        const val LAYOUT_TRI_EVEN = 3
        const val LAYOUT_CORNERS = 4
        const val LAYOUT_TRI_SIDE_MAIN_SIDE = 6
        const val LAYOUT_QUAD_ROW_EVEN = 7
        const val LAYOUT_CUSTOM_DYNAMIC = 99

        const val CHANNEL_ID = "OverlayServiceChannel"
        const val TAG = "FloatingService"
        const val DEBUG_TAG = "DROIDOS_DEBUG"
        const val ACTION_OPEN_DRAWER = "com.example.quadrantlauncher.OPEN_DRAWER"
        const val ACTION_UPDATE_ICON = "com.example.quadrantlauncher.UPDATE_ICON"
        const val HIGHLIGHT_COLOR = 0xFF00A0E9.toInt()
    }

    private lateinit var windowManager: WindowManager
    private var displayContext: Context? = null
    private var currentDisplayId = 0
    private var lastPhysicalDisplayId = Display.DEFAULT_DISPLAY 

    private var bubbleView: View? = null
    private var drawerView: View? = null
    private var debugStatusView: TextView? = null
    
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var drawerParams: WindowManager.LayoutParams

    private var isExpanded = false
    private val selectedAppsQueue = mutableListOf<MainActivity.AppInfo>()
    private val allAppsList = mutableListOf<MainActivity.AppInfo>()
    private val displayList = mutableListOf<Any>()
    
    private var activeProfileName: String? = null
    private var currentMode = MODE_SEARCH
    private var selectedLayoutType = 2
    private var selectedResolutionIndex = 0
    private var currentDpiSetting = -1
    private var currentFontSize = 16f
    
    private var activeCustomRects: List<Rect>? = null
    private var activeCustomLayoutName: String? = null
    
    private var killAppOnExecute = true
    private var targetDisplayIndex = 1 
    private var isExtinguished = false
    private var isInstantMode = true 
    private var showShizukuWarning = true 
    private var useAltScreenOff = false
    
    private var isVirtualDisplayActive = false
    private var currentDrawerHeightPercent = 70
    private var currentDrawerWidthPercent = 90
    private var autoResizeEnabled = true
    
    private var reorderSelectionIndex = -1
    private var isReorderDragEnabled = true
    private var isReorderTapEnabled = true
    
    private val PACKAGE_BLANK = "internal.blank.spacer"
    private val PACKAGE_TRACKPAD = "com.katsuyamaki.DroidOSTrackpadKeyboard"
    
    private var shellService: IShellService? = null
    private var isBound = false
    private val uiHandler = Handler(Looper.getMainLooper())

    private val shizukuBinderListener = Shizuku.OnBinderReceivedListener { if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() }
    private val shizukuPermissionListener = Shizuku.OnRequestPermissionResultListener { _, grantResult -> if (grantResult == PackageManager.PERMISSION_GRANTED) bindShizuku() }

    private val commandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_OPEN_DRAWER) { if (isExtinguished) wakeUp() else if (!isExpanded) toggleDrawer() } 
            else if (intent?.action == ACTION_UPDATE_ICON) { updateBubbleIcon(); if (currentMode == MODE_SETTINGS) switchMode(MODE_SETTINGS) }
        }
    }
    private val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun getMovementFlags(r: RecyclerView, v: RecyclerView.ViewHolder): Int {
            val pos = v.adapterPosition; if (pos == RecyclerView.NO_POSITION || pos >= displayList.size) return 0
            val item = displayList[pos]
            val isSwipeable = when (currentMode) {
                MODE_LAYOUTS -> (item is LayoutOption && item.type == LAYOUT_CUSTOM_DYNAMIC && item.isCustomSaved)
                MODE_RESOLUTION -> (item is ResolutionOption && item.index >= 100)
                MODE_PROFILES -> (item is ProfileOption && !item.isCurrent)
                MODE_SEARCH -> true
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
            if (currentMode == MODE_SEARCH) { val item = displayList.getOrNull(pos) as? MainActivity.AppInfo ?: return; if (item.packageName == PACKAGE_BLANK) { (drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view).adapter as RofiAdapter).notifyItemChanged(pos); return }; if (direction == ItemTouchHelper.LEFT && !item.isFavorite) toggleFavorite(item) else if (direction == ItemTouchHelper.RIGHT && item.isFavorite) toggleFavorite(item); refreshSearchList() }
        }
    }

    private val selectedAppsDragCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
        override fun isLongPressDragEnabled(): Boolean = isReorderDragEnabled
        override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean { Collections.swap(selectedAppsQueue, v.adapterPosition, t.adapterPosition); r.adapter?.notifyItemMoved(v.adapterPosition, t.adapterPosition); return true }
        override fun onSwiped(v: RecyclerView.ViewHolder, direction: Int) { dismissKeyboardAndRestore(); val pos = v.adapterPosition; if (pos != RecyclerView.NO_POSITION) { val app = selectedAppsQueue[pos]; if (app.packageName != PACKAGE_BLANK) { Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start(); safeToast("Killed ${app.label}") }; selectedAppsQueue.removeAt(pos); if (reorderSelectionIndex != -1) endReorderMode(false); updateSelectedAppsDock(); drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() } }
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) { shellService = IShellService.Stub.asInterface(binder); isBound = true; updateExecuteButtonColor(true); updateBubbleIcon(); safeToast("Shizuku Connected") }
        override fun onServiceDisconnected(name: ComponentName?) { shellService = null; isBound = false; updateExecuteButtonColor(false); updateBubbleIcon() }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        try { Shizuku.addBinderReceivedListener(shizukuBinderListener); Shizuku.addRequestPermissionResultListener(shizukuPermissionListener) } catch (e: Exception) {}
        val filter = IntentFilter().apply { addAction(ACTION_OPEN_DRAWER); addAction(ACTION_UPDATE_ICON) }
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(commandReceiver, filter, Context.RECEIVER_EXPORTED) else registerReceiver(commandReceiver, filter)
        try { if (rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() } catch (e: Exception) {}
        
        loadInstalledApps(); currentFontSize = AppPreferences.getFontSize(this)
        killAppOnExecute = AppPreferences.getKillOnExecute(this); targetDisplayIndex = AppPreferences.getTargetDisplayIndex(this)
        isInstantMode = AppPreferences.getInstantMode(this); showShizukuWarning = AppPreferences.getShowShizukuWarning(this)
        useAltScreenOff = AppPreferences.getUseAltScreenOff(this); isReorderDragEnabled = AppPreferences.getReorderDrag(this)
        isReorderTapEnabled = AppPreferences.getReorderTap(this); currentDrawerHeightPercent = AppPreferences.getDrawerHeightPercent(this)
        currentDrawerWidthPercent = AppPreferences.getDrawerWidthPercent(this); autoResizeEnabled = AppPreferences.getAutoResizeKeyboard(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val targetDisplayId = intent?.getIntExtra("DISPLAY_ID", Display.DEFAULT_DISPLAY) ?: Display.DEFAULT_DISPLAY
        if (bubbleView != null && targetDisplayId != currentDisplayId) { try { windowManager.removeView(bubbleView); if (isExpanded) windowManager.removeView(drawerView) } catch (e: Exception) {}; setupDisplayContext(targetDisplayId); setupBubble(); setupDrawer(); updateBubbleIcon(); loadDisplaySettings(currentDisplayId); isExpanded = false; safeToast("Moved to Display $targetDisplayId") } 
        else if (bubbleView == null) { try { setupDisplayContext(targetDisplayId); setupBubble(); setupDrawer(); selectedLayoutType = AppPreferences.getLastLayout(this); activeCustomLayoutName = AppPreferences.getLastCustomLayoutName(this); updateGlobalFontSize(); updateBubbleIcon(); loadDisplaySettings(currentDisplayId); if (selectedLayoutType == LAYOUT_CUSTOM_DYNAMIC && activeCustomLayoutName != null) { val data = AppPreferences.getCustomLayoutData(this, activeCustomLayoutName!!); if (data != null) { val rects = mutableListOf<Rect>(); val rectParts = data.split("|"); for (rp in rectParts) { val coords = rp.split(","); if (coords.size == 4) rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt())) }; activeCustomRects = rects } }; try { if (shellService == null && rikka.shizuku.Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) bindShizuku() } catch (e: Exception) {} } catch (e: Exception) { stopSelf() } }
        return START_NOT_STICKY
    }
    
    private fun loadDisplaySettings(displayId: Int) { selectedResolutionIndex = AppPreferences.getDisplayResolution(this, displayId); currentDpiSetting = AppPreferences.getDisplayDpi(this, displayId) }

    override fun onDestroy() {
        super.onDestroy()
        isExtinguished = false
        // Restore brightness on destroy
        if (useAltScreenOff) { 
             try { shellService?.setBrightness(128) } catch(e: Exception) {}
        }
        try { Shizuku.removeBinderReceivedListener(shizukuBinderListener); Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener); unregisterReceiver(commandReceiver) } catch (e: Exception) {}
        try { if (bubbleView != null) windowManager.removeView(bubbleView); if (isExpanded) windowManager.removeView(drawerView) } catch (e: Exception) {}
        if (isBound) { try { ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection); isBound = false } catch (e: Exception) {} }
    }
    
    private fun safeToast(msg: String) { 
        uiHandler.post { 
            try { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() } catch(e: Exception) { }
            if (debugStatusView != null) debugStatusView?.text = msg 
        }
    }
    
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
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val display = displayManager.getDisplay(displayId)
        if (display == null) { windowManager = getSystemService(WINDOW_SERVICE) as WindowManager; return }
        currentDisplayId = displayId; displayContext = createDisplayContext(display); windowManager = displayContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
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
        bubbleParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT)
        bubbleParams.gravity = Gravity.TOP or Gravity.START; bubbleParams.x = 50; bubbleParams.y = 200
        var velocityTracker: VelocityTracker? = null
        bubbleView?.setOnTouchListener(object : View.OnTouchListener {
            var initialX = 0; var initialY = 0; var initialTouchX = 0f; var initialTouchY = 0f; var isDrag = false
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (velocityTracker == null) velocityTracker = VelocityTracker.obtain(); velocityTracker?.addMovement(event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { initialX = bubbleParams.x; initialY = bubbleParams.y; initialTouchX = event.rawX; initialTouchY = event.rawY; isDrag = false; return true }
                    MotionEvent.ACTION_MOVE -> { if (Math.abs(event.rawX - initialTouchX) > 10 || Math.abs(event.rawY - initialTouchY) > 10) isDrag = true; if (isDrag) { bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt(); bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt(); windowManager.updateViewLayout(bubbleView, bubbleParams) }; return true }
                    MotionEvent.ACTION_UP -> { velocityTracker?.computeCurrentVelocity(1000); val vX = velocityTracker?.xVelocity ?: 0f; val vY = velocityTracker?.yVelocity ?: 0f; val totalVel = hypot(vX.toDouble(), vY.toDouble()); if (isDrag && totalVel > 2500) { safeToast("Closing..."); stopSelf(); return true }; if (!isDrag) { if (!isBound && showShizukuWarning) { if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) { bindShizuku() } else { safeToast("Shizuku NOT Connected. Opening Shizuku..."); launchShizuku() } } else { toggleDrawer() } }; velocityTracker?.recycle(); velocityTracker = null; return true }
                    MotionEvent.ACTION_CANCEL -> { velocityTracker?.recycle(); velocityTracker = null }
                }
                return false
            }
        })
        windowManager.addView(bubbleView, bubbleParams)
    }
    
    private fun launchShizuku() { try { val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api"); if (intent != null) { intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(intent) } else { safeToast("Shizuku app not found") } } catch(e: Exception) { safeToast("Failed to launch Shizuku") } }
    private fun updateBubbleIcon() { val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon) ?: return; if (!isBound && showShizukuWarning) { uiHandler.post { iconView.setImageResource(android.R.drawable.ic_dialog_alert); iconView.setColorFilter(Color.RED); iconView.imageTintList = null }; return }; uiHandler.post { try { val uriStr = AppPreferences.getIconUri(this); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); if (bitmap != null) { iconView.setImageBitmap(bitmap); iconView.imageTintList = null; iconView.clearColorFilter() } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } else { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } catch (e: Exception) { iconView.setImageResource(R.drawable.ic_launcher_bubble); iconView.imageTintList = null; iconView.clearColorFilter() } } }
    private fun dismissKeyboardAndRestore() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); if (searchBar != null && searchBar.hasFocus()) { searchBar.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(searchBar.windowToken, 0) }; val dpiInput = drawerView?.findViewById<EditText>(R.id.input_dpi_value); if (dpiInput != null && dpiInput.hasFocus()) { dpiInput.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(dpiInput.windowToken, 0) }; updateDrawerHeight(false) }

    private fun setupDrawer() {
        val context = displayContext ?: this
        val themeContext = ContextThemeWrapper(context, R.style.Theme_QuadrantLauncher)
        drawerView = LayoutInflater.from(themeContext).inflate(R.layout.layout_rofi_drawer, null)
        drawerView!!.fitsSystemWindows = true 
        drawerParams = WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT)
        drawerParams.gravity = Gravity.TOP or Gravity.START; drawerParams.x = 0; drawerParams.y = 0
        drawerParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        
        val container = drawerView?.findViewById<LinearLayout>(R.id.drawer_container)
        if (container != null) { 
            val lp = container.layoutParams as? FrameLayout.LayoutParams
            if (lp != null) { lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; lp.topMargin = 100; container.layoutParams = lp }
            
            debugStatusView = TextView(context)
            debugStatusView?.text = "Ready"
            debugStatusView?.setTextColor(Color.GREEN)
            debugStatusView?.textSize = 10f
            debugStatusView?.gravity = Gravity.CENTER
            container.addView(debugStatusView, 0)
        }

        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val mainRecycler = drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view); val selectedRecycler = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        if (isBound) executeBtn.setColorFilter(Color.GREEN) else executeBtn.setColorFilter(Color.RED)
        drawerView!!.findViewById<ImageView>(R.id.icon_search_mode).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_SEARCH) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_window).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_LAYOUTS) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_resolution).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_RESOLUTION) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_dpi).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_DPI) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_profiles).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_PROFILES) }
        drawerView!!.findViewById<ImageView>(R.id.icon_mode_settings).setOnClickListener { dismissKeyboardAndRestore(); switchMode(MODE_SETTINGS) }
        executeBtn.setOnClickListener { executeLaunch(selectedLayoutType, closeDrawer = true) }
        searchBar.addTextChangedListener(object : TextWatcher { override fun afterTextChanged(s: Editable?) { filterList(s.toString()) }; override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}; override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {} })
        searchBar.imeOptions = EditorInfo.IME_ACTION_DONE
        searchBar.setOnEditorActionListener { v, actionId, event -> if (actionId == EditorInfo.IME_ACTION_DONE) { dismissKeyboardAndRestore(); return@setOnEditorActionListener true }; false }
        searchBar.setOnKeyListener { _, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) { if (searchBar.text.isEmpty() && selectedAppsQueue.isNotEmpty()) { val lastIndex = selectedAppsQueue.size - 1; selectedAppsQueue.removeAt(lastIndex); updateSelectedAppsDock(); mainRecycler.adapter?.notifyDataSetChanged(); return@setOnKeyListener true } }; if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { if (searchBar.hasFocus()) { dismissKeyboardAndRestore(); return@setOnKeyListener true } }; return@setOnKeyListener false }
        searchBar.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) { updateDrawerHeight(hasFocus) } }
        mainRecycler.layoutManager = LinearLayoutManager(themeContext); mainRecycler.adapter = RofiAdapter(); val itemTouchHelper = ItemTouchHelper(swipeCallback); itemTouchHelper.attachToRecyclerView(mainRecycler)
        mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() { override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) { if (newState == RecyclerView.SCROLL_STATE_DRAGGING) { dismissKeyboardAndRestore() } } })
        mainRecycler.setOnTouchListener { v, event -> if (event.action == MotionEvent.ACTION_DOWN) { dismissKeyboardAndRestore() }; false }
        selectedRecycler.layoutManager = LinearLayoutManager(themeContext, LinearLayoutManager.HORIZONTAL, false); selectedRecycler.adapter = SelectedAppsAdapter(); val dockTouchHelper = ItemTouchHelper(selectedAppsDragCallback); dockTouchHelper.attachToRecyclerView(selectedRecycler)
        drawerView!!.setOnClickListener { toggleDrawer() }
        drawerView!!.isFocusableInTouchMode = true
        drawerView!!.setOnKeyListener { _, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) { toggleDrawer(); true } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && isExtinguished) { wakeUp(); true } else false }
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

    private fun toggleDrawer() {
        if (isExpanded) { try { windowManager.removeView(drawerView) } catch(e: Exception) {}; bubbleView?.visibility = View.VISIBLE; isExpanded = false } else { setupDisplayContext(currentDisplayId); updateDrawerHeight(false); try { windowManager.addView(drawerView, drawerParams) } catch(e: Exception) {}; bubbleView?.visibility = View.GONE; isExpanded = true; switchMode(MODE_SEARCH); val et = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); et?.setText(""); et?.clearFocus(); updateSelectedAppsDock(); if (isInstantMode) fetchRunningApps() }
    }
    private fun updateGlobalFontSize() { val searchBar = drawerView?.findViewById<EditText>(R.id.rofi_search_bar); searchBar?.textSize = currentFontSize; drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() }
    private fun loadInstalledApps() { val pm = packageManager; val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }; val riList = pm.queryIntentActivities(intent, 0); allAppsList.clear(); allAppsList.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)); for (ri in riList) { val pkg = ri.activityInfo.packageName; if (pkg == PACKAGE_TRACKPAD) continue; val app = MainActivity.AppInfo(ri.loadLabel(pm).toString(), pkg, AppPreferences.isFavorite(this, pkg)); allAppsList.add(app) }; allAppsList.sortBy { it.label.lowercase() } }
    
    private fun launchTrackpad() {
        if (isTrackpadRunning()) { safeToast("Trackpad is already active"); return }
        try { val intent = packageManager.getLaunchIntentForPackage(PACKAGE_TRACKPAD); if (intent != null) { intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP); val dm = DisplayMetrics(); val display = displayContext?.display ?: windowManager.defaultDisplay; display.getRealMetrics(dm); val w = dm.widthPixels; val h = dm.heightPixels; val targetW = (w * 0.5f).toInt(); val targetH = (h * 0.5f).toInt(); val left = (w - targetW) / 2; val top = (h - targetH) / 2; val bounds = Rect(left, top, left + targetW, top + targetH); val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(bounds); try { val method = android.app.ActivityOptions::class.java.getMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType); method.invoke(options, 5) } catch (e: Exception) {}; startActivity(intent, options.toBundle()); toggleDrawer(); if (shellService != null) { uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(PACKAGE_TRACKPAD, left, top, left+targetW, top+targetH) } catch(e: Exception) { Log.e(TAG, "Shell launch failed", e) } }.start() }, 400) } } else { safeToast("Trackpad App not found") } } catch (e: Exception) { safeToast("Error launching Trackpad") }
    }

    private fun isTrackpadRunning(): Boolean { try { val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager; val runningApps = am.runningAppProcesses; if (runningApps != null) { for (info in runningApps) { if (info.processName == PACKAGE_TRACKPAD) return true } } } catch (e: Exception) {}; return false }
    private fun getLayoutName(type: Int): String { return when(type) { LAYOUT_FULL -> "1 App - Full"; LAYOUT_SIDE_BY_SIDE -> "Split"; LAYOUT_TOP_BOTTOM -> "Top/Bot"; LAYOUT_TRI_EVEN -> "Tri-Split"; LAYOUT_CORNERS -> "Quadrant"; LAYOUT_TRI_SIDE_MAIN_SIDE -> "3 Apps - Side/Main/Side"; LAYOUT_QUAD_ROW_EVEN -> "4 Apps - Row"; LAYOUT_CUSTOM_DYNAMIC -> "Custom"; else -> "Unknown" } }
    private fun getRatioName(index: Int): String { return when(index) { 1 -> "1:1"; 2 -> "16:9"; 3 -> "32:9"; else -> "Default" } }
    private fun getTargetDimensions(index: Int): Pair<Int, Int>? { return when(index) { 1 -> 1422 to 1500; 2 -> 1920 to 1080; 3 -> 3840 to 1080; else -> null } }
    private fun getResolutionCommand(index: Int): String { return when(index) { 1 -> "wm size 1422x1500 -d $currentDisplayId"; 2 -> "wm size 1920x1080 -d $currentDisplayId"; 3 -> "wm size 3840x1080 -d $currentDisplayId"; else -> "wm size reset -d $currentDisplayId" } }
    private fun sortAppQueue() { selectedAppsQueue.sortWith(compareBy { it.isMinimized }) }
    private fun updateSelectedAppsDock() { val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); if (selectedAppsQueue.isEmpty()) { dock.visibility = View.GONE } else { dock.visibility = View.VISIBLE; dock.adapter?.notifyDataSetChanged(); dock.scrollToPosition(selectedAppsQueue.size - 1) } }
    private fun refreshSearchList() { val query = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString() ?: ""; filterList(query) }
    private fun filterList(query: String) {
        if (currentMode != MODE_SEARCH) return; val actualQuery = query.substringAfterLast(",").trim(); displayList.clear()
        val filtered = if (actualQuery.isEmpty()) { allAppsList } else { allAppsList.filter { it.label.contains(actualQuery, ignoreCase = true) } }
        val sorted = filtered.sortedWith(compareBy<MainActivity.AppInfo> { it.packageName != PACKAGE_BLANK }.thenByDescending { it.isFavorite }.thenBy { it.label.lowercase() }); displayList.addAll(sorted); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    private fun addToSelection(app: MainActivity.AppInfo) {
        dismissKeyboardAndRestore(); val et = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar)
        if (app.packageName == PACKAGE_BLANK) { selectedAppsQueue.add(app); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate(); return }
        val existing = selectedAppsQueue.find { it.packageName == app.packageName }; if (existing != null) { selectedAppsQueue.remove(existing); Thread { try { shellService?.forceStop(app.packageName) } catch(e: Exception) {} }.start(); safeToast("Removed ${app.label}"); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); et.setText(""); if (isInstantMode) applyLayoutImmediate() } 
        else { app.isMinimized = false; selectedAppsQueue.add(app); sortAppQueue(); updateSelectedAppsDock(); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); et.setText(""); if (isInstantMode) { launchViaApi(app.packageName, null); launchViaShell(app.packageName); uiHandler.postDelayed({ applyLayoutImmediate() }, 200); uiHandler.postDelayed({ applyLayoutImmediate() }, 800) } }
    }
    private fun toggleFavorite(app: MainActivity.AppInfo) { val newState = AppPreferences.toggleFavorite(this, app.packageName); app.isFavorite = newState; allAppsList.find { it.packageName == app.packageName }?.isFavorite = newState }
    private fun launchViaApi(pkg: String, bounds: Rect?) { try { val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return; intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP); val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); if (bounds != null) options.setLaunchBounds(bounds); startActivity(intent, options.toBundle()) } catch (e: Exception) {} }
    private fun launchViaShell(pkg: String) { try { val intent = packageManager.getLaunchIntentForPackage(pkg) ?: return; if (shellService != null) { val component = intent.component?.flattenToShortString() ?: pkg; val cmd = "am start -n $component -a android.intent.action.MAIN -c android.intent.category.LAUNCHER --display $currentDisplayId --windowingMode 5 --user 0"; Thread { shellService?.runCommand(cmd) }.start() } } catch (e: Exception) {} }
    
    private fun cycleDisplay() {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val displays = dm.displays
        if (isVirtualDisplayActive) { val virtualDisp = displays.firstOrNull { it.displayId >= 2 }; if (virtualDisp != null) { val targetId = if (currentDisplayId == virtualDisp.displayId) { if (displays.any { it.displayId == lastPhysicalDisplayId }) lastPhysicalDisplayId else Display.DEFAULT_DISPLAY } else { lastPhysicalDisplayId = currentDisplayId; virtualDisp.displayId }; performDisplayChange(targetId); return } }
        val currentIdx = displays.indexOfFirst { it.displayId == currentDisplayId }; val nextIdx = if (currentIdx == -1) 0 else (currentIdx + 1) % displays.size; performDisplayChange(displays[nextIdx].displayId)
    }
    private fun performDisplayChange(newId: Int) {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val targetDisplay = dm.getDisplay(newId) ?: return; try { if (bubbleView != null && bubbleView!!.isAttachedToWindow) windowManager.removeView(bubbleView); if (drawerView != null && drawerView!!.isAttachedToWindow) windowManager.removeView(drawerView) } catch (e: Exception) {}; currentDisplayId = newId; setupDisplayContext(currentDisplayId); targetDisplayIndex = currentDisplayId; AppPreferences.setTargetDisplayIndex(this, targetDisplayIndex); setupBubble(); setupDrawer(); loadDisplaySettings(currentDisplayId); updateBubbleIcon(); isExpanded = false; safeToast("Switched to Display $currentDisplayId (${targetDisplay.name})")
    }
    private fun toggleVirtualDisplay(enable: Boolean) { isVirtualDisplayActive = enable; Thread { try { if (enable) { shellService?.runCommand("settings put global overlay_display_devices \"1920x1080/320\""); uiHandler.post { safeToast("Creating Virtual Display... Wait a moment, then Switch Display.") } } else { shellService?.runCommand("settings delete global overlay_display_devices"); uiHandler.post { safeToast("Destroying Virtual Display...") } } } catch (e: Exception) { Log.e(TAG, "Virtual Display Toggle Failed", e) } }.start(); if (currentMode == MODE_SETTINGS) uiHandler.postDelayed({ switchMode(MODE_SETTINGS) }, 500) }

    // --- SCREEN OFF LOGIC (UPDATED FOR ANDROID 14) ---
    private fun performExtinguish() {
        vibrate()
        isExtinguished = true
        
        if (useAltScreenOff) {
             // New Methodology: Fast Binder Call via Shizuku
             Thread {
                 try {
                     if (shellService != null) {
                         // -1 triggers the specific Extinguish logic in ShellUserService (ContextWrapper + putFloat -1.0f)
                         shellService?.setBrightness(-1)
                         uiHandler.post { safeToast("Pixels OFF (Extinguish Mode)") }
                     } else {
                         safeToast("Service Disconnected!")
                     }
                 } catch (e: Exception) {
                     Log.e(TAG, "Binder Call Failed", e)
                     safeToast("Error: ${e.message}")
                 }
            }.start()
        } else {
            // Default: SurfaceControl Power Off
            Thread { try { shellService?.setScreenOff(0, true); if (currentDisplayId != 0) shellService?.setScreenOff(currentDisplayId, true) } catch (e: Exception) {} }.start()
            safeToast("Screen OFF (SurfaceControl)")
        }
    }
    
    private fun wakeUp() {
        vibrate()
        isExtinguished = false
        if (useAltScreenOff) {
             // Use the fast Binder API to restore brightness
             Thread {
                try {
                    shellService?.setBrightness(128)
                    uiHandler.post { safeToast("Brightness Restored") }
                } catch (e: Exception) {}
            }.start()
        } else {
            Thread { try { shellService?.setScreenOff(0, false); shellService?.setScreenOff(currentDisplayId, false) } catch (e: Exception) {} }.start()
            safeToast("Screen Woke Up")
        }
        if (currentMode == MODE_SETTINGS) drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }

    private fun applyLayoutImmediate() { executeLaunch(selectedLayoutType, closeDrawer = false) }
    private fun fetchRunningApps() { if (shellService == null) return; Thread { try { val visiblePackages = shellService!!.getVisiblePackages(currentDisplayId); val allRunning = shellService!!.getAllRunningPackages(); val lastQueue = AppPreferences.getLastQueue(this); uiHandler.post { selectedAppsQueue.clear(); for (pkg in lastQueue) { if (pkg == PACKAGE_BLANK) { selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)) } else if (allRunning.contains(pkg)) { val appInfo = allAppsList.find { it.packageName == pkg }; if (appInfo != null) { appInfo.isMinimized = !visiblePackages.contains(pkg); selectedAppsQueue.add(appInfo) } } }; for (pkg in visiblePackages) { if (!lastQueue.contains(pkg)) { val appInfo = allAppsList.find { it.packageName == pkg }; if (appInfo != null) { appInfo.isMinimized = false; selectedAppsQueue.add(appInfo) } } }; sortAppQueue(); updateSelectedAppsDock(); drawerView?.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); safeToast("Instant Mode: Active") } } catch (e: Exception) { Log.e(TAG, "Error fetching apps", e) } }.start() }
    private fun selectLayout(opt: LayoutOption) { dismissKeyboardAndRestore(); selectedLayoutType = opt.type; activeCustomRects = opt.customRects; if (opt.type == LAYOUT_CUSTOM_DYNAMIC) { activeCustomLayoutName = opt.name; AppPreferences.saveLastCustomLayoutName(this, opt.name) } else { activeCustomLayoutName = null; AppPreferences.saveLastCustomLayoutName(this, null) }; AppPreferences.saveLastLayout(this, opt.type); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() }
    private fun saveCurrentAsCustom() { Thread { try { val rawLayouts = shellService!!.getWindowLayouts(currentDisplayId); if (rawLayouts.isEmpty()) { safeToast("Found 0 active app windows"); return@Thread }; val rectStrings = mutableListOf<String>(); for (line in rawLayouts) { val parts = line.split("|"); if (parts.size == 2) { rectStrings.add(parts[1]) } }; if (rectStrings.isEmpty()) { safeToast("Found 0 valid frames"); return@Thread }; val count = rectStrings.size; var baseName = "$count Apps - Custom"; val existingNames = AppPreferences.getCustomLayoutNames(this); var counter = 1; var finalName = "$baseName $counter"; while (existingNames.contains(finalName)) { counter++; finalName = "$baseName $counter" }; AppPreferences.saveCustomLayout(this, finalName, rectStrings.joinToString("|")); safeToast("Saved: $finalName"); uiHandler.post { switchMode(MODE_LAYOUTS) } } catch (e: Exception) { Log.e(TAG, "Failed to save custom layout", e); safeToast("Error saving: ${e.message}") } }.start() }
    private fun applyResolution(opt: ResolutionOption) { dismissKeyboardAndRestore(); if (opt.index != -1) { selectedResolutionIndex = opt.index; AppPreferences.saveDisplayResolution(this, currentDisplayId, opt.index) }; drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode && opt.index != -1) { Thread { val resCmd = getResolutionCommand(selectedResolutionIndex); shellService?.runCommand(resCmd); Thread.sleep(1500); uiHandler.post { applyLayoutImmediate() } }.start() } }
    private fun selectDpi(value: Int) { currentDpiSetting = if (value == -1) -1 else value.coerceIn(50, 600); AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); Thread { try { if (currentDpiSetting == -1) { shellService?.runCommand("wm density reset -d $currentDisplayId") } else { val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; shellService?.runCommand(dpiCmd) } } catch(e: Exception) { e.printStackTrace() } }.start() }
    private fun changeFontSize(newSize: Float) { currentFontSize = newSize.coerceIn(10f, 30f); AppPreferences.saveFontSize(this, currentFontSize); updateGlobalFontSize(); if (currentMode == MODE_SETTINGS) { switchMode(MODE_SETTINGS) } }
    private fun changeDrawerHeight(delta: Int) { currentDrawerHeightPercent = (currentDrawerHeightPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerHeightPercent(this, currentDrawerHeightPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun changeDrawerWidth(delta: Int) { currentDrawerWidthPercent = (currentDrawerWidthPercent + delta).coerceIn(30, 100); AppPreferences.setDrawerWidthPercent(this, currentDrawerWidthPercent); updateDrawerHeight(false); if (currentMode == MODE_SETTINGS) { drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged() } }
    private fun pickIcon() { toggleDrawer(); try { refreshDisplayId(); val intent = Intent(this, IconPickerActivity::class.java); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); val metrics = windowManager.maximumWindowMetrics; val w = 1000; val h = (metrics.bounds.height() * 0.7).toInt(); val x = (metrics.bounds.width() - w) / 2; val y = (metrics.bounds.height() - h) / 2; val options = android.app.ActivityOptions.makeBasic(); options.setLaunchDisplayId(currentDisplayId); options.setLaunchBounds(Rect(x, y, x+w, y+h)); startActivity(intent, options.toBundle()) } catch (e: Exception) { safeToast("Error launching picker: ${e.message}") } }
    private fun saveProfile() { var name = drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.text?.toString()?.trim(); if (name.isNullOrEmpty()) { val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()); name = "Profile_$timestamp" }; val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveProfile(this, name, selectedLayoutType, selectedResolutionIndex, currentDpiSetting, pkgs); safeToast("Saved: $name"); drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText(""); switchMode(MODE_PROFILES) }
    private fun loadProfile(name: String) { val data = AppPreferences.getProfileData(this, name) ?: return; try { val parts = data.split("|"); selectedLayoutType = parts[0].toInt(); selectedResolutionIndex = parts[1].toInt(); currentDpiSetting = parts[2].toInt(); val pkgList = parts[3].split(","); selectedAppsQueue.clear(); for (pkg in pkgList) { if (pkg.isNotEmpty()) { if (pkg == PACKAGE_BLANK) { selectedAppsQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK)) } else { val app = allAppsList.find { it.packageName == pkg }; if (app != null) selectedAppsQueue.add(app) } } }; AppPreferences.saveLastLayout(this, selectedLayoutType); AppPreferences.saveDisplayResolution(this, currentDisplayId, selectedResolutionIndex); AppPreferences.saveDisplayDpi(this, currentDisplayId, currentDpiSetting); activeProfileName = name; updateSelectedAppsDock(); safeToast("Loaded: $name"); drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged(); if (isInstantMode) applyLayoutImmediate() } catch (e: Exception) { Log.e(TAG, "Failed to load profile", e) } }
    
    private fun executeLaunch(layoutType: Int, closeDrawer: Boolean) { 
        if (closeDrawer) toggleDrawer(); refreshDisplayId(); val pkgs = selectedAppsQueue.map { it.packageName }; AppPreferences.saveLastQueue(this, pkgs)
        Thread { try { val resCmd = getResolutionCommand(selectedResolutionIndex); shellService?.runCommand(resCmd); if (currentDpiSetting > 0) { val dpiCmd = "wm density $currentDpiSetting -d $currentDisplayId"; shellService?.runCommand(dpiCmd) } else { if (currentDpiSetting == -1) shellService?.runCommand("wm density reset -d $currentDisplayId") }; Thread.sleep(800); val targetDim = getTargetDimensions(selectedResolutionIndex); var w = 0; var h = 0; if (targetDim != null) { w = targetDim.first; h = targetDim.second } else { val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; val display = dm.getDisplay(currentDisplayId); if (display != null) { val metrics = DisplayMetrics(); display.getRealMetrics(metrics); w = metrics.widthPixels; h = metrics.heightPixels } else { val bounds = windowManager.maximumWindowMetrics.bounds; w = bounds.width(); h = bounds.height() } }; val rects = mutableListOf<Rect>(); when (layoutType) { LAYOUT_FULL -> { rects.add(Rect(0, 0, w, h)) }; LAYOUT_SIDE_BY_SIDE -> { rects.add(Rect(0, 0, w/2, h)); rects.add(Rect(w/2, 0, w, h)) }; LAYOUT_TOP_BOTTOM -> { rects.add(Rect(0, 0, w, h/2)); rects.add(Rect(0, h/2, w, h)) }; LAYOUT_TRI_EVEN -> { val third = w / 3; rects.add(Rect(0, 0, third, h)); rects.add(Rect(third, 0, third * 2, h)); rects.add(Rect(third * 2, 0, w, h)) }; LAYOUT_CORNERS -> { rects.add(Rect(0, 0, w/2, h/2)); rects.add(Rect(w/2, 0, w, h/2)); rects.add(Rect(0, h/2, w/2, h)); rects.add(Rect(w/2, h/2, w, h)) }; LAYOUT_TRI_SIDE_MAIN_SIDE -> { val quarter = w / 4; rects.add(Rect(0, 0, quarter, h)); rects.add(Rect(quarter, 0, quarter * 3, h)); rects.add(Rect(quarter * 3, 0, w, h)) }; LAYOUT_QUAD_ROW_EVEN -> { val quarter = w / 4; rects.add(Rect(0, 0, quarter, h)); rects.add(Rect(quarter, 0, quarter * 2, h)); rects.add(Rect(quarter * 2, 0, quarter * 3, h)); rects.add(Rect(quarter * 3, 0, w, h)) }; LAYOUT_CUSTOM_DYNAMIC -> { if (activeCustomRects != null) { rects.addAll(activeCustomRects!!) } else { rects.add(Rect(0, 0, w/2, h)); rects.add(Rect(w/2, 0, w, h)) } } }; if (selectedAppsQueue.isNotEmpty()) { val minimizedApps = selectedAppsQueue.filter { it.isMinimized }; for (app in minimizedApps) { if (app.packageName != PACKAGE_BLANK) { try { val tid = shellService?.getTaskId(app.packageName) ?: -1; if (tid != -1) shellService?.moveTaskToBack(tid) } catch (e: Exception) { Log.e(TAG, "Failed to minimize ${app.packageName}", e) } } }; val activeApps = selectedAppsQueue.filter { !it.isMinimized }; if (killAppOnExecute) { for (app in activeApps) { if (app.packageName != PACKAGE_BLANK) { shellService?.forceStop(app.packageName) } }; Thread.sleep(400) } else { Thread.sleep(100) }; val count = Math.min(activeApps.size, rects.size); for (i in 0 until count) { val pkg = activeApps[i].packageName; val bounds = rects[i]; if (pkg == PACKAGE_BLANK) continue; uiHandler.postDelayed({ launchViaApi(pkg, bounds) }, (i * 150).toLong()); uiHandler.postDelayed({ launchViaShell(pkg) }, (i * 150 + 50).toLong()); if (!killAppOnExecute) { uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {} }.start() }, (i * 150 + 150).toLong()) }; uiHandler.postDelayed({ Thread { try { shellService?.repositionTask(pkg, bounds.left, bounds.top, bounds.right, bounds.bottom) } catch (e: Exception) {} }.start() }, (i * 150 + 800).toLong()) }; if (closeDrawer) { uiHandler.post { selectedAppsQueue.clear(); updateSelectedAppsDock() } } } } catch (e: Exception) { Log.e(TAG, "Execute Failed", e); safeToast("Execute Failed: ${e.message}") } }.start(); drawerView?.findViewById<EditText>(R.id.rofi_search_bar)?.setText("") 
    }
    
    private fun calculateGCD(a: Int, b: Int): Int { return if (b == 0) a else calculateGCD(b, a % b) }

    private fun switchMode(mode: Int) {
        currentMode = mode
        val searchBar = drawerView!!.findViewById<EditText>(R.id.rofi_search_bar); val searchIcon = drawerView!!.findViewById<ImageView>(R.id.icon_search_mode); val iconWin = drawerView!!.findViewById<ImageView>(R.id.icon_mode_window); val iconRes = drawerView!!.findViewById<ImageView>(R.id.icon_mode_resolution); val iconDpi = drawerView!!.findViewById<ImageView>(R.id.icon_mode_dpi); val iconProf = drawerView!!.findViewById<ImageView>(R.id.icon_mode_profiles); val iconSet = drawerView!!.findViewById<ImageView>(R.id.icon_mode_settings); val executeBtn = drawerView!!.findViewById<ImageView>(R.id.icon_execute)
        searchIcon.setColorFilter(if(mode==MODE_SEARCH) Color.WHITE else Color.GRAY); iconWin.setColorFilter(if(mode==MODE_LAYOUTS) Color.WHITE else Color.GRAY); iconRes.setColorFilter(if(mode==MODE_RESOLUTION) Color.WHITE else Color.GRAY); iconDpi.setColorFilter(if(mode==MODE_DPI) Color.WHITE else Color.GRAY); iconProf.setColorFilter(if(mode==MODE_PROFILES) Color.WHITE else Color.GRAY); iconSet.setColorFilter(if(mode==MODE_SETTINGS) Color.WHITE else Color.GRAY)
        executeBtn.visibility = if (isInstantMode) View.GONE else View.VISIBLE; displayList.clear(); val dock = drawerView!!.findViewById<RecyclerView>(R.id.selected_apps_recycler); dock.visibility = if (mode == MODE_SEARCH && selectedAppsQueue.isNotEmpty()) View.VISIBLE else View.GONE
        
        when (mode) {
            MODE_SEARCH -> { searchBar.hint = "Search apps..."; refreshSearchList() }
            MODE_LAYOUTS -> { 
                searchBar.hint = "Select Layout"; displayList.add(ActionOption("Save Current Arrangement") { saveCurrentAsCustom() }); displayList.add(LayoutOption("1 App - Full Screen", LAYOUT_FULL)); displayList.add(LayoutOption("2 Apps - Side by Side", LAYOUT_SIDE_BY_SIDE)); displayList.add(LayoutOption("2 Apps - Top & Bottom", LAYOUT_TOP_BOTTOM)); displayList.add(LayoutOption("3 Apps - Even", LAYOUT_TRI_EVEN)); displayList.add(LayoutOption("3 Apps - Side/Main/Side (25/50/25)", LAYOUT_TRI_SIDE_MAIN_SIDE)); displayList.add(LayoutOption("4 Apps - Corners", LAYOUT_CORNERS)); displayList.add(LayoutOption("4 Apps - Row (Even)", LAYOUT_QUAD_ROW_EVEN));
                val customNames = AppPreferences.getCustomLayoutNames(this).sorted(); for (name in customNames) { val data = AppPreferences.getCustomLayoutData(this, name); if (data != null) { try { val rects = mutableListOf<Rect>(); val rectParts = data.split("|"); for (rp in rectParts) { val coords = rp.split(","); if (coords.size == 4) { rects.add(Rect(coords[0].toInt(), coords[1].toInt(), coords[2].toInt(), coords[3].toInt())) } }; displayList.add(LayoutOption(name, LAYOUT_CUSTOM_DYNAMIC, true, rects)) } catch(e: Exception) {} } } 
            }
            MODE_RESOLUTION -> {
                searchBar.hint = "Select Resolution"; displayList.add(CustomResInputOption); val savedResNames = AppPreferences.getCustomResolutionNames(this).sorted(); for (name in savedResNames) { val value = AppPreferences.getCustomResolutionValue(this, name) ?: continue; displayList.add(ResolutionOption(name, "wm size  -d $currentDisplayId", 100 + savedResNames.indexOf(name))) }; displayList.add(ResolutionOption("Default (Reset)", "wm size reset -d $currentDisplayId", 0)); displayList.add(ResolutionOption("1:1 Square (1422x1500)", "wm size 1422x1500 -d $currentDisplayId", 1)); displayList.add(ResolutionOption("16:9 Landscape (1920x1080)", "wm size 1920x1080 -d $currentDisplayId", 2)); displayList.add(ResolutionOption("32:9 Ultrawide (3840x1080)", "wm size 3840x1080 -d $currentDisplayId", 3))
            }
            MODE_DPI -> { searchBar.hint = "Adjust Density (DPI)"; displayList.add(ActionOption("Reset Density (Default)") { selectDpi(-1) }); var savedDpi = currentDpiSetting; if (savedDpi <= 0) { savedDpi = displayContext?.resources?.configuration?.densityDpi ?: 160 }; displayList.add(DpiOption(savedDpi)) }
            MODE_PROFILES -> { searchBar.hint = "Enter Profile Name..."; displayList.add(ProfileOption("Save Current as New", true, 0,0,0, emptyList())); val profileNames = AppPreferences.getProfileNames(this).sorted(); for (pName in profileNames) { val data = AppPreferences.getProfileData(this, pName); if (data != null) { try { val parts = data.split("|"); val lay = parts[0].toInt(); val res = parts[1].toInt(); val d = parts[2].toInt(); val pkgs = parts[3].split(",").filter { it.isNotEmpty() }; displayList.add(ProfileOption(pName, false, lay, res, d, pkgs)) } catch(e: Exception) {} } } }
            MODE_SETTINGS -> {
                searchBar.hint = "Settings"
                displayList.add(ActionOption("Launch DroidOS Trackpad") { launchTrackpad() }) 
                displayList.add(ActionOption("Switch Display (Current $currentDisplayId)") { cycleDisplay() })
                displayList.add(ToggleOption("Virtual Display (1080p)", isVirtualDisplayActive) { toggleVirtualDisplay(it) })
                displayList.add(HeightOption(currentDrawerHeightPercent))
                displayList.add(WidthOption(currentDrawerWidthPercent))
                displayList.add(ToggleOption("Auto-Shrink for Keyboard", autoResizeEnabled) { autoResizeEnabled = it; AppPreferences.setAutoResizeKeyboard(this, it) })
                displayList.add(FontSizeOption(currentFontSize))
                displayList.add(IconOption("Launcher Icon (Tap to Change)"))
                displayList.add(ToggleOption("Reorder: Drag & Drop", isReorderDragEnabled) { isReorderDragEnabled = it; AppPreferences.setReorderDrag(this, it) })
                displayList.add(ToggleOption("Reorder: Tap to Swap (Long Press)", isReorderTapEnabled) { isReorderTapEnabled = it; AppPreferences.setReorderTap(this, it) })
                displayList.add(ToggleOption("Instant Mode (Live Changes)", isInstantMode) { isInstantMode = it; AppPreferences.setInstantMode(this, it); executeBtn.visibility = if (it) View.GONE else View.VISIBLE; if (it) fetchRunningApps() })
                displayList.add(ToggleOption("Kill App on Execute", killAppOnExecute) { killAppOnExecute = it; AppPreferences.setKillOnExecute(this, it) })
                displayList.add(ToggleOption("Display Off (Touch on)", isExtinguished) { if (it) performExtinguish() else wakeUp() })
                
                // Alt Screen Off: Brightness -1 (OLED Pixel Off)
                displayList.add(ToggleOption("Alt Screen Off (Brightness -1)", useAltScreenOff) { useAltScreenOff = it; AppPreferences.setUseAltScreenOff(this, it) })
                displayList.add(ToggleOption("Shizuku Warning (Icon Alert)", showShizukuWarning) { showShizukuWarning = it; AppPreferences.setShowShizukuWarning(this, it); updateBubbleIcon() })
            }
        }
        drawerView!!.findViewById<RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
    }
    
    object CustomResInputOption
    data class LayoutOption(val name: String, val type: Int, val isCustomSaved: Boolean = false, val customRects: List<Rect>? = null)
    data class ResolutionOption(val name: String, val command: String, val index: Int)
    data class DpiOption(val currentDpi: Int)
    data class ProfileOption(val name: String, val isCurrent: Boolean, val layout: Int, val resIndex: Int, val dpi: Int, val apps: List<String>)
    data class FontSizeOption(val currentSize: Float)
    data class HeightOption(val currentPercent: Int)
    data class WidthOption(val currentPercent: Int)
    data class IconOption(val name: String)
    data class ActionOption(val name: String, val action: () -> Unit)
    data class ToggleOption(val name: String, var isEnabled: Boolean, val onToggle: (Boolean) -> Unit)
    data class TimeoutOption(val seconds: Int)

    inner class SelectedAppsAdapter : RecyclerView.Adapter<SelectedAppsAdapter.Holder>() {
        inner class Holder(v: View) : RecyclerView.ViewHolder(v) { val icon: ImageView = v.findViewById(R.id.selected_app_icon) }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder { return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_app, parent, false)) }
        override fun onBindViewHolder(holder: Holder, position: Int) { 
            val app = selectedAppsQueue[position]; if (position == reorderSelectionIndex) { holder.icon.setColorFilter(HIGHLIGHT_COLOR); holder.icon.alpha = 1.0f; holder.itemView.scaleX = 1.1f; holder.itemView.scaleY = 1.1f; holder.itemView.background = null } else { holder.icon.clearColorFilter(); holder.itemView.scaleX = 1.0f; holder.itemView.scaleY = 1.0f; holder.itemView.background = null; if (app.packageName == PACKAGE_BLANK) { holder.icon.setImageResource(R.drawable.ic_box_outline); holder.icon.alpha = 1.0f } else { try { holder.icon.setImageDrawable(packageManager.getApplicationIcon(app.packageName)) } catch (e: Exception) { holder.icon.setImageResource(R.drawable.ic_launcher_bubble) }; holder.icon.alpha = if (app.isMinimized) 0.4f else 1.0f } }
            holder.itemView.setOnClickListener { try { dismissKeyboardAndRestore(); if (reorderSelectionIndex != -1) { if (position == reorderSelectionIndex) { endReorderMode(false) } else { swapReorderItem(position) } } else { if (app.packageName != PACKAGE_BLANK) { app.isMinimized = !app.isMinimized; notifyItemChanged(position); if (isInstantMode) applyLayoutImmediate() } } } catch(e: Exception) {} }
            holder.itemView.setOnLongClickListener { if (isReorderTapEnabled) { startReorderMode(position); true } else { false } }
        }
        override fun getItemCount() = selectedAppsQueue.size
    }

    inner class RofiAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        inner class AppHolder(v: View) : RecyclerView.ViewHolder(v) { val icon: ImageView = v.findViewById(R.id.rofi_app_icon); val text: TextView = v.findViewById(R.id.rofi_app_text); val star: ImageView = v.findViewById(R.id.rofi_app_star) }
        inner class LayoutHolder(v: View) : RecyclerView.ViewHolder(v) { val nameInput: EditText = v.findViewById(R.id.layout_name); val btnSave: ImageView = v.findViewById(R.id.btn_save_profile); val btnExtinguish: ImageView = v.findViewById(R.id.btn_extinguish_item) }
        inner class DpiHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_dpi_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_dpi_plus); val input: EditText = v.findViewById(R.id.input_dpi_value) }
        inner class FontSizeHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_font_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_font_plus); val textVal: TextView = v.findViewById(R.id.text_font_value) }
        inner class HeightHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_height_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_height_plus); val textVal: TextView = v.findViewById(R.id.text_height_value) }
        inner class WidthHolder(v: View) : RecyclerView.ViewHolder(v) { val btnMinus: ImageView = v.findViewById(R.id.btn_width_minus); val btnPlus: ImageView = v.findViewById(R.id.btn_width_plus); val textVal: TextView = v.findViewById(R.id.text_width_value) }
        inner class ProfileRichHolder(v: View) : RecyclerView.ViewHolder(v) { val name: EditText = v.findViewById(R.id.profile_name_text); val details: TextView = v.findViewById(R.id.profile_details_text); val iconsContainer: LinearLayout = v.findViewById(R.id.profile_icons_container); val btnSave: ImageView = v.findViewById(R.id.btn_save_profile_rich) }
        inner class IconSettingHolder(v: View) : RecyclerView.ViewHolder(v) { val preview: ImageView = v.findViewById(R.id.icon_setting_preview) }
        inner class CustomResInputHolder(v: View) : RecyclerView.ViewHolder(v) { val inputW: EditText = v.findViewById(R.id.input_res_w); val inputH: EditText = v.findViewById(R.id.input_res_h); val btnSave: ImageView = v.findViewById(R.id.btn_save_res) }

        override fun getItemViewType(position: Int): Int { return when (displayList[position]) { is MainActivity.AppInfo -> 0; is LayoutOption -> 1; is ResolutionOption -> 1; is DpiOption -> 2; is ProfileOption -> 4; is FontSizeOption -> 3; is IconOption -> 5; is ToggleOption -> 1; is ActionOption -> 6; is HeightOption -> 7; is WidthOption -> 8; is CustomResInputOption -> 9; else -> 0 } }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder { return when (viewType) { 0 -> AppHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_app_rofi, parent, false)); 1 -> LayoutHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false)); 2 -> DpiHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dpi_custom, parent, false)); 3 -> FontSizeHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_font_size, parent, false)); 4 -> ProfileRichHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_rich, parent, false)); 5 -> IconSettingHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_icon_setting, parent, false)); 6 -> LayoutHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false)); 7 -> HeightHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_height_setting, parent, false)); 8 -> WidthHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_width_setting, parent, false)); 9 -> CustomResInputHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_custom_resolution, parent, false)); else -> AppHolder(View(parent.context)) } }
        private fun startRename(editText: EditText) { editText.isEnabled = true; editText.isFocusable = true; editText.isFocusableInTouchMode = true; editText.requestFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT) }
        private fun endRename(editText: EditText) { editText.isFocusable = false; editText.isFocusableInTouchMode = false; editText.isEnabled = false; val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(editText.windowToken, 0) }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = displayList[position]
            if (holder is AppHolder) holder.text.textSize = currentFontSize
            if (holder is LayoutHolder) holder.nameInput.textSize = currentFontSize
            if (holder is ProfileRichHolder) holder.name.textSize = currentFontSize

            if (holder is AppHolder && item is MainActivity.AppInfo) { holder.text.text = item.label; if (item.packageName == PACKAGE_BLANK) { holder.icon.setImageResource(R.drawable.ic_box_outline) } else { try { holder.icon.setImageDrawable(packageManager.getApplicationIcon(item.packageName)) } catch (e: Exception) { holder.icon.setImageResource(R.drawable.ic_launcher_bubble) } }; val isSelected = selectedAppsQueue.any { it.packageName == item.packageName }; if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.star.visibility = if (item.isFavorite) View.VISIBLE else View.GONE; holder.itemView.setOnClickListener { addToSelection(item) }; holder.itemView.setOnLongClickListener { toggleFavorite(item); refreshSearchList(); true } }
            else if (holder is ProfileRichHolder && item is ProfileOption) { holder.name.setText(item.name); holder.iconsContainer.removeAllViews(); if (!item.isCurrent) { for (pkg in item.apps.take(5)) { val iv = ImageView(holder.itemView.context); val lp = LinearLayout.LayoutParams(60, 60); lp.marginEnd = 8; iv.layoutParams = lp; if (pkg == PACKAGE_BLANK) { iv.setImageResource(R.drawable.ic_box_outline) } else { try { iv.setImageDrawable(packageManager.getApplicationIcon(pkg)) } catch (e: Exception) { iv.setImageResource(R.drawable.ic_launcher_bubble) } }; holder.iconsContainer.addView(iv) }; val info = "${getLayoutName(item.layout)} | ${getRatioName(item.resIndex)} | ${item.dpi}dpi"; holder.details.text = info; holder.details.visibility = View.VISIBLE; holder.btnSave.visibility = View.GONE; if (activeProfileName == item.name) { holder.itemView.setBackgroundResource(R.drawable.bg_item_active) } else { holder.itemView.setBackgroundResource(0) }; holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); loadProfile(item.name) }; holder.itemView.setOnLongClickListener { startRename(holder.name); true }; val saveProfileName = { val newName = holder.name.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameProfile(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); switchMode(MODE_PROFILES) } }; endRename(holder.name) }; holder.name.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveProfileName(); holder.name.clearFocus(); val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager; imm.hideSoftInputFromWindow(holder.name.windowToken, 0); updateDrawerHeight(false); true } else false }; holder.name.setOnFocusChangeListener { v, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus); if (!hasFocus) saveProfileName() } } else { holder.iconsContainer.removeAllViews(); holder.details.visibility = View.GONE; holder.btnSave.visibility = View.VISIBLE; holder.itemView.setBackgroundResource(0); holder.name.isEnabled = true; holder.name.isFocusable = true; holder.name.isFocusableInTouchMode = true; holder.itemView.setOnClickListener { saveProfile() }; holder.btnSave.setOnClickListener { saveProfile() } } }
            else if (holder is LayoutHolder) {
                holder.btnSave.visibility = View.GONE; holder.btnExtinguish.visibility = View.GONE
                if (item is LayoutOption) { holder.nameInput.setText(item.name); val isSelected = if (item.type == LAYOUT_CUSTOM_DYNAMIC) { item.type == selectedLayoutType && item.name == activeCustomLayoutName } else { item.type == selectedLayoutType && activeCustomLayoutName == null }; if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { selectLayout(item) }; if (item.isCustomSaved) { holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }; val saveLayoutName = { val newName = holder.nameInput.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameCustomLayout(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); if (activeCustomLayoutName == item.name) { activeCustomLayoutName = newName; AppPreferences.saveLastCustomLayoutName(holder.itemView.context, newName) }; switchMode(MODE_LAYOUTS) } }; endRename(holder.nameInput) }; holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveLayoutName(); true } else false }; holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveLayoutName() } } else { holder.nameInput.isEnabled = false; holder.nameInput.isFocusable = false; holder.nameInput.setTextColor(Color.WHITE) } }
                else if (item is ResolutionOption) { 
                    holder.nameInput.setText(item.name); if (item.index >= 100) { holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }; val saveResName = { val newName = holder.nameInput.text.toString().trim(); if (newName.isNotEmpty() && newName != item.name) { if (AppPreferences.renameCustomResolution(holder.itemView.context, item.name, newName)) { safeToast("Renamed to $newName"); switchMode(MODE_RESOLUTION) } }; endRename(holder.nameInput) }; holder.nameInput.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { saveResName(); true } else false }; holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveResName() } } else { holder.nameInput.isEnabled = false; holder.nameInput.isFocusable = false; holder.nameInput.setTextColor(Color.WHITE) }; val isSelected = (item.index == selectedResolutionIndex); if (isSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { applyResolution(item) } 
                }
                else if (item is IconOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { pickIcon() } }
                else if (item is ToggleOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); if (item.isEnabled) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) else holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.isEnabled = !item.isEnabled; item.onToggle(item.isEnabled); notifyItemChanged(position) } } 
                else if (item is ActionOption) { holder.nameInput.setText(item.name); holder.nameInput.isEnabled = false; holder.nameInput.setTextColor(Color.WHITE); holder.itemView.setBackgroundResource(R.drawable.bg_item_press); holder.itemView.setOnClickListener { dismissKeyboardAndRestore(); item.action() } }
            }
            else if (holder is CustomResInputHolder) {
                holder.btnSave.setOnClickListener { val wStr = holder.inputW.text.toString().trim(); val hStr = holder.inputH.text.toString().trim(); if (wStr.isNotEmpty() && hStr.isNotEmpty()) { val w = wStr.toIntOrNull(); val h = hStr.toIntOrNull(); if (w != null && h != null && w > 0 && h > 0) { val gcdVal = calculateGCD(w, h); val wRatio = w / gcdVal; val hRatio = h / gcdVal; val resString = "${w}x${h}"; val name = "$wRatio:$hRatio Custom ($resString)"; AppPreferences.saveCustomResolution(holder.itemView.context, name, resString); safeToast("Added $name"); dismissKeyboardAndRestore(); switchMode(MODE_RESOLUTION) } else { safeToast("Invalid numbers") } } else { safeToast("Input W and H") } }
                holder.inputW.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }; holder.inputH.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus) }
            }
            else if (holder is IconSettingHolder && item is IconOption) { try { val uriStr = AppPreferences.getIconUri(holder.itemView.context); if (uriStr != null) { val uri = Uri.parse(uriStr); val input = contentResolver.openInputStream(uri); val bitmap = BitmapFactory.decodeStream(input); input?.close(); holder.preview.setImageBitmap(bitmap) } else { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) } } catch(e: Exception) { holder.preview.setImageResource(R.drawable.ic_launcher_bubble) }; holder.itemView.setOnClickListener { pickIcon() } }
            else if (holder is DpiHolder && item is DpiOption) { 
                holder.input.setText(item.currentDpi.toString()); holder.input.setOnEditorActionListener { v, actionId, _ -> if (actionId == EditorInfo.IME_ACTION_DONE) { val valInt = v.text.toString().toIntOrNull(); if (valInt != null) { selectDpi(valInt); safeToast("DPI set to $valInt") }; dismissKeyboardAndRestore(); true } else false }; holder.input.setOnFocusChangeListener { _, hasFocus -> if (autoResizeEnabled) updateDrawerHeight(hasFocus); if (!hasFocus) { val valInt = holder.input.text.toString().toIntOrNull(); if (valInt != null && valInt != item.currentDpi) { selectDpi(valInt) } } }; holder.btnMinus.setOnClickListener { val v = holder.input.text.toString().toIntOrNull() ?: 160; val newVal = (v - 5).coerceAtLeast(50); holder.input.setText(newVal.toString()); selectDpi(newVal) }; holder.btnPlus.setOnClickListener { val v = holder.input.text.toString().toIntOrNull() ?: 160; val newVal = (v + 5).coerceAtMost(600); holder.input.setText(newVal.toString()); selectDpi(newVal) } 
            }
            else if (holder is FontSizeHolder && item is FontSizeOption) { holder.textVal.text = item.currentSize.toInt().toString(); holder.btnMinus.setOnClickListener { changeFontSize(item.currentSize - 1) }; holder.btnPlus.setOnClickListener { changeFontSize(item.currentSize + 1) } }
            else if (holder is HeightHolder && item is HeightOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerHeight(-5) }; holder.btnPlus.setOnClickListener { changeDrawerHeight(5) } }
            else if (holder is WidthHolder && item is WidthOption) { holder.textVal.text = item.currentPercent.toString(); holder.btnMinus.setOnClickListener { changeDrawerWidth(-5) }; holder.btnPlus.setOnClickListener { changeDrawerWidth(5) } }
        }
        override fun getItemCount() = displayList.size
    }
}
