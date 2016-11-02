package com.software.ssp.erkc.modules.mainscreen.receiptlist

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


class ReceiptListAdapter(dataList: List<ReceiptSectionViewModel>,
                         val paymentClickListener: ((ReceiptViewModel) -> Unit)? = null,
                         val transferClickListener: ((ReceiptViewModel) -> Unit)? = null,
                         val historyClickListener: ((ReceiptViewModel) -> Unit)? = null,
                         val onDeleteClickListener: ((ReceiptViewModel, Int) -> Unit)? = null) : BaseReceiptAdapter<ReceiptListAdapter.ViewHolder>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(if (viewType == VIEW_TYPE_HEADER) R.layout.item_receipt_header else R.layout.item_receipt, parent, false)
        return ViewHolder(view, paymentClickListener, transferClickListener, historyClickListener, onDeleteClickListener)
    }

    class ViewHolder(view: View,
                     val onPaymentClickListener: ((ReceiptViewModel) -> Unit)?,
                     val onTransferClickListener: ((ReceiptViewModel) -> Unit)?,
                     val onHistoryClickListener: ((ReceiptViewModel) -> Unit)?,
                     val onDeleteClickListener: ((ReceiptViewModel, Int) -> Unit)?) : BaseReceiptViewHolder(view) {

        override fun bindReceipt(receipt: ReceiptViewModel) {
            super.bindReceipt(receipt)

            with(itemView) {

                receiptLastTransferLayout.visibility = View.GONE
                receiptLastPayLayout.visibility = View.GONE

                receiptPayButton.onClick { onPaymentClickListener?.invoke(receipt) }
                receiptTransferButton.onClick { onTransferClickListener?.invoke(receipt) }

                receiptPaymentHistoryImageButton.onClick { onHistoryClickListener?.invoke(receipt) }

                deleteButton.onClick {
                    swipeLayout.animateReset()
                    onDeleteClickListener?.invoke(receipt, adapterPosition)
                }
            }
        }
    }
}
