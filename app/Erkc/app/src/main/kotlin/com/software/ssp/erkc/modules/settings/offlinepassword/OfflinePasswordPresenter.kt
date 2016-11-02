package com.software.ssp.erkc.modules.settings.offlinepassword

import android.util.Log
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.OfflineSettings
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class OfflinePasswordPresenter @Inject constructor(view: IOfflinePasswordView) : RxPresenter<IOfflinePasswordView>(view), IOfflinePasswordPresenter {

    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var activeSession: ActiveSession

    private lateinit var login: String
    private lateinit var password: String
    private lateinit var offlineSettings: OfflineSettings

    override fun onViewAttached() {
        login = activeSession.user!!.login
        offlineSettings = realmRepository.fetchOfflineSettings(login)
    }

    override fun onFirstInputChange(text: String) {
        password = text
    }

    override fun onSecondInputChange(text: String) {
        if (validate(text)) {
            view?.enableSendButton(true)
            view?.showSecondPasswordError(null)
        } else {
            view?.enableSendButton(false)
            view?.showSecondPasswordError(R.string.offline_password_second_error)
        }
    }

    override fun onSaveButtonClick() {
        offlineSettings.password = password
        subscriptions += realmRepository.updateOfflineSettings(offlineSettings).subscribe(
                { isSuccess ->
                    view?.dismiss()
                },
                { throwable ->
                    view?.showMessage(throwable.parsedMessage())
                    view?.dismiss()
                })
    }

    private fun validate(secondPassword: String): Boolean {
        return password == secondPassword
    }
}