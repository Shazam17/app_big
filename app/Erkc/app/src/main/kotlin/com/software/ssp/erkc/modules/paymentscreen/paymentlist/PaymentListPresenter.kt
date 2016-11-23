package com.software.ssp.erkc.modules.paymentscreen.paymentlist

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class PaymentListPresenter @Inject constructor(view: IPaymentListView) : RxPresenter<IPaymentListView>(view), IPaymentListPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewAttached() {
        super.onViewAttached()
        showReceiptsList()
    }

    override fun onViewDetached() {
        super.onViewDetached()
        realmRepository.close()
    }

    override fun onSwipeToRefresh() {
        subscriptions += receiptsRepository.fetchReceipts(activeSession.accessToken!!)
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
        view?.navigateToPayScreen(receipt.id)
    }

    override fun onAddReceiptButtonClick() {
        view?.navigateToAddReceiptScreen()
    }

    override fun onReceiptDeleted(receipt: RealmReceipt) {
        subscriptions += receiptsRepository.deleteReceipt(activeSession.accessToken!!, receipt.id)
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
