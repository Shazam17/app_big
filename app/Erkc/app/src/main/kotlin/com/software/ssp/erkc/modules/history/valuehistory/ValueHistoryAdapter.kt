package com.software.ssp.erkc.modules.history.valuehistory

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmIpu

/**
 * @author Alexander Popov on 05/12/2016.
 */
class ValueHistoryAdapter(val dataList: List<RealmIpu>) : RecyclerView.Adapter<ValueHistoryAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_value_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(ipu: RealmIpu) {

        }
    }
}