package com.software.ssp.erkc.modules.sendvalues

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_send_values.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 26/10/2016.
 */
class SendValuesActivity : MvpActivity(), ISendValuesView {

    @Inject lateinit var presenter: ISendValuesPresenter
    private var receipt: Receipt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_values)
        receipt = intent.getParcelableExtra<Receipt>(Constants.KEY_RECEIPT)
        initViews()
        presenter.onViewAttached()
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
    }

    private fun fillViews() {
        barcode.text = receipt?.barcode
        address.text = receipt?.address
        debts.text = receipt?.amount
    }
}