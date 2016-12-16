package com.software.ssp.erkc.modules.signin

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.repositories.*
import com.software.ssp.erkc.extensions.parsedMessage
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class SignInPresenter @Inject constructor(view: ISignInView) : RxPresenter<ISignInView>(view), ISignInPresenter {

    @Inject lateinit var authProvider: AuthProvider
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var accountRepository: AccountRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var cardsRepository: CardsRepository
    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onViewAttached() {
        super.onViewAttached()
    }

    override fun onLoginButtonClick(login: String, password: String) {
        if (validateFields(login, password)) {
            if (activeSession.appToken == null) {
                offlineLogin(login, password)
            } else {
                login(login, password)
            }
        }
    }

    override fun onForgotPasswordButtonClick() {
        view?.navigateToForgotPasswordScreen()
    }

    override fun onViewDetached() {
        super.onViewDetached()
        realmRepository.close()
    }

    private fun validateFields(login: String?, password: String?): Boolean {
        var isValid = true

        if (login == null || login.isEmpty()) {
            isValid = false
            view?.showLoginFieldError(R.string.sign_in_error_fill_login_text)
        }

        if (password == null || password.isEmpty()) {
            isValid = false
            view?.showPasswordFieldError(R.string.sign_in_error_fill_password_text)
        }

        return isValid
    }

    private fun login(login: String, password: String) {
        view?.setProgressVisibility(true)

        subscriptions += authRepository
                .authenticate(login, password)
                .concatMap {
                    authData ->
                    activeSession.accessToken = authData.access_token
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
                    settingsRepository.getSettings()
                }
                .concatMap {
                    settings ->
                    realmRepository.updateSettings(settings)
                }
                .concatMap {
                    cardsRepository.fetchCards()
                }
                .concatMap {
                    cards ->
                    realmRepository.saveCardsList(cards ?: emptyList())
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
                            view?.setProgressVisibility(false)
                            view?.setResultOk()
                            view?.close()
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            error.printStackTrace()
                            activeSession.clear()
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun offlineLogin(login: String, password: String) {
        view?.setProgressVisibility(true)
        subscriptions += realmRepository.fetchUser(login)
                .concatMap {
                    user ->
                    if (user == null) {
                        view?.showMessage(R.string.sign_in_offline_login_not_exist)
                    } else {
                        with(user.settings!!) {
                            if (offlineModeEnabled) {
                                if (checkPassword(password)) {
                                    return@concatMap realmRepository.setCurrentUser(user)
                                } else {
                                    view?.showMessage(R.string.sign_in_error_incorrect_login_or_password_text)
                                }
                            } else {
                                view?.showInfoDialog(R.string.sign_in_offline_disabled)
                            }
                        }
                    }

                    return@concatMap Observable.just(false)
                }
                .subscribe(
                        {
                            result ->
                            view?.setProgressVisibility(false)
                            if (result) {
                                activeSession.isOfflineSession = true
                                view?.setResultOk()
                                view?.close()
                            }
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            error.printStackTrace()
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }
}
