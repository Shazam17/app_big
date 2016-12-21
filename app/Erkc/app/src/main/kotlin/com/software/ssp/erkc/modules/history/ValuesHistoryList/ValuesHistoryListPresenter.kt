package com.software.ssp.erkc.modules.history.ValuesHistoryList

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.modules.history.filter.HistoryFilterField
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import rx.Observable
import rx.lang.kotlin.plusAssign
import rx.lang.kotlin.toObservable
import javax.inject.Inject
import kotlin.comparisons.compareBy


class ValuesHistoryListPresenter @Inject constructor(view: IValuesHistoryListView) : RxPresenter<IValuesHistoryListView>(view), IValuesHistoryListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var ipuRepository: IpuRepository

    override var currentFilter: HistoryFilterModel = HistoryFilterModel()
        set(value) {
            field = value
            view?.showCurrentFilter(value)
            showReceiptsList()
        }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onSwipeToRefresh() {
        //disabled
    }

    override fun onReceiptClick(receipt: RealmReceipt) {
        view?.navigateToIpuValueInfo(receipt)
    }

    override fun onFilterDeleted(filterField: HistoryFilterField) {
        when (filterField) {
            HistoryFilterField.BARCODE -> currentFilter.barcode = ""
            HistoryFilterField.STREET -> currentFilter.street = ""
            HistoryFilterField.HOUSE -> currentFilter.house = ""
            HistoryFilterField.APARTMENT -> currentFilter.apartment = ""
            HistoryFilterField.PERIOD -> {
                currentFilter.periodFrom = null
                currentFilter.periodTo = null
            }
            HistoryFilterField.DEVICE_NUMBER -> currentFilter.deviceNumber = ""
            HistoryFilterField.DEVICE_PLACE -> currentFilter.deviceInstallPlace = ""
            else -> return
        }
        showReceiptsList()
    }

    override fun onFilterClick() {
        view?.navigateToFilter(currentFilter)
    }

    override fun onRefreshClick() {
        view?.setLoadingVisible(true)

        subscriptions += realmRepository
                .fetchReceiptsList()
                .flatMap {
                    receipts ->
                    receipts.toObservable()
                }
                .filter { it.lastIpuTransferDate != null }
                .flatMap {
                    receipt ->
                    updateIpusForReceipt(receipt)
                }
                .toList()
                .subscribe(
                        {
                            showReceiptsList()
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setLoadingVisible(false)
                        }
                )
    }

    private fun showReceiptsList() {
        subscriptions += realmRepository.fetchIpuList()
                .flatMap {
                    ipuList ->
                    ipuList.toObservable()
                }
                .filter {
                    ipu ->
                    when {
                        !currentFilter.barcode.isNullOrBlank() && ipu.receipt!!.barcode != currentFilter.barcode -> return@filter false
                        !currentFilter.street.isNullOrBlank() && ipu.receipt!!.street != currentFilter.street -> return@filter false
                        !currentFilter.house.isNullOrBlank() && !ipu.receipt!!.house.equals(currentFilter.house, true) -> return@filter false
                        !currentFilter.apartment.isNullOrBlank() && ipu.receipt!!.apart != currentFilter.apartment -> return@filter false
                    }

                    ipu.ipuValues.forEach {
                        if ((currentFilter.deviceNumber.isNullOrBlank() || it.number == currentFilter.deviceNumber)
                                && (currentFilter.deviceInstallPlace.isNullOrBlank() || it.installPlace == currentFilter.deviceInstallPlace)
                                && (currentFilter.periodFrom == null || it.date != null && it.date!! >= currentFilter.periodFrom)
                                && (currentFilter.periodTo == null || it.date != null && it.date!! <= currentFilter.periodTo)) {
                            return@filter true
                        }
                    }

                    return@filter false
                }
                .toList()
                .subscribe(
                        {
                            filteredIpus ->
                            view?.showData(filteredIpus.map { it.receipt!! }.sortedWith(compareBy({ it.address }, { it.lastIpuTransferDate })))
                        },
                        {
                            error ->
                            view?.setLoadingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        })
    }

    private fun updateIpusForReceipt(receipt: RealmReceipt): Observable<Boolean> {
        return ipuRepository
                .getHistoryByReceipt(receipt.barcode)
                .concatMap {
                    ipus ->
                    realmRepository.saveIpusWithReceipt(ipus, receipt.id)
                }
    }
}
