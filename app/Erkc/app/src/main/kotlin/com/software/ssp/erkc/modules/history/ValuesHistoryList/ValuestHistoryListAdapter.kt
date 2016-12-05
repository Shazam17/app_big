package com.software.ssp.erkc.modules.history.ValuesHistoryList

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.extensions.historyFormat
import kotlinx.android.synthetic.main.item_history.view.*
import org.jetbrains.anko.onClick


class ValuesHistoryListAdapter(val dataList: List<RealmReceipt>,
                               val onItemClick: ((RealmReceipt) -> Unit)? = null) : RecyclerView.Adapter<ValuesHistoryListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setHeaderVisibility(position == 0 || dataList[position].address != dataList[position - 1].address)
        holder.bindReceipt(dataList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view, onItemClick)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class ViewHolder(view: View, val onItemClick: ((RealmReceipt) -> Unit)?) : RecyclerView.ViewHolder(view) {

        fun bindReceipt(receipt: RealmReceipt) {
            itemView.apply {
                addressHeaderText.text = receipt.address
                dateText.text = receipt.lastIpuTransferDate?.historyFormat
                nameText.text = receipt.name
                barcodeText.text = receipt.barcode

                paymentLayout.visibility = View.GONE

                onClick { onItemClick?.invoke(receipt) }
            }
        }

        fun setHeaderVisibility(isVisible: Boolean) {
            itemView.addressHeaderText.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
}
