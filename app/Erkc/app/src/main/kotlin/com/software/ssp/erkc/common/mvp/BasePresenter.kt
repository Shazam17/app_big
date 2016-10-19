package com.software.ssp.erkc.common.mvp

abstract class BasePresenter<V : IView> : IPresenter<V> {

    protected var view:V? = null
    constructor(view: V){
        this.view = view
    }

    override fun takeView(view: V) {
        this.view = view
        onViewAttached()
    }

    override fun onViewAttached() {

    }

    override fun dropView() {
        view = null
        onViewDetached()
    }

    override fun onViewDetached() {

    }
}
