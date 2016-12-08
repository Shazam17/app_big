package com.software.ssp.erkc.modules.signin

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.repositories.*
import com.software.ssp.erkc.extensions.parsedMessage
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
            login(login, password)
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
//                .concatMap { settingsRepository.getSettings() } TODO uncomment when worked
//                .concatMap {
//                    settings ->
//                    realmRepository.updateSettings(settings)
//                }
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
                            view?.navigateToMainScreen()
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
