package com.software.ssp.erkc.modules.paymentscreen

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class PaymentScreenPresenter @Inject constructor(view: IPaymentScreenView) : RxPresenter<IPaymentScreenView>(view), IPaymentScreenPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewAttached() {
        if(activeSession.isOfflineSession) {
            view?.navigateToPaymentsList()
            return
        }

        if(activeSession.accessToken.isNullOrEmpty()){
            view?.navigateToAddReceiptScreen()
            return
        }

        subscriptions += realmRepository.fetchReceiptsList()
                .subscribe(
                        {
                            receipts ->
                            when {
                                receipts == null || receipts.count() == 0 -> view?.navigateToAddReceiptScreen()
                                else -> view?.navigateToPaymentsList()
                            }
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        })
    }

    override fun onViewDetached() {
        super.onViewDetached()
        realmRepository.close()
    }
}
