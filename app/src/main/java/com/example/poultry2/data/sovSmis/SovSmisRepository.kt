package com.example.poultry2.data.sovSmis

import com.example.poultry2.data.Data


class SovSmisRepository(private val sovSmisDao: SovSmisDao) {


    fun insert(sovSmis: SovSmis)  {
        sovSmisDao.insert(sovSmis)
    }

    fun deleteAll(cid: String,date:String){
        sovSmisDao.deleteAll(cid,date)
    }

    fun deletePeriod(cid:String,dateFrom:String,dateTo:String){
        sovSmisDao.deletePeriod(cid,dateFrom,dateTo)
    }

    fun getCid(cid:String,dateFrom:String,dateTo:String):String?{
        return sovSmisDao.getCid(cid,dateFrom,dateTo)
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    fun sovSmisOrdered(cid:String,dateFrom:String,dateTo:String,transType:String,
                   clusterId:Int,rid: String,channel:String,bunitId:String):List<Data.Ordered>{
        return sovSmisDao.sovSmisOrdered(cid,dateFrom,dateTo,transType,clusterId,rid,channel,
            bunitId)
    }

    fun sovSmisNotOrdered(cid:String,dateFrom:String,dateTo:String,universeFrom:String,
                      transType:String,clusterId:Int,rid: String,channel:String,
                      bunitId: String):List<Data.NotOrdered>{
        return sovSmisDao.sovSmisNotOrdered(cid,dateFrom,dateTo,universeFrom,transType,clusterId,
            rid,channel,bunitId)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    fun sovDspBunit(cid:String,dateFrom:String,dateTo:String,universeFrom:String,
                    lyFrom:String,lyTo:String,lmFrom:String,lmTo:String,
                    transType:String,clusterId:Int):List<Data.SovDspBunit> {
        return sovSmisDao.sovDspBunit(cid,dateFrom,dateTo,universeFrom,lyFrom,lyTo,lmFrom,lmTo,
            transType,clusterId)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
}