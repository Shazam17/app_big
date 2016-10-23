package com.software.ssp.erkc.modules.splash

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 23.10.2016.
 */
@ActivityScope
        @Component(modules = arrayOf(SplashModule::class), dependencies = arrayOf(AppComponent::class))
interface SplashComponent {
    fun inject(splashActivity: SplashActivity)
}


@Module(includes = arrayOf(SplashModule.Declarations::class))
    class SplashModule(val splashView: ISplashView) {

    @Provides
    fun provideSplashView(): ISplashView {
        return splashView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun binndSplashPresenter(splashPresenter: SplashPresenter): ISplashPresenter
    }
}