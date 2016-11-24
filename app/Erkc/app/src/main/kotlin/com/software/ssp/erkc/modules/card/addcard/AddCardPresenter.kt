package com.software.ssp.erkc.modules.card.addcard

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class AddCardPresenter @Inject constructor(view: IAddCardView) : RxPresenter<IAddCardView>(view), IAddCardPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var cardsRepository: CardsRepository
    @Inject lateinit var realmRepository: RealmRepository

    private var cardId: String = ""

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onCreateCardClick(name: String) {
        view?.setPending(true)
        subscriptions += cardsRepository
                .addCard(activeSession.accessToken!!, name)
                .concatMap {
                    card ->
                    cardsRepository.fetchCard(activeSession.accessToken!!, card!!.id)
                }
                .concatMap {
                    card ->
                    cardId = card.id
                    realmRepository.saveCard(card)
                }
                .concatMap {
                    cardsRepository.registrateCard(activeSession.accessToken!!, cardId)
                }
                .subscribe({
                    cardReg ->
                    view?.setPending(false)
                    view?.navigateToUrl(cardReg!!.url)
                }, {
                    error ->
                    view?.setPending(false)
                    view?.showMessage(error.parsedMessage())
                    view?.close()
                })
    }
}
