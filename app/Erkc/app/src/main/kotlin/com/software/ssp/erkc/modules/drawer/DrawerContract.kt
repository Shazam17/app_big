package com.software.ssp.erkc.modules.drawer

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.User


interface IDrawerView : IView {

    fun showUserInfo(user: User)
    fun clearUserInfo()
    fun setAuthedMenuVisible(isVisible: Boolean)
    fun navigateToDrawerItem(drawerItem: DrawerItem)
    fun navigateToMainScreen()
    fun navigateToUserProfile()
}

interface IDrawerPresenter : IPresenter<IDrawerView> {
    fun onLogoutClick()
    fun onUserProfileClick()
    fun onUserProfileUpdated()
}