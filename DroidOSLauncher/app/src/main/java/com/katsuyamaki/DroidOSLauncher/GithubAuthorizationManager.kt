package com.katsuyamaki.DroidOSLauncher

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.time.Instant
import java.util.Locale
import java.util.UUID

object GithubAuthorizationManager {

    private const val PREFS_NAME = "GithubAuthFlowPrefs"
    private const val KEY_INSTALL_ID = "github_install_id"
    private val CLAIM_CODE_REGEX = Regex("^[A-Za-z0-9_-]{6,128}$")

    sealed class ExchangeResult {
        data class Success(
            val accessToken: String,
            val tokenId: String?,
            val expiresAtEpochSeconds: Long?
        ) : ExchangeResult()

        data class Failure(val message: String) : ExchangeResult()
    }

    fun isPlausibleClaimCode(claimCode: String): Boolean {
        return CLAIM_CODE_REGEX.matches(claimCode.trim())
    }

    fun buildClaimPortalUri(context: Context, portalUrl: String): Uri? {
        val cleanUrl = portalUrl.trim()
        if (cleanUrl.isEmpty()) return null

        return try {
            Uri.parse(cleanUrl)
                .buildUpon()
                .appendQueryParameter("channel", "github")
                .appendQueryParameter("app_version", appVersionLabel())
                .appendQueryParameter("app_version_code", BuildConfig.VERSION_CODE.toString())
                .appendQueryParameter("device_id_hash", getDeviceIdHash(context))
                .appendQueryParameter("package_name", context.packageName)
                .appendQueryParameter("app_id", context.packageName)
                .appendQueryParameter("signing_cert_sha256", getSigningCertSha256Fingerprint(context))
                .build()
        } catch (e: Exception) {
            null
        }
    }

    fun exchangeClaimCode(context: Context, exchangeUrl: String, claimCode: String): ExchangeResult {
        if (!BuildConfig.USE_GITHUB_AUTH) {
            return ExchangeResult.Failure("GitHub authorization is disabled in this build")
        }

        val cleanClaimCode = claimCode.trim()
        if (!isPlausibleClaimCode(cleanClaimCode)) {
            return ExchangeResult.Failure("Claim code format is invalid")
        }

        val resolvedUrl = resolveExchangeUrl(exchangeUrl)
        if (resolvedUrl.isEmpty()) {
            return ExchangeResult.Failure("Auth exchange endpoint is not configured for this build")
        }

        val requestPayload = JSONObject().apply {
            val deviceIdHash = getDeviceIdHash(context)
            put("claim_code", cleanClaimCode)
            put("channel", "github")
            put("app_version", appVersionLabel())
            put("app_version_code", BuildConfig.VERSION_CODE)
            put("device_id_hash", deviceIdHash)
            put("install_id_hash", deviceIdHash)
            put("package_name", context.packageName)
            put("app_id", context.packageName)
            put("signing_cert_sha256", getSigningCertSha256Fingerprint(context))
            put("store_channel", BuildConfig.STORE_CHANNEL)
        }

        val connection = try {
            (URL(resolvedUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15000
                readTimeout = 15000
                doOutput = true
                doInput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "DroidOSLauncher/${appVersionLabel()} (${BuildConfig.STORE_CHANNEL})")
            }
        } catch (e: Exception) {
            return ExchangeResult.Failure("Invalid auth endpoint configuration")
        }

        return try {
            connection.outputStream.use { output ->
                output.write(requestPayload.toString().toByteArray(Charsets.UTF_8))
                output.flush()
            }

            val statusCode = connection.responseCode
            val responseBody = readResponseBody(connection, statusCode in 200..299)
            val responseJson = parseJson(responseBody)

            if (statusCode !in 200..299) {
                return ExchangeResult.Failure(extractFailureMessage(statusCode, responseJson))
            }

            val accessToken = responseJson?.optString("access_token").orEmpty()
                .ifBlank { responseJson?.optString("token").orEmpty() }
                .trim()

            if (accessToken.isEmpty()) {
                return ExchangeResult.Failure("Token exchange succeeded but no access token was returned")
            }

            val tokenId = responseJson?.optString("token_id")
                ?.trim()
                ?.takeIf { it.isNotEmpty() }

            val expiresAt = parseExpiryEpochSeconds(responseJson)
            ExchangeResult.Success(accessToken, tokenId, expiresAt)
        } catch (e: Exception) {
            ExchangeResult.Failure("Unable to exchange claim code. Check network and try again.")
        } finally {
            connection.disconnect()
        }
    }

    fun getDeviceIdHash(context: Context): String {
        val installId = getOrCreateInstallId(context)
        return sha256LowercaseHex((context.packageName + ":" + installId).toByteArray(Charsets.UTF_8))
    }

    private fun resolveExchangeUrl(raw: String): String {
        val clean = raw.trim().removeSuffix("/")
        if (clean.isEmpty()) return ""
        return if (clean.endsWith("/exchange-token")) clean else "$clean/exchange-token"
    }

    private fun getOrCreateInstallId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_INSTALL_ID, null)?.trim().orEmpty()
        if (existing.isNotEmpty()) return existing

        val created = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_INSTALL_ID, created).apply()
        return created
    }

    private fun getSigningCertSha256Fingerprint(context: Context): String {
        return try {
            @Suppress("DEPRECATION")
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    PackageManager.GET_SIGNING_CERTIFICATES
                } else {
                    PackageManager.GET_SIGNATURES
                }
            )

            val certBytes: ByteArray? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signingInfo = packageInfo.signingInfo ?: return ""
                val signatures = if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }
                signatures.firstOrNull()?.toByteArray()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures?.firstOrNull()?.toByteArray()
            }

            certBytes?.let { sha256UppercaseFingerprint(it) }.orEmpty()
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseExpiryEpochSeconds(json: JSONObject?): Long? {
        if (json == null) return null

        val numericKeys = listOf(
            "expires_at_epoch_seconds",
            "expires_at_epoch",
            "expires_epoch_seconds",
            "expiry_epoch_seconds",
            "exp"
        )

        for (key in numericKeys) {
            if (!json.has(key)) continue

            val asLong = json.optLong(key, -1L)
            if (asLong > 0L) return asLong

            val text = json.optString(key).trim()
            val parsed = text.toLongOrNull()
            if (parsed != null && parsed > 0L) return parsed
        }

        val isoExpiry = json.optString("expires_at").trim()
        if (isoExpiry.isNotEmpty()) {
            try {
                return Instant.parse(isoExpiry).epochSecond
            } catch (_: Exception) {
            }
        }

        return null
    }

    private fun parseJson(raw: String): JSONObject? {
        val body = raw.trim()
        if (body.isEmpty()) return null

        return try {
            JSONObject(body)
        } catch (e: Exception) {
            null
        }
    }

    private fun readResponseBody(connection: HttpURLConnection, success: Boolean): String {
        val stream = if (success) connection.inputStream else connection.errorStream
        return stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
    }

    private fun extractFailureMessage(statusCode: Int, json: JSONObject?): String {
        val candidates = listOf(
            json?.optString("error_description"),
            json?.optString("error"),
            json?.optString("message"),
            json?.optString("detail")
        )

        val message = candidates.firstOrNull { !it.isNullOrBlank() }?.trim()
        return if (!message.isNullOrEmpty()) message else "Token exchange failed ($statusCode)"
    }

    private fun appVersionLabel(): String {
        return "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
    }

    private fun sha256LowercaseHex(input: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input)
        return digest.joinToString(separator = "") { byte -> "%02x".format(Locale.US, byte.toInt() and 0xFF) }
    }

    private fun sha256UppercaseFingerprint(input: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input)
        return digest.joinToString(separator = ":") { byte -> "%02X".format(Locale.US, byte.toInt() and 0xFF) }
    }
}