package com.software.ssp.erkc.modules.paymentsinfo

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmPaymentInfo
import com.software.ssp.erkc.data.rest.ActiveSession
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

    override var paymentId: String = ""

    private var payment: RealmPaymentInfo? = null

    override fun onViewAttached() {
        fetchPaymentInfo()
    }

    override fun onRetryClick() {
        view?.navigateToRetryPayment(payment!!)
    }

    override fun onGetCheckClick() {
        if (activeSession.isOfflineSession) {
            view?.showMessage(R.string.offline_mode_error)
            return
        }

        view?.navigateToCheck(payment!!)
    }

    private fun fetchPaymentInfo() {
        view?.setProgressVisibility(true)
        subscriptions += realmRepository
                .fetchPaymentInfoById(paymentId)
                .subscribe(
                        {
                            payment ->
                            this.payment = payment
                            view?.fillData(payment)
                            view?.setProgressVisibility(false)
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }
}
