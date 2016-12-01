package com.software.ssp.erkc.modules.autopayments.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.models.AutoPaymentMode
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.materialDialog
import kotlinx.android.synthetic.main.activity_autopayment_settings.*
import org.jetbrains.anko.onCheckedChange
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textChangedListener
import javax.inject.Inject


class AutoPaymentSettingsActivity : MvpActivity(), IAutoPaymentSettingsView {

    @Inject lateinit var presenter: IAutoPaymentSettingsPresenter

    private var receiptId: String? by extras("receiptId")

    private var autoPaymentModeString: String = ""
    private var oneClickModeString: String = ""
    private var noPaymentModeString: String = ""

    private var dialogRadioButtons: MutableList<RadioButton> = mutableListOf()

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerAutoPaymentSettingsComponent.builder()
                .appComponent(appComponent)
                .autoPaymentSettingsModule(AutoPaymentSettingsModule(this))
                .build()
                .inject(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autopayment_settings)

        initViews()

        presenter.incomingReceiptId = receiptId  // todo test
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showPaymentTypeSelectDialog(autoPaymentMode: AutoPaymentMode) {
        materialDialog {
            title(R.string.autopayment_screen_payment_type_label)
            items(listOf(autoPaymentModeString, oneClickModeString))
            itemsCallbackSingleChoice(autoPaymentMode.ordinal - 1, {
                dialog, view, which, text ->
                true
            })
            onPositive { materialDialog, dialogAction ->
                presenter.onPaymentModeSelect(AutoPaymentMode.values()[materialDialog.selectedIndex])
            }
        }.show()
    }

    override fun showAutoPaymentMode(autoPaymentMode: AutoPaymentMode) {
        when (autoPaymentMode) {
            AutoPaymentMode.OFF -> paymentModeSelectTextView.text = noPaymentModeString
            AutoPaymentMode.AUTO -> paymentModeSelectTextView.text = autoPaymentModeString
            AutoPaymentMode.ONE_CLICK -> paymentModeSelectTextView.text = oneClickModeString
        }

        setMaxSumVisibility(autoPaymentMode == AutoPaymentMode.AUTO)
    }

    override fun showReceiptSelectDialog(receipts: List<RealmReceipt>) {
        val title = getString(R.string.autopayment_screen_receipt_label)

        val dialog = materialDialog {
            title(title)
            customView(R.layout.dialog_custom_view_void, true)
            positiveText(R.string.autopayment_screen_receipt_button_ok)
            onPositive { materialDialog, dialogAction ->
                materialDialog.dismiss()
                presenter.onDialogClose()
            }
            negativeText(R.string.autopayment_screen_receipt_button_close)
            onNegative { materialDialog, dialogAction -> materialDialog.dismiss() }

        }.build()

        addItemsToReceiptDialog(dialog, receipts)

        dialog.show()
    }

    override fun showCardSelectDialog(cards: List<RealmCard>) {
        val title = getString(R.string.autopayment_screen_cardpayment_label)

        val dialog = materialDialog {
            title(title)
            customView(R.layout.dialog_custom_view_void, true)
            positiveText(R.string.autopayment_screen_receipt_button_ok)
            onPositive { materialDialog, dialogAction ->
                materialDialog.dismiss()
                presenter.onDialogClose()
            }
            negativeText(R.string.autopayment_screen_receipt_button_close)
            onNegative { materialDialog, dialogAction -> materialDialog.dismiss() }

        }.build()

        addItemsToCardDialog(dialog, cards)

        dialog.show()
    }



    override fun showReceiptDetails(receipt: RealmReceipt?) {
        if (receipt == null) {
            receiptIdTextView.text = getString(R.string.autopayment_screen_receipt_add_button_label)
            receiptTypeTextView.text = ""
            receiptAddressTextView.text = ""
            maxSumEditText.setText("")
            showComissionPercent(0.0)
        } else {
            receiptIdTextView.text = receipt.barcode   // todo format
            receiptTypeTextView.text = receipt.name
            receiptAddressTextView.text = receipt.address
            maxSumEditText.setText("${receipt.maxSum}")
            showComissionPercent(receipt.percent)
        }
    }

    override fun showCardDetails(card: RealmCard?) {
        if (card == null) {
            cardIdTextView.text = getString(R.string.autopayment_screen_receipt_add_button_label)
            cardTypeTextView.text = ""
        } else {
            cardIdTextView.text = card.maskedCardNumber
            cardTypeTextView.text = card.name
        }
    }

    override fun setMaxSumVisibility(visible: Boolean) {
        maxSumTextInputLayout.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun showComissionPercent(percent: Double) {
        val agreementTemplate = getString(R.string.autopayment_screen_agreement_checkbox_label)
        val notZeroPercent = percent != 0.0

        agreementTextView.visibility = if (notZeroPercent) View.VISIBLE else View.GONE
        agreementCheckBox.visibility = if (notZeroPercent) View.VISIBLE else View.GONE
        agreementTextView.text = String.format(agreementTemplate, percent)
    }

    override fun dismiss() {
        onBackPressed()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        supportActionBar?.title = getString(R.string.autopayment_screen_tab_title)

        autoPaymentModeString = getString(R.string.autopayment_screen_autopayment_mode_text)
        oneClickModeString = getString(R.string.autopayment_screen_oneclick_mode_text)
        noPaymentModeString = getString(R.string.autopayment_screen_payment_no_mode_text)

        paymentModeSelectTextView.onClick { presenter.onPaymentModeClick() }

        receiptIdTextView.onClick { presenter.onReceiptClick() }
        receiptTypeTextView.onClick { presenter.onReceiptClick() }
        receiptAddressTextView.onClick { presenter.onReceiptClick() }

        cardIdTextView.onClick { presenter.onCardClick() }
        cardTypeTextView.onClick { presenter.onCardClick() }

        agreementCheckBox.onCheckedChange { compoundButton, checked -> presenter.onAgreementChecked(checked) }

        maxSumEditText.textChangedListener {
            onTextChanged { text, start, before, count -> presenter.onMaxSumChanged(text.toString()) }
        }

        continueButton.onClick { presenter.onContinueButtonClick() }

    }

    private fun addItemsToReceiptDialog(dialog: MaterialDialog, receipts: List<RealmReceipt>) {
        val rootView = dialog.customView as LinearLayout

        dialogRadioButtons = mutableListOf()

        for (receipt in receipts) {
            val receiptItemView = layoutInflater.inflate(R.layout.item_autopayment_dialog, rootView, false)
            val titleTextView = receiptItemView.findViewById(R.id.titleTextView) as TextView
            val subtitleTextView = receiptItemView.findViewById(R.id.subtitleTextView) as TextView
            val radioButton = receiptItemView.findViewById(R.id.radioButton) as RadioButton

            titleTextView.text = "${receipt.barcode}\n${receipt.name}"
            subtitleTextView.text = "${receipt.address}"
            radioButton.isChecked = receipt.id == presenter.editingReceipt?.id

            radioButton.onCheckedChange { compoundButton, checked ->
                if (checked) {
                    presenter.onReceiptSelect(receipt)
                }
            }

            dialogRadioButtons.add(radioButton)
            receiptItemView.onClick {
                dialogRadioButtons.forEach { it.isChecked = false }
                radioButton.isChecked = true
            }

            rootView.addView(receiptItemView)
        }
    }

    private fun addItemsToCardDialog(dialog: MaterialDialog, cards: List<RealmCard>) {
        val rootView = dialog.customView as LinearLayout

        dialogRadioButtons = mutableListOf()

        for (card in cards) {
            val receiptItemView = layoutInflater.inflate(R.layout.item_autopayment_dialog, rootView, false)
            val titleTextView = receiptItemView.findViewById(R.id.titleTextView) as TextView
            val subtitleTextView = receiptItemView.findViewById(R.id.subtitleTextView) as TextView
            val radioButton = receiptItemView.findViewById(R.id.radioButton) as RadioButton

            titleTextView.text = "${card.maskedCardNumber}"
            subtitleTextView.text = card.name
            radioButton.isChecked = card.id == presenter.editingReceipt?.linkedCard?.id

            radioButton.onCheckedChange { compoundButton, checked ->
                if (checked) {
                    presenter.onCardSelect(card)
                }
            }

            dialogRadioButtons.add(radioButton)
            receiptItemView.onClick {
                dialogRadioButtons.forEach { it.isChecked = false }
                radioButton.isChecked = true
            }
            rootView.addView(receiptItemView)
        }
    }

}