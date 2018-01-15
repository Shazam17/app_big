package com.software.ssp.erkc.modules.signin

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.BuildConfig
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity.KEY_PIN
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity.PREFERENCES
import com.software.ssp.erkc.modules.fastauth.createpin.CreatePinActivity
import com.software.ssp.erkc.modules.passwordrecovery.PasswordRecoveryActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.*
import javax.inject.Inject

class SignInActivity : MvpActivity(), ISignInView {

    @Inject lateinit var presenter: ISignInPresenter

    private val isOfflineSignIn: Boolean by extras(defaultValue = false)

    companion object {
        val SIGN_IN_REQUEST_CODE = 24512
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initViews()
        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerSignInComponent.builder()
                .appComponent(appComponent)
                .signInModule(SignInModule(this))
                .build()
                .inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showMessage(message: String) {
        signInPasswordTextInputLayout.error = message
    }

    override fun showMessage(messageResId: Int) {
        signInPasswordTextInputLayout.error = getString(messageResId)
    }

    override fun setProgressVisibility(isVisible: Boolean) {
        signInLoginEditText.isEnabled = !isVisible
        signInPasswordEditText.isEnabled = !isVisible
        signInLoginButton.isEnabled = !isVisible
        signInForgotPasswordView.isEnabled = !isVisible
        signInProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun navigateToForgotPasswordScreen() {
        startActivity<PasswordRecoveryActivity>()
    }

    override fun setResultOk() {
        setResult(Activity.RESULT_OK)
    }

    override fun navigateToMainScreen() {
        startActivity(intentFor<DrawerActivity>()
            .newTask()
            .clearTop())
        finish()
    }

    override fun close() {
        System.exit(0)
    }

    override fun showInfoDialog(stringResId: Int) {
        materialDialog {
            content(stringResId)
            positiveText(R.string.sign_in_offline_info_positive)
        }.show()
    }

    override fun showLoginFieldError(errorResId: Int) {
        signInLoginTextInputLayout.error = getString(errorResId)
    }

    override fun showPasswordFieldError(errorResId: Int) {
        signInPasswordTextInputLayout.error = getString(errorResId)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun showPinSuggestDialog(login: String) {
        ErkcApplication.login = login
        val prefs = getSharedPreferences(EnterPinActivity.PREFERENCES, Context.MODE_PRIVATE)
        val pin = prefs.getString(EnterPinActivity.KEY_PIN + login, "")
        if (pin.isNullOrEmpty() && prefs.getBoolean(EnterPinActivity.SHOULD_SUGGEST_SET_PIN + login, true)) {
            prefs.edit().putBoolean(EnterPinActivity.SHOULD_SUGGEST_SET_PIN + login, false).apply()
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.pin_suggest_dialog_message)
                .setPositiveButton(R.string.splash_offline_dialog_positive, DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(this, CreatePinActivity::class.java)
                    startActivity(intent)
                })
                .setNegativeButton(R.string.splash_offline_dialog_negative, DialogInterface.OnClickListener { dialog, id ->
                    presenter.onPinReject()
                })
                .setNeutralButton(R.string.splash_offline_dialog_neutral, DialogInterface.OnClickListener { dialog, id ->
                    prefs.edit().putBoolean(EnterPinActivity.SHOULD_SUGGEST_SET_PIN + login, true).apply()
                    presenter.onPinReject()
                })
                .setCancelable(false)
            builder.create().show()
        }
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(if (isOfflineSignIn) R.string.sing_in_offline_title else R.string.sign_in_title)
        signInPasswordTextInputLayout?.hint = getString(if (isOfflineSignIn) R.string.sign_in_password_offline_hint else R.string.sign_in_password_hint)

        signInForgotPasswordView.visibility = if (isOfflineSignIn) View.GONE else View.VISIBLE

        signInLoginEditText.textChangedListener {
            onTextChanged { charSequence, i, j, k -> signInLoginTextInputLayout.error = null }
        }

        signInPasswordEditText.textChangedListener {
            onTextChanged { charSequence, i, j, k -> signInPasswordTextInputLayout.error = null }
        }

        signInLoginButton.onClick {
            val prefs = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            prefs.edit().remove(KEY_PIN + signInLoginEditText.text.toString()).apply()
            prefs.edit().putBoolean(EnterPinActivity.SHOULD_SUGGEST_SET_PIN + signInLoginEditText.text.toString(), true).apply()
            presenter.onLoginButtonClick(signInLoginEditText.text.toString(), signInPasswordEditText.text.toString())
        }
        signInForgotPasswordView.onClick { presenter.onForgotPasswordButtonClick() }

        if (BuildConfig.DEBUG) {
//            signInLoginEditText.setText("dimas19")
//            signInPasswordEditText.setText("dimas19")
            signInLoginEditText.setText("director")
            signInPasswordEditText.setText("987456")
        }
    }
}
