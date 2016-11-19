package com.software.ssp.erkc.common.receipt

import android.support.v7.widget.RecyclerView
import com.software.ssp.erkc.data.rest.models.Receipt


abstract class BaseReceiptAdapter<VH : BaseReceiptViewHolder>(var dataList: List<Receipt>) : RecyclerView.Adapter<VH>() {
    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.setHeaderVisibility(position == 0 || dataList[position].address != dataList[position - 1].address)
        holder.bindReceipt(dataList[position])
    }
}
