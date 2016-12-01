package com.software.ssp.erkc.modules.autopayments.settings

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.AutoPaymentMode
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.data.rest.ActiveSession
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

    override var receiptId: String? = null

    override var selectedReceipt: RealmReceipt? = null
    override var selectedCard: RealmCard? = null

    private var autoPaymentMode: AutoPaymentMode = AutoPaymentMode.OFF

    private lateinit var currentUser: RealmUser

    override fun onViewAttached() {
        fetchData(receiptId ?: "")
    }

    override fun onPaymentModeClick() {
        view?.showPaymentTypeSelectDialog(autoPaymentMode)
    }

    override fun onPaymentModeSelect(mode: AutoPaymentMode) {
        autoPaymentMode = mode
        view?.showAutoPaymentMode(autoPaymentMode)
    }

    override fun onReceiptClick() {
        val receipts = currentUser.receipts.filter { it.autoPayMode == AutoPaymentMode.OFF.ordinal }
        view?.showReceiptSelectDialog(receipts)
    }

    override fun onCardClick() {
        val cards = currentUser.cards.filter { it.statusId == CardStatus.ACTIVATED.ordinal }
        view?.showCardSelectDialog(cards)
    }

    override fun onContinueButtonClick() {
        if (!dataValidation()) {
            return
        }
        view?.setPendingVisible(true)
        subscriptions += receiptsRepository
                .updateReceipt(
                        token = activeSession.accessToken!!,
                        receiptId = selectedReceipt!!.id,
                        user_card_id = selectedCard!!.id,
                        maxsumma = selectedReceipt!!.maxSum.toString(),
                        mode_id = autoPaymentMode.ordinal.toString()
                )
                .subscribe(
                        {
                            response ->
                            view?.setPendingVisible(false)
                            view?.close()
                        },
                        {
                            error ->
                            view?.setPendingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    override fun onReceiptSelect(receipt: RealmReceipt?) {
        selectedReceipt = receipt
        view?.showReceiptDetails(receipt)
    }

    override fun onCardSelect(card: RealmCard?) {
        selectedCard = card
        view?.showCardDetails(card)
    }

    override fun onMaxSumChanged(text: String) {
        selectedReceipt?.maxSum = if (text.isNotBlank()) text.toDouble() else 0.0
    }

    private fun fetchData(fetchedReceiptId: String = "") {
        view?.setPendingVisible(true)
        subscriptions += realmRepository.fetchCurrentUser()
                .subscribe(
                        {
                            user ->
                            currentUser = user
                            selectedReceipt = currentUser.receipts.firstOrNull { it.id == fetchedReceiptId }
                            selectedCard = selectedReceipt?.linkedCard
                            autoPaymentMode = AutoPaymentMode.values()[selectedReceipt?.autoPayMode ?: 0]

                            view?.showAutoPaymentMode(autoPaymentMode)
                            view?.showReceiptDetails(selectedReceipt)
                            view?.showCardDetails(selectedCard)
                            view?.setPendingVisible(false)
                        },
                        {
                            error ->
                            view?.setPendingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun dataValidation(): Boolean {
        var isValid = false
        when {
            selectedReceipt == null -> view?.showMessage(R.string.autopayment_screen_error_receipt_must_have)
            selectedCard == null -> view?.showMessage(R.string.autopayment_screen_error_card_must_have)
            isMaxSumNoValid() -> view?.showMessage(R.string.autopayment_screen_error_maxsum_must_have)
            else -> isValid = true
        }
        return isValid
    }

    private fun isMaxSumNoValid(): Boolean {
        val isMaxSumNeeded = selectedReceipt?.autoPayMode == AutoPaymentMode.AUTO.ordinal
        val isMaxSum = selectedReceipt?.maxSum
        val isMaxSumNoValid = isMaxSum == null || isMaxSum <= 0
        return isMaxSumNeeded && isMaxSumNoValid
    }
}