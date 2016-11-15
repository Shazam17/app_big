package com.software.ssp.erkc.modules.card.changestatus

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

/**
 * @author Alexander Popov on 01/11/2016.
 */
interface IChangeStatusCardView : IView {
    fun navigateToResults()
    fun navigateToCards()
}

interface IChangeStatusCardPresenter : IPresenter<IChangeStatusCardView> {
    fun onBankConfirm()
    fun onDoneClick()
}