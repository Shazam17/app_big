package com.software.ssp.erkc.modules.drawer

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.User


interface IDrawerView : IView {

    fun showUserInfo(user: User)
    fun navigateToLoginScreen()
}

interface IDrawerPresenter : IPresenter<IDrawerView> {

    fun onLogoutClick()
}

