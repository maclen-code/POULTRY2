package com.example.poultry2.data.inventory



class InventoryRepository(private val inventoryDao: InventoryDao) {


    fun insert(inventory: Inventory)  {
        inventoryDao.insert(inventory)
    }

    fun deleteAll(cid: String){
        inventoryDao.deleteAll(cid)
    }

    fun inventoryProduct(cid:String):List<Inventory>{
       return inventoryDao.inventoryProduct(cid)
    }

}