package com.software.ssp.erkc.modules.passwordrecovery

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IPasswordRecoveryView : IView {
    fun setSendButtonEnabled(enabled: Boolean = true)
    fun navigateToSignInScreen()
}

interface IPasswordRecoveryPresenter : IPresenter<IPasswordRecoveryView> {
    fun onLoginChanged(login: String)
    fun onEmailChanged(email: String)

    fun onSendButtonClick()
}
