package com.software.ssp.erkc.modules.mainscreen.authedaddreceipt

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IAuthedAddReceiptView : IView {
    fun navigateToBarCodeScanScreen()
    fun navigateToAddressSelectScreen()
    fun navigateToIPUinputScreen()
    fun navigateToPayScreen()

    fun showBarcodeError(errorStringResId: Int)
    fun showStreetError(errorStringResId: Int)
    fun showHouseError(errorStringResId: Int)
    fun showApartmentError(errorStringResId: Int)

    fun setBarcodeField(barcode: String)
    fun fillStreet(street: String)
}

interface IAuthedAddReceiptPresenter : IPresenter<IAuthedAddReceiptView> {
    fun onBarCodeScanButtonClick()
    fun onAddressClick()
    fun onBarCodeScanned(code: String)
    fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isCounterChecked: Boolean)
    fun onStreetSelected(address: String)
}