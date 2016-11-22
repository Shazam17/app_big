package com.software.ssp.erkc.modules.mainscreen.receiptlist

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class ReceiptListPresenter @Inject constructor(view: IReceiptListView) : RxPresenter<IReceiptListView>(view), IReceiptListPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached() {
        super.onViewAttached()
        showReceipts(activeSession.cachedReceipts!!)
    }

    override fun onSwipeToRefresh() {
        subscriptions += receiptsRepository.fetchReceipts(activeSession.accessToken!!)
                .subscribe(
                        {
                            receipts ->
                            activeSession.cachedReceipts = receipts?.sortedBy { it.address }
                            showReceipts(activeSession.cachedReceipts!!)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setLoadingVisible(false)
                        })
    }

    override fun onPayButtonClick(receipt: Receipt) {
        view?.navigateToPayScreen(receipt)
    }

    override fun onTransferButtonClick(receipt: Receipt) {
        view?.navigateToIPUInputScreen(receipt)
    }

    override fun onHistoryButtonClick(receipt: Receipt) {
        view?.navigateToHistoryScreen(receipt)
    }

    override fun onAutoPaymentButtonClick(receipt: Receipt) {
        view?.navigateToAutoPaymentSettingScreen(receipt)
    }

    override fun onAddReceiptButtonClick() {
        view?.navigateToAddReceiptScreen()
    }

    override fun onReceiptDeleted(receipt: Receipt) {
        subscriptions += receiptsRepository.deleteReceipt(activeSession.accessToken!!, receipt.id)
                .concatMap {
                    view?.receiptDeleted(receipt)
                    view?.showMessage(R.string.receipts_deleted)
                    receiptsRepository.fetchReceipts(activeSession.accessToken!!)
                }
                .subscribe(
                        {
                            receipts ->
                            if(receipts == null || receipts.count() == 0){
                                activeSession.cachedReceipts = null
                                view?.navigateToEmptyReceiptsList()
                            } else {
                                activeSession.cachedReceipts = receipts.sortedBy { it.address }
                            }
                        },
                        {
                            error ->
                            view?.receiptDidNotDeleted(receipt)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun showReceipts(receipts: List<Receipt>) {
        view?.showData(receipts.map { ReceiptViewModel(it, false) })
    }
}
