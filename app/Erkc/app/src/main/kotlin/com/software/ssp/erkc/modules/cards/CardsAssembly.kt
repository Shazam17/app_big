package com.software.ssp.erkc.modules.cards

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 28/10/2016.
 */
@ActivityScope
@Component(modules = arrayOf(CardsModule::class), dependencies = arrayOf(AppComponent::class))
interface CardsComponent {
    fun inject(cardsFragment: CardsFragment)
}

@Module(includes = arrayOf(CardsModule.Declarations::class))
class CardsModule(val cardsView: ICardsView) {

    @Provides
    fun provideCardsView(): ICardsView {
        return cardsView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindCardsPresener(cardsPresenter: CardsPresenter): ICardsPresenter
    }
}