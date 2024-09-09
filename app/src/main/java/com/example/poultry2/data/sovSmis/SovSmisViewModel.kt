package com.example.poultry2.data.sovSmis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.poultry2.data.AppDatabase
import com.example.poultry2.data.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SovSmisViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SovSmisRepository

    init {

        val dao = AppDatabase.getDatabase(application,viewModelScope).sovSmisDao()
        repository = SovSmisRepository(dao)
    }

    fun insert(sovSmis: SovSmis) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(sovSmis)
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

    ///////////////////////////////////////////////////////////////////////////////////////////////

    fun sovSmisOrdered(cid:String,dateFrom:String,dateTo:String,transType:String,
                   clusterId:Int,rid: String,channel:String,bunitId:String):List<Data.Ordered>{
        return repository.sovSmisOrdered(cid,dateFrom,dateTo,transType,clusterId,rid,channel,
            bunitId)
    }

    fun sovSmisNotOrdered(cid:String,dateFrom:String,dateTo:String,universeFrom:String,
                      transType:String,clusterId:Int,rid: String,channel:String,
                      bunitId: String):List<Data.NotOrdered>{
        return repository.sovSmisNotOrdered(cid,dateFrom,dateTo,universeFrom,transType,clusterId,
            rid,channel,bunitId)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    fun sovDspBunit(cid:String,dateFrom:String,dateTo:String,universeFrom:String,
                    lyFrom:String,lyTo:String,lmFrom:String,lmTo:String,
                    transType:String,clusterId:Int):List<Data.SovDspBunit> {
        return repository.sovDspBunit(cid,dateFrom,dateTo,universeFrom,lyFrom,lyTo,lmFrom,lmTo,
            transType,clusterId)
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

}