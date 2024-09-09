package com.example.poultry2.data.inventory

import androidx.room.*

@Dao
interface InventoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(inventory: Inventory)

    @Query("Delete  FROM inventory where cid=:cid")
    fun deleteAll(cid:String)

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Query("Select * from inventory x " +
            "where cid=:cid  " +
            "order by x.bunitId,x.catId ")
    fun inventoryProduct(cid:String):List<Inventory>

    ///////////////////////////////////////////////////////////////////////////////////////////////

}