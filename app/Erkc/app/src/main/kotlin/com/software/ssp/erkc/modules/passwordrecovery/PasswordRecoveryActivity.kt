package com.software.ssp.erkc.modules.passwordrecovery

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.extensions.onTextChange
import com.software.ssp.erkc.extensions.setBase64Bitmap
import kotlinx.android.synthetic.main.activity_password_recovery.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onEditorAction
import javax.inject.Inject


class PasswordRecoveryActivity : MvpActivity(), IPasswordRecoveryView {

    @Inject lateinit var presenter: IPasswordRecoveryPresenter

    private var passwordRecoveryEmail: String? by extras()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)

        initViews()
        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerPasswordRecoveryComponent.builder()
                .appComponent(appComponent)
                .passwordRecoveryModule(PasswordRecoveryModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun setSendButtonEnabled(enabled: Boolean) {
        passwordRecoverySendButton.enabled = enabled
    }

    override fun navigateToSignInScreen() {
        onBackPressed()
    }

    override fun setCaptchaImage(imageBase64: String) {
        passwordRecoveryRecaptchaImageView.setBase64Bitmap(imageBase64)
    }


    override fun showCaptchaError(errorStringResId: Int) {
        passwordRecoveryCaptchaEditText.requestFocus()
        passwordRecoveryCaptchaEditText.error = getString(errorStringResId)
    }

    private fun initViews() {
        passwordRecoveryEmail?.let {
            passwordRecoveryEmailEditText.setText(it)
        }

        //  todo delete when API will be ready OR captcha will be removed from project
        passwordRecoveryRecaptchaImageView.visibility = View.GONE
        passwordRecoveryCaptchaEditText.visibility = View.GONE

        passwordRecoveryLoginEditText.onTextChange { charSequence -> presenter.onLoginChanged(charSequence.toString()) }
        passwordRecoveryEmailEditText.onTextChange { charSequence -> presenter.onEmailChanged(charSequence.toString()) }
        passwordRecoveryCaptchaEditText.onTextChange { charSequence -> presenter.onCaptchaChanged(charSequence.toString()) }

        passwordRecoveryCaptchaEditText.onEditorAction { editText, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onSendButtonClick()
                true
            } else {
                false
            }
        }

        passwordRecoverySendButton.onClick { onSendButtonClick() }
    }

    private fun onSendButtonClick() {
        hideKeyboard()
        presenter.onSendButtonClick()
    }

}