package com.software.ssp.erkc.modules.card.editcard

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Card

/**
 * @author Alexander Popov on 08/11/2016.
 */
interface IEditCardView : IView {
    fun navigateToDrawer()
    fun setLoadingVisible(isVisible: Boolean)
}

interface IEditCardPresenter : IPresenter<IEditCardView> {
    fun onSaveClick(card: Card, cardName: String)
}