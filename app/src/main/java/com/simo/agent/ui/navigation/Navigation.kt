package com.simo.agent.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.simo.agent.ui.chat.ChatScreen
import com.simo.agent.ui.dashboard.DashboardScreen
import com.simo.agent.ui.settings.SettingsScreen
import com.simo.agent.ui.skills.SkillsScreen

private val DarkBackground = Color(0xFF0D0D0D)
private val CardBg      = Color(0xFF161616)
private val NeonCyan    = Color(0xFF00F0FF)
private val TextSecondary = Color(0xFF666666)

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "الرئيسية", Icons.Default.Dashboard)
    object Chat      : Screen("chat",      "الشات",    Icons.Default.Chat)
    object Skills    : Screen("skills",    "المهارات", Icons.Default.Star)
    object Settings  : Screen("settings",  "الإعدادات", Icons.Default.Settings)
}

val screens = listOf(Screen.Dashboard, Screen.Chat, Screen.Skills, Screen.Settings)

@Composable
fun SimoNavHost() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            NavigationBar(
                containerColor = CardBg,
                tonalElevation = androidx.compose.ui.unit.Dp(0f)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDest = navBackStackEntry?.destination

                screens.forEach { screen ->
                    val selected = currentDest?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = NeonCyan,
                            selectedTextColor   = NeonCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor      = NeonCyan.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Dashboard.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Chat.route)      { ChatScreen() }
            composable(Screen.Skills.route)    { SkillsScreen() }
            composable(Screen.Settings.route)  { SettingsScreen() }
        }
    }
}
