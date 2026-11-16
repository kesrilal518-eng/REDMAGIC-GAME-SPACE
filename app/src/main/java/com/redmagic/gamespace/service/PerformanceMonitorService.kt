package com.redmagic.gamespace.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.redmagic.gamespace.MainActivity
import com.redmagic.gamespace.R
import com.redmagic.gamespace.data.database.AppDatabase
import com.redmagic.gamespace.data.model.PerformanceLog
import com.redmagic.gamespace.data.repository.SettingsRepository
import com.redmagic.gamespace.util.PerformanceSimulator
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
    private var monitorJob: Job? = null

    private lateinit var settingsRepo: SettingsRepository
    private lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        settingsRepo = SettingsRepository(applicationContext)
        database = AppDatabase.getInstance(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification("Monitoring performance..."))
        startMonitoring()
        return START_STICKY
    }

    private fun startMonitoring() {
        monitorJob?.cancel()
        monitorJob = scope.launch {
            while (isActive) {
                val settings = settingsRepo.settingsFlow.first()
                val battery = readBattery()
                val metrics = PerformanceSimulator.nextMetrics(
                    mode = settings.performanceMode,
                    fanSpeed = settings.fanSpeed,
                    iceCooling = settings.iceCoolingEnabled,
                    batteryLevel = battery.first,
                    isCharging = battery.second
                )

                if (System.currentTimeMillis() % 30000 < 5000) {
                    database.performanceLogDao().insert(
                        PerformanceLog(
                            cpuUsage = metrics.cpuUsage,
                            gpuUsage = metrics.gpuUsage,
                            ramUsage = metrics.ramUsageMb.toFloat(),
                            temperature = metrics.temperatureCelsius,
                            fps = metrics.fps,
                            batteryLevel = metrics.batteryLevel,
                            fanRpm = metrics.fanRpm,
                            performanceMode = metrics.performanceMode.name
                        )
                    )
                }

                val status = "CPU ${metrics.cpuUsage.toInt()}%  |  ${metrics.temperatureCelsius.toInt()}C  |  ${metrics.fps} FPS"
                updateNotification(status)
                delay(5000)
            }
        }
    }

    private fun readBattery(): Pair<Int, Boolean> {
        val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 100
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val pct = if (scale > 0) (level * 100 / scale) else 100
        return pct to isCharging
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_performance),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Real-time gaming performance monitoring"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(content: String): Notification {
        val pending = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("REDMAGIC Game Space")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pending)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(content: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID, buildNotification(content))
    }

    override fun onDestroy() {
        monitorJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "performance_monitor"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            context.startForegroundService(Intent(context, PerformanceMonitorService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, PerformanceMonitorService::class.java))
        }
    }
}
