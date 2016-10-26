package com.software.ssp.erkc.modules.sendvalues

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 26/10/2016.
 */

@ActivityScope
@Component(modules = arrayOf(SendValuesModule::class), dependencies = arrayOf(AppComponent::class))
interface SendValuesComponent {
    fun inject(sendValuesActivity: SendValuesActivity)
}

@Module(includes = arrayOf(SendValuesModule.Declarations::class))
class SendValuesModule(val sendValuesView: ISendValuesView) {
    @Provides
    fun provideSendValuesView(): ISendValuesView {
        return sendValuesView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindSendValuesPresenter(sendValuesPresenter: SendValuesPresenter): ISendValuesPresenter
    }
}