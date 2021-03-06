package com.software.ssp.erkc.modules.history.paymenthistorylist

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmPaymentInfo
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.PaymentRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.modules.history.filter.HistoryFilterField
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import rx.lang.kotlin.plusAssign
import rx.lang.kotlin.toObservable
import javax.inject.Inject


class PaymentHistoryListPresenter @Inject constructor(view: IPaymentHistoryListView) : RxPresenter<IPaymentHistoryListView>(view), IPaymentHistoryListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var paymentRepository: PaymentRepository

    override var currentFilter: HistoryFilterModel = HistoryFilterModel()
        set(value) {
            field = value
            view?.showCurrentFilter(value)
            showPaymentsList()
        }

    override fun onViewAttached() {
        super.onViewAttached()

        if(!activeSession.isOfflineSession)
            fetchPayments()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onSwipeToRefresh() {
        //disabled
    }

    override fun onPaymentClick(payment: RealmPaymentInfo) {
        view?.navigateToPaymentInfo(payment)
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
            HistoryFilterField.MAX_SUM -> currentFilter.paymentSum = null
            HistoryFilterField.PAYMENT_TYPE -> currentFilter.paymentType = ""
            HistoryFilterField.PAYMENT_METHOD -> currentFilter.paymentMethod = null
            else -> return
        }
        showPaymentsList()
    }

    override fun onFilterClick() {
        view?.navigateToFilter(currentFilter)
    }

    private fun fetchPayments() {
        view?.setLoadingVisible(true)

        subscriptions += paymentRepository
            .fetchPayments()
            .concatMap {
                payments ->
                realmRepository.savePaymentsList(payments)
            }
            .subscribe(
                {
                    showPaymentsList()
                },
                {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.parsedMessage())
                }
            )
    }

    override fun onRefreshClick() {
        if(!activeSession.isOfflineSession)
            fetchPayments()
        else
            view?.showMessage(R.string.offline_mode_error)
    }

    private fun showPaymentsList() {
        subscriptions += realmRepository.fetchPayments()
                .flatMap {
                    payments ->
                    payments.toObservable()
                }
                .filter {
                    payment ->
                    payment.receipt?.let {
                        when {
                            !currentFilter.barcode.isNullOrBlank() && it.barcode != currentFilter.barcode -> return@filter false
                            !currentFilter.street.isNullOrBlank() && it.street != currentFilter.street -> return@filter false
                            !currentFilter.house.isNullOrBlank() && !it.house.equals(currentFilter.house, true) -> return@filter false
                            !currentFilter.apartment.isNullOrBlank() && it.apart != currentFilter.apartment -> return@filter false
                            !currentFilter.paymentType.isNullOrBlank() && it.name != currentFilter.paymentType -> return@filter false
                            else -> {
                            }
                        }
                    }

                    currentFilter.paymentMethod?.let {
                        if (payment.modeId != it.ordinal) {
                            return@filter false
                        }
                    }

                    currentFilter.periodFrom?.let {
                        if (payment.date != null && (payment.date!! < it || payment.date!! > currentFilter.periodTo!!)) {
                            return@filter false
                        }
                    }

                    currentFilter.paymentSum?.let {
                        if (it != payment.amount) {
                            return@filter false
                        }
                    }

                    return@filter true
                }
                .toList()
                .subscribe(
                        {
                            payments ->
                            val sortedPayments = payments.sortedByDescending { it.date }
                            view?.setLoadingVisible(false)
                            view?.showData(sortedPayments)
                        },
                        {
                            error ->
                            view?.setLoadingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        })
    }
}
