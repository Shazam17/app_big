package com.software.ssp.erkc.modules.createrequest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.software.ssp.erkc.data.realm.models.RealmTypeHouse

class TypeHouseAdapter(context: Context, resource: Int, private val typeHouseList: List<RealmTypeHouse>) : ArrayAdapter<RealmTypeHouse>(context, resource) {

    override fun getItem(position: Int): RealmTypeHouse? {
        return typeHouseList[position]
    }

    override fun getCount(): Int {
        return typeHouseList.count()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val typeHouse: RealmTypeHouse = getItem(position)!!
        if (convertView == null) {
            val view = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_spinner_item, null)
            view.findViewById<TextView>(android.R.id.text1)?.text = typeHouse.name
            return view
        }
        convertView.findViewById<TextView>(android.R.id.text1)?.text = typeHouse.name
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val typeHouse: RealmTypeHouse = getItem(position)!!
        if (convertView == null) {
            val view = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_spinner_item, null)
            view.findViewById<TextView>(android.R.id.text1)?.text = typeHouse.name
            return view
        }
        convertView.findViewById<TextView>(android.R.id.text1)?.text = typeHouse.name
        return convertView
    }
}