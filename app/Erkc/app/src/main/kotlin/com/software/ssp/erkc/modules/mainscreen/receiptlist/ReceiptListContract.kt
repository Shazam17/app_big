package com.software.ssp.erkc.modules.mainscreen.receiptlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt


interface IReceiptListView : IListView<ReceiptViewModel> {
    fun navigateToAddReceiptScreen()
    fun navigateToEmptyReceiptsList()
    fun navigateToRequestList()
    fun navigateToIPUInputScreen(receipt: RealmReceipt)
    fun navigateToPayScreen(receipt: RealmReceipt)
    fun navigateToAutoPaymentSettingScreen(receiptId: String)

    fun receiptDidNotDeleted(receipt: RealmReceipt)
    fun receiptDeleted(receipt: RealmReceipt)

    fun showNoActivatedCardsDialog()
}

interface IReceiptListPresenter : IListPresenter<ReceiptViewModel, IReceiptListView> {
    fun onPayButtonClick(receipt: RealmReceipt)
    fun onTransferButtonClick(receipt: RealmReceipt)
    fun onHistoryButtonClick(receipt: RealmReceipt)
    fun onRequestButtonClick()
    fun onAutoPaymentButtonClick(receipt: RealmReceipt)
    fun onReceiptDeleted(receipt: RealmReceipt)
    fun onAddReceiptButtonClick()
}
