package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.SettingsDataSource
import com.software.ssp.erkc.data.rest.models.Setting
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
                .compose(this.applySchedulers<List<Setting>>())
                .flatMap {
                    settings ->
                    Observable.just(
                            Settings(
                                    operationsNotificationStatus = settings.find { it.param == "statusoperations" }?.value ?: 0,
                                    newsNotificationStatus = settings.find { it.param == "getnews" }?.value ?: 0,
                                    paymentRemindStatus = settings.find { it.param == "needtopay" }?.value ?: 0,
                                    ipuRemindStatus = settings.find { it.param == "needtosendmeters" }?.value ?: 0
                            )
                    )
                }
    }

    fun registerFbToken(deviceId: String, fbToken: String): Observable<ResponseBody> {
        return settingsDataSource
                .registerFbToken(
                        mapOf(
                                "deviceid" to deviceId,
                                "fbtoken" to fbToken
                        )
                )
                .compose(this.applySchedulers<ResponseBody>())
    }

    fun unregisterFbToken(deviceId: String): Observable<ResponseBody> {
        return registerFbToken(deviceId, "0")
    }
}
