package com.software.ssp.erkc.modules.card.editcard

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class EditCardPresenter @Inject constructor(view: IEditCardView) : RxPresenter<IEditCardView>(view), IEditCardPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var cardsRepository: CardsRepository
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onSaveClick(card: Card, cardName: String) {
        view?.setPending(true)
        subscriptions += cardsRepository.updateCard(card.id, cardName)
                .concatMap {
                    cardsRepository.fetchCard(card.id)
                }
                .concatMap {
                    card ->
                    realmRepository.saveCard(card)
                }
                .subscribe({
                    view?.setPending(false)
                    view?.close()
                }, {
                    error ->
                    view?.setPending(false)
                    view?.showMessage(error.message!!)
                })
    }
}
