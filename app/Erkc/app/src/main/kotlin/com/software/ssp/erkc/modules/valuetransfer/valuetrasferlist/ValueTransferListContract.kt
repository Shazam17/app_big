package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmReceipt


interface IValueTransferListView : IListView<RealmReceipt> {
    fun navigateToAddReceiptScreen()
    fun navigateToEmptyReceiptsList()

    fun navigateToSendValues(receiptId: String)

    fun receiptDidNotDeleted(receipt: RealmReceipt)
    fun receiptDeleted(receipt: RealmReceipt)
}

interface IValueTransferListPresenter : IListPresenter<RealmReceipt, IValueTransferListView> {
    fun onTransferValueClick(receipt: RealmReceipt)
    fun onAddNewValueTransferClick()
    fun onReceiptDeleted(receipt: RealmReceipt)
}

