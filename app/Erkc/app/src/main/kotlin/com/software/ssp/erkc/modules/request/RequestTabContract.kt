package com.software.ssp.erkc.modules.request

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IRequestTabView : IView {
    fun openFilterAlert()
    fun refreshCurrentList()
}

interface IRequestTabPresenter : IPresenter<IRequestTabView> {
    fun onFilterClick()
    fun onRefreshClick()
}