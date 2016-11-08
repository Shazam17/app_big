package com.software.ssp.erkc.modules.card.editcard

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 08/11/2016.
 */
class EditCardPresenter @Inject constructor(view: IEditCardView) : RxPresenter<IEditCardView>(view), IEditCardPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var cardsRepo: CardsRepository

    override fun onSaveClick(card: Card, cardName: String) {
        view?.setLoadingVisible(true)
        subscriptions += cardsRepo.updateCard(activeSession.accessToken!!, card.id, cardName)
                .subscribe({
                    response ->
                    view?.setLoadingVisible(false)
                    view?.navigateToDrawer()
                }, {
                    error ->
                    view?.showMessage(error.message!!)
                })
    }

}