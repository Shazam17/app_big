package com.software.ssp.erkc.modules.pushnotifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.common.ActivityLifecycle
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import com.software.ssp.erkc.modules.drawer.DrawerItem
import com.software.ssp.erkc.modules.notifications.notificationscreen.NotificationScreenActivity
import com.software.ssp.erkc.modules.paymentsinfo.PaymentInfoActivity
import com.software.ssp.erkc.modules.splash.SplashActivity
import org.jetbrains.anko.newTask
import org.jetbrains.anko.singleTop
import java.util.*

class NotificationBroadcastReceiver : BroadcastReceiver() {

    lateinit var activeSession: ActiveSession

    override fun onReceive(context: Context, intent: Intent) {

        val application = context.applicationContext as ErkcApplication
        activeSession = application.appComponent.provideActiveSession()

        if (intent.action == Constants.NOTIFICATION_ACTION_CLICK) {

            val notificationType = intent.getSerializableExtra(Constants.KEY_NOTIFICATION_TYPE) as PushNotificationType
            val notificationId = intent.getStringExtra(Constants.KEY_NOTIFICATION_ID)

            val intents = ArrayList<Intent>()

            if (activeSession.accessToken != null) {

                //стартуем начинася с дровера только если мы получили нотификацию в выключенном приложении
                if (ActivityLifecycle.isBackground) {
                    val drawerIntent = Intent(context.applicationContext, DrawerActivity::class.java)
                    drawerIntent.newTask().singleTop()
                    intents.add(drawerIntent)
                }
                when (notificationType) {
                    PushNotificationType.NOTIFICATION -> {
                        val newIntent = Intent(context.applicationContext, NotificationScreenActivity::class.java)
                        newIntent.putExtra(Constants.KEY_NOTIFICATION_ID, notificationId)
                        if (intents.isEmpty()) {
                            newIntent.newTask().singleTop()
                        }
                        intents.add(newIntent)
                    }
                    PushNotificationType.PAYMENT -> {
                        val newIntent = Intent(context.applicationContext, PaymentInfoActivity::class.java)
                        newIntent.putExtra(Constants.KEY_PAYMENT, notificationId)
                        if (intents.isEmpty()) {
                            newIntent.newTask().singleTop()
                        }
                        intents.add(newIntent)
                    }
                    PushNotificationType.CARD -> {
                        //drawer navigation to cards list
                        if (ActivityLifecycle.isBackground) {
                            val drawerIntent = Intent(context.applicationContext, DrawerActivity::class.java)
                            drawerIntent.newTask().singleTop()
                            drawerIntent.putExtra(Constants.KEY_SELECTED_DRAWER_ITEM, DrawerItem.CARDS)
                            intents.add(drawerIntent)
                        }
                    }
                }

            } else {
                val newIntent = Intent(context.applicationContext, SplashActivity::class.java)
                newIntent.newTask().singleTop()
                newIntent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME)
                intents.add(newIntent)
            }

            context.startActivities(intents.toTypedArray())
        }
    }
}
