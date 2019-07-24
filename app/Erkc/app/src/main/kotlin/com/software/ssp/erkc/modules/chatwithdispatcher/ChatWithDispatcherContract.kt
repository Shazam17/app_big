package com.software.ssp.erkc.modules.chatwithdispatcher

import android.net.Uri
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Comment

interface IChatWithDispatcherView: IView {
    fun createChatAdapter(comments: List<Comment>)
    fun setVisibleProgressBar(isVisible: Boolean)
    fun hideAttachmentContainer()
    fun setVisibileInputContainer(isVisible: Boolean)
    fun showAttachmentContainer()
    fun showGallery()
    fun showCamera()
    fun showCameraOrGalleryDialog()
}

interface IChatWithDispatcherPresenter: IPresenter<IChatWithDispatcherView> {
    var requestId:Int
    var imageUri: Uri?
    fun onAddAttachmentButtonClick()
    fun onCameraButtonClick()
    fun onGalleryButtonClick()
    fun onDeleteAttachmentButtonClick()
}