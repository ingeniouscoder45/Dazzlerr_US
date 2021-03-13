package com.dazzlerr_usa.views.fragments.intro

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.Scroller

class CustomViewPager(context: Context, attrs: AttributeSet) : androidx.viewpager.widget.ViewPager(context, attrs)
{

    private var mScroller: FixedSpeedScroller? = null
    private var enabledFlag: Boolean = false

    init
    {
        this.enabledFlag = true
        init()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (this.enabledFlag) {
            return super.onTouchEvent(event)
        }
        init()
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.enabledFlag) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.enabledFlag = enabled
    }


    /*
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private fun init() {
        try {
            val viewpager = androidx.viewpager.widget.ViewPager::class.java
            val scroller = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            mScroller = FixedSpeedScroller(context,
                    DecelerateInterpolator())
            scroller.set(this, mScroller)
        } catch (ignored: Exception) {
        }

    }

    /*
     * Set the factor by which the duration will change
     */
    fun setScrollDuration(duration: Int) {
        mScroller!!.setScrollDuration(duration)
    }

    private inner class FixedSpeedScroller : Scroller {

        private var mDuration = 600

        constructor(context: Context) : super(context) {}

        constructor(context: Context, interpolator: Interpolator) : super(context, interpolator) {}

        constructor(context: Context, interpolator: Interpolator, flywheel: Boolean) : super(context, interpolator, flywheel) {}

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        fun setScrollDuration(duration: Int) {
            mDuration = duration
        }
    }
}