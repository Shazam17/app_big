package com.software.ssp.erkc.modules.history.valuehistory

import android.support.annotation.StringRes
import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmIpu
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import java.util.*

/**
 * @author Alexander Popov on 05/12/2016.
 */
interface IValueHistoryView : IListView<RealmIpuValue> {
    fun fillData(name: String, total: String, average: String, @StringRes unit: Int)
}

interface IValueHistoryPresenter : IListPresenter<RealmIpuValue, IValueHistoryView> {
    fun onViewAttached(dateFrom: Date?, dateTo: Date?, receiptId: String)
}