package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.data.rest.datasource.AuthDataSource
import com.software.ssp.erkc.data.rest.models.AuthResponse
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import javax.inject.Inject


class AuthRepository @Inject constructor(private val authDataSource: AuthDataSource) : Repository() {

    fun authenticate(token: String, login: String, password: String): Observable<AuthResponse> {
        return authDataSource
                .authenticate(token, login, password)
                .compose(this.applySchedulers<AuthResponse>())
    }

    fun authenticateApp(): Observable<ResponseBody> {
        return authDataSource
                .authenticateApp(Constants.API_OAUTH_URL, Constants.API_OAUTH_RESPONSE_TYPE, Constants.API_OAUTH_CLIENT_ID)
                .compose(this.applySchedulers<ResponseBody>())
    }

    fun fetchAppToken(params: Map<String, String>): Observable<Response<ResponseBody>> {
        return authDataSource
                .fetchAppToken(Constants.API_OAUTH_URL, params)
                .compose(this.applySchedulers<Response<ResponseBody>>())
    }

    fun recoverPassword(token: String, login: String, email: String, number: String = "12345"): Observable<AuthResponse> {
        return authDataSource
                .recoverPassword(token, login, email, number)
                .compose(this.applySchedulers<AuthResponse>())
    }

}