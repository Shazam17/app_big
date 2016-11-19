package com.software.ssp.erkc.modules.autopayments

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import javax.inject.Inject


class AutoPaymentsTabPresenter @Inject constructor(view: IAutoPaymentsTabView) : RxPresenter<IAutoPaymentsTabView>(view), IAutoPaymentsTabPresenter {

    @Inject lateinit var activeSession: ActiveSession

    override fun onAddNewAutoPaymentClick() {
        /*TODO check
        1) Receipts with mode == 0
        2) User has linked cards
        show message if error
         */
        view?.navigateToNewAutoPayment()
    }
}
