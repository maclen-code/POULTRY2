package com.example.poultry.data.siv

import androidx.room.*
import com.example.poultry.data.Data

@Dao
interface SivDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(siv: Siv)

    @Query("Delete  FROM siv where cid=:cid and date>=:date")
    fun deleteAll(cid:String,date:String)

    @Query("Delete  FROM siv where Cid=:cid and date between :dateFrom and :dateTo")
    fun deletePeriod(cid:String,dateFrom:String,dateTo:String)

    @Query("select cid  FROM siv where Cid=:cid and date between :dateFrom and :dateTo limit 1")
    fun getCid(cid:String,dateFrom:String,dateTo:String):String?

}