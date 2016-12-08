package com.software.ssp.erkc.modules.history.ValuesHistoryList

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject
import kotlin.comparisons.compareBy


class ValuesHistoryListPresenter @Inject constructor(view: IValuesHistoryListView) : RxPresenter<IValuesHistoryListView>(view), IValuesHistoryListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository

    override fun onViewAttached() {
        showReceiptsList()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onSwipeToRefresh() {
        subscriptions += receiptsRepository.fetchReceipts(activeSession.accessToken!!)
                .concatMap {
                    receipts ->
                    realmRepository.saveReceiptsList(receipts)
                }
                .subscribe(
                        {
                            showReceiptsList()
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setLoadingVisible(false)
                        })
    }

    override fun onReceiptClick(receipt: RealmReceipt) {
        //todo change date from filter
        val time = Date().time
        view?.navigateToIpuValueInfo(receipt, Date(time - 60000 * 60 * 24 * 7), Date(time + 60000 * 60 * 24))
    }

    private fun showReceiptsList() {
        subscriptions += realmRepository.fetchReceiptsList()
                .subscribe(
                        {
                            receipts ->
                            view?.showData(receipts
                                    .filter { it.lastIpuTransferDate != null }
                                    .sortedWith(compareBy({ it.address }, { it.lastIpuTransferDate }))
                            )
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        })
    }
}
