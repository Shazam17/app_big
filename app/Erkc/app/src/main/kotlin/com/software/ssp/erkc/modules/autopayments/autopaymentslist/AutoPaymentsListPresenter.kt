package com.software.ssp.erkc.modules.autopayments.autopaymentslist

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class AutoPaymentsListPresenter @Inject constructor(view: IAutoPaymentsListView) : RxPresenter<IAutoPaymentsListView>(view), IAutoPaymentsListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var realmRepository: RealmRepository

    override var autoPaymentMode: Int = 0

    override fun onViewAttached() {
        showReceiptsList()
    }

    override fun onSwipeToRefresh() {
        subscriptions += receiptsRepository.fetchReceipts()
                .concatMap {
                    receipts ->
                    realmRepository.saveReceiptsList(receipts ?: emptyList())
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

    override fun onDeleteButtonClick(receipt: RealmReceipt) {
        view?.showConfirmDeleteDialog(receipt)
    }

    override fun onEditButtonClick(receipt: RealmReceipt) {
        view?.navigateToEditAutoPayment(receipt)
    }

    override fun onConfirmDelete(receipt: RealmReceipt) {
        subscriptions += receiptsRepository.clearReceiptSettings(receipt.id)
                .concatMap {
                    view?.autoPaymentDeleted(receipt)
                    receiptsRepository.fetchReceiptInfo(receipt.barcode)
                }
                .concatMap {
                    receipt ->
                    realmRepository.saveReceipt(receipt)
                }
                .subscribe(
                        { },
                        {
                            error ->
                            view?.autoPaymentDidNotDeleted(receipt)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun showReceiptsList() {
        subscriptions += realmRepository.fetchReceiptsList()
                .subscribe(
                        {
                            receipts ->
                            view?.showData(receipts.filter { it.autoPayMode == autoPaymentMode }.map { ReceiptViewModel(it) })
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        })
    }
}
