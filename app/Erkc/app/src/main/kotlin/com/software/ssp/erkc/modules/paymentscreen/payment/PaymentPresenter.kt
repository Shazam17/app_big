package com.software.ssp.erkc.modules.paymentscreen.payment

import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ApiException
import com.software.ssp.erkc.common.OpenCardsEvent
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.ApiErrorType
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.PaymentMethod
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
    @Inject lateinit var eventBus: Relay<Any, Any>

    override fun onViewAttached(receipt: Receipt) {
        super.onViewAttached()
        view?.showReceiptInfo(receipt)
        if (activeSession.user != null) {
            view?.setProgressVisibility(true)
            subscriptions += cardsRepository
                    .fetchCards()
                    .flatMap { cards ->
                        if (cards != null) {
                            Observable.just(cards.filter { card -> card.statusId == CardStatus.ACTIVATED.ordinal })
                        } else {
                            Observable.just(emptyList())
                        }
                    }
                    .subscribe({
                        cards ->
                        view?.setProgressVisibility(false)
                        calculateSum(receipt.amount, receipt.persent)
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

    override fun onChooseCardClick(cards: List<Card>) {
        view?.generateCardsChooseLayout(cards)
    }

    override fun onConfirmClick(receipt: Receipt, card: Card?, sum: String, email: String) {
        view?.setProgressVisibility(true)
        val amount = sum.removeSuffix(" р.")
        val method = if (card == null) PaymentMethod.DEFAULT.ordinal else PaymentMethod.ONE_CLICK.ordinal

        subscriptions += paymentRepository.init(
                receipt.barcode,
                method,
                amount,
                email,
                card?.id)
                .subscribe({
                    response ->
                    view?.setProgressVisibility(false)
                    if (card == null) {
                        view?.navigateToResult(response.url)
                    } else {
                        view?.showResult(true)
                    }
                }, { error ->
                    if (error is ApiException && error.errorCode == ApiErrorType.PAYMENT_ERROR) {
                        view?.showResult(false)
                    } else {
                        view?.showMessage(error.message!!)
                    }
                    view?.setProgressVisibility(false)
                })
    }

    override fun onNextClick(receipt: Receipt, userCard: Card?, sum: String, email: String) {
        if (validateData(sum, email)) {
            if (userCard != null) {
                view?.showConfirmDialog(sum.toDouble() * receipt.persent / 100,
                        sum.toDouble() + sum.toDouble() * receipt.persent / 100,
                        email)
            } else {
                view?.setProgressVisibility(true)
                val amount = "%.2f".format(sum.toDouble() + sum.toDouble() * receipt.persent / 100)
                subscriptions += paymentRepository.init(
                        receipt.barcode,
                        PaymentMethod.DEFAULT.ordinal,
                        amount.replace(',','.'),
                        email,
                        null)
                        .subscribe({
                            response ->
                            view?.setProgressVisibility(false)
                            view?.navigateToResult(response.url)
                        }, { error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.message!!)
                        })
            }
        }
    }

    override fun onSumChange(payment: String, percent: Double) {
        try {
            calculateSum(payment.toDouble(), percent)
        } catch (e: Exception) {
            view?.showSumError(R.string.error_field_required)
        }
    }

    override fun onAddCardClick() {
        eventBus.call(OpenCardsEvent())
        view?.close()
    }

    override fun onDoneClick() {
        view?.close()
    }

    private fun calculateSum(sum: Double, percent: Double) {
        val commission = sum * percent / 100
        view?.fillAmountAndCommission(commission, sum + commission)
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
