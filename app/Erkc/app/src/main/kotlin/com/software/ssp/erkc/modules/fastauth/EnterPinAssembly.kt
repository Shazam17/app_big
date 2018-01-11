package com.software.ssp.erkc.modules.fastauth

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(EnterPinModule::class), dependencies = arrayOf(AppComponent::class))
interface EnterPinComponent {
    fun inject(enterPinActivity: EnterPinActivity)
}

@Module(includes = arrayOf(EnterPinModule.Declarations::class))
class EnterPinModule(val enterPinView: IEnterPinView) {

    @Provides
    fun provideEnterPinView(): IEnterPinView {
        return enterPinView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindEnterPinPresenter(enterPinPresenter: EnterPinPresenter): IEnterPinPresenter
    }
}