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
    private val IME_TRACE_TAG = "IME_TRACE"

    @Volatile private var lastTraceMsg: String = ""
    @Volatile private var lastTraceAt: Long = 0L
    @Volatile private var lastInternalSetReason: String = ""
    @Volatile private var lastInternalSetTarget: String = ""
    @Volatile private var lastInternalSetAt: Long = 0L

    private fun log(msg: String) {
        android.util.Log.d(TAG, msg)
    }

    private fun shortIme(ime: String?): String {
        if (ime.isNullOrBlank()) return "none"
        return ime.substringAfterLast("/")
    }

    private fun isDockIme(ime: String?): Boolean {
        if (ime.isNullOrBlank()) return false
        return ime.contains("DockInputMethodService")
    }

    private fun isNullIme(ime: String?): Boolean {
        if (ime.isNullOrBlank()) return false
        return ime.contains("NullInputMethodService")
    }

    private fun isSamsungIme(ime: String?): Boolean {
        if (ime.isNullOrBlank()) return false
        return ime.contains("honeyboard") || ime.contains("com.sec.android.inputmethod")
    }

    private fun isVoiceIme(ime: String?): Boolean {
        if (ime.isNullOrBlank()) return false
        return ime.contains("com.google.android.tts") || ime.contains("VoiceInputMethodService")
    }

    private fun normalizeDockIme(ime: String?): String? {
        if (ime.isNullOrBlank()) return null
        return if (isDockIme(ime)) {
            "${service.packageName}/com.katsuyamaki.DroidOSTrackpadKeyboard.DockInputMethodService"
        } else {
            ime
        }
    }

    private fun isRestoreCandidateIme(ime: String?): Boolean {
        if (ime.isNullOrBlank()) return false
        if (isNullIme(ime)) return false
        if (isVoiceIme(ime)) return false
        if (isSamsungIme(ime)) return false
        return true
    }

    private fun isExplicitPickerPending(prefs: android.content.SharedPreferences, now: Long): Boolean {
        return prefs.getLong("ime_explicit_pick_until", 0L) > now
    }

    private fun clearExplicitPickerPending(prefs: android.content.SharedPreferences) {
        prefs.edit().putLong("ime_explicit_pick_until", 0L).apply()
    }

    private fun traceIme(event: String, details: String) {
        val now = System.currentTimeMillis()
        val msg = "event=$event d=${service.currentDisplayId} block=${service.prefs.prefBlockSoftKeyboard} $details"
        synchronized(this) {
            if (msg == lastTraceMsg && now - lastTraceAt < 1000L) return
            lastTraceMsg = msg
            lastTraceAt = now
        }
        android.util.Log.i(IME_TRACE_TAG, msg)
    }

    private fun markImeSet(reason: String, targetIme: String) {
        lastInternalSetReason = reason
        lastInternalSetTarget = targetIme
        lastInternalSetAt = System.currentTimeMillis()
        traceIme("REQ_SET", "reason=$reason target=${shortIme(targetIme)}")
    }

    private fun classifyObservedSource(currentIme: String): String {
        val now = System.currentTimeMillis()
        if (lastInternalSetTarget.isNotEmpty() && now - lastInternalSetAt <= 5000L) {
            return if (currentIme == lastInternalSetTarget) {
                "internal:$lastInternalSetReason"
            } else {
                "internal-miss:$lastInternalSetReason"
            }
        }
        return "external"
    }

    private val imeObserver = object : ContentObserver(handler) {
        private var lastObservedIme: String = ""
        private var lastFightTime: Long = 0

        override fun onChange(selfChange: Boolean) {
            val current = Settings.Secure.getString(service.contentResolver, "default_input_method") ?: ""

            if (current != lastObservedIme) {
                traceIme(
                    "OBS_CHANGE",
                    "from=${shortIme(lastObservedIme)} to=${shortIme(current)} src=${classifyObservedSource(current)}"
                )
            }

            log("IME_OBSERVER: onChange - current=$current, last=$lastObservedIme, displayId=${service.currentDisplayId}")

            if (current.isEmpty()) {
                lastObservedIme = current
                return
            }
            
            val isDock = current.contains("DockInputMethodService") || current.contains("NullInputMethodService")
            val isSamsung = current.contains("honeyboard") || current.contains("com.sec.android.inputmethod")
            val wasGboard = lastObservedIme.contains("com.google.android.inputmethod.latin")
            val wasDock = lastObservedIme.contains("DockInputMethodService") || lastObservedIme.contains("NullInputMethodService")
            val isOnMainScreen = service.currentDisplayId == 0
            
            log("IME_OBSERVER: isDock=$isDock, isSamsung=$isSamsung, wasGboard=$wasGboard, wasDock=$wasDock, isOnMainScreen=$isOnMainScreen")

            if (isSamsung && wasGboard && isOnMainScreen && !wasDock) {
                log("IME_OBSERVER: FIGHT - Samsung took over from Gboard on main screen")
                val now = System.currentTimeMillis()
                if (now - lastFightTime > 1000) {
                    lastFightTime = now
                    
                    Thread {
                        try {
                            val gboardId = "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME"
                            val samsungId = current
                            
                            shellService?.runCommand("ime disable $samsungId")
                            markImeSet("observer_fight_main_gboard", gboardId)
                            shellService?.runCommand("settings put secure default_input_method $gboardId")
                            shellService?.runCommand("ime set $gboardId")
                            
                            Thread.sleep(5000)
                            
                            shellService?.runCommand("ime enable $samsungId")
                            
                            val finalIme = shellService?.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            val success = finalIme.contains("google.android.inputmethod.latin")
                            
                            if (!success) {
                                shellService?.runCommand("ime disable $samsungId")
                                Thread.sleep(500)
                                markImeSet("observer_fight_main_gboard_retry", gboardId)
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
                            markImeSet("observer_restore_dock", dockId)
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
            
            val isTtsVoice = isVoiceIme(current)
            val prefs = service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            val now = System.currentTimeMillis()
            val explicitPending = isExplicitPickerPending(prefs, now)
            val normalizedCurrent = normalizeDockIme(current)

            val shouldSave =
                isOnMainScreen &&
                explicitPending &&
                !isSamsung &&
                !isTtsVoice &&
                !isNullIme(current) &&
                isRestoreCandidateIme(normalizedCurrent)

            if (shouldSave && normalizedCurrent != null) {
                prefs.edit()
                    .putString("user_preferred_ime", normalizedCurrent)
                    .putLong("ime_explicit_pick_until", 0L)
                    .apply()
                traceIme("PREF_SAVE", "ime=${shortIme(normalizedCurrent)} explicit=true")

                val name = when {
                    normalizedCurrent.contains("google.android.inputmethod.latin") -> "Gboard"
                    normalizedCurrent.contains("DockInputMethodService") -> "DroidOS Dock"
                    normalizedCurrent.contains("honeyboard") -> "Samsung"
                    normalizedCurrent.contains("juloo.keyboard2") -> "Unexpected KB"
                    else -> "Custom Keyboard"
                }
                handler.post { service.showToast("Saved: $name") }
                handler.post { service.menuManager?.refresh() }
            } else if (explicitPending && isOnMainScreen && current != lastObservedIme) {
                clearExplicitPickerPending(prefs)
                traceIme("PREF_SKIP", "ime=${shortIme(current)} reason=not_restore_candidate")
            }
            
            lastObservedIme = current
        }
    }

    fun startMonitoring() {
        traceIme("MONITOR", "state=start")
        service.contentResolver.registerContentObserver(
            Settings.Secure.getUriFor("default_input_method"),
            false,
            imeObserver
        )
    }

    fun stopMonitoring() {
        traceIme("MONITOR", "state=stop")
        service.contentResolver.unregisterContentObserver(imeObserver)
    }

    fun setSoftKeyboardBlocking(enabled: Boolean) {
        log("setSoftKeyboardBlocking($enabled) called, displayId=${service.currentDisplayId}, shellService=${shellService != null}")
        traceIme("CALL_BLOCK", "enabled=$enabled")

        if (shellService == null) {
            log("setSoftKeyboardBlocking: ABORT - shellService is null")
            traceIme("ABORT_BLOCK", "reason=no_shell")
            return
        }

        if (enabled && service.currentDisplayId != 1) {
            log("setSoftKeyboardBlocking: ABORT - enabled but displayId=${service.currentDisplayId} != 1")
            traceIme("ABORT_BLOCK", "reason=wrong_display")
            return
        }

        if (!enabled && isKeyboardRestoreInProgress) {
            log("setSoftKeyboardBlocking: ABORT - restore already in progress")
            traceIme("ABORT_BLOCK", "reason=restore_in_progress")
            return
        }

        Thread {
            try {
                log("setSoftKeyboardBlocking: querying IMEs...")
                val allImes = shellService.runCommand("ime list -a -s") ?: ""
                log("setSoftKeyboardBlocking: all IMEs = ${allImes.replace("\n", " | ")}")
                
                val myImeId = allImes.lines().firstOrNull { 
                    it.contains(service.packageName) && (it.contains("DockInputMethodService") || it.contains("NullInputMethodService")) 
                }?.trim()

                if (myImeId.isNullOrEmpty()) {
                    log("setSoftKeyboardBlocking: ERROR - DroidOS IME not found in list")
                    handler.post { service.showToast("Error: DroidOS Keyboard not found.") }
                    return@Thread
                }
                
                log("setSoftKeyboardBlocking: found myImeId=$myImeId")

                if (enabled) {
                    log("setSoftKeyboardBlocking: BLOCKING - running ime enable/set...")

                    val preCoverIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                    if (isRestoreCandidateIme(preCoverIme) && !isDockIme(preCoverIme)) {
                        service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
                            .edit()
                            .putString("ime_pre_cover_block", preCoverIme)
                            .apply()
                        traceIme("SAVE_PRE_COVER", "ime=${shortIme(preCoverIme)}")
                    }

                    // Find Samsung KB
                    val samsungId = allImes.lines().find { 
                        it.contains("honeyboard") || it.contains("com.sec.android.inputmethod") 
                    }?.trim()
                    
                    // Disable Samsung KB more aggressively using pm disable-user
                    if (!samsungId.isNullOrEmpty()) {
                        log("setSoftKeyboardBlocking: disabling Samsung KB package")
                        shellService.runCommand("pm disable-user --user 0 com.samsung.android.honeyboard")
                        Thread.sleep(300)
                    }
                    
                    // Retry loop - Samsung aggressively re-enables itself
                    var success = false
                    for (attempt in 1..3) {
                        log("setSoftKeyboardBlocking: attempt $attempt/3")
                        
                        // Also use ime disable as backup
                        if (!samsungId.isNullOrEmpty()) {
                            log("setSoftKeyboardBlocking: ime disable Samsung KB: $samsungId")
                            shellService.runCommand("ime disable $samsungId")
                            Thread.sleep(150)
                        }
                        
                        // Set DroidOS IME
                        shellService.runCommand("ime enable $myImeId")
                        markImeSet("setSoftKeyboardBlocking:block:attempt$attempt", myImeId)
                        shellService.runCommand("settings put secure default_input_method $myImeId")
                        val r = shellService.runCommand("ime set $myImeId")
                        log("setSoftKeyboardBlocking: ime set result: $r")
                        shellService.runCommand("settings put secure show_ime_with_hard_keyboard 0")

                        Thread.sleep(150)

                        // Verify
                        val currentIme = shellService.runCommand("settings get secure default_input_method")?.trim()
                        log("setSoftKeyboardBlocking: VERIFY attempt $attempt - IME: $currentIme")

                        success = currentIme?.contains("DroidOS") == true || currentIme?.contains("DockInputMethodService") == true
                        traceIme("VERIFY_BLOCK", "attempt=$attempt target=${shortIme(myImeId)} current=${shortIme(currentIme)} ok=$success")
                        if (success) {
                            log("setSoftKeyboardBlocking: SUCCESS on attempt $attempt")
                            break
                        }
                        log("setSoftKeyboardBlocking: FAILED attempt $attempt, Samsung still active")
                    }

                    traceIme("DONE_BLOCK", "enabled=true success=$success target=${shortIme(myImeId)}")
                    handler.post { service.showToast(if (success) "KB Blocked" else "KB Block Failed") }
                } else {
                    isKeyboardRestoreInProgress = true
                    
                    try {
                        val initialIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                        
                        val sharedPrefs = service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
                        val prefImeRaw = sharedPrefs.getString("user_preferred_ime", null)
                        val preCoverImeRaw = sharedPrefs.getString("ime_pre_cover_block", null)

                        var targetIme = normalizeDockIme(prefImeRaw)
                        val preCoverIme = normalizeDockIme(preCoverImeRaw)

                        if (!isRestoreCandidateIme(targetIme)) targetIme = null
                        if (targetIme.isNullOrEmpty() && isRestoreCandidateIme(preCoverIme)) {
                            targetIme = preCoverIme
                        }

                        val enabledImes = shellService.runCommand("ime list -s") ?: ""
                        val samsungId = enabledImes.lines().find {
                            it.contains("honeyboard") || it.contains("com.sec.android.inputmethod")
                        }?.trim()

                        traceIme(
                            "RESTORE_CANDIDATE",
                            "pref=${shortIme(prefImeRaw)} precover=${shortIme(preCoverIme)} target=${shortIme(targetIme)}"
                        )

                        if (targetIme.isNullOrEmpty()) {
                            traceIme("ABORT_RESTORE", "reason=no_explicit_or_precover_target")
                            handler.post { service.showToast("No keyboard to restore") }
                            isKeyboardRestoreInProgress = false
                            return@Thread
                        }
                        
                        traceIme(
                            "RESTORE_TARGET",
                            "initial=${shortIme(initialIme)} target=${shortIme(targetIme)} samsungPath=${!samsungId.isNullOrEmpty() && initialIme.contains("honeyboard")}"
                        )

                        if (!samsungId.isNullOrEmpty() && initialIme.contains("honeyboard")) {
                            shellService.runCommand("ime disable $samsungId")
                            
                            Thread.sleep(100)

                            shellService.runCommand("ime enable $targetIme")
                            markImeSet("setSoftKeyboardBlocking:restore:samsung_bridge", targetIme)
                            shellService.runCommand("settings put secure default_input_method $targetIme")
                            shellService.runCommand("ime set $targetIme")

                            Thread.sleep(500)
                            
                            shellService.runCommand("ime enable $samsungId")
                            
                            Thread.sleep(300)
                            val finalIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            
                            val success = finalIme == targetIme || finalIme.contains("google.android.inputmethod.latin")
                            traceIme("VERIFY_RESTORE", "path=samsung_bridge target=${shortIme(targetIme)} final=${shortIme(finalIme)} ok=$success")

                            if (success) {
                                handler.post { service.showToast("Keyboard Restored") }
                            } else {
                                shellService.runCommand("ime disable $samsungId")
                                Thread.sleep(200)

                                markImeSet("setSoftKeyboardBlocking:restore:samsung_retry", targetIme)
                                shellService.runCommand("settings put secure default_input_method $targetIme")
                                shellService.runCommand("ime set $targetIme")

                                Thread.sleep(3000)
                                
                                shellService.runCommand("ime enable $samsungId")
                                
                                Thread.sleep(500)
                                val nuclearFinal = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                                
                                val retrySuccess = nuclearFinal.contains("google.android.inputmethod.latin")
                                traceIme("VERIFY_RESTORE", "path=samsung_retry target=${shortIme(targetIme)} final=${shortIme(nuclearFinal)} ok=$retrySuccess")
                                if (retrySuccess) {
                                    handler.post { service.showToast("Keyboard Restored (retry)") }
                                } else {
                                    handler.post { service.showToast("Keyboard restore failed") }
                                }
                            }
                            
                        } else {
                            shellService.runCommand("ime enable $targetIme")
                            markImeSet("setSoftKeyboardBlocking:restore:simple", targetIme)
                            shellService.runCommand("settings put secure default_input_method $targetIme")
                            shellService.runCommand("ime set $targetIme")

                            Thread.sleep(300)
                            val finalIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                            
                            val success = finalIme == targetIme
                            traceIme("VERIFY_RESTORE", "path=simple target=${shortIme(targetIme)} final=${shortIme(finalIme)} ok=$success")
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
        log("ensureKeyboardBlocked() called, displayId=${service.currentDisplayId}")
        
        if (service.currentDisplayId != 1) {
            log("ensureKeyboardBlocked: SKIP - not on cover screen (displayId=${service.currentDisplayId})")
            return
        }
        
        if (System.currentTimeMillis() - lastCoverCheck < 2000) {
            log("ensureKeyboardBlocked: THROTTLED - last check was recent")
            return
        }
        lastCoverCheck = System.currentTimeMillis()

        Thread {
            try {
                val current = shellService?.runCommand("settings get secure default_input_method") ?: ""
                log("ensureKeyboardBlocked: current IME = $current")
                
                if (!current.contains("NullInputMethodService") && !current.contains("DockInputMethodService")) {
                    log("ensureKeyboardBlocked: IME is NOT DroidOS - calling setSoftKeyboardBlocking(true)")
                    handler.post { setSoftKeyboardBlocking(true) }
                } else {
                    log("ensureKeyboardBlocked: IME is already DroidOS - OK")
                }
            } catch(e: Exception) {
                log("ensureKeyboardBlocked: ERROR - ${e.message}")
            }
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
        log("triggerAggressiveBlocking() called, displayId=${service.currentDisplayId}")
        
        // GUARD: Only block keyboard on Cover Screen (display 1)
        if (service.currentDisplayId != 1) {
            log("triggerAggressiveBlocking: SKIP - not on cover screen")
            return
        }

        // Rely on standard Android API to suppress keyboard
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val currentMode = service.softKeyboardController.showMode
                log("triggerAggressiveBlocking: current showMode=$currentMode")
                
                if (currentMode != AccessibilityService.SHOW_MODE_HIDDEN) {
                    service.softKeyboardController.showMode = AccessibilityService.SHOW_MODE_HIDDEN
                    log("triggerAggressiveBlocking: set showMode to HIDDEN")
                }
            } catch (e: Exception) {
                log("triggerAggressiveBlocking: ERROR - ${e.message}")
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

    /**
     * Force-refresh the IME when leaving cover screen.
     * Disables NullIME first to force system fallback, then sets target IME.
     */
    fun forceRefreshIme() {
        log("forceRefreshIme() called, shellService=${shellService != null}")
        traceIme("CALL_FORCE_REFRESH", "shell=${shellService != null}")

        if (shellService == null) {
            log("forceRefreshIme: ABORT - shellService is null")
            traceIme("ABORT_FORCE_REFRESH", "reason=no_shell")
            return
        }

        Thread {
            try {
                val nullIme = "${service.packageName}/com.katsuyamaki.DroidOSTrackpadKeyboard.NullInputMethodService"
                val dockIme = "${service.packageName}/com.katsuyamaki.DroidOSTrackpadKeyboard.DockInputMethodService"
                
                val currentIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                log("forceRefreshIme: current IME = $currentIme")
                
                // Get user's preferred IME
                val sharedPrefs = service.getSharedPreferences("TrackpadPrefs", android.content.Context.MODE_PRIVATE)
                val userPrefRaw = sharedPrefs.getString("user_preferred_ime", null)
                val preCoverImeRaw = sharedPrefs.getString("ime_pre_cover_block", null)
                val userPref = normalizeDockIme(userPrefRaw)
                val preCoverIme = normalizeDockIme(preCoverImeRaw)
                log("forceRefreshIme: userPref = $userPref")

                var targetIme: String? = null
                if (isRestoreCandidateIme(userPref)) {
                    targetIme = userPref
                } else if (isRestoreCandidateIme(preCoverIme)) {
                    targetIme = preCoverIme
                } else if (isDockIme(currentIme)) {
                    targetIme = dockIme
                }

                if (targetIme.isNullOrEmpty()) {
                    traceIme(
                        "SKIP_FORCE_REFRESH",
                        "reason=no_explicit_target current=${shortIme(currentIme)} saved=${shortIme(userPrefRaw)} precover=${shortIme(preCoverImeRaw)}"
                    )
                    shellService.runCommand("ime enable $nullIme")
                    shellService.runCommand("pm enable com.samsung.android.honeyboard")
                    return@Thread
                }

                log("forceRefreshIme: targetIme = $targetIme")
                traceIme(
                    "FORCE_TARGET",
                    "current=${shortIme(currentIme)} saved=${shortIme(userPref)} precover=${shortIme(preCoverIme)} target=${shortIme(targetIme)}"
                )

                // Step 1: Disable NullInputMethodService to force system to switch
                log("forceRefreshIme: disabling NullIME...")
                shellService.runCommand("ime disable $nullIme")
                Thread.sleep(100)
                
                // Step 2: Enable and set target IME
                log("forceRefreshIme: enabling and setting target IME...")
                shellService.runCommand("ime enable $targetIme")
                markImeSet("forceRefreshIme", targetIme)
                val r1 = shellService.runCommand("settings put secure default_input_method $targetIme")
                log("forceRefreshIme: settings put result = $r1")
                val r2 = shellService.runCommand("ime set $targetIme")
                log("forceRefreshIme: ime set result = $r2")
                Thread.sleep(200)
                
                // Step 3: Re-enable NullInputMethodService for future cover screen use
                log("forceRefreshIme: re-enabling NullIME...")
                shellService.runCommand("ime enable $nullIme")
                
                // Step 4: Re-enable Samsung KB package (was disabled on cover screen)
                log("forceRefreshIme: re-enabling Samsung KB package")
                shellService.runCommand("pm enable com.samsung.android.honeyboard")
                Thread.sleep(200)
                
                // Also ime enable
                val allImes = shellService.runCommand("ime list -a -s") ?: ""
                val samsungId = allImes.lines().find { 
                    it.contains("honeyboard") || it.contains("com.sec.android.inputmethod") 
                }?.trim()
                if (!samsungId.isNullOrEmpty()) {
                    log("forceRefreshIme: ime enable Samsung KB: $samsungId")
                    shellService.runCommand("ime enable $samsungId")
                }
                
                // Verify
                val finalIme = shellService.runCommand("settings get secure default_input_method")?.trim() ?: ""
                log("forceRefreshIme: VERIFY final IME = $finalIme")

                val success = finalIme == targetIme || finalIme.contains("DockInputMethodService") || finalIme.contains(targetIme.substringAfterLast("/"))
                traceIme("VERIFY_FORCE_REFRESH", "target=${shortIme(targetIme)} final=${shortIme(finalIme)} ok=$success")
                handler.post { 
                    service.showToast(if (success) "KB Ready" else "KB: $finalIme")
                }
            } catch (e: Exception) {
                log("forceRefreshIme: ERROR - ${e.message}")
                handler.post { service.showToast("KB Error: ${e.message}") }
            }
        }.start()
    }
}