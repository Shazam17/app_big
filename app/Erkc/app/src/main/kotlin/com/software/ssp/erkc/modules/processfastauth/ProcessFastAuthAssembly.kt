package com.software.ssp.erkc.modules.processfastauth

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.LifetimeScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@LifetimeScope
@Component(modules = arrayOf(ProcessFastAuthModule::class), dependencies = arrayOf(AppComponent::class))
interface ProcessFastAuthComponent {
    fun inject(processFastAuthActivity: ProcessFastAuthActivity)
}

@Module(includes = arrayOf(ProcessFastAuthModule.Declarations::class))
class ProcessFastAuthModule(val processFastAuthView: IProcessFastAuthView) {

    @Provides
    fun provideProcessFastAuthView(): IProcessFastAuthView {
        return processFastAuthView
    }

    @Module
    interface Declarations {

        @Binds
        @LifetimeScope
        fun bindProcessFastAuthPresenter(processFastAuthPresenter: ProcessFastAuthPresenter): IProcessFastAuthPresenter
    }
}
