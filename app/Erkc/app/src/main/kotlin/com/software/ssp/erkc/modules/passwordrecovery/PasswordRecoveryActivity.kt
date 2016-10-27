package com.software.ssp.erkc.modules.passwordrecovery

import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.extensions.onTextChange
import kotlinx.android.synthetic.main.activity_password_recovery.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onEditorAction
import javax.inject.Inject


class PasswordRecoveryActivity : MvpActivity(), IPasswordRecoveryView {

    @Inject lateinit var presenter: IPasswordRecoveryPresenter

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun setSendButtonEnabled(enabled: Boolean) {
        passwordRecoverySendButton.enabled = enabled
    }

    override fun navigateToSignInScreen() {
        onBackPressed()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        passwordRecoveryLoginEditText.onTextChange { charSequence -> presenter.onLoginChanged(charSequence.toString()) }
        passwordRecoveryEmailEditText.onTextChange { charSequence -> presenter.onEmailChanged(charSequence.toString()) }
        passwordRecoveryEmailEditText.onEditorAction { editText, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                passwordRecoveryLayout.requestFocus()
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