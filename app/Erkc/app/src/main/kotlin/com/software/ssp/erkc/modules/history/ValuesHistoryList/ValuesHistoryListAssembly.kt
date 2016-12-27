package com.software.ssp.erkc.modules.history.valueshistorylist

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(ValuesHistoryListModule::class), dependencies = arrayOf(AppComponent::class))
interface ValuesHistoryListComponent {
    fun inject(valuesHistoryListFragment: ValuesHistoryListFragment)
}

@Module(includes = arrayOf(ValuesHistoryListModule.Declarations::class))
class ValuesHistoryListModule(val valuesHistoryListView: IValuesHistoryListView) {

    @Provides
    fun provideValuesHistoryListView(): IValuesHistoryListView {
        return valuesHistoryListView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindValuesHistoryListScreenPresenter(valuesHistoryListPresenter: ValuesHistoryListPresenter): IValuesHistoryListPresenter
    }
}
