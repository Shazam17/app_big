package com.software.ssp.erkc.di

import com.google.gson.Gson
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.AuthProvider
import com.software.ssp.erkc.data.rest.datasource.*
import com.software.ssp.erkc.di.modules.AppModule
import com.software.ssp.erkc.di.modules.NetworkModule
import dagger.Component
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetworkModule::class))
interface AppComponent {
    fun proivdeGson(): Gson
    fun provideHttpClient(): OkHttpClient
    fun provideAuthProvider(): AuthProvider
    fun provideAuthDataSource(): AuthDataSource
    fun provideDictionaryDataSource(): DictionaryDataSource
    fun provideAccountDataSource(): AccountDataSource
    fun provideReceiptsDataSource(): ReceiptsDataSource
    fun provideCardsDataSource(): CardsDataSource
    fun provideActiveSession(): ActiveSession
}