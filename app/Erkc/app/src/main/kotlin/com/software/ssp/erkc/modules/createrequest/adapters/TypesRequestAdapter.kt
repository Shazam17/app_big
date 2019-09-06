package com.software.ssp.erkc.modules.createrequest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.software.ssp.erkc.R

class TypesRequestAdapter(context: Context, resource: Int, private val typesRequestList: List<String>) : ArrayAdapter<String>(context, resource) {


    override fun getItem(position: Int): String? {
        return typesRequestList[position]
    }

    override fun getCount(): Int {
        return typesRequestList.count()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val typeRequest: String = getItem(position)!!
        if (convertView == null) {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_spinner_type_request, null)
            view.findViewById<TextView>(R.id.textTypeRequest)?.text = typeRequest
            return view
        }
        convertView.findViewById<TextView>(R.id.textTypeRequest)?.text = typeRequest

        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val typeRequest: String = getItem(position)!!
        if (convertView == null) {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_spinner_type_request, null)
            view.findViewById<TextView>(R.id.textTypeRequest)?.text = typeRequest
            return view
        }
        convertView.findViewById<TextView>(R.id.textTypeRequest)?.text = typeRequest
        return convertView
    }
}