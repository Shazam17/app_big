package com.software.ssp.erkc.modules.contacts

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject

class ContactsPresenter @Inject constructor(view: IContactsView) : RxPresenter<IContactsView>(view), IContactsPresenter {

    override fun onViewAttached() {
        //TODO check authorised user or not
        view?.setControlsEnabled(true)
    }

    override fun onSendButtonClick() {
        view?.sendEmailMessage()
    }
}