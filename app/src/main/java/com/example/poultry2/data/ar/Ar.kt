package com.example.poultry2.data.ar

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "ar", [Index(value = ["cid","rid", "acctNo","channel"])])
@Parcelize
data class Ar(
    @PrimaryKey(autoGenerate = true) val id: Long,
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
    @ColumnInfo(name = "invoiceNo") var invoiceNo: String,
    @ColumnInfo(name = "terms") var terms: String,
    @ColumnInfo(name = "balanceType") var balanceType: String,
    @ColumnInfo(name = "daysDue") var daysDue: Int,
    @ColumnInfo(name = "agingId") var agingId: Int,
    @ColumnInfo(name = "aging") var aging: String,
    @ColumnInfo(name = "dueDate") var dueDate: String,
    @ColumnInfo(name = "balance") var balance: Double,
    @ColumnInfo(name = "checkNo") var checkNo: String,
    @ColumnInfo(name = "checkDate") var checkDate: String?

): Parcelable