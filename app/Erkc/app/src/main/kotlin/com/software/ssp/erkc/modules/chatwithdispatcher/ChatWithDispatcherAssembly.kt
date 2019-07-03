package com.software.ssp.erkc.modules.chatwithdispatcher

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@ActivityScope
@Component(modules = arrayOf(ChatWithDispatcherModule::class), dependencies = arrayOf(AppComponent::class))
interface ChatWithDispatcherComponent {
    fun inject(chatWithDispatcherActivity: ChatWithDispatcherActivity)
}

@Module(includes = arrayOf(ChatWithDispatcherModule.Declarations::class))
class ChatWithDispatcherModule(val chatWithDispatcherView: IChatWithDispatcherView) {

    @Provides
    fun provideChatWithDispatcherView(): IChatWithDispatcherView {
        return chatWithDispatcherView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindChatWithDispatcherPresenter(chatWithDispatcherPresenter: ChatWithDispatcherPresenter): IChatWithDispatcherPresenter
    }

}