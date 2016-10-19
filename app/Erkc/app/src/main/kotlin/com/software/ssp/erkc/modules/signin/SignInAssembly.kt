package com.software.ssp.erkc.modules.signin

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(SignInModule::class), dependencies = arrayOf(AppComponent::class))
interface SignInComponent {
    fun inject(signInActivity: SignInActivity)
}

@Module(includes = arrayOf(SignInModule.Declarations::class))
class SignInModule(val signInView: ISignInView) {

    @Provides
    fun provideSignInView(): ISignInView {
        return signInView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindSignInPresenter(signInPresenter: SignInPresenter): ISignInPresenter
    }
}

