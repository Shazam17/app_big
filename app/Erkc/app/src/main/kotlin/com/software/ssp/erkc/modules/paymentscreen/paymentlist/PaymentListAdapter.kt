package com.software.ssp.erkc.modules.paymentscreen.paymentlist

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


class PaymentListAdapter(dataList: List<Receipt>,
                         val paymentClickListener: ((Receipt) -> Unit)? = null,
                         val onDeleteClickListener: ((Receipt, Int) -> Unit)? = null) : BaseReceiptAdapter<PaymentListAdapter.ViewHolder>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_receipt, parent, false)
        return ViewHolder(view, paymentClickListener, onDeleteClickListener)
    }

    class ViewHolder(view: View,
                     val onPaymentClickListener: ((Receipt) -> Unit)?,
                     val onDeleteClickListener: ((Receipt, Int) -> Unit)?) : BaseReceiptViewHolder(view) {

        override fun bindReceipt(receipt: Receipt) {
            super.bindReceipt(receipt)

            with(itemView) {

                receiptLastTransferLayout.visibility = View.GONE
                receiptTransferButton.visibility = View.GONE
                receiptMenuImage.visibility = View.GONE

                receiptPayButton.onClick { onPaymentClickListener?.invoke(receipt) }

                deleteButton.onClick {
                    deleteProgressBar.visibility = View.VISIBLE
                    receiptPayButton.enabled = false
                    deleteButton.isEnabled = false
                    onDeleteClickListener?.invoke(receipt, adapterPosition)
                }
            }
        }
    }
}
