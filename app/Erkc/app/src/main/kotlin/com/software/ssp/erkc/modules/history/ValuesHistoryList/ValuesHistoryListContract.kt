package com.software.ssp.erkc.modules.history.ValuesHistoryList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import java.util.*


interface IValuesHistoryListView : IListView<RealmReceipt> {
    fun navigateToIpuValueInfo(receipt: RealmReceipt, dateFrom: Date, dateTo: Date)
}

interface IValuesHistoryListPresenter : IListPresenter<RealmReceipt, IValuesHistoryListView> {
    fun onReceiptClick(receipt: RealmReceipt)
}
