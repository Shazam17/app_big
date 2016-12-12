package com.software.ssp.erkc.modules.paymentscreen.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.RadioButton
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.extensions.setTextColorByContextCompat
import com.software.ssp.erkc.modules.confirmbyurl.ConfirmByUrlActivity
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.confirm_payment_layout.view.*
import kotlinx.android.synthetic.main.item_autopayment_dialog.view.*
import org.jetbrains.anko.*
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 10/11/2016.
 */
class PaymentActivity : MvpActivity(), IPaymentView {

    @Inject lateinit var presenter: IPaymentPresenter
    private var receipt: Receipt? by extras()
    private var receiptId: String? by extras()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        receipt?.let {
            presenter.receipt = receipt!!
        }

        presenter.receiptId = receiptId

        initViews()
        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerPaymentComponent.builder()
                .appComponent(appComponent)
                .paymentModule(PaymentModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                close()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun setProgressVisibility(isVisible: Boolean) {
        paymentCardAdd.enabled = !isVisible
        paymentEmail.enabled = !isVisible
        paymentSum.enabled = !isVisible
        paymentDoneButton.enabled = !isVisible
        paymentButton.enabled = paymentAcceptCheckbox.isChecked && !isVisible
        paymentAcceptCheckbox.enabled = !isVisible
        paymentCardWrapper.isClickable = !isVisible
        paymentAcceptCheckbox.enabled = !isVisible
        paymentProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showReceiptInfo(receipt: Receipt) {
        paymentDebts.text = getString(R.string.payment_sum).format(receipt.amount)
        paymentBarcode.text = "${receipt.barcode} (${receipt?.name})"
        paymentAddress.text = receipt.address
        paymentCommissionPercents.text = getString(R.string.payment_commission_percent).format(receipt.percent)
        paymentSum.setText(receipt.amount.toString())
    }

    override fun showReceiptInfo(receipt: RealmReceipt) {
        paymentDebts.text = getString(R.string.payment_sum).format(receipt.amount)
        paymentBarcode.text = "${receipt.barcode} (${receipt?.name})"
        paymentAddress.text = receipt.address
        paymentCommissionPercents.text = getString(R.string.payment_commission_percent).format(receipt.percent)
        paymentSum.setText(receipt.amount.toString())
        paymentCardChooseContainer.visibility = View.VISIBLE
    }

    override fun showSelectedCard(card: RealmCard?) {
        if (card != null) {
            paymentCardAdd.visibility = View.GONE
            paymentCardWrapper.visibility = View.VISIBLE
            paymentCardNo.text = card.maskedCardNumber
            paymentCardName.text = card.name
        } else {
            paymentCardAdd.visibility = View.VISIBLE
            paymentCardWrapper.visibility = View.GONE
        }
    }

    override fun showUserInfo(user: RealmUser) {
        paymentEmail.setText(user.email)
    }

    override fun showPaymentConfirmDialog(receipt: RealmReceipt, card: RealmCard, commission: Double, sum: Double, email: String) {
        materialDialog {
            val view = LayoutInflater.from(this.context).inflate(R.layout.confirm_payment_layout, null, false)
            view.paymentConfirmBarcode.text = "${receipt.barcode} (${receipt.name})"
            view.paymentConfirmAddress.text = receipt.address
            view.paymentConfirmCommission.text = getString(R.string.payment_commission).format(commission, receipt.percent)
            view.paymentConfirmAmount.text = getString(R.string.payment_sum).format(sum)
            view.paymentConfirmCardName.text = card.name
            view.paymentConfirmCardNo.text = card.maskedCardNumber
            customView(view, true)
            positiveText(R.string.payment_dialog_ok)
            onPositive { materialDialog, dialogAction ->
                presenter.onPaymentConfirmClick(email)
            }
            negativeText(R.string.payment_dialog_cancel)
        }.show()
    }

    override fun showNavigateToCardsDialog() {
        materialDialog {
            content(R.string.payment_navigate_to_cards_dialog_text)
            positiveText(R.string.payment_navigate_to_cards_dialog_positive)
            negativeText(R.string.payment_navigate_to_cards_dialog_negative)
            onPositive { materialDialog, dialogAction ->
                presenter.onNavigateToCardsConfirmClick()
            }
        }.show()
    }

    override fun fillAmountAndCommission(commission: Double, sum: Double) {
        paymentCommissionSum.text = getString(R.string.payment_commission_sum).format(commission)
        paymentAmount.text = getString(R.string.payment_sum).format(sum)
    }

    override fun showSumError(errorRes: Int) {
        paymentSumLayout.error = getString(errorRes)
    }

    override fun showEmailError(errorRes: Int) {
        paymentEmailLayout.error = getString(errorRes)
    }

    override fun showResult(result: Boolean) {
        paymentContainer.visibility = View.GONE
        paymentResultContainer.visibility = View.VISIBLE
        if (result) {
            paymentResultImageView.setImageResource(R.drawable.ic_circle_success)
            paymentResultTextView.setText(R.string.payment_result_success)
            paymentResultTextView.setTextColorByContextCompat(R.color.colorPaymentResultSuccess)
        } else {
            paymentResultImageView.setImageResource(R.drawable.ic_circle_warning)
            paymentResultTextView.setText(R.string.payment_result_error)
            paymentResultTextView.setTextColorByContextCompat(R.color.colorPaymentResultError)
        }
    }

    override fun showCardSelectDialog(cardsViewModels: List<PaymentCardViewModel>) {
        val dialog = materialDialog {
            title(R.string.payment_dialog_cards_title)
            customView(R.layout.dialog_custom_view_void, true)
            positiveText(R.string.autopayment_screen_receipt_button_ok)
            onPositive { materialDialog, dialogAction ->
                presenter.onCardSelected(cardsViewModels.find { it.isSelected }?.card)
                materialDialog.dismiss()
            }
            negativeText(R.string.autopayment_screen_receipt_button_close)
            onNegative { materialDialog, dialogAction ->
                materialDialog.dismiss()
            }
        }.build()

        val rootView = dialog.customView as LinearLayout
        val dialogRadioButtons = ArrayList<RadioButton>()

        cardsViewModels.forEach {
            with(layoutInflater.inflate(R.layout.item_card_select_dialog, rootView, false)) {
                titleTextView.text = it.card.maskedCardNumber
                subtitleTextView.text = it.card.name
                radioButton.isChecked = it.isSelected

                radioButton.onCheckedChange { compoundButton, checked ->
                    it.isSelected = checked
                }

                dialogRadioButtons.add(radioButton)

                onClick {
                    dialogRadioButtons.forEach { it.isChecked = false }
                    radioButton.isChecked = true
                }

                rootView.addView(this)
            }
        }

        with(layoutInflater.inflate(R.layout.item_card_select_dialog, rootView, false)) {
            titleTextView.text = getString(R.string.payment_non_selected_card)
            radioButton.isChecked = cardsViewModels.indexOfFirst { it.isSelected } == -1

            dialogRadioButtons.add(radioButton)

            onClick {
                dialogRadioButtons.forEach { it.isChecked = false }
                radioButton.isChecked = true
            }

            rootView.addView(this)
        }

        dialog.show()
    }

    override fun navigateToResult(url: String) {
        finish()
        startActivity<ConfirmByUrlActivity>(Constants.KEY_URL to url)
    }

    override fun close() {
        finish()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)

        val listener = MaskedTextChangedListener(
                "[099999999]{.}[99]",
                false,
                paymentSum,
                null,
                object : MaskedTextChangedListener.ValueListener {
                    override fun onExtracted(value: String) {
                        paymentSumLayout.error = null
                        presenter.onSumTextChange(value)
                    }

                    override fun onMandatoryCharactersFilled(complete: Boolean) {
                    }
                }
        )

        paymentSum.addTextChangedListener(listener)
        paymentSum.onFocusChangeListener = listener

        paymentEmail.textChangedListener {
            onTextChanged { charSequence, start, before, count ->
                paymentEmailLayout.error = null
            }
        }

        paymentEmail.onEditorAction { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                rootLayout.requestFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        paymentButton.onClick {
            presenter.onNextClick(paymentEmail.text.toString())
        }

        paymentCardAdd.onClick {
            presenter.onChooseCardClick()
        }

        paymentCardWrapper.onClick {
            presenter.onChooseCardClick()
        }

        paymentAcceptCheckbox.onCheckedChange { compoundButton, isChecked ->
            paymentButton.isEnabled = isChecked
        }

        paymentDoneButton.onClick {
            presenter.onDoneClick()
        }
    }
}
