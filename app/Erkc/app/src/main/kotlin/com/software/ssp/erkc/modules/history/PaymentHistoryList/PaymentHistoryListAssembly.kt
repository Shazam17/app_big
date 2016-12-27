package com.software.ssp.erkc.modules.history.paymenthistorylist

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(PaymentHistoryListModule::class), dependencies = arrayOf(AppComponent::class))
interface PaymentHistoryListComponent {
    fun inject(paymentHistoryListFragment: PaymentHistoryListFragment)
}

@Module(includes = arrayOf(PaymentHistoryListModule.Declarations::class))
class PaymentHistoryListModule(val paymentHistoryListView: IPaymentHistoryListView) {

    @Provides
    fun providePaymentHistoryListView(): IPaymentHistoryListView {
        return paymentHistoryListView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindPaymentHistoryListScreenPresenter(paymentHistoryListPresenter: PaymentHistoryListPresenter): IPaymentHistoryListPresenter
    }
}
