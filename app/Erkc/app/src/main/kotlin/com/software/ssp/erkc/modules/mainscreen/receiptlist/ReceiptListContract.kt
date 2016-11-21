package com.software.ssp.erkc.modules.mainscreen.receiptlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.models.Receipt


interface IReceiptListView : IListView<RealmReceipt> {
    fun navigateToAddReceiptScreen()
    fun navigateToEmptyReceiptsList()
    fun navigateToIPUInputScreen(receipt: RealmReceipt)
    fun navigateToPayScreen(receipt: RealmReceipt)
    fun navigateToHistoryScreen(receipt: RealmReceipt)
    fun receiptDidNotDeleted(receipt: RealmReceipt)
    fun receiptDeleted(receipt: RealmReceipt)
    fun navigateToAutoPaymentSettingScreen(receipt: RealmReceipt)
}

interface IReceiptListPresenter : IListPresenter<RealmReceipt, IReceiptListView> {
    fun onPayButtonClick(receipt: RealmReceipt)
    fun onTransferButtonClick(receipt: RealmReceipt)
    fun onHistoryButtonClick(receipt: RealmReceipt)
    fun onAutoPaymentButtonClick(receipt: RealmReceipt)
    fun onReceiptDeleted(receipt: RealmReceipt)
    fun onAddReceiptButtonClick()
}
