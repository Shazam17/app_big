package com.software.ssp.erkc.modules.history.PaymentHistoryList

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmPayment
import kotlinx.android.synthetic.main.item_history.view.*
import org.jetbrains.anko.onClick
import java.text.SimpleDateFormat
import java.util.*


class PaymentHistoryListAdapter(val dataList: List<RealmPayment>,
                                val onItemClick: ((RealmPayment) -> Unit)? = null) : RecyclerView.Adapter<PaymentHistoryListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setHeaderVisibility(position == 0 || dataList[position].receipt?.address != dataList[position - 1].receipt?.address)
        holder.bindReceipt(dataList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view, onItemClick)
    }

    class ViewHolder(view: View, val onItemClick: ((RealmPayment) -> Unit)?) : RecyclerView.ViewHolder(view) {

        fun bindReceipt(payment: RealmPayment) {
            itemView.apply {
                addressHeaderText.text = payment.receipt?.address
                nameText.text = payment.receipt?.name
                barcodeText.text = payment.receipt?.barcode

                dateText.text = SimpleDateFormat("dd MMM", Locale.US).format(Date()) //TODO Replace with real date

                moneyText.text = context.getString(R.string.history_money_format).format(payment.amount)
                commissionText.text = context.getString(R.string.history_commission_format).format(payment.amount * payment.receipt!!.percent / 100, payment.receipt!!.percent)


                onClick { onItemClick?.invoke(payment) }
            }
        }

        fun setHeaderVisibility(isVisible: Boolean) {
            itemView.addressHeaderText.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
}
