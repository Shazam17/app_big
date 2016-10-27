package com.software.ssp.erkc.modules.signup

import android.app.Activity
import android.content.Intent
import android.lib.recaptcha.ReCaptcha
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.address.SearchAddressActivity
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SignUpActivity : MvpActivity(), ISignUpView, ReCaptcha.OnShowChallengeListener, ReCaptcha.OnVerifyAnswerListener {
    override fun onAnswerVerified(success: Boolean) {
        recaptcha.showChallengeAsync(Constants.RECAPTCHA_PUBLIC_KEY, this)
        if (success) {
            if (validateFields()) {
                presenter.onRegistrationButtonClick(
                        loginEditText.text.toString(),
                        passwordEditText.text.toString(),
                        password2EditText.text.toString(),
                        firstNameEditText.text.toString(),
                        emailEditText.text.toString()
                )
            }
        } else {
            captchaEditText.error = "Наверно введены символы с картинки"
            captchaEditText.requestFocus()
        }
    }

    override fun onChallengeShown(shown: Boolean) {

    }

    @Inject lateinit var presenter: ISignUpPresenter

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

    override fun navigateToDrawerScreen() {
        finish()
        startActivity<DrawerActivity>()
    }

    private fun initViews() {
        recaptcha.showChallengeAsync(Constants.RECAPTCHA_PUBLIC_KEY, this)
        recaptcha.onClick {
            recaptcha.showChallengeAsync(Constants.RECAPTCHA_PUBLIC_KEY, this)
        }
        signUpButton.onClick {
            if (!captchaEditText.text.toString().isEmpty()) {
                recaptcha.verifyAnswerAsync(Constants.RECAPTCHA_PRIVATE_KEY, captchaEditText.text.toString(), this)
            } else {
                captchaEditText.error ="Поле обязательно для ввода"
                captchaEditText.requestFocus()
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
    }

    private fun validateFields(): Boolean {
        if (passwordEditText.text.toString() != passwordEditText.text.toString()) {
            password2EditText.error = "Пароли не совпадают"
            password2EditText.requestFocus()
            return false
        }
        if (loginEditText.text.toString().isEmpty() ||
                passwordEditText.text.toString().isEmpty() ||
                firstNameEditText.text.toString().isEmpty() ||
                emailEditText.text.toString().isEmpty()) {
            showMessage("Все поля обязательны для заполнения")
            return false
        }
        return true
    }


}