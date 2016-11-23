package com.software.ssp.erkc.modules.mainscreen.receiptlist

import android.support.v7.widget.PopupMenu
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


class ReceiptListAdapter(dataList: List<ReceiptViewModel>,
                         val interactionListener: InteractionListener? = null) : BaseReceiptAdapter<ReceiptListAdapter.ViewHolder>(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_receipt, parent, false)
        return ViewHolder(view, interactionListener)
    }

    class ViewHolder(view: View,
                     val interactionListener: InteractionListener? = null) : BaseReceiptViewHolder(view) {

        override fun bindReceipt(receiptViewModel: ReceiptViewModel) {
            super.bindReceipt(receiptViewModel)

            with(itemView) {

                receiptLastTransferLayout.visibility = View.GONE
                receiptLastPayLayout.visibility = View.GONE

                receiptMenuImage.onClick {
                    val popup = PopupMenu(itemView.context, receiptMenuImage)
                    popup.inflate(R.menu.receipt_list_item_menu)

                    popup.setOnMenuItemClickListener {
                        interactionListener?.menuClick(it.itemId, receiptViewModel.receipt)
                        true
                    }

                    popup.show()
                }

                receiptPayButton.onClick { interactionListener?.paymentClick(receiptViewModel.receipt) }
                receiptTransferButton.onClick { interactionListener?.transferClick(receiptViewModel.receipt) }

                deleteButton.onClick {
                    deleteProgressBar.visibility = View.VISIBLE
                    receiptPayButton.enabled = false
                    receiptTransferButton.enabled = false
                    receiptMenuImage.isEnabled = false
                    deleteButton.isEnabled = false
                    interactionListener?.deleteClick(receiptViewModel.receipt)
                    receiptViewModel.isRemovePending = true
                }
            }
        }
    }

    interface InteractionListener {
        fun paymentClick(receipt: RealmReceipt)
        fun transferClick(receipt: RealmReceipt)
        fun menuClick(menuId: Int, receipt: RealmReceipt)
        fun deleteClick(receipt: RealmReceipt)
    }
}
