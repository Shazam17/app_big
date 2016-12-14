package com.software.ssp.erkc.modules.transaction.paymenttransaction

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 13/12/2016.
 */
@FragmentScope
@Component(modules = arrayOf(PaymentTransactionListModule::class), dependencies = arrayOf(AppComponent::class))
interface PaymentTransactionListComponent {
    fun inject(paymentTransactionListFragment: PaymentTransactionListFragment)
}

@Module(includes = arrayOf(PaymentTransactionListModule.Declarations::class))
class PaymentTransactionListModule(val paymentTransactionListView: IPaymentTransactionListView) {
    @Provides
    fun providePaymentTransactionListView(): IPaymentTransactionListView {
        return paymentTransactionListView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindPaymentTransactionListPresenter(paymentTransactionListPresenter: PaymentTransactionListPresenter) : IPaymentTransactionListPresenter

    }
}
