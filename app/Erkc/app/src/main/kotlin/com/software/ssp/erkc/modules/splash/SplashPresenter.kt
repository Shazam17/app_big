package com.software.ssp.erkc.modules.splash

import com.software.ssp.erkc.AppPrefs
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.common.LogoutFinished
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmUser
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
    private var can_process_next = false

    override fun onViewAttached() {
        super.onViewAttached()

        fetchCurrentUser()

        //Needed 5 second splash showing (3 sec timer + requests time)
        subscriptions += Observable.timer(TIMER_SECONDS, TimeUnit.SECONDS)
                .subscribe( {can_process_next = true} )

        authenticateApp()
//        subscriptions += Observable.timer(TIMER_SECONDS, TimeUnit.SECONDS)
//                .subscribe {
//                    authenticateApp()
//                }
    }

    fun fetchCurrentUser() {
        subscriptions += realmRepository
            .fetchCurrentUser()
            .subscribe(
                {
                    currentUser ->
                    if(currentUser!=null) {
                        view?.setUserLogin(currentUser.login)
                    } else {
                        view?.setUserLogin("")
                    }
                },
                {
                    error ->
                    //error.printStackTrace()
                }
            )
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun clearToken() {
        authRepository.saveTokenApi("")
        //activeSession.accessToken = ""
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
                    if ((AppPrefs.lastCashingDate != -1L && (Date().time - AppPrefs.lastCashingDate >= 80000000)) || !realmRepository.streetsLoaded()) {
                        Observable.zip(dictionaryRepository.fetchStreets(), dictionaryRepository.fetchIpu(), {streets,ipu->DictionaryRepository.DictionaryCacheable(streets, ipu)})
                    } else {
                        Observable.just(null)
                    }
                }
                .concatMap {
                    cache ->
                    if (cache != null) {
                        realmRepository.saveStreetList(cache.streets)
                                .concatMap {  isCached -> realmRepository.saveIpuDictionary(cache.ipu)}
                    } else {
                        Observable.just(false)
                    }
                }
                .concatMap {
                    isCashed ->
                    if (isCashed) {
                        AppPrefs.lastCashingDate = Date().time
                    }

                    Observable.just(null)
                }
                .subscribe(
                        {
                            if(activeSession.accessToken.isNullOrEmpty())
                                activeSession.accessToken = authRepository.getLocalTokenApi()

                            // Происходит в случае потери токена, поэтому не надо отображать экран ввода пинкода
                            if(activeSession.accessToken.isNullOrEmpty() && authRepository.getLocalTokenApi().isEmpty()) {
                                view?.setUserLogin("")
                                resetCurrentUser()
                            } else {
                                navigate(view!!::navigateToDrawer)
                            }
                        },
                        {
                            error ->

                            if (Constants.DEBUG_OFFLINE_MODE || error is UnknownHostException) {
                                view?.showOfflineLoginDialog()
                            }

                            view?.showTryAgainSnack(error.parsedMessage())
                            //error.printStackTrace()
                        }
                )
    }

    private fun resetCurrentUser() {
        subscriptions += realmRepository.setCurrentUser(RealmUser())
            .subscribe(
                {
                    navigate(view!!::navigateToDrawer)
                },
                {
                    error ->
                    navigate(view!!::navigateToDrawer)
                }
            )
    }

    override fun onTryAgainClicked() {
        activeSession.isOfflineSession = false
        authenticateApp()
    }

    override fun onConfirmOfflineLogin() {
        activeSession.isOfflineSession = true
        navigate(view!!::navigateToOfflineSignIn)
    }

    fun navigate(navigateFunction: ()->Unit) {
        subscriptions += Observable.interval(100, TimeUnit.MILLISECONDS)
                .filter({can_process_next})
                .first()
                .subscribe( {navigateFunction()} )
    }
}
