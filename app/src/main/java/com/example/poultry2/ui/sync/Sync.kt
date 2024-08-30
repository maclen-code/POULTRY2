package com.example.poultry2.ui.sync


import com.example.poultry2.data.Data
import com.example.poultry2.ui.global.filter.Filter

object Sync {

    var listData=mutableListOf<Data.Sync>()
    var mode=2
    var cid=""
    var dateFrom=""
    var dateTo=""

    fun add(download:Boolean, dataName:String,userTypeCodeList:List<String>?=null){
        var add=false
        if (userTypeCodeList==null || userTypeCodeList.contains(Filter.user.userType))
            add=true

        if (add && !listData.any { it.download==download && it.dataName == dataName }) {
            listData.add(
                Data.Sync(
                    download,
                    dataName,
                    "pending",
                    0.0,
                    "",
                    ""
                )
            )
        }
    }

    fun createSyncList(){
//        add(true,"Address")
        add(true,"Siv Target")
        add(true,"Dsp Target")
        add(true,"Account Target")

        add(true,"Siv")
        add(true,"Sov")
        add(true,"Sov Promo")

        add(true,"AR")

    }
}