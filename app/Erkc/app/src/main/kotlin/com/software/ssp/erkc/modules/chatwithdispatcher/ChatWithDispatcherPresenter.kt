package com.software.ssp.erkc.modules.chatwithdispatcher

import android.net.Uri
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.repositories.RequestRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class ChatWithDispatcherPresenter @Inject constructor(view: IChatWithDispatcherView) : RxPresenter<IChatWithDispatcherView>(view), IChatWithDispatcherPresenter {

    @Inject
    lateinit var requestRepository: RequestRepository

    override var requestId: Int = -1
    override var imageUri: Uri? = null

    override fun onViewAttached() {
        super.onViewAttached()
        fetchComments()
    }

    private fun fetchComments() {
        view?.setVisibleProgressBar(isVisible = true)
        subscriptions += requestRepository.fetchRequestById(requestId)
                .subscribe(
                        {
                            view?.setVisibleProgressBar(isVisible = false)
                            view?.createChatAdapter(it.comments!!)
                            // TODO скрыть клаву
                            view?.setVisibileInputContainer(isVisible = true)
                        },
                        { error ->
                            view?.setVisibleProgressBar(isVisible = false)
                            error.printStackTrace()
                        }
                )
    }

    override fun onAddAttachmentButtonClick() {

    }

    override fun onCameraButtonClick() {
        view?.showCamera()
    }

    override fun onGalleryButtonClick() {
        view?.showGallery()
    }

    override fun onDeleteAttachmentButtonClick() {
        imageUri = null
        view?.hideAttachmentContainer()
    }
}