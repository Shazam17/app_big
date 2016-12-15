package com.software.ssp.erkc.modules.history.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.PaymentMethod
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.getStringResId
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.extensions.toString
import com.software.ssp.erkc.modules.address.SearchAddressActivity
import com.software.ssp.erkc.modules.barcodescanner.BarcodeScannerActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_history_filter.*
import org.jetbrains.anko.*
import java.util.*
import javax.inject.Inject


class HistoryFilterActivity : MvpActivity(), IHistoryFilterView {

    @Inject lateinit var presenter: IHistoryFilterPresenter

    companion object {
        const val REQUEST_CODE = 24222
        const val RESULT_KEY = "history_filter_result_key"
        const val KEY_CURRENT_FILTER = "current_filter"
        const val DATE_PICKER_DIALOG_TAG = "datePickerDialog"
    }

    private val isPaymentFilter: Boolean by extras(defaultValue = true)

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerHistoryFilterComponent.builder()
                .appComponent(appComponent)
                .historyFilterModule(HistoryFilterModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_filter)

        initViews()

        presenter.currentFilter = intent.getParcelableExtra<HistoryFilterModel>(KEY_CURRENT_FILTER) ?: HistoryFilterModel()

        presenter.onViewAttached()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            BarcodeScannerActivity.BARCODE_SCANNER_REQUEST_CODE -> {
                barcodeEditText.setText(data?.getStringExtra(BarcodeScannerActivity.BARCODE_SCANNED_RESULT_KEY))
            }
            SearchAddressActivity.SEARCH_ADDRESS_REQUEST_CODE -> {
                streetEditText.setText(data?.getStringExtra(SearchAddressActivity.SEARCH_ADDRESS_RESULT_KEY))
            }
        }
    }

    override fun showCurrentFilter(currentFilter: HistoryFilterModel) {
        with(currentFilter) {
            barcodeEditText.setText(barcode)
            streetEditText.setText(street)
            houseEditText.setText(house)
            apartmentEditText.setText(apartment)

            paymentSum?.let {
                paymentSumEditText.setText(String.format("%.2f", it))
            }

            periodFrom?.let {
                showSelectedPeriod(it, periodTo!!)
            }

            paymentMethod?.let {
                showSelectedPaymentMethod(it)
            }

            if (paymentType.isNotBlank()) {
                showSelectedPaymentType(paymentType)
            }

            if (deviceNumber.isNotBlank()) {
                showSelectedDeviceNumber(deviceNumber)
            }

            if (deviceInstallPlace.isNotBlank()) {
                showSelectedDevicePlace(deviceInstallPlace)
            }
        }
    }

    override fun showListSelectDialog(titleRes: Int, itemsRes: Int, selectedIndex: Int, onConfirm: (Int) -> Unit) {
        materialDialog {
            title(titleRes)
            items(itemsRes)
            itemsCallbackSingleChoice(
                    selectedIndex,
                    { materialDialog, view, index, charSequence -> true }
            )
            positiveText(R.string.history_filter_dialog_ok)
            negativeText(R.string.history_filter_dialog_cancel)
            onPositive {
                dialog, action ->
                onConfirm(dialog.selectedIndex)
            }
        }.show()
    }

    override fun showListSelectDialog(titleRes: Int, items: List<String>, selectedItem: String, onConfirm: (String) -> Unit) {
        materialDialog {
            title(titleRes)
            items(items)
            itemsCallbackSingleChoice(
                    items.indexOfFirst { it == selectedItem },
                    { materialDialog, view, index, charSequence -> true }
            )
            positiveText(R.string.history_filter_dialog_ok)
            negativeText(R.string.history_filter_dialog_cancel)
            onPositive {
                dialog, action ->
                onConfirm(items[dialog.selectedIndex])
            }
        }.show()
    }

    override fun showSelectPeriodFromDialog(date: Date) {
        val curDate = Calendar.getInstance()
        curDate.time = date
        val dialog = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                    presenter.onPeriodDateFromSelected(selectedDate.time)
                },
                curDate[Calendar.YEAR],
                curDate[Calendar.MONTH],
                curDate[Calendar.DAY_OF_MONTH]
        )

        dialog.setTitle(getString(R.string.history_filter_date_from_dialog_title))
        dialog.show(fragmentManager, DATE_PICKER_DIALOG_TAG)
    }

    override fun showSelectPeriodToDialog(date: Date) {
        val fromDate = Calendar.getInstance()
        fromDate.time = date
        val dialog = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth, 23, 59, 59)
                    presenter.onPeriodDateToSelected(selectedDate.time)
                },
                fromDate[Calendar.YEAR],
                fromDate[Calendar.MONTH],
                fromDate[Calendar.DAY_OF_MONTH]
        )

        dialog.minDate = fromDate
        dialog.setTitle(getString(R.string.history_filter_date_to_dialog_title).format(date.toString(Constants.RECEIPT_DATE_FORMAT)))
        dialog.show(fragmentManager, DATE_PICKER_DIALOG_TAG)
    }

    override fun showSelectedPeriod(dateFrom: Date, dateTo: Date) {
        periodText.text = "%s - %s".format(dateFrom.toString(Constants.RECEIPT_DATE_FORMAT), dateTo.toString(Constants.RECEIPT_DATE_FORMAT))
    }

    override fun showSelectedPaymentMethod(paymentMethod: PaymentMethod) {
        paymentProcessText.setText(paymentMethod.getStringResId())
    }

    override fun showSelectedPaymentType(paymentType: String) {
        paymentTypeText.text = paymentType
    }

    override fun showSelectedDeviceNumber(deviceNumber: String) {
        deviceIdText.text = deviceNumber
    }

    override fun showSelectedDevicePlace(devicePlace: String) {
        installPlaceText.text = devicePlace
    }

    override fun navigateToBarcodeScanner() {
        startActivityForResult<BarcodeScannerActivity>(BarcodeScannerActivity.BARCODE_SCANNER_REQUEST_CODE)
    }

    override fun navigateToSearchAddress() {
        startActivityForResult<SearchAddressActivity>(SearchAddressActivity.SEARCH_ADDRESS_REQUEST_CODE)
    }

    override fun applyFilter(currentFilter: HistoryFilterModel) {
        val intent = Intent()
        intent.putExtra(RESULT_KEY, currentFilter)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)

        barcodeEditText.textChangedListener {
            afterTextChanged { code ->
                presenter.onBarCodeTextChanged(code.toString())
            }
        }

        streetEditText.textChangedListener {
            afterTextChanged { street ->
                presenter.onStreetTextChanged(street.toString())
            }
        }

        streetInputLayout.isHintAnimationEnabled = false
        streetEditText.onTouch { view, motionEvent ->
            when {
                motionEvent.actionMasked == MotionEvent.ACTION_UP -> {
                    rootLayout.requestFocus()
                    presenter.onAddressClick()
                    false
                }
                else -> true
            }
        }

        houseEditText.textChangedListener {
            afterTextChanged { house ->
                presenter.onHouseTextChanged(house.toString())
            }
        }

        apartmentEditText.textChangedListener {
            afterTextChanged { apartment ->
                presenter.onApartmentTextChanged(apartment.toString())
            }
        }

        apartmentEditText.onEditorAction { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                rootLayout.requestFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        val listener = MaskedTextChangedListener(
                "[099999999]{.}[99]",
                false,
                paymentSumEditText,
                null,
                object : MaskedTextChangedListener.ValueListener {
                    override fun onExtracted(value: String) {
                        presenter.onPaymentSumTextChanged(value)
                    }

                    override fun onMandatoryCharactersFilled(complete: Boolean) {
                    }
                }
        )

        paymentSumEditText.addTextChangedListener(listener)
        paymentSumEditText.onFocusChangeListener = listener

        paymentSumEditText.onEditorAction { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                rootLayout.requestFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        barCodeScanButton.onClick { presenter.onBarCodeScanButtonClick() }
        periodText.onClick { presenter.onSelectPeriodClick() }
        applyFilterButton.onClick { presenter.onApplyFilterClick() }
        paymentTypeText.onClick { presenter.onSelectPaymentTypeClick() }
        paymentProcessText.onClick { presenter.onSelectPaymentProcessClick() }

        if (!isPaymentFilter) {
            paymentSumInputLayout.visibility = View.GONE
            paymentTypeText.visibility = View.GONE
            paymentTypeCaption.visibility = View.GONE
            paymentProcessText.visibility = View.GONE
            paymentProcessCaption.visibility = View.GONE
            periodCaption.setText(R.string.history_filter_transfer_value_period_caption)

            deviceIdText.visibility = View.VISIBLE
            deviceIdCaption.visibility = View.VISIBLE
            installPlaceText.visibility = View.VISIBLE
            installPlaceCaption.visibility = View.VISIBLE

            deviceIdText.onClick { presenter.onSelectDeviceNumberClick() }
            installPlaceText.onClick { presenter.onSelectInstallPlaceClick() }
        }
    }
}
