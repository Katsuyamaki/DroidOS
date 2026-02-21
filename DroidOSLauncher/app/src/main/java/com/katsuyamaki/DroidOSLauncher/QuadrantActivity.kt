package com.katsuyamaki.DroidOSLauncher

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class QuadrantActivity : AppCompatActivity() {

    companion object {
        const val Q1_KEY = "Q1_PACKAGE"
        const val Q2_KEY = "Q2_PACKAGE"
        const val Q3_KEY = "Q3_PACKAGE"
        const val Q4_KEY = "Q4_PACKAGE"
    }

    private var q1Package: String? = null
    private var q2Package: String? = null
    private var q3Package: String? = null
    private var q4Package: String? = null

    private lateinit var q1Button: Button
    private lateinit var q2Button: Button
    private lateinit var q3Button: Button
    private lateinit var q4Button: Button
    private lateinit var launchButton: Button

    private var currentQuadrant = -1
    private var hasShizukuPermission = false

    private val REQUEST_PERMISSION_RESULT_LISTENER =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == ShizukuHelper.SHIZUKU_PERMISSION_REQUEST_CODE) {
                hasShizukuPermission = grantResult == PackageManager.PERMISSION_GRANTED
                checkShizukuPermission() // Update status
            }
        }

    private val appSelectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val packageName = result.data?.getStringExtra(MainActivity.SELECTED_APP_PACKAGE)
            if (packageName != null) {
                val simple = AppPreferences.getSimpleName(packageName)
                when (currentQuadrant) {
                    1 -> {
                        q1Package = packageName
                        q1Button.text = "Q1: $simple"
                        AppPreferences.savePackage(this, Q1_KEY, packageName)
                    }
                    2 -> {
                        q2Package = packageName
                        q2Button.text = "Q2: $simple"
                        AppPreferences.savePackage(this, Q2_KEY, packageName)
                    }
                    3 -> {
                        q3Package = packageName
                        q3Button.text = "Q3: $simple"
                        AppPreferences.savePackage(this, Q3_KEY, packageName)
                    }
                    4 -> {
                        q4Package = packageName
                        q4Button.text = "Q4: $simple"
                        AppPreferences.savePackage(this, Q4_KEY, packageName)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
        setContentView(R.layout.activity_quadrant)

        q1Button = findViewById(R.id.q1_button)
        q2Button = findViewById(R.id.q2_button)
        q3Button = findViewById(R.id.q3_button)
        q4Button = findViewById(R.id.q4_button)
        launchButton = findViewById(R.id.launch_button)

        loadSavedApps()

        q1Button.setOnClickListener { currentQuadrant = 1; launchAppPicker() }
        q2Button.setOnClickListener { currentQuadrant = 2; launchAppPicker() }
        q3Button.setOnClickListener { currentQuadrant = 3; launchAppPicker() }
        q4Button.setOnClickListener { currentQuadrant = 4; launchAppPicker() }

        launchButton.setOnClickListener {
            if (q1Package != null && q2Package != null &&
                q3Package != null && q4Package != null) {
                launchQuadrantApps()
            } else {
                Toast.makeText(this, "Select all 4 apps.", Toast.LENGTH_SHORT).show()
            }
        }
        checkShizukuPermission()
    }

    private fun loadSavedApps() {
        q1Package = AppPreferences.loadPackage(this, Q1_KEY)
        q1Button.text = "Q1: ${AppPreferences.getSimpleName(q1Package)}"
        q2Package = AppPreferences.loadPackage(this, Q2_KEY)
        q2Button.text = "Q2: ${AppPreferences.getSimpleName(q2Package)}"
        q3Package = AppPreferences.loadPackage(this, Q3_KEY)
        q3Button.text = "Q3: ${AppPreferences.getSimpleName(q3Package)}"
        q4Package = AppPreferences.loadPackage(this, Q4_KEY)
        q4Button.text = "Q4: ${AppPreferences.getSimpleName(q4Package)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
    }

    private fun checkShizukuPermission() {
        if (ShizukuHelper.isShizukuAvailable()) {
            if (ShizukuHelper.hasPermission()) {
                hasShizukuPermission = true
            } else {
                ShizukuHelper.requestPermission()
            }
        }
    }

    private fun launchAppPicker() {
        appSelectLauncher.launch(Intent(this, MainActivity::class.java))
    }

    private fun launchQuadrantApps() {
        val metrics = windowManager.maximumWindowMetrics
        val w = metrics.bounds.width()
        val h = metrics.bounds.height()

        val q1 = Rect(0, 0, w / 2, h / 2)
        val q2 = Rect(w / 2, 0, w, h / 2)
        val q3 = Rect(0, h / 2, w / 2, h)
        val q4 = Rect(w / 2, h / 2, w, h)

        // Kill all first (Synchronously wait for shell command)
        if (hasShizukuPermission) {
            // Launch in background thread to avoid freezing UI if kill takes time
            Thread {
                ShizukuHelper.killApp(q1Package!!)
                ShizukuHelper.killApp(q2Package!!)
                ShizukuHelper.killApp(q3Package!!)
                ShizukuHelper.killApp(q4Package!!)
                
                // IMPORTANT: Wait for OS to clean up windows
                try { Thread.sleep(400) } catch (e: InterruptedException) {}

                // Back to UI thread to launch
                runOnUiThread {
                    launchAppIntent(q1Package!!, q1)
                    launchAppIntent(q2Package!!, q2)
                    launchAppIntent(q3Package!!, q3)
                    launchAppIntent(q4Package!!, q4)
                }
            }.start()
        } else {
            // Fallback (wont work well for moving windows)
            launchAppIntent(q1Package!!, q1)
            launchAppIntent(q2Package!!, q2)
            launchAppIntent(q3Package!!, q3)
            launchAppIntent(q4Package!!, q4)
        }
    }

    private fun launchAppIntent(packageName: String, bounds: Rect) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent == null) return

            // CLEAR_TOP helps reset the task state
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            
            val options = ActivityOptions.makeBasic().setLaunchBounds(bounds)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
        }
    }
}
