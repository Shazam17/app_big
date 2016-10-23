package com.software.ssp.erkc.modules.signup

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
@Component(modules = arrayOf(SignUpModule::class), dependencies = arrayOf(AppComponent::class))
interface SignUpComponent {
    fun inject(signUpActivity: SignUpActivity)
}

@Module(includes = arrayOf(SignUpModule.Declarations::class))
class SignUpModule(val signUpView: ISignUpView) {

    @Provides
    fun provideSignInView(): ISignUpView {
        return signUpView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindSignInPresenter(signUpPresenter: SignUpPresenter): ISignUpPresenter
    }
}
