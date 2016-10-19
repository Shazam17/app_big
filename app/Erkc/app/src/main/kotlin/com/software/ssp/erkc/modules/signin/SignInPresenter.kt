package com.software.ssp.erkc.modules.signin

import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject


class SignInPresenter @Inject constructor(view: ISignInView) : RxPresenter<ISignInView>(view), ISignInPresenter {

    @Inject lateinit var authProvider: AuthProvider
    @Inject lateinit var authRespository: AuthRepository

    private fun authenticate(login: String, password: String) {

    }
}