package com.software.ssp.erkc.modules.paymentcheck

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 30/11/2016.
 */
@ActivityScope
@Component(modules = arrayOf(PaymentCheckModule::class), dependencies = arrayOf(AppComponent::class))
interface PaymentCheckComponent {
    fun inject(paymentCheckActivity: PaymentCheckActivity)
}

@Module(includes = arrayOf(PaymentCheckModule.Declarations::class))
class PaymentCheckModule(val paymentCheckView: IPaymentCheckView) {

    @Provides
    fun providePaymentCheckView(): IPaymentCheckView {
        return paymentCheckView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindPaymentCheckPresenter(paymentCheckPresenter: PaymentCheckPresenter): IPaymentCheckPresenter
    }

}