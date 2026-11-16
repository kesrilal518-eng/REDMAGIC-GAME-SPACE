package com.redmagic.gamespace.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.redmagic.gamespace.data.model.AppSettings
import com.redmagic.gamespace.data.model.FanSpeed
import com.redmagic.gamespace.data.model.PerformanceMode
import com.redmagic.gamespace.data.model.RgbMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "redmagic_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val PERFORMANCE_MODE = stringPreferencesKey("performance_mode")
        val FAN_SPEED = stringPreferencesKey("fan_speed")
        val CHARGE_BYPASS = booleanPreferencesKey("charge_bypass")
        val BATTERY_PROTECTION = booleanPreferencesKey("battery_protection")
        val BATTERY_LIMIT = intPreferencesKey("battery_limit")
        val ICE_COOLING = booleanPreferencesKey("ice_cooling")
        val RGB_MODE = stringPreferencesKey("rgb_mode")
        val RGB_COLOR = longPreferencesKey("rgb_color")
        val RGB_BRIGHTNESS = floatPreferencesKey("rgb_brightness")
        val SHOW_FPS = booleanPreferencesKey("show_fps")
        val SHOW_LATENCY = booleanPreferencesKey("show_latency")
        val SHOW_CPU = booleanPreferencesKey("show_cpu")
        val WELLBEING = booleanPreferencesKey("wellbeing")
        val WELLBEING_LIMIT = intPreferencesKey("wellbeing_limit")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val HAPTIC = booleanPreferencesKey("haptic")
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            performanceMode = prefs[Keys.PERFORMANCE_MODE]?.let {
                runCatching { PerformanceMode.valueOf(it) }.getOrDefault(PerformanceMode.BALANCE)
            } ?: PerformanceMode.BALANCE,
            fanSpeed = prefs[Keys.FAN_SPEED]?.let {
                runCatching { FanSpeed.valueOf(it) }.getOrDefault(FanSpeed.MEDIUM)
            } ?: FanSpeed.MEDIUM,
            chargeBypassEnabled = prefs[Keys.CHARGE_BYPASS] ?: false,
            batteryProtectionEnabled = prefs[Keys.BATTERY_PROTECTION] ?: true,
            batteryProtectionLimit = prefs[Keys.BATTERY_LIMIT] ?: 80,
            iceCoolingEnabled = prefs[Keys.ICE_COOLING] ?: true,
            rgbMode = prefs[Keys.RGB_MODE]?.let {
                runCatching { RgbMode.valueOf(it) }.getOrDefault(RgbMode.STATIC)
            } ?: RgbMode.STATIC,
            rgbColor = prefs[Keys.RGB_COLOR] ?: 0xFFE60012,
            rgbBrightness = prefs[Keys.RGB_BRIGHTNESS] ?: 0.8f,
            showFpsOverlay = prefs[Keys.SHOW_FPS] ?: true,
            showNetworkLatency = prefs[Keys.SHOW_LATENCY] ?: true,
            showCpuOverlay = prefs[Keys.SHOW_CPU] ?: false,
            digitalWellbeingEnabled = prefs[Keys.WELLBEING] ?: true,
            wellbeingLimitMinutes = prefs[Keys.WELLBEING_LIMIT] ?: 120,
            darkTheme = prefs[Keys.DARK_THEME] ?: true,
            hapticFeedback = prefs[Keys.HAPTIC] ?: true,
            selectedTheme = prefs[Keys.SELECTED_THEME] ?: "REDMAGIC Classic"
        )
    }

    suspend fun updateSettings(transform: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val current = AppSettings(
                performanceMode = prefs[Keys.PERFORMANCE_MODE]?.let {
                    runCatching { PerformanceMode.valueOf(it) }.getOrDefault(PerformanceMode.BALANCE)
                } ?: PerformanceMode.BALANCE,
                fanSpeed = prefs[Keys.FAN_SPEED]?.let {
                    runCatching { FanSpeed.valueOf(it) }.getOrDefault(FanSpeed.MEDIUM)
                } ?: FanSpeed.MEDIUM,
                chargeBypassEnabled = prefs[Keys.CHARGE_BYPASS] ?: false,
                batteryProtectionEnabled = prefs[Keys.BATTERY_PROTECTION] ?: true,
                batteryProtectionLimit = prefs[Keys.BATTERY_LIMIT] ?: 80,
                iceCoolingEnabled = prefs[Keys.ICE_COOLING] ?: true,
                rgbMode = prefs[Keys.RGB_MODE]?.let {
                    runCatching { RgbMode.valueOf(it) }.getOrDefault(RgbMode.STATIC)
                } ?: RgbMode.STATIC,
                rgbColor = prefs[Keys.RGB_COLOR] ?: 0xFFE60012,
                rgbBrightness = prefs[Keys.RGB_BRIGHTNESS] ?: 0.8f,
                showFpsOverlay = prefs[Keys.SHOW_FPS] ?: true,
                showNetworkLatency = prefs[Keys.SHOW_LATENCY] ?: true,
                showCpuOverlay = prefs[Keys.SHOW_CPU] ?: false,
                digitalWellbeingEnabled = prefs[Keys.WELLBEING] ?: true,
                wellbeingLimitMinutes = prefs[Keys.WELLBEING_LIMIT] ?: 120,
                darkTheme = prefs[Keys.DARK_THEME] ?: true,
                hapticFeedback = prefs[Keys.HAPTIC] ?: true,
                selectedTheme = prefs[Keys.SELECTED_THEME] ?: "REDMAGIC Classic"
            )
            val updated = transform(current)
            prefs[Keys.PERFORMANCE_MODE] = updated.performanceMode.name
            prefs[Keys.FAN_SPEED] = updated.fanSpeed.name
            prefs[Keys.CHARGE_BYPASS] = updated.chargeBypassEnabled
            prefs[Keys.BATTERY_PROTECTION] = updated.batteryProtectionEnabled
            prefs[Keys.BATTERY_LIMIT] = updated.batteryProtectionLimit
            prefs[Keys.ICE_COOLING] = updated.iceCoolingEnabled
            prefs[Keys.RGB_MODE] = updated.rgbMode.name
            prefs[Keys.RGB_COLOR] = updated.rgbColor
            prefs[Keys.RGB_BRIGHTNESS] = updated.rgbBrightness
            prefs[Keys.SHOW_FPS] = updated.showFpsOverlay
            prefs[Keys.SHOW_LATENCY] = updated.showNetworkLatency
            prefs[Keys.SHOW_CPU] = updated.showCpuOverlay
            prefs[Keys.WELLBEING] = updated.digitalWellbeingEnabled
            prefs[Keys.WELLBEING_LIMIT] = updated.wellbeingLimitMinutes
            prefs[Keys.DARK_THEME] = updated.darkTheme
            prefs[Keys.HAPTIC] = updated.hapticFeedback
            prefs[Keys.SELECTED_THEME] = updated.selectedTheme
        }
    }
}
