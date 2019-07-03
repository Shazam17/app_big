package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList.draftRequestList

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@FragmentScope
@Component(modules = arrayOf(DraftRequestListModule::class), dependencies = arrayOf(AppComponent::class))
interface DraftRequestListComponent {
    fun inject(draftRequestListModule: DraftRequestListFragment)
}

@Module(includes = arrayOf(DraftRequestListModule.Declarations::class))
class DraftRequestListModule(val draftRequestListView: IDraftRequestListView) {

    @Provides
    fun provideDraftRequestListView(): IDraftRequestListView {
        return draftRequestListView
    }

    @Module
    interface Declarations {
        @Binds
        @FragmentScope
        fun bindDraftRequestListPresenter(draftRequestListPresenter: DraftRequestListPresenter): IDraftRequestListPresenter
    }
}