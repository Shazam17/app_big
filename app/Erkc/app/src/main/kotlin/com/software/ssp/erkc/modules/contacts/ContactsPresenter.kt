package com.software.ssp.erkc.modules.contacts

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.FaqRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class ContactsPresenter @Inject constructor(view: IContactsView) : RxPresenter<IContactsView>(view), IContactsPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var faqRepository: FaqRepository
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewAttached() {
        view?.setControlsVisible(activeSession.accessToken != null)
    }

    override fun onViewDetached() {
        super.onViewDetached()
        realmRepository.close()
    }

    override fun onSendButtonClick(message: String, subjectPrefix: String) {
        if (!validData(message)) {
            return
        }

        view?.setPending(true)
        subscriptions += realmRepository.fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    faqRepository.sendMessage(
                            currentUser.name,
                            currentUser.login,
                            currentUser.email,
                            message,
                            subjectPrefix + message.subSequence(0, Math.min(message.length, 60))
                    )
                }
                .subscribe(
                        { response ->
                            view?.setPending(false)
                            view?.didSentMessage()
                        },
                        { error ->
                            view?.setPending(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
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
