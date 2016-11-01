package com.software.ssp.erkc.modules.splash

import android.text.format.DateUtils
import com.software.ssp.erkc.AppPrefs
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.data.rest.repositories.DictionaryRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import org.jsoup.Connection
import org.jsoup.Jsoup
import rx.Observable
import rx.lang.kotlin.plusAssign
import java.util.*
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
                    authRepository.fetchAppToken(fetchParamsFromHtmlPage(authPage))
                }
                .concatMap {
                    appToken ->
                    if (appToken.isNullOrEmpty()) {
                        error("Didn't get application token")
                    }
                    activeSession.appToken = appToken
                    if (AppPrefs.lastCashingDate == -1L && !DateUtils.isToday(AppPrefs.lastCashingDate)) {
                        dictionaryRepo.fetchAddresses(activeSession.appToken!!)
                    } else {
                        Observable.just(null)
                    }
                }.subscribe({
            dictionaryAddressesResponse ->
            if (dictionaryAddressesResponse != null) {
                realmRepo.saveAddressesList(dictionaryAddressesResponse.addresses)
            }
            view?.navigateToDrawer()
        }, {
            error ->
            view?.showTryAgainSnack(error.message!!)
        })
    }

    override fun onTryAgainClicked() {
        authenticateApp()
    }

    override fun onViewDetached() {
        realmRepo.close()
        super.onViewDetached()
    }

    private fun fetchParamsFromHtmlPage(page: String): Map<String, String> {
        val doc = Jsoup.parse(page)
        val formData = doc.select("form").forms().first().formData()

        val params = HashMap<String, String>()

        for (item: Connection.KeyVal in formData) {
            params.put(item.key(), item.value())
        }

        return params
    }
}