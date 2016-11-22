package com.software.ssp.erkc.modules.splash

import android.text.format.DateUtils
import com.software.ssp.erkc.AppPrefs
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.data.rest.repositories.DictionaryRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SplashPresenter @Inject constructor(view: ISplashView) : RxPresenter<ISplashView>(view), ISplashPresenter {

    @Inject lateinit var dictionaryRepo: DictionaryRepository
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepo: RealmRepository

    override fun onViewAttached() {
        super.onViewAttached()
        authenticateApp()
    }

    private fun authenticateApp() {
        subscriptions += authRepository.authenticateApp()
                .concatMap {
                    response ->
                    val authPage = response.string()
                    authRepository.fetchAppToken(authPage)
                }
                .concatMap {
                    appToken ->
                    if (appToken.isNullOrEmpty()) {
                        error("Didn't get application token")
                    }
                    activeSession.appToken = appToken
                    if (AppPrefs.lastCashingDate == -1L && !DateUtils.isToday(AppPrefs.lastCashingDate)) {
                        dictionaryRepo.fetchAddresses()
                    } else {
                        Observable.just(null)
                    }
                }.subscribe({
            addresses ->
            if (addresses != null) {
                realmRepo.saveAddressesList(addresses)
            }
            view?.navigateToDrawer()
        }, {
            error ->
            view?.showTryAgainSnack(error.message!!)
            error.printStackTrace()
        })
    }

    override fun onTryAgainClicked() {
        authenticateApp()
    }

    override fun onViewDetached() {
        realmRepo.close()
        super.onViewDetached()
    }
}