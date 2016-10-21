package com.software.ssp.erkc.modules.contacts

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(ContactsModule::class), dependencies = arrayOf(AppComponent::class))
interface ContactsComponent {
    fun inject(contactsFragment: ContactsFragment)
}

@Module(includes = arrayOf(ContactsModule.Declarations::class))
class ContactsModule(val contactsView: IContactsView) {

    @Provides
    fun provideContactsView(): IContactsView {
        return contactsView
    }

    @Module
    interface Declarations {

        @Binds
        @ActivityScope
        fun bindContactsPresenter(contactsPresenter: ContactsPresenter): IContactsPresenter
    }
}

