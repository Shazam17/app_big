package com.software.ssp.erkc.common.receipt

import android.support.v7.widget.RecyclerView


abstract class BaseReceiptAdapter<VH : BaseReceiptViewHolder>(var dataList: List<ReceiptViewModel>) : RecyclerView.Adapter<VH>() {
    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.setHeaderVisibility(position == 0 || dataList[position].receipt.address != dataList[position - 1].receipt.address)
        holder.bindReceipt(dataList[position])
    }
}
