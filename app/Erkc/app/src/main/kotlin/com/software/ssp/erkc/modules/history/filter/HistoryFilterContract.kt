package com.software.ssp.erkc.modules.history.filter

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.ReceiptType
import com.software.ssp.erkc.data.rest.models.PaymentMethod
import java.util.*


interface IHistoryFilterView : IView {
    fun navigateToBarcodeScanner()
    fun navigateToSearchAddress()

    fun showListSelectDialog(titleRes: Int, itemsRes: Int, selectedIndex: Int, onConfirm: (Int) -> Unit)

    fun showSelectPeriodFromDialog(date: Date)
    fun showSelectPeriodToDialog(date: Date)

    fun showSelectedPeriod(dateFrom: Date, dateTo: Date)
    fun showSelectedPaymentType(paymentType: ReceiptType)
    fun showSelectedPaymentMethod(paymentMethod: PaymentMethod)

    fun showCurrentFilter(currentFilter: HistoryFilterModel)

    fun applyFilter(currentFilter: HistoryFilterModel)
}

interface IHistoryFilterPresenter : IPresenter<IHistoryFilterView> {

    var currentFilter: HistoryFilterModel

    fun onBarCodeScanButtonClick()
    fun onSelectPeriodClick()
    fun onAddressClick()
    fun onApplyFilterClick()
    fun onSelectPaymentProcessClick()
    fun onSelectPaymentTypeClick()

    fun onBarCodeTextChanged(barcode: String)
    fun onStreetTextChanged(street: String)
    fun onHouseTextChanged(house: String)
    fun onApartmentTextChanged(apartment: String)
    fun onPaymentSumTextChanged(paymentSum: String)

    fun onPeriodDateFromSelected(date: Date)
    fun onPeriodDateToSelected(date: Date)
}
