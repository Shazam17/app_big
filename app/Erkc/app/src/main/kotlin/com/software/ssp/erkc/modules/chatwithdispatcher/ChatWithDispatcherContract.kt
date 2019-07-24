package com.software.ssp.erkc.modules.chatwithdispatcher

import android.content.Context
import android.net.Uri
import android.text.Editable
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmComment
import com.software.ssp.erkc.data.rest.models.Comment

interface IChatWithDispatcherView: IView {
    fun createChatAdapter(comments: List<RealmComment>)
    fun setVisibleProgressBar(isVisible: Boolean)
    fun hideAttachmentContainer()
    fun setVisibleInputContainer(isVisible: Boolean)
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
    fun onSendMessageButtonClick(textMessage: String, context: Context)
}