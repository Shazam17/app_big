package com.software.ssp.erkc.modules.processfastauth

import android.os.Bundle
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_process_fast_auth.*
import javax.inject.Inject

class ProcessFastAuthActivity : MvpActivity(), IProcessFastAuthView {

    @Inject lateinit var presenter: IProcessFastAuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_fast_auth)

        initViews()

        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerProcessFastAuthComponent.builder()
                .appComponent(appComponent)
                .processFastAuthModule(ProcessFastAuthModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    private fun initViews() {
        //TODO init views
    }
}
