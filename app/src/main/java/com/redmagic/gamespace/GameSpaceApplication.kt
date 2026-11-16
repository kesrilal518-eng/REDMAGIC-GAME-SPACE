package com.redmagic.gamespace

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.redmagic.gamespace.data.database.AppDatabase

class GameSpaceApplication : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val nm = getSystemService(NotificationManager::class.java)

        val channels = listOf(
            NotificationChannel(
                "performance_monitor",
                getString(R.string.notification_channel_performance),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Real-time performance monitoring"
                setShowBadge(false)
            },
            NotificationChannel(
                "game_overlay",
                getString(R.string.notification_channel_overlay),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Game overlay service"
                setShowBadge(false)
            },
            NotificationChannel(
                "wellbeing",
                getString(R.string.notification_channel_wellbeing),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Digital wellbeing reminders"
            }
        )
        channels.forEach { nm.createNotificationChannel(it) }
    }
}
