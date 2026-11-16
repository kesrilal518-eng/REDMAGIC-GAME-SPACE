package com.redmagic.gamespace.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Games
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.redmagic.gamespace.ui.screens.BatteryScreen
import com.redmagic.gamespace.ui.screens.CoolingScreen
import com.redmagic.gamespace.ui.screens.DashboardScreen
import com.redmagic.gamespace.ui.screens.GamesScreen
import com.redmagic.gamespace.ui.screens.RgbScreen
import com.redmagic.gamespace.ui.screens.SettingsScreen
import com.redmagic.gamespace.viewmodel.MainViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard : Screen(
        "dashboard", "Dashboard",
        Icons.Filled.Dashboard, Icons.Outlined.Dashboard
    )
    data object Games : Screen(
        "games", "Games",
        Icons.Filled.Games, Icons.Outlined.Games
    )
    data object Cooling : Screen(
        "cooling", "Cooling",
        Icons.Filled.Thermostat, Icons.Outlined.Thermostat
    )
    data object Battery : Screen(
        "battery", "Battery",
        Icons.Filled.BatteryChargingFull, Icons.Outlined.BatteryChargingFull
    )
    data object Rgb : Screen(
        "rgb", "RGB",
        Icons.Filled.LightMode, Icons.Outlined.LightMode
    )
    data object Settings : Screen(
        "settings", "Settings",
        Icons.Filled.Settings, Icons.Outlined.Settings
    )
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Games,
    Screen.Cooling,
    Screen.Battery,
    Screen.Rgb,
    Screen.Settings
)

@Composable
fun GameSpaceNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(viewModel = viewModel)
        }
        composable(Screen.Games.route) {
            GamesScreen(viewModel = viewModel)
        }
        composable(Screen.Cooling.route) {
            CoolingScreen(viewModel = viewModel)
        }
        composable(Screen.Battery.route) {
            BatteryScreen(viewModel = viewModel)
        }
        composable(Screen.Rgb.route) {
            RgbScreen(viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = viewModel)
        }
    }
}
