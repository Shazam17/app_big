package com.software.ssp.erkc.modules.splash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity.KEY_PIN
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity.PREFERENCES
import com.software.ssp.erkc.modules.longrunningupdate.LongRunningUpdateService
import com.software.ssp.erkc.modules.signin.SignInActivity
import org.jetbrains.anko.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SplashActivity : MvpActivity(), ISplashView {

    @Inject lateinit var presenter: ISplashPresenter

    var login = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ErkcApplication.wasSplash = false
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            SignInActivity.SIGN_IN_REQUEST_CODE -> navigateToDrawer()
        }
    }

    override fun showOfflineLoginDialog() {
        materialDialog {
            positiveText(R.string.splash_offline_dialog_positive)
            negativeText(R.string.splash_offline_dialog_negative)
            content(R.string.splash_offline_dialog_content)
            onPositive { materialDialog, dialogAction ->
                presenter.onConfirmOfflineLogin()
            }
        }.show()
    }

    override fun setUserLogin(login: String) {
        this.login = login
        ErkcApplication.login = login
    }

    fun getPin(): String {
        if(!login.isEmpty()) {
            val prefs = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            return prefs.getString(KEY_PIN + login, "")
        } else {
            return ""
        }
    }

    override fun navigateToDrawer() {
        val pin = getPin()
        ErkcApplication.wasSplash = true
        if (pin.isEmpty()) {
            presenter.clearToken()
            startActivity(intentFor<DrawerActivity>()
                .newTask()
                .clearTop())
        } else {
            startActivity(intentFor<EnterPinActivity>()
                .newTask()
                .singleTop()
                .putExtra("initialAuth", true))
        }
        this.finish()
    }

    override fun navigateToOfflineSignIn() {
        startActivity<SignInActivity>("isOfflineSignIn" to true)
        this.finish()
    }

    override fun showTryAgainSnack(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.splash_try_again_text) { presenter.onTryAgainClicked() }
                .show()
    }
}
