package com.software.ssp.erkc.modules.autopayments

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IAutoPaymentsTabView : IView {
    fun navigateToNewAutoPayment()
}

interface IAutoPaymentsTabPresenter : IPresenter<IAutoPaymentsTabView> {
    fun onAddNewAutoPaymentClick()
}
