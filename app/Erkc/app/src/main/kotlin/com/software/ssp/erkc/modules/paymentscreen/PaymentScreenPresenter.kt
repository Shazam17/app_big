package com.software.ssp.erkc.modules.paymentscreen

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import javax.inject.Inject

class PaymentScreenPresenter @Inject constructor(view: IPaymentScreenView) : RxPresenter<IPaymentScreenView>(view), IPaymentScreenPresenter {

    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached() {
        when {
            activeSession.cachedReceipts == null || activeSession.cachedReceipts!!.isEmpty() -> view?.navigateToAddReceiptScreen()
            else -> view?.navigateToPaymentsList()
        }
    }
}
