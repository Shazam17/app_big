package com.software.ssp.erkc.modules.history

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import javax.inject.Inject


class HistoryTabPresenter @Inject constructor(view: IHistoryTabView) : RxPresenter<IHistoryTabView>(view), IHistoryTabPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onFilterClick() {
        view?.navigateToFilter()
    }

    override fun onRefreshClick() {
        view?.refreshCurrentList()
    }
}
