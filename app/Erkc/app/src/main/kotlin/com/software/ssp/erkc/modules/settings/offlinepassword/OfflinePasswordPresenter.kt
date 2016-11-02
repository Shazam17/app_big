package com.software.ssp.erkc.modules.settings.offlinepassword

import android.util.Log
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.OfflineUserSettings
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
    private lateinit var offlineUserSettings: OfflineUserSettings

    override fun onViewAttached() {
        login = activeSession.user!!.login
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
        subscriptions += realmRepository.updateOfflineSettings(offlineUserSettings).subscribe(
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

    private fun fetchData() {
        subscriptions += realmRepository.fetchOfflineSettings(login)
                .subscribe(
                        { settings ->
                            offlineUserSettings = settings

                            // todo delete after testing
                            Log.d("---", "offlineUserSettings password: ${offlineUserSettings.password} ");
                            Log.d("---", "offlineUserSettings passwordHash: ${offlineUserSettings.passwordHash} ");
                        },
                        { throwable -> view?.showMessage(throwable.parsedMessage()) }
                )
    }
}