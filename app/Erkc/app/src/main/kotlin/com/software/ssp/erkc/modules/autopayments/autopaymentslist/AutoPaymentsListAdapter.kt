package com.software.ssp.erkc.modules.autopayments.autopaymentslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.extensions.dp
import com.software.ssp.erkc.extensions.setIcon
//import com.software.ssp.erkc.extensions.getIconResId
import kotlinx.android.synthetic.main.item_autopayments.view.*
import org.jetbrains.anko.onClick


class AutoPaymentsListAdapter(val dataList: List<ReceiptViewModel>,
                              val interactionListener: InteractionListener? = null) : RecyclerView.Adapter<AutoPaymentsListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setHeaderVisibility(position == 0 || dataList[position].receipt.address != dataList[position - 1].receipt.address)
        holder.bindReceipt(dataList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AutoPaymentsListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_autopayments, parent, false)
        return AutoPaymentsListAdapter.ViewHolder(view, interactionListener)
    }

    class ViewHolder(view: View,
                     val interactionListener: InteractionListener?) : RecyclerView.ViewHolder(view) {

        fun bindReceipt(receiptViewModel: ReceiptViewModel) {
            itemView.apply {

                addressHeaderText.text = receiptViewModel.receipt.address

                nameText.text = receiptViewModel.receipt.name
                barcodeText.text = receiptViewModel.receipt.barcode

                //typeImage.setImageResource(receiptViewModel.receipt.receiptType.getIconResId())
                typeImage.setIcon(receiptViewModel.receipt)

                linkedCardNameText.text = receiptViewModel.receipt.linkedCard?.name ?: ""

                editImageButton.onClick { interactionListener?.onAutoPaymentEditClick(receiptViewModel.receipt) }

                deleteButton.onClick {
                    deleteProgressBar.visibility = View.VISIBLE
                    deleteButton.isEnabled = false
                    editImageButton.isEnabled = false
                    receiptViewModel.isRemovePending = true
                    interactionListener?.onAutoPaymentDeleteClick(receiptViewModel.receipt)
                }

                deleteProgressBar.visibility = if (receiptViewModel.isRemovePending) View.VISIBLE else View.GONE
                deleteButton.isEnabled = !receiptViewModel.isRemovePending
                editImageButton.isEnabled = !receiptViewModel.isRemovePending
                swipeLayout.reset()

                if (receiptViewModel.isRemovePending) {
                    swipeLayout.offset = -80.dp
                }
            }
        }

        fun setHeaderVisibility(isVisible: Boolean) {
            itemView.addressHeaderText.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    interface InteractionListener {
        fun onAutoPaymentEditClick(receipt: RealmReceipt)
        fun onAutoPaymentDeleteClick(receipt: RealmReceipt)
    }
}
