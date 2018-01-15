package com.software.ssp.erkc.modules.signup

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

/**
 * @author Alexander Popov on 23.10.2016.
 */
interface ISignUpView : IView {
    fun navigateToMainScreen()
    fun showCaptcha(image: ByteArray)
    fun setProgressVisibility(isVisible: Boolean)
    fun showPinSuggestDialog(login: String)
}

interface ISignUpPresenter : IPresenter<ISignUpView> {
    fun onSignUpButtonClick(
            login: String,
            password: String,
            password2: String,
            name: String,
            email: String,
            turing: String
    )
    fun onPinReject()
    fun onCaptchaClick()
}