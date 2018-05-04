package com.software.ssp.erkc.modules.transaction.valuestransaction

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmOfflineIpu
import com.software.ssp.erkc.extensions.setIcon
//import com.software.ssp.erkc.extensions.getIconResId
import com.software.ssp.erkc.extensions.toString
import kotlinx.android.synthetic.main.item_transaction_ipu.view.*

/**
 * @author Alexander Popov on 14/12/2016.
 */
class ValuesTransactionListAdapter(val dataList: List<RealmOfflineIpu>,
                                   val interactionListener: InteractionListener? = null) : RecyclerView.Adapter<ValuesTransactionListAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_transaction_ipu, parent, false)
        return ViewHolder(view, interactionListener)
    }

    class ViewHolder(view: View, val interactionListener: InteractionListener?) : RecyclerView.ViewHolder(view) {
        fun bind(offlineIpu: RealmOfflineIpu) {
            itemView.apply {
                addressText.text = offlineIpu.receipt.address
                nameText.text = offlineIpu.receipt.name
                barcodeText.text = offlineIpu.receipt.barcode

                createDateText.text = offlineIpu.createDate?.toString(Constants.VALUES_DATE_FORMAT)

                //typeImage.setImageResource(offlineIpu.receipt.receiptType.getIconResId())
                typeImage.setIcon(offlineIpu.receipt)

                swipeLayout.reset()

                sendValuesTextView.setOnClickListener {
                    interactionListener?.onSendValuesClick(offlineIpu)
                }

                deleteButton.setOnClickListener {
                    interactionListener?.onDeleteOfflineIpuClick(offlineIpu)
                }
            }
        }
    }

    interface InteractionListener {
        fun onSendValuesClick(offlineIpu: RealmOfflineIpu)
        fun onDeleteOfflineIpuClick(offlineIpu: RealmOfflineIpu)
    }
}