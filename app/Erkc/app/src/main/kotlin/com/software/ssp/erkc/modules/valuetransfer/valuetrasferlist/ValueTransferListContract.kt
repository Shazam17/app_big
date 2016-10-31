package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.rest.models.Receipt


interface IValueTransferListView : IListView<ReceiptsViewModel> {
    fun navigateToSendValues(receipt: Receipt)
    fun navigateToNewValueTransfer()
}

interface IValueTransferListPresenter : IListPresenter<ReceiptsViewModel, IValueTransferListView> {
    fun onTransferValueClick(receipt: Receipt)
    fun onAddNewValueTransferClick()
    fun onReceiptDeleted(receipt: Receipt)
}

