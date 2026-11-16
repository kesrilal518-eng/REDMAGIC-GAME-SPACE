package com.redmagic.gamespace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redmagic.gamespace.ui.components.PremiumCard
import com.redmagic.gamespace.ui.components.SectionHeader
import com.redmagic.gamespace.ui.components.SettingRow
import com.redmagic.gamespace.ui.theme.RedMagicBlack
import com.redmagic.gamespace.ui.theme.RedMagicRed
import com.redmagic.gamespace.ui.theme.TextMuted
import com.redmagic.gamespace.ui.theme.TextPrimary
import com.redmagic.gamespace.ui.theme.TextSecondary
import com.redmagic.gamespace.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(RedMagicBlack)
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("REDMAGIC", style = MaterialTheme.typography.labelLarge, color = RedMagicRed, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
        Text("SETTINGS", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text("Overlay, wellbeing & preferences", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(20.dp))
        SectionHeader("OVERLAY")
        SettingRow(title = "FPS Overlay Preference", subtitle = "Show FPS when available", checked = settings.showFpsOverlay, onCheckedChange = { viewModel.setShowFpsOverlay(it) }, icon = Icons.Default.Speed)
        Spacer(Modifier.height(10.dp))
        SettingRow(title = "Network Latency Preference", subtitle = "Show latency widget when available", checked = settings.showNetworkLatency, onCheckedChange = { viewModel.setShowNetworkLatency(it) }, icon = Icons.Default.Visibility)

        Spacer(Modifier.height(20.dp))
        SectionHeader("DIGITAL WELLBEING")
        SettingRow(title = "Session Reminders", subtitle = "Notify after continuous play", checked = settings.digitalWellbeingEnabled, onCheckedChange = { viewModel.setDigitalWellbeing(it) }, icon = Icons.Default.Timer)
        if (settings.digitalWellbeingEnabled) {
            Spacer(Modifier.height(12.dp))
            PremiumCard {
                Text("Limit: ${settings.wellbeingLimitMinutes} minutes", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Slider(
                    value = settings.wellbeingLimitMinutes.toFloat(),
                    onValueChange = { viewModel.setWellbeingLimit(it.toInt()) },
                    valueRange = 30f..480f,
                    steps = 14,
                    colors = SliderDefaults.colors(thumbColor = RedMagicRed, activeTrackColor = RedMagicRed, inactiveTrackColor = Color(0xFF333333))
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        SectionHeader("ABOUT")
        PremiumCard {
            Text("REDMAGIC GAME SPACE", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text("Independent open-source utility", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Text("Target: Redmi Note 11T 5G & similar", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Text("Not affiliated with Nubia / REDMAGIC / ZTE", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            Text("Metrics: real battery + temperature only. CPU/GPU/FPS/fan shown as Unavailable when hardware is not present.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        Spacer(Modifier.height(32.dp))
    }
}
