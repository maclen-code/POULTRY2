package com.example.poultry2.data.accountTarget

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import kotlinx.parcelize.Parcelize


@Entity(tableName = "accountTarget",[Index(value = ["acctNo","customerNo"])]
    , primaryKeys = ["acctNo","date"])
@Parcelize
data class AccountTarget(
    @ColumnInfo(name = "cid") var cid: String,
    @ColumnInfo(name = "acctNo") var acctNo: String,
    @ColumnInfo(name = "customerNo") var customerNo: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "volumeTarget") var volumeTarget: Int,
    @ColumnInfo(name = "amountTarget") var amountTarget: Int,
    @ColumnInfo(name = "uploaded") var uploaded: Boolean
): Parcelable