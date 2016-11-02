package com.software.ssp.erkc.common.mvp

import com.software.ssp.erkc.ErkcApplication

interface IView {
    fun application(): ErkcApplication
    fun showMessage(message: String)
    fun showMessage(messageResId: Int)
}

interface IListView<M> : IView {
    fun setLoadingVisible(isVisible: Boolean)
    fun showData(dataset: List<M>)
}

interface IPresenter<in V : IView> {
    fun takeView(view: V)
    fun onViewAttached()
    fun dropView()
    fun onViewDetached()
}

interface IListPresenter<M, V : IListView<*>> : IPresenter<V> {
    fun onSwipeToRefresh()
    fun onItemClick(item: M)
}
