package com.software.ssp.erkc.modules.fastauth.changepin

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IChangePinView : IView {
    fun navigateToMainScreen()
    fun setLogin(login: String)
}

interface IChangePinPresenter : IPresenter<IChangePinView> {
    fun saveAccessToken()
    fun onAttemptsFailed()
}