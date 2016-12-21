package com.software.ssp.erkc.modules.autopayments

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.CardStatus
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class AutoPaymentsTabPresenter @Inject constructor(view: IAutoPaymentsTabView) : RxPresenter<IAutoPaymentsTabView>(view), IAutoPaymentsTabPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onAddNewAutoPaymentClick() {
        if (activeSession.isOfflineSession) {
            view?.showMessage(R.string.offline_mode_error)
            return
        }

        subscriptions += realmRepository.fetchCurrentUser()
                .subscribe(
                        {
                            currentUser ->
                            when {
                                currentUser.receipts.count { it.autoPayMode == 0 } == 0 -> {
                                    view?.showMessage(R.string.auto_payments_no_receipts_message)
                                }
                                currentUser.cards.count { it.statusId == CardStatus.ACTIVATED.ordinal } == 0 -> {
                                    view?.showMessage(R.string.auto_payments_no_cards_message)
                                }
                                else -> view?.navigateToNewAutoPayment()
                            }
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    override fun onInfoClick() {
        view?.navigateToInstruction()
    }
}
