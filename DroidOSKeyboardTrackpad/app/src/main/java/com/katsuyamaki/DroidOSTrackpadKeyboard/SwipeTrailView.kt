package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View

// =================================================================================
// CLASS: SwipeTrailView
// SUMMARY: A custom View that renders a trail/path for swipe gestures.
//          Supports configurable trail color for:
//          - Blue (0x00A0E9) - Normal swipe typing trail
//          - Orange (0xFF9900) - Virtual mirror orientation trail
//          The view is non-interactive and passes all touches through.
// =================================================================================
class SwipeTrailView(context: Context) : View(context) {

    private val path = Path()
    private val paint = Paint().apply {
        color = Color.parseColor("#00A0E9") // DroidOS Blue (default)
        style = Paint.Style.STROKE
        strokeWidth = 12f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
        alpha = 180
    }

    init {
        // Important: Pass touches through this view so the keyboard underneath gets them
        isClickable = false
        isFocusable = false
    }

    // =================================================================================
    // FUNCTION: setTrailColor
    // SUMMARY: Sets the color of the trail. Used to differentiate between:
    //          - Blue trail for normal swipe typing
    //          - Orange trail for virtual mirror orientation mode
    // @param color - ARGB color integer (e.g., 0xFFFF9900 for orange)
    // =================================================================================
    fun setTrailColor(color: Int) {
        paint.color = color
        // Preserve alpha transparency
        paint.alpha = 180
        invalidate()
    }
    // =================================================================================
    // END BLOCK: setTrailColor
    // =================================================================================

    // =================================================================================
    // FUNCTION: resetToDefaultColor
    // SUMMARY: Resets trail color back to DroidOS blue (default swipe color).
    // =================================================================================
    fun resetToDefaultColor() {
        paint.color = Color.parseColor("#00A0E9")
        paint.alpha = 180
        invalidate()
    }
    // =================================================================================
    // END BLOCK: resetToDefaultColor
    // =================================================================================

    // =================================================================================
    // FUNCTION: addPoint
    // SUMMARY: Adds a point to the trail path. Called during touch move events.
    // @param x - X coordinate relative to the view
    // @param y - Y coordinate relative to the view
    // =================================================================================
    fun addPoint(x: Float, y: Float) {
        if (path.isEmpty) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
        invalidate()
    }
    // =================================================================================
    // END BLOCK: addPoint
    // =================================================================================

    // =================================================================================
    // FUNCTION: clear
    // SUMMARY: Clears the trail path. Called when swipe ends or orientation mode exits.
    // =================================================================================
    fun clear() {
        path.reset()
        invalidate()
    }
    // =================================================================================
    // END BLOCK: clear
    // =================================================================================

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }
}
// =================================================================================
// END CLASS: SwipeTrailView
// =================================================================================
