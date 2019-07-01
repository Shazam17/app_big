package com.software.ssp.erkc.modules.createrequest

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(CreateRequestModule::class), dependencies = arrayOf(AppComponent::class))
interface CreateRequestComponent {
    fun inject(createRequestActivity: CreateRequestActivity)
}

@Module(includes = arrayOf(CreateRequestModule.Declarations::class))
class CreateRequestModule(val createRequestView: ICreateRequestView) {

    @Provides
    fun provideCreateRequestView(): ICreateRequestView {
        return createRequestView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindCreateRequestPresenter(createRequestPresenter: CreateRequestPresenter): ICreateRequestPresenter
    }
}