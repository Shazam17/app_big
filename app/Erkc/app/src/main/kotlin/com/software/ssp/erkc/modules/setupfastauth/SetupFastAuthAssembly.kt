package com.software.ssp.erkc.modules.setupfastauth

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.LifetimeScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@LifetimeScope
@Component(modules = arrayOf(SetupFastAuthModule::class), dependencies = arrayOf(AppComponent::class))
interface SetupFastAuthComponent {
    fun inject(setupFastAuthActivity: SetupFastAuthActivity)
}

@Module(includes = arrayOf(SetupFastAuthModule.Declarations::class))
class SetupFastAuthModule(val setupFastAuthView: ISetupFastAuthView) {

    @Provides
    fun provideSetupFastAuthView(): ISetupFastAuthView {
        return setupFastAuthView
    }

    @Module
    interface Declarations {

        @Binds
        @LifetimeScope
        fun bindSetupFastAuthPresenter(setupFastAuthPresenter: SetupFastAuthPresenter): ISetupFastAuthPresenter
    }
}
