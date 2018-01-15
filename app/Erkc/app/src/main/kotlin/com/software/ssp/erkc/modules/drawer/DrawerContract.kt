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
    fun navigateToSplashScreen()
    fun navigateToUserProfile()
    fun navigateToHistory(receiptCode: String)
    fun navigateBack()
    fun setProgressVisibility(isVisible: Boolean)
    fun setUserLogin(login: String)
}

interface IDrawerPresenter : IPresenter<IDrawerView> {
    fun onLogoutClick()
    fun onUserProfileClick()
    fun onUserProfileUpdated()
    fun onBackPressed()
    fun onClear()
    fun setNonAuthImitation()
}
