package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.common.receipt.ReceiptSectionViewModel
import com.software.ssp.erkc.common.receipt.ReceiptType
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.rest.ActiveSession
import java.util.*
import javax.inject.Inject

class ValueTransferListPresenter @Inject constructor(view: IValueTransferListView) : RxPresenter<IValueTransferListView>(view), IValueTransferListPresenter {

    @Inject lateinit var activeSession: ActiveSession

    private val dataSet: MutableList<ReceiptSectionViewModel> = ArrayList()

    override fun onViewAttached() {
        super.onViewAttached()

        onSwipeToRefresh()
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

    override fun onItemClick(item: ReceiptSectionViewModel) {
    }

    override fun onTransferValueClick(receiptViewModel: ReceiptViewModel) {
        val receipt = activeSession.cachedReceipts?.find { it.barcode == receiptViewModel.barcode }
        view?.navigateToSendValues(receipt!!)
    }

    override fun onAddNewValueTransferClick() {
        view?.navigateToNewValueTransfer()
    }

    override fun onReceiptDeleted(receiptViewModel: ReceiptViewModel) {
        //TODO DELETE API CALL
    }

    private fun addReceiptToDataSet(receipt: ReceiptViewModel){
        dataSet.find { it.address == receipt.address }?.receipts?.add(receipt) ?:
                dataSet.add(ReceiptSectionViewModel(receipt.address, arrayListOf(receipt)))
    }
}
