package com.example.poultry2.data.dspTarget

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "dspTarget",[Index(value = ["clusterId","rid","cid"])]
    , primaryKeys =["rid","date"])
@Parcelize
data class DspTarget(
    @ColumnInfo(name = "rid") var rid: String,
    @ColumnInfo(name = "cid") var cid: String,
    @ColumnInfo(name = "clusterId") var clusterId: Int,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "volumeTarget") var volumeTarget: Int,
    @ColumnInfo(name = "amountTarget") var amountTarget: Int,
    @ColumnInfo(name = "uploaded") var uploaded: Boolean
): Parcelable