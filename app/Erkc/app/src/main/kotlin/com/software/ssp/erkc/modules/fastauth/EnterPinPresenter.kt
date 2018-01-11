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
    private var doubleBackToExitPressedOnce = false

    override fun onViewAttached() {
        super.onViewAttached()

        subscribeToLogoutEvent()
    }

    fun logout() {
        if (activeSession.isOfflineSession) {
            activeSession.clear()
            authRepository.saveTokenApi("")
            eventBus.call(LogoutFinished())
        } else {
            subscriptions += settingsRepository
                    .unregisterFbToken(notificationServiceManager.deviceId)
                    .subscribe(
                            {
                                notificationServiceManager.stopPushService()
                                activeSession.clear()
                                authRepository.saveTokenApi("")
                                view?.navigateToSignInScreen()
                                eventBus.call(LogoutFinished())
                            },
                            {
                                error ->
                                view?.showMessage(error.parsedMessage())
                            }
                    )
        }
    }

    override fun onArrowClosePressed() {
        logout()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            view?.close()
        } else {
            doubleBackToExitPressedOnce = true
            view?.showMessage(R.string.on_back_pressed_text)
            subscriptions += Observable.timer(2, TimeUnit.SECONDS)
                    .subscribe({
                        doubleBackToExitPressedOnce = false
                    })
        }

    }

    private fun subscribeToLogoutEvent() {
        subscriptions += eventBus.ofType(Logout::class.java)
                .subscribe {
                    event ->
                    logout()
                }
    }
}