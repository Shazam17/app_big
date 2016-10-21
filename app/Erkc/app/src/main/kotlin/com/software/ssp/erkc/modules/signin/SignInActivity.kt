package com.software.ssp.erkc.modules.signin

import android.os.Bundle
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textChangedListener
import org.jetbrains.anko.toast
import javax.inject.Inject

class SignInActivity : MvpActivity(), ISignInView {

    @Inject lateinit var presenter: ISignInPresenter

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

    override fun setProgressVisibility(isVisible: Boolean) {
        signInLoginEditText.isEnabled = !isVisible
        signInPasswordEditText.isEnabled = !isVisible
        signInLoginButton.isEnabled = !isVisible
        signInForgotPasswordView.isEnabled = !isVisible
        signInProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun navigateToForgotPasswordScreen(email: String) {
        toast("TODO: navigate to Forgot Password screen")
    }

    override fun navigateToDrawerScreen() {
        toast("TODO: navigate to Drawer screen")
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

    private fun initViews() {
        signInLoginEditText.textChangedListener {
            onTextChanged { charSequence, i, j, k ->  signInLoginTextInputLayout.error = null}
        }

        signInPasswordEditText.textChangedListener {
            onTextChanged { charSequence, i, j, k ->  signInPasswordTextInputLayout.error = null}
        }

        signInLoginButton.onClick { presenter.onLoginButtonClick(signInLoginEditText.text.toString(), signInPasswordEditText.text.toString())}
        signInForgotPasswordView.onClick { presenter.onForgotPasswordButtonClick(signInLoginEditText.text.toString())}
    }
}

