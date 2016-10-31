package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@ActivityScope
@Component(modules = arrayOf(ValueTransferListModule::class), dependencies = arrayOf(AppComponent::class))
interface ValueTransferListComponent {
    fun inject(valueTransferListFragment: ValueTransferListFragment)
}

@Module(includes = arrayOf(ValueTransferListModule.Declarations::class))
class ValueTransferListModule(val valueTransferListView: IValueTransferListView) {

    @Provides
    fun provideValueTransferListView(): IValueTransferListView {
        return valueTransferListView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindValueTransferListPresenter(valueTransferListPresenter: ValueTransferListPresenter): IValueTransferListPresenter
    }
}

