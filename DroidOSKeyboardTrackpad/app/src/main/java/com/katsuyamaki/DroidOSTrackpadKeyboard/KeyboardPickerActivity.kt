package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast

class KeyboardPickerActivity : Activity() {

    private var resultReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestKeyboardList()
    }

    override fun onDestroy() {
        resultReceiver?.let {
            try { unregisterReceiver(it) } catch (_: Exception) {}
        }
        super.onDestroy()
    }

    private fun requestKeyboardList() {
        // Minimize tiled app windows so picker isn't hidden behind them
        try {
            val minimizeIntent = Intent("com.katsuyamaki.DroidOSLauncher.WINDOW_MANAGER")
            minimizeIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
            minimizeIntent.putExtra("COMMAND", "MINIMIZE_ALL")
            sendBroadcast(minimizeIntent)
        } catch (_: Exception) {}

        // Register receiver for the result from OverlayService
        resultReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val allLines = intent.getStringExtra("ALL_IMES") ?: ""
                val enabledLines = intent.getStringExtra("ENABLED_IMES") ?: ""
                if (allLines.isEmpty()) {
                    Toast.makeText(this@KeyboardPickerActivity, "No keyboards found (Shizuku?)", Toast.LENGTH_SHORT).show()
                    openKeyboardSettings()
                    return
                }
                showPickerFromShell(allLines, enabledLines)
            }
        }
        registerReceiver(resultReceiver, IntentFilter("com.katsuyamaki.DroidOSTrackpadKeyboard.LIST_KEYBOARDS_RESULT"), Context.RECEIVER_NOT_EXPORTED)

        // Ask OverlayService to query via Shizuku
        val req = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.LIST_KEYBOARDS")
        req.setPackage(packageName)
        sendBroadcast(req)
    }

    private fun showPickerFromShell(allImeStr: String, enabledImeStr: String) {
        try {
            val pm = packageManager
            val enabledSet = enabledImeStr.lines().map { it.trim() }.filter { it.isNotEmpty() }.toSet()

            // Parse "ime list -a -s" output: one IME id per line
            val allIds = allImeStr.lines().map { it.trim() }.filter { it.isNotEmpty() && it.contains("/") }

            val imeIds = mutableListOf<String>()
            val imeNames = mutableListOf<String>()
            val imeEnabled = mutableListOf<Boolean>()

            val droidOSPackage = "com.katsuyamaki.DroidOSTrackpadKeyboard"

            for (imeId in allIds) {
                val isDroidOS = imeId.startsWith(droidOSPackage)
                val enabled = enabledSet.contains(imeId)

                // Skip disabled keyboards unless they're ours
                if (!enabled && !isDroidOS) continue

                // Resolve display name: service label > app label > raw id
                val label = if (isDroidOS) {
                    "DroidOS Keyboard Toolbar"
                } else try {
                    val comp = android.content.ComponentName.unflattenFromString(imeId)
                    if (comp != null) {
                        val si = pm.getServiceInfo(comp, 0)
                        val sl = si.loadLabel(pm).toString()
                        if (sl != si.packageName && sl != si.name) sl
                        else pm.getApplicationLabel(pm.getApplicationInfo(comp.packageName, 0)).toString()
                    } else imeId
                } catch (_: Exception) { imeId.substringBefore("/").substringAfterLast(".") }

                imeIds.add(imeId)
                imeNames.add(label)
                imeEnabled.add(enabled)
            }

            if (imeIds.isEmpty()) {
                Toast.makeText(this, "No keyboards found", Toast.LENGTH_SHORT).show()
                openKeyboardSettings()
                return
            }

            // Sort: DroidOS first, then alphabetically
            val indices = imeIds.indices.sortedWith(compareBy<Int> {
                val id = imeIds[it]
                if (id.startsWith(droidOSPackage)) 0 else if (imeEnabled[it]) 1 else 2
            }.thenBy { imeNames[it] })

            val names = indices.map { imeNames[it] }
            val ids = indices.map { imeIds[it] }
            val isEnabled = indices.map { imeEnabled[it] }

            // Find current keyboard
            val activeImeId = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
            val currentIndex = ids.indexOf(activeImeId).coerceAtLeast(0)

            // Custom adapter with compact styled items
            val adapter = object : ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, names) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)

                    // Compact: smaller text and reduced padding
                    textView.textSize = 13f
                    textView.setPadding(8, 4, 8, 4)
                    textView.minHeight = 0
                    textView.minimumHeight = 0
                    view.setPadding(view.paddingLeft, 2, view.paddingRight, 2)

                    // Scale down the radio button drawable so it doesn't clip
                    if (textView is android.widget.CheckedTextView) {
                        val mark = textView.checkMarkDrawable
                        if (mark != null) {
                            val scale = 0.7
                            val w = (mark.intrinsicWidth * scale).toInt()
                            val h = (mark.intrinsicHeight * scale).toInt()
                            mark.setBounds(0, 0, w, h)
                            textView.checkMarkDrawable = mark
                        }
                    }

                    if (!isEnabled[position]) {
                        val name = names[position]
                        val note = "  ⚠ Not enabled"
                        val spannable = SpannableString("$name$note")
                        spannable.setSpan(ForegroundColorSpan(Color.GRAY), 0, name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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
                    return isEnabled[position]
                }
            }

            val dialog = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setTitle("Select Keyboard")
                .setSingleChoiceItems(adapter, currentIndex) { dlg, which ->
                    if (!isEnabled[which]) {
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
            if (Settings.canDrawOverlays(this)) {
                dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            }
            dialog.show()

        } catch (e: Exception) {

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
