package com.katsuyamaki.DroidOSLauncher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class FeatureUnlockPurchaseActivity : Activity(), PlayBillingManager.Listener {

    companion object {
        const val EXTRA_FEATURE_ID = "extra_feature_id"
    }

    private var requestedFeatureId: String? = null
    private var purchaseFlowLaunched = false
    private var billingManager: PlayBillingManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedFeatureId = intent.getStringExtra(EXTRA_FEATURE_ID)
        val featureId = requestedFeatureId
        if (featureId.isNullOrBlank()) {
            finish()
            return
        }

        billingManager = PlayBillingManager(this, this)
        billingManager?.connect()
    }

    override fun onDestroy() {
        billingManager?.disconnect()
        super.onDestroy()
    }

    override fun onBillingReady(availableProductIds: Set<String>) {
        val featureId = requestedFeatureId ?: run {
            finish()
            return
        }

        if (FeatureUnlockCatalog.isFeatureUnlocked(this, featureId)) {
            notifyUnlockUpdated()
            Toast.makeText(this, "Feature already unlocked.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val productId = FeatureUnlockCatalog.getProductIdForFeature(featureId)
        if (productId.isNullOrBlank()) {
            Toast.makeText(this, "Unknown unlock target.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (!availableProductIds.contains(productId)) {
            Toast.makeText(this, "Product not found in Play Console: $productId", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (!purchaseFlowLaunched) {
            purchaseFlowLaunched = true
            billingManager?.launchPurchaseForFeature(featureId)
        }
    }

    override fun onFeatureUnlockStateChanged(unlockedFeatureIds: Set<String>) {
        val featureId = requestedFeatureId ?: return
        if (unlockedFeatureIds.contains(featureId)) {
            notifyUnlockUpdated()
            Toast.makeText(this, "Feature unlocked.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onPurchaseFlowCanceled() {
        finish()
    }

    override fun onBillingError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun notifyUnlockUpdated() {
        val intent = Intent(FeatureUnlockCatalog.ACTION_FEATURE_UNLOCKS_UPDATED)
            .setPackage(packageName)
        sendBroadcast(intent)
    }
}