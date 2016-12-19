package com.software.ssp.erkc.modules.transaction.valuestransaction

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 14/12/2016.
 */
@FragmentScope
@Component(modules = arrayOf(ValuesTransactionListModule::class), dependencies = arrayOf(AppComponent::class))
interface ValuesTransactionListComponent {
    fun inject(valuesTransactionListFragment: ValuesTransactionListFragment)
}

@Module(includes = arrayOf(ValuesTransactionListModule.Declarations::class))
class ValuesTransactionListModule(val valuesTransactionListView: IValuesTransactionListView) {

    @Provides
    fun provideValuesTransactionListView(): IValuesTransactionListView {
        return valuesTransactionListView
    }

    @Module
    interface Declarations {
        @Binds
        @FragmentScope
        fun bindValuesTransactionListPresenter(valuesTransactionListPresenter: ValuesTransactionListPresenter): IValuesTransactionListPresenter
    }

}