package com.software.ssp.erkc.modules.transaction.paymenttransaction

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmOfflinePayment
import com.software.ssp.erkc.extensions.getIconResId
import com.software.ssp.erkc.extensions.toString
import kotlinx.android.synthetic.main.item_transaction_payment.view.*
import org.jetbrains.anko.onClick

/**
 * @author Alexander Popov on 13/12/2016.
 */
class PaymentTransactionListAdapter(val dataList: List<RealmOfflinePayment>,
                                    val interactionListener: InteractionListener) : RecyclerView.Adapter<PaymentTransactionListAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], interactionListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_transaction_payment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }




    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(payment: RealmOfflinePayment,
                 interactionListener: InteractionListener) {
            itemView.apply {

                createDateText.text = payment.createDate?.toString(Constants.VALUES_DATE_FORMAT)

                val receipt = payment.receipt

                addressText.text = receipt.address
                nameText.text = receipt.name
                barcodeText.text = receipt.barcode

                typeImage.setImageResource(receipt.receiptType.getIconResId())

                swipeLayout.reset()

                payButtonTextView.onClick {
                    interactionListener.onPayClick(payment)
                }

                deleteButton.onClick {
                    deleteButton.isEnabled = false
                    interactionListener.deleteClick(payment)
                }
            }
        }
    }

    interface InteractionListener {
        fun onPayClick(payment: RealmOfflinePayment)
        fun deleteClick(payment: RealmOfflinePayment)
    }
}