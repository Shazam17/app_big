package com.software.ssp.erkc.modules.mainscreen

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import javax.inject.Inject

class MainScreenPresenter @javax.inject.Inject constructor(view: IMainScreenView) : RxPresenter<IMainScreenView>(view), IMainScreenPresenter {

    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached() {
        when {
            activeSession.user == null -> view?.showNonAuthedScreen()
            //activeSession.cachedReceipts == null || activeSession.cachedReceipts!!.isEmpty() -> view?.showAddReceiptScreen()
            else -> view?.showReceiptListScreen()
        }
    }
}
