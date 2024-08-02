package com.example.poultry.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.poultry.data.resetDbase.ResetDbaseDao
import com.example.poultry.data.siv.Siv
import com.example.poultry.data.siv.SivDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [
    Siv::class
], version = 1, exportSchema = false)


abstract class AppDatabase : RoomDatabase() {
    abstract fun sivDao():SivDao

    abstract fun resetDbaseDao():ResetDbaseDao

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
                    "bmeg"
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