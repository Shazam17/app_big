package com.software.ssp.erkc.modules.autopayments.autopaymentslist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.models.Receipt


interface IAutoPaymentsListView: IListView<ReceiptViewModel> {
    fun navigateToEditAutoPayment(receipt: RealmReceipt)
    fun autoPaymentDidNotDeleted(receipt: RealmReceipt)
    fun autoPaymentDeleted(receipt: RealmReceipt)
    fun showConfirmDeleteDialog(receipt: RealmReceipt)
}

interface IAutoPaymentsListPresenter: IListPresenter<Receipt, IAutoPaymentsListView> {
    fun onDeleteButtonClick(receipt: RealmReceipt)
    fun onEditButtonClick(receipt: RealmReceipt)
    fun onConfirmDelete(receipt: RealmReceipt)
}
