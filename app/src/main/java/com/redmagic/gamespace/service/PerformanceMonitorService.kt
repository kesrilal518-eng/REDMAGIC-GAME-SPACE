package com.redmagic.gamespace.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.redmagic.gamespace.MainActivity
import com.redmagic.gamespace.R
import com.redmagic.gamespace.data.database.AppDatabase
import com.redmagic.gamespace.data.model.PerformanceLog
import com.redmagic.gamespace.data.repository.SettingsRepository
import com.redmagic.gamespace.util.RealMetricsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PerformanceMonitorService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIFICATION_ID, buildNotification("Monitoring…"))
        startLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    private fun startLoop() {
        job?.cancel()
        job = scope.launch {
            val settingsRepo = SettingsRepository(applicationContext)
            val db = AppDatabase.getInstance(applicationContext)
            while (isActive) {
                try {
                    val settings = settingsRepo.settingsFlow.first()
                    val metrics = RealMetricsProvider.read(applicationContext, settings.performanceMode)
                    val tempStr = metrics.batteryTempCelsius?.let { "%.1f°C".format(it) } ?: "N/A"
                    val status = "Battery ${metrics.batteryLevel}%  |  $tempStr  |  ${settings.performanceMode.displayName}"
                    getSystemService(NotificationManager::class.java)
                        .notify(NOTIFICATION_ID, buildNotification(status))
                    db.performanceLogDao().insert(
                        PerformanceLog(
                            cpuUsage = 0f,
                            gpuUsage = 0f,
                            ramUsage = metrics.ramUsageMb?.toFloat() ?: 0f,
                            temperature = metrics.batteryTempCelsius ?: 0f,
                            fps = 0,
                            batteryLevel = metrics.batteryLevel,
                            fanRpm = 0,
                            performanceMode = settings.performanceMode.name
                        )
                    )
                } catch (_: Exception) { }
                delay(5000)
            }
        }
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "Performance Monitor", NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(content: String): Notification {
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("REDMAGIC Game Space")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pi)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onDestroy() {
        job?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "perf_monitor"
        private const val NOTIFICATION_ID = 1002
        fun start(context: android.content.Context) {
            context.startForegroundService(Intent(context, PerformanceMonitorService::class.java))
        }
    }
}
