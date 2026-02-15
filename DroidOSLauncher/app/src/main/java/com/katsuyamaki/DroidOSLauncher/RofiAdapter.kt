package com.katsuyamaki.DroidOSLauncher

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Main adapter for the rofi-style launcher drawer.
 * Handles apps, layouts, settings, profiles, keybinds, etc.
 * 
 * NOTE: This is a direct extraction. Further decomposition into
 * smaller adapters per mode (AppAdapter, SettingsAdapter, etc.) 
 * is recommended for maintainability.
 */
class RofiAdapter(
    private val actionHandler: LauncherActionHandler
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // === VIEW HOLDERS ===
    inner class AppHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val icon: ImageView = v.findViewById(R.id.rofi_app_icon)
        val text: TextView = v.findViewById(R.id.rofi_app_text)
        val star: ImageView = v.findViewById(R.id.rofi_app_star) 
    }
    
    inner class LayoutHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val nameInput: EditText = v.findViewById(R.id.layout_name)
        val btnEdit: ImageView = v.findViewById(R.id.btn_edit_layout_name)
        val btnSave: ImageView = v.findViewById(R.id.btn_save_profile)
        val btnExtinguish: ImageView = v.findViewById(R.id.btn_extinguish_item) 
    }

    inner class DpiHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val slider: android.widget.SeekBar = v.findViewById(R.id.sb_dpi_slider)
        val input: EditText = v.findViewById(R.id.input_dpi_value) 
    }

    inner class FontSizeHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val btnMinus: ImageView = v.findViewById(R.id.btn_font_minus)
        val btnPlus: ImageView = v.findViewById(R.id.btn_font_plus)
        val textVal: TextView = v.findViewById(R.id.text_font_value)
        val textLabel: TextView = v.findViewById(R.id.text_font_label)
        val textUnit: TextView = v.findViewById(R.id.text_font_unit) 
    }
    
    inner class HeightHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val btnMinus: ImageView = v.findViewById(R.id.btn_height_minus)
        val btnPlus: ImageView = v.findViewById(R.id.btn_height_plus)
        val textVal: TextView = v.findViewById(R.id.text_height_value)
        val textLabel: TextView = v.findViewById(R.id.text_height_label)
        val textUnit: TextView = v.findViewById(R.id.text_height_unit) 
    }
    
    inner class WidthHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val btnMinus: ImageView = v.findViewById(R.id.btn_width_minus)
        val btnPlus: ImageView = v.findViewById(R.id.btn_width_plus)
        val textVal: TextView = v.findViewById(R.id.text_width_value)
        val textLabel: TextView = v.findViewById(R.id.text_width_label)
        val textUnit: TextView = v.findViewById(R.id.text_width_unit) 
    }
    
    inner class ProfileRichHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val name: EditText = v.findViewById(R.id.profile_name_text)
        val details: TextView = v.findViewById(R.id.profile_details_text)
        val iconsContainer: LinearLayout = v.findViewById(R.id.profile_icons_container)
        val btnSave: ImageView = v.findViewById(R.id.btn_save_profile_rich) 
    }
    
    inner class IconSettingHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val preview: ImageView = v.findViewById(R.id.icon_setting_preview)
        val text: TextView = v.findViewById(R.id.icon_setting_text) 
    }
    
    inner class CustomResInputHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val inputW: EditText = v.findViewById(R.id.input_res_w)
        val inputH: EditText = v.findViewById(R.id.input_res_h)
        val btnSave: ImageView = v.findViewById(R.id.btn_save_res) 
    }
    
    inner class KeybindHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val title: TextView = v.findViewById(R.id.kb_title)
        val desc: TextView = v.findViewById(R.id.kb_desc)
        val btnMod: android.widget.Button = v.findViewById(R.id.btn_mod)
        val btnKey: android.widget.Button = v.findViewById(R.id.btn_key) 
    }
    
    inner class CustomModHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val input: EditText = v.findViewById(R.id.input_custom_mod) 
    }
    
    inner class MarginHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val label: TextView = v.findViewById(R.id.text_margin_label)
        val slider: android.widget.SeekBar = v.findViewById(R.id.sb_margin_slider)
        val text: TextView = v.findViewById(R.id.text_margin_value)
    }
    
    inner class HeaderHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val nameInput: EditText = v.findViewById(R.id.layout_name)
        val btnEdit: View = v.findViewById(R.id.btn_edit_layout_name)
        val btnSave: View = v.findViewById(R.id.btn_save_profile)
        val btnExtinguish: View = v.findViewById(R.id.btn_extinguish_item) 
    }
    
    inner class ActionHolder(v: View) : RecyclerView.ViewHolder(v) { 
        val nameInput: EditText = v.findViewById(R.id.layout_name)
        val btnSave: View = v.findViewById(R.id.btn_save_profile)
        val btnExtinguish: View = v.findViewById(R.id.btn_extinguish_item) 
    }

    // === VIEW TYPE MAPPING ===
    override fun getItemViewType(position: Int): Int {
        val displayList = actionHandler.getDisplayList()
        return when (displayList[position]) {
            is MainActivity.AppInfo -> 0
            is FloatingLauncherService.LayoutOption -> 1
            is FloatingLauncherService.ResolutionOption -> 1
            is FloatingLauncherService.DpiOption -> 2
            is FloatingLauncherService.ProfileOption -> 4
            is FloatingLauncherService.FontSizeOption -> 3
            is FloatingLauncherService.IconOption -> 5
            is FloatingLauncherService.ToggleOption -> 1
            is FloatingLauncherService.ActionOption -> 6
            is FloatingLauncherService.HeightOption -> 7
            is FloatingLauncherService.BubbleSizeOption -> 7
            is FloatingLauncherService.WidthOption -> 8
            is FloatingLauncherService.CustomResInputOption -> 9
            is FloatingLauncherService.RefreshHeaderOption -> 10
            is FloatingLauncherService.LegendOption -> 10
            is FloatingLauncherService.RefreshItemOption -> 11
            is FloatingLauncherService.KeybindOption -> 12
            is FloatingLauncherService.CustomModConfigOption -> 13
            is FloatingLauncherService.MarginOption -> 14
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> AppHolder(inflater.inflate(R.layout.item_app_rofi, parent, false))
            1 -> LayoutHolder(inflater.inflate(R.layout.item_layout_option, parent, false))
            2 -> DpiHolder(inflater.inflate(R.layout.item_dpi_custom, parent, false))
            3 -> FontSizeHolder(inflater.inflate(R.layout.item_font_size, parent, false))
            4 -> ProfileRichHolder(inflater.inflate(R.layout.item_profile_rich, parent, false))
            5 -> IconSettingHolder(inflater.inflate(R.layout.item_icon_setting, parent, false))
            6 -> LayoutHolder(inflater.inflate(R.layout.item_layout_option, parent, false))
            7 -> HeightHolder(inflater.inflate(R.layout.item_height_setting, parent, false))
            8 -> WidthHolder(inflater.inflate(R.layout.item_width_setting, parent, false))
            9 -> CustomResInputHolder(inflater.inflate(R.layout.item_custom_resolution, parent, false))
            10 -> HeaderHolder(inflater.inflate(R.layout.item_layout_option, parent, false))
            11 -> ActionHolder(inflater.inflate(R.layout.item_layout_option, parent, false))
            12 -> KeybindHolder(inflater.inflate(R.layout.item_keybind, parent, false))
            13 -> CustomModHolder(inflater.inflate(R.layout.item_custom_mod, parent, false))
            14 -> MarginHolder(inflater.inflate(R.layout.item_margin_setting, parent, false))
            else -> AppHolder(View(parent.context))
        }
    }

    // === BINDING LOGIC ===
    // NOTE: The full onBindViewHolder is ~700 lines in the original.
    // This stub shows the structure. Full implementation requires copying
    // the complete binding logic from lines 7989-8685.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val displayList = actionHandler.getDisplayList()
        val item = displayList[position]
        val currentFontSize = actionHandler.getCurrentFontSize()
        val selectedListIndex = actionHandler.getSelectedListIndex()
        val isKeyboardSelected = (position == selectedListIndex)
        val bgRes = if (isKeyboardSelected) R.drawable.bg_item_active else R.drawable.bg_item_press
        
        // Apply generic background
        if (holder !is ProfileRichHolder && holder !is ActionHolder && holder !is LayoutHolder) {
            holder.itemView.setBackgroundResource(bgRes)
        }
        
        when {
            holder is AppHolder && item is MainActivity.AppInfo -> bindAppHolder(holder, item, isKeyboardSelected)
            holder is ProfileRichHolder && item is FloatingLauncherService.ProfileOption -> bindProfileHolder(holder, item, isKeyboardSelected)
            holder is LayoutHolder -> bindLayoutHolder(holder, item, isKeyboardSelected, position)
            holder is HeaderHolder && item is FloatingLauncherService.RefreshHeaderOption -> bindRefreshHeader(holder, item)
            holder is HeaderHolder && item is FloatingLauncherService.LegendOption -> bindLegendHeader(holder, item)
            holder is ActionHolder && item is FloatingLauncherService.RefreshItemOption -> bindRefreshItem(holder, item)
            holder is CustomResInputHolder -> bindCustomResInput(holder)
            holder is IconSettingHolder && item is FloatingLauncherService.IconOption -> bindIconSetting(holder, item)
            holder is DpiHolder && item is FloatingLauncherService.DpiOption -> bindDpiHolder(holder, item)
            holder is FontSizeHolder && item is FloatingLauncherService.FontSizeOption -> bindFontSizeHolder(holder, item)
            holder is HeightHolder && item is FloatingLauncherService.BubbleSizeOption -> bindBubbleSizeHolder(holder, item)
            holder is HeightHolder && item is FloatingLauncherService.HeightOption -> bindHeightHolder(holder, item)
            holder is WidthHolder && item is FloatingLauncherService.WidthOption -> bindWidthHolder(holder, item)
            holder is MarginHolder && item is FloatingLauncherService.MarginOption -> bindMarginHolder(holder, item, position)
            holder is KeybindHolder && item is FloatingLauncherService.KeybindOption -> bindKeybindHolder(holder, item, position)
            holder is CustomModHolder && item is FloatingLauncherService.CustomModConfigOption -> bindCustomModHolder(holder, item)
        }
    }

    override fun getItemCount() = actionHandler.getDisplayList().size

    // === BINDING HELPER METHODS ===
    // These would contain the extracted logic from each binding case
    
    private fun bindAppHolder(holder: AppHolder, item: MainActivity.AppInfo, isKeyboardSelected: Boolean) {
        val currentFontSize = actionHandler.getCurrentFontSize()
        val queue = actionHandler.getSelectedAppsQueue()
        
        holder.text.setScaledTextSize(currentFontSize, 1.0f)
        holder.text.text = item.label
        
        if (item.packageName == LauncherActionHandler.PACKAGE_BLANK) {
            holder.icon.setImageResource(R.drawable.ic_box_outline)
        } else {
            try {
                val basePkg = if (item.packageName.contains(":")) item.packageName.substringBefore(":") else item.packageName
                holder.icon.setImageDrawable(actionHandler.getPackageManagerRef().getApplicationIcon(basePkg))
            } catch (e: Exception) {
                holder.icon.setImageResource(R.drawable.ic_launcher_bubble)
            }
        }
        
        val isSelectedInQueue = queue.any { it.packageName == item.packageName }
        if (isSelectedInQueue || isKeyboardSelected) {
            holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
        }
        
        holder.star.visibility = if (item.isFavorite) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener { actionHandler.addToSelection(item) }
        holder.itemView.setOnLongClickListener { 
            actionHandler.toggleFavorite(item)
            actionHandler.refreshSearchList()
            true 
        }
    }
    
    private fun bindProfileHolder(holder: ProfileRichHolder, item: FloatingLauncherService.ProfileOption, isKeyboardSelected: Boolean) {
        // TODO: Copy full implementation from lines 8036-8166
        holder.name.setText(item.name)
        holder.iconsContainer.removeAllViews()
        // ... rest of profile binding logic
    }
    
    private fun bindLayoutHolder(holder: LayoutHolder, item: Any, isKeyboardSelected: Boolean, position: Int) {
        // TODO: Copy full implementation from lines 8168-8296
        // Handles LayoutOption, ResolutionOption, IconOption, ToggleOption, ActionOption
    }
    
    private fun bindRefreshHeader(holder: HeaderHolder, item: FloatingLauncherService.RefreshHeaderOption) {
        val currentFontSize = actionHandler.getCurrentFontSize()
        holder.nameInput.setText(item.text)
        holder.nameInput.isEnabled = false
        holder.nameInput.setTextColor(Color.GREEN)
        holder.nameInput.setScaledTextSize(currentFontSize, 1.0f)
        holder.nameInput.gravity = Gravity.CENTER
        holder.itemView.setBackgroundResource(0)
        holder.btnEdit.visibility = View.GONE
        holder.btnSave.visibility = View.GONE
        holder.btnExtinguish.visibility = View.GONE
    }
    
    private fun bindLegendHeader(holder: HeaderHolder, item: FloatingLauncherService.LegendOption) {
        // TODO: Copy implementation from lines 8308-8353
    }
    
    private fun bindRefreshItem(holder: ActionHolder, item: FloatingLauncherService.RefreshItemOption) {
        // TODO: Copy implementation from lines 8360-8396
    }
    
    private fun bindCustomResInput(holder: CustomResInputHolder) {
        // TODO: Copy implementation from lines 8400-8405
    }
    
    private fun bindIconSetting(holder: IconSettingHolder, item: FloatingLauncherService.IconOption) {
        // TODO: Copy implementation from lines 8406-8418
    }
    
    private fun bindDpiHolder(holder: DpiHolder, item: FloatingLauncherService.DpiOption) {
        // TODO: Copy implementation from lines 8421-8480
    }
    
    private fun bindFontSizeHolder(holder: FontSizeHolder, item: FloatingLauncherService.FontSizeOption) {
        val currentFontSize = actionHandler.getCurrentFontSize()
        holder.textLabel.setScaledTextSize(currentFontSize, 1.0f)
        holder.textVal.text = item.currentSize.toInt().toString()
        holder.textVal.setScaledTextSize(currentFontSize, 1.125f)
        holder.textUnit.setScaledTextSize(currentFontSize, 0.75f)
        holder.btnMinus.setOnClickListener { actionHandler.changeFontSize(item.currentSize - 1) }
        holder.btnPlus.setOnClickListener { actionHandler.changeFontSize(item.currentSize + 1) }
    }
    
    private fun bindBubbleSizeHolder(holder: HeightHolder, item: FloatingLauncherService.BubbleSizeOption) {
        val currentFontSize = actionHandler.getCurrentFontSize()
        holder.textLabel.text = "Bubble Size:"
        holder.textLabel.setScaledTextSize(currentFontSize, 1.0f)
        holder.textVal.text = item.currentPercent.toString()
        holder.textVal.setScaledTextSize(currentFontSize, 1.125f)
        holder.textUnit.text = "%"
        holder.textUnit.setScaledTextSize(currentFontSize, 0.75f)
        holder.btnMinus.setOnClickListener { actionHandler.changeBubbleSize(-10) }
        holder.btnPlus.setOnClickListener { actionHandler.changeBubbleSize(10) }
    }
    
    private fun bindHeightHolder(holder: HeightHolder, item: FloatingLauncherService.HeightOption) {
        // TODO: Copy implementation from lines 8500-8506
    }
    
    private fun bindWidthHolder(holder: WidthHolder, item: FloatingLauncherService.WidthOption) {
        // TODO: Copy implementation from lines 8508-8514
    }
    
    private fun bindMarginHolder(holder: MarginHolder, item: FloatingLauncherService.MarginOption, position: Int) {
        // TODO: Copy implementation from lines 8516-8569
    }
    
    private fun bindKeybindHolder(holder: KeybindHolder, item: FloatingLauncherService.KeybindOption, position: Int) {
        // TODO: Copy implementation from lines 8570-8684
    }
    
    private fun bindCustomModHolder(holder: CustomModHolder, item: FloatingLauncherService.CustomModConfigOption) {
        // TODO: Implementation for custom mod key configuration
    }
    
    // === HELPER METHODS ===
    private fun startRename(editText: EditText) {
        editText.isEnabled = true
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val context = actionHandler.getContext()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
    
    private fun endRename(editText: EditText) {
        editText.isFocusable = false
        editText.isFocusableInTouchMode = false
        editText.isEnabled = false
        val context = actionHandler.getContext()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
    
    private fun TextView.setScaledTextSize(baseFontSize: Float, scaleFactor: Float) {
        this.textSize = baseFontSize * scaleFactor
    }
}