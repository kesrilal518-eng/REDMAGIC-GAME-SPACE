package com.redmagic.gamespace.viewmodel

import android.app.Application
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
import com.redmagic.gamespace.util.RealMetricsProvider
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
        startMetricsCollection()
        loadInstalledGames()
        try {
            PerformanceMonitorService.start(application)
        } catch (_: Exception) { }
    }

    private fun startMetricsCollection() {
        metricsJob?.cancel()
        metricsJob = viewModelScope.launch {
            while (isActive) {
                val s = settings.value
                _metrics.value = RealMetricsProvider.read(getApplication(), s.performanceMode)
                delay(2000)
            }
        }
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

    fun setBatteryProtection(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(batteryProtectionEnabled = enabled) }
        }
    }

    fun setBatteryProtectionLimit(limit: Int) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(batteryProtectionLimit = limit.coerceIn(50, 100)) }
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
            settingsRepo.updateSettings { it.copy(rgbBrightness = brightness.coerceIn(0.1f, 1f)) }
        }
    }

    fun setShowFpsOverlay(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(showFpsOverlay = enabled) }
        }
    }

    fun setShowNetworkLatency(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(showNetworkLatency = enabled) }
        }
    }

    fun setDigitalWellbeing(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(digitalWellbeingEnabled = enabled) }
        }
    }

    fun setWellbeingLimit(minutes: Int) {
        viewModelScope.launch {
            settingsRepo.updateSettings { it.copy(wellbeingLimitMinutes = minutes.coerceIn(30, 480)) }
        }
    }

    fun saveGameProfile(profile: GameProfile) {
        viewModelScope.launch {
            gameRepo.saveProfile(profile)
        }
    }

    fun createProfileForGame(game: InstalledGame) {
        viewModelScope.launch {
            val existing = profiles.value.find { it.packageName == game.packageName }
            if (existing == null) {
                gameRepo.saveProfile(
                    GameProfile(
                        packageName = game.packageName,
                        appName = game.appName,
                        performanceMode = settings.value.performanceMode.name,
                        fanSpeed = settings.value.fanSpeed.name,
                        rgbMode = settings.value.rgbMode.name,
                        rgbColor = settings.value.rgbColor
                    )
                )
            }
        }
    }

    fun deleteProfile(profile: GameProfile) {
        viewModelScope.launch {
            gameRepo.deleteProfile(profile)
        }
    }
}
