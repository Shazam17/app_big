package com.software.ssp.erkc.modules.card.cards

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import com.software.ssp.erkc.extensions.CardStatus
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 28/10/2016.
 */
class CardsPresenter @Inject constructor(view: ICardsView) : RxPresenter<ICardsView>(view), ICardsPresenter {
    @Inject lateinit var cardsRepository: CardsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached() {
        super.onViewAttached()
        onSwipeToRefresh()
    }

    override fun onEditClick(card: Card) {
        view?.navigateToEditCard(card)
    }

    override fun onAddClick() {
        view?.navigateToAddCard()
    }

    override fun onHelpClick() {
        view?.navigateToHelp()
    }

    override fun onByStatusClick(card: Card) {
        when (card.statusId) {
            CardStatus.NOT_REGISTERED.ordinal -> {
                view?.setProgressVisibility(true)
                subscriptions += cardsRepository
                        .registrateCard(activeSession.accessToken!!, card.id)
                        .subscribe({
                            response ->
                            view?.setProgressVisibility(false)
                            view?.navigateToBankSite(response.url)
                        }, {
                            error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.message!!)
                        })
            }
            CardStatus.REGISTERED.ordinal -> {
                view?.setProgressVisibility(true)
                subscriptions += cardsRepository
                        .activateCard(activeSession.accessToken!!, card.id)
                        .subscribe({
                            response ->
                            view?.setProgressVisibility(false)
                            view?.navigateToBankSite(response.url)
                        }, {
                            error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.message!!)
                        })
            }
        }
    }

    override fun onDeleteClick(card: Card) {
        view?.setLoadingVisible(true)
        subscriptions += cardsRepository
                .deleteCard(activeSession.accessToken!!, card.id)
                .flatMap {
                    cardsRepository.fetchCards(activeSession.accessToken!!)
                }
                .subscribe({
                    cards ->
                    view?.setLoadingVisible(false)
                    view?.showData(cards)
                }, {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.message!!)
                })
    }


    override fun onSwipeToRefresh() {
        subscriptions += cardsRepository
                .fetchCards(activeSession.accessToken!!)
                .subscribe({
                    cards ->
                    view?.setLoadingVisible(false)
                    view?.showData(cards ?: emptyList())
                }, {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.message!!)
                })
    }

    override fun onItemClick(item: Card) {
    }


}