package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import com.software.ssp.erkc.R
import kotlinx.android.synthetic.main.item_receipt_transfer_value.view.*
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick

class ValueTransferAdapter(var dataList: List<ReceiptsViewModel>,
                           val transferClickListener: ((ReceiptViewModel) -> Unit)? = null,
                           val onDeleteClickListener: ((ReceiptViewModel, Int) -> Unit)? = null) : SectionedRecyclerViewAdapter<ValueTransferAdapter.ViewHolder>() {

    override fun getItemCount(section: Int): Int {
        return dataList[section].receipts.count()
    }

    override fun getSectionCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder?, section: Int, relativePosition: Int, absolutePosition: Int) {
        holder?.bindReceipt(dataList[section].receipts[relativePosition])
    }

    override fun onBindHeaderViewHolder(holder: ViewHolder?, section: Int) {
        holder?.bindHeader(dataList[section].address)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(if (viewType == VIEW_TYPE_HEADER) R.layout.item_receipt_header else R.layout.item_receipt_transfer_value, parent, false)
        return ViewHolder(view, transferClickListener, onDeleteClickListener)
    }

    class ViewHolder(view: View, val transferClickListener: ((ReceiptViewModel) -> Unit)?, val onDeleteClickListener: ((ReceiptViewModel, Int) -> Unit)?) : RecyclerView.ViewHolder(view) {

        fun bindReceipt(receipt: ReceiptViewModel) {

            with(itemView) {

                receiptName.text = receipt.name
                receiptBarcodeText.text = receipt.barcode
                receiptTransferButton.onClick { transferClickListener?.invoke(receipt) }

                deleteButton.onClick {
                    swipeLayout.animateReset()
                    onDeleteClickListener?.invoke(receipt, adapterPosition)
                }

                swipeLayout.reset()
            }
        }

        fun bindHeader(title: String) {
            itemView.find<TextView>(R.id.receiptAddressHeaderText).text = title
        }
    }
}