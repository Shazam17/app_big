package com.software.ssp.erkc.modules.history

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IHistoryTabView : IView {
    fun navigateToFilter()
}

interface IHistoryTabPresenter : IPresenter<IHistoryTabView> {
    fun onFilterClick()
}
