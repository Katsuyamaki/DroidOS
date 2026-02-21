package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import android.hardware.display.DisplayManager
import android.os.Build
import android.content.Context

class TrackpadService : Service() {

    private val TAG = "TrackpadService" // Define TAG

    private lateinit var windowManager: WindowManager
    private var trackpadView: View? = null 
    private var currentDisplayId: Int = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /* * FUNCTION: onStartCommand
     * SUMMARY: Handles the "Recall" logic. If the service is already running but 
     * receives a different display ID, it destroys the old view and recreates 
     * it on the new display context.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val targetDisplayId = intent?.getIntExtra("TARGET_DISPLAY_ID", currentDisplayId) ?: currentDisplayId


        if (trackpadView != null) {
            // If display has changed, migrate the view
            if (targetDisplayId != currentDisplayId) {

                try {
                    windowManager.removeView(trackpadView)
                } catch (e: Exception) {
                }
                trackpadView = null
                setupUI(targetDisplayId)
            }
        } else {
            // First time initialization

            setupUI(targetDisplayId)
        }
        
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        setupShizuku() // This will be defined later
    }

    /* * FUNCTION: setupUI
     * SUMMARY: Creates a WindowManager context bound specifically to the 
     * requested displayId. This forces the view to the physical screen.
     */
    private fun setupUI(displayId: Int) {
        currentDisplayId = displayId
        val dm = getSystemService(Context.DISPLAY_SERVICE) as android.hardware.display.DisplayManager
        val targetDisplay = dm.getDisplay(displayId) ?: dm.getDisplay(0)
        
        // CRITICAL: Create context for the specific physical display
        val displayContext = createDisplayContext(targetDisplay)
        windowManager = displayContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val inflater = android.view.LayoutInflater.from(displayContext)
        trackpadView = inflater.inflate(R.layout.layout_trackpad, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) 
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
            else 
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            android.graphics.PixelFormat.TRANSLUCENT
        )

        setupTrackpadLogic(trackpadView!!)
        windowManager.addView(trackpadView, params)
    }

    private fun setupTrackpadLogic(view: View) {
        // Setup Safety Exit Button
        val closeBtn = view.findViewById<Button>(R.id.btn_close)
        closeBtn?.setOnClickListener {
            stopSelf()
        }

        // Setup Trackpad Touch Listener
        val trackpadArea = view.findViewById<View>(R.id.view_trackpad)
        trackpadArea?.setOnTouchListener { _, _ ->
            // This consumes the touch event so it doesn't pass through to the launcher
            // We will add the Shizuku injection logic here next.
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (trackpadView != null) {
            try {
                windowManager.removeView(trackpadView)
            } catch (e: Exception) {

            }
            trackpadView = null
        }
        Toast.makeText(this, "Trackpad Overlay Stopped", Toast.LENGTH_SHORT).show()
    }

    private fun setupShizuku() {
        // Placeholder for Shizuku setup logic
        // This will be implemented when Shizuku functionality is added.

    }
}
