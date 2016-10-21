package com.software.ssp.erkc.modules.drawer

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(DrawerModule::class), dependencies = arrayOf(AppComponent::class))
interface DrawerComponent {
    fun inject(drawerActivity: DrawerActivity)
}

@Module(includes = arrayOf(DrawerModule.Declarations::class))
class DrawerModule(val drawerView: IDrawerView) {

    @Provides
    fun provideDrawerView(): IDrawerView {
        return drawerView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindDrawerPresenter(drawerPresenter: DrawerPresenter): IDrawerPresenter
    }
}
