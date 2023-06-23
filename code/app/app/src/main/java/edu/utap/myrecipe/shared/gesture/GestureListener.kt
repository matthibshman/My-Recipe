package edu.utap.myrecipe.shared.gesture

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class GestureListener(val onSwipeRight: () -> Unit, val onSwipeLeft: () -> Unit) :
    GestureDetector.SimpleOnGestureListener() {
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    override fun onDown(event: MotionEvent): Boolean {
        return true
    }

    // swipe detection modeled from:
    // https://www.tutorialspoint.com/how-to-detect-swipe-direction-between-left-right-and-up-down-in-android
    override fun onFling(
        event1: MotionEvent, event2: MotionEvent, velocityX: Float, velocityY: Float
    ): Boolean {
        var result = false;
        try {
            val diffY = event2.y - event1.y
            val diffX = event2.x - event1.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    result = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}