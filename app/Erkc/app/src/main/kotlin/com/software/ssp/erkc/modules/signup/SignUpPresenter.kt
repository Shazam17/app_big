package com.software.ssp.erkc.modules.signup

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.repositories.AccountRepository
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.extensions.isEmail
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

    override fun onViewAttached() {
        super.onViewAttached()
        subscriptions += authRepository.
                getCapcha(activeSession.appToken!!)
                .subscribe({
                    captcha ->
                    view?.showCaptcha(captcha.image)
                }, {
                    error ->
                    view?.showMessage(error.message!!)
                })
    }

    override fun onSignUpButtonClick(login: String, password: String, password2: String, name: String, email: String, turing: String) {
        if (login.isBlank()
                || password.isBlank()
                || password2.isBlank()
                || name.isBlank()
                || email.isBlank()
                || turing.isBlank()) {
            view?.showMessage(R.string.error_all_field_required)
            return
        }
        if (!email.isEmail()) {
            view?.showMessage(R.string.error_invalid_email)
            return
        }
        view?.setProgressVisibility(true)
        subscriptions += authRepository
                .registration(
                        activeSession.appToken!!,
                        name,
                        login,
                        email,
                        password,
                        password2,
                        turing)
                .concatMap {
                    authRepository.authenticate(activeSession.appToken!!,
                            login,
                            password)
                }
                .concatMap {
                    authData ->
                    activeSession.accessToken = authData.access_token
                    accountRepository.fetchUserInfo(activeSession.accessToken!!)
                }
                .subscribe(
                        {
                            userResponse ->
                            activeSession.user = userResponse
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