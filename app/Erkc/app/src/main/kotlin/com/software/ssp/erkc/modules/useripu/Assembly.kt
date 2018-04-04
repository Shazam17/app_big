package com.software.ssp.erkc.modules.useripu

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@ActivityScope
@Component(modules = arrayOf(AModule::class), dependencies = arrayOf(AppComponent::class))
interface AComponent {
    fun inject(addUserIPUActivity: Activity)
}

@Module(includes = arrayOf(AModule.Declarations::class))
class AModule(val sendValuesView: IModuleView) {
    @Provides
    fun provideAddUserIPUView(): IModuleView {
        return sendValuesView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindSendValuesPresenter(sendValuesPresenter: Presenter): IModulePresenter
    }
}