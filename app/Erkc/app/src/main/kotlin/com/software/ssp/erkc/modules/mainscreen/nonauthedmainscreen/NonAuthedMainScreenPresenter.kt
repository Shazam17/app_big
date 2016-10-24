package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import rx.lang.kotlin.plusAssign
import rx.lang.kotlin.subscribeWith
import javax.inject.Inject

class NonAuthedMainScreenPresenter @Inject constructor(view: INonAuthedMainScreenView) : RxPresenter<INonAuthedMainScreenView>(view), INonAuthedMainScreenPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isSendValue: Boolean) {
        if(!validFields(barcode, street, house, apartment)){
            return
        }

        subscriptions += receiptsRepository.fetchReceiptInfo(activeSession.apiToken!!, barcode, street, house, apartment)
        .subscribeWith {
            onNext { receipt ->
                //TODO do something with receipt
                if(isSendValue) {
                    view?.navigateToSendValuesScreen()
                } else {
                    view?.navigateToPaymentScreen()
                }
            }
            onError { throwable ->
                view?.showMessage(throwable.message.toString())
            }
        }
    }

    override fun onSignInClick() {
        view?.navigateToSignInScreen()
    }

    override fun onSignUpClick() {
        view?.navigateToSignUpScreen()
    }

    override fun onBarCodeScanned(code: String) {
        view?.showScannedBarcode(code)
    }

    private fun validFields(barcode: String, street: String, house: String, apartment: String): Boolean{
        var isValid = true
        if(barcode.isNullOrEmpty()){
            isValid = false
            view?.showErrorBarcodeMessage(R.string.main_screen_not_filled_error)
        }
        if(street.isNullOrEmpty()){
            isValid = false
            view?.showErrorStreetMessage(R.string.main_screen_not_filled_error)
        }
        if(house.isNullOrEmpty()){
            isValid = false
            view?.showErrorHouseMessage(R.string.main_screen_not_filled_error)
        }
        if(apartment.isNullOrEmpty()){
            isValid = false
            view?.showErrorApartmentMessage(R.string.main_screen_not_filled_error)
        }
        return isValid
    }
}
