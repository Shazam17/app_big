package com.software.ssp.erkc.modules.userprofile

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(UserProfileModule::class), dependencies = arrayOf(AppComponent::class))
interface UserProfileComponent {
    fun inject(userProfileActivity: UserProfileActivity)
}

@Module(includes = arrayOf(UserProfileModule.Declarations::class))
class UserProfileModule(val userProfileView: IUserProfileView) {

    @Provides
    fun provideUserProfileView(): IUserProfileView {
        return userProfileView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindUserProfilePresenter(userProfilePresenter: UserProfilePresenter): IUserProfilePresenter
    }
}
