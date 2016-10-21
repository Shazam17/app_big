package com.software.ssp.erkc.modules.signin

import android.net.Uri
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.models.AuthResponse
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import okhttp3.ResponseBody
import org.jsoup.Connection
import org.jsoup.Jsoup
import retrofit2.Response
import rx.Subscriber
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject


class SignInPresenter @Inject constructor(view: ISignInView) : RxPresenter<ISignInView>(view), ISignInPresenter {

    @Inject lateinit var authProvider: AuthProvider
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached() {
        super.onViewAttached()
        authenticateApp()
    }

    override fun onLoginButtonClick(email: String, password: String) {
        if (validateFields(email, password)) {
            login(email, password)
        }
    }

    override fun onForgotPasswordButtonClick(email: String) {
        view?.navigateToForgotPasswordScreen(email)
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private fun validateFields(email: String?, password: String?): Boolean {
        var isValid = true

        if (email == null || email.isEmpty()) {
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
                .authenticate(activeSession.accessToken!!, email, password)
                .subscribe(object : Subscriber<AuthResponse>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        view?.setProgressVisibility(false)
                        e.printStackTrace()
                        view?.showMessage(e.message.toString())
                    }

                    override fun onNext(authResponse: AuthResponse) {
                        view?.setProgressVisibility(false)
                        activeSession.accessToken = authResponse.data.access_token

                        view?.navigateToDrawerScreen()
                    }
        })
    }

    private fun authenticateApp() {
        view?.setProgressVisibility(true)

        subscriptions += authRepository
                .authenticateApp()
                .concatMap { response ->
                    val authPage = response.string()
                    return@concatMap authRepository.fetchAppToken(fetchParamsFromHtmlPage(authPage))
                }
                .subscribe(object : Subscriber<Response<ResponseBody>>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        view?.setProgressVisibility(false)
                        e.printStackTrace()
                        view?.showMessage(e.message.toString())
                    }

                    override fun onNext(response: Response<ResponseBody>) {
                        view?.setProgressVisibility(false)
                        val uri = Uri.parse(response.raw().request().url().toString())

                        if (uri != null && uri.toString().startsWith(Constants.API_OAUTH_REDIRECT_URI)) {
                            val token = uri.getQueryParameter("access_token")
                            activeSession.accessToken = token
                        } else {
                            view?.showMessage("cannot fetch token :с")
                        }
                    }
                })
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