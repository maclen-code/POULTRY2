package com.example.poultry2.data.siv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.poultry2.data.AppDatabase
import com.example.poultry2.data.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SivViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SivRepository

    init {

        val dao = AppDatabase.getDatabase(application,viewModelScope).sivDao()
        repository =SivRepository(dao)
    }

    fun insert(siv: Siv) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(siv)
    }

    fun deleteAll(cid:String,date:String)  {
        repository.deleteAll(cid,date)
    }

    fun deletePeriod(cid:String,dateFrom:String,dateTo:String){
        repository.deletePeriod(cid,dateFrom,dateTo)
    }
    fun getCid(cid:String,dateFrom:String,dateTo:String):String?{
        return repository.getCid(cid,dateFrom,dateTo)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    fun sivSovCluster(cid:String,dateFrom:String,dateTo:String):List<Data.SivSovCluster>{
        return repository.sivSovCluster(cid,dateFrom,dateTo)
    }

    fun volumeClusterTradeType(cid:String,dateFrom:String,dateTo:String):List<Data.SivSovClusterTradeType>{
        return repository.volumeClusterTradeType(cid,dateFrom,dateTo)
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    fun sivSovClusterCategory(cid:String,dateFrom:String,dateTo:String,
                              clusterId:Int):List<Data.SivSovClusterCategory>{
        return repository.sivSovClusterCategory(cid,dateFrom,dateTo,clusterId)
    }

}