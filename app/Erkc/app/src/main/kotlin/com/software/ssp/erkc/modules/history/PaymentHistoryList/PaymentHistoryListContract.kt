package com.software.ssp.erkc.modules.history.paymenthistorylist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmPayment
import com.software.ssp.erkc.modules.history.filter.HistoryFilterField
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel


interface IPaymentHistoryListView : IListView<RealmPayment> {
    fun navigateToPaymentInfo(payment: RealmPayment)
    fun showCurrentFilter(currentFilter: HistoryFilterModel)
    fun navigateToFilter(currentFilter: HistoryFilterModel)
}

interface IPaymentHistoryListPresenter : IListPresenter<RealmPayment, IPaymentHistoryListView> {
    var currentFilter: HistoryFilterModel
    fun onPaymentClick(payment: RealmPayment)
    fun onFilterDeleted(filterField: HistoryFilterField)
    fun onFilterClick()
    fun onRefreshClick()
}
