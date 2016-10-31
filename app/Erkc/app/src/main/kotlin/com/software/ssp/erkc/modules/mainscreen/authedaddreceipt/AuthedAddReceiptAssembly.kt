package com.software.ssp.erkc.modules.mainscreen.authedaddreceipt

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@ActivityScope
@Component(modules = arrayOf(AuthedAddReceiptScreenModule::class), dependencies = arrayOf(AppComponent::class))
interface AuthedAddReceiptScreenComponent {
    fun inject(nonAuthedMainScreenFragment: AuthedAddReceiptFragment)
}

@Module(includes = arrayOf(AuthedAddReceiptScreenModule.Declarations::class))
class AuthedAddReceiptScreenModule(val nonAuthedMainScreenView: IAuthedAddReceiptView) {

    @Provides
    fun provideAuthedAddReceiptScreenView(): IAuthedAddReceiptView {
        return nonAuthedMainScreenView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindAuthedAddReceiptScreenScreenPresenter(nonAuthedMainScreenPresenter: AuthedAddReceiptPresenter): IAuthedAddReceiptPresenter
    }
}