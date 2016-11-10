package com.software.ssp.erkc.modules.paymentscreen.payment

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Card

/**
 * @author Alexander Popov on 10/11/2016.
 */
interface IPaymentView : IView {
    fun setLoadingVisible(isVisible: Boolean)
    fun navigateToDrawer()
    fun navigateToResult()
    fun showConfirmDialog()
    fun showActivatedCards(cards : List<Card>)
    fun showNotificationsDialog()
}

interface IPaymentPresenter : IPresenter<IPaymentView> {
    fun validateData()
    fun onChooseCardClick()
    fun onChooseBankClick()
    fun onChooseNotificationClick()
    fun onConfirmClick()
    fun onNextClick()
}