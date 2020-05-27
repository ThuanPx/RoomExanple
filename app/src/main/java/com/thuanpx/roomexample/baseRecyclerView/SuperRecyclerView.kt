package com.thuanpx.roomexample.baseRecyclerView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.thuanpx.roomexample.R


class SuperRecyclerView : FrameLayout {

    private var recyclerView: RecyclerView? = null
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    public var swipeRefreshLayout: ScrollChildSwipeRefreshLayout? = null

    private lateinit var loadMoreAdapter: BaseLoadMoreAdapter<*>

    private var isRefresh = false
    private var currentPage = PAGE_DEFAULT

    private var loadDataListener: LoadDataListener? = null
    private var isEnableLoadMore: Boolean = true

    constructor(@NonNull context: Context) : super(context) {
        initView()
    }

    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.super_recyclerview, this, false)
        recyclerView = view.findViewById(R.id.super_recyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout?.setColorSchemeResources(R.color.teel_200)
        addView(view)
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        loadMoreAdapter = adapter as BaseLoadMoreAdapter<*>
        recyclerView?.adapter = adapter
    }

    fun setRecyclerViewPool(viewType: Int, max: Int) {
        recyclerView?.recycledViewPool?.setMaxRecycledViews(viewType, max)
    }

    fun enableRecyclerViewPool() {
        recyclerView?.setRecycledViewPool(RecyclerView.RecycledViewPool())
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        if (layoutManager is GridLayoutManager) {
            val spanSize = layoutManager.spanCount
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (loadMoreAdapter.isLoadMore
                            && position == loadMoreAdapter.bottomItemPosition()) {
                        spanSize
                    } else 1
                }
            }
        }
        recyclerView?.layoutManager = layoutManager

        onScrollListener = object : EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (!isEnableLoadMore) {
                    return
                }
                startLoadMore()
            }
        }
        recyclerView?.addOnScrollListener(onScrollListener as EndlessRecyclerOnScrollListener)

        swipeRefreshLayout?.setOnRefreshListener {
            if (swipeRefreshLayout?.isEnabled == false) return@setOnRefreshListener
            this.startRefreshData()
        }

        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                loadDataListener?.onScroll()
            }
        })
    }

    fun setDisableItemAnimator() {
        recyclerView?.itemAnimator = null
    }

    fun setHasFixedSize(isFixed: Boolean) {
        recyclerView?.setHasFixedSize(isFixed)
    }

    fun setEnableSwipe(isEnable: Boolean) {
        swipeRefreshLayout?.isEnabled = isEnable
    }

    fun scrollToTop() {
        recyclerView?.smoothScrollToPosition(0)
    }

    fun startRefreshData() {
        if (isRefresh) return

        isRefresh = true
        currentPage = PAGE_DEFAULT

        swipeRefreshLayout?.isRefreshing = true
        loadDataListener?.onRefreshData()
    }

    fun stopRefreshData() {
        if (!isRefresh) return
        isRefresh = false
        swipeRefreshLayout?.isRefreshing = false
    }

    fun startLoadMore() {
        if (loadMoreAdapter.isLoadMore || isRefresh) {
            return
        }
        currentPage++
        loadMoreAdapter.onStartLoadMore()
        loadDataListener?.onLoadMore(currentPage)
    }

    fun stopLoadMore() {
        loadMoreAdapter.onStopLoadMore()
    }

    fun stopAllStatusLoadData() {
        stopRefreshData()
        stopLoadMore()
    }

    fun refreshAdapter() {
        resetState()
        currentPage = PAGE_DEFAULT
        loadMoreAdapter.isLoadMore = false
    }

    fun getViewItem(position: Int): RecyclerView.ViewHolder? {
        return recyclerView?.findViewHolderForAdapterPosition(position)
    }
    fun setEnableLoadMore(isEnable: Boolean) {
        isEnableLoadMore = isEnable
    }


    fun disableAnimateRecyclerView() {
        (recyclerView?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    fun enableAnimateRecyclerView() {
        (recyclerView?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = true
    }

    fun resetState() {
        (onScrollListener as EndlessRecyclerOnScrollListener).reset()
    }

    fun setLoadDataListener(listener: LoadDataListener) {
        loadDataListener = listener
    }

    interface LoadDataListener {
        fun onLoadMore(page: Int)

        fun onRefreshData()

        fun onScroll()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        loadDataListener = null
        swipeRefreshLayout?.setOnRefreshListener(null)
        swipeRefreshLayout = null
        onScrollListener?.let { recyclerView?.removeOnScrollListener(it) }
    }

    companion object {
        private val TAG = SuperRecyclerView::class.java.simpleName
        private const val PAGE_DEFAULT = 1
    }
}
