package com.software.ssp.erkc.modules.splash

import android.support.annotation.StringRes
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

/**
 * @author Alexander Popov on 23.10.2016.
 */
interface ISplashView : IView {
    fun navigateToSignIn()
    fun showTryAgainSnack(@StringRes message: Int)
    fun showTryAgainSnack(message: String)
}

interface ISplashPresenter : IPresenter<ISplashView> {
    fun onTryAgainClicked()
}