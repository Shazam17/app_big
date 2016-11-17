package com.software.ssp.erkc.modules.confirmbyurl

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 01/11/2016.
 */
class ConfirmByUrlPresenter @Inject constructor(view: IConfirmByUrlView) : RxPresenter<IConfirmByUrlView>(view), IConfirmByUrlPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var cardsRepo: CardsRepository

    override fun onBankConfirm() {
        view?.navigateToResults()
    }

    override fun onDoneClick() {
        view?.navigateToCards()
    }

}