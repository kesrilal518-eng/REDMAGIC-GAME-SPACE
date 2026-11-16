package com.redmagic.gamespace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.redmagic.gamespace.ui.components.SectionTitle
import com.redmagic.gamespace.ui.components.SettingSwitchRow
import com.redmagic.gamespace.ui.theme.RedMagicCard
import com.redmagic.gamespace.ui.theme.RedMagicDark
import com.redmagic.gamespace.ui.theme.RedMagicRed
import com.redmagic.gamespace.ui.theme.TextPrimary
import com.redmagic.gamespace.ui.theme.TextSecondary
import com.redmagic.gamespace.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(RedMagicDark)
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.displayMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text("Overlay | Wellbeing | About", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(24.dp))
        SectionTitle("Game Overlay")
        SettingSwitchRow(
            title = "FPS Counter",
            subtitle = "Show frames-per-second in overlay",
            checked = settings.showFpsOverlay,
            onCheckedChange = { viewModel.setOverlayOptions(it, settings.showNetworkLatency, settings.showCpuOverlay) },
            icon = Icons.Default.Visibility
        )
        Spacer(Modifier.height(10.dp))
        SettingSwitchRow(
            title = "Network Latency",
            subtitle = "Show ping in overlay",
            checked = settings.showNetworkLatency,
            onCheckedChange = { viewModel.setOverlayOptions(settings.showFpsOverlay, it, settings.showCpuOverlay) },
            icon = Icons.Default.Visibility
        )
        Spacer(Modifier.height(10.dp))
        SettingSwitchRow(
            title = "CPU Overlay",
            subtitle = "Show CPU usage in overlay",
            checked = settings.showCpuOverlay,
            onCheckedChange = { viewModel.setOverlayOptions(settings.showFpsOverlay, settings.showNetworkLatency, it) },
            icon = Icons.Default.Visibility
        )

        Spacer(Modifier.height(20.dp))
        SectionTitle("Digital Wellbeing")
        SettingSwitchRow(
            title = "Wellbeing Reminder",
            subtitle = "Remind after long gaming sessions",
            checked = settings.digitalWellbeingEnabled,
            onCheckedChange = { viewModel.setWellbeing(it, settings.wellbeingLimitMinutes) },
            icon = Icons.Default.AccessTime
        )

        if (settings.digitalWellbeingEnabled) {
            Spacer(Modifier.height(12.dp))
            Card(colors = CardDefaults.cardColors(containerColor = RedMagicCard), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Session limit: ${settings.wellbeingLimitMinutes} min", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Slider(
                        value = settings.wellbeingLimitMinutes.toFloat(),
                        onValueChange = { viewModel.setWellbeing(true, it.toInt()) },
                        valueRange = 30f..240f,
                        steps = 13,
                        colors = SliderDefaults.colors(thumbColor = RedMagicRed, activeTrackColor = RedMagicRed)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        SectionTitle("About")
        Card(colors = CardDefaults.cardColors(containerColor = RedMagicCard), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("REDMAGIC GAME SPACE", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("Version 1.0.0", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Inspired by REDMAGIC 10 Pro Game Space. Built with Kotlin and Jetpack Compose. Independent community project, not affiliated with Nubia/REDMAGIC/ZTE.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
