package com.software.ssp.erkc.modules.history.PaymentHistoryList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmReceipt


interface IPaymentHistoryListView: IListView<RealmReceipt> {
}

interface IPaymentHistoryListPresenter: IListPresenter<RealmReceipt, IPaymentHistoryListView> {
}
