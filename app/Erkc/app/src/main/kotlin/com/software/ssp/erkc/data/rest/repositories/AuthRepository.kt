package com.software.ssp.erkc.data.rest.repositories

import android.net.Uri
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.data.rest.datasource.AuthDataSource
import com.software.ssp.erkc.data.rest.models.AuthData
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

    fun registration(token: String, name: String, login: String, email: String, password: String, repassword: String): Observable<Response<ResponseBody>> {
        return authDataSource.registration(token, name, login, email, password, repassword).compose(this.applySchedulers<Response<ResponseBody>>())
    }
}