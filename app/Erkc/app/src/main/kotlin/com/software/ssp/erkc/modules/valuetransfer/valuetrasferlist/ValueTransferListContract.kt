package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.common.receipt.ReceiptSectionViewModel
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.rest.models.Receipt


interface IValueTransferListView : IListView<ReceiptSectionViewModel> {
    fun navigateToSendValues(receipt: Receipt)
    fun navigateToNewValueTransfer()
}

interface IValueTransferListPresenter : IListPresenter<ReceiptSectionViewModel, IValueTransferListView> {
    fun onTransferValueClick(receiptViewModel: ReceiptViewModel)
    fun onAddNewValueTransferClick()
    fun onReceiptDeleted(receiptViewModel: ReceiptViewModel)
}

