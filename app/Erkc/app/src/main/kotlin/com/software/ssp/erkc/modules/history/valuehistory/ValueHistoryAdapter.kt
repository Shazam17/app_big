package com.software.ssp.erkc.modules.history.valuehistory

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import kotlinx.android.synthetic.main.item_value_history.view.*
import java.text.SimpleDateFormat

/**
 * @author Alexander Popov on 05/12/2016.
 */
class ValueHistoryAdapter(val dataList: List<RealmIpuValue>) : RecyclerView.Adapter<ValueHistoryAdapter.ViewHolderTitle>() {

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderTitle {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_value_history, parent, false)
        return ViewHolderTitle(view)
    }

    override fun onBindViewHolder(holder: ViewHolderTitle, position: Int) {
        holder.bind(dataList[position], if (position != 0) dataList[position - 1] else null)
    }


    class ViewHolderTitle(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(ipu: RealmIpuValue, ipuPre: RealmIpuValue?) {
            ipu.apply {
                itemView.itemValueHistoryDate.text = SimpleDateFormat(Constants.VALUES_DATE_FORMAT).format(date)
                itemView.itemValueHistoryDateValue.text = value.toString()
                if (ipuPre == null || number != ipuPre.number) {
                    itemView.itemValueHistoryTitle.visibility = View.VISIBLE
                    itemView.itemValueHistoryTitle.text = itemView.context.getString(R.string.history_value_item_title).format(serviceName, number, installPlace)
                } else {
                    itemView.itemValueHistoryTitle.visibility = View.GONE
                }
            }
        }
    }
}