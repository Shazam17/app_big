package com.software.ssp.erkc.modules.userprofile

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AccountRepository
import com.software.ssp.erkc.extensions.isEmail
import javax.inject.Inject


class UserProfilePresenter @Inject constructor(view: IUserProfileView) : RxPresenter<IUserProfileView>(view), IUserProfilePresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var accountRepository: AccountRepository

    override fun onViewAttached() {
        super.onViewAttached()
        activeSession.user?.let {
            view?.showUserInfo(it)
        }
    }

    override fun onSaveButtonClick(name: String, email: String, password: String, rePassword: String) {
        if(!validateFields(name, email, password, rePassword)){
            return
        }
        view?.setProgressVisibility(true)

        //TODO update user API when it fix
//        subscriptions += accountRepository.updateUserInfo(activeSession.accessToken!!, name, email)
//        .subscribe(
//                {
//                    response ->
//                    view?.setProgressVisibility(false)
//                    view?.showMessage(R.string.user_profile_data_saved)
//                    view?.close()
//
//                },
//                {
//                    error ->
//                    view?.setProgressVisibility(false)
//                    view?.showMessage(error.message.toString())
//                }
//        )

        view?.setProgressVisibility(false)
        view?.showMessage("TODO: API IMPLEMENT")
    }

    private fun validateFields(name: String, email: String, password: String, rePassword: String) : Boolean {
        var isValid = true

        if(name.isNullOrEmpty()){
            isValid = false
            view?.showErrorNameMessage(R.string.error_empty_field)
        }

        if(email.isNullOrEmpty()){
            isValid = false
            view?.showErrorEmailMessage(R.string.error_empty_field)
        } else {
            if(!email.isEmail()){
                isValid = false
                view?.showErrorEmailMessage(R.string.error_invalid_login)
            }
        }

        if(!password.isNullOrEmpty() || !rePassword.isNullOrEmpty()){
            if(password != rePassword){
                view?.showErrorPasswordMessage(R.string.error_passwords_not_match)
                isValid = false
            }
        }

        return isValid
    }
}
