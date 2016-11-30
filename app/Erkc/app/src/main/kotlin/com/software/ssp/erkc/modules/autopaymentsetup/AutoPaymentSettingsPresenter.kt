package com.software.ssp.erkc.modules.autopaymentsetup

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.AutoPaymentMode
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.CardStatus
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class AutoPaymentSettingsPresenter @Inject constructor(view: IAutoPaymentSettingsView)
    : RxPresenter<IAutoPaymentSettingsView>(view), IAutoPaymentSettingsPresenter {

    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override var editingReceipt: RealmReceipt? = null
        set(value) {
            value?.let {
                field = value
                autoPaymentMode = AutoPaymentMode.values()[it.autoPayMode]
            }
        }
    override var incomingReceiptId: String? = null

    private lateinit var currentUser: RealmUser
    private var autoPaymentMode: AutoPaymentMode = AutoPaymentMode.OFF
        set(value) {
            field = value
            editingReceipt?.autoPayMode = autoPaymentMode.ordinal
        }


    private var isAgreementChecked = false

    override fun onViewAttached() {
        fetchData(incomingReceiptId?:"")
    }

    override fun onPaymentModeClick() {
        view?.showPaymentTypeSelect(editingReceipt)
    }

    override fun onPaymentModeSelect(mode: AutoPaymentMode) {
        autoPaymentMode = mode
        view?.showAutoPaymentMode(autoPaymentMode)
    }

    override fun onAgreementChecked(checked: Boolean) {
        isAgreementChecked = checked
    }

    override fun onReceiptClick() {
        val receipts = currentUser.receipts.filter { it.autoPayMode == AutoPaymentMode.OFF.ordinal }
        view?.showReceiptSelect(receipts)
    }

    override fun onCardClick() {
        val cards = currentUser.cards.filter { it.statusId == CardStatus.ACTIVATED.ordinal }
        view?.showCardSelect(cards)
    }


    override fun onContinueButtonClick() {
        if (validate()) {
            saveData()
        }
    }

    override fun onReceiptSelect(receipt: RealmReceipt?) {
        editingReceipt = receipt
        view?.showReceiptDetails(receipt)
    }

    override fun onCardSelect(card: RealmCard?) {
        editingReceipt?.linkedCard = card
        view?.showCardDetails(card)
    }

    override fun onMaxSumChanged(text: String) {
        if (text.isNotBlank()) {
            editingReceipt?.maxSum = text.toDouble()
        } else {
            editingReceipt?.maxSum = 0.0
        }
    }

    override fun onDialogClose() {
        view?.showReceiptDetails(editingReceipt)
        view?.showCardDetails(editingReceipt?.linkedCard)
    }

    private fun fetchData(fetchedReceiptId: String="") {
        subscriptions += realmRepository.fetchCurrentUser()
                .subscribe(
                        { user ->
                            currentUser = user
                            editingReceipt = currentUser.receipts.firstOrNull { it.id == fetchedReceiptId } ?: currentUser.receipts.first()

                            view?.showPaymentTypeSelect(editingReceipt)
                            view?.showReceiptDetails(editingReceipt)
                        },
                        { throwable -> view?.showMessage(throwable.parsedMessage()) }
                )
    }

    private fun saveData() {
        // todo pending indicator ..............
        subscriptions += receiptsRepository.updateReceipt(
                token = activeSession.accessToken!!,
                receiptId = editingReceipt!!.id,
                user_card_id = editingReceipt!!.linkedCard!!.id,
                maxsumma = editingReceipt!!.maxSum.toString(),
                mode_id = editingReceipt!!.autoPayMode.toString()
        ).subscribe(
                { response ->
                    view?.dismiss()
                },
                { throwable -> view?.showMessage(throwable.parsedMessage()) }
        )
    }

    private fun validate(): Boolean {
        var isValid = false
        when {
            editingReceipt == null -> view?.showMessage(R.string.autopayment_screen_error_receipt_must_have)
            editingReceipt!!.linkedCard == null -> view?.showMessage(R.string.autopayment_screen_error_card_must_have)
            isMaxSumNoValid() -> view?.showMessage(R.string.autopayment_screen_error_maxsum_must_have)
            !isAgreementChecked -> view?.showMessage(R.string.autopayment_screen_error_agreement_must_have)
            else -> isValid = true
        }
        return isValid
    }

    private fun isMaxSumNoValid(): Boolean {
        val maxSumNeeded = autoPaymentMode == AutoPaymentMode.AUTO
        val maxSum = editingReceipt?.maxSum
        val maxSumNoValid = maxSum == null || !(maxSum > 0)
        return maxSumNeeded && maxSumNoValid
    }
}