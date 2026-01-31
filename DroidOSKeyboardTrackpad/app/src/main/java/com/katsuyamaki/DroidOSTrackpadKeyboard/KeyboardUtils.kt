package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.view.KeyEvent

object KeyboardUtils {

    // Special IDs for logic
    const val KEY_SHIFT = -100
    const val KEY_SYM = -101
    const val KEY_SPACE = KeyEvent.KEYCODE_SPACE
    const val KEY_DEL = KeyEvent.KEYCODE_DEL
    const val KEY_ENTER = KeyEvent.KEYCODE_ENTER
    
    // Arrows
    const val KEY_UP = KeyEvent.KEYCODE_DPAD_UP
    const val KEY_DOWN = KeyEvent.KEYCODE_DPAD_DOWN
    const val KEY_LEFT = KeyEvent.KEYCODE_DPAD_LEFT
    const val KEY_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT

    data class KeyDef(val label: String, val code: Int, val widthWeight: Float = 1.0f)

    val ROW_1_LOWER = listOf(
        KeyDef("q", KeyEvent.KEYCODE_Q), KeyDef("w", KeyEvent.KEYCODE_W), KeyDef("e", KeyEvent.KEYCODE_E),
        KeyDef("r", KeyEvent.KEYCODE_R), KeyDef("t", KeyEvent.KEYCODE_T), KeyDef("y", KeyEvent.KEYCODE_Y),
        KeyDef("u", KeyEvent.KEYCODE_U), KeyDef("i", KeyEvent.KEYCODE_I), KeyDef("o", KeyEvent.KEYCODE_O), KeyDef("p", KeyEvent.KEYCODE_P)
    )
    
    val ROW_2_LOWER = listOf(
        KeyDef("a", KeyEvent.KEYCODE_A), KeyDef("s", KeyEvent.KEYCODE_S), KeyDef("d", KeyEvent.KEYCODE_D),
        KeyDef("f", KeyEvent.KEYCODE_F), KeyDef("g", KeyEvent.KEYCODE_G), KeyDef("h", KeyEvent.KEYCODE_H),
        KeyDef("j", KeyEvent.KEYCODE_J), KeyDef("k", KeyEvent.KEYCODE_K), KeyDef("l", KeyEvent.KEYCODE_L)
    )

    val ROW_3_LOWER = listOf(
        KeyDef("⇧", KEY_SHIFT, 1.5f), KeyDef("z", KeyEvent.KEYCODE_Z), KeyDef("x", KeyEvent.KEYCODE_X),
        KeyDef("c", KeyEvent.KEYCODE_C), KeyDef("v", KeyEvent.KEYCODE_V), KeyDef("b", KeyEvent.KEYCODE_B),
        KeyDef("n", KeyEvent.KEYCODE_N), KeyDef("m", KeyEvent.KEYCODE_M), KeyDef("⌫", KEY_DEL, 1.5f)
    )

    // UPPERCASE 
    val ROW_1_UPPER = ROW_1_LOWER.map { it.copy(label = it.label.uppercase()) }
    val ROW_2_UPPER = ROW_2_LOWER.map { it.copy(label = it.label.uppercase()) }
    val ROW_3_UPPER = ROW_3_LOWER.map { 
        if(it.label.length == 1) it.copy(label = it.label.uppercase()) else it 
    }

    // SYMBOLS
    val ROW_1_SYM = listOf(
        KeyDef("1", KeyEvent.KEYCODE_1), KeyDef("2", KeyEvent.KEYCODE_2), KeyDef("3", KeyEvent.KEYCODE_3),
        KeyDef("4", KeyEvent.KEYCODE_4), KeyDef("5", KeyEvent.KEYCODE_5), KeyDef("6", KeyEvent.KEYCODE_6),
        KeyDef("7", KeyEvent.KEYCODE_7), KeyDef("8", KeyEvent.KEYCODE_8), KeyDef("9", KeyEvent.KEYCODE_9), KeyDef("0", KeyEvent.KEYCODE_0)
    )
    
    val ROW_2_SYM = listOf(
        KeyDef("@", KeyEvent.KEYCODE_AT), KeyDef("#", KeyEvent.KEYCODE_POUND), KeyDef("$", KeyEvent.KEYCODE_4), // $ shares 4 shift usually, but we map roughly
        KeyDef("%", KeyEvent.KEYCODE_5), KeyDef("&", KeyEvent.KEYCODE_7), KeyDef("-", KeyEvent.KEYCODE_MINUS),
        KeyDef("+", KeyEvent.KEYCODE_PLUS), KeyDef("(", KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN), KeyDef(")", KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN)
    )
    
    val ROW_3_SYM = listOf(
        KeyDef("ABC", KEY_SYM, 1.5f), KeyDef("!", KeyEvent.KEYCODE_1), KeyDef("\"", KeyEvent.KEYCODE_APOSTROPHE),
        KeyDef("'", KeyEvent.KEYCODE_APOSTROPHE), KeyDef(":", KeyEvent.KEYCODE_SEMICOLON), KeyDef(";", KeyEvent.KEYCODE_SEMICOLON),
        KeyDef("/", KeyEvent.KEYCODE_SLASH), KeyDef("?", KeyEvent.KEYCODE_SLASH), KeyDef("⌫", KEY_DEL, 1.5f)
    )

    val ROW_4 = listOf(
        KeyDef("?123", KEY_SYM, 1.5f),
        KeyDef(",", KeyEvent.KEYCODE_COMMA),
        KeyDef("SPACE", KEY_SPACE, 4.0f),
        KeyDef(".", KeyEvent.KEYCODE_PERIOD),
        KeyDef("⏎", KEY_ENTER, 1.5f)
    )
    
    val ROW_5_ARROWS = listOf(
        KeyDef("HIDE", -999, 2.0f), // Hide
        KeyDef("◄", KEY_LEFT),
        KeyDef("▲", KEY_UP),
        KeyDef("▼", KEY_DOWN),
        KeyDef("►", KEY_RIGHT)
    )
}
