package com.software.ssp.erkc.modules.passwordrecovery

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class PasswordRecoveryPresenter @Inject constructor(view: IPasswordRecoveryView) : RxPresenter<IPasswordRecoveryView>(view), IPasswordRecoveryPresenter {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession

    private var isLoading = false
    private var email: String = ""
    private var login: String = ""
    private var captcha: String = ""

    override fun onViewAttached() {
        fetchCaptcha()
    }

    override fun onLoginChanged(login: String) {
        this.login = login
        validateCredentials()
    }

    override fun onEmailChanged(email: String) {
        this.email = email
        validateCredentials()
    }

    override fun onCaptchaChanged(captcha: String) {
        this.captcha = captcha
        validateCredentials()
    }

    override fun onSendButtonClick() {
        if (isLoading) return

        subscriptions += authRepository.recoverPassword(login, email, captcha)
                .doOnSubscribe { isLoading = true }
                .doOnTerminate { isLoading = false }
                .subscribe(
                        {
                            view?.showMessage(R.string.pass_recovery_sent_message)
                            view?.navigateToSignInScreen()
                        },
                        {
                            throwable ->
                            view?.showMessage(throwable.parsedMessage())
                            fetchCaptcha()
                        }
                )
    }

    override fun onCaptchaClick() {
        fetchCaptcha()
    }

    private fun validateCredentials() {
        val isValid = !email.isBlank() && !login.isBlank() && !captcha.isBlank()
        view?.setSendButtonEnabled(isValid)

    }

    private fun fetchCaptcha() {
        if (isLoading) return

        subscriptions += authRepository.getCaptcha()
                .doOnSubscribe { isLoading = true }
                .doOnTerminate { isLoading = false }
                .subscribe(
                        {
                            captcha ->
                            view?.showCaptcha(captcha.image)
                        },
                        {
                            error ->
                            view?.showMessage(error.message!!)
                        }
                )
    }
}
