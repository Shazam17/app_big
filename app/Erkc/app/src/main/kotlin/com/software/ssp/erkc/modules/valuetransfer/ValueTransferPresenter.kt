package com.software.ssp.erkc.modules.valuetransfer

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.modules.mainscreen.authedreceiptlist.getMockReceiptList
import javax.inject.Inject

class ValueTransferPresenter @Inject constructor(view: IValueTransferView) : RxPresenter<IValueTransferView>(view), IValueTransferPresenter {

    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached() {

        //TODO REMOVE next
        activeSession.cachedReceipts = getMockReceiptList().sortedBy { it.address }

        when {
            activeSession.cachedReceipts != null -> view?.navigateToValueTransferListScreen()
            else -> view?.navigateToNewValueTransferScreen()
        }
    }
}
