package com.software.ssp.erkc.modules.fastauth.deletepin

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IDeletePinView : IView {
    fun navigateToMainScreen()
    fun setLogin(login: String)
}

interface IDeletePinPresenter : IPresenter<IDeletePinView> {
    fun removeAccessToken()
    fun onAttemptsFailed()
}