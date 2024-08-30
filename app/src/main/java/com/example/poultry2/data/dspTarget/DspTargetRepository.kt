package com.example.poultry2.data.dspTarget




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

//    fun getAll(rid:String,date:String,salesFrom:String,salesTo:String):LiveData<List<Data.Target>>{
//        return dspTargetDao.getAll(rid,date,salesFrom,salesTo)
//    }
}