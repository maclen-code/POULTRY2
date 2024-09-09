package com.example.poultry2.data.ar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.poultry2.data.AppDatabase
import com.example.poultry2.data.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ArRepository

    init {

        val dao = AppDatabase.getDatabase(application,viewModelScope).arDao()
        repository = ArRepository(dao)
    }

    fun insert(ar: Ar) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(ar)
    }

    fun deleteAll(cid:String)  {
        repository.deleteAll(cid)
    }

    fun customerArSummary(cid:String,clusterId:Int,tradeCode:String,rid:String,customerNo:String,
                             balanceType:String):List<Data.CustomerArSummary>{
        return repository.customerArSummary(cid,clusterId,tradeCode,rid,customerNo,balanceType)
    }

    fun acctArSummary(cid:String,clusterId:Int,tradeCode: String,rid:String,
                      balanceType:String):List<Data.AcctArSummary>{
        return repository.acctArSummary(cid,clusterId,tradeCode,rid,balanceType)
    }

    fun arSummary(cid:String,clusterId: Int,tradeCode: String,rid:String,
                  customerNo:String,channel:String, balanceType:String):List<Data.ArSummary>{
        return repository.arSummary(cid,clusterId,tradeCode,rid,customerNo,channel,balanceType)
    }

    fun arInvoice(acctNo:String, balanceType:String):List<Data.ArInvoice>{
        return repository.arInvoice(acctNo,balanceType)
    }
}