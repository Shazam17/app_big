package com.software.ssp.erkc.modules.request.nonauthedrequest

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@FragmentScope
@Component(modules = arrayOf(RequestNonAuthModule::class), dependencies = arrayOf(AppComponent::class))
interface RequestNonAuthComponent {
    fun inject(requestNonAuthModule: RequestNonAuthFragment)
}

@Module(includes = arrayOf(RequestNonAuthModule.Declarations::class))
class RequestNonAuthModule(val requestNonAuthView: IRequestNonAuthView) {

    @Provides
    fun provideRequestNonAuthView(): IRequestNonAuthView {
        return requestNonAuthView
    }

    @Module
    interface Declarations {
        @Binds
        @FragmentScope
        fun bindRequestNonAuthPresenter(requestNonAuthPresenter: RequestNonAuthPresenter): IRequestNonAuthPresenter
    }
}