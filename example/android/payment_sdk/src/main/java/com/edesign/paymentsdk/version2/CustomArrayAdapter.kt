package com.edesign.paymentsdk.version2

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.edesign.paymentsdk.R
import java.util.ArrayList


class CustomArrayAdapter(
    context: Context,
    var arrayList: ArrayList<String>
) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        convertView = inflater.inflate(R.layout.custom_spinner, parent, false)
        val txtName = convertView.findViewById<TextView>(R.id.text1)
        txtName.text = arrayList[position]
        if (position==0){
            txtName.visibility=View.GONE
        }
        return convertView
    }

}