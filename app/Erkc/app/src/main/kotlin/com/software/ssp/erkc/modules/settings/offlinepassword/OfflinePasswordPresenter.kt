package com.software.ssp.erkc.modules.settings.offlinepassword

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmSettings
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class OfflinePasswordPresenter @Inject constructor(view: IOfflinePasswordView) : RxPresenter<IOfflinePasswordView>(view), IOfflinePasswordPresenter {

    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var activeSession: ActiveSession

    private lateinit var password: String
    private lateinit var offlineUserSettings: RealmSettings

    override fun onViewAttached() {
        fetchData()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onPasswordChange(text: String) {
        password = text
    }

    override fun onConfirmPasswordChange(text: String) {
        if (validate(text)) {
            view?.enableSendButton(true)
            view?.showSecondPasswordNormalState()
        } else {
            view?.enableSendButton(false)
            view?.showSecondPasswordError(R.string.offline_password_second_error)
        }
    }

    override fun onSaveButtonClick() {
        offlineUserSettings.password = password
        subscriptions += realmRepository.updateOfflineSettings(offlineUserSettings)
                .subscribe(
                {
                    view?.didSavedOfflinePassword()
                    view?.dismiss()
                },
                {
                    error ->
                    view?.showMessage(error.parsedMessage())
                    view?.dismiss()
                })
    }

    private fun validate(secondPassword: String): Boolean {
        return password == secondPassword
    }

    private fun fetchData() {
        subscriptions += realmRepository.fetchCurrentUser()
                .subscribe(
                        {
                            currentUser ->
                            offlineUserSettings = currentUser.settings!!
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }
}
