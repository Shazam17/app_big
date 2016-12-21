package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ApiException
import com.software.ssp.erkc.common.OpenInstructionsList
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
    @Inject lateinit var eventBus: Relay<Any, Any>

    override fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isSendValue: Boolean, isWithAddress: Boolean) {
        if (!validFields(barcode, street, house, apartment, isWithAddress)) {
            return
        }

        view?.showProgressVisible(true)

        subscriptions += receiptsRepository.fetchReceiptInfo(barcode, street, house, apartment)
                .subscribe(
                        { receipt ->
                            view?.showProgressVisible(false)

                            if (isSendValue) {
                                view?.navigateToSendValuesScreen(receipt)
                            } else {
                                view?.navigateToPaymentScreen(receipt)
                            }
                        },
                        { error ->
                            view?.showProgressVisible(false)

                            if (error is ApiException) {
                                when (error.errorCode) {
                                    ApiErrorType.UNKNOWN_BARCODE -> view?.showErrorBarcodeMessage(R.string.api_error_unknown_barcode)
                                    ApiErrorType.INVALID_REQUEST -> view?.showMessage(R.string.api_error_invalid_request)
                                    else -> view?.showMessage(error.parsedMessage())
                                }
                            } else {
                                view?.showMessage(error.parsedMessage())
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

    override fun onInfoClick() {
        eventBus.call(OpenInstructionsList())
    }

    override fun onBarCodeScanned(code: String) {
        view?.showProgressVisible(true)
        subscriptions += receiptsRepository.fetchReceiptInfo(code)
                .subscribe(
                        { receipt ->
                            view?.showProgressVisible(false)
                            view?.showReceiptData(receipt)
                        },
                        { error ->
                            view?.showProgressVisible(false)
                            if (error is ApiException && error.errorCode == ApiErrorType.UNKNOWN_BARCODE) {
                                view?.showErrorBarcodeMessage(R.string.api_error_unknown_barcode)
                            } else {
                                view?.showMessage(error.parsedMessage())
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

    private fun validFields(barcode: String, street: String, house: String, apartment: String, isWithAddress: Boolean): Boolean {
        when {
            barcode.isNullOrBlank() -> {
                view?.showErrorBarcodeMessage(R.string.error_empty_field)
                return false
            }

            isWithAddress && (street.isNullOrBlank() || house.isNullOrBlank() || apartment.isNullOrBlank()) -> {
                view?.showMessage(R.string.error_all_fields_required)
                return false
            }
        }
        return true
    }
}
