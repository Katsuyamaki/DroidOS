package com.katsuyamaki.DroidOSLauncher

import android.app.Activity
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class SplitActivity : AppCompatActivity() {

    companion object {
        const val SPLIT1_KEY = "SPLIT1_PACKAGE"
        const val SPLIT2_KEY = "SPLIT2_PACKAGE"
        const val TAG = "SplitActivity"
    }

    private var app1Package: String? = null
    private var app2Package: String? = null
    private lateinit var app1Button: Button
    private lateinit var app2Button: Button
    private lateinit var launchButton: Button
    
    private var currentApp = -1
    private var shellService: IShellService? = null
    private var isBound = false

    // Connection to the Shell Service (Running inside Shizuku)
    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {

            shellService = IShellService.Stub.asInterface(binder)
            isBound = true
            launchButton.text = "LAUNCH SPLIT (Ready)"
        }

        override fun onServiceDisconnected(name: ComponentName?) {

            shellService = null
            isBound = false
            launchButton.text = "LAUNCH SPLIT (Disconnected)"
        }
    }

    // Listener 1: When Shizuku itself starts/connects
    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        checkShizukuStatus()
    }

    // Listener 2: When user grants permission
    private val requestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
        checkShizukuStatus()
    }

    private val appSelectLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val pkg = result.data?.getStringExtra(MainActivity.SELECTED_APP_PACKAGE)
            if (pkg != null) {
                val simple = AppPreferences.getSimpleName(pkg)
                if (currentApp == 1) {
                    app1Package = pkg
                    app1Button.text = "App 1: $simple"
                    AppPreferences.savePackage(this, SPLIT1_KEY, pkg)
                } else if (currentApp == 2) {
                    app2Package = pkg
                    app2Button.text = "App 2: $simple"
                    AppPreferences.savePackage(this, SPLIT2_KEY, pkg)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split)

        app1Button = findViewById(R.id.app1_button)
        app2Button = findViewById(R.id.app2_button)
        launchButton = findViewById(R.id.launch_button_split)
        
        loadSavedApps()

        app1Button.setOnClickListener { currentApp = 1; launchAppPicker() }
        app2Button.setOnClickListener { currentApp = 2; launchAppPicker() }

        launchButton.setOnClickListener {
            if (app1Package != null && app2Package != null) {
                launchSplitApps()
            } else {
                Toast.makeText(this, "Select two apps.", Toast.LENGTH_SHORT).show()
            }
        }

        // Add Listeners
        Shizuku.addBinderReceivedListener(binderReceivedListener)
        Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        
        // Initial Check
        checkShizukuStatus()
    }

    private fun checkShizukuStatus() {
        if (Shizuku.getBinder() == null) {
            // Shizuku not attached yet
            launchButton.text = "Waiting for Shizuku..."
            return
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            bindShizukuService()
        } else {
            launchButton.text = "Requesting Permission..."
            Shizuku.requestPermission(0)
        }
    }

    private fun bindShizukuService() {
        if (isBound) return
        
        try {
            val component = ComponentName(packageName, ShellUserService::class.java.name)
            // VERSION CODE 1 (Matches BuildConfig.VERSION_CODE usually)
            ShizukuBinder.bind(component, userServiceConnection, true, 1)
            launchButton.text = "Binding..."
        } catch (e: Exception) {
            launchButton.text = "Bind Failed"
        }
    }

    private fun loadSavedApps() {
        app1Package = AppPreferences.loadPackage(this, SPLIT1_KEY)
        app1Button.text = "App 1: ${AppPreferences.getSimpleName(app1Package)}"
        app2Package = AppPreferences.loadPackage(this, SPLIT2_KEY)
        app2Button.text = "App 2: ${AppPreferences.getSimpleName(app2Package)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
        
        if (isBound) {
            val component = ComponentName(packageName, ShellUserService::class.java.name)
            ShizukuBinder.unbind(component, userServiceConnection)
            isBound = false
        }
    }

    private fun launchAppPicker() {
        appSelectLauncher.launch(Intent(this, MainActivity::class.java))
    }

    private fun launchSplitApps() {
        val metrics = windowManager.maximumWindowMetrics
        val w = metrics.bounds.width()
        val h = metrics.bounds.height()
        val left = Rect(0, 0, w / 2, h)
        val right = Rect(w / 2, 0, w, h)

        if (isBound && shellService != null) {
            launchButton.text = "LAUNCHING..."
            Thread {
                try {
                    shellService?.forceStop(app1Package)
                    shellService?.forceStop(app2Package)
                    Thread.sleep(400)
                    runOnUiThread {
                        launchApp(app1Package!!, left)
                        launchApp(app2Package!!, right)
                        launchButton.text = "LAUNCH SPLIT"
                    }
                } catch (e: Exception) {
                }
            }.start()
        } else {
            Toast.makeText(this, "Shizuku NOT READY. Launching anyway.", Toast.LENGTH_LONG).show()
            checkShizukuStatus() // Try connecting again
            launchApp(app1Package!!, left)
            launchApp(app2Package!!, right)
        }
    }

    private fun launchApp(packageName: String, bounds: Rect) {
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
