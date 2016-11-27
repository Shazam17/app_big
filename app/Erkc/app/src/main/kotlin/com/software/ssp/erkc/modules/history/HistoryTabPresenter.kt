package com.software.ssp.erkc.modules.history

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.CardStatus
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class HistoryTabPresenter @Inject constructor(view: IHistoryTabView) : RxPresenter<IHistoryTabView>(view), IHistoryTabPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }
}
