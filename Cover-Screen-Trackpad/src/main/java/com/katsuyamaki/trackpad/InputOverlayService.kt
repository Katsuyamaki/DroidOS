package com.katsuyamaki.trackpad

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import rikka.shizuku.Shizuku
import java.io.OutputStream

class InputOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    
    // handlers
    private val uiHandler = Handler(Looper.getMainLooper())
    private val inputThread = HandlerThread("InputWorker").apply { start() }
    private val backgroundHandler = Handler(inputThread.looper)

    // Persistent Shell
    private var shellProcess: Process? = null
    private var shellOutputStream: OutputStream? = null

    // Repeat Logic
    private var isRepeating = false
    private var repeatRunnable: Runnable? = null
    private val initialRepeatDelay = 400L
    private val repeatInterval = 50L 

    // Double Space Logic
    private var lastSpacePressTime: Long = 0
    private val doubleSpaceThreshold = 300L

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        initializeShell()
        setupOverlay()
    }

    private fun initializeShell() {
        backgroundHandler.post {
            try {
                // Check permission first
                if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    // Open a persistent 'sh' process. This is much faster than calling 'input' repeatedly.
                    shellProcess = Shizuku.newProcess(arrayOf("sh"), null, null)
                    shellOutputStream = shellProcess?.outputStream
                    Log.d("DroidOS", "Shizuku Shell Initialized")
                } else {
                    Log.e("DroidOS", "Shizuku permission not granted!")
                }
            } catch (e: Exception) {
                Log.e("DroidOS", "Failed to init Shizuku shell", e)
            }
        }
    }

    private fun setupOverlay() {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.y = 0 

        val inflater = LayoutInflater.from(this)
        overlayView = inflater.inflate(R.layout.layout_input_overlay, null)

        setupInputButtons(overlayView)
        setupTrackpad(overlayView)

        try {
            windowManager.addView(overlayView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupTrackpad(view: View) {
        // Placeholder for mouse logic
    }

    private fun setupInputButtons(view: View) {
        val btnEsc = view.findViewById<View>(R.id.btn_esc)
        val btnTab = view.findViewById<View>(R.id.btn_tab)
        val btnUp = view.findViewById<View>(R.id.btn_up)
        val btnDown = view.findViewById<View>(R.id.btn_down)
        val btnLeft = view.findViewById<View>(R.id.btn_left)
        val btnRight = view.findViewById<View>(R.id.btn_right)
        val btnEnter = view.findViewById<View>(R.id.btn_enter)
        val btnSpace = view.findViewById<View>(R.id.btn_space)
        val btnBackspace = view.findViewById<View>(R.id.btn_backspace)
        val btnClose = view.findViewById<View>(R.id.btn_close)

        setupRepeatableKey(btnEsc, KeyEvent.KEYCODE_ESCAPE)
        setupRepeatableKey(btnTab, KeyEvent.KEYCODE_TAB)
        setupRepeatableKey(btnUp, KeyEvent.KEYCODE_DPAD_UP)
        setupRepeatableKey(btnDown, KeyEvent.KEYCODE_DPAD_DOWN)
        setupRepeatableKey(btnLeft, KeyEvent.KEYCODE_DPAD_LEFT)
        setupRepeatableKey(btnRight, KeyEvent.KEYCODE_DPAD_RIGHT)
        setupRepeatableKey(btnEnter, KeyEvent.KEYCODE_ENTER)
        setupRepeatableKey(btnBackspace, KeyEvent.KEYCODE_DEL)

        setupSpaceKey(btnSpace)

        btnClose.setOnClickListener {
            stopSelf()
        }
    }

    private fun setupSpaceKey(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.isPressed = true
                    val currentTime = System.currentTimeMillis()
                    
                    if (currentTime - lastSpacePressTime < doubleSpaceThreshold) {
                        Log.d("DroidOS", "Double Space Detected")
                        // Double Tap: DELETE previous space, period, space
                        // Send commands as a batch
                        injectCommand("input keyevent 67 && input keyevent 56 && input keyevent 62")
                        lastSpacePressTime = 0
                    } else {
                        // Single Tap: Space
                        injectKey(KeyEvent.KEYCODE_SPACE)
                        lastSpacePressTime = currentTime
                        
                        // Start Repeat
                        isRepeating = true
                        repeatRunnable = object : Runnable {
                            override fun run() {
                                if (isRepeating) {
                                    injectKey(KeyEvent.KEYCODE_SPACE)
                                    uiHandler.postDelayed(this, repeatInterval)
                                }
                            }
                        }
                        uiHandler.postDelayed(repeatRunnable!!, initialRepeatDelay)
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.isPressed = false
                    isRepeating = false
                    repeatRunnable?.let { uiHandler.removeCallbacks(it) }
                    v.performClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRepeatableKey(view: View, keyCode: Int) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.isPressed = true
                    isRepeating = true
                    
                    // Inject initial
                    injectKey(keyCode)
                    
                    // Setup Repeat
                    repeatRunnable = object : Runnable {
                        override fun run() {
                            if (isRepeating) {
                                injectKey(keyCode)
                                uiHandler.postDelayed(this, repeatInterval)
                            }
                        }
                    }
                    uiHandler.postDelayed(repeatRunnable!!, initialRepeatDelay)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.isPressed = false
                    isRepeating = false
                    repeatRunnable?.let { uiHandler.removeCallbacks(it) }
                    v.performClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun injectKey(keyCode: Int) {
        injectCommand("input keyevent $keyCode")
    }

    private fun injectCommand(command: String) {
        backgroundHandler.post {
            try {
                if (shellOutputStream == null) {
                    initializeShell()
                    // Small delay to let shell start if it was null
                    Thread.sleep(50) 
                }
                
                shellOutputStream?.let { stream ->
                    stream.write((command + "\n").toByteArray())
                    stream.flush()
                } ?: run {
                    // Fallback if stream failed to open
                    Log.e("DroidOS", "Shell Stream null, fallback to newProcess")
                    Shizuku.newProcess(arrayOf("sh", "-c", command), null, null).waitFor()
                }
            } catch (e: Exception) {
                Log.e("DroidOS", "Injection failed: $command", e)
                // Reset shell on error
                try { shellProcess?.destroy() } catch(ex:Exception){}
                shellOutputStream = null
                shellProcess = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRepeating = false
        uiHandler.removeCallbacksAndMessages(null)
        inputThread.quitSafely()
        
        try {
            shellOutputStream?.close()
            shellProcess?.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }
}
