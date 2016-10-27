package com.software.ssp.erkc.modules.valuetransfer.newvaluetransfer

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(NewValueTransferModule::class), dependencies = arrayOf(AppComponent::class))
interface NewValueTransferComponent {
    fun inject(newValueTransferFragment: NewValueTransferFragment)
}

@Module(includes = arrayOf(NewValueTransferModule.Declarations::class))
class NewValueTransferModule(val newValueTransferView: INewValueTransferView) {

    @Provides
    fun provideNewValueTransferView(): INewValueTransferView {
        return newValueTransferView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindNewValueTransferPresenter(newValueTransferPresenter: NewValueTransferPresenter): INewValueTransferPresenter
    }
}

