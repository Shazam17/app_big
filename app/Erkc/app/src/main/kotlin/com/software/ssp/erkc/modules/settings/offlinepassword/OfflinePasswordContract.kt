package com.software.ssp.erkc.modules.settings.offlinepassword

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IOfflinePasswordView : IView {
    fun showSecondPasswordError(errorResId: Int?)
    fun enableSendButton(enabled: Boolean)
    fun dismiss()
}

interface IOfflinePasswordPresenter : IPresenter<IOfflinePasswordView> {
    fun onFirstInputChange(text: String)
    fun onSecondInputChange(text: String)
    fun onSaveButtonClick()
}