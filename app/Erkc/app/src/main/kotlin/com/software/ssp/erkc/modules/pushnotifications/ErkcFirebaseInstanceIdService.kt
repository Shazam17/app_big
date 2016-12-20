package com.software.ssp.erkc.modules.pushnotifications

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.data.rest.repositories.SettingsRepository
import com.software.ssp.erkc.extensions.deviceId
import rx.Subscription


class ErkcFirebaseInstanceIdService : FirebaseInstanceIdService() {

    lateinit var settingsRepository: SettingsRepository
    var registerTokenSubscription: Subscription? = null

    override fun onCreate() {
        super.onCreate()
        settingsRepository = (applicationContext as ErkcApplication).appComponent.provideSettingsRepository()
    }

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        registerForPushNotifications()
    }

    override fun onDestroy() {
        registerTokenSubscription?.unsubscribe()
        super.onDestroy()
    }

    private fun registerForPushNotifications() {
        val token = FirebaseInstanceId.getInstance().token ?: return

        registerTokenSubscription = settingsRepository.registerFbToken(deviceId, token)
                .subscribe(
                        {
                            //TODO Remove in release
                            Log.d("----", "--------------------------------------------------------")
                            Log.d("----", "FirebaseInstanceId.getInstance().token = ${token}")
                            Log.d("----", "--------------------------------------------------------")
                        },
                        Throwable::printStackTrace
                )
    }
}
