package com.software.ssp.erkc.modules.drawer

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


class DrawerPresenter @Inject constructor(view: IDrawerView) : RxPresenter<IDrawerView>(view), IDrawerPresenter {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var eventBus: Relay<Any, Any>
    @Inject lateinit var notificationServiceManager: NotificationServiceManager
    private var doubleBackToExitPressedOnce = false

    var nonAuthImitation = false

    override fun setNonAuthImitation() {
        nonAuthImitation = true
    }

    override fun onViewAttached() {
        super.onViewAttached()

        subscribeToOpenCardsEvent()
        subscribeToOpenHistoryWithReceipt()
        subscribeToOpenInstructionsEvent()
        subscribeToLogoutEvent()

        showCurrentUser()
    }

    override fun onViewDetached() {
        super.onViewDetached()
        realmRepository.close()
    }

    override fun onLogoutClick() {
        if (activeSession.isOfflineSession) {
            authRepository.saveTokenApi("")
            activeSession.clear()
            view?.clearUserInfo()
            view?.setAuthedMenuVisible(false)
            eventBus.call(LogoutFinished())
            view?.navigateToSplashScreen()
        } else {
            subscriptions += settingsRepository
                    .unregisterFbToken(notificationServiceManager.deviceId)
                    .subscribe(
                            {
                                notificationServiceManager.stopPushService()
                                authRepository.saveTokenApi("")
                                activeSession.clear()
                                view?.clearUserInfo()
                                view?.setAuthedMenuVisible(false)
                                view?.navigateToMainScreen()
                                eventBus.call(LogoutFinished())
                            },
                            {
                                error ->
                                view?.showMessage(error.parsedMessage())
                            }
                    )
        }
    }

    override fun onClear() {
        authRepository.saveTokenApi("")
        activeSession.clear()
    }

    override fun onUserProfileClick() {
        view?.navigateToUserProfile()
    }

    override fun onUserProfileUpdated() {
        showCurrentUser()
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            view?.navigateBack()
        } else {
            doubleBackToExitPressedOnce = true
            view?.showMessage(R.string.on_back_pressed_text)
            subscriptions += Observable.timer(2, TimeUnit.SECONDS)
                    .subscribe({
                        doubleBackToExitPressedOnce = false
                    })
        }

    }

    private fun showCurrentUser() {
        if (!authRepository.getLocalTokenApi().isEmpty() && activeSession.accessToken.isNullOrEmpty())
            activeSession.accessToken = authRepository.getLocalTokenApi()

        if (!activeSession.isOfflineSession && activeSession.accessToken.isNullOrEmpty() && !nonAuthImitation) {
            view?.setAuthedMenuVisible(false)
            view?.navigateToMainScreen()
            return
        }

        subscriptions += realmRepository.fetchCurrentUser()
                .subscribe(
                        {
                            currentUser ->
                            view?.setUserLogin(currentUser.login)
                            view?.showUserInfo(currentUser)
                            view?.setAuthedMenuVisible(true)
                            view?.navigateToMainScreen()
                            view?.updateCurrentScreen()
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun subscribeToOpenCardsEvent() {
        subscriptions += eventBus.ofType(OpenCardsEvent::class.java)
                .subscribe {
                    view?.navigateToDrawerItem(DrawerItem.CARDS)
                }
    }

    private fun subscribeToOpenHistoryWithReceipt() {
        subscriptions += eventBus.ofType(OpenHistoryWithReceiptEvent::class.java)
                .subscribe {
                    event ->
                    view?.navigateToHistory(event.receiptCode)
                }
    }

    private fun subscribeToOpenInstructionsEvent() {
        subscriptions += eventBus.ofType(OpenInstructionsList::class.java)
                .subscribe {
                    event ->
                    view?.navigateToDrawerItem(DrawerItem.TUTORIAL)
                }
    }

    private fun subscribeToLogoutEvent() {
        subscriptions += eventBus.ofType(Logout::class.java)
                .subscribe {
                    event ->
                    onLogoutClick()
                }
    }
}
