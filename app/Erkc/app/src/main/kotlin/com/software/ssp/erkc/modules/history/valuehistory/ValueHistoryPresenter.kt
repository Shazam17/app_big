package com.software.ssp.erkc.modules.history.valuehistory

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmIpu
import com.software.ssp.erkc.data.realm.models.RealmReceipt
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

    override fun onViewAttached(dateFrom: Date?, dateTo: Date?, receipt: RealmReceipt) {
        this.dateFrom = dateFrom
        this.dateTo = dateTo
        this.receipt = receipt
        fetchData()
    }

    override fun onSwipeToRefresh() {
        fetchData()
    }

    private fun fetchData() {
        subscriptions += ipuProvider.getHistoryByReceipt(activeSession.accessToken!!, receipt!!.barcode)
                .concatMap {
                    ipus ->
                    realmRepo.saveIpusByBarсode(ipus, receipt!!)
                }
                .subscribe({
                    fetchIpus()
                }, {
                    error ->
                    view?.showMessage(error.parsedMessage())
                    view?.setLoadingVisible(false)
                })
    }

    private fun fetchIpus() {
        subscriptions += realmRepo.fetchReceiptsListByRange(dateFrom, dateTo)
                .subscribe({
                    ipus ->
                    view?.showData(ipus)
                    fillData(ipus)
                })
    }

    private fun fillData(ipus: List<RealmIpu>) {
        val value = ipus
                .flatMap { it.ipuValues }
                .sumBy { it.value }

        val startCalendar = GregorianCalendar()
        startCalendar.time = dateFrom
        val endCalendar = GregorianCalendar()
        endCalendar.time = dateTo

        val diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)
        val diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)
        view?.fillData(value, value / diffMonth)
    }

}