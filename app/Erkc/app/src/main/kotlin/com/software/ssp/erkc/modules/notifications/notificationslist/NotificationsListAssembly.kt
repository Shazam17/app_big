package com.software.ssp.erkc.modules.notifications.notificationslist

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@FragmentScope
@Component(modules = arrayOf(NotificationsListModule::class), dependencies = arrayOf(AppComponent::class))
interface NotificationsListComponent {
    fun inject(notificationsListFragment: NotificationsListFragment)
}

@Module(includes = arrayOf(NotificationsListModule.Declarations::class))
class NotificationsListModule(val notificationsListView: INotificationsListView) {

    @Provides
    fun provideNotificationsListView(): INotificationsListView {
        return notificationsListView
    }

    @Module
    interface Declarations {

        @Binds
        @FragmentScope
        fun bindNotificationsListScreenPresenter(notificationsListPresenter: NotificationsListPresenter): INotificationsListPresenter
    }
}
