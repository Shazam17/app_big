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

    private lateinit var offlineUserSettings: RealmSettings

    private var password: String = ""
    private var rePassword: String = ""

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
        rePassword = text
    }

    override fun onSaveButtonClick() {
        if (!validateData()) {
            return
        }

        offlineUserSettings.password = password

        subscriptions += realmRepository.updateOfflineSettings(offlineUserSettings)
                .subscribe(
                        {
                            view?.didSavedOfflinePassword()
                            view?.close()
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.close()
                        })
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

    private fun validateData(): Boolean {
        return when {
            password.isNullOrBlank() -> {
                view?.showPasswordError(R.string.error_empty_field)
                false
            }

            rePassword.isNullOrBlank() -> {
                view?.showRePasswordError(R.string.error_empty_field)
                false
            }

            password != rePassword -> {
                view?.showRePasswordError(R.string.password_not_equals)
                false
            }

            else -> true
        }
    }
}
