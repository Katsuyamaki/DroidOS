package com.katsuyamaki.DroidOSLauncher


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VisualQueueAdapter(
    private val handler: LauncherActionHandler,
    private val highlightIndex: Int
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
        val app = handler.selectedAppsQueue[position]
        val slotNum = position + 1

        val isFocused = (app.packageName == handler.activePackageName) ||
                        (app.packageName == "com.google.android.apps.bard" && handler.activePackageName == "com.google.android.googlequicksearchbox")


        holder.underline.visibility = if (isFocused) View.VISIBLE else View.GONE
        holder.badge.text = slotNum.toString()
        holder.badge.setScaledTextSize(handler.currentFontSize, 0.625f)

        if (app.packageName == PACKAGE_BLANK) {
            holder.icon.setImageResource(R.drawable.ic_box_outline)
            holder.icon.alpha = 0.5f
        } else {
            try {
                val basePkg = if (app.packageName.contains(":")) app.packageName.substringBefore(":") else app.packageName
                holder.icon.setImageDrawable(handler.handlerPackageManager.getApplicationIcon(basePkg))
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
        val isCommandSource = (handler.pendingArg1 >= 0 && position == handler.pendingArg1)
        
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
            handler.handleCommandInput(slotNum)
        }
    }
    
    override fun getItemCount() = handler.selectedAppsQueue.size
    
    private fun TextView.setScaledTextSize(baseFontSize: Float, scaleFactor: Float = 1.0f) {
        this.textSize = baseFontSize * scaleFactor
    }
}