package com.software.ssp.erkc.modules.createrequest

import android.os.Bundle
import android.view.MenuItem
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import javax.inject.Inject

class CreateRequestActivity: MvpActivity(), ICreateRequestView {

    @Inject lateinit var presenter: ICreateRequestPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request)
        initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }


    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerCreateRequestComponent.builder()
                .appComponent(appComponent)
                .createRequestModule(CreateRequestModule(this))
                .build()
                .inject(this)
    }

    private fun initViews() {
        // TODO init views in activity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun beforeDestroy() {
        presenter.onViewDetached()
    }
}