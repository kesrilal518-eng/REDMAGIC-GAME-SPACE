package com.redmagic.gamespace.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redmagic.gamespace.ui.theme.RedMagicCard
import com.redmagic.gamespace.ui.theme.RedMagicCardElevated
import com.redmagic.gamespace.ui.theme.RedMagicCyan
import com.redmagic.gamespace.ui.theme.RedMagicRed
import com.redmagic.gamespace.ui.theme.TextMuted
import com.redmagic.gamespace.ui.theme.TextPrimary
import com.redmagic.gamespace.ui.theme.TextSecondary

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = RedMagicRed,
        letterSpacing = 1.5.sp,
        modifier = modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = RedMagicCyan,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = RedMagicCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun ProgressMetric(
    label: String,
    value: Float,
    max: Float = 100f,
    color: Color = RedMagicCyan,
    valueText: String? = null
) {
    val animated by animateFloatAsState(
        targetValue = (value / max).coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "progress"
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(
                valueText ?: "${value.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF2A2A2A))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(listOf(color.copy(alpha = 0.7f), color))
                    )
            )
        }
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RedMagicCard)
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, null, tint = RedMagicRed, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = RedMagicRed,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Color(0xFF333333)
            )
        )
    }
}

@Composable
fun ModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) RedMagicRed else RedMagicCardElevated
    val borderColor = if (selected) RedMagicRed else Color(0xFF333333)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else TextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ColorSwatch(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) Color.White else Color(0xFF444444),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}
