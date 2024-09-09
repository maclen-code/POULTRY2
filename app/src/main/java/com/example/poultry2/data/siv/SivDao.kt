package com.example.poultry2.data.siv

import androidx.room.*
import com.example.poultry2.data.Data

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

    ///////////////////////////////////////////////////////////////////////////////////////////
    @Query("Select x.clusterId, x.cluster,ifnull(sivVolume,0) sivVolume," +
            "ifnull(sivAmount,0) sivAmount,ifnull(sivVolumeTarget,0) sivVolumeTarget," +
            "ifnull(sivAmountTarget,0) sivAmountTarget," +
            "ifnull(sovVolume,0) sovVolume,ifnull(sovAmount,0) sovAmount," +
            "ifnull(sovVolumeTarget,0) sovVolumeTarget,ifnull(promoDiscount,0) promoDiscount " +
            "from (" +
            "Select distinct x.clusterId,cluster " +
            "from siv x " +
            "where cid=:cid  " +
            "and x.date between :dateFrom and :dateTo " +
            "union " +
            "Select distinct x.clusterId,cluster " +
            "from sov x " +
            "where cid=:cid  " +
            "and x.date between :dateFrom and :dateTo " +
            ") x " +

            "left join " +
            "( " +
            "Select clusterId,sum(volume) sivVolume,sum(totalNet) sivAmount  " +
            "from siv " +
            "where cid=:cid  " +
            "and date between :dateFrom and :dateTo " +
            "group by clusterId " +
            ") siv on siv.clusterId=x.clusterId " +

            "left join " +
            "( " +
            "Select clusterId,sum(volume) sovVolume,sum(totalNet) sovAmount " +
            "from sov " +
            "where cid=:cid  " +
            "and date between :dateFrom and :dateTo " +
            "group by clusterId " +
            ") sov on sov.clusterId=x.clusterId " +

            "left join " +
            "( " +
            "Select clusterId,sum(volumeTarget) sivVolumeTarget,sum(amountTarget) sivAmountTarget " +
            "from sivTarget " +
            "where cid=:cid  " +
            "and date between :dateFrom and :dateTo " +
            "group by clusterId " +
            ") sivt on sivt.clusterId=x.clusterId " +

            "left join " +
            "( " +
            "Select clusterId,sum(volumeTarget) sovVolumeTarget " +
            "from dspTarget " +
            "where cid=:cid  " +
            "and date between :dateFrom and :dateTo " +
            "group by clusterId " +
            ") sovt on sovt.clusterId=x.clusterId " +
            "left join " +
            "( " +
            "Select clusterId,sum(promoDiscount) promoDiscount " +
            "from sovPromoDisc " +
            "where cid=:cid " +
            "and date between :dateFrom and :dateTo " +
            "group by clusterId " +
            ") sovPd on sovPd.clusterId=x.clusterId " +
            "order by sov.sovVolume desc")

    fun sivSovCluster(cid:String,dateFrom:String,dateTo:String):List<Data.SivSovCluster>


    @Query("Select x.clusterId, x.cluster,'SIV' transType,tradeType,sum(x.volume) volume " +
            "from siv x " +
            "where cid=:cid " +
            "and x.date between :dateFrom and :dateTo " +
            "group by x.clusterId, x.cluster,tradeType " +
            "union all " +
            "Select x.clusterId, x.cluster,'SOV' trans,tradeType,sum(x.volume) volume " +
            "from sov x " +
            "where cid=:cid  " +
            "and x.date between :dateFrom and :dateTo " +
            "group by x.clusterId, x.cluster,tradeType ")
    fun volumeClusterTradeType(cid:String,dateFrom:String,dateTo:String):List<Data.SivSovClusterTradeType>

    ///////////////////////////////////////////////////////////////////////////////////////////
    @Query("Select x.catId, x.category,ifnull(sivVolume,0) sivVolume," +
            "ifnull(sivAmount,0) sivAmount," +
            "ifnull(sovVolume,0) sovVolume,ifnull(sovAmount,0) sovAmount " +
            "from (" +
            "Select distinct x.catId,category " +
            "from siv x " +
            "where cid=:cid  " +
            "and x.date between :dateFrom and :dateTo " +
            "and clusterId=:clusterId " +
            "union " +
            "Select distinct x.catId,category " +
            "from sov x " +
            "where cid=:cid  " +
            "and x.date between :dateFrom and :dateTo " +
            "and clusterId=:clusterId " +
            ") x " +

            "left join " +
            "( " +
            "Select catId,sum(volume) sivVolume,sum(totalNet) sivAmount  " +
            "from siv " +
            "where cid=:cid  " +
            "and date between :dateFrom and :dateTo " +
            "and clusterId=:clusterId " +
            "group by catId " +
            ") siv on siv.catId=x.catId " +

            "left join " +
            "( " +
            "Select catId,sum(volume) sovVolume,sum(totalNet) sovAmount " +
            "from sov " +
            "where cid=:cid  " +
            "and clusterId=:clusterId " +
            "and date between :dateFrom and :dateTo " +
            "group by catId " +
            ") sov on sov.catId=x.catId " +
            "")

    fun sivSovClusterCategory(cid:String,dateFrom:String,
                              dateTo:String,clusterId:Int):List<Data.SivSovClusterCategory>
}