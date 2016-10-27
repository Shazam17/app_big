package com.software.ssp.erkc.modules.passwordrecovery

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.plusAssign
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SignInPresenter @Inject constructor(view: IPasswordRecoveryView) : RxPresenter<IPasswordRecoveryView>(view), IPasswordRecoveryPresenter {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession

    private var isLoading = false
    private var email: String = ""
    private var login: String = ""
    private var captcha: String = "12345"  //  todo change to "" when API will be ready OR delete IF captcha will be removed from project

    private val READING_DELAY_SECONDS = 3L

    override fun onViewAttached() {
    }

    override fun onLoginChanged(login: String) {
        this.login = login
        validateCredentials()
    }

    override fun onEmailChanged(email: String) {
        this.email = email
        validateCredentials()
    }

    override fun onSendButtonClick() {
        if (isLoading) return
        isLoading = true
        subscriptions += authRepository.recoverPassword(activeSession.appToken!!, login, email)
                .flatMap { response ->
                    isLoading = false
                    view?.showMessage(R.string.pass_recovery_sent_message)
                    Observable.interval(READING_DELAY_SECONDS, TimeUnit.SECONDS).take(1)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response -> view?.navigateToSignInScreen() },
                        { throwable ->
                            isLoading = false
                            view?.showMessage(throwable.parsedMessage())
                        }
                )
    }

    private fun validateCredentials() {
        val isValid = !email.isBlank() && !login.isBlank() && !captcha.isBlank()
        view?.setSendButtonEnabled(isValid)

    }
}