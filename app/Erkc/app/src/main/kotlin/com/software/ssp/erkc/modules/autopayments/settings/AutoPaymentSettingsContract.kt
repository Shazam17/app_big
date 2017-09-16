package com.software.ssp.erkc.modules.autopayments.settings

import android.content.Context
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.models.PaymentMethod


interface IAutoPaymentSettingsView : IView {
    fun showPaymentTypeSelectDialog(autoPaymentMode: PaymentMethod)
    fun showReceiptSelectDialog(receipts: List<RealmReceipt>)
    fun showCardSelectDialog(cards: List<RealmCard>)

    fun showAutoPaymentMode(autoPaymentMode: PaymentMethod, selectedReceipt: RealmReceipt?)
    fun showReceiptDetails(receipt: RealmReceipt?)
    fun showCardDetails(card: RealmCard?)
    fun showCommissionPercent(percent: Double)

    fun setProgressVisibility(isVisible: Boolean)

    fun close()
}

interface IAutoPaymentSettingsPresenter : IPresenter<IAutoPaymentSettingsView> {
    var receiptId: String?
    var selectedReceipt: RealmReceipt?
    var selectedCard: RealmCard?

    fun onPaymentModeClick()
    fun onReceiptClick()
    fun onCardClick()
    fun onContinueButtonClick()

    fun onMaxSumChanged(text: String)

    fun onPaymentModeSelect(mode: PaymentMethod)
    fun onReceiptSelect(receipt: RealmReceipt?)
    fun onCardSelect(card: RealmCard?)
}