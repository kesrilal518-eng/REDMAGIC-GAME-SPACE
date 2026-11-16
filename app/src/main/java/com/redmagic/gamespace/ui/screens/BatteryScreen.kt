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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Shield
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
import com.redmagic.gamespace.ui.components.CircularGauge
import com.redmagic.gamespace.ui.components.PremiumCard
import com.redmagic.gamespace.ui.components.SectionHeader
import com.redmagic.gamespace.ui.components.SettingRow
import com.redmagic.gamespace.ui.theme.RedMagicBlack
import com.redmagic.gamespace.ui.theme.RedMagicCyan
import com.redmagic.gamespace.ui.theme.RedMagicRed
import com.redmagic.gamespace.ui.theme.TextMuted
import com.redmagic.gamespace.ui.theme.TextPrimary
import com.redmagic.gamespace.ui.theme.TextSecondary
import com.redmagic.gamespace.viewmodel.MainViewModel

@Composable
fun BatteryScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()
    val metrics by viewModel.metrics.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(RedMagicBlack)
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("REDMAGIC", style = MaterialTheme.typography.labelLarge, color = RedMagicRed, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
        Text("BATTERY LAB", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text("Charge bypass & protection", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(20.dp))

        PremiumCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                CircularGauge(
                    progress = metrics.batteryLevel / 100f,
                    valueText = "${metrics.batteryLevel}%",
                    label = if (metrics.isCharging) "CHARGING" else "BATTERY",
                    accent = if (metrics.isCharging) RedMagicCyan else RedMagicRed,
                    size = 130.dp
                )
                Column(modifier = Modifier.padding(start = 8.dp), verticalArrangement = Arrangement.Center) {
                    Text(metrics.batteryTempCelsius?.let { "%.1f °C".format(it) } ?: "N/A", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Battery Temp", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Spacer(Modifier.height(16.dp))
                    Text(if (metrics.isCharging) "Plugged in" else "On battery", style = MaterialTheme.typography.titleMedium, color = if (metrics.isCharging) RedMagicCyan else TextSecondary)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        SettingRow(title = "Charge Bypass", subtitle = "Prefer charger power (OS limited on this device)", checked = settings.chargeBypassEnabled, onCheckedChange = { viewModel.setChargeBypass(it) }, icon = Icons.Default.Bolt)
        Spacer(Modifier.height(10.dp))
        SettingRow(title = "Battery Protection", subtitle = "Stop charging at selected limit", checked = settings.batteryProtectionEnabled, onCheckedChange = { viewModel.setBatteryProtection(it) }, icon = Icons.Default.Shield)

        if (settings.batteryProtectionEnabled) {
            Spacer(Modifier.height(12.dp))
            PremiumCard {
                Text("Charge limit: ${settings.batteryProtectionLimit}%", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Slider(
                    value = settings.batteryProtectionLimit.toFloat(),
                    onValueChange = { viewModel.setBatteryProtectionLimit(it.toInt()) },
                    valueRange = 50f..100f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = RedMagicRed, activeTrackColor = RedMagicRed, inactiveTrackColor = Color(0xFF333333))
                )
                Text("Recommended 80% for longevity", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
