package com.software.ssp.erkc.modules.transaction.paymenttransaction

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmOfflinePayment
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 13/12/2016.
 */
class PaymentTransactionListPresenter @Inject constructor(view: IPaymentTransactionListView) : RxPresenter<IPaymentTransactionListView>(view), IPaymentTransactionListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onPaymentClick(payment: RealmOfflinePayment) {
        view?.navigateToPaymentInfo(payment)
    }

    override fun onDeleteClick(payment: RealmOfflinePayment) {
        showPaymentsList()
    }

    override fun onSwipeToRefresh() {

    }

    override fun onViewAttached() {
        super.onViewAttached()
        showPaymentsList()
    }

    override fun onViewDetached() {
        realmRepository.close()
    }

    private fun showPaymentsList() {
        subscriptions += realmRepository.fetchOfflinePayments()
                .subscribe(
                        {
                            payments ->
                            view?.showData(payments)
                        },
                        {
                            error ->
                            view?.setLoadingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        })
    }

}