package com.software.ssp.erkc.modules.request

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import javax.inject.Inject

class RequestTabPresenter @Inject constructor(view:IRequestTabView):RxPresenter<IRequestTabView>(view),IRequestTabPresenter{

    @Inject lateinit var activeSession:ActiveSession

    override fun onFilterClick() {
        view?.openFilterAlert()
    }

    override fun onRefreshClick() {
        view?.refreshCurrentList()
    }
}