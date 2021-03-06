package com.software.ssp.erkc.modules.valuetransfer

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class ValueTransferPresenter @Inject constructor(view: IValueTransferView) : RxPresenter<IValueTransferView>(view), IValueTransferPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewAttached() {
        if(activeSession.isOfflineSession) {
            view?.navigateToValueTransferListScreen()
            return
        }

        if(activeSession.accessToken.isNullOrEmpty()){
            view?.navigateToAddReceiptScreen()
            return
        }

        subscriptions += realmRepository.fetchReceiptsList()
                .subscribe(
                        {
                            receipts ->
                            when {
                                receipts == null || receipts.count() == 0 -> view?.navigateToAddReceiptScreen()
                                else -> view?.navigateToValueTransferListScreen()
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
