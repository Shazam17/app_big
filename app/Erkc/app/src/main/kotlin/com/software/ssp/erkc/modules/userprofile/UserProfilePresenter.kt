package com.software.ssp.erkc.modules.userprofile

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import javax.inject.Inject


class UserProfilePresenter @Inject constructor(view: IUserProfileView) : RxPresenter<IUserProfileView>(view), IUserProfilePresenter {

    @Inject lateinit var authProvider: AuthProvider
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession


}
