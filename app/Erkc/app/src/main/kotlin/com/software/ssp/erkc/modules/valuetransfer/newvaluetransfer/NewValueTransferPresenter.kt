package com.software.ssp.erkc.modules.valuetransfer.newvaluetransfer

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class NewValueTransferPresenter @Inject constructor(view: INewValueTransferView) : RxPresenter<INewValueTransferView>(view), INewValueTransferPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isWithAddress: Boolean) {
        if(!validFields(barcode, street, house, apartment, isWithAddress)){
            return
        }
        view?.showProgressVisible(true)

        subscriptions += receiptsRepository.fetchReceiptInfo(activeSession.appToken!!, barcode, street, house, apartment)
        .subscribe(
            { receipt ->
                view?.navigateToEnterValues(receipt)
                view?.showProgressVisible(false)
            },
            { throwable ->
                view?.showProgressVisible(false)
                view?.showMessage(throwable.message.toString())
            }
        )
    }

    override fun onBarCodeScanned(code: String) {
        view?.showScannedBarcode(code)
    }

    private fun validFields(barcode: String, street: String, house: String, apartment: String, isWithAddress: Boolean): Boolean{
        var isValid = true
        if(barcode.isNullOrEmpty()){
            isValid = false
            view?.showErrorBarcodeMessage(R.string.error_empty_field)
        }
        if(isWithAddress && street.isNullOrEmpty()){
            isValid = false
            view?.showErrorStreetMessage(R.string.error_empty_field)
        }
        if(isWithAddress && house.isNullOrEmpty()){
            isValid = false
            view?.showErrorHouseMessage(R.string.error_empty_field)
        }
        if(isWithAddress && apartment.isNullOrEmpty()){
            isValid = false
            view?.showErrorApartmentMessage(R.string.error_empty_field)
        }
        return isValid
    }
}
