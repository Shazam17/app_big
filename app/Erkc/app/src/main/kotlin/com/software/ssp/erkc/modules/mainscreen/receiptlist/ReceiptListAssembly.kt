package com.software.ssp.erkc.modules.mainscreen.receiptlist

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(ReceiptListScreenModule::class), dependencies = arrayOf(AppComponent::class))
interface ReceiptListScreenComponent {
    fun inject(receiptListScreenFragment: ReceiptListFragment)
}

@Module(includes = arrayOf(ReceiptListScreenModule.Declarations::class))
class ReceiptListScreenModule(val receiptListScreenView: IReceiptListView) {

    @Provides
    fun provideReceiptListScreenView(): IReceiptListView {
        return receiptListScreenView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindReceiptListScreenScreenPresenter(receiptListScreenPresenter: ReceiptListPresenter): IReceiptListPresenter
    }
}