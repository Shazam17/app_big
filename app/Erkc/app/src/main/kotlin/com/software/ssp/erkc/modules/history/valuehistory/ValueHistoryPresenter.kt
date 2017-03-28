package com.software.ssp.erkc.modules.history.valuehistory

import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.extensions.toString
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import rx.Observable
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
        fetchData()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    private fun fetchData() {
        subscriptions += realmRepository.fetchReceiptsById(receiptId)
                .concatMap {
                    receipt ->

                    view?.showReceiptData(receipt)

                    if (activeSession.isOfflineSession) {
                        Observable.just(null)
                    } else {
                        ipuRepository.getHistoryByReceipt(receipt!!.barcode)
                    }
                }
                .concatMap {
                    ipus ->
                    if (ipus == null) {
                        Observable.just(null)
                    } else {
                        realmRepository.saveIpusWithReceipt(ipus, receiptId)
                    }
                }
                .concatMap {
                    realmRepository.fetchIpuByReceiptId(receiptId)
                }
                .doOnSubscribe { view?.setProgressVisible(true) }
                .doOnTerminate { view?.setProgressVisible(false) }
                .subscribe(
                        {
                            ipu ->
                            val filteredIpuValues = ipu.ipuValues
                                    .filter {
                                        ipuValue ->

                                        when {
                                            !currentFilter.deviceNumber.isNullOrBlank() && ipuValue.number != currentFilter.deviceNumber -> return@filter false
                                            !currentFilter.deviceInstallPlace.isNullOrBlank() && ipuValue.installPlace != currentFilter.deviceInstallPlace -> return@filter false
                                            currentFilter.periodFrom != null && ipuValue.date != null && ipuValue.date!! < currentFilter.periodFrom -> return@filter false
                                            currentFilter.periodTo != null && ipuValue.date != null && ipuValue.date!! > currentFilter.periodTo -> return@filter false
                                        }

                                        return@filter true
                                    }
                                    .sortedByDescending { it.date }

                            fillData(filteredIpuValues)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun fillData(ipus: List<RealmIpuValue>) {
        val startCalendar = GregorianCalendar()
        val dateFrom = ipus.last().date
        startCalendar.time = dateFrom

        val endCalendar = GregorianCalendar()
        val dateTo = ipus.first().date
        endCalendar.time = dateTo

        val diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)
        val diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)

        view?.showPeriod(dateFrom!!.toString(Constants.VALUES_DATE_FORMAT), dateTo!!.toString(Constants.VALUES_DATE_FORMAT))

        ipus.groupBy { it.shortName }.forEach {

            var total = 0L

            it.value.groupBy { it.number }.forEach {
                val ipu = it.value

                val ipuTotal = if (ipu.count() > 1) ipu.first().value.toLong() - ipu.last().value.toLong() else ipu.first().value.toLong()
                val ipuAverage: Double = if (diffMonth <= 1) 0.0 else (1.0) * ipuTotal / diffMonth
                view?.addIpuData(ValueHistoryViewModel(ipu, ipuTotal, ipuAverage))

                total += ipuTotal
            }

            val average: Double = if (diffMonth <= 1) 0.0 else (1.0) * total / diffMonth

            view?.addServiceData(it.key ?: "", total, average)
        }
    }
}