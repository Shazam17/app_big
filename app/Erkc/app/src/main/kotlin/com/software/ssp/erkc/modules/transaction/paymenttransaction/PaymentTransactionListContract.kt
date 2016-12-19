package com.software.ssp.erkc.modules.transaction.paymenttransaction

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmOfflinePayment

/**
 * @author Alexander Popov on 13/12/2016.
 */
interface IPaymentTransactionListView : IListView<RealmOfflinePayment> {
    fun navigateToPaymentInfo(payment: RealmOfflinePayment)
}

interface IPaymentTransactionListPresenter : IListPresenter<RealmOfflinePayment, IPaymentTransactionListView> {
    fun onPaymentClick(payment: RealmOfflinePayment)
    fun onDeleteClick(payment: RealmOfflinePayment)
}