package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.FaqDataSource
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import javax.inject.Inject


class FaqRepository @Inject constructor(private val faqDataSource: FaqDataSource) : Repository() {

    fun sendMessage(name: String,
                    login: String,
                    email: String,
                    text: String,
                    subject: String): Observable<Response<ResponseBody>> {
        return faqDataSource.sendMessage(mapOf(
                "name" to name,
                "login" to login,
                "email" to email,
                "text" to text,
                "subject" to subject)
        ).compose(this.applySchedulers<Response<ResponseBody>>())
    }

}