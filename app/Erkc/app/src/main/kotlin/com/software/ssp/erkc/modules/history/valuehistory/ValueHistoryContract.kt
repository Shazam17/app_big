package com.software.ssp.erkc.modules.history.valuehistory

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmIpu
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import java.util.*

/**
 * @author Alexander Popov on 05/12/2016.
 */
interface IValueHistoryView : IListView<RealmIpu> {
    fun fillData(total: Int, average: Int)
}

interface IValueHistoryPresenter : IListPresenter<RealmIpu, IValueHistoryView> {
    fun onViewAttached(dateFrom: Date?, dateTo: Date?, receipt: RealmReceipt)
}