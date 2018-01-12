package com.software.ssp.erkc.data.rest.repositories

import android.content.Context
import android.net.Uri
import com.securepreferences.SecurePreferences
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.common.views.pinLockView.util.Utils
import com.software.ssp.erkc.data.rest.datasource.AuthDataSource
import com.software.ssp.erkc.data.rest.models.AuthData
import com.software.ssp.erkc.data.rest.models.Captcha
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity.PREFERENCES
import okhttp3.ResponseBody
import org.jsoup.Connection
import org.jsoup.Jsoup
import retrofit2.Response
import rx.Observable
import java.util.*
import javax.inject.Inject


class AuthRepository @Inject constructor(private val authDataSource: AuthDataSource, private val context: Context) : Repository() {

    companion object {
        val LOCAL_TOKEN = "local_token"
    }

    fun authenticateApp(): Observable<ResponseBody> {
        return authDataSource
                .authenticateApp(Constants.API_OAUTH_URL, Constants.API_OAUTH_RESPONSE_TYPE, Constants.API_OAUTH_CLIENT_ID)
                .compose(this.applySchedulers<ResponseBody>())
    }

    fun getLocalTokenApi(): String {
        val prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        return prefs.getString(LOCAL_TOKEN, "")
    }

    private fun getPrivateTokenPreferences(): SecurePreferences {
        return SecurePreferences(context, "", "prefs.xml")
    }

    fun saveTokenApi(apiToken: String) {
            val prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            prefs.edit().putString(LOCAL_TOKEN, apiToken).apply()
        getPrivateTokenPreferences().edit()
                .putString("AuthToken", apiToken)
                .apply()
    }

    fun fetchAppToken(authHtmlPage: String): Observable<String> {
        val params: Map<String, String> = fetchParamsFromHtmlPage(authHtmlPage)

        return authDataSource
                .fetchAppToken(Constants.API_OAUTH_URL, params)
                .compose(this.applySchedulers<Response<ResponseBody>>())
                .map {
                    val uri = Uri.parse(it.raw().request().url().toString())
                    var token: String? = null

                    if (uri != null && (uri.toString().startsWith(Constants.API_OAUTH_REDIRECT_URI))) {
                        token = uri.getQueryParameter("access_token")
                    }

                    return@map token
                }
    }

    fun authenticate(login: String, password: String): Observable<AuthData> {
        return authDataSource
                .authenticate(
                        mapOf(
                                "login" to login,
                                "password" to password
                        )
                )
                .compose(this.applySchedulers<AuthData>())
    }

    fun recoverPassword(login: String, email: String, number: String): Observable<AuthData> {
        return authDataSource
                .recoverPassword(login, email, number)
                .compose(this.applySchedulers<AuthData>())
    }

    fun registration(name: String,
                     login: String,
                     email: String,
                     password: String,
                     repassword: String,
                     turing: String): Observable<Response<ResponseBody>> {
        return authDataSource.registration(
                mapOf(
                        "login" to login,
                        "password" to password,
                        "repassword" to repassword,
                        "name" to name,
                        "email" to email,
                        "turing" to turing
                )
        ).compose(this.applySchedulers<Response<ResponseBody>>())
    }

    fun getCaptcha(): Observable<Captcha> {
        return authDataSource.captcha().compose(this.applySchedulers<Captcha>())
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