package com.software.ssp.erkc.modules.mainscreen

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.*
import com.software.ssp.erkc.extensions.parsedMessage
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class MainScreenPresenter @javax.inject.Inject constructor(view: IMainScreenView) : RxPresenter<IMainScreenView>(view), IMainScreenPresenter {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    var nonAuthImitation = false

    override fun setNonAuthImitation() {
        nonAuthImitation = true
    }

    override fun onViewAttached() {
        if(activeSession.isOfflineSession) {
            view?.showReceiptListScreen()
            return
        }

        if (!authRepository.getLocalTokenApi().isEmpty() && activeSession.accessToken.isNullOrEmpty())
            activeSession.accessToken = authRepository.getLocalTokenApi()

        if(activeSession.accessToken.isNullOrEmpty() || nonAuthImitation){
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
