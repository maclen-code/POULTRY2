package com.example.poultry.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class Data {
    class Policy(var name:String, var userTypeCodeList:List<String>)
    class UserType(var code:String,var description: String)
    class User(var userType: String,var userCode: String)

    @Parcelize
    class Server(var cid:String,var name:String,var localIp:String,var publicIp:String,
                 var database:String,var password:String,
                 var userType: String,var userCode: String,var isLocal: Boolean,
                 var dbaseVersion:String,var lastSync:String):Parcelable

    class FilterSupervisor(var sno:String, var supervisor:String, var area: String,
                           var isChecked: Boolean)

    class Dates(var from:String,var to:String,var universeFrom:String,
                     var lastMonthFrom:String,var lastMonthTo:String,
                     var lastYearFrom:String,var lastYearTo:String)


    class FilterTransType(var transType:String,var type:String, var isChecked: Boolean)

    class Sync(var download:Boolean,var dataName:String,var process:String,var progress:Double,
               var status:String,var error:String)
}