package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.inDebugMode
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.modules.address.SearchAddressActivity
import com.software.ssp.erkc.modules.barcodescanner.BarcodeScannerActivity
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import com.software.ssp.erkc.modules.drawer.DrawerItem
import com.software.ssp.erkc.modules.paymentscreen.payment.PaymentActivity
import com.software.ssp.erkc.modules.sendvalues.SendValuesActivity
import com.software.ssp.erkc.modules.signin.SignInActivity
import com.software.ssp.erkc.modules.signup.SignUpActivity
import kotlinx.android.synthetic.main.fragment_non_authed_main_screen.*
import org.jetbrains.anko.*
import javax.inject.Inject

class NonAuthedMainScreenFragment : MvpFragment(), INonAuthedMainScreenView {

    @Inject lateinit var presenter: INonAuthedMainScreenPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_non_authed_main_screen, container, false)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            //TODO remake
            Constants.REQUEST_CODE_PAYMENT -> {
                (activity as DrawerActivity).navigateToDrawerItem(data?.getSerializableExtra(Constants.KEY_DRAWER_ITEM_FOR_SELECT) as DrawerItem)
            }
            BarcodeScannerActivity.BARCODE_SCANNER_REQUEST_CODE -> presenter.onBarCodeScanned(data!!.getStringExtra(BarcodeScannerActivity.BARCODE_SCANNED_RESULT_KEY))
            SearchAddressActivity.SEARCH_ADDRESS_REQUEST_CODE -> presenter.onStreetSelected(data!!.getStringExtra(SearchAddressActivity.SEARCH_ADDRESS_RESULT_KEY))
        }
    }

    override fun navigateToStreetSelectScreen() {
        startActivityForResult<SearchAddressActivity>(SearchAddressActivity.SEARCH_ADDRESS_REQUEST_CODE)
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
        activity.startActivityForResult<SignInActivity>(SignInActivity.SIGN_IN_REQUEST_CODE)
    }

    override fun navigateToSignUpScreen() {
        activity.startActivityForResult<SignUpActivity>(SignUpActivity.SIGN_UP_REQUEST_CODE)
    }

    override fun navigateToPaymentScreen(receipt: Receipt) {
        startActivityForResult<PaymentActivity>(Constants.REQUEST_CODE_PAYMENT, Constants.KEY_RECEIPT to receipt)
    }

    override fun navigateToSendValuesScreen(data: Receipt) {
        startActivity<SendValuesActivity>(Constants.KEY_RECEIPT to data)
    }

    override fun showProgressVisible(isVisible: Boolean) {
        mainScreenContinueButton.enabled = !isVisible
        mainScreenSingInButton.enabled = !isVisible
        mainScreenRegistrationButton.enabled = !isVisible
        mainScreenProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setStreetField(street: String) {
        mainScreenStreetEditText.setText(street)
    }

    override fun showReceiptData(receipt: Receipt) {
        mainScreenBarcodeEditText.setText(receipt.barcode)

        mainScreenStreetLayout.isEnabled = false
        mainScreenHouseLayout.isEnabled = false
        mainScreenApartmentLayout.isEnabled = false

        mainScreenStreetEditText.setText(receipt.street)
        mainScreenHouseEditText.setText(receipt.house)
        mainScreenApartmentEditText.setText(receipt.apart)
    }

    override fun fillStreet(street: String) {
        mainScreenStreetEditText.setText(street)
    }

    private fun initViews() {
        inDebugMode {
            mainScreenBarcodeEditText.setText("3524770401487")
        }
        mainScreenBarcodeEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count ->
                mainScreenBarcodeLayout.error = null

                if (charSequence?.length == 0) {
                    mainScreenStreetLayout.isEnabled = true
                    mainScreenHouseLayout.isEnabled = true
                    mainScreenApartmentLayout.isEnabled = true
                    mainScreenStreetEditText.setText("")
                    mainScreenHouseEditText.setText("")
                    mainScreenApartmentEditText.setText("")
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
        mainScreenStreetEditText.onClick {
            presenter.onAddressClick()
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
            startActivityForResult<BarcodeScannerActivity>(BarcodeScannerActivity.BARCODE_SCANNER_REQUEST_CODE)
        }

        mainScreenSingInButton.onClick { navigateToSignInScreen() }
        mainScreenRegistrationButton.onClick { navigateToSignUpScreen() }
    }

}

