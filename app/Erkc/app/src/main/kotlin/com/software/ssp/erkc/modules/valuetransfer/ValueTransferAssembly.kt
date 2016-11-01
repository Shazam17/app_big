package com.software.ssp.erkc.modules.valuetransfer

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(ValueTransferModule::class), dependencies = arrayOf(AppComponent::class))
interface ValueTransferComponent {
    fun inject(valueTransferFragment: ValueTransferFragment)
}

@Module(includes = arrayOf(ValueTransferModule.Declarations::class))
class ValueTransferModule(val valueTransferView: IValueTransferView) {

    @Provides
    fun provideValueTransferView(): IValueTransferView {
        return valueTransferView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindValueTransferPresenter(valueTransferPresenter: ValueTransferPresenter): IValueTransferPresenter
    }
}

