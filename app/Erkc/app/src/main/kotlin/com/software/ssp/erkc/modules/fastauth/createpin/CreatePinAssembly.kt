package com.software.ssp.erkc.modules.fastauth.createpin

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(CreatePinModule::class), dependencies = arrayOf(AppComponent::class))
interface CreatePinComponent {
    fun inject(createPinActivity: CreatePinActivity)
}

@Module(includes = arrayOf(CreatePinModule.Declarations::class))
class CreatePinModule(val createPinView: ICreatePinView) {

    @Provides
    fun provideCreatePinView(): ICreatePinView {
        return createPinView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindCreatePinPresenter(createPinPresenter: CreatePinPresenter): ICreatePinPresenter
    }
}