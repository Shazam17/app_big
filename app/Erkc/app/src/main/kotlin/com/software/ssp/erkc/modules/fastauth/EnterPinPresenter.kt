package com.software.ssp.erkc.modules.fastauth

import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.*
import com.software.ssp.erkc.common.mvp.RxPresenter
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

        fetchUserLogin()
        subscribeToLogoutEvent()
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
        authRepository.saveTokenApi(activeSession.accessToken ?: "")
    }

    fun logout() {
        if (activeSession.isOfflineSession) {
            authRepository.saveTokenApi("")
            view?.navigateToMainScreen()
            eventBus.call(LogoutFinished())
        } else {
            subscriptions += settingsRepository
                    .unregisterFbToken(notificationServiceManager.deviceId)
                    .subscribe(
                            {
                                notificationServiceManager.stopPushService()
                                authRepository.saveTokenApi("")
                                view?.navigateToMainScreen()
                                eventBus.call(LogoutFinished())
                            },
                            {
                                error ->
                                authRepository.saveTokenApi("")
                                view?.navigateToMainScreen()
                                eventBus.call(LogoutFinished())
                            }
                    )
        }
    }

    override fun onArrowClosePressed() {
        logout()
    }

    override fun onAttemptsFailed() {
        logout()
    }

    override fun onBackPressed() {
        logout()
    }

    private fun subscribeToLogoutEvent() {
        subscriptions += eventBus.ofType(Logout::class.java)
                .subscribe {
                    event ->
                    logout()
                }
    }
}