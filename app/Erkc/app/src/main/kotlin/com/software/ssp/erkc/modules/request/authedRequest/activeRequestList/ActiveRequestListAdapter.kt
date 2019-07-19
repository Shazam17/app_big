package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.realm.models.StatusColors
import kotlinx.android.synthetic.main.item_request.view.*
import org.jetbrains.anko.onClick
import java.util.*
import java.text.SimpleDateFormat


class ActiveRequestListAdapter(val dataList: List<RealmRequest>,
                               val onItemClick: ((RealmRequest) -> Unit)? = null) : RecyclerView.Adapter<ActiveRequestListAdapter.RequestViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view, onItemClick)
    }

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bindRequest(dataList[position])
    }

    class RequestViewHolder(val view: View, val onItemClick: ((RealmRequest) -> Unit)?) : RecyclerView.ViewHolder(view) {
        fun bindRequest(data: RealmRequest) {
            view.apply {

                requestTitle.text = data.name
                requestState.text = data.state?.stateLabel
                requestState.setTextColor(Color.parseColor(StatusColors.getColor(data.state?.stateLabel!!)))
                requestType.text = data.type?.name
                // TODO detect request or appeal
                val formatter = SimpleDateFormat("dd.MM.yyyy hh:mm")
                val date=formatter.format((Date(data.created_at!!)))
                requestSubtitleTextView.text = "Обращение №${data.id} от ${date}"
                //
                requestCardVIew.onClick {
                    onItemClick?.invoke(data)
                }
            }
        }
    }

}