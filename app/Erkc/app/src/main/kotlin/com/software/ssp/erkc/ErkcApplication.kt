package com.software.ssp.erkc

import android.content.Context
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.jenzz.appstate.AppState
import com.jenzz.appstate.adapter.rxjava.RxAppStateMonitor
import com.software.ssp.erkc.common.ActivityLifecycle
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.DaggerAppComponent
import com.software.ssp.erkc.di.modules.AppModule
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity
import com.software.ssp.erkc.modules.splash.SplashActivity
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.singleTop
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class ErkcApplication : MultiDexApplication() {

    companion object {
        @JvmField
        var login = ""

        @JvmField
        var isAppLaunching = true

        @JvmField
        var wasSplash = false
    }

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

        isAppLaunching = true
        login = ""
        wasSplash = false

        RxAppStateMonitor.monitor(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ appState ->
                when (appState) {
                    AppState.FOREGROUND -> {
                        if (appComponent.provideActiveSession().appToken != null) {
                            if (!isAppLaunching && wasSplash) {
                                val pin: String
                                if(!login.isEmpty()) {
                                    val prefs = this.getSharedPreferences(EnterPinActivity.PREFERENCES, Context.MODE_PRIVATE)
                                    pin = prefs.getString(EnterPinActivity.KEY_PIN + login, "")
                                } else {
                                    pin = ""
                                }
                                if (!pin.isEmpty()) {
                                    startActivity(intentFor<EnterPinActivity>()
                                        .newTask()
                                        .singleTop())
                                } else {
                                    /*startActivity(intentFor<DrawerActivity>()
                                        .newTask()
                                        .clearTop()
                                        .putExtra("nonAuthImitation", true))*/
                                }
                            }
                        }
                    }
                    AppState.BACKGROUND -> {
                        isAppLaunching = false
                    }
                    else -> {
                        // Nothing
                    }
                }
            },
            { error ->
                //error.printStackTrace()
            })
    }

    fun getContext(): Context {
        return this.applicationContext
    }

    private fun initFabric() {
        Fabric.with(this, Crashlytics())
    }
}