package com.software.ssp.erkc.modules.history.valuehistory

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import java.util.*

/**
 * @author Alexander Popov on 05/12/2016.
 */
interface IValueHistoryView : IListView<RealmIpuValue> {
    fun fillData(name: String, total: String, average: String, @StringRes unit: Int, @DrawableRes drawable: Int)
    fun fillDateRange(dateFrom: String, dateTo: String)
}

interface IValueHistoryPresenter : IListPresenter<RealmIpuValue, IValueHistoryView> {
    fun onViewAttached(receiptId: String)
}