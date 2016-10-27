package com.software.ssp.erkc.modules.valuetransfer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.data.rest.models.Receipt

class ValueTransferAdapter(var dataList: List<Receipt>,
                           val itemClickListener: ((Receipt) -> Unit)? = null,
                           val transferClickListener: ((Int) -> Unit)? = null) : RecyclerView.Adapter<ValueTransferAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(viewType, parent, false)
        return ViewHolder(view, itemClickListener, transferClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindReceipt(dataList[position])
    }

    class ViewHolder(view: View, val itemClick: ((Receipt) -> Unit)?, val transferClickListener: ((Int) -> Unit)? = null) : RecyclerView.ViewHolder(view) {

        fun bindReceipt(receipt: Receipt){
            //TODO Bind
        }
    }
}