package com.software.ssp.erkc.modules.transaction.valuestransaction

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmOfflineIpu
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

    override fun onViewAttached() {
        super.onViewAttached()
        showReceiptsList()
    }

    override fun onViewDetached() {
        realmRepository.close()
    }

    override fun onIpuClick(realmOfflineIpu: RealmOfflineIpu) {
        if (activeSession.isOfflineSession) {
            view?.showAlert(R.string.offline_mode_transaction_error)
            return
        }
        view?.navigateToIpuValueInfo(realmOfflineIpu)
    }

    override fun onSwipeToRefresh() {
    }

    override fun onDeleteClick(realmOfflineIpu: RealmOfflineIpu) {
        subscriptions += realmRepository.deleteOfflineIpu(realmOfflineIpu)
                .subscribe(
                        {
                            view?.notifyItemRemoved(realmOfflineIpu)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    override fun showReceiptsList() {
        subscriptions += realmRepository.fetchOfflineIpus()
                .subscribe(
                        {
                            offlineIpus ->
                            view?.showData(offlineIpus.sortedByDescending { it.createDate })
                        },
                        {
                            error ->
                            view?.setLoadingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }
}
