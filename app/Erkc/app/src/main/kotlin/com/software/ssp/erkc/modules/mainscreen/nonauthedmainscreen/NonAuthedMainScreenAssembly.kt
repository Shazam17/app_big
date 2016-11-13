package com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(NonAuthedMainScreenModule::class), dependencies = arrayOf(AppComponent::class))
interface NonAuthedMainScreenComponent {
    fun inject(nonAuthedMainScreenFragment: NonAuthedMainScreenFragment)
}

@Module(includes = arrayOf(NonAuthedMainScreenModule.Declarations::class))
class NonAuthedMainScreenModule(val nonAuthedMainScreenView: INonAuthedMainScreenView) {

    @Provides
    fun provideNonAuthedMainScreenView(): INonAuthedMainScreenView {
        return nonAuthedMainScreenView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindNonAuthedMainScreenPresenter(nonAuthedMainScreenPresenter: NonAuthedMainScreenPresenter): INonAuthedMainScreenPresenter
    }
}

