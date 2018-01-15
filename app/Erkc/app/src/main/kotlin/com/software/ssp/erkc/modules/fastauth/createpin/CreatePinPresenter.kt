package com.software.ssp.erkc.modules.fastauth.createpin

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class CreatePinPresenter @Inject constructor(view: ICreatePinView) : RxPresenter<ICreatePinView>(view), ICreatePinPresenter {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewAttached() {
        super.onViewAttached()

        fetchUserLogin()
    }

    override fun saveAccessToken() {
        authRepository.saveTokenApi(activeSession.accessToken ?: "")
    }

    override fun onBackPressed() {
        view?.navigateToDrawerScreen()
    }

    fun fetchUserLogin() {
        subscriptions += realmRepository
            .fetchCurrentUser()
            .subscribe(
                {
                    currentUser ->
                    view?.setLogin(currentUser.login)
                },
                {
                    error ->
                    view?.showMessage(error.parsedMessage())
                    view?.navigateToDrawerScreen()
                }
            )
    }
}