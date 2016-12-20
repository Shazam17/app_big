package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Setting
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

/**
 * @author Alexander Popov on 02/12/2016.
 */
interface SettingsDataSource {

    @FormUrlEncoded
    @POST("?method=settings.set")
    fun setSettings(@Field("param") param: String, @Field("value") value: Int): Observable<ResponseBody>

    @GET("?method=settings.get")
    fun getSettings(): Observable<List<Setting>>

    @POST("?method=settings.registerfbtoken")
    fun registerFbToken(@QueryMap params: Map<String, String>): Observable<ResponseBody>
}
