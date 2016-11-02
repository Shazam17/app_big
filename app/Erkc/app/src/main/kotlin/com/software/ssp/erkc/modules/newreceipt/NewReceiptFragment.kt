package com.software.ssp.erkc.modules.newreceipt

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.args
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import kotlinx.android.synthetic.main.fragment_new_receipt.*
import org.jetbrains.anko.*
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import javax.inject.Inject


class NewReceiptFragment : MvpFragment(), INewReceiptView {

    @Inject lateinit var presenter: INewReceiptPresenter

    private var isTransferValueVisible: Boolean by args(defaultValue = true)
    private var isTransferValue: Boolean by args(defaultValue = false)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater!!.inflate(R.layout.fragment_new_receipt, container, false)  // todo change
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerNewReceiptComponent.builder()
                .appComponent(appComponent)
                .newReceiptModule(NewReceiptModule(this))
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        presenter.onViewAttached()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun navigateToBarCodeScanScreen() {
        // todo
        toast("navigateToBarCodeScanScreen")
    }

    override fun navigateToStreetSelectScreen() {
        // todo
        toast("navigateToStreetSelectScreen")
    }

    override fun navigateToIPUInputScreen(receipt: Receipt) {
        // todo
        toast("navigateToIPUInputScreen")
    }

    override fun navigateToPayScreen(receipt: Receipt) {
        // todo
        toast("navigateToPayScreen")
    }

    override fun showBarcodeError(errorStringResId: Int) {
        barcodeInputLayout.error = getString(errorStringResId)
    }

    override fun showStreetError(errorStringResId: Int) {
        streetInputLayout.error = getString(errorStringResId)
    }

    override fun showHouseError(errorStringResId: Int) {
        houseInputLayout.error = getString(errorStringResId)
    }

    override fun showApartmentError(errorStringResId: Int) {
        apartmentInputLayout.error = getString(errorStringResId)
    }

    override fun setBarcodeField(barcode: String) {
        barcodeEditText.setText(barcode)
        streetInputLayout.isEnabled = false
        houseInputLayout.isEnabled = false
        apartmentInputLayout.isEnabled = false
        streetEditText.setText("")
        houseEditText.setText("")
        apartmentEditText.setText("")
    }

    override fun showProgressVisible(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        continueButton.enabled = !isVisible
    }

    private fun initViews() {
        val slots = UnderscoreDigitSlotsParser().parseSlots(Constants.BARCODE_FORMAT)
        val barcodeWatcher = MaskFormatWatcher(MaskImpl.createTerminated(slots))
        barcodeWatcher.installOn(barcodeEditText)

        barcodeEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count ->
                barcodeInputLayout.error = null

                if (charSequence?.length == 0) {
                    streetInputLayout.isEnabled = true
                    houseInputLayout.isEnabled = true
                    apartmentInputLayout.isEnabled = true
                }
            }
        }

        streetEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> streetInputLayout.error = null }
        }

        houseEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> houseInputLayout.error = null }
        }

        apartmentEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> apartmentInputLayout.error = null }
        }

        apartmentEditText.onEditorAction { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                rootLayout.requestFocus()
                activity.hideKeyboard()
                true
            } else {
                false
            }
        }

        barCodeScanButton.onClick { presenter.onBarCodeScanButtonClick() }
        streetInputLayout.onClick { presenter.onAddressClick() }
        continueButton.onClick {
            presenter.onContinueClick(
                    barcodeEditText.text.toString(),
                    streetEditText.text.toString(),
                    houseEditText.text.toString(),
                    apartmentEditText.text.toString(),
                    isTransferValue,
                    streetInputLayout.isEnabled)
        }

        sendValueCheckBox.visibility = if (isTransferValueVisible) View.VISIBLE else View.GONE
        sendValueCheckBox.setOnCheckedChangeListener { compoundButton, isChecked -> isTransferValue = isChecked }
    }
}
