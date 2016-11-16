package com.software.ssp.erkc.modules.paymentscreen.payment

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.CardStatus
import com.software.ssp.erkc.extensions.isEmail
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 10/11/2016.
 */
class PaymentPresenter @Inject constructor(view: IPaymentView) : RxPresenter<IPaymentView>(view), IPaymentPresenter {

    @Inject lateinit var cardsRepository: CardsRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached(receipt: Receipt) {
        super.onViewAttached()
        view?.setProgressVisibility(true)
        subscriptions += cardsRepository
                .fetchCards(activeSession.accessToken!!)
                .flatMap { cards ->
                    Observable.just(cards.filter { card -> card.statusId == CardStatus.ACTIVATED.ordinal })
                }
                .subscribe({
                    cards ->
                    view?.setProgressVisibility(false)
                    calculateSum(receipt.amount)
                    view?.fillData(activeSession.user, cards)
                }, {
                    error ->
                    view?.setProgressVisibility(false)
                    view?.showMessage(error.message!!)
                })
    }

    override fun onChooseCardClick() {
          view?.showMessage("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChooseBankClick() {
          view?.showMessage("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChooseNotificationClick() {
          view?.showMessage("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConfirmClick() {
        view?.showMessage("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNextClick(receipt: Receipt, userCard: Card?, sum: String, email: String) {
        if (validateData(sum, email)) {
            view?.showConfirmDialog("Комиссия ${(sum.toDouble() / 10).toString().format(2)} р.(10 %)", "${(sum.toDouble() + sum.toDouble() / 10).toString().format(2)} р.")
        }
    }

    override fun onSumChange(payment: String) {
       try {
           calculateSum(payment.toDouble())
       } catch (e: Exception) {
           view?.showSumError(R.string.error_field_required)
       }
    }

    private fun calculateSum(sum: Double) {
        view?.fillAmountAndCommission("Комиссия 10% (${(sum / 10).toString().format(2)} р.)", "${(sum + sum / 10).toString().format(2)} р.")
    }

    fun validateData(sum: String, email: String): Boolean {
        try {
            sum.toDouble()
        } catch (e: Exception) {
            view?.showSumError(R.string.error_field_required)
            return false
        }
        if (email.isNullOrBlank()) {
            view?.showEmailError(R.string.error_field_required)
            return false
        }
        if (!email.isEmail()) {
            view?.showEmailError(R.string.error_invalid_email)
            return false
        }
        return true
    }
}