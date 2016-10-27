package com.software.ssp.erkc.modules.splash

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.data.rest.repositories.DictionaryRepository
import io.realm.Realm
import org.jsoup.Connection
import org.jsoup.Jsoup
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
                    if(appToken.isNullOrEmpty()){
                        error("Didn't get application token")
                    }
                    activeSession.appToken = appToken
                    dictionaryRepo.fetchAddresses(activeSession.appToken!!)
                }.subscribe({
                    dictionaryAddressesResponse ->
                    val realm = Realm.getDefaultInstance()
                    realm.executeTransaction {
                        realm.deleteAll()
                        for (address in dictionaryAddressesResponse.addresses) {
                            address.query = address.name.toLowerCase()
                            realm.copyToRealm(address)
                        }
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