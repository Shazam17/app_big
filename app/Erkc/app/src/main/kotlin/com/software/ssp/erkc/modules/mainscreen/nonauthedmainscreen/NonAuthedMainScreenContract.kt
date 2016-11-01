package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface INonAuthedMainScreenView : IView {
    fun showErrorBarcodeMessage(resId: Int)
    fun showErrorStreetMessage(resId: Int)
    fun showErrorHouseMessage(resId: Int)
    fun showErrorApartmentMessage(resId: Int)
    fun showScannedBarcode(code: String)
    fun navigateToSignInScreen()
    fun navigateToSignUpScreen()
    fun navigateToSendValuesScreen()
    fun navigateToPaymentScreen()
    fun showProgressVisible(isVisible: Boolean)
    fun fillAddress(houseNo: String, street: String)
    fun navigateToAddressSelectScreen()
}

interface INonAuthedMainScreenPresenter : IPresenter<INonAuthedMainScreenView> {
    fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isSendValue: Boolean, isWithAddress: Boolean)
    fun onSignInClick()
    fun onSignUpClick()
    fun onBarCodeScanned(code: String)
    fun onAddressSelected(address: String)
    fun onAddressClick()
}

