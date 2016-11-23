package com.software.ssp.erkc.modules.mainscreen

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class MainScreenPresenter @javax.inject.Inject constructor(view: IMainScreenView) : RxPresenter<IMainScreenView>(view), IMainScreenPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewAttached() {
        if(activeSession.accessToken == null){
            view?.showNonAuthedScreen()
            return
        }

        subscriptions += realmRepository.fetchReceiptsList()
                .subscribe(
                        {
                            receipts ->
                            when {
                                receipts == null || receipts.count() == 0 -> view?.showAddReceiptScreen()
                                else -> view?.showReceiptListScreen()
                            }
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        })
    }

    override fun onViewDetached() {
        super.onViewDetached()
        realmRepository.close()
    }
}
