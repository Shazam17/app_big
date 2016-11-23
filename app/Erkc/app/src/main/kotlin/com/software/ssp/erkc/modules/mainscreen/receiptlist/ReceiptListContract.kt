package com.software.ssp.erkc.modules.mainscreen.receiptlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt


interface IReceiptListView : IListView<ReceiptViewModel> {
    fun navigateToAddReceiptScreen()
    fun navigateToEmptyReceiptsList()

    fun navigateToIPUInputScreen(receipt: RealmReceipt)
    fun navigateToPayScreen(receipt: RealmReceipt)
    fun navigateToHistoryScreen(receiptId: String)
    fun navigateToAutoPaymentSettingScreen(receiptId: String)

    fun receiptDidNotDeleted(receipt: RealmReceipt)
    fun receiptDeleted(receipt: RealmReceipt)
}

interface IReceiptListPresenter : IListPresenter<ReceiptViewModel, IReceiptListView> {
    fun onPayButtonClick(receipt: RealmReceipt)
    fun onTransferButtonClick(receipt: RealmReceipt)
    fun onHistoryButtonClick(receipt: RealmReceipt)
    fun onAutoPaymentButtonClick(receipt: RealmReceipt)
    fun onReceiptDeleted(receipt: RealmReceipt)
    fun onAddReceiptButtonClick()
}
