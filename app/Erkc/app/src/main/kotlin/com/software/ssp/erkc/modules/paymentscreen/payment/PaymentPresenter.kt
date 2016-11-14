package com.software.ssp.erkc.modules.paymentscreen.payment

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.CardStatus
import rx.Observable
import rx.functions.Func1
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
                    Observable.just(cards.filter { card -> card.statusId.equals(CardStatus.ACTIVATED.ordinal) })
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

    override fun validateData() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChooseCardClick() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChooseBankClick() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChooseNotificationClick() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConfirmClick() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNextClick() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSumChange(payment: Double) {
       calculateSum(payment)
    }

    private fun calculateSum(sum: Double) {
        view?.fillAmountAndCommission("Комиссия 10% (${(sum / 10).toString().format(2)} р.)", "${(sum + sum / 10).toString().format(2)} р.")
    }

}