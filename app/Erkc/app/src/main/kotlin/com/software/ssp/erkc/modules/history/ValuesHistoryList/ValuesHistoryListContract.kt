package com.software.ssp.erkc.modules.history.valueshistorylist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.modules.history.filter.HistoryFilterField
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel


interface IValuesHistoryListView : IListView<RealmReceipt> {
    fun navigateToIpuValueInfo(receipt: RealmReceipt)
    fun showCurrentFilter(currentFilter: HistoryFilterModel)
    fun navigateToFilter(currentFilter: HistoryFilterModel)
}

interface IValuesHistoryListPresenter : IListPresenter<RealmReceipt, IValuesHistoryListView> {
    var currentFilter: HistoryFilterModel
    fun onReceiptClick(receipt: RealmReceipt)
    fun onFilterDeleted(filterField: HistoryFilterField)
    fun onFilterClick()
    fun onRefreshClick()
}
