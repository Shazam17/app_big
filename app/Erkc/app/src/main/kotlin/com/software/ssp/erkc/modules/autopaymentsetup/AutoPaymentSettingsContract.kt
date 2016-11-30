package com.software.ssp.erkc.modules.autopaymentsetup

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.models.AutoPaymentMode

interface IAutoPaymentSettingsView : IView {
    fun showPaymentTypeSelect(receipt: RealmReceipt?)
    fun showReceiptSelect(receipts: List<RealmReceipt>)
    fun showCardSelect(cards: List<RealmCard>)

    fun showAutoPaymentMode(autopaymentMode: AutoPaymentMode)
    fun showReceiptDetails(receipt: RealmReceipt?)
    fun showCardDetails(card: RealmCard?)
    fun setMaxSumVisibility(visible: Boolean)
    fun showComissionPercent(percent: Double)

    fun dismiss()
}


interface IAutoPaymentSettingsPresenter : IPresenter<IAutoPaymentSettingsView> {
    val editingReceipt: RealmReceipt?
    var incomingReceiptId: String?

    fun onPaymentModeClick()
    fun onReceiptClick()
    fun onCardClick()
    fun onContinueButtonClick()

    fun onAgreementChecked(checked: Boolean = true)
    fun onMaxSumChanged(text: String)

    fun onPaymentModeSelect(mode: AutoPaymentMode)
    fun onReceiptSelect(receipt: RealmReceipt?)
    fun onCardSelect(card: RealmCard?)

    fun onDialogClose()
}