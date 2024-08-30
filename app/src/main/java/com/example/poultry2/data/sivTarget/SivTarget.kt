package com.example.poultry2.data.sivTarget

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import kotlinx.parcelize.Parcelize


@Entity(tableName = "sivTarget",[Index(value = ["sno","cid","clusterId"])]
    , primaryKeys =["sno","cid","clusterId","date"])
@Parcelize
data class SivTarget(
    @ColumnInfo(name = "sno") var sno: String,
    @ColumnInfo(name = "cid") var cid: String,
    @ColumnInfo(name = "clusterId") var clusterId: Int,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "volumeTarget") var volumeTarget: Int,
    @ColumnInfo(name = "amountTarget") var amountTarget: Int,
    @ColumnInfo(name = "uploaded") var uploaded: Boolean

): Parcelable