package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.modules.barcodescanner.BarcodeScannerActivity
import com.software.ssp.erkc.modules.signin.SignInActivity
import com.software.ssp.erkc.modules.signup.SignUpActivity
import kotlinx.android.synthetic.main.fragment_non_authed_main_screen.*
import org.jetbrains.anko.*
import javax.inject.Inject

class NonAuthedMainScreenFragment : MvpFragment(), INonAuthedMainScreenView {

    @Inject lateinit var presenter: INonAuthedMainScreenPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
    }

    override fun beforeDestroy() {
        presenter.dropView()
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
    }

    override fun navigateToSignInScreen() {
        startActivity<SignInActivity>()
    }

    override fun navigateToSignUpScreen() {
        startActivity<SignUpActivity>()
    }

    override fun navigateToPaymentScreen() {
        showMessage("TODO: NavigateToPayment")
    }

    override fun navigateToSendValuesScreen() {
        showMessage("TODO: NavigateToSendValues")
    }

    private fun initViews() {
        mainScreenBarcodeEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count ->
                mainScreenBarcodeLayout.error = null
            }
        }

        mainScreenStreetEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> mainScreenStreetLayout.error = null }
        }

        mainScreenHouseEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> mainScreenHouseLayout.error = null }
        }

        mainScreenApartmentEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> mainScreenApartmentLayout.error = null }
        }

        mainScreenApartmentEditText.onEditorAction { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hiddenViewForFocus.requestFocus()
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
                    sendValueCheckBox.isChecked)
        }

        mainScreenCameraButton.onClick {
            startActivityForResult<BarcodeScannerActivity>(Constants.REQUEST_CODE_BARCODE_SCAN)
        }

        mainScreenSingInButton.onClick { navigateToSignInScreen() }
        mainScreenRegistrationButton.onClick { navigateToSignUpScreen() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_CODE_BARCODE_SCAN ->
                if (resultCode == Activity.RESULT_OK) {
                    mainScreenBarcodeEditText.setText(data!!.getStringExtra(Constants.KEY_SCAN_RESULT))
                }
        }
    }
}

