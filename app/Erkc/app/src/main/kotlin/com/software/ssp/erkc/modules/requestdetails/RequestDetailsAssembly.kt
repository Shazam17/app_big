package com.software.ssp.erkc.modules.requestdetails

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(RequestDetailsModule::class), dependencies = arrayOf(AppComponent::class))
interface RequestDetailsComponent {
    fun inject(requestDetailsFragment: RequestDetailsActivity)
}

@Module(includes = arrayOf(RequestDetailsModule.Declarations::class))
class RequestDetailsModule(val requestDetailsView: IRequestDetailsView) {

    @Provides
    fun provideRequestDetailsView(): IRequestDetailsView {
        return requestDetailsView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindRequestDetailsPresenter(requestDetailsPresenter: RequestDetailsPresenter): IRequestDetailsPresenter
    }
}