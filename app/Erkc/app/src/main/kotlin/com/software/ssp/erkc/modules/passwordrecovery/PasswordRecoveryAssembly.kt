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
    fun inject(signInActivity: PasswordRecoveryActivity)
}

@Module(includes = arrayOf(PasswordRecoveryModule.Declarations::class))
class PasswordRecoveryModule(val signInView: IPasswordRecoveryView) {

    @Provides
    fun provideSignInView(): IPasswordRecoveryView {
        return signInView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindSignInPresenter(signInPresenter: SignInPresenter): IPasswordRecoveryPresenter
    }
}