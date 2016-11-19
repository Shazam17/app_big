package com.software.ssp.erkc.modules.card.addcard

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

/**
 * @author Alexander Popov on 01/11/2016.
 */
interface IAddCardView : IView {
    fun navigateToUrl(url: String)
    fun navigateToCards()
}

interface IAddCardPresenter : IPresenter<IAddCardView> {
    fun onNameConfirm(name: String)
}