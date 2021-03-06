package com.software.ssp.erkc.di.modules

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.rxrelay.PublishRelay
import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.data.realm.models.CURRENT_VERSION
import com.software.ssp.erkc.data.realm.models.Migrations
import com.software.ssp.erkc.data.rest.ActiveSession
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton

@Module
class AppModule(val application: ErkcApplication) {

    @Provides
    fun provideApplication(): Application {
        return application
    }

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
                .schemaVersion(CURRENT_VERSION)
                .migration(Migrations())
                .deleteRealmIfMigrationNeeded()
                .build()
        return Realm.getInstance(realmConfiguration)
    }

    @Provides
    @Singleton
    fun provideBus(): Relay<Any, Any> {
        return PublishRelay.create()
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application.applicationContext
    }
}
