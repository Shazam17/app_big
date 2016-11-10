package com.software.ssp.erkc.modules.card.addcard

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 01/11/2016.
 */
class AddCardPresenter @Inject constructor(view: IAddCardView) : RxPresenter<IAddCardView>(view), IAddCardPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var cardsRepo: CardsRepository

    override fun onNameConfirm(name: String) {
        subscriptions += cardsRepo
                .addCard(activeSession.accessToken!!, name)
                .concatMap {
                    card ->
                    cardsRepo.registrateCard(activeSession.accessToken!!, card!!.id)
                }
                .subscribe({
                    cardReg ->
                    view?.navigateToUrl(cardReg!!.url)
                }, {
                    error ->
                    view?.showMessage(error.message!!)
                    //уходим на список карточек.
                    // т.к. мы могли ее добавить,
                    // но не зарегистировать и она там будет доступна
                    view?.navigateToCards()
                })
    }

    override fun onBankConfirm() {
        view?.navigateToResults(true)
    }

    override fun onDoneClick() {
        view?.navigateToCards()
    }

}