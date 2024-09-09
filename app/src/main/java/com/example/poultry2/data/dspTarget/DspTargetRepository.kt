package com.example.poultry2.data.dspTarget

import com.example.poultry2.data.Data


class DspTargetRepository(private val dspTargetDao: DspTargetDao) {


    fun insert(dspTarget: DspTarget)  {
        dspTargetDao.insert(dspTarget)
    }

    fun deletePeriod(cid: String,dateFrom: String,dateTo:String){
        dspTargetDao.deletePeriod(cid,dateFrom,dateTo)
    }

    fun getCid(cid:String,dateFrom:String,dateTo:String):String?{
        return dspTargetDao.getCid(cid,dateFrom,dateTo)
    }


    fun upload(cid:String):List<DspTarget>{
        return dspTargetDao.upload(cid)
    }

    fun uploadSuccess(cid: String,rid:String,date:String){
        dspTargetDao.uploadSuccess(cid,rid,date)
    }

    fun dspTarget(cid:String,date:String,clusterId:Int):List<Data.TargetDsp>{
        return dspTargetDao.dspTarget(cid,date,clusterId)
    }
}