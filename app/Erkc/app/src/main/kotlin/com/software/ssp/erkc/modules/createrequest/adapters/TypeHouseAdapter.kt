package com.software.ssp.erkc.modules.createrequest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.software.ssp.erkc.R

class TypeHouseAdapter(context: Context, resource: Int, private val typeHouseList: List<String>) : ArrayAdapter<String>(context, resource) {


    override fun getItem(position: Int): String? {
        return typeHouseList[position]
    }

    override fun getCount(): Int {
        return typeHouseList.count()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val typeHouse: String = getItem(position)!!
        if (convertView == null) {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_spinner_property_type_request, null)
            view.findViewById<TextView>(R.id.textPropertyTypeRequest)?.text = typeHouse
            return view
        }
        convertView.findViewById<TextView>(R.id.textPropertyTypeRequest)?.text = typeHouse

        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val typeHouse: String = getItem(position)!!
        if (convertView == null) {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_spinner_property_type_request, null)
            view.findViewById<TextView>(R.id.textPropertyTypeRequest)?.text = typeHouse
            return view
        }
        convertView.findViewById<TextView>(R.id.textPropertyTypeRequest)?.text = typeHouse
        return convertView
    }
}