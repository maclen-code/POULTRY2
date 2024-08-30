package com.example.poultry2.data.siv

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(
    tableName = "siv"
    , [Index(value = ["cid","clusterId","tradeCode"])]
)
@Parcelize
data class Siv(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "cid") var cid: String,
    @ColumnInfo(name = "tradeCode") var tradeCode: String,
    @ColumnInfo(name = "tradeType") var tradeType: String,
    @ColumnInfo(name = "clusterId") var clusterId: Int,
    @ColumnInfo(name = "cluster") var cluster: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "sno") var sno: String,
    @ColumnInfo(name = "supervisor") var supervisor: String,
    @ColumnInfo(name = "invoiceNo") var invoiceNo: String,
    @ColumnInfo(name = "itemId") var itemId: String,
    @ColumnInfo(name = "itemCode") var itemCode: String,
    @ColumnInfo(name = "itemDesc") var itemDesc: String,
    @ColumnInfo(name = "bunitId") var bunitId: String,
    @ColumnInfo(name = "bunit") var bunit: String,
    @ColumnInfo(name = "catId") var catId: String,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "qty") var  qty: Int,
    @ColumnInfo(name = "retailUnit") var retailUnit: String,
    @ColumnInfo(name = "volume") var  volume: Double,
    @ColumnInfo(name = "volumeUnit") var volumeUnit: String,
    @ColumnInfo(name = "totalNet") var  totalNet: Double
): Parcelable
