package com.software.ssp.erkc.modules.valuetransfer.newvaluetransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import kotlinx.android.synthetic.main.fragment_new_value_trasfer.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onEditorAction
import org.jetbrains.anko.textChangedListener
import javax.inject.Inject

class NewValueTransferFragment : MvpFragment(), INewValueTransferView {

    @Inject lateinit var presenter: INewValueTransferPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_new_value_trasfer, container, false)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerNewValueTransferComponent.builder()
                .appComponent(appComponent)
                .newValueTransferModule(NewValueTransferModule(this))
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
        newValueTransferBarcodeLayout.error = message
    }

    override fun showErrorBarcodeMessage(resId: Int) {
        newValueTransferBarcodeLayout.error = getString(resId)
    }

    override fun showErrorStreetMessage(resId: Int) {
        newValueTransferStreetLayout.error = getString(resId)
    }

    override fun showErrorHouseMessage(resId: Int) {
        newValueTransferHouseLayout.error = getString(resId)
    }

    override fun showErrorApartmentMessage(resId: Int) {
        newValueTransferApartmentLayout.error = getString(resId)
    }

    override fun showScannedBarcode(code: String) {
        newValueTransferBarcodeEditText.setText(code)
        newValueTransferStreetLayout.isEnabled = false
        newValueTransferHouseLayout.isEnabled = false
        newValueTransferApartmentLayout.isEnabled = false
        newValueTransferStreetEditText.setText("")
        newValueTransferHouseEditText.setText("")
        newValueTransferApartmentEditText.setText("")
    }

    override fun showProgressVisible(isVisible: Boolean) {
        newValueTransferContinueButton.enabled = !isVisible
        newValueTransferProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun navigateToEnterValues(receipt: Receipt) {
        //TODO: NavigateToEnterValues
        showMessage("TODO: NavigateToSendValues")
    }

    private fun initViews() {
        newValueTransferBarcodeEditText.textChangedListener {

            onTextChanged { charSequence, start, before, count ->
                newValueTransferBarcodeLayout.error = null

                if (charSequence?.length == 0) {
                    newValueTransferStreetLayout.isEnabled = true
                    newValueTransferHouseLayout.isEnabled = true
                    newValueTransferApartmentLayout.isEnabled = true
                }
            }
        }

        newValueTransferStreetEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> newValueTransferStreetLayout.error = null }
        }

        newValueTransferHouseEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> newValueTransferHouseLayout.error = null }
        }

        newValueTransferApartmentEditText.textChangedListener {
            onTextChanged { charSequence, start, before, count -> newValueTransferApartmentLayout.error = null }
        }

        newValueTransferApartmentEditText.onEditorAction { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                newValueTransferRootLayout.requestFocus()
                activity.hideKeyboard()
                true
            } else {
                false
            }
        }

        newValueTransferContinueButton.onClick {
            presenter.onContinueClick(newValueTransferBarcodeEditText.text.toString(),
                    newValueTransferStreetEditText.text.toString(),
                    newValueTransferHouseEditText.text.toString(),
                    newValueTransferApartmentEditText.text.toString(),
                    newValueTransferStreetLayout.isEnabled)
        }

        newValueTransferCameraButton.onClick {
            //TODO Scan BarCode presenter.onBarCodeScanned(code)
        }
    }
}
