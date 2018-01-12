package com.software.ssp.erkc.modules.fastauth

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IEnterPinView : IView {
    fun close()
    fun navigateToSignInScreen()
}

interface IEnterPinPresenter : IPresenter<IEnterPinView> {
    fun onBackPressed()
    fun onArrowClosePressed()
    fun saveAccessToken()
}