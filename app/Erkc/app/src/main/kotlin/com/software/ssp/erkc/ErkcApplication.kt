package com.software.ssp.erkc

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.DaggerAppComponent
import com.software.ssp.erkc.di.modules.AppModule
import io.fabric.sdk.android.Fabric

class ErkcApplication : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        initFabric()
    }

    private fun initFabric() {
        Fabric.with(this, Crashlytics())
    }
}