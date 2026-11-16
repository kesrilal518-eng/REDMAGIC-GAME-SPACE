package com.redmagic.gamespace.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.redmagic.gamespace.data.model.GameProfile
import com.redmagic.gamespace.data.model.PerformanceLog
import kotlinx.coroutines.flow.Flow

@Dao
interface GameProfileDao {
    @Query("SELECT * FROM game_profiles ORDER BY lastPlayed DESC")
    fun getAllProfiles(): Flow<List<GameProfile>>

    @Query("SELECT * FROM game_profiles WHERE isFavorite = 1 ORDER BY appName ASC")
    fun getFavoriteProfiles(): Flow<List<GameProfile>>

    @Query("SELECT * FROM game_profiles WHERE packageName = :packageName LIMIT 1")
    suspend fun getProfileByPackage(packageName: String): GameProfile?

    @Query("SELECT * FROM game_profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): GameProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: GameProfile): Long

    @Update
    suspend fun update(profile: GameProfile)

    @Delete
    suspend fun delete(profile: GameProfile)

    @Query("DELETE FROM game_profiles WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)
}

@Dao
interface PerformanceLogDao {
    @Query("SELECT * FROM performance_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<PerformanceLog>>

    @Query("SELECT * FROM performance_logs WHERE timestamp >= :since ORDER BY timestamp ASC")
    fun getLogsSince(since: Long): Flow<List<PerformanceLog>>

    @Insert
    suspend fun insert(log: PerformanceLog): Long

    @Query("DELETE FROM performance_logs WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("SELECT AVG(temperature) FROM performance_logs WHERE timestamp >= :since")
    suspend fun getAverageTemperature(since: Long): Float?
}
