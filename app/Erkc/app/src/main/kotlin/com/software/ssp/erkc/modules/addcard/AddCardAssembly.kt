package com.software.ssp.erkc.modules.addcard

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
@Component(modules = arrayOf(AddCardModule::class), dependencies = arrayOf(AppComponent::class))
interface AddCardComponent {
    fun inject(addCardActivity: AddCardActivity)
}

@Module(includes = arrayOf(AddCardModule.Declarations::class))
class AddCardModule(val addCardView: IAddCardView) {

    @Provides
    fun provideAddCardView(): IAddCardView {
        return addCardView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindAddCardPresenter(addCardPresenter: AddCardPresenter): IAddCardPresenter
    }

}