package com.software.ssp.erkc.modules.adduseripu

import android.os.Bundle
import android.view.MenuItem
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.toString
import kotlinx.android.synthetic.main.activity_add_user_ipu.*
import kotlinx.android.synthetic.main.activity_send_values.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textChangedListener
import javax.inject.Inject

class Activity : MvpActivity(), IModuleView {

    @Inject lateinit var presenter: IModulePresenter

    private val receipt: Receipt? by extras()
    private val receiptId: String? by extras(Constants.KEY_RECEIPT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_ipu)
        initViews()

        presenter.receipt = receipt
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        commitButton.onClick { presenter.commitClicked() }


    }

    override fun bindData(ipu_data: Presenter.UserIPUData) {
        number.textChangedListener { afterTextChanged { e->ipu_data.number = e.toString() } }
        location.textChangedListener { afterTextChanged { e->ipu_data.location = e.toString() } }
    }
}