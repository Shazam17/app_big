package com.software.ssp.erkc.modules.paymentscreen.paymentlist

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.common.receipt.ReceiptSectionViewModel
import com.software.ssp.erkc.common.receipt.ReceiptType
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import java.util.*
import javax.inject.Inject


class PaymentListPresenter @Inject constructor(view: IPaymentListView) : RxPresenter<IPaymentListView>(view), IPaymentListPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    private val dataSet: MutableList<ReceiptSectionViewModel> = ArrayList()

    override fun onViewAttached() {
        super.onViewAttached()
        onSwipeToRefresh()
    }

    override fun onItemClick(item: ReceiptSectionViewModel) {
    }

    override fun onSwipeToRefresh() {
        dataSet.clear()

        val rnd = Random()

        activeSession.cachedReceipts?.forEach {
            //TODO API CALLs

            addReceiptToDataSet(
                    ReceiptViewModel(
                            null,
                            it.serviceName,
                            it.address,
                            it.barcode,
                            it.amount,
                            if(rnd.nextBoolean()) ReceiptType.POWER else ReceiptType.WATER,
                            rnd.nextBoolean(),
                            rnd.nextBoolean(),
                            "1.1.1",
                            "2.2.2"))
        }

        view?.showData(dataSet)
    }

    override fun onPayButtonClick(receiptViewModel: ReceiptViewModel) {
        val receipt = activeSession.cachedReceipts?.find { it.barcode == receiptViewModel.barcode }
        view?.navigateToPayScreen(receipt!!)
    }

    override fun onAddReceiptButtonClick() {
        view?.navigateToAddReceiptScreen()
    }

    override fun onReceiptDeleted(receiptViewModel: ReceiptViewModel) {
        //TODO DELETE API CALL
    }

    private fun addReceiptToDataSet(receipt: ReceiptViewModel){
        dataSet.find { it.address == receipt.address }?.receipts?.add(receipt) ?:
                dataSet.add(ReceiptSectionViewModel(receipt.address, arrayListOf(receipt)))
    }
}