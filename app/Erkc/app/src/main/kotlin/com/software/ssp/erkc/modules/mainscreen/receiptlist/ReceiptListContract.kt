package com.software.ssp.erkc.modules.mainscreen.receiptlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.common.receipt.ReceiptSectionViewModel
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.rest.models.Receipt


interface IReceiptListView : IListView<ReceiptSectionViewModel> {
    fun navigateToAddReceiptScreen()
    fun navigateToIPUInputScreen(receipt: Receipt)
    fun navigateToPayScreen(receipt: Receipt)
    fun navigateToHistoryScreen(receipt: Receipt)
}

interface IReceiptListPresenter : IListPresenter<ReceiptSectionViewModel, IReceiptListView> {
    fun onPayButtonClick(receiptViewModel: ReceiptViewModel)
    fun onTransferButtonClick(receiptViewModel: ReceiptViewModel)
    fun onHistoryButtonClick(receiptViewModel: ReceiptViewModel)
    fun onReceiptDeleted(receiptViewModel: ReceiptViewModel)
    fun onAddReceiptButtonClick()
}
