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
class SettingsModule(val signInView: ISettingsView) {

    @Provides
    fun provideSignInView(): ISettingsView {
        return signInView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindSignInPresenter(settingsPresenter: SettingsPresenter): ISettingsPresenter
    }
}