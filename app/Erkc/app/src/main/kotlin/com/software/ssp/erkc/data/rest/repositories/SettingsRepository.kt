package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.SettingsDataSource
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject

/**
 * @author Alexander Popov on 02/12/2016.
 */
class SettingsRepository @Inject constructor(private val settingsDataSource: SettingsDataSource) : Repository() {

    fun setStatusOperations(token: String, value: Boolean): Observable<ResponseBody> {
        return settingsDataSource.setSettings(token, "statusoperations", if (value) 1 else 0).compose(this.applySchedulers<ResponseBody>())
    }

    fun setGetNews(token: String, value: Boolean): Observable<ResponseBody> {
        return settingsDataSource.setSettings(token, "getnews", if (value) 1 else 0).compose(this.applySchedulers<ResponseBody>())
    }

    fun setNeedToPay(token: String, value: Boolean): Observable<ResponseBody> {
        return settingsDataSource.setSettings(token, "needtopay", if (value) 1 else 0).compose(this.applySchedulers<ResponseBody>())
    }

    fun setNeedToSendMeters(token: String, value: Boolean): Observable<ResponseBody> {
        return settingsDataSource.setSettings(token, "needtosendmeters", if (value) 1 else 0).compose(this.applySchedulers<ResponseBody>())
    }

}