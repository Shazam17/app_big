package com.software.ssp.erkc.common.mvp

import rx.subscriptions.CompositeSubscription

abstract class RxPresenter<V : IView>(view: V) : BasePresenter<V>(view) {

    protected val subscriptions: CompositeSubscription

    init {
        subscriptions = CompositeSubscription()
    }

    override fun onViewDetached() {
        subscriptions.clear()
        super.onViewDetached()
    }

}