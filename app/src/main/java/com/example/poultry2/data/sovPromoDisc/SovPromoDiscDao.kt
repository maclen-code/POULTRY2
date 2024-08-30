package com.example.poultry2.data.sovPromoDisc

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SovPromoDiscDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sovPromoDisc: SovPromoDisc)

    @Query("DELETE FROM sovPromoDisc where cid=:cid and date>=:date")
    fun deleteAll(cid:String,date:String)

    @Query("DELETE FROM sovPromoDisc where Cid=:cid and date between :dateFrom and :dateTo")
    fun deletePeriod(cid:String,dateFrom:String,dateTo:String)

    @Query("select cid FROM sovPromoDisc where Cid=:cid and date between :dateFrom and :dateTo limit 1")
    fun getCid(cid:String,dateFrom:String,dateTo:String):String?

}