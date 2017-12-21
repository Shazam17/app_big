package com.software.ssp.erkc.modules.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.securepreferences.SecurePreferences
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import com.software.ssp.erkc.modules.processfastauth.ProcessFastAuthActivity
import com.software.ssp.erkc.modules.signin.SignInActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SplashActivity : MvpActivity(), ISplashView {

    @Inject lateinit var presenter: ISplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    fun getPin(): String {
        val securePrefs = SecurePreferences(this, "", getString(R.string.secure_prefs_filename))
        return securePrefs.getString(getString(R.string.user_pin_key), "")
    }

    override fun navigateToDrawer() {
        finish()
        val pin = getPin()
        if (!pin.isNullOrEmpty()) {
            startActivity<ProcessFastAuthActivity>()
            return
        }
        startActivity<DrawerActivity>()
    }

    override fun navigateToOfflineSignIn() {
        startActivityForResult<SignInActivity>(SignInActivity.SIGN_IN_REQUEST_CODE, "isOfflineSignIn" to true)
    }

    override fun showTryAgainSnack(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.splash_try_again_text) { presenter.onTryAgainClicked() }
                .show()
    }
}
