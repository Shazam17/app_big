package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.rest.models.Receipt


interface IValueTransferListView : IListView<Receipt> {
    fun navigateToSendValues(receipt: Receipt)
    fun navigateToAddReceiptScreen()
    fun navigateToEmptyReceiptsList()
    fun receiptDidNotDeleted(receipt: Receipt)
    fun receiptDeleted(receipt: Receipt)
}

interface IValueTransferListPresenter : IListPresenter<Receipt, IValueTransferListView> {
    fun onTransferValueClick(receipt: Receipt)
    fun onAddNewValueTransferClick()
    fun onReceiptDeleted(receipt: Receipt)
}

