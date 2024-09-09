package com.example.poultry2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.poultry2.data.accountTarget.AccountTarget
import com.example.poultry2.data.accountTarget.AccountTargetDao
import com.example.poultry2.data.address.Address
import com.example.poultry2.data.address.AddressDao
import com.example.poultry2.data.ar.Ar
import com.example.poultry2.data.ar.ArDao
import com.example.poultry2.data.dspTarget.DspTarget
import com.example.poultry2.data.dspTarget.DspTargetDao
import com.example.poultry2.data.inventory.Inventory
import com.example.poultry2.data.inventory.InventoryDao
import com.example.poultry2.data.resetDbase.ResetDbaseDao
import com.example.poultry2.data.siv.Siv
import com.example.poultry2.data.siv.SivDao
import com.example.poultry2.data.sivTarget.SivTarget
import com.example.poultry2.data.sivTarget.SivTargetDao
import com.example.poultry2.data.sov.Sov
import com.example.poultry2.data.sov.SovDao
import com.example.poultry2.data.sovPromoDisc.SovPromoDisc
import com.example.poultry2.data.sovPromoDisc.SovPromoDiscDao
import com.example.poultry2.data.sovSmis.SovSmis
import com.example.poultry2.data.sovSmis.SovSmisDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [
    Siv::class,
    Sov::class,
    SovSmis::class,
    Address::class,
    SivTarget::class,
    DspTarget::class,
    SovPromoDisc::class,
    Ar::class,
    AccountTarget::class,
    Inventory::class


], version = 31, exportSchema = false)


abstract class AppDatabase : RoomDatabase() {
    abstract fun resetDbaseDao():ResetDbaseDao
    abstract fun sivDao():SivDao
    abstract fun sovDao(): SovDao
    abstract fun sovSmisDao(): SovSmisDao
    abstract fun addressDao(): AddressDao
    abstract fun sivTargetDao():SivTargetDao
    abstract fun dspTargetDao():DspTargetDao
    abstract fun sovPromoDiscDao():SovPromoDiscDao
    abstract fun arDao():ArDao
    abstract fun accountTargetDao():AccountTargetDao
    abstract fun inventoryDao():InventoryDao

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {
                scope.launch {
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "poultry"
                )
                    .addCallback(
                        DatabaseCallback(
                            scope
                        )
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }


        }

        fun RoomDatabase.getDBVersion(): Int {
            return this.openHelper.readableDatabase.version
        }

        fun reset(){
            INSTANCE?.clearAllTables()
        }
    }


    


}