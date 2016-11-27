package com.software.ssp.erkc.modules.history.PaymentHistoryList

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import kotlinx.android.synthetic.main.item_history.view.*
import org.jetbrains.anko.onClick
import java.text.SimpleDateFormat
import java.util.*


class PaymentHistoryListAdapter(val dataList: List<RealmReceipt>,
                                val onItemClick: ((RealmReceipt) -> Unit)? = null) : RecyclerView.Adapter<PaymentHistoryListAdapter.ViewHolder>() {

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

    class ViewHolder(view: View, val onItemClick: ((RealmReceipt) -> Unit)?) : RecyclerView.ViewHolder(view) {

        fun bindReceipt(receipt: RealmReceipt) {
            itemView.apply {
                addressHeaderText.text = receipt.address
                dateText.text = SimpleDateFormat("dd MMM", Locale.US).format(Date()) //TODO Replace with real date
                nameText.text = receipt.name
                barcodeText.text = receipt.barcode
                moneyText.text = context.getString(R.string.history_money_format).format(receipt.amount)
                commissionText.text = context.getString(R.string.history_commission_format).format(receipt.amount*receipt.percent/100, receipt.percent)

                onClick { onItemClick?.invoke(receipt) }
            }
        }

        fun setHeaderVisibility(isVisible: Boolean) {
            itemView.addressHeaderText.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
}
