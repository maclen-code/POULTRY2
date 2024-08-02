package com.example.poultry.ui.sync


import com.example.poultry.data.Data
import com.example.poultry.ui.global.filter.Filter

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
        add(true,"Siv")

    }
}