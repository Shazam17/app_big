package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class NonAuthedMainScreenPresenter @Inject constructor(view: INonAuthedMainScreenView) : RxPresenter<INonAuthedMainScreenView>(view), INonAuthedMainScreenPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isSendValue: Boolean, isWithAddress: Boolean) {
        if(!validFields(barcode)){
            return
        }
        view?.showProgressVisible(true)

        subscriptions += receiptsRepository.fetchReceiptInfo(activeSession.accessToken!!, barcode, street, house, apartment)
        .subscribe(
            { receiptResponse ->
                //TODO do something with receipt

                view?.showProgressVisible(false)

                if(isSendValue) {
                    view?.navigateToSendValuesScreen(receiptResponse.data)
                } else {
                    view?.navigateToPaymentScreen()
                }
            },
            { throwable ->
                view?.showProgressVisible(false)
                view?.showMessage(throwable.message.toString())
            }
        )
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

    private fun validFields(barcode: String): Boolean{
        var isValid = true
        if(barcode.isBlank()){
            isValid = false
            view?.showErrorBarcodeMessage(R.string.main_screen_not_filled_error)
        }

        return isValid
    }
}
