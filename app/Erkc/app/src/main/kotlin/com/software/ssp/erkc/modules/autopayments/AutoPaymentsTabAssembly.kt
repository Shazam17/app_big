package com.software.ssp.erkc.modules.autopayments

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(AutoPaymentsTabModule::class), dependencies = arrayOf(AppComponent::class))
interface AutoPaymentsTabComponent {
    fun inject(autoPaymentsTabFragment: AutoPaymentsTabFragment)
}

@Module(includes = arrayOf(AutoPaymentsTabModule.Declarations::class))
class AutoPaymentsTabModule(val autoPaymentsTabView: IAutoPaymentsTabView) {

    @Provides
    fun provideAutoPaymentsTabView(): IAutoPaymentsTabView {
        return autoPaymentsTabView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindAutoPaymentsTabPresenter(autoPaymentsTabPresenter: AutoPaymentsTabPresenter): IAutoPaymentsTabPresenter
    }
}
