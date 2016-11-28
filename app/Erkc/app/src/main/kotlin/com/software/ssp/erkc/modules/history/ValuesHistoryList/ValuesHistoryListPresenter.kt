package com.software.ssp.erkc.modules.history.ValuesHistoryList

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class ValuesHistoryListPresenter @Inject constructor(view: IValuesHistoryListView) : RxPresenter<IValuesHistoryListView>(view), IValuesHistoryListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewAttached() {
        showReceiptsList()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onSwipeToRefresh() {
        showReceiptsList()
    }

    override fun onIpuValueClick(ipuValue: RealmIpuValue) {
        view?.navigateToIpuValueInfo(ipuValue)
    }

    private fun showReceiptsList() {
        subscriptions += realmRepository.fetchIpuValues()
                .subscribe(
                        {
                            ipuValues ->
                            view?.showData(ipuValues)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        })
    }
}
