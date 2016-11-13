package com.software.ssp.erkc.modules.mainscreen.receiptlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.rest.models.Receipt


interface IReceiptListView : IListView<Receipt> {
    fun navigateToAddReceiptScreen()
    fun navigateToEmptyReceiptsList()
    fun navigateToIPUInputScreen(receipt: Receipt)
    fun navigateToPayScreen(receipt: Receipt)
    fun navigateToHistoryScreen(receipt: Receipt)
    fun receiptDidNotDeleted(receipt: Receipt)
    fun receiptDeleted(receipt: Receipt)
}

interface IReceiptListPresenter : IListPresenter<Receipt, IReceiptListView> {
    fun onPayButtonClick(receipt: Receipt)
    fun onTransferButtonClick(receipt: Receipt)
    fun onHistoryButtonClick(receipt: Receipt)
    fun onReceiptDeleted(receipt: Receipt)
    fun onAddReceiptButtonClick()
}
