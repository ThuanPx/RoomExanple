package com.thuanpx.roomexample.baseRecyclerView

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Created by ThuanPx on 3/15/20.
 */
abstract class EndlessRecyclerOnScrollListener protected constructor(private val layoutManager: RecyclerView.LayoutManager) :
    RecyclerView.OnScrollListener() {
    private var currentPage = 1
    private val startingPageIndex = 1
    private var previousTotalItemCount = 0
    private var loading = true
    fun reset() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
        layoutManager.scrollToPosition(0)
    }

    fun resetNoScroll() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    private val lastVisibleItemPosition: Int
        get() {
            var lastVisibleItemPosition = 0
            when (layoutManager) {
                is StaggeredGridLayoutManager -> {
                    val lastVisibleItemPositions =
                        layoutManager.findLastVisibleItemPositions(
                            null
                        )
                    lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
                }
                is GridLayoutManager -> {
                    lastVisibleItemPosition =
                        layoutManager.findLastVisibleItemPosition()
                }
                is LinearLayoutManager -> {
                    lastVisibleItemPosition =
                        layoutManager.findLastVisibleItemPosition()
                }
            }
            return lastVisibleItemPosition
        }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(view, dx, dy)
        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = lastVisibleItemPosition

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && lastVisibleItemPosition + VISIBLE_THRESHOLD > totalItemCount) {
            currentPage++
            onLoadMore(currentPage)
            loading = true
        }
    }

    abstract fun onLoadMore(currentPage: Int)

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

}