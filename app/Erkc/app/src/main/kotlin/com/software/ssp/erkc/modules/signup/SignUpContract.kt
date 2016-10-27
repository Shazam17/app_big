package com.software.ssp.erkc.modules.signup

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

/**
 * @author Alexander Popov on 23.10.2016.
 */
interface ISignUpView : IView {
    fun navigateToDrawerScreen()
    fun setProgressVisibility(isVisible: Boolean)
}

interface ISignUpPresenter : IPresenter<ISignUpView> {
    fun onRegistrationButtonClick(
            login: String,
            password: String,
            password2: String,
            name: String,
            email: String
    )
}