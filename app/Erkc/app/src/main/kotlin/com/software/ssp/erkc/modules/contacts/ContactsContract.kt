package com.software.ssp.erkc.modules.contacts

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IContactsView : IView {
    fun setPending(isPending: Boolean)
    fun setControlsVisible(isVisible: Boolean)
    fun showMessageEmptyError(resId: Int)
    fun showDidSentMessage()
}

interface IContactsPresenter : IPresenter<IContactsView> {
    fun onSendButtonClick(message: String, subjectPrefix: String)
}

