package com.katsuyamaki.DroidOSLauncher

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecureTokenStore {

    private const val PREFS_NAME = "SecureAuthPrefs"
    private const val KEY_GITHUB_TOKEN = "github_auth_token_ciphertext"
    private const val KEY_ALIAS = "droidos_github_auth_token_key"
    private const val KEYSTORE_TYPE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_BITS = 128

    fun setGithubAuthorizationToken(context: Context, token: String) {
        val encrypted = encrypt(token.trim()) ?: return
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_GITHUB_TOKEN, encrypted)
            .apply()
    }

    fun getGithubAuthorizationToken(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encrypted = prefs.getString(KEY_GITHUB_TOKEN, null)?.trim().orEmpty()
        if (encrypted.isEmpty()) return ""

        val decrypted = decrypt(encrypted)
        if (decrypted == null) {
            prefs.edit().remove(KEY_GITHUB_TOKEN).apply()
            return ""
        }
        return decrypted
    }

    fun clearGithubAuthorizationToken(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_GITHUB_TOKEN)
            .apply()
    }

    private fun encrypt(raw: String): String? {
        return try {
            val secretKey = getOrCreateSecretKey() ?: return null
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encrypted = cipher.doFinal(raw.toByteArray(Charsets.UTF_8))

            val ivPart = Base64.encodeToString(iv, Base64.NO_WRAP)
            val dataPart = Base64.encodeToString(encrypted, Base64.NO_WRAP)
            "$ivPart.$dataPart"
        } catch (e: Exception) {
            null
        }
    }

    private fun decrypt(encoded: String): String? {
        return try {
            val parts = encoded.split(".", limit = 2)
            if (parts.size != 2) return null

            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val encrypted = Base64.decode(parts[1], Base64.NO_WRAP)
            val secretKey = getOrCreateSecretKey() ?: return null

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_BITS, iv))
            String(cipher.doFinal(encrypted), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    private fun getOrCreateSecretKey(): SecretKey? {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_TYPE).apply { load(null) }
            val existing = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
            if (existing != null) return existing

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_TYPE)
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build()

            keyGenerator.init(spec)
            keyGenerator.generateKey()
        } catch (e: Exception) {
            null
        }
    }
}