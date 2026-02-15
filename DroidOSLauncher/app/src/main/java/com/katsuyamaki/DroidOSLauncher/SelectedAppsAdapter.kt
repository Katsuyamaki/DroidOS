package com.katsuyamaki.DroidOSLauncher

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for the selected apps queue dock in the drawer.
 * Supports minimize toggle, reorder, and keyboard navigation.
 */
class SelectedAppsAdapter(
    private val actionHandler: LauncherActionHandler
) : RecyclerView.Adapter<SelectedAppsAdapter.Holder>() {
    
    inner class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.selected_app_icon)
        val underline: View = v.findViewById(R.id.focus_underline)
        val frame: View = itemView
        val slotBadge: TextView = v.findViewById(R.id.slot_number_badge)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_app, parent, false))
    }
    
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val queue = actionHandler.getSelectedAppsQueue()
        val app = queue[position]
        val activePackage = actionHandler.getActivePackageName()
        val currentFocusArea = actionHandler.getCurrentFocusArea()
        val queueSelectedIndex = actionHandler.getQueueSelectedIndex()
        val queueCommandPending = actionHandler.getQueueCommandPending()
        val queueCommandSourceIndex = actionHandler.getQueueCommandSourceIndex()
        val showSlotNumbers = actionHandler.isShowSlotNumbersInQueue()
        val reorderIndex = actionHandler.getReorderSelectionIndex()
        
        // Focus underline
        val isFocused = (app.packageName == activePackage) ||
                        (app.packageName == "com.google.android.apps.bard" && activePackage == "com.google.android.googlequicksearchbox")
        holder.underline.visibility = if (isFocused) View.VISIBLE else View.GONE
        
        // Keyboard navigation highlight
        val isNavSelected = (currentFocusArea == LauncherActionHandler.FOCUS_QUEUE && position == queueSelectedIndex)
        val isCommandSource = (queueCommandPending != null && position == queueCommandSourceIndex)
        
        if (isNavSelected) {
            val bg = GradientDrawable()
            bg.setStroke(4, Color.WHITE)
            bg.cornerRadius = 8f
            bg.setColor(Color.parseColor("#44FFFFFF"))
            holder.frame.background = bg
        } else if (isCommandSource) {
            val bg = GradientDrawable()
            bg.setStroke(4, Color.GREEN, 10f, 5f)
            bg.cornerRadius = 8f
            holder.frame.background = bg
        } else {
            holder.frame.background = null
        }
        
        // Slot number badge
        if (showSlotNumbers) {
            holder.slotBadge.visibility = View.VISIBLE
            holder.slotBadge.text = (position + 1).toString()
        } else {
            holder.slotBadge.visibility = View.GONE
        }
        
        holder.icon.clearColorFilter()
        
        if (app.packageName == LauncherActionHandler.PACKAGE_BLANK) {
            holder.icon.setImageResource(R.drawable.ic_box_outline)
        } else {
            try {
                holder.icon.setImageDrawable(actionHandler.getPackageManagerRef().getApplicationIcon(app.packageName))
            } catch (e: Exception) {
                holder.icon.setImageResource(R.drawable.ic_launcher_bubble)
            }
            holder.icon.alpha = if (app.isMinimized) 0.4f else 1.0f
        }
        
        holder.itemView.setOnClickListener {
            actionHandler.dismissKeyboardAndRestore()
            if (reorderIndex != -1) {
                // Handle reorder mode via service
                val intent = Intent().apply {
                    putExtra("COMMAND", "REORDER_TAP")
                    putExtra("INDEX", position)
                }
                actionHandler.handleWindowManagerCommand(intent)
            } else {
                if (app.packageName != LauncherActionHandler.PACKAGE_BLANK) {
                    val intent = Intent().apply {
                        putExtra("COMMAND", "TOGGLE_MINIMIZE")
                        putExtra("INDEX", position + 1)
                    }
                    actionHandler.handleWindowManagerCommand(intent)
                }
            }
        }
        
        holder.itemView.setOnLongClickListener {
            val intent = Intent().apply {
                putExtra("COMMAND", "START_REORDER")
                putExtra("INDEX", position)
            }
            actionHandler.handleWindowManagerCommand(intent)
            true
        }
    }
    
    override fun getItemCount() = actionHandler.getSelectedAppsQueue().size
}