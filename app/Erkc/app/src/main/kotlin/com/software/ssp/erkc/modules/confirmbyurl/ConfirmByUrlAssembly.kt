package com.software.ssp.erkc.modules.confirmbyurl

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 01/11/2016.
 */
@ActivityScope
@Component(modules = arrayOf(ConfirmByUrlModule::class), dependencies = arrayOf(AppComponent::class))
interface ConfirmByUrlComponent {
    fun inject(addCardActivity: ConfirmByUrlActivity)
}

@Module(includes = arrayOf(ConfirmByUrlModule.Declarations::class))
class ConfirmByUrlModule(val addCardView: IConfirmByUrlView) {

    @Provides
    fun provideAddCardView(): IConfirmByUrlView {
        return addCardView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindAddCardPresenter(addCardPresenter: ConfirmByUrlPresenter): IConfirmByUrlPresenter
    }

}