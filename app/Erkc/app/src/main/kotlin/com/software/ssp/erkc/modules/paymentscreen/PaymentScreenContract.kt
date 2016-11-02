package com.software.ssp.erkc.modules.paymentscreen

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IPaymentScreenView: IView {
    fun navigateToAddReceiptScreen()
    fun navigateToPaymentsList()
}

interface IPaymentScreenPresenter: IPresenter<IPaymentScreenView> {

}