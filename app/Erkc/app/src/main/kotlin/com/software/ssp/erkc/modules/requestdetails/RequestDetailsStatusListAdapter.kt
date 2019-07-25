package com.software.ssp.erkc.modules.requestdetails

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmRequestStatus
import com.software.ssp.erkc.data.realm.models.StatusColors
import kotlinx.android.synthetic.main.item_status_request.view.*

class RequestDetailsStatusListAdapter(val requestStatusItems: List<RealmRequestStatus>) : RecyclerView.Adapter<RequestDetailsStatusListAdapter.RequestDetailsStatusViewHolder>() {
    override fun getItemCount(): Int {
        return requestStatusItems.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestDetailsStatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_status_request, parent, false)
        return RequestDetailsStatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestDetailsStatusViewHolder, position: Int) {
        holder.bindRequestStatus(requestStatusItems[position])
    }


    class RequestDetailsStatusViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindRequestStatus(status: RealmRequestStatus) {
            view.apply {

                itemStatusRequestTextStatusTextView.text = status.getTitle()
                itemStatusRequestDateStatusTextView.text = status.date
                itemStatusRequestOvalView.setCardBackgroundColor(Color.parseColor(StatusColors.getColor(status.type ?: "Canceled")))
            }
        }
    }
}