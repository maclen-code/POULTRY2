package com.example.poultry2.data.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.poultry2.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository

    init {

        val dao = AppDatabase.getDatabase(application,viewModelScope).inventoryDao()
        repository = InventoryRepository(dao)
    }

    fun insert(inventory: Inventory) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(inventory)
    }

    fun deleteAll(cid:String)  {
        repository.deleteAll(cid)
    }

    fun inventoryProduct(cid:String):List<Inventory>{
        return repository.inventoryProduct(cid)
    }

}