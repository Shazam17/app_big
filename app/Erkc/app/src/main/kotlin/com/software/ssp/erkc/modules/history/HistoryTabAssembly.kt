package com.software.ssp.erkc.modules.history

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(HistoryTabModule::class), dependencies = arrayOf(AppComponent::class))
interface HistoryTabComponent {
    fun inject(historyFragment: HistoryTabFragment)
}

@Module(includes = arrayOf(HistoryTabModule.Declarations::class))
class HistoryTabModule(val historyView: IHistoryTabView) {

    @Provides
    fun provideHistoryTabView(): IHistoryTabView {
        return historyView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindHistoryTabPresenter(historyPresenter: HistoryTabPresenter): IHistoryTabPresenter
    }
}
