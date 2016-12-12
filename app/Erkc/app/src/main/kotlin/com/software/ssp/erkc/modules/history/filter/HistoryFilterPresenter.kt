package com.software.ssp.erkc.modules.history.filter

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.ReceiptType
import com.software.ssp.erkc.data.rest.models.PaymentMethod
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import java.util.*
import javax.inject.Inject


class HistoryFilterPresenter @Inject constructor(view: IHistoryFilterView) : RxPresenter<IHistoryFilterView>(view), IHistoryFilterPresenter {

    @Inject lateinit var realmRepository: RealmRepository

    override lateinit var currentFilter: HistoryFilterModel

    override fun onViewAttached() {
        super.onViewAttached()

        view?.showCurrentFilter(currentFilter)
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
        view?.showListSelectDialog(
                R.string.history_filter_payment_method_caption,
                R.array.payment_methods,
                currentFilter.paymentMethod?.ordinal ?: -1,
                {index ->
                    currentFilter.paymentMethod = PaymentMethod.values()[index]
                    view?.showSelectedPaymentMethod(currentFilter.paymentMethod!!)
                })
    }

    override fun onSelectPaymentTypeClick() {
        view?.showListSelectDialog(
                R.string.history_filter_payment_type_caption,
                R.array.receipt_types,
                currentFilter.paymentType?.ordinal ?: -1,
                {index ->
                    currentFilter.paymentType = ReceiptType.values()[index]
                    view?.showSelectedPaymentType(currentFilter.paymentType!!)
                })
    }

    override fun onApplyFilterClick() {
        view?.applyFilter(currentFilter)
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
        if(paymentSum.isNullOrBlank()) {
            currentFilter.paymentSum = null
        } else {
            currentFilter.paymentSum = paymentSum.toDouble()
        }
    }

    override fun onPeriodDateFromSelected(date: Date) {
        currentFilter.periodFrom = date
        view?.showSelectPeriodToDialog(date)
    }

    override fun onPeriodDateToSelected(date: Date) {
        currentFilter.periodTo = date
        view?.showSelectedPeriod(currentFilter.periodFrom!!, currentFilter.periodTo!!)
    }

    override fun onSelectDeviceNumberClick() {
        //TODO device numbers list from realmRepository
        view?.showMessage("Not implemented")
//        view?.showListSelectDialog(
//                R.string.history_filter_device_number_caption,
//                listOf("asd", "asd2", "asd3"),
//                currentFilter.deviceNumber,
//                {
//                    deviceNumber ->
//                    currentFilter.deviceNumber = deviceNumber
//                    view?.showSelectedDeviceNumber(deviceNumber)
//                })
    }

    override fun onSelectInstallPlaceClick() {
        //TODO device places list from realmRepository
        view?.showMessage("Not implemented")
//        view?.showListSelectDialog(
//                R.string.history_filter_install_place_caption,
//                listOf("фыв", "фыв2", "фыв3"),
//                currentFilter.deviceInstallPlace,
//                {
//                    devicePlace ->
//                    currentFilter.deviceInstallPlace = devicePlace
//                    view?.showSelectedDevicePlace(devicePlace)
//                })
    }
}
