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
import kotlinx.android.synthetic.main.fragment_main_authed_add_receipt.*
import kotlinx.android.synthetic.main.fragment_non_authed_main_screen.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onEditorAction
import org.jetbrains.anko.toast
import javax.inject.Inject


class AuthedAddReceiptFragment : MvpFragment(), IAuthedAddReceiptView {

    @Inject lateinit var presenter: IAuthedAddReceiptPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_main_authed_add_receipt, container, false)  // todo change
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
        toast("navigateToIPUinputScreen")
    }

    override fun navigateToPayScreen() {
        // todo
        toast("navigateToPayScreen")
    }

    override fun showBarcodeError(errorStringResId: Int) {
        authedMainAddReceiptScreenBarcodeLayout.error = getString(errorStringResId)
    }

    override fun showStreetError(errorStringResId: Int) {
        authedMainAddReceiptScreenStreetEditText.error = getString(errorStringResId)
    }

    override fun showHouseError(errorStringResId: Int) {
        authedMainAddReceiptScreenHouseEditText.error = getString(errorStringResId)
    }

    override fun showApartmentError(errorStringResId: Int) {
        authedMainAddReceiptScreenApartmentEditText.error = getString(errorStringResId)
    }

    override fun setBarcodeField(barcode: String) {
        authedMainAddReceiptScreenBarcodeEditText.setText(barcode)
    }

    private fun initViews() {
        authedMainAddReceiptBarCodeScanButton.onClick { presenter.onBarCodeScanButtonClick() }
        authedMainAddReceiptStreetLayout.onClick { presenter.onAddressClick() }
        authedMainAddReceiptScreenContinueButton.onClick {
            presenter.onContinueClick(
                    authedMainAddReceiptScreenBarcodeEditText.text.toString(),
                    authedMainAddReceiptScreenStreetEditText.text.toString(),
                    authedMainAddReceiptScreenHouseEditText.text.toString(),
                    authedMainAddReceiptScreenApartmentEditText.text.toString(),
                    authedAddReceiptCounterCheckBox.isChecked)
        }

        authedMainAddReceiptScreenApartmentEditText.onEditorAction { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mainScreenRootLayout.requestFocus()
                activity.hideKeyboard()
                true
            } else {
                false
            }
        }
    }

}