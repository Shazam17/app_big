package com.software.ssp.erkc.modules.request.authedRequest.archiveRequestList

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@FragmentScope
@Component(modules = arrayOf(ArchiveRequestListModule::class), dependencies = arrayOf(AppComponent::class))
interface ArchiveRequestListComponent {
    fun inject(archiveRequestListFragment: ArchiveRequestListFragment)
}

@Module(includes = arrayOf(ArchiveRequestListModule.Declarations::class))
class ArchiveRequestListModule(val archiveRequestListView: IArchiveRequestListView) {

    @Provides
    fun provideArchiveRequestListView(): IArchiveRequestListView {
        return archiveRequestListView
    }

    @Module
    interface Declarations {
        @Binds
        @FragmentScope
        fun bindArchiveRequestListPresenter(archiveRequestListPresenter: ArchiveRequestListPresenter): IArchiveRequestListPresenter
    }
}