package com.example.poultry2.data.sovPromoDisc

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.poultry2.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SovPromoDiscViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SovPromoDiscRepository

    init {

        val dao = AppDatabase.getDatabase(application,viewModelScope).sovPromoDiscDao()
        repository = SovPromoDiscRepository(dao)
    }

    fun insert(sovPromoDisc: SovPromoDisc) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(sovPromoDisc)
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
}