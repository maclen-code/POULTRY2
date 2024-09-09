package com.example.poultry2.data.ar

import com.example.poultry2.data.Data

class ArRepository(private val arDao: ArDao) {


    fun insert(ar: Ar)  {
        arDao.insert(ar)
    }

    fun deleteAll(cid: String){
        arDao.deleteAll(cid)
    }

    fun customerArSummary(cid:String,clusterId:Int,tradeCode:String,rid:String,
                          customerNo:String,balanceType:String):List<Data.CustomerArSummary>{
        return arDao.customerArSummary(cid,clusterId,tradeCode,rid,customerNo,balanceType)
    }

    fun acctArSummary(cid:String,clusterId:Int,tradeCode: String,rid:String,
                             balanceType:String):List<Data.AcctArSummary>{
        return arDao.acctArSummary(cid,clusterId,tradeCode,rid,balanceType)
    }

    fun arSummary(cid:String,clusterId: Int,tradeCode: String,rid:String,
                  customerNo:String,channel:String,balanceType:String):List<Data.ArSummary>{
        return arDao.arSummary(cid,clusterId,tradeCode,rid,customerNo,channel,balanceType)
    }


    fun arInvoice(acctNo:String, balanceType:String):List<Data.ArInvoice>{
        return arDao.arInvoice(acctNo,balanceType)
    }


}