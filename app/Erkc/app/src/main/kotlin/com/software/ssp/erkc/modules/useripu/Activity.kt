package com.software.ssp.erkc.modules.useripu

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.DatePicker
import android.widget.EditText
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.*
import kotlinx.android.synthetic.main.activity_add_user_ipu.*
import kotlinx.android.synthetic.main.activity_send_values.*
import org.jetbrains.anko.*
import rx.Observable
import java.util.*
import javax.inject.Inject
import com.software.ssp.erkc.modules.useripu.IModulePresenter.FilterType.*


class Activity : MvpActivity(), IModuleView {

    @Inject lateinit var presenter: IModulePresenter

    private val receiptId: String? by extras(Constants.KEY_RECEIPT)
    private val ipu_number: String? by extras(Constants.KEY_IPU_NUMBER)

    private var delete_menu_item: MenuItem? = null
    private var edit_mode = false
//    private val date_watcher = DateWatcher()
//
//    class DateWatcher : TextWatcher {
//        val digits = "0123456789"
//        val dot = "."
//        val allowed_chars = arrayOf(digits, digits, dot, digits, digits, dot, digits, digits, digits, digits)
//        var prev = ""
//
//        override fun afterTextChanged(e: Editable?) {
//            val s = e.toString()
//            if (!allowed(s)) {
//                e?.
//            }
//        }
//        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_ipu)
        initViews()

        presenter.ipu_number = ipu_number
        presenter.receiptId = receiptId

        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerAComponent.builder()
                .appComponent(appComponent)
                .aModule(AModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_ipu_menu, menu)
        delete_menu_item = menu?.findItem(R.id.menu_delete)
        if (edit_mode) setModeEdit() else setModeAdd() //because of late init for menu item
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.menu_delete -> {
                presenter.delete()
            }
        }
        return true
    }

    private fun initViews() {
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        commitButton.onClick { presenter.commitClicked() }

        status_til.visibility = GONE
    }

    override fun setupFilters() {
        val null_focused = commitButton

        begin_date.beDatePicker(this, null_focused = null_focused)
        install_date.beDatePicker(this, null_focused = null_focused)
        next_check_date.beDatePicker(this, null_focused = null_focused)

        location.beFiltered(this, presenter.filter(FILTER_LOCATION), null_focused, other_allowed = false)
        service_name.beFiltered(this, presenter.filter(FILTER_SERVICE_NAME), null_focused)
        check_interval.beFiltered(this, presenter.filter(FILTER_CHECK_INTERVAL), null_focused)
        type.beFiltered(this, presenter.filter(FILTER_TYPE), null_focused)
        type_tariff.beFiltered(this, presenter.filter(FILTER_TYPE_TARIFF), null_focused)
        status.beFiltered(this, presenter.filter(FILTER_STATUS), null_focused)
    }


    override fun bindData(ipu_data: Presenter.UserIPUData) {
        number.bind { ipu_data.number = it }
        location.bind { ipu_data.location = it }
        service_name.bind { ipu_data.service_name = it }
        brand.bind { ipu_data.brand = it }
        model.bind { ipu_data.model = it }
        check_interval.bind { ipu_data.check_interval = it }
        type.bind { ipu_data.type = it }
        type_tariff.bind { ipu_data.type_tariff = it }
        begin_date.bind { ipu_data.begin_date = it }
        install_date.bind { ipu_data.install_date = it }
        next_check_date.bind { ipu_data.next_check_date = it }
        status.bind { ipu_data.status = it }
    }

    override fun close() {
        finish()
    }

    override fun setModeAdd() {
        edit_mode = false
        delete_menu_item?.setVisible(false)
        commitButton.setText(R.string.user_ipu_commit)
        setTitle(R.string.user_ipu_add_mode)
    }

    override fun setModeEdit() {
        edit_mode = true
        delete_menu_item?.setVisible(true)
        commitButton.setText(R.string.user_ipu_commit_save)
        setTitle(R.string.user_ipu_edit_mode)
    }

    override fun setData(data: Presenter.UserIPUData) {
        number.setText(data.number)
        location.setText(data.location)
        service_name.setText(data.service_name)
        brand.setText(data.brand)
        model.setText(data.model)
        check_interval.setText(data.check_interval)
        type.setText(data.type)
        type_tariff.setText(data.type_tariff)
        begin_date.setText(data.begin_date)
        install_date.setText(data.install_date)
        next_check_date.setText(data.next_check_date)
        status.setText(data.status)
    }

    override fun askDeleteIPUAndData(): Observable<Boolean> {
        return Observable.create({
            e->
                alert(R.string.user_ipu_ask_delete) {
                    positiveButton(R.string.answer_positive, {e.onNext(true)})
                    negativeButton(R.string.answer_negative, {e.onNext(false)})
                    onCancel { e.onNext(false) }
                }.show()
        })
    }

    override fun validateDataBeforeCommit(): Boolean {
        var has_errors = false

        if (emptyCheck(number)) has_errors = true
        if (emptyCheck(location)) has_errors = true
        if (emptyCheck(service_name)) has_errors = true

        return !has_errors
    }

    private fun emptyCheck(view: TextInputEditText): Boolean {
        if (view.length() == 0) {
            view.setTilError(getString(R.string.error_empty_field))
            return true
        } else {
            view.setTilError(null)
            return false
        }
    }

    override fun showProgress(show: Boolean) {
        commitButton.isEnabled = !show
        progress.visibility = if (show) VISIBLE else GONE
    }
}