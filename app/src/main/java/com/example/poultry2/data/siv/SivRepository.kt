package com.example.poultry2.data.siv

import com.example.poultry2.data.Data

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
    ///////////////////////////////////////////////////////////////////////////////////////////
    fun sivSovCluster(cid:String,dateFrom:String,dateTo:String):List<Data.SivSovCluster>{
        return sivDao.sivSovCluster(cid,dateFrom,dateTo)
    }

    fun volumeClusterTradeType(cid:String,dateFrom:String,dateTo:String):List<Data.SivSovClusterTradeType>{
        return sivDao.volumeClusterTradeType(cid,dateFrom,dateTo)
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    fun sivSovClusterCategory(cid:String,dateFrom:String,dateTo:String,
                              clusterId:Int):List<Data.SivSovClusterCategory>{
        return sivDao.sivSovClusterCategory(cid,dateFrom,dateTo,clusterId)
    }

}