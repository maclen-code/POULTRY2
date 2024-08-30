package com.example.poultry2.data.sovPromoDisc


class SovPromoDiscRepository(private val sovPromoDiscDao: SovPromoDiscDao) {


    fun insert(sovPromoDisc: SovPromoDisc)  {
        sovPromoDiscDao.insert(sovPromoDisc)
    }

    fun deleteAll(cid: String,date:String){
        sovPromoDiscDao.deleteAll(cid,date)
    }

    fun deletePeriod(cid:String,dateFrom:String,dateTo:String){
        sovPromoDiscDao.deletePeriod(cid,dateFrom,dateTo)
    }

    fun getCid(cid:String,dateFrom:String,dateTo:String):String?{
        return sovPromoDiscDao.getCid(cid,dateFrom,dateTo)
    }

}