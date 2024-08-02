package com.example.poultry.data.siv

import com.example.poultry.data.Data

class SivRepository(private val sivDao: SivDao) {


    fun insert(siv: Siv)  {
        sivDao.insert(siv)
    }

    fun deleteAll(cid: String,date:String){
        sivDao.deleteAll(cid,date)
    }

    fun deletePeriod(cid:String,dateFrom:String,dateTo:String){
        sivDao.deletePeriod(cid,dateFrom,dateTo)
    }

    fun getCid(cid:String,dateFrom:String,dateTo:String):String?{
        return sivDao.getCid(cid,dateFrom,dateTo)
    }

}