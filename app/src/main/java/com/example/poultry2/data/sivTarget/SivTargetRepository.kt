package com.example.poultry2.data.sivTarget

import androidx.lifecycle.LiveData
import com.example.poultry2.data.Data


class SivTargetRepository(private val sivTargetDao: SivTargetDao) {


    fun insert(sivTarget: SivTarget)  {
        sivTargetDao.insert(sivTarget)
    }

    fun deletePeriod(cid: String,dateFrom: String,dateTo:String){
        sivTargetDao.deletePeriod(cid,dateFrom,dateTo)
    }

    fun getCid(cid:String,dateFrom:String,dateTo:String):String?{
        return sivTargetDao.getCid(cid,dateFrom,dateTo)
    }

    fun upload(cid:String):List<SivTarget>{
        return sivTargetDao.upload(cid)
    }

    fun uploadSuccess(cid: String,clusterId:Int,date:String){
        sivTargetDao.uploadSuccess(cid,clusterId,date)
    }

    fun getAll(cid:String,date:String,salesFrom:String,salesTo:String):LiveData<List<Data.TargetCluster>>{
        return sivTargetDao.getAll(cid,date,salesFrom,salesTo)
    }

}