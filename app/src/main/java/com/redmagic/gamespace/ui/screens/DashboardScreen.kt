package com.redmagic.gamespace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.redmagic.gamespace.data.model.PerformanceMode
import com.redmagic.gamespace.ui.components.MetricCard
import com.redmagic.gamespace.ui.components.ModeChip
import com.redmagic.gamespace.ui.components.ProgressMetric
import com.redmagic.gamespace.ui.components.SectionTitle
import com.redmagic.gamespace.ui.theme.RedMagicCard
import com.redmagic.gamespace.ui.theme.RedMagicCyan
import com.redmagic.gamespace.ui.theme.RedMagicDark
import com.redmagic.gamespace.ui.theme.RedMagicGreen
import com.redmagic.gamespace.ui.theme.RedMagicOrange
import com.redmagic.gamespace.ui.theme.RedMagicPurple
import com.redmagic.gamespace.ui.theme.RedMagicRed
import com.redmagic.gamespace.ui.theme.TextPrimary
import com.redmagic.gamespace.ui.theme.TextSecondary
import com.redmagic.gamespace.viewmodel.MainViewModel

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val metrics by viewModel.metrics.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(RedMagicDark)
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("REDMAGIC", style = MaterialTheme.typography.labelMedium, color = RedMagicRed, fontWeight = FontWeight.Bold)
        Text("Game Space", style = MaterialTheme.typography.displayMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text("Performance Command Center", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(24.dp))
        SectionTitle("Performance Mode")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PerformanceMode.entries.forEach { mode ->
                ModeChip(
                    label = mode.displayName,
                    selected = settings.performanceMode == mode,
                    onClick = { viewModel.setPerformanceMode(mode) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        SectionTitle("Live Metrics")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("CPU", "${metrics.cpuUsage.toInt()}%", icon = Icons.Default.Memory, accentColor = RedMagicCyan, modifier = Modifier.weight(1f))
            MetricCard("GPU", "${metrics.gpuUsage.toInt()}%", icon = Icons.Default.Speed, accentColor = RedMagicPurple, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                "Temp", "${metrics.temperatureCelsius.toInt()}C",
                subtitle = if (metrics.temperatureCelsius > 45) "Warm" else "Cool",
                icon = Icons.Default.Thermostat,
                accentColor = if (metrics.temperatureCelsius > 45) RedMagicOrange else RedMagicGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard("FPS", "${metrics.fps}", subtitle = "Realtime", icon = Icons.Default.Speed, accentColor = RedMagicRed, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))
        Card(colors = CardDefaults.cardColors(containerColor = RedMagicCard), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProgressMetric("CPU Load", metrics.cpuUsage, color = RedMagicCyan)
                Spacer(Modifier.height(14.dp))
                ProgressMetric("GPU Load", metrics.gpuUsage, color = RedMagicPurple)
                Spacer(Modifier.height(14.dp))
                ProgressMetric("RAM", metrics.ramUsageMb.toFloat(), max = metrics.ramTotalMb.toFloat().coerceAtLeast(1f), color = RedMagicGreen, valueText = "${metrics.ramUsageMb} / ${metrics.ramTotalMb} MB")
                Spacer(Modifier.height(14.dp))
                ProgressMetric("Network Latency", metrics.networkLatencyMs.toFloat(), max = 100f, color = RedMagicOrange, valueText = "${metrics.networkLatencyMs} ms")
            }
        }

        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(RedMagicRed.copy(alpha = 0.3f), RedMagicPurple.copy(alpha = 0.2f))))
                .padding(16.dp)
        ) {
            Column {
                Text("Active Mode: ${settings.performanceMode.displayName}", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(settings.performanceMode.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Fan ${settings.fanSpeed.displayName}  |  ICE ${if (settings.iceCoolingEnabled) "ON" else "OFF"}  |  Battery ${metrics.batteryLevel}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
