package com.software.ssp.erkc.modules.pushnotifications

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.extensions.getCompatColor

class ErkcMessagingService : FirebaseMessagingService() {

    private val defaultVibrationDuration = 500L

    lateinit var gson: Gson
    lateinit var activeSession: ActiveSession
    lateinit var eventBus: Relay<Any, Any>

    override fun onCreate() {
        super.onCreate()

        val appComponent = (application as ErkcApplication).appComponent
        activeSession = appComponent.provideActiveSession()
        gson = appComponent.proivdeGson()
        eventBus = appComponent.provideEventBus()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.push_icon)
        val intent = Intent(Constants.NOTIFICATION_ACTION_CLICK)
        val pendingIntent = PendingIntent.getBroadcast(this, System.currentTimeMillis().toInt(), intent, 0)
        val builder = NotificationCompat.Builder(this)
                .setLargeIcon(iconBitmap)
                .setSmallIcon(R.drawable.push_icon)
                .setContentTitle(remoteMessage.notification.title)
                .setContentText(remoteMessage.notification.body)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(defaultVibrationDuration, defaultVibrationDuration, defaultVibrationDuration, defaultVibrationDuration))
                .setLights(Color.CYAN, 1, 1)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(getCompatColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, builder.build())
    }
}
