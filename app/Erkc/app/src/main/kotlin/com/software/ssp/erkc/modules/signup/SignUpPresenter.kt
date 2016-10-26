package com.software.ssp.erkc.modules.signup

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.models.Address
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import io.realm.Realm
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SignUpPresenter @Inject constructor(view: ISignUpView) : RxPresenter<ISignUpView>(view), ISignUpPresenter {

    @Inject lateinit var authProvider: AuthProvider
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession


    override fun onRegistrationButtonClick(login: String, password: String, firstName: String, street: String, build: String, flat: String, email: String) {
        // need api descriptions first
        view?.setProgressVisibility(true)
    }

    override fun onAddressSelected(addressId: Long) {
        val realm = Realm.getDefaultInstance()
        val address = realm.where(Address::class.java).equalTo("id", addressId).findFirst()
        view?.fillAddress(address.name)
    }

}