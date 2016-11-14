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
    fun navigateToDrawer()
    fun navigateToResult()
    fun showConfirmDialog()
    fun showActivatedCards(cards : List<Card>)
    fun showNotificationsDialog()
    fun fillAmountAndCommission(commission: String, sum: String)
    fun fillData(user: User?, cards: List<Card>)
}

interface IPaymentPresenter : IPresenter<IPaymentView> {
    fun validateData()
    fun onChooseCardClick()
    fun onChooseBankClick()
    fun onChooseNotificationClick()
    fun onConfirmClick()
    fun onNextClick()
    fun onSumChange(payment: Double)
    fun onViewAttached(receipt: Receipt)
}