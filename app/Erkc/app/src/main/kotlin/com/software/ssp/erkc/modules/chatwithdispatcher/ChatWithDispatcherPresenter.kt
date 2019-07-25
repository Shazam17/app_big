package com.software.ssp.erkc.modules.chatwithdispatcher

import android.content.Context
import android.net.Uri
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.RequestRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject
import android.provider.MediaStore
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmRequest
import java.text.SimpleDateFormat
import java.util.*


class ChatWithDispatcherPresenter @Inject constructor(view: IChatWithDispatcherView) : RxPresenter<IChatWithDispatcherView>(view), IChatWithDispatcherPresenter {

    @Inject
    lateinit var requestRepository: RequestRepository

    @Inject
    lateinit var realmRepository: RealmRepository

    override var requestId: Int = -1
    override var imageUri: Uri? = null
    private var realmRequest: RealmRequest? = null

    override fun onViewAttached() {
        super.onViewAttached()
        fetchComments()
    }

    private fun fetchComments() {
        view?.setVisibleProgressBar(isVisible = true)
        subscriptions += realmRepository.fetchRequestById(requestId)
                .subscribe(
                        {request ->
                            setLabels(request)
                            realmRequest = request
                            view?.setVisibleProgressBar(isVisible = false)
                            view?.createChatAdapter(request.comment!!)
                            // TODO скрыть клаву
                            view?.setVisibleInputContainer(isVisible = true)
                        },
                        { error ->
                            view?.setVisibleProgressBar(isVisible = false)
                            error.printStackTrace()
                        }
                )
    }

    private fun setLabels(request: RealmRequest) {
        val formatter = SimpleDateFormat("dd.MM.yyyy hh:mm")
        val date=formatter.format((Date(request.created_at!!)))
        val subtitle = "Обращение №${request.id} от ${date}"
        val title = request.name ?: ""
        view?.setTitleAndSubTitle(title = title, subtitle = subtitle)
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

        if (imageUri != null) {

            val cursor = context.contentResolver.query(imageUri, null, null, null, null)
            imagePath = if (cursor == null) {
                imageUri!!.path
            } else {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                cursor.getString(idx)
            }
        }
        if (imagePath != null || textMessage != "") {
            view?.setEnableInputContainer(isEnable = false)
            subscriptions += requestRepository.sendComment(requestId = requestId, message = textMessage, imagePath = imagePath)
                    .concatMap {comment ->
                        realmRepository.addCommentRequest(comment = comment, realmRequest = realmRequest!!)
                    }
                    .subscribe(
                            {
                                fetchComments()
                                view?.cleanInputContainer()
                                view?.setEnableInputContainer(isEnable = true)
                            },
                            {error ->
                                view?.showMessage(R.string.error_sending_message)
                                view?.setEnableInputContainer(isEnable = true)
                                error.printStackTrace()
                            }
                    )
        }
    }
}