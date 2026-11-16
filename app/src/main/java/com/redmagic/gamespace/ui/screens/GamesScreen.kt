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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.redmagic.gamespace.data.model.GameProfile
import com.redmagic.gamespace.data.model.InstalledGame
import com.redmagic.gamespace.ui.theme.RedMagicCard
import com.redmagic.gamespace.ui.theme.RedMagicCyan
import com.redmagic.gamespace.ui.theme.RedMagicDark
import com.redmagic.gamespace.ui.theme.RedMagicRed
import com.redmagic.gamespace.ui.theme.TextMuted
import com.redmagic.gamespace.ui.theme.TextPrimary
import com.redmagic.gamespace.ui.theme.TextSecondary
import com.redmagic.gamespace.viewmodel.MainViewModel

@Composable
fun GamesScreen(viewModel: MainViewModel) {
    val profiles by viewModel.profiles.collectAsState()
    val games by viewModel.installedGames.collectAsState()
    val isLoading by viewModel.isLoadingGames.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(RedMagicDark).padding(16.dp)) {
        Text("Game Profiles", style = MaterialTheme.typography.displayMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text("Per-game performance and RGB presets", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(Modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RedMagicRed)
            }
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (profiles.isNotEmpty()) {
                item { Text("SAVED PROFILES", style = MaterialTheme.typography.labelMedium, color = RedMagicRed) }
                items(profiles, key = { it.id }) { profile ->
                    Card(colors = CardDefaults.cardColors(containerColor = RedMagicCard), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(RedMagicRed.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Games, null, tint = RedMagicRed)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(profile.appName, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${profile.performanceMode} | Fan ${profile.fanSpeed}", style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            IconButton(onClick = { viewModel.saveGameProfile(profile.copy(isFavorite = !profile.isFavorite)) }) {
                                Icon(if (profile.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder, "Favorite", tint = if (profile.isFavorite) RedMagicCyan else TextMuted)
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(12.dp)) }
            }

            item { Text("INSTALLED GAMES", style = MaterialTheme.typography.labelMedium, color = RedMagicRed) }

            if (games.isEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = RedMagicCard), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Games, null, tint = TextMuted, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("No games detected", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text("Install games or grant package visibility.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            TextButton(onClick = { viewModel.loadInstalledGames() }) {
                                Text("Refresh", color = RedMagicRed)
                            }
                        }
                    }
                }
            } else {
                items(games, key = { it.packageName }) { game ->
                    val hasProfile = profiles.any { it.packageName == game.packageName }
                    Card(colors = CardDefaults.cardColors(containerColor = RedMagicCard), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(RedMagicCard), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Games, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(game.appName, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(game.packageName, style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            if (hasProfile) {
                                Text("Profiled", style = MaterialTheme.typography.labelMedium, color = RedMagicCyan)
                            } else {
                                IconButton(onClick = { viewModel.createProfileForGame(game) }) {
                                    Icon(Icons.Default.Add, "Add profile", tint = RedMagicRed)
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
