package com.software.ssp.erkc.modules.fastauth.createpin

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface ICreatePinView : IView {
    fun setLogin(login: String)
    fun navigateToDrawerScreen()
}

interface ICreatePinPresenter : IPresenter<ICreatePinView> {
    fun saveAccessToken()
    fun onBackPressed()
}