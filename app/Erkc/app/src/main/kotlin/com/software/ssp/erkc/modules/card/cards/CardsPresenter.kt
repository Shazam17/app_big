package com.software.ssp.erkc.modules.card.cards

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.CardStatus
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.extensions.toCard
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 28/10/2016.
 */
class CardsPresenter @Inject constructor(view: ICardsView) : RxPresenter<ICardsView>(view), ICardsPresenter {

    @Inject lateinit var cardsRepository: CardsRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    private var isCardRequestPending = false

    override fun onViewAttached() {
        super.onViewAttached()
        showCards()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onEditClick(card: RealmCard) {
        if (activeSession.isOfflineSession) {
            view?.showMessage(R.string.offline_mode_error)
        } else {
            view?.navigateToEditCard(card.toCard())
        }
    }

    override fun onAddClick() {
        if (activeSession.isOfflineSession) {
            view?.showMessage(R.string.offline_mode_error)
        } else {
            view?.navigateToAddCard()
        }
    }

    override fun onHelpClick() {
        view?.navigateToHelp()
    }

    override fun onStatusClick(card: RealmCard) {
        if (isCardRequestPending) return

        isCardRequestPending = true
        view?.setCardPending(card, true)

        when (CardStatus.values()[card.statusId]) {
            CardStatus.NOT_REGISTERED -> {
                subscriptions += cardsRepository
                        .registerCard(card.id)
                        .subscribe({
                            response ->
                            isCardRequestPending = false
                            view?.setCardPending(card, false)
                            view?.navigateToBankSite(response.url)
                        }, {
                            error ->
                            isCardRequestPending = false
                            view?.setCardPending(card, false)
                            view?.showMessage(error.parsedMessage())
                        })
            }
            CardStatus.REGISTERED -> {
                subscriptions += cardsRepository
                        .activateCard(card.id)
                        .subscribe({
                            response ->
                            isCardRequestPending = false
                            view?.setCardPending(card, false)
                            view?.navigateToBankSite(response.url)
                        }, {
                            error ->
                            isCardRequestPending = false
                            view?.setCardPending(card, false)
                            view?.showMessage(error.parsedMessage())
                        })
            }
            else -> {
                isCardRequestPending = false
                view?.setCardPending(card, false)
            }
        }
    }

    override fun onDeleteClick(card: RealmCard) {
        subscriptions += cardsRepository
                .deleteCard(card.id)
                .concatMap {
                    realmRepository.removeCard(card)
                }
                .subscribe({
                    view?.cardDeleted(card)
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                    view?.cardDidNotDeleted(card)
                })
    }

    override fun onSwipeToRefresh() {
        subscriptions += cardsRepository
                .fetchCards()
                .concatMap {
                    cards ->
                    realmRepository.saveCardsList(cards ?: emptyList())
                }
                .subscribe({
                    cards ->
                    showCards()
                }, {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.parsedMessage())
                })
    }

    private fun showCards() {
        subscriptions += realmRepository
                .fetchCardsList()
                .subscribe(
                        {
                            cards ->
                            view?.showData(cards.map { CardViewModel(it) })
                        },
                        {
                            error ->
                            view?.setLoadingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        })
    }
}
