package com.software.ssp.erkc.modules.sendvalues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmIpu
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.toString
import kotlinx.android.synthetic.main.activity_send_values.*
import kotlinx.android.synthetic.main.sendparameters_ipu_layout.view.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textChangedListener
import javax.inject.Inject

/**
 * @author Alexander Popov on 26/10/2016.
 */
class SendValuesActivity : MvpActivity(), ISendValuesView {

    @Inject lateinit var presenter: ISendValuesPresenter

    private val receipt: Receipt? by extras()
    private val receiptId: String? by extras()

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerSendValuesComponent.builder()
                .appComponent(appComponent)
                .sendValuesModule(SendValuesModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_values)
        initViews()

        presenter.receipt = receipt
        presenter.receiptId = receiptId

        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun showIpu(ipu: RealmIpu) {
        sendValuesBarcode.text = ipu.receipt?.barcode
        sendValuesAddress.text = ipu.receipt?.address
        sendValuesDebts.text = "${ipu.receipt?.amount} р. (${ipu.ipuValues.last().period?.toString(Constants.PERIOD_DATE_FORMAT_UI)})"
        val layoutInflater = LayoutInflater.from(this)
        ipu.ipuValues.filter { !it.isSent }.forEach {
            val ipuLayout = layoutInflater.inflate(R.layout.sendparameters_ipu_layout, parametersContainer, false)
            ipuLayout.ipuLocation.text = it.installPlace
            ipuLayout.ipuValueWrapper.hint = "%s (%s)".format(it.serviceName, it.number)
            ipuLayout.ipuValue.textChangedListener {
                afterTextChanged {
                    text ->
                    it.value = text.toString()
                }
            }
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

    override fun close() {
        finish()
    }

    override fun setProgressVisibility(isVisible: Boolean) {
        sendValuesButton.enabled = !isVisible
        sendValuesProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun initViews() {
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)

        sendValuesButton.onClick { presenter.onSendValuesClick() }
    }
}