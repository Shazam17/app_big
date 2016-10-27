package com.software.ssp.erkc.modules.signin

import android.support.annotation.StringRes
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface ISignInView : IView {
    fun setProgressVisibility(isVisible: Boolean)
    fun navigateToForgotPasswordScreen(email: String)
    fun navigateToDrawerScreen()
    fun showLoginFieldError(@StringRes errorResId: Int)
    fun showPasswordFieldError(@StringRes errorResId: Int)
}

interface ISignInPresenter : IPresenter<ISignInView> {
    fun onLoginButtonClick(email: String, password: String)
    fun onForgotPasswordButtonClick(email: String)
}