package com.software.ssp.erkc.modules.contacts

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IContactsView : IView {
    fun sendEmailMessage(userInfo: String, message: String)
    fun setControlsEnabled(isEnabled: Boolean)
}

interface IContactsPresenter : IPresenter<IContactsView> {
    fun onSendButtonClick(message: String)
}

