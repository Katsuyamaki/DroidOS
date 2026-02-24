package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.os.SystemClock
import java.util.LinkedHashMap

/**
 * Tracks overlay->IME broadcast processing health across services.
 */
object ImeBroadcastHealth {

    data class Snapshot(
        val sequence: Long,
        val requestId: Long,
        val action: String,
        val success: Boolean,
        val detail: String,
        val updatedAtElapsedMs: Long
    )

    private const val FALLBACK_TTL_MS = 5000L
    private const val MAX_FALLBACK_REQUESTS = 128

    private val lock = Any()

    private var sequence = 0L
    private var requestId = -1L
    private var action = ""
    private var success = false
    private var detail = "init"
    private var updatedAtElapsedMs = 0L

    private val fallbackRequests = LinkedHashMap<Long, Long>()

    fun record(action: String, requestId: Long, success: Boolean, detail: String) {
        synchronized(lock) {
            sequence += 1L
            this.requestId = requestId
            this.action = action
            this.success = success
            this.detail = detail
            this.updatedAtElapsedMs = SystemClock.elapsedRealtime()
        }
    }

    fun snapshot(): Snapshot {
        synchronized(lock) {
            return Snapshot(
                sequence = sequence,
                requestId = requestId,
                action = action,
                success = success,
                detail = detail,
                updatedAtElapsedMs = updatedAtElapsedMs
            )
        }
    }

    fun markFallbackRequest(requestId: Long) {
        if (requestId < 0L) return
        synchronized(lock) {
            val now = SystemClock.elapsedRealtime()
            trimExpiredLocked(now)
            fallbackRequests[requestId] = now
            while (fallbackRequests.size > MAX_FALLBACK_REQUESTS) {
                val firstKey = fallbackRequests.entries.firstOrNull()?.key ?: break
                fallbackRequests.remove(firstKey)
            }
        }
    }

    fun consumeFallbackRequest(requestId: Long): Boolean {
        if (requestId < 0L) return false
        synchronized(lock) {
            trimExpiredLocked(SystemClock.elapsedRealtime())
            return fallbackRequests.remove(requestId) != null
        }
    }

    private fun trimExpiredLocked(now: Long) {
        val iterator = fallbackRequests.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (now - entry.value > FALLBACK_TTL_MS) {
                iterator.remove()
            }
        }
    }
}