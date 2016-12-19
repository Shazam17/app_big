package com.software.ssp.erkc.modules.settings.offlinepassword

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IOfflinePasswordView : IView {
    fun showSecondPasswordError(errorResId: Int)
    fun showSecondPasswordNormalState()
    fun enableSendButton(enabled: Boolean)
    fun dismiss()
    fun didSavedOfflinePassword()
}

interface IOfflinePasswordPresenter : IPresenter<IOfflinePasswordView> {
    fun onPasswordChange(text: String)
    fun onConfirmPasswordChange(text: String)
    fun onSaveButtonClick()
}