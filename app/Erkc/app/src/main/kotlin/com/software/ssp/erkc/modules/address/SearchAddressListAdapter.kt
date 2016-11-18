package com.software.ssp.erkc.modules.address

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.db.StreetCache
import kotlinx.android.synthetic.main.address_item.view.*

/**
 * @author Alexander Popov on 26/10/2016.
 */
class SearchAddressListAdapter(private val onClick: (StreetCache) -> Unit) : RecyclerView.Adapter<SearchAddressListAdapter.ViewHolder>() {

    private var dataSet = emptyList<StreetCache>()

    fun swapData(mNewDataSet: List<StreetCache>) {
        dataSet = mNewDataSet
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAddressListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchAddressListAdapter.ViewHolder, position: Int) {

        val address = dataSet[position]
        holder.name.text = address.name

        holder.itemView.setOnClickListener {
            onClick(dataSet[position])
        }
    }

    override fun getItemCount(): Int {
        return dataSet.count()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView

        init {
            name = view.addressTextView
        }
    }
}