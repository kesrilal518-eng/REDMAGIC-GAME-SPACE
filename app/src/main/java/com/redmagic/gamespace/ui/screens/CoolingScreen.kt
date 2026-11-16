package com.redmagic.gamespace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redmagic.gamespace.data.model.FanSpeed
import com.redmagic.gamespace.ui.components.CircularGauge
import com.redmagic.gamespace.ui.components.EnergyModeChip
import com.redmagic.gamespace.ui.components.PremiumCard
import com.redmagic.gamespace.ui.components.SectionHeader
import com.redmagic.gamespace.ui.components.SettingRow
import com.redmagic.gamespace.ui.theme.RedMagicBlack
import com.redmagic.gamespace.ui.theme.RedMagicCyan
import com.redmagic.gamespace.ui.theme.RedMagicRed
import com.redmagic.gamespace.ui.theme.RedMagicRedGlow
import com.redmagic.gamespace.ui.theme.TextMuted
import com.redmagic.gamespace.ui.theme.TextPrimary
import com.redmagic.gamespace.ui.theme.TextSecondary
import com.redmagic.gamespace.viewmodel.MainViewModel

@Composable
fun CoolingScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()
    val metrics by viewModel.metrics.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(RedMagicBlack)
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("REDMAGIC", style = MaterialTheme.typography.labelLarge, color = RedMagicRed, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
        Text("ICE COOLING", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text("Thermal control & fan curves", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(20.dp))

        PremiumCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                CircularGauge(
                    progress = metrics.batteryTempCelsius?.let { (it / 50f).coerceIn(0f, 1f) },
                    valueText = metrics.batteryTempCelsius?.let { "%.1f".format(it) } ?: "N/A",
                    label = "TEMP °C",
                    accent = RedMagicRedGlow,
                    size = 120.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                            .background(
                                if (settings.iceCoolingEnabled)
                                    Brush.radialGradient(listOf(RedMagicCyan.copy(0.4f), Color.Transparent))
                                else Brush.radialGradient(listOf(Color(0xFF333333), Color.Transparent))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Air, null, tint = if (settings.iceCoolingEnabled) RedMagicCyan else TextMuted, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(if (settings.iceCoolingEnabled) "ICE ACTIVE" else "ICE OFF", style = MaterialTheme.typography.labelLarge, color = if (settings.iceCoolingEnabled) RedMagicCyan else TextMuted, fontWeight = FontWeight.Bold)
                    Text("Fan: Unavailable", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        SettingRow(
            title = "ICE Cooling System",
            subtitle = "Intelligent thermal management (software preference)",
            checked = settings.iceCoolingEnabled,
            onCheckedChange = { viewModel.setIceCooling(it) },
            icon = Icons.Default.Thermostat
        )

        Spacer(Modifier.height(20.dp))
        SectionHeader("FAN LEVEL (PREFERENCE)")
        Text("No internal fan on Redmi Note 11T 5G. Level stored for profiles.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FanSpeed.entries.forEach { speed ->
                EnergyModeChip(label = speed.displayName, selected = settings.fanSpeed == speed, onClick = { viewModel.setFanSpeed(speed) }, modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
