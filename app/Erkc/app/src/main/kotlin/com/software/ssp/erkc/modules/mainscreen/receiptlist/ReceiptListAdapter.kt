package com.software.ssp.erkc.modules.mainscreen.receiptlist

import android.support.v7.widget.PopupMenu
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


class ReceiptListAdapter(dataList: List<Receipt>,
                         val paymentClickListener: ((Receipt) -> Unit)? = null,
                         val transferClickListener: ((Receipt) -> Unit)? = null,
                         val menuClickListener: ((ReceiptMenuItem, Receipt) -> Unit)? = null,
                         val onDeleteClickListener: ((Receipt, Int) -> Unit)? = null) : BaseReceiptAdapter<ReceiptListAdapter.ViewHolder>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_receipt, parent, false)
        return ViewHolder(view, paymentClickListener, transferClickListener, menuClickListener, onDeleteClickListener)
    }

    class ViewHolder(view: View,
                     val onPaymentClickListener: ((Receipt) -> Unit)?,
                     val onTransferClickListener: ((Receipt) -> Unit)?,
                     val menuClickListener: ((ReceiptMenuItem, Receipt) -> Unit)?,
                     val onDeleteClickListener: ((Receipt, Int) -> Unit)?) : BaseReceiptViewHolder(view) {

        override fun bindReceipt(receipt: Receipt) {
            super.bindReceipt(receipt)

            with(itemView) {

                receiptLastTransferLayout.visibility = View.GONE
                receiptLastPayLayout.visibility = View.GONE

                receiptMenuImage.onClick {
                    val popup = PopupMenu(itemView.context, receiptMenuImage)
                    popup.inflate(R.menu.receipt_list_item_menu)

                    popup.setOnMenuItemClickListener {
                        when(it.itemId){
                            ReceiptMenuItem.FIRST.itemId -> menuClickListener?.invoke(ReceiptMenuItem.FIRST, receipt)
                            ReceiptMenuItem.SECOND.itemId -> menuClickListener?.invoke(ReceiptMenuItem.SECOND, receipt)
                        }
                        false
                    }

                    popup.show()
                }

                receiptPayButton.onClick { onPaymentClickListener?.invoke(receipt) }
                receiptTransferButton.onClick { onTransferClickListener?.invoke(receipt) }

                deleteButton.onClick {
                    deleteProgressBar.visibility = View.VISIBLE
                    receiptPayButton.enabled = false
                    receiptTransferButton.enabled = false
                    receiptMenuImage.isEnabled = false
                    deleteButton.isEnabled = false
                    onDeleteClickListener?.invoke(receipt, adapterPosition)
                }
            }
        }
    }
}
