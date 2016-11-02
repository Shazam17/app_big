package com.software.ssp.erkc.modules.paymentscreen

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(PaymentScreenModule::class), dependencies = arrayOf(AppComponent::class))
interface PaymentScreenComponent {
    fun inject(paymentScreenFragment: PaymentScreenFragment)
}

@Module(includes = arrayOf(PaymentScreenModule.Declarations::class))
class PaymentScreenModule(val paymentScreenView: IPaymentScreenView) {

    @Provides
    fun providePaymentScreenView(): IPaymentScreenView {
        return paymentScreenView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindPaymentScreenPresenter(paymentScreenPresenter: PaymentScreenPresenter): IPaymentScreenPresenter
    }
}
