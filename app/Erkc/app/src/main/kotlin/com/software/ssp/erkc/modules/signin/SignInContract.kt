package com.software.ssp.erkc.modules.signin

import android.support.annotation.StringRes
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface ISignInView : IView {
    fun navigateToForgotPasswordScreen()
    fun close()
    fun navigateBack()
    fun showLoginFieldError(@StringRes errorResId: Int)
    fun showPasswordFieldError(@StringRes errorResId: Int)
    fun setProgressVisibility(isVisible: Boolean)
    fun showInfoDialog(stringResId: Int)
    fun setResultOk()
}

interface ISignInPresenter : IPresenter<ISignInView> {
    fun onLoginButtonClick(login: String, password: String)
    fun onForgotPasswordButtonClick()
    fun onBackPressed(shouldCloseAppOnBackPressed: Boolean)
}