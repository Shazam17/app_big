package com.software.ssp.erkc.common.mvp

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import kotlinx.android.synthetic.main.layout_recycler_view.*
import java.util.*

abstract class BaseListFragment<M> : MvpFragment(), IListView<M> {

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
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.layout_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
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

    abstract protected fun onSwipeToRefresh()

    protected open fun setEmptyViewVisible(visible: Boolean) {
        emptyMessageTextView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    protected open fun initViews() {
        swipeRefreshLayout!!.setOnRefreshListener {
            onSwipeToRefresh()
        }

        val accentColorId = ContextCompat.getColor(activity, R.color.colorPrimary)
        swipeRefreshLayout.setColorSchemeColors(accentColorId)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        adapter = createAdapter()
        if (adapter == null) {
            throw IllegalArgumentException("createAdapter() should return adapter instance.")
        }
        recyclerView.adapter = adapter
    }

    protected abstract fun createAdapter(): RecyclerView.Adapter<*>
}
