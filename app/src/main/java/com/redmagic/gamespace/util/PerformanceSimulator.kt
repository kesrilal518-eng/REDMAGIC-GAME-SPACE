package com.redmagic.gamespace.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.redmagic.gamespace.data.model.PerformanceMetrics
import com.redmagic.gamespace.data.model.PerformanceMode

/**
 * Provides real metrics only. On Redmi Note 11T 5G (and most non-REDMAGIC devices):
 * - Battery level + charging status: real
 * - Battery temperature: real (from BatteryManager)
 * - CPU / GPU / FPS / internal fan: unavailable → null
 *
 * No simulated / fake performance numbers are returned.
 */
object RealMetricsProvider {

    fun read(context: Context, mode: PerformanceMode): PerformanceMetrics {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val tempTenths = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE) ?: Int.MIN_VALUE

        val pct = if (level >= 0 && scale > 0) (level * 100 / scale) else 100
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val batteryTemp = if (tempTenths != Int.MIN_VALUE) tempTenths / 10f else null

        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
        var ramUsed: Int? = null
        var ramTotal: Int? = null
        if (am != null) {
            val mi = android.app.ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            ramTotal = (mi.totalMem / (1024 * 1024)).toInt()
            ramUsed = ((mi.totalMem - mi.availMem) / (1024 * 1024)).toInt()
        }

        return PerformanceMetrics(
            batteryLevel = pct,
            isCharging = isCharging,
            batteryTempCelsius = batteryTemp,
            cpuUsage = null,
            gpuUsage = null,
            ramUsageMb = ramUsed,
            ramTotalMb = ramTotal,
            deviceTempCelsius = batteryTemp,
            fps = null,
            fanRpm = null,
            networkLatencyMs = null,
            performanceMode = mode,
            hardwareFanAvailable = false,
            hardwareRgbAvailable = false
        )
    }
}
