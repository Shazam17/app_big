package com.software.ssp.erkc.modules.history.paymenthistorylist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmPaymentInfo
import com.software.ssp.erkc.extensions.getCompatColor
import com.software.ssp.erkc.extensions.isSameDay
import com.software.ssp.erkc.extensions.toString
import kotlinx.android.synthetic.main.item_history.view.*
import org.jetbrains.anko.textColor


class PaymentHistoryListAdapter(val dataList: List<RealmPaymentInfo>,
                                val onItemClick: ((RealmPaymentInfo) -> Unit)? = null) : RecyclerView.Adapter<PaymentHistoryListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isDateHeaderVisible = position == 0 || (dataList[position].date != null
                && dataList[position - 1].date != null
                && !dataList[position].date!!.isSameDay(dataList[position - 1].date!!))

        holder.setDateVisibility(isDateHeaderVisible)
        holder.bindReceipt(dataList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view, onItemClick)
    }

    class ViewHolder(view: View, val onItemClick: ((RealmPaymentInfo) -> Unit)?) : RecyclerView.ViewHolder(view) {

        fun bindReceipt(payment: RealmPaymentInfo) {
            itemView.apply {
                addressTextView.text = payment.receipt?.address
                nameText.text = payment.receipt?.name
                barcodeText.text = payment.receipt?.barcode

                dateHeaderText.text = payment.date?.toString(Constants.HISTORY_DATE_FORMAT)

                val statusColor = context.getCompatColor(if (payment.status == 0) R.color.colorRed else R.color.colorGreen)
                moneyText.textColor = statusColor
                commissionText.textColor = statusColor

                val paymentWithoutCommission = payment.sum * 100 / (payment.receipt!!.percent + 100)
                moneyText.text = context.getString(R.string.history_money_format).format(paymentWithoutCommission)
                commissionText.text = context.getString(R.string.history_commission_format).format(payment.sum - paymentWithoutCommission, payment.receipt!!.percent)

                rootLayout.setOnClickListener { onItemClick?.invoke(payment) }
            }
        }

        fun setDateVisibility(isVisible: Boolean) {
            itemView.dateHeaderText.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
}
