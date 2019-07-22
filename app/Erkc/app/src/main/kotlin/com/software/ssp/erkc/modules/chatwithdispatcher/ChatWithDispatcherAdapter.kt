package com.software.ssp.erkc.modules.chatwithdispatcher

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.StatusColors
import com.software.ssp.erkc.data.rest.models.Comment
import com.software.ssp.erkc.modules.request.authedRequest.activeRequestList.ActiveRequestListAdapter
import kotlinx.android.synthetic.main.item_message_chat.view.*
import kotlinx.android.synthetic.main.item_request.view.*
import org.jetbrains.anko.onClick
import org.jsoup.Jsoup
import java.util.*
import java.text.SimpleDateFormat


class ChatWithDispatcherAdapter(val dataList: List<Comment>) : RecyclerView.Adapter<ChatWithDispatcherAdapter.CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_chat, parent, false)
        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bindRequest(dataList[position])
    }

    class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindRequest(data: Comment) {
            view.apply {
                if (data.initiator?.id == 8) {
                    messageByDispatcher.visibility = View.VISIBLE
                    descriptionMessageByDispatcherTextView.text = data.message
                    val formatter = SimpleDateFormat("hh:mm")
                    val date = formatter.format((Date(data.created_at!!)))
                    timeMessageByDispatcherTextView.text = date

                } else {
                    if (data.downloadLink != null) {
                        messageByUser.visibility = if (data.message != null) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                        descriptionMessageByUserTextView.text = data.message
                        val formatter = SimpleDateFormat("hh:mm")
                        val date = formatter.format((Date(data.created_at!!)))
                        timeMessageByUserTextView.text = date
                        photoByUser.visibility = View.VISIBLE
                        val glideUrl = GlideUrl("http://fon.zayavki.pro" + data.downloadLink, LazyHeaders.Builder()
                                .addHeader("Authorization", "Basic Z2poV3BUT2lJRlBfTnY4THg4SWNqZ0ItOWxOZ2lwcFE6")
                                .build())
                        Glide.with(view).load(glideUrl).into(photoMessageByUserImageView)
                    } else {
                        messageByUser.visibility = if (data.message != "") {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                        descriptionMessageByUserTextView.text = data.message
                        val formatter = SimpleDateFormat("hh:mm")
                        val date = formatter.format((Date(data.created_at!!)))
                        timeMessageByUserTextView.text = date
                    }
                }
            }
        }
    }

}