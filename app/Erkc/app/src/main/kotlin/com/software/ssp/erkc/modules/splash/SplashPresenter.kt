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
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SplashPresenter @Inject constructor(view: ISplashView) : RxPresenter<ISplashView>(view), ISplashPresenter {

    @Inject lateinit var dictionaryRepository: DictionaryRepository
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    private val TIMER_SECONDS = 3L

    override fun onViewAttached() {
        super.onViewAttached()
        activeSession.clear()

        //Needed 5 second splash showing (3 sec timer + requests time)
        subscriptions += Observable.timer(TIMER_SECONDS, TimeUnit.SECONDS)
                .subscribe {
                    authenticateApp()
                }
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    private fun authenticateApp() {
        subscriptions += authRepository
                .authenticateApp()
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
                    if (AppPrefs.lastCashingDate == -1L && !DateUtils.isToday(AppPrefs.lastCashingDate) || !realmRepository.streetsLoaded()) {
                        dictionaryRepository.fetchStreets()
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
                .subscribe(
                        {
                            isCashed ->
                            if (isCashed) {
                                AppPrefs.lastCashingDate = Date().time
                            }
                            view?.navigateToDrawer()
                        },
                        {
                            error ->

                            if (error is UnknownHostException) {
                                view?.showOfflineLoginDialog()
                            }

                            view?.showTryAgainSnack(error.parsedMessage())
                            error.printStackTrace()
                        }
                )
    }

    override fun onTryAgainClicked() {
        activeSession.isOfflineSession = false
        authenticateApp()
    }

    override fun onConfirmOfflineLogin() {
        activeSession.isOfflineSession = true
        view?.navigateToOfflineSignIn()
    }
}
