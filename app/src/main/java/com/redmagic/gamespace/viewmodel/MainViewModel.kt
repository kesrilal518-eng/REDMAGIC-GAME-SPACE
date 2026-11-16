package com.redmagic.gamespace.viewmodel

import android.app.Application
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.redmagic.gamespace.data.database.AppDatabase
import com.redmagic.gamespace.data.model.AppSettings
import com.redmagic.gamespace.data.model.FanSpeed
import com.redmagic.gamespace.data.model.GameProfile
import com.redmagic.gamespace.data.model.InstalledGame
import com.redmagic.gamespace.data.model.PerformanceMetrics
import com.redmagic.gamespace.data.model.PerformanceMode
import com.redmagic.gamespace.data.model.RgbMode
import com.redmagic.gamespace.data.repository.GameRepository
import com.redmagic.gamespace.data.repository.SettingsRepository
import com.redmagic.gamespace.service.PerformanceMonitorService
import com.redmagic.gamespace.util.PerformanceSimulator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val gameRepo = GameRepository(
        database.gameProfileDao(),
        database.performanceLogDao(),
        application
    )
    private val settingsRepo = SettingsRepository(application)

    val settings: StateFlow<AppSettings> = settingsRepo.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val profiles: StateFlow<List<GameProfile>> = gameRepo.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _metrics = MutableStateFlow(PerformanceMetrics())
    val metrics: StateFlow<PerformanceMetrics> = _metrics.asStateFlow()

    private val _installedGames = MutableStateFlow<List<InstalledGame>>(emptyList())
    val installedGames: StateFlow<List<InstalledGame>> = _installedGames.asStateFlow()

    private val _isLoadingGames = MutableStateFlow(false)
    val isLoadingGames: StateFlow<Boolean> = _isLoadingGames.asStateFlow()

    private var metricsJob: Job? = null

    init {
        startMetricsSimulation()
        loadInstalledGames()
        try {
            PerformanceMonitorService.start(application)
        } catch (_: Exception) {
        }
    }

    private fun startMetricsSimulation() {
        metricsJob?.cancel()
        metricsJob = viewModelScope.launch {
            while (isActive) {
                val s = settings.value
                val battery = readBattery()
                _metrics.value = PerformanceSimulator.nextMetrics(
                    mode = s.performanceMode,
                    fanSpeed = s.fanSpeed,
                    iceCooling = s.iceCoolingEnabled,
                    batteryLevel = battery.first,
                    isCharging = battery.second
                )
                delay(1500)
            }
        }
    }

    private fun readBattery(): Pair<Int, Boolean> {
        val intent = getApplication<Application>().registerReceiver(
            null,
            IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
        )
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 100
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val pct = if (scale > 0) (level * 100 / scale) else 100
        return pct to isCharging
    }

    fun loadInstalledGames() {
        viewModelScope.launch {
            _isLoadingGames.value = true
            _installedGames.value = gameRepo.getInstalledGames()
            _isLoadingGames.value = false
        }
    }

    fun setPerformanceMode(mode: PerformanceMode) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(performanceMode = mode) }
        }
    }

    fun setFanSpeed(speed: FanSpeed) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(fanSpeed = speed) }
        }
    }

    fun setIceCooling(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(iceCoolingEnabled = enabled) }
        }
    }

    fun setChargeBypass(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(chargeBypassEnabled = enabled) }
        }
    }

    fun setBatteryProtection(enabled: Boolean, limit: Int = 80) {
        viewModelScope.launch {
            settingsRepo.updateSettings {
                it.copy(batteryProtectionEnabled = enabled, batteryProtectionLimit = limit)
            }
        }
    }

    fun setRgbMode(mode: RgbMode) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(rgbMode = mode) }
        }
    }

    fun setRgbColor(color: Long) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(rgbColor = color) }
        }
    }

    fun setRgbBrightness(brightness: Float) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(rgbBrightness = brightness) }
        }
    }

    fun setOverlayOptions(fps: Boolean, latency: Boolean, cpu: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateSettings {
                it.copy(showFpsOverlay = fps, showNetworkLatency = latency, showCpuOverlay = cpu)
            }
        }
    }

    fun setWellbeing(enabled: Boolean, limitMinutes: Int = 120) {
        viewModelScope.launch {
            settingsRepo.updateSettings {
                it.copy(digitalWellbeingEnabled = enabled, wellbeingLimitMinutes = limitMinutes)
            }
        }
    }

    fun setTheme(themeName: String) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(selectedTheme = themeName) }
        }
    }

    fun saveGameProfile(profile: GameProfile) {
        viewModelScope.launch { gameRepo.saveProfile(profile) }
    }

    fun deleteGameProfile(profile: GameProfile) {
        viewModelScope.launch { gameRepo.deleteProfile(profile) }
    }

    fun createProfileForGame(game: InstalledGame) {
        viewModelScope.launch {
            val profile = GameProfile(
                packageName = game.packageName,
                appName = game.appName,
                performanceMode = settings.value.performanceMode.name,
                fanSpeed = settings.value.fanSpeed.name,
                chargeBypass = settings.value.chargeBypassEnabled,
                fpsCounter = settings.value.showFpsOverlay,
                networkLatency = settings.value.showNetworkLatency,
                rgbMode = settings.value.rgbMode.name,
                rgbColor = settings.value.rgbColor
            )
            gameRepo.saveProfile(profile)
        }
    }

    override fun onCleared() {
        metricsJob?.cancel()
        super.onCleared()
    }
}
