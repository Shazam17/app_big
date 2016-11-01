package com.software.ssp.erkc.modules.mainscreen.authedaddreceipt

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.getHouse
import com.software.ssp.erkc.extensions.getStreet
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class AuthedAddReceiptPresenter @Inject constructor(view: IAuthedAddReceiptView) : RxPresenter<IAuthedAddReceiptView>(view), IAuthedAddReceiptPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onBarCodeScanButtonClick() {
        view?.navigateToBarCodeScanScreen()
    }

    override fun onAddressClick() {
        view?.navigateToAddressSelectScreen()
    }

    override fun onBarCodeScanned(code: String) {
        view?.setBarcodeField(code)
    }

    override fun onContinueClick(barcode: String, street: String, house: String, apartment: String, isCounterChecked: Boolean) {
        if (!validFields(barcode, street, house, apartment)) {
            return
        }
        //      view?.showProgressVisible(true)

        subscriptions += receiptsRepository.fetchReceiptInfo(activeSession.appToken!!, barcode, street, house, apartment)
                .subscribe(
                        { receiptResponse ->
                            //TODO do something with receipt

                            //     view?.showProgressVisible(false)

                            if (isCounterChecked) {
                                //    view?.navigateToSendValuesScreen()
                            } else {
                                //    view?.navigateToPaymentScreen()
                            }
                        },
                        { throwable ->
                            //   view?.showProgressVisible(false)
                            view?.showMessage(throwable.message.toString())
                        }
                )
    }

    override fun onAddressSelected(address: String) {
        view?.fillAddress(address.getStreet(), address.getHouse())
    }

    private fun validFields(barcode: String, street: String, house: String, apartment: String): Boolean {
        when{
            barcode.isBlank() -> view?.showBarcodeError(R.string.error_empty_field)
            street.isBlank() -> view?.showStreetError(R.string.error_empty_field)
            house.isBlank() -> view?.showHouseError(R.string.error_empty_field)
            apartment.isBlank() -> view?.showApartmentError(R.string.error_empty_field)
        }
        return barcode.isBlank() && street.isBlank() && house.isBlank() && apartment.isBlank()
    }


}