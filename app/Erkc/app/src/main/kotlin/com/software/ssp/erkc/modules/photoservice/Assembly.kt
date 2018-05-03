package com.software.ssp.erkc.modules.photoservice

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class))
interface AComponent {
    fun inject(service: PhotoSendingService)
}
