package com.software.ssp.erkc.modules.notifications.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.extensions.toString
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_notifications_filter.*
import org.jetbrains.anko.onCheckedChange
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onEditorAction
import org.jetbrains.anko.textChangedListener
import java.util.*
import javax.inject.Inject


class NotificationsFilterActivity : MvpActivity(), INotificationsFilterView {

    @Inject lateinit var presenter: INotificationsFilterPresenter

    companion object {
        const val REQUEST_CODE = 24223
        const val RESULT_KEY = "notifications_filter_result_key"
        const val KEY_CURRENT_FILTER = "current_filter"
        const val DATE_PICKER_DIALOG_TAG = "datePickerDialog"
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerNotificationsFilterComponent.builder()
                .appComponent(appComponent)
                .notificationsFilterModule(NotificationsFilterModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_filter)

        initViews()

        presenter.currentFilter = intent.getParcelableExtra<NotificationsFilterModel>(KEY_CURRENT_FILTER) ?: NotificationsFilterModel()

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

    override fun showCurrentFilter(currentFilter: NotificationsFilterModel) {
        with(currentFilter) {
            titleEditText.setText(title)
            messageEditText.setText(message)

            periodFrom?.let {
                showSelectedPeriod(it, periodTo!!)
            }

            readSwitch.isChecked = isRead
        }
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

        dialog.setTitle(getString(R.string.notifications_filter_date_from_dialog_title))
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
        dialog.setTitle(getString(R.string.notifications_filter_date_to_dialog_title).format(date.toString(Constants.RECEIPT_DATE_FORMAT)))
        dialog.show(fragmentManager, DATE_PICKER_DIALOG_TAG)
    }

    override fun showSelectedPeriod(dateFrom: Date, dateTo: Date) {
        periodText.text = "%s - %s".format(dateFrom.toString(Constants.RECEIPT_DATE_FORMAT), dateTo.toString(Constants.RECEIPT_DATE_FORMAT))
    }

    override fun applyFilter(currentFilter: NotificationsFilterModel) {
        val intent = Intent()
        intent.putExtra(RESULT_KEY, currentFilter)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)

        titleEditText.textChangedListener {
            afterTextChanged { title ->
                presenter.onTitleTextChanged(title.toString())
            }
        }

        messageEditText.textChangedListener {
            afterTextChanged { message ->
                presenter.onMessageTextChanged(message.toString())
            }
        }

        messageEditText.onEditorAction { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                rootLayout.requestFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        readSwitch.onCheckedChange { compoundButton, isChecked ->
            presenter.onReadSwitchChanged(isChecked)
        }

        periodText.onClick { presenter.onSelectPeriodClick() }
        applyFilterButton.onClick { presenter.onApplyFilterClick() }
    }
}
