package com.software.ssp.erkc.modules.passwordrecovery

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@ActivityScope
@Component(modules = arrayOf(PasswordRecoveryModule::class), dependencies = arrayOf(AppComponent::class))
interface PasswordRecoveryComponent {
    fun inject(passwordRecoveryActivity: PasswordRecoveryActivity)
}

@Module(includes = arrayOf(PasswordRecoveryModule.Declarations::class))
class PasswordRecoveryModule(val passwordRecoveryView: IPasswordRecoveryView) {

    @Provides
    fun provideSignInView(): IPasswordRecoveryView {
        return passwordRecoveryView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindPasswordRecoveryPresenter(passwordRecoveryPresenter: PasswordRecoveryPresenter): IPasswordRecoveryPresenter
    }
}