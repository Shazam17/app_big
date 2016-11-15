package com.software.ssp.erkc.modules.newreceipt

import android.app.Activity
import android.content.Intent
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
import com.software.ssp.erkc.modules.address.SearchAddressActivity
import com.software.ssp.erkc.modules.barcodescanner.BarcodeScannerActivity
import kotlinx.android.synthetic.main.fragment_new_receipt.*
import org.jetbrains.anko.*
import javax.inject.Inject


class NewReceiptFragment : MvpFragment(), INewReceiptView {

    @Inject lateinit var presenter: INewReceiptPresenter

    private var isTransferValueVisible: Boolean by args(defaultValue = true)
    private var isTransferValue: Boolean by args(defaultValue = false)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater!!.inflate(R.layout.fragment_new_receipt, container, false)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            Constants.REQUEST_CODE_BARCODE_SCAN -> presenter.onBarCodeScanned(data!!.getStringExtra(Constants.KEY_SCAN_RESULT))
            Constants.REQUEST_CODE_ADDRESS_FIND -> presenter.onAddressSelected(data!!.getStringExtra(Constants.KEY_ADDRESS_NAME_RESULT))
        }
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun navigateToBarCodeScanScreen() {
        startActivityForResult<BarcodeScannerActivity>(Constants.REQUEST_CODE_BARCODE_SCAN)
    }

    override fun navigateToStreetSelectScreen() {
        startActivityForResult<SearchAddressActivity>(Constants.REQUEST_CODE_ADDRESS_FIND)
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


    override fun showProgressVisible(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        continueButton.enabled = !isVisible
    }

    override fun showReceiptData(receipt: Receipt) {
        barcodeEditText.setText(receipt.barcode)

        streetInputLayout.isEnabled = false
        houseInputLayout.isEnabled = false
        apartmentInputLayout.isEnabled = false

        streetEditText.setText(receipt.street)
        houseEditText.setText(receipt.house)
        apartmentEditText.setText(receipt.apart)
    }

    override fun setStreetField(street: String) {
        streetEditText.setText(street)
    }

    private fun initViews() {
        barcodeEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count ->
                barcodeInputLayout.error = null

                if (charSequence?.length == 0) {
                    streetInputLayout.isEnabled = true
                    houseInputLayout.isEnabled = true
                    apartmentInputLayout.isEnabled = true
                    streetEditText.setText("")
                    houseEditText.setText("")
                    apartmentEditText.setText("")
                }
            }
        }

        streetEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> streetInputLayout.error = null }
        }

        streetInputLayout.isHintAnimationEnabled = false
        streetEditText.onTouch { view, motionEvent ->
            when {
                motionEvent.actionMasked == MotionEvent.ACTION_UP -> {
                    rootLayout.requestFocus()
                    presenter.onAddressClick()
                    true
                }
                else -> true
            }
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
