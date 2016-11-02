package com.software.ssp.erkc.modules.contacts

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.FaqRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class ContactsPresenter @Inject constructor(view: IContactsView) : RxPresenter<IContactsView>(view), IContactsPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var faqRepository: FaqRepository

    override fun onViewAttached() {
        view?.setControlsVisible(activeSession.user != null)
    }

    override fun onSendButtonClick(message: String, subjectPrefix: String) {
        if(!validData(message)){
            return
        }

        with(activeSession) {
            view?.setPending(true)
            subscriptions += faqRepository.sendMessage(accessToken!!, user?.name!!, user?.login!!, user?.email!!, message, subjectPrefix + message.subSequence(0, Math.min(message.length, 60)))
                    .subscribe(
                            { response ->
                                view?.setPending(false)
                                view?.showDidSentMessage()
                            },
                            { error ->
                                view?.setPending(false)
                                view?.showMessage(error.message.toString())
                            }
                    )
        }
    }

    private fun validData(message: String): Boolean {
        var isValid = true

        if (message.isNullOrBlank()) {
            view?.showMessageEmptyError(R.string.error_empty_field)
            isValid = false
        }

        return isValid
    }
}
