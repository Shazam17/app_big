package com.software.ssp.erkc.di

import android.content.Context
import com.google.gson.Gson
import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.datasource.*
import com.software.ssp.erkc.di.modules.AppModule
import com.software.ssp.erkc.di.modules.NetworkModule
import dagger.Component
import io.realm.Realm
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetworkModule::class))
interface AppComponent {
    fun proivdeGson(): Gson
    fun provideEventBus(): Relay<Any, Any>
    fun provideHttpClient(): OkHttpClient
    fun provideAuthProvider(): AuthProvider
    fun provideAuthDataSource(): AuthDataSource
    fun provideIpuDataSource(): IpuDataSource
    fun provideDictionaryDataSource(): DictionaryDataSource
    fun provideAccountDataSource(): AccountDataSource
    fun provideReceiptsDataSource(): ReceiptsDataSource
    fun provideFaqDataSource(): FaqDataSource
    fun provideCardsDataSource(): CardsDataSource
    fun provideActiveSession(): ActiveSession
    fun providerPaymentDataSource() : PaymentDataSource
    fun providerSettingsDataSource() : SettingsDataSource
    fun provideRealm(): Realm
    fun provideContext(): Context
}