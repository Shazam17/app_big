package com.software.ssp.erkc.modules.history.ValuesHistoryList

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.modules.history.filter.HistoryFilterField
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import rx.lang.kotlin.plusAssign
import rx.lang.kotlin.toObservable
import javax.inject.Inject
import kotlin.comparisons.compareBy


class ValuesHistoryListPresenter @Inject constructor(view: IValuesHistoryListView) : RxPresenter<IValuesHistoryListView>(view), IValuesHistoryListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository

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
        subscriptions += receiptsRepository.fetchReceipts()
                .concatMap {
                    receipts ->
                    realmRepository.saveReceiptsList(receipts)
                }
                .subscribe(
                        {
                            showReceiptsList()
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setLoadingVisible(false)
                        })
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
        //TODO Transfer filter
            else -> return
        }
        showReceiptsList()
    }

    override fun onFilterClick() {
        view?.navigateToFilter(currentFilter)
    }

    private fun showReceiptsList() {
        subscriptions += realmRepository.fetchReceiptsList()
                .flatMap {
                    receipts ->
                    receipts.toObservable()
                }
                .filter {
                    receipt ->
                    when {
                        receipt.lastIpuTransferDate == null -> return@filter false
                        !currentFilter.barcode.isNullOrBlank() && receipt.barcode != currentFilter.barcode -> return@filter false
                        !currentFilter.street.isNullOrBlank() && receipt.street != currentFilter.street -> return@filter false
                        !currentFilter.house.isNullOrBlank() && !receipt.house.equals(currentFilter.house, true) -> return@filter false
                        !currentFilter.apartment.isNullOrBlank() && receipt.apart != currentFilter.apartment -> return@filter false
                    }

                    currentFilter.periodFrom?.let {
                        if (receipt.lastIpuTransferDate != null && (receipt.lastIpuTransferDate!!.before(it) || receipt.lastIpuTransferDate!!.after(currentFilter.periodTo!!))) {
                            return@filter false
                        }
                    }

                    //TODO other ipu filters

                    return@filter true
                }
                .toList()
                .subscribe(
                        {
                            receipts ->
                            view?.showData(receipts.sortedWith(compareBy({ it.address }, { it.lastIpuTransferDate })))
                        },
                        {
                            error ->
                            view?.setLoadingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        })
    }
}
