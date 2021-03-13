package com.dazzlerr_usa.utilities

/**
 * Created by Raghubeer Singh Virk
 */
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView

class TouchDetectableScrollView : NestedScrollView {

    var myScrollChangeListener: OnMyScrollChangeListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        if (myScrollChangeListener != null) {
            myScrollChangeListener!!.onScroll()
            if (t > oldt) {
                myScrollChangeListener!!.onScrollDown()
            } else if (t < oldt) {
                myScrollChangeListener!!.onScrollUp()
            }
            val view = getChildAt(getChildCount() - 1) as View
            val diff = view.bottom - (getHeight() + getScrollY())




            if (diff == 0) {
                myScrollChangeListener!!.onBottomReached()
            }

            if (getScrollY() === 0) {
                myScrollChangeListener!!.onTopReached()
            }

        }
    }

    interface OnMyScrollChangeListener {
        fun onScroll()
        fun onScrollUp()
        fun onScrollDown()
        fun onBottomReached()
        fun onTopReached()

    }

    companion object {

        var lengthCount = 4000
    }
}