package com.software.ssp.erkc.modules.history.PaymentHistoryList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmPayment


interface IPaymentHistoryListView: IListView<RealmPayment> {
    fun navigateToPaymentInfo(payment: RealmPayment)
}

interface IPaymentHistoryListPresenter: IListPresenter<RealmPayment, IPaymentHistoryListView> {
    fun onPaymentClick(payment: RealmPayment)
}
