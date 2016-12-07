package com.software.ssp.erkc.modules.history.filter

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(HistoryFilterModule::class), dependencies = arrayOf(AppComponent::class))
interface HistoryFilterComponent {
    fun inject(historyFilterActivity: HistoryFilterActivity)
}

@Module(includes = arrayOf(HistoryFilterModule.Declarations::class))
class HistoryFilterModule(val historyFilterView: IHistoryFilterView) {

    @Provides
    fun provideHistoryFilterView(): IHistoryFilterView {
        return historyFilterView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindHistoryFilterPresenter(historyFilterPresenter: HistoryFilterPresenter): IHistoryFilterPresenter
    }
}

