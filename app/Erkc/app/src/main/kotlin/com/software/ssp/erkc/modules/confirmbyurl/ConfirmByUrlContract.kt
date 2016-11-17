package com.software.ssp.erkc.modules.confirmbyurl

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

/**
 * @author Alexander Popov on 01/11/2016.
 */
interface IConfirmByUrlView : IView {
    fun navigateToResults()
    fun navigateToCards()
}

interface IConfirmByUrlPresenter : IPresenter<IConfirmByUrlView> {
    fun onBankConfirm()
    fun onDoneClick()
}