package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.receipt.BaseReceiptAdapter
import com.software.ssp.erkc.common.receipt.BaseReceiptViewHolder
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import kotlinx.android.synthetic.main.item_receipt.view.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick

class ValueTransferAdapter(dataList: List<RealmReceipt>,
                           val transferClickListener: ((RealmReceipt) -> Unit)? = null,
                           val onDeleteClickListener: ((RealmReceipt, Int) -> Unit)? = null) : BaseReceiptAdapter<ValueTransferAdapter.ViewHolder>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_receipt, parent, false)
        return ViewHolder(view, transferClickListener, onDeleteClickListener)
    }

    class ViewHolder(view: View,
                     val onTransferClickListener: ((RealmReceipt) -> Unit)?,
                     val onDeleteClickListener: ((RealmReceipt, Int) -> Unit)?) : BaseReceiptViewHolder(view) {

        override fun bindReceipt(receipt: RealmReceipt) {
            super.bindReceipt(receipt)

            with(itemView) {

                receiptAmountText.visibility = View.GONE
                receiptPayButton.visibility = View.GONE
                receiptLastPayLayout.visibility = View.GONE
                receiptMenuImage.visibility = View.GONE

                receiptTransferButton.onClick { onTransferClickListener?.invoke(receipt) }
                deleteButton.onClick {
                    deleteProgressBar.visibility = View.VISIBLE
                    receiptTransferButton.enabled = false
                    deleteButton.isEnabled = false
                    onDeleteClickListener?.invoke(receipt, adapterPosition)
                }
            }
        }
    }
}
