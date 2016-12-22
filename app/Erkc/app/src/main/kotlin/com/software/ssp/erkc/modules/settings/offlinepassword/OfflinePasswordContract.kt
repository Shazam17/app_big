package com.software.ssp.erkc.modules.settings.offlinepassword

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IOfflinePasswordView : IView {
    fun showPasswordError(errorResId: Int)
    fun showRePasswordError(errorResId: Int)
    fun close()
    fun didSavedOfflinePassword()
}

interface IOfflinePasswordPresenter : IPresenter<IOfflinePasswordView> {
    fun onPasswordChange(text: String)
    fun onConfirmPasswordChange(text: String)
    fun onSaveButtonClick()
}