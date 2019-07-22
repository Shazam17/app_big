package com.software.ssp.erkc.modules.chatwithdispatcher

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.repositories.RequestRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class ChatWithDispatcherPresenter @Inject constructor(view: IChatWithDispatcherView) : RxPresenter<IChatWithDispatcherView>(view), IChatWithDispatcherPresenter {

    @Inject
    lateinit var requestRepository: RequestRepository

    override var requestId: Int = -1

    override fun onViewAttached() {
        super.onViewAttached()
        fetchCommnets()
    }

    private fun fetchCommnets() {
        subscriptions += requestRepository.fetchRequestById(requestId)
                .subscribe(
                        {
                            view?.createChatAdapter(it.comments!!)
                        },
                        { error ->
                            error.printStackTrace()
                        }
                )
    }
}