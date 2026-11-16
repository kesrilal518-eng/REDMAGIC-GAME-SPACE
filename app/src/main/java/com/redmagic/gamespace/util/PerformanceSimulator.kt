package com.redmagic.gamespace.util

import com.redmagic.gamespace.data.model.FanSpeed
import com.redmagic.gamespace.data.model.PerformanceMetrics
import com.redmagic.gamespace.data.model.PerformanceMode
import kotlin.math.max
import kotlin.random.Random

object PerformanceSimulator {

    private var currentCpu = 35f
    private var currentGpu = 25f
    private var currentTemp = 38f
    private var currentFps = 60
    private var currentLatency = 28

    fun nextMetrics(
        mode: PerformanceMode,
        fanSpeed: FanSpeed,
        iceCooling: Boolean,
        batteryLevel: Int,
        isCharging: Boolean
    ): PerformanceMetrics {
        val loadFactor = when (mode) {
            PerformanceMode.ECO -> 0.55f
            PerformanceMode.BALANCE -> 0.75f
            PerformanceMode.PERFORMANCE -> 0.92f
            PerformanceMode.BEAST -> 1.0f
        }

        val coolingFactor = when {
            !iceCooling || fanSpeed == FanSpeed.OFF -> 1.15f
            fanSpeed == FanSpeed.LOW -> 1.05f
            fanSpeed == FanSpeed.MEDIUM -> 0.95f
            fanSpeed == FanSpeed.HIGH -> 0.85f
            fanSpeed == FanSpeed.TURBO -> 0.75f
            else -> 1.0f
        }

        currentCpu = smooth(currentCpu, 20f + loadFactor * 70f + Random.nextFloat() * 12f - 6f)
        currentGpu = smooth(currentGpu, 15f + loadFactor * 75f + Random.nextFloat() * 15f - 7f)
        currentTemp = smooth(
            currentTemp,
            (36f + loadFactor * 18f * coolingFactor) + Random.nextFloat() * 3f - 1.5f
        )
        currentFps = when (mode) {
            PerformanceMode.ECO -> Random.nextInt(45, 61)
            PerformanceMode.BALANCE -> Random.nextInt(55, 91)
            PerformanceMode.PERFORMANCE -> Random.nextInt(85, 121)
            PerformanceMode.BEAST -> Random.nextInt(110, 145)
        }
        currentLatency = max(12, (35 * (1.2f - loadFactor * 0.3f) + Random.nextInt(-8, 12)).toInt())

        val ramUsed = (2800 + loadFactor * 2200 + Random.nextInt(-150, 200)).toInt()
        val chargingTemp = if (isCharging) {
            currentTemp + 2f + Random.nextFloat() * 4f
        } else {
            currentTemp - 1f
        }

        return PerformanceMetrics(
            cpuUsage = currentCpu.coerceIn(5f, 99f),
            gpuUsage = currentGpu.coerceIn(3f, 99f),
            ramUsageMb = ramUsed.coerceIn(1800, 5500),
            ramTotalMb = 6144,
            temperatureCelsius = currentTemp.coerceIn(32f, 55f),
            fps = currentFps,
            batteryLevel = batteryLevel,
            isCharging = isCharging,
            chargingTemp = chargingTemp.coerceIn(30f, 48f),
            fanRpm = fanSpeed.rpm + Random.nextInt(-80, 120).coerceAtLeast(0),
            networkLatencyMs = currentLatency,
            performanceMode = mode
        )
    }

    private fun smooth(current: Float, target: Float): Float {
        val delta = (target - current) * 0.25f
        return current + delta
    }
}
