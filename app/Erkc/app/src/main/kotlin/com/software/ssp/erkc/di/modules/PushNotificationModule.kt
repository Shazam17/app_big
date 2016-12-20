package com.software.ssp.erkc.di.modules

import android.app.Application
import com.software.ssp.erkc.modules.pushnotifications.NotificationServiceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PushNotificationModule() {

    @Provides
    @Singleton
    fun provideNotificationServiceManager(application: Application): NotificationServiceManager {
        return NotificationServiceManager(application)
    }

}
