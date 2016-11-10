package com.software.ssp.erkc.modules.card.cards

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import com.software.ssp.erkc.di.scopes.FragmentScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 28/10/2016.
 */
@FragmentScope
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
        @FragmentScope
        fun bindCardsPresener(cardsPresenter: CardsPresenter): ICardsPresenter
    }
}