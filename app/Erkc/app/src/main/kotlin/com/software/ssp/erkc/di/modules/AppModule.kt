package com.software.ssp.erkc.di.modules

import android.app.Application
import com.google.gson.Gson
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
        return Gson()
    }

    @Provides
    @Singleton
    fun provideActiveSession(): ActiveSession {
        val activeSession = ActiveSession()
        return activeSession
    }

    @Provides
    @Singleton
    fun provideRealmInstance(): Realm {
        val realmConfiguration = RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        return Realm.getInstance(realmConfiguration)
    }
}