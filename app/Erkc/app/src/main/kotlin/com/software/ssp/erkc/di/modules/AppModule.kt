package com.software.ssp.erkc.di.modules

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.software.ssp.erkc.data.rest.ActiveSession
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton

@Module
class AppModule(val application: Application) {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return gson
    }

    @Provides
    @Singleton
    fun provideActiveSession(): ActiveSession {
        return ActiveSession()
    }

    @Provides
    fun provideRealm(): Realm {
        val realmConfiguration = RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        return Realm.getInstance(realmConfiguration)
    }
}