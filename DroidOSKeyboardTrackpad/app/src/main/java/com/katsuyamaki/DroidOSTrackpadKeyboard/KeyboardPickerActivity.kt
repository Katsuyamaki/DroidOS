package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast

class KeyboardPickerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showCustomKeyboardPicker()
    }
    
    private fun showCustomKeyboardPicker() {
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val enabledIMEs = imm.enabledInputMethodList.toMutableList()
            
            // Check if DroidOS IME is enabled
            val droidOSPackage = "com.katsuyamaki.DroidOSTrackpadKeyboard"
            val isDroidOSEnabled = enabledIMEs.any { it.packageName == droidOSPackage }
            
            // If DroidOS not enabled, add it as a placeholder from all IMEs
            var droidOSPlaceholderIndex = -1
            if (!isDroidOSEnabled) {
                val allIMEs = imm.inputMethodList
                val droidOSIME = allIMEs.find { it.packageName == droidOSPackage }
                if (droidOSIME != null) {
                    enabledIMEs.add(0, droidOSIME) // Add at top
                    droidOSPlaceholderIndex = 0
                }
            }
            
            if (enabledIMEs.isEmpty()) {
                Toast.makeText(this, "No keyboards found", Toast.LENGTH_SHORT).show()
                openKeyboardSettings()
                return
            }
            
            // Build list data
            val names = enabledIMEs.map { it.loadLabel(packageManager).toString() }
            val ids = enabledIMEs.map { it.id }
            val packages = enabledIMEs.map { it.packageName }
            
            // Find current keyboard
            val currentId = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
            val currentIndex = ids.indexOf(currentId).coerceAtLeast(0)
            
            // Custom adapter for styled items
            val adapter = object : ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, names) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)
                    
                    // Match overlay menu font size (14sp)
                    textView.textSize = 14f
                    
                    val isDisabled = position == droidOSPlaceholderIndex && !isDroidOSEnabled
                    
                    if (isDisabled) {
                        // Grey out and add red note
                        val name = names[position]
                        val note = "\n⚠ Not enabled - tap Settings below"
                        val spannable = SpannableString("$name$note")
                        
                        // Grey for name
                        spannable.setSpan(ForegroundColorSpan(Color.GRAY), 0, name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        // Red and smaller for note
                        spannable.setSpan(ForegroundColorSpan(Color.RED), name.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(RelativeSizeSpan(0.75f), name.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(StyleSpan(Typeface.ITALIC), name.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        
                        textView.text = spannable
                        view.alpha = 0.7f
                    } else {
                        textView.text = names[position]
                        textView.setTextColor(Color.WHITE)
                        view.alpha = 1.0f
                    }
                    
                    return view
                }
                
                override fun isEnabled(position: Int): Boolean {
                    // Disable clicking on the not-enabled DroidOS entry
                    return !(position == droidOSPlaceholderIndex && !isDroidOSEnabled)
                }
            }
            
            val dialog = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setTitle("Select Keyboard")
                .setSingleChoiceItems(adapter, currentIndex) { dlg, which ->
                    // Skip if disabled item
                    if (which == droidOSPlaceholderIndex && !isDroidOSEnabled) {
                        Toast.makeText(this, "Enable in Settings first", Toast.LENGTH_SHORT).show()
                        return@setSingleChoiceItems
                    }
                    
                    val selectedName = names[which]
                    val selectedId = ids[which]
                    
                    if (which == currentIndex) {
                        Toast.makeText(this, "$selectedName is already active", Toast.LENGTH_SHORT).show()
                        dlg.dismiss()
                        finish()
                        return@setSingleChoiceItems
                    }
                    
                    // Send broadcast to OverlayService to switch keyboard via Shizuku
                    val switchIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SWITCH_KEYBOARD")
                    switchIntent.setPackage(packageName)
                    switchIntent.putExtra("IME_ID", selectedId)
                    switchIntent.putExtra("IME_NAME", selectedName)
                    sendBroadcast(switchIntent)
                    
                    Toast.makeText(this, "Switching to $selectedName...", Toast.LENGTH_SHORT).show()
                    dlg.dismiss()
                    finish()
                }
                .setNegativeButton("Cancel") { dlg, _ ->
                    dlg.dismiss()
                    finish()
                }
                .setNeutralButton("Settings") { dlg, _ ->
                    dlg.dismiss()
                    openKeyboardSettings()
                }
                .setOnCancelListener {
                    finish()
                }
                .create()
            
            // Set window type to appear above overlay keyboard but below bubble
            dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            dialog.show()
                
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading keyboards", Toast.LENGTH_SHORT).show()
            openKeyboardSettings()
        }
    }
    
    private fun openKeyboardSettings() {
        try {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open settings", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}
