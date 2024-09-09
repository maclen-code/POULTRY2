package com.example.poultry2.data.dspTarget

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.poultry2.data.Data

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

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Query("Select x.rid,x.volumeTarget,x.amountTarget " +
            "from dspTarget x " +
            "where cid=:cid  " +
            "and x.date=:date " +
            "and (x.clusterId=:clusterId or :clusterId=-1) " +
            " ")
    fun dspTarget(cid:String,date:String,clusterId:Int):List<Data.TargetDsp>

    ///////////////////////////////////////////////////////////////////////////////////////////////

}