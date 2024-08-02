package com.example.poultry.data.resetDbase

import androidx.room.*

@Dao
interface ResetDbaseDao {

    @Query("DELETE FROM sqlite_sequence")
    fun clearPrimaryKeyIndex()

}