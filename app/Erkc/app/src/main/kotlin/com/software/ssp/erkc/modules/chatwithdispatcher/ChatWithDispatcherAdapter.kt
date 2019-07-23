package com.software.ssp.erkc.modules.chatwithdispatcher

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.models.Comment
import kotlinx.android.synthetic.main.item_message_chat.view.*
import java.text.SimpleDateFormat
import java.util.*


class ChatWithDispatcherAdapter(val dataList: List<Comment>) : RecyclerView.Adapter<ChatWithDispatcherAdapter.CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_chat, parent, false)
        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bindComment(dataList[position])
    }

    class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        internal enum class TypeWritter {
            USER,
            DISPATCHER
        }

        fun bindComment(data: Comment) {
            view.apply {
                val USER_ID = 9
                when (data.initiator?.id) {
                    USER_ID -> {
                        configureMessageBy(writter = TypeWritter.USER, view = view, comment = data)
                    }

                    else -> {
                        configureMessageBy(writter = TypeWritter.DISPATCHER, view = view, comment = data)
                    }
                }
//                if (data.initiator?.id == 8) {
//                    messageByDispatcher.visibility = View.VISIBLE
//                    descriptionMessageByDispatcherTextView.text = data.message
//                    val formatter = SimpleDateFormat("hh:mm")
//                    val date = formatter.format((Date(data.created_at!!)))
//                    timeMessageByDispatcherTextView.text = date
//
//                } else {
//                    if (data.downloadLink != null) {
//                        messageByUser.visibility = if (data.message != null) {
//                            View.VISIBLE
//                        } else {
//                            View.GONE
//                        }
//                        descriptionMessageByUserTextView.text = data.message
//                        val formatter = SimpleDateFormat("hh:mm")
//                        val date = formatter.format((Date(data.created_at!!)))
//                        timePhotoByUserTextView.text = date
//                        photoByUser.visibility = View.VISIBLE
//                        val glideUrl = GlideUrl("http://fon.zayavki.pro" + data.downloadLink, LazyHeaders.Builder()
//                                .addHeader("Authorization", "Basic Z2poV3BUT2lJRlBfTnY4THg4SWNqZ0ItOWxOZ2lwcFE6")
//                                .build())
//                        Glide.with(view).load(glideUrl).into(imageAttachmentByUserImageView)
//                    } else {
//                        messageByUser.visibility = if (data.message != "") {
//                            View.VISIBLE
//                        } else {
//                            View.GONE
//                        }
//                        descriptionMessageByUserTextView.text = data.message
//                        val formatter = SimpleDateFormat("hh:mm")
//                        val date = formatter.format((Date(data.created_at!!)))
//                        timeMessageByUserTextView.text = date
//                    }
//                }
            }
        }

//        private fun configureUserMessage(view: View, comment: Comment) {
//            view.apply {
//                if (!comment.message.isNullOrEmpty()) {
//                    messageByDispatcher.visibility = View.VISIBLE
//                    descriptionMessageByUserTextView.text = comment.message
//                    timeMessageByUserTextView.text = dateSendMessage
//                }
//                if (!comment.downloadLink.isNullOrEmpty()) {
//                    photoByUser.visibility = View.VISIBLE
//                    timePhotoByUserTextView.text = dateSendMessage
//                    val glideUrl = GlideUrl("http://fon.zayavki.pro" + comment.downloadLink, LazyHeaders.Builder()
//                                .addHeader("Authorization", "Basic Z2poV3BUT2lJRlBfTnY4THg4SWNqZ0ItOWxOZ2lwcFE6")
//                                .build())
//                    Glide.with(view).load(glideUrl).into(imageAttachmentByUserImageView)
//                }
//            }
//        }

        private fun configureMessageBy(writter: TypeWritter, view: View, comment: Comment) {
            val formatter = SimpleDateFormat("hh:mm")
            val dateSendMessage = formatter.format((Date(comment.created_at!!)))

            view.apply {
                if (!comment.message.isNullOrEmpty()) {
                    val messageLayout = getMessageLayout(writter, view)
                    messageLayout.visibility = View.VISIBLE
                    val descriptionTextView = getDescriptionTextView(writter, view)
                    descriptionTextView.text = comment.message
                    val timeSendMessageTextView = getDateSendMessageTextView(writter, view)
                    timeSendMessageTextView.text = dateSendMessage
                }
                if (!comment.downloadLink.isNullOrEmpty()) {
                    val attachmentLayout = getAttachmentLayout(writter, view)
                    attachmentLayout.visibility = View.VISIBLE
                    val timeSendAttachmentTextView = getDateSendAttachmentTextView(writter, view)
                    timeSendAttachmentTextView.text = dateSendMessage
                    val glideUrl = GlideUrl("http://fon.zayavki.pro" + comment.downloadLink, LazyHeaders.Builder()
                            .addHeader("Authorization", "Basic Z2poV3BUT2lJRlBfTnY4THg4SWNqZ0ItOWxOZ2lwcFE6")
                            .build())
                    Glide.with(view).load(glideUrl).into(getAttachmentView(writter, view))
                }
            }

        }

        private fun getAttachmentView(writter: TypeWritter, view: View) : ImageView {
            return when (writter) {
                TypeWritter.USER ->  view.findViewById(R.id.imageAttachmentByUserImageView)
                TypeWritter.DISPATCHER -> view.findViewById(R.id.imageAttachmentByDispatcherImageView)
            }
        }

        private fun getDescriptionTextView(writter: TypeWritter, view: View) : TextView {
            return when (writter) {
                TypeWritter.USER -> view.findViewById(R.id.descriptionMessageByUserTextView)
                TypeWritter.DISPATCHER -> view.findViewById(R.id.descriptionMessageByDispatcherTextView)
            }
        }

        private fun getDateSendMessageTextView(writter: TypeWritter, view: View) : TextView {
            return when (writter) {
                TypeWritter.USER -> view.findViewById(R.id.timeMessageByUserTextView)
                TypeWritter.DISPATCHER -> view.findViewById(R.id.timeMessageByDispatcherTextView)
            }
        }

        private fun getDateSendAttachmentTextView(writter: TypeWritter, view: View) : TextView {
            return when (writter) {
                TypeWritter.USER -> view.findViewById(R.id.timePhotoByUserTextView)
                TypeWritter.DISPATCHER -> view.findViewById(R.id.timeAttachmentByDispatcherTextView)
            }
        }

        private fun getMessageLayout(writter: TypeWritter, view: View) : View {
            return when (writter) {
                TypeWritter.USER -> view.findViewById(R.id.messageByUser)
                TypeWritter.DISPATCHER -> view.findViewById(R.id.messageByDispatcher)
            }
        }

        private fun getAttachmentLayout(writter: TypeWritter, view: View) : View {
            return when (writter) {
                TypeWritter.USER -> view.findViewById(R.id.photoByUser)
                TypeWritter.DISPATCHER -> view.findViewById(R.id.attachmentMessageByDispatcherLinearLayout)
            }
        }
    }

}