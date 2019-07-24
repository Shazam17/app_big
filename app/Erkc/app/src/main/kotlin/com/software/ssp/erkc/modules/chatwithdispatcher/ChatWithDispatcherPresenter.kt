package com.software.ssp.erkc.modules.chatwithdispatcher

import android.content.Context
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.util.Log
import android.webkit.MimeTypeMap
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.RequestRepository
import com.software.ssp.erkc.utils.FileUtils
import rx.lang.kotlin.plusAssign
import java.io.File
import javax.inject.Inject
import android.provider.MediaStore



class ChatWithDispatcherPresenter @Inject constructor(view: IChatWithDispatcherView) : RxPresenter<IChatWithDispatcherView>(view), IChatWithDispatcherPresenter {

    @Inject
    lateinit var requestRepository: RequestRepository

    @Inject
    lateinit var realmRepository: RealmRepository

    override var requestId: Int = -1
    override var imageUri: Uri? = null

    override fun onViewAttached() {
        super.onViewAttached()
        fetchComments()
    }

    private fun fetchComments() {
        view?.setVisibleProgressBar(isVisible = true)
        subscriptions += realmRepository.fetchRequestById(requestId)
                .subscribe(
                        {
                            view?.setVisibleProgressBar(isVisible = false)
                            view?.createChatAdapter(it.comment!!)
                            // TODO скрыть клаву
                            view?.setVisibleInputContainer(isVisible = true)
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

    override fun onSendMessageButtonClick(textMessage: String, context: Context) {
        var imagePath: String? = null
//        if (imageUri != null) {
//            val cR = context.contentResolver
//            val mime = MimeTypeMap.getSingleton()
//            val type = mime.getExtensionFromMimeType(cR.getType(imageUri))
//            if (imageUri!!.path.contains("content://")) {
//                imagePath = imageUri!!.path.split("content://")[1] + ".$type"
//            } else {
//                imagePath = imageUri!!.path
//            }
//        }
        if (imageUri != null) {
//            when {
//                Build.VERSION.SDK_INT < 11 -> imagePath = FileUtils.shareInstance.getRealPathFromURI_BelowAPI11(context = context, contentUri = imageUri!!)
//                Build.VERSION.SDK_INT < 19 -> imagePath = FileUtils.shareInstance.getRealPathFromURI_API11to18(context = context, contentUri = imageUri!!)
//                else -> imagePath = FileUtils.shareInstance.getRealPathFromURI_API19(context = context, uri = imageUri!!)
//            }

            val cursor = context.contentResolver.query(imageUri, null, null, null, null)
            imagePath = if (cursor == null) {
                imageUri!!.path
            } else {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                cursor.getString(idx)
            }

            print(imagePath)
        }



        subscriptions += requestRepository.sendComment(requestId = requestId, message = textMessage, imagePath = imagePath)
                .subscribe(
                        { commentResponse ->
                            print(commentResponse)
                        },
                        {error ->
                            Log.e("SEND MESSAGE", error.localizedMessage)
                            error.printStackTrace()
                        }
                )
    }
}