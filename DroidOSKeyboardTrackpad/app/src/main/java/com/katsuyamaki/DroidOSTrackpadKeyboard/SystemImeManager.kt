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
                    
                    Thread {
                        try {
                            val gboardId = "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME"
                            val samsungId = current
                            
                            shellService?.runCommand("ime disable $samsungId")
                            shellService?.runCommand("settings put secure default_input_method $gboardId")
                            shellService?.runCommand("ime set $gboardId")
                            
                            Thread.sleep(5000)
                            
                            shellService?.runCommand("ime enable $samsungId")
                            
                            val finalIme = shellService?.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            val success = finalIme.contains("google.android.inputmethod.latin")
                            
                            if (!success) {
                                shellService?.runCommand("ime disable $samsungId")
                                Thread.sleep(500)
                                shellService?.runCommand("settings put secure default_input_method $gboardId")
                                shellService?.runCommand("ime set $gboardId")
                                Thread.sleep(3000)
                                shellService?.runCommand("ime enable $samsungId")
                            }
                            
                        } catch (e: Exception) {
                        }
                    }.start()
                }
            }
            
            if (isSamsung && wasDock && !service.prefs.prefBlockSoftKeyboard) {
                val now = System.currentTimeMillis()
                if (now - lastFightTime > 1000) {
                    lastFightTime = now
                    
                    Thread {
                        try {
                            val dockId = "${service.packageName}/com.katsuyamaki.DroidOSTrackpadKeyboard.DockInputMethodService"
                            val samsungId = current
                            
                            shellService?.runCommand("ime disable $samsungId")
                            shellService?.runCommand("ime enable $dockId")
                            shellService?.runCommand("settings put secure default_input_method $dockId")
                            shellService?.runCommand("ime set $dockId")

                            Thread.sleep(3000)
                            
                            shellService?.runCommand("ime enable $samsungId")
                            
                            Thread.sleep(500)
                            
                        } catch (e: Exception) {
                        }
                    }.start()
                }
            }
            
            val shouldSave = when {
                isSamsung -> false
                isDock && service.prefs.prefBlockSoftKeyboard -> false
                isDock && !service.prefs.prefBlockSoftKeyboard -> true
                !isOnMainScreen -> false
                else -> true
            }
            
            if (shouldSave) {
                service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("user_preferred_ime", current)
                    .apply()
                
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
            return
        }
        
        if (!enabled && isKeyboardRestoreInProgress) {
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
                    
                    try {
                        val initialIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                        
                        val sharedPrefs = service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
                        var targetIme = sharedPrefs.getString("user_preferred_ime", null)
                        
                        if (targetIme != null && targetIme.contains("NullInputMethodService")) {
                            isKeyboardRestoreInProgress = false
                            return@Thread
                        }
                        
                        val enabledImes = shellService.runCommand("ime list -s") ?: ""
                        val gboardId = enabledImes.lines().find { 
                            it.contains("com.google.android.inputmethod.latin") 
                        }?.trim()
                        val samsungId = enabledImes.lines().find { 
                            it.contains("honeyboard") || it.contains("com.sec.android.inputmethod") 
                        }?.trim()
                        
                        if (targetIme.isNullOrEmpty() || targetIme.contains("honeyboard") || targetIme.contains("com.sec")) {
                            targetIme = gboardId
                        }
                        
                        if (targetIme.isNullOrEmpty()) {
                            handler.post { service.showToast("No keyboard to restore") }
                            isKeyboardRestoreInProgress = false
                            return@Thread
                        }
                        
                        if (!samsungId.isNullOrEmpty() && initialIme.contains("honeyboard")) {
                            shellService.runCommand("ime disable $samsungId")
                            
                            Thread.sleep(100)
                            
                            shellService.runCommand("ime enable $targetIme")
                            shellService.runCommand("settings put secure default_input_method $targetIme")
                            shellService.runCommand("ime set $targetIme")
                            
                            Thread.sleep(500)
                            
                            shellService.runCommand("ime enable $samsungId")
                            
                            Thread.sleep(300)
                            val finalIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            
                            val success = finalIme == targetIme || finalIme.contains("google.android.inputmethod.latin")
                            
                            if (success) {
                                handler.post { service.showToast("Keyboard Restored") }
                            } else {
                                shellService.runCommand("ime disable $samsungId")
                                Thread.sleep(200)
                                
                                shellService.runCommand("settings put secure default_input_method $targetIme")
                                shellService.runCommand("ime set $targetIme")
                                
                                Thread.sleep(3000)
                                
                                shellService.runCommand("ime enable $samsungId")
                                
                                Thread.sleep(500)
                                val nuclearFinal = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                                
                                if (nuclearFinal.contains("google.android.inputmethod.latin")) {
                                    handler.post { service.showToast("Keyboard Restored (retry)") }
                                } else {
                                    handler.post { service.showToast("Keyboard restore failed") }
                                }
                            }
                            
                        } else {
                            shellService.runCommand("ime enable $targetIme")
                            shellService.runCommand("settings put secure default_input_method $targetIme")
                            shellService.runCommand("ime set $targetIme")
                            
                            Thread.sleep(300)
                            val finalIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            
                            val success = finalIme == targetIme
                            if (success) {
                                handler.post { service.showToast("Keyboard Restored") }
                            } else {
                                handler.post { service.showToast("Keyboard restore failed") }
                            }
                        }
                        
                    } catch (e: Exception) {
                        handler.post { service.showToast("Restore error: ${e.message}") }
                    }
                    
                    isKeyboardRestoreInProgress = false
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
                        return@Thread
                    }
                    
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
                    handler.post { setSoftKeyboardBlocking(false) }
                }
            } catch(e: Exception) {}
        }.start()
    }

    fun triggerAggressiveBlocking() {
        // GUARD: Only block keyboard on Cover Screen (display 1)
        if (service.currentDisplayId != 1) {
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