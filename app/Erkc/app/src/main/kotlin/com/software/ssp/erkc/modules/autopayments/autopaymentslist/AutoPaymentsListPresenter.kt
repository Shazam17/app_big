package com.software.ssp.erkc.modules.autopayments.autopaymentslist

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class AutoPaymentsListPresenter @Inject constructor(view: IAutoPaymentsListView) : RxPresenter<IAutoPaymentsListView>(view), IAutoPaymentsListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var receiptsRepository: ReceiptsRepository

    override fun onViewAttached() {
        onSwipeToRefresh()
    }

    override fun onSwipeToRefresh() {
        view?.showData(activeSession.cachedReceipts?.filter { it.autoPayMode == 0 } ?: emptyList())
    }

    override fun onItemClick(item: Receipt) {
    }

    override fun onDeleteButtonClick(receipt: Receipt) {
        view?.showConfirmDeleteDialog(receipt)
    }

    override fun onEditButtonClick(receipt: Receipt) {
        view?.navigateToEditAutoPayment(receipt)
    }

    override fun onConfirmDelete(receipt: Receipt) {
        subscriptions += receiptsRepository.updateReceipt(activeSession.accessToken!!, receipt.id!!)
                .subscribe(
                        {
                            view?.autoPaymentDeleted(receipt)
                            //TODO update list
                        },
                        {
                            error ->
                            view?.autoPaymentDidNotDeleted(receipt)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }
}
