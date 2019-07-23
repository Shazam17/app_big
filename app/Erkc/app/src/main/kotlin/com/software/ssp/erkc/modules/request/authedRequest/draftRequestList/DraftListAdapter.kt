package com.software.ssp.erkc.modules.request.authedRequest.draftRequestList

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmDraft
import kotlinx.android.synthetic.main.item_request.view.*
import org.jetbrains.anko.onClick


class DraftListAdapter(val dataList: List<RealmDraft>,
                       val onItemClick: ((RealmDraft) -> Unit)? = null) : RecyclerView.Adapter<DraftListAdapter.DraftViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return DraftViewHolder(view, onItemClick)
    }

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        holder.bindRequest(dataList[position])
    }

    class DraftViewHolder(val view: View, val onItemClick: ((RealmDraft) -> Unit)?) : RecyclerView.ViewHolder(view) {
        fun bindRequest(data: RealmDraft) {
            view.apply {

                requestTitle.text = data.title

                requestType.text = data.typeRequest

                requestCardVIew.onClick {
                    onItemClick?.invoke(data)
                }
            }
        }
    }

}