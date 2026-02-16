package com.katsuyamaki.DroidOSLauncher

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Manages the Visual Queue HUD overlay for keyboard command input.
 * Uses lazy initialization and dynamic context for safety.
 */
class VisualQueueManager(
    private val context: Context,
    private val uiHandler: Handler
) {
    companion object {
        private const val TAG = "VisualQueueManager"
    }

    // View state only - WindowManager passed dynamically
    private var visualQueueView: View? = null
    private var visualQueueParams: WindowManager.LayoutParams? = null
    private var visualQueueWindowManager: WindowManager? = null
    var isVisualQueueVisible = false
        private set

    // Callback interface
    interface Callback {
        fun onToast(msg: String)
        fun onSendBroadcast(intent: Intent)
        fun onFetchRunningApps()
        fun onSortAppQueue()
        fun getSelectedAppsQueue(): MutableList<MainActivity.AppInfo>
        fun getActivePackageName(): String?
        fun getCurrentDisplayId(): Int
        fun getDisplayContext(): Context?
        fun getShellService(): IShellService?
        fun getPackageBlank(): String
        fun getPackageTrackpad(): String
        fun getTopMarginPercent(): Int
        fun getEffectiveBottomMarginPercent(): Int
        fun getCurrentFontSize(): Float
        fun getPendingArg1(): Int
        fun getBaseWindowManager(): WindowManager
    }

    private var callback: Callback? = null

    fun setCallback(cb: Callback) {
        callback = cb
    }

    private fun setupVisualQueue(displayContext: Context?, baseWindowManager: WindowManager) {
        try {
            // Cleanup existing
            if (isVisualQueueVisible && visualQueueView != null) {
                try { visualQueueWindowManager?.removeView(visualQueueView) } catch (e: Exception) {}
                try { (displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.removeView(visualQueueView) } catch (e: Exception) {}
                try { baseWindowManager.removeView(visualQueueView) } catch (e: Exception) {}
                isVisualQueueVisible = false
                visualQueueWindowManager = null
            }

            val themeContext = ContextThemeWrapper(displayContext ?: context, R.style.Theme_QuadrantLauncher)
            visualQueueView = LayoutInflater.from(themeContext).inflate(R.layout.layout_visual_queue, null)

            val targetType = if (Build.VERSION.SDK_INT >= 26) 2032 else WindowManager.LayoutParams.TYPE_PHONE

            visualQueueParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                targetType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            )

            val cb = callback ?: return
            val targetWM = displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: baseWindowManager
            val display = targetWM.defaultDisplay
            val h = display.height

            val topPx = (h * cb.getTopMarginPercent() / 100f).toInt()
            val bottomPx = (h * cb.getEffectiveBottomMarginPercent() / 100f).toInt()
            val yShift = (topPx - bottomPx) / 2

            visualQueueParams?.gravity = Gravity.CENTER
            visualQueueParams?.y = yShift

            val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
            recycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            val dummyInput = visualQueueView?.findViewById<EditText>(R.id.vq_dummy_input)
            dummyInput?.showSoftInputOnFocus = true
        } catch (e: Exception) {
            Log.e(TAG, "setupVisualQueue failed", e)
        }
    }

    fun showVisualQueue(prompt: String, highlightSlot0Based: Int = -1, onInputCallback: (Int) -> Unit) {
        val cb = callback ?: return
        
        try {
            val displayContext = cb.getDisplayContext()
            val baseWindowManager = cb.getBaseWindowManager()
            val PACKAGE_TRACKPAD = cb.getPackageTrackpad()

            // Fast path: update existing view
            if (isVisualQueueVisible && visualQueueView != null) {
                val promptView = visualQueueView?.findViewById<TextView>(R.id.visual_queue_prompt)
                promptView?.text = prompt
                val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
                recycler?.adapter = VisualQueueAdapter(highlightSlot0Based, cb, onInputCallback)
                Thread {
                    val visible = cb.getShellService()?.getVisiblePackages(cb.getCurrentDisplayId()) ?: emptyList()
                    uiHandler.post {
                        (recycler?.adapter as? VisualQueueAdapter)?.updateVisibility(visible)
                    }
                }.start()
                return
            }

            // Cleanup stale views
            if (visualQueueView != null) {
                Log.w(TAG, "Visual Queue cleanup: removing stale view")
                try { visualQueueWindowManager?.removeView(visualQueueView) } catch (e: Exception) {}
                try { (displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.removeView(visualQueueView) } catch (e: Exception) {}
                try { baseWindowManager.removeView(visualQueueView) } catch (e: Exception) {}
                isVisualQueueVisible = false
                visualQueueWindowManager = null
            }

            if (visualQueueView == null) setupVisualQueue(displayContext, baseWindowManager)

            // Sync queue
            if (cb.getShellService() != null) {
                cb.onFetchRunningApps()
            } else {
                cb.onSortAppQueue()
            }

            // Background thread for visibility
            Thread {
                val visible = cb.getShellService()?.getVisiblePackages(cb.getCurrentDisplayId()) ?: emptyList()
                uiHandler.post {
                    val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
                    recycler?.adapter = VisualQueueAdapter(highlightSlot0Based, cb, onInputCallback)
                    (recycler?.adapter as? VisualQueueAdapter)?.updateVisibility(visible)
                }
            }.start()

            val promptView = visualQueueView?.findViewById<TextView>(R.id.visual_queue_prompt)
            promptView?.text = prompt

            val recycler = visualQueueView?.findViewById<RecyclerView>(R.id.visual_queue_recycler)
            recycler?.adapter = VisualQueueAdapter(highlightSlot0Based, cb, onInputCallback)

            val dummy = visualQueueView?.findViewById<EditText>(R.id.vq_dummy_input)
            dummy?.requestFocus()

            if (!isVisualQueueVisible) {
                try {
                    val targetWM = displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: baseWindowManager
                    visualQueueWindowManager = targetWM
                    targetWM.addView(visualQueueView, visualQueueParams)
                    isVisualQueueVisible = true
                    Log.d(TAG, "Visual Queue Added to Display ${cb.getCurrentDisplayId()}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to add Visual Queue", e)
                }
            }

            // Tell Trackpad to redirect input
            val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
            captureIntent.setPackage(PACKAGE_TRACKPAD)
            captureIntent.putExtra("CAPTURE", true)
            cb.onSendBroadcast(captureIntent)

            val layerIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_NUM_LAYER")
            layerIntent.setPackage(PACKAGE_TRACKPAD)
            layerIntent.putExtra("ACTIVE", true)
            cb.onSendBroadcast(layerIntent)
        } catch (e: Exception) {
            Log.e(TAG, "showVisualQueue failed", e)
        }
    }

    fun hideVisualQueue() {
        val cb = callback ?: return
        
        try {
            val displayContext = cb.getDisplayContext()
            val baseWindowManager = cb.getBaseWindowManager()
            val PACKAGE_TRACKPAD = cb.getPackageTrackpad()

            if (isVisualQueueVisible && visualQueueView != null) {
                var removed = false
                visualQueueWindowManager?.let { wm ->
                    try {
                        wm.removeView(visualQueueView)
                        removed = true
                    } catch (e: Exception) {}
                }
                if (!removed) {
                    try {
                        val targetWM = displayContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
                        targetWM?.removeView(visualQueueView)
                        removed = true
                    } catch (e: Exception) {}
                }
                if (!removed) {
                    try {
                        baseWindowManager.removeView(visualQueueView)
                        removed = true
                    } catch (e: Exception) {}
                }
                if (removed) {
                    isVisualQueueVisible = false
                    visualQueueWindowManager = null
                } else {
                    Log.e(TAG, "CRITICAL: Failed to remove Visual Queue!")
                    visualQueueView = null
                    isVisualQueueVisible = false
                    visualQueueWindowManager = null
                }
            }

            val captureIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_INPUT_CAPTURE")
            captureIntent.setPackage(PACKAGE_TRACKPAD)
            captureIntent.putExtra("CAPTURE", false)
            cb.onSendBroadcast(captureIntent)

            val layerIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SET_NUM_LAYER")
            layerIntent.setPackage(PACKAGE_TRACKPAD)
            layerIntent.putExtra("ACTIVE", false)
            cb.onSendBroadcast(layerIntent)
        } catch (e: Exception) {
            Log.e(TAG, "hideVisualQueue failed", e)
        }
    }

    fun getPromptText(): String {
        return visualQueueView?.findViewById<TextView>(R.id.visual_queue_prompt)?.text?.toString() ?: ""
    }

    fun cleanup() {
        try {
            if (visualQueueView != null) {
                try { visualQueueWindowManager?.removeView(visualQueueView) } catch (e: Exception) {}
                try { callback?.getBaseWindowManager()?.removeView(visualQueueView) } catch (e: Exception) {}
                visualQueueView = null
            }
            visualQueueWindowManager = null
            isVisualQueueVisible = false
        } catch (e: Exception) {
            Log.e(TAG, "cleanup failed", e)
        }
    }

    // Inner adapter class
    inner class VisualQueueAdapter(
        private val highlightIndex: Int,
        private val cb: Callback,
        private val onInputCallback: (Int) -> Unit
    ) : RecyclerView.Adapter<VisualQueueAdapter.Holder>() {

        private var visiblePackages: List<String> = emptyList()

        fun updateVisibility(visible: List<String>) {
            visiblePackages = visible
            notifyDataSetChanged()
        }

        inner class Holder(v: View) : RecyclerView.ViewHolder(v) {
            val icon: ImageView = v.findViewById(R.id.vq_app_icon)
            val badge: TextView = v.findViewById(R.id.vq_slot_number)
            val highlight: View = v.findViewById(R.id.vq_highlight)
            val underline: View = v.findViewById(R.id.focus_underline)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_visual_queue_app, parent, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val selectedAppsQueue = cb.getSelectedAppsQueue()
            if (position >= selectedAppsQueue.size) return
            
            val app = selectedAppsQueue[position]
            val slotNum = position + 1

            val activePackageName = cb.getActivePackageName()
            val isFocused = (app.packageName == activePackageName) ||
                            (app.packageName == "com.google.android.apps.bard" && activePackageName == "com.google.android.googlequicksearchbox")

            holder.underline.visibility = if (isFocused) View.VISIBLE else View.GONE
            holder.badge.text = slotNum.toString()
            holder.badge.setScaledTextSize(cb.getCurrentFontSize(), 0.625f)

            if (app.packageName == cb.getPackageBlank()) {
                holder.icon.setImageResource(R.drawable.ic_box_outline)
                holder.icon.alpha = 0.5f
            } else {
                try {
                    val basePkg = if (app.packageName.contains(":")) app.packageName.substringBefore(":") else app.packageName
                    holder.icon.setImageDrawable(context.packageManager.getApplicationIcon(basePkg))
                } catch (e: Exception) {
                    holder.icon.setImageResource(R.drawable.ic_launcher_bubble)
                }

                val isVisibleOnScreen = visiblePackages.contains(app.getBasePackage()) ||
                                      (app.getBasePackage() == "com.google.android.apps.bard" && visiblePackages.contains("com.google.android.googlequicksearchbox"))

                val isActuallyActive = if (visiblePackages.isNotEmpty()) {
                    isVisibleOnScreen
                } else {
                    !app.isMinimized
                }

                holder.icon.alpha = if (isActuallyActive) 1.0f else 0.4f
            }

            val isCurrentSelection = (position == highlightIndex)
            val pendingArg1 = cb.getPendingArg1()
            val isCommandSource = (pendingArg1 >= 0 && position == pendingArg1)

            if (isCurrentSelection) {
                holder.highlight.visibility = View.VISIBLE
                val bg = GradientDrawable()
                bg.setStroke(4, Color.WHITE)
                bg.cornerRadius = 8f
                bg.setColor(Color.parseColor("#44FFFFFF"))
                holder.highlight.background = bg
            } else if (isCommandSource) {
                holder.highlight.visibility = View.VISIBLE
                val bg = GradientDrawable()
                bg.setStroke(4, Color.GREEN, 10f, 5f)
                bg.cornerRadius = 8f
                bg.setColor(Color.TRANSPARENT)
                holder.highlight.background = bg
            } else {
                holder.highlight.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                onInputCallback(slotNum)
            }
        }

        override fun getItemCount() = cb.getSelectedAppsQueue().size
    }
}
