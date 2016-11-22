package com.software.ssp.erkc.modules.newreceipt

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ApiException
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.ApiErrorType
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.utils.getStreetFromShortAddress
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

    override fun onBarCodeScanned(barcode: String) {
        view?.showProgressVisible(true)
        subscriptions += receiptsRepository.fetchReceiptInfo(barcode)
                .subscribe(
                        { receipt ->
                            view?.showProgressVisible(false)
                            view?.showReceiptData(receipt)
                        },
                        { throwable ->
                            view?.showProgressVisible(false)
                            if (throwable is ApiException && throwable.errorCode == ApiErrorType.UNKNOWN_BARCODE) {
                                view?.showBarcodeError(R.string.api_error_unknown_barcode)
                            } else {
                                view?.showMessage(throwable.parsedMessage())
                            }
                        }
                )

    }

    override fun onAddressSelected(address: String) {
        val street = getStreetFromShortAddress(address)
        view?.setStreetField(street)
    }

    override fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isSendValue: Boolean, isWithAddress: Boolean) {
        if (!validFields(barcode, street, house, apartment, isWithAddress)) {
            return
        }
        view?.showProgressVisible(true)

        subscriptions += receiptsRepository.fetchReceiptInfo(barcode, street, house, apartment)
                .concatMap {
                    receipt ->
                    view?.showProgressVisible(false)
                    if (isSendValue) {
                        view?.navigateToIPUInputScreen(receipt)
                    } else {
                        view?.navigateToPayScreen(receipt)
                    }

                    receiptsRepository.fetchReceipts()
                }
                .subscribe(
                        { receipts ->
                            activeSession.cachedReceipts = receipts
                        },
                        { throwable ->
                            view?.showProgressVisible(false)
                            if (throwable is ApiException) {
                                when (throwable.errorCode) {
                                    ApiErrorType.UNKNOWN_BARCODE -> view?.showBarcodeError(R.string.api_error_unknown_barcode)
                                    ApiErrorType.INVALID_REQUEST -> view?.showMessage(R.string.api_error_invalid_request)
                                    else -> view?.showMessage(throwable.parsedMessage())
                                }
                            } else {
                                view?.showMessage(throwable.parsedMessage())
                            }
                        }
                )
    }


    private fun validFields(barcode: String, street: String, house: String, apartment: String, isWithAddress: Boolean): Boolean {
        var isValid = true
        if (barcode.isNullOrBlank()) {
            isValid = false
            view?.showBarcodeError(R.string.error_empty_field)
        }
        if (isWithAddress && street.isNullOrBlank()) {
            isValid = false
            view?.showStreetError(R.string.error_empty_field)
        }
        if (isWithAddress && house.isNullOrBlank()) {
            isValid = false
            view?.showHouseError(R.string.error_empty_field)
        }
        if (isWithAddress && apartment.isNullOrBlank()) {
            isValid = false
            view?.showApartmentError(R.string.error_empty_field)
        }
        return isValid
    }
}
