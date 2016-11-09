package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.receipt.BaseReceiptAdapter
import com.software.ssp.erkc.common.receipt.BaseReceiptViewHolder
import com.software.ssp.erkc.data.rest.models.Receipt
import kotlinx.android.synthetic.main.item_receipt.view.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick

class ValueTransferAdapter(dataList: List<Receipt>,
                           val transferClickListener: ((Receipt) -> Unit)? = null,
                           val onDeleteClickListener: ((Receipt, Int) -> Unit)? = null) : BaseReceiptAdapter<ValueTransferAdapter.ViewHolder>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_receipt, parent, false)
        return ViewHolder(view, transferClickListener, onDeleteClickListener)
    }

    class ViewHolder(view: View,
                     val onTransferClickListener: ((Receipt) -> Unit)?,
                     val onDeleteClickListener: ((Receipt, Int) -> Unit)?) : BaseReceiptViewHolder(view) {

        override fun bindReceipt(receipt: Receipt) {
            super.bindReceipt(receipt)

            with(itemView) {

                receiptAmountText.visibility = View.GONE
                receiptPayButton.visibility = View.GONE
                receiptLastPayLayout.visibility = View.GONE
                receiptIconsLayout.visibility = View.GONE

                receiptTransferButton.onClick { onTransferClickListener?.invoke(receipt) }
                deleteButton.onClick {
                    deleteProgressBar.visibility = View.VISIBLE
                    receiptPayButton.enabled = false
                    receiptTransferButton.enabled = false
                    receiptPaymentHistoryImageButton.isEnabled = false
                    deleteButton.isEnabled = false
                    onDeleteClickListener?.invoke(receipt, adapterPosition)
                }
            }
        }
    }
}
