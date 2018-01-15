package com.software.ssp.erkc.modules.fastauth.deletepin

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(DeletePinModule::class), dependencies = arrayOf(AppComponent::class))
interface DeletePinComponent {
    fun inject(deletePinActivity: DeletePinActivity)
}

@Module(includes = arrayOf(DeletePinModule.Declarations::class))
class DeletePinModule(val deletePinView: IDeletePinView) {

    @Provides
    fun provideDeletePinView(): IDeletePinView {
        return deletePinView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindDeletePinPresenter(deletePinPresenter: DeletePinPresenter): IDeletePinPresenter
    }
}