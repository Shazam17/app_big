package com.software.ssp.erkc.modules.request

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.modules.request.authedRequest.filterRequest.StatusModel

interface IRequestTabView : IView {
    fun refreshCurrentList()
}

interface IRequestTabPresenter : IPresenter<IRequestTabView> {

    fun onRefreshClick()
}