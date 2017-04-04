package com.software.ssp.erkc.modules.history.valuehistory

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.IpuType
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel

/**
 * @author Alexander Popov on 05/12/2016.
 */
interface IValueHistoryView : IView {
    fun showPeriod(dateFrom: String, dateTo: String)
    fun addServiceData(name: String, ipuType: IpuType, total: Long, average: Double)
    fun addIpuData(ipu: ValueHistoryViewModel)
    fun showReceiptData(receipt: RealmReceipt)
    fun setProgressVisible(isVisible: Boolean)
}

interface IValueHistoryPresenter : IPresenter<IValueHistoryView> {
    var receiptId: String
    var currentFilter: HistoryFilterModel
}