package com.example.poultry2.data.sovSmis

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(
    tableName = "sovSmis"
    , [Index(value = ["cid","clusterId","tradeCode","rid","customerNo","acctNo","channel",
        "bunitId"])]
)
@Parcelize
data class SovSmis(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "transType") var transType: String,
    @ColumnInfo(name = "cid") var cid: String,
    @ColumnInfo(name = "tradeCode") var tradeCode: String,
    @ColumnInfo(name = "tradeType") var tradeType: String,
    @ColumnInfo(name = "clusterId") var clusterId: Int,
    @ColumnInfo(name = "cluster") var cluster: String,
    @ColumnInfo(name = "rid") var rid: String,
    @ColumnInfo(name = "dsp") var dsp: String,
    @ColumnInfo(name = "customerNo") var customerNo: String,
    @ColumnInfo(name = "customer") var customer: String,
    @ColumnInfo(name = "acctNo") var acctNo: String,
    @ColumnInfo(name = "storeName") var storeName: String,
    @ColumnInfo(name = "channel") var channel: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "bunitId") var bunitId: String,
    @ColumnInfo(name = "bunit") var bunit: String,
    @ColumnInfo(name = "volume") var  volume: Double,
    @ColumnInfo(name = "totalNet") var  totalNet: Double
): Parcelable
