package com.software.ssp.erkc.modules.signup

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.repositories.AccountRepository
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
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


    override fun onRegistrationButtonClick(login: String, password: String, password2: String, name: String, email: String) {
        view?.setProgressVisibility(true)
        subscriptions += authRepository
                .registration(
                        activeSession.accessToken!!,
                        name,
                        login,
                        email,
                        password,
                        password2)
                .concatMap {
                    authRepository.authenticate(activeSession.accessToken!!,
                            login,
                            password)
                }
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