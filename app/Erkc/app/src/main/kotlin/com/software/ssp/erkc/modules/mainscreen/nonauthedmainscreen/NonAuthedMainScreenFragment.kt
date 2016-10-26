package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
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

    override fun navigateToPaymentScreen() {
        //TODO: NavigateToPayment
        showMessage("TODO: NavigateToPayment")
    }

    override fun navigateToSendValuesScreen() {
        //TODO: NavigateToSendValues
        showMessage("TODO: NavigateToSendValues")
    }

    override fun showProgressVisible(isVisible: Boolean) {
        mainScreenContinueButton.enabled = !isVisible
        mainScreenSingInButton.enabled = !isVisible
        mainScreenRegistrationButton.enabled = !isVisible
        mainScreenProgressBar.visibility = if(isVisible) View.VISIBLE else View.GONE
    }

    private fun initViews() {
        mainScreenBarcodeEditText.textChangedListener {

            onTextChanged { charSequence, start, before, count ->
                mainScreenBarcodeLayout.error = null
            }

            afterTextChanged { text ->
                text?.let {
                    if (text.length == 0) {
                        mainScreenStreetLayout.isEnabled = true
                        mainScreenHouseLayout.isEnabled = true
                        mainScreenApartmentLayout.isEnabled = true
                    }
                }
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
            //TODO Scan BarCode presenter.onBarCodeScanned(code)
        }

        mainScreenSingInButton.onClick { navigateToSignInScreen() }
        mainScreenRegistrationButton.onClick { navigateToSignUpScreen() }
    }
}

