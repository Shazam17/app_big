package com.software.ssp.erkc.modules.paymentsinfo

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmPayment
import com.software.ssp.erkc.data.realm.models.RealmPaymentInfo

/**
 * @author Alexander Popov on 01/12/2016.
 */
interface IPaymentInfoView : IView {
    fun close()
    fun fillData(paymentInfo: RealmPaymentInfo, payment: RealmPayment)
    fun setProgressVisibility(isVisible: Boolean)
    fun navigateToCheck()
    fun navigateToRetryPayment(paymentId: String)
}

interface IPaymentInfoPresenter : IPresenter<IPaymentInfoView> {
    var paymentId: String

    fun onRetryClick()
    fun onGetCheckClick()
}