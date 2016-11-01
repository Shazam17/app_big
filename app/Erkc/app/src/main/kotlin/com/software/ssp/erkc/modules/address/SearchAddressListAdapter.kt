package com.software.ssp.erkc.modules.address

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.models.Address
import java.util.*

/**
 * @author Alexander Popov on 26/10/2016.
 */
class SearchAddressListAdapter(private val onClick: (Address) -> Unit) : RecyclerView.Adapter<SearchAddressListAdapter.ViewHolder>() {

    private var mDataSet = ArrayList<Address>()

    private var mLastAnimatedItemPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView

        init {
            name = view.findViewById(R.id.address) as TextView
        }
    }

    fun swapData(mNewDataSet: List<Address>) {
        mDataSet = mNewDataSet as ArrayList<Address>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAddressListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchAddressListAdapter.ViewHolder, position: Int) {

        val address = mDataSet[position]
        holder.name.text = address.name

        if (mLastAnimatedItemPosition < position) {
            mLastAnimatedItemPosition = position
        }

            holder.itemView.setOnClickListener {
                onClick(mDataSet[position])
            }
    }

    override fun getItemCount(): Int {
        return mDataSet.count()
    }
}