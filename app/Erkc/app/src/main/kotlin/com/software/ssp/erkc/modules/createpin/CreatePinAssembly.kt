package com.software.ssp.erkc.modules.createpin

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.LifetimeScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@LifetimeScope
@Component(modules = arrayOf(CreatePinModule::class), dependencies = arrayOf(AppComponent::class))
interface CreatePinComponent {
    fun inject(createPinFragment: CreatePinFragment)
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
        @LifetimeScope
        fun bindCreatePinPresenter(createPinPresenter: CreatePinPresenter): ICreatePinPresenter
    }
}
