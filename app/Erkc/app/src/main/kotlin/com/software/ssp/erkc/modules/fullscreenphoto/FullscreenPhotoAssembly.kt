package com.software.ssp.erkc.modules.fullscreenphoto

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component (modules = arrayOf(FullscreenPhotoModule::class),dependencies = arrayOf(AppComponent::class))
interface FullscreenPhotoComponent{
    fun inject (fullscreenPhotoActivity: FullscreenPhotoActivity)
}

@Module (includes = arrayOf(FullscreenPhotoModule.Declarations::class))
class FullscreenPhotoModule(val fullscreenPhotoView:IFullscreenPhotoView){

    @Provides
    fun provideFullscreenPhotoView():IFullscreenPhotoView{
        return fullscreenPhotoView
    }

    @Module
    interface Declarations{
        @Binds
        @ActivityScope
        fun bindFullScreenPresenter(fullscreenPhotoPresenter: FullscreenPhotoPresenter):IFullscreenPhotoPresenter
    }
}