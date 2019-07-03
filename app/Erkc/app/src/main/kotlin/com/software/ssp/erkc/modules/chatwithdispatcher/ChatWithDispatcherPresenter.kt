package com.software.ssp.erkc.modules.chatwithdispatcher

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject

class ChatWithDispatcherPresenter @Inject constructor(view: IChatWithDispatcherView) : RxPresenter<IChatWithDispatcherView>(view), IChatWithDispatcherPresenter {

}