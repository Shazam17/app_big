package com.software.ssp.erkc.modules.history.filter

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.models.PaymentMethod
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject


class HistoryFilterPresenter @Inject constructor(view: IHistoryFilterView) : RxPresenter<IHistoryFilterView>(view), IHistoryFilterPresenter {

    @Inject lateinit var realmRepository: RealmRepository

    override lateinit var currentFilter: HistoryFilterModel

    private val paymentTypes = ArrayList<String>()
    private val deviceNumbers = ArrayList<String>()
    private val deviceInstallPlaces = ArrayList<String>()

    override fun onViewAttached() {
        super.onViewAttached()

        fetchCurrentUser()
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
                { index ->
                    currentFilter.paymentMethod = PaymentMethod.values()[index]
                    view?.showSelectedPaymentMethod(currentFilter.paymentMethod!!)
                })
    }

    override fun onSelectPaymentTypeClick() {
        view?.showListSelectDialog(
                R.string.history_filter_device_number_caption,
                paymentTypes,
                currentFilter.paymentType,
                {
                    paymentType ->
                    currentFilter.paymentType = paymentType
                    view?.showSelectedPaymentType(paymentType)
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
        if (paymentSum.isNullOrBlank()) {
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
        view?.showListSelectDialog(
                R.string.history_filter_device_number_caption,
                deviceNumbers,
                currentFilter.deviceNumber,
                {
                    deviceNumber ->
                    currentFilter.deviceNumber = deviceNumber
                    view?.showSelectedDeviceNumber(deviceNumber)
                })
    }

    override fun onSelectInstallPlaceClick() {
        view?.showListSelectDialog(
                R.string.history_filter_install_place_caption,
                deviceInstallPlaces,
                currentFilter.deviceInstallPlace,
                {
                    devicePlace ->
                    currentFilter.deviceInstallPlace = devicePlace
                    view?.showSelectedDevicePlace(devicePlace)
                })
    }

    private fun fetchCurrentUser() {
        subscriptions += realmRepository
                .fetchCurrentUser()
                .subscribe(
                        {
                            currentUser ->

                            currentUser.receipts
                                    .distinctBy { it.name }
                                    .mapTo(paymentTypes, { it.name })

                            val numbers = ArrayList<String>()
                            val places = ArrayList<String>()

                            currentUser.ipus.forEach {
                                numbers.addAll(it.ipuValues.map { it.number })
                                places.addAll(it.ipuValues.map { it.installPlace })
                            }

                            deviceNumbers.addAll(numbers.distinct())
                            deviceInstallPlaces.addAll(places.distinct())

                            view?.showCurrentFilter(currentFilter)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }
}
