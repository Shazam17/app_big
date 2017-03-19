package com.software.ssp.erkc.modules.history.valueshistorylist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.extensions.isSameDay
import com.software.ssp.erkc.extensions.toString
import kotlinx.android.synthetic.main.item_history.view.*


class ValuesHistoryListAdapter(val dataList: List<RealmReceipt>,
                               val onItemClick: ((RealmReceipt) -> Unit)? = null) : RecyclerView.Adapter<ValuesHistoryListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isDateHeaderVisible = position == 0 || (dataList[position].lastIpuTransferDate != null
                && dataList[position - 1].lastIpuTransferDate != null
                && !dataList[position].lastIpuTransferDate!!.isSameDay(dataList[position - 1].lastIpuTransferDate!!))

        holder.setDateVisibility(isDateHeaderVisible)
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
                addressTextView.text = receipt.address
                dateHeaderText.text = receipt.lastIpuTransferDate?.toString(Constants.HISTORY_DATE_FORMAT)
                nameText.text = receipt.name
                barcodeText.text = receipt.barcode

                paymentLayout.visibility = View.GONE

                rootLayout.setOnClickListener { onItemClick?.invoke(receipt) }
            }
        }

        fun setDateVisibility(isVisible: Boolean) {
            itemView.dateHeaderText.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
}
