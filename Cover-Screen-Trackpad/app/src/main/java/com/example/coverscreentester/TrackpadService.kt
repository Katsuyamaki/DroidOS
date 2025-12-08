package com.example.coverscreentester

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

class TrackpadService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Layout Params: TYPE_APPLICATION_OVERLAY is crucial for Android 8.0+
        // FLAG_NOT_FOCUSABLE ensures the overlay doesn't eat physical button presses (like Power/Vol)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        try {
            val inflater = LayoutInflater.from(this)
            overlayView = inflater.inflate(R.layout.service_overlay, null)

            // Setup Safety Exit Button
            val closeBtn = overlayView?.findViewById<Button>(R.id.btn_close)
            closeBtn?.setOnClickListener {
                stopSelf()
            }

            // Setup Trackpad Touch Listener
            val trackpadView = overlayView?.findViewById<View>(R.id.view_trackpad)
            trackpadView?.setOnTouchListener { _, _ ->
                // This consumes the touch event so it doesn't pass through to the launcher
                // We will add the Shizuku injection logic here next.
                true
            }

            windowManager.addView(overlayView, params)
            Toast.makeText(this, "Trackpad Overlay Started", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (overlayView != null) {
            try {
                windowManager.removeView(overlayView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            overlayView = null
        }
        Toast.makeText(this, "Trackpad Overlay Stopped", Toast.LENGTH_SHORT).show()
    }
}
