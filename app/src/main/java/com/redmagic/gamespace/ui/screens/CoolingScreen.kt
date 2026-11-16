package com.redmagic.gamespace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
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
import com.redmagic.gamespace.data.model.FanSpeed
import com.redmagic.gamespace.ui.components.MetricCard
import com.redmagic.gamespace.ui.components.ModeChip
import com.redmagic.gamespace.ui.components.SectionTitle
import com.redmagic.gamespace.ui.components.SettingSwitchRow
import com.redmagic.gamespace.ui.theme.RedMagicCard
import com.redmagic.gamespace.ui.theme.RedMagicCyan
import com.redmagic.gamespace.ui.theme.RedMagicDark
import com.redmagic.gamespace.ui.theme.RedMagicGreen
import com.redmagic.gamespace.ui.theme.RedMagicOrange
import com.redmagic.gamespace.ui.theme.RedMagicRed
import com.redmagic.gamespace.ui.theme.TextPrimary
import com.redmagic.gamespace.ui.theme.TextSecondary
import com.redmagic.gamespace.viewmodel.MainViewModel

@Composable
fun CoolingScreen(viewModel: MainViewModel) {
    val metrics by viewModel.metrics.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(RedMagicDark)
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("ICE Cooling", style = MaterialTheme.typography.displayMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text("Active cooling and thermal management", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(24.dp))
        SettingSwitchRow(
            title = "ICE Cooling System",
            subtitle = "Enable intelligent thermal control",
            checked = settings.iceCoolingEnabled,
            onCheckedChange = { viewModel.setIceCooling(it) },
            icon = Icons.Default.AcUnit
        )

        Spacer(Modifier.height(20.dp))
        SectionTitle("Fan Control")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FanSpeed.entries.forEach { speed ->
                ModeChip(
                    label = speed.displayName,
                    selected = settings.fanSpeed == speed,
                    onClick = { viewModel.setFanSpeed(speed) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = RedMagicCard), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current RPM", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Text("${metrics.fanRpm}", style = MaterialTheme.typography.displayLarge, color = RedMagicCyan, fontWeight = FontWeight.Bold)
                Text("Target: ${settings.fanSpeed.rpm} RPM", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(12.dp))
                Slider(
                    value = settings.fanSpeed.level.toFloat(),
                    onValueChange = { v ->
                        val nearest = FanSpeed.entries.minByOrNull { kotlin.math.abs(it.level - v.toInt()) } ?: FanSpeed.MEDIUM
                        viewModel.setFanSpeed(nearest)
                    },
                    valueRange = 0f..4f,
                    steps = 3,
                    colors = SliderDefaults.colors(thumbColor = RedMagicRed, activeTrackColor = RedMagicRed)
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        SectionTitle("Thermal Status")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                "Device Temp", "${metrics.temperatureCelsius.toInt()}C",
                subtitle = when {
                    metrics.temperatureCelsius < 38f -> "Cool"
                    metrics.temperatureCelsius < 43f -> "Normal"
                    metrics.temperatureCelsius < 48f -> "Warm"
                    else -> "Hot"
                },
                icon = Icons.Default.Thermostat,
                accentColor = when {
                    metrics.temperatureCelsius < 38f -> RedMagicGreen
                    metrics.temperatureCelsius < 43f -> RedMagicCyan
                    metrics.temperatureCelsius < 48f -> RedMagicOrange
                    else -> RedMagicRed
                },
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                "Fan Status", settings.fanSpeed.displayName,
                subtitle = if (settings.iceCoolingEnabled) "Active" else "Standby",
                icon = Icons.Default.Air,
                accentColor = RedMagicCyan,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}
