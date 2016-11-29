package com.software.ssp.erkc.modules.paymentscreen.payment

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.LinearLayout
import android.widget.RadioButton
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.models.User
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.setTextColorByContextCompat
import com.software.ssp.erkc.modules.confirmbyurl.ConfirmByUrlActivity
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.confirm_payment_layout.view.*
import org.jetbrains.anko.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 10/11/2016.
 */
class PaymentActivity : MvpActivity(), IPaymentView {

    @Inject lateinit var presenter: IPaymentPresenter
    private var receipt: Receipt? = null
    private var userCard: Card? = null

    companion object {
        const val RADIO_BUTTON_TAG = "RADIO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        receipt = intent.getParcelableExtra<Receipt>(Constants.KEY_RECEIPT)
        initViews()
        presenter.onViewAttached(receipt!!)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun close() {
        finish()
    }

    override fun navigateToResult(url: String) {
        finish()
        startActivity<ConfirmByUrlActivity>(Constants.KEY_URL to url)
    }

    override fun showConfirmDialog(commission: Double, amount: Double, email: String) {
        val layoutParamsWithMargin = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParamsWithMargin.bottomMargin = 16
        alert {
            val view = LayoutInflater.from(this.ctx).inflate(R.layout.confirm_payment_layout, null, false)
            view.paymentConfirmBarcode.text = "${receipt?.barcode} (${receipt?.name})"
            view.paymentConfirmAddress.text = receipt?.address
            view.paymentConfirmCommission.text = getString(R.string.payment_commission).format(commission, receipt?.persent)
            view.paymentConfirmAmount.text = getString(R.string.payment_sum).format(amount)
            view.paymentConfirmCardName.text = userCard?.name
            view.paymentConfirmCardNo.text = userCard?.maskCardNo
            customView(view)
            positiveButton(R.string.payment_dialog_ok, {
                presenter.onConfirmClick(receipt!!, userCard, getString(R.string.payment_sum).format(amount), email)
                dismiss()
            })
            negativeButton(R.string.payment_dialog_cancel, {
                dismiss()
            })
        }.show()
    }

    override fun fillAmountAndCommission(commission: Double, sum: Double) {
        paymentCommission.text = getString(R.string.payment_commission).format(commission, receipt?.persent)
        paymentAmount.text = getString(R.string.payment_sum).format(sum)
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerPaymentComponent.builder()
                .appComponent(appComponent)
                .paymentModule(PaymentModule(this))
                .build()
                .inject(this)
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

    override fun fillData(user: User?, cards: List<Card>) {
        if (user != null) {
            paymentEmail.setText(user.email)
            paymentCardAdd.visibility = if (cards.isEmpty()) View.VISIBLE else View.GONE
            paymentCardWrapper.visibility = if (cards.isEmpty()) View.GONE else View.VISIBLE
            if (cards.isNotEmpty()) {
                userCard = if (receipt?.linkedCardId == null || receipt?.linkedCardId.equals("1")) cards.first() else cards.find { card -> card.id == receipt?.linkedCardId }
            }
            paymentCardWrapper.onClick {
                presenter.onChooseCardClick(cards)
            }
            paymentCardNo.text = userCard?.maskCardNo
            paymentCardName.text = userCard?.name
        } else {
            paymentCardChooseContainer.visibility = View.GONE
        }
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun setProgressVisibility(isVisible: Boolean) {
        paymentCardAdd.enabled = !isVisible
        paymentEmail.enabled = !isVisible
        paymentSum.enabled = !isVisible
        paymentDoneButton.enabled = !isVisible
        paymentAcceptCheckbox.enabled = !isVisible
        paymentCardWrapper.isClickable = !isVisible
        paymentProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showReceiptInfo(receipt: Receipt) {
        paymentDebts.text = receipt.amount.toString().format(2) + getString(R.string.payment_currency_symbol)
        paymentBarcode.text = "${receipt.barcode} (${receipt?.name})"
        paymentAddress.text = receipt.address
        paymentSum.setText(receipt.amount.toString())
    }

    override fun generateCardsChooseLayout(cards: List<Card>) {
        alert {
            customTitle {
                textView {
                    text = getString(R.string.payment_dialog_cards_title)
                    gravity = Gravity.CENTER
                    textSize = 20f
                    padding = 16
                    setTypeface(null, Typeface.BOLD)
                    textColor = ContextCompat.getColor(this.context, R.color.colorPaymentBodyText)
                }
            }
            customView {
                scrollView {
                    verticalLayout {
                        padding = 16
                        radioGroup {
                            for (card in cards) {
                                linearLayout {
                                    orientation = LinearLayout.HORIZONTAL
                                    gravity = Gravity.CENTER_VERTICAL
                                    radioButton {
                                        isChecked = card.id == userCard?.id
                                        highlightColor = ContextCompat.getColor(this.context, R.color.colorPrimary)
                                        tag = RADIO_BUTTON_TAG
                                        isFocusable = false
                                        isClickable = false
                                    }.onCheckedChange { compoundButton, b ->
                                        if (b) {
                                            userCard = card
                                        }
                                    }
                                    verticalLayout {
                                        padding = 16
                                        textView {
                                            isFocusable = false
                                            isClickable = false
                                            text = card.maskCardNo
                                            textSize = 16f
                                            textColor = ContextCompat.getColor(this.context, R.color.colorPaymentBodyText)
                                        }
                                        textView {
                                            isFocusable = false
                                            isClickable = false
                                            text = card.name
                                            textSize = 14f
                                            textColor = ContextCompat.getColor(this.context, R.color.colorPaymentSubText)
                                        }
                                    }

                                }.onClick {
                                    (this.findViewWithTag(RADIO_BUTTON_TAG) as RadioButton).isChecked = true
                                }

                            }
                        }
                    }
                }
            }
            positiveButton(R.string.payment_dialog_ok, {
                paymentCardNo.text = userCard?.maskCardNo
                paymentCardName.text = userCard?.name
                dismiss()
            })
            negativeButton(R.string.payment_dialog_cancel, {
                dismiss()
            })
        }.show()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        paymentButton.onClick {
            presenter.onNextClick(receipt!!, userCard, paymentSum.text.toString(), paymentEmail.text.toString())
        }

        paymentCardAdd.onClick {
            presenter.onAddCardClick()
        }
        paymentAcceptCheckbox.onCheckedChange { compoundButton, b ->
            paymentButton.isEnabled = b
        }
        paymentEmail.textChangedListener {
            onTextChanged { charSequence, start, before, count -> paymentEmailLayout.error = null }
        }
        paymentSum.textChangedListener {
            onTextChanged { charSequence, start, before, count ->
                paymentSumLayout.error = null
                presenter.onSumChange(charSequence.toString(), receipt!!.persent)
            }
        }
        paymentDoneButton.onClick {
            presenter.onDoneClick()
        }
    }

}