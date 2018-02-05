package com.software.ssp.erkc.modules.paymentscreen.payment

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.models.Receipt

/**
 * @author Alexander Popov on 10/11/2016.
 */
interface IPaymentView : IView {

    fun setProgressVisibility(isVisible: Boolean)
    fun showReceiptInfo(receipt: Receipt)
    fun showReceiptInfo(receipt: RealmReceipt)
    fun showSelectedCard(card: RealmCard?)
    fun setCommissionCheck(checked: Boolean)
    fun setCommissionCheckVisibility(isVisible: Boolean)
    fun showEmail(email: String)
    fun showSumError(errorRes: Int)
    fun showEmailError(errorRes: Int)
    fun showResult(result: Boolean)
    fun fillAmountAndCommission(commission: Double, sum: Double)
    fun showPaymentConfirmDialog(receipt: RealmReceipt, card: RealmCard, commission: Double, sum: Double, email: String)
    fun showCardSelectDialog(cardsViewModels: List<PaymentCardViewModel>)
    fun showNavigateToCardsDialog()
    fun showPaymentSum(sum: Double)
    fun navigateToResult(url: String)
    fun close()
}

interface IPaymentPresenter : IPresenter<IPaymentView> {

    var receipt: Receipt
    var receiptId: String?
    var paymentId: String?
    var fromTransaction: Boolean

    fun onChooseCardClick()
    fun onNextClick(email: String)
    fun onPaymentConfirmClick(email: String)

    fun onSumTextChange(sum: String)

    fun onDoneClick()

    fun onCardSelected(card: RealmCard?)

    fun onNavigateToCardsConfirmClick()
}

