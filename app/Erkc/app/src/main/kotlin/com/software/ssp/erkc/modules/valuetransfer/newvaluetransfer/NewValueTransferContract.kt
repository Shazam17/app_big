package com.software.ssp.erkc.modules.valuetransfer.newvaluetransfer

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Receipt


interface INewValueTransferView : IView {
    fun showErrorBarcodeMessage(resId: Int)
    fun showErrorStreetMessage(resId: Int)
    fun showErrorHouseMessage(resId: Int)
    fun showErrorApartmentMessage(resId: Int)
    fun showScannedBarcode(code: String)
    fun showProgressVisible(isVisible: Boolean)
    fun navigateToEnterValues(receipt: Receipt)
}

interface INewValueTransferPresenter : IPresenter<INewValueTransferView> {
    fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isWithAddress: Boolean)
    fun onBarCodeScanned(code: String)
}

