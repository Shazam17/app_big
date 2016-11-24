package com.software.ssp.erkc.modules.card.cards

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.rest.models.Card

/**
 * @author Alexander Popov on 28/10/2016.
 */
interface ICardsView : IListView<CardViewModel> {
    fun setCardPending(card: RealmCard, isPending: Boolean)

    fun navigateToEditCard(card: Card)
    fun navigateToAddCard()
    fun navigateToHelp()
    fun navigateToBankSite(url: String)

    fun cardDidNotDeleted(card: RealmCard)
    fun cardDeleted(card: RealmCard)
}

interface ICardsPresenter : IListPresenter<CardViewModel, ICardsView> {
    fun onAddClick()
    fun onHelpClick()
    fun onEditClick(card: RealmCard)
    fun onStatusClick(card: RealmCard)
    fun onDeleteClick(card: RealmCard)
}
