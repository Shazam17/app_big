package com.software.ssp.erkc.modules.userprofile

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmUser


interface IUserProfileView : IView {
    fun close()
    fun setProgressVisibility(isVisible: Boolean)
    fun showUserInfo(user: RealmUser)
    fun showErrorNameMessage(resId: Int)
    fun showErrorEmailMessage(resId: Int)
    fun showErrorPasswordMessage(resId: Int)
    fun didUserProfileUpdated()
    fun navigateToPinCreateScreen()
    fun navigateToPinDeleteScreen()
    fun navigateToPinChangeScreen()
    fun showPinSuggestDialog()
    fun setUserLogin(login: String)
    fun showPinStatus()
}

interface IUserProfilePresenter : IPresenter<IUserProfileView> {
    fun onSaveButtonClick(name: String, email: String, password: String, rePassword: String)
    fun onPinChangeClick()
    fun onPinDeleteClick()
    fun onPinCreateClick()
    fun onPinReject()
    fun resumed()
}