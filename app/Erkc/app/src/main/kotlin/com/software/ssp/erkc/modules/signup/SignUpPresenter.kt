package com.software.ssp.erkc.modules.signup

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.repositories.*
import com.software.ssp.erkc.extensions.isEmail
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.modules.pushnotifications.NotificationServiceManager
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SignUpPresenter @Inject constructor(view: ISignUpView) : RxPresenter<ISignUpView>(view), ISignUpPresenter {

    @Inject lateinit var authProvider: AuthProvider
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var accountRepository: AccountRepository
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var notificationServiceManager: NotificationServiceManager

    override fun onViewAttached() {
        super.onViewAttached()

        fetchCaptcha()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onCaptchaClick() {
        fetchCaptcha()
    }

    override fun onSignUpButtonClick(login: String, password: String, password2: String, name: String, email: String, turing: String) {

        if (login.isBlank()
                || password.isBlank()
                || password2.isBlank()
                || name.isBlank()
                || email.isBlank()
                || turing.isBlank()) {
            view?.showMessage(R.string.error_all_fields_required)
            return
        }
        if (!email.isEmail()) {
            view?.showMessage(R.string.error_invalid_email)
            return
        }
        view?.setProgressVisibility(true)
        subscriptions += authRepository
                .registration(
                        name,
                        login,
                        email,
                        password,
                        password2,
                        turing)
                .concatMap {
                    authRepository.authenticate(login, password)
                }
                .concatMap {
                    authData ->
                    activeSession.accessToken = authData.access_token
                    notificationServiceManager.fcmToken?.let {
                        settingsRepository.registerFbToken(notificationServiceManager.deviceId, it)
                    }

                    Observable.just(null)
                }
                .concatMap {
                    accountRepository.fetchUserInfo()
                }
                .concatMap {
                    user ->
                    realmRepository.fetchUser(user)
                }
                .concatMap {
                    realmUser ->
                    realmRepository.setCurrentUser(realmUser)
                }
                .concatMap {
                    receiptsRepository.fetchReceipts()
                }
                .concatMap {
                    receipts ->
                    realmRepository.saveReceiptsList(receipts ?: emptyList())
                }
                .subscribe(
                        {
                            notificationServiceManager.startPushService()
                            view?.setProgressVisibility(false)
                            view?.showPinSuggestDialog(login)
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            error.printStackTrace()
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    override fun onPinReject() {
        authRepository.saveTokenApi("")
        view?.navigateToMainScreen()
    }

    private fun fetchCaptcha() {
        subscriptions += authRepository
                .getCaptcha()
                .subscribe(
                        {
                            captcha ->
                            view?.showCaptcha(captcha.image)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }
}
