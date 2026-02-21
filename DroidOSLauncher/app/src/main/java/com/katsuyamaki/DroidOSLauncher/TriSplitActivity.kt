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

class TriSplitActivity : AppCompatActivity() {

    companion object {
        const val TRI1_KEY = "TRI1_PACKAGE"
        const val TRI2_KEY = "TRI2_PACKAGE"
        const val TRI3_KEY = "TRI3_PACKAGE"
    }

    private var app1Package: String? = null
    private var app2Package: String? = null
    private var app3Package: String? = null

    private lateinit var app1Button: Button
    private lateinit var app2Button: Button
    private lateinit var app3Button: Button
    private lateinit var launchButton: Button

    private var currentApp = -1
    private var hasShizukuPermission = false

    private val REQUEST_PERMISSION_RESULT_LISTENER =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == ShizukuHelper.SHIZUKU_PERMISSION_REQUEST_CODE) {
                hasShizukuPermission = grantResult == PackageManager.PERMISSION_GRANTED
                checkShizukuPermission()
            }
        }

    private val appSelectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val pkg = result.data?.getStringExtra(MainActivity.SELECTED_APP_PACKAGE)
            if (pkg != null) {
                val simple = AppPreferences.getSimpleName(pkg)
                when (currentApp) {
                    1 -> {
                        app1Package = pkg
                        app1Button.text = "App 1: $simple"
                        AppPreferences.savePackage(this, TRI1_KEY, pkg)
                    }
                    2 -> {
                        app2Package = pkg
                        app2Button.text = "App 2: $simple"
                        AppPreferences.savePackage(this, TRI2_KEY, pkg)
                    }
                    3 -> {
                        app3Package = pkg
                        app3Button.text = "App 3: $simple"
                        AppPreferences.savePackage(this, TRI3_KEY, pkg)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
        setContentView(R.layout.activity_tri_split)

        app1Button = findViewById(R.id.app1_button_tri)
        app2Button = findViewById(R.id.app2_button_tri)
        app3Button = findViewById(R.id.app3_button_tri)
        launchButton = findViewById(R.id.launch_button_tri_split)

        loadSavedApps()

        app1Button.setOnClickListener { currentApp = 1; pickApp() }
        app2Button.setOnClickListener { currentApp = 2; pickApp() }
        app3Button.setOnClickListener { currentApp = 3; pickApp() }

        launchButton.setOnClickListener {
            if (app1Package != null && app2Package != null && app3Package != null) {
                launchSplitApps()
            } else {
                Toast.makeText(this, "Select three apps.", Toast.LENGTH_SHORT).show()
            }
        }
        checkShizukuPermission()
    }

    private fun loadSavedApps() {
        app1Package = AppPreferences.loadPackage(this, TRI1_KEY)
        app1Button.text = "App 1: ${AppPreferences.getSimpleName(app1Package)}"
        app2Package = AppPreferences.loadPackage(this, TRI2_KEY)
        app2Button.text = "App 2: ${AppPreferences.getSimpleName(app2Package)}"
        app3Package = AppPreferences.loadPackage(this, TRI3_KEY)
        app3Button.text = "App 3: ${AppPreferences.getSimpleName(app3Package)}"
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

    private fun pickApp() {
        appSelectLauncher.launch(Intent(this, MainActivity::class.java))
    }

    private fun launchSplitApps() {
        val metrics = windowManager.maximumWindowMetrics
        val w = metrics.bounds.width()
        val h = metrics.bounds.height()
        val colWidth = w / 3

        val left = Rect(0, 0, colWidth, h)
        val middle = Rect(colWidth, 0, colWidth * 2, h)
        val right = Rect(colWidth * 2, 0, w, h)

        if (hasShizukuPermission) {
            Thread {
                ShizukuHelper.killApp(app1Package!!)
                ShizukuHelper.killApp(app2Package!!)
                ShizukuHelper.killApp(app3Package!!)
                try { Thread.sleep(400) } catch (e: InterruptedException) {}
                runOnUiThread {
                    launchAppIntent(app1Package!!, left)
                    launchAppIntent(app2Package!!, middle)
                    launchAppIntent(app3Package!!, right)
                }
            }.start()
        } else {
            launchAppIntent(app1Package!!, left)
            launchAppIntent(app2Package!!, middle)
            launchAppIntent(app3Package!!, right)
        }
    }

    private fun launchAppIntent(packageName: String, bounds: Rect) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent == null) return

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val options = ActivityOptions.makeBasic().setLaunchBounds(bounds)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
        }
    }
}
