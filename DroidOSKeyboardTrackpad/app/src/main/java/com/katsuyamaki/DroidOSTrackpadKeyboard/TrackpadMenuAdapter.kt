package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrackpadMenuAdapter(private val items: List<MenuItem>) : 
    RecyclerView.Adapter<TrackpadMenuAdapter.Holder>() {

    private val dpadRepeatHandler = Handler(Looper.getMainLooper())
    private val activeDpadRepeats = mutableMapOf<View, Runnable>()

    private val dpadInitialRepeatDelayMs = 260L
    private val dpadRepeatIntervalMs = 70L

    private fun stopDpadRepeat(button: View) {
        activeDpadRepeats.remove(button)?.let { dpadRepeatHandler.removeCallbacks(it) }
    }

    private fun bindDpadRepeat(button: Button, command: String, action: ((Any) -> Unit)?) {
        stopDpadRepeat(button)
        button.setOnClickListener(null)
        button.setOnLongClickListener { true }
        button.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    action?.invoke(command)
                    val repeater = object : Runnable {
                        override fun run() {
                            action?.invoke(command)
                            dpadRepeatHandler.postDelayed(this, dpadRepeatIntervalMs)
                        }
                    }
                    activeDpadRepeats[button] = repeater
                    dpadRepeatHandler.postDelayed(repeater, dpadInitialRepeatDelayMs)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    stopDpadRepeat(button)
                    v.performClick()
                    true
                }
                else -> true
            }
        }
    }

    data class MenuItem(
        val title: CharSequence,

        val iconRes: Int,
        val type: Type,
        val initValue: Int = 0, // For sliders (0-100) or Toggles (0/1)
        val max: Int = 100,     // NEW: Custom max value for sliders
        val action: ((Any) -> Unit)? = null // Callback
    )

    enum class Type {
        ACTION, // Simple Click
        TOGGLE, // Switch
        SLIDER, // SeekBar
        DPAD,   // Directional Pad
        INFO,   // Text only
        HEADER, // Main Section Header (Bold, White)
        SUBHEADER // Sub-section Header (Small, Grey)
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
        
        // Reset Visibility & Style
        (holder.itemView as android.view.ViewGroup).getChildAt(0).visibility = View.VISIBLE
        holder.valueText.visibility = View.GONE
        holder.switch.visibility = View.GONE
        holder.actionIcon.visibility = View.GONE
        holder.slider.visibility = View.GONE
        holder.dpadGrid.visibility = View.GONE
        holder.helpText.visibility = View.GONE
        holder.title.visibility = View.VISIBLE
        holder.icon.visibility = View.VISIBLE
        
        holder.title.text = item.title
        holder.icon.setImageResource(item.iconRes)
        
        // Default text style (Reset)
        holder.title.setTypeface(null, android.graphics.Typeface.NORMAL)
        holder.title.setTextColor(Color.WHITE)
        holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        
        // Reset Item Background/Padding
        holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
        holder.itemView.setPadding(0,0,0,0) // Reset padding
        
        // Reset click listener
        holder.itemView.setOnClickListener(null)

        // Reset D-pad listeners/state because RecyclerView reuses holders across item types.
        stopDpadRepeat(holder.btnUp)
        stopDpadRepeat(holder.btnDown)
        stopDpadRepeat(holder.btnLeft)
        stopDpadRepeat(holder.btnRight)
        holder.btnUp.setOnTouchListener(null)
        holder.btnDown.setOnTouchListener(null)
        holder.btnLeft.setOnTouchListener(null)
        holder.btnRight.setOnTouchListener(null)
        holder.btnCenter.setOnTouchListener(null)
        holder.btnUp.setOnClickListener(null)
        holder.btnDown.setOnClickListener(null)
        holder.btnLeft.setOnClickListener(null)
        holder.btnRight.setOnClickListener(null)
        holder.btnCenter.setOnClickListener(null)
        holder.btnUp.setOnLongClickListener(null)
        holder.btnDown.setOnLongClickListener(null)
        holder.btnLeft.setOnLongClickListener(null)
        holder.btnRight.setOnLongClickListener(null)
        holder.btnCenter.setOnLongClickListener(null)

        when (item.type) {
            Type.ACTION -> {
                holder.actionIcon.visibility = View.GONE
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
                holder.slider.max = item.max
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
                bindDpadRepeat(holder.btnUp, "UP", item.action)
                bindDpadRepeat(holder.btnDown, "DOWN", item.action)
                bindDpadRepeat(holder.btnLeft, "LEFT", item.action)
                bindDpadRepeat(holder.btnRight, "RIGHT", item.action)
                holder.btnCenter.setOnTouchListener(null)
                holder.btnCenter.setOnLongClickListener(null)
                holder.btnCenter.setOnClickListener { item.action?.invoke("CENTER") }
            }
            Type.INFO -> {
                (holder.itemView as android.view.ViewGroup).getChildAt(0).visibility = View.GONE
                holder.helpText.visibility = View.VISIBLE
                holder.helpText.text = item.title 
                holder.title.visibility = View.GONE 
                holder.icon.visibility = View.GONE
                holder.itemView.setBackgroundResource(android.R.color.transparent) // No press effect for INFO
            }
            Type.HEADER -> {
                holder.title.visibility = View.VISIBLE
                holder.title.text = item.title
                holder.title.setTypeface(null, android.graphics.Typeface.BOLD)
                holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Larger
                holder.icon.visibility = View.GONE
                holder.itemView.setBackgroundResource(android.R.color.transparent) // No press effect
            }
            Type.SUBHEADER -> {
                holder.title.visibility = View.VISIBLE
                holder.title.text = item.title
                holder.title.setTypeface(null, android.graphics.Typeface.NORMAL)
                holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f) // Small
                holder.title.setTextColor(Color.parseColor("#AAAAAA")) // Grey
                holder.icon.visibility = View.GONE
                holder.itemView.setBackgroundResource(android.R.color.transparent) // No press effect
                // Optional: Add top margin if possible via params, or just rely on list order
            }
        }
    }

    override fun onViewRecycled(holder: Holder) {
        super.onViewRecycled(holder)
        stopDpadRepeat(holder.btnUp)
        stopDpadRepeat(holder.btnDown)
        stopDpadRepeat(holder.btnLeft)
        stopDpadRepeat(holder.btnRight)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        dpadRepeatHandler.removeCallbacksAndMessages(null)
        activeDpadRepeats.clear()
    }

    override fun getItemCount(): Int = items.size
}
