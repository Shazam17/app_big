package com.software.ssp.erkc.modules.setupfastauth

import android.os.Bundle
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_setup_fast_auth.*
import javax.inject.Inject

class SetupFastAuthActivity : MvpActivity(), ISetupFastAuthView {

    @Inject lateinit var presenter: ISetupFastAuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_fast_auth)

        initViews()

        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerSetupFastAuthComponent.builder()
                .appComponent(appComponent)
                .setupFastAuthModule(SetupFastAuthModule(this))
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
