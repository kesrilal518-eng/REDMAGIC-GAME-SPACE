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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.redmagic.gamespace.data.model.RgbMode
import com.redmagic.gamespace.ui.components.ColorSwatch
import com.redmagic.gamespace.ui.components.ModeChip
import com.redmagic.gamespace.ui.components.SectionTitle
import com.redmagic.gamespace.ui.theme.RedMagicCard
import com.redmagic.gamespace.ui.theme.RedMagicDark
import com.redmagic.gamespace.ui.theme.TextPrimary
import com.redmagic.gamespace.ui.theme.TextSecondary
import com.redmagic.gamespace.viewmodel.MainViewModel

private val presetColors = listOf(
    0xFFE60012L, 0xFF00F0FFL, 0xFF9B59FFL, 0xFF00FF9DL,
    0xFFFF8C00L, 0xFFFFD600L, 0xFFFFFFFFL, 0xFFFF2D55L
)

@Composable
fun RgbScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()
    val currentColor = Color(settings.rgbColor)

    Column(
        modifier = Modifier.fillMaxSize().background(RedMagicDark)
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("RGB Lighting", style = MaterialTheme.typography.displayMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text("Light up your game space", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(20.dp))
                .background(
                    if (settings.rgbMode == RgbMode.OFF) Color(0xFF1A1A1A)
                    else Brush.horizontalGradient(
                        listOf(
                            currentColor.copy(alpha = settings.rgbBrightness * 0.3f),
                            currentColor.copy(alpha = settings.rgbBrightness),
                            currentColor.copy(alpha = settings.rgbBrightness * 0.3f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape)
                    .background(if (settings.rgbMode == RgbMode.OFF) Color(0xFF333333) else currentColor)
            )
        }

        Spacer(Modifier.height(20.dp))
        SectionTitle("Lighting Mode")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RgbMode.entries.chunked(3).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { mode ->
                        ModeChip(
                            label = mode.displayName,
                            selected = settings.rgbMode == mode,
                            onClick = { viewModel.setRgbMode(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        SectionTitle("Color")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            presetColors.forEach { colorLong ->
                ColorSwatch(
                    color = Color(colorLong),
                    selected = settings.rgbColor == colorLong,
                    onClick = { viewModel.setRgbColor(colorLong) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Card(colors = CardDefaults.cardColors(containerColor = RedMagicCard), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Brightness: ${(settings.rgbBrightness * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Slider(
                    value = settings.rgbBrightness,
                    onValueChange = { viewModel.setRgbBrightness(it) },
                    valueRange = 0.1f..1f,
                    colors = SliderDefaults.colors(thumbColor = currentColor, activeTrackColor = currentColor)
                )
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
