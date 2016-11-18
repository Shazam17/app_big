package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ApiException
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.ApiErrorType
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class NonAuthedMainScreenPresenter @Inject constructor(view: INonAuthedMainScreenView) : RxPresenter<INonAuthedMainScreenView>(view), INonAuthedMainScreenPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isSendValue: Boolean, isWithAddress: Boolean) {
        if (!validFields(barcode)) {
            return
        }
        view?.showProgressVisible(true)

        subscriptions += receiptsRepository
                .fetchReceiptInfo(activeSession.appToken!!, barcode, street, house, apartment)
                .subscribe(
                        { receipt ->
                            view?.showProgressVisible(false)
                            if (isSendValue) {
                                view?.navigateToSendValuesScreen(receipt)
                            } else {
                                view?.navigateToPaymentScreen(receipt)
                            }
                        },
                        { throwable ->
                            view?.showProgressVisible(false)

                            if (throwable is ApiException) {
                                when (throwable.errorCode) {
                                    ApiErrorType.UNKNOWN_BARCODE -> view?.showErrorBarcodeMessage(R.string.api_error_unknown_barcode)
                                    ApiErrorType.INVALID_REQUEST -> view?.showMessage(R.string.api_error_invalid_request)
                                    else -> view?.showMessage(throwable.parsedMessage())
                                }
                            } else {
                                view?.showMessage(throwable.parsedMessage())
                            }
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
        view?.showProgressVisible(true)
        subscriptions += receiptsRepository.fetchReceiptInfo(activeSession.appToken!!, code)
                .subscribe(
                        { receipt ->
                            view?.showProgressVisible(false)
                            view?.showReceiptData(receipt)
                        },
                        { throwable ->
                            view?.showProgressVisible(false)
                            if (throwable is ApiException && throwable.errorCode == ApiErrorType.UNKNOWN_BARCODE) {
                                view?.showErrorBarcodeMessage(R.string.api_error_unknown_barcode)
                            } else {
                                view?.showMessage(throwable.parsedMessage())
                            }
                        }
                )
    }

    override fun onAddressClick() {
        view?.navigateToStreetSelectScreen()
    }

    override fun onStreetSelected(address: String) {
        view?.fillStreet(address)
    }

    private fun validFields(barcode: String): Boolean {
        var isValid = true
        if (barcode.isNullOrEmpty()) {
            isValid = false
            view?.showErrorBarcodeMessage(R.string.error_empty_field)
        }
        return isValid
    }
}
