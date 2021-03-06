package com.software.ssp.erkc.modules.autopayments.settings

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.PaymentMethod
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.CardStatus
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject
import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences
import android.content.Context.MODE_PRIVATE



class
AutoPaymentSettingsPresenter @Inject constructor(view: IAutoPaymentSettingsView)
    : RxPresenter<IAutoPaymentSettingsView>(view), IAutoPaymentSettingsPresenter {

    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override var receiptId: String? = null

    override var selectedReceipt: RealmReceipt? = null
    override var selectedCard: RealmCard? = null

    private var autoPaymentMode: PaymentMethod = PaymentMethod.DEFAULT

    private lateinit var currentUser: RealmUser

    override fun onViewAttached() {
        fetchData(receiptId ?: "")
    }

    override fun onPaymentModeClick() {
        view?.showPaymentTypeSelectDialog(autoPaymentMode)
    }

    override fun onPaymentModeSelect(mode: PaymentMethod) {
        autoPaymentMode = mode
        view?.showAutoPaymentMode(autoPaymentMode, null)
    }

    override fun onReceiptClick() {
        val receipts = currentUser.receipts.filter { it.autoPayMode == PaymentMethod.DEFAULT.ordinal }
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

        view?.setProgressVisibility(true)

        subscriptions += receiptsRepository
                .updateReceipt(
                        selectedReceipt!!.id,
                        selectedCard!!.id,
                        if (autoPaymentMode == PaymentMethod.AUTO) selectedReceipt!!.maxSum else 0.0,
                        autoPaymentMode.ordinal.toString()
                )
                .concatMap {
                    selectedReceipt!!.linkedCard = selectedCard
                    selectedReceipt!!.autoPayMode = autoPaymentMode.ordinal

                    realmRepository.updateReceipt(selectedReceipt!!)
                }
                .subscribe(
                        {
                            view?.setProgressVisibility(false)
                            view?.close()
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
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
        view?.setProgressVisibility(true)
        subscriptions += realmRepository.fetchCurrentUser()
                .subscribe(
                        {
                            user ->
                            currentUser = user
                            selectedReceipt = currentUser.receipts.firstOrNull { it.id == fetchedReceiptId }
                            selectedCard = selectedReceipt?.linkedCard
                            autoPaymentMode = PaymentMethod.values()[selectedReceipt?.autoPayMode ?: 0]

                            view?.showAutoPaymentMode(autoPaymentMode, selectedReceipt)
                            view?.showReceiptDetails(selectedReceipt)
                            view?.showCardDetails(selectedCard)
                            view?.setProgressVisibility(false)
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun dataValidation(): Boolean {
        return when {
            selectedReceipt == null -> {
                view?.showMessage(R.string.autopayment_screen_error_receipt_must_have)
                false
            }
            selectedCard == null -> {
                view?.showMessage(R.string.autopayment_screen_error_card_must_have)
                false
            }
            selectedReceipt!!.autoPayMode != PaymentMethod.AUTO.ordinal -> true
            selectedReceipt!!.maxSum <= 0.0 -> {
                view?.showMessage(R.string.autopayment_screen_error_maxsum_must_have)
                false
            }
            else -> true
        }
    }
}