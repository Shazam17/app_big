package com.software.ssp.erkc.modules.autopayments.autopaymentslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.extensions.getIconResId
import kotlinx.android.synthetic.main.item_autopayments.view.*
import org.jetbrains.anko.onClick


class AutoPaymentsListAdapter(val dataList: List<Receipt>,
                              val onEditClick: ((Receipt) -> Unit)?,
                              val onDeleteClick: ((Receipt) -> Unit)?) : RecyclerView.Adapter<AutoPaymentsListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setHeaderVisibility(position == 0 || dataList[position].address != dataList[position - 1].address)
        holder.bindReceipt(dataList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AutoPaymentsListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_autopayments, parent, false)
        return AutoPaymentsListAdapter.ViewHolder(view, onEditClick, onDeleteClick)
    }

    class ViewHolder(view: View,
                     val onEditClick: ((Receipt) -> Unit)?,
                     val onDeleteClick: ((Receipt) -> Unit)?) : RecyclerView.ViewHolder(view) {

        fun bindReceipt(receipt: Receipt){
            itemView.apply {

                addressHeaderText.text = receipt.address

                nameText.text = receipt.name
                barcodeText.text = receipt.barcode

                typeImage.setImageResource(receipt.receiptType.getIconResId())

                linkedCardNameText.text = "TODO Replace with real name" //TODO Replace with real name

                editImageButton.onClick { onEditClick?.invoke(receipt) }

                deleteButton.onClick {
                    onDeleteClick?.invoke(receipt)
                    deleteProgressBar.visibility = View.VISIBLE
                    deleteButton.isEnabled = false
                    editImageButton.isEnabled = false
                }

                deleteProgressBar.visibility = View.GONE
                deleteButton.isEnabled = true
                editImageButton.isEnabled = true
                swipeLayout.reset()
            }
        }

        fun setHeaderVisibility(isVisible: Boolean) {
            itemView.addressHeaderText.visibility = if(isVisible) View.VISIBLE else View.GONE
        }
    }
}
