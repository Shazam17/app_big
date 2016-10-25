package com.software.ssp.erkc.modules.signin

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.repositories.AccountRepository
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class SignInPresenter @Inject constructor(view: ISignInView) : RxPresenter<ISignInView>(view), ISignInPresenter {

    @Inject lateinit var authProvider: AuthProvider
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var accountRepository: AccountRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onLoginButtonClick(email: String, password: String) {
        if (validateFields(email, password)) {
            login(email, password)
        }
    }

    override fun onForgotPasswordButtonClick(email: String) {
        view?.navigateToForgotPasswordScreen(email)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private fun validateFields(email: String?, password: String?): Boolean {
        var isValid = true

        if (email == null || email.isEmpty()) {
            isValid = false
            view?.showLoginFieldError(R.string.sign_in_error_fill_login_text)
        }

        if (password == null || password.isEmpty()) {
            isValid = false
            view?.showPasswordFieldError(R.string.sign_in_error_fill_password_text)
        }

        return isValid
    }

    private fun login(email: String, password: String) {
        view?.setProgressVisibility(true)

        subscriptions += authRepository
                .authenticate(activeSession.appToken!!, email, password)
                .concatMap {
                    authResponse ->
                    activeSession.accessToken = authResponse.data.access_token
                    accountRepository.fetchUserInfo(activeSession.accessToken!!)
                }
                .subscribe(
                        {
                            userResponse ->
                            activeSession.user = userResponse.data
                            view?.setProgressVisibility(false)
                            view?.navigateToDrawerScreen()
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            error.printStackTrace()
                            view?.showMessage(error.message.toString())
                        }
                )
    }
}

