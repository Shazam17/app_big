package com.software.ssp.erkc.modules.splash

import android.os.Bundle
import android.support.design.widget.Snackbar
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SplashActivity : MvpActivity(), ISplashView {

    @Inject lateinit var presenter: ISplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // для использования 2-й версии splash скрина раскоменатрить тут и убрать из стиля windowBackground
        //setContentView(R.layout.activity_splash)
        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerSplashComponent.builder()
                .appComponent(appComponent)
                .splashModule(SplashModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun navigateToSignIn() {
        finish()
        startActivity<DrawerActivity>()
    }

    override fun showTryAgainSnack(message: Int) {
        showTryAgainSnack(getString(message))
    }

    override fun showTryAgainSnack(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction(R.string.splash_try_again_text) {presenter.onTryAgainClicked()}
                .show()
    }

}