package com.example.poultry2.data.sivTarget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.poultry2.data.AppDatabase
import com.example.poultry2.data.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SivTargetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SivTargetRepository

    init {

        val dao = AppDatabase.getDatabase(application,viewModelScope).sivTargetDao()
        repository = SivTargetRepository(dao)
    }

    fun insert(sivTarget: SivTarget) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(sivTarget)
    }

    fun deletePeriod(cid: String,dateFrom: String,dateTo:String){
        repository.deletePeriod(cid,dateFrom,dateTo)
    }

    fun getCid(cid:String,dateFrom:String,dateTo:String):String?{
        return repository.getCid(cid,dateFrom,dateTo)
    }

    fun upload(cid:String):List<SivTarget>{
        return repository.upload(cid)
    }

    fun uploadSuccess(cid: String,clusterId:Int,date:String){
        repository.uploadSuccess(cid,clusterId,date)
    }

    fun getAll(cid:String,date:String,salesFrom:String,salesTo:String):LiveData<List<Data.TargetCluster>>{
        return repository.getAll(cid,date,salesFrom,salesTo)
    }

}