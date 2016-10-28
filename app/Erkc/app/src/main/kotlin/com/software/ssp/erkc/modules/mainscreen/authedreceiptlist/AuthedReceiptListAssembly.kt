package com.software.ssp.erkc.modules.mainscreen.authedreceiptlist

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides


@ActivityScope
@Component(modules = arrayOf(AuthedReceiptListScreenModule::class), dependencies = arrayOf(AppComponent::class))
interface AuthedReceiptListScreenComponent {
    fun inject(nonAuthedMainScreenFragment: AuthedReceiptListFragment)
}

@Module(includes = arrayOf(AuthedReceiptListScreenModule.Declarations::class))
class AuthedReceiptListScreenModule(val nonAuthedMainScreenView: IAuthedReceiptListView) {

    @Provides
    fun provideAuthedReceiptListScreenView(): IAuthedReceiptListView {
        return nonAuthedMainScreenView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindAuthedReceiptListScreenScreenPresenter(nonAuthedMainScreenPresenter: AuthedReceiptListPresenter): IAuthedReceiptListPresenter
    }
}