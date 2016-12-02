package com.software.ssp.erkc.modules.history.filter

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import java.util.*
import javax.inject.Inject


class HistoryFilterPresenter @Inject constructor(view: IHistoryFilterView) : RxPresenter<IHistoryFilterView>(view), IHistoryFilterPresenter {

    @Inject lateinit var realmRepository: RealmRepository

    override var currentFilter: HistoryFilterModel = HistoryFilterModel()

    override fun onViewAttached() {
        super.onViewAttached()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onBarCodeScanButtonClick() {
        view?.navigateToBarcodeScanner()
    }

    override fun onSelectPeriodClick() {
        view?.showSelectPeriodFromDialog(currentFilter.periodFrom ?: Calendar.getInstance().time)
    }

    override fun onAddressClick() {
        view?.navigateToSearchAddress()
    }

    override fun onSelectPaymentProcessClick() {
        view?.showMessage("not implemented")
    }

    override fun onSelectPaymentTypeClick() {
//        subscriptions += realmRepository.fetchReceiptsList()
//                .subscribe(
//                        {
//                            receipt ->
//                            view?.showListSelectDialog(
//                                    R.string.history_filter_payment_type_caption,
//                                    receipt.map { Resources.getSystem().getString(it.receiptType.getStringResId()) },
//                                    { index -> }
//                            )
//                        },
//                        {
//                            error ->
//                            view?.showMessage(error.parsedMessage())
//                        }
//                )
        view?.showListSelectDialog(R.string.history_filter_payment_type_caption,
                listOf("asd", "asd2", "asd3"),
                {index -> })
    }

    override fun onApplyFilterClick() {
        view?.showMessage("not implemented")
    }

    override fun onBarCodeTextChanged(barcode: String) {
        currentFilter.barcode = barcode
    }

    override fun onStreetTextChanged(street: String) {
        currentFilter.street = street
    }

    override fun onHouseTextChanged(house: String) {
        currentFilter.house = house
    }

    override fun onApartmentTextChanged(apartment: String) {
        currentFilter.apartment = apartment
    }

    override fun onPaymentSumTextChanged(paymentSum: String) {
        currentFilter.paymentSum = paymentSum
    }

    override fun onPeriodDateFromSelected(date: Date) {
        currentFilter.periodFrom = date
        view?.showSelectPeriodToDialog(date)
    }

    override fun onPeriodDateToSelected(date: Date) {
        currentFilter.periodTo = date
        view?.showSelectedPeriod(currentFilter.periodFrom!!, currentFilter.periodTo!!)
    }
}
