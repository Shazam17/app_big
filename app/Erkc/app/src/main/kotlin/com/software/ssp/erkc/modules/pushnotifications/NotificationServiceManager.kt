package com.software.ssp.erkc.modules.pushnotifications

import android.content.Context
import com.google.firebase.iid.FirebaseInstanceId
import com.software.ssp.erkc.extensions.deviceId
import com.software.ssp.erkc.extensions.stopService
import org.jetbrains.anko.startService

class NotificationServiceManager(val applicationContext: Context) {

    val deviceId: String
        get() {
            return applicationContext.deviceId
        }

    val fcmToken: String?
        get() {
            return FirebaseInstanceId.getInstance().token
        }

    fun startPushService() {
        applicationContext.startService<ErkcFirebaseInstanceIdService>()
        applicationContext.startService<ErkcMessagingService>()
    }

    fun stopPushService() {
        applicationContext.stopService<ErkcFirebaseInstanceIdService>()
        applicationContext.stopService<ErkcMessagingService>()
    }
}
