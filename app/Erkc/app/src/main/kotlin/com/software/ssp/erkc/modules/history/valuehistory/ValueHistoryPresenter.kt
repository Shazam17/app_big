package com.software.ssp.erkc.modules.history.valuehistory

import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.extensions.toString
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 05/12/2016.
 */
class ValueHistoryPresenter @Inject constructor(view: IValueHistoryView) : RxPresenter<IValueHistoryView>(view), IValueHistoryPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var ipuRepository: IpuRepository

    override var receiptId: String = ""
    override var currentFilter: HistoryFilterModel = HistoryFilterModel()

    override fun onViewAttached() {
        view?.setLoadingVisible(true)

        fetchData()
    }

    override fun onSwipeToRefresh() {
    }

    private fun fetchData() {
        view?.setLoadingVisible(true)
        subscriptions += realmRepository.fetchReceiptsById(receiptId)
                .concatMap {
                    receipt ->
                    ipuRepository.getHistoryByReceipt(activeSession.accessToken!!, receipt!!.barcode)
                }
                .concatMap {
                    ipus ->
                    realmRepository.saveIpusWithReceipt(ipus, receiptId)
                }
                .concatMap {
                    realmRepository.fetchIpuByReceiptId(receiptId)
                }
                .subscribe(
                        {
                            ipu ->
                            val filteredIpuValues = ipu.ipuValues
                                    .filter {
                                        ipuValue ->

                                        currentFilter.periodFrom?.let {
                                            if (ipuValue.date != null && (ipuValue.date!! < it || ipuValue.date!! > currentFilter.periodTo!!)) {
                                                return@filter false
                                            }
                                        }

                                        when {
                                            !currentFilter.deviceNumber.isNullOrBlank() && ipuValue.number != currentFilter.deviceNumber -> return@filter false
                                            !currentFilter.deviceInstallPlace.isNullOrBlank() && ipuValue.installPlace != currentFilter.deviceInstallPlace -> return@filter false
                                        }

                                        return@filter true
                                    }
                                    .sortedBy { it.number }

                            view?.showData(filteredIpuValues)
                            fillData(filteredIpuValues)

                            view?.setLoadingVisible(false)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setLoadingVisible(false)
                        }
                )
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

        view?.showPeriod(dateFrom!!.toString(Constants.VALUES_DATE_FORMAT), dateTo!!.toString(Constants.VALUES_DATE_FORMAT))

        ipus.groupBy { it.serviceName }.forEach {

            var total = 0L

            it.value.groupBy { it.number }.forEach {
                val ipu = it.value
                total += if (ipu.count() > 1) ipu.first().value.toLong() - ipu.last().value.toLong() else ipu.first().value.toLong()
            }

            val average = total / if (diffMonth == 0) 1 else diffMonth

            val serviceName = it.key

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