package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.receipt.BaseReceiptAdapter
import com.software.ssp.erkc.common.receipt.BaseReceiptViewHolder
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import kotlinx.android.synthetic.main.item_receipt.view.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick


class ValueTransferAdapter(dataList: List<ReceiptViewModel>,
                           val interactionListener: InteractionListener? = null) : BaseReceiptAdapter<ValueTransferAdapter.ViewHolder>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_receipt, parent, false)
        return ViewHolder(view, interactionListener)
    }

    class ViewHolder(view: View,
                     val interactionListener: InteractionListener? = null) : BaseReceiptViewHolder(view) {

        override fun bindReceipt(receiptViewModel: ReceiptViewModel) {
            super.bindReceipt(receiptViewModel)

            with(itemView) {

                receiptAmountText.visibility = View.GONE
                receiptPayButton.visibility = View.GONE
                receiptLastPayLayout.visibility = View.GONE
                receiptMenuImage.visibility = View.GONE
                receiptAutoPaymentText.visibility = View.GONE

                receiptTransferButton.onClick { interactionListener?.transferClick(receiptViewModel.receipt) }
                deleteButton.onClick {
                    deleteProgressBar.visibility = View.VISIBLE
                    receiptTransferButton.enabled = false
                    deleteButton.isEnabled = false
                    interactionListener?.deleteClick(receiptViewModel.receipt)
                    receiptViewModel.isRemovePending = true
                }
            }
        }
    }

    interface InteractionListener {
        fun transferClick(receipt: RealmReceipt)
        fun deleteClick(receipt: RealmReceipt)
    }
}
