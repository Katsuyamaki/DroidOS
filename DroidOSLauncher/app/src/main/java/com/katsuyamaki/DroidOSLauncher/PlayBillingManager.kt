package com.katsuyamaki.DroidOSLauncher

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

class PlayBillingManager(
    private val activity: Activity,
    private val listener: Listener
) : PurchasesUpdatedListener {

    interface Listener {
        fun onBillingReady(availableProductIds: Set<String>)
        fun onFeatureUnlockStateChanged(unlockedFeatureIds: Set<String>)
        fun onPurchaseFlowCanceled()
        fun onBillingError(message: String)
    }

    private var billingClient: BillingClient? = null
    private val productDetailsById = mutableMapOf<String, ProductDetails>()

    fun connect() {
        if (billingClient?.isReady == true) {
            queryCatalogAndOwnedPurchases()
            return
        }

        billingClient = BillingClient.newBuilder(activity)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryCatalogAndOwnedPurchases()
                } else {
                    listener.onBillingError("Billing unavailable: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                // Caller can retry by invoking connect() again.
            }
        })
    }

    fun disconnect() {
        billingClient?.endConnection()
        billingClient = null
        productDetailsById.clear()
    }

    fun launchPurchaseForFeature(featureId: String) {
        val productId = FeatureUnlockCatalog.getProductIdForFeature(featureId)
        if (productId.isNullOrBlank()) {
            listener.onBillingError("Unknown feature: $featureId")
            return
        }

        val details = productDetailsById[productId]
        if (details == null) {
            listener.onBillingError("Product unavailable in Play Console: $productId")
            return
        }

        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .build()

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()

        val result = billingClient?.launchBillingFlow(activity, params)
        if (result == null) {
            listener.onBillingError("Billing client not ready")
            return
        }

        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> Unit
            BillingClient.BillingResponseCode.USER_CANCELED -> listener.onPurchaseFlowCanceled()
            else -> listener.onBillingError("Purchase launch failed: ${result.debugMessage}")
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                processPurchases(purchases.orEmpty())
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                listener.onPurchaseFlowCanceled()
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                queryOwnedPurchases()
            }
            else -> {
                listener.onBillingError("Purchase failed: ${result.debugMessage}")
            }
        }
    }

    fun queryOwnedPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient?.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                listener.onBillingError("Restore failed: ${result.debugMessage}")
                return@queryPurchasesAsync
            }
            processPurchases(purchases)
        }
    }

    private fun queryCatalogAndOwnedPurchases() {
        queryProductDetails()
        queryOwnedPurchases()
    }

    private fun queryProductDetails() {
        val products = FeatureUnlockCatalog.getAllProductIds().map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        if (products.isEmpty()) {
            listener.onBillingReady(emptySet())
            return
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()

        billingClient?.queryProductDetailsAsync(params) { result, detailsList ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                listener.onBillingError("Product query failed: ${result.debugMessage}")
                return@queryProductDetailsAsync
            }

            productDetailsById.clear()
            detailsList.forEach { details ->
                productDetailsById[details.productId] = details
            }
            listener.onBillingReady(productDetailsById.keys)
        }
    }

    private fun processPurchases(purchases: List<Purchase>) {
        val purchasedProductIds = mutableSetOf<String>()

        purchases.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                purchasedProductIds.addAll(purchase.products)
                acknowledgeIfNeeded(purchase)
            }
        }

        val unlocked = FeatureUnlockCatalog.syncUnlockedFeatures(activity, purchasedProductIds)
        listener.onFeatureUnlockStateChanged(unlocked)
    }

    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.isAcknowledged) return

        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(params) { result ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w("PlayBillingManager", "Acknowledge failed: ${result.debugMessage}")
            }
        }
    }
}