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
    fun showConfirmDialog(commission: String, amount: String, email: String)
    fun showNotificationsDialog()
    fun fillAmountAndCommission(commission: String, sum: String)
    fun fillData(user: User?, cards: List<Card>)
    fun showSumError(errorRes: Int)
    fun showEmailError(errorRes: Int)
    fun setProgressVisibility(isVisible: Boolean)
    fun showResult(result: Boolean, textRes: Int)
}

interface IPaymentPresenter : IPresenter<IPaymentView> {
    fun onChooseCardClick()
    fun onChooseBankClick()
    fun onChooseNotificationClick()
    fun onConfirmClick(receipt: Receipt, card: Card?, sum: String, email: String)
    fun onNextClick(receipt: Receipt, userCard: Card?, sum: String, email: String)
    fun onSumChange(payment: String)
    fun onViewAttached(receipt: Receipt)
    fun onPaymentResult(result: Boolean)
}
