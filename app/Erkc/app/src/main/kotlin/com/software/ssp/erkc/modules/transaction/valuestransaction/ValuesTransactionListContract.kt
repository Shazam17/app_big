package com.software.ssp.erkc.modules.transaction.valuestransaction

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmOfflineIpu

/**
 * @author Alexander Popov on 14/12/2016.
 */
interface IValuesTransactionListView : IListView<RealmOfflineIpu> {
    fun navigateToIpuValueInfo(realmOfflineIpu: RealmOfflineIpu)
}

interface IValuesTransactionListPresenter : IListPresenter<RealmOfflineIpu, IValuesTransactionListView> {
    fun onIpuClick(realmOfflineIpu: RealmOfflineIpu)
    fun onDeleteClick(realmOfflineIpu: RealmOfflineIpu)
}