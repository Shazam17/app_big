package com.software.ssp.erkc.data.rest.repositories

import android.net.Uri
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.data.rest.datasource.AuthDataSource
import com.software.ssp.erkc.data.rest.models.AuthData
import com.software.ssp.erkc.data.rest.models.Captcha
import com.software.ssp.erkc.data.rest.models.DataResponse
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import javax.inject.Inject


class AuthRepository @Inject constructor(private val authDataSource: AuthDataSource) : Repository() {

    fun authenticate(token: String, login: String, password: String): Observable<AuthData> {
        return authDataSource
                .authenticate(token, login, password)
                .compose(this.applySchedulers<DataResponse<AuthData>>())
                .map { it.data }
    }

    fun authenticateApp(): Observable<ResponseBody> {
        return authDataSource
                .authenticateApp(Constants.API_OAUTH_URL, Constants.API_OAUTH_RESPONSE_TYPE, Constants.API_OAUTH_CLIENT_ID)
                .compose(this.applySchedulers<ResponseBody>())
    }

    fun fetchAppToken(params: Map<String, String>): Observable<String> {
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

    fun recoverPassword(token: String, login: String, email: String, number: String = "12345"): Observable<DataResponse<AuthData>> {
        return authDataSource
                .recoverPassword(token, login, email, number)
                .compose(this.applySchedulers<DataResponse<AuthData>>())
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

    fun getCapcha(token: String): Observable<DataResponse<Captcha>> {
        return authDataSource.captcha(token).compose(this.applySchedulers<DataResponse<Captcha>>())
    }
}