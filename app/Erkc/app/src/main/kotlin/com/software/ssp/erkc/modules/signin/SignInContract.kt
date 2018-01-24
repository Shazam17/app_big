package com.software.ssp.erkc.modules.signin

import android.support.annotation.StringRes
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface ISignInView : IView {
    fun navigateToForgotPasswordScreen()
    fun navigateToMainScreen()
    fun showLoginFieldError(@StringRes errorResId: Int)
    fun showPasswordFieldError(@StringRes errorResId: Int)
    fun setProgressVisibility(isVisible: Boolean)
    fun showInfoDialog(stringResId: Int)
    fun setResultOk()
    fun showPinSuggestDialog(login: String)
    fun navigateToFastAuthScreen()
    fun setUserLogin(login: String)
}

interface ISignInPresenter : IPresenter<ISignInView> {
    fun onLoginButtonClick(login: String, password: String)
    fun onForgotPasswordButtonClick()
    fun onPinReject()
    fun onFastAuthClick()
    fun onPinExist()
    fun onMenuCreated()
}