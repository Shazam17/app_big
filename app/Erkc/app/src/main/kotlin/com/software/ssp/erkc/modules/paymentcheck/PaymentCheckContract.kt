package com.software.ssp.erkc.modules.paymentcheck

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import java.io.File

/**
 * @author Alexander Popov on 30/11/2016.
 */
interface IPaymentCheckView : IView {
    fun setLoadingVisible(isVisible: Boolean)
    fun showCheck(file: File)
    fun showShareDialog(file: File)
    fun saveFile(file: File)
}

interface IPaymentCheckPresenter : IPresenter<IPaymentCheckView> {
    fun onViewAttached(id: String)
    fun onShareClick(file: File?)
    fun onDownloadClick(file: File?)
}