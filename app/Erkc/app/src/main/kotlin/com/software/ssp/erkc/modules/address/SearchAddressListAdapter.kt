package com.software.ssp.erkc.modules.address

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmStreet
import kotlinx.android.synthetic.main.address_item.view.*


class SearchAddressListAdapter(val dataSet: List<RealmStreet>, val onClick: (RealmStreet) -> Unit) : RecyclerView.Adapter<SearchAddressListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAddressListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: SearchAddressListAdapter.ViewHolder, position: Int) {
        holder.bindStreet(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.count()
    }

    class ViewHolder(view: View, val onStreetClick: (RealmStreet) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bindStreet(street: RealmStreet) {
            itemView.apply {
                addressTextView.text = street.name
                setOnClickListener {
                    onStreetClick(street)
                }
            }
        }
    }
}
