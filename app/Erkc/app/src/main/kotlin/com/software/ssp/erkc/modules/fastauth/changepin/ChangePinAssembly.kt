package com.software.ssp.erkc.modules.fastauth.changepin

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(ChangePinModule::class), dependencies = arrayOf(AppComponent::class))
interface ChangePinComponent {
    fun inject(changePinActivity: ChangePinActivity)
}

@Module(includes = arrayOf(ChangePinModule.Declarations::class))
class ChangePinModule(val changePinView: IChangePinView) {

    @Provides
    fun provideChangePinView(): IChangePinView {
        return changePinView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindChangePinPresenter(changePinPresenter: ChangePinPresenter): IChangePinPresenter
    }
}