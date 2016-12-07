package com.software.ssp.erkc.modules.history.valuehistory

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.realm.models.ReceiptType
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 05/12/2016.
 */
class ValueHistoryPresenter @Inject constructor(view: IValueHistoryView) : RxPresenter<IValueHistoryView>(view), IValueHistoryPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepo: RealmRepository
    @Inject lateinit var ipuProvider: IpuRepository
    var dateFrom: Date? = null
    var dateTo: Date? = null
    var receipt: RealmReceipt? = null

    override fun onViewAttached(dateFrom: Date?, dateTo: Date?, receiptId: String) {
        this.dateFrom = dateFrom
        this.dateTo = dateTo
        view?.setLoadingVisible(true)
        subscriptions += realmRepo.fetchReceiptsById(receiptId)
                .subscribe({
                    receipt ->
                    this.receipt = receipt
                    view?.setLoadingVisible(false)
                    fetchData()
                }, {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onSwipeToRefresh() {
        fetchData()
    }

    private fun fetchData() {
        view?.setLoadingVisible(true)
        subscriptions += ipuProvider.getHistoryByReceipt(activeSession.accessToken!!, receipt!!.barcode)
                .concatMap {
                    ipus ->
                    realmRepo.saveIpusByBarсode(ipus, receipt!!)
                }
                .concatMap {
                    realmRepo.fetchIpuValuesListByRange(dateFrom, dateTo, receipt!!)
                }
                .subscribe({
                    ipuValues ->
                    view?.showData(ipuValues)
                    fillData(ipuValues)
                    view?.setLoadingVisible(false)
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                    view?.setLoadingVisible(false)
                })
    }

    private fun fillData(ipus: List<RealmIpuValue>) {
        val startCalendar = GregorianCalendar()
        startCalendar.time = dateFrom
        val endCalendar = GregorianCalendar()
        endCalendar.time = dateTo

        val diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)
        val diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)
        for (ipu in ipus) {

        }
        val map = ipus.groupBy { it.serviceName }
        map.forEach {
            val value = it.value.last().value - it.value.first().value
            val average = value / diffMonth
            val unit = if (receipt?.receiptType == ReceiptType.WATER) R.string.history_value_water_unit else R.string.history_value_electro_unit
            view?.fillData(it.key, value.toString(), average.toString(), unit)
        }
    }
}