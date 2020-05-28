package com.thuanpx.roomexample.baseRecyclerView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Extends [SwipeRefreshLayout] to support non-direct descendant scrolling views.
 *
 *
 * [SwipeRefreshLayout] works as expected when a scroll view is a direct child: it triggers
 * the refresh only when the view is on top. This class adds a way (@link #setScrollUpChild} to
 * define which view controls this behavior.
 */
class ScrollChildSwipeRefreshLayout : SwipeRefreshLayout {
    private var scrollUpChild: View? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun canChildScrollUp(): Boolean {
        return if (scrollUpChild != null) {
            scrollUpChild!!.canScrollVertically(-1)
        } else super.canChildScrollUp()
    }

    fun setScrollUpChild(view: View?) {
        scrollUpChild = view
    }
}