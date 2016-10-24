package com.software.ssp.erkc.modules.passwordrecovery

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IPasswordRecoveryView : IView {
    fun setSendButtonEnabled(enabled: Boolean = true)
    fun navigateToSignInScreen()
    fun setCaptchaImage(imageBase64: String)
    fun showCaptchaError(errorStringResId: Int)
}

interface IPasswordRecoveryPresenter : IPresenter<IPasswordRecoveryView> {
    fun onLoginChanged(login: String)
    fun onEmailChanged(email: String)
    fun onCaptchaChanged(captcha: String)
    fun onSendButtonClick()
}
