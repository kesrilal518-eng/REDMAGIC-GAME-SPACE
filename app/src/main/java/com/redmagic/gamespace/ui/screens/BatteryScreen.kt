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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HealthAndSafety
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
import com.redmagic.gamespace.ui.components.MetricCard
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
fun BatteryScreen(viewModel: MainViewModel) {
    val metrics by viewModel.metrics.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(RedMagicDark)
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("Battery and Charging", style = MaterialTheme.typography.displayMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text("Charge Bypass | Protection | Thermal", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                "Battery", "${metrics.batteryLevel}%",
                subtitle = if (metrics.isCharging) "Charging" else "Discharging",
                icon = Icons.Default.BatteryChargingFull,
                accentColor = if (metrics.batteryLevel > 20) RedMagicGreen else RedMagicOrange,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                "Charge Temp", "${metrics.chargingTemp.toInt()}C",
                subtitle = "While charging",
                icon = Icons.Default.Thermostat,
                accentColor = RedMagicCyan,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))
        SettingSwitchRow(
            title = "Charge Bypass",
            subtitle = "Power from charger, reduce battery heat during long sessions",
            checked = settings.chargeBypassEnabled,
            onCheckedChange = { viewModel.setChargeBypass(it) },
            icon = Icons.Default.Bolt
        )
        Spacer(Modifier.height(12.dp))
        SettingSwitchRow(
            title = "Battery Protection",
            subtitle = "Limit max charge to extend battery health",
            checked = settings.batteryProtectionEnabled,
            onCheckedChange = { viewModel.setBatteryProtection(it, settings.batteryProtectionLimit) },
            icon = Icons.Default.HealthAndSafety
        )

        if (settings.batteryProtectionEnabled) {
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = RedMagicCard), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Charge Limit: ${settings.batteryProtectionLimit}%", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Slider(
                        value = settings.batteryProtectionLimit.toFloat(),
                        onValueChange = { viewModel.setBatteryProtection(true, it.toInt()) },
                        valueRange = 50f..100f,
                        steps = 9,
                        colors = SliderDefaults.colors(thumbColor = RedMagicRed, activeTrackColor = RedMagicRed)
                    )
                    Text("Recommended: 80% for daily use", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        SectionTitle("About")
        Card(colors = CardDefaults.cardColors(containerColor = RedMagicCard), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Charge Bypass keeps the battery cooler during long gaming sessions. Battery Protection stops charging at a set percentage to improve longevity.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
