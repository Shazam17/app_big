package com.software.ssp.erkc.modules.mainscreen.authedaddreceipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import kotlinx.android.synthetic.main.fragment_new_receipt.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onEditorAction
import org.jetbrains.anko.toast
import javax.inject.Inject


class AuthedAddReceiptFragment : MvpFragment(), IAuthedAddReceiptView {

    @Inject lateinit var presenter: IAuthedAddReceiptPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_new_receipt, container, false)  // todo change
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerAuthedAddReceiptScreenComponent.builder()
                .appComponent(appComponent)
                .authedAddReceiptScreenModule(AuthedAddReceiptScreenModule(this))
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
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

    override fun navigateToIPUinputScreen() {
        // todo
        toast("navigateToIPUInputScreen")
    }

    override fun navigateToPayScreen() {
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
    }

    private fun initViews() {
        barCodeScanButton.onClick { presenter.onBarCodeScanButtonClick() }
        streetInputLayout.onClick { presenter.onAddressClick() }
        continueButton.onClick {
            presenter.onContinueClick(
                    barcodeEditText.text.toString(),
                    streetEditText.text.toString(),
                    houseEditText.text.toString(),
                    apartmentEditText.text.toString(),
                    sendValueCheckBox.isChecked)
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
    }
}
