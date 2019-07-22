package com.software.ssp.erkc.modules.createrequest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.software.ssp.erkc.data.realm.models.RealmAddressRequest

class AddressRequestAdapter(context: Context, resource: Int, private val addressList: List<RealmAddressRequest>) : ArrayAdapter<RealmAddressRequest>(context, resource) {

    private val DEFAULT_VALUE = -1

    override fun getCount(): Int = addressList.count()

    override fun getItem(position: Int): RealmAddressRequest?  = addressList[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val address: RealmAddressRequest?
        if (position == DEFAULT_VALUE) {
            address = null
        } else {
            address = getItem(position)
        }
        if (convertView == null) {
            val view = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_spinner_item, null)
            if (address == null) {
                view.findViewById<TextView>(android.R.id.text1)?.text = "Адрес"
            } else {
                view.findViewById<TextView>(android.R.id.text1)?.text = address.address
            }
            return view
        }
        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val address: RealmAddressRequest?
        if (position == DEFAULT_VALUE) {
            address = null
        } else {
            address = getItem(position)
        }
        if (convertView == null) {
            val view = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_spinner_item, null)
            if (address == null) {
                view.findViewById<TextView>(android.R.id.text1)?.text = "Адрес"
            } else {
                view.findViewById<TextView>(android.R.id.text1)?.text = address.address
            }
            return view
        }
        return convertView
    }
}