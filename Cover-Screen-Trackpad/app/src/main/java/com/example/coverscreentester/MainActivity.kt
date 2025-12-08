package com.example.coverscreentester

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Please grant Overlay Permission", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            finish()
            return
        }

        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(0)
        }

        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "Please enable DroidOS Trackpad in Accessibility Settings", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            return
        }

        Toast.makeText(this, "Trackpad Service Already Running!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val expectedComponentName = ComponentName(this, OverlayService::class.java)
        for (service in enabledServices) {
            val enabledServiceName = service.resolveInfo.serviceInfo.let {
                ComponentName(it.packageName, it.name)
            }
            if (enabledServiceName == expectedComponentName) {
                return true
            }
        }
        return false
    }
}
