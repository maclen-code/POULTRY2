package com.example.poultry2.data.sovPromoDisc

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(
    tableName = "sovPromoDisc"
    , [Index(value = ["clusterId"])]
)
@Parcelize
data class SovPromoDisc(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "cid") var cid: String,
    @ColumnInfo(name = "sno") var sno: String,
    @ColumnInfo(name = "clusterId") var clusterId: Int,
    @ColumnInfo(name = "cluster") var cluster: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "promoDiscount") var  totalNet: Double
): Parcelable
