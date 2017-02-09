package com.software.ssp.erkc

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.software.ssp.erkc.common.ActivityLifecycle
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.DaggerAppComponent
import com.software.ssp.erkc.di.modules.AppModule
import com.software.ssp.erkc.modules.splash.SplashActivity
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.singleTop


class ErkcApplication : MultiDexApplication() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        initFabric()
        registerActivityLifecycleCallbacks(ActivityLifecycle)
        Realm.init(this)
        // При Runtime exception система пытается востановить весь стэк активити, но
        // т.к. краш -> потерялся токен + внутри стека нет SplashActivity
        // -> стратуем его если токен пустой
        if (appComponent.provideActiveSession().appToken == null) {
            startActivity(intentFor<SplashActivity>()
                    .newTask()
                    .singleTop())
        }
    }

    private fun initFabric() {
        Fabric.with(this, Crashlytics())
    }
}