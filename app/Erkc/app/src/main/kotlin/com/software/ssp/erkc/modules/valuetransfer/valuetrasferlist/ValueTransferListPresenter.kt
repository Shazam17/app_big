package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class ValueTransferListPresenter @Inject constructor(view: IValueTransferListView) : RxPresenter<IValueTransferListView>(view), IValueTransferListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var receiptsRepository: ReceiptsRepository

    override fun onViewAttached() {
        super.onViewAttached()
        view?.showData(activeSession.cachedReceipts!!)
    }

    override fun onSwipeToRefresh() {
        subscriptions += receiptsRepository.fetchReceipts(activeSession.accessToken!!)
                .subscribe(
                        {
                            receipts ->
                            activeSession.cachedReceipts = receipts?.sortedBy { it.address }
                            view?.showData(activeSession.cachedReceipts!!)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setLoadingVisible(false)
                        })
    }

    override fun onItemClick(item: Receipt) {
    }

    override fun onTransferValueClick(receipt: Receipt) {
        view?.navigateToSendValues(receipt)
    }

    override fun onAddNewValueTransferClick() {
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
}
