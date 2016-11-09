package com.software.ssp.erkc.modules.signin

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.AccountRepository
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class SignInPresenter @Inject constructor(view: ISignInView) : RxPresenter<ISignInView>(view), ISignInPresenter {

    @Inject lateinit var authProvider: AuthProvider
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var accountRepository: AccountRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached() {
        super.onViewAttached()
    }

    override fun onLoginButtonClick(login: String, password: String) {
        if (validateFields(login, password)) {
            login(login, password)
        }
    }

    override fun onForgotPasswordButtonClick() {
        view?.navigateToForgotPasswordScreen()
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private fun validateFields(login: String?, password: String?): Boolean {
        var isValid = true

        if (login == null || login.isEmpty()) {
            isValid = false
            view?.showLoginFieldError(R.string.sign_in_error_fill_login_text)
        }

        if (password == null || password.isEmpty()) {
            isValid = false
            view?.showPasswordFieldError(R.string.sign_in_error_fill_password_text)
        }

        return isValid
    }

    private fun login(email: String, password: String) {
        view?.setProgressVisibility(true)

        subscriptions += authRepository
                .authenticate(activeSession.appToken!!, email, password)
                .concatMap {
                    authData ->
                    activeSession.accessToken = authData.access_token
                    accountRepository.fetchUserInfo(activeSession.accessToken!!)
                }
                .concatMap {
                    userResponse ->
                    activeSession.user = userResponse
                    receiptsRepository.fetchReceipts(activeSession.accessToken!!)
                }
                .subscribe(
                        {
                            receipts ->

                            activeSession.cachedReceipts = receipts?.sortedBy { it.address }

                            //TODO REMOVE NEXT
                            activeSession.cachedReceipts = listOf(
                                    Receipt("1", null, "1", "aaa", "2", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("2", null, "1", "bbb", "2", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("3", null, "1", "ccc", "2", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("4", null, "1", "ddd", "1", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("5", null, "1", "eee", "1", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("6", null, "1", "fff", "1", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("7", null, "1", "ggg", "3", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("8", null, "1", "hhh", "1", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("9", null, "1", "iii", "4", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("10", null, "1", "jjj", "1", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("11", null, "1", "kkk", "4", "asdasd", "0000000000000", "type", null, null),
                                    Receipt("12", null, "1", "ddd", "1", "asdasd", "0000000000000", "type", null, null)
                            ).sortedBy { it.address }

                            view?.setProgressVisibility(false)
                            view?.navigateToDrawerScreen()
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            error.printStackTrace()
                            view?.showMessage(error.message.toString())
                        }
                )
    }
}

