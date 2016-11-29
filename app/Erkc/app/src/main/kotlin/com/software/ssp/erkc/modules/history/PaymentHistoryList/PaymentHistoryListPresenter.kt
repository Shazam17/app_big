package com.software.ssp.erkc.modules.history.PaymentHistoryList

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmPayment
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.PaymentRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class PaymentHistoryListPresenter @Inject constructor(view: IPaymentHistoryListView) : RxPresenter<IPaymentHistoryListView>(view), IPaymentHistoryListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var paymentRepository: PaymentRepository

    override fun onViewAttached() {
        showPaymentsList()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onSwipeToRefresh() {
        view?.setLoadingVisible(false)
        //TODO Add fetch payments api call
    }

    override fun onPaymentClick(payment: RealmPayment) {
        view?.navigateToPaymentInfo(payment)
    }

    private fun showPaymentsList() {
        subscriptions += realmRepository.fetchPayments()
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
