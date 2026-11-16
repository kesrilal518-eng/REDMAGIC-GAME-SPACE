package com.redmagic.gamespace.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.redmagic.gamespace.data.dao.GameProfileDao
import com.redmagic.gamespace.data.dao.PerformanceLogDao
import com.redmagic.gamespace.data.model.GameProfile
import com.redmagic.gamespace.data.model.PerformanceLog

@Database(
    entities = [GameProfile::class, PerformanceLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameProfileDao(): GameProfileDao
    abstract fun performanceLogDao(): PerformanceLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "redmagic_gamespace.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
