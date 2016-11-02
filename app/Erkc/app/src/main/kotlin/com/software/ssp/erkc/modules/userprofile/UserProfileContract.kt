package com.software.ssp.erkc.modules.userprofile

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.User


interface IUserProfileView : IView {
    fun close()
    fun setProgressVisibility(isVisible: Boolean)
    fun showUserInfo(user: User)
    fun showErrorNameMessage(resId: Int)
    fun showErrorEmailMessage(resId: Int)
    fun showErrorPasswordMessage(resId: Int)
    fun didUserProfileUpdated()
}

interface IUserProfilePresenter : IPresenter<IUserProfileView> {
    fun onSaveButtonClick(name: String, email: String, password: String, rePassword: String)
}