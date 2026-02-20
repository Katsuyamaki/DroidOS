package com.katsuyamaki.DroidOSLauncher

import com.katsuyamaki.DroidOSLauncher.FocusAreas
import com.katsuyamaki.DroidOSLauncher.LauncherModes
import com.katsuyamaki.DroidOSLauncher.LayoutTypes

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
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

class RofiAdapter(
    private val handler: LauncherActionHandler
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
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

    override fun getItemViewType(position: Int): Int { 
        return when (handler.displayList[position]) { 
            is MainActivity.AppInfo -> 0
            is LayoutOption -> 1
            is ResolutionOption -> 1
            is DpiOption -> 2
            is ProfileOption -> 4
            is FontSizeOption -> 3
            is IconOption -> 5
            is ToggleOption -> 1
            is ActionOption -> 6
            is HeightOption -> 7
            is BubbleSizeOption -> 7
            is WidthOption -> 8
            is CustomResInputOption -> 9
            is RefreshHeaderOption -> 10
            is LegendOption -> 10
            is RefreshItemOption -> 11
            is KeybindOption -> 12
            is CustomModConfigOption -> 13
            is MarginOption -> 14
            else -> 0 
        } 
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder { 
        return when (viewType) { 
            0 -> AppHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_app_rofi, parent, false))
            1 -> LayoutHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false))
            2 -> DpiHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dpi_custom, parent, false))
            3 -> FontSizeHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_font_size, parent, false))
            4 -> ProfileRichHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_rich, parent, false))
            5 -> IconSettingHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_icon_setting, parent, false))
            6 -> LayoutHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false))
            7 -> HeightHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_height_setting, parent, false))
            8 -> WidthHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_width_setting, parent, false))
            9 -> CustomResInputHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_custom_resolution, parent, false))
            10 -> HeaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false))
            11 -> ActionHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout_option, parent, false))
            12 -> KeybindHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_keybind, parent, false))
            13 -> CustomModHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_custom_mod, parent, false))
            14 -> MarginHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_margin_setting, parent, false))
            else -> AppHolder(View(parent.context)) 
        } 
    }
    
    private fun startRename(editText: EditText) { 
        editText.isEnabled = true
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val imm = handler.handlerContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT) 
    }
    
    private fun endRename(editText: EditText) { 
        editText.isFocusable = false
        editText.isFocusableInTouchMode = false
        editText.isEnabled = false
        val imm = handler.handlerContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0) 
    }

    private fun isDoneAction(actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) return true
        return event?.action == KeyEvent.ACTION_DOWN &&
            (event.keyCode == KeyEvent.KEYCODE_ENTER || event.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = handler.displayList[position]
        if (holder is AppHolder) holder.text.setScaledTextSize(handler.currentFontSize, 1.0f)
        if (holder is LayoutHolder) holder.nameInput.setScaledTextSize(handler.currentFontSize, 1.0f)
        if (holder is ProfileRichHolder) holder.name.setScaledTextSize(handler.currentFontSize, 1.0f)

        val isKeyboardSelected = (position == handler.selectedListIndex)
        val bgRes = if (isKeyboardSelected) R.drawable.bg_item_active else R.drawable.bg_item_press
        
        if (holder !is ProfileRichHolder && holder !is ActionHolder && holder !is LayoutHolder) {
             holder.itemView.setBackgroundResource(bgRes)
        }

        // APP HOLDER BINDING
        if (holder is AppHolder && item is MainActivity.AppInfo) {
            holder.text.text = item.label
            if (item.packageName == PACKAGE_BLANK) {
                holder.icon.setImageResource(R.drawable.ic_box_outline)
            } else {
                try {
                    val basePkg = if (item.packageName.contains(":")) item.packageName.substringBefore(":") else item.packageName
                    holder.icon.setImageDrawable(handler.handlerPackageManager.getApplicationIcon(basePkg))
                } catch (e: Exception) {
                    holder.icon.setImageResource(R.drawable.ic_launcher_bubble)
                }
            }
            val isSelectedInQueue = handler.selectedAppsQueue.any { it.packageName == item.packageName }
            
            if (isSelectedInQueue || isKeyboardSelected) {
                 holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
            } else {
                 holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
            }
            
            holder.star.visibility = if (item.isFavorite) View.VISIBLE else View.GONE
            holder.itemView.setOnClickListener { handler.addToSelection(item) }
            holder.itemView.setOnLongClickListener { handler.toggleFavorite(item); handler.refreshSearchList(); true }
        }
        // PROFILE HOLDER BINDING
        else if (holder is ProfileRichHolder && item is ProfileOption) { 
            bindProfileHolder(holder, item, position, isKeyboardSelected)
        }
        // LAYOUT HOLDER BINDING
        else if (holder is LayoutHolder) {
            bindLayoutHolder(holder, item, position, isKeyboardSelected)
        }
        // HEADER HOLDER BINDING
        else if (holder is HeaderHolder && item is RefreshHeaderOption) {
            bindRefreshHeader(holder, item)
        }
        else if (holder is HeaderHolder && item is LegendOption) {
            bindLegendOption(holder, item)
        }
        // REFRESH ITEM OPTION BINDING
        else if (holder is ActionHolder && item is RefreshItemOption) {
            bindRefreshItem(holder, item, isKeyboardSelected)
        }
        // CUSTOM RESOLUTION INPUT BINDING
        else if (holder is CustomResInputHolder) {
            bindCustomResInput(holder)
        }
        // ICON SETTING BINDING
        else if (holder is IconSettingHolder && item is IconOption) { 
            bindIconSetting(holder)
        }
        // DPI HOLDER BINDING
        else if (holder is DpiHolder && item is DpiOption) { 
            bindDpiHolder(holder, item)
        }
        // FONT SIZE BINDING
        else if (holder is FontSizeHolder && item is FontSizeOption) { 
            bindFontSizeHolder(holder, item)
        }
        // BUBBLE SIZE BINDING
        else if (holder is HeightHolder && item is BubbleSizeOption) {
            bindBubbleSizeHolder(holder, item)
        }
        // HEIGHT BINDING
        else if (holder is HeightHolder && item is HeightOption) { 
            bindHeightHolder(holder)
        }
        // WIDTH BINDING
        else if (holder is WidthHolder && item is WidthOption) { 
            bindWidthHolder(holder)
        }
        // MARGIN BINDING
        else if (holder is MarginHolder && item is MarginOption) {
            bindMarginHolder(holder, item, position)
        }
        // CUSTOM MOD BINDING
        else if (holder is CustomModHolder && item is CustomModConfigOption) {
            bindCustomModHolder(holder, item)
        }
        // KEYBIND BINDING
        else if (holder is KeybindHolder && item is KeybindOption) {
            bindKeybindHolder(holder, item, position)
        }
    }
    
    override fun getItemCount() = handler.displayList.size
    
    // ========== BINDING HELPERS ==========
    
    private fun bindProfileHolder(holder: ProfileRichHolder, item: ProfileOption, position: Int, isKeyboardSelected: Boolean) {
        holder.name.setText(item.name)
        holder.iconsContainer.removeAllViews()
        
        if (!item.isCurrent) { 
            if (item.apps.isNotEmpty()) {
                for (pkg in item.apps.take(5)) { 
                    val iv = ImageView(holder.itemView.context)
                    val lp = LinearLayout.LayoutParams(60, 60)
                    lp.marginEnd = 8
                    iv.layoutParams = lp
                    if (pkg == PACKAGE_BLANK) { 
                        iv.setImageResource(R.drawable.ic_box_outline) 
                    } else { 
                        try { 
                            iv.setImageDrawable(handler.handlerPackageManager.getApplicationIcon(pkg)) 
                        } catch (e: Exception) { 
                            iv.setImageResource(R.drawable.ic_launcher_bubble) 
                        } 
                    }
                    holder.iconsContainer.addView(iv) 
                }
            }
            
            val typeLabel = when (item.profileType) {
                1 -> "[Layout]"
                2 -> "[Queue]"
                else -> "[Full]"
            }
            val marginInfo = if (item.profileType != 2 && (item.topMargin != 0 || item.bottomMargin != 0 || item.autoAdjustMargin)) {
                " | M:${item.topMargin}/${item.bottomMargin}${if (item.autoAdjustMargin) "*" else ""}"
            } else ""
            val info = when (item.profileType) {
                1 -> "$typeLabel ${handler.getLayoutName(item.layout)} | ${handler.getRatioName(item.resIndex)} | ${item.dpi}dpi$marginInfo"
                2 -> "$typeLabel ${item.apps.size} apps"
                else -> "$typeLabel ${handler.getLayoutName(item.layout)} | ${item.apps.size} apps$marginInfo"
            }
            holder.details.text = info
            holder.details.visibility = View.VISIBLE
            holder.btnSave.visibility = View.GONE
            
            if (handler.activeProfileName == item.name || isKeyboardSelected) {
                holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
            }
            
            holder.name.apply {
                isEnabled = true
                setTextColor(Color.WHITE)
                background = null
                isFocusable = false
                isFocusableInTouchMode = false
                isClickable = true
                isLongClickable = false
                inputType = 0
            }
            
            val clickAction = View.OnClickListener {
                if (handler.isProfileNameEditMode) {
                    holder.name.isFocusable = true
                    holder.name.isFocusableInTouchMode = true
                    holder.name.isClickable = true
                    holder.name.inputType = android.text.InputType.TYPE_CLASS_TEXT
                    startRename(holder.name)
                } else {
                    handler.dismissKeyboardAndRestore()
                    handler.loadProfile(item.name)
                }
            }
            holder.itemView.setOnClickListener(clickAction)
            holder.name.setOnClickListener(clickAction)
            holder.itemView.setOnLongClickListener(null)

            var isSaving = false
            val saveProfileName = {
                if (!isSaving) {
                    isSaving = true
                    val newName = holder.name.text.toString().trim()
                    var changed = false
                    if (newName.isNotEmpty() && newName != item.name) {
                        if (AppPreferences.renameProfile(holder.itemView.context, item.name, newName)) {
                            handler.safeToast("Renamed to $newName")
                            handler.switchMode(LauncherModes.MODE_PROFILES)
                            changed = true
                        }
                    }
                    if (!changed) {
                        endRename(holder.name)
                        holder.name.isEnabled = true
                        holder.name.isFocusable = false
                        holder.name.isClickable = true
                        holder.name.inputType = 0
                        isSaving = false
                    }
                }
            }

            holder.name.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val imm = handler.handlerContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(holder.name.windowToken, 0)
                    holder.name.clearFocus()
                    handler.updateDrawerHeight(false)
                    saveProfileName()
                    true
                } else false
            }

            holder.name.setOnFocusChangeListener { v, hasFocus ->
                if (handler.autoResizeEnabled) handler.updateDrawerHeight(hasFocus)
                if (!hasFocus && !isSaving) saveProfileName()
            } 
        } else { 
            holder.iconsContainer.removeAllViews()
            holder.details.visibility = View.GONE
            holder.btnSave.visibility = View.VISIBLE
            holder.itemView.setBackgroundResource(if (isKeyboardSelected) R.drawable.bg_item_active else R.drawable.bg_item_press)
            holder.name.isEnabled = true
            holder.name.isFocusable = true
            holder.name.isFocusableInTouchMode = true
            holder.itemView.setOnClickListener { handler.saveProfile() }
            holder.btnSave.setOnClickListener { handler.saveProfile() } 
        }
    }
    
    private fun bindLayoutHolder(holder: LayoutHolder, item: Any, position: Int, isKeyboardSelected: Boolean) {
        if (isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
        else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
        
        holder.btnEdit.visibility = View.GONE
        holder.btnSave.visibility = View.GONE
        holder.btnExtinguish.visibility = View.GONE

        holder.nameInput.onFocusChangeListener = null
        holder.nameInput.setOnEditorActionListener(null)
        holder.itemView.setOnLongClickListener(null)
        holder.itemView.setOnClickListener(null)
        holder.nameInput.setOnClickListener(null)

        holder.nameInput.isClickable = false
        holder.nameInput.isFocusable = false
        holder.nameInput.isFocusableInTouchMode = false
        holder.nameInput.background = null
        
        when (item) {
            is LayoutOption -> bindLayoutOption(holder, item, isKeyboardSelected)
            is ResolutionOption -> bindResolutionOption(holder, item, isKeyboardSelected)
            is IconOption -> bindIconOption(holder, item, isKeyboardSelected)
            is ToggleOption -> bindToggleOption(holder, item, position, isKeyboardSelected)
            is ActionOption -> bindActionOption(holder, item, isKeyboardSelected)
        }
    }
    
    private fun bindLayoutOption(holder: LayoutHolder, item: LayoutOption, isKeyboardSelected: Boolean) {
        holder.nameInput.setText(item.name)
        val isSelected = if (item.type == LayoutTypes.LAYOUT_CUSTOM_DYNAMIC) { 
            item.type == handler.selectedLayoutType && item.name == handler.activeCustomLayoutName 
        } else { 
            item.type == handler.selectedLayoutType && handler.activeCustomLayoutName == null 
        }
        
        if (isSelected || isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) 
        else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
        
        holder.nameInput.apply {
            isEnabled = true
            setTextColor(Color.WHITE)
            background = null
            isFocusable = false
            isFocusableInTouchMode = false
            isClickable = true
            isLongClickable = false
            inputType = 0
        }
        
        holder.btnEdit.visibility = View.GONE
        
        val clickAction = View.OnClickListener {
            if (handler.isLayoutNameEditMode) {
                holder.nameInput.isFocusable = true
                holder.nameInput.isFocusableInTouchMode = true
                holder.nameInput.isClickable = true
                holder.nameInput.inputType = android.text.InputType.TYPE_CLASS_TEXT
                startRename(holder.nameInput)
            } else {
                handler.selectLayout(item)
            }
        }
        holder.itemView.setOnClickListener(clickAction)
        holder.nameInput.setOnClickListener(clickAction)

        var isSaving = false
        val saveAction = {
            if (!isSaving) {
                isSaving = true
                val newName = holder.nameInput.text.toString().trim()
                var changed = false

                if (newName.isNotEmpty() && newName != item.name) {
                    if (item.isCustomSaved) {
                        if (AppPreferences.renameCustomLayout(holder.itemView.context, item.name, newName)) {
                            handler.safeToast("Renamed to $newName")
                            if (handler.activeCustomLayoutName == item.name) {
                                handler.activeCustomLayoutName = newName
                                AppPreferences.saveLastCustomLayoutName(holder.itemView.context, newName, handler.currentDisplayId)
                                AppPreferences.saveLastCustomLayoutName(holder.itemView.context, newName, handler.currentDisplayId, handler.orientSuffix())
                            }
                            handler.switchMode(LauncherModes.MODE_LAYOUTS)
                            changed = true
                        }
                    } else {
                        AppPreferences.saveDefaultLayoutName(holder.itemView.context, item.type, newName)
                        handler.safeToast("Renamed to $newName")
                        handler.switchMode(LauncherModes.MODE_LAYOUTS)
                        changed = true
                    }
                }

                if (!changed) {
                    endRename(holder.nameInput)
                    holder.nameInput.isEnabled = true
                    holder.nameInput.isFocusable = false
                    holder.nameInput.isClickable = true
                    holder.nameInput.inputType = 0
                    isSaving = false
                }
            }
        }

        holder.nameInput.setOnEditorActionListener { _, actionId, event ->
            if (isDoneAction(actionId, event)) {
                val imm = handler.handlerContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(holder.nameInput.windowToken, 0)
                holder.nameInput.clearFocus()
                handler.updateDrawerHeight(false)
                saveAction()
                true
            } else false
        }
        holder.nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !isSaving) saveAction()
        }
    }
    
    private fun bindResolutionOption(holder: LayoutHolder, item: ResolutionOption, isKeyboardSelected: Boolean) {
        holder.nameInput.setText(item.name)
        if (item.index >= 100) { 
            holder.nameInput.isEnabled = false
            holder.nameInput.setTextColor(Color.WHITE)
            holder.itemView.setOnLongClickListener { startRename(holder.nameInput); true }
            val saveResName = { 
                val newName = holder.nameInput.text.toString().trim()
                if (newName.isNotEmpty() && newName != item.name) { 
                    if (AppPreferences.renameCustomResolution(holder.itemView.context, item.name, newName)) { 
                        handler.safeToast("Renamed to $newName")
                        handler.switchMode(LauncherModes.MODE_RESOLUTION) 
                    } 
                }
                endRename(holder.nameInput) 
            }
            holder.nameInput.setOnEditorActionListener { v, actionId, _ -> 
                if (actionId == EditorInfo.IME_ACTION_DONE) { saveResName(); true } else false 
            }
            holder.nameInput.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) saveResName() } 
        } else { 
            holder.nameInput.isEnabled = false
            holder.nameInput.isFocusable = false
            holder.nameInput.setTextColor(Color.WHITE) 
        }
        holder.nameInput.setScaledTextSize(handler.currentFontSize, 1.0f)
        val isSelected = (item.index == handler.selectedResolutionIndex)
        if (isSelected || isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) 
        else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
        holder.itemView.setOnClickListener { handler.applyResolution(item) }
    }
    
    private fun bindIconOption(holder: LayoutHolder, item: IconOption, isKeyboardSelected: Boolean) {
        holder.nameInput.setText(item.name)
        holder.nameInput.isEnabled = false
        holder.nameInput.setTextColor(Color.WHITE)
        holder.nameInput.setScaledTextSize(handler.currentFontSize, 1.0f)
        if(isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) 
        else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
        holder.itemView.setOnClickListener { handler.pickIcon() }
    }
    
    private fun bindToggleOption(holder: LayoutHolder, item: ToggleOption, position: Int, isKeyboardSelected: Boolean) {
        holder.nameInput.setText(item.name)
        holder.nameInput.isEnabled = false
        holder.nameInput.setScaledTextSize(handler.currentFontSize, 1.0f)

        if (!item.canToggle) {
            holder.nameInput.setTextColor(Color.GRAY)
            holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
            holder.itemView.alpha = 0.6f
            holder.itemView.setOnClickListener {
                handler.dismissKeyboardAndRestore()
                handler.safeToast(item.disabledNote ?: "DroidOS Toolbar Keyboard is needed for this function")
            }
            return
        }

        holder.itemView.alpha = 1.0f
        holder.nameInput.setTextColor(Color.WHITE)
        if (item.isEnabled || isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
        else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
        holder.itemView.setOnClickListener {
            handler.dismissKeyboardAndRestore()
            item.isEnabled = !item.isEnabled
            item.onToggle(item.isEnabled)
            notifyItemChanged(position)
        }
    }
    
    private fun bindActionOption(holder: LayoutHolder, item: ActionOption, isKeyboardSelected: Boolean) {
        holder.nameInput.setText(item.name)
        holder.nameInput.isEnabled = false
        holder.nameInput.setTextColor(Color.WHITE)
        holder.nameInput.setScaledTextSize(handler.currentFontSize, 1.0f)
        if(isKeyboardSelected) holder.itemView.setBackgroundResource(R.drawable.bg_item_active) 
        else holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
        holder.itemView.setOnClickListener { handler.dismissKeyboardAndRestore(); item.action() }
    }
    
    private fun bindRefreshHeader(holder: HeaderHolder, item: RefreshHeaderOption) {
        holder.nameInput.setText(item.text)
        holder.nameInput.isEnabled = false
        holder.nameInput.setTextColor(Color.GREEN)
        holder.nameInput.setScaledTextSize(handler.currentFontSize, 1.0f)
        holder.nameInput.gravity = Gravity.CENTER
        holder.itemView.setBackgroundResource(0)
        holder.btnEdit.visibility = View.GONE
        holder.btnSave.visibility = View.GONE
        holder.btnExtinguish.visibility = View.GONE
    }
    
    private fun bindLegendOption(holder: HeaderHolder, item: LegendOption) {
        holder.nameInput.setText(item.text)
        holder.nameInput.setTextColor(if (item.text.startsWith("NOTE:")) Color.RED else Color.GRAY)
        holder.nameInput.setScaledTextSize(handler.currentFontSize, 0.75f)
        holder.nameInput.gravity = Gravity.CENTER
        holder.itemView.setBackgroundResource(0)
        holder.btnEdit.visibility = View.GONE
        holder.btnSave.visibility = View.GONE
        holder.btnExtinguish.visibility = View.GONE
        
        holder.nameInput.apply {
            isEnabled = true
            background = null
            isFocusable = false
            isFocusableInTouchMode = false
            isClickable = true
            inputType = 0
        }
        
        val clickAction = View.OnClickListener {
            if (handler.isLayoutNameEditMode) {
                holder.nameInput.isFocusable = true
                holder.nameInput.isFocusableInTouchMode = true
                holder.nameInput.isClickable = true
                holder.nameInput.inputType = android.text.InputType.TYPE_CLASS_TEXT
                startRename(holder.nameInput)
            }
        }
        holder.itemView.setOnClickListener(clickAction)
        holder.nameInput.setOnClickListener(clickAction)
        
        holder.nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newText = holder.nameInput.text.toString().trim()
                if (newText.isNotEmpty() && newText != item.text) {
                    AppPreferences.saveLegendText(handler.handlerContext, newText)
                    handler.safeToast("Legend updated")
                }
                endRename(holder.nameInput)
                holder.nameInput.isFocusable = false
                holder.nameInput.inputType = 0
            }
        }
    }
    
    private fun bindRefreshItem(holder: ActionHolder, item: RefreshItemOption, isKeyboardSelected: Boolean) {
        holder.nameInput.setText(item.label)
        holder.nameInput.isEnabled = false
        holder.btnSave.visibility = View.GONE
        holder.btnExtinguish.visibility = View.GONE
        holder.nameInput.setScaledTextSize(handler.currentFontSize, 1.0f)

        if (item.isAvailable) {
            if (item.isSelected) {
                holder.itemView.setBackgroundResource(R.drawable.bg_item_active)
                holder.nameInput.setTextColor(Color.WHITE)
                holder.nameInput.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
                holder.nameInput.setTextColor(Color.WHITE)
                holder.nameInput.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
            
            holder.itemView.alpha = 1.0f
            holder.itemView.setOnClickListener {
                handler.dismissKeyboardAndRestore()
                handler.applyRefreshRate(item.targetRate)
            }
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_item_press)
            holder.nameInput.setTextColor(Color.GRAY)
            holder.nameInput.setTypeface(null, android.graphics.Typeface.ITALIC)
            holder.itemView.alpha = 0.5f
            
            holder.itemView.setOnClickListener {
                handler.dismissKeyboardAndRestore()
                handler.safeToast("${item.targetRate.toInt()}Hz not supported by this display")
            }
        }
    }
    
    private fun bindCustomResInput(holder: CustomResInputHolder) {
        holder.inputW.setScaledTextSize(handler.currentFontSize, 1.0f)
        holder.inputH.setScaledTextSize(handler.currentFontSize, 1.0f)
        holder.btnSave.setOnClickListener { 
            val wStr = holder.inputW.text.toString().trim()
            val hStr = holder.inputH.text.toString().trim()
            if (wStr.isNotEmpty() && hStr.isNotEmpty()) { 
                val w = wStr.toIntOrNull()
                val h = hStr.toIntOrNull()
                if (w != null && h != null && w > 0 && h > 0) { 
                    val gcdVal = calculateGCD(w, h)
                    val wRatio = w / gcdVal
                    val hRatio = h / gcdVal
                    val resString = "${w}x${h}"
                    val name = "$wRatio:$hRatio Custom ($resString)"
                    AppPreferences.saveCustomResolution(holder.itemView.context, name, resString)
                    handler.safeToast("Added $name")
                    handler.dismissKeyboardAndRestore()
                    handler.switchMode(LauncherModes.MODE_RESOLUTION) 
                } else { 
                    handler.safeToast("Invalid numbers") 
                } 
            } else { 
                handler.safeToast("Input W and H") 
            } 
        }
        holder.inputW.setOnFocusChangeListener { _, hasFocus -> if (handler.autoResizeEnabled) handler.updateDrawerHeight(hasFocus) }
        holder.inputH.setOnFocusChangeListener { _, hasFocus -> if (handler.autoResizeEnabled) handler.updateDrawerHeight(hasFocus) }
    }
    
    private fun bindIconSetting(holder: IconSettingHolder) {
        holder.text.setScaledTextSize(handler.currentFontSize, 1.0f)
        try { 
            val uriStr = AppPreferences.getIconUri(holder.itemView.context)
            if (uriStr != null) { 
                val uri = Uri.parse(uriStr)
                val input = handler.handlerContext.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(input)
                input?.close()
                holder.preview.setImageBitmap(bitmap) 
            } else { 
                holder.preview.setImageResource(R.drawable.ic_launcher_bubble) 
            } 
        } catch(e: Exception) { 
            holder.preview.setImageResource(R.drawable.ic_launcher_bubble) 
        }
        holder.itemView.setOnClickListener { handler.pickIcon() }
    }
    
    private fun bindDpiHolder(holder: DpiHolder, item: DpiOption) {
        val safeDpi = if (item.currentDpi > 0) item.currentDpi else 0
        holder.input.setText(safeDpi.toString())
        holder.input.setScaledTextSize(handler.currentFontSize, 1.0f)
        holder.slider.progress = safeDpi

        holder.slider.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val snapped = (progress / 5) * 5
                    val finalDpi = snapped.coerceAtLeast(72)
                    
                    if (holder.input.text.toString() != finalDpi.toString()) {
                        holder.input.setText(finalDpi.toString())
                        holder.input.setSelection(holder.input.text.length)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                val valInt = holder.input.text.toString().toIntOrNull()
                if (valInt != null) handler.selectDpi(valInt)
            }
        })

        holder.input.setOnEditorActionListener { v, actionId, _ -> 
            if (actionId == EditorInfo.IME_ACTION_DONE) { 
                val valInt = v.text.toString().toIntOrNull()
                if (valInt != null) { 
                    holder.slider.progress = valInt
                    handler.selectDpi(valInt)
                    handler.safeToast("DPI set to $valInt") 
                }
                handler.dismissKeyboardAndRestore()
                true 
            } else false 
        }
        
        holder.input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val valInt = s.toString().toIntOrNull()
                if (valInt != null && holder.slider.progress != valInt) {
                    holder.slider.progress = valInt
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        holder.input.setOnFocusChangeListener { _, hasFocus -> 
            if (handler.autoResizeEnabled) handler.updateDrawerHeight(hasFocus)
        }
    }
    
    private fun bindFontSizeHolder(holder: FontSizeHolder, item: FontSizeOption) {
        holder.textLabel.setScaledTextSize(handler.currentFontSize, 1.0f)
        holder.textVal.text = item.currentSize.toInt().toString()
        holder.textVal.setScaledTextSize(handler.currentFontSize, 1.125f)
        holder.textUnit.setScaledTextSize(handler.currentFontSize, 0.75f)
        holder.btnMinus.setOnClickListener { handler.changeFontSize(item.currentSize - 1) }
        holder.btnPlus.setOnClickListener { handler.changeFontSize(item.currentSize + 1) }
    }
    
    private fun bindBubbleSizeHolder(holder: HeightHolder, item: BubbleSizeOption) {
        holder.textLabel.text = "Bubble Size:"
        holder.textLabel.setScaledTextSize(handler.currentFontSize, 1.0f)
        holder.textVal.text = item.currentPercent.toString()
        holder.textVal.setScaledTextSize(handler.currentFontSize, 1.125f)
        holder.textUnit.text = "%"
        holder.textUnit.setScaledTextSize(handler.currentFontSize, 0.75f)
        holder.btnMinus.setOnClickListener { handler.changeBubbleSize(-10) }
        holder.btnPlus.setOnClickListener { handler.changeBubbleSize(10) }
    }
    
    private fun bindHeightHolder(holder: HeightHolder) {
        holder.textLabel.setScaledTextSize(handler.currentFontSize, 1.0f)
        holder.textVal.text = handler.currentDrawerHeightPercent.toString()
        holder.textVal.setScaledTextSize(handler.currentFontSize, 1.125f)
        holder.textUnit.setScaledTextSize(handler.currentFontSize, 0.75f)
        holder.btnMinus.setOnClickListener { handler.changeDrawerHeight(-5) }
        holder.btnPlus.setOnClickListener { handler.changeDrawerHeight(5) }
    }
    
    private fun bindWidthHolder(holder: WidthHolder) {
        holder.textLabel.setScaledTextSize(handler.currentFontSize, 1.0f)
        holder.textVal.text = handler.currentDrawerWidthPercent.toString()
        holder.textVal.setScaledTextSize(handler.currentFontSize, 1.125f)
        holder.textUnit.setScaledTextSize(handler.currentFontSize, 0.75f)
        holder.btnMinus.setOnClickListener { handler.changeDrawerWidth(-5) }
        holder.btnPlus.setOnClickListener { handler.changeDrawerWidth(5) }
    }
    
    private fun bindMarginHolder(holder: MarginHolder, item: MarginOption, position: Int) {
        holder.label.text = if (item.type == 0) "Top Margin:" else "Bottom Margin:"
        holder.label.setScaledTextSize(handler.currentFontSize, 1.0f)
        holder.text.text = "${item.currentPercent}%"
        holder.text.setScaledTextSize(handler.currentFontSize, 1.125f)
        
        if (holder.slider.progress != item.currentPercent) {
            holder.slider.progress = item.currentPercent
        }
        
        holder.slider.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    holder.text.text = "$progress%"
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                val progress = seekBar?.progress ?: 0
                
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val newOption = MarginOption(item.type, progress)
                    handler.displayList[pos] = newOption
                    if (pos < handler.unfilteredDisplayList.size && handler.unfilteredDisplayList[pos] is MarginOption) {
                        handler.unfilteredDisplayList[pos] = newOption
                    }
                }
                
                if (item.type == 0) {
                    handler.topMarginPercent = progress
                    AppPreferences.setTopMarginPercent(holder.itemView.context, handler.currentDisplayId, progress)
                    AppPreferences.setTopMarginPercent(holder.itemView.context, handler.currentDisplayId, progress, handler.orientSuffix())
                    handler.safeToast("Top Margin: $progress% (Display ${handler.currentDisplayId})")
                } else {
                    handler.bottomMarginPercent = progress
                    AppPreferences.setBottomMarginPercent(holder.itemView.context, handler.currentDisplayId, progress)
                    AppPreferences.setBottomMarginPercent(holder.itemView.context, handler.currentDisplayId, progress, handler.orientSuffix())

                    holder.itemView.context.getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE).edit()
                        .putInt("auto_resize_scale${handler.orientSuffix()}", progress)
                        .putInt("auto_resize_scale", progress)
                        .apply()

                    handler.safeToast("Bottom Margin: $progress% (Display ${handler.currentDisplayId})")
                    
                    val intent = Intent("com.katsuyamaki.DroidOSLauncher.MARGIN_CHANGED")
                    intent.putExtra("PERCENT", progress)
                    intent.setPackage("com.katsuyamaki.DroidOSTrackpadKeyboard")
                    handler.handlerContext.sendBroadcast(intent)
                }
                
                handler.setupVisualQueue()
                handler.applyLayoutImmediate(null)
            }
        })
    }
    
    private fun bindCustomModHolder(holder: CustomModHolder, item: CustomModConfigOption) {
        holder.input.setScaledTextSize(handler.currentFontSize, 1.0f)
        val currentStr = handler.getCharFromKeyCode(item.currentKeyCode)
        if (holder.input.text.toString() != currentStr) {
            holder.input.setText(currentStr)
        }

        holder.input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    val char = s[0]
                    val code = handler.getKeyCodeFromChar(char)
                    if (code != 0) {
                        AppPreferences.saveCustomModKey(handler.handlerContext, code)
                        handler.customModKey = code
                        handler.safeToast("Custom Mod set to: $char (Code $code)")
                        handler.sendCustomModToTrackpad()
                    } else {
                        handler.safeToast("Unsupported Key Character")
                    }
                } else {
                    AppPreferences.saveCustomModKey(handler.handlerContext, 0)
                    handler.customModKey = 0
                    handler.sendCustomModToTrackpad()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })
    }
    
    private fun bindKeybindHolder(holder: KeybindHolder, item: KeybindOption, position: Int) {
        holder.title.text = item.def.label
        holder.title.setScaledTextSize(handler.currentFontSize, 1.0f)
        holder.desc.text = item.def.description
        holder.desc.setScaledTextSize(handler.currentFontSize, 0.75f)

        val modText = when (item.modifier) {
            KeyEvent.META_ALT_ON -> "ALT"
            KeyEvent.META_SHIFT_ON -> "SHIFT"
            KeyEvent.META_CTRL_ON -> "CTRL"
            KeyEvent.META_META_ON -> "META"
            handler.MOD_CUSTOM -> "CSTM"
            0 -> "NONE"
            else -> "MOD"
        }
        
        if (item.modifier == 0) holder.btnMod.setTextColor(Color.RED)
        else if (item.modifier == handler.MOD_CUSTOM) holder.btnMod.setTextColor(Color.CYAN)
        else holder.btnMod.setTextColor(Color.GREEN)
        
        holder.btnMod.text = modText
        holder.btnMod.setScaledTextSize(handler.currentFontSize, 0.875f)
        holder.btnMod.setOnClickListener {
            item.modifier = when (item.modifier) {
                0 -> KeyEvent.META_ALT_ON
                KeyEvent.META_ALT_ON -> KeyEvent.META_SHIFT_ON
                KeyEvent.META_SHIFT_ON -> KeyEvent.META_CTRL_ON
                KeyEvent.META_CTRL_ON -> KeyEvent.META_META_ON
                KeyEvent.META_META_ON -> handler.MOD_CUSTOM
                handler.MOD_CUSTOM -> 0
                else -> KeyEvent.META_ALT_ON
            }
            AppPreferences.saveKeybind(handler.handlerContext, item.def.id, item.modifier, item.keyCode)
            handler.broadcastKeybindsToKeyboard()
            notifyItemChanged(position)
        }

        val keyName = handler.SUPPORTED_KEYS.entries.find { it.value == item.keyCode }?.key ?: "?"
        holder.btnKey.text = keyName
        holder.btnKey.setScaledTextSize(handler.currentFontSize, 0.875f)
        holder.btnKey.setOnClickListener {
            var safeMod = item.modifier
            if (safeMod == 0) {
                safeMod = KeyEvent.META_ALT_ON
                AppPreferences.saveKeybind(handler.handlerContext, item.def.id, safeMod, item.keyCode)
                handler.broadcastKeybindsToKeyboard()
                handler.safeToast("Safety: Modifier set to ALT")
            }
            handler.showKeyPicker(item.def.id, safeMod)
        }

        holder.itemView.setOnLongClickListener {
            val adbCmd = handler.buildAdbCommand(item.def.id)
            if (adbCmd != null) {
                val clipboard = handler.handlerContext.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("ADB Command", adbCmd)
                clipboard.setPrimaryClip(clip)
                handler.safeToast("ADB command copied!")
            }
            true
        }
    }
    
    // ========== UTILITY ==========
    
    private fun calculateGCD(a: Int, b: Int): Int {
        return if (b == 0) a else calculateGCD(b, a % b)
    }
    
    private fun TextView.setScaledTextSize(baseFontSize: Float, scaleFactor: Float = 1.0f) {
        this.textSize = baseFontSize * scaleFactor
    }
}