package com.example.poultry2.data.ar

import androidx.room.*
import com.example.poultry2.data.Data


@Dao
interface ArDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ar: Ar)

    @Query("Delete  FROM ar where cid=:cid")
    fun deleteAll(cid:String)

    @Query("Select x.customerNo,x.customer,balanceType," +
            "sum(case when agingId= -9999 then balance else 0 end) as a1, " +
            "sum(case when agingId = 1 then balance else 0 end) as a2, " +
            "sum(case when agingId = 16 then balance else 0 end) as a3, " +
            "sum(case when agingId >= 31 then balance else 0 end) as a4," +
            "sum(balance) as total " +
            "FROM ar x " +

            "left join " +
            "( " +
            "Select customerNo,count(distinct acctNo) branchCount " +
            "from sov " +
            "where cid=:cid and (:sno like '%' || sno || '%'  or :sno='') " +
            "and (customerNo=:customerNo  or :customerNo='') " +
            "and (clusterId=:clusterId  or :clusterId=-1) " +
            "and (tradeCode=:tradeCode  or :tradeCode='') " +
            "and (rid=:rid  or :rid='') " +
            "group by customerNo " +
            ") bc on bc.customerNo=x.customerNo  " +

            "where cid=:cid " +
            "and (:sno like '%' || sno || '%'  or :sno='') " +
            "and (x.customerNo=:customerNo  or :customerNo='') " +
            "and (balanceType=:balanceType or :balanceType='') " +
            "and bc.branchCount>1 " +
            "and (clusterId=:clusterId  or :clusterId=-1) " +
            "and (tradeCode=:tradeCode  or :tradeCode='') " +
            "and (rid=:rid  or :rid='') " +
            "group by x.customerNo,x.customer,x.balanceType ")
    fun customerArSummary(cid:String,sno:String,clusterId:Int,tradeCode:String,rid:String,
                          customerNo:String,balanceType:String):List<Data.CustomerArSummary>


    @Query("Select acctNo,storeName,balanceType," +
            "sum(case when agingId= -9999 then balance else 0 end) as a1, " +
            "sum(case when agingId = 1 then balance else 0 end) as a2, " +
            "sum(case when agingId = 16 then balance else 0 end) as a3, " +
            "sum(case when agingId >= 31 then balance else 0 end) as a4," +
            "sum(balance) as total " +
            "FROM ar x " +
            "where cid=:cid " +
            "and (:sno like '%' || sno || '%'  or :sno='') " +
            "and (clusterId=:clusterId  or :clusterId=-1) " +
            "and (tradeCode=:tradeCode  or :tradeCode='') " +
            "and (rid=:rid  or :rid='') " +
            "and (balanceType=:balanceType or :balanceType='') " +
            "group by acctNo,storeName,x.balanceType")
    fun acctArSummary(cid:String,sno:String,clusterId:Int,tradeCode: String,rid:String,
                             balanceType:String):List<Data.AcctArSummary>

    @Query("Select balanceType," +
            "sum(case when agingId= -9999 then balance else 0 end) as a1, " +
            "sum(case when agingId = 1 then balance else 0 end) as a2, " +
            "sum(case when agingId = 16 then balance else 0 end) as a3, " +
            "sum(case when agingId >= 31 then balance else 0 end) as a4," +
            "sum(balance) as total " +
            "FROM ar x " +
            "where cid=:cid " +
            "and (:sno like '%' || sno || '%'  or :sno='') " +
            "and (x.clusterId=:clusterId or :clusterId=-1) " +
            "and (x.tradeCode=:tradeCode or :tradeCode='') " +
            "and (rid=:rid  or :rid='') " +
            "and (channel=:channel  or :channel='') " +
            "and (acctNo=:acctNo  or :acctNo='') " +
            "and (customerNo=:customerNo  or :customerNo='') " +
            "group by x.balanceType")
    fun arSummary(cid:String,sno:String,clusterId: Int,tradeCode: String,rid:String,channel:String,
                  acctNo:String,customerNo:String):List<Data.ArSummary>


    @Query("Select agingId, aging,date,invoiceNo,balanceType,terms," +
            "dueDate,balance,checkNo,checkDate,0 isChecked " +
            "FROM ar " +
            "where acctNo=:acctNo " +
            "and (balanceType=:balanceType or :balanceType='') " +
            "")
    fun arInvoice(acctNo:String, balanceType:String):List<Data.ArInvoice>
}