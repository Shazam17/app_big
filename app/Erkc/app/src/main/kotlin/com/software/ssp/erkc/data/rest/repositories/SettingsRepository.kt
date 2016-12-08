package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.SettingsDataSource
import com.software.ssp.erkc.data.rest.models.Settings
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject

/**
 * @author Alexander Popov on 02/12/2016.
 */
class SettingsRepository @Inject constructor(private val settingsDataSource: SettingsDataSource) : Repository() {

    fun setStatusOperations(value: Boolean): Observable<ResponseBody> {
        return settingsDataSource.setSettings("statusoperations", if (value) 1 else 0).compose(this.applySchedulers<ResponseBody>())
    }

    fun setGetNews(value: Boolean): Observable<ResponseBody> {
        return settingsDataSource.setSettings("getnews", if (value) 1 else 0).compose(this.applySchedulers<ResponseBody>())
    }

    fun setNeedToPay(value: Boolean): Observable<ResponseBody> {
        return settingsDataSource.setSettings("needtopay", if (value) 1 else 0).compose(this.applySchedulers<ResponseBody>())
    }

    fun setNeedToSendMeters(value: Boolean): Observable<ResponseBody> {
        return settingsDataSource.setSettings("needtosendmeters", if (value) 1 else 0).compose(this.applySchedulers<ResponseBody>())
    }

    fun getSettings(): Observable<Settings> {
        return settingsDataSource
                .getSettings()
                .compose(this.applySchedulers<Settings>())
    }

}
