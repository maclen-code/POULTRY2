package com.example.poultry2.data.sovSmis

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.poultry2.data.Data

@Dao
interface SovSmisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sovSmis: SovSmis)

    @Query("DELETE FROM sovSmis where cid=:cid and date>=:date")
    fun deleteAll(cid:String,date:String)

    @Query("DELETE FROM sovSmis where Cid=:cid and date between :dateFrom and :dateTo")
    fun deletePeriod(cid:String,dateFrom:String,dateTo:String)

    @Query("select cid FROM sovSmis where Cid=:cid and date between :dateFrom and :dateTo limit 1")
    fun getCid(cid:String,dateFrom:String,dateTo:String):String?

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Query("select x.acctNo,x.storeName,x.dsp,x.channel,sum(volume) volume,sum(x.totalNet) totalNet " +
            "from sovSmis x " +
            "where cid=:cid  " +
            "and date between :dateFrom and :dateTo " +
            "and (:transType like '%' || transType || '%' or :transType='') " +
            "and (x.clusterId=:clusterId   or :clusterId=-1) " +
            "and (rid=:rid  or :rid='') " +
            "and (x.channel=:channel   or :channel='') " +
            "and ( bunitId=:bunitId or :bunitId='') " +
            "group by x.acctNo,x.storeName")
    fun sovSmisOrdered(cid:String,dateFrom:String,dateTo:String,transType:String,
                   clusterId:Int,rid: String,channel:String,bunitId:String):List<Data.Ordered>

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Query("Select x.acctNo,x.storeName,dsp,channel,max(date) lastOrdered " +
            "from sovSmis x " +
            "left join " +
            "( " +
            "Select acctNo " +
            "from sovSmis " +
            "where cid=:cid " +
            "and date between :dateFrom and :dateTo " +
            "and (:transType like '%' || transType || '%' or :transType='') " +
            "and (clusterId=:clusterId   or :clusterId=-1) " +
            "and (rid=:rid  or :rid='') " +
            "and (channel=:channel   or :channel='') " +
            "and ( bunitId=:bunitId or :bunitId='') " +
            "group by acctNo " +
            ") s on s.acctNo=x.acctNo  " +
            "where x.cid=:cid " +
            "and x.date between :universeFrom and :dateTo " +
            "and (:transType like '%' || x.transType || '%' or :transType='') " +
            "and (clusterId=:clusterId   or :clusterId=-1) " +
            "and (rid=:rid  or :rid='') " +
            "and (channel=:channel   or :channel='') " +
            "and ( bunitId=:bunitId or :bunitId='') " +
            "and s.acctNo is null " +
            "group by x.acctNo,x.storeName")
    fun sovSmisNotOrdered(cid:String,dateFrom:String,dateTo:String,universeFrom:String,
                      transType:String,clusterId:Int,rid: String,channel:String,
                      bunitId: String):List<Data.NotOrdered>

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////


    @Query("Select 'S' productType, x.rid,x.dsp,x.bunitId,bunit,ifnull(s.volume,0) volume," +
            "ifnull(s.totalNet,0) totalNet,ifnull(s.ordered,0) ordered, " +
            "count(distinct acctNo) universe,ifnull(ly.lastYearVolume,0) lastYearVolume," +
            "ifnull(lm.lastMonthVolume,0) lastMonthVolume " +
            "from sovSmis x " +

            "left join " +
            "( " +
            "Select rid,bunitId,sum(volume) volume,count(distinct acctNo) ordered, " +
            "sum(totalNet) totalNet from sovSmis " +
            "where cid=:cid  " +
            "and date between :dateFrom and :dateTo " +
            "and (:transType like '%' || transType || '%' or :transType='') " +
            "and (clusterId=:clusterId or :clusterId=-1) " +
            "group by rid,bunitId " +
            ") s on s.rid=x.rid and s.bunitId=x.bunitId " +

            "left join " +
            "( " +
            "Select rid,bunitId,sum(volume) lastYearVolume " +
            "from sovSmis " +
            "where cid=:cid  " +
            "and date between :lyFrom and :lyTo " +
            "and (:transType like '%' || transType || '%' or :transType='') " +
            "and (clusterId=:clusterId or :clusterId=-1) " +
            "group by rid,bunitId " +
            ") ly on ly.rid=x.rid and ly.bunitId=x.bunitId  " +

            "left join " +
            "( " +
            "Select rid,bunitId,sum(volume) lastMonthVolume " +
            "from sovSmis " +
            "where cid=:cid  " +
            "and date between :lmFrom and :lmTo " +
            "and (:transType like '%' || transType || '%' or :transType='') " +
            "and (clusterId=:clusterId or :clusterId=-1) " +
            "group by rid,bunitId " +
            ") lm on lm.rid=x.rid and lm.bunitId=x.bunitId  " +

            "where cid=:cid  " +
            "and x.date between :universeFrom and :dateTo " +
            "and (:transType like '%' || x.transType || '%' or :transType='') " +
            "and (x.clusterId=:clusterId or :clusterId=-1) " +
            "group by x.rid,x.dsp,x.bunitId,x.bunit")
    fun sovDspBunit(cid:String,dateFrom:String,dateTo:String,universeFrom:String,
                    lyFrom:String,lyTo:String,lmFrom:String,lmTo:String,
                    transType:String,clusterId:Int):List<Data.SovDspBunit>

    ///////////////////////////////////////////////////////////////////////////////////////////////
}