package com.software.ssp.erkc.modules.paymentscreen.paymentlist

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(PaymentListScreenModule::class), dependencies = arrayOf(AppComponent::class))
interface PaymentListScreenComponent {
    fun inject(paymentListScreenFragment: PaymentListFragment)
}

@Module(includes = arrayOf(PaymentListScreenModule.Declarations::class))
class PaymentListScreenModule(val paymentListScreenView: IPaymentListView) {

    @Provides
    fun providePaymentListScreenView(): IPaymentListView {
        return paymentListScreenView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindPaymentListScreenScreenPresenter(paymentListScreenPresenter: PaymentListPresenter): IPaymentListPresenter
    }
}