package com.quote.platon.util;

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

open class OnSwipeTouchListener : View.OnTouchListener {

        private val gestureDetector = GestureDetector(GestureListener())

        fun onTouch(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
        }

        private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onDown(e: MotionEvent): Boolean {
        return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        onTouch(e)
        return true
        }


        override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
        ): Boolean {
        val result = false
        try {
        val diffY = e2.y - e1!!.y
        val diffX = e2.x - e1.x
        if (Math.abs(diffX) > Math.abs(diffY)) {
        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
        if (diffX > 0) {
        onSwipeRight()
        } else {
        onSwipeLeft()
        }
        }
        } else {
        // onTouch(e);
        }
        } catch (exception: Exception) {
        exception.printStackTrace()
        }

        return result
        }
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
        }

        open fun onSwipeRight() {}

        open fun onSwipeLeft() {}

        open fun onSwipeTop() {}

        open fun onSwipeBottom() {}
        }