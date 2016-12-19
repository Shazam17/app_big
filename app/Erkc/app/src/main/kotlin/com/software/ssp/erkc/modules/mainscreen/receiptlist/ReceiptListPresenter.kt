package com.software.ssp.erkc.modules.mainscreen.receiptlist

import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.OpenHistoryWithReceiptEvent
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.CardStatus
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class ReceiptListPresenter @Inject constructor(view: IReceiptListView) : RxPresenter<IReceiptListView>(view), IReceiptListPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var eventBus: Relay<Any, Any>

    override fun onViewAttached() {
        super.onViewAttached()
        showReceiptsList()
    }

    override fun onViewDetached() {
        super.onViewDetached()
        realmRepository.close()
    }

    override fun onSwipeToRefresh() {
        subscriptions += receiptsRepository.fetchReceipts()
                .concatMap {
                    receipts ->
                    realmRepository.saveReceiptsList(receipts)
                }
                .subscribe(
                        {
                            showReceiptsList()
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setLoadingVisible(false)
                        })
    }


    override fun onPayButtonClick(receipt: RealmReceipt) {
        view?.navigateToPayScreen(receipt)
    }

    override fun onTransferButtonClick(receipt: RealmReceipt) {
        view?.navigateToIPUInputScreen(receipt)
    }

    override fun onHistoryButtonClick(receipt: RealmReceipt) {
        eventBus.call(OpenHistoryWithReceiptEvent(receipt.barcode))
    }

    override fun onAutoPaymentButtonClick(receipt: RealmReceipt) {
        if (activeSession.isOfflineSession) {
            view?.showMessage(R.string.offline_mode_error)
            return
        }

        if (receipt.linkedCard != null) {
            view?.navigateToAutoPaymentSettingScreen(receipt.id)
            return
        }

        //check available activated cards for linking
        subscriptions += realmRepository.fetchCardsList()
                .subscribe(
                        {
                            cards ->
                            if (cards.count { it.statusId == CardStatus.ACTIVATED.ordinal } == 0) {
                                view?.showNoActivatedCardsDialog()
                            } else {
                                view?.navigateToAutoPaymentSettingScreen(receipt.id)
                            }
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    override fun onAddReceiptButtonClick() {
        if (activeSession.isOfflineSession) {
            view?.showMessage(R.string.offline_mode_error)
        } else {
            view?.navigateToAddReceiptScreen()
        }
    }

    override fun onReceiptDeleted(receipt: RealmReceipt) {
        subscriptions += receiptsRepository.deleteReceipt(receipt.id)
                .concatMap {
                    view?.receiptDeleted(receipt)
                    view?.showMessage(R.string.receipts_deleted)
                    realmRepository.removeReceipt(receipt)
                }
                .concatMap { realmRepository.fetchCurrentUser() }
                .subscribe(
                        {
                            currentUser ->
                            if (currentUser.receipts.count() == 0) {
                                view?.navigateToEmptyReceiptsList()
                            }
                        },
                        {
                            error ->
                            view?.receiptDidNotDeleted(receipt)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun showReceiptsList() {
        subscriptions += realmRepository.fetchReceiptsList()
                .subscribe(
                        {
                            receipts ->
                            view?.showData(receipts.map { ReceiptViewModel(it, false) })
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        })
    }
}
