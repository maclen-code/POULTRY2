package com.example.poultry2.data.sivTarget

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.poultry2.data.Data

@Dao
interface SivTargetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sivTarget: SivTarget)

    @Query("Delete  FROM sivTarget where  cid=:cid and date between :dateFrom and :dateTo and uploaded=1")
    fun deletePeriod(cid:String,dateFrom: String,dateTo:String)

    @Query("select cid  FROM sivTarget where Cid=:cid and date between :dateFrom and :dateTo limit 1")
    fun getCid(cid:String,dateFrom:String,dateTo:String):String?

    @Query("Select *  FROM sivTarget where cid=:cid and uploaded=0")
    fun upload(cid:String):List<SivTarget>

    @Query("Update sivTarget set uploaded=1 where cid=:cid and clusterId=:clusterId and date=:date and uploaded=0")
    fun uploadSuccess(cid: String,clusterId:Int,date:String)

    @Query("Select x.clusterId, x.cluster,ifnull(z.volumeTarget,0) volumeTarget," +
            "ifnull(z.amountTarget,0) amountTarget " +
            "FROM  siv x " +
            "left join  " +
            "(select x.clusterId,x.volumeTarget,x.amountTarget from sivTarget x " +
            "where cid=:cid " +
            "and x.date=:date ) z  " +
            "on z.clusterId=x.clusterId  " +
            "where  cid=:cid " +
            "and date between :salesFrom and :salesTo " +
            "group by x.clusterId,x.cluster")
    fun getAll(cid:String,date:String,salesFrom:String,salesTo:String):LiveData<List<Data.TargetCluster>>
    //////////////////////////////////////////////////////////////////////////////////////////////


}