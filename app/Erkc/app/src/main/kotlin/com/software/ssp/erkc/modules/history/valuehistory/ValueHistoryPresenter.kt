package com.software.ssp.erkc.modules.history.valuehistory

import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.ipuValuesFormat
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 05/12/2016.
 */
class ValueHistoryPresenter @Inject constructor(view: IValueHistoryView) : RxPresenter<IValueHistoryView>(view), IValueHistoryPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var ipuRepository: IpuRepository
    var receipt: RealmReceipt? = null

    override fun onViewAttached(receiptId: String) {
        view?.setLoadingVisible(true)
        subscriptions += realmRepository.fetchReceiptsById(receiptId)
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
    }

    private fun fetchData() {
        view?.setLoadingVisible(true)
        subscriptions += ipuRepository.getHistoryByReceipt(activeSession.accessToken!!, receipt!!.barcode)
                .concatMap {
                    ipus ->
                    realmRepository.saveIpusByBarсode(ipus, receipt!!)
                }
                .concatMap {
                    realmRepository.fetchIpuValuesList(receipt!!) //todo проставить даты из фильтра
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
        val dateFrom = ipus.first().date
        startCalendar.time = dateFrom
        val endCalendar = GregorianCalendar()
        val dateTo = ipus.last().date
        endCalendar.time = dateTo

        val diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)
        val diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)
        val map = ipus.groupBy { it.serviceName }
        view?.fillDateRange(dateFrom!!.ipuValuesFormat, dateTo!!.ipuValuesFormat)
        map.forEach {
            val ipu = it.value
            val serviceName = it.key
            val diffValue = ipu.last().value - ipu.first().value
            val average = if (diffValue != 0) diffValue / diffMonth else ipu.first().value
            val total = if (diffValue != 0) diffValue else ipu.first().value
            val unit: Int
            val drawable: Int
            when {
                serviceName.contains(Constants.HOT_WATER) -> {
                    unit = R.string.history_value_water_unit
                    drawable = R.drawable.pic_hot_water
                }
                serviceName.contains(Constants.COLD_WATER) -> {
                    unit = R.string.history_value_water_unit
                    drawable = R.drawable.pic_cold_water
                }
                else -> {
                    unit = R.string.history_value_electro_unit
                    drawable = R.drawable.pic_electro
                }
            }
            view?.fillData(it.key, total.toString(), average.toString(), unit, drawable)
        }
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }
}