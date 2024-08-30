package com.example.poultry2.data.dspTarget

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DspTargetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dspTarget: DspTarget)

    @Query("Delete  FROM dspTarget where  cid=:cid and date between :dateFrom and :dateTo and uploaded=1")
    fun deletePeriod(cid:String,dateFrom: String,dateTo:String)

    @Query("select cid  FROM dspTarget where Cid=:cid and date between :dateFrom and :dateTo limit 1")
    fun getCid(cid:String,dateFrom:String,dateTo:String):String?

    @Query("Select *  FROM dspTarget where cid=:cid and uploaded=0")
    fun upload(cid:String):List<DspTarget>

    @Query("Update dspTarget set uploaded=1 where cid=:cid and rid=:rid and date=:date and uploaded=0")
    fun uploadSuccess(cid: String,rid:String,date:String)

//    @Query("Select x.bunitId, x.bunit,x.catId,x.category,ifnull(z.volumeTarget,0) volumeTarget " +
//            "FROM  sov x " +
//            "left join  " +
//            "(select x.catId,x.volumeTarget from dspTarget x " +
//            "where x.rid=:rid and x.date=:date ) z  " +
//            "on z.catId=x.catId  " +
//            "where x.rid=:rid and date between :salesFrom and :salesTo " +
//            "group by x.bunitId,x.bUnit,x.catId,x.category")
//    fun getAll(rid:String,date:String,salesFrom:String,salesTo:String):LiveData<List<Data.Target>>

}