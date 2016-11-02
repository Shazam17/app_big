package com.software.ssp.erkc.modules.address

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 25/10/2016.
 */

@ActivityScope
@Component(modules = arrayOf(SearchAddressModule::class), dependencies = arrayOf(AppComponent::class))
interface SearchAddressComponent {
    fun inject(searchAddressActivity: SearchAddressActivity)
}

@Module(includes = arrayOf(SearchAddressModule.Declarations::class))
class SearchAddressModule(val searchAddressView: ISearchAddressView) {

    @Provides
    fun provideSearchAddressView(): ISearchAddressView {
        return searchAddressView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindSearchAddressPresenter(searchAddressPresenter: SearchAddressPresenter): ISearchAddressPresenter
    }
}