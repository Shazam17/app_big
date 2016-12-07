package com.software.ssp.erkc.modules.paymentsinfo

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 01/12/2016.
 */

@ActivityScope
@Component(modules = arrayOf(PaymentInfoModule::class), dependencies = arrayOf(AppComponent::class))
interface PaymentInfoComponent {
    fun inject(paymentInfoActivity: PaymentInfoActivity)
}

@Module(includes = arrayOf(PaymentInfoModule.Declarations::class))
class PaymentInfoModule(val paymentInfoView: IPaymentInfoView) {

    @Provides
    fun providePaymentInfoView(): IPaymentInfoView {
        return paymentInfoView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindPaymentInfoPresenter(paymentInfoPresenter: PaymentInfoPresenter): IPaymentInfoPresenter
    }
}