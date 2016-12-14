package com.software.ssp.erkc.modules.history.valuehistory

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 05/12/2016.
 */
@ActivityScope
@Component(modules = arrayOf(ValueHistoryModule::class), dependencies = arrayOf(AppComponent::class))
interface ValueHistoryComponent {
    fun inject(valueHistoryActivity: ValueHistoryActivity)
}

@Module(includes = arrayOf(ValueHistoryModule.Declarations::class))
class ValueHistoryModule(val valueHistoryView: IValueHistoryView) {

    @Provides
    fun provideValueHistoryView(): IValueHistoryView {
        return valueHistoryView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindValueHistoryPresenter(vhistoryPresenter: ValueHistoryPresenter): IValueHistoryPresenter

    }

}