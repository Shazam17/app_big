package com.software.ssp.erkc.modules.signup

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

/**
 * @author Alexander Popov on 23.10.2016.
 */
interface ISignUpView : IView {
    fun navigateToMain()
    fun setProgressVisibility(isVisible: Boolean)
    fun fillAddress(name: String)
}

interface ISignUpPresenter : IPresenter<ISignUpView> {
    fun onRegistrationButtonClick(
            login: String,
            password: String,
            firstName: String,
            street: String,
            build: String,
            flat: String,
            email: String
    )

    fun onAddressSelected(addressId: Long)
}