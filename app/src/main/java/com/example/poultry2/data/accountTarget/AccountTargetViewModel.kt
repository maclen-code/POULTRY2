package com.example.poultry2.data.accountTarget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.poultry2.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountTargetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AccountTargetRepository

    init {

        val dao = AppDatabase.getDatabase(application,viewModelScope).accountTargetDao()
        repository = AccountTargetRepository(dao)
    }

    fun insert(accountTarget: AccountTarget) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(accountTarget)
    }

    fun deletePeriod(cid: String,dateFrom: String,dateTo:String){
        repository.deletePeriod(cid,dateFrom,dateTo)
    }

    fun getCid(cid:String,dateFrom:String,dateTo:String):String?{
        return repository.getCid(cid,dateFrom,dateTo)
    }

//    fun upload(cid:String):List<AccountTarget>{
//        return repository.upload(cid)
//    }
//
//    fun uploadSuccess(acctNo: String,rid:String){
//        repository.uploadSuccess(acctNo,rid)
//    }
//
//    fun getAll(acctNo: String,rid:String,date:String,salesFrom:String,salesTo:String):LiveData<List<Data.Target>>{
//        return repository.getAll(acctNo,rid,date,salesFrom,salesTo)
//    }
}