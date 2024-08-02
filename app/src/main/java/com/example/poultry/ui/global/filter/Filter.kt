package com.example.poultry.ui.global.filter


import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.poultry.data.Data
import com.example.poultry.ui.function.Access
import com.example.poultry.ui.function.MyDate.toDateString
import com.example.poultry.ui.function.MyDate.toLocalDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object Filter {
    var user=Data.User("","")
    lateinit var dates:Data.Dates
    var range:String=""

    var updated=MutableLiveData<Boolean>()

    var listServer= mutableListOf<Data.Server>()
//    var listServer=mutableListOf<Data.FilterServer>()
    var cid:String=""

    var listSupervisor=mutableListOf<Data.FilterSupervisor>()
    var sno:String=""


    var listTransType= mutableListOf<Data.FilterTransType>()
    var transType:String=""


    init {
        updated.postValue(false)
    }
    fun setDates(from:LocalDate,to:LocalDate){
        val universeFrom=from.minusMonths(2)

        val lastMonthFrom=to.minusMonths(1).withDayOfMonth(1)
        val lastMonthTo=to.minusMonths(1)

        val lastYearFrom=to.minusYears(1).withDayOfMonth(1)
        val lastYearTo=to.minusYears(1)


        dates= Data.Dates(from.toDateString(),
            to.toDateString(),
            universeFrom.toDateString(),
            lastMonthFrom.toDateString(),
            lastMonthTo.toDateString(),
            lastYearFrom.toDateString(),
            lastYearTo.toDateString()
        )

        range= setDateRange()
    }

    private fun setDateRange():String{
        val f= dates.from.toLocalDate()
        val t= dates.to.toLocalDate()

        val first:String
        val second:String
        if (f.year !=t.year){
            first=f.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
            second=t.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        }else
        {
            first=f.format(DateTimeFormatter.ofPattern("MMM d"))
            second=t.format(DateTimeFormatter.ofPattern("MMM d"))
        }
        return "$first - $second"
    }

    fun saveServerToDevice(context: Context){
        val gson = Gson()
        val jsonString = gson.toJson(listServer)
//        jsonString=AESEncyption.encrypt(jsonString)
        val sh = context.getSharedPreferences("servers", Context.MODE_PRIVATE)
        if (sh != null) {
            with(sh.edit()) {
                putString("listServer", jsonString)
                apply()
            }
        }
    }

    fun getServersFromDevice(context: Context){
        val sh = context.getSharedPreferences("servers", Context.MODE_PRIVATE)
        val listServerString=sh.getString("listServer", "").toString()
        if (listServerString!="") {
//            listServerString=AESEncyption.decrypt(listServerString)!!
            val gson = Gson()
            val listOfMyClassObject: Type =
                object : TypeToken<ArrayList<Data.Server?>?>() {}.type
            listServer = gson.fromJson(listServerString, listOfMyClassObject)
            if (listServer.isNotEmpty()) {
                val server= listServer[0]
                cid = server.cid
                user=Data.User(server.userType,server.userCode)
            }
        }
    }



}