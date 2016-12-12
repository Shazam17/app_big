package com.software.ssp.erkc.modules.paymentcheck

import android.net.Uri
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

/**
 * @author Alexander Popov on 30/11/2016.
 */
interface IPaymentCheckView : IView {
    fun setLoadingVisible(isVisible: Boolean)
    fun showCheck(uri: Uri)
    fun showShareDialog(uri: Uri)
}

interface IPaymentCheckPresenter : IPresenter<IPaymentCheckView> {
    fun onViewAttached(id: String)
    fun onShareClick()
    fun onDownloadClick(paymentId: String)
}