package com.software.ssp.erkc.modules.autopayments.autopaymentslist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.rest.models.Receipt


interface IAutoPaymentsListView: IListView<Receipt> {
    fun navigateToEditAutoPayment(receipt: Receipt)
    fun autoPaymentDidNotDeleted(receipt: Receipt)
    fun autoPaymentDeleted(receipt: Receipt)
    fun showConfirmDeleteDialog(receipt: Receipt)
}

interface IAutoPaymentsListPresenter: IListPresenter<Receipt, IAutoPaymentsListView> {
    fun onDeleteButtonClick(receipt: Receipt)
    fun onEditButtonClick(receipt: Receipt)
    fun onConfirmDelete(receipt: Receipt)
}
