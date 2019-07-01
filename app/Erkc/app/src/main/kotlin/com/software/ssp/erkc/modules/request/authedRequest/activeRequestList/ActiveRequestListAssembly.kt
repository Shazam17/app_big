package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@FragmentScope
@Component(modules = arrayOf(ActiveRequestListModule::class), dependencies = arrayOf(AppComponent::class))
interface ActiveRequestListComponent {
    fun inject(activeRequestListModule: ActiveRequestListFragment)
}

@Module(includes = arrayOf(ActiveRequestListModule.Declarations::class))
class ActiveRequestListModule(val activeRequestListView: IActiveRequestListView) {

    @Provides
    fun provideActiveRequestListView(): IActiveRequestListView {
        return activeRequestListView
    }

    @Module
    interface Declarations {
        @Binds
        @FragmentScope
        fun bindActiveRequestListPresenter(activeRequestListPresenter: ActiveRequestListPresenter): IActiveRequestListPresenter
    }
}