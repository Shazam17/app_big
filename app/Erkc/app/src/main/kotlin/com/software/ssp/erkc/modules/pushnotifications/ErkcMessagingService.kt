package com.software.ssp.erkc.modules.pushnotifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import com.software.ssp.erkc.data.rest.repositories.MessagesRepository
import com.software.ssp.erkc.data.rest.repositories.PaymentRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.getCompatColor
import rx.Observable

class ErkcMessagingService : FirebaseMessagingService() {

    private val defaultVibrationDuration = 500L

    lateinit var activeSession: ActiveSession
    lateinit var realmRepository: RealmRepository
    lateinit var cardsRepository: CardsRepository
    lateinit var messagesRepository: MessagesRepository
    lateinit var paymentRepository: PaymentRepository

    override fun onCreate() {
        super.onCreate()

        val appComponent = (application as ErkcApplication).appComponent
        activeSession = appComponent.provideActiveSession()
        realmRepository = appComponent.provideRealmRepository()
        cardsRepository = appComponent.provideCardsRepository()
        messagesRepository = appComponent.provideMessagesRepository()
        paymentRepository = appComponent.providePaymentRepository()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val dataMessage = remoteMessage.data

        val pushNotification = PushNotificationModel(
                dataMessage["title"]!!,
                dataMessage["body"]!!,
                dataMessage["id"]!!,
                PushNotificationType.valueOf(dataMessage["type"]!!.toUpperCase()),
                dataMessage["ext_id"]
        )

        if (activeSession.accessToken != null) {
            syncNotificationData(pushNotification)
        } else {
            showNotification(pushNotification)
        }
    }

    private fun syncNotificationData(notification: PushNotificationModel) {
        messagesRepository.fetchMessageById(notification.id)
                .concatMap {
                    notification ->
                    realmRepository.saveNotification(notification)
                }
                .concatMap {
                    when (notification.type) {
                        PushNotificationType.PAYMENT -> syncPaymentInfo(notification.typed_id!!)
                        PushNotificationType.CARD -> syncCard(notification.typed_id!!)
                        else -> Observable.just(true)
                    }
                }
                .subscribe(
                        {
                            realmRepository.close()
                            showNotification(notification)
                        },
                        {
                            error ->
                            realmRepository.close()
                            error.printStackTrace()
                            showNotification(notification)
                        }
                )
    }

    private fun syncPaymentInfo(paymentId: String): Observable<Boolean> {
        return paymentRepository
                .fetchPaymentInfo(paymentId)
                .flatMap {
                    paymentInfo ->
                    realmRepository.savePaymentInfo(paymentInfo)
                }
    }

    private fun syncCard(cardId: String): Observable<Boolean> {
        return cardsRepository
                .fetchCard(cardId)
                .flatMap {
                    card ->
                    realmRepository.saveCard(card)
                }
    }

    private fun showNotification(notification: PushNotificationModel) {
        val intent = Intent(Constants.NOTIFICATION_ACTION_CLICK)
        intent.putExtra(Constants.KEY_NOTIFICATION_TYPE, notification.type)

        val id = when (notification.type) {
            PushNotificationType.NOTIFICATION -> notification.id
            PushNotificationType.PAYMENT -> notification.typed_id
            PushNotificationType.CARD -> notification.typed_id
        }

        intent.putExtra(Constants.KEY_NOTIFICATION_ID, id)
        val pendingIntent = PendingIntent.getBroadcast(this, System.currentTimeMillis().toInt(), intent, 0)
        val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.push_icon)
        val builder = NotificationCompat.Builder(this)
                .setLargeIcon(iconBitmap)
                .setSmallIcon(R.drawable.push_icon)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setTicker(notification.body)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(longArrayOf(defaultVibrationDuration, defaultVibrationDuration, defaultVibrationDuration))
                .setLights(Color.CYAN, 500, 1000)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(getCompatColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, builder.build())
    }
}
