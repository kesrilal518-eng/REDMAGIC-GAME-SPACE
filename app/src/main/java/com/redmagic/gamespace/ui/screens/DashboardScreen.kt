package com.redmagic.gamespace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Memory
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redmagic.gamespace.data.model.PerformanceMode
import com.redmagic.gamespace.ui.components.CircularGauge
import com.redmagic.gamespace.ui.components.EnergyModeChip
import com.redmagic.gamespace.ui.components.PremiumCard
import com.redmagic.gamespace.ui.components.SectionHeader
import com.redmagic.gamespace.ui.theme.RedMagicBlack
import com.redmagic.gamespace.ui.theme.RedMagicCard
import com.redmagic.gamespace.ui.theme.RedMagicCyan
import com.redmagic.gamespace.ui.theme.RedMagicRed
import com.redmagic.gamespace.ui.theme.RedMagicRedGlow
import com.redmagic.gamespace.ui.theme.TextMuted
import com.redmagic.gamespace.ui.theme.TextPrimary
import com.redmagic.gamespace.ui.theme.TextSecondary
import com.redmagic.gamespace.viewmodel.MainViewModel

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val metrics by viewModel.metrics.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedMagicBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "REDMAGIC",
                    style = MaterialTheme.typography.labelLarge,
                    color = RedMagicRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
                Text(
                    "ENERGY BASE",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(RedMagicRed.copy(0.15f))
                    .border(1.dp, RedMagicRed.copy(0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    if (metrics.isCharging) "CHARGING ${metrics.batteryLevel}%" else "${metrics.batteryLevel}%",
                    color = RedMagicRed,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        SectionHeader("PERFORMANCE MODE")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PerformanceMode.entries.forEach { mode ->
                EnergyModeChip(
                    label = mode.displayName,
                    selected = settings.performanceMode == mode,
                    onClick = { viewModel.setPerformanceMode(mode) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Text(
            settings.performanceMode.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.height(24.dp))

        SectionHeader("SYSTEM STATUS")
        PremiumCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircularGauge(
                    progress = metrics.batteryLevel / 100f,
                    valueText = "${metrics.batteryLevel}",
                    label = "BATTERY",
                    accent = if (metrics.isCharging) RedMagicCyan else RedMagicRed,
                    size = 100.dp
                )
                CircularGauge(
                    progress = metrics.batteryTempCelsius?.let { (it / 50f).coerceIn(0f, 1f) },
                    valueText = metrics.batteryTempCelsius?.let { "%.1f".format(it) } ?: "N/A",
                    label = "BATT °C",
                    accent = RedMagicRedGlow,
                    size = 100.dp
                )
                CircularGauge(
                    progress = metrics.ramUsageMb?.let { used ->
                        metrics.ramTotalMb?.let { total -> if (total > 0) used.toFloat() / total else null }
                    },
                    valueText = metrics.ramUsageMb?.let { "${it / 1024}G" } ?: "N/A",
                    label = "RAM",
                    accent = RedMagicCyan,
                    size = 100.dp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        PremiumCard {
            Column {
                Text(
                    "Hardware Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                MetricRow("CPU Usage", "Unavailable")
                MetricRow("GPU Usage", "Unavailable")
                MetricRow("FPS Counter", "Unavailable on this device")
                MetricRow("Internal Fan", "No hardware fan detected")
                MetricRow(
                    "Device Temp",
                    metrics.deviceTempCelsius?.let { "%.1f °C".format(it) } ?: "N/A"
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        SectionHeader("QUICK CONTROLS")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickAction(
                icon = Icons.Default.Thermostat,
                label = "ICE",
                active = settings.iceCoolingEnabled,
                onClick = { viewModel.setIceCooling(!settings.iceCoolingEnabled) },
                modifier = Modifier.weight(1f)
            )
            QuickAction(
                icon = Icons.Default.BatteryChargingFull,
                label = "BYPASS",
                active = settings.chargeBypassEnabled,
                onClick = { viewModel.setChargeBypass(!settings.chargeBypassEnabled) },
                modifier = Modifier.weight(1f)
            )
            QuickAction(
                icon = Icons.Default.Memory,
                label = "PROTECT",
                active = settings.batteryProtectionEnabled,
                onClick = { viewModel.setBatteryProtection(!settings.batteryProtectionEnabled) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextMuted, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun QuickAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) RedMagicRed.copy(0.2f) else RedMagicCard)
            .border(
                1.dp,
                if (active) RedMagicRed else Color(0xFF2A2A2A),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (active) RedMagicRed else TextMuted,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = if (active) RedMagicRed else TextSecondary
            )
        }
    }
}
