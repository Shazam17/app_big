package com.software.ssp.erkc.modules.splash

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

/**
 * @author Alexander Popov on 23.10.2016.
 */
interface ISplashView : IView {
    fun navigateToDrawer()
    fun showTryAgainSnack(message: String)
}

interface ISplashPresenter : IPresenter<ISplashView> {
    fun onTryAgainClicked()
}