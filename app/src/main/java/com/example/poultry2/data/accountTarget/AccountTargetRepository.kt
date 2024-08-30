package com.example.poultry2.data.accountTarget

class AccountTargetRepository(private val accountTargetDao: AccountTargetDao) {


    fun insert(accountTarget: AccountTarget)  {
        accountTargetDao.insert(accountTarget)
    }

    fun deletePeriod(cid: String,dateFrom: String,dateTo:String){
        accountTargetDao.deletePeriod(cid,dateFrom,dateTo)
    }

    fun getCid(cid:String,dateFrom:String,dateTo:String):String?{
        return accountTargetDao.getCid(cid,dateFrom,dateTo)
    }

//    fun upload(cid:String):List<AccountTarget>{
//        return accountTargetDao.upload(cid)
//    }
//
//    fun uploadSuccess(acctNo: String,rid:String){
//        accountTargetDao.uploadSuccess(acctNo,rid)
//    }
//
//    fun getAll(acctNo: String,rid:String,date:String,salesFrom:String,salesTo:String):LiveData<List<Data.Target>>{
//        return accountTargetDao.getAll(acctNo,rid,date,salesFrom,salesTo)
//    }

}