package com.software.ssp.erkc.modules.chatwithdispatcher

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Comment

interface IChatWithDispatcherView: IView {
    fun createChatAdapter(comments: List<Comment>)
}

interface IChatWithDispatcherPresenter: IPresenter<IChatWithDispatcherView> {
    var requestId:Int
}