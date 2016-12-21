package com.software.ssp.erkc.modules.paymentsinfo

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.PaymentRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.Observable
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
        if (activeSession.isOfflineSession) {
            view?.showMessage(R.string.offline_mode_error)
            return
        }

        view?.navigateToCheck()
    }

    override fun onViewAttached(id: String) {
        view?.setProgressVisibility(true)

        subscriptions += Observable.just(activeSession.isOfflineSession)
                .concatMap {
                    isOfflineSession ->
                    if (isOfflineSession) {
                        Observable.just(null)
                    } else {
                        updatePaymentInfo(id)
                    }
                }
                .concatMap {
                    Observable.zip(realmRepository.fetchPaymentInfoById(id), realmRepository.fetchPaymentById(id),
                            {
                                paymentInfo, payment ->

                                if (paymentInfo == null) {
                                    view?.showMessage(R.string.payment_info_no_cached_payment_dialog_content)
                                    view?.close()
                                    return@zip
                                }

                                view?.fillData(paymentInfo, payment)
                            })
                }
                .subscribe(
                        {
                            view?.setProgressVisibility(false)
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun updatePaymentInfo(paymentId: String): Observable<Boolean> {
        return paymentRepository.fetchPaymentInfo(paymentId)
                .concatMap {
                    paymentInfo ->
                    realmRepository.savePaymentInfo(paymentInfo)
                }
    }
}