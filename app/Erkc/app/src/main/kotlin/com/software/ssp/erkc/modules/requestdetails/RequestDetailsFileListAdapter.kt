package com.software.ssp.erkc.modules.requestdetails

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmComment
import kotlinx.android.synthetic.main.item_photo_request.view.*
import org.jetbrains.anko.onClick

class RequestDetailsFileListAdapter(val requestComments: List<RealmComment>,  val onItemClick: ((RealmComment) -> Unit)? = null) : RecyclerView.Adapter<RequestDetailsFileListAdapter.RequestDetailsFileViewHolder>() {
    override fun getItemCount(): Int {
        return requestComments.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestDetailsFileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_request, parent, false)
        return RequestDetailsFileViewHolder(view,onItemClick)
    }

    override fun onBindViewHolder(holder: RequestDetailsFileViewHolder, position: Int) {
        holder.bindRequestFile(requestComments[position])
    }


    class RequestDetailsFileViewHolder(val view: View,val onItemClick: ((RealmComment) -> Unit)?) : RecyclerView.ViewHolder(view) {
        fun bindRequestFile(status: RealmComment) {
            view.apply {
                if (status.downloadLink!=null) {
                    val glideUrl = GlideUrl("http://fon.zayavki.pro" + status.downloadLink, LazyHeaders.Builder()
                            .addHeader("Authorization", "Basic Z2poV3BUT2lJRlBfTnY4THg4SWNqZ0ItOWxOZ2lwcFE6")
                            .build())
                    Glide.with(view).load(glideUrl).into(photoRequestImage)
                    removeImageButton.visibility=View.GONE
                }
                itemPhotoRequest.onClick {
                    onItemClick?.invoke(status)
                }

            }
        }
    }
}