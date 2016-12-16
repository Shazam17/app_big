package com.software.ssp.erkc.modules.transaction.valuestransaction

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 14/12/2016.
 */
class ValuesTransactionListPresenter @Inject constructor(view: IValuesTransactionListView) : RxPresenter<IValuesTransactionListView>(view), IValuesTransactionListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository

    override fun onIpuClick(receipt: RealmReceipt) {
        view?.navigateToIpuValueInfo(receipt)
    }

    override fun onSwipeToRefresh() {

    }

    override fun onDeleteClick(receipt: RealmReceipt) {
        subscriptions += realmRepository.deleteOfflineIpu(receipt.id)
                .subscribe({
                    showReceiptsList()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onViewAttached() {
        super.onViewAttached()
        showReceiptsList()
    }

    override fun onViewDetached() {
        realmRepository.close()
    }

    private fun showReceiptsList() {
        subscriptions += realmRepository.fetchOfflineIpu()
                .subscribe(
                        {
                            paymentsInfo ->
                            view?.showData(paymentsInfo.groupBy { it.receipt }.keys.toList())
                        },
                        {
                            error ->
                            view?.setLoadingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

}