package com.example.poultry2.data.accountTarget

import androidx.room.*


@Dao
interface AccountTargetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(accountTarget: AccountTarget)

    @Query("Delete  FROM accountTarget where cid=:cid and date between :dateFrom and :dateTo and uploaded=1")
    fun deletePeriod(cid:String,dateFrom: String,dateTo:String)

    @Query("select cid  FROM accountTarget where Cid=:cid and date between :dateFrom and :dateTo limit 1")
    fun getCid(cid:String,dateFrom:String,dateTo:String):String?

//    @Query("Select *  FROM accountTarget where cid=:cid and uploaded=0")
//    fun upload(cid:String):List<AccountTarget>
//
//    @Query("Update accountTarget set uploaded=1 where acctNo=:acctNo and rid=:rid")
//    fun uploadSuccess(acctNo: String,rid:String)


//    @Query("Select x.bunitId, x.bunit,x.catId,x.category,ifnull(z.volumeTarget,0) volumeTarget " +
//            "FROM  sov x " +
//            "left join  " +
//            "(select x.acctNo,x.rid,x.catId,x.volumeTarget from accountTarget x " +
//            "where x.acctNo=:acctNo and rid=:rid and x.date=:date ) z  " +
//            "on z.catId=x.catId  " +
//            "where x.acctNo=:acctNo and x.rid=:rid and date between :salesFrom and :salesTo " +
//            "group by x.bunitId,x.bUnit,x.catId,x.category")
//    fun getAll(acctNo:String,rid:String,date:String,salesFrom:String,salesTo:String):LiveData<List<Data.Target>>
}