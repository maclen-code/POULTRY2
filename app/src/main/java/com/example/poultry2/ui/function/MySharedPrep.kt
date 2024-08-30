package com.example.poultry2.ui.function

import android.content.Context

object MySharedPrep {


    fun get(context: Context, name:String, key:String):String {
        val sh = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        if (sh!=null) return sh.getString(key, "").toString()
        return ""
    }

    fun set(context: Context, name:String, key:String,value:String) {
        val sh = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        if (sh != null) {
            with (sh.edit()) {
                putString(key,value)
                apply()
            }
        }
    }

}