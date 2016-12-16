package com.software.ssp.erkc.modules.transaction.valuestransaction

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.extensions.getIconResId
import kotlinx.android.synthetic.main.item_transaction_ipu.view.*
import org.jetbrains.anko.onClick

/**
 * @author Alexander Popov on 14/12/2016.
 */
class ValuesTransactionListAdapter(val dataList: List<RealmReceipt>,
                                   val interactionListener: InteractionListener) : RecyclerView.Adapter<ValuesTransactionListAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], interactionListener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_transaction_ipu, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(receipt: RealmReceipt, interactionListener: InteractionListener) {
            itemView.apply {
                addressHeaderText.text = receipt.address
                nameText.text = receipt.name
                barcodeText.text = receipt.barcode

                typeImage.setImageResource(receipt.receiptType.getIconResId())

                swipeLayout.reset()

                sendValuesTextView.onClick {
                    interactionListener.itemClick(receipt)
                }

                deleteButton.onClick {
                    deleteButton.isEnabled = false
                    interactionListener.deleteClick(receipt)
                }
            }
        }
    }

    interface InteractionListener {
        fun itemClick(ipu: RealmReceipt)
        fun deleteClick(ipu: RealmReceipt)
    }
}