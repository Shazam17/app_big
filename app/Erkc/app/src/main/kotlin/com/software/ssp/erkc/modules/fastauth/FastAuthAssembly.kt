package com.software.ssp.erkc.modules.fastauth

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.LifetimeScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@LifetimeScope
@Component(modules = arrayOf(FastAuthModule::class), dependencies = arrayOf(AppComponent::class))
interface FastAuthComponent {
    fun inject(fastAuthFragment: FastAuthFragment)
}

@Module(includes = arrayOf(FastAuthModule.Declarations::class))
class FastAuthModule(val fastAuthView: IFastAuthView) {

    @Provides
    fun provideFastAuthView(): IFastAuthView {
        return fastAuthView
    }

    @Module
    interface Declarations {

        @Binds
        @LifetimeScope
        fun bindFastAuthPresenter(fastAuthPresenter: FastAuthPresenter): IFastAuthPresenter
    }
}
