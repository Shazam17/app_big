package com.software.ssp.erkc.modules.mainscreen

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@ActivityScope
@Component(modules = arrayOf(MainScreenModule::class), dependencies = arrayOf(AppComponent::class))
interface MainScreenComponent {
    fun inject(nonAuthedMainScreenFragment: MainScreenFragment)
}

@Module(includes = arrayOf(MainScreenModule.Declarations::class))
class MainScreenModule(val nonAuthedMainScreenView: IMainScreenView) {

    @Provides
    fun provideMainScreenView(): IMainScreenView {
        return nonAuthedMainScreenView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindMainScreenScreenPresenter(nonAuthedMainScreenPresenter: MainScreenPresenter): IMainScreenPresenter
    }
}
