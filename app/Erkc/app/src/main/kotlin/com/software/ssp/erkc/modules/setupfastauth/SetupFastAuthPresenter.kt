package com.software.ssp.erkc.modules.setupfastauth

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import javax.inject.Inject


class SetupFastAuthPresenter @Inject constructor(view: ISetupFastAuthView) : RxPresenter<ISetupFastAuthView>(view), ISetupFastAuthPresenter {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession
    override fun onViewAttached() {
        super.onViewAttached()
    }

    override fun saveApiToken() {
        authRepository.saveTokenApi(activeSession.accessToken ?: "")
    }
}
