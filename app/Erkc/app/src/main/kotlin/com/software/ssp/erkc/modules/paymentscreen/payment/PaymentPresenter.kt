package com.software.ssp.erkc.modules.paymentscreen.payment

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import com.software.ssp.erkc.data.rest.repositories.PaymentRepository
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
    @Inject lateinit var paymentRepository: PaymentRepository

    override fun onViewAttached(receipt: Receipt) {
        super.onViewAttached()
        if (activeSession.user != null) {
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
        } else {
            view?.fillData(null, emptyList())
        }
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

    override fun onConfirmClick(receipt: Receipt, card: Card?, sum: String) {
        view?.setProgressVisibility(true)
        val summ = sum.removeSuffix(" р.").toFloat()
        subscriptions += paymentRepository.init(activeSession.accessToken ?: activeSession.appToken!!, receipt.barcode, if (card != null) 0 else 1, summ)
                .subscribe({
                    response ->
                    view?.setProgressVisibility(false)
                    view?.navigateToResult(response.url)
                }, { error ->
                    view?.setProgressVisibility(false)
                    view?.showMessage(error.message!!)
                })
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