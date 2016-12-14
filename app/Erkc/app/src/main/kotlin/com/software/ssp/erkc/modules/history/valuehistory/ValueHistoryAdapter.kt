package com.software.ssp.erkc.modules.history.valuehistory

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.extensions.toString
import kotlinx.android.synthetic.main.item_value_history.view.*

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
            itemView.apply {
                itemValueHistoryDate.text = ipu.date?.toString(Constants.VALUES_DATE_FORMAT)
                itemValueHistoryDateValue.text = ipu.value
                if (ipuPre == null || ipu.number != ipuPre.number) {
                    itemValueHistoryTitle.visibility = View.VISIBLE
                    itemValueHistoryTitle.text = itemView.context.getString(R.string.history_value_item_title).format(ipu.serviceName, ipu.number, ipu.installPlace)
                } else {
                    itemValueHistoryTitle.visibility = View.GONE
                }
            }
        }
    }
}
