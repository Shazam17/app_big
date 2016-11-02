package com.software.ssp.erkc.common.receipt

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter


abstract class BaseReceiptAdapter<VH : BaseReceiptViewHolder>(var dataList: List<ReceiptSectionViewModel>) : SectionedRecyclerViewAdapter<VH>() {

    override fun getSectionCount(): Int {
        return dataList.count()
    }

    override fun getItemCount(section: Int): Int {
        return dataList[section].receipts.count()
    }

    override fun onBindHeaderViewHolder(holder: VH?, section: Int) {
        holder?.bindHeader(dataList[section].address)
    }

    override fun onBindViewHolder(holder: VH?, section: Int, relativePosition: Int, absolutePosition: Int) {
        holder?.bindReceipt(dataList[section].receipts[relativePosition])
    }
}
