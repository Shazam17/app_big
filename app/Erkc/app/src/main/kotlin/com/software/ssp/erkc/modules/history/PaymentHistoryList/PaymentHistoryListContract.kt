package com.software.ssp.erkc.modules.history.paymenthistorylist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmPaymentInfo
import com.software.ssp.erkc.modules.history.filter.HistoryFilterField
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel


interface IPaymentHistoryListView : IListView<RealmPaymentInfo> {
    fun navigateToPaymentInfo(payment: RealmPaymentInfo)
    fun showCurrentFilter(currentFilter: HistoryFilterModel)
    fun navigateToFilter(currentFilter: HistoryFilterModel)
}

interface IPaymentHistoryListPresenter : IListPresenter<RealmPaymentInfo, IPaymentHistoryListView> {
    var currentFilter: HistoryFilterModel
    fun onPaymentClick(payment: RealmPaymentInfo)
    fun onFilterDeleted(filterField: HistoryFilterField)
    fun onFilterClick()
    fun onRefreshClick()
}
