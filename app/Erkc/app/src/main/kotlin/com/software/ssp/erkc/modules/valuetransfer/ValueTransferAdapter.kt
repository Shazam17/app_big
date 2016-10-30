package com.software.ssp.erkc.modules.valuetransfer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.models.Receipt
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick

class ValueTransferAdapter(var dataList: List<ReceiptsViewModel>,
                           val transferClickListener: ((Receipt) -> Unit)? = null) : SectionedRecyclerViewAdapter<ValueTransferAdapter.ViewHolder>() {

    override fun getItemCount(section: Int): Int {
        return dataList[section].itemsInSection.count()
    }

    override fun getSectionCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder?, section: Int, relativePosition: Int, absolutePosition: Int) {
        holder?.bindReceipt(dataList[section].itemsInSection[relativePosition])
    }

    override fun onBindHeaderViewHolder(holder: ViewHolder?, section: Int) {
        holder?.bindHeader(dataList[section].headerTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(if(viewType == VIEW_TYPE_HEADER) R.layout.item_receipt_header else R.layout.item_receipt_transfer_value, parent, false)
        return ViewHolder(view, transferClickListener)
    }

    class ViewHolder(view: View, val transferClickListener: ((Receipt) -> Unit)?) : RecyclerView.ViewHolder(view) {

        fun bindReceipt(receipt: Receipt) {

            itemView.find<TextView>(R.id.receiptName).text = receipt.serviceName
            itemView.find<TextView>(R.id.receiptBarcodeText).text = receipt.barcode

            itemView.find<TextView>(R.id.receiptTransferButton).onClick {
                transferClickListener?.invoke(receipt)
            }
        }

        fun bindHeader(title: String){
            itemView.find<TextView>(R.id.receiptAddressHeaderText).text = title
        }
    }
}