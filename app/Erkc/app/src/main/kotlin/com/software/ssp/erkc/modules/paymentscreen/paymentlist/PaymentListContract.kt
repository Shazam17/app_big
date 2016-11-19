package com.software.ssp.erkc.modules.paymentscreen.paymentlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.rest.models.Receipt


interface IPaymentListView : IListView<Receipt> {
    fun navigateToAddReceiptScreen()
    fun navigateToEmptyReceiptsList()
    fun navigateToPayScreen(receipt: Receipt)
    fun receiptDidNotDeleted(receipt: Receipt)
    fun receiptDeleted(receipt: Receipt)
}

interface IPaymentListPresenter : IListPresenter<Receipt, IPaymentListView> {
    fun onPayButtonClick(receipt: Receipt)
    fun onReceiptDeleted(receipt: Receipt)
    fun onAddReceiptButtonClick()
}
