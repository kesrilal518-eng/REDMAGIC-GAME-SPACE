package com.redmagic.gamespace.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.redmagic.gamespace.data.dao.GameProfileDao
import com.redmagic.gamespace.data.dao.PerformanceLogDao
import com.redmagic.gamespace.data.model.GameProfile
import com.redmagic.gamespace.data.model.InstalledGame
import com.redmagic.gamespace.data.model.PerformanceLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(
    private val profileDao: GameProfileDao,
    private val logDao: PerformanceLogDao,
    private val context: Context
) {
    fun getAllProfiles(): Flow<List<GameProfile>> = profileDao.getAllProfiles()

    fun getFavoriteProfiles(): Flow<List<GameProfile>> = profileDao.getFavoriteProfiles()

    suspend fun getProfile(packageName: String): GameProfile? =
        profileDao.getProfileByPackage(packageName)

    suspend fun saveProfile(profile: GameProfile): Long {
        val existing = profileDao.getProfileByPackage(profile.packageName)
        return if (existing != null) {
            profileDao.update(profile.copy(id = existing.id))
            existing.id
        } else {
            profileDao.insert(profile)
        }
    }

    suspend fun deleteProfile(profile: GameProfile) = profileDao.delete(profile)

    suspend fun insertLog(log: PerformanceLog) = logDao.insert(log)

    fun getRecentLogs(limit: Int = 50): Flow<List<PerformanceLog>> =
        logDao.getRecentLogs(limit)

    suspend fun cleanupOldLogs(daysToKeep: Int = 7) {
        val cutoff = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000)
        logDao.deleteOlderThan(cutoff)
    }

    suspend fun getInstalledGames(): List<InstalledGame> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)

        val games = mutableListOf<InstalledGame>()
        val seen = mutableSetOf<String>()

        for (info in resolveInfos) {
            val packageName = info.activityInfo.packageName
            if (packageName == context.packageName || seen.contains(packageName)) continue
            seen.add(packageName)

            try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                val isGame = (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0 ||
                        appInfo.category == ApplicationInfo.CATEGORY_GAME ||
                        isLikelyGame(packageName, pm.getApplicationLabel(appInfo).toString())

                if (isGame) {
                    val hasProfile = profileDao.getProfileByPackage(packageName) != null
                    games.add(
                        InstalledGame(
                            packageName = packageName,
                            appName = pm.getApplicationLabel(appInfo).toString(),
                            isGame = true,
                            hasProfile = hasProfile
                        )
                    )
                }
            } catch (_: Exception) {
            }
        }

        games.sortedBy { it.appName.lowercase() }
    }

    private fun isLikelyGame(packageName: String, label: String): Boolean {
        val keywords = listOf(
            "game", "play", "pubg", "cod", "mlbb", "mobile legends", "genshin",
            "honkai", "free fire", "ff", "roblox", "minecraft", "call of duty",
            "arena", "battle", "racing", "soccer", "football", "nba", "fifa"
        )
        val lowerPkg = packageName.lowercase()
        val lowerLabel = label.lowercase()
        return keywords.any { lowerPkg.contains(it) || lowerLabel.contains(it) }
    }
}
