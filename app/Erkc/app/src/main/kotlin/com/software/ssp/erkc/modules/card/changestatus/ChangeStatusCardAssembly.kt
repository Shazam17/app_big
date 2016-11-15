package com.software.ssp.erkc.modules.card.changestatus

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
@Component(modules = arrayOf(ChangeStatusCardModule::class), dependencies = arrayOf(AppComponent::class))
interface ChangeStatusCardComponent {
    fun inject(addCardActivity: ChangeStatusCardActivity)
}

@Module(includes = arrayOf(ChangeStatusCardModule.Declarations::class))
class ChangeStatusCardModule(val addCardView: IChangeStatusCardView) {

    @Provides
    fun provideAddCardView(): IChangeStatusCardView {
        return addCardView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindAddCardPresenter(addCardPresenter: ChangeStatusCardPresenter): IChangeStatusCardPresenter
    }

}