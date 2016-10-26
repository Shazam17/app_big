package com.software.ssp.erkc.modules.userprofile

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IUserProfileView : IView {
    fun navigateToMain()
    fun setProgressVisibility(isVisible: Boolean)
}

interface IUserProfilePresenter : IPresenter<IUserProfileView> {
}