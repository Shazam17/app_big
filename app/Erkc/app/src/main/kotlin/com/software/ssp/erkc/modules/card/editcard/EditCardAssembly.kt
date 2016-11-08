package com.software.ssp.erkc.modules.card.editcard

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 08/11/2016.
 */
@ActivityScope
@Component(modules = arrayOf(EditCardModule::class), dependencies = arrayOf(AppComponent::class))
interface EditCardComponent {
    fun inject(editCardActivity: EditCardActivity)
}

@Module(includes = arrayOf(EditCardModule.Declarations::class))
class EditCardModule(val editCardView: IEditCardView) {

    @Provides
    fun provideEditCardView(): IEditCardView {
        return editCardView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindEditCardPresenter(editCardPresenter: EditCardPresenter) : IEditCardPresenter
    }

}