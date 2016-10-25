package com.software.ssp.erkc.modules.splash

import android.net.Uri
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import okhttp3.ResponseBody
import org.jsoup.Connection
import org.jsoup.Jsoup
import retrofit2.Response
import rx.Subscriber
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SplashPresenter @Inject constructor(view: ISplashView) : RxPresenter<ISplashView>(view), ISplashPresenter {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached() {
        super.onViewAttached()
        authenticateApp()
    }


    private fun authenticateApp() {
        subscriptions += authRepository
                .authenticateApp()
                .concatMap { response ->
                    val authPage = response.string()
                    return@concatMap authRepository.fetchAppToken(fetchParamsFromHtmlPage(authPage))
                }
                .subscribe(object : Subscriber<Response<ResponseBody>>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        view?.showTryAgainSnack(e.message.toString())
                    }

                    override fun onNext(response: Response<ResponseBody>) {
                        val uri = Uri.parse(response.raw().request().url().toString())

                        if (uri != null && uri.toString().startsWith(Constants.API_OAUTH_REDIRECT_URI)) {
                            val token = uri.getQueryParameter("access_token")
                            activeSession.accessToken = token
                            view?.navigateToSignIn()
                        } else {
                            view?.showTryAgainSnack(R.string.splash_error_fetching_token_text)
                        }
                    }
                })
    }

    override fun onTryAgainClicked() {
        authenticateApp()
    }

    private fun fetchParamsFromHtmlPage(page: String) : Map<String, String> {
        val doc = Jsoup.parse(page)
        val formData = doc.select("form").forms().first().formData()

        val params = HashMap<String, String>()

        for(item: Connection.KeyVal in formData) {
            params.put(item.key(), item.value())
        }

        return params
    }

}