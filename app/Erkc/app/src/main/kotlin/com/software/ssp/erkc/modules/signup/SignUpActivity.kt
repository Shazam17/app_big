package com.software.ssp.erkc.modules.signup

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.load
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity
import com.software.ssp.erkc.modules.fastauth.createpin.CreatePinActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.onClick
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SignUpActivity : MvpActivity(), ISignUpView {

    @Inject lateinit var presenter: ISignUpPresenter

    companion object {
        val SIGN_UP_REQUEST_CODE = 25512
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
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
        DaggerSignUpComponent.builder()
                .appComponent(appComponent)
                .signUpModule(SignUpModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun setProgressVisibility(isVisible: Boolean) {
        signUpButton.isEnabled = !isVisible
        signUpProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun navigateToMainScreen() {
        setResult(Activity.RESULT_OK)
        startActivity(intentFor<DrawerActivity>()
            .newTask()
            .clearTop())
        finish()
    }

    override fun showCaptcha(image: ByteArray) {
        signUpCaptchaImageView.load(image)
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
        } else {
            presenter.onPinReject()
        }
    }

    private fun initViews() {
        signUpButton.onClick {
            val prefs = this.getSharedPreferences(EnterPinActivity.PREFERENCES, Context.MODE_PRIVATE)
            prefs.edit().remove(EnterPinActivity.KEY_PIN + signUpLoginEditText.text.toString()).apply()
            prefs.edit().putBoolean(EnterPinActivity.SHOULD_SUGGEST_SET_PIN + signUpLoginEditText.text.toString(), true).apply()
            presenter.onSignUpButtonClick(
                    signUpLoginEditText.text.toString(),
                    signUpPasswordEditText.text.toString(),
                    signUpPassword2EditText.text.toString(),
                    signUpFirstNameEditText.text.toString(),
                    signUpEmailEditText.text.toString(),
                    signUpCaptchaEditText.text.toString()
            )
        }

        signUpCaptchaImageView.onClick {
            signUpCaptchaImageView.setImageResource(android.R.color.transparent)
            presenter.onCaptchaClick()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
    }
}
