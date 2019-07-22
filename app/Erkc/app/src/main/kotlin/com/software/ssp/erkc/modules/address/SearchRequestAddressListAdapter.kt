package com.software.ssp.erkc.modules.address

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmAddressRequest
import kotlinx.android.synthetic.main.address_item.view.*

class SearchRequestAddressListAdapter(val dataSet: List<RealmAddressRequest>, val onClick: (RealmAddressRequest) -> Unit) : RecyclerView.Adapter<SearchRequestAddressListAdapter.RequestAddressViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRequestAddressListAdapter.RequestAddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)
        return RequestAddressViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: SearchRequestAddressListAdapter.RequestAddressViewHolder, position: Int) {
        holder.bindStreet(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.count()
    }

    class RequestAddressViewHolder(view: View, val onStreetClick: (RealmAddressRequest) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bindStreet(address: RealmAddressRequest) {
            itemView.apply {
                addressTextView.text = address.address
                setOnClickListener {
                    onStreetClick(address)
                }
            }
        }
    }
}