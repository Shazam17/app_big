package com.software.ssp.erkc.modules.autopayments.autopaymentslist

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(AutoPaymentsListModule::class), dependencies = arrayOf(AppComponent::class))
interface AutoPaymentsListComponent {
    fun inject(autoPaymentsListFragment: AutoPaymentsListFragment)
}

@Module(includes = arrayOf(AutoPaymentsListModule.Declarations::class))
class AutoPaymentsListModule(val autoPaymentsListView: IAutoPaymentsListView) {

    @Provides
    fun provideAutoPaymentsListView(): IAutoPaymentsListView {
        return autoPaymentsListView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindAutoPaymentsListScreenPresenter(autoPaymentsListPresenter: AutoPaymentsListPresenter): IAutoPaymentsListPresenter
    }
}
