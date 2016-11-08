package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.modules.address.SearchAddressActivity
import com.software.ssp.erkc.modules.barcodescanner.BarcodeScannerActivity
import com.software.ssp.erkc.modules.signin.SignInActivity
import com.software.ssp.erkc.modules.signup.SignUpActivity
import com.software.ssp.erkc.utils.splitFullAddress
import kotlinx.android.synthetic.main.fragment_non_authed_main_screen.*
import org.jetbrains.anko.*
import javax.inject.Inject

class NonAuthedMainScreenFragment : MvpFragment(), INonAuthedMainScreenView {

    @Inject lateinit var presenter: INonAuthedMainScreenPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater!!.inflate(R.layout.fragment_non_authed_main_screen, container, false)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerNonAuthedMainScreenComponent.builder()
                .appComponent(appComponent)
                .nonAuthedMainScreenModule(NonAuthedMainScreenModule(this))
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        presenter.onViewAttached()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.clear()
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

    override fun navigateToStreetSelectScreen() {
        startActivityForResult<SearchAddressActivity>(Constants.REQUEST_CODE_ADDRESS_FIND)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun showMessage(message: String) {
        mainScreenBarcodeLayout.error = message
    }

    override fun showErrorBarcodeMessage(resId: Int) {
        mainScreenBarcodeLayout.error = getString(resId)
    }

    override fun showErrorStreetMessage(resId: Int) {
        mainScreenStreetLayout.error = getString(resId)
    }

    override fun showErrorHouseMessage(resId: Int) {
        mainScreenHouseLayout.error = getString(resId)
    }

    override fun showErrorApartmentMessage(resId: Int) {
        mainScreenApartmentLayout.error = getString(resId)
    }

    override fun showScannedBarcode(code: String) {
        mainScreenBarcodeEditText.setText(code)
        mainScreenStreetLayout.isEnabled = false
        mainScreenHouseLayout.isEnabled = false
        mainScreenApartmentLayout.isEnabled = false
        mainScreenStreetEditText.setText("")
        mainScreenHouseEditText.setText("")
        mainScreenApartmentEditText.setText("")
    }

    override fun navigateToSignInScreen() {
        startActivity<SignInActivity>()
    }

    override fun navigateToSignUpScreen() {
        startActivity<SignUpActivity>()
    }

    override fun navigateToPaymentScreen(receipt: Receipt) {
        //TODO: NavigateToPayment
        showMessage("TODO: NavigateToPayment")
    }

    override fun navigateToEnterValues(receipt: Receipt){
        //TODO: NavigateToEnterValues
        showMessage("TODO: NavigateToSendValues")
    }

    override fun showProgressVisible(isVisible: Boolean) {
        mainScreenContinueButton.enabled = !isVisible
        mainScreenSingInButton.enabled = !isVisible
        mainScreenRegistrationButton.enabled = !isVisible
        mainScreenProgressBar.visibility = if(isVisible) View.VISIBLE else View.GONE
    }

    override fun setStreetField(street: String) {
        mainScreenStreetEditText.setText(street)
    }

    override fun showReceiptData(receipt: Receipt) {
        mainScreenBarcodeEditText.setText(receipt.barcode)

        mainScreenStreetLayout.isEnabled = false
        mainScreenHouseLayout.isEnabled = false
        mainScreenApartmentLayout.isEnabled = false

        val addressParts = splitFullAddress(receipt.address)
        val street = addressParts[0]
        val house = if (addressParts.size > 1) addressParts[1] else ""
        val apartment = if (addressParts.size > 2) addressParts[2] else ""

        mainScreenStreetEditText.setText(street)
        mainScreenHouseEditText.setText(house)
        mainScreenApartmentEditText.setText(apartment)
    }

    private fun initViews() {
        mainScreenBarcodeEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count ->
                mainScreenBarcodeLayout.error = null

                if (charSequence?.length == 0) {
                    mainScreenStreetLayout.isEnabled = true
                    mainScreenHouseLayout.isEnabled = true
                    mainScreenApartmentLayout.isEnabled = true
                }
            }
        }

        mainScreenStreetEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> mainScreenStreetLayout.error = null }
        }


        mainScreenStreetLayout.isHintAnimationEnabled = false
        mainScreenStreetEditText.onTouch { view, motionEvent ->
                    when {
                        motionEvent.actionMasked == MotionEvent.ACTION_UP -> {
                            mainScreenRootLayout.requestFocus()
                            presenter.onAddressClick()
                            true
                        }
                        else -> true
                    }
                }

        mainScreenHouseEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> mainScreenHouseLayout.error = null }
        }

        mainScreenApartmentEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> mainScreenApartmentLayout.error = null }
        }

        mainScreenApartmentEditText.onEditorAction { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mainScreenRootLayout.requestFocus()
                activity.hideKeyboard()
                true
            } else {
                false
            }
        }

        mainScreenContinueButton.onClick {
            presenter.onContinueClick(mainScreenBarcodeEditText.text.toString(),
                    mainScreenStreetEditText.text.toString(),
                    mainScreenHouseEditText.text.toString(),
                    mainScreenApartmentEditText.text.toString(),
                    sendValueCheckBox.isChecked,
                    mainScreenStreetLayout.isEnabled)
        }

        mainScreenCameraButton.onClick {
            startActivityForResult<BarcodeScannerActivity>(Constants.REQUEST_CODE_BARCODE_SCAN)
        }

        mainScreenSingInButton.onClick { navigateToSignInScreen() }
        mainScreenRegistrationButton.onClick { navigateToSignUpScreen() }
    }
}

