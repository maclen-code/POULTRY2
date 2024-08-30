package com.example.poultry2.ui.server

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.poultry2.R
import com.example.poultry2.data.Data

class UserTypeListAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int
) :
    ArrayAdapter< Data.UserType>(mContext, mLayoutResourceId) {

    private var list = mutableListOf<Data.UserType>()

    override fun getCount(): Int {
        return list.size
    }
    override fun getItem(position: Int): Data.UserType {
        return list[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var myView = convertView
        if (myView == null) {
            val inflater = (mContext as Activity).layoutInflater
            myView = inflater.inflate(mLayoutResourceId, parent, false)
        }
        try {
            val data: Data.UserType = getItem(position)
            val tv = myView!!.findViewById<View>(R.id.tv_item) as TextView
            tv.text = data.description.uppercase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return myView!!
    }

    internal fun setList(list: List<Data.UserType>) {
        this.list = list.toMutableList()
    }


}