package com.software.ssp.erkc.modules.newreceipt

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Receipt


interface INewReceiptView : IView {
    fun navigateToBarCodeScanScreen()
    fun navigateToStreetSelectScreen()

    fun navigateToIPUInputScreen(receipt: Receipt)
    fun navigateToPayScreen(receipt: Receipt)

    fun showBarcodeError(errorStringResId: Int)
    fun showStreetError(errorStringResId: Int)
    fun showHouseError(errorStringResId: Int)
    fun showApartmentError(errorStringResId: Int)

    fun setBarcodeField(barcode: String)

    fun showProgressVisible(isVisible: Boolean)
}

interface INewReceiptPresenter : IPresenter<INewReceiptView> {
    fun onBarCodeScanButtonClick()
    fun onAddressClick()
    fun onBarCodeScanned(code: String)
    fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isSendValue: Boolean, isWithAddress: Boolean)
}