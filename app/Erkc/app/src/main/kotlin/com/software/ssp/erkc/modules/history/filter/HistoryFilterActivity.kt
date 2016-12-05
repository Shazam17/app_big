package com.software.ssp.erkc.modules.history.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.ReceiptType
import com.software.ssp.erkc.data.rest.models.PaymentMethod
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.getStringResId
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.extensions.receiptFormat
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
    }

    val DATE_PICKER_DIALOG_TAG: String = "datePickerDialog"

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
        with(currentFilter){
            barcodeEditText.setText(barcode)
            streetEditText.setText(street)
            houseEditText.setText(house)
            apartmentEditText.setText(apartment)
            paymentSumEditText.setText(paymentSum)

            periodFrom?.let {
                showSelectedPeriod(it, periodTo!!)
            }

            paymentType?.let {
                showSelectedPaymentType(it)
            }

            paymentMethod?.let {
                showSelectedPaymentMethod(it)
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

    override fun showSelectPeriodFromDialog(date: Date) {
        val curDate = Calendar.getInstance()
        curDate.time = date
        val dialog = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
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
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    presenter.onPeriodDateToSelected(selectedDate.time)
                },
                fromDate[Calendar.YEAR],
                fromDate[Calendar.MONTH],
                fromDate[Calendar.DAY_OF_MONTH]
        )

        dialog.minDate = fromDate
        dialog.setTitle(getString(R.string.history_filter_date_to_dialog_title).format(date.receiptFormat))
        dialog.show(fragmentManager, DATE_PICKER_DIALOG_TAG)
    }

    override fun showSelectedPeriod(dateFrom: Date, dateTo: Date) {
        periodText.text = "%s - %s".format(dateFrom.receiptFormat, dateTo.receiptFormat)
    }

    override fun showSelectedPaymentMethod(paymentMethod: PaymentMethod) {
        paymentProcessText.setText(paymentMethod.getStringResId())
    }

    override fun showSelectedPaymentType(paymentType: ReceiptType) {
        paymentTypeText.setText(paymentType.getStringResId())
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

        streetEditText.onFocusChange { view, isFocus ->
            if(isFocus) {
                presenter.onAddressClick()
                rootLayout.requestFocus()
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

        paymentSumEditText.textChangedListener {
            afterTextChanged { sum ->
                presenter.onPaymentSumTextChanged(sum.toString())
            }
        }

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
        //streetInputLayout.onClick { presenter.onAddressClick() }
        periodText.onClick { presenter.onSelectPeriodClick() }
        applyFilterButton.onClick { presenter.onApplyFilterClick() }
        paymentTypeText.onClick { presenter.onSelectPaymentTypeClick() }
        paymentProcessText.onClick { presenter.onSelectPaymentProcessClick() }
    }
}
