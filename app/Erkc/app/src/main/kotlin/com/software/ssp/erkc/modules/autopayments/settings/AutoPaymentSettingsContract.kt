package com.software.ssp.erkc.modules.autopayments.settings

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.AutoPaymentMode
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.realm.models.RealmReceipt

interface IAutoPaymentSettingsView : IView {
    fun showPaymentTypeSelectDialog(autoPaymentMode: AutoPaymentMode)
    fun showReceiptSelectDialog(receipts: List<RealmReceipt>)
    fun showCardSelectDialog(cards: List<RealmCard>)

    fun showAutoPaymentMode(autoPaymentMode: AutoPaymentMode)
    fun showReceiptDetails(receipt: RealmReceipt?)
    fun showCardDetails(card: RealmCard?)
    fun showCommissionPercent(percent: Double)

    fun setPendingVisible(isPending: Boolean)

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

    fun onPaymentModeSelect(mode: AutoPaymentMode)
    fun onReceiptSelect(receipt: RealmReceipt?)
    fun onCardSelect(card: RealmCard?)
}