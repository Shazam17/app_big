package com.software.ssp.erkc.data.rest.repositories

import android.net.Uri
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.data.rest.datasource.AuthDataSource
import com.software.ssp.erkc.data.rest.models.AuthData
import com.software.ssp.erkc.data.rest.models.Captcha
import okhttp3.ResponseBody
import org.jsoup.Connection
import org.jsoup.Jsoup
import retrofit2.Response
import rx.Observable
import java.util.*
import javax.inject.Inject


class AuthRepository @Inject constructor(private val authDataSource: AuthDataSource) : Repository() {

    fun authenticate(token: String, login: String, password: String): Observable<AuthData> {
        return authDataSource
                .authenticate(token, login, password)
                .compose(this.applySchedulers<AuthData>())
    }

    fun authenticateApp(): Observable<ResponseBody> {
        return authDataSource
                .authenticateApp(Constants.API_OAUTH_URL, Constants.API_OAUTH_RESPONSE_TYPE, Constants.API_OAUTH_CLIENT_ID)
                .compose(this.applySchedulers<ResponseBody>())
    }

    fun fetchAppToken(authHtmlPage: String): Observable<String> {
        val params: Map<String, String> = fetchParamsFromHtmlPage(authHtmlPage)

        return authDataSource
                .fetchAppToken(Constants.API_OAUTH_URL, params)
                .compose(this.applySchedulers<Response<ResponseBody>>())
                .map {
                    val uri = Uri.parse(it.raw().request().url().toString())
                    var token: String? = null

                    if (uri != null && uri.toString().startsWith(Constants.API_OAUTH_REDIRECT_URI)) {
                        token = uri.getQueryParameter("access_token")
                    }

                    return@map token
                }
    }

    fun recoverPassword(token: String, login: String, email: String, number: String = "12345"): Observable<AuthData> {
        return authDataSource
                .recoverPassword(token, Constants.API_OAUTH_CLIENT_ID, login, email, number, getSig(mapOf(
                        "method" to "users.recover",
                        "app_id" to Constants.API_OAUTH_CLIENT_ID,
                        "token" to token,
                        "login" to login,
                        "email" to email,
                        "number" to number
                )))
                .compose(this.applySchedulers<AuthData>())
    }

    fun registration(token: String,
                     name: String,
                     login: String,
                     email: String,
                     password: String,
                     repassword: String,
                     turing: String): Observable<Response<ResponseBody>> {
        return authDataSource.registration(mapOf(
                "token" to token,
                "name" to name,
                "login" to login,
                "email" to email,
                "password" to password,
                "repassword" to repassword,
                "turing" to turing)
        ).compose(this.applySchedulers<Response<ResponseBody>>())
    }

    fun getCapcha(token: String): Observable<Captcha> {
        return authDataSource.captcha(token).compose(this.applySchedulers<Captcha>())
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