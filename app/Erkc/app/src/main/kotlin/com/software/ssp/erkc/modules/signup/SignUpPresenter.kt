package com.software.ssp.erkc.modules.signup

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.repositories.AccountRepository
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.isEmail
import com.software.ssp.erkc.extensions.parsedMessage
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

    private fun fetchCaptcha() {
        subscriptions += authRepository.
                getCapcha()
                .subscribe({
                    captcha ->
                    view?.showCaptcha(captcha.image)
                }, {
                    error ->
                    view?.showMessage(error.message!!)
                })
    }
}
