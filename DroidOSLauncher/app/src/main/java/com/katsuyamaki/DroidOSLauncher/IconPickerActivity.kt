package com.katsuyamaki.DroidOSLauncher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class IconPickerActivity : ComponentActivity() {

    private val pickImage = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                // Persist permission so we can read this after reboot
                contentResolver.takePersistableUriPermission(
                    uri, 
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                AppPreferences.saveIconUri(this, uri.toString())
                
                // Notify Service to update
                sendBroadcast(Intent("com.katsuyamaki.DroidOSLauncher.UPDATE_ICON"))
            } catch (e: Exception) {

            }
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Invisible activity
        
        try {
            pickImage.launch(arrayOf("image/*"))
        } catch (e: Exception) {
            finish()
        }
    }
}
