package com.software.ssp.erkc.modules.history.ValuesHistoryList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmReceipt


interface IValuesHistoryListView : IListView<RealmReceipt> {
    fun navigateToIpuValueInfo(receipt: RealmReceipt)
}

interface IValuesHistoryListPresenter : IListPresenter<RealmReceipt, IValuesHistoryListView> {
    fun onReceiptClick(receipt: RealmReceipt)
}
