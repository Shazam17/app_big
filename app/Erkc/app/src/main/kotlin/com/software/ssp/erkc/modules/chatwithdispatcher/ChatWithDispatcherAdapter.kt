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
import com.software.ssp.erkc.data.realm.models.RealmComment
import kotlinx.android.synthetic.main.item_message_chat.view.*
import java.text.SimpleDateFormat
import java.util.*


class ChatWithDispatcherAdapter(val dataList: List<RealmComment>) : RecyclerView.Adapter<ChatWithDispatcherAdapter.CommentViewHolder>() {

    var lastDateSendMessage = Date().time

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_chat, parent, false)
        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.count()
    }


    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bindComment(dataList[position], dataList, position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private var lastDateMessage = Date().time

        internal enum class TypeWritter {
            USER,
            DISPATCHER
        }

        fun bindComment(data: RealmComment, commentList: List<RealmComment>, position: Int) {
            view.apply {
                val USER_ID = 9
                when (data.initiator?.id) {
                    USER_ID -> {
                        configureMessageBy(writter = TypeWritter.USER, view = view, comment = data, commentList = commentList, position = position)
                    }

                    else -> {
                        configureMessageBy(writter = TypeWritter.DISPATCHER, view = view, comment = data, commentList = commentList, position = position)
                    }
                }
            }
        }

        private fun configureMessageBy(writter: TypeWritter, view: View, comment: RealmComment, commentList: List<RealmComment>, position: Int) {
            val formatter = SimpleDateFormat("HH:mm")
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

                var previousCommentTime = 0L

                if (position > 0) {
                    previousCommentTime = commentList[position - 1].created_at!!
                }

                setFullTimeTextVisibility(currentCommentTime = comment.created_at!!, previousCommentTime = previousCommentTime, comment = comment, view = view)
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

        private fun setFullTimeTextVisibility(currentCommentTime: Long, previousCommentTime: Long, comment: RealmComment, view: View) {
            view.apply {

                if (previousCommentTime == 0L) {
                    val lastMessageFormatter = SimpleDateFormat("dd.MM.yyyy")
                    dateMessage.visibility = View.VISIBLE
                    dateMessage.text = lastMessageFormatter.format(Date(comment.created_at!!))
                } else {
                    val cal1 = Calendar.getInstance()
                    val cal2 = Calendar.getInstance()

                    cal1.timeInMillis = currentCommentTime
                    cal2.timeInMillis = previousCommentTime

                    val isNowDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)

                    if (!isNowDay) {
                        val lastMessageFormatter = SimpleDateFormat("dd.MM.yyyy")
                        dateMessage.visibility = View.VISIBLE
                        dateMessage.text = lastMessageFormatter.format(Date(comment.created_at!!))
                    }
                }
            }
        }
    }

}