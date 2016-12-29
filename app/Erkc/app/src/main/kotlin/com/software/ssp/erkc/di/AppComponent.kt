package com.software.ssp.erkc.di

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.datasource.*
import com.software.ssp.erkc.data.rest.repositories.*
import com.software.ssp.erkc.di.modules.AppModule
import com.software.ssp.erkc.di.modules.NetworkModule
import com.software.ssp.erkc.di.modules.PushNotificationModule
import com.software.ssp.erkc.modules.pushnotifications.NotificationServiceManager
import dagger.Component
import io.realm.Realm
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetworkModule::class, PushNotificationModule::class))
interface AppComponent {

    fun provideApplication(): Application
    fun provideGson(): Gson
    fun provideEventBus(): Relay<Any, Any>
    fun provideActiveSession(): ActiveSession
    fun provideRealm(): Realm
    fun provideContext(): Context

    fun provideHttpClient(): OkHttpClient
    fun provideAuthProvider(): AuthProvider
    fun provideAuthDataSource(): AuthDataSource
    fun provideIpuDataSource(): IpuDataSource
    fun provideDictionaryDataSource(): DictionaryDataSource
    fun provideAccountDataSource(): AccountDataSource
    fun provideReceiptsDataSource(): ReceiptsDataSource
    fun provideFaqDataSource(): FaqDataSource
    fun provideCardsDataSource(): CardsDataSource
    fun providerPaymentDataSource(): PaymentDataSource
    fun providerSettingsDataSource(): SettingsDataSource
    fun provideMessagesDataSource(): MessagesDataSource

    fun provideSettingsRepository(): SettingsRepository
    fun provideRealmRepository(): RealmRepository
    fun provideCardsRepository(): CardsRepository
    fun provideMessagesRepository(): MessagesRepository
    fun providePaymentRepository(): PaymentRepository

    fun provideNotificationServiceManager(): NotificationServiceManager
}
