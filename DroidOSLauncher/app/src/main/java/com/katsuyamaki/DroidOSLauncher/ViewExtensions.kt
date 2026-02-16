package com.katsuyamaki.DroidOSLauncher

import android.widget.TextView

fun TextView.setScaledTextSize(baseFontSize: Float, scaleFactor: Float = 1.0f) {
    this.textSize = baseFontSize * scaleFactor
}