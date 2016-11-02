package com.software.ssp.erkc.modules.newreceipt

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class NewReceiptPresenter @Inject constructor(view: INewReceiptView) : RxPresenter<INewReceiptView>(view), INewReceiptPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onBarCodeScanButtonClick() {
        view?.navigateToBarCodeScanScreen()
    }

    override fun onAddressClick() {
        view?.navigateToStreetSelectScreen()
    }

    override fun onBarCodeScanned(code: String) {
        view?.setBarcodeField(code)
    }

    override fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isSendValue: Boolean, isWithAddress: Boolean) {
        if(!validFields(barcode, street, house, apartment, isWithAddress)){
            return
        }
        view?.showProgressVisible(true)

        subscriptions += receiptsRepository.fetchReceiptInfo(activeSession.appToken!!, barcode, street, house, apartment)
                .subscribe(
                        { receipt ->

                            //TODO Add new receipt to List

                            view?.showProgressVisible(false)

                            if(isSendValue) {
                                view?.navigateToIPUInputScreen(receipt)
                            } else {
                                view?.navigateToPayScreen(receipt)
                            }
                        },
                        { throwable ->
                            view?.showProgressVisible(false)
                            view?.showMessage(throwable.message.toString())
                        }
                )
    }


    private fun validFields(barcode: String, street: String, house: String, apartment: String, isWithAddress: Boolean): Boolean{
        var isValid = true
        if(barcode.isNullOrEmpty()){
            isValid = false
            view?.showBarcodeError(R.string.error_empty_field)
        }
        if(isWithAddress && street.isNullOrEmpty()){
            isValid = false
            view?.showStreetError(R.string.error_empty_field)
        }
        if(isWithAddress && house.isNullOrEmpty()){
            isValid = false
            view?.showHouseError(R.string.error_empty_field)
        }
        if(isWithAddress && apartment.isNullOrEmpty()){
            isValid = false
            view?.showApartmentError(R.string.error_empty_field)
        }
        return isValid
    }
}
