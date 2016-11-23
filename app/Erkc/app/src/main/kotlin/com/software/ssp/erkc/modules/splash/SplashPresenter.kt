package com.software.ssp.erkc.modules.splash

import android.text.format.DateUtils
import com.software.ssp.erkc.AppPrefs
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.data.rest.repositories.DictionaryRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.Observable
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SplashPresenter @Inject constructor(view: ISplashView) : RxPresenter<ISplashView>(view), ISplashPresenter {

    @Inject lateinit var dictionaryRepository: DictionaryRepository
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

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
                    if (AppPrefs.lastCashingDate == -1L && !DateUtils.isToday(AppPrefs.lastCashingDate) && !realmRepository.streetsLoaded()) {
                        dictionaryRepository.fetchStreets(activeSession.appToken!!)
                    } else {
                        Observable.just(null)
                    }
                }
                .concatMap {
                    streets ->
                    if (streets != null) {
                        realmRepository.saveStreetList(streets)
                    } else {
                        Observable.just(false)
                    }
                }
                .subscribe({
                    isCashed ->
                    if (isCashed) {
                        AppPrefs.lastCashingDate = Date().time
                    }
                    view?.navigateToDrawer()
                }, {
                    error ->
                    view?.showTryAgainSnack(error.parsedMessage())
                    error.printStackTrace()
                })
    }

    override fun onTryAgainClicked() {
        authenticateApp()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }
}
