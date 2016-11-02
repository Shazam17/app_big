package com.software.ssp.erkc.modules.settings

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(SettingsModule::class), dependencies = arrayOf(AppComponent::class))
interface SettingsComponent {
    fun inject(settingsFragment: SettingsFragment)
}

@Module(includes = arrayOf(SettingsModule.Declarations::class))
class SettingsModule(val settingsView: ISettingsView) {

    @Provides
    fun provideSettingsView(): ISettingsView {
        return settingsView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindSettingsPresenter(settingsPresenter: SettingsPresenter): ISettingsPresenter
    }
}