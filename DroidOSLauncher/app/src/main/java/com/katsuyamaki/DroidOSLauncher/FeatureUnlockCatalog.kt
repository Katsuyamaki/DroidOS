package com.katsuyamaki.DroidOSLauncher

import android.content.Context

object FeatureUnlockCatalog {
    const val ACTION_FEATURE_UNLOCKS_UPDATED =
        "com.katsuyamaki.DroidOSLauncher.FEATURE_UNLOCKS_UPDATED"

    const val FEATURE_EXTERNAL_BROADCAST_ACCESS = "feature_external_broadcast_access"

    private const val PREFS_NAME = "FeatureUnlockPrefs"
    private const val KEY_PREFIX = "feature_unlocked_"

    // Product IDs must exactly match Google Play Console product IDs.
    private val featureToProductId = mapOf(
        FEATURE_EXTERNAL_BROADCAST_ACCESS to "unlock_external_broadcast_access"
    )

    fun getProductIdForFeature(featureId: String): String? = featureToProductId[featureId]

    fun getFeatureIdForProduct(productId: String): String? {
        return featureToProductId.entries.firstOrNull { it.value == productId }?.key
    }

    fun getAllProductIds(): Set<String> = featureToProductId.values.toSet()

    fun isFeatureUnlocked(context: Context, featureId: String): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_PREFIX + featureId, false)
    }

    fun setFeatureUnlocked(context: Context, featureId: String, unlocked: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_PREFIX + featureId, unlocked)
            .apply()
    }

    fun syncUnlockedFeatures(context: Context, purchasedProductIds: Set<String>): Set<String> {
        val unlockedFeatures = mutableSetOf<String>()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        for ((featureId, productId) in featureToProductId) {
            val unlocked = purchasedProductIds.contains(productId)
            editor.putBoolean(KEY_PREFIX + featureId, unlocked)
            if (unlocked) unlockedFeatures.add(featureId)
        }

        editor.apply()
        return unlockedFeatures
    }
}