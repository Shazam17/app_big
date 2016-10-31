package com.software.ssp.erkc.di.modules

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.software.ssp.erkc.data.rest.ActiveSession
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val application: Application) {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideActiveSession(): ActiveSession {
        val activeSession = ActiveSession()
        return activeSession
    }
}