package com.software.ssp.erkc.modules.drawer

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmUser


interface IDrawerView : IView {
    fun showUserInfo(user: RealmUser)
    fun clearUserInfo()
    fun setAuthedMenuVisible(isVisible: Boolean)
    fun updateCurrentScreen()
    fun navigateToDrawerItem(drawerItem: DrawerItem)
    fun navigateToMainScreen()
    fun navigateToUserProfile()
}

interface IDrawerPresenter : IPresenter<IDrawerView> {
    fun onLogoutClick()
    fun onUserProfileClick()
    fun onUserProfileUpdated()
}
