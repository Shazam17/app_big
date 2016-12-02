package com.software.ssp.erkc.modules.paymentsinfo

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.PaymentRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 01/12/2016.
 */
class PaymentInfoPresenter @Inject constructor(view: IPaymentInfoView) : RxPresenter<IPaymentInfoView>(view), IPaymentInfoPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var paymentRepository: PaymentRepository

    override fun onRetryClick() {
        view?.showMessage("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGetCheckClick() {
        view?.showMessage("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onViewAttached(id: String) {
        view?.setProgressVisibility(true)
        subscriptions += realmRepository.fetchPaymentById(id)
                .concatMap {
                    payment ->
                    view?.fillData(payment)
                    paymentRepository.fetchPaymentInfo(activeSession.accessToken!!, id)
                }
                .concatMap {
                    paymentInfo ->
                    realmRepository.savePaymentInfo(paymentInfo)
                }
                .subscribe({
                    showPaymentInfo(id)
                })
    }

    private fun showPaymentInfo(id: String) {
        subscriptions += realmRepository.fetchPaymentInfoById(id)
                .subscribe(
                        {
                            paymentInfo ->
                            view?.fillData(paymentInfo)
                            view?.setProgressVisibility(false)
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.parsedMessage())
                        })
    }

}