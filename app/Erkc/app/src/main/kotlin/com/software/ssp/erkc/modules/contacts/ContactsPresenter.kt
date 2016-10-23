package com.software.ssp.erkc.modules.contacts

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import javax.inject.Inject

class ContactsPresenter @Inject constructor(view: IContactsView) : RxPresenter<IContactsView>(view), IContactsPresenter {

    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached() {

        view?.setControlsVisible(activeSession.user!=null)
    }

    override fun onSendButtonClick(message: String) {
        //TODO SEND MASSAGE API CALL (include: login, name, email, message, subject = (const string + 60 chars from message))
        view?.showDidSentMessage()
    }
}