package com.software.ssp.erkc.modules.fastauth

import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.*
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.SettingsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.modules.pushnotifications.NotificationServiceManager
import rx.Observable
import rx.lang.kotlin.plusAssign
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EnterPinPresenter @Inject constructor(view: IEnterPinView) : RxPresenter<IEnterPinView>(view), IEnterPinPresenter {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var eventBus: Relay<Any, Any>
    @Inject lateinit var notificationServiceManager: NotificationServiceManager

    override fun onViewAttached() {
        super.onViewAttached()

        checkAuth()

        fetchUserLogin()
        subscribeToLogoutEvent()
    }

    private fun checkAuth() {
        if(activeSession.accessToken.isNullOrEmpty() && authRepository.getLocalTokenApi().isEmpty()) {
            logout()
        }
    }

    fun fetchUserLogin() {
        subscriptions += realmRepository
            .fetchCurrentUser()
            .subscribe(
                {
                    currentUser ->
                    view?.setLogin(currentUser.login)
                },
                {
                    error ->
                    view?.showMessage(error.parsedMessage())
                    view?.navigateToMainScreen()
                }
            )
    }

    override fun saveAccessToken() {
        if(!activeSession.accessToken.isNullOrEmpty()) {
            authRepository.saveTokenApi(activeSession.accessToken ?: "")
        } else {
            activeSession.accessToken = authRepository.getLocalTokenApi()
        }
    }

    private fun resetCurrentUser() {
        subscriptions += realmRepository.setCurrentUser(RealmUser())
            .subscribe(
                {
                    view?.navigateToMainScreen()
                },
                {
                    error ->
                    view?.showMessage(error.parsedMessage())
                }
            )
    }

    fun logout() {
        if (activeSession.isOfflineSession) {
            authRepository.saveTokenApi("")
            resetCurrentUser()
            eventBus.call(LogoutFinished())
        } else {
            subscriptions += settingsRepository
                    .unregisterFbToken(notificationServiceManager.deviceId)
                    .subscribe(
                            {
                                notificationServiceManager.stopPushService()
                                authRepository.saveTokenApi("")
                                resetCurrentUser()
                                eventBus.call(LogoutFinished())
                            },
                            {
                                error ->
                                authRepository.saveTokenApi("")
                                resetCurrentUser()
                                eventBus.call(LogoutFinished())
                            }
                    )
        }
    }

    override fun onArrowClosePressed() {
        view?.navigateToMainScreen()
    }

    override fun onAttemptsFailed() {
        logout()
    }

    override fun onBackPressed() {
        view?.navigateToMainScreen()
    }

    private fun subscribeToLogoutEvent() {
        subscriptions += eventBus.ofType(Logout::class.java)
                .subscribe {
                    event ->
                    logout()
                }
    }
}