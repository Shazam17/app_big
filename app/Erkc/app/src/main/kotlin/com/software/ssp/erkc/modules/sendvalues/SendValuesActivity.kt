package com.software.ssp.erkc.modules.sendvalues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Ipu
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_send_values.*
import kotlinx.android.synthetic.main.sendparameters_ipu_layout.*
import kotlinx.android.synthetic.main.sendparameters_ipu_layout.view.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 26/10/2016.
 */
class SendValuesActivity : MvpActivity(), ISendValuesView {

    @Inject lateinit var presenter: ISendValuesPresenter
    private var receipt: Receipt? = null
    private var ipus: List<Ipu>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_values)
        receipt = intent.getParcelableExtra<Receipt>(Constants.KEY_RECEIPT)
        initViews()
        presenter.onViewAttached(receipt?.barcode!!)
    }

    override fun fillData(data: List<Ipu>) {
        ipus = data
        sendValuesButton.enabled = true
        sendValuesDebts.text = "${receipt?.amount} р.(${SimpleDateFormat(Constants.PERIOD_DATE_FORMAT_UI, Locale("ru")).format(data.first().period)})"
        for ((uslugaName, mestoUstan, period, id, nomer) in data) {
            val layoutInflater = LayoutInflater.from(this)
            val ipuLayout = layoutInflater.inflate(R.layout.sendparameters_ipu_layout, parametersContainer, false)
            ipuLayout.ipuLocation.text = mestoUstan
            ipuLayout.ipuValue.tag = id
            ipuLayout.ipuValueWrapper.hint = "$uslugaName ($nomer)"
            parametersContainer.addView(ipuLayout)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerSendValuesComponent.builder()
                .appComponent(appComponent)
                .sendValuesModule(SendValuesModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun navigateToDrawer() {
        finish()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        sendValuesButton.onClick {
            val params = HashMap<String, String>()
            for ((uslugaName, mestoUstan, period, id) in ipus!!) {
                val value = (parametersContainer.findViewWithTag(id) as EditText).text.toString()
                if (value.isBlank()) {
                    showMessage(R.string.error_all_fields_required)
                    return@onClick
                }
                params.put("ipu_$id", value)
            }
            presenter.onSendValuesClick(receipt!!.barcode, params)
        }
        sendValuesBarcode.text = receipt?.barcode
        sendValuesAddress.text = receipt?.address
        sendValuesButton.enabled = false
    }
}