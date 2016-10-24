package com.software.ssp.erkc.modules.passwordrecovery

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.plusAssign
import rx.lang.kotlin.subscribeWith
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SignInPresenter @Inject constructor(view: IPasswordRecoveryView) : RxPresenter<IPasswordRecoveryView>(view), IPasswordRecoveryPresenter {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession

    private val READING_DELAY = 3L
    private var isLoading = false

    override fun onTextsChanged(login: String, email: String) {
        val isValid = !(login.isBlank() || email.isBlank())
        view?.setSendButtonEnabled(isValid)
    }

    override fun onSendButtonClick(login: String, email: String) {
        if (isLoading) return

        subscriptions += authRepository.recoverPassword(activeSession.accessToken!!, login, email)
                .flatMap { response ->
                    view?.showMessage(R.string.pass_recovery_sent_message)
                    Observable.interval(READING_DELAY, TimeUnit.SECONDS).take(1)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith {
            onNext { view?.navigateToSignInScreen() }
            onError { throwable ->
                isLoading = false
                view?.showMessage(throwable.parsedMessage())
            }
        }
    }

}