package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import android.provider.Settings
import com.katsuyamaki.DroidOSTrackpadKeyboard.IShellService

class SystemImeManager(private val service: OverlayService, private val shellService: IShellService?) {

    @Volatile private var isKeyboardRestoreInProgress = false
    private var lastMainCheck = 0L
    private var lastCoverCheck = 0L
    private var lastBlockTime: Long = 0

    private val handler: Handler = service.handler
    private val TAG = "SystemImeManager"

    private val imeObserver = object : ContentObserver(handler) {
        private var lastObservedIme: String = ""
        private var lastFightTime: Long = 0

        override fun onChange(selfChange: Boolean) {
            val current = Settings.Secure.getString(service.contentResolver, "default_input_method") ?: ""
            
            android.util.Log.d(TAG, "IME Observer: '$lastObservedIme' -> '$current' (display=${service.currentDisplayId})")
            
            if (current.isEmpty()) {
                lastObservedIme = current
                return
            }
            
            val isDock = current.contains("DockInputMethodService") || current.contains("NullInputMethodService")
            val isSamsung = current.contains("honeyboard") || current.contains("com.sec.android.inputmethod")
            val wasGboard = lastObservedIme.contains("com.google.android.inputmethod.latin")
            val wasDock = lastObservedIme.contains("DockInputMethodService") || lastObservedIme.contains("NullInputMethodService")
            val isOnMainScreen = service.currentDisplayId == 0

            if (isSamsung && wasGboard && isOnMainScreen && !wasDock) {
                val now = System.currentTimeMillis()
                if (now - lastFightTime > 1000) {
                    lastFightTime = now
                    android.util.Log.w(TAG, "IME Observer: SAMSUNG TAKEOVER DETECTED! Fighting back...")
                    
                    Thread {
                        try {
                            val gboardId = "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME"
                            val samsungId = current
                            
                            android.util.Log.w(TAG, "┌─ IMMEDIATE SAMSUNG FIGHT ─┐")
                            
                            shellService?.runCommand("ime disable $samsungId")
                            android.util.Log.w(TAG, "├─ Samsung disabled")
                            
                            shellService?.runCommand("settings put secure default_input_method $gboardId")
                            shellService?.runCommand("ime set $gboardId")
                            android.util.Log.w(TAG, "├─ Gboard set")
                            
                            Thread.sleep(5000)
                            
                            shellService?.runCommand("ime enable $samsungId")
                            android.util.Log.w(TAG, "├─ Samsung re-enabled")
                            
                            val finalIme = shellService?.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            val success = finalIme.contains("google.android.inputmethod.latin")
                            android.util.Log.w(TAG, "├─ Final: $finalIme")
                            android.util.Log.w(TAG, "└─ Success: $success")
                            
                            if (!success) {
                                android.util.Log.w(TAG, "┌─ RETRY FIGHT ─┐")
                                shellService?.runCommand("ime disable $samsungId")
                                Thread.sleep(500)
                                shellService?.runCommand("settings put secure default_input_method $gboardId")
                                shellService?.runCommand("ime set $gboardId")
                                Thread.sleep(3000)
                                shellService?.runCommand("ime enable $samsungId")
                                
                                val retryFinal = shellService?.runCommand("settings get secure default_input_method")?.trim() ?: ""
                                android.util.Log.w(TAG, "└─ Retry final: $retryFinal")
                            }
                            
                        } catch (e: Exception) {
                            android.util.Log.e(TAG, "Samsung fight error: ${e.message}")
                        }
                    }.start()
                }
            }
            
            if (isSamsung && wasDock && !service.prefs.prefBlockSoftKeyboard) {
                val now = System.currentTimeMillis()
                if (now - lastFightTime > 1000) {
                    lastFightTime = now
                    android.util.Log.w(TAG, "IME Observer: Samsung took over from NullKB! Restoring NullKB...")
                    
                    Thread {
                        try {
                            val dockId = "${service.packageName}/com.katsuyamaki.DroidOSTrackpadKeyboard.DockInputMethodService"
                            val samsungId = current

                            
                            android.util.Log.w(TAG, "┌─ NULLKB RESTORATION ─┐")
                            
                            shellService?.runCommand("ime disable $samsungId")
                            android.util.Log.w(TAG, "├─ Samsung disabled")
                            
                            shellService?.runCommand("ime enable $dockId")
                            shellService?.runCommand("settings put secure default_input_method $dockId")
                            shellService?.runCommand("ime set $dockId")
                            android.util.Log.w(TAG, "├─ Dock set")

                            Thread.sleep(3000)
                            
                            shellService?.runCommand("ime enable $samsungId")
                            android.util.Log.w(TAG, "├─ Samsung re-enabled")
                            
                            Thread.sleep(500)
                            val finalIme = shellService?.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            val success = finalIme.contains("DockInputMethodService")
                            android.util.Log.w(TAG, "├─ Final: $finalIme")

                            android.util.Log.w(TAG, "└─ Success: $success")
                            
                        } catch (e: Exception) {
                            android.util.Log.e(TAG, "NullKB restore error: ${e.message}")
                        }
                    }.start()
                }
            }
            
            val shouldSave = when {
                isSamsung -> {
                    android.util.Log.d(TAG, "IME Observer: SKIPPING Samsung save")
                    false
                }
                isDock && service.prefs.prefBlockSoftKeyboard -> {
                    android.util.Log.d(TAG, "IME Observer: SKIPPING Dock save (blocking ON)")
                    false
                }
                isDock && !service.prefs.prefBlockSoftKeyboard -> {
                    android.util.Log.i(TAG, "IME Observer: SAVING Dock as user preference (blocking OFF)")
                    true
                }
                !isOnMainScreen -> {
                    android.util.Log.d(TAG, "IME Observer: SKIPPING save (not on main screen)")
                    false
                }
                else -> true
            }
            
            if (shouldSave) {
                service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("user_preferred_ime", current)
                    .apply()
                    
                android.util.Log.i(TAG, "IME Observer: SAVED preference: $current")
                
                val name = when {
                    current.contains("google.android.inputmethod.latin") -> "Gboard"
                    current.contains("DockInputMethodService") -> "DroidOS Dock"
                    current.contains("NullInputMethodService") -> "DroidOS Dock"
                    current.contains("honeyboard") -> "Samsung"
                    current.contains("juloo.keyboard2") -> "Unexpected KB"
                    else -> "Custom Keyboard"
                }
                handler.post { service.showToast("Saved: $name") }
                handler.post { service.menuManager?.refresh() }
            }
            
            lastObservedIme = current
        }
    }

    fun startMonitoring() {
        service.contentResolver.registerContentObserver(
            Settings.Secure.getUriFor("default_input_method"),
            false,
            imeObserver
        )
    }

    fun stopMonitoring() {
        service.contentResolver.unregisterContentObserver(imeObserver)
    }

    fun setSoftKeyboardBlocking(enabled: Boolean) {
        if (shellService == null) return
        
        if (enabled && service.currentDisplayId != 1) {
            android.util.Log.w(TAG, "setSoftKeyboardBlocking: Blocking only works on cover screen (display 1), current=${service.currentDisplayId} - skipping")
            return
        }
        
        if (!enabled && isKeyboardRestoreInProgress) {
            android.util.Log.w(TAG, "setSoftKeyboardBlocking: Already restoring, skipping duplicate call")
            return
        }

        Thread {
            try {
                val allImes = shellService.runCommand("ime list -a -s") ?: ""
                val myImeId = allImes.lines().firstOrNull { 
                    it.contains(service.packageName) && (it.contains("DockInputMethodService") || it.contains("NullInputMethodService")) 
                }?.trim()

                if (myImeId.isNullOrEmpty()) {
                    handler.post { service.showToast("Error: DroidOS Keyboard not found.") }
                    return@Thread
                }

                if (enabled) {
                    shellService.runCommand("ime enable $myImeId")
                    shellService.runCommand("ime set $myImeId")
                    shellService.runCommand("settings put secure show_ime_with_hard_keyboard 0")
                    handler.post { service.showToast("Keyboard Blocked (Cover Screen)") }
                } else {
                    isKeyboardRestoreInProgress = true
                    
                    android.util.Log.w(TAG, "┌──────────────────────────────────────────────────────────┐")
                    android.util.Log.w(TAG, "│ KEYBOARD RESTORATION - SAMSUNG WORKAROUND               │")
                    android.util.Log.w(TAG, "└──────────────────────────────────────────────────────────┘")
                    
                    try {
                        val initialIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                        android.util.Log.w(TAG, "├─ Initial IME: $initialIme")
                        
                        val sharedPrefs = service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
                        var targetIme = sharedPrefs.getString("user_preferred_ime", null)
                        android.util.Log.w(TAG, "├─ Saved preference: ${targetIme ?: "NULL"}")
                        
                        if (targetIme != null && targetIme.contains("NullInputMethodService")) {
                            android.util.Log.w(TAG, "├─ User preference is NullKB - not restoring away from it")
                            isKeyboardRestoreInProgress = false
                            android.util.Log.w(TAG, "└─ KEYBOARD RESTORATION SKIPPED (NullKB is preference)")
                            return@Thread
                        }
                        
                        val enabledImes = shellService.runCommand("ime list -s") ?: ""
                        val gboardId = enabledImes.lines().find { 
                            it.contains("com.google.android.inputmethod.latin") 
                        }?.trim()
                        val samsungId = enabledImes.lines().find { 
                            it.contains("honeyboard") || it.contains("com.sec.android.inputmethod") 
                        }?.trim()
                        
                        android.util.Log.w(TAG, "├─ Gboard ID: ${gboardId ?: "NOT FOUND"}")
                        android.util.Log.w(TAG, "├─ Samsung ID: ${samsungId ?: "NOT FOUND"}")
                        
                        if (targetIme.isNullOrEmpty() || targetIme.contains("honeyboard") || targetIme.contains("com.sec")) {
                            targetIme = gboardId
                        }
                        
                        if (targetIme.isNullOrEmpty()) {
                            android.util.Log.e(TAG, "├─ ERROR: No target keyboard found!")
                            handler.post { service.showToast("No keyboard to restore") }
                            isKeyboardRestoreInProgress = false
                            return@Thread
                        }
                        
                        android.util.Log.w(TAG, "├─ Target IME: $targetIme")
                        
                        if (!samsungId.isNullOrEmpty() && initialIme.contains("honeyboard")) {
                            android.util.Log.w(TAG, "├─ WORKAROUND: Temporarily disabling Samsung Keyboard...")
                            
                            val disableResult = shellService.runCommand("ime disable $samsungId")
                            android.util.Log.w(TAG, "│  ├─ Disable result: ${disableResult ?: "null"}")
                            
                            Thread.sleep(100)
                            
                            shellService.runCommand("ime enable $targetIme")
                            shellService.runCommand("settings put secure default_input_method $targetIme")
                            val setResult = shellService.runCommand("ime set $targetIme")
                            android.util.Log.w(TAG, "│  ├─ Set Gboard result: ${setResult ?: "null"}")
                            
                            Thread.sleep(500)
                            
                            val afterSet = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            android.util.Log.w(TAG, "│  ├─ After set: $afterSet")
                            
                            android.util.Log.w(TAG, "│  └─ Re-enabling Samsung Keyboard...")
                            shellService.runCommand("ime enable $samsungId")
                            
                            Thread.sleep(300)
                            val finalIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            android.util.Log.w(TAG, "├─ FINAL IME: $finalIme")
                            
                            val success = finalIme == targetIme || finalIme.contains("google.android.inputmethod.latin")
                            android.util.Log.w(TAG, "├─ SUCCESS: $success")
                            
                            if (success) {
                                handler.post { service.showToast("Keyboard Restored") }
                            } else {
                                android.util.Log.w(TAG, "├─ First attempt failed, trying nuclear option...")
                                
                                shellService.runCommand("ime disable $samsungId")
                                Thread.sleep(200)
                                
                                shellService.runCommand("settings put secure default_input_method $targetIme")
                                shellService.runCommand("ime set $targetIme")
                                
                                Thread.sleep(3000)
                                
                                val nuclearCheck = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                                android.util.Log.w(TAG, "├─ After nuclear: $nuclearCheck")
                                
                                shellService.runCommand("ime enable $samsungId")
                                
                                Thread.sleep(500)
                                val nuclearFinal = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                                android.util.Log.w(TAG, "├─ Nuclear final: $nuclearFinal")
                                
                                if (nuclearFinal.contains("google.android.inputmethod.latin")) {
                                    handler.post { service.showToast("Keyboard Restored (retry)") }
                                } else {
                                    android.util.Log.e(TAG, "├─ NUCLEAR FAILED")
                                    handler.post { service.showToast("Keyboard restore failed") }
                                }
                            }
                            
                        } else {
                            android.util.Log.w(TAG, "├─ Simple restore (Samsung not active)...")
                            shellService.runCommand("ime enable $targetIme")
                            shellService.runCommand("settings put secure default_input_method $targetIme")
                            shellService.runCommand("ime set $targetIme")
                            
                            Thread.sleep(300)
                            val finalIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            android.util.Log.w(TAG, "├─ FINAL IME: $finalIme")
                            
                            val success = finalIme == targetIme
                            if (success) {
                                handler.post { service.showToast("Keyboard Restored") }
                            } else {
                                handler.post { service.showToast("Keyboard restore failed") }
                            }
                        }
                        
                    } catch (e: Exception) {
                        android.util.Log.e(TAG, "├─ EXCEPTION: ${e.message}", e)
                        handler.post { service.showToast("Restore error: ${e.message}") }
                    }
                    
                    isKeyboardRestoreInProgress = false
                    android.util.Log.w(TAG, "└─ KEYBOARD RESTORATION COMPLETE")
                }
            } catch (e: Exception) {
                handler.post { service.showToast("Error: ${e.message}") }
            }
        }.start()
    }

    fun ensureSystemKeyboardRestored() {
        if (System.currentTimeMillis() - lastMainCheck < 2000) return
        lastMainCheck = System.currentTimeMillis()

        Thread {
            try {
                val current = shellService?.runCommand("settings get secure default_input_method") ?: ""
                if (current.contains(service.packageName) && current.contains("NullInputMethodService")) {
                    
                    val savedPref = service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
                        .getString("user_preferred_ime", null)
                    
                    if (savedPref != null && (savedPref.contains("DockInputMethodService") || savedPref.contains("NullInputMethodService"))) {
                        android.util.Log.d(TAG, "ensureSystemKeyboardRestored: Dock is user preference, keeping it")
                        return@Thread
                    }
                    
                    android.util.Log.i(TAG, "Main Screen Detected: Restoring System Keyboard...")
                    handler.post { setSoftKeyboardBlocking(false) }
                }
            } catch(e: Exception) {}
        }.start()
    }

    fun ensureKeyboardBlocked() {
        if (service.currentDisplayId != 1) return
        
        if (System.currentTimeMillis() - lastCoverCheck < 2000) return
        lastCoverCheck = System.currentTimeMillis()

        Thread {
            try {
                val current = shellService?.runCommand("settings get secure default_input_method") ?: ""
                if (!current.contains("NullInputMethodService") && !current.contains("DockInputMethodService")) {
                    android.util.Log.i(TAG, "Cover Screen (D1): Enforcing DroidOS Keyboard...")
                    handler.post { setSoftKeyboardBlocking(true) }
                }
            } catch(e: Exception) {}
        }.start()
    }

    fun ensureCoverKeyboardEnforced() {
        // Throttle: Check max once every 2 seconds to save battery
        if (System.currentTimeMillis() - lastCoverCheck < 2000) return
        lastCoverCheck = System.currentTimeMillis()

        Thread {
            try {
                // 1. Check what is currently active
                val current = shellService?.runCommand("settings get secure default_input_method")?.trim() ?: ""
                
                // 2. If Dock is active, DO NOT interfere
                // User may have intentionally set it as their default keyboard
                if (current.contains("DockInputMethodService") || current.contains("NullInputMethodService")) {
                    android.util.Log.d(TAG, "ensureCoverKeyboardEnforced: Dock active, not interfering")
                    return@Thread
                }
                
                // 3. Get User's Preferred Keyboard
                val sharedPrefs = service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
                val targetIme = sharedPrefs.getString("user_preferred_ime", null) ?: return@Thread
                
                // 4. If target is Dock, don't fight - it should already be handled
                if (targetIme.contains("DockInputMethodService") || targetIme.contains("NullInputMethodService")) {
                    return@Thread
                }

                
                // 5. If current matches target, nothing to do
                if (current == targetIme) {
                    return@Thread
                }
                
                // 6. Only fight if Samsung took over (not if user switched to something else)
                val isSamsung = current.contains("honeyboard") || current.contains("com.sec.android.inputmethod")
                if (isSamsung) {
                    android.util.Log.i(TAG, "ensureCoverKeyboardEnforced: Samsung detected, forcing $targetIme...")
                    handler.post { setSoftKeyboardBlocking(false) }
                }
            } catch(e: Exception) {}
        }.start()
    }

    fun triggerAggressiveBlocking() {
        // GUARD: Only block keyboard on Cover Screen (display 1)
        if (service.currentDisplayId != 1) {
            android.util.Log.d(TAG, "triggerAggressiveBlocking: Skipping - not on cover screen (display ${service.currentDisplayId})")
            return
        }

        // Rely on standard Android API to suppress keyboard
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                if (service.softKeyboardController.showMode != AccessibilityService.SHOW_MODE_HIDDEN) {
                    service.softKeyboardController.showMode = AccessibilityService.SHOW_MODE_HIDDEN
                }
            } catch (e: Exception) {
                // Controller might not be connected yet
            }
        }
    }

    fun triggerAggressiveBlockingWithThrottle() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBlockTime > 500) {
            lastBlockTime = currentTime
            triggerAggressiveBlocking()
        }
    }
}