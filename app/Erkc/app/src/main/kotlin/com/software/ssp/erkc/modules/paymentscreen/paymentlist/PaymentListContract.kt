package com.software.ssp.erkc.modules.paymentscreen.paymentlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmReceipt


interface IPaymentListView : IListView<RealmReceipt> {
    fun navigateToAddReceiptScreen()
    fun navigateToEmptyReceiptsList()

    fun navigateToPayScreen(receiptId: String)

    fun receiptDidNotDeleted(receipt: RealmReceipt)
    fun receiptDeleted(receipt: RealmReceipt)
}

interface IPaymentListPresenter : IListPresenter<RealmReceipt, IPaymentListView> {
    fun onPayButtonClick(receipt: RealmReceipt)
    fun onReceiptDeleted(receipt: RealmReceipt)
    fun onAddReceiptButtonClick()
}
