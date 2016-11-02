package com.software.ssp.erkc.modules.paymentscreen.paymentlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.receipt.BaseReceiptAdapter
import com.software.ssp.erkc.common.receipt.BaseReceiptViewHolder
import com.software.ssp.erkc.common.receipt.ReceiptSectionViewModel
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import kotlinx.android.synthetic.main.item_receipt.view.*
import org.jetbrains.anko.onClick


class PaymentListAdapter(dataList: List<ReceiptSectionViewModel>,
                         val paymentClickListener: ((ReceiptViewModel) -> Unit)? = null,
                         val onDeleteClickListener: ((ReceiptViewModel, Int) -> Unit)? = null) : BaseReceiptAdapter<PaymentListAdapter.ViewHolder>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(if (viewType == VIEW_TYPE_HEADER) R.layout.item_receipt_header else R.layout.item_receipt, parent, false)
        return ViewHolder(view, paymentClickListener, onDeleteClickListener)
    }

    class ViewHolder(view: View,
                     val onPaymentClickListener: ((ReceiptViewModel) -> Unit)?,
                     val onDeleteClickListener: ((ReceiptViewModel, Int) -> Unit)?) : BaseReceiptViewHolder(view) {

        override fun bindReceipt(receipt: ReceiptViewModel) {
            super.bindReceipt(receipt)

            with(itemView) {

                receiptLastTransferLayout.visibility = View.GONE
                receiptTransferButton.visibility = View.GONE
                receiptIconsLayout.visibility = View.GONE

                receiptPayButton.onClick { onPaymentClickListener?.invoke(receipt) }

                deleteButton.onClick {
                    swipeLayout.animateReset()
                    onDeleteClickListener?.invoke(receipt, adapterPosition)
                }
            }
        }
    }
}
