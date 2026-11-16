package com.redmagic.gamespace.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PerformanceMode(val displayName: String, val description: String) {
    ECO("Eco", "Battery focused, reduced clocks"),
    BALANCE("Balance", "Optimal mix of performance & battery"),
    PERFORMANCE("Performance", "High clocks, sustained gaming"),
    BEAST("Beast Mode", "Maximum performance, aggressive cooling")
}

enum class FanSpeed(val displayName: String, val rpm: Int, val level: Int) {
    OFF("Off", 0, 0),
    LOW("Low", 2500, 1),
    MEDIUM("Medium", 4500, 2),
    HIGH("High", 6500, 3),
    TURBO("Turbo", 8500, 4)
}

enum class RgbMode(val displayName: String) {
    OFF("Off"),
    STATIC("Static"),
    BREATHING("Breathing"),
    RAINBOW("Rainbow"),
    WAVE("Wave"),
    MUSIC("Music Sync"),
    CUSTOM("Custom")
}

@Entity(tableName = "game_profiles")
data class GameProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val performanceMode: String = PerformanceMode.BALANCE.name,
    val fanSpeed: String = FanSpeed.MEDIUM.name,
    val chargeBypass: Boolean = false,
    val fpsCounter: Boolean = true,
    val networkLatency: Boolean = true,
    val rgbMode: String = RgbMode.STATIC.name,
    val rgbColor: Long = 0xFFE60012,
    val touchSensitivity: Float = 1.0f,
    val resolutionScale: Float = 1.0f,
    val isFavorite: Boolean = false,
    val lastPlayed: Long = System.currentTimeMillis()
)

@Entity(tableName = "performance_logs")
data class PerformanceLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val cpuUsage: Float,
    val gpuUsage: Float,
    val ramUsage: Float,
    val temperature: Float,
    val fps: Int,
    val batteryLevel: Int,
    val fanRpm: Int,
    val performanceMode: String
)

data class AppSettings(
    val performanceMode: PerformanceMode = PerformanceMode.BALANCE,
    val fanSpeed: FanSpeed = FanSpeed.MEDIUM,
    val chargeBypassEnabled: Boolean = false,
    val batteryProtectionEnabled: Boolean = true,
    val batteryProtectionLimit: Int = 80,
    val iceCoolingEnabled: Boolean = true,
    val rgbMode: RgbMode = RgbMode.STATIC,
    val rgbColor: Long = 0xFFE60012,
    val rgbBrightness: Float = 0.8f,
    val showFpsOverlay: Boolean = true,
    val showNetworkLatency: Boolean = true,
    val showCpuOverlay: Boolean = false,
    val digitalWellbeingEnabled: Boolean = true,
    val wellbeingLimitMinutes: Int = 120,
    val darkTheme: Boolean = true,
    val hapticFeedback: Boolean = true,
    val selectedTheme: String = "REDMAGIC Classic"
)

/**
 * Real-time metrics. Only battery level, charging state and battery temperature
 * are guaranteed real on non-REDMAGIC hardware (e.g. Redmi Note 11T 5G).
 * CPU / GPU / FPS / fan RPM are null → UI shows "N/A" or "Unavailable".
 */
data class PerformanceMetrics(
    val batteryLevel: Int = 100,
    val isCharging: Boolean = false,
    val batteryTempCelsius: Float? = null,
    val cpuUsage: Float? = null,
    val gpuUsage: Float? = null,
    val ramUsageMb: Int? = null,
    val ramTotalMb: Int? = null,
    val deviceTempCelsius: Float? = null,
    val fps: Int? = null,
    val fanRpm: Int? = null,
    val networkLatencyMs: Int? = null,
    val performanceMode: PerformanceMode = PerformanceMode.BALANCE,
    val hardwareFanAvailable: Boolean = false,
    val hardwareRgbAvailable: Boolean = false
)

data class InstalledGame(
    val packageName: String,
    val appName: String,
    val isGame: Boolean = true,
    val hasProfile: Boolean = false
)
