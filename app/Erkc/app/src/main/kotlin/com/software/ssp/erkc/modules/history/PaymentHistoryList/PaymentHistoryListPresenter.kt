package com.software.ssp.erkc.modules.history.PaymentHistoryList

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmPayment
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
        showPaymentsList()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onSwipeToRefresh() {
        subscriptions += paymentRepository
                .fetchPayments(activeSession.accessToken!!)
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

    override fun onPaymentClick(payment: RealmPayment) {
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
            HistoryFilterField.MAX_SUM -> currentFilter.paymentSum = ""
            HistoryFilterField.PAYMENT_TYPE -> currentFilter.paymentType = null
            HistoryFilterField.PAYMENT_METHOD -> currentFilter.paymentMethod = null
        }
    }

    override fun onFilterClick() {
        view?.navigateToFilter(currentFilter)
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
                            !it.barcode.contains(currentFilter.barcode) -> return@filter false
                            !it.street.contains(currentFilter.street) -> return@filter false
                            !it.house.contains(currentFilter.house, true) -> return@filter false
                            !it.apart.contains(currentFilter.apartment) -> return@filter false
                            currentFilter.paymentType != null && it.receiptType != currentFilter.paymentType -> return@filter false
                            else -> {}
                        }
                    }

                    currentFilter.paymentMethod?.let {
                        if(payment.methodId != it.ordinal) {
                            return@filter false
                        }
                    }

                    currentFilter.periodFrom?.let {
                        if(payment.date != null && (payment.date!!.before(it) || payment.date!!.after(currentFilter.periodTo!!))) {
                            return@filter false
                        }
                    }

                    //TODO maxsum filter

                    return@filter true
                }
                .toList()
                .subscribe(
                        {
                            payments ->
                            view?.showData(payments)
                        },
                        {
                            error ->
                            view?.setLoadingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        })
    }
}
