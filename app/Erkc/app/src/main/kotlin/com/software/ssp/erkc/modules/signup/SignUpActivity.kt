package com.software.ssp.erkc.modules.signup

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.load
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SignUpActivity : MvpActivity(), ISignUpView {


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

    override fun showCaptcha(image: ByteArray) {
        captcha.load(image)
    }

    private fun initViews() {
        signUpButton.onClick {
            presenter.onSignUpButtonClick(
                    loginEditText.text.toString(),
                    passwordEditText.text.toString(),
                    password2EditText.text.toString(),
                    firstNameEditText.text.toString(),
                    emailEditText.text.toString(),
                    captchaEditText.text.toString()
            )
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
    }

}