package com.example.poultry2.data.inventory

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "inventory", [Index(value = ["cid","bunitId","catId", "itemId"])])
@Parcelize
data class Inventory(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "cid") var cid: String,
    @ColumnInfo(name = "itemId") var itemId: String,
    @ColumnInfo(name = "itemCode") var itemCode: String,
    @ColumnInfo(name = "itemDesc") var itemDesc: String,
    @ColumnInfo(name = "bunitId") var bunitId: String,
    @ColumnInfo(name = "bunit") var bunit: String,
    @ColumnInfo(name = "catId") var catId: String,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "lastActualDate") var lastActualDate: String,
    @ColumnInfo(name = "actual") var actual: Double,
    @ColumnInfo(name = "purchase") var purchase: Double,
    @ColumnInfo(name = "goodStocks") var goodStocks: Double,
    @ColumnInfo(name = "routeReturn") var routeReturn: Double,
    @ColumnInfo(name = "received") var received: Double,
    @ColumnInfo(name = "routeIssue") var routeIssue: Double,
    @ColumnInfo(name = "salesPresell") var salesPresell: Double,
    @ColumnInfo(name = "pullOut") var pullOut: Double,
    @ColumnInfo(name = "warehouseBo") var warehouseBo: Double,
    @ColumnInfo(name = "onHand") var onHand: Double


): Parcelable