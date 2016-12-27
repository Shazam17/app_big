package com.software.ssp.erkc.modules.notifications.filter

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(NotificationsFilterModule::class), dependencies = arrayOf(AppComponent::class))
interface NotificationsFilterComponent {
    fun inject(notificationsFilterActivity: NotificationsFilterActivity)
}

@Module(includes = arrayOf(NotificationsFilterModule.Declarations::class))
class NotificationsFilterModule(val notificationsFilterView: INotificationsFilterView) {

    @Provides
    fun provideNotificationsFilterView(): INotificationsFilterView {
        return notificationsFilterView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindNotificationsFilterPresenter(notificationsFilterPresenter: NotificationsFilterPresenter): INotificationsFilterPresenter
    }
}
