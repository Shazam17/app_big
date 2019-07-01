package com.software.ssp.erkc.modules.request

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@FragmentScope
@Component(modules = arrayOf(RequestTabModule::class), dependencies = arrayOf(AppComponent::class))
interface RequestTabComponent {
    fun inject(requestTabFragment: RequestTabFragment)
}

@Module(includes = arrayOf(RequestTabModule.Declarations::class))
class RequestTabModule(val requestTabView: IRequestTabView) {

    @Provides
    fun provideRequestTabView(): IRequestTabView {
        return requestTabView
    }

    @Module
    interface Declarations {
        @Binds
        @FragmentScope
        fun bindRequestTabPresenter(requestTabPresenter: RequestTabPresenter): IRequestTabPresenter
    }
}
