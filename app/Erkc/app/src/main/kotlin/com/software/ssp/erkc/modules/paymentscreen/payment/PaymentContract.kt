package com.software.ssp.erkc.modules.paymentscreen.payment

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.models.User

/**
 * @author Alexander Popov on 10/11/2016.
 */
interface IPaymentView : IView {
    fun close()
    fun navigateToResult(url: String)
    fun showConfirmDialog(commission: Double, amount: Double, email: String)
    fun fillAmountAndCommission(commission: Double, sum: Double)
    fun fillData(user: User?, cards: List<Card>)
    fun showSumError(errorRes: Int)
    fun showEmailError(errorRes: Int)
    fun setProgressVisibility(isVisible: Boolean)
    fun showResult(result: Boolean)
    fun generateCardsChooseLayout(cards: List<Card>)
    fun showReceiptInfo(receipt: Receipt)
}

interface IPaymentPresenter : IPresenter<IPaymentView> {
    fun onChooseCardClick(cards: List<Card>)
    fun onConfirmClick(receipt: Receipt, card: Card?, sum: String, email: String)
    fun onNextClick(receipt: Receipt, userCard: Card?, sum: String, email: String)
    fun onSumChange(payment: String, percent: Double)
    fun onViewAttached(receipt: Receipt)
    fun onAddCardClick()
    fun onDoneClick()
}

