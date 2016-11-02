package com.software.ssp.erkc.common.mvp

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.R
import kotlinx.android.synthetic.main.layout_recycler_view.*
import java.util.*

abstract class BaseListActivity<M, V : IListView<M>, P : IListPresenter<M, V>> : MvpActivity(), IListView<M> {

    protected var dataset: MutableList<M> = ArrayList()
    protected var adapter: RecyclerView.Adapter<*>? = null

    protected var emptyMessageText: String = ""
        set(message) {
            emptyMessageTextView.text = message
        }

    protected var swipeToRefreshEnabled = false
        set (isEnabled) {
            swipeRefreshLayout.isEnabled = isEnabled
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_recycler_view)
    }

    override fun setLoadingVisible(isVisible: Boolean) {
        if (!swipeRefreshLayout.isRefreshing) {
            progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    override fun showData(dataset: List<M>) {
        this.dataset.clear()
        this.dataset.addAll(dataset)
        swipeRefreshLayout.isRefreshing = false
        setLoadingVisible(false)
        setEmptyViewVisible(this.dataset.isEmpty())
        adapter?.notifyDataSetChanged()
    }

    open protected fun onSwipeToRefresh() {
    }

    protected fun setEmptyViewVisible(visible: Boolean) {
        emptyMessageTextView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    // ===========================================================
    // Methods
    // ===========================================================

    protected open fun initViews() {
        swipeRefreshLayout!!.setOnRefreshListener {
            onSwipeToRefresh()
        }

        val accentColorId = ContextCompat.getColor(this, R.color.colorPrimary)
        swipeRefreshLayout.setColorSchemeColors(accentColorId)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        adapter = createAdapter()
        if (adapter == null) {
            throw IllegalArgumentException("createAdapter() should return adapter instance.")
        }
        recyclerView.adapter = adapter
    }

    protected abstract fun createAdapter(): RecyclerView.Adapter<*>

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
