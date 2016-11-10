package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Receipt


interface INonAuthedMainScreenView : IView {
    fun showErrorBarcodeMessage(resId: Int)
    fun showScannedBarcode(code: String)
    fun navigateToSignInScreen()
    fun navigateToSignUpScreen()
    fun navigateToPaymentScreen(receipt: Receipt)
    fun navigateToSendValuesScreen(data: Receipt)
    fun showProgressVisible(isVisible: Boolean)

    fun setStreetField(street: String)
    fun navigateToStreetSelectScreen()
    fun showReceiptData(receipt: Receipt)
}

interface INonAuthedMainScreenPresenter : IPresenter<INonAuthedMainScreenView> {
    fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isSendValue: Boolean, isWithAddress: Boolean)
    fun onSignInClick()
    fun onSignUpClick()
    fun onBarCodeScanned(code: String)
    fun onAddressSelected(address: String)
    fun onAddressClick()
}

