package com.software.ssp.erkc.modules.newreceipt

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ApiException
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.ApiErrorType
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.utils.getStreetFromShortAddress
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class NewReceiptPresenter @Inject constructor(view: INewReceiptView) : RxPresenter<INewReceiptView>(view), INewReceiptPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onBarCodeScanButtonClick() {
        view?.navigateToBarCodeScanScreen()
    }

    override fun onAddressClick() {
        view?.navigateToStreetSelectScreen()
    }

    override fun onBarCodeScanned(barcode: String) {
        view?.showProgressVisible(true)
        subscriptions += receiptsRepository.fetchReceiptInfo(activeSession.appToken!!, barcode)
                .subscribe(
                        { receipt ->
                            view?.showProgressVisible(false)
                            view?.showReceiptData(receipt)
                        },
                        { error ->
                            view?.showProgressVisible(false)
                            if (error is ApiException && error.errorCode == ApiErrorType.UNKNOWN_BARCODE) {
                                view?.showBarcodeError(R.string.api_error_unknown_barcode)
                            } else {
                                view?.showMessage(error.parsedMessage())
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

        subscriptions += receiptsRepository.fetchReceiptInfo(activeSession.accessToken ?: activeSession.appToken!!, barcode, street, house, apartment)
                .concatMap {
                    receipt ->

                    view?.showProgressVisible(false)

                    if (activeSession.accessToken != null) {
                        if (isSendValue) {
                            view?.navigateToIPUInputScreen(receipt.id!!)
                        } else {
                            view?.navigateToPayScreen(receipt.id!!)
                        }

                        realmRepository.saveReceipt(receipt)

                    } else {
                        if (isSendValue) {
                            view?.navigateToIPUInputScreen(receipt)
                        } else {
                            view?.navigateToPayScreen(receipt)
                        }

                        Observable.empty()
                    }
                }
                .subscribe(
                        { },
                        { error ->
                            view?.showProgressVisible(false)
                            if (error is ApiException) {
                                when (error.errorCode) {
                                    ApiErrorType.UNKNOWN_BARCODE -> view?.showBarcodeError(R.string.api_error_unknown_barcode)
                                    ApiErrorType.INVALID_REQUEST -> view?.showMessage(R.string.api_error_invalid_request)
                                    else -> view?.showMessage(error.parsedMessage())
                                }
                            } else {
                                view?.showMessage(error.parsedMessage())
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
