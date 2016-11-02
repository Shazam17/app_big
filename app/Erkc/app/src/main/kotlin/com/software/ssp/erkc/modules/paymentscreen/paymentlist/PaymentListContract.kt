package com.software.ssp.erkc.modules.paymentscreen.paymentlist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.common.receipt.ReceiptSectionViewModel
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.rest.models.Receipt


interface IPaymentListView : IListView<ReceiptSectionViewModel> {
    fun navigateToAddReceiptScreen()
    fun navigateToPayScreen(receipt: Receipt)
}

interface IPaymentListPresenter : IListPresenter<ReceiptSectionViewModel, IPaymentListView> {
    fun onPayButtonClick(receiptViewModel: ReceiptViewModel)
    fun onReceiptDeleted(receiptViewModel: ReceiptViewModel)
    fun onAddReceiptButtonClick()
}
