package com.example.coverscreentester

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrackpadMenuAdapter(private val items: List<MenuItem>) : 
    RecyclerView.Adapter<TrackpadMenuAdapter.Holder>() {

    data class MenuItem(
        val title: String,
        val iconRes: Int,
        val type: Type,
        val initValue: Int = 0, // For sliders (0-100) or Toggles (0/1)
        val action: ((Any) -> Unit)? = null // Callback
    )

    enum class Type {
        ACTION, // Simple Click
        TOGGLE, // Switch
        SLIDER, // SeekBar
        DPAD,   // Directional Pad
        INFO    // Text only
    }

    inner class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.item_icon)
        val title: TextView = v.findViewById(R.id.item_title)
        val valueText: TextView = v.findViewById(R.id.item_value_text)
        val switch: Switch = v.findViewById(R.id.item_switch)
        val actionIcon: ImageView = v.findViewById(R.id.item_action_icon)
        val slider: SeekBar = v.findViewById(R.id.item_seekbar)
        val dpadGrid: GridLayout = v.findViewById(R.id.item_dpad_grid)
        val helpText: TextView = v.findViewById(R.id.item_help_text)

        // D-Pad Buttons
        val btnUp: Button = v.findViewById(R.id.btn_up)
        val btnDown: Button = v.findViewById(R.id.btn_down)
        val btnLeft: Button = v.findViewById(R.id.btn_left)
        val btnRight: Button = v.findViewById(R.id.btn_right)
        val btnCenter: Button = v.findViewById(R.id.btn_center)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trackpad_menu, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        
        // Reset Visibility
        holder.valueText.visibility = View.GONE
        holder.switch.visibility = View.GONE
        holder.actionIcon.visibility = View.GONE
        holder.slider.visibility = View.GONE
        holder.dpadGrid.visibility = View.GONE
        holder.helpText.visibility = View.GONE
        
        holder.title.text = item.title
        holder.icon.setImageResource(item.iconRes)

        when (item.type) {
            Type.ACTION -> {
                holder.actionIcon.visibility = View.VISIBLE
                holder.itemView.setOnClickListener { item.action?.invoke(true) }
            }
            Type.TOGGLE -> {
                holder.switch.visibility = View.VISIBLE
                holder.switch.isChecked = item.initValue == 1
                holder.switch.setOnCheckedChangeListener { _, isChecked ->
                    item.action?.invoke(isChecked)
                }
                holder.itemView.setOnClickListener { holder.switch.toggle() }
            }
            Type.SLIDER -> {
                holder.valueText.visibility = View.VISIBLE
                holder.slider.visibility = View.VISIBLE
                
                // Scale 0-100 to display logic handled by callback, usually 
                holder.valueText.text = "${item.initValue}"
                holder.slider.progress = item.initValue
                
                holder.slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, v: Int, fromUser: Boolean) {
                        if (fromUser) {
                            holder.valueText.text = "$v"
                            item.action?.invoke(v)
                        }
                    }
                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) {}
                })
            }
            Type.DPAD -> {
                holder.dpadGrid.visibility = View.VISIBLE
                // Map strings: "UP", "DOWN", "LEFT", "RIGHT", "CENTER"
                holder.btnUp.setOnClickListener { item.action?.invoke("UP") }
                holder.btnDown.setOnClickListener { item.action?.invoke("DOWN") }
                holder.btnLeft.setOnClickListener { item.action?.invoke("LEFT") }
                holder.btnRight.setOnClickListener { item.action?.invoke("RIGHT") }
                holder.btnCenter.setOnClickListener { item.action?.invoke("CENTER") }
            }
            Type.INFO -> {
                holder.helpText.visibility = View.VISIBLE
                holder.helpText.text = item.title // Title used as help text content
                holder.title.visibility = View.GONE // Hide standard title
                holder.icon.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
