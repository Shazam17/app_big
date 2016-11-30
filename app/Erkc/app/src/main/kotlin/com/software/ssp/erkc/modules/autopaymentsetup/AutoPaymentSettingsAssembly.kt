package com.software.ssp.erkc.modules.autopaymentsetup

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@ActivityScope
@Component(modules = arrayOf(AutoPaymentSettingsModule::class), dependencies = arrayOf(AppComponent::class))
interface AutoPaymentSettingsComponent {
    fun inject(autoPaymentSettingsActivity: AutoPaymentSettingsActivity)
}

@Module(includes = arrayOf(AutoPaymentSettingsModule.Declarations::class))
class AutoPaymentSettingsModule(val autoPaymentSettingsActivity: AutoPaymentSettingsActivity) {

    @Provides
    fun provideAutoPaymentSettingsView(): IAutoPaymentSettingsView {
        return autoPaymentSettingsActivity
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindAutoPaymentSettingsPresenter(presenter: AutoPaymentSettingsPresenter): IAutoPaymentSettingsPresenter
    }
}