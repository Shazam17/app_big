package com.software.ssp.erkc.modules.signin

import android.util.Log
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.*
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.modules.pushnotifications.NotificationServiceManager
import rx.Observable
import rx.lang.kotlin.onError
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class SignInPresenter @Inject constructor(view: ISignInView) : RxPresenter<ISignInView>(view), ISignInPresenter {

    @Inject
    lateinit var authRepository: AuthRepository
    @Inject
    lateinit var accountRepository: AccountRepository
    @Inject
    lateinit var receiptsRepository: ReceiptsRepository
    @Inject
    lateinit var activeSession: ActiveSession
    @Inject
    lateinit var realmRepository: RealmRepository
    @Inject
    lateinit var cardsRepository: CardsRepository
    @Inject
    lateinit var settingsRepository: SettingsRepository
    @Inject
    lateinit var notificationServiceManager: NotificationServiceManager
    @Inject
    lateinit var requestRepository: RequestRepository

    override fun onViewAttached() {
        super.onViewAttached()
    }

    override fun onMenuCreated() {
        fetchUserLogin()
    }

    fun fetchUserLogin() {
        subscriptions += realmRepository
                .fetchCurrentUser()
                .subscribe(
                        { currentUser ->
                            if (currentUser != null) {
                                view?.setUserLogin(currentUser.login)
                            } else {
                                view?.setUserLogin("")
                            }
                        },
                        { error ->
                        }
                )
    }

    override fun onLoginButtonClick(login: String, password: String) {
        if (validateFields(login, password)) {
            if (activeSession.isOfflineSession) {
                offlineLogin(login, password)
            } else {
                login(login, password)
            }
        }
    }

    override fun onForgotPasswordButtonClick() {
        view?.navigateToForgotPasswordScreen()
    }

    override fun onViewDetached() {
        super.onViewDetached()
        realmRepository.close()
    }

    override fun onFastAuthClick() {
        view?.navigateToFastAuthScreen()
    }

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

    private fun login(login: String, password: String) {
        view?.setProgressVisibility(true)
        Log.i("APPTOKEN", activeSession.appToken)
        var userInfo: RealmUser? = null
        subscriptions += authRepository
                .authenticate(login, password)
                .concatMap { authData ->

                    activeSession.accessToken = authData.access_token
                    notificationServiceManager.fcmToken?.let {
                        return@concatMap settingsRepository.registerFbToken(notificationServiceManager.deviceId, it)
                    }

                    Observable.just(null)
                }
                .concatMap {
                    accountRepository.fetchUserInfo()
                }
                .concatMap { user ->
                    realmRepository.fetchUser(user)
                }
                .concatMap { realmUser ->
                    userInfo = realmUser
                    activeSession.userAccessToken = realmUser.token
                    realmRepository.setCurrentUser(realmUser)
                }
                .concatMap {
                    settingsRepository.getSettings()
                }
                .concatMap { settings ->
                    realmRepository.updateSettings(settings)
                }
                .concatMap {
                    cardsRepository.fetchCards()
                }
                .concatMap { cards ->
                    realmRepository.saveCardsList(cards ?: emptyList())
                }
                .concatMap {
                    receiptsRepository.fetchReceipts()
                }
                .concatMap { receipts ->
                    realmRepository.saveReceiptsList(receipts ?: emptyList())
                }

                .concatMap { requestRepository.fetchTypeHouse() }
                .concatMap { typeHouseList ->
                    realmRepository.saveTypeHouseList(typeHouseList = typeHouseList)
                }
                .concatMap { requestRepository.fetchRequestStates() }
                .concatMap { statesList ->
                    realmRepository.saveRequestStateList(stateList = statesList)
                }
                .concatMap { requestRepository.fetchRequestAddress() }
                .concatMap { addressList ->
                    realmRepository.saveRequestAddressList(addressList = addressList)
                }
                .concatMap {
                    realmRepository.deleteRequestList()
                }
                .concatMap {
                    requestRepository.fetchRequestList().onError {
                        Observable.just(null)
                    }
                }
                .concatMap {
                    realmRepository.saveRequestList(it ?: emptyList())
                }
                .subscribe(
                        {
                            notificationServiceManager.startPushService()
                            view?.setProgressVisibility(false)
                            view?.setResultOk()
                            view?.showPinSuggestDialog(login)
                        },
                        { error ->
                            view?.setProgressVisibility(false)
                            //error.printStackTrace()
                            resetCurrentUser()

                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun resetCurrentUser() {
        subscriptions += realmRepository.setCurrentUser(RealmUser())
                .subscribe(
                        {
                            activeSession.clear()
                            authRepository.saveTokenApi("")
                            fetchUserLogin()
                        },
                        { error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun offlineLogin(login: String, password: String) {
        view?.setProgressVisibility(true)
        subscriptions += realmRepository.fetchUser(login)
                .concatMap { user ->
                    if (user == null) {
                        view?.showInfoDialog(R.string.sign_in_offline_login_not_exist)
                    } else {
                        with(user.settings!!) {
                            if (Constants.DEBUG_OFFLINE_MODE) return@concatMap realmRepository.setCurrentUser(user)

                            if (offlineModeEnabled) {
                                if (checkPassword(password)) {
                                    return@concatMap realmRepository.setCurrentUser(user)
                                } else {
                                    view?.showMessage(R.string.sign_in_error_incorrect_login_or_password_text)
                                }
                            } else {
                                view?.showInfoDialog(R.string.sign_in_offline_disabled)
                            }
                        }
                    }

                    return@concatMap Observable.just(false)
                }
                .subscribe(
                        { result ->
                            view?.setProgressVisibility(false)
                            if (result) {
                                view?.setResultOk()
                                view?.navigateToMainScreen()
                            }
                        },
                        { error ->
                            view?.setProgressVisibility(false)
                            //error.printStackTrace()
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    override fun onPinExist() {
        view?.navigateToMainScreen()
    }

    override fun onPinReject() {
        authRepository.saveTokenApi("")
        view?.navigateToMainScreen()
    }
}
