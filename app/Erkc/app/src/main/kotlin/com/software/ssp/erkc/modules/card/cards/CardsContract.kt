package com.software.ssp.erkc.modules.card.cards

import android.support.annotation.StringRes
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Card

/**
 * @author Alexander Popov on 28/10/2016.
 */
interface ICardsView : IListView<Card> {
    fun navigateToEditCard(card: Card)
    fun navigateToAddCard()
    fun navigateToHelp()
}

interface ICardsPresenter : IListPresenter<Card, ICardsView> {
    fun onEditClick(card: Card)
    fun onAddClick()
    fun onHelpClick()
    fun onByStatusClick(card: Card)
    fun onDeleteClick(card: Card)
}