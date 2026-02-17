package com.katsuyamaki.DroidOSLauncher

import android.content.Intent
import android.os.Handler
import android.util.Log
import java.util.ArrayDeque

class WindowCommandController(
    private val uiHandler: Handler,
    private val isShellReady: () -> Boolean,
    private val executeCommand: (Intent) -> Unit,
    private val requestShellBind: () -> Unit,
    private val logTag: String = "WindowCommandController"
) {
    companion object {
        private const val INITIAL_FLUSH_DELAY_MS = 100L
        private const val SHELL_RETRY_DELAY_MS = 600L
        private const val COMMAND_INTERVAL_MS = 400L
        private const val ERROR_RETRY_DELAY_MS = 300L
    }

    private val pendingCommands: ArrayDeque<Intent> = ArrayDeque()
    private var retryScheduled = false
    private var lastBindAttemptAt = 0L
    private var isFlushing = false

    private val flushRunnable = Runnable {
        retryScheduled = false
        flush()
    }

    fun enqueue(intent: Intent) {
        val copy = Intent(intent)
        if (pendingCommands.size >= 20) {
            pendingCommands.removeFirst()
        }
        pendingCommands.addLast(copy)
        scheduleFlush(INITIAL_FLUSH_DELAY_MS)
    }

    fun onShellAvailable() {
        flush()
    }

    fun onShellUnavailable() {
        // no-op; retry scheduling happens from flush()
    }

    fun destroy() {
        pendingCommands.clear()
        uiHandler.removeCallbacks(flushRunnable)
        retryScheduled = false
        isFlushing = false
    }

    private fun scheduleFlush(delayMs: Long) {
        if (retryScheduled) return
        retryScheduled = true
        uiHandler.postDelayed(flushRunnable, delayMs)
    }

    private fun flush() {
        if (isFlushing) return

        if (!isShellReady()) {
            val now = System.currentTimeMillis()
            if (now - lastBindAttemptAt > 1000L) {
                lastBindAttemptAt = now
                requestShellBind()
            }
            if (pendingCommands.isNotEmpty()) {
                scheduleFlush(SHELL_RETRY_DELAY_MS)
            }
            return
        }

        isFlushing = true
        try {
            val next = if (pendingCommands.isEmpty()) null else pendingCommands.removeFirst()
            if (next != null) {
                executeCommand(next)
            }

            if (pendingCommands.isNotEmpty()) {
                scheduleFlush(COMMAND_INTERVAL_MS)
            }
        } catch (e: Exception) {
            Log.e(logTag, "Window command flush failed", e)
            if (pendingCommands.isNotEmpty()) {
                scheduleFlush(ERROR_RETRY_DELAY_MS)
            }
        } finally {
            isFlushing = false
        }
    }
}