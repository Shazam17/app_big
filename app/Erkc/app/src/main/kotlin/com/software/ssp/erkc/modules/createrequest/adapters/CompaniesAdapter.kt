package com.software.ssp.erkc.modules.createrequest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.software.ssp.erkc.R

class CompaniesAdapter(context: Context, resource: Int, private val companiesList: List<String>) : ArrayAdapter<String>(context, resource) {


    override fun getItem(position: Int): String? {
        return companiesList[position]
    }

    override fun getCount(): Int {
        return companiesList.count()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val company: String = getItem(position)!!
        if (convertView == null) {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_spinner_company_request, null)
            view.findViewById<TextView>(R.id.textCompanyRequest)?.text = company
            return view
        }
        convertView.findViewById<TextView>(R.id.textCompanyRequest)?.text = company

        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val company: String = getItem(position)!!
        if (convertView == null) {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_spinner_company_request, null)
            view.findViewById<TextView>(R.id.textCompanyRequest)?.text = company
            return view
        }
        convertView.findViewById<TextView>(R.id.textCompanyRequest)?.text = company
        return convertView
    }
}