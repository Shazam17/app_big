package com.software.ssp.erkc.modules.newreceipt

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(NewReceiptModule::class), dependencies = arrayOf(AppComponent::class))
interface NewReceiptComponent {
    fun inject(newReceiptFragment: NewReceiptFragment)
}

@Module(includes = arrayOf(NewReceiptModule.Declarations::class))
class NewReceiptModule(val newReceiptView: INewReceiptView) {

    @Provides
    fun provideNewReceiptView(): INewReceiptView {
        return newReceiptView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindNewReceiptPresenter(newReceiptPresenter: NewReceiptPresenter): INewReceiptPresenter
    }
}
