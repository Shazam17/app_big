package com.software.ssp.erkc.modules.settings.offlinepassword

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@ActivityScope
@Component(modules = arrayOf(OfflinePasswordModule::class), dependencies = arrayOf(AppComponent::class))
interface OfflinePasswordComponent {
    fun inject(offlinePasswordActivity: OfflinePasswordActivity)
}

@Module(includes = arrayOf(OfflinePasswordModule.Declarations::class))
class OfflinePasswordModule(val offlinePasswordView: IOfflinePasswordView) {

    @Provides
    fun provideOfflinePasswordView(): IOfflinePasswordView {
        return offlinePasswordView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindOfflinePasswordPresenter(offlinePasswordPresenter: OfflinePasswordPresenter): IOfflinePasswordPresenter
    }
}