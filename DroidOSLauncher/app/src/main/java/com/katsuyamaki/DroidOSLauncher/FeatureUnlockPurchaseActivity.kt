package com.katsuyamaki.DroidOSLauncher

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

class FeatureUnlockPurchaseActivity : Activity(), PlayBillingManager.Listener {

    companion object {
        const val EXTRA_FEATURE_ID = "extra_feature_id"
    }

    private var requestedFeatureId: String? = null
    private var purchaseFlowLaunched = false
    private var storeFallbackOpened = false
    private var billingOutcomeResolved = false
    private var billingManager: PlayBillingManager? = null
    private var statusTextView: TextView? = null
    private val uiHandler = Handler(Looper.getMainLooper())
    private val startupTimeoutRunnable = Runnable {
        if (billingOutcomeResolved || purchaseFlowLaunched || isFinishing) return@Runnable
        updateStatus("Google Play billing unavailable. Opening Play Store...")
        Toast.makeText(this, "Google Play billing unavailable. Opening Play Store.", Toast.LENGTH_SHORT).show()
        openPlayStoreFallbackListing()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createContentView())
        updateStatus("Connecting to Google Play Billing...")

        requestedFeatureId = intent.getStringExtra(EXTRA_FEATURE_ID)
        val featureId = requestedFeatureId
        if (featureId.isNullOrBlank()) {
            finish()
            return
        }

        billingManager = PlayBillingManager(this, this)
        billingManager?.connect()
        uiHandler.postDelayed(startupTimeoutRunnable, 4000L)
    }

    override fun onDestroy() {
        uiHandler.removeCallbacks(startupTimeoutRunnable)
        billingManager?.disconnect()
        super.onDestroy()
    }

    override fun onBillingReady(availableProductIds: Set<String>) {
        val featureId = requestedFeatureId ?: run {
            finish()
            return
        }
        if (billingOutcomeResolved) {
            finish()
            return
        }

        if (FeatureUnlockCatalog.isFeatureUnlocked(this, featureId)) {
            billingOutcomeResolved = true
            updateStatus("Feature already unlocked. Opening Play Store...")
            notifyUnlockUpdated()
            Toast.makeText(this, "Feature already unlocked. Opening Play Store.", Toast.LENGTH_SHORT).show()
            openPlayStoreFallbackListing()
            finish()
            return
        }

        val productId = FeatureUnlockCatalog.getProductIdForFeature(featureId)
        if (productId.isNullOrBlank()) {
            billingOutcomeResolved = true
            updateStatus("Unknown unlock target.")
            Toast.makeText(this, "Unknown unlock target.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (!availableProductIds.contains(productId)) {
            billingOutcomeResolved = true
            updateStatus("Product not found. Opening Play Store...")
            Toast.makeText(this, "Product not found in Play Console: $productId", Toast.LENGTH_LONG).show()
            openPlayStoreFallbackListing()
            finish()
            return
        }

        if (!purchaseFlowLaunched) {
            updateStatus("Launching Google Play purchase screen...")
            purchaseFlowLaunched = true
            billingManager?.launchPurchaseForFeature(featureId)
        }
    }

    override fun onFeatureUnlockStateChanged(unlockedFeatureIds: Set<String>) {
        val featureId = requestedFeatureId ?: return
        if (unlockedFeatureIds.contains(featureId)) {
            billingOutcomeResolved = true
            notifyUnlockUpdated()
            Toast.makeText(this, "Feature unlocked.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onPurchaseFlowCanceled() {
        billingOutcomeResolved = true
        updateStatus("Purchase canceled.")
        Toast.makeText(this, "Purchase canceled.", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onBillingError(message: String) {
        billingOutcomeResolved = true
        updateStatus("Billing error. Opening Play Store...")
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        openPlayStoreFallbackListing()
        finish()
    }

    private fun createContentView(): LinearLayout {
        val density = resources.displayMetrics.density
        val spacing = (16 * density).toInt()
        val padding = (24 * density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(padding, padding, padding, padding)
            setBackgroundColor(Color.BLACK)
        }

        val spinner = ProgressBar(this).apply {
            isIndeterminate = true
        }
        root.addView(spinner)

        statusTextView = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 16f
            gravity = Gravity.CENTER
            text = "Preparing Google Play unlock flow..."
            setPadding(0, spacing, 0, spacing)
        }
        root.addView(
            statusTextView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val openStoreButton = Button(this).apply {
            text = "Open Google Play Store"
            setOnClickListener {
                openPlayStoreFallbackListing()
                finish()
            }
        }
        root.addView(
            openStoreButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val cancelButton = Button(this).apply {
            text = "Cancel"
            setOnClickListener { finish() }
        }
        root.addView(
            cancelButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = spacing / 2 }
        )

        return root
    }

    private fun updateStatus(message: String) {
        statusTextView?.text = message
    }

    private fun openPlayStoreFallbackListing() {
        if (storeFallbackOpened) return
        storeFallbackOpened = true
        billingOutcomeResolved = true

        val intents = listOf(
            Intent(Intent.ACTION_VIEW, Uri.parse("market://store")),
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store")),
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")),
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
        )

        for (storeIntent in intents) {
            try {
                startActivity(storeIntent)
                return
            } catch (_: Exception) {
            }
        }
    }

    private fun notifyUnlockUpdated() {
        val intent = Intent(FeatureUnlockCatalog.ACTION_FEATURE_UNLOCKS_UPDATED)
            .setPackage(packageName)
        sendBroadcast(intent)
    }
}