package com.example.poultry2.data.dspTarget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.poultry2.data.AppDatabase
import com.example.poultry2.data.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DspTargetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DspTargetRepository

    init {

        val dao = AppDatabase.getDatabase(application,viewModelScope).dspTargetDao()
        repository = DspTargetRepository(dao)
    }

    fun insert(dspTarget: DspTarget) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(dspTarget)
    }

    fun deletePeriod(cid: String,dateFrom: String,dateTo:String){
        repository.deletePeriod(cid,dateFrom,dateTo)
    }

    fun getCid(cid:String,dateFrom:String,dateTo:String):String?{
        return repository.getCid(cid,dateFrom,dateTo)
    }

    fun upload(cid:String):List<DspTarget>{
        return repository.upload(cid)
    }

    fun uploadSuccess(cid: String,rid:String,date:String){
        repository.uploadSuccess(cid,rid,date)
    }

    fun dspTarget(cid:String,date:String,clusterId:Int):List<Data.TargetDsp>{
        return repository.dspTarget(cid,date,clusterId)
    }
}