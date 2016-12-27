package com.software.ssp.erkc.modules.notifications.notificationscreen

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(NotificationScreenModule::class), dependencies = arrayOf(AppComponent::class))
interface NotificationScreenComponent {
    fun inject(notificationScreenActivity: NotificationScreenActivity)
}

@Module(includes = arrayOf(NotificationScreenModule.Declarations::class))
class NotificationScreenModule(val notificationScreenView: INotificationScreenView) {

    @Provides
    fun provideNotificationScreenView(): INotificationScreenView {
        return notificationScreenView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindNotificationScreenPresenter(notificationScreenPresenter: NotificationScreenPresenter): INotificationScreenPresenter
    }
}
