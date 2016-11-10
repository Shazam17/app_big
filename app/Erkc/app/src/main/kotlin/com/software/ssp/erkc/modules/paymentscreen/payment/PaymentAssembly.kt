package com.software.ssp.erkc.modules.paymentscreen.payment

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 10/11/2016.
 */
@ActivityScope
@Component(modules = arrayOf(PaymentModule::class), dependencies = arrayOf(AppComponent::class))
interface PaymentComponent {
    fun inject(paymentActivity: PaymentActivity)
}


@Module(includes = arrayOf(PaymentModule.Declarations::class))
class PaymentModule(val paymentView: IPaymentView) {

    @Provides
    fun providePaymentView() : IPaymentView {
        return paymentView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindPaymentPresenter(paymentPresenter: PaymentPresenter) : IPaymentPresenter
    }
}