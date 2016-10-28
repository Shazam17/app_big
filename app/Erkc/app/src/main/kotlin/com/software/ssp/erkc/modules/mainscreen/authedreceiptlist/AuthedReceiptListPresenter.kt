package com.software.ssp.erkc.modules.mainscreen.authedreceiptlist

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import javax.inject.Inject


class AuthedReceiptListPresenter @Inject constructor(view: IAuthedReceiptListView) : RxPresenter<IAuthedReceiptListView>(view), IAuthedReceiptListPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onPayButtonClick() {
        // todo
    }

    override fun onIPUButtonClick() {
        // todo
    }
}