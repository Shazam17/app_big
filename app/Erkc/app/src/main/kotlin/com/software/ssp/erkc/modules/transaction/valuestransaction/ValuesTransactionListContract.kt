package com.software.ssp.erkc.modules.transaction.valuestransaction

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmReceipt

/**
 * @author Alexander Popov on 14/12/2016.
 */
interface IValuesTransactionListView : IListView<RealmReceipt> {
    fun navigateToIpuValueInfo(receipt: RealmReceipt)
}

interface IValuesTransactionListPresenter : IListPresenter<RealmReceipt, IValuesTransactionListView> {
    fun onIpuClick(receipt: RealmReceipt)
    fun onDeleteClick(receipt: RealmReceipt)
}