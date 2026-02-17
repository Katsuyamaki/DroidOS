package com.katsuyamaki.DroidOSLauncher

import com.katsuyamaki.DroidOSLauncher.FocusAreas

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectedAppsAdapter(
    private val handler: LauncherActionHandler
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
        val app = handler.selectedAppsQueue[position]

        val isFocused = (app.packageName == handler.activePackageName) ||
                        (app.packageName == "com.google.android.apps.bard" && handler.activePackageName == "com.google.android.googlequicksearchbox")
        holder.underline.visibility = if (isFocused) View.VISIBLE else View.GONE

        val isNavSelected = (handler.currentFocusArea == FocusAreas.FOCUS_QUEUE && position == handler.queueSelectedIndex)
        val isCommandSource = (handler.queueCommandPending != null && position == handler.queueCommandSourceIndex)

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
        
        if (handler.showSlotNumbersInQueue) {
            holder.slotBadge.visibility = View.VISIBLE
            holder.slotBadge.text = (position + 1).toString()
        } else {
            holder.slotBadge.visibility = View.GONE
        }

        holder.icon.clearColorFilter()
        
        if (app.packageName == PACKAGE_BLANK) { 
            holder.icon.setImageResource(R.drawable.ic_box_outline) 
        } else { 
            try { 
                val iconPkg = app.packageName
                holder.icon.setImageDrawable(handler.handlerPackageManager.getApplicationIcon(iconPkg)) 
            } catch (e: Exception) { 
                holder.icon.setImageResource(R.drawable.ic_launcher_bubble) 
            }
            holder.icon.alpha = if (app.isMinimized) 0.4f else 1.0f 
        }
        
        holder.itemView.setOnClickListener { 
            try { 
                handler.dismissKeyboardAndRestore()
                if (handler.reorderSelectionIndex != -1) { 
                    if (position == handler.reorderSelectionIndex) { 
                        handler.endReorderMode(false) 
                    } else { 
                        handler.swapReorderItem(position) 
                    } 
                } else { 
                    if (app.packageName != PACKAGE_BLANK) { 
                        val intent = Intent().apply {
                            putExtra("COMMAND", "TOGGLE_MINIMIZE")
                            putExtra("INDEX", position + 1)
                        }
                        handler.handleWindowManagerCommand(intent)
                    } 
                } 
            } catch(e: Exception) {} 
        }
        
        holder.itemView.setOnLongClickListener { 
            if (handler.isReorderTapEnabled) { 
                handler.startReorderMode(position)
                true 
            } else { 
                false 
            } 
        }
    }

    override fun getItemCount() = handler.selectedAppsQueue.size
}