package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Receipt
import java.util.*
import javax.inject.Inject

class ValueTransferListPresenter @Inject constructor(listView: IValueTransferListView) : RxPresenter<IValueTransferListView>(listView), IValueTransferListPresenter {

    @Inject lateinit var activeSession: ActiveSession

    private val dataSet: MutableList<ReceiptsViewModel> = ArrayList()

    override fun onViewAttached() {
        super.onViewAttached()

        onSwipeToRefresh()
    }

    override fun onSwipeToRefresh() {
        dataSet.clear()

        activeSession.cachedReceipts?.forEach {
            addReceiptToDataSet(it)
        }

        view?.showData(dataSet)
    }

    override fun onItemClick(item: ReceiptsViewModel) {
    }

    override fun onTransferValueClick(receipt: Receipt) {
        view?.navigateToSendValues(receipt)
    }

    override fun onAddNewValueTransferClick() {
        view?.navigateToNewValueTransfer()
    }

    override fun onReceiptDeleted(receipt: Receipt) {
        val rnd = Random()
        if(rnd.nextBoolean()){
            view?.showMessage("Успешно удалено")
        } else {
            view?.showMessage("Ошибка при удалении")
            addReceiptToDataSet(receipt)
            view?.showData(dataSet)
        }
    }

    private fun addReceiptToDataSet(receipt: Receipt){
        dataSet.find { it.address == receipt.address }?.receipts?.add(receipt) ?: dataSet.add(
                ReceiptsViewModel(receipt.address, arrayListOf(receipt))
        )
    }
}
