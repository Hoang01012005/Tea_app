package com.example.teacare.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.teacare.data.local.dao.CareTaskDao
import com.example.teacare.data.local.dao.ScanHistoryDao
import com.example.teacare.data.local.entity.CareTaskEntity
import com.example.teacare.data.local.entity.ScanHistoryEntity

@Database(
    entities = [
        CareTaskEntity::class,
        ScanHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TeaCareDatabase : RoomDatabase() {
    abstract fun careTaskDao(): CareTaskDao
    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: TeaCareDatabase? = null

        fun getDatabase(context: Context): TeaCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TeaCareDatabase::class.java,
                    "teacare_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
