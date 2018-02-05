package com.software.ssp.erkc.modules.fastauth

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IEnterPinView : IView {
    fun navigateToMainScreen()
    fun navigateToLoginScreen()
    fun setLogin(login: String)
}

interface IEnterPinPresenter : IPresenter<IEnterPinView> {
    fun onBackPressed()
    fun onArrowClosePressed()
    fun onAttemptsFailed()
    fun saveAccessToken()
}